
//package Services;
//
//import Models.EtatEvenement;
//import Models.Evenement;
//
//import Utils.MyDb;
//
//import java.io.ByteArrayInputStream;
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class EvenementService implements Crud <Evenement> {
//    Connection conn;
//    public EvenementService(){
//        this.conn= MyDb.getInstance().getConn();
//    }
//
//@Override
//public boolean create(Evenement obj) throws Exception {
//    String sql = "INSERT INTO evenement (titre, description, dateDebut, dateFin, lieu, etat, prix, image, type, organisateur, capaciteMaximale) " +
//            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//
//    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//        stmt.setString(1, obj.getTitre());
//        stmt.setString(2, obj.getDescription());
//        stmt.setDate(3, java.sql.Date.valueOf(obj.getDateDebut()));
//        stmt.setDate(4, java.sql.Date.valueOf(obj.getDateFin()));
//        stmt.setString(5, obj.getLieu());
//        stmt.setString(6, obj.getEtat().name());
//        stmt.setDouble(7, obj.getPrix());
//
//        // Check if the image is provided and handle it as a Blob
//        if (obj.getImage() != null && obj.getImage().length > 0) {
//            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(obj.getImage());
//            stmt.setBinaryStream(8, byteArrayInputStream, obj.getImage().length);
//        } else {
//            stmt.setNull(8, Types.BLOB);  // If no image, set it to NULL
//        }
//
//        stmt.setString(9, obj.getType());
//        stmt.setString(10, obj.getOrganisateur());
//        stmt.setInt(11, obj.getCapaciteMaximale());
//
//        int res = stmt.executeUpdate();
//        if (res > 0) {
//            System.out.println("Ajout evenement avec succès !");
//            return true;
//        } else {
//            System.out.println("Aucune ajout de evenement à effectuée");
//        }
//    } catch (SQLException e) {
//        e.printStackTrace();
//        return false;
//    }
//    return false;
//}
//
//
//    @Override
//    public void update(Evenement obj) throws Exception {
//        String sql = "UPDATE evenement SET titre = ?, description = ?, dateDebut = ?, dateFin = ?, lieu = ?, etat = ?, prix = ?, image = ?, type = ?, organisateur = ?, capaciteMaximale = ? WHERE id = ?";
//
//        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setString(1, obj.getTitre());
//            stmt.setString(2, obj.getDescription());
//            stmt.setDate(3, java.sql.Date.valueOf(obj.getDateDebut()));
//            stmt.setDate(4, java.sql.Date.valueOf(obj.getDateFin()));
//            stmt.setString(5, obj.getLieu());
//            stmt.setString(6, obj.getEtat().name());
//            stmt.setDouble(7, obj.getPrix());
//
//            // Handle image: if it's not null, convert it to BinaryStream; if null, set to NULL
//            if (obj.getImage() != null && obj.getImage().length > 0) {
//                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(obj.getImage());
//                stmt.setBinaryStream(8, byteArrayInputStream, obj.getImage().length);
//            } else {
//                stmt.setNull(8, Types.BLOB);  // If no image, set it to NULL
//            }
//
//            stmt.setString(9, obj.getType());
//            stmt.setString(10, obj.getOrganisateur());
//            stmt.setInt(11, obj.getCapaciteMaximale());
//            stmt.setInt(12, obj.getId());
//
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

//    @Override
//    public void delete(int id) throws Exception {
//        String req = "DELETE FROM evenement WHERE `id`=?";
//        try (PreparedStatement pstmt = conn.prepareStatement(req)) {
//
//            pstmt.setInt(1, id);
//            int rowsAffected = pstmt.executeUpdate();
//
//
//            if (rowsAffected > 0) {
//                System.out.println("Suppression evenement effectuée avec succès !");
//            } else {
//                System.out.println("Aucune ligne supprimée evenement. Vérifiez l'ID.");
//            }
//        } catch (SQLException e) {
//            // Handle the exception more gracefully, e.g., log the error or display a user-friendly message
//            e.printStackTrace();
//        }
//    }
//    @Override
//    public List<Evenement> getAll() throws Exception {
//        String sql = "SELECT * FROM evenement";
//        Statement stmt = conn.createStatement();
//        ResultSet rs = stmt.executeQuery(sql);
//        List<Evenement> evenements = new ArrayList<>();
//
//        while (rs.next()) {
//            Evenement obj = new Evenement();
//            obj.setId(rs.getInt("id"));
//            obj.setTitre(rs.getString("titre"));
//            obj.setDescription(rs.getString("description"));
//            obj.setDateDebut(rs.getDate("dateDebut").toLocalDate());
//            obj.setDateFin(rs.getDate("dateFin").toLocalDate());
//            obj.setLieu(rs.getString("lieu"));
//            obj.setEtat(EtatEvenement.valueOf(rs.getString("etat")));
//            obj.setPrix(rs.getDouble("prix"));
//
//            // Retrieve image as byte[] and set
//            Blob blob = rs.getBlob("image");
//            if (blob != null) {
//                obj.setImage(blob.getBytes(1, (int) blob.length()));
//            }
//
//            obj.setType(rs.getString("type"));
//            obj.setOrganisateur(rs.getString("organisateur"));
//            obj.setCapaciteMaximale(rs.getInt("capaciteMaximale"));
//            evenements.add(obj);
//        }
//        return evenements;
//    }

//    //get by id
//    @Override
//    public Evenement getById(int id) throws Exception {
//        String sql = "SELECT * FROM evenement WHERE id = ?";
//        Evenement obj = null;
//
//        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setInt(1, id);
//            try (ResultSet rs = stmt.executeQuery()) {
//                if (rs.next()) {
//                    obj = new Evenement();
//                    obj.setId(rs.getInt("id"));
//                    obj.setTitre(rs.getString("titre"));
//                    obj.setDescription(rs.getString("description"));
//                    obj.setDateDebut(rs.getDate("dateDebut").toLocalDate());
//                    obj.setDateFin(rs.getDate("dateFin").toLocalDate());
//                    obj.setLieu(rs.getString("lieu"));
//                    obj.setEtat(EtatEvenement.valueOf(rs.getString("etat")));
//                    obj.setPrix(rs.getDouble("prix"));
//
//                    // Retrieve image as byte[] and set
//                    Blob blob = rs.getBlob("image");
//                    if (blob != null) {
//                        obj.setImage(blob.getBytes(1, (int) blob.length()));  // Convert Blob to byte[]
//                    }
//
//                    obj.setType(rs.getString("type"));
//                    obj.setOrganisateur(rs.getString("organisateur"));
//                    obj.setCapaciteMaximale(rs.getInt("capaciteMaximale"));
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return obj;
//    }
//
//
//}









package Services;

import Models.EtatEvenement;
import Models.Evenement;
import Utils.MyDb;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EvenementService {
    private static Connection conn = null;

    public EvenementService() throws SQLException {
        this.conn = MyDb.getInstance().getConn();
    }


    public boolean create(Evenement obj) throws Exception {
        String sql = "INSERT INTO evenement (titre, description, dateDebut, dateFin, lieu, etat, prix, image, type, organisateur, capaciteMaximale, idCreateurEvenement) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, obj.getTitre());
            stmt.setString(2, obj.getDescription());
            stmt.setDate(3, Date.valueOf(obj.getDateDebut()));
            stmt.setDate(4, Date.valueOf(obj.getDateFin()));
            stmt.setString(5, obj.getLieu());
            stmt.setString(6, obj.getEtat().name());
            stmt.setDouble(7, obj.getPrix());

            if (obj.getImage() != null && obj.getImage().length > 0) {
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(obj.getImage());
                stmt.setBinaryStream(8, byteArrayInputStream, obj.getImage().length);
            } else {
                stmt.setNull(8, Types.BLOB);
            }

            stmt.setString(9, obj.getType());
            stmt.setString(10, obj.getOrganisateur());
            stmt.setInt(11, obj.getCapaciteMaximale());
            stmt.setInt(12, obj.getIdCreateurEvenement());

            int res = stmt.executeUpdate();
            if (res > 0) {
                System.out.println("Ajout evenement avec succès !");
                return true;
            } else {
                System.out.println("Aucune ajout de evenement à effectuée");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'ajout de l'événement: " + e.getMessage());
            return false;
        }
        return false;
    }

    public void update(Evenement obj) throws Exception {
        String sql = "UPDATE evenement SET titre = ?, description = ?, dateDebut = ?, dateFin = ?, lieu = ?, etat = ?, prix = ?, image = ?, type = ?, organisateur = ?, capaciteMaximale = ?, idCreateurEvenement = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, obj.getTitre());
            stmt.setString(2, obj.getDescription());
            stmt.setDate(3, Date.valueOf(obj.getDateDebut()));
            stmt.setDate(4, Date.valueOf(obj.getDateFin()));
            stmt.setString(5, obj.getLieu());
            stmt.setString(6, obj.getEtat().name());
            stmt.setDouble(7, obj.getPrix());

            if (obj.getImage() != null && obj.getImage().length > 0) {
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(obj.getImage());
                stmt.setBinaryStream(8, byteArrayInputStream, obj.getImage().length);
            } else {
                stmt.setNull(8, Types.BLOB);
            }

            stmt.setString(9, obj.getType());
            stmt.setString(10, obj.getOrganisateur());
            stmt.setInt(11, obj.getCapaciteMaximale());
            stmt.setInt(12, obj.getIdCreateurEvenement());
            stmt.setInt(13, obj.getId());

            int res = stmt.executeUpdate();
            if (res > 0) {
                System.out.println("Mise à jour de l'événement réussie !");
            } else {
                System.out.println("Aucune mise à jour de l'événement effectuée.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la mise à jour de l'événement: " + e.getMessage());
        }
    }

    public static Evenement getById(int id) throws Exception {
        String sql = "SELECT * FROM evenement WHERE id = ?";
        Evenement obj = null;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    obj = new Evenement();
                    obj.setId(rs.getInt("id"));
                    obj.setTitre(rs.getString("titre"));
                    obj.setDescription(rs.getString("description"));
                    obj.setDateDebut(rs.getDate("dateDebut").toLocalDate());
                    obj.setDateFin(rs.getDate("dateFin").toLocalDate());
                    obj.setLieu(rs.getString("lieu"));
                    obj.setEtat(EtatEvenement.valueOf(rs.getString("etat")));
                    obj.setPrix(rs.getDouble("prix"));

                    Blob blob = rs.getBlob("image");
                    if (blob != null) {
                        obj.setImage(blob.getBytes(1, (int) blob.length()));
                    }

                    obj.setType(rs.getString("type"));
                    obj.setOrganisateur(rs.getString("organisateur"));
                    obj.setCapaciteMaximale(rs.getInt("capaciteMaximale"));
                    obj.setIdCreateurEvenement(rs.getInt("idCreateurEvenement"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return obj;
    }

    public void delete(int id) throws Exception {
        String req = "DELETE FROM evenement WHERE `id`=?";
        try (PreparedStatement pstmt = conn.prepareStatement(req)) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();


            if (rowsAffected > 0) {
                System.out.println("Suppression evenement effectuée avec succès !");
            } else {
                System.out.println("Aucune ligne supprimée evenement. Vérifiez l'ID.");
            }
        } catch (SQLException e) {
            // Handle the exception more gracefully, e.g., log the error or display a user-friendly message
            e.printStackTrace();
        }
    }

    public List<Evenement> getAll() throws Exception {
        updateExpiredEvents();
        updateEtatWhenFull();
        String sql = "SELECT * FROM evenement";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        List<Evenement> evenements = new ArrayList<>();

        while (rs.next()) {
            Evenement obj = new Evenement();
            obj.setId(rs.getInt("id"));
            obj.setTitre(rs.getString("titre"));
            obj.setDescription(rs.getString("description"));
            obj.setDateDebut(rs.getDate("dateDebut").toLocalDate());
            obj.setDateFin(rs.getDate("dateFin").toLocalDate());
            obj.setLieu(rs.getString("lieu"));
            obj.setEtat(EtatEvenement.valueOf(rs.getString("etat")));
            obj.setPrix(rs.getDouble("prix"));

            // Retrieve image as byte[] and set
            Blob blob = rs.getBlob("image");
            if (blob != null) {
                obj.setImage(blob.getBytes(1, (int) blob.length()));
            }

            obj.setType(rs.getString("type"));
            obj.setOrganisateur(rs.getString("organisateur"));
            obj.setCapaciteMaximale(rs.getInt("capaciteMaximale"));
            obj.setIdCreateurEvenement(rs.getInt("idCreateurEvenement"));
            evenements.add(obj);

        }
        return evenements;
    }
    public List<Evenement> myEvents(int idCreateurEvenement) throws Exception {
        String sql = "SELECT * FROM evenement WHERE idCreateurEvenement = ?";
        List<Evenement> evenements = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCreateurEvenement);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Evenement obj = new Evenement();
                    obj.setId(rs.getInt("id"));
                    obj.setTitre(rs.getString("titre"));
                    obj.setDescription(rs.getString("description"));
                    obj.setDateDebut(rs.getDate("dateDebut").toLocalDate());
                    obj.setDateFin(rs.getDate("dateFin").toLocalDate());
                    obj.setLieu(rs.getString("lieu"));
                    obj.setEtat(EtatEvenement.valueOf(rs.getString("etat")));
                    obj.setPrix(rs.getDouble("prix"));

                    Blob blob = rs.getBlob("image");
                    if (blob != null) {
                        obj.setImage(blob.getBytes(1, (int) blob.length()));
                    }

                    obj.setType(rs.getString("type"));
                    obj.setOrganisateur(rs.getString("organisateur"));
                    obj.setCapaciteMaximale(rs.getInt("capaciteMaximale"));
                    obj.setIdCreateurEvenement(rs.getInt("idCreateurEvenement"));
                    evenements.add(obj);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la récupération des événements: " + e.getMessage());
        }

        return evenements;
    }
    public static void updateExpiredEvents() {
        String sql = "UPDATE evenement SET etat = 'EXPIRE' " +
                "WHERE etat = 'ACTIF' AND dateDebut < CURRENT_DATE";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();


        } catch (SQLException e) {
            // Log the exact error

            e.printStackTrace();
        }
    }
    public static void updateEtatWhenFull() {
        String sql = "UPDATE evenement e " +
                "SET etat = 'EXPIRE' " +
                "WHERE e.etat = 'ACTIF' AND " +
                "      e.id IN ( " +
                "          SELECT p.evenementId " +
                "          FROM participantevenement p " +
                "          GROUP BY p.evenementId " +
                "          HAVING COUNT(p.id) >= e.capaciteMaximale " +
                "      )";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {


            int rowsUpdated = pstmt.executeUpdate();
            System.out.println("[DEBUG] Updated " + rowsUpdated + " full events to EXPIRE.");

        } catch (SQLException e) {
            System.err.println("[ERROR] SQL Exception: " + e.getMessage());
        }
    }
}