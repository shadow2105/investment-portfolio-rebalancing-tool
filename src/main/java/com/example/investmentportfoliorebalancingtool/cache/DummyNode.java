package com.example.investmentportfoliorebalancingtool.cache;

/*
 * Dummy Node
 * A node in the list which has no value associated with it.
 * Usually located at the start and/or end of the list. (head and/or tail point to the dummy node)
 * Make certain list algorithms easier to implement as they ensure that there will be at least one node,
 * thus removing the need to have special cases for dealing with null first/last elements or empty lists;
 */
public class DummyNode<E> implements LinkedListNode<E> {

    private DoublyLinkedList<E> list;

    @Override
    public boolean hasElement() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public E getElement() throws NullPointerException {
        throw new NullPointerException();
    }

    @Override
    public E setElement(E element) throws NullPointerException {
        throw new NullPointerException();
    }

    @Override
    public LinkedListNode<E> search(E element) {
        return this;
    }

    @Override
    public LinkedListNode<E> getPrev() {
        return this;
    }

    @Override
    public LinkedListNode<E> setPrev(LinkedListNode<E> prev) {
        return prev;
    }

    @Override
    public LinkedListNode<E> getNext() {
        return this;
    }

    @Override
    public LinkedListNode<E> setNext(LinkedListNode<E> next) {
        return next;
    }

    @Override
    public void detach() {
        return;
    }

    @Override
    public DoublyLinkedList<E> getListReference() {
        return this.list;
    }
}
