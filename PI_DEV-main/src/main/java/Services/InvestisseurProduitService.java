package Services;

import Models.InvestisseurProduit;
import Models.User;
import Models.UserData;
import Utils.MyDb;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class InvestisseurProduitService {

    private Connection conn;
    private final UserService userService;

    public InvestisseurProduitService() throws SQLException {
        this.conn = MyDb.getInstance().getConn();
        this.userService = new UserService();
    }

    public InvestisseurProduit mapUserDataToInvestisseur(UserData userData) {
        InvestisseurProduit investisseurProduit = new InvestisseurProduit();

        investisseurProduit.setNom(userData.getNom());
        investisseurProduit.setPrenom(userData.getPrenom());
        investisseurProduit.setEmail(userData.getEmail());
        investisseurProduit.setMDP(userData.getMDP());
        investisseurProduit.setDiscr(userData.getDiscr());
        investisseurProduit.setNom_entreprise(userData.getNomEntreprise());
        investisseurProduit.setDescription(userData.getDescriptionInvestisseur());
        investisseurProduit.setAdresse(userData.getAdresseInvestisseur());
        investisseurProduit.setTelephone(userData.getTelephoneInvestisseur());
        return investisseurProduit;
    }

    public boolean createInvestisseurProduit(UserData userData) {
        try {
            InvestisseurProduit investisseurProduit = mapUserDataToInvestisseur(userData);
            // Étape 1 : Insérer l'utilisateur et récupérer son ID
            int userId = userService.createAndReturnId(investisseurProduit);
            if (userId == -1) return false; // Erreur lors de l'insertion de l'utilisateur

            // Étape 2 : Insérer le createur d'evenement avec l'ID récupéré
            String sql = "INSERT INTO investisseurproduit (id, Nom_entreprise, description, adresse, telephone, certificat_valide) VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, userId);
                pst.setString(2, investisseurProduit.getNom_entreprise());
                pst.setString(3, investisseurProduit.getDescription());
                pst.setString(4, investisseurProduit.getAdresse());
                pst.setString(5, investisseurProduit.getTelephone());
                pst.setBoolean(6, investisseurProduit.getCertificat_valide() == 1);


                int res = pst.executeUpdate();
                if (res > 0) {
                    System.out.println(" ✅ Ajout de l' investisseurProduit avec succès !");
                    return true;
                } else {
                    System.out.println(" ❌ Échec de l'ajout de l' investisseurProduit.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean deleteInvestisseurProduit(int id) {
        try {
            // Étape 1 : Supprimer d'abord l' investisseurProduit
            String sqlInvestisseurProduit = "DELETE FROM investisseurProduit WHERE id = ?";
            try (PreparedStatement pst = conn.prepareStatement(sqlInvestisseurProduit)) {
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
    public List<InvestisseurProduit> getAll() {
        List<InvestisseurProduit> investisseurProduits = new ArrayList<>();
        String sql = "SELECT u.id, u.nom, u.prenom, u.image, u.email, u.MDP,u.discr, " +
                "i.Nom_entreprise, i.description, i.adresse, i.telephone, i.certificat_valide " +
                "FROM user u " +
                "JOIN investisseurproduit i ON u.id = i.id"; // Jointure entre les tables user et investisseurproduit

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                // Récupérer l'image sous forme de chaîne de caractères (chemin de fichier)
                String imagePath = rs.getString("image");  // Chemin du fichier image

                // Création d'un investisseurProduit avec les données de la base
                InvestisseurProduit investisseurProduit = new InvestisseurProduit(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        imagePath,  // Le chemin de l'image
                        rs.getString("email"),
                        rs.getString("MDP"),
                        rs.getString("discr"),
                        rs.getString("nom_entreprise"),
                        rs.getString("description"),
                        rs.getString("adresse"),
                        rs.getString("telephone"),
                        rs.getByte("certificat_valide")
                );
                investisseurProduits.add(investisseurProduit);  // Ajouter l'investisseurProduit à la liste
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return investisseurProduits;
    }
    public InvestisseurProduit getInvestisseurProduitById(int id) {
        String sql = "SELECT u.id, u.nom, u.prenom, u.image, u.email, u.MDP,u.discr, " +
                "i.Nom_entreprise, i.description, i.adresse, i.telephone, i.certificat_valide " +
                "FROM user u " +
                "JOIN investisseurProduit i ON u.id = i.id " +
                "WHERE u.id = ?";
// Jointure pour récupérer les informations de l'utilisateur et de l' investisseurProduit

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new InvestisseurProduit(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("image"),
                            rs.getString("email"),
                            rs.getString("MDP"),
                            rs.getString("discr"),
                            rs.getString("nom_entreprise"),
                            rs.getString("description"),
                            rs.getString("adresse"),
                            rs.getString("telephone"),
                            rs.getByte("certificat_valide")
                    );
                } else {
                    System.out.println("❌ Aucun investisseurProduit trouvé avec l'id : " + id);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }




    public boolean updateInvestisseurProduit(InvestisseurProduit investisseurProduit) {
        String sql = "UPDATE createurevenement SET Nom_entreprise = ?, description = ?, adresse = ?, telephone = ?, certificat_valide = ? WHERE id = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, investisseurProduit.getNom_entreprise());
            pst.setString(2, investisseurProduit.getDescription());
            pst.setString(3, investisseurProduit.getAdresse());
            pst.setString(4, investisseurProduit.getTelephone());
            pst.setBoolean(5, investisseurProduit.getCertificat_valide() == 1);
            pst.setInt(6, investisseurProduit.getId());

            pst.executeUpdate();

            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println(" ✅ investisseurProduit mis à jour avec succès !");
                return true;
            } else {
                System.out.println(" ❌ Aucune mise à jour pour l' investisseurProduit'.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean updateInvestisseurProduitWithUser(InvestisseurProduit investisseurProduit) {
        // Mise à jour de l'utilisateur
        User user = new User(investisseurProduit.getId(), investisseurProduit.getNom(), investisseurProduit.getPrenom(), investisseurProduit.getImage(), investisseurProduit.getEmail(), investisseurProduit.getMDP(), investisseurProduit.getDiscr());
        boolean userUpdated = userService.updateUser(user);

        // Si l'utilisateur est mis à jour avec succès, mettre à jour le coach
        if (userUpdated) {
            return updateInvestisseurProduit(investisseurProduit);
        }
        return false;
    }


    public int getNombreINV() {
        String query = "SELECT COUNT(*) FROM investisseurProduit WHERE certificat_valide = 1";  // Assure-toi que le nom de la table est correct
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
    public boolean isInvestisseurProduit(int id) {
        String sql = "SELECT COUNT(*) FROM investisseurproduit WHERE id = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    // If count > 0, the user is a investisseur de produit
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
