package com.example.investmentportfoliorebalancingtool.cache;

public interface LinkedListNode<E> {
    boolean hasElement();
    boolean isEmpty();

    E getElement() throws NullPointerException;

    E setElement(E element) throws NullPointerException;

    LinkedListNode<E> search(E element);

    LinkedListNode<E> getPrev();

    LinkedListNode<E> setPrev(LinkedListNode<E> prev);

    LinkedListNode<E> getNext();

    LinkedListNode<E> setNext(LinkedListNode<E> next);

    void detach();

    DoublyLinkedList<E> getListReference();
}
