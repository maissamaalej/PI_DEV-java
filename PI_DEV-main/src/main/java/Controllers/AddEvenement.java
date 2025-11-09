
package Controllers;

import Models.EtatEvenement;
import Models.Evenement;
import Services.CreateurEvenementService;
import Services.EvenementService;
import Utils.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import java.io.File;

import javafx.scene.image.ImageView;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class AddEvenement {

    @FXML
    private Button event;

    @FXML
    private Button home;

    @FXML
    private Button idButton;

    @FXML
    private Button idButton2;

    @FXML
    private TextField idCaoMax;

    @FXML
    private DatePicker idDateD;

    @FXML
    private DatePicker idDateF;

    @FXML
    private TextArea idDescription;

    @FXML
    private ChoiceBox<String> idEtat;

    @FXML
    private Button idImgButton;

    @FXML
    private Label idImgLabel;

    @FXML
    private TextField idLieu;

    @FXML
    private TextField idOrganisateur;

    @FXML
    private TextField idPrix;

    @FXML
    private TextField idTitle;

    @FXML
    private TextField idType;

    @FXML
    private ImageView isearch;

    @FXML
    private ImageView logout;

    @FXML
    private Button offre;

    @FXML
    private Button parametre;

    @FXML
    private Button produit;

    @FXML
    private Button reclamation;

    @FXML
    private Button seance;

    @FXML
    private TextField search;
    private File imageFile;
    private EvenementService evenementService = new EvenementService();

    public AddEvenement() throws SQLException {
    }

    // Initialize ChoiceBox with statuses
    @FXML
    public void initialize() {
        idEtat.getItems().addAll("ACTIF", "EXPIRE");
        idEtat.setValue("Etat:");
    }

    public void handleButtonClick(ActionEvent event) {
        try {
            String title = idTitle.getText().trim();
            String description = idDescription.getText().trim();
            LocalDate dateDebut = idDateD.getValue();
            LocalDate dateFin = idDateF.getValue();
            String lieu = idLieu.getText().trim();
            String organisateur = idOrganisateur.getText().trim();
            String type = idType.getText().trim();
            String etat = idEtat.getValue();

            // Vérifier si les champs obligatoires sont vides
            if (title.isEmpty() || description.isEmpty() || dateDebut == null || dateFin == null || lieu.isEmpty() || organisateur.isEmpty() || type.isEmpty()) {
                showAlert(AlertType.ERROR, "Formulaire incomplet", "Tous les champs sont obligatoires.");
                return;
            }

            // Vérifier la longueur des textes
            if (title.length() > 100) {
                showAlert(AlertType.ERROR, "Titre trop long", "Le titre ne doit pas dépasser 100 caractères.");
                return;
            }
            if (description.length() > 500) {
                showAlert(AlertType.ERROR, "Description trop longue", "La description ne doit pas dépasser 500 caractères.");
                return;
            }
            // Vérifier que la date de début n'est pas dans le passé
            if (dateDebut.isBefore(LocalDate.now())) {
                showAlert(AlertType.ERROR, "Date invalide", "La date de début doit être aujourd'hui ou plus tard.");
                return;
            }
            // Vérifier que la date de fin est après la date de début
            if (dateFin.isBefore(dateDebut)) {
                showAlert(AlertType.ERROR, "Erreur de date", "La date de fin doit être après la date de début.");
                return;
            }

            // Vérifier les valeurs numériques
            double prix;
            int capaciteMaximale;
            try {
                prix = Double.parseDouble(idPrix.getText().trim());
                if (prix < 0) {
                    showAlert(AlertType.ERROR, "Prix invalide", "Le prix doit être un nombre positif.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(AlertType.ERROR, "Prix invalide", "Veuillez entrer un prix valide.");
                return;
            }

            try {
                capaciteMaximale = Integer.parseInt(idCaoMax.getText().trim());
                if (capaciteMaximale <= 0) {
                    showAlert(AlertType.ERROR, "Capacité invalide", "La capacité maximale doit être un nombre positif.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(AlertType.ERROR, "Capacité invalide", "Veuillez entrer une capacité valide.");
                return;
            }

            // Vérifier l'état de l'événement
            EtatEvenement etatEnum;
            try {
                etatEnum = EtatEvenement.valueOf(etat);
            } catch (IllegalArgumentException e) {
                showAlert(AlertType.ERROR, "Erreur d'état", "L'état sélectionné est invalide.");
                return;
            }

            // Vérifier l'image
            byte[] imageBytes = null;
            if (imageFile != null) {
                try (FileInputStream fis = new FileInputStream(imageFile)) {
                    imageBytes = fis.readAllBytes();
                } catch (IOException e) {
                    showAlert(AlertType.ERROR, "Erreur d'image", "Impossible de lire l'image sélectionnée.");
                    return;
                }
            }
            int idCreateurEvenement = Session.getCurrentUser().getId();
            // Création de l'événement
            Evenement evenement = new Evenement(
                    title, description, dateDebut, dateFin, lieu, imageBytes, prix,
                    etatEnum, type, organisateur, capaciteMaximale,idCreateurEvenement
            );

            // Sauvegarde dans la base de données
            if (evenementService.create(evenement)) {
                showAlert(AlertType.INFORMATION, "Succès", "L'événement a été ajouté avec succès.");
                clearForm();
            } else {
                showAlert(AlertType.ERROR, "Erreur", "Impossible d'ajouter l'événement.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur interne", "Une erreur inattendue s'est produite.");
        }
    }

    // Handle image file upload
    public void handleImageUpload(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        imageFile = fileChooser.showOpenDialog(null);

        if (imageFile != null) {
            idImgLabel.setText(imageFile.getName()); // Display file name
        } else {
            idImgLabel.setText("Aucune image sélectionnée");
        }
    }




    // Utility method to show alert messages
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    // Clear the form after successful event creation
    private void clearForm() {
        idTitle.clear();
        idDescription.clear();
        idLieu.clear();
        idPrix.clear();
        idCaoMax.clear();
        idOrganisateur.clear();
        idType.clear();
        idDateD.setValue(null);
        idDateF.setValue(null);
        idEtat.setValue("ACTIF");
        idImgLabel.setText("Aucune image sélectionnée");
        imageFile = null;
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















//
//
//@FXML
//    void goToEventList(ActionEvent event) {
//
//    }
//
//    @FXML
//    void handleButtonClick(ActionEvent event) {
//
//    }
//
//    @FXML
//    void handleImageUpload(ActionEvent event) {
//
//    }
//
//}
