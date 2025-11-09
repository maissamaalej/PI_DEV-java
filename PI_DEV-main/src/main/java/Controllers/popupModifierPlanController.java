package Controllers;

import Models.Planning;
import Services.PlanningService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class popupModifierPlanController {

    @FXML
    private TextField titreModif, tarifModif;

    private Planning planning;
    private ajoutplanningController planningController;

    // Méthode appelée lors du clic sur le bouton "Enregistrer" du pop-up
    public void ModifierPlanning(ActionEvent actionEvent) throws Exception {
        if (planning == null) {
            afficherAlerte("Erreur", "Aucun planning à modifier !", Alert.AlertType.ERROR);
            return;
        }

        if (!validerChamps()) {
            return;
        }

        // Met à jour les valeurs de l'objet Planning existant
        planning.setTitre(titreModif.getText());
        planning.setTarif(Double.parseDouble(tarifModif.getText()));

        // Mise à jour dans la base de données
        PlanningService ps = new PlanningService();
        ps.update(planning);

        // Ferme la fenêtre après modification
        Stage stage = (Stage) titreModif.getScene().getWindow();
        stage.close();

        // Met à jour l'affichage dans la liste
        if (planningController != null) {
            planningController.mettreAJourAffichage(planning);
        }
    }

    @FXML
    public void annulermodification(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    // Méthode d'initialisation pour remplir les champs du pop-up avec les données existantes
    public void initData(Planning planning, ajoutplanningController planningController) {
        this.planning = planning;
        this.planningController = planningController;  // Stocke la référence du contrôleur principal

        titreModif.setText(planning.getTitre());
        tarifModif.setText(String.valueOf(planning.getTarif()));
    }

    // 🔍 Validation des champs
    private boolean validerChamps() {
        if (titreModif.getText().isEmpty()) {
            afficherAlerte("Champs vide", "Le titre ne peut pas être vide.", Alert.AlertType.WARNING);
            return false;
        }

        if (!estDoublePositif(tarifModif.getText())) {
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
}
