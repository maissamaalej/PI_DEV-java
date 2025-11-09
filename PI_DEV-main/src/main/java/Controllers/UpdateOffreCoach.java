package Controllers;

import Models.OffreCoach;
import Models.Etato;
import Services.OffreCoachService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.sql.Date;

public class UpdateOffreCoach {

    @FXML
    private TextField nomField;

    @FXML
    private TextField descriptionField;

    @FXML
    private DatePicker dureeValiditeField;

    @FXML
    private ChoiceBox<String> etatField;

    @FXML
    private TextField nouveauTarifField;

    private OffreCoachService offreCoachService = new OffreCoachService();
    private OffreCoach offreCoach;

    public UpdateOffreCoach() throws SQLException {
    }

    public void setOffreCoach(OffreCoach offreCoach) {
        this.offreCoach = offreCoach;
        nomField.setText(offreCoach.getNom());
        descriptionField.setText(offreCoach.getDescription());

        if (offreCoach.getDuree_validite() != null) {
            dureeValiditeField.setValue(
                    new Date(offreCoach.getDuree_validite().getTime()).toLocalDate()
            );
        }

        etatField.setValue(offreCoach.getEtat().name());
        nouveauTarifField.setText(String.valueOf(offreCoach.getNouveauTarif()));
    }

    @FXML
    private void handleUpdateButtonAction(ActionEvent event) {
        try {
            offreCoach.setNom(nomField.getText().trim());
            offreCoach.setDescription(descriptionField.getText().trim());

            if (dureeValiditeField.getValue() != null) {
                offreCoach.setDuree_validite(Date.valueOf(dureeValiditeField.getValue()));
            }

            offreCoach.setEtat(Etato.valueOf(etatField.getValue()));
            offreCoach.setNouveauTarif(Double.parseDouble(nouveauTarifField.getText().trim()));

            // Tentative de mise à jour dans la base de données
            offreCoachService.update(offreCoach);

            // Afficher un message de succès
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Offre Coach mise à jour avec succès.");
        } catch (Exception e) {
            // Afficher une alerte en cas d'erreur
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de la mise à jour.");
            e.printStackTrace(); // Affiche l'erreur dans la console pour le débogage
        }
    }

    // Méthode pour afficher des alertes
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
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