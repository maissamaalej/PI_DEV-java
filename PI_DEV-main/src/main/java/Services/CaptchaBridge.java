package Services;
import netscape.javascript.JSObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class CaptchaBridge {
    private boolean isHuman = false;  // Flag to track verification status

    public void onCaptchaSuccess(String token) {
        System.out.println("Received reCAPTCHA token: " + token);

        // Validate the token with Google's API
        isHuman = validateCaptcha(token);
    }

    public boolean isHuman() {
        return isHuman;  // Used in login method
    }

    private boolean validateCaptcha(String token) {
        String secretKey = "6LcIu-QqAAAAAJ9E-6LRr9g4o8XPMxok5bZT-2mD";
        String url = "https://www.google.com/recaptcha/api/siteverify";

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            String postParams = "secret=" + secretKey + "&response=" + token;
            OutputStream os = conn.getOutputStream();
            os.write(postParams.getBytes());
            os.flush();
            os.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            String jsonResponse = response.toString();
            System.out.println("reCAPTCHA Validation Response: " + jsonResponse);

            return jsonResponse.contains("\"success\": true");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
