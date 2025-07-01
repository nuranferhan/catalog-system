package org.example.productcatalogsystem;

import javafx.scene.image.Image;
import java.io.*;

public class FileHandler {

    private static final String FILE_PATH = "products.txt";

    // Ürünleri txt dosyasına kaydedilmesi
    public static void saveProducts(LinkedList productList) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            // Liste boşsa boş dosya oluşturulması ve çıkılması
            if (productList.isEmpty()) {
                return;
            }

            Product temp = productList.head;
            while (temp != null) {
                StringBuilder line = new StringBuilder();
                line.append(temp.getId()).append(",");
                line.append(temp.getName()).append(",");
                line.append(temp.getBrand()).append(",");
                line.append(temp.getCategory()).append(",");
                line.append(temp.getDescription().replace(',', ';')).append(","); // Açıklamadaki virgülleri noktalı virgülle değiştir
                line.append(temp.getPrice()).append(",");
                line.append(temp.getStock()).append(",");

                // Resim yolunun kaydedilmesi
                String imagePath = "null";
                if (temp.getImage() != null) {
                    // JavaFX Image nesnesinin URL'sini alınması
                    String url = temp.getImage().getUrl();
                    if (url != null) {
                        imagePath = url;
                    }
                }
                line.append(imagePath);

                writer.println(line);
                temp = temp.next;
            }

        } catch (IOException e) {
            System.err.println("Ürünler kaydedilirken hata oluştu: " + e.getMessage());
        }
    }

    // Ürünleri txt dosyasından yüklenmesi
    public static LinkedList loadProducts() {
        LinkedList productList = new LinkedList();
        File file = new File(FILE_PATH);

        // Dosya yoksa boş liste döndürülmesi
        if (!file.exists()) {
            return productList;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 8); // En fazla 8 parça (son parça resim yolu)

                if (parts.length >= 7) {
                    int id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    String brand = parts[2];
                    String category = parts[3];
                    String description = parts[4].replace(';', ','); // Noktalı virgülleri virgüle geri çevir
                    double price = Double.parseDouble(parts[5]);
                    int stock = Integer.parseInt(parts[6]);

                    // Resim yolunu kontrol et ve resmi yükle
                    Image image = null;
                    if (parts.length > 7 && !parts[7].equals("null")) {
                        try {
                            image = new Image(parts[7]);
                        } catch (Exception e) {
                            System.err.println("Resim yüklenemedi: " + parts[7]);
                        }
                    }

                    // Yeni ürün oluşturulması ve listeye eklenmesi
                    Product product = new Product(id, name, brand, category, description, price, stock, image);
                    productList.add(product);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Ürünler yüklenirken hata oluştu: " + e.getMessage());
        }

        return productList;
    }
}