package Controllers;

import Models.*;
import Utils.MyDb;
import Utils.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;


public class ProfilController {


        @FXML
        private Label AdresseO;

        @FXML
        private Label MDPpROFIL;

        @FXML
        private Label NP;

        @FXML
        private AnchorPane Organisation;

        @FXML
        private Label TelO;

        @FXML
        private AnchorPane adherent;

        @FXML
        private Label agee;

        @FXML
        private AnchorPane coach;

        @FXML
        private Label descO;

        @FXML
        private Label emailProfil;

        @FXML
        private AnchorPane entreprise;

        @FXML
        private Label experience;

        @FXML
        private Label genre;

        @FXML
        private Label idadresseentrep;

        @FXML
        private Label iddescE;

        @FXML
        private Label idnomentrep;

        @FXML
        private Label idtelE;

        @FXML
        private Label niveau;

        @FXML
        private Label nomO;

        @FXML
        private Label objectif;

        @FXML
        private ImageView photo;

        @FXML
        private Label poids;

        @FXML
        private Button resetMDPBUTTON;

        @FXML
        private Button retour;

        @FXML
        private Label specialite;

        @FXML
        private Label taille;

        @FXML
        private Button togglePasswordBtn;
    @FXML
    private Circle circle;

    private User currentUser =  Session.getInstance().getCurrentUser();

    private boolean isPasswordVisible = false;

    @FXML
    public void initialize() {
        System.out.println("ProfilController initialized!");
        System.out.println("Utilisateur récupéré de la session : " + currentUser);
        afficherInformations();
        getImageFromDatabase(currentUser.getId());
    }


    // Méthode pour charger l'image de l'utilisateur depuis la base de données
    private void getImageFromDatabase(int userId) {
        String sql = "SELECT image FROM user WHERE id = ?";
        try (Connection conn = MyDb.getInstance().getConn();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                String imagePath = rs.getString("image"); // récupère le chemin de l'image depuis la base de données
                loadImage(imagePath); // Charger l'image dans le cercle
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    private void loadImage(String imagePath) throws URISyntaxException {
        // Charger l'image depuis le dossier resources/img
        File imageFile = new File(getClass().getResource("/img/OIP.jpeg").toURI());

        if (imageFile.exists()) {

            // Charger l'image
            Image image = new Image(imageFile.toURI().toString());

            // Redimensionner l'image pour qu'elle s'adapte au cercle
            double circleRadius = circle.getRadius();  // Récupère le rayon du cercle
            double imageSize = Math.min(image.getWidth(), image.getHeight());  // Trouver la plus petite dimension de l'image

            // Calculer un facteur de mise à l'échelle pour garder le ratio de l'image
            double scaleFactor = circleRadius * 2 / imageSize;

            // Appliquer l'échelle à l'image
            Image scaledImage = new Image(imageFile.toURI().toString(), image.getWidth() * scaleFactor, image.getHeight() * scaleFactor, false, true);

            // Appliquer l'image au cercle
            circle.setFill(new ImagePattern(scaledImage));  // Remplir le cercle avec l'image redimensionnée
        } else {
            System.out.println("L'image est introuvable à ce chemin : /img/OIP.jpeg");
        }}


    private void afficherInformations() {
        System.out.println("Appel de afficherInformations()");
        System.out.println("Displaying information for user: " + currentUser);

        if (currentUser == null) {
            System.out.println("No user is currently logged in.");
            return;
        }

        // Common information
        NP.setText(currentUser.getNom() + " " + currentUser.getPrenom());
        emailProfil.setText(currentUser.getEmail());

        // Hide all specific panels
        adherent.setVisible(false);
        coach.setVisible(false);
        entreprise.setVisible(false);
        Organisation.setVisible(false);

        // Hide password by default
        MDPpROFIL.setText("********");

        // Gérer l'affichage en fonction du type d'utilisateur
        if (currentUser instanceof Adherent) {
            System.out.println("User is an Adhérent");
            adherent.setVisible(true);
            adherent.setManaged(true);
            Adherent adherentUser = (Adherent) currentUser;
            agee.setText(String.valueOf(adherentUser.getAge()));
            poids.setText(String.valueOf(adherentUser.getPoids()));
            taille.setText(String.valueOf(adherentUser.getTaille()));
            niveau.setText(adherentUser.getNiveau_activites().name());
            objectif.setText(adherentUser.getObjectif_personnelle().name());
            genre.setText(adherentUser.getGenre().name());
        }
        else if (currentUser instanceof Coach) {
            System.out.println("User is a Coach");
            coach.setVisible(true);
            coach.setManaged(true);
            Coach coachUser = (Coach) currentUser;
            experience.setText(String.valueOf(coachUser.getAnnee_experience()));
            specialite.setText(coachUser.getSpecialite().name());
        }
        else if (currentUser instanceof InvestisseurProduit) {
            System.out.println("User is an Investisseur de produits");
            entreprise.setVisible(true);
            entreprise.setManaged(true);
            InvestisseurProduit investisseurUser = (InvestisseurProduit) currentUser;
            idnomentrep.setText(investisseurUser.getNom_entreprise());
            iddescE.setText(investisseurUser.getDescription());
            idadresseentrep.setText(investisseurUser.getAdresse());
            idtelE.setText(investisseurUser.getTelephone());
        }
        else if (currentUser instanceof CreateurEvenement) {
            System.out.println("User is a Créateur d'événements");
            Organisation.setVisible(true);
            Organisation.setManaged(true);
            CreateurEvenement createurUser = (CreateurEvenement) currentUser;
            nomO.setText(createurUser.getNom_organisation());
            descO.setText(createurUser.getDescription());
            AdresseO.setText(createurUser.getAdresse());
            TelO.setText(createurUser.getTelephone());
        }



    // Toggle password visibility
        togglePasswordBtn.setOnAction(event -> togglePassword());
        System.out.println("Type réel de currentUser : " + currentUser.getClass().getName());}




    private void togglePassword() {
        if (isPasswordVisible) {
            MDPpROFIL.setText("********");
            togglePasswordBtn.setText("👁");
        } else {
            MDPpROFIL.setText(currentUser.getMDP());
            togglePasswordBtn.setText("🔒");
        }
        isPasswordVisible = !isPasswordVisible;
    }
    @FXML
    private void ouvrirResetPassword() {
        try {
            System.out.println("test");
            // Charger le fichier FXML de la réinitialisation du mot de passe
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/reset_password.fxml"));
            BorderPane root = loader.load();

            // Créer une nouvelle scène et appliquer le CSS
            Scene scene = new Scene(root, 400, 300);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/reset_password.css")).toExternalForm());

            // Ouvrir une nouvelle fenêtre
            Stage stage = new Stage();
            stage.setTitle("Réinitialisation du mot de passe");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void GoToUpdateProfile(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateUser.fxml"));
            Parent root = loader.load();
            ((Button) actionEvent.getSource()).getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}