package org.example.productcatalogsystem;

public class Queue<T> {
    private Object[] data;
    private int front, rear, size;
    private int capacity;

    public Queue(int capacity) {
        this.capacity = capacity;
        data = new Object[capacity];
        front = 0;
        rear = 0;
        size = 0;
    }

    public void enqueue(T item) {
        if (size == capacity) {
            // Kuyruk doluysa, en eski elemanın çıkarılması
            dequeue();
        }
        data[rear] = item;
        rear = (rear + 1) % capacity;
        size++;
    }

    public T dequeue() {
        if (size == 0) return null;
        T item = (T) data[front];
        data[front] = null;
        front = (front + 1) % capacity;
        size--;
        return item;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }

    public T[] toArray(T[] arr) {
        for (int i = 0; i < size; i++) {
            arr[i] = (T) data[(front + i) % capacity];
        }
        return arr;
    }

    // Kuyruğun temizlenmesi
    public void clear() {
        for (int i = 0; i < capacity; i++) {
            data[i] = null;
        }
        front = 0;
        rear = 0;
        size = 0;
    }
}