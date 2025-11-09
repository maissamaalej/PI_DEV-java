package Controllers;

import Models.Seance;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
public class PlanningAdherentController implements Initializable {
    ZonedDateTime dateFocus;
    ZonedDateTime today;

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
    private int idAdherent;

    public PlanningAdherentController() throws SQLException {
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
    }

    @FXML
    void backOneMonth() throws SQLException {
        dateFocus = dateFocus.minusMonths(1);
        calendar.getChildren().clear();
        drawCalendar();
    }
    @FXML
    void forwardOneMonth() throws SQLException {
        dateFocus = dateFocus.plusMonths(1);
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

        // Récupérer les séances pour l'adhérent et le mois actuel
        idAdherent = Session.getInstance().getCurrentUser().getId();
        Map<Integer, List<Seance>> calendrierSeances = getSeancesForMonth(dateFocus, idAdherent);

        // Vide la grille avant de redessiner
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
                    double textTranslationY = -(rectangleHeight / 2) * 0.75;
                    date.setTranslateY(textTranslationY);
                    stackPane.getChildren().add(date);

                    // Vérifie si des séances existent pour ce jour
                    List<Seance> seancesDuJour = calendrierSeances.get(calculatedDate);
                    if (seancesDuJour != null && !seancesDuJour.isEmpty()) {
                        rectangle.setFill(Color.web("#F58400"));  // Colorer en orange
                    }

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

    public void showSessionsForDate(int dayOfMonth) throws SQLException {
        System.out.println("Affichage des séances pour le jour: " + dayOfMonth);

        idAdherent = Session.getInstance().getCurrentUser().getId();
        List<Seance> sessionsForDay = getSessionsForSelectedDay(dayOfMonth, idAdherent);

        // Ajout d'un log pour vérifier si des séances sont récupérées
        System.out.println("Nombre de séances trouvées pour ce jour: " + sessionsForDay.size());

        gridSeance.getChildren().clear();
        gridSeance.getRowConstraints().clear();
        gridSeance.setVgap(10);
        gridSeance.setPadding(Insets.EMPTY);

        double cardHeight = 300;

        for (int i = 0; i < sessionsForDay.size(); i++) {
            Seance session = sessionsForDay.get(i);
            VBox sessionCard = createSeanceCard(session);

            sessionCard.setAlignment(Pos.CENTER);
            sessionCard.setPadding(new Insets(10));

            gridSeance.add(sessionCard, 0, i);

            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setValignment(VPos.TOP);
            gridSeance.getRowConstraints().add(rowConstraints);
        }

        double gridHeight = cardHeight * sessionsForDay.size() + gridSeance.getVgap() * (sessionsForDay.size() - 1);
        gridSeance.setPrefHeight(gridHeight);
    }


    private List<Seance> getSessionsForSelectedDay(int dayOfMonth, int id_adherent) throws SQLException {
        Map<Integer, List<Seance>> seanceMap = getSeancesForMonth(dateFocus, id_adherent);

        List<Seance> sessions = seanceMap.getOrDefault(dayOfMonth, new ArrayList<>());

        // Ajout de logs pour vérifier si des séances sont récupérées
        System.out.println("Jour sélectionné: " + dayOfMonth + " | Mois: " + dateFocus.getMonthValue() + " | Année: " + dateFocus.getYear());
        System.out.println("Séances trouvées: " + sessions.size());

        return sessions;
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

        Label videoLink = new Label("🎥 Vidéo: " + seance.getLienVideo());
        videoLink.setStyle("-fx-font-size: 16; -fx-text-fill: #000000;");

        Label startTime = new Label("🕒 Début: " + seance.getHeureDebut().toString());
        startTime.setStyle("-fx-font-size: 16; -fx-text-fill: #000000;");

        Label endTime = new Label("🕕 Fin: " + seance.getHeureFin().toString());
        endTime.setStyle("-fx-font-size: 16; -fx-text-fill: #000000;");

        Button btnparticiper = new Button("👤 participer");
        btnparticiper.setStyle("-fx-background-color: #F58400; -fx-text-fill: white; -fx-font-weight: bold;");
        btnparticiper.setOnAction(event -> ParticiperSeance(seance));

       HBox buttonBox = new HBox(10, btnparticiper);
       buttonBox.setAlignment(Pos.CENTER);

        card.getChildren().addAll(title, description, date, coachId, adherentId, type, videoLink, startTime, endTime, buttonBox);

        return card;
    }



    private void ParticiperSeance(Seance seance) {

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

    private Map<Integer, List<Seance>> getSeancesForMonth(ZonedDateTime dateFocus, int idAdherent) throws SQLException {
        SeanceService sc = new SeanceService();
        List<Seance> allSeances = sc.getSeancesByAdherentId(idAdherent);

        // Filtrer par mois et année
        List<Seance> filteredSeances = new ArrayList<>();
        for (Seance seance : allSeances) {
            LocalDate localDate = seance.getDate().toLocalDate();
            if (localDate.getYear() == dateFocus.getYear() && localDate.getMonthValue() == dateFocus.getMonthValue()) {
                filteredSeances.add(seance);
            }
        }

        return createSeanceMap(filteredSeances);
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
}
