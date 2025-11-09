
package Services;

import Models.Coach;
import Models.SpecialiteC;
import Models.User;
import Models.UserData;
import Utils.MyDb;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CoachService {


    private Connection conn;
    private final UserService userService;

    public CoachService() throws SQLException {
        this.conn = MyDb.getInstance().getConn();
        this.userService = new UserService();
    }
    public Coach mapUserDataToCoach(UserData userData) {
        Coach coach = new Coach();
        coach.setNom(userData.getNom());
        coach.setPrenom(userData.getPrenom());
        coach.setEmail(userData.getEmail());
        coach.setMDP(userData.getMDP());
        coach.setDiscr(userData.getDiscr());
        coach.setSpecialite(userData.getSpecialite()); // Ajoute la spécialité du coach
        coach.setAnnee_experience(userData.getAnneeExperience());// Ajoute les années d'expérience
        return coach;
    }

    public boolean createCoach(UserData userData) {
        try {
            Coach coach = mapUserDataToCoach(userData);
            // Étape 1 : Insérer l'utilisateur et récupérer son ID
            int userId = userService.createAndReturnId(coach);
            if (userId == -1) return false; // Erreur lors de l'insertion de l'utilisateur

            // Étape 2 : Insérer le coach avec l'ID récupéré
            String sql = "INSERT INTO coach (id, annee_experience, certificat_valide, specialite, note ) VALUES (?, ?, ?, ?, ? )";

            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, userId);
                pst.setInt(2, coach.getAnnee_experience());
                pst.setBoolean(3, coach.getCertificat_valide() == 1);
                pst.setString(4, coach.getSpecialite().toString());
                pst.setInt(5, coach.getNote());


                int res = pst.executeUpdate();
                if (res > 0) {
                    System.out.println(" ✅ Ajout du coach avec succès !");
                    return true;
                } else {
                    System.out.println(" ❌ Échec de l'ajout du coach.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean deleteCoach(int id) {
        try {
            // Étape 1 : Supprimer d'abord le coach
            String sqlCoach = "DELETE FROM coach WHERE id = ?";
            try (PreparedStatement pst = conn.prepareStatement(sqlCoach)) {
                pst.setInt(1, id);
                pst.executeUpdate();
            }

            // Étape 2 : Appeler deleteUser de UserService
            return userService.deleteUser(id);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public List<Coach> getAll() {
        List<Coach> coaches = new ArrayList<>();
        String sql = "SELECT u.id, u.nom, u.prenom, u.image, u.email, u.MDP, u.discr," +
                "c.annee_experience, c.certificat_valide, c.specialite, c.note  " +
                "FROM user u " +
                "JOIN coach c ON u.id = c.id"; // Jointure entre les tables user et coach

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                // Récupérer l'image comme une chaîne de caractères (chemin de fichier)
                String imagePath = rs.getString("image");  // Chemin du fichier image

                // Création d'un coach avec les données de la base
                Coach coach = new Coach(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        imagePath,  // Le chemin de l'image
                        rs.getString("email"),
                        rs.getString("MDP"),
                        rs.getString("discr"),
                        rs.getByte("certificat_valide"),
                        SpecialiteC.valueOf(rs.getString("specialite")), // Assurez-vous que la spécialité est bien stockée en tant que String dans la base
                        rs.getInt("note"),
                        rs.getInt("annee_experience")

                );
                coaches.add(coach);  // Ajouter le coach à la liste
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return coaches;
    }

    public Coach getCoachById(int id) {
        String sql = "SELECT u.id, u.nom, u.prenom, u.image, u.email, u.MDP,u.discr, " +
                "c.annee_experience, c.certificat_valide, c.specialite, c.note " +
                "FROM user u " +
                "JOIN coach c ON u.id = c.id " +
                "WHERE u.id = ?"; // Jointure pour récupérer les informations de l'utilisateur et du coach

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Coach(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("image"),
                            rs.getString("email"),
                            rs.getString("MDP"),
                            rs.getString("discr"),
                            rs.getByte("certificat_valide"),
                            SpecialiteC.valueOf(rs.getString("specialite")), // Assurez-vous que la spécialité est bien un enum
                            rs.getInt("note"),
                            rs.getInt("annee_experience")
                    );
                } else {
                    System.out.println(" ❌ Aucun coach trouvé avec l'id : " + id);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }





public boolean updateCoach(Coach coach) {
        String sql = "UPDATE coach SET annee_experience = ?, certificat_valide = ?, specialite = ?, note = ?   WHERE id = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, coach.getAnnee_experience());
            pst.setByte(2, coach.getCertificat_valide());
            pst.setString(3, coach.getSpecialite().name()); // Assurez-vous que `specialite` est bien un enum ou une string
            pst.setInt(4, coach.getNote());
            pst.setInt(5, coach.getId());

            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println(" ✅ Coach mis à jour avec succès !");
                return true;
            } else {
                System.out.println(" ❌ Aucune mise à jour pour le coach.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean updateCoachWithUser(Coach coach) {
        // Mise à jour de l'utilisateur
        User user = new User(coach.getId(), coach.getNom(), coach.getPrenom(), coach.getImage(), coach.getEmail(), coach.getMDP() , coach.getDiscr());
        boolean userUpdated = userService.updateUser(user);

        // Si l'utilisateur est mis à jour avec succès, mettre à jour le coach
        if (userUpdated) {
            return updateCoach(coach);
        }
        return false;
    }


    public int getNombreCoaches() {
        String query = "SELECT COUNT(*) FROM coach WHERE certificat_valide = 1";  // Assure-toi que le nom de la table est correct
        try (PreparedStatement statement = conn.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;  // Retourne 0 en cas d'erreur
    }
    public boolean isCoach(int id) {
        String sql = "SELECT COUNT(*) FROM coach WHERE id = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    // If count > 0, the user is a coach
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Coach> getAllValide() {
        List<Coach> coaches = new ArrayList<>();
        String sql = "SELECT u.id, u.nom, u.prenom, u.image, u.email, u.MDP,u.discr, " +
                "c.annee_experience, c.certificat_valide, c.specialite, c.note " +
                "FROM user u " +
                "JOIN coach c ON u.id = c.id " + // Jointure entre les tables user et coach
                "WHERE c.certificat_valide = 1"; // Condition pour filtrer les coachs avec un certificat valide

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                // Création d'un coach avec les données de la base
                Coach coach = new Coach(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("image"),
                        rs.getString("email"),
                        rs.getString("MDP"),
                        rs.getString("discr"),
                        rs.getByte("certificat_valide"),
                        SpecialiteC.valueOf(rs.getString("specialite")), // Assurez-vous que la spécialité est bien stockée en tant que String dans la base
                        rs.getInt("note"),
                        rs.getInt("annee_experience")
                );
                coaches.add(coach);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return coaches;
    }
}
