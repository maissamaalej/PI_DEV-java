package Services;

import Models.OffreCoach;
import Models.Offre;
import Models.Etato;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OffreCoachService extends OffreService {

    public OffreCoachService() throws SQLException {
    }

    @Override
    public boolean create(Offre obj) throws Exception {
        if (!(obj instanceof OffreCoach)) {
            throw new Exception("L'objet doit être une instance de OffreCoach");
        }

        OffreCoach offreCoach = (OffreCoach) obj;

        // Créer d'abord l'offre dans la table offre
        super.create(offreCoach);

        String sql = "INSERT INTO offrecoach (offre_id, idCoach, nouveau_tarif, reservationActuelle, reservationMax) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, offreCoach.getId()); // Utiliser l'ID généré de l'offre
            stmt.setInt(2, offreCoach.getIdCoach());
            stmt.setDouble(3, offreCoach.getNouveauTarif());
            stmt.setInt(4, offreCoach.getReservationActuelle());
            stmt.setInt(5, offreCoach.getReservationMax());

            stmt.executeUpdate();
            System.out.println("OffreCoach ajoutée avec succès !");
            return true;
        } catch (SQLException e) {
            throw new Exception("Erreur lors de l'ajout de l'offreCoach : " + e.getMessage());
        }
    }

   @Override
    public void update(Offre obj) throws Exception {
        if (!(obj instanceof OffreCoach)) {
            throw new Exception("L'objet doit être une instance de OffreCoach");
        }

        OffreCoach offreCoach = (OffreCoach) obj;
        String sql = "UPDATE offrecoach SET idCoach = ?, nouveau_tarif = ?, reservationActuelle = ?, reservationMax = ? WHERE offre_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, offreCoach.getIdCoach());
            stmt.setDouble(2, offreCoach.getNouveauTarif());
            stmt.setInt(3, offreCoach.getReservationActuelle());
            stmt.setInt(4, offreCoach.getReservationMax());
            stmt.setInt(5, offreCoach.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new Exception("Aucune offreCoach trouvée avec l'ID " + offreCoach.getId());
            }

            System.out.println("OffreCoach mise à jour avec succès !");
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la mise à jour de l'offreCoach : " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM offrecoach WHERE offre_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("OffreCoach supprimée avec succès !");
            } else {
                System.out.println("Aucune ligne supprimée. Vérifiez l'ID.");
            }
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la suppression de l'offreCoach : " + e.getMessage());
        }
    }

    @Override
    public List<Offre> getAll() throws Exception {
        String sql = "SELECT * FROM offrecoach INNER JOIN offre ON offrecoach.offre_id = offre.id";
        List<Offre> offresCoaches = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                OffreCoach offreCoach = new OffreCoach(
                        rs.getInt("offre.id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getDate("duree_validite"),
                        Etato.valueOf(rs.getString("etat")),
                        rs.getInt("idCoach"),
                        rs.getDouble("nouveau_tarif"),
                        rs.getInt("reservationActuelle"),
                        rs.getInt("reservationMax")
                );

                offresCoaches.add(offreCoach);
            }
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la récupération des offresCoach : " + e.getMessage());
        }

        return offresCoaches;
    }

    @Override
    public Offre getById(int id) throws Exception {
        String sql = "SELECT * FROM offrecoach INNER JOIN offre ON offrecoach.offre_id = offre.id WHERE offrecoach.offre_id = ?";
        OffreCoach offreCoach = null;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                offreCoach = new OffreCoach(
                        rs.getInt("offre.id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getDate("duree_validite"),
                        Etato.valueOf(rs.getString("etat")),
                        rs.getInt("idCoach"),
                        rs.getDouble("nouveau_tarif"),
                        rs.getInt("reservationActuelle"),
                        rs.getInt("reservationMax")
                );
            }
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la récupération de l'offreCoach : " + e.getMessage());
        }

        return offreCoach;
    }
}