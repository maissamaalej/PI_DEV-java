package Controllers;

import Models.CreateurEvenement;
import Services.CreateurEvenementService;
import Services.SmsSender;
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

public class creatersController implements Initializable {

    private final CreateurEvenementService createurEvenementService = new CreateurEvenementService();

    public creatersController() throws SQLException {
    }

    public void afficherCreateursEvenements(List<CreateurEvenement> createurEvenements) {
        EventersContainer.getChildren().clear(); // Effacer le contenu actuel

        GridPane gridPaneValides = new GridPane();
        GridPane gridPaneDemandes = new GridPane();

        configurerGridPane(gridPaneValides);
        configurerGridPane(gridPaneDemandes);

        ajouterEnTetes(gridPaneValides);
        ajouterEnTetes(gridPaneDemandes);

        int rowValides = 1, rowDemandes = 1;

        for (CreateurEvenement createur : createurEvenements) {
            if (createur.getCertificat_valide() == 1) {
                ajouterCreateurALaGrille(gridPaneValides, createur, rowValides, createurEvenements, true);
                rowValides++;
            } else {
                ajouterCreateurALaGrille(gridPaneDemandes, createur, rowDemandes, createurEvenements, false);
                rowDemandes++;
            }
        }

        ScrollPane scrollPaneValides = new ScrollPane(gridPaneValides);
        ScrollPane scrollPaneDemandes = new ScrollPane(gridPaneDemandes);

        // Titre "Createurs Validés" avec style
        Label labelCreateursValides = new Label("Createurs Validés");
        labelCreateursValides.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: green; -fx-padding: 10px 0;");

        // Titre "Demandes en attente" avec style
        Label labelDemandesEnAttente = new Label("Demandes en attente");
        labelDemandesEnAttente.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: red ; -fx-padding: 10px 0;");

        // Ajout des titres stylisés et des ScrollPane avec contenu
        EventersContainer.getChildren().addAll(labelCreateursValides, scrollPaneValides,
                labelDemandesEnAttente, scrollPaneDemandes);
    }

    private void configurerGridPane(GridPane gridPane) {
        gridPane.setHgap(60);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(15));
        gridPane.setAlignment(Pos.CENTER);
    }

    private void ajouterEnTetes(GridPane gridPane) {
        String[] headers = {"Nom", "Prénom", "Email", "Organisation", "Description", "Adresse", "Téléphone", "Action"};

        for (int col = 0; col < headers.length; col++) {
            Label headerLabel = new Label(headers[col]);
            headerLabel.getStyleClass().add("header-label");
            headerLabel.setAlignment(Pos.CENTER);
            headerLabel.setMinWidth(120);
            headerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #F58400; -fx-text-fill: white;");
            GridPane.setHalignment(headerLabel, HPos.CENTER);
            gridPane.add(headerLabel, col, 0);
        }
    }

    private void ajouterCreateurALaGrille(GridPane gridPane, CreateurEvenement createur, int row, List<CreateurEvenement> createurEvenements, boolean estValide) {
        Label nomLabel = new Label(createur.getNom());
        Label prenomLabel = new Label(createur.getPrenom());
        Label emailLabel = new Label(createur.getEmail() != null ? createur.getEmail() : "Non renseigné");
        Label organisationLabel = new Label(createur.getNom_organisation());
        Label descriptionLabel = new Label(createur.getDescription());
        Label adresseLabel = new Label(createur.getAdresse());
        Label telephoneLabel = new Label(createur.getTelephone());

        Label[] labels = {nomLabel, prenomLabel, emailLabel, organisationLabel, descriptionLabel, adresseLabel, telephoneLabel};
        for (Label label : labels) {
            label.setAlignment(Pos.CENTER);
            label.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
            GridPane.setHalignment(label, HPos.CENTER);
        }

        gridPane.add(nomLabel, 0, row);
        gridPane.add(prenomLabel, 1, row);
        gridPane.add(emailLabel, 2, row);
        gridPane.add(organisationLabel, 3, row);
        gridPane.add(descriptionLabel, 4, row);
        gridPane.add(adresseLabel, 5, row);
        gridPane.add(telephoneLabel, 6, row);

        Button supprimerButton = new Button("Supprimer");
        supprimerButton.getStyleClass().add("supprimer-button");
        supprimerButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-padding: 10px; -fx-font-size: 14px; -fx-cursor: hand;");
        GridPane.setHalignment(supprimerButton, HPos.CENTER);

        supprimerButton.setOnAction(event -> {
            if (createurEvenementService.deleteCreateurEvenement(createur.getId())) {
                createurEvenements.remove(createur);
                afficherCreateursEvenements(createurEvenements);  // Rafraîchir l'affichage
            }
        });

        if (estValide) {
            gridPane.add(supprimerButton, 7, row);
        } else {
            Button validerButton = new Button("Valider");
            validerButton.getStyleClass().add("valider-button");
            validerButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-padding: 10px; -fx-font-size: 14px; -fx-cursor: hand;");
            GridPane.setHalignment(validerButton, HPos.CENTER);

            validerButton.setOnAction(event -> {
                createur.setCertificat_valide((byte) 1); // On marque comme validé

                if (createurEvenementService.updateCreateurEvenement(createur)) {
                    afficherCreateursEvenements(createurEvenements);

                    String telephone = createur.getTelephone();
                    if (telephone != null) {
                        if (!telephone.startsWith("+216")) {
                            telephone = "+216" + telephone;  // Ajout du préfixe +216 si nécessaire
                        }

                        String message = "Félicitations ! Vous êtes désormais un membre officiel de Coachini.";
                        SmsSender.envoyerSms(telephone, message);  // Envoi du SMS
                        System.out.println("SMS envoyé à " + telephone);
                    }
                }
            });

            HBox actionsBox = new HBox(10, validerButton, supprimerButton);
            actionsBox.setAlignment(Pos.CENTER);
            gridPane.add(actionsBox, 7, row);
        }
    }

    @FXML
    private TextField search;

    @FXML
    private void rechercherCreateur() {
        String searchText = search.getText().toLowerCase(); // Récupérer le texte et le mettre en minuscule

        List<CreateurEvenement> filteredCreateurs = createurEvenementService.getAll().stream()
                .filter(createur -> createur.getNom().toLowerCase().contains(searchText) ||
                        createur.getPrenom().toLowerCase().contains(searchText) ||
                        createur.getAdresse().toLowerCase().contains(searchText)) // Exemple de filtrage par secteur
                .toList();

        afficherCreateursEvenements(filteredCreateurs); // Mettre à jour l'affichage avec les créateurs filtrés
    }

    @FXML
    private ComboBox<String> sortComboBox;

    @FXML
    private void trierCreateurs() {
        String criteria = sortComboBox.getValue();
        List<CreateurEvenement> sortedCreateurs = new ArrayList<>(createurEvenementService.getAll());

        switch (criteria) {
            case "Nom":
                sortedCreateurs.sort(Comparator.comparing(CreateurEvenement::getNom));
                break;
            case "Adresse":
                sortedCreateurs.sort(Comparator.comparing(CreateurEvenement::getAdresse)); // Tri par secteur
                break;

        }

        afficherCreateursEvenements(sortedCreateurs);
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
    private VBox EventersContainer;

    @FXML
    private AnchorPane chart;

    @FXML
    private AnchorPane chart1;

    @FXML
    private AnchorPane demandecoachContainer;

    @FXML
    private Button event;

    @FXML
    private AnchorPane eventers;

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
    private AnchorPane upchart;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Récupérer la liste des investisseurs de produit
        List<CreateurEvenement> createurEvenements = createurEvenementService.getAll();
        afficherCreateursEvenements(createurEvenements);
        sortComboBox.setOnAction(event -> trierCreateurs());
        search.textProperty().addListener((observable, oldValue, newValue) -> rechercherCreateur());
    }

}