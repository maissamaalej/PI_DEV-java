package Controllers;

import Models.Coach;
import Models.SpecialiteC;
import Services.CoachService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class CoachController implements Initializable {

    private final CoachService coachService = new CoachService();

    public CoachController() throws SQLException {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Récupérer la liste des coachs
        List<Coach> coachs = coachService.getAll();

        // Afficher les coachs dans le VBox
        afficherCoachs(coachs);
        search.textProperty().addListener((observable, oldValue, newValue) -> rechercherCoach());
        sortComboBox.setOnAction(event -> trierCoachs());
        filterComboBox.getItems().setAll(SpecialiteC.values()); // Ajouter les spécialités disponibles
        filterComboBox.setOnAction(event -> filtrerCoachs());



    }
    public void afficherCoachs(List<Coach> coachs) {
        coachContainer.getChildren().clear(); // Effacer le contenu actuel

        GridPane gridPaneValides = new GridPane();
        GridPane gridPaneDemandes = new GridPane();

        configurerGridPane(gridPaneValides);
        configurerGridPane(gridPaneDemandes);

        ajouterEnTetes(gridPaneValides);
        ajouterEnTetes(gridPaneDemandes);

        int rowValides = 1, rowDemandes = 1;

        for (Coach coach : coachs) {
            if (coach.getCertificat_valide() == 1) {
                ajouterCoachALaGrille(gridPaneValides, coach, rowValides, coachs, true);
                rowValides++;
            } else {
                ajouterCoachALaGrille(gridPaneDemandes, coach, rowDemandes, coachs, false);
                rowDemandes++;
            }
        }

        ScrollPane scrollPaneValides = new ScrollPane(gridPaneValides);
        ScrollPane scrollPaneDemandes = new ScrollPane(gridPaneDemandes);

        Label labelCoachsValides = new Label("Coachs Validés");
        labelCoachsValides.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: green; -fx-padding: 10px 0;");

        Label labelDemandesEnAttente = new Label("Demandes en attente");
        labelDemandesEnAttente.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: red ; -fx-padding: 10px 0;");

        coachContainer.getChildren().addAll(labelCoachsValides, scrollPaneValides, labelDemandesEnAttente, scrollPaneDemandes);
    }
    private void configurerGridPane(GridPane gridPane) {
        gridPane.setHgap(60);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(15));
        gridPane.setAlignment(Pos.CENTER);
    }


    private void ajouterEnTetes(GridPane gridPane) {
        String[] headers = {"Nom", "Prénom", "Email", "Spécialité" , "Expérience", "Action"};

        for (int col = 0; col < headers.length; col++) {
            Label headerLabel = new Label(headers[col]);
            headerLabel.getStyleClass().add("header-label");
            headerLabel.setAlignment(Pos.CENTER);
            headerLabel.setMinWidth(120);
            GridPane.setHalignment(headerLabel, HPos.CENTER);
            gridPane.add(headerLabel, col, 0);
        }
    }

    private void ajouterCoachALaGrille(GridPane gridPane, Coach coach, int row, List<Coach> coachs, boolean estValide) {
        Label nomLabel = new Label(coach.getNom());
        Label prenomLabel = new Label(coach.getPrenom());
        Label emailLabel = new Label(coach.getEmail() != null ? coach.getEmail() : "Non renseigné");
        Label experienceLabel = new Label(String.valueOf(coach.getAnnee_experience()));
        Label specialiteLabel = new Label(coach.getSpecialite().name());

        Label[] labels = {nomLabel, prenomLabel, emailLabel, specialiteLabel, experienceLabel};
        for (Label label : labels) {
            label.setAlignment(Pos.CENTER);
            label.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
            GridPane.setHalignment(label, HPos.CENTER);
        }

        gridPane.add(nomLabel, 0, row);
        gridPane.add(prenomLabel, 1, row);
        gridPane.add(emailLabel, 2, row);
        gridPane.add(specialiteLabel, 3, row);
        gridPane.add(experienceLabel, 4, row);

        // Créer un HBox pour contenir les boutons
        HBox actionsBox = new HBox(10);
        actionsBox.setAlignment(Pos.CENTER);

        Button supprimerButton = new Button("Supprimer");
        supprimerButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-padding: 10px; -fx-font-size: 14px; -fx-cursor: hand;");

        supprimerButton.setOnAction(event -> {
            if (coachService.deleteCoach(coach.getId())) {
                coachs.remove(coach);
                afficherCoachs(coachs);
            }
        });

        if (estValide) {
            actionsBox.getChildren().add(supprimerButton);
        } else {
            Button validerButton = new Button("Valider");
            validerButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-padding: 10px; -fx-font-size: 14px; -fx-cursor: hand;");

            validerButton.setOnAction(event -> {
                coach.setCertificat_valide((byte) 1);
                if (coachService.updateCoach(coach)) {
                    afficherCoachs(coachs);
                }
            });

            actionsBox.getChildren().addAll(validerButton, supprimerButton);
        }

        // Ajouter le HBox contenant les boutons dans la colonne "Action"
        gridPane.add(actionsBox, 5, row);
    }
    @FXML
    private void rechercherCoach() {
        String searchText = search.getText().toLowerCase(); // Récupérer le texte et le mettre en minuscule

        List<Coach> filteredCoachs = coachService.getAll().stream()
                .filter(coach -> coach.getNom().toLowerCase().contains(searchText) ||
                        coach.getPrenom().toLowerCase().contains(searchText) ||
                        coach.getSpecialite().name().toLowerCase().contains(searchText))
                .toList();

        afficherCoachs(filteredCoachs); // Mettre à jour l'affichage avec les coachs filtrés
    }
    @FXML
    private ComboBox<String> sortComboBox;

    @FXML
    private void trierCoachs() {
        String criteria = sortComboBox.getValue();
        List<Coach> sortedCoachs = new ArrayList<>(coachService.getAll());

        switch (criteria) {
            case "Nom":
                sortedCoachs.sort(Comparator.comparing(Coach::getNom));
                break;
            case "Expérience":
                sortedCoachs.sort(Comparator.comparingInt(Coach::getAnnee_experience).reversed()); // Tri décroissant
                break;
            case "Note":
                sortedCoachs.sort(Comparator.comparingDouble(Coach::getNote).reversed()); // Tri décroissant
                break;
        }

        afficherCoachs(sortedCoachs);
    }
    @FXML
    private ComboBox<SpecialiteC> filterComboBox;

    @FXML
    private void filtrerCoachs() {
        SpecialiteC selectedSpecialite = filterComboBox.getValue();

        if (selectedSpecialite == null) {
            afficherCoachs(coachService.getAll()); // Afficher tous les coachs si aucun filtre n'est sélectionné
            return;
        }

        List<Coach> filteredCoachs = coachService.getAll().stream()
                .filter(coach -> coach.getSpecialite() == selectedSpecialite)
                .toList();

        afficherCoachs(filteredCoachs);
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
    private VBox coachContainer;

    @FXML
    private AnchorPane coachs;

    @FXML
    private AnchorPane demandecoachContainer;

    @FXML
    private Button event;

    @FXML
    private Button home;

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

}

