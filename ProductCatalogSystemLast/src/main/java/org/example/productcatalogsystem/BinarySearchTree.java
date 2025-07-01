package org.example.productcatalogsystem;

// Fiyata göre sıralama yapan Binary Search Tree
class BinarySearchTree {
    private Node root;

    private class Node {
        Product data;
        Node left, right;

        Node(Product data) {
            this.data = data;
        }
    }

    public void insert(Product product) {
        root = insertRec(root, product);
    }

    private Node insertRec(Node root, Product product) {
        if (root == null) return new Node(product);

        if (product.getPrice() < root.data.getPrice()) {
            root.left = insertRec(root.left, product);
        } else {
            root.right = insertRec(root.right, product);
        }
        return root;
    }

    public Product[] getSortedArray() {
        ProductList list = new ProductList();
        inOrder(root, list);
        return list.toArray();
    }

    private void inOrder(Node root, ProductList list) {
        if (root != null) {
            inOrder(root.left, list);
            list.add(root.data);
            inOrder(root.right, list);
        }
    }
}

// İsme göre alfabetik sıralama yapan Binary Search Tree
class NameBinarySearchTree {
    private Node root;

    private class Node {
        Product data;
        Node left, right;

        Node(Product data) {
            this.data = data;
        }
    }

    public void insert(Product product) {
        root = insertRec(root, product);
    }

    private Node insertRec(Node root, Product product) {
        if (root == null) return new Node(product);

        int cmp = product.getName().toLowerCase().compareTo(root.data.getName().toLowerCase());
        if (cmp < 0) {
            root.left = insertRec(root.left, product);
        } else {
            root.right = insertRec(root.right, product);
        }
        return root;
    }

    public Product[] getSortedArray() {
        ProductList list = new ProductList();
        inOrder(root, list);
        return list.toArray();
    }

    private void inOrder(Node root, ProductList list) {
        if (root != null) {
            inOrder(root.left, list);
            list.add(root.data);
            inOrder(root.right, list);
        }
    }
}

// Basit dinamik dizi sınıfı
class ProductList {
    private Product[] items = new Product[10];
    private int size = 0;

    public void add(Product product) {
        if (size >= items.length) {
            Product[] newItems = new Product[items.length * 2];
            System.arraycopy(items, 0, newItems, 0, size);
            items = newItems;
        }
        items[size++] = product;
    }

    public Product[] toArray() {
        Product[] result = new Product[size];
        System.arraycopy(items, 0, result, 0, size);
        return result;
    }
}