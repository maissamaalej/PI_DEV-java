package Controllers;

import Models.Adherent;
import Models.ObjP;
import Services.AdherentService;
import Services.CoachService;
import Services.CreateurEvenementService;
import Services.InvestisseurProduitService;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



public class DashboardController {

    @FXML
    private BarChart<String, Number> Barchart;

    @FXML
    private Label NBrInv;

    @FXML
    private Label NbREVENT;

    @FXML
    private Label NbrAdherent;

    @FXML
    private AnchorPane chart;

    @FXML
    private AnchorPane chart1;

    @FXML
    private Button event;

    @FXML
    private ImageView isearch;

    @FXML
    private ImageView logout;

    @FXML
    private Label nNbrCOACH;

    @FXML
    private Button offre;

    @FXML
    private Button parametre;

    @FXML
    private Button produit;

    @FXML
    private Button reclamation;

    @FXML
    private Button reclamation1;

    @FXML
    private Button seance;

    @FXML
    private TextField search;

    @FXML
    private AnchorPane upchart;
    @FXML
    private CategoryAxis X;

    @FXML
    private NumberAxis Y;
    @FXML
    private VBox chartContainer; // 📌 Conteneur dans lequel le PieChart sera affiché

    private List<Adherent> adherents = new ArrayList<>();


    AdherentService adherentService = new AdherentService();
    InvestisseurProduitService investisseurProduitService = new InvestisseurProduitService();
    CoachService coachService = new CoachService();
    CreateurEvenementService createurEvenementService = new CreateurEvenementService();

    public DashboardController() throws SQLException {
    }


    @FXML
    public void initialize() {
        afficherNombreAdherents();
        afficherNombreCoachs();
        afficherNombreEVENT();
        afficherNombreINV();
        afficherUserBarChart();
        System.out.println("🔄 Chargement des adhérents...");

        List<Adherent> adherents = adherentService.getAll();  // Appel de getAll() depuis AdherentService

        if (adherents == null || adherents.isEmpty()) {
            System.out.println("⚠ Aucun adhérent trouvé !");
            return;
        }

        System.out.println("✅ Nombre d'adhérents : " + adherents.size());

        // Calculer et afficher les objectifs
        Map<ObjP, Long> objectifsCount = adherents.stream()
                .collect(Collectors.groupingBy(Adherent::getObjectif_personnelle, Collectors.counting()));

        PieChart pieChart = createPieChart(objectifsCount);

        chartContainer.getChildren().clear(); // Vider le conteneur
        chartContainer.getChildren().add(pieChart);
    }

    // 🥧 **Création du PieChart avec des couleurs personnalisées**
    private PieChart createPieChart(Map<ObjP, Long> objectifsCount) {
        PieChart pieChart = new PieChart();

        for (Map.Entry<ObjP, Long> entry : objectifsCount.entrySet()) {
            PieChart.Data slice = new PieChart.Data(entry.getKey().toString(), entry.getValue());
            pieChart.getData().add(slice);
        }



        pieChart.setTitle("Répartition des objectifs des adhérents");
        return pieChart;
    }

    private void afficherUserBarChart() {
        // Récupérer les données depuis la base de données
        int nombreAdherents = adherentService.getNombreAdherents();
        int nombreCoachs = coachService.getNombreCoaches();
        int nombreInvestisseurs = investisseurProduitService.getNombreINV();
        int nombreCreateurs = createurEvenementService.getNombreEVENT();

        // Définir les labels des axes
        X.setLabel("Type d'Utilisateur");
        Y.setLabel("Nombre d'Utilisateurs");

        // Création de la série de données
        XYChart.Series<String, Number> series = new XYChart.Series<>();


        // Ajouter les données pour chaque type d'utilisateur
        series.getData().add(new XYChart.Data<>("Adhérents", nombreAdherents));
        series.getData().add(new XYChart.Data<>("Coachs", nombreCoachs));
        series.getData().add(new XYChart.Data<>("Investisseurs", nombreInvestisseurs));
        series.getData().add(new XYChart.Data<>("Créateurs", nombreCreateurs));

        // Ajouter la série au BarChart
        Barchart.getData().clear();
        Barchart.getData().add(series);

        // Appliquer les classes CSS aux barres
        Platform.runLater(() -> {
            // Attendre que le BarChart soit rendu avant d'accéder aux nœuds
            Barchart.applyCss();
            Barchart.layout();

            // Parcourir les barres pour appliquer les styles et les animations
            for (XYChart.Data<String, Number> data : series.getData()) {
                Node node = data.getNode();
                if (node != null) {
                    node.getStyleClass().add(getStyleClassForCategory(data.getXValue()));

                    // Ajouter un Tooltip avec le nom de la catégorie
                    Tooltip tooltip = new Tooltip(data.getXValue());
                    Tooltip.install(node, tooltip);

                    // Ajouter un Label au-dessus de la barre
                    Label label = new Label(data.getXValue());
                    label.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    label.setTranslateY(-20);  // Positionner au-dessus de la barre
                    label.setTranslateX(node.getBoundsInParent().getMinX() + node.getBoundsInParent().getWidth() / 2 - label.getWidth() / 2);

                    // Ajouter le label à la scène
                    ((Group) Barchart.getParent()).getChildren().add(label);

                    // Animer la barre pour qu'elle monte de 0 à sa hauteur finale
                    animateBar(node);
                }
            }
        });
    }

    private void animateBar(Node node) {
        if (node instanceof Rectangle) {
            Rectangle bar = (Rectangle) node;

            // Initialiser la barre à une hauteur de 0
            bar.setScaleY(0);

            // Créer une animation de montée
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(500), bar);
            scaleTransition.setToY(1); // Hauteur finale
            scaleTransition.setInterpolator(Interpolator.EASE_BOTH); // Animation fluide
            scaleTransition.play();
        }
    }

    private String getStyleClassForCategory(String category) {
        switch (category) {
            case "Adhérents":
                return "default-bar";
            case "Coachs":
                return "coach-bar";
            case "Investisseurs":
                return "investor-bar";
            case "Créateurs":
                return "creator-bar";
            default:
                return "default-bar";
        }
    }

    private void afficherNombreAdherents() {
        int nombreAdherents = adherentService.getNombreAdherents();
        NbrAdherent.setText(String.valueOf(nombreAdherents));
    }
    private void afficherNombreCoachs() {
        int nombreCoachs = coachService.getNombreCoaches();
        nNbrCOACH.setText(String.valueOf(nombreCoachs));
    }
    private void afficherNombreINV() {
        int nombreINV = investisseurProduitService.getNombreINV();
        NBrInv.setText(String.valueOf(nombreINV));
    }
    private void afficherNombreEVENT() {
        int nombreEVENT = createurEvenementService.getNombreEVENT();
        NbREVENT.setText(String.valueOf(nombreEVENT));
    }



    @FXML

    void goToRECC(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Gestion_Rec.fxml"));
            Parent root = loader.load();
            ((Button) actionEvent.getSource()).getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML

    void GoToAdherent(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adherents.fxml"));
            Parent root = loader.load();
            ((Button) actionEvent.getSource()).getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void GoToCoach(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adherents.fxml"));
            Parent root = loader.load();
            ((Button) actionEvent.getSource()).getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void goToDach(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adherents.fxml"));
            Parent root = loader.load();
            ((Button) actionEvent.getSource()).getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void GoToInv(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adherents.fxml"));
            Parent root = loader.load();
            ((Button) actionEvent.getSource()).getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void GoToCrea(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adherents.fxml"));
            Parent root = loader.load();
            ((Button) actionEvent.getSource()).getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
