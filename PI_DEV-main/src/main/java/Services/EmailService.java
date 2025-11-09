//package Services;
//
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.util.Base64;
//import java.util.List;
//import java.util.Map;
//import com.google.gson.Gson;
//
//public class EmailService {
//    private static final String API_KEY = "81c81df9c92012c50d0d0e31f4b9fa06";
//    private static final String SECRET_KEY = "bfd8c05d49fb4314f461dd55df93c282";
//    private static final String SENDER_EMAIL = "farah.benyedder@esprit.tn";
//
//    public static void envoyerEmail(String destinataire, String code) {
//        try {
//            String url = "https://api.mailjet.com/v3.1/send";
//            String auth = Base64.getEncoder().encodeToString((API_KEY + ":" + SECRET_KEY).getBytes());
//
//            Map<String, Object> message = Map.of(
//                    "Messages", List.of(Map.of(
//                            "From", Map.of("Email", SENDER_EMAIL, "Name", "Support"),
//                            "To", List.of(Map.of("Email", destinataire)),
//                            "Subject", "Récupération de mot de passe",
//                            "TextPart", "Votre code de récupération : " + code,
//                            "HTMLPart", "<h3>Votre code : <b>" + code + "</b></h3>"
//                    ))
//            );
//
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create(url))
//                    .header("Authorization", "Basic " + auth)
//                    .header("Content-Type", "application/json")
//                    .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(message)))
//                    .build();
//
//            HttpClient client = HttpClient.newHttpClient();
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//            System.out.println("Réponse EmailJet : " + response.body());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
package Services;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class EmailService {
    private static final String API_KEY = "81c81df9c92012c50d0d0e31f4b9fa06";
    private static final String SECRET_KEY = "bfd8c05d49fb4314f461dd55df93c282";
    private static final String SENDER_EMAIL = "farah.benyedder@esprit.tn";
    private static final int TEMPLATE_ID = 6765931; // Remplacez par l'ID de votre modèle

    public static void envoyerEmail(String destinataire, String code) {
        try {
            String url = "https://api.mailjet.com/v3.1/send";
            String auth = Base64.getEncoder().encodeToString((API_KEY + ":" + SECRET_KEY).getBytes());

            // Construction des données pour envoyer l'email avec le modèle et les variables
            Map<String, Object> message = Map.of(
                    "Messages", List.of(Map.of(
                            "From", Map.of("Email", SENDER_EMAIL, "Name", "Support"),
                            "To", List.of(Map.of("Email", destinataire)),
                            "TemplateID", TEMPLATE_ID,  // ID de votre template
                            "TemplateLanguage", true,  // Permet d'activer le mode dynamique
                            "Variables", Map.of(
                                    // Valeur de la variable {{code}}
                                    "EMAIL_TO", destinataire, "CODE", code  // Valeur de la variable {{Email_TO}}
                            )
                    ))
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Basic " + auth)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(message)))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Réponse EmailJet : " + response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
