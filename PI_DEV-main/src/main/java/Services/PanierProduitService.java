package Services;

import Models.etatP;
import Models.panier;
import Models.panierProduit;
import Models.produit;
import Utils.MyDb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
public class PanierProduitService implements Crud<panierProduit> {
    Connection conn;

    public PanierProduitService() throws SQLException {
        this.conn = MyDb.getInstance().getConn();
    }
    @Override
    public boolean create(panierProduit obj) throws Exception {
        throw new UnsupportedOperationException("Create Not supported");
    }

    public void ajouterProduitAuPanier(panier panier, produit produit, int quantite) throws SQLException {
        if (panier == null || produit == null) {
            throw new IllegalArgumentException("Le panier et le produit ne peuvent pas être null.");
        }
        // Vérifier si le produit existe dans la table produit
        String checkProduitQuery = "SELECT COUNT(*) FROM produit WHERE id = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkProduitQuery)) {
            checkStmt.setInt(1, produit.getId());
            ResultSet resultSet = checkStmt.executeQuery();
            resultSet.next();
            if (resultSet.getInt(1) == 0) {
                throw new SQLException("Le produit avec l'ID " + produit.getId() + " n'existe pas.");
            }
        }

        String query = "INSERT INTO panierproduit (panierId,produitId,etat_paiement, quantite, montant, date) " +
                "VALUES (?, ?, ?, ?, ?, NOW())";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, panier.getId()); // ID du panier
            statement.setInt(2, produit.getId()); // ID du produit
            statement.setString(3, "En_Attente"); // Etat de paiement par défaut
            statement.setInt(4, quantite); // Quantité du produit
            statement.setFloat(5, produit.getPrix() * quantite); // Montant calculé
            // Exécuter l'insertion
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du produit au panier : " + e.getMessage());
            throw e;
        }
    }
    @Override
    public void delete(int id)throws Exception {
        String sql = "DELETE FROM panierproduit WHERE id = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Produit supprimé du panier avec succès !");
            } else {
                System.out.println("Aucun produit trouvé avec les IDs spécifiés.");
            }
        }
    }
    public void modifierQuantiteProduitDansPanier(int id, int nouvelleQuantite) throws Exception {
        String sql = "UPDATE panierproduit SET quantite = ? WHERE id = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            // Définir les paramètres dans la requête SQL
            statement.setInt(1, nouvelleQuantite); // Utiliser nouvelleQuantite ici
            statement.setInt(2, id); // Utiliser id pour spécifier quel produit à mettre à jour

            // Exécuter la mise à jour
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Quantité mise à jour avec succès !");
            } else {
                System.out.println("Aucun produit trouvé avec les IDs spécifiés.");
            }
        }
    }
    @Override
    public void update(panierProduit obj)throws Exception {
        throw new UnsupportedOperationException("Update not supported.");
    }
    public void updatePaymentStatusByPanierId(int panierId,etatP etatPaiement) throws Exception {
        String query = "UPDATE panierproduit SET etat_paiement = ? WHERE panierId = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            // Remplir les paramètres de la requête
            stmt.setString(1, etatPaiement.name()); // Exemple : "Payé"
            stmt.setInt(2, panierId);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("État du paiement mis à jour avec succès pour le panier avec ID : " + panierId);
            } else {
                System.out.println("Aucun panier trouvé avec l'ID : " + panierId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Erreur lors de la mise à jour de l'état du paiement", e);
        }
    }
    @Override
    public List<panierProduit> getAll(){
        throw new UnsupportedOperationException("getAll not supported.");
    }
    public List<panierProduit> getProduitsDansPanier(int panierId) throws Exception {
        String sql = "SELECT * FROM panierproduit WHERE panierId = ?";
        List<panierProduit> produitsDansPanier = new ArrayList<>();

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, panierId); // Définir le panierId
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                panierProduit panierProd = new panierProduit();
                panierProd.setId(rs.getInt("id"));
                panierProd.setProduitId(rs.getInt("produitId"));
                panierProd.setPanierId(rs.getInt("panierId"));
                panierProd.setQuantite(rs.getInt("quantite"));
                panierProd.setDate(rs.getTimestamp("date").toLocalDateTime());
                panierProd.setMontant(rs.getFloat("montant"));
                panierProd.setEtat_paiement(etatP.valueOf(rs.getString("etat_paiement")));

                produitsDansPanier.add(panierProd);
            }
        }
        System.out.println("Produits dans le panier récupérés : " + produitsDansPanier.size());
        return produitsDansPanier;
    }
    @Override
    public panierProduit getById(int id) throws Exception {
        String sql = "select * from panierproduit where id=?";
        panierProduit obj = null;
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            int idpanier = rs.getInt("panierId");
            int idproduit = rs.getInt("produitId");
            int quantite = rs.getInt("quantite");
            LocalDateTime date=rs.getTimestamp("date").toLocalDateTime();
            float montant = rs.getFloat("montant");
            etatP etat_paiement= etatP.valueOf(rs.getString("etat_paiement"));
            obj=new panierProduit(id,idproduit,idpanier,quantite,date,montant,etat_paiement);
            return obj;
        }
        else {
        // Si aucun produit n'est trouvé avec cet ID
        System.out.println("Aucun panier produit trouvé avec l'ID : " + id);
    }
        return obj;
    }
    public void supprimerProduitsParPanierId(int panierId) {
        // Code pour supprimer les produits dans la base de données liés au panierId
        String query = "DELETE FROM panierProduit WHERE panierId = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, panierId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean mettreAJourStockProduit(produit produit) {
        try {
            // Exemple de requête SQL pour mettre à jour le stock
            String query = "UPDATE panierproduit SET quantite = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, produit.getQuantite());
            ps.setInt(2, produit.getId());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
