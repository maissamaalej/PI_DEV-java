
package Controllers;

import Models.ParticipantEvenement;
import Services.CreateurEvenementService;
import Utils.Session;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import Models.Evenement;
import Services.EvenementService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class Events {

    @FXML
    private VBox eventList; // VBox où les événements seront affichés


    private EvenementService evenementService = new EvenementService();
    @FXML
    private TextField searchField; // Champ de recherche

    public Events() throws SQLException {
    }

    @FXML
    public void initialize() {
        loadEvents();
        searchField.textProperty().addListener((observable, oldValue, newValue) -> searchEvents(newValue));
    }

    private CreateurEvenementService createurEvenementService = new CreateurEvenementService();

    private void loadPEventWithFilter(String etatFiltre) {
        try {
            List<Evenement> events = evenementService.getAll();
            List<Evenement> filterEvents = events.stream()
                    .filter(p -> p.getEtat().toString().equals(etatFiltre))
                    .toList();

            eventList.getChildren().clear(); // Nettoyer la liste avant de charger les événements

            for (Evenement event : filterEvents) {
                VBox eventCard = createEventCard(event); // Correction ici : ajouter l'événement en paramètre
                eventList.getChildren().add(eventCard); // Correction ici : utiliser eventList au lieu de eventContainer
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private VBox createEventCard(Evenement event) {
        VBox eventCard = new VBox();
        eventCard.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 15; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0); -fx-padding: 20;");
        eventCard.setPrefSize(350, 309);
        eventCard.setSpacing(10);

        // ImageView
        ImageView imageView = new ImageView();
        if (event.getImage() != null && event.getImage().length > 0) {
            imageView.setImage(new Image(new ByteArrayInputStream(event.getImage())));
        } else {
            imageView.setImage(new Image(getClass().getResourceAsStream("/path/to/default-image.jpg")));
        }
        imageView.setFitWidth(350);
        imageView.setPreserveRatio(true);
        imageView.setStyle("-fx-background-radius: 15 15 0 0; -fx-cursor: hand;");

        // Titre
        Label titleLabel = new Label(event.getTitre());
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: 700; -fx-text-fill: #333333;");

        // Type et état
        HBox typeStatusBox = new HBox(10);
        Label typeLabel = new Label("Type: " + event.getType());
        typeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666; "
                + "-fx-padding: 5 7; -fx-background-radius: 5; -fx-background-color: #e0e0e0;");

        Label etatLabel = new Label("Etat: " + event.getEtat());
        String etat = String.valueOf(event.getEtat());
        if ("ACTIF".equals(etat)) {
            etatLabel.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 5 7; -fx-background-radius: 5;");
        } else if ("EXPIRE".equals(etat)) {
            etatLabel.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-padding: 5 7; -fx-background-radius: 5;");
        } else {
            etatLabel.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: black; -fx-padding: 5 7; -fx-background-radius: 5;");
        }

        typeStatusBox.getChildren().addAll(typeLabel, etatLabel);

        // Description
        Label descriptionLabel = new Label("Description: " + event.getDescription());
        descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");
        descriptionLabel.setWrapText(true);

        // Détails (Lieu, Date, Prix)
        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(10);
        detailsGrid.setVgap(8);
        Label locationLabel = new Label(event.getLieu());
        Label dateLabel = new Label("Du " + event.getDateDebut() + " au " + event.getDateFin());
        Label priceLabel = new Label(event.getPrix() + " DT");
        detailsGrid.addRow(0, new Label("Lieu:"), locationLabel);
        detailsGrid.addRow(1, new Label("Date:"), dateLabel);
        detailsGrid.addRow(2, new Label("Prix:"), priceLabel);

        priceLabel.setStyle("-fx-text-fill: #2196F3; -fx-font-weight: 700;");

        // Séparateur
        Separator separator = new Separator();
        separator.setStyle("-fx-padding: 10 0;");

        // Organisateur et Capacité maximale
        HBox organizerBox = new HBox(15);
        Label organizerLabel = new Label("👤 " + event.getOrganisateur());
        Label maxLabel = new Label("👥 " + event.getCapaciteMaximale());
        organizerBox.getChildren().addAll(organizerLabel, maxLabel);

        // Ajouter les composants à la carte
        eventCard.getChildren().addAll(imageView, titleLabel, typeStatusBox, descriptionLabel, detailsGrid, separator, organizerBox);

        // Ajouter un événement de clic sur la carte
        eventCard.setOnMouseClicked(eventClick -> showEventDetails(event));

        return eventCard;
    }
    private List<Evenement> allEvents;

    private void searchEvents(String query) {
        // Filtrer les événements en fonction de la requête de recherche
        List<Evenement> filteredEvents = allEvents.stream()
                .filter(event -> event.getTitre().toLowerCase().contains(query.toLowerCase())
                        || event.getLieu().toLowerCase().contains(query.toLowerCase())
                        || event.getType().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());

        // Nettoyer la liste des événements actuels
        eventList.getChildren().clear();

        // Ajouter un titre de la liste filtrée
        Label titleLabell = new Label("Événements trouvés :");
        titleLabell.setStyle("-fx-font-size: 24px; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");
        eventList.getChildren().add(titleLabell);

        // Créer une carte pour chaque événement filtré
        for (Evenement event : filteredEvents) {
            VBox eventCard = createEventCard(event);  // Créer une carte pour chaque événement
            eventList.getChildren().add(eventCard);  // Ajouter la carte à la liste des événements
        }
    }



    void loadEvents() {
        try {
            allEvents = evenementService.getAll();
            eventList.getChildren().clear(); // Nettoyer la liste avant de charger les événements
            Label titleLabell = new Label("Liste des Événements :");
            titleLabell.setStyle("-fx-font-size: 24px; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");

            // S'assurer qu'il reste toujours visible
            eventList.getChildren().add(titleLabell);

            int id = Session.getInstance().getCurrentUser().getId();
            // Vérifier si l'utilisateur est un créateur d'événements
            if (createurEvenementService.isCreateurEvenement(id)) {
                HBox buttonBox = new HBox(20);
                buttonBox.setStyle("-fx-alignment: center-right; -fx-padding: 10;");

                Button creerEventBtn = new Button("Créer un événement");
                Button consulterMesEventsBtn = new Button("Consulter mes événements");

                creerEventBtn.setStyle("-fx-background-color: #F58400; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 5;");
                consulterMesEventsBtn.setStyle("-fx-background-color: #708090; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 5;");

                // Ajouter des actions aux boutons
                creerEventBtn.setOnAction(e -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddEvenement.fxml"));
                        Parent root = loader.load();

                        // Vérifie que le bouton a bien une scène avant d'appeler getWindow()
                        if (creerEventBtn.getScene() != null) {
                            Stage stage = (Stage) creerEventBtn.getScene().getWindow();
                            stage.setScene(new Scene(root));
                            stage.show();
                        } else {
                            System.out.println("Erreur : le bouton n'est pas attaché à une scène.");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

                consulterMesEventsBtn.setOnAction(e -> { try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/MyEvents.fxml"));
                    Parent root = loader.load();

                    // Vérifie que le bouton a bien une scène avant d'appeler getWindow()
                    if (consulterMesEventsBtn.getScene() != null) {
                        Stage stage = (Stage) consulterMesEventsBtn.getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.show();
                    } else {
                        System.out.println("Erreur : le bouton n'est pas attaché à une scène.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                });


                buttonBox.getChildren().addAll(creerEventBtn, consulterMesEventsBtn);
                eventList.getChildren().add(buttonBox);
            }else {
                HBox buttonBox = new HBox(20);
                buttonBox.setStyle("-fx-alignment: center-right; -fx-padding: 10;");

                Button parEventBtn = new Button("Mes Participation");

                parEventBtn.setStyle("-fx-background-color: #F58400; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 5;");

                // Ajouter des actions aux boutons
                parEventBtn.setOnAction(e -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MaParticipation.fxml"));
                        Parent root = loader.load();

                        // Vérifie que le bouton a bien une scène avant d'appeler getWindow()
                        if (parEventBtn.getScene() != null) {
                            Stage stage = (Stage) parEventBtn.getScene().getWindow();
                            stage.setScene(new Scene(root));
                            stage.show();
                        } else {
                            System.out.println("Erreur : le bouton n'est pas attaché à une scène.");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });




                buttonBox.getChildren().addAll(parEventBtn);
                eventList.getChildren().add(buttonBox);

            }

            List<Evenement> events = evenementService.getAll();
            HBox row = new HBox(50); // HBox pour contenir 2 cartes par ligne
            row.setStyle("-fx-alignment: center-start;"); // Alignement à gauche

            for (int i = 0; i < events.size(); i++) {
                Evenement event = events.get(i);

                // Création de la carte de l'événement (VBox)
                VBox eventCard = new VBox();
                eventCard.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0); -fx-padding: 20;");
                eventCard.setPrefSize(350, 309);
                eventCard.setSpacing(10);

                // ImageView
                ImageView imageView = new ImageView();
                if (event.getImage() != null && event.getImage().length > 0) {
                    imageView.setImage(new Image(new ByteArrayInputStream(event.getImage())));
                } else {
                    imageView.setImage(new Image(getClass().getResourceAsStream("/path/to/default-image.jpg")));
                }
                imageView.setFitWidth(350);
                imageView.setPreserveRatio(true);
                imageView.setStyle("-fx-background-radius: 15 15 0 0; -fx-cursor: hand;");

                // Titre
                Label titleLabel = new Label(event.getTitre());
                titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: 700; -fx-text-fill: #333333;");

                // Type et état
                HBox typeStatusBox = new HBox(10);
                Label typeLabel = new Label("Type: " + event.getType());
                typeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666; -fx-padding: 5 7; -fx-background-radius: 5; -fx-background-color: #e0e0e0;");

                Label etatLabel = new Label("Etat: " + event.getEtat());
                String etat = String.valueOf(event.getEtat());
                if ("ACTIF".equals(etat)) {
                    etatLabel.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 5 7; -fx-background-radius: 5;");
                } else if ("EXPIRE".equals(etat)) {
                    etatLabel.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-padding: 5 7; -fx-background-radius: 5;");
                } else {
                    etatLabel.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: black; -fx-padding: 5 7; -fx-background-radius: 5;");
                }

                typeStatusBox.getChildren().addAll(typeLabel, etatLabel);

                // Description
                Label descriptionLabel = new Label("Description: " + event.getDescription());
                descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");
                descriptionLabel.setWrapText(true);

                // Détails (Lieu, Date, Prix)
                GridPane detailsGrid = new GridPane();
                detailsGrid.setHgap(10);
                detailsGrid.setVgap(8);
                Label locationLabel = new Label(event.getLieu());
                Label dateLabel = new Label("Du " + event.getDateDebut() + " au " + event.getDateFin());
                Label priceLabel = new Label(event.getPrix() + " DT");
                detailsGrid.addRow(0, new Label("Lieu:"), locationLabel);
                detailsGrid.addRow(1, new Label("Date:"), dateLabel);
                detailsGrid.addRow(2, new Label("Prix:"), priceLabel);

                priceLabel.setStyle("-fx-text-fill: #2196F3; -fx-font-weight: 700;");
                // Séparateur
                Separator separator = new Separator();
                separator.setStyle("-fx-padding: 10 0;");

                // Organisateur et Capacité maximale
                HBox organizerBox = new HBox(15);
                Label organizerLabel = new Label("👤 " + event.getOrganisateur());
                Label maxLabel = new Label("👥 " + event.getCapaciteMaximale());
                organizerBox.getChildren().addAll(organizerLabel, maxLabel);

                // Ajouter les composants à la carte
                eventCard.getChildren().addAll(imageView, titleLabel, typeStatusBox, descriptionLabel, detailsGrid, separator, organizerBox);

                // Ajouter un événement de clic sur la carte
                eventCard.setOnMouseClicked(eventClick -> showEventDetails(event));

                // Ajouter la carte à la ligne actuelle
                row.getChildren().add(eventCard);

                // Tous les 2 événements, ajouter la ligne à la VBox et créer une nouvelle ligne
                if ((i + 1) % 2 == 0 || i == events.size() - 1) {
                    eventList.getChildren().add(row);
                    row = new HBox(50);
                    row.setStyle("-fx-alignment: center-start;");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showEventDetails(Evenement event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EventDetail.fxml"));
            Parent root = loader.load();

            EventDetailsController eventDetailsController = loader.getController();
            eventDetailsController.setEventDetails(event);

            Stage stage = (Stage) eventList.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Détails de l'événement");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    //ROOT

    @FXML
    void GoToEvent(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Events.fxml"));
            Parent root = loader.load();
            ((Button) actionEvent.getSource()).getScene().setRoot(root);
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajoutplanning.fxml"));
            Parent root = loader.load();
            ((Button) actionEvent.getSource()).getScene().setRoot(root);
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
    void filterEnACTIF(ActionEvent event) {
        loadPEventWithFilter("ACTIF");
    }

    @FXML
    void filterEXPIRE(ActionEvent event) {
        loadPEventWithFilter("EXPIRE");
    }


    @FXML
    void filterTout(ActionEvent event) {
        loadEvents();
    }
}

