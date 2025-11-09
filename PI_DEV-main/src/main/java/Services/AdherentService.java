
package Services;

import Models.*;
import Utils.MyDb;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class AdherentService {


    private Connection conn;
    private final UserService userService;

    public AdherentService() throws SQLException {
        this.conn = MyDb.getInstance().getConn();
        this.userService = new UserService();
    }
    public Adherent mapUserDataToAdherent(UserData userData) {
        Adherent adherent = new Adherent();
        adherent.setNom(userData.getNom());
        adherent.setPrenom(userData.getPrenom());
        adherent.setEmail(userData.getEmail());
        adherent.setMDP(userData.getMDP());
        adherent.setDiscr(userData.getDiscr());
        adherent.setPoids(userData.getPoids());
        adherent.setTaille(userData.getTaille());
        adherent.setAge(userData.getAge());
        adherent.setGenre(userData.getGenre());
        adherent.setObjectif_personnelle(userData.getObjectifPersonnel());
        adherent.setNiveau_activites(userData.getNiveauActivite());
        return adherent;
    }

    public boolean createAdherent(UserData userData) {
        try {
          Adherent adherent = mapUserDataToAdherent(userData);
            // Étape 1 : Insérer l'utilisateur et récupérer son ID
            int userId = userService.createAndReturnId(userData);
            if (userId == -1) return false; // Erreur lors de l'insertion de l'utilisateur

            // Étape 2 : Insérer l'adherent avec l'ID récupéré
            String sql = "INSERT INTO adherent (id, poids, taille, age, genre, Objectif_personnelle, niveau_activites) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, userId);
                    pst.setFloat(2, adherent.getPoids());
                    pst.setFloat(3, adherent.getTaille());
                    pst.setInt(4, adherent.getAge());
                    pst.setString(5, adherent.getGenre().toString());
                    pst.setString(6, adherent.getObjectif_personnelle().toString());
                    pst.setString(7, adherent.getNiveau_activites().toString());


                int res = pst.executeUpdate();
                if (res > 0) {
                    System.out.println(" ✅ Ajout de l'adhérent avec succès !");
                    return true;
                } else {
                    System.out.println(" ❌ Échec de l'ajout de l'adhérent.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean deleteAdherent(int id) {
        try {
            // Étape 1 : Supprimer d'abord le coach
            String sqlAdherent = "DELETE FROM adherent WHERE id = ?";
            try (PreparedStatement pst = conn.prepareStatement(sqlAdherent)) {
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
    public List<Adherent> getAll() {
        List<Adherent> adherents = new ArrayList<>();
        String sql = "SELECT u.id, u.nom, u.prenom, u.image, u.email, u.MDP,u.discr, a.Niveau_activites, a.Objectif_personnelle, a.genre, a.age, a.taille, a.poids FROM user u JOIN adherent a ON u.id = a.id";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                // Création d'un adhérent avec les données récupérées de la base
                Adherent adherent = new Adherent(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("image"),  // Le champ image est une chaîne (chemin du fichier)
                        rs.getString("email"),
                        rs.getString("MDP"),
                        rs.getString("discr"),
                        NiveauA.valueOf(rs.getString("niveau_activites")),
                        ObjP.valueOf(rs.getString("objectif_personnelle")),
                        GenreG.valueOf(rs.getString("genre")),
                        rs.getInt("age"),
                        rs.getFloat("taille"),
                        rs.getFloat("poids")
                );

                adherents.add(adherent);  // Ajouter l'adhérent à la liste
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return adherents;
    }


    public boolean updateAdherent(Adherent adherent) {
        String sql = "UPDATE adherent SET age = ?, taille = ?, poids = ?, genre = ?, objectif_personnelle = ?, niveau_activites = ? WHERE id = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, adherent.getAge());
            pst.setFloat(2, adherent.getTaille());
            pst.setFloat(3, adherent.getPoids());
            pst.setString(4, adherent.getGenre().toString());
            pst.setString(5, adherent.getObjectif_personnelle().toString());
            pst.setString(6, adherent.getNiveau_activites().toString());
            pst.setInt(7, adherent.getId());

            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println(" ✅ Adhérent mis à jour avec succès !");
                return true;
            } else {
                System.out.println(" ❌ Aucune mise à jour pour l'adhérent.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean updateAdherentWithUser(Adherent adherent) {
        // Mise à jour de l'utilisateur
        User user = new User(adherent.getId(), adherent.getNom(), adherent.getPrenom(), adherent.getImage(), adherent.getEmail(), adherent.getMDP() , adherent.getDiscr());
        boolean userUpdated = userService.updateUser(user);

        // Si l'utilisateur est mis à jour avec succès, mettre à jour l'adhérent
        if (userUpdated) {
            return updateAdherent(adherent);
        }
        return false;
    }



    public int getNombreAdherents() {
        String query = "SELECT COUNT(*) FROM adherent";  // Assure-toi que le nom de la table est correct
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
    public Adherent getAdherentById(int id) {
        String sql = "SELECT u.id, u.nom, u.prenom, u.image, u.email, u.MDP, u.discr, " +
                "a.Niveau_activites, a.Objectif_personnelle, a.genre, a.age , a.taille , a.poids " +
                "FROM user u " +
                "JOIN adherent a ON u.id = a.id " +
                "WHERE u.id = ?"; // Jointure pour récupérer les informations de l'utilisateur et du coach

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Adherent(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("image"),
                            rs.getString("email"),
                            rs.getString("MDP"),
                            rs.getString("discr"),
                            NiveauA.valueOf(rs.getString("niveau_activites")),
                            ObjP.valueOf(rs.getString("objectif_personnelle")),
                            GenreG.valueOf(rs.getString("genre")),
                            rs.getInt("age"),
                            rs.getFloat("taille"),
                            rs.getFloat("poids")
                    );
                } else {
                    System.out.println(" ❌ Aucun adhérent trouvé avec l'id : " + id);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean isAdherent(int id) {
        String sql = "SELECT COUNT(*) FROM adherent WHERE id = ?";

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

}





