package com.example.investmentportfoliorebalancingtool.cache;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

// https://www.baeldung.com/java-lru-cache
public class LRUCache<K, V> implements Cache<K, V> {
    // Cache has a fixed limited size
    private final int CACHE_SIZE;

    // All cache operations should run in O(1)
    // To remove/dequeue the oldest element in O(1), a queue is implemented using a Doubly Linked List
    private final DoublyLinkedList<CacheElement<K,V>> doublyLinkedList;

    // But all queue operations don't run in O(1) (like access/search), so a Hashmap is required
    private final Map<K, LinkedListNode<CacheElement<K, V>>> linkedListNodeMap;

    // All cache operations must support concurrency
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public LRUCache(int size) {
        this.CACHE_SIZE = size;
        // JDK implementations of Doubly linked Lists don't expose their internal nodes
        // which need to be stored in the Hashmap. Hence, a custom implementation is used
        this.doublyLinkedList = new DoublyLinkedList<>();
        this.linkedListNodeMap = new ConcurrentHashMap<>(size);
    }

    // Add or update an item/object with the given key and value to the cache
    @Override
    public boolean set(K key, V value) {
        this.lock.writeLock().lock();
        try {
            CacheElement<K, V> item = new CacheElement<K, V>(key, value);
            LinkedListNode<CacheElement<K, V>> newNode;
            // [cache hit] - if key is present in the hashmap
            if (this.linkedListNodeMap.containsKey(key)) {
                // Get the list node mapped to the key
                LinkedListNode<CacheElement<K, V>> node = this.linkedListNodeMap.get(key);
                // Remove the retrieved node from the list and Add it (as a new node with updated element) to the front of the list
                newNode = doublyLinkedList.updateAndMoveToFront(node, item);
            }
            // [cache miss] - if key is not present in the hashmap
            else {
                // if current size of cache is greater than or equal to its defined capacity (CACHE_SIZE)
                if (this.size() >= this.CACHE_SIZE) {
                    // remove/evict the oldest element from the cache
                    this.evictElement();
                }
                // Add the given element to the front of the list
                newNode = this.doublyLinkedList.add(item);
            }
            // if newNode = dummy node, dummyNode.isEmpty() is always true;
            if (newNode.isEmpty()) {
                return false;
            }
            // Update the existing mapping of the key is it already exists, or
            // Add a new mapping for the new key - to the newly added node
            this.linkedListNodeMap.put(key, newNode);
            return true;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    // Get or access an item/object from the cache for the given key
    @Override
    public Optional<V> get(K key) {
        this.lock.readLock().lock();
        try {
            // Get the list node mapped to the key
            LinkedListNode<CacheElement<K, V>> linkedListNode = this.linkedListNodeMap.get(key);
            // if key exists or, is not mapped to null or a dummy node
            if (linkedListNode != null && !linkedListNode.isEmpty()) {
                // Recently accessed item is moved to the front of the cache, i.e.
                // retrieved node is removed from the list and added (as a new node) to the front of the list
                // Update the existing mapping of the key
                linkedListNodeMap.put(key, this.doublyLinkedList.moveToFront(linkedListNode));
                // Return the accessed item or object as an Optional
                return Optional.of(linkedListNode.getElement().getValue());
            }
            return Optional.empty();
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    // Get the current size of the cache
    @Override
    public int size() {
        try {
            // current size of the cache = size of the underlying doubly linked list
            return doublyLinkedList.size();
        } finally {
            this.lock.readLock().unlock();
        }
    }

    // Check if the cache is empty or not
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    // Clear the cache by clearing the underlying hashmap and doubly linked lists structures
    @Override
    public void clear() {
        this.lock.writeLock().lock();
        try {
            // Clear the hashmap by removing all mappings
            linkedListNodeMap.clear();
            // Clear the doubly linked list by pointing Head and Tail to the dummy node and resetting list size to zero;
            doublyLinkedList.clear();
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    // Remove/evict the oldest element from the cache
    private boolean evictElement() {
        this.lock.writeLock().lock();
        try {
            // Oldest cache element is at the tail of the list used to implement the queue
            // Retrieve and remove the last element from the list (node referenced by the Tail pointer)
            LinkedListNode<CacheElement<K, V>> linkedListNode = doublyLinkedList.removeTail();
            // if tail = dummy node, dummyNode.isEmpty() is always true;
            if (linkedListNode.isEmpty()) {
                return false;
            }
            // remove mapping of removed linked list node from the hashmap
            // The reason why both key and value are stored as an element in the linked list is because
            // the hashmap cannot keep track of the key of the oldest node as it is not an ordered structure
            linkedListNodeMap.remove(linkedListNode.getElement().getKey());
            return true;
        } finally {
            this.lock.writeLock().unlock();
        }
    }
}
