package Controllers;

import Models.Seance;
import Services.CoachService;
import Services.CreateurEvenementService;
import Services.PlanningService;
import Utils.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.io.File;
import java.sql.SQLException;

public class RegarderVideo {
    public Button btnPlay;
    public Button btnStop;
    public BorderPane borderPane;
    private CreateurEvenementService createurEvenementService = new CreateurEvenementService();
    private CoachService coachService = new CoachService();
    private Media media;
    private MediaPlayer mediaPlayer;
    @FXML
    private Slider slider;
    @FXML
    private Label Durée;

    @FXML
    private MediaView MediaView;

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

    private Seance Seance;

    private boolean isPlayed = false;

    public RegarderVideo() throws SQLException {
    }

    @FXML
    void PlayVideo(ActionEvent event) {
        if(!isPlayed){
            btnPlay.setText("Pause");
            mediaPlayer.play();
            isPlayed = true;
        }else {
            btnPlay.setText("Play");
            mediaPlayer.pause();
            isPlayed = false;
        }
    }

    @FXML
    void StopVideo(ActionEvent event) {
        btnPlay.setText("Play");
        mediaPlayer.stop();
        isPlayed = false;
    }

    public void initData(Seance seance) {
        this.Seance = seance;
        System.out.println("Lecture de la vidéo : " + seance.getLienVideo());

        if (seance.getLienVideo() != null && !seance.getLienVideo().isEmpty()) {
            File file = new File(seance.getLienVideo());
            if (file.exists()) {
                Media media = new Media(file.toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                MediaView.setMediaPlayer(mediaPlayer);
                System.out.println("Vidéo chargée avec succès !");

                // Lier les dimensions du MediaView à celles du BorderPane
                if (borderPane != null) {
                    MediaView.fitWidthProperty().bind(borderPane.widthProperty());
                    MediaView.fitHeightProperty().bind(borderPane.heightProperty());
                    MediaView.setPreserveRatio(true);
                }

                // Configuration du slider et du label de durée
                mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                    slider.setValue(newValue.toSeconds());
                    Durée.setText("Duration: " + (int) slider.getValue()
                            + " / " + (int) media.getDuration().toSeconds());
                });

                mediaPlayer.setOnReady(() -> {
                    Duration totalDuration = media.getDuration();
                    slider.setMax(totalDuration.toSeconds());
                    Durée.setText("Duration: 00 / " + (int) totalDuration.toSeconds());
                });
            } else {
                System.out.println("Erreur : Le fichier vidéo n'existe pas -> " + seance.getLienVideo());
            }
        } else {
            System.out.println("Aucun lien vidéo fourni.");
        }
    }











    @FXML
    void GoToEvent(ActionEvent actionEvent) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
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
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
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
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
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
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
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
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
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
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddOffre.fxml"));
            Parent root = loader.load();
            ((Button) actionEvent.getSource()).getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SliderPressed(MouseEvent mouseEvent) {
        mediaPlayer.seek(Duration.seconds(slider.getValue()));
    }
}
