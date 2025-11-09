package Services;

import okhttp3.*;

import java.io.IOException;

public class RecommondationService {
    private static final String API_URL = "https://api-inference.huggingface.co/models/gpt2";
    private final String apiKey;

    public RecommondationService(String apiKey) {
        this.apiKey = apiKey;
    }

    public String generateRecommendation(String prompt) {
        OkHttpClient client = new OkHttpClient();

        // Build JSON request body
        String jsonBody = "{\n" +
                "  \"inputs\": \"" + prompt + "\"\n" +
                "}";

        RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                return parseResponse(responseBody);
            } else {
                return handleApiError(response);
            }
        } catch (IOException e) {
            return "Erreur de connexion au service : " + e.getMessage();
        }
    }

    private String parseResponse(String responseBody) {
        // Here you can parse the response as needed
        if (responseBody != null && !responseBody.isEmpty()) {
            return responseBody;
        } else {
            return "La réponse du service est vide.";
        }
    }

    private String handleApiError(Response response) {
        try {
            String errorBody = response.body() != null ? response.body().string() : "Pas de détails d'erreur.";
            return "Erreur de l'API (Code " + response.code() + ") : " + errorBody;
        } catch (IOException e) {
            return "Erreur lors de la récupération des détails de l'erreur : " + e.getMessage();
        }
    }
}
