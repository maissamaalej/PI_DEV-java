

package Controllers;

import Models.*;
import Services.ParticipantEvenementService;
import Utils.Session;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.services.calendar.model.EventReminder;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


import java.awt.*;
import java.io.*;

import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.resource.Emailv31;
import javafx.scene.layout.HBox;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.json.JSONArray;
import org.json.JSONObject;

import Utils.StripeConfig;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Token;
import com.stripe.param.ChargeCreateParams;


import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;

import java.security.GeneralSecurityException;

import static Services.GoogleCalendarService.*;


import javafx.scene.control.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.io.File;
import java.io.IOException;
import java.io.File;

import java.io.IOException;

public class EventDetailsController {

    @FXML private ImageView img;
    @FXML private Label titleLabel;
    @FXML private Label typeLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label locationLabel;
    @FXML private Label priceLabel;
    @FXML private Label dateLabel;
    @FXML private Label etatLabel;
    @FXML private Label organisateurLabel;
    @FXML private Label maxLabel;
    @FXML private Label weatherLabel;


    @FXML
    private Button payerBtn;
    @FXML
    private TextField cvc;
    @FXML
    private TextField mm;
    @FXML
    private TextField ncarte;
    @FXML
    private TextField nomT;
    @FXML
    private TextField yy;


    @FXML
    private ImageView weatherIcon;
    @FXML private HBox weatherContainer;


    @FXML private Label temperatureLabel;
    @FXML private Label descriptionnLabel;
    public void initialize() {
        StripeConfig.init(); // Initialiser Stripe
        payerBtn.setOnAction(this::handlePayment);


//        Font.loadFont(getClass().getResource("/fonts/fontawesome-webfont.ttf").toExternalForm(), 12);
    }

    public void setEventDetails(Evenement event) {
        this.event = event;
        // Check if the event image is available
        if (event.getImage() != null && event.getImage().length > 0) {
            Image image = new Image(new ByteArrayInputStream(event.getImage()));
            img.setImage(image);  // Set the image in the ImageView
        } else {
            // If there's no image, set a default image
            img.setImage(new Image(getClass().getResourceAsStream("/path/to/default-image.jpg")));
        }

        // Set the details of the event to the corresponding labels
        titleLabel.setText(event.getTitre());
        descriptionLabel.setText("Description: " + event.getDescription());
        locationLabel.setText(event.getLieu());
        priceLabel.setText("prix: " + event.getPrix());
        dateLabel.setText("Du " + event.getDateDebut() + " au " + event.getDateFin());
        typeLabel.setText("Type: " + event.getType());
        organisateurLabel.setText("Organisateur: " + event.getOrganisateur());
        maxLabel.setText("capacite Maximale : " + event.getCapaciteMaximale());
        // Set price label with "DT" suffix
        double price = event.getPrix();  // Assuming getPrix() returns a double
        priceLabel.setText( price + " DT");

        // Update the etatLabel based on the event state (ACTIF or EXPIRE)
        String eventState = String.valueOf(event.getEtat());
        System.out.println("Event state: " + eventState);  // Debugging output

        if ("ACTIF".equals(eventState)) {
            etatLabel.setText("Etat: " + eventState);
            etatLabel.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;-fx-padding: 5 7; -fx-background-radius: 5;");
        } else if ("EXPIRE".equals(eventState)) {
            etatLabel.setText("Etat: " + eventState);
            etatLabel.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;-fx-padding: 5 7; -fx-background-radius: 5;");  // Red for expired
        } else {
            etatLabel.setText("Etat: " + eventState);
            etatLabel.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: black;");  // Default gray color
        }
        displayWeatherForEvent(event.getLieu());
    }

private void displayWeatherForEvent(String location) {
    try {
        String apiKey = "ca261522f8b8207fb287fca1899b3690";
        String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + location + "&appid=" + apiKey + "&units=metric";

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JSONObject jsonResponse = new JSONObject(response.toString());
        String description = jsonResponse.getJSONArray("weather")
                .getJSONObject(0).getString("description");
        double temp = jsonResponse.getJSONObject("main").getDouble("temp");
        String iconCode = jsonResponse.getJSONArray("weather")
                .getJSONObject(0).getString("icon");

        Platform.runLater(() -> {
            String iconPath = getWeatherIconPath(iconCode);
            weatherIcon.setImage(new Image(getClass().getResourceAsStream(iconPath)));
            temperatureLabel.setText(String.format("%.1f°C", temp));
            descriptionnLabel.setText(capitalize(description));
        });

    } catch (Exception e) {
        showAlert(Alert.AlertType.ERROR, "Error",
                "Failed to fetch weather data: " + e.getMessage());
    }
}

    private String getWeatherIconPath(String iconCode) {
        switch (iconCode) {
            case "01d": return "/icons/sun.png";  // sun (day)
            case "01n": return "/icons/moon.png";  // moon (night)
            case "02d": return "/icons/cloud.png";  // cloud (day)
            case "02n": return "/icons/cloud.png";  // cloud (night)
            case "03d": case "03n": return "/icons/cloud.png";  // scattered clouds
            case "04d": case "04n": return "/icons/cloud.png";  // broken clouds
            case "09d": case "09n": return "/icons/rain.png";  // shower rain
            case "10d": return "/icons/rain.png";  // rain (day)
            case "10n": return "/icons/rain.png";  // rain (night)
            case "11d": case "11n": return "/icons/thunderstorm.png";  // thunderstorm
            case "13d": case "13n": return "/icons/snow.png";  // snow
            case "50d": case "50n": return "/icons/mist.png";  // mist
            default: return "/icons/default.png";  // default icon
        }
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    private Evenement event;
    @FXML
    private void handleReservationClick(ActionEvent event) throws SQLException {
        if (this.event == null) {
            System.out.println("Erreur : Aucun événement sélectionné.");
            return;
        }
        if (this.event.getEtat() == EtatEvenement.valueOf("EXPIRE")){
            System.out.println("Erreur : L'etat de evenement est expire.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Échec de la réservation");
            alert.setHeaderText(null);
            alert.setContentText("Erreur : L'etat de evenement est expire.");
            alert.showAndWait();
            return;

        }        int idEvenement = this.event.getId();
        int idParticipant = Session.getInstance().getCurrentUser().getId(); // Récupère l'ID du participant depuis la session
        Date dateInscription = new Date(); // Date actuelle
        etatPaiement etat = etatPaiement.EN_ATTENTE; // Valeur par défaut

        ParticipantEvenement participant = new ParticipantEvenement(idParticipant, dateInscription, etat, idEvenement);

        ParticipantEvenementService service = new ParticipantEvenementService();
        try {
            boolean success = service.create(participant);
            if (success) {
                //send mail
                String recipientEmail = Session.getInstance().getCurrentUser().getEmail();
                sendEmail(recipientEmail, this.event);

                // Affiche une alerte de succès avec un message de rappel
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Réservation réussie");
                alert.setHeaderText(null);
                alert.setContentText("Votre réservation a été effectuée avec succès !\n\n⚠️Attention : Si vous ne fixez pas votre état de paiement \ndans 3 jours, votre réservation sera annulée.");
                alert.showAndWait();
            } else {
                // Affiche une alerte d'erreur
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Échec de la réservation");
                alert.setHeaderText(null);
                alert.setContentText("Une erreur est survenue lors de votre réservation.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de l'inscription : " + e.getMessage());

            // Affiche une alerte d'erreur en cas d'exception
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Une erreur est survenue");
            alert.setContentText("Détails : " + e.getMessage());
            alert.showAndWait();
        }
    }




    private void sendEmail(String recipientEmail, Evenement event) {
        // Mailjet API credentials

        final String apiKey = "7d6d1541371e53bbe4db88b129dbbdf3";
        final String apiSecret = "8a79a0d0bdef1ec3122fe35e642bad4b";
        // Initialize Mailjet client using the builder pattern
        ClientOptions options = ClientOptions.builder()
                .apiKey(apiKey)
                .apiSecretKey(apiSecret)
                .build();
        MailjetClient client = new MailjetClient(options);

        // Create the email content
        JSONObject message = new JSONObject();
        message.put(Emailv31.Message.FROM, new JSONObject()
                .put("Email", "houssemm.labidi@gmail.com")
                .put("Name", "Couchini"));
        message.put(Emailv31.Message.TO, new JSONArray()
                .put(new JSONObject()
                        .put("Email", recipientEmail)
                        .put("Name", "Recipient Name")));
        message.put(Emailv31.Message.SUBJECT, "Evénement Details: " + event.getTitre());
        message.put(Emailv31.Message.TEXTPART,
                "Titre : " + event.getTitre() + "\n" +
                        "Description: " + event.getDescription() + "\n" +
                        "Lieu: " + event.getLieu() + "\n" +
                        "Prix: " + event.getPrix() + " DT\n" +
                        "Date : " + event.getDateDebut() + " au " + event.getDateFin() + "\n" +
                        "Type: " + event.getType() + "\n" +
                        "Organisateur : " + event.getOrganisateur() + "\n\n"

        );
        message.put(Emailv31.Message.HTMLPART,
                "Bonjour <b>" +Session.getInstance().getCurrentUser().getPrenom()+"</b><br />" +
                        "Vous êtes bien inscrit à l’événement "+event.getTitre()+" via notre application Coachini.<br />" +
                        " Voici les détails de votre inscription :<br /><br />"+

                        "<h3>Evénement Details:</h3><br />" +
                        "<b>Titre:</b> " + event.getTitre() + "<br />" +
                        "<b>Description:</b> " + event.getDescription() + "<br />" +
                        "<b>Lieu:</b> " + event.getLieu() + "<br />" +
                        "<b>Prix:</b> " + event.getPrix() + " DT<br />" +
                        "<b>Date:</b> " + event.getDateDebut() + " to " + event.getDateFin() + "<br />" +
                        "<b>Type:</b> " + event.getType() + "<br />" +
                        "<b>Organisateur :</b> " + event.getOrganisateur() + "<br /><br />"+
                        "<b>⚠\uFE0F Important :</b> Paiement requis <br />"+
                        "Pour valider définitivement votre inscription, veuillez effectuer le paiement dans un délai de trois jours. <br /><br />"+
                        "Passé ce délai, votre inscription sera annulée automatiquement. <br />"+
                        "Merci de votre confiance et à bientôt ! <br />"+
                        "<h2>L’équipe Coachini<h2>"
        );

        // Create the Mailjet request
        MailjetRequest request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray().put(message));

        try {
            // Send the email
            MailjetResponse response = client.post(request);
            if (response.getStatus() == 200) {
                System.out.println("Email sent successfully!");
            } else {
                System.out.println("Error occurred: " + response.getStatus());
                System.out.println(response.getData());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void sendPaymentConfirmationEmail(String recipientEmail, Evenement event) {
        // Mailjet API credentials
        final String apiKey = "7d6d1541371e53bbe4db88b129dbbdf3";
        final String apiSecret = "8a79a0d0bdef1ec3122fe35e642bad4b";

        ClientOptions options = ClientOptions.builder()
                .apiKey(apiKey)
                .apiSecretKey(apiSecret)
                .build();
        MailjetClient client = new MailjetClient(options);

        // Create the email content
        JSONObject message = new JSONObject();
        message.put(Emailv31.Message.FROM, new JSONObject()
                .put("Email", "houssemm.labidi@gmail.com")
                .put("Name", "Couchini"));

        message.put(Emailv31.Message.TO, new JSONArray()
                .put(new JSONObject()
                        .put("Email", recipientEmail)
                        .put("Name", "Client")));

        message.put(Emailv31.Message.SUBJECT, "Confirmation de Paiement - " + event.getTitre());

        message.put(Emailv31.Message.TEXTPART,
                "Bonjour " + Session.getInstance().getCurrentUser().getPrenom() + ",\n\n" +
                        "Votre paiement pour l'événement " + event.getTitre() + " a été effectué avec succès.\n\n" +
                        "Détails du paiement:\n" +
                        "Montant: " + event.getPrix() + " DT\n" +
                        "Date: " + new Date() + "\n\n" +
                        "Merci pour votre confiance!\n" +
                        "L'équipe Coachini"
        );

        message.put(Emailv31.Message.HTMLPART,
                "<html>" +
                        "<body>" +
                        "<h2>Bonjour <b>" + Session.getInstance().getCurrentUser().getPrenom() + "</b>,</h2>" +
                        "<p>Votre paiement pour l'événement <strong>" + event.getTitre() + "</strong> a été effectué avec succès.</p>" +
                        "<h3>Détails du paiement :</h3>" +
                        "<ul>" +
                        "<li><strong>Événement:</strong> " + event.getTitre() + "</li>" +
                        "<li><strong>Montant:</strong> " + event.getPrix() + " DT</li>" +
                        "<li><strong>Date de paiement:</strong> " + new Date() + "</li>" +
                        "</ul>" +
                        "<p>Vous pouvez maintenant accéder à l'événement en toute sérénité.</p>" +
                        "<p>Merci pour votre confiance !</p>" +
                        "<br/>" +
                        "<h3>L'équipe Coachini</h3>" +
                        "</body>" +
                        "</html>"
        );

        MailjetRequest request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray().put(message));

        try {
            MailjetResponse response = client.post(request);
            if (response.getStatus() == 200) {
                System.out.println("Confirmation email sent successfully!");
            } else {
                System.out.println("Failed to send confirmation email: " + response.getStatus());
            }
        } catch (Exception e) {
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
    private void handlePayment(ActionEvent event) {
        try {
            if (this.event == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun événement sélectionné !");
                return;
            }
            if (this.event.getEtat() == EtatEvenement.valueOf("EXPIRE")) {
                System.out.println("Erreur : L'état de l'événement est expiré.");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Échec de la réservation");
                alert.setHeaderText(null);
                alert.setContentText("Erreur : L'état de l'événement est expiré.");
                alert.showAndWait();
                return;
            }

            // Vérifier les champs
            if (!validatePaymentFields()) {
                return;
            }

            // 🔥 Générer un vrai token Stripe
            String dynamicToken = createStripeToken();
            if (dynamicToken == null || dynamicToken.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Token de paiement invalide !");
                return;
            }

            long amount = (long) (this.event.getPrix() * 100); // Prix en centimes

            Charge charge = Charge.create(ChargeCreateParams.builder()
                    .setAmount(amount)
                    .setCurrency("usd")
                    .setSource(dynamicToken) // 🔥 Utilisation du vrai token reçu
                    .setDescription("Paiement pour l'événement: " + this.event.getTitre())
                    .build());

            if (charge.getPaid()) {
                new ParticipantEvenementService().updatePaymentStatus(
                        Session.getInstance().getCurrentUser().getId(),
                        this.event.getId()
                );
                sendPaymentConfirmationEmail(
                        Session.getInstance().getCurrentUser().getEmail(),
                        this.event
                );

                // Générer le pass pour l'événement
//                String passFilePath = generateEventPass(this.event);
                generateModernEventPass(this.event);

                // Afficher un lien de téléchargement ou un bouton pour télécharger le pass
//                showDownloadLink(passFilePath);

                showAlert(Alert.AlertType.INFORMATION, "Succès", "Paiement effectué avec succès !");

            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Le paiement a échoué.");
            }

        } catch (StripeException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Problème de paiement : " + e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



//
//    public void generateModernEventPass(Evenement event) throws IOException {
//        try (PDDocument document = new PDDocument()) {
//            PDPage page = new PDPage(PDRectangle.A4);
//            document.addPage(page);
//
//            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
//                // Setup code remains the same
//                float margin = 50;
//                float width = page.getMediaBox().getWidth() - 2 * margin;
//                float yStart = page.getMediaBox().getHeight() - margin;
//
//                // Header drawing code remains the same
//                contentStream.setNonStrokingColor(23, 107, 135);
//                contentStream.addRect(0, yStart - 40, page.getMediaBox().getWidth(), 40);
//                contentStream.fill();
//
//                // Title text
//                contentStream.beginText();
//                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 24);
//                contentStream.setNonStrokingColor(255, 255, 255);
//                contentStream.newLineAtOffset(margin, yStart - 30);
//                contentStream.showText("EVENT PASS");
//                contentStream.endText();
//
//                float yPosition = yStart - 80;
//
//                // Image handling remains the same
//                if (event.getImage() != null && event.getImage().length > 0) {
//                    PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, event.getImage(), "event");
//                    float imageWidth = 150;
//                    float imageHeight = 150 * pdImage.getHeight() / pdImage.getWidth();
//                    contentStream.drawImage(pdImage, margin, yPosition - imageHeight, imageWidth, imageHeight);
//                    yPosition -= imageHeight + 20;
//                }
//
//                // Event details with corrected text handling
//                contentStream.setNonStrokingColor(0, 0, 0);
//                addDetailRow(contentStream, margin, yPosition, "DATE:",
//                        event.getDateDebut() + " - " + event.getDateFin());
//                yPosition -= 30;
//
//                addDetailRow(contentStream, margin, yPosition, "LOCATION:", event.getLieu());
//                yPosition -= 30;
//
//                addDetailRow(contentStream, margin, yPosition, "PRICE:",
//                        String.format("%.2f TND", event.getPrix()));
//                yPosition -= 30;
//
//                addDetailRow(contentStream, margin, yPosition, "ORGANIZER:", event.getOrganisateur());
//                yPosition -= 30;
//
//                addDetailRow(contentStream, margin, yPosition, "CAPACITY:",
//                        String.valueOf(event.getCapaciteMaximale()) + " seats");
//                yPosition -= 40;
//
//                // Description box
//                contentStream.setNonStrokingColor(245, 245, 245);
//                contentStream.addRect(margin, yPosition - 130, width, 120);
//                contentStream.fill();
//
//                // Récupérer l'utilisateur connecté
//                User currentUser = Session.getInstance().getCurrentUser();
//                String userName = currentUser.getNom();
//                String userEmail = currentUser.getEmail();
//
//// Affichage des informations de l'utilisateur
//                addDetailRow(contentStream, margin, yPosition, "USER:", userName);
//                yPosition -= 30;
//                addDetailRow(contentStream, margin, yPosition, "EMAIL:", userEmail);
//                yPosition -= 40;
//
//                // Description text with proper text handling
//                contentStream.beginText();
//                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
//                contentStream.newLineAtOffset(margin + 5, yPosition - 20);
//                contentStream.showText("DESCRIPTION:");
//                contentStream.setFont(PDType1Font.HELVETICA, 10);
//                contentStream.newLineAtOffset(0, -15);
//                addWrappedText(contentStream, event.getDescription(), width - 10, margin + 5);
//                contentStream.endText();
//
//            } // contentStream auto-closed here
//
//            // Save and open code remains the same
//            String fileName = "Event_Pass_" + event.getId() + ".pdf";
//            File file = new File(System.getProperty("user.home"), "Downloads/" + fileName);
//            document.save(file);
//            openPDF(file);
//        }
//    }
public void generateModernEventPass(Evenement event) throws IOException {
    try (PDDocument document = new PDDocument()) {
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            // Setup code remains the same
            float margin = 50;
            float width = page.getMediaBox().getWidth() - 2 * margin;
            float yStart = page.getMediaBox().getHeight() - margin;

            // Header drawing code remains the same
            contentStream.setNonStrokingColor(23, 107, 135); // Blue header background
            contentStream.addRect(0, yStart - 40, page.getMediaBox().getWidth(), 40);
            contentStream.fill();

            // Title text
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 24);
            contentStream.setNonStrokingColor(255, 255, 255); // White title text
            contentStream.newLineAtOffset(margin, yStart - 30);
            contentStream.showText("EVENT PASS");
            contentStream.endText();

            float yPosition = yStart - 80;

            // Image handling remains the same
            if (event.getImage() != null && event.getImage().length > 0) {
                PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, event.getImage(), "event");
                float imageWidth = 150;
                float imageHeight = 150 * pdImage.getHeight() / pdImage.getWidth();
                contentStream.drawImage(pdImage, margin, yPosition - imageHeight, imageWidth, imageHeight);
                yPosition -= imageHeight + 20;
            }

            // Event details with corrected text handling
            contentStream.setNonStrokingColor(0, 0, 0); // Ensure black text color

            addDetailRow(contentStream, margin, yPosition, "DATE:", event.getDateDebut() + " - " + event.getDateFin());
            yPosition -= 30;

            addDetailRow(contentStream, margin, yPosition, "LOCATION:", event.getLieu());
            yPosition -= 30;

            addDetailRow(contentStream, margin, yPosition, "PRICE:", String.format("%.2f TND", event.getPrix()));
            yPosition -= 30;

            addDetailRow(contentStream, margin, yPosition, "ORGANIZER:", event.getOrganisateur());
            yPosition -= 30;

            addDetailRow(contentStream, margin, yPosition, "CAPACITY:", String.valueOf(event.getCapaciteMaximale()) + " seats");
            yPosition -= 40;

            // Description box
            contentStream.setNonStrokingColor(245, 245, 245); // Light gray background for description
            contentStream.addRect(margin, yPosition - 130, width, 120);
            contentStream.fill();

            // Récupérer l'utilisateur connecté
            User currentUser = Session.getInstance().getCurrentUser();
            String userName = "Not Available";
            String userEmail = "Not Available";

            if (currentUser != null) {
                userName = currentUser.getNom();
                userEmail = currentUser.getEmail();

                // Handle cases where getNom() or getEmail() might return null
                if (userName == null || userName.isEmpty()) {
                    userName = "Not Available";
                }
                if (userEmail == null || userEmail.isEmpty()) {
                    userEmail = "Not Available";
                }
            }

            // Affichage des informations de l'utilisateur
            contentStream.setNonStrokingColor(0, 0, 0); // Ensure black text color for user details
            addDetailRow(contentStream, margin, yPosition, "USER:", userName);
            yPosition -= 30;
            addDetailRow(contentStream, margin, yPosition, "EMAIL:", userEmail);
            yPosition -= 40;

            // Description text with proper text handling
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.setNonStrokingColor(0, 0, 0); // Ensure black text color for description
            contentStream.newLineAtOffset(margin + 5, yPosition - 20);
            contentStream.showText("DESCRIPTION:");
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.newLineAtOffset(0, -15);
            addWrappedText(contentStream, event.getDescription(), width - 10, margin + 5);
            contentStream.endText();

        } // contentStream auto-closed here

        // Save and open code remains the same
        String fileName = "Event_Pass_" + event.getId() + ".pdf";
        File file = new File(System.getProperty("user.home"), "Downloads/" + fileName);
        document.save(file);
        openPDF(file);
    }
}

    // Helper method to add detail rows
    private void addDetailRow(PDPageContentStream contentStream, float x, float y, String label, String value) throws IOException {
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.setNonStrokingColor(0, 0, 0); // Ensure black text color
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(label);

        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.newLineAtOffset(80, 0); // Offset for the value
        contentStream.showText(value != null ? value : "Not Available");
        contentStream.endText();
    }

//    private void addDetailRow(PDPageContentStream contentStream, float x, float y,
//                              String label, String value) throws IOException {
//        contentStream.beginText();
//        try {
//            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
//            contentStream.newLineAtOffset(x, y);
//            contentStream.showText(label);
//            contentStream.setFont(PDType1Font.HELVETICA, 12);
//            contentStream.newLineAtOffset(100, 0);
//            contentStream.showText(value);
//        } finally {
//            contentStream.endText();
//        }
//    }

    private void addWrappedText(PDPageContentStream contentStream, String text,
                                float maxWidth, float startX) throws IOException {
        String[] words = text.split(" ");
        float currentOffset = 0;
        StringBuilder line = new StringBuilder();
        PDType1Font font = PDType1Font.HELVETICA;
        float fontSize = 10;

        for (String word : words) {
            float wordWidth = font.getStringWidth(word + " ") * fontSize / 1000;
            if (currentOffset + wordWidth > maxWidth) {
                // Draw current line
                contentStream.showText(line.toString());
                contentStream.newLineAtOffset(0, -15);
                line = new StringBuilder();
                currentOffset = 0;
            }
            line.append(word).append(" ");
            currentOffset += wordWidth;
        }

        if (!line.toString().isEmpty()) {
            contentStream.showText(line.toString());
        }
    }
    private void openPDF(File file) throws IOException {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(file);
        }
    }

    //
//    @FXML
//    private void handlePayment(ActionEvent event) {
//        try {
//            if (this.event == null) {
//                showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun événement sélectionné !");
//                return;
//            }
//            if (this.event.getEtat() == EtatEvenement.valueOf("EXPIRE")){
//                System.out.println("Erreur : L'etat de evenement est expire.");
//                Alert alert = new Alert(Alert.AlertType.ERROR);
//                alert.setTitle("Échec de la réservation");
//                alert.setHeaderText(null);
//                alert.setContentText("Erreur : L'etat de evenement est expire.");
//                alert.showAndWait();
//                return;
//
//            }
//
//            // Vérifier les champs
//            if (!validatePaymentFields()) {
//                return;
//            }
//
//            // 🔥 Générer un vrai token Stripe
//            String dynamicToken = createStripeToken();
//            if (dynamicToken == null || dynamicToken.isEmpty()) {
//                showAlert(Alert.AlertType.ERROR, "Erreur", "Token de paiement invalide !");
//                return;
//            }
//
//            long amount = (long) (this.event.getPrix() * 100); // Prix en centimes
//
//            Charge charge = Charge.create(ChargeCreateParams.builder()
//                    .setAmount(amount)
//                    .setCurrency("usd")
//                    .setSource(dynamicToken) // 🔥 Utilisation du vrai token reçu
//                    .setDescription("Paiement pour l'événement: " + this.event.getTitre())
//                    .build());
//
//            if (charge.getPaid()) {
//                new ParticipantEvenementService().updatePaymentStatus(
//                        Session.getInstance().getCurrentUser().getId(),
//                        this.event.getId()
//                );
//                sendPaymentConfirmationEmail(
//                        Session.getInstance().getCurrentUser().getEmail(),
//                        this.event
//                );
//
//                showAlert(Alert.AlertType.INFORMATION, "Succès", "Paiement effectué avec succès !");
//            } else {
//                showAlert(Alert.AlertType.ERROR, "Erreur", "Le paiement a échoué.");
//            }
//
//        } catch (StripeException e) {
//            showAlert(Alert.AlertType.ERROR, "Erreur", "Problème de paiement : " + e.getMessage());
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
    private String createStripeToken() throws StripeException {
        Stripe.apiKey = "sk_test_51QwWQy5NfsiWXvvbzS7EsLjI4Z2CY93sXua9vFXB9WjSAhwimEEQEtXI6Ks3jY6EiOwRAdb7ZrYgPXhpZinTDYz800VyNMFBt4"; // Remplace par ta clé secrète Stripe

        // Création des données de la carte
        Map<String, Object> cardParams = new HashMap<>();
        cardParams.put("number", ncarte.getText());
        cardParams.put("exp_month", Integer.parseInt(mm.getText()));
        cardParams.put("exp_year", Integer.parseInt("20" + yy.getText())); // Ajoute "20" devant l'année
        cardParams.put("cvc", cvc.getText());

        // Création du token
        Map<String, Object> tokenParams = new HashMap<>();
        tokenParams.put("card", cardParams);
        if (isTestingMode()) {
            return "tok_visa"; // Token de test Stripe prédéfini
        }
        Token token = Token.create(tokenParams);
        return token.getId(); // Retourne le token généré
    }
    private boolean isTestingMode() {
        return true;
    }



    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

    }

    private boolean validatePaymentFields() {
        if (this.event.getEtat() == EtatEvenement.valueOf("EXPIRE")){
            System.out.println("Erreur : L'etat de evenement est expire.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Échec de la réservation");
            alert.setHeaderText(null);
            alert.setContentText("Erreur : L'etat de evenement est expire.");
            alert.showAndWait();
            return false;

        }
        if (!ncarte.getText().matches("^(4242\\d{12}|tok_.*)$")) { // Accepte 4242... ou un token
            showAlert(Alert.AlertType.ERROR, "Erreur", "numéro de carte bancaire invalide");
            return false;
        }
        if (ncarte.getText().isEmpty() || ncarte.getText().length() != 16) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Numéro de carte invalide !");
            return false;
        }
        if (mm.getText().isEmpty() || yy.getText().isEmpty() || Integer.parseInt(mm.getText()) > 12 || Integer.parseInt(yy.getText()) < 24) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Date d'expiration invalide !");
            return false;
        }
        if (cvc.getText().isEmpty() || cvc.getText().length() != 3) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "CVC invalide !");
            return false;
        }
        if (nomT.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Nom du titulaire invalide !");
            return false;
        }
        return true;
    }
    @FXML
    void btnAddToGoogleCalendar(ActionEvent actionEvent) {
        if (this.event == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun événement sélectionné !");
            return;
        }
        if (this.event.getEtat() == EtatEvenement.valueOf("EXPIRE")){
            System.out.println("Erreur : L'etat de evenement est expire.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Échec de la réservation");
            alert.setHeaderText(null);
            alert.setContentText("Erreur : L'etat de evenement est expire.");
            alert.showAndWait();
            return;

        }
        // Gather event details
        String title = this.event.getTitre();
        String location = this.event.getLieu();
        String description = this.event.getDescription();
        LocalDate startDate = this.event.getDateDebut(); // Assuming this returns LocalDate
         // Assuming this returns LocalTime
        LocalDate endDate = this.event.getDateFin(); // Assuming this returns LocalDate
         // Assuming this returns LocalTime

        try {
            addToGoogleCalendar(title, location, description, startDate, endDate);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Événement ajouté à Google Calendar !");
        } catch (IOException | GeneralSecurityException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout à Google Calendar: " + e.getMessage());
        }
    }
    public static void addToGoogleCalendar(String title, String location, String description,
                                           LocalDate startDate,
                                           LocalDate endDate) throws IOException, GeneralSecurityException {
        // Create an instance of Google Calendar API
        HttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = getCredentials(HTTP_TRANSPORT);

        // Create a Google Calendar service
        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();



        // Create an event
        Event event = new Event()
                .setSummary(title)
                .setLocation(location)
                .setDescription(description);





        // Add reminders (email and popup)
        EventReminder[] reminderOverrides = new EventReminder[]{
                new EventReminder().setMethod("email").setMinutes(24 * 60),  // 24 hours before
                new EventReminder().setMethod("popup").setMinutes(10),      // 10 minutes before
        };

        // Add the event to Google Calendar
        event = service.events().insert("primary", event).execute();
        System.out.println("Event added: " + event.getHtmlLink());
    }
}


