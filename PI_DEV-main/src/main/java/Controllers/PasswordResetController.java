package Controllers;
import Services.EmailService;
import Services.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class PasswordResetController {





        @FXML private TextField emailField;
        @FXML private Button sendCodeButton;
        @FXML private TextField codeField;
        @FXML private PasswordField newPasswordField;
        @FXML private Button resetPasswordButton;
        @FXML private Label statusLabel;

        private String generatedCode;

        @FXML
        private void envoyerCodeDeRecuperation() {
            String email = emailField.getText().trim();

            if (email.isEmpty()) {
                afficherMessage("Veuillez entrer un email valide !", true);
                return;
            }

            // Générer un code de récupération
            generatedCode = genererCode();

            // Envoyer l'email
            EmailService.envoyerEmail(email, generatedCode);

            // Afficher les champs pour entrer le code et le mot de passe
            codeField.setVisible(true);
            newPasswordField.setVisible(true);
            resetPasswordButton.setVisible(true);

            afficherMessage("Un email avec un code de récupération a été envoyé !",false);
        }

    @FXML
    private void resetPassword() {
        String enteredCode = codeField.getText().trim();
        String newPassword = newPasswordField.getText().trim();
        String email = emailField.getText().trim();  // Récupère l'email du champ


        if (enteredCode.isEmpty() || newPassword.isEmpty()) {
            afficherMessage("Veuillez remplir tous les champs !", true);
            return;
        }

        if (!enteredCode.equals(generatedCode)) {
            afficherMessage("Code incorrect. Vérifiez votre email !", true);
            return;
        }

        // Appel de la méthode de mise à jour du mot de passe
        boolean isPasswordUpdated = UserService.updatePassword(email, newPassword);

        if (isPasswordUpdated) {
            afficherMessage("Mot de passe réinitialisé avec succès !", false);
            Stage stage = (Stage) codeField.getScene().getWindow();
            stage.close();
        } else {
            afficherMessage("Une erreur est survenue. Veuillez réessayer.", true);
        }
    }


    private void afficherMessage(String message, boolean isError) {
        Alert alert;
        if (isError) {
            // Alerta d'erreur
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
        } else {
            // Alerta de succès
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
        }
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    private String genererCode() {
            return String.format("%06d", (int) (Math.random() * 1000000));
        }
    }

