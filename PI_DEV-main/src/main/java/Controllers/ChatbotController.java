package Controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ChatbotController {

    @FXML
    private ListView<String> chatListView;
    @FXML
    private TextField inputField;
    @FXML
    private Button sendButton;

    private ObservableList<String> messages = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        chatListView.setItems(messages);
        sendButton.setOnAction(event -> sendMessage());
        inputField.setOnAction(event -> sendMessage());

        // Personnalisation de l'affichage de chaque message
        chatListView.setCellFactory(listView -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    if (item.startsWith("Vous : ")) {
                        // Message de l'utilisateur
                        setText(item.substring(7)); // Retirer "Vous : " du début
                        setTextFill(Color.ORANGE); // Couleur du texte
                        setFont(Font.font("Arial", 14)); // Police en taille 14
                        setStyle("-fx-font-weight: bold;"); // Texte en gras
                    } else if (item.startsWith("CoachiniBot : ")) {
                        // Message du chatbot
                        setText(item.substring(14)); // Retirer "CoachiniBot : " du début
                        setTextFill(Color.BLACK); // Couleur du texte
                        setFont(Font.font("Arial", 14)); // Police en taille 14
                        setStyle("-fx-font-style: italic;"); // Texte en italique
                    }
                }
            }
        });
    }

    private void sendMessage() {
        String userMessage = inputField.getText().trim();
        if (!userMessage.isEmpty()) {
            messages.add("Vous : " + userMessage);
            String botResponse = getChatbotResponse(userMessage);
            messages.add("CoachiniBot : " + botResponse);
            inputField.clear();
        }
    }



        private String getChatbotResponse(String message) {
        try {
            // Créer un client HTTP
            HttpClient client = HttpClient.newHttpClient();

            // Corps de la requête JSON
            String requestBody = "{\"text\": \"" + message + "\"}";

            // Créer la requête HTTP POST
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://208e-196-235-3-101.ngrok-free.app/chat"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // Envoyer la requête et obtenir la réponse
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Analyser la réponse JSON pour extraire le texte brut
            JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
            System.out.println("Réponse JSON brute : " + jsonResponse);


            // Extraire uniquement le texte de la réponse (sans le champ 'response' ou JSON supplémentaire)
            return jsonResponse.get("response").getAsString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de la communication avec le chatbot."; // Message d'erreur en cas de problème
        }
    }

}
