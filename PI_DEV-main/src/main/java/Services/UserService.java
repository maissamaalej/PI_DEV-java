
package Services;

import Models.*;
import Utils.MyDb;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    Connection conn;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService() throws SQLException {
        this.conn = MyDb.getInstance().getConn();
        this.passwordEncoder = new BCryptPasswordEncoder(13);
    }



    public static boolean updatePassword(String email, String newPassword) {
        String sql = "UPDATE user SET MDP = ? WHERE email = ?";

        try (Connection conn = MyDb.getInstance().getConn();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            // Vérification de la validité des entrées
            if (email == null || email.isEmpty() || newPassword == null || newPassword.isEmpty()) {
                afficherMessage("Veuillez remplir tous les champs.");
                return false;
            }

            // Paramétrer la requête
            String hashedPassword = new BCryptPasswordEncoder(13).encode(newPassword);
            statement.setString(1, hashedPassword);
            statement.setString(2, email);

            // Exécution de la mise à jour
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                afficherMessage("Mot de passe mis à jour avec succès.");
                return true;
            } else {
                afficherMessage("Aucun utilisateur trouvé avec cet email.");
                return false;
            }

        } catch (SQLException e) {
            // Imprimer l'exception dans la console pour plus de détails
            e.printStackTrace();

            // Afficher un message d'erreur à l'utilisateur avec les détails de l'exception
            afficherMessage("Une erreur est survenue : " + e.getMessage());

            // Retourner false pour indiquer l'échec de la mise à jour
            return false;
        }
    }

    private  static void afficherMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public int createAndReturnId(User obj) {
        String defaultImage = "/img/OIP.jpeg"; // Chemin de l'image par défaut
        // Vérifier si l'image est nulle ou vide, et utiliser l'image par défaut si c'est le cas
        String imageToInsert = (obj.getImage() == null || obj.getImage().isEmpty()) ? defaultImage : obj.getImage();

        String sql = "INSERT INTO user (nom, prenom, image, email, MDP, discr) VALUES (?, ?, ?, ?, ?,?)";
        try (PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, obj.getNom());
            pst.setString(2, obj.getPrenom());
            pst.setString(3, imageToInsert); // Utiliser l'image par défaut ou celle fournie par l'utilisateur
            pst.setString(4, obj.getEmail());
            pst.setString(5, obj.getMDP());
            pst.setString(6, obj.getDiscr());

            int res = pst.executeUpdate();
            if (res > 0) {
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1); // Récupérer l'ID généré
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Indique une erreur
    }


    // Méthode pour charger l'image de l'utilisateur depuis la base de données
    private Image getImageFromDatabase(int userId) {
        String sql = "SELECT image FROM user WHERE id = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/MyDb", "username", "password");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Récupérer le BLOB de l'image depuis la base de données
                byte[] imageBytes = rs.getBytes("image");

                if (imageBytes != null) {
                    // Convertir le tableau de bytes en Image
                    return new Image(new ByteArrayInputStream(imageBytes));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Retourner une image par défaut si aucune image n'est trouvée
        return new Image("file:resources/images/default_user.png");
    }

//
//    public boolean updateUser(User user) {
//        String sql = "UPDATE user SET nom = ?, prenom = ?, image = ?, email = ?, MDP = ? WHERE id = ?";
//
//        try (PreparedStatement pst = conn.prepareStatement(sql)) {
//            pst.setString(1, user.getNom());
//            pst.setString(2, user.getPrenom());
//            pst.setString(3, user.getImage());
//            pst.setString(4, user.getEmail());
//            pst.setString(5, user.getMDP());
//            pst.setInt(6, user.getId());
//
//            int rowsUpdated = pst.executeUpdate();
//            if (rowsUpdated > 0) {
//                System.out.println(" ✅ Utilisateur mis à jour avec succès !");
//                return true;
//            } else {
//                System.out.println("Aucune mise à jour pour l'utilisateur.");
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }


    public boolean updateUser(User user) {
        String sql = "UPDATE user SET nom = ?, prenom = ?, image = ?, email = ?, MDP = ? , discr=? WHERE id = ?";
        try (Connection conn = MyDb.getInstance().getConn(); // Get a new connection each time
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, user.getNom());
            pst.setString(2, user.getPrenom());
            pst.setString(3, user.getImage());
            pst.setString(4, user.getEmail());
            pst.setString(5, user.getMDP());
            pst.setString(6, user.getDiscr());
            pst.setInt(7, user.getId());

            int rowsUpdated = pst.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateAdherentWithUser(Adherent adherent) {
        boolean userUpdated = updateUser(adherent);  // Mettre à jour l'utilisateur d'abord
        if (!userUpdated) {
            return false;
        }

        // Mettre à jour les informations spécifiques à Adherent
        String sql = "UPDATE adherent SET age = ?, poids = ?, taille = ?, genre = ?, objectif_personnel = ?, niveau_activite = ? WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, adherent.getAge());
            pst.setFloat(2, adherent.getPoids());
            pst.setFloat(3, adherent.getTaille());
            pst.setString(4, adherent.getGenre().name());  // Assurez-vous que `genre` est un enum ou une string
            pst.setString(5, adherent.getObjectif_personnelle().name()); // idem
            pst.setString(6, adherent.getNiveau_activites().name());
            pst.setInt(7, adherent.getId());

            int rowsUpdated = pst.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
//    public boolean updateCoachWithUser(Coach coach) {
//        boolean userUpdated = updateUser(coach);  // Met à jour l'utilisateur d'abord
//        if (!userUpdated) {
//            return false;
//        }
//
//        // Mettre à jour les informations spécifiques à Coach
//        String sql = "UPDATE coach SET annee_experience = ?, certificat_valide = ?, specialite = ?, note = ? WHERE id = ?";
//        try (PreparedStatement pst = conn.prepareStatement(sql)) {
//            pst.setInt(1, coach.getAnnee_experience());
//            pst.setByte(2, coach.getCertificat_valide());
//            pst.setString(3, coach.getSpecialite().name());  // Assurez-vous que `specialite` est bien un enum ou une string
//            pst.setInt(4, coach.getNote());
//            pst.setInt(5, coach.getId());
//
//            int rowsUpdated = pst.executeUpdate();
//            return rowsUpdated > 0;
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }


    public boolean updateCoachWithUser(Coach coach) {
        boolean userUpdated = updateUser(coach);  // Update the user first
        if (!userUpdated) {
            return false;
        }

        String sql = "UPDATE coach SET annee_experience = ?, certificat_valide = ?, specialite = ?, note = ? WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, coach.getAnnee_experience());
            pst.setByte(2, coach.getCertificat_valide());
            pst.setString(3, coach.getSpecialite() != null ? coach.getSpecialite().name() : null);            pst.setInt(4, coach.getNote());
            pst.setInt(5, coach.getId());

            int rowsUpdated = pst.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean updateInvestisseurWithUser(InvestisseurProduit investisseur) {
        boolean userUpdated = updateUser(investisseur);  // Met à jour l'utilisateur d'abord
        if (!userUpdated) {
            return false;
        }

        // Mettre à jour les informations spécifiques à InvestisseurProduit
        String sql = "UPDATE investisseur_produit SET nom_entreprise = ?, description = ?, adresse = ?, telephone = ? WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, investisseur.getNom_entreprise());
            pst.setString(2, investisseur.getDescription());
            pst.setString(3, investisseur.getAdresse());
            pst.setString(4, investisseur.getTelephone());
            pst.setInt(5, investisseur.getId());

            int rowsUpdated = pst.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean updateCreateurEvenementWithUser(CreateurEvenement createur) {
        boolean userUpdated = updateUser(createur);  // Met à jour l'utilisateur d'abord
        if (!userUpdated) {
            return false;
        }

        // Mettre à jour les informations spécifiques à CreateurEvenement
        String sql = "UPDATE createur_evenement SET nom_organisation = ?, description = ?, adresse = ?, telephone = ? WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, createur.getNom_organisation());
            pst.setString(2, createur.getDescription());
            pst.setString(3, createur.getAdresse());
            pst.setString(4, createur.getTelephone());
            pst.setInt(5, createur.getId());

            int rowsUpdated = pst.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



    public boolean deleteUser(int id) {
        String sql = "DELETE FROM user WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            int res = pst.executeUpdate();
            if (res > 0) {
                System.out.println("✅ Utilisateur supprimé avec succès !");
                return true;
            } else {
                System.out.println("❌ Aucune suppression d'utilisateur effectuée.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("image"),
                        rs.getString("email"),
                        rs.getString("MDP"),
                        rs.getString("discr")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public User getUserById(int id) {
        String sql = "SELECT * FROM user WHERE id = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("image"),
                            rs.getString("email"),
                            rs.getString("MDP"),
                            rs.getString("discr")
                    );
                } else {
                    System.out.println("Aucun utilisateur trouvé avec l'id : " + id);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean login(String email, String password) {
        String sql = "SELECT MDP FROM user WHERE email = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("MDP");
                if (hashedPassword == null || !hashedPassword.startsWith("$2") || hashedPassword.length() < 59 || hashedPassword.length() > 61) {
                    System.out.println("Erreur : Le mot de passe stocké pour l'email " + email + " n'est pas un hachage bcrypt valide. Hachage : " + hashedPassword);
                    return false;
                }
                try {
                    return passwordEncoder.matches(password, hashedPassword); // Vérifier le mot de passe
                } catch (IllegalArgumentException e) {
                    System.out.println("Erreur lors de la vérification du mot de passe pour l'email " + email + " : " + e.getMessage());
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }





public String getUserType(int userId) {
        try {
            // Vérifier d'abord si l'utilisateur est un Adherent
            String queryAdherent = "SELECT id FROM adherent WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(queryAdherent)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return "adherent";
                }
            }

            // Vérifier si c'est un Coach
            String queryCoach = "SELECT id FROM coach WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(queryCoach)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return "coach";
                }
            }

            // Vérifier si c'est un InvestisseurProduit
            String queryInvestisseur = "SELECT id FROM investisseurproduit WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(queryInvestisseur)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return "investisseur";
                }
            }

            // Vérifier si c'est un CreateurEvenement
            String queryCreateur = "SELECT id FROM createurevenement WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(queryCreateur)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return "createur";
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "unknown"; // Si l'ID ne correspond à aucune catégorie
    }
//    public User getUserByEmailAndPassword(String email, String password) {
//        String query = "SELECT * FROM user WHERE email = ? AND MDP = ?";
//        try (PreparedStatement stmt = conn.prepareStatement(query)) {
//            stmt.setString(1, email);
//            stmt.setString(2, password);
//            ResultSet rs = stmt.executeQuery();
//
//            if (rs.next()) {
//                int userId = rs.getInt("id");
//                String userType = getUserType(userId);
//
//                switch (userType) {
//                    case "adherent":
//                        return getAdherentById(userId);
//                    case "coach":
//                        return getCoachById(userId);
//                    case "investisseur":
//                        return getInvestisseurProduitById(userId);
//                    case "createur":
//                        return getCreateurEvenementById(userId);
//                    default:
//                        return new User(userId, rs.getString("nom"), rs.getString("prenom"),
//                                rs.getString("email"), rs.getString("MDP") , rs.getString("discr"));
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
public User getUserByEmailAndPassword(String email, String password) {
    String query = "SELECT * FROM user WHERE email = ?";
    try (PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            String hashedPassword = rs.getString("MDP");
            if (passwordEncoder.matches(password, hashedPassword)) {
                int userId = rs.getInt("id");
                String userType = getUserType(userId);

                switch (userType) {
                    case "adherent":
                        return getAdherentById(userId);
                    case "coach":
                        return getCoachById(userId);
                    case "investisseur":
                        return getInvestisseurProduitById(userId);
                    case "createur":
                        return getCreateurEvenementById(userId);
                    default:
                        return new User(
                                rs.getInt("id"),
                                rs.getString("nom"),
                                rs.getString("prenom"),
                                rs.getString("image"),
                                rs.getString("email"),
                                rs.getString("MDP"),
                                rs.getString("discr")
                        );
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
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
    public InvestisseurProduit getInvestisseurProduitById(int id) {
        String sql = "SELECT u.id, u.nom, u.prenom, u.image, u.email, u.MDP,u.discr, " +
                "i.nom_entreprise, i.description, i.adresse, i.telephone, i.certificat_valide " +
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
    public Adherent getAdherentById(int id) {
        String sql = "SELECT u.id, u.nom, u.prenom, u.image, u.email, u.MDP,u.discr, " +
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




}
