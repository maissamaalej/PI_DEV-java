package Controllers;

import Models.Adherent;
import Models.ObjP;
import Services.AdherentService;
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
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class AdherentsController  implements Initializable {


    private final AdherentService adherentService = new AdherentService();

    @FXML
    private VBox adherentContainer;
    @FXML
    private ComboBox<String> sortComboBox;
    @FXML
    private ComboBox<String> filterComboBox;

    public AdherentsController() throws SQLException {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Récupérer la liste des adhérents
        List<Adherent> adherents = adherentService.getAll();

        // Afficher les adhérents dans le VBox
        afficherAdherents(adherents);
        sortComboBox.setOnAction(event -> trierAdherents());
        search.textProperty().addListener((observable, oldValue, newValue) -> rechercherAdherent());
        //filterComboBox.getItems().setAll(Arrays.toString(ObjP.values())); // Ajouter les spécialités disponibles
        //filterComboBox.setOnAction(event -> filtrerAdherents());

    }


    @FXML
    private void rechercherAdherent() {
        String searchText = search.getText().toLowerCase(); // Récupérer le texte et le mettre en minuscule

        List<Adherent> filteredAdherents = adherentService.getAll().stream()
                .filter(adherent -> adherent.getNom().toLowerCase().contains(searchText) ||
                        adherent.getPrenom().toLowerCase().contains(searchText) ||
                        adherent.getEmail().toLowerCase().contains(searchText))
                .toList();

        afficherAdherents(filteredAdherents); // Mettre à jour l'affichage avec les adhérents filtrés
    }


    @FXML
    private void trierAdherents() {
        String criteria = sortComboBox.getValue();
        List<Adherent> sortedAdherents = new ArrayList<>(adherentService.getAll());

        switch (criteria) {
            case "Nom":
                sortedAdherents.sort(Comparator.comparing(Adherent::getNom));
                break;
            case "Âge":
                sortedAdherents.sort(Comparator.comparingInt(Adherent::getAge));
                break;
            case "Poids":
                sortedAdherents.sort(Comparator.comparingDouble(Adherent::getPoids));
                break;
            case "Taille":
                sortedAdherents.sort(Comparator.comparingDouble(Adherent::getTaille));
                break;
        }

        afficherAdherents(sortedAdherents);
    }

//    @FXML
//    private void filtrerAdherents() {
//        ObjP selectedObjectif = ObjP.valueOf(filterComboBox.getValue());
//
//        if (selectedObjectif == null) {
//            afficherAdherents(adherentService.getAll()); // Afficher tous les adhérents si aucun filtre n'est sélectionné
//            return;
//        }
//
//        List<Adherent> filteredAdherents = adherentService.getAll().stream()
//                .filter(adherent -> adherent.getObjectif_personnelle() == selectedObjectif)
//                .toList();
//
//        afficherAdherents(filteredAdherents);
//    }



    public void afficherAdherents(List<Adherent> adherents) {
        AdherentContainer.getChildren().clear(); // Effacer le contenu actuel

        // Création d'une grille pour afficher les adhérents
        GridPane gridPane = new GridPane();
        configurerGridPane(gridPane);
        ajouterEnTetesAdherents(gridPane);

        int row = 1;
        for (Adherent adherent : adherents) {
            ajouterAdherentALaGrille(gridPane, adherent, row, adherents);
            row++;
        }

        // Ajout du ScrollPane contenant la grille dans le VBox
        ScrollPane scrollPane = new ScrollPane(gridPane);
        Label titleLabel = new Label("Liste des Adhérents");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: green; -fx-padding: 10px 0;");
        AdherentContainer.getChildren().addAll(titleLabel, scrollPane);


    }

    private void configurerGridPane(GridPane gridPane) {
        gridPane.setHgap(60);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(15));
        gridPane.setAlignment(Pos.CENTER);
    }

    private void ajouterEnTetesAdherents(GridPane gridPane) {
        String[] headers = {"Nom", "Prénom", "Email", "Âge", "Taille", "Poids", "Genre", "Objectif", "Niveau", "Action"};

        for (int col = 0; col < headers.length; col++) {
            Label headerLabel = new Label(headers[col]);
            headerLabel.getStyleClass().add("header-label");
            headerLabel.setAlignment(Pos.CENTER);
            headerLabel.setMinWidth(120);
            GridPane.setHalignment(headerLabel, HPos.CENTER);
            gridPane.add(headerLabel, col, 0);
        }
    }

    private void ajouterAdherentALaGrille(GridPane gridPane, Adherent adherent, int row, List<Adherent> adherents) {
        Label nomLabel = new Label(adherent.getNom());
        Label prenomLabel = new Label(adherent.getPrenom());
        Label emailLabel = new Label(adherent.getEmail() != null ? adherent.getEmail() : "Non renseigné");
        Label ageLabel = new Label(String.valueOf(adherent.getAge()));
        Label tailleLabel = new Label(String.valueOf(adherent.getTaille()));
        Label poidsLabel = new Label(String.valueOf(adherent.getPoids()));
        Label genreLabel = new Label(adherent.getGenre().name());
        Label objectifLabel = new Label(adherent.getObjectif_personnelle().name());
        Label niveauLabel = new Label(adherent.getNiveau_activites().name());

        Label[] labels = {nomLabel, prenomLabel, emailLabel, ageLabel, tailleLabel, poidsLabel, genreLabel, objectifLabel, niveauLabel};
        for (Label label : labels) {
            label.setAlignment(Pos.CENTER);
            label.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
            GridPane.setHalignment(label, HPos.CENTER);
        }

        gridPane.add(nomLabel, 0, row);
        gridPane.add(prenomLabel, 1, row);
        gridPane.add(emailLabel, 2, row);
        gridPane.add(ageLabel, 3, row);
        gridPane.add(tailleLabel, 4, row);
        gridPane.add(poidsLabel, 5, row);
        gridPane.add(genreLabel, 6, row);
        gridPane.add(objectifLabel, 7, row);
        gridPane.add(niveauLabel, 8, row);

        // Bouton Supprimer
        Button supprimerButton = new Button("Supprimer");
        supprimerButton.getStyleClass().add("supprimer-button");
        supprimerButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-padding: 10px; -fx-font-size: 14px; -fx-cursor: hand;");
        GridPane.setHalignment(supprimerButton, HPos.CENTER);

        supprimerButton.setOnAction(event -> {
            // Supprimer l'adhérent de la base de données
            if (adherentService.deleteAdherent(adherent.getId())) {
                // Retirer l'adhérent de la liste affichée
                adherents.remove(adherent);
                afficherAdherents(adherents); // Rafraîchir l'affichage
            }
        });

        gridPane.add(supprimerButton, 9, row);
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
    private VBox AdherentContainer;

    @FXML
    private AnchorPane adherents;

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

    @FXML
    private AnchorPane upchart;




}