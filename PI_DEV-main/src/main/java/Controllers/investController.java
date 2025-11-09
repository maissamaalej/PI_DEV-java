package Controllers;

import Models.InvestisseurProduit;
import Services.InvestisseurProduitService;
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

public class investController implements Initializable {

    private final InvestisseurProduitService investisseurProduitService = new InvestisseurProduitService();

    public investController() throws SQLException {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Récupérer la liste des investisseurs de produit
        List<InvestisseurProduit> investisseurProduits = investisseurProduitService.getAll();
        afficherInvestisseurs(investisseurProduits);
        sortComboBox.setOnAction(event -> trierInvestisseurs());
        search.textProperty().addListener((observable, oldValue, newValue) -> rechercherInvestisseur());
    }

    public void afficherInvestisseurs(List<InvestisseurProduit> investisseurProduits) {
        InvestorContainer.getChildren().clear(); // Effacer le contenu actuel

        GridPane gridPaneValides = new GridPane();
        GridPane gridPaneDemandes = new GridPane();

        configurerGridPane(gridPaneValides);
        configurerGridPane(gridPaneDemandes);

        ajouterEnTetes(gridPaneValides);
        ajouterEnTetes(gridPaneDemandes);

        int rowValides = 1, rowDemandes = 1;

        for (InvestisseurProduit investisseur : investisseurProduits) {
            if (investisseur.getCertificat_valide() == 1) {
                ajouterInvestisseurALaGrille(gridPaneValides, investisseur, rowValides, investisseurProduits, true);
                rowValides++;
            } else {
                ajouterInvestisseurALaGrille(gridPaneDemandes, investisseur, rowDemandes, investisseurProduits, false);
                rowDemandes++;
            }
        }

        ScrollPane scrollPaneValides = new ScrollPane(gridPaneValides);
        ScrollPane scrollPaneDemandes = new ScrollPane(gridPaneDemandes);

        Label labelCoachsValides = new Label("Investisseurs de produits Validés");
        labelCoachsValides.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: green; -fx-padding: 10px 0;");

        Label labelDemandesEnAttente = new Label("Demandes en attente");
        labelDemandesEnAttente.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: red ; -fx-padding: 10px 0;");

        InvestorContainer.getChildren().addAll(labelCoachsValides, scrollPaneValides, labelDemandesEnAttente, scrollPaneDemandes);

    }

    private void configurerGridPane(GridPane gridPane) {
        gridPane.setHgap(60);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(15));
        gridPane.setAlignment(Pos.CENTER);
    }

    private void ajouterEnTetes(GridPane gridPane) {
        String[] headers = {"Nom", "Prénom", "Email", "Nom Entreprise", "Description", "Adresse", "Téléphone", "Action"};

        for (int col = 0; col < headers.length; col++) {
            Label headerLabel = new Label(headers[col]);
            headerLabel.getStyleClass().add("header-label");
            headerLabel.setAlignment(Pos.CENTER);
            headerLabel.setMinWidth(120);
            GridPane.setHalignment(headerLabel, HPos.CENTER);
            gridPane.add(headerLabel, col, 0);
        }
    }

    private void ajouterInvestisseurALaGrille(GridPane gridPane, InvestisseurProduit investisseur, int row, List<InvestisseurProduit> investisseurProduits, boolean estValide) {
        Label nomLabel = new Label(investisseur.getNom());
        Label prenomLabel = new Label(investisseur.getPrenom());
        Label emailLabel = new Label(investisseur.getEmail() != null ? investisseur.getEmail() : "Non renseigné");
        Label entrepriseLabel = new Label(investisseur.getNom_entreprise());
        Label descriptionLabel = new Label(investisseur.getDescription());
        Label adresseLabel = new Label(investisseur.getAdresse());
        Label telephoneLabel = new Label(investisseur.getTelephone());

        Label[] labels = {nomLabel, prenomLabel, emailLabel, entrepriseLabel, descriptionLabel, adresseLabel, telephoneLabel};
        for (Label label : labels) {
            label.setAlignment(Pos.CENTER);
            label.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
            GridPane.setHalignment(label, HPos.CENTER);
        }

        gridPane.add(nomLabel, 0, row);
        gridPane.add(prenomLabel, 1, row);
        gridPane.add(emailLabel, 2, row);
        gridPane.add(entrepriseLabel, 3, row);
        gridPane.add(descriptionLabel, 4, row);
        gridPane.add(adresseLabel, 5, row);
        gridPane.add(telephoneLabel, 6, row);

        Button supprimerButton = new Button("Supprimer");
        supprimerButton.getStyleClass().add("supprimer-button");
        supprimerButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-padding: 10px; -fx-font-size: 14px; -fx-cursor: hand;");

        GridPane.setHalignment(supprimerButton, HPos.CENTER);

        supprimerButton.setOnAction(event -> {
            if (investisseurProduitService.deleteInvestisseurProduit(investisseur.getId())) {
                investisseurProduits.remove(investisseur);
                afficherInvestisseurs(investisseurProduits);  // Rafraîchir l'affichage
            }
        });

        if (estValide) {
            gridPane.add(supprimerButton, 7, row);
        } else {
            Button validerButton = new Button("Valider");
            validerButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-padding: 10px; -fx-font-size: 14px; -fx-cursor: hand;");
            validerButton.getStyleClass().add("valider-button");
            GridPane.setHalignment(validerButton, HPos.CENTER);

            validerButton.setOnAction(event -> {
                investisseur.setCertificat_valide((byte) 1);
                if (investisseurProduitService.updateInvestisseurProduit(investisseur)) {
                    afficherInvestisseurs(investisseurProduits);
                    String telephone = investisseur.getTelephone();
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
    private void rechercherInvestisseur() {
        String searchText = search.getText().toLowerCase(); // Récupérer le texte et le mettre en minuscule

        List<InvestisseurProduit> filteredInvestisseurs = investisseurProduitService.getAll().stream()
                .filter(investisseur -> investisseur.getNom().toLowerCase().contains(searchText) ||
                        investisseur.getPrenom().toLowerCase().contains(searchText) ||
                        investisseur.getAdresse().toLowerCase().contains(searchText)) // Exemple de filtrage par secteur
                .toList();

        afficherInvestisseurs(filteredInvestisseurs); // Mettre à jour l'affichage avec les investisseurs filtrés
    }
    @FXML
    private ComboBox<String> sortComboBox;

    @FXML
    private void trierInvestisseurs() {
        String criteria = sortComboBox.getValue();
        List<InvestisseurProduit> sortedInvestisseurs = new ArrayList<>(investisseurProduitService.getAll());

        switch (criteria) {
            case "Nom":
                sortedInvestisseurs.sort(Comparator.comparing(InvestisseurProduit::getNom));
                break;
            case "Adresse":
                sortedInvestisseurs.sort(Comparator.comparing(InvestisseurProduit::getAdresse)); // Tri par secteur
                break;

        }

        afficherInvestisseurs(sortedInvestisseurs);
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
    private VBox InvestorContainer;

    @FXML
    private AnchorPane chart;

    @FXML
    private AnchorPane chart1;

    @FXML
    private AnchorPane demandecoachContainer;

    @FXML
    private Button event;

    @FXML
    private Button home;

    @FXML
    private AnchorPane investors;

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
    private AnchorPane upchart;}