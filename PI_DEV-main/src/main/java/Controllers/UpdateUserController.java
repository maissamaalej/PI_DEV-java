package Controllers;

import Models.*;
import Services.UserService;
import Utils.MyDb;
import Utils.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateUserController {
        private User currentUser =  Session.getInstance().getCurrentUser();

        @FXML
        private AnchorPane adherentFields1;

        @FXML
        private TextField adresseCreateur;

        @FXML
        private TextField adresseInvestisseur;

        @FXML
        private TextField age1;

        @FXML
        private TextField anneeExperience;

        @FXML
        private Circle circle;

        @FXML
        private AnchorPane coachFields;

        @FXML
        private AnchorPane createurEventFields;

        @FXML
        private TextField descriptionCreateur;

        @FXML
        private TextField descriptionInvestisseur;

        @FXML
        private ComboBox<?> genre1;

        @FXML
        private AnchorPane investisseurFields;

        @FXML
        private AnchorPane main_form;

        @FXML
        private ComboBox<?> niveauActivite1;

        @FXML
        private TextField nomEntreprise;

        @FXML
        private TextField nomOrganisation;

        @FXML
        private ComboBox<?> objectifPersonnel1;

        @FXML
        private ImageView photo;

        @FXML
        private TextField poids1;

        @FXML
        private AnchorPane signup_form;

        @FXML
        private ComboBox<?> specialite;

        @FXML
        private StackPane stack;

        @FXML
        private TextField su_email;

        @FXML
        private TextField su_nom;

        @FXML
        private TextField su_prenom;

        @FXML
        private Button su_signupbutton;

        @FXML
        private TextField taille1;

        @FXML
        private TextField telephoneCreateur;

        @FXML
        private TextField telephoneInvestisseur;

        @FXML
        private Button uploadButton1;
//        @FXML
//        public void inventoryImportBtn() {
//                FileChooser openFile = new FileChooser();
//                openFile.getExtensionFilters().add(new FileChooser.ExtensionFilter("Open Image File", "*.png", "*.jpg"));
//
//                File file = openFile.showOpenDialog(main_form.getScene().getWindow());
//
//                if (file != null) {
//                        System.out.println("Chemin de l'image : " + file.toURI().toString()); // Vérifiez le chemin
//                        Image image = new Image(file.toURI().toString());
//
//                        // Vérifiez que l'image est chargée correctement
//                        if (image.isError()) {
//                                System.out.println("Erreur lors du chargement de l'image : " + image.getException().getMessage());
//                                return; // Arrêtez l'exécution si l'image n'est pas chargée
//                        }
//                        circle.setFill (new ImagePattern(image) );
//
//        }}


        public void setUserData(User user) {
                this.currentUser = user;

                // Charger les informations générales
                su_nom.setText(currentUser.getNom());
                su_prenom.setText(currentUser.getPrenom());
                su_email.setText(currentUser.getEmail());

                // Cacher tous les panneaux spécifiques
                adherentFields1.setVisible(false);
                coachFields.setVisible(false);
                investisseurFields.setVisible(false);
                createurEventFields.setVisible(false);

                // Déterminer le type d'utilisateur et afficher les champs correspondants
                if (currentUser instanceof Adherent) {
                        System.out.println("User is an Adhérent");
                        adherentFields1.setVisible(true);
                        Adherent adherentUser = (Adherent) currentUser;
                        age1.setText(String.valueOf(adherentUser.getAge()));
                        taille1.setText(String.valueOf(adherentUser.getTaille()));
                        poids1.setText(String.valueOf(adherentUser.getPoids()));

                        // Vérification de la sélection avant d'appeler toString()
                        if (genre1.getSelectionModel().getSelectedItem() != null) {
                                adherentUser.setGenre(GenreG.valueOf(genre1.getSelectionModel().getSelectedItem().toString()));
                        }
                        if (objectifPersonnel1.getSelectionModel().getSelectedItem() != null) {
                                adherentUser.setObjectif_personnelle(ObjP.valueOf(objectifPersonnel1.getSelectionModel().getSelectedItem().toString()));
                        }
                        if (niveauActivite1.getSelectionModel().getSelectedItem() != null) {
                                adherentUser.setNiveau_activites(NiveauA.valueOf(niveauActivite1.getSelectionModel().getSelectedItem().toString()));
                        }
                }
                else if (currentUser instanceof Coach) {
                        System.out.println("User is a Coach");
                        coachFields.setVisible(true);
                        Coach coachUser = (Coach) currentUser;
                        anneeExperience.setText(String.valueOf(coachUser.getAnnee_experience()));

                        // Vérification de la sélection avant d'appeler toString()
                        if (specialite.getSelectionModel().getSelectedItem() != null) {
                                coachUser.setSpecialite(SpecialiteC.valueOf(specialite.getSelectionModel().getSelectedItem().toString()));
                        }
                }
                else if (currentUser instanceof InvestisseurProduit) {
                        System.out.println("User is an Investisseur de produits");
                        investisseurFields.setVisible(true);
                        InvestisseurProduit investisseurUser = (InvestisseurProduit) currentUser;
                        nomEntreprise.setText(investisseurUser.getNom_entreprise());
                        descriptionInvestisseur.setText(investisseurUser.getDescription());
                        adresseInvestisseur.setText(investisseurUser.getAdresse());
                        telephoneInvestisseur.setText(investisseurUser.getTelephone());
                }
                else if (currentUser instanceof CreateurEvenement) {
                        System.out.println("User is a Créateur d'événements");
                        createurEventFields.setVisible(true);
                        CreateurEvenement createurUser = (CreateurEvenement) currentUser;
                        nomOrganisation.setText(createurUser.getNom_organisation());
                        descriptionCreateur.setText(createurUser.getDescription());
                        adresseCreateur.setText(createurUser.getAdresse());
                        telephoneCreateur.setText(createurUser.getTelephone());
                }
        }

        private void afficherPhoto() {
                if (currentUser != null && currentUser.getImage() != null && !currentUser.getImage().isEmpty()) {
                        File file = new File(currentUser.getImage()); // Convertir en fichier
                        if (file.exists()) {
                                Image img = new Image(file.toURI().toString());
                                circle.setFill (new ImagePattern(img));
                                photo.setImage(img); // Afficher l'image dans l'ImageView
                        } else {
                                System.out.println("⚠️ L'image n'existe pas : " + currentUser.getImage());
                        }
                } else {
                        System.out.println("⚠️ Aucun chemin d'image défini pour cet utilisateur.");
                }
        }
        @FXML
        public void inventoryImportBtn() throws SQLException {
                // Créer un FileChooser pour que l'utilisateur sélectionne une image
                FileChooser openFile = new FileChooser();
                openFile.getExtensionFilters().add(new FileChooser.ExtensionFilter("Open Image File", "*.png", "*.jpg"));

                // Ouvrir le dialogue pour choisir un fichier
                File file = openFile.showOpenDialog(main_form.getScene().getWindow());

                // Vérifier si un fichier a été sélectionné
                if (file != null) {
                        System.out.println("Chemin de l'image : " + file.toURI().toString());

                        // Lire le contenu du fichier comme un tableau de bytes
                        try {
                                byte[] imageData = Files.readAllBytes(file.toPath());
                                saveImageToDatabase(imageData); // Sauvegarder les données de l'image dans la base de données
                        } catch (IOException e) {
                                e.printStackTrace();
                                System.out.println("Erreur lors de la lecture de l'image.");
                        }
                }
        }
        private void saveImageToDatabase(byte[] imageData) throws SQLException {
                // Récupérer l'utilisateur actuel
                if (currentUser == null) {
                        System.out.println("❌ Aucun utilisateur connecté !");
                        return;
                }

                // Préparer la requête SQL pour mettre à jour l'image dans la base de données
                String sql = "UPDATE user SET image = ? WHERE id = ?";
                try (Connection conn = MyDb.getInstance().getConn();
                     PreparedStatement pst = conn.prepareStatement(sql)) {

                        pst.setBytes(1, imageData); // Définir les données binaires de l'image
                        pst.setInt(2, currentUser.getId()); // Définir l'ID de l'utilisateur

                        int rowsUpdated = pst.executeUpdate();
                        if (rowsUpdated > 0) {
                                System.out.println("✅ Image sauvegardée avec succès !");
                        } else {
                                System.out.println("❌ Échec de la sauvegarde de l'image.");
                        }
                } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("Erreur lors de la mise à jour de l'image : " + e.getMessage());
                }
        }
        private void loadImageFromDatabase(int userId) {
                String sql = "SELECT image FROM user WHERE id = ?";
                try (Connection conn = MyDb.getInstance().getConn();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {

                        stmt.setInt(1, userId);
                        ResultSet rs = stmt.executeQuery();

                        if (rs.next()) {
                                // Récupérer les données binaires de l'image
                                byte[] imageData = rs.getBytes("image");

                                if (imageData != null) {
                                        // Convertir les données binaires en Image JavaFX
                                        Image image = new Image(new ByteArrayInputStream(imageData));
                                        circle.setFill(new ImagePattern(image)); // Afficher l'image dans un cercle
                                        photo.setImage(image); // Afficher l'image dans un ImageView
                                } else {
                                        System.out.println("⚠️ Aucune image trouvée pour cet utilisateur.");
                                }
                        }
                } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("Erreur lors du chargement de l'image : " + e.getMessage());
                }
        }

//        @FXML
//        public void saveUpdatedUser() throws SQLException {
//                if (currentUser == null) {
//                        System.out.println("❌ Aucun utilisateur sélectionné !");
//                        return;
//                }
//
//                boolean success = false;
//                 // Récupère la connexion depuis MyDb
//
//
//
//
//                // Crée l'instance de UserService si la connexion est valide
//                UserService userService = new UserService();
//
//                // Mise à jour de l'utilisateur avec les informations communes
//                currentUser.setNom(su_nom.getText());
//                currentUser.setPrenom(su_prenom.getText());
//                currentUser.setEmail(su_email.getText());
//
//                success = userService.updateUser(currentUser);
//
//                // Vérifie que la mise à jour de l'utilisateur a réussi
//                if (success) {
//                        // Mettre à jour les champs spécifiques en fonction du type d'utilisateur
//                        if (currentUser instanceof Adherent) {
//                                Adherent adherentUser = (Adherent) currentUser;
//                                adherentUser.setAge(Integer.parseInt(age1.getText()));
//                                adherentUser.setTaille(Float.parseFloat(taille1.getText()));
//                                adherentUser.setPoids(Float.parseFloat(poids1.getText()));
//                                adherentUser.setGenre((GenreG) genre1.getSelectionModel().getSelectedItem());
//                                adherentUser.setObjectif_personnelle((ObjP) objectifPersonnel1.getSelectionModel().getSelectedItem());
//                                adherentUser.setNiveau_activites((NiveauA) niveauActivite1.getSelectionModel().getSelectedItem());
//
//                                success = userService.updateAdherentWithUser(adherentUser);
//                        } else if (currentUser instanceof Coach) {
//                                Coach coachUser = (Coach) currentUser;
//                                coachUser.setAnnee_experience(Integer.parseInt(anneeExperience.getText()));
//                                coachUser.setSpecialite((SpecialiteC) specialite.getSelectionModel().getSelectedItem());
//
//                                success = userService.updateCoachWithUser(coachUser);
//                        } else if (currentUser instanceof InvestisseurProduit) {
//                                InvestisseurProduit investisseurUser = (InvestisseurProduit) currentUser;
//                                investisseurUser.setNom_entreprise(nomEntreprise.getText());
//                                investisseurUser.setDescription(descriptionInvestisseur.getText());
//                                investisseurUser.setAdresse(adresseInvestisseur.getText());
//                                investisseurUser.setTelephone(telephoneInvestisseur.getText());
//
//                                success = userService.updateInvestisseurWithUser(investisseurUser);
//                        } else if (currentUser instanceof CreateurEvenement) {
//                                CreateurEvenement createurUser = (CreateurEvenement) currentUser;
//                                createurUser.setNom_organisation(nomOrganisation.getText());
//                                createurUser.setDescription(descriptionCreateur.getText());
//                                createurUser.setAdresse(adresseCreateur.getText());
//                                createurUser.setTelephone(telephoneCreateur.getText());
//
//                                success = userService.updateCreateurEvenementWithUser(createurUser);
//                        }
//                }
//
//                if (success) {
//                        System.out.println("✅ Mise à jour réussie !");
//                } else {
//                        System.out.println("❌ Échec de la mise à jour !");
//                }
//        }


        @FXML
        public void saveUpdatedUser() throws SQLException {
                if (currentUser == null) {
                        System.out.println("❌ Aucun utilisateur sélectionné !");
                        return;
                }

                UserService userService = new UserService(); // Create a new instance with a fresh connection

                currentUser.setNom(su_nom.getText());
                currentUser.setPrenom(su_prenom.getText());
                currentUser.setEmail(su_email.getText());

                boolean success = userService.updateUser(currentUser);

                if (success) {
                        if (currentUser instanceof Adherent) {
                                Adherent adherentUser = (Adherent) currentUser;
                                adherentUser.setAge(Integer.parseInt(age1.getText()));
                                adherentUser.setTaille(Float.parseFloat(taille1.getText()));
                                adherentUser.setPoids(Float.parseFloat(poids1.getText()));
                                adherentUser.setGenre((GenreG) genre1.getSelectionModel().getSelectedItem());
                                adherentUser.setObjectif_personnelle((ObjP) objectifPersonnel1.getSelectionModel().getSelectedItem());
                                adherentUser.setNiveau_activites((NiveauA) niveauActivite1.getSelectionModel().getSelectedItem());

                                success = userService.updateAdherentWithUser(adherentUser);
                        } else if (currentUser instanceof Coach) {
                                Coach coachUser = (Coach) currentUser;
                                coachUser.setAnnee_experience(Integer.parseInt(anneeExperience.getText()));

                                // Ensure that a valid selection is made for specialite
                                Object selectedSpecialite = specialite.getSelectionModel().getSelectedItem();
                                if (selectedSpecialite != null) {
                                        coachUser.setSpecialite(SpecialiteC.valueOf(selectedSpecialite.toString()));
                                } else {
                                        System.out.println("⚠️ Aucune spécialité sélectionnée !");
                                }
                                success = userService.updateCoachWithUser(coachUser);
                        } else if (currentUser instanceof InvestisseurProduit) {
                                InvestisseurProduit investisseurUser = (InvestisseurProduit) currentUser;
                                investisseurUser.setNom_entreprise(nomEntreprise.getText());
                                investisseurUser.setDescription(descriptionInvestisseur.getText());
                                investisseurUser.setAdresse(adresseInvestisseur.getText());
                                investisseurUser.setTelephone(telephoneInvestisseur.getText());

                                success = userService.updateInvestisseurWithUser(investisseurUser);
                        } else if (currentUser instanceof CreateurEvenement) {
                                CreateurEvenement createurUser = (CreateurEvenement) currentUser;
                                createurUser.setNom_organisation(nomOrganisation.getText());
                                createurUser.setDescription(descriptionCreateur.getText());
                                createurUser.setAdresse(adresseCreateur.getText());
                                createurUser.setTelephone(telephoneCreateur.getText());

                                success = userService.updateCreateurEvenementWithUser(createurUser);
                        }
                }

                // Display an alert based on the success status
                Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
                alert.setTitle("Résultat de la mise à jour");
                alert.setHeaderText(null);
                alert.setContentText(success ? "Mise à jour réussie !" : "Échec de la mise à jour.");

                // Show the alert and wait for user response
                alert.showAndWait();

                // If the update was successful, redirect to the profile page
                if (success) {
                        try {
                                // Load the profile page FXML file
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Profil.fxml")); // Adjust the path as needed
                                Parent root = loader.load();

                                // Get the controller of the loaded FXML
                                 // Pass the current user to the profile controller

                                // Replace the current scene with the profile scene
                                Scene currentScene = su_signupbutton.getScene();
                                currentScene.setRoot(root);
                        } catch (IOException e) {
                                e.printStackTrace();
                                System.out.println("❌ Erreur lors du chargement de la page de profil.");
                        }

        }
        }
        @FXML
        public void initialize() throws URISyntaxException {

                afficherPhoto();
                setUserData(currentUser);

        }

    }


