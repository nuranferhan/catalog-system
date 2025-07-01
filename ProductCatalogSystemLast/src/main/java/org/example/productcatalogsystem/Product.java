package org.example.productcatalogsystem;

import javafx.scene.image.Image;

public class Product {
    // Ürün özellikleri
    private int id;
    private String name;
    private String brand;
    private String category;
    private String description;
    private double price;
    private int stock;
    private Image image;

    // Bağlı liste yapısı için sonraki ürün referansı
    public Product next;  // LinkedList için
    public Product left;  // BinaryTree için
    public Product right; // BinaryTree için

    // Yapıcı metod - ürün nesnesi oluşturulması
    public Product(int id, String name, String brand, String category, String description, double price, int stock, Image image) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.image = image;
        this.next = null;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Product getNext() {
        return next;
    }

    public void setNext(Product next) {
        this.next = next;
    }

    // Ürün bilgilerini metin olarak döndürülmesi
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", brand='" + brand + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                '}';
    }
}
