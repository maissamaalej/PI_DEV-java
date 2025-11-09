package Controllers;

import Models.Coach;
import Services.CoachService;
import Services.CreateurEvenementService;
import Services.PlanningService;
import Utils.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

public class TousLesCoachsController {

    @FXML private GridPane gridPane;
    private final CoachService coachService = new CoachService();
    private final CreateurEvenementService createurEvenementService = new CreateurEvenementService();

    public TousLesCoachsController() throws SQLException {
    }

    public void setCoaches(List<Coach> coaches) {
        afficherCoachs(coaches);
    }

    private void afficherCoachs(List<Coach> coaches) {
        gridPane.getChildren().clear();  // Nettoyer le GridPane avant d'ajouter les coachs
        int colonnes = 5, ligne = 0, colonne = 0;

        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(15);  // Espacement horizontal
        gridPane.setVgap(15);
        gridPane.setPrefWidth(1000);
        gridPane.setMinWidth(800);
        gridPane.setMaxWidth(1000);

        System.out.println("Nombre de coachs reçus : " + coaches.size());

        for (Coach coach : coaches) {
            System.out.println("Ajout du coach : " + coach.getNom() + " " + coach.getPrenom());
            VBox coachCard = createCoachCard(coach);
            gridPane.add(coachCard, colonne, ligne);

            colonne++;
            if (colonne >= colonnes) {
                colonne = 0;
                ligne++;
            }
        }

        // Ajuster la hauteur du GridPane en fonction du nombre de lignes
        int cardHeight = 250; // Hauteur d'une carte
        int vgap = 15;
        gridPane.setPrefHeight((ligne + 1) * (cardHeight + vgap));
    }

    private VBox createCoachCard(Coach coach) {
        VBox card = new VBox(10);
        card.setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-background-color: #fff;-fx-border-radius: 10");
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(200);
        card.setPrefHeight(250);

        // Charger l'image avec une gestion d'erreur
        ImageView imageView = loadCoachImage(coach);

        Label nameLabel = new Label(coach.getNom() + " " + coach.getPrenom());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label specialtyLabel = new Label("Spécialité : " + coach.getSpecialite());
        Label experienceLabel = new Label("Expérience : " + coach.getAnnee_experience() + " ans");

        // ⭐ Créer le conteneur pour les étoiles
        HBox hboxNote = createStarRating((int) coach.getNote()); // Convertir la note en entier

        // Ajouter les éléments à la carte
        card.getChildren().addAll(imageView, nameLabel, specialtyLabel, experienceLabel, hboxNote);

        // Ajouter l'événement de clic pour ouvrir la scène de paiement
        card.setOnMouseClicked(event -> afficherPaiementCoach(coach));

        return card;
    }

    // ✅ Fonction pour générer les étoiles (uniquement pleines ou vides)
    private HBox createStarRating(int note) {
        HBox hboxNote = new HBox(2);  // Espacement entre les étoiles
        hboxNote.setAlignment(Pos.CENTER);

        for (int i = 0; i < 5; i++) {
            ImageView star = new ImageView();
            String starImagePath = (i < note) ? "/img/etoile.png" : "/img/etoilevide.png";

            InputStream starStream = getClass().getResourceAsStream(starImagePath);
            if (starStream != null) {
                star.setImage(new Image(starStream));
            }

            star.setFitWidth(16);
            star.setFitHeight(16);
            hboxNote.getChildren().add(star);
        }

        return hboxNote;
    }

    private ImageView loadCoachImage(Coach coach) {
        ImageView imageView = new ImageView();
        String imagePath = "/img/" + coach.getImage();
        InputStream imageStream = getClass().getResourceAsStream(imagePath);

        if (imageStream == null) {
            System.out.println("❌ Image introuvable : " + imagePath);
            imageStream = getClass().getResourceAsStream("/img/default-image.png"); // Image par défaut
        }

        if (imageStream != null) {
            imageView.setImage(new Image(imageStream));
            System.out.println("✅ Image chargée : " + imagePath);
        } else {
            System.out.println("⚠ Erreur : L'image par défaut est aussi introuvable !");
        }

        imageView.setFitWidth(120);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(true);
        return imageView;
    }
    private void afficherPaiementCoach(Coach coach) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PaiementCoachPlan.fxml"));
            Parent root = loader.load();

            PaiementCoachPlan controller = loader.getController();
            controller.setCoach(coach);

            Scene scene = new Scene(root);
            Stage stage = (Stage) gridPane.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Paiement de Planning du Coach");
            stage.show();
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
        int id = Session.getInstance().getCurrentUser().getId();
        String path = "";

        try {
            if (coachService.isCoach(id)) {
                PlanningService ps = new PlanningService();

                // Vérifie si le coach a déjà un planning
                if (ps.getPlanningByCoachId(id) != null) {
                    path = "/planning.fxml"; // Redirige vers la page de planning existant
                } else {
                    path = "/ajoutplanning.fxml"; // Redirige vers l'ajout de planning
                }

            } else {
                path = "/planningAdherent.fxml"; // Redirige les adhérents vers leur planning
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();
            ((Node) actionEvent.getSource()).getScene().setRoot(root);

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


    @FXML
    void GoToEvent(ActionEvent actionEvent) {
        int id = Session.getInstance().getCurrentUser().getId();
        String path = "";

        try {
            if (createurEvenementService.isCreateurEvenement(id)) {
                path = "/AddEvenement.fxml";
            } else {
                path = "/Events.fxml";
            }

            // Now load the determined path
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();
            ((Node) actionEvent.getSource()).getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
