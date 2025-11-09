package Services;

import Utils.MyDb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminService {

    // Méthode pour valider un utilisateur (investisseur ou créateur d'événements) et envoyer un SMS si validé
    public static void validerUtilisateur(int userId, String role) {
        try (Connection conn = MyDb.getInstance().getConn()) {  // Utilisation de getConn() pour obtenir la connexion
            String sql = "";
            if ("investisseur".equals(role)) {
                sql = "SELECT Telephone, Certificat_valide FROM investisseurproduit WHERE id = ?";
            } else if ("createur".equals(role)) {
                sql = "SELECT Telephone, Certificat_valide FROM createurevenement WHERE id = ?";
            }

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                boolean certificatValide = rs.getBoolean("Certificat_valide");
                String telephone = rs.getString("Telephone");

                if (certificatValide && telephone != null) {
                    // Ajouter le code pays +216 au début du numéro de téléphone si ce n'est pas déjà présent
                    if (!telephone.startsWith("+216")) {
                        telephone = "+216" + telephone;  // Ajout du préfixe si nécessaire
                    }

                    // Envoi du SMS de validation si certificatValide est true
                    String message = "Félicitations ! Vous êtes désormais un membre officiel de Coachini.";
                    SmsSender.envoyerSms(telephone, message);
                    System.out.println("SMS envoyé à " + telephone);
                } else {
                    System.out.println("L'utilisateur n'est pas encore validé ou n'a pas de téléphone.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
