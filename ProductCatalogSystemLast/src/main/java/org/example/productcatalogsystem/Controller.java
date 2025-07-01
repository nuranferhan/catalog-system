package org.example.productcatalogsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private TextField aciklamaField;

    @FXML
    private TextField fiyatField;

    @FXML
    private ComboBox<String> kategoriComboBox;

    @FXML
    private ComboBox<String> kategoriComboBox1;

    @FXML
    private ComboBox<String> kategoriComboBox2;

    @FXML
    private TextField markaField;

    @FXML
    private TextField stockField;

    @FXML
    private TableView<Product> tableView;

    @FXML
    private TableColumn<Product, Integer> idColumn;

    @FXML
    private TableColumn<Product, String> nameColumn;

    @FXML
    private TableColumn<Product, String> brandColumn;

    @FXML
    private TableColumn<Product, String> categoryColumn;

    @FXML
    private TableColumn<Product, String> descriptionColumn;

    @FXML
    private TableColumn<Product, Double> priceColumn;

    @FXML
    private TableColumn<Product, Integer> stockColumn;

    @FXML
    private TextField urunAdiField;

    @FXML
    private TextField urunIdField;

    @FXML
    private TextField searchField;

    @FXML
    private ImageView productImageView;

    @FXML
    private Button sonEklenenlerButton;

    private Image selectedImage;
    private LinkedList productList = new LinkedList();
    private final Stack<Action> undoStack = new Stack<>();
    private Queue<Product> recentProducts = new Queue<>(5);  // Son 5 ürün için kuyruğu oluşturduk
    private boolean showingRecent = false;
    private HashTable<Integer, Product> idHashTable = new HashTable<>();
    private HashTable<String, Product> nameHashTable = new HashTable<>();

    // Kategori filtrelemesi için hash table'ı oluşturduk
    private HashTable<String, ArrayList<Product>> categoryHashTable = new HashTable<>();

    private class Action {
        private final ActionType type;
        private final Product product;

        public Action(ActionType type, Product product) {
            this.type = type;
            this.product = product;
        }
    }

    private enum ActionType {
        ADD,
        REMOVE
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Ürünleri dosyadan yükledik
        productList = FileHandler.loadProducts();
        Product[] allProducts = productList.toArray();

        // Hash tablolarını doldurduk
        buildHashTables();

        // Son eklenen ürünleri kuyruğa ekledik (son 5 ürün)
        updateRecentProductsQueue();

        // Tablo sütunlarını ayarladık
        setupTableColumns();

        // ComboBox ayarlarını yaptık
        setupComboBoxes();

        // Tablodan seçim yapıldığında form alanlarını doldurmak için dinleyici ayarladık
        setupTableSelectionListener();

        // Tabloyu güncelledik
        refreshTable();

        // Event handler'ları ayarladık
        kategoriComboBox2.setOnAction(event -> applyCurrentFiltersAndSort());
        kategoriComboBox.setOnAction(event -> applyCurrentFiltersAndSort());
    }

    // Hash tablolarını oluşturduk ve doldurduk
    private void buildHashTables() {
        // Önceki hash tablolarını temizledik
        idHashTable = new HashTable<>();
        nameHashTable = new HashTable<>();
        categoryHashTable = new HashTable<>();

        Product[] allProducts = productList.toArray();

        for (int i = 0; i < allProducts.length; i++) {
            Product product = allProducts[i];

            // ID ve name hash tablolarını doldurduk
            idHashTable.put(product.getId(), product);
            nameHashTable.put(product.getName().toLowerCase(), product);

            // Kategori hash tablosunu doldurduk
            String category = product.getCategory();
            ArrayList<Product> categoryProducts = categoryHashTable.get(category);
            if (categoryProducts == null) {
                categoryProducts = new ArrayList<>();
                categoryHashTable.put(category, categoryProducts);
            }
            categoryProducts.add(product);
        }
    }

    // Tablo sütunlarını ayarladık
    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
    }

    // ComboBox'ları ayarladık
    private void setupComboBoxes() {
        ObservableList<String> categories = FXCollections.observableArrayList(
                "Tümü", "Bilgisayar", "Telefon", "Tablet", "Ev Aletleri", "Aksesuar"
        );

        kategoriComboBox.setItems(categories);
        kategoriComboBox.setValue("Tümü"); // Varsayılan değer
        kategoriComboBox1.setItems(categories);

        ObservableList<String> sortOptions = FXCollections.observableArrayList(
                "Eskiden yeniye göre", "Yeniden eskiye göre", "Alfabetik (A-Z)",
                "Artan fiyata göre", "Azalan fiyata göre"
        );

        kategoriComboBox2.setItems(sortOptions);
        kategoriComboBox2.setValue("Eskiden yeniye göre"); // Varsayılan değer
    }

    // Tablo seçim dinleyicisini ayarladık
    private void setupTableSelectionListener() {
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                urunIdField.setText(String.valueOf(newSelection.getId()));
                urunAdiField.setText(newSelection.getName());
                markaField.setText(newSelection.getBrand());
                kategoriComboBox1.setValue(newSelection.getCategory());
                aciklamaField.setText(newSelection.getDescription());
                fiyatField.setText(String.valueOf(newSelection.getPrice()));
                stockField.setText(String.valueOf(newSelection.getStock()));

                if (newSelection.getImage() != null) {
                    productImageView.setImage(newSelection.getImage());
                    selectedImage = newSelection.getImage();
                } else {
                    productImageView.setImage(null);
                    selectedImage = null;
                }
            }
        });
    }

    // Mevcut filtreleri ve sıralamayı uyguladık
    private void applyCurrentFiltersAndSort() {
        String selectedCategory = kategoriComboBox.getValue();
        String selectedSort = kategoriComboBox2.getValue();

        if (selectedCategory == null) selectedCategory = "Tümü";
        if (selectedSort == null) selectedSort = "Eskiden yeniye göre";

        // Önce kategori filtrelemesi yaptık
        Product[] filteredProducts = getFilteredProducts(selectedCategory);

        // Sonra sıralama uyguladık
        ObservableList<Product> sortedProducts = applySorting(filteredProducts, selectedSort);

        // Tabloyu güncelledik
        tableView.setItems(sortedProducts);
        sonEklenenlerButton.setText("Tüm Ürünler");
        showingRecent = true;
    }

    // Kategoriye göre filtrelenmiş ürünleri getirdik
    private Product[] getFilteredProducts(String category) {
        if (category == null || category.equals("Tümü")) {
            return productList.toArray();
        }

        // Hash table'dan kategori ürünlerini aldık
        ArrayList<Product> categoryProducts = categoryHashTable.get(category);
        if (categoryProducts == null || categoryProducts.isEmpty()) {
            return new Product[0];
        }

        return categoryProducts.toArray(new Product[0]);
    }

    // Sıralama işlemini uyguladık
    private ObservableList<Product> applySorting(Product[] products, String sortOption) {
        ObservableList<Product> result = FXCollections.observableArrayList();

        if (sortOption.equals("Eskiden yeniye göre")) {
            for (Product p : products) {
                result.add(p);
            }
        } else if (sortOption.equals("Yeniden eskiye göre")) {
            Stack<Product> stack = new Stack<>();
            for (Product p : products) {
                stack.push(p);
            }
            while (!stack.isEmpty()) {
                result.add(stack.pop());
            }
        } else if (sortOption.equals("Alfabetik (A-Z)")) {
            // İsme göre alfabetik sıralama yaptık
            NameBinarySearchTree nameBst = new NameBinarySearchTree();
            for (Product p : products) {
                nameBst.insert(p);
            }
            Product[] sortedArray = nameBst.getSortedArray();
            for (Product p : sortedArray) {
                result.add(p);
            }
        } else if (sortOption.equals("Artan fiyata göre")) {
            // Fiyata göre artan sıralama yaptık
            BinarySearchTree bst = new BinarySearchTree();
            for (Product p : products) {
                bst.insert(p);
            }
            Product[] sortedArray = bst.getSortedArray();
            for (Product p : sortedArray) {
                result.add(p);
            }
        } else if (sortOption.equals("Azalan fiyata göre")) {
            // Fiyata göre azalan sıralama yaptık
            BinarySearchTree bst = new BinarySearchTree();
            for (Product p : products) {
                bst.insert(p);
            }
            Product[] sortedArray = bst.getSortedArray();
            for (int i = sortedArray.length - 1; i >= 0; i--) {
                result.add(sortedArray[i]);
            }
        }

        return result;
    }

    // Son eklenen ürünler kuyruğunu güncelledik
    private void updateRecentProductsQueue() {
        recentProducts.clear(); // Kuyruğu temizledik

        Product[] allProducts = productList.toArray();

        // Son 5 ürünü (veya mevcut ürün sayısı 5'ten azsa hepsini) kuyruğa ekledik
        int start = Math.max(0, allProducts.length - 5);
        for (int i = start; i < allProducts.length; i++) {
            recentProducts.enqueue(allProducts[i]);
        }
    }

    @FXML
    void handleUrunAra(ActionEvent event) {
        String searchText = searchField.getText().trim();

        if (searchText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Uyarı", "Lütfen arama yapmak için bir değer girin!");
            return;
        }

        ObservableList<Product> searchResults = FXCollections.observableArrayList();
        String searchLower = searchText.toLowerCase();

        // Önce hash table ile tam eşleşme denedik
        Product exactIdMatch = null;
        Product exactNameMatch = null;

        try {
            // ID ile tam eşleşme
            int id = Integer.parseInt(searchText);
            exactIdMatch = idHashTable.get(id);
        } catch (NumberFormatException e) {
            // ID değil, devam ettik
        }

        // İsim ile tam eşleşme
        exactNameMatch = nameHashTable.get(searchLower);

        // Tam eşleşmeleri ekledik
        if (exactIdMatch != null) {
            searchResults.add(exactIdMatch);
        }
        if (exactNameMatch != null && !searchResults.contains(exactNameMatch)) {
            searchResults.add(exactNameMatch);
        }

        // Eğer tam eşleşme bulunamadıysa veya daha fazla sonuç istiyorsak, kısmi eşleşme yaptık
        if (searchResults.isEmpty() || searchText.length() < 3) { // 3 karakterden az ise sadece tam eşleşme
            Product[] allProducts = productList.toArray();

            for (Product product : allProducts) {
                // Zaten eklenmiş ürünleri tekrar eklemedik
                if (searchResults.contains(product)) {
                    continue;
                }

                boolean matches = false;

                // ID ile kısmi eşleşme kontrolü yaptık
                String idStr = String.valueOf(product.getId());
                if (idStr.contains(searchText)) {
                    matches = true;
                }

                // İsim ile kısmi eşleşme kontrolü yaptık
                if (product.getName().toLowerCase().contains(searchLower)) {
                    matches = true;
                }

                // Marka ile kısmi eşleşme kontrolü yaptık
                if (product.getBrand().toLowerCase().contains(searchLower)) {
                    matches = true;
                }

                // Kategori ile kısmi eşleşme kontrolü yaptık
                if (product.getCategory().toLowerCase().contains(searchLower)) {
                    matches = true;
                }

                if (matches) {
                    searchResults.add(product);
                }
            }
        }

        if (!searchResults.isEmpty()) {
            tableView.setItems(searchResults);
            sonEklenenlerButton.setText("Tüm Ürünler");
            showingRecent = true;

            // İlk bulunan ürünü seçtik
            tableView.getSelectionModel().selectFirst();

            String message = searchResults.size() + " ürün bulundu.";
            if (exactIdMatch != null || exactNameMatch != null) {
                message += " (Tam eşleşme öncelikli)";
            }
            showAlert(Alert.AlertType.INFORMATION, "Arama Sonucu", message);
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Arama Sonucu", "Ürün bulunamadı.");
        }
    }

    @FXML
    void handleSirala() {
        applyCurrentFiltersAndSort();
    }

    @FXML
    void handleEkleButton(ActionEvent event) {
        try {
            // Form verilerini aldık
            int id = Integer.parseInt(urunIdField.getText());
            String name = urunAdiField.getText();
            String brand = markaField.getText();
            String category = kategoriComboBox1.getValue();
            String description = aciklamaField.getText();
            double price = Double.parseDouble(fiyatField.getText());
            int stock = Integer.parseInt(stockField.getText());

            // Alanların boş olup olmadığını kontrol ettik
            if (name.isEmpty() || brand.isEmpty() || category == null || description.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Hata", "Lütfen tüm alanları doldurun!");
                return;
            }

            // Aynı ID'ye sahip ürün var mı kontrol ettik
            if (productList.findById(id) != null) {
                showAlert(Alert.AlertType.ERROR, "Hata", "Bu ID ile bir ürün zaten var!");
                return;
            }

            // Yeni ürün oluşturduk
            Product newProduct = new Product(id, name, brand, category, description, price, stock, selectedImage);

            // Listeye ekledik
            productList.add(newProduct);

            // Hash tablolarını yeniden oluşturduk
            buildHashTables();

            // İşlemi stack'e ekledik (geri alma için)
            undoStack.push(new Action(ActionType.ADD, newProduct));

            // Yeni ürünü kuyruğa ekledik
            recentProducts.enqueue(newProduct);

            // Ürünleri dosyaya kaydettik
            FileHandler.saveProducts(productList);

            // Tabloyu güncelledik (mevcut filtreleri koruyarak)
            applyCurrentFiltersAndSort();

            // Formu temizledik
            clearForm();

            showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Ürün başarıyla eklendi!");

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Lütfen geçerli sayısal değerler girin!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Bir hata oluştu: " + e.getMessage());
        }
    }

    @FXML
    void handleGeriAl(ActionEvent event) {
        if (!undoStack.isEmpty()) {
            Action lastAction = undoStack.pop();

            if (lastAction.type == ActionType.ADD) {
                productList.remove(lastAction.product.getId());

                // Hash tablolarını yeniden oluşturduk
                buildHashTables();

                // Son eklenenler kuyruğunu güncelledik
                updateRecentProductsQueue();

                showAlert(Alert.AlertType.INFORMATION, "Bilgi", "Son eklenen ürün geri alındı.");
            } else if (lastAction.type == ActionType.REMOVE) {
                productList.add(lastAction.product);

                // Hash tablolarını yeniden oluşturduk
                buildHashTables();

                // Son eklenenler kuyruğunu güncelledik
                updateRecentProductsQueue();

                showAlert(Alert.AlertType.INFORMATION, "Bilgi", "Son silinen ürün geri alındı.");
            }

            // Değişiklikleri dosyaya kaydettik
            FileHandler.saveProducts(productList);

            // Tabloyu güncelledik (mevcut filtreleri koruyarak)
            applyCurrentFiltersAndSort();
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Bilgi", "Geri alınacak işlem yok!");
        }
    }

    @FXML
    void handleSecButton(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Ürün Resmi Seç");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Resim Dosyaları", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            selectedImage = new Image(selectedFile.toURI().toString());
            productImageView.setImage(selectedImage);
        }
    }

    @FXML
    void handleSonEklenenler(ActionEvent event) {
        if (showingRecent) {
            // Tüm ürünleri gösterdik (mevcut filtreleri uygulayarak)
            applyCurrentFiltersAndSort();
            sonEklenenlerButton.setText("Son Eklenenler");
            showingRecent = false;
            searchField.clear(); // Arama alanını temizledik
        } else {
            // Son eklenen ürünleri gösterdik
            Product[] qArray = new Product[recentProducts.size()];
            qArray = recentProducts.toArray(qArray);
            ObservableList<Product> lastItems = FXCollections.observableArrayList();
            for (Product p : qArray) {
                lastItems.add(p);
            }
            tableView.setItems(lastItems);
            sonEklenenlerButton.setText("Tüm Ürünler");
            showingRecent = true;
        }
    }

    @FXML
    void handleSilButton(ActionEvent event) {
        try {
            String idText = urunIdField.getText();
            if (idText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Hata", "Lütfen silinecek ürünün ID'sini girin!");
                return;
            }

            int id = Integer.parseInt(idText);
            Product productToRemove = productList.findById(id);

            if (productToRemove == null) {
                showAlert(Alert.AlertType.ERROR, "Hata", "Bu ID ile bir ürün bulunamadı!");
                return;
            }

            // Ürünü silmeden önce yedekledik (geri alma için)
            Product backup = new Product(
                    productToRemove.getId(),
                    productToRemove.getName(),
                    productToRemove.getBrand(),
                    productToRemove.getCategory(),
                    productToRemove.getDescription(),
                    productToRemove.getPrice(),
                    productToRemove.getStock(),
                    productToRemove.getImage()
            );

            // Listeden sildik
            productList.remove(id);

            // Hash tablolarını yeniden oluşturduk
            buildHashTables();

            // Son eklenenler kuyruğunu güncelledik
            updateRecentProductsQueue();

            // İşlemi stack'e ekledik (geri alma için)
            undoStack.push(new Action(ActionType.REMOVE, backup));

            // Değişiklikleri dosyaya kaydettik
            FileHandler.saveProducts(productList);

            // Tabloyu güncelledik (mevcut filtreleri koruyarak)
            applyCurrentFiltersAndSort();

            // Formu temizledik
            clearForm();

            showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Ürün başarıyla silindi!");

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Lütfen geçerli bir ID numarası girin!");
        }
    }

    private void refreshTable() {
        // Mevcut filtreleri ve sıralamaları uygulayarak tabloyu yeniledik
        applyCurrentFiltersAndSort();
    }

    private void clearForm() {
        urunIdField.clear();
        urunAdiField.clear();
        markaField.clear();
        kategoriComboBox1.setValue(null);
        aciklamaField.clear();
        fiyatField.clear();
        stockField.clear();
        productImageView.setImage(null);
        selectedImage = null;
        searchField.clear(); // Arama alanını da temizledik
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}