package org.example.productcatalogsystem;

public class Stack<T> {


    private class Node {
        T data;
        Node next;

        Node(T data) {
            this.data = data;
        }
    }

    private Node top;
    private int size;

    // Yığına eleman eklenmesi push
    public void push(T item) {
        Node newNode = new Node(item);
        newNode.next = top;
        top = newNode;
        size++;
    }

    // Yığından eleman çıkarılması pop
    public T pop() {
        if (isEmpty()) return null;
        T data = top.data;
        top = top.next;
        size--;
        return data;
    }

    // Yığın boş mu kontrol edilmesi
    public boolean isEmpty() {
        return top == null;
    }

    // Yığının boyutunu döndürülmesi
    public int size() {
        return size;
    }

    // Tepe elemanını çıkarmadan gösterilmesi (peek)
    public T peek() {
        if (isEmpty()) return null;
        return top.data;
    }

    // Yığının temizlenmesi
    public void clear() {
        top = null;
        size = 0;
    }

    // Yığında belirli bir eleman var mı kontrol edilmesi
    public boolean contains(T item) {
        Node current = top;
        while (current != null) {
            if (current.data != null && current.data.equals(item)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    // Yığını dizi olarak döndürülmesi
    @SuppressWarnings("unchecked")
    public T[] toArray(T[] array) {
        if (array.length < size) {
            // Yeni bir dizi oluşturulması boyut yetersizse
            array = (T[]) java.lang.reflect.Array.newInstance(
                    array.getClass().getComponentType(), size);
        }

        Node current = top;
        int index = 0;
        while (current != null && index < array.length) {
            array[index] = current.data;
            current = current.next;
            index++;
        }

        // Kullanılmayan alanları null yapılması
        for (int i = index; i < array.length; i++) {
            array[i] = null;
        }

        return array;
    }
}