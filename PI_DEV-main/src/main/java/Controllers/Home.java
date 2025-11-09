//package Controllers;
//
//import Services.*;
//import Utils.MyDb;
//import Utils.Session;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Node;
//import javafx.scene.Parent;
//import javafx.scene.control.Alert;
//import javafx.scene.control.Button;
//import javafx.scene.control.TextField;
//import javafx.scene.image.ImageView;
//
//public class Home {
//
//    @FXML
//    private Button event;
//
//    @FXML
//    private Button home;
//
//    @FXML
//    private ImageView isearch;
//
//    @FXML
//    private ImageView logout;
//
//    @FXML
//    private Button offre;
//
//    @FXML
//    private Button parametre;
//
//    @FXML
//    private Button produit;
//
//    @FXML
//    private Button reclamation;
//
//    @FXML
//    private Button seance;
//
//    @FXML
//    private TextField search;
////ROOT
////    @FXML
////    void GoToEvent(ActionEvent actionEvent) {
////        try {
////            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddEvenement.fxml"));
////            Parent root = loader.load();
////            ((Button) actionEvent.getSource()).getScene().setRoot(root);
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////    }
//
//    @FXML
//    void GoToEvent(ActionEvent actionEvent) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Events.fxml"));
//            Parent root = loader.load();
//            ((Button) actionEvent.getSource()).getScene().setRoot(root);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    @FXML
//    void GoToHome(ActionEvent actionEvent) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/home.fxml"));
//            Parent root = loader.load();
//            ((Button) actionEvent.getSource()).getScene().setRoot(root);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    private InvestisseurProduitService InvestisseurService = new InvestisseurProduitService();
//    private Services.AdherentService AdherentService = new AdherentService();
//    private Services.CoachService CoachService = new CoachService();

//    @FXML
//    void GoToProduit(ActionEvent actionEvent) {
//        int id = Session.getInstance().getCurrentUser().getId();
//        String path = "";
//        try {
//            if (InvestisseurService.isInvestisseurProduit(id)) {
//                path = "/produit.fxml";
//            } else if(AdherentService.isAdherent(id) || CoachService.isCoach(id)) {
//                path = "/PanierClient.fxml";
//            }
//            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
//            Parent root = loader.load();
//            ((Button) actionEvent.getSource()).getScene().setRoot(root);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    @FXML
//    void GoToSeance(ActionEvent actionEvent) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajoutplanning.fxml"));
//            Parent root = loader.load();
//            ((Button) actionEvent.getSource()).getScene().setRoot(root);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    @FXML
//    void GoToRec(ActionEvent actionEvent) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserReclamation.fxml"));
//            Parent root = loader.load();
//            ((Button) actionEvent.getSource()).getScene().setRoot(root);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    @FXML
//    void GoToOffre(ActionEvent actionEvent) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddOffre.fxml"));
//            Parent root = loader.load();
//            ((Button) actionEvent.getSource()).getScene().setRoot(root);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
package Controllers;

import Models.Coach;
import Services.*;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Home {



    // Constantes pour les styles
    private static final String CARD_STYLE = "-fx-padding: 10; -fx-border-color: #ddd; -fx-background-color: #fff; " +
            "-fx-border-radius: 8; -fx-alignment: center; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);";
    private static final String IMAGE_CONTAINER_STYLE = "-fx-background-color: white; -fx-padding: 10;";
    private static final String NAME_LABEL_STYLE = "-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333;";
    private static final String INFO_LABEL_STYLE = "-fx-font-size: 12px; -fx-text-fill: #666;";

    // Composants FXML
    @FXML private Button event;
    @FXML private Button home;
    @FXML private ImageView isearch;
    @FXML private ImageView logout;
    @FXML private Button offre;
    @FXML private Button parametre;
    @FXML private Button produit;
    @FXML private Button reclamation;
    @FXML private Button seance;
    @FXML private TextField search;
    @FXML private ImageView imagecoach;
    @FXML private Label namecoach;
    @FXML private Label specialite;
    @FXML private HBox coachcard;
    @FXML private ScrollPane scrollPane;

    // Services
    private final CoachService coachService = new CoachService();
    private final CreateurEvenementService createurEvenementService = new CreateurEvenementService();

    public Home() throws SQLException {
    }

    @FXML
    public void initialize() throws SQLException {
        loadCoaches();
    }

    /**
     * Charge et affiche les coachs valides.
     */
    private void loadCoaches() throws SQLException {
        if (scrollPane == null || coachcard == null) {
            System.out.println("Erreur : scrollPane ou coachcard est null !");
            return;
        }

        // Récupérer tous les coachs valides
        List<Coach> coaches = coachService.getAllValide();

        // Filtrer pour ne garder que ceux qui ont un planning
        PlanningService planningService = new PlanningService();
        List<Coach> coachesAvecPlanning = new ArrayList<>();
        for (Coach coach : coaches) {
            if (planningService.getPlanningByCoachId(coach.getId()) != null) {
                coachesAvecPlanning.add(coach);
            }
        }

        // Mettre à jour l'affichage avec les coachs ayant un planning
        coachcard.getChildren().clear();
        coachcard.setSpacing(20);
        coachcard.setStyle("-fx-padding: 10; -fx-alignment: center-left;");
        coachcard.setPrefWidth(Math.min(coachesAvecPlanning.size(), 5) * 260); // Limite la largeur à 5 coachs
        coachcard.setPrefHeight(270);

        int maxCoachesToShow = 5;
        for (int i = 0; i < Math.min(coachesAvecPlanning.size(), maxCoachesToShow); i++) {
            VBox coachCard = createCoachCard(coachesAvecPlanning.get(i));
            coachcard.getChildren().add(coachCard);
        }

        // Si plus de 5 coachs ont un planning, ajouter un bouton "Voir plus"
        if (coachesAvecPlanning.size() > maxCoachesToShow) {
            Button voirPlusButton = new Button("Voir plus");
            voirPlusButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
            voirPlusButton.setOnAction(event -> afficherTousLesCoachs(coachesAvecPlanning));
            coachcard.getChildren().add(voirPlusButton);
        }

        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(coachcard);
    }


    private void afficherPaiementCoach(Coach coach) throws SQLException {
        int idUtilisateur = Session.getInstance().getCurrentUser().getId();
        AdherentService adherentService = new AdherentService();
        if (adherentService.isAdherent(idUtilisateur)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/PaiementCoachPlan.fxml"));
                Parent root = loader.load();

                PaiementCoachPlan controller = loader.getController();
                controller.setCoach(coach);

                Scene scene = new Scene(root);
                Stage stage = (Stage) coachcard.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Paiement de Planning du Coach");
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Seuls les adhérents peuvent accéder au paiement.");
        }
    }


    /**
     * Crée une carte visuelle pour un coach.
     */
    private VBox createCoachCard(Coach coach) {
        VBox card = new VBox(5);
        card.setStyle(CARD_STYLE);
        card.setPrefWidth(260);
        card.setPrefHeight(250);

        // Charger l'image du coach
        ImageView imageView = loadCoachImage(coach);

        // Conteneur pour centrer l'image
        VBox imageContainer = new VBox(imageView);
        imageContainer.setStyle(IMAGE_CONTAINER_STYLE);
        imageContainer.setPrefHeight(160);
        imageContainer.setAlignment(Pos.CENTER);

        // Créer les informations du coach
        VBox infoContainer = createCoachInfo(coach);

        // Ajouter les éléments à la carte
        card.getChildren().addAll(imageContainer, infoContainer);

        // Gérer le clic sur la carte
        card.setOnMouseClicked(event -> {
            try {
                afficherPaiementCoach(coach);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        return card;
    }
    private ImageView loadCoachImage(Coach coach) {
        ImageView imageView = new ImageView();
        String imagePath = "/img/" + coach.getImage();

        InputStream imageStream = getClass().getResourceAsStream(imagePath);
        if (imageStream != null) {
            imageView.setImage(new Image(imageStream));
        } else {
            System.out.println("Image non trouvée : " + imagePath);
            InputStream defaultImageStream = getClass().getResourceAsStream("/img/default-image.png");

            if (defaultImageStream != null) {
                imageView.setImage(new Image(defaultImageStream));
            } else {
                System.out.println("⚠ Erreur: L'image par défaut est aussi introuvable !");
            }
        }

        imageView.setFitWidth(150);
        imageView.setFitHeight(130);
        imageView.setPreserveRatio(true);

        return imageView;
    }

    /**
     * Crée les informations du coach (nom, spécialité, expérience).
     */
    private VBox createCoachInfo(Coach coach) {
        Label nameLabel = new Label(coach.getNom() + " " + coach.getPrenom());
        nameLabel.setStyle(NAME_LABEL_STYLE);

        Label specialiteLabel = new Label("Spécialité : " + coach.getSpecialite());
        specialiteLabel.setStyle(INFO_LABEL_STYLE);

        Label experienceLabel = new Label("Expérience : " + coach.getAnnee_experience() + " ans");
        experienceLabel.setStyle(INFO_LABEL_STYLE);

        VBox infoContainer = new VBox(3);
        infoContainer.setAlignment(Pos.CENTER);
        infoContainer.getChildren().addAll(nameLabel, specialiteLabel, experienceLabel);

        return infoContainer;
    }

    /**
     * Affiche la scène de paiement pour un coach sélectionné.
     */

    private void afficherTousLesCoachs(List<Coach> coaches) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TousLesCoachs.fxml"));
            Parent root = loader.load();

            TousLesCoachsController controller = loader.getController();
            controller.setCoaches(coaches);

            Scene scene = new Scene(root);
            Stage stage = (Stage) coachcard.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Tous les coachs");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //ROOT
//    @FXML
//    void GoToEvent(ActionEvent actionEvent) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddEvenement.fxml"));
//            Parent root = loader.load();
//            ((Button) actionEvent.getSource()).getScene().setRoot(root);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


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
    private InvestisseurProduitService InvestisseurService = new InvestisseurProduitService();
    private Services.AdherentService AdherentService = new AdherentService();
    private Services.CoachService CoachService = new CoachService();

    @FXML
    void GoToProduit(ActionEvent actionEvent) {
        int id = Session.getInstance().getCurrentUser().getId();
        String path = "";
        try {
            if (InvestisseurService.isInvestisseurProduit(id)) {
                path = "/produit.fxml";
            } else if(AdherentService.isAdherent(id) || CoachService.isCoach(id)) {
                path = "/PanierClient.fxml";
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
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
    void GoToProfile(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Profil.fxml"));
            Parent root = loader.load();
            ((Node) event.getSource()).getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void GoToChat(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chatbot .fxml"));
            Parent root = loader.load();
            ((Button) actionEvent.getSource()).getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
