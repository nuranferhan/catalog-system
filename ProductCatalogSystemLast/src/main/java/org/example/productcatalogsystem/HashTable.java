package org.example.productcatalogsystem;

public class HashTable<K, V> {

    // Statik bağlı liste düğümü
    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int SIZE = 16;  // Tablo boyutu belirleme
    private Node<K, V>[] buckets;

    @SuppressWarnings("unchecked")
    public HashTable() {
        buckets = (Node<K, V>[]) new Node[SIZE];  // Java'da generic array oluşturma şekli
    }

    // Hash fonksiyonu
    private int getIndex(K key) {
        return Math.abs(key.hashCode()) % SIZE;
    }

    // Değer ekle veya güncelleme
    public void put(K key, V value) {
        int index = getIndex(key);
        Node<K, V> head = buckets[index];

        while (head != null) {
            if (head.key.equals(key)) {
                head.value = value;  // Güncelleme
                return;
            }
            head = head.next;
        }

        Node<K, V> newNode = new Node<>(key, value);
        newNode.next = buckets[index];
        buckets[index] = newNode;
    }

    // Değer getirme
    public V get(K key) {
        int index = getIndex(key);
        Node<K, V> head = buckets[index];

        while (head != null) {
            if (head.key.equals(key)) {
                return head.value;
            }
            head = head.next;
        }

        return null;
    }

    // Değer silme
    public void remove(K key) {
        int index = getIndex(key);
        Node<K, V> head = buckets[index];
        Node<K, V> prev = null;

        while (head != null) {
            if (head.key.equals(key)) {
                if (prev == null) {
                    buckets[index] = head.next;
                } else {
                    prev.next = head.next;
                }
                return;
            }
            prev = head;
            head = head.next;
        }
    }

    // Anahtar var mı kontrolü
    public boolean containsKey(K key) {
        return get(key) != null;
    }
}
