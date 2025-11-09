
package Controllers;

import Models.EtatEvenement;
import Models.ParticipantEvenement;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import Models.Evenement;
import Models.User;
import Services.ParticipantEvenementService;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.ByteArrayInputStream;
import java.sql.SQLException;
import java.util.List;

public class MesParticpent {

    @FXML private VBox eventDetailsContainer;
    @FXML private VBox participantsContainer;  // Updated to reference VBox for cards

    private ParticipantEvenementService participantService = new ParticipantEvenementService();

    public MesParticpent() throws SQLException {
    }

    public void initialize() {
        // No need for TableColumn and TableView initialization anymore
    }

    public void setEventData(Evenement event, MyEvents myEvents) {
        // Clear previous content
        eventDetailsContainer.getChildren().clear();

        // Create event card similar to MyEvents
        VBox eventCard = createEventCard(event);
        eventDetailsContainer.getChildren().addAll(eventCard, participantsContainer);

        loadParticipants(event.getId());
    }
    private int eventId;
    private VBox createEventCard(Evenement event) {
        this.eventId = event.getId();
        VBox eventCard = new VBox();
        eventCard.getStyleClass().add("event-card");
        eventCard.setSpacing(10);
        eventCard.setPadding(new Insets(15));
        eventCard.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 20, 0, 0, 0);");

        // Image section
        ImageView imageView = new ImageView();
        if (event.getImage() != null && event.getImage().length > 0) {
            imageView.setImage(new Image(new ByteArrayInputStream(event.getImage())));
        } else {
            imageView.setImage(new Image(getClass().getResourceAsStream("/images/default_event.jpg")));
        }
        imageView.setFitHeight(200);
        imageView.setFitWidth(350);
        imageView.setPreserveRatio(true);

        // Text content section
        VBox textContent = new VBox(5);
        Label titleLabel = new Label(event.getTitre());
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: 700; -fx-text-fill: #333333;");

        Label typeLabel = new Label("Type: " + event.getType());
        typeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");

        Label locationLabel = new Label("Lieu: " + event.getLieu());
        locationLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");

        Label dateLabel = new Label("Date : Du " + event.getDateDebut() + " au " + event.getDateFin());
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");

        Label descLabel = new Label("Description: " + event.getDescription());
        descLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");

        Label priceLabel = new Label("Prix: " + event.getPrix() + " DT");
        priceLabel.setStyle("-fx-text-fill: #2196F3; -fx-font-weight: 700;");

        // Status label
        Label etatLabel = new Label("Etat: " + event.getEtat());
        EtatEvenement etat = event.getEtat();
        if ("ACTIF".equals(etat)) {
            etatLabel.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 5 7; -fx-background-radius: 5;");
        } else if ("EXPIRE".equals(etat)) {
            etatLabel.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-padding: 5 7; -fx-background-radius: 5;");
        } else {
            etatLabel.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: black; -fx-padding: 5 7; -fx-background-radius: 5;");
        }

        // Organizer info
        HBox organizerBox = new HBox(15);
        Label organizerLabel = new Label("👤 " + event.getOrganisateur());
        Label maxLabel = new Label("👥 " + event.getCapaciteMaximale());
        organizerBox.getChildren().addAll(organizerLabel, maxLabel);

        // Assemble text content
        textContent.getChildren().addAll(
                titleLabel,
                descLabel,
                typeLabel,
                locationLabel,
                dateLabel,
                priceLabel,
                etatLabel,
                organizerBox
        );

        // Create content row
        HBox contentRow = new HBox(15);
        contentRow.getChildren().addAll(imageView, textContent);

        eventCard.getChildren().add(contentRow);
        return eventCard;
    }

    private void loadParticipants(int eventId) {
        List<ParticipantEvenement> participants = participantService.getParticipantsByEvent(eventId);

        // Clear previous participant cards
        participantsContainer.getChildren().clear();

        // Create a card for each participant
        for (ParticipantEvenement participant : participants) {
            HBox participantCard = createParticipantCard(participant);
            participantsContainer.getChildren().add(participantCard);
        }
    }

    private void loadParticipantsWithFilter(String etatFiltre) {
        List<ParticipantEvenement> allParticipants = participantService.getParticipantsByEvent(eventId);  // Utiliser eventId stocké

        List<ParticipantEvenement> filteredParticipants = allParticipants.stream()
                .filter(p -> p.getEtatPaiement().toString().equals(etatFiltre))
                .toList();

        participantsContainer.getChildren().clear();

        for (ParticipantEvenement participant : filteredParticipants) {
            HBox participantCard = createParticipantCard(participant);
            participantsContainer.getChildren().add(participantCard);
        }
    }


    private HBox createParticipantCard(ParticipantEvenement participantEvenement) {
        HBox participantCard = new HBox();
        participantCard.setSpacing(15); // Espacement uniforme entre tous les éléments
        participantCard.setPadding(new Insets(15));
        participantCard.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 20, 0, 0, 0);");

        User participant = participantEvenement.getParticipant(); // Accéder à l'objet User

        // Nom
        Label nameLabel = new Label(participant.getNom());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: 700; -fx-text-fill: #333333;");

        // Prénom
        Label prenomLabel = new Label(participant.getPrenom());
        prenomLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: 700; -fx-text-fill: #333333;");

        // Email
        Label emailLabel = new Label(participant.getEmail());
        emailLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");

        // Image
        Label imgLabel = new Label(participant.getImage());
        imgLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: 700; -fx-text-fill: #333333;");

        // EtatPaiement
        Label etatPaiementLabel = new Label("" + participantEvenement.getEtatPaiement());
        etatPaiementLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 700;");

        switch (participantEvenement.getEtatPaiement()) {
            case PAYE:
                etatPaiementLabel.setStyle(etatPaiementLabel.getStyle() + " -fx-text-fill: #4CAF50;"); // Vert
                break;
            case EN_ATTENTE:
                etatPaiementLabel.setStyle(etatPaiementLabel.getStyle() + " -fx-text-fill: #FF9800;"); // Orange
                break;
            case ANNULER:
                etatPaiementLabel.setStyle(etatPaiementLabel.getStyle() + " -fx-text-fill: #F44336;"); // Rouge
                break;
            default:
                etatPaiementLabel.setStyle(etatPaiementLabel.getStyle() + " -fx-text-fill: #666666;"); // Gris par défaut
                break;
        }

        // Date
        Label dateLabel = new Label("Le: " + participantEvenement.getDateInscription());
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");

        // Ajouter tous les labels à la HBox
        participantCard.getChildren().addAll(emailLabel, nameLabel, prenomLabel, imgLabel, etatPaiementLabel, dateLabel);

        return participantCard;
    }

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
    void filterEnAttente(ActionEvent event) {
        loadParticipantsWithFilter("EN_ATTENTE");
    }

    @FXML
    void filterPaye(ActionEvent event) {
        loadParticipantsWithFilter("PAYE");
    }

    @FXML
    void filterAnnuler(ActionEvent event) {
        loadParticipantsWithFilter("ANNULER");
    }
    @FXML
    void filterTout(ActionEvent event) {
        loadParticipants(eventId);
    }
}


