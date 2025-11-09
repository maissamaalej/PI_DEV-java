package Controllers;

import Models.Seance;
import Models.Type;
import Services.PaiementPlanningService;
import Services.PlanningService;
import Services.SeanceService;
import Utils.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class popupSeanceController {
    @FXML
    public Button btnChoisirVideo;
    @FXML
    private TextArea fieldDescription;

    @FXML
    private TextField fieldLien;

    @FXML
    private ChoiceBox<Type> fieldType;

    @FXML
    private TextField field_HeureDebut;

    @FXML
    private TextField field_Heurefin;

    @FXML
    private ChoiceBox<Integer> field_adherent_Id;

    @FXML
    private TextField fieldtitre;

    @FXML
    private DatePicker field_Date;

    private int idPlanning;

    private planningController calendarController;
    @FXML
    public void initialize() {
        fieldType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Type.ENREGISTRE) {
                btnChoisirVideo.setDisable(false);
            } else {
                btnChoisirVideo.setDisable(true);
            }
        });
        fieldType.getItems().setAll(Type.values());
    }
    public void loadAdherents() throws SQLException {
        PaiementPlanningService paiementService = new PaiementPlanningService();
        List<Integer> adherentsPayeurs = paiementService.getAdherentsPayeByPlanning(idPlanning);
        field_adherent_Id.getItems().setAll(adherentsPayeurs);
    }

    public void setCalendarController(planningController calendarController) {
        this.calendarController = calendarController;
    }

    public void setIdPlanning(int idPlanning) {
        this.idPlanning = idPlanning;
    }
    @FXML
    void ajouterSeance() {
        if (!validerChamps()) {
            return;
        }
        try {
            // Récupérer la date sélectionnée et la date actuelle
            LocalDate today = LocalDate.now();
            LocalDate selected = field_Date.getValue();
            LocalTime currentTime = LocalTime.now();

            // Vérifier la date
            if (selected.isBefore(today)) {
                afficherAlerte("Erreur de date", "La date de la séance ne peut pas être dans le passé.", Alert.AlertType.ERROR);
                return;
            } else if (selected.equals(today)) {
                // Si la séance est prévue pour aujourd'hui, vérifier les heures
                LocalTime startTime = LocalTime.parse(field_HeureDebut.getText());
                LocalTime endTime = LocalTime.parse(field_Heurefin.getText());
                if (startTime.isBefore(currentTime)) {
                    afficherAlerte("Erreur de saisie", "Pour une séance aujourd'hui, l'heure de début doit être dans le futur.", Alert.AlertType.ERROR);
                    return;
                }
                if (endTime.isBefore(currentTime)) {
                    afficherAlerte("Erreur de saisie", "Pour une séance aujourd'hui, l'heure de fin doit être dans le futur.", Alert.AlertType.ERROR);
                    return;
                }
            }

            // Conversion de la date sélectionnée en java.sql.Date
            Date sqlDate = Date.valueOf(selected);

            // Vérifier que l'heure de début est avant l'heure de fin
            Time heureDebut = Time.valueOf(field_HeureDebut.getText() + ":00");
            Time heureFin = Time.valueOf(field_Heurefin.getText() + ":00");
            if (heureDebut.after(heureFin) || heureDebut.equals(heureFin)) {
                afficherAlerte("Erreur de saisie", "L'heure de début doit être inférieure à l'heure de fin.", Alert.AlertType.ERROR);
                return;
            }

            // Vérifier la sélection d'un adhérent
            Integer idAdherent = field_adherent_Id.getValue();
            if (idAdherent == null) {
                afficherAlerte("Erreur de saisie", "Veuillez sélectionner un adhérent.", Alert.AlertType.ERROR);
                return;
            }
            int idCoach = Session.getInstance().getCurrentUser().getId();
            PlanningService ps = new PlanningService();
            int idPlanning = ps.getIdPlanningByCoachId(idCoach);

            SeanceService sc = new SeanceService();
            Seance s1 = new Seance(
                    fieldtitre.getText(),
                    fieldDescription.getText(),
                    sqlDate,  // Utiliser la date convertie
                    idCoach,
                    idAdherent,
                    fieldType.getValue(),
                    fieldLien.getText(),
                    idPlanning,
                    heureDebut,
                    heureFin
            );

            sc.create(s1);
            if (calendarController != null) {
                calendarController.refreshCalendar();
            }
            fermerFenetre();
        } catch (Exception e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Une erreur est survenue lors de l'ajout de la séance.", Alert.AlertType.ERROR);
        }
    }

    private boolean validerChamps() {
        if (fieldtitre.getText().isEmpty() || fieldDescription.getText().isEmpty() || fieldLien.getText().isEmpty()
                || field_HeureDebut.getText().isEmpty()
                || field_Heurefin.getText().isEmpty() || field_adherent_Id.getValue() == null
                || field_Date.getValue() == null || fieldType.getValue() == null) {
            afficherAlerte("Champs vides", "Tous les champs doivent être remplis.", Alert.AlertType.WARNING);
            return false;
        }


        if (!estHeureValide(field_HeureDebut.getText())) {
            afficherAlerte("Format heure invalide", "L'heure de début doit être au format HH:mm.", Alert.AlertType.ERROR);
            return false;
        }

        if (!estHeureValide(field_Heurefin.getText())) {
            afficherAlerte("Format heure invalide", "L'heure de fin doit être au format HH:mm.", Alert.AlertType.ERROR);
            return false;

        }
        if (!fieldLien.getText().toLowerCase().endsWith(".mp4")) {
            afficherAlerte("Format vidéo invalide", "La vidéo doit être au format MP4.", Alert.AlertType.ERROR);
            return false;
        }

        return true;
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

    private void fermerFenetre() {
        Stage stage = (Stage) fieldtitre.getScene().getWindow();
        stage.close();
    }
    @FXML
    public void annulerSeance() {
        fermerFenetre();
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
            fieldLien.setText(selectedFile.getAbsolutePath());
        }
    }


}
