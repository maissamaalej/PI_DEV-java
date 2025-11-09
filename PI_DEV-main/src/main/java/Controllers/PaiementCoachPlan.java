package Controllers;

import Models.Coach;
import Models.Planning;
import Services.CoachService;
import Services.CreateurEvenementService;
import Services.PaiementPlanningService;
import Services.PlanningService;
import Utils.Session;
import Utils.StripeConfig;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Token;
import com.stripe.param.ChargeCreateParams;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class PaiementCoachPlan {
    @FXML
    private Label labelNometPrenom;
    @FXML
    private ImageView imagecoach;
    @FXML
    private TextField specialite;
    @FXML
    private TextField anneeExp;
    @FXML
    private TextField descriptionplan;
    @FXML
    private HBox hboxNote;
    @FXML
    private TextField Tarif;
    @FXML
    private Button payerBtn;
    @FXML
    private TextField nomT;
    @FXML
    private TextField cvc;
    @FXML
    private TextField yy;
    @FXML
    private TextField mm;
    @FXML
    private TextField ncarte;

    private Coach coach;
    CoachService coachService = new CoachService();
    private final CreateurEvenementService createurEvenementService = new CreateurEvenementService();

    public PaiementCoachPlan() throws SQLException {
    }

    @FXML
    public void initialize() {
        
        StripeConfig.init();
        payerBtn.setOnAction(this::handlePayment);

    }

    public void setCoach(Coach coach) throws SQLException {
        this.coach = coach;
        updateUI();
        PlanningService ps = new PlanningService();
        Planning planning = ps.getPlanningByCoachId(coach.getId());

        if (planning != null) {
            this.planning = planning; // Stocker le planning du coach sélectionné
        } else {
            showAlert(Alert.AlertType.WARNING, "Avertissement", "Ce coach n'a pas de planning disponible.");
        }
    }


    private void updateUI() throws SQLException {
        // Afficher les informations du coach
        labelNometPrenom.setText(coach.getNom() + " " + coach.getPrenom());
        specialite.setText(coach.getSpecialite().toString());
        anneeExp.setText(coach.getAnnee_experience() + " ans");

        // Charger et afficher l'image du coach
        String imagePath = "/img/" + coach.getImage(); // Chemin de l'image dans les ressources
        InputStream imageStream = getClass().getResourceAsStream(imagePath);
        if (imageStream != null) {
            Image image = new Image(imageStream);
            imagecoach.setImage(image);
        } else {
            System.out.println("Image non trouvée : " + imagePath);
            InputStream defaultImageStream = getClass().getResourceAsStream("/img/default-image.jpg");
            if (defaultImageStream != null) {
                imagecoach.setImage(new Image(defaultImageStream));
            } else {
                System.out.println("⚠ Erreur: L'image par défaut est aussi introuvable !");
            }
        }

        // Charger et afficher le planning du coach
        PlanningService ps = new PlanningService();
        Planning planning = ps.getPlanningByCoachId(coach.getId());
        if (planning != null) {
            descriptionplan.setText(planning.getTitre());
            Tarif.setText(planning.getTarif() + " $");  // Ajout de la récupération du Tarif ✅
        } else {
            descriptionplan.setText("Aucun planning disponible.");
            Tarif.setText("Non défini");
        }

        // Afficher les étoiles de notation
        int note = (int) coach.getNote();
        hboxNote.getChildren().clear();
        for (int i = 0; i < 5; i++) {
            ImageView star = new ImageView();
            String starImagePath = (i < note) ? "/img/etoile.png" : "/img/etoilevide.png";
            InputStream starStream = getClass().getResourceAsStream(starImagePath);
            if (starStream != null) {
                star.setImage(new Image(starStream));
            }
            star.setFitWidth(16);
            star.setFitHeight(16);
            hboxNote.getChildren().add(star);
        }
    }
    private Planning planning;
    @FXML
    private void handlePayment(ActionEvent event) {
        try {
            if (this.planning == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun événement sélectionné !");
                return;
            }

            if (!validatePaymentFields()) {
                return;
            }

            String dynamicToken = createStripeToken();
            if (dynamicToken == null || dynamicToken.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Token de paiement invalide !");
                return;
            }

            long amount = (long) (this.planning.getTarif() * 100); // Prix en centimes

            Charge charge = Charge.create(ChargeCreateParams.builder()
                    .setAmount(amount)
                    .setCurrency("usd")
                    .setSource(dynamicToken)
                    .setDescription("Paiement pour planning: " + this.planning.getTitre())
                    .build());

            if (charge.getPaid()) {
                new PaiementPlanningService().updatePaymentStatus(
                        Session.getInstance().getCurrentUser().getId(),
                        this.planning.getIdPlanning()
                );
                sendPaymentConfirmationEmail(Session.getInstance().getCurrentUser().getEmail(), this.planning);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Paiement effectué avec succès !");
                // Vider les champs du formulaire
                clearPaymentFields();
                // Retourner à Home.fxml
                retourauhome(event);
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Le paiement a échoué.");
            }

        } catch (StripeException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Problème de paiement : " + e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendPaymentConfirmationEmail(String recipientEmail, Planning planning) {
        try {
            // Initialisation du client Mailjet avec vos clés API (remplacez par vos vraies valeurs)
            com.mailjet.client.MailjetClient client = new com.mailjet.client.MailjetClient("92b9c99b7e0265f5751812c3bfdab1b3", "7b671e8a35bd8cbe1d6861848ffe1b11");

            // Construction du message
            JSONObject message = new JSONObject();
            message.put(com.mailjet.client.resource.Emailv31.Message.FROM, new JSONObject()
                    .put("Email", "maissa.maalej3@gmail.com")  // Remplacez par votre email validé sur Mailjet
                    .put("Name", "Coachini"));

            message.put(com.mailjet.client.resource.Emailv31.Message.TO, new JSONArray()
                    .put(new JSONObject()
                            .put("Email", recipientEmail)
                            .put("Name", "Client")));

            message.put(com.mailjet.client.resource.Emailv31.Message.SUBJECT, "Confirmation de Paiement - " + planning.getTitre());

            message.put(com.mailjet.client.resource.Emailv31.Message.TEXTPART,
                    "Bonjour " + Session.getInstance().getCurrentUser().getPrenom() + ",\n\n" +
                            "Votre paiement pour le planning " + planning.getTitre() + " a été effectué avec succès.\n\n" +
                            "Tarif: " + planning.getTarif() + " $\n" +
                            "Date: " + new java.util.Date() + "\n\n" +
                            "Merci pour votre confiance !\n" +
                            "L'équipe Coachini");

            message.put(com.mailjet.client.resource.Emailv31.Message.HTMLPART,
                    "<html>" +
                            "<body>" +
                            "<h2>Bonjour <b>" + Session.getInstance().getCurrentUser().getPrenom() + "</b>,</h2>" +
                            "<p>Votre paiement pour le planning <strong>" + planning.getTitre() + "</strong> a été effectué avec succès.</p>" +
                            "<h3>Détails du paiement :</h3>" +
                            "<ul>" +
                            "<li><strong>Planning :</strong> " + planning.getTitre() + "</li>" +
                            "<li><strong>Tarif :</strong> " + planning.getTarif() + " $</li>" +
                            "<li><strong>Date de paiement :</strong> " + new java.util.Date() + "</li>" +
                            "</ul>" +
                            "<p>Merci pour votre confiance !</p>" +
                            "<br/>" +
                            "<h3>L'équipe Coachini</h3>" +
                            "</body>" +
                            "</html>");

            com.mailjet.client.MailjetRequest request = new com.mailjet.client.MailjetRequest(com.mailjet.client.resource.Emailv31.resource)
                    .property(com.mailjet.client.resource.Emailv31.MESSAGES, new JSONArray().put(message));

            com.mailjet.client.MailjetResponse response = client.post(request);
            if (response.getStatus() == 200) {
                System.out.println("Confirmation email sent successfully!");
                showAlert(Alert.AlertType.INFORMATION, "Email envoyé", "Votre email de confirmation a été envoyé avec succès !");
            } else {
                System.out.println("Failed to send confirmation email: " + response.getStatus());
                System.out.println("Response Data: " + response.getData());
                showAlert(Alert.AlertType.ERROR, "Erreur d'envoi d'email", "L'envoi du mail de confirmation a échoué, code: " + response.getStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur d'envoi d'email", "Une erreur est survenue lors de l'envoi du mail de confirmation.");
        }
    }


    private void clearPaymentFields() {
        ncarte.clear();
        mm.clear();
        yy.clear();
        cvc.clear();
        nomT.clear();
    }
    private String createStripeToken() throws StripeException {
        Stripe.apiKey = "sk_test_51QwZ5fH2PLJVXcLsZl7pJSavS7UUEFDq0O5i54JVifuuvgSy7s3Dgus2EJrQgGwU6klpTiZEnl5gC3gESEeOT78p00wQTk1uck"; // Remplace par ta clé secrète Stripe

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
        if (ncarte.getText().isEmpty() || ncarte.getText().length() != 16) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Numéro de carte invalide !");
            return false;
        }
        if (mm.getText().isEmpty() || yy.getText().isEmpty() ||
                Integer.parseInt(mm.getText()) > 12 || Integer.parseInt(yy.getText()) < 24) {
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
    public void retourauhome(ActionEvent event) {
        try {
            // Charger le fichier FXML de la scène Home
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Obtenir la fenêtre actuelle (stage) à partir de l'événement
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Définir la nouvelle scène sur le stage
            stage.setScene(scene);
            stage.setTitle("Accueil"); // Titre de la fenêtre
            stage.show(); // Afficher la nouvelle scène
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la scène Home.fxml : " + e.getMessage());
        }
    }

    public void reserverCoach(ActionEvent event) throws SQLException {
        if (this.coach == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun coach sélectionné !");
            return;
        }

        if (this.planning == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun planning trouvé pour ce coach !");
            return;
        }

        int idAdherent = Session.getInstance().getCurrentUser().getId(); // 🔥 Récupérer l'adhérent connecté
        int idPlanning = this.planning.getIdPlanning(); // 🔥 Récupérer l'ID du planning du coach

        PaiementPlanningService paiementService = new PaiementPlanningService();
        paiementService.reserverPlanning(idAdherent, idPlanning); // ✅ Réserver le bon planning

        showAlert(Alert.AlertType.INFORMATION, "Réservation réussie", "Votre réservation a été enregistrée avec succès !");
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


}
