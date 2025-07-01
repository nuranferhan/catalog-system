package org.example.productcatalogsystem;

// Bağlı liste veri yapısı
public class LinkedList {
    Product head;

    // Listeye ürün ekleme
    public void add(Product product) {
        if (head == null) {
            head = product;
        } else {
            Product temp = head;
            while (temp.next != null) {
                temp = temp.next;
            }
            temp.next = product;
        }
    }

    public int size() {
        int count = 0;
        Product current = head;
        while (current != null) {
            count++;
            current = current.next;
        }
        return count;
    }



    // Belirli bir ID'ye sahip ürünü listeden silme
    public void remove(int id) {
        if (head == null) {
            System.out.println("Listede ürün yok");
        } else if (head.getId() == id) {
            // Silinecek ürün listenin başındaysa
            head = head.next;
        } else {
            Product prev = null;
            Product temp = head;

            // İlgili ID'ye sahip ürünü bulana kadar dolaşma
            while (temp != null && temp.getId() != id) {
                prev = temp;
                temp = temp.next;
            }

            if (temp != null) {
                // Ürünü listeden çıkar
                prev.next = temp.next;
            } else {
                System.out.println("Ürün bulunamadı: " + id);
            }
        }
    }

    // Listedeki tüm ürünleri ekrana yazdırma
    public void display() {
        if (head == null) {
            System.out.println("Liste boş");
            return;
        }

        Product temp = head;
        while (temp != null) {
            System.out.println(temp.toString());
            temp = temp.next;
        }
    }

    // Listedeki tüm ürünleri bir dizi (array) olarak döndürme
    public Product[] toArray() {
        int size = 0;
        Product temp = head;

        // Eleman sayısını bulma
        while (temp != null) {
            size++;
            temp = temp.next;
        }

        Product[] array = new Product[size];
        temp = head;
        int i = 0;

        // Ürünleri diziye kopyalama
        while (temp != null) {
            array[i++] = temp;
            temp = temp.next;
        }

        return array;
    }

    // Belirli ID'ye sahip ürünü bulma
    public Product findById(int id) {
        Product temp = head;
        while (temp != null) {
            if (temp.getId() == id) {
                return temp;
            }
            temp = temp.next;
        }
        return null; // Bulunamazsa null döndürme
    }

    // Liste boş mu kontrol etme
    public boolean isEmpty() {
        return head == null;
    }
}
