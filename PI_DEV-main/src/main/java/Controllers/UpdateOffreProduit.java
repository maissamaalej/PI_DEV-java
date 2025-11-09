package Controllers;

import Models.OffreProduit;
import Models.Etato;
import Services.OffreProduitService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.sql.Date;

public class UpdateOffreProduit {

    @FXML
    private TextField nomField;

    @FXML
    private TextField descriptionField;

    @FXML
    private DatePicker dureeValiditeField;

    @FXML
    private ChoiceBox<String> etatField;

    @FXML
    private TextField nouveauPrixField;

    @FXML
    private TextField quantiteMaxField;

    private OffreProduitService offreProduitService = new OffreProduitService();
    private OffreProduit offreProduit;

    public UpdateOffreProduit() throws SQLException {
    }

    // Méthode pour initialiser l'offre produit dans le formulaire
    public void setOffreProduit(OffreProduit offreProduit) {
        this.offreProduit = offreProduit;
        nomField.setText(offreProduit.getNom());
        descriptionField.setText(offreProduit.getDescription());

        // Conversion de java.sql.Date en java.time.LocalDate
        if (offreProduit.getDuree_validite() != null) {
            dureeValiditeField.setValue(offreProduit.getDuree_validite().toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate());
        }



        etatField.setValue(offreProduit.getEtat().name());
        nouveauPrixField.setText(String.valueOf(offreProduit.getNouveauPrix()));
        quantiteMaxField.setText(String.valueOf(offreProduit.getQuantiteMax()));
    }

    @FXML
    private void handleUpdateButtonAction(ActionEvent event) {
        try {
            offreProduit.setNom(nomField.getText().trim());
            offreProduit.setDescription(descriptionField.getText().trim());

            // Conversion de LocalDate en java.sql.Date
            LocalDate selectedDate = dureeValiditeField.getValue();
            if (selectedDate != null) {
                offreProduit.setDuree_validite(Date.valueOf(selectedDate));
            }

            offreProduit.setEtat(Etato.valueOf(etatField.getValue()));
            offreProduit.setNouveauPrix(Double.parseDouble(nouveauPrixField.getText().trim()));
            offreProduit.setQuantiteMax(Integer.parseInt(quantiteMaxField.getText().trim()));

            // Mise à jour dans la base de données
            offreProduitService.update(offreProduit);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Offre Produit mise à jour avec succès.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de la mise à jour.");
            e.printStackTrace();
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