package Controllers;

import Models.OffreProduit;
import Services.TwilioSMSService;
import Models.Etato;
import Services.OffreProduitService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.sql.Date;

public class AddOffreProduit {

    @FXML
    private TextField nomField;

    @FXML
    private TextField descriptionField;

    @FXML
    private DatePicker dureeValiditeField;

    @FXML
    private ChoiceBox<String> etatField;

    @FXML
    private TextField idProduitField;

    @FXML
    private TextField nouveauPrixField;

    @FXML
    private TextField quantiteMaxField;

    private OffreProduitService offreProduitService = new OffreProduitService();

    public AddOffreProduit() throws SQLException {
    }

    @FXML
    public void initialize() {
        etatField.getItems().addAll("ACTIF", "INACTIF");
        etatField.setValue("ACTIF");
    }

    @FXML
    private void handleSaveButtonAction(ActionEvent event) {
        try {
            String nom = nomField.getText().trim();
            String description = descriptionField.getText().trim();
            LocalDate dureeValidite = dureeValiditeField.getValue();
            String etat = etatField.getValue();

            // Vérifier si les champs obligatoires sont vides
            if (nom.isEmpty() || description.isEmpty() || dureeValidite == null || idProduitField.getText().trim().isEmpty() || nouveauPrixField.getText().trim().isEmpty() || quantiteMaxField.getText().trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Formulaire incomplet", "Tous les champs sont obligatoires.");
                return;
            }

            // Vérifier la longueur des textes
            if (nom.length() > 100) {
                showAlert(Alert.AlertType.ERROR, "Nom trop long", "Le nom ne doit pas dépasser 100 caractères.");
                return;
            }
            if (description.length() > 500) {
                showAlert(Alert.AlertType.ERROR, "Description trop longue", "La description ne doit pas dépasser 500 caractères.");
                return;
            }

            // Vérifier que la date de validité n'est pas dans le passé
            if (dureeValidite.isBefore(LocalDate.now())) {
                showAlert(Alert.AlertType.ERROR, "Date invalide", "La date de validité doit être aujourd'hui ou plus tard.");
                return;
            }

            // Vérifier les valeurs numériques
            int idProduit;
            double nouveauPrix;
            int quantiteMax;
            try {
                idProduit = Integer.parseInt(idProduitField.getText().trim());
                if (idProduit <= 0) {
                    showAlert(Alert.AlertType.ERROR, "ID Produit invalide", "L'ID Produit doit être un nombre positif.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "ID Produit invalide", "Veuillez entrer un ID Produit valide.");
                return;
            }

            try {
                nouveauPrix = Double.parseDouble(nouveauPrixField.getText().trim());
                if (nouveauPrix < 0) {
                    showAlert(Alert.AlertType.ERROR, "Prix invalide", "Le prix doit être un nombre positif.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Prix invalide", "Veuillez entrer un prix valide.");
                return;
            }

            try {
                quantiteMax = Integer.parseInt(quantiteMaxField.getText().trim());
                if (quantiteMax <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Quantité invalide", "La quantité maximale doit être un nombre positif.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Quantité invalide", "Veuillez entrer une quantité valide.");
                return;
            }

            // Vérifier l'état de l'offre
            Etato etatEnum;
            try {
                etatEnum = Etato.valueOf(etat);
            } catch (IllegalArgumentException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur d'état", "L'état sélectionné est invalide.");
                return;
            }

            // Création de l'offre
            OffreProduit offreProduit = new OffreProduit(0, nom, description, Date.valueOf(dureeValidite), etatEnum, idProduit, nouveauPrix, quantiteMax, 0);
            offreProduitService.create(offreProduit);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Offre Produit ajoutée avec succès.");
            TwilioSMSService.sendSms("+21656452244", "Bonjour ! vous avez ajouter un nouveau produit 🚀");

            clearForm();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout de l'offre Produit.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelButtonAction(ActionEvent event) {
        // Logique pour annuler l'opération et revenir à la page précédente
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Clear the form after successful offer creation
    private void clearForm() {
        nomField.clear();
        descriptionField.clear();
        dureeValiditeField.setValue(null);
        etatField.setValue("ACTIF");
        idProduitField.clear();
        nouveauPrixField.clear();
        quantiteMaxField.clear();
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