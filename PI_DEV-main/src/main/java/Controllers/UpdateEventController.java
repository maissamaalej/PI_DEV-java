
package Controllers;

import Models.EtatEvenement;
import Models.Evenement;
import Services.CreateurEvenementService;
import Services.EvenementService;
import Utils.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.time.LocalDate;

public class UpdateEventController {

    @FXML
    private ImageView IMG;

    @FXML
    private DatePicker dateD;

    @FXML
    private DatePicker dateF;

    @FXML
    private TextArea descriptionField;

    @FXML
    private ChoiceBox<String> etatField;

    @FXML
    private TextField lieuField;

    @FXML
    private TextField mxField;

    @FXML
    private TextField organisateurField;

    @FXML
    private TextField prixField;

    @FXML
    private Button saveButton;

    @FXML
    private TextField titreField;

    @FXML
    private TextField typeField;

    @FXML
    private Button uploadButton;

    private byte[] newImageData = null; // Stocke l'image sélectionnée
    private Evenement event;
    private EvenementService evenementService = new EvenementService();
    private MyEvents parentController; // Pour rafraîchir après modification

    public UpdateEventController() throws SQLException {
    }

    @FXML
    public void initialize() {
        etatField.getItems().addAll("ACTIF", "EXPIRE");
        etatField.setValue("ACTIF");
    }

    public void setEventData(Evenement event, MyEvents parentController) {
        this.event = event;
        this.parentController = parentController;

        // Pré-remplir les champs avec les infos existantes
        titreField.setText(event.getTitre());
        descriptionField.setText(event.getDescription());
        lieuField.setText(event.getLieu());
        prixField.setText(String.valueOf(event.getPrix()));
        typeField.setText(event.getType());
        organisateurField.setText(event.getOrganisateur());
        mxField.setText(String.valueOf(event.getCapaciteMaximale()));
        dateD.setValue(event.getDateDebut());
        dateF.setValue(event.getDateFin());
        if (event.getImage() != null) {
            ByteArrayInputStream bis = new ByteArrayInputStream(event.getImage());
            IMG.setImage(new Image(bis));
        }
    }

    @FXML
    private void uploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(uploadButton.getScene().getWindow());
        if (selectedFile != null) {
            try {
                // Charger l'image dans l'ImageView
                Image image = new Image(selectedFile.toURI().toString());
                IMG.setImage(image);

                // Convertir l'image en byte[] pour la base de données
                newImageData = Files.readAllBytes(selectedFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    @FXML
//    private void saveEvent() {
//        try {
//            // Validation des champs
//            String titre = titreField.getText().trim();
//            String description = descriptionField.getText().trim();
//            LocalDate dateDebut = dateD.getValue();
//            LocalDate dateFin = dateF.getValue();
//            String lieu = lieuField.getText().trim();
//            String organisateur = organisateurField.getText().trim();
//            String type = typeField.getText().trim();
//            String etat = etatField.getValue();
//
//            if (titre.isEmpty() || description.isEmpty() || dateDebut == null || dateFin == null || lieu.isEmpty() || organisateur.isEmpty() || type.isEmpty()) {
//                showAlert(Alert.AlertType.ERROR, "Formulaire incomplet", "Tous les champs sont obligatoires.");
//                return;
//            }
//
//            if (titre.length() > 100) {
//                showAlert(Alert.AlertType.ERROR, "Titre trop long", "Le titre ne doit pas dépasser 100 caractères.");
//                return;
//            }
//
//            if (description.length() > 500) {
//                showAlert(Alert.AlertType.ERROR, "Description trop longue", "La description ne doit pas dépasser 500 caractères.");
//                return;
//            }
//
//
//            if (dateFin.isBefore(dateDebut)) {
//                showAlert(Alert.AlertType.ERROR, "Erreur de date", "La date de fin doit être après la date de début.");
//                return;
//            }
//
//            double prix;
//            try {
//                prix = Double.parseDouble(prixField.getText().trim());
//                if (prix < 0) {
//                    showAlert(Alert.AlertType.ERROR, "Prix invalide", "Le prix doit être un nombre positif.");
//                    return;
//                }
//            } catch (NumberFormatException e) {
//                showAlert(Alert.AlertType.ERROR, "Prix invalide", "Veuillez entrer un prix valide.");
//                return;
//            }
//
//            int capaciteMaximale;
//            try {
//                capaciteMaximale = Integer.parseInt(mxField.getText().trim());
//                if (capaciteMaximale <= 0) {
//                    showAlert(Alert.AlertType.ERROR, "Capacité invalide", "La capacité maximale doit être un nombre positif.");
//                    return;
//                }
//            } catch (NumberFormatException e) {
//                showAlert(Alert.AlertType.ERROR, "Capacité invalide", "Veuillez entrer une capacité valide.");
//                return;
//            }
//
//            EtatEvenement etatEnum;
//            try {
//                etatEnum = EtatEvenement.valueOf(etat);
//            } catch (IllegalArgumentException e) {
//                showAlert(Alert.AlertType.ERROR, "Erreur d'état", "L'état sélectionné est invalide.");
//                return;
//            }
//
//            // Mettre à jour l'événement
//            event.setTitre(titre);
//            event.setDescription(description);
//            event.setLieu(lieu);
//            event.setPrix(prix);
//            event.setDateDebut(dateDebut);
//            event.setDateFin(dateFin);
//            event.setEtat(etatEnum);
//            event.setCapaciteMaximale(capaciteMaximale);
//            event.setType(type);
//            event.setOrganisateur(organisateur);
//
//            // Si une nouvelle image a été sélectionnée, on la met à jour
//            if (newImageData != null) {
//                event.setImage(newImageData);
//            }
//
//            // Mettre à jour dans la base de données
//            evenementService.update(event);
//
//            // Fermer la fenêtre de modification
//            Stage stage = (Stage) saveButton.getScene().getWindow();
//            stage.close();
//
//            // Rafraîchir la liste des événements
//            parentController.loadEvents();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            showAlert(Alert.AlertType.ERROR, "Erreur interne", "Une erreur s'est produite pendant la mise à jour.");
//        }
//    }



    @FXML
    private void saveEvent() {
        try {
            // Validation des champs
            String titre = titreField.getText().trim();
            String description = descriptionField.getText().trim();
            LocalDate dateDebut = dateD.getValue();
            LocalDate dateFin = dateF.getValue();
            String lieu = lieuField.getText().trim();
            String organisateur = organisateurField.getText().trim();
            String type = typeField.getText().trim();
            String etat = etatField.getValue();

            if (titre.isEmpty() || description.isEmpty() || dateDebut == null || dateFin == null || lieu.isEmpty() || organisateur.isEmpty() || type.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Formulaire incomplet", "Tous les champs sont obligatoires.");
                return;
            }

            if (titre.length() > 100) {
                showAlert(Alert.AlertType.ERROR, "Titre trop long", "Le titre ne doit pas dépasser 100 caractères.");
                return;
            }

            if (description.length() > 500) {
                showAlert(Alert.AlertType.ERROR, "Description trop longue", "La description ne doit pas dépasser 500 caractères.");
                return;
            }

            if (dateFin.isBefore(dateDebut)) {
                showAlert(Alert.AlertType.ERROR, "Erreur de date", "La date de fin doit être après la date de début.");
                return;
            }

            double prix;
            try {
                prix = Double.parseDouble(prixField.getText().trim());
                if (prix < 0) {
                    showAlert(Alert.AlertType.ERROR, "Prix invalide", "Le prix doit être un nombre positif.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Prix invalide", "Veuillez entrer un prix valide.");
                return;
            }

            int capaciteMaximale;
            try {
                capaciteMaximale = Integer.parseInt(mxField.getText().trim());
                if (capaciteMaximale <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Capacité invalide", "La capacité maximale doit être un nombre positif.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Capacité invalide", "Veuillez entrer une capacité valide.");
                return;
            }

            EtatEvenement etatEnum;
            try {
                etatEnum = EtatEvenement.valueOf(etat);
            } catch (IllegalArgumentException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur d'état", "L'état sélectionné est invalide.");
                return;
            }

            // Mettre à jour l'événement
            event.setTitre(titre);
            event.setDescription(description);
            event.setLieu(lieu);
            event.setPrix(prix);
            event.setDateDebut(dateDebut);
            event.setDateFin(dateFin);
            event.setEtat(etatEnum);
            event.setCapaciteMaximale(capaciteMaximale);
            event.setType(type);
            event.setOrganisateur(organisateur);

            // Si une nouvelle image a été sélectionnée, on la met à jour
            if (newImageData != null) {
                event.setImage(newImageData);
            }

            // Mettre à jour dans la base de données
            evenementService.update(event);

            // Rafraîchir la liste des événements sans fermer la fenêtre
            parentController.loadEvents();

            // Recharger les champs avec les nouvelles données
            setEventData(event, parentController);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "L'événement a été mis à jour avec succès.");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur interne", "Une erreur s'est produite pendant la mise à jour.");
        }
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
