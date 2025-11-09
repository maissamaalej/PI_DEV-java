package Controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class LiveStreamController {

    @FXML private Button btnStartLive;
    @FXML private Button btnStopLive;
    @FXML private WebView webView;

    private Process ffmpegProcess;
    private static String RTMPS_URL;
    private static String STREAM_KEY;
    private static String PLAYER_URL;
    // Pour le polling
    private static String LIVE_STREAM_ID;
    private static String TOKEN;

    private static final String FFMPEG_PATH = "C:\\ffmpeg\\bin\\ffmpeg.exe";
    private static final String WEBCAM_NAME = "USB2.0 HD UVC WebCam";
    // Remplacez par le nom exact tel qu'affiché par "ffmpeg -list_devices ..."
    private static final String AUDIO_DEVICE = "Réseau de microphones (Realtek(R) Audio)";
    private static final String API_KEY = "O3Iomt3ax0ry4pDBzlAFuPuzPrKE1oo9sx8ajcRLzi9";
    private static final String AUTH_URL = "https://sandbox.api.video/auth/api-key";
    private static final String LIVE_STREAM_URL = "https://sandbox.api.video/live-streams";

    private static void fetchLiveStreamInfo() {
        try {
            if (API_KEY == null || API_KEY.isEmpty()) {
                System.err.println("❌ Clé API manquante !");
                return;
            }
            String jsonTokenRequest = "{\"apiKey\": \"" + API_KEY + "\"}";
            String tokenResponse = sendPostRequest(AUTH_URL, jsonTokenRequest);
            if (tokenResponse == null) {
                System.err.println("❌ Impossible d'obtenir un token JWT.");
                return;
            }
            JSONObject tokenJson = new JSONObject(tokenResponse);
            if (tokenJson.has("access_token")) {
                TOKEN = tokenJson.getString("access_token");
                System.out.println("Token récupéré : " + TOKEN);
                String jsonLiveStreamRequest = "{\"name\": \"MyLiveStream\"}";
                String liveStreamResponse = sendPostRequestWithAuth(LIVE_STREAM_URL, TOKEN, jsonLiveStreamRequest);
                if (liveStreamResponse != null) {
                    JSONObject liveStreamJson = new JSONObject(liveStreamResponse);
                    LIVE_STREAM_ID = liveStreamJson.optString("id", null);
                    STREAM_KEY = liveStreamJson.optString("streamKey", null);
                    PLAYER_URL = liveStreamJson.getJSONObject("assets").optString("player", null);
                    if (LIVE_STREAM_ID == null && PLAYER_URL != null) {
                        String[] parts = PLAYER_URL.split("/");
                        if (parts.length > 0) {
                            LIVE_STREAM_ID = parts[parts.length - 1];
                            System.out.println("LIVE_STREAM_ID extrait : " + LIVE_STREAM_ID);
                        }
                    }
                    RTMPS_URL = "rtmps://broadcast.api.video:1936/s/" + STREAM_KEY;
                    System.out.println("LIVE_STREAM_ID : " + LIVE_STREAM_ID);
                    System.out.println("Stream Key : " + STREAM_KEY);
                    System.out.println("Player URL : " + PLAYER_URL);
                    System.out.println("RTMPS URL : " + RTMPS_URL);
                } else {
                    System.err.println("❌ Réponse vide lors de la création du live stream.");
                }
            } else {
                System.err.println("❌ Token non trouvé dans la réponse de l'API.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void startLive() {
        try {
            System.out.println("🔴 Démarrage du live...");
            fetchLiveStreamInfo();
            if (RTMPS_URL == null || STREAM_KEY == null) {
                System.err.println("❌ Impossible de démarrer le live : RTMPS_URL ou STREAM_KEY manquant.");
                return;
            }
            if (!isFFmpegAvailable()) {
                System.err.println("❌ FFmpeg non disponible !");
                return;
            }
            if (!isCameraAvailable()) {
                System.err.println("❌ Périphérique vidéo non détecté !");
                return;
            }
            stopLive();

            // Commande FFmpeg avec entrées vidéo et audio séparées
            String[] ffmpegCmd = {
                    FFMPEG_PATH,
                    "-f", "dshow",
                    "-rtbufsize", "256M",
                    "-video_size", "640x480",
                    "-framerate", "30",
                    "-i", "video=" + WEBCAM_NAME,         // Entrée vidéo
                    "-f", "dshow",
                    "-i", "audio=" + AUDIO_DEVICE,          // Entrée audio
                    "-c:v", "libx264",
                    "-preset", "veryfast",
                    "-tune", "zerolatency",
                    "-c:a", "aac",
                    "-b:a", "128k",
                    "-g", "60",
                    "-keyint_min", "60",
                    "-b:v", "2500k",
                    "-f", "flv",
                    RTMPS_URL
            };

            ProcessBuilder builder = new ProcessBuilder(ffmpegCmd);
            builder.redirectErrorStream(true);
            ffmpegProcess = builder.start();

            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(ffmpegProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("FFmpeg : " + line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            btnStartLive.setDisable(true);
            btnStopLive.setDisable(false);

            // Charger waiting.html une seule fois
            Platform.runLater(() -> {
                try {
                    InputStream htmlStream = getClass().getResourceAsStream("/waiting.html");
                    if (htmlStream == null) {
                        System.err.println("❌ Fichier waiting.html introuvable.");
                        return;
                    }
                    BufferedReader br = new BufferedReader(new InputStreamReader(htmlStream, StandardCharsets.UTF_8));
                    StringBuilder htmlContent = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        htmlContent.append(line).append("\n");
                    }
                    br.close();
                    String waitingHtml = htmlContent.toString().replace("{{HLS_URL}}", (PLAYER_URL != null ? PLAYER_URL : ""));
                    webView.getEngine().loadContent(waitingHtml);
                } catch (Exception e) {
                    System.err.println("❌ Erreur lors du chargement de waiting.html : " + e.getMessage());
                }
                // Charger directement le player et démarrer le polling
                Platform.runLater(() -> {
                    webView.getEngine().load(PLAYER_URL);
                    startPollingPlayerStatus();
                });
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode de polling utilisant Timeline (exécuté sur le thread FX)
    // Déclarez ce champ dans votre classe LiveStreamController
    private Timeline pollingTimeline;

    private void startPollingPlayerStatus() {
        // Créez et affectez le Timeline à la variable de classe
        pollingTimeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            try {
                Object result = webView.getEngine().executeScript(
                        "var v = document.querySelector('video'); " +
                                "v != null && !v.paused;"
                );
                boolean isLive = (result instanceof Boolean) ? (Boolean) result : false;
                System.out.println("Player live ? " + isLive);
                if (isLive) {
                    pollingTimeline.stop();
                    System.out.println("Le live est détecté !");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
        pollingTimeline.setCycleCount(Timeline.INDEFINITE);
        pollingTimeline.play();
    }




    private String getLiveStreamStatus() {
        try {
            if (LIVE_STREAM_ID == null || TOKEN == null) {
                return "unknown";
            }
            URL url = new URL(LIVE_STREAM_URL + "/" + LIVE_STREAM_ID);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + TOKEN);
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line.trim());
                }
                reader.close();
                JSONObject jsonResponse = new JSONObject(response.toString());
                return jsonResponse.optString("status", "unknown");
            } else {
                System.err.println("Erreur lors de la récupération du statut, code : " + responseCode);
            }
        } catch (UnknownHostException uhe) {
            System.err.println("Erreur de résolution d'hôte pour " + LIVE_STREAM_URL + " : " + uhe.getMessage());
            return "error";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    @FXML
    private void stopLive() {
        if (ffmpegProcess != null) {
            System.out.println("⏹️ Arrêt du live streaming...");
            ffmpegProcess.destroy();
            ffmpegProcess = null;
        }
        btnStartLive.setDisable(false);
        btnStopLive.setDisable(true);
    }

    private static boolean isFFmpegAvailable() {
        try {
            Process process = new ProcessBuilder(FFMPEG_PATH, "-version").start();
            return process.waitFor() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isCameraAvailable() {
        try {
            Process process = new ProcessBuilder(FFMPEG_PATH, "-list_devices", "true", "-f", "dshow", "-i", "dummy").start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("\"" + WEBCAM_NAME + "\"")) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private static String sendPostRequest(String urlString, String jsonInput) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonInput.getBytes(StandardCharsets.UTF_8));
            }
            return readResponse(conn);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String sendPostRequestWithAuth(String urlString, String token, String jsonInput) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonInput.getBytes(StandardCharsets.UTF_8));
            }
            return readResponse(conn);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String readResponse(HttpURLConnection conn) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line.trim());
        }
        br.close();
        return response.toString();
    }
}
