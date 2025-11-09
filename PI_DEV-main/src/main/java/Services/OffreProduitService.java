package Services;

import Models.OffreProduit;
import Models.Offre;
import Models.Etato;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OffreProduitService extends OffreService {

    public OffreProduitService() throws SQLException {
    }

    @Override
    public boolean create(Offre obj) throws Exception {
        if (!(obj instanceof OffreProduit)) {
            throw new Exception("L'objet doit être une instance de OffreProduit");
        }

        OffreProduit offreProduit = (OffreProduit) obj;

        // Créer d'abord l'offre dans la table offre
        super.create(offreProduit);

        String sql = "INSERT INTO offreproduit (offre_id, idProduit, nouveauPrix, quantiteMax, quantiteVendue) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, offreProduit.getId()); // Utiliser l'ID généré de l'offre
            stmt.setInt(2, offreProduit.getIdProduit());
            stmt.setDouble(3, offreProduit.getNouveauPrix());
            stmt.setInt(4, offreProduit.getQuantiteMax());
            stmt.setInt(5, offreProduit.getQuantiteVendue());

            stmt.executeUpdate();
            System.out.println("OffreProduit ajoutée avec succès !");
            return true;
        } catch (SQLException e) {
            throw new Exception("Erreur lors de l'ajout de l'offreProduit : " + e.getMessage());
        }
    }

    @Override
    public void update(Offre obj) throws Exception {
        if (!(obj instanceof OffreProduit)) {
            throw new Exception("L'objet doit être une instance de OffreProduit");
        }

        OffreProduit offreProduit = (OffreProduit) obj;
        String sql = "UPDATE offreproduit SET idProduit = ?, nouveauPrix = ?, quantiteMax = ?, quantiteVendue = ? WHERE offre_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, offreProduit.getIdProduit());
            stmt.setDouble(2, offreProduit.getNouveauPrix());
            stmt.setInt(3, offreProduit.getQuantiteMax());
            stmt.setInt(4, offreProduit.getQuantiteVendue());
            stmt.setInt(5, offreProduit.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new Exception("Aucune offreProduit trouvée avec l'ID " + offreProduit.getId());
            }

            System.out.println("OffreProduit mise à jour avec succès !");
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la mise à jour de l'offreProduit : " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM offreproduit WHERE offre_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("OffreProduit supprimée avec succès !");
            } else {
                System.out.println("Aucune ligne supprimée. Vérifiez l'ID.");
            }
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la suppression de l'offreProduit : " + e.getMessage());
        }
    }

    @Override
    public List<Offre> getAll() throws Exception {
        String sql = "SELECT * FROM offreproduit INNER JOIN offre ON offreproduit.offre_id = offre.id";
        List<Offre> offresProduits = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                OffreProduit offreProduit = new OffreProduit(
                        rs.getInt("offre.id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getDate("duree_validite"),
                        Etato.valueOf(rs.getString("etat")),
                        rs.getInt("idProduit"),
                        rs.getDouble("nouveauPrix"),
                        rs.getInt("quantiteMax"),
                        rs.getInt("quantiteVendue")
                );

                offresProduits.add(offreProduit);
            }
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la récupération des offresProduit : " + e.getMessage());
        }

        return offresProduits; // Retourne la liste d'OffreProduit
    }

    @Override
    public Offre getById(int id) throws Exception {
        String sql = "SELECT * FROM offreproduit INNER JOIN offre ON offreproduit.offre_id = offre.id WHERE offreproduit.offre_id = ?";
        OffreProduit offreProduit = null;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                offreProduit = new OffreProduit(
                        rs.getInt("offre.id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getDate("duree_validite"),
                        Etato.valueOf(rs.getString("etat")),
                        rs.getInt("idProduit"),
                        rs.getDouble("nouveauPrix"),
                        rs.getInt("quantiteMax"),
                        rs.getInt("quantiteVendue")
                );
            }
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la récupération de l'offreProduit : " + e.getMessage());
        }

        return offreProduit; // Retourne l'objet OffreProduit
    }
}