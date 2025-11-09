package Services;

import Models.Seance;
import Models.Type;
import Utils.MyDb;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeanceService implements Crud<Seance> {
    Connection conn = MyDb.getInstance().getConn();

    public SeanceService() throws SQLException {}

    @Override
    public boolean create(Seance obj) {
        String sql = "INSERT INTO seance (Titre, Description, Date, LienVideo, Type, heureDebut, heureFin, idCoach, idAdherent, Planning_id) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, obj.getTitre());
            pstmt.setString(2, obj.getDescription());
            pstmt.setDate(3, obj.getDate());
            pstmt.setString(4, obj.getLienVideo());
            pstmt.setString(5, obj.getType().toString());
            pstmt.setTime(6, obj.getHeureDebut());
            pstmt.setTime(7, obj.getHeureFin());
            pstmt.setInt(8, obj.getIdCoach());
            pstmt.setInt(9, obj.getIdAdherent());
            pstmt.setInt(10, obj.getPlanningId());

            int res = pstmt.executeUpdate();

            if (res > 0) {
                System.out.println("Ajout de seance avec succès !");
                return true;
            } else {
                System.out.println("Aucun seance ajouté.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de l'ajout de seance : " + e.getMessage());
            return false;
        }
    }

    @Override
    public void update(Seance obj) throws Exception {
        String sql = "UPDATE Seance SET Titre= ?, Description= ?, Date= ?, LienVideo= ?, Type= ?, heureDebut= ?, heureFin= ? WHERE id = ?";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, obj.getTitre());
            stmt.setString(2, obj.getDescription());
            stmt.setDate(3, obj.getDate());
            stmt.setString(4, obj.getLienVideo());
            stmt.setString(5, obj.getType().toString());
            stmt.setTime(6, obj.getHeureDebut());
            stmt.setTime(7, obj.getHeureFin());
            stmt.setInt(8, obj.getId());  // Assurez-vous que l'ID est bien passé ici.

            // Vérification des paramètres avant l'exécution
            System.out.println("Exécution de la requête de mise à jour avec ID: " + obj.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour de la séance : " + e.getMessage());
        }
    }


    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Seance WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Séance supprimée avec succès !");
            } else {
                System.out.println("Aucune séance trouvée avec l'ID " + id + ".");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de la séance : " + e.getMessage());
        }
    }

    @Override
    public List<Seance> getAll() throws Exception {
        String sql = "SELECT * FROM Seance";
        List<Seance> seances = new ArrayList<>();
        try (Statement stmt = this.conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Seance seance = new Seance();
                seance.setId(rs.getInt("id"));  // Ensure that the id is properly set
                seance.setTitre(rs.getString("Titre"));
                seance.setDescription(rs.getString("Description"));
                seance.setDate(rs.getDate("Date"));
                seance.setType(Type.valueOf(rs.getString("Type")));
                seance.setLienVideo(rs.getString("LienVideo"));
                seance.setHeureDebut(rs.getTime("HeureDebut"));
                seance.setHeureFin(rs.getTime("HeureFin"));
                seance.setIdCoach(rs.getInt("idCoach"));
                seance.setIdAdherent(rs.getInt("idAdherent"));
                seance.setPlanningId(rs.getInt("planning_id"));
                System.out.println("Seance ID: " + seance.getId());  // Debugging line to verify ID
                seances.add(seance);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des séances : " + e.getMessage());
        }
        return seances;
    }

    @Override
    public Seance getById(int id) throws Exception {
        String sql = "SELECT * FROM Seance WHERE id=?";
        Seance obj = null;
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    obj = new Seance(
                            rs.getInt("id"),
                            rs.getString("Titre"),
                            rs.getString("Description"),
                            rs.getDate("Date"),
                            rs.getInt("idCoach"),
                            rs.getInt("idAdherent"),
                            Type.valueOf(rs.getString("Type")),
                            rs.getString("LienVideo"),
                            rs.getInt("planning_id"),
                            rs.getTime("HeureDebut"),
                            rs.getTime("HeureFin")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de la séance par ID : " + e.getMessage());
        }
        return obj;
    }

    public List<Seance> getSeancesByPlanningId(int idPlanning) {
        List<Seance> seances = new ArrayList<>();
        String query = "SELECT * FROM Seance WHERE Planning_id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, idPlanning);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Seance seance = new Seance();
                    seance.setId(resultSet.getInt("id"));  // Make sure to set ID properly
                    seance.setTitre(resultSet.getString("Titre"));
                    seance.setDescription(resultSet.getString("Description"));
                    seance.setDate(resultSet.getDate("Date"));
                    seance.setType(Type.valueOf(resultSet.getString("Type")));
                    seance.setLienVideo(resultSet.getString("LienVideo"));
                    seance.setHeureDebut(resultSet.getTime("HeureDebut"));
                    seance.setHeureFin(resultSet.getTime("HeureFin"));
                    seance.setIdCoach(resultSet.getInt("idCoach"));
                    seance.setIdAdherent(resultSet.getInt("idAdherent"));
                    seance.setPlanningId(resultSet.getInt("planning_id"));
                    seances.add(seance);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des séances par planning ID : " + e.getMessage());
        }
        return seances;
    }
    public List<Seance> getSeancesByAdherentId(int idAdherent) {
        List<Seance> seances = new ArrayList<>();
        String query = "SELECT * FROM seance WHERE idAdherent = ?";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, idAdherent);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Seance seance = new Seance();
                    seance.setId(resultSet.getInt("id"));
                    seance.setTitre(resultSet.getString("Titre"));
                    seance.setDescription(resultSet.getString("Description"));
                    seance.setDate(resultSet.getDate("Date"));
                    seance.setType(Type.valueOf(resultSet.getString("Type")));
                    seance.setLienVideo(resultSet.getString("LienVideo"));
                    seance.setHeureDebut(resultSet.getTime("HeureDebut"));
                    seance.setHeureFin(resultSet.getTime("HeureFin"));
                    seance.setIdCoach(resultSet.getInt("idCoach"));
                    seance.setIdAdherent(resultSet.getInt("idAdherent"));
                    seance.setPlanningId(resultSet.getInt("planning_id"));
                    seances.add(seance);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des séances par idAdherent : " + e.getMessage());
        }
        return seances;
    }

}
