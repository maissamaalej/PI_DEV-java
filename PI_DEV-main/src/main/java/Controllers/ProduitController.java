package Controllers;

import Models.etat;
import Models.produit;
import Services.CreateurEvenementService;
import Services.categorieService;
import Services.produitService;
import Utils.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProduitController {
    @FXML
    private GridPane GridPaneProd;
    @FXML
    private TextField desc_produit;
    @FXML
    private ChoiceBox<etat> etat_produit;
    @FXML
    private ChoiceBox<Integer> Id_categorie;
    @FXML
    private ChoiceBox<Integer> Id_categorie1;
    @FXML
    private TextField Nom_Produit1;
    @FXML
    private TextField desc_prod1;
    @FXML
    private ChoiceBox<etat> etat_produit1;
    @FXML
    private TextField prix1;
    @FXML
    private TextField quantite_produit1;
    @FXML
    private TextField quantite_produit;
    @FXML
    private TextField nom_produit;
    @FXML
    private TextField prix_produit;
    @FXML
    private ImageView imageView;
    @FXML
    private ImageView imgView;
    @FXML
    private ScrollPane scrollPaneProd;
    @FXML
    private TextField search;
    private File selectedImageFile;
    private produitService produitService = new produitService();
    private produit produitUpdated=new produit();
    private categorieService categService = new categorieService();
    private int selectedProduitId;

    public ProduitController() throws SQLException {
    }

    @FXML
    void Ajouter()throws Exception {
        if (!validateInput()) return;

        String nom = nom_produit.getText();
        String description = desc_produit.getText();
        etat etatProduit = etat.valueOf(etat_produit.getValue().toString());
        Integer idCategorie = Id_categorie.getValue();
        // Récupérer l'ID de l'investisseur connecté à partir de la session
        int idInvestisseur = Session.getInstance().getCurrentUser().getId();
        int quantite=Integer.parseInt(quantite_produit.getText());
        float prix = Float.parseFloat(prix_produit.getText());
        String imageName1 = "";
        // Récupérer uniquement le nom du fichier
        imageName1 = selectedImageFile.getName();
        produit prod=new produit(idInvestisseur,nom,description,imageName1,etatProduit,idCategorie,quantite,prix);
        produitService.create(prod);

        initialize();

        // Réinitialiser les champs après l'ajout
        nom_produit.clear();
        desc_produit.clear();
        Id_categorie.getItems().clear();
        quantite_produit.clear();
        prix_produit.clear();
        imageView.setImage(null);
        selectedImageFile = null;
        showSuccessAlert();
    }
    private void loadCategories() throws Exception {
        Id_categorie.getItems().clear();
        Id_categorie1.getItems().clear();
        // Récupérer les IDs des catégories depuis la base de données
        List<Integer> categoryIds = categService.getCategoryId();

        // Utiliser un Set pour éliminer les doublons
        Set<Integer> uniqueCategoryIds = new HashSet<>(categoryIds);

        // Ajouter les IDs uniques dans le ChoiceBox
        Id_categorie.getItems().addAll(uniqueCategoryIds);
        Id_categorie1.getItems().addAll(uniqueCategoryIds);
    }
    public void initialize() throws Exception {
        // Remplir le ChoiceBox pour l'état
        etat_produit.getItems().setAll(etat.values());
        etat_produit1.getItems().setAll(etat.values());
        loadCategories();

        if (GridPaneProd == null) {
            System.out.println("GridPane is null! Check FXML configuration.");
        } else {
            System.out.println("GridPane initialized successfully.");
        }
        // Configuration du ScrollPane
        scrollPaneProd.setFitToHeight(true);
        scrollPaneProd.setFitToWidth(false);

        // Ajouter un espacement entre les éléments du GridPane
        GridPaneProd.setHgap(20); // Espacement horizontal entre les colonnes
        GridPaneProd.setAlignment(Pos.CENTER); // Centrer le contenu dans le GridPane

        // Charger et afficher initialement tous les produits de l'investisseur
        int idInvestisseur = Session.getInstance().getCurrentUser().getId();
        List<produit> produits = produitService.getAll_ByInvestisseur(idInvestisseur);
        updateGrid(produits);

        // Ajout d'un listener sur le TextField pour la recherche dynamique
        search.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                // On récupère la liste des produits pour l'investisseur
                List<produit> allProduits = produitService.getAll_ByInvestisseur(idInvestisseur);
                // Filtrer en fonction du texte saisi (ici on peut filtrer sur le nom du produit par exemple)
                List<produit> filteredProduits = allProduits.stream()
                        .filter(prod -> prod.getNom().toLowerCase().contains(newValue.toLowerCase()))
                        .collect(Collectors.toList());
                // Mettre à jour l'affichage
                updateGrid(filteredProduits);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Méthode pour mettre à jour le GridPane avec la liste fournie de produits.
     */
    private void updateGrid(List<produit> produits) {
        // Nettoyer le GridPane
        GridPaneProd.getChildren().clear();

        int columnIndex = 0; // Index pour les colonnes
        for (produit prod : produits) {
            ImageView imageView = new ImageView();
            imageView.setFitWidth(60); // Taille de l'image (ajustée)
            imageView.setFitHeight(50); // Taille de l'image (ajustée)

            // Créer le chemin relatif de l'image dans resources/img
            String imagePath = "/img/" + prod.getImage();
            InputStream imageStream = getClass().getResourceAsStream(imagePath);
            if (imageStream != null) {
                Image image = new Image(imageStream);
                imageView.setImage(image);
            } else {
                System.out.println("Image non trouvée : " + imagePath);
            }

            // Créer un texte pour le nom du produit
            Text nomText = new Text("Nom : " + prod.getNom());

            // Création de la description avec un texte abrégé (truncation)
            String descriptionAbrigee = prod.getDescription();
            if (descriptionAbrigee.length() > 40) {  // Limiter à 40 caractères
                descriptionAbrigee = descriptionAbrigee.substring(0, 40) + "...";  // Ajouter "..." pour l'abréviation
            }
            Text descText = new Text("Description : " + descriptionAbrigee);

            // Créer un Tooltip avec la description complète
            Tooltip descTooltip = new Tooltip("Description complète : " + prod.getDescription());
            Tooltip.install(descText, descTooltip);  // Associer le Tooltip à la description

            Text etatText = new Text("État : " + prod.getEtat());
            Text quantiteText = new Text("Quantité : " + prod.getQuantite());
            Text prixText = new Text("Prix : " + prod.getPrix() + " TND");
            Text investisseurText = new Text("Investisseur : " + prod.getIdInvestisseur());
            Text categorieText = new Text("Catégorie : " + prod.getCategorieId());

            // Ajuster la largeur pour permettre le wrapping du texte
            nomText.setWrappingWidth(150);
            descText.setWrappingWidth(150);  // Largeur fixe pour éviter que la description prenne trop de place
            // Centrer le texte
            nomText.setTextAlignment(TextAlignment.CENTER);
            descText.setTextAlignment(TextAlignment.CENTER);

            // Bouton "Supprimer"
            Button deleteButton = new Button("Supprimer");
            deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-border-radius: 5; -fx-font-weight: bold;");
            deleteButton.setOnAction(event -> {
                if (confirmerSuppression()) {
                    try {
                        produitService.delete(prod.getId());
                        // Rafraîchir l'affichage après suppression
                        int idInvestisseur = Session.getInstance().getCurrentUser().getId();
                        List<produit> newProduits = produitService.getAll_ByInvestisseur(idInvestisseur);
                        updateGrid(newProduits);
                        System.out.println("Produit supprimé avec succès.");
                    } catch (Exception e) {
                        System.out.println("Erreur lors de la suppression du produit : " + e.getMessage());
                    }
                }
            });

            // Bouton "Modifier"
            Button modifyButton = new Button("Modifier");
            modifyButton.setStyle("-fx-background-color: #f58400; -fx-text-fill: white; -fx-border-radius: 5; -fx-font-weight: bold;");
            modifyButton.setOnAction(event -> {
                Nom_Produit1.setText(prod.getNom());
                desc_prod1.setText(prod.getDescription());
                quantite_produit1.setText(String.valueOf(prod.getQuantite()));
                prix1.setText(String.valueOf(prod.getPrix()));
                Id_categorie1.setValue(prod.getCategorieId());
                etat_produit1.setValue(prod.getEtat());
                selectedProduitId = prod.getId();

                String imageName = prod.getImage();
                String imagePath1 = "/img/" + imageName;
                InputStream imageStream1 = getClass().getResourceAsStream(imagePath1);
                if (imageStream1 != null) {
                    Image image = new Image(imageStream1);
                    imgView.setImage(image);
                } else {
                    System.out.println("L'image n'a pas été trouvée : " + imagePath1);
                }
            });

            // Création d'une VBox pour contenir les informations du produit
            VBox produitBox = new VBox(10); // Espacement vertical de 10 pixels
            produitBox.setStyle("-fx-padding: 15; -fx-border-color: gray; -fx-border-width: 1; -fx-background-color: white; -fx-background-radius: 10;");
            produitBox.setAlignment(Pos.CENTER);
            produitBox.setPrefWidth(200);
            produitBox.setMinHeight(330);

            // Ajout de tous les éléments à la VBox
            produitBox.getChildren().addAll(imageView, nomText, descText, etatText, quantiteText, prixText, investisseurText, categorieText, deleteButton, modifyButton);

            // Ajout de la VBox dans le GridPane
            GridPaneProd.add(produitBox, columnIndex, 0);
            columnIndex++;
        }
        // Ajustement de la largeur totale du GridPane en fonction du nombre de produits affichés
        double largeurCarte = 220;
        GridPaneProd.setPrefWidth(produits.size() * largeurCarte);
    }

    @FXML
    void upload_Image() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImageFile = file;
            // Charger et afficher l'image dans l'ImageView
            Image image = new Image(file.toURI().toString());
            imageView.setImage(image);
            imageView.setFitWidth(90);  // Largeur maximale
            imageView.setFitHeight(80); // Hauteur maximale
            imageView.setPreserveRatio(false);  // Permet de déformer l'image si nécessaire
            imageView.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-border-radius: 10; -fx-padding: 10;");
        }
    }
    @FXML
    void upload_new_Image() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImageFile = file;
            Image image = new Image(file.toURI().toString());
            imgView.setFitWidth(90);
            imgView.setFitHeight(80);
            imgView.setPreserveRatio(false);
            imgView.setImage(image);
            imgView.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-border-radius: 10; -fx-padding: 10;");
        }
    }
    @FXML
    void Modifier() throws Exception {
        if (!validateInput1()) return;
        if (selectedImageFile == null || !selectedImageFile.exists()) {
            showAlert("Erreur", "L'image du produit est manquante.");
            return;
        }
        produitUpdated.setNom(Nom_Produit1.getText());
        produitUpdated.setDescription(desc_prod1.getText());
        String imageName = selectedImageFile.getName();
        produitUpdated.setImage(imageName);
        produitUpdated.setEtat(etat.valueOf(etat_produit1.getValue().toString()));
        produitUpdated.setCategorieId(Integer.parseInt(Id_categorie1.getValue().toString()));
        produitUpdated.setQuantite(Integer.parseInt(quantite_produit1.getText()));
        produitUpdated.setPrix(Float.parseFloat(prix1.getText()));
        produitUpdated.setId(selectedProduitId); // Associer l'ID de la catégorie sélectionnée
        // Appeler le service pour mettre à jour la catégorie dans la base de données
        produitService.update(produitUpdated);

        Id_categorie1.getItems().clear();
        Nom_Produit1.clear();
        desc_prod1.clear();
        etat_produit1.getItems().clear();
        prix1.clear();
        quantite_produit1.clear();
        imgView.setImage(null);
        selectedImageFile=null;

        showSuccessAlert1();
        // mise a jour
        initialize();
    }
    private boolean validateInput() {
        if (nom_produit.getText().trim().isEmpty() || nom_produit.getText() == null) {
            showAlert("Erreur de saisie", "Le champ 'Nom du produit' ne peut pas être vide.");
            return false;
        }
        if (!nom_produit.getText().matches("[a-zA-Z\\s]*")) {
            showAlert("Erreur de saisie", "Le champ 'Nom du produit' doit contenir des lettres.");
            return false;
        }
        if (desc_produit.getText().trim().isEmpty()) {
            showAlert("Erreur de saisie", "Le champ 'Description du produit' ne peut pas être vide.");
            return false;
        }
        if (!desc_produit.getText().matches("[a-zA-Z\\s]*")) {
            showAlert("Erreur de saisie", "Le champ 'description du produit' doit contenir des lettres .");
            return false;
        }
        if (etat_produit.getValue() == null) {
            showAlert("Erreur de saisie", "Veuillez sélectionner un état pour le produit.");
            return false;
        }
        if (quantite_produit.getText().isEmpty() || !quantite_produit.getText().matches("[1-9][0-9]*")) {
            showAlert("Erreur de saisie", "la quantite doit être un nombre valide.");
            return false;
        }
        if (Id_categorie.getValue() == null) {
            showAlert("Erreur de saisie", "L'ID de catégorie doit être un nombre valide.");
            return false;
        }
        if (prix_produit.getText().isEmpty() || !prix_produit.getText().matches("[1-9][0-9]*(\\.[0-9]+)?")) {
            showAlert("Erreur de saisie", "le prix de produit doit être un nombre valide.");
            return false;
        }
        if (selectedImageFile == null) {
            showAlert("Erreur de saisie", "Veuillez sélectionner une image pour le produit.");
            return false;
        }
        return true;
    }
    private boolean validateInput1() {
        if (!Nom_Produit1.getText().matches("[a-zA-Z\\s]*")) {
            showAlert("Erreur de saisie", "Le champ 'Nom du produit' doit contenir des lettres.");
            return false;
        }
        if (Nom_Produit1.getText().trim().isEmpty()) {
            showAlert("Erreur de saisie", "Le champ 'Nom de produit' ne peut pas être vide.");
            return false;
        }
        if (desc_prod1.getText().trim().isEmpty()) {
            showAlert("Erreur de saisie", "Le champ 'Description du produit' ne peut pas être vide.");
            return false;
        }
        if (!desc_prod1.getText().matches("[a-zA-Z\\s]*")) {
            showAlert("Erreur de saisie", "Le champ 'description du produit' doit contenir des lettres .");
            return false;
        }
        if (etat_produit1.getValue() == null) {
            showAlert("Erreur de saisie", "Veuillez sélectionner un état pour le produit.");
            return false;
        }
        if (quantite_produit1.getText().isEmpty() || !quantite_produit1.getText().matches("[1-9][0-9]*")) {
            showAlert("Erreur de saisie", "la quantite doit être un nombre valide.");
            return false;
        }
        if (Id_categorie1.getValue() == null) {
            showAlert("Erreur de saisie", "L'ID de catégorie doit être un nombre valide.");
            return false;
        }
        if (prix1.getText().isEmpty() || !prix1.getText().matches("[1-9][0-9]*(\\.[0-9]+)?")) {
            showAlert("Erreur de saisie", "le prix de produit doit être un nombre valide.");
            return false;
        }
        return true;
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private boolean confirmerSuppression() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Voulez-vous vraiment supprimer ce produit ?");

        // Afficher la boîte de dialogue et retourner la réponse
        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }
    private void showSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText("Le produit a été ajouté avec succès !");
        alert.showAndWait();
    }
    private void showSuccessAlert1() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText("Le produit a été modifié avec succès !");
        alert.showAndWait();
    }
    //ROOT
    private CreateurEvenementService createurEvenementService = new CreateurEvenementService();
    @FXML
    void GoToEvent(ActionEvent actionEvent) {
        int id = Session.getInstance().getCurrentUser().getId();
        String path = "";

        try {
            if (createurEvenementService.isCreateurEvenement(id)) {
                path = "/AddEvenement.fxml";
            } else {
                path = "/Events.fxml";
            }

            // Now load the determined path
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();
            ((Node) actionEvent.getSource()).getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    @FXML
    void GoToHome(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/home.fxml"));
            Parent root = loader.load();
            ((Button) actionEvent.getSource()).getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void GoToProduit(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Produit.fxml"));
            Parent root = loader.load();
            ((Button) actionEvent.getSource()).getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void GoToCategorie(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Categorie.fxml"));
            Parent root = loader.load();
            ((Button) actionEvent.getSource()).getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void GoToSeance(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajoutplanning.fxml"));
            Parent root = loader.load();
            ((Button) actionEvent.getSource()).getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void GoToRec(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserReclamation.fxml"));
            Parent root = loader.load();
            ((Button) actionEvent.getSource()).getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void GoToOffre(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddOffre.fxml"));
            Parent root = loader.load();
            ((Button) actionEvent.getSource()).getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
