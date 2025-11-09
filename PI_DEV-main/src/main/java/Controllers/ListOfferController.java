package Controllers;

import Models.Offre;
import Models.OffreProduit;

import java.sql.SQLException;
import java.util.Comparator;
import Models.OffreCoach;
import Services.OffreService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox; // Import pour ComboBox
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ListOfferController {

    @FXML
    private VBox offerList; // VBox où les offres seront affichées
    @FXML
    private TextField search; // Champ de recherche
    private ObservableList<String> searchHistory = FXCollections.observableArrayList(); // Historique des recherches
    @FXML
    private ComboBox<String> historyComboBox; // ComboBox pour afficher l'historique

    private OffreService offreService = new OffreService();

    @FXML
    private ComboBox<String> sortComboBox;

    public ListOfferController() throws SQLException {
    }


    @FXML
    public void initialize() {
        setupSortComboBox();
        loadOffers();
        setupHistoryComboBox();

        System.out.println("Historique initial: " + searchHistory); // Debug
    }
    private void setupSortComboBox() {
        sortComboBox.getItems().addAll(
                "Par Nom (A-Z)",
                "Par Nom (Z-A)",
                "Par Date ↑",
                "Par Date ↓",
                "Par Prix ↑",
                "Par Prix ↓"
        );

        sortComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal != null) {
                loadOffers();
            }
        });
    }


    void loadOffers() {
        try {
            List<Offre> offers = offreService.getAll();
            applySorting(offers);
            displayOffers(offers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void applySorting(List<Offre> offers) {
        String sortType = sortComboBox.getValue();

        if(sortType != null) {
            switch(sortType) {
                case "Par Nom (A-Z)":
                    offers.sort(Comparator.comparing(Offre::getNom));
                    break;
                case "Par Nom (Z-A)":
                    offers.sort(Comparator.comparing(Offre::getNom).reversed());
                    break;
                case "Par Date ↑":
                    offers.sort(Comparator.comparing(Offre::getDuree_validite));
                    break;
                case "Par Date ↓":
                    offers.sort(Comparator.comparing(Offre::getDuree_validite).reversed());
                    break;
                case "Par Prix ↑":
                    offers.sort(Comparator.comparingDouble(this::getOfferPrice));
                    break;
                case "Par Prix ↓":
                    offers.sort(Comparator.comparingDouble(this::getOfferPrice).reversed());
                    break;
            }
        }
    }

    private double getOfferPrice(Offre offre) {
        if (offre instanceof OffreCoach) {
            return ((OffreCoach) offre).getNouveauTarif();
        } else if (offre instanceof OffreProduit) {
            return ((OffreProduit) offre).getNouveauPrix();
        }
        return 0.0;
    }



    private void displayOffers(List<Offre> offers) {
        offerList.getChildren().clear(); // Clear existing offers
        HBox row = new HBox(50); // HBox pour contenir 2 cartes par ligne
        row.setStyle("-fx-alignment: center;"); // Alignement à gauche

        for (int i = 0; i < offers.size(); i++) {
            Offre offer = offers.get(i);

            // Création de la carte de l'offre (VBox)
            VBox offerCard = new VBox();
            offerCard.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0); -fx-padding: 20;");
            offerCard.setPrefSize(350, 309);
            offerCard.setSpacing(10);

            // Nom
            Label nameLabel = new Label(offer.getNom());
            nameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: 700; -fx-text-fill: #333333;");

            // Type et état
            HBox typeStatusBox = new HBox(10);
            Label typeLabel = new Label("Type: " + (offer instanceof OffreCoach ? "Coach" : "Produit"));
            typeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666; -fx-padding: 5 7; -fx-background-radius: 5; -fx-background-color: #e0e0e0;");

            Label etatLabel = new Label("Etat: " + offer.getEtat());
            String etat = String.valueOf(offer.getEtat());
            if ("ACTIF".equals(etat)) {
                etatLabel.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 5 7; -fx-background-radius: 5;");
            } else if ("INACTIF".equals(etat)) {
                etatLabel.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-padding: 5 7; -fx-background-radius: 5;");
            } else {
                etatLabel.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: black; -fx-padding: 5 7; -fx-background-radius: 5;");
            }

            typeStatusBox.getChildren().addAll(typeLabel, etatLabel);

            // Description
            Label descriptionLabel = new Label("Description: " + offer.getDescription());
            descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");
            descriptionLabel.setWrapText(true);

            // Détails (Durée de validité)
            GridPane detailsGrid = new GridPane();
            detailsGrid.setHgap(10);
            detailsGrid.setVgap(8);
            Label validiteLabel = new Label("" + offer.getDuree_validite());
            detailsGrid.addRow(0, new Label("Validité:"), validiteLabel);

            // Séparateur
            Separator separator = new Separator();
            separator.setStyle("-fx-padding: 10 0;");

            // Boutons de mise à jour et de suppression
            HBox buttonBox = new HBox(10);
            Button updateButton = new Button("Mettre à jour");
            updateButton.setOnAction(event -> handleUpdateButtonAction(offer));
            Button deleteButton = new Button("Supprimer");
            deleteButton.setOnAction(event -> handleDeleteButtonAction(offer));
            buttonBox.getChildren().addAll(updateButton, deleteButton);

            // Ajouter les composants à la carte
            offerCard.getChildren().addAll(nameLabel, typeStatusBox, descriptionLabel, detailsGrid, separator, buttonBox);

            // Ajouter la carte à la ligne actuelle
            row.getChildren().add(offerCard);

            // Tous les 2 offres, ajouter la ligne à la VBox et créer une nouvelle ligne
            if ((i + 1) % 2 == 0 || i == offers.size() - 1) {
                offerList.getChildren().add(row);
                row = new HBox(50);
                row.setStyle("-fx-alignment: center-start;");
            }
        }
    }

    @FXML
    void filterOffers() {
        String query = search.getText().toLowerCase(); // Récupérer le texte de recherche
        if (!query.isEmpty() && !searchHistory.contains(query)) {
            searchHistory.add(query); // Ajouter à l'historique si ce n'est pas déjà présent
            historyComboBox.getItems().add(query); // Mettre à jour la ComboBox
        }
        try {
            List<Offre> allOffers = offreService.getAll(); // Récupérer toutes les offres
            List<Offre> filteredOffers = allOffers.stream()
                    .filter(offer -> offer.getNom().toLowerCase().contains(query)) // Filtrer par nom
                    .collect(Collectors.toList());
            displayOffers(filteredOffers); // Afficher les offres filtrées
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode pour mettre à jour une offre
    private void handleUpdateButtonAction(Offre offer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateOffre.fxml"));
            Parent root = loader.load();

            UpdateOfferController updateOfferController = loader.getController();
            updateOfferController.setOfferDetails(offer);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Mettre à jour l'offre");

            // Modification clé : utiliser showAndWait() et rafraîchir après fermeture
            stage.showAndWait();
            loadOffers(); // Rafraîchir la liste après mise à jour

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour supprimer une offre
    private void handleDeleteButtonAction(Offre offer) {
        // Créer une boîte de dialogue de confirmation
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation de suppression");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Êtes-vous sûr de vouloir supprimer cette offre ?");

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    offreService.delete(offer.getId());
                    offerList.getChildren().clear();
                    loadOffers(); // Recharger les offres après suppression
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Configurer la ComboBox d'historique
    private void setupHistoryComboBox() {
        // Ne pas créer une nouvelle ComboBox, utiliser celle injectée par @FXML
        historyComboBox.setItems(searchHistory);
        historyComboBox.setOnAction(event -> {
            String selected = historyComboBox.getValue();
            if (selected != null) {
                search.setText(selected);
                filterOffers();
            }
        });
        // Retirer l'ajout à offerList.getChildren()
    }
    @FXML
    void GoToEvent(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Events.fxml"));
            Parent root = loader.load();
            ((Button) actionEvent.getSource()).getScene().setRoot(root);
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