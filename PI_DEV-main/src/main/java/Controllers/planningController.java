package Controllers;

import Models.Seance;
import Models.Type;
import Services.CoachService;
import Services.CreateurEvenementService;
import Services.PlanningService;
import Services.SeanceService;
import Utils.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import video.api.client.ApiVideoClient;
import video.api.client.api.models.LiveStream;
import video.api.client.api.models.LiveStreamAssets;
import video.api.client.api.models.LiveStreamCreationPayload;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


public class planningController implements Initializable {

    public TextField search;
    ZonedDateTime dateFocus;
    ZonedDateTime today;
    private int selectedDayOfMonth = 0;

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private GridPane gridSeance;
    @FXML
    private Text year;
    @FXML
    private Text month;
    @FXML
    private FlowPane calendar;

    private int idCoach;
    private int idPlanning;
    private Button btnLancerLive;
    private String lienVideo;


    private String apiKey = "SJ9ExzbAMWr99kTXcls9nygm4eqUTOtK5SJ75i5GU8a"; // Remplace par ta clé API
    private String liveStreamId;
    private ApiVideoClient client;

    public planningController() throws SQLException {
    }

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dateFocus = ZonedDateTime.now();
        today = ZonedDateTime.now();
        try {
            drawCalendar();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        client = new ApiVideoClient(apiKey);

        search.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                RechercheSeance();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });


    }
    @FXML
    void backOneMonth() throws SQLException {
        dateFocus = dateFocus.minusMonths(1);
        System.out.println("⬅ Mois précédent : " + dateFocus.getMonthValue() + " - Année : " + dateFocus.getYear());
        refreshCalendar();
    }
    @FXML
    void forwardOneMonth() throws SQLException {
        dateFocus = dateFocus.plusMonths(1);
        System.out.println("➡ Mois suivant : " + dateFocus.getMonthValue() + " - Année : " + dateFocus.getYear());
        refreshCalendar();
    }
    void refreshCalendar() throws SQLException {
        calendar.getChildren().clear();
        drawCalendar();
    }
    private void drawCalendar() throws SQLException {
        DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy", Locale.FRENCH);
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM", Locale.FRENCH);

        year.setText(dateFocus.format(yearFormatter));
        month.setText(dateFocus.format(monthFormatter));

        double calendarWidth = calendar.getPrefWidth();
        double calendarHeight = calendar.getPrefHeight();
        double strokeWidth = 1;
        double spacingH = calendar.getHgap();
        double spacingV = calendar.getVgap();

        int monthMaxDate = dateFocus.getMonth().maxLength();
        if (dateFocus.getYear() % 4 != 0 && monthMaxDate == 29) {
            monthMaxDate = 28;
        }

        int dateOffset = ZonedDateTime.of(dateFocus.getYear(), dateFocus.getMonthValue(), 1, 0, 0, 0, 0, dateFocus.getZone()).getDayOfWeek().getValue() - 1;

        idCoach=Session.getInstance().getCurrentUser().getId();
        PlanningService ps = new PlanningService();
        idPlanning=ps.getIdPlanningByCoachId(idCoach);
        // Charger les séances pour le mois
        Map<Integer, List<Seance>> seancesMap = getSeancesForMonth(dateFocus, idPlanning);

        calendar.getChildren().clear();

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                StackPane stackPane = new StackPane();
                Rectangle rectangle = new Rectangle();
                rectangle.setFill(Color.TRANSPARENT);
                rectangle.setStroke(Color.BLACK);
                rectangle.setStrokeWidth(strokeWidth);
                double rectangleWidth = (calendarWidth / 7) - strokeWidth - spacingH;
                rectangle.setWidth(rectangleWidth);
                double rectangleHeight = (calendarHeight / 6) - strokeWidth - spacingV;
                rectangle.setHeight(rectangleHeight);
                stackPane.getChildren().add(rectangle);

                int calculatedDate = (i * 7) + j + 1 - dateOffset;
                if (calculatedDate > 0 && calculatedDate <= monthMaxDate) {
                    Text date = new Text(String.valueOf(calculatedDate));
                    date.setTranslateY(-(rectangleHeight / 2) * 0.75);
                    stackPane.getChildren().add(date);

                    // Vérifier si des séances existent pour cette date
                    List<Seance> seances = seancesMap.get(calculatedDate);
                    if (seances != null && !seances.isEmpty()) {
                        rectangle.setFill(Color.web("#F58400"));  // Colorier si une séance existe
                    }

                    // Gérer le clic sur la case du calendrier
                    stackPane.setOnMouseClicked(event -> {
                        try {
                            showSessionsForDate(calculatedDate);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
                calendar.getChildren().add(stackPane);
            }
        }
    }

    private void updateGridSeance(List<Seance> sessions) {
        gridSeance.getChildren().clear();
        gridSeance.getRowConstraints().clear();
        gridSeance.setVgap(10);
        gridSeance.setPadding(Insets.EMPTY);

        double cardHeight = 300;
        for (int i = 0; i < sessions.size(); i++) {
            Seance session = sessions.get(i);
            VBox sessionCard = createSeanceCard(session);
            sessionCard.setAlignment(Pos.CENTER);
            sessionCard.setPadding(new Insets(10));
            gridSeance.add(sessionCard, 0, i);

            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setValignment(VPos.TOP);
            gridSeance.getRowConstraints().add(rowConstraints);
        }

        double gridHeight = cardHeight * sessions.size() + gridSeance.getVgap() * (sessions.size() - 1);
        gridSeance.setPrefHeight(gridHeight);
    }
    public void showSessionsForDate(int dayOfMonth) throws SQLException {
        System.out.println("Jour sélectionné : " + dayOfMonth);

        // Mémoriser le jour sélectionné
        this.selectedDayOfMonth = dayOfMonth;

        PlanningService ps = new PlanningService();
        int idCoach = Session.getInstance().getCurrentUser().getId();
        int idPlanning = ps.getIdPlanningByCoachId(idCoach);

        List<Seance> sessionsForDay = getSessionsForSelectedDay(dayOfMonth, idPlanning);
        System.out.println("Date sélectionnée: " + dayOfMonth + " Mois: " + dateFocus.getMonthValue());
        System.out.println("Séances trouvées: " + sessionsForDay.size());

        updateGridSeance(sessionsForDay);
    }


    private List<Seance> getSessionsForSelectedDay(int dayOfMonth, int idPlanning) throws SQLException {
        Map<Integer, List<Seance>> seanceMap = getSeancesForMonth(dateFocus, idPlanning);
        return seanceMap.getOrDefault(dayOfMonth, new ArrayList<>());
    }
    public VBox createSeanceCard(Seance seance) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: #ffffff; " +
                "-fx-border-color: #ddd; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-padding: 15; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 5, 0, 0, 3);");

        // Effet au survol (hover)
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #f8f9fa; " +
                "-fx-border-color: #bbb; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-padding: 15; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 8, 0, 0, 4);"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: #ffffff; " +
                "-fx-border-color: #ddd; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-padding: 15; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 5, 0, 0, 3);"));

        card.setMaxWidth(400);
        card.setMinHeight(400);
        gridSeance.setVgap(10);

        // Contenu de la carte
        Label title = new Label("📌 " + seance.getTitre());
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #F58400;");

        Label description = new Label("📝 " + seance.getDescription());
        description.setStyle("-fx-font-size: 16; -fx-text-fill: #000000;");

        Label date = new Label("📅 " + seance.getDate());
        date.setStyle("-fx-font-size: 16; -fx-text-fill: #000000;");

        Label coachId = new Label("👤 Coach ID: " + seance.getIdCoach());
        coachId.setStyle("-fx-font-size: 16; -fx-text-fill: #000000;");

        Label adherentId = new Label("👥 Adhérent ID: " + seance.getIdAdherent());
        adherentId.setStyle("-fx-font-size: 16; -fx-text-fill: #000000;");

        Label type = new Label("📖 Type: " + seance.getType());
        type.setStyle("-fx-font-size: 16; -fx-text-fill: #000000;");


        Label startTime = new Label("🕒 Début: " + seance.getHeureDebut().toString());
        startTime.setStyle("-fx-font-size: 16; -fx-text-fill: #000000;");

        Label endTime = new Label("🕕 Fin: " + seance.getHeureFin().toString());
        endTime.setStyle("-fx-font-size: 16; -fx-text-fill: #000000;");


        Button btnModifier = new Button("✏ Modifier");
        btnModifier.setStyle("-fx-background-color: #F58400; -fx-text-fill: white; -fx-font-weight: bold;");
        btnModifier.setOnAction(event -> modifierSeance(seance));


        Button btnSupprimer = new Button("🗑 Supprimer");
        btnSupprimer.setStyle("-fx-background-color: #F58400; -fx-text-fill: white; -fx-font-weight: bold;");
        btnSupprimer.setOnAction(event -> {
            try {
                supprimerSeance(seance);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        Button btnLive = new Button();
        btnLive.setStyle("-fx-background-color: #F58400; -fx-text-fill: white; -fx-font-weight: bold;");

        if (seance.getType() == Type.EN_DIRECT) {
            btnLive.setText("📡 Lancer Live");
            btnLive.setOnAction(event -> lancerLive(seance));
        } else {
            btnLive.setText("▶ Regarder Vidéo");
            btnLive.setOnAction(event -> regarderVideo(seance));
        }

        // Ajouter les boutons dans un HBox
        HBox buttonBox = new HBox(10, btnModifier, btnSupprimer, btnLive);
        buttonBox.setAlignment(Pos.CENTER);

        card.getChildren().addAll(title, description, date, coachId, adherentId, type, startTime, endTime, buttonBox);

        return card;
    }

    private void regarderVideo(Seance seance) {

        try {
            // Charger le fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/regarderVideo.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur de la nouvelle scène
            RegarderVideo controller = loader.getController();
            controller.initData(seance); // Passer la séance au contrôleur

            // Créer une nouvelle fenêtre (Stage)
            Stage stage = new Stage();
            stage.setTitle("Regarder la vidéo");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void lancerLive(Seance seance) {

        try {
            // Créer un live stream si ce n'est pas encore fait
            LiveStream liveStream = client.liveStreams().create(
                    new LiveStreamCreationPayload().name("Séance en direct")
            );

            LiveStreamAssets assets = liveStream.getAssets();
            String playerUrl = liveStream.getAssets().getHls().toString();


            // Ouvrir la fenêtre du live
            ouvrirFenetreLive(playerUrl);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void ouvrirFenetreLive(String url) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/liveStream.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Live Streaming");
            stage.setScene(new Scene(root));
            stage.setResizable(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<Integer, List<Seance>> createSeanceMap(List<Seance> seances) {
        Map<Integer, List<Seance>> SeanceMap = new HashMap<>();
        for (Seance se : seances) {
            LocalDate localDate = se.getDate().toLocalDate();
            int seanceDate = localDate.getDayOfMonth();
            SeanceMap.computeIfAbsent(seanceDate, k -> new ArrayList<>()).add(se);
        }
        return SeanceMap;
    }

    private Map<Integer, List<Seance>> getSeancesForMonth(ZonedDateTime dateFocus, int idPlanning) throws SQLException {
        SeanceService sc = new SeanceService();
        List<Seance> seances = sc.getSeancesByPlanningId(idPlanning);
        System.out.println("🔄 Chargement des séances pour le mois : " + dateFocus.getMonthValue() + " - Année : " + dateFocus.getYear());

        List<Seance> filteredSeances = new ArrayList<>();
        for (Seance s : seances) {
            LocalDate seanceDate = s.getDate().toLocalDate();

            // Vérifier que la séance appartient bien au mois et à l'année de dateFocus
            if (seanceDate.getYear() == dateFocus.getYear() && seanceDate.getMonthValue() == dateFocus.getMonthValue()) {
                filteredSeances.add(s);
            }
        }

        // Debugging : Vérifier quelles séances sont gardées
        for (Seance s : filteredSeances) {
            System.out.println("Séance filtrée : " + s.getTitre() + " - " + s.getDate());
        }

        return createSeanceMap(filteredSeances);
    }

    private void modifierSeance(Seance seance) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/popupModifierSeance.fxml"));
            DialogPane dialogPane = loader.load();

            popupModifierSeanceController controller = loader.getController();
            controller.initData(seance);
            controller.setPlanningController(this);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            Optional<ButtonType> result = dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
    private void supprimerSeance(Seance seance) throws SQLException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer la séance ?");
        alert.setContentText("Voulez-vous vraiment supprimer cette séance ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            SeanceService service = new SeanceService();
            service.delete(seance.getId());

            // Remove only the card for the deleted session
            for (Node node : gridSeance.getChildren()) {
                if (node instanceof VBox) {
                    VBox card = (VBox) node;
                    Label title = (Label) card.getChildren().get(0); // assuming title is the first label

                    // If the title matches the session being deleted, remove the card
                    if (title.getText().contains(seance.getTitre())) {
                        gridSeance.getChildren().remove(card);
                        break; // Exit after removing the matching card
                    }}}}}



    public void ajouterSeance(ActionEvent actionEvent) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/popupSeance.fxml"));
        DialogPane dialogPane = loader.load();

        popupSeanceController popupController = loader.getController();
        popupController.setCalendarController(this);

        popupController.setIdPlanning(this.idPlanning);//////////
        popupController.loadAdherents();///////////////////////

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane(dialogPane);
        dialog.showAndWait();
    }
    public void TriParHeureDebut(ActionEvent event) throws SQLException {
        // Vérifier qu'un jour est sélectionné
        if (selectedDayOfMonth == 0) {
            System.out.println("Aucun jour sélectionné !");
            return;
        }

        PlanningService ps = new PlanningService();
        int idCoach = Session.getInstance().getCurrentUser().getId();
        int idPlanning = ps.getIdPlanningByCoachId(idCoach);
        List<Seance> sessions = getSessionsForSelectedDay(selectedDayOfMonth, idPlanning);

        // Trier la liste par heure de début
        sessions.sort(Comparator.comparing(Seance::getHeureDebut));

        updateGridSeance(sessions);
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
                    path = "/planning.fxml"; // Redirige vers la page de calendrier existant
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
    private void RechercheSeance() throws SQLException {
        // Récupérer le texte saisi
        String query = search.getText().toLowerCase().trim();

        // Charger toutes les séances du planning
        int idCoach = Session.getInstance().getCurrentUser().getId();
        int idPlanning = new PlanningService().getIdPlanningByCoachId(idCoach);
        List<Seance> allSeances = new SeanceService().getSeancesByPlanningId(idPlanning);

        // Filtrer uniquement par le titre (nom) de la séance, en protégeant contre les null
        List<Seance> filteredSeances = allSeances.stream()
                .filter(s -> s.getTitre() != null && s.getTitre().toLowerCase().contains(query))
                .collect(Collectors.toList());

        updateGridSeance(filteredSeances);
    }




}
