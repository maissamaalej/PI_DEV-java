package Controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import Utils.MyDb;
import Models.Offre;
import Models.OffreCoach;
import Models.OffreProduit;
import Models.Etato;
import Services.OffreService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.ZoneId;
import java.sql.Date;

public class UpdateOfferController {

    private Connection conn = MyDb.getInstance().getConn();

    @FXML
    private TextField nomField;

    @FXML
    private TextField descriptionField;

    @FXML
    private DatePicker dureeValiditeField;

    @FXML
    private ChoiceBox<String> etatField;

    @FXML
    private TextField idField;

    @FXML
    private TextField nouveauPrixTarifField;

    @FXML
    private TextField quantiteReservationMaxField;

    private Offre currentOffer;
    private OffreService offreService = new OffreService();

    public UpdateOfferController() throws SQLException {
    }

    @FXML
    public void initialize() {
        etatField.getItems().addAll("ACTIF", "INACTIF");
    }

    public void setOfferDetails(Offre offer) {
        this.currentOffer = offer;
        nomField.setText(offer.getNom());
        descriptionField.setText(offer.getDescription());
        dureeValiditeField.setValue(convertToLocalDateViaSqlDate(offer.getDuree_validite()));
        etatField.setValue(offer.getEtat().toString());

        if (offer instanceof OffreCoach) {
            OffreCoach offreCoach = (OffreCoach) offer;
            idField.setText(String.valueOf(offreCoach.getIdCoach()));
            nouveauPrixTarifField.setText(String.valueOf(offreCoach.getNouveauTarif()));
            quantiteReservationMaxField.setText(String.valueOf(offreCoach.getReservationMax()));
        } else if (offer instanceof OffreProduit) {
            OffreProduit offreProduit = (OffreProduit) offer;
            idField.setText(String.valueOf(offreProduit.getIdProduit()));
            nouveauPrixTarifField.setText(String.valueOf(offreProduit.getNouveauPrix()));
            quantiteReservationMaxField.setText(String.valueOf(offreProduit.getQuantiteMax()));
        }
    }

    private LocalDate convertToLocalDateViaSqlDate(java.util.Date dateToConvert) {
        return new Date(dateToConvert.getTime()).toLocalDate();
    }

    private void closeWindow() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void handleSaveButtonAction(ActionEvent event) {
        try {
            // Validation basique
            if (!validateInputs()) {
                return;
            }

            // Mise à jour des champs communs
            currentOffer.setNom(nomField.getText());
            currentOffer.setDescription(descriptionField.getText());
            currentOffer.setDuree_validite(Date.valueOf(dureeValiditeField.getValue()));
            currentOffer.setEtat(Etato.valueOf(etatField.getValue()));

            // Mise à jour via le service existant (champs de base seulement)
            OffreService offreService = new OffreService();
            offreService.update(currentOffer);

            // Mise à jour manuelle des champs spécifiques
            if (currentOffer instanceof OffreCoach) {
                updateOffreCoachFields((OffreCoach) currentOffer);
            } else if (currentOffer instanceof OffreProduit) {
                updateOffreProduitFields((OffreProduit) currentOffer);
            }

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Offre mise à jour !");
            closeWindow();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Valeurs numériques invalides");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur technique: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateInputs() {
        if (nomField.getText().isEmpty()
                || descriptionField.getText().isEmpty()
                || dureeValiditeField.getValue() == null
                || etatField.getValue() == null) {

            showAlert(Alert.AlertType.WARNING, "Champs manquants", "Remplissez tous les champs obligatoires");
            return false;
        }
        return true;
    }

    private void updateOffreCoachFields(OffreCoach offreCoach) throws Exception {
        // Mise à jour directe en base pour les champs spécifiques
        String sql = "UPDATE OffreCoach SET "
                + "idCoach = ?, nouveauTarif = ?, reservationMax = ? "
                + "WHERE id = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, Integer.parseInt(idField.getText()));
            pst.setDouble(2, Double.parseDouble(nouveauPrixTarifField.getText()));
            pst.setInt(3, Integer.parseInt(quantiteReservationMaxField.getText()));
            pst.setInt(4, currentOffer.getId());

            pst.executeUpdate();
        }
    }

    private void updateOffreProduitFields(OffreProduit offreProduit) throws Exception {
        // Mise à jour directe en base pour les champs spécifiques
        String sql = "UPDATE OffreProduit SET "
                + "idProduit = ?, nouveauPrix = ?, quantiteMax = ? "
                + "WHERE id = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, Integer.parseInt(idField.getText()));
            pst.setDouble(2, Double.parseDouble(nouveauPrixTarifField.getText()));
            pst.setInt(3, Integer.parseInt(quantiteReservationMaxField.getText()));
            pst.setInt(4, currentOffer.getId());

            pst.executeUpdate();
        }
    }

    @FXML
    private void handleCancelButtonAction(ActionEvent event) {
        // Fermer la fenêtre actuelle
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    //ROOT

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