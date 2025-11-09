package Services;

import Models.Offre;
import Models.Etato;
import Utils.MyDb;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public  class OffreService implements Crud<Offre> {
    protected Connection conn;

    public OffreService() throws SQLException {
        this.conn = MyDb.getInstance().getConn();
    }

    @Override
    public boolean create(Offre obj) throws Exception {
        String sql = "INSERT INTO offre (nom, description, duree_validite, etat) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, obj.getNom());
            stmt.setString(2, obj.getDescription());
            stmt.setDate(3, new Date(obj.getDuree_validite().getTime()));
            stmt.setString(4, obj.getEtat().name());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        obj.setId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("Offre ajoutée avec succès !");
                return true; // Insertion réussie
            } else {
                System.out.println("Aucune offre ajoutée.");
                return false; // Aucune ligne insérée
            }
        } catch (SQLException e) {
            throw new Exception("Erreur lors de l'ajout de l'offre : " + e.getMessage());
        }
    }

    @Override
    public void update(Offre obj) throws Exception {
        String sql = "UPDATE offre SET nom = ?, description = ?, duree_validite = ?, etat = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, obj.getNom());
            stmt.setString(2, obj.getDescription());
            stmt.setDate(3, new Date(obj.getDuree_validite().getTime()));
            stmt.setString(4, obj.getEtat().name());
            stmt.setInt(5, obj.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new Exception("Aucune offre trouvée avec l'ID " + obj.getId());
            }

            System.out.println("Offre mise à jour avec succès !");
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la mise à jour de l'offre : " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) throws Exception { // Changer ici pour correspondre à l'interface Crud
        String sql = "DELETE FROM offre WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new Exception("Aucune offre trouvée avec l'ID " + id);
            }
            System.out.println("Offre supprimée avec succès !");
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la suppression de l'offre : " + e.getMessage());
        }
    }

    @Override
    public List<Offre> getAll() throws Exception {
        String sql = "SELECT * FROM offre";
        List<Offre> offres = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Offre offre = new Offre();
                offre.setId(rs.getInt("id"));
                offre.setNom(rs.getString("nom"));
                offre.setDescription(rs.getString("description"));
                offre.setDuree_validite(rs.getDate("duree_validite"));
                offre.setEtat(Etato.valueOf(rs.getString("etat")));
                offres.add(offre);
            }
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la récupération des offres : " + e.getMessage());
        }
        return offres;
    }

    @Override
    public Offre getById(int id) throws Exception {
        String sql = "SELECT * FROM offre WHERE id = ?";
        Offre offre = null;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                offre = new Offre();
                offre.setId(rs.getInt("id"));
                offre.setNom(rs.getString("nom"));
                offre.setDescription(rs.getString("description"));
                offre.setDuree_validite(rs.getDate("duree_validite"));
                offre.setEtat(Etato.valueOf(rs.getString("etat")));
            }
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la récupération de l'offre : " + e.getMessage());
        }

        return offre;
    }
}