package Controllers;

import Models.Planning;
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
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class ajoutplanningController {

    @FXML
    private TextField titreplan, tarifplan;
    @FXML
    private VBox planningVBox;

    private HBox headerRow;

    public ajoutplanningController() throws SQLException {
    }

    @FXML
    public void initialize() {

        setupHeader();
    }


    @FXML
    void ajouterPlanning() throws SQLException {
        if (!validerChamps()) {
            return;
        }

        PlanningService ps = new PlanningService();
        int idCoach =Session.getInstance().getCurrentUser().getId();

        // Vérifier si le coach a déjà un planning
        if (ps.getPlanningByCoachId(idCoach) != null) {
            afficherAlerte("Erreur", "Vous avez déjà un planning et ne pouvez pas en ajouter un autre.", Alert.AlertType.ERROR);
            return;
        }

        try {
            Planning p1 = new Planning(idCoach, titreplan.getText(), Double.parseDouble(tarifplan.getText()));
            ps.create(p1);
            AffichagePlanning(p1);
        } catch (Exception e) {
            afficherAlerte("Erreur", "Une erreur est survenue lors de l'ajout du planning.", Alert.AlertType.ERROR);
        }
    }

    private boolean validerChamps() {
        if ( titreplan.getText().isEmpty() || tarifplan.getText().isEmpty()) {
            afficherAlerte("Champs vides", "Tous les champs doivent être remplis.", Alert.AlertType.WARNING);
            return false;
        }

        if (!estDoublePositif(tarifplan.getText())) {
            afficherAlerte("Format invalide", "Le tarif doit être un nombre positif valide.", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private boolean estDoublePositif(String valeur) {
        try {
            double d = Double.parseDouble(valeur);
            return d > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void afficherAlerte(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void AffichagePlanning(Planning p) {
        HBox planningRow = new HBox(20);
        planningRow.setAlignment(Pos.CENTER);
        planningRow.setStyle("-fx-border-color: black; -fx-border-width: 1px; -fx-padding: 15px;");

        Label titreLabel = new Label(p.getTitre());
        Label tarifLabel = new Label(p.getTarif() + " Dt");
        titreLabel.setStyle("-fx-font-size: 14px;");
        tarifLabel.setStyle("-fx-font-size: 14px;");

        Button modifyButton = new Button("Modifier");
        modifyButton.setOnAction(e -> {
            try {
                openModifierPopUp(p);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        planningRow.getChildren().addAll(titreLabel, new Label("|"), tarifLabel, new Label("|"), modifyButton);
        planningVBox.getChildren().add(planningRow);
    }

    public void mettreAJourAffichage(Planning planning) {
        planningVBox.getChildren().clear();
        setupHeader();
        AffichagePlanning(planning);
    }

    private void openModifierPopUp(Planning planning) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/popUpModifierPlanning.fxml"));
        DialogPane dialogPane = loader.load();

        popupModifierPlanController popUpController = loader.getController();
        popUpController.initData(planning, this);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane(dialogPane);
        dialog.setTitle("Modifier Planning");
        dialog.showAndWait();
    }

    public void setupHeader() {
        headerRow = new HBox(20);
        headerRow.setAlignment(Pos.CENTER);
        headerRow.setStyle("-fx-background-color:  #f58400; -fx-padding:10px; -fx-border-color: black; -fx-border-width: 1px;");

        Label titreHeader = new Label("Titre");
        Label tarifHeader = new Label("Tarif");
        Label actionsHeader = new Label("Actions");

        titreHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        tarifHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        actionsHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        headerRow.getChildren().addAll(titreHeader, new Label("|"), tarifHeader, new Label("|"), actionsHeader);
        planningVBox.getChildren().add(headerRow);
    }

    @FXML
    void consulterplan(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/planning.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle("Consulter Planning");
        stage.setScene(new Scene(root));
        stage.show();
    }
    //ROOT
    private CreateurEvenementService createurEvenementService = new CreateurEvenementService();

    private CoachService coachService = new CoachService();
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
}
