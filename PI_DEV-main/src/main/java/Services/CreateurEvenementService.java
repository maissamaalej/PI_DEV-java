package Services;

import Models.CreateurEvenement;
import Models.User;
import Models.UserData;
import Utils.MyDb;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CreateurEvenementService {

    private Connection conn;
    private final UserService userService;

    public CreateurEvenementService() throws SQLException {
        this.conn = MyDb.getInstance().getConn();
        this.userService = new UserService();
    }
    public CreateurEvenement mapUserDataToCreateur(UserData userData) {
        CreateurEvenement createurEvenement = new CreateurEvenement();

        createurEvenement.setNom(userData.getNom());
        createurEvenement.setPrenom(userData.getPrenom());
        createurEvenement.setEmail(userData.getEmail());
        createurEvenement.setMDP(userData.getMDP());
        createurEvenement.setDiscr(userData.getDiscr());
        createurEvenement.setNom_organisation(userData.getNomOrganisation());
        createurEvenement.setDescription(userData.getDescriptionCreateur());
        createurEvenement.setAdresse(userData.getAdresseCreateur());
        createurEvenement.setTelephone(userData.getTelephoneCreateur());
        return createurEvenement;
    }
    public boolean createCreateurEvenement(UserData userData) {
        try {
            CreateurEvenement createurEvenement = mapUserDataToCreateur(userData);
            // Étape 1 : Insérer l'utilisateur et récupérer son ID
            int userId = userService.createAndReturnId(createurEvenement);
            if (userId == -1) return false; // Erreur lors de l'insertion de l'utilisateur

            // Étape 2 : Insérer le createur d'evenement avec l'ID récupéré
            String sql = "INSERT INTO createurevenement (id, nom_organisation, description, adresse, telephone, certificat_valide) VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, userId);
                pst.setString(2, createurEvenement.getNom_organisation());
                pst.setString(3, createurEvenement.getDescription());
                pst.setString(4, createurEvenement.getAdresse());
                pst.setString(5, createurEvenement.getTelephone());
                pst.setBoolean(6, createurEvenement.getCertificat_valide() == 1);


                int res = pst.executeUpdate();
                if (res > 0) {
                    System.out.println(" ✅ Ajout du createurEvenement avec succès !");
                    return true;
                } else {
                    System.out.println(" ❌ Échec de l'ajout du createurEvenement.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean deleteCreateurEvenement(int id) {
        try {
            // Étape 1 : Supprimer d'abord le createurEvenement
            String sqlCreateurEvenement = "DELETE FROM createurevenement WHERE id = ?";
            try (PreparedStatement pst = conn.prepareStatement(sqlCreateurEvenement)) {
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
    public List<CreateurEvenement> getAll() {
        List<CreateurEvenement> createurEvenements = new ArrayList<>();
        String sql = "SELECT u.id, u.nom, u.prenom, u.image, u.email, u.MDP,u.discr, " +
                "c.nom_organisation, c.description, c.adresse, c.telephone, c.certificat_valide " +
                "FROM user u " +
                "JOIN createurevenement c ON u.id = c.id"; // Jointure entre les tables user et createur d'evenement

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                // Récupérer l'image comme une chaîne de caractères (chemin de fichier)
                String imagePath = rs.getString("image");  // Chemin du fichier image

                // Création d'un createur d'evenement avec les données de la base
                CreateurEvenement createur = new CreateurEvenement(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        imagePath,  // Le chemin de l'image
                        rs.getString("email"),
                        rs.getString("MDP"),
                        rs.getString("discr"),
                        rs.getString("nom_organisation"),
                        rs.getString("description"),
                        rs.getString("adresse"),
                        rs.getString("telephone"),
                        rs.getByte("certificat_valide")
                );
                createurEvenements.add(createur);  // Ajouter le createur à la liste
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return createurEvenements;
    }
    public CreateurEvenement getCreateurEvenementById(int id) {
        String sql = "SELECT u.id, u.nom, u.prenom, u.image, u.email, u.MDP,u.discr, " +
                "c.nom_organisation, c.description, c.adresse, c.telephone, c.certificat_valide " +
                "FROM user u " +
                "JOIN createurevenement c ON u.id = c.id " +
                "WHERE u.id = ?";
// Jointure pour récupérer les informations de l'utilisateur et du coach

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new CreateurEvenement(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("image"),
                            rs.getString("email"),
                            rs.getString("MDP"),
                            rs.getString("discr"),
                            rs.getString("nom_organisation"),
                            rs.getString("description"),
                            rs.getString("adresse"),
                            rs.getString("telephone"),
                            rs.getByte("certificat_valide")
                    );
                } else {
                    System.out.println(" ❌ Aucun créateur d'événement trouvé avec l'id : " + id);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean updateCreateurEvenement(CreateurEvenement createurEvenement) {
        String sql = "UPDATE createurevenement SET nom_organisation = ?, description = ?, adresse = ?, telephone = ?, certificat_valide = ? WHERE id = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, createurEvenement.getNom_organisation());
            pst.setString(2, createurEvenement.getDescription());
            pst.setString(3, createurEvenement.getAdresse());
            pst.setString(4, createurEvenement.getTelephone());
            pst.setBoolean(5, createurEvenement.getCertificat_valide() == 1);
            pst.setInt(6, createurEvenement.getId());

            pst.executeUpdate();

        int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println(" ✅ Createur d'evénement mis à jour avec succès !");
                return true;
            } else {
                System.out.println(" ❌ Aucune mise à jour pour le Createur d'evénement.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean updateCreateurEvenementWithUser(CreateurEvenement createurEvenement) {
        // Mise à jour de l'utilisateur
        User user = new User(createurEvenement.getId(), createurEvenement.getNom(), createurEvenement.getPrenom(), createurEvenement.getImage(), createurEvenement.getEmail(), createurEvenement.getMDP() , createurEvenement.getDiscr());
        boolean userUpdated = userService.updateUser(user);

        // Si l'utilisateur est mis à jour avec succès, mettre à jour le coach
        if (userUpdated) {
            return updateCreateurEvenement(createurEvenement);
        }
        return false;
    }

    public boolean isCreateurEvenement(int id) {
        String sql = "SELECT COUNT(*) FROM createurevenement WHERE id = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    // If count > 0, the user is a createur evenement
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public int getNombreEVENT() {
        String query = "SELECT COUNT(*) FROM createurevenement WHERE certificat_valide = 1";  // Assure-toi que le nom de la table est correct
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
}
