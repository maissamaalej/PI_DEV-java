package Controllers;

import Models.Seance;
import Models.Type;
import Services.SeanceService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Date;
import java.sql.Time;
import java.time.format.DateTimeFormatter;

public class popupModifierSeanceController {

    public Button btnChoisirVideo;
    @FXML
    private TextArea ModifyDescription;

    @FXML
    private TextField ModifyLien;

    @FXML
    private ChoiceBox<Type> ModifyType;

    @FXML
    private DatePicker Modify_Date;

    @FXML
    private TextField Modify_HeureDebut;

    @FXML
    private TextField Modify_Heurefin;

    @FXML
    private TextField Modify_adherent_Id;

    @FXML
    private TextField Modifytitre;

    private int idCoach;
    private int idPlanning;
    private int idSeance;
    private planningController calendarController;

    public void setPlanningController(planningController calendarController) {
        this.calendarController = calendarController;
    }
    public void annulerSeance(ActionEvent event) {
        Stage stage = (Stage) Modifytitre.getScene().getWindow();
        stage.close();
    }
    @FXML
    public void initialize() {

        ModifyType.getItems().setAll(Type.values());
        ModifyType.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == Type.ENREGISTRE) {
                btnChoisirVideo.setDisable(false);
            } else {
                btnChoisirVideo.setDisable(true);
            }
        });


    }
    @FXML
    public void ModifierSeance() throws Exception {

            if (!validerChamps()) {
                return;
            }

            try {

                Date currentDate = new Date(System.currentTimeMillis()); // Date actuelle du système
                Date selectedDate = Date.valueOf(Modify_Date.getValue()); // Date de la séance sélectionnée

                if (selectedDate.before(currentDate)) {
                    afficherAlerte("Erreur de date", "La date de la séance ne peut pas être dans le passé.", Alert.AlertType.ERROR);
                    return;
                }

                // Vérifier que l'heure de début est avant l'heure de fin
                Time heureDebut = Time.valueOf(Modify_HeureDebut.getText()+ ":00");
                Time heureFin = Time.valueOf(Modify_Heurefin.getText()+ ":00");

                if (heureDebut.after(heureFin) || heureDebut.equals(heureFin)) {
                    afficherAlerte("Erreur de saisie", "L'heure de début doit être inférieure à l'heure de fin.", Alert.AlertType.ERROR);
                    return;
                }

                SeanceService sc = new SeanceService();
                Seance s1 = new Seance(
                        idSeance,
                        Modifytitre.getText(),
                        ModifyDescription.getText(),
                        selectedDate,
                        idCoach,
                        Integer.parseInt(Modify_adherent_Id.getText()),
                        ModifyType.getValue(),
                        ModifyLien.getText(),
                        idPlanning,
                        heureDebut,
                        heureFin
                );

                sc.update(s1);
                if (calendarController != null) {
                    calendarController.refreshCalendar();
                }

                fermerFenetre();
            } catch (IllegalArgumentException e) {

                afficherAlerte("Erreur de format", "Veuillez vérifier le format des heures (HH:mm:ss).", Alert.AlertType.ERROR);
            } catch (Exception e) {

                afficherAlerte("Erreur", "Une erreur est survenue lors de la modification de la séance.", Alert.AlertType.ERROR);
            }
        }

    private boolean validerChamps() {

        if (Modifytitre.getText().isEmpty() || ModifyDescription.getText().isEmpty() || ModifyLien.getText().isEmpty()
                || Modify_HeureDebut.getText().isEmpty()
                || Modify_Heurefin.getText().isEmpty() || Modify_adherent_Id.getText().isEmpty()
                || Modify_Date.getValue() == null || ModifyType.getValue() == null) {
            afficherAlerte("Champs vides", "Tous les champs doivent être remplis.", Alert.AlertType.WARNING);
            return false;
        }

        if (!estEntier(Modify_adherent_Id.getText())) {
            afficherAlerte("Format invalide", "L'ID de l'adhérent doit être un nombre entier.", Alert.AlertType.ERROR);
            return false;
        }

        if (!estHeureValide(Modify_HeureDebut.getText())) {
            afficherAlerte("Format heure invalide", "L'heure de début doit être au format HH:mm.", Alert.AlertType.ERROR);
            return false;
        }

        if (!estHeureValide(Modify_Heurefin.getText())) {
            afficherAlerte("Format heure invalide", "L'heure de fin doit être au format HH:mm.", Alert.AlertType.ERROR);
            return false;
        }
        if (!ModifyLien.getText().toLowerCase().endsWith(".mp4")) {
            afficherAlerte("Format vidéo invalide", "La vidéo doit être au format MP4.", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }


    private boolean estEntier(String valeur) {
        try {
            Integer.parseInt(valeur);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    // si une chaîne de caractères représente une heure au format HH:mm,
    // où l'heure est comprise entre 00 et 23 et les minutes entre 00 et 59.
    private boolean estHeureValide(String heure) {
        String regex = "^([01][0-9]|2[0-3]):([0-5][0-9])$";
        return heure.matches(regex);
    }

    private void afficherAlerte(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void initData(Seance seance) {
        this.idCoach = seance.getIdCoach();
        this.idPlanning = seance.getPlanningId();
        this.idSeance = seance.getId();

        Modifytitre.setText(seance.getTitre());
        ModifyDescription.setText(seance.getDescription());
        ModifyLien.setText(seance.getLienVideo());

        Modify_Date.setValue(seance.getDate().toLocalDate());

        Modify_adherent_Id.setText(String.valueOf(seance.getIdAdherent()));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        Modify_HeureDebut.setText(seance.getHeureDebut().toLocalTime().format(formatter));
        Modify_Heurefin.setText(seance.getHeureFin().toLocalTime().format(formatter));

        System.out.println("Séance chargée pour modification : " + seance.getTitre() + " ID: " + seance.getId());
    }
    private void fermerFenetre() {
        Stage stage = (Stage) Modifytitre.getScene().getWindow();
        stage.close();
    }

    public void ChoisirVideo(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une vidéo");

        // Filtre pour ne permettre que la sélection de fichiers vidéo
        FileChooser.ExtensionFilter videoFilter = new FileChooser.ExtensionFilter("Fichiers vidéo", "*.mp4", "*.avi", "*.mov", "*.mkv");
        fileChooser.getExtensionFilters().add(videoFilter);

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            // Stocker le chemin du fichier dans le champ Lien vidéo
            ModifyLien.setText(selectedFile.getAbsolutePath());
        }
    }
}
