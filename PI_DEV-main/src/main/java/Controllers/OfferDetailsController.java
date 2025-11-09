package Controllers;

import Models.Offre;
import Models.OffreCoach;
import Models.OffreProduit;
import Services.OffreService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.control.ButtonType;


import java.io.IOException;
import java.sql.SQLException;

public class OfferDetailsController {

    @FXML private Label nameLabel;
    @FXML private Label typeLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label validiteLabel;
    @FXML private Label etatLabel;
    @FXML private Label specificLabel1;
    @FXML private Label specificLabel2;
    @FXML private Label specificLabel3;
    @FXML private Label specificLabel4;
    @FXML private Button update;
    @FXML private Button deleteButton;

    private Offre currentOffer;
    private OffreService offreService = new OffreService();

    public OfferDetailsController() throws SQLException {
    }

    public void setOfferDetails(Offre offer) {
        this.currentOffer = offer;

        // Set the general offer details
        nameLabel.setText(offer.getNom());
        descriptionLabel.setText("Description: " + offer.getDescription());
        validiteLabel.setText("Validité: " + offer.getDuree_validite());
        etatLabel.setText("Etat: " + offer.getEtat());

        // Assure que tous les labels spécifiques sont visibles et réinitialisés
        specificLabel1.setText("");
        specificLabel2.setText("");
        specificLabel3.setText("");
        specificLabel4.setText("");

        specificLabel1.setVisible(true);
        specificLabel2.setVisible(true);
        specificLabel3.setVisible(true);
        specificLabel4.setVisible(true);

        // Set the type-specific details
        if (offer instanceof OffreCoach) {
            OffreCoach offreCoach = (OffreCoach) offer;
            typeLabel.setText("Type: Coach");
            specificLabel1.setText("ID Coach: " + offreCoach.getIdCoach());
            specificLabel2.setText("Nouveau Tarif: " + offreCoach.getNouveauTarif());
            specificLabel3.setText("Réservation Max: " + offreCoach.getReservationMax());
            specificLabel4.setVisible(false); // Cache le 4ème label inutile
        } else if (offer instanceof OffreProduit) {
            OffreProduit offreProduit = (OffreProduit) offer;
            typeLabel.setText("Type: Produit");
            specificLabel1.setText("ID Produit: " + offreProduit.getIdProduit());
            specificLabel2.setText("Nouveau Prix: " + offreProduit.getNouveauPrix());
            specificLabel3.setText("Quantité Max: " + offreProduit.getQuantiteMax());
            specificLabel4.setVisible(false); // Cache le 4ème label inutile
        } else {
            typeLabel.setText("Type: Inconnu");
            specificLabel1.setVisible(false);
            specificLabel2.setVisible(false);
            specificLabel3.setVisible(false);
            specificLabel4.setVisible(false);
        }

        // Mise à jour du style pour l'état
        String offerState = String.valueOf(offer.getEtat());
        if ("ACTIF".equals(offerState)) {
            etatLabel.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 5 7; -fx-background-radius: 5;");
        } else if ("INACTIF".equals(offerState)) {
            etatLabel.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-padding: 5 7; -fx-background-radius: 5;");
        } else {
            etatLabel.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: black; -fx-padding: 5 7; -fx-background-radius: 5;");
        }
    }


    @FXML
    private void handleUpdateButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateOffer.fxml"));
            Parent root = loader.load();

            UpdateOfferController updateOfferController = loader.getController();
            updateOfferController.setOfferDetails(currentOffer);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Mettre à jour l'offre");

            // Ajouter le showAndWait() et rafraîchissement
            stage.showAndWait();

            // Recharger les données après mise à jour
            Offre updatedOffer = offreService.getById(currentOffer.getId());
            setOfferDetails(updatedOffer);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML
    private void handleDeleteButtonAction(ActionEvent event) {
        // Créer une boîte de dialogue de confirmation
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation de suppression");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Êtes-vous sûr de vouloir supprimer cette offre ?");

        // Ajouter des boutons OK et Annuler
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // L'utilisateur a confirmé la suppression
                try {
                    offreService.delete(currentOffer.getId());
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Offre supprimée avec succès.");

                    // Fermer la fenêtre actuelle
                    Stage stage = (Stage) deleteButton.getScene().getWindow();
                    stage.close();
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression de l'offre.");
                    e.printStackTrace();
                }
            } else {
                System.out.println("User cancelled deletion");
                // L'utilisateur a annulé la suppression
                // Vous pouvez ajouter un message ou simplement ne rien faire
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