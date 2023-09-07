package com.example.investmentportfoliorebalancingtool.cache;

public class Node<E> implements LinkedListNode<E> {

    private E element;
    private LinkedListNode<E> next;
    private LinkedListNode<E> prev;
    private DoublyLinkedList<E> list;

    public Node(E element, LinkedListNode<E> next, DoublyLinkedList<E> list) {
        this.element = element;
        this.next = next;
        this.prev = this.next.getPrev();
        this.next.setPrev(this);
        this.prev.setNext(this);
        this.list = list;
    }

    @Override
    public boolean hasElement() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public E getElement() {
        return this.element;
    }

    @Override
    public E setElement(E element) {
        this.element = element;
        return this.element;
    }

    @Override
    public LinkedListNode<E> search(E element) {
        return this.getElement() == element ? this : this.getNext().search(element);
    }

    @Override
    public LinkedListNode<E> getPrev() {
        return this.prev;
    }

    @Override
    public LinkedListNode<E> setPrev(LinkedListNode<E> prev) {
        this.prev = prev;
        return this;
    }

    @Override
    public LinkedListNode<E> getNext() {
        return this.next;
    }

    @Override
    public LinkedListNode<E> setNext(LinkedListNode<E> next) {
        this.next = next;
        return this;
    }

    @Override
    public void detach() {
        this.prev.setNext(this.next);
        this.next.setPrev(this.prev);
        this.prev = null;
        this.next = null;
    }

    @Override
    public DoublyLinkedList<E> getListReference() {
        return this.list;
    }
}
