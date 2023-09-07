package com.example.investmentportfoliorebalancingtool.cache;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DoublyLinkedList<E> {
    // Size is declared as an AtomicInteger to atomically perform operations on it
    // Atomic operations are performed as a single unit of work,
    // (effectively happen all at once without interference of other operations)
    // Atomicity is required in a multithreaded application/environment to avoid inconsistent results
    //
    // Using Locks or synchronized keyword with methods can also solve the problem of concurrent update of a shared mutable state
    // However, using read/write locks, the losing threads are locked or suspended
    // The process of suspending and then resuming a thread is very expensive and affects the overall efficiency of the system.
    // In case of atomic operations, the losing threads are not suspended but diverted to other work.
    private AtomicInteger size;
    private DummyNode<E> dummyNode;
    private LinkedListNode<E> head;
    private LinkedListNode<E> tail;

    // ReentrantReadWriteLock class of Java is an implementation of ReadWriteLock, that also supports ReentrantLock functionality.
    // "A ReadWriteLock maintains a pair of associated locks, one for read-only operations and one for writing.
    // The read lock may be held simultaneously by multiple reader threads, so long as there are no writers.
    // The write lock is exclusive."
    //
    // ReentrantLock allows threads to enter into the lock on a resource more than once -
    // - "same basic behavior and semantics as the implicit monitor lock accessed using synchronized methods and statements,
    // but with extended capabilities."

    // Using Locks API gives more flexibility in when to use a lock for reading and writing,
    // over declaring methods as synchronized.
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public DoublyLinkedList() {
        // Initialize a new dummy node.
        this.dummyNode = new DummyNode<E>();
        // Head and Tail initially point to the dummy node and initial size of the list is set to zero
        this.clear();
    }

    // Clear the doubly linked list by pointing Head and Tail to the dummy node and resetting list size to zero;
    public void clear() {
        // https://stackoverflow.com/a/18354623
        // writeLock.lock() -
        // * "if any other thread is reading or writing, stop here and wait until no other thread is reading or writing."
        // * "Once the lock is granted, no other thread will be allowed to read or write (i.e. take a read or write lock) -
        // - until the lock is released."
        this.lock.writeLock().lock();
        try {
            head = dummyNode;
            tail = dummyNode;
            size = new AtomicInteger(0);
        }
        // Releasing the lock before exiting the function with a finally block at the end of the method -
        // - is important to prevent a deadlock.
        finally {
            this.lock.writeLock().unlock();
        }
    }

    public int size() {
        // https://stackoverflow.com/a/18354623
        // readLock.lock() -
        // * "if any other thread is writing (i.e. holds a write lock) then stop here until no other thread is writing."
        // * "Once the lock is granted no other thread will be allowed to write (i.e. take a write lock) -
        // - until the lock is released."
        this.lock.readLock().lock();
        try {
            return size.get();
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public boolean isEmpty() {

        this.lock.readLock().lock();
        try {
            // if head = dummy node, dummyNode.isEmpty() is always true;
            // if head = node, node.isEmpty() is always false;
            return head.isEmpty();
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public LinkedListNode<E> search(E element) {
        this.lock.readLock().lock();
        try {
            // if head = dummy node, dummyNode.search(e) returns the dummy node itself;
            // if head = node, node.search(e) returns the node with element 'e' using recursion;
            return head.search(element);
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public boolean contains(E element) {
        this.lock.readLock().lock();
        try {
            // if value returned by search(e) is the dummy node, dummyNode.hasElement() is always false;
            // if value returned by search(e) is a node, node.hasElement() is always true;
            return search(element).hasElement();
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    // Add the given element to the front of the list
    public LinkedListNode<E> add(E element) {
        this.lock.writeLock().lock();
        try {
            // Initialize the node to be added to the front of the list -
            // - it's element, next pointer and (reference of) list to which it is to be added
            // Node() constructor takes care of the next and prev pointers of the node to be added
            head = new Node<E>(element, head, this);
            // if tail = dummy node, dummyNode.isEmpty() is always true;   head -> dummyNode <- tail
            // if tail = node, node.isEmpty() is always false;             dummyNode, head <=> ... node <=> tail, dummyNode
            if (tail.isEmpty()) {
                tail = head;
            }
            // Atomically increment the size of the list instead of size++ or size += 1 compound operations
            size.incrementAndGet();
            // Return head of the list which points to the newly added node
            return head;
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    // Add the given elements to the front of the list
    public boolean addAll(Collection<E> elements) {
        this.lock.writeLock().lock();
        try {
            for (E element : elements) {
                // if value returned by add(e) is the dummy node, dummyNode.isEmpty() is always true;
                // if value returned by add(e) is a node, node.isEmpty() is always false;
                if (add(element).isEmpty()) {
                    return false;
                }
            }
            // Return true if all the elements in the collection are added to the list
            return true;
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    // Remove the given element from the list
    public LinkedListNode<E> remove(E element) {
        this.lock.writeLock().lock();
        try {
            // Search and retrieve the (reference of) node storing the given element to be removed from the list
            // if value returned by search(e) is the dummy node, dummyNode.isEmpty() is always true;
            // if value returned by search(e) is a node, node.isEmpty() is always false;
            LinkedListNode<E> removedNode = search(element);
            // if node to be removed is not the dummy node, update pointers based on 3 cases
            if (!removedNode.isEmpty()) {
                // 1. Remove from Head;        dummyNode, head <=> removedNode ... node <=> tail, dummyNode
                if (removedNode == head) {
                    // update the Head pointer
                    head = head.getNext();
                }
                // 2. Remove from Tail;        dummyNode, head <=> node ... removedNode <=> tail, dummyNode
                if (removedNode == tail) {
                    // update the Tail pointer
                    tail = tail.getPrev();
                }

                // 3. Remove from between;     dummyNode, head <=> node ... removedNode ... node <=> tail, dummyNode
                // update next and prev pointers of the node to be removed
                removedNode.detach();
                // Atomically decrement the size of the list instead of size-- or size -= 1 compound operations
                size.decrementAndGet();
            }

            // Return dummy node or the retrieved node to be removed
            return removedNode;
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    // Remove the last element from the list (node referenced by the Tail pointer)
    public LinkedListNode<E> removeTail() {
        this.lock.writeLock().lock();
        try {
            // Retrieve the (reference of) node referenced by the Tail pointer
            // if tail = dummy node, dummyNode.isEmpty() is always true;
            // if tail = node, node.isEmpty() is always false;
            LinkedListNode<E> oldTail = tail;
            // if tail node or node to be removed is not the dummy node, update pointers based on 2 cases
            if (!oldTail.isEmpty()) {
                // 1. Only node in the list;    dummyNode, head <=> removedNode <=> tail, dummyNode
                if (oldTail == head) {
                    head = dummyNode;
                    tail = dummyNode;
                    oldTail.setNext(null);
                    oldTail.setPrev(null);
                }
                // 2. Not the only node in the list;    dummyNode, head <=> node ... removedNode <=> tail, dummyNode
                else {
                    tail = tail.getPrev();
                    // update next and prev pointers of the node to be removed
                    oldTail.detach();
                }

                // Atomically decrement the size of the list instead of size-- or size -= 1 compound operations
                size.decrementAndGet();
            }

            // Return dummy node or the retrieved node to be removed
            return oldTail;
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    // Remove the given node from the list and Add it (as a new node with updated element) to the front of the list
    public LinkedListNode<E> updateAndMoveToFront(LinkedListNode<E> node, E newElement) {
        this.lock.writeLock().lock();
        try {
            // if given node is a dummy node (dummyNode.isEmpty() is always true) or
            // the given node doesn't belong to this doubly linked list;
            // return a dummy node
            if (node.isEmpty() || (this != (node.getListReference()))) {
                return dummyNode;
            }
            // Remove the given node from the list
            detach(node);
            // Add the given element to the front of the list
            add(newElement);
            // Return head of the list which points to the newly added node
            return head;
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    // Remove the given node from the list and Add it (as a new node) to the front of the list
    public LinkedListNode<E> moveToFront(LinkedListNode<E> node) {
        // if given node is a dummy node (dummyNode.isEmpty() is always true), return a dummy node
        // else, updateAndMoveToFront(n, n.getElement()) the given node
        return node.isEmpty() ? dummyNode : updateAndMoveToFront(node, node.getElement());
    }

    private void detach(LinkedListNode<E> node) {
        // dummyNode, head <=> ... removedNode ... node <=> tail, dummyNode
        if(node != tail) {
            node.detach();
            // dummyNode, head <=> removedNode ... node <=> tail, dummyNode
            if(node == head) {
                head = head.getNext();
            }
            size.decrementAndGet();
        }
        // dummyNode, head <=> ... removedNode <=> tail, dummyNode
        else {
            removeTail();
        }
    }
}
