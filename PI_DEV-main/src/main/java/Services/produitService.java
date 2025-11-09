package Services;

import Models.etat;
import Models.produit;
import Utils.MyDb;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class produitService implements Crud<produit> {
    Connection conn;

    public produitService() throws SQLException {
        this.conn = MyDb.getInstance().getConn();
    }

    @Override
    public boolean create(produit obj) throws Exception {
        String sql = "insert into produit (idInvestisseur,nom,description,image,etat,categorieId,quantite,prix) values ('" +
                obj.getIdInvestisseur() + "','" + obj.getNom() + "','" +
                obj.getDescription() + "','" + obj.getImage() + "','" +
                obj.getEtat() + "','" + obj.getCategorieId() + "','" +
                obj.getQuantite() + "','" + obj.getPrix() + "')";
        try {
            Statement st = conn.createStatement();
            int res = st.executeUpdate(sql);
            if (res > 0) {
                System.out.println("Ajout produit avec succès !");
                return true;
            } else {
                System.out.println("Aucun ajout de produit à effectuée ");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return false;
    }
    @Override
    public void update(produit obj) throws Exception {
        String sql = "update produit set nom = ?,description = ?,image= ?,etat= ?,categorieId= ?,quantite= ?,prix= ? where id = ? ";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, obj.getNom());
            stmt.setString(2, obj.getDescription());
            stmt.setString(3, obj.getImage());
            stmt.setString(4, obj.getEtat().name());
            stmt.setInt(5, obj.getCategorieId());
            stmt.setInt(6, obj.getQuantite());
            stmt.setFloat(7, obj.getPrix());
            stmt.setInt(8, obj.getId());
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Modification effectuée avec succès !");
            } else {
                System.out.println("Vérifier l' id de categorie");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM produit WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            // Vérifier si un produit a été supprimé
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new Exception("Aucun produit trouvé avec l'ID " + id);
            }
            System.out.println("produit supprimé avec succès !");
        } catch (Exception e) {
            throw new Exception("Erreur lors de la suppression de produit: " + e.getMessage());
        }
    }

    @Override
    public List<produit> getAll() throws Exception {
        String sql = "select * from produit";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        List<produit> produits = new ArrayList<>();
        while (rs.next()) {
            produit prod = new produit();
            prod.setId(rs.getInt("id"));
            prod.setIdInvestisseur(rs.getInt("IdInvestisseur"));
            prod.setNom(rs.getString("nom"));
            prod.setDescription(rs.getString("Description"));
            prod.setImage(rs.getString("image"));
            prod.setEtat(etat.valueOf(rs.getString("etat")));
            prod.setCategorieId(rs.getInt("categorieId"));
            prod.setQuantite(rs.getInt("quantite"));
            prod.setPrix(rs.getFloat("prix"));
            produits.add(prod);
        }
        return produits;
    }

    @Override
    public produit getById(int id) throws Exception {
        String sql = "select * from produit where id=?";
        produit obj = null;
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {

            int idInvestisseur = rs.getInt("idInvestisseur");
            String nom = rs.getString("nom");
            String description = rs.getString("description");
            String image = rs.getString("image");
            etat etat = Models.etat.valueOf(rs.getString("etat"));
            int categorieId = rs.getInt("categorieId");
            int quantite = rs.getInt("quantite");
            float prix = rs.getFloat("prix");
            obj = new produit(id, idInvestisseur, nom, description, image, etat, categorieId, quantite, prix);
            return obj;
        }
        return obj;
    }

    public List<produit> getProduitsByCategorie(int categorieId) throws Exception {
        List<produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM produit WHERE categorieId = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);

        stmt.setInt(1, categorieId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            int idProduit = rs.getInt("id");
            int idInvestisseur = rs.getInt("idInvestisseur");
            String nom = rs.getString("nom");
            String description = rs.getString("description");
            String image = rs.getString("image");
            etat etat = Models.etat.valueOf(rs.getString("etat"));
            int quantite = rs.getInt("quantite");
            float prix = rs.getFloat("prix");
            produit produit = new produit(idProduit,idInvestisseur, nom, description, image, etat, categorieId , quantite, prix);

            // Ajouter le produit à la liste
            produits.add(produit);
        }
        return produits;
    }
    public List<produit> getAll_ByInvestisseur(int idInvestisseur) throws Exception {
        String sql = "SELECT * FROM produit WHERE idInvestisseur = ?";
        List<produit> produits = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Définir l'ID de l'investisseur dans la requête
            stmt.setInt(1, idInvestisseur);

            // Exécuter la requête
            ResultSet rs = stmt.executeQuery();
            // Parcourir les résultats
            while (rs.next()) {
                produit prod = new produit();
                prod.setId(rs.getInt("id"));
                prod.setIdInvestisseur(rs.getInt("IdInvestisseur"));
                prod.setNom(rs.getString("nom"));
                prod.setDescription(rs.getString("Description"));
                prod.setImage(rs.getString("image"));
                prod.setEtat(etat.valueOf(rs.getString("etat")));
                prod.setCategorieId(rs.getInt("categorieId"));
                prod.setQuantite(rs.getInt("quantite"));
                prod.setPrix(rs.getFloat("prix"));
                produits.add(prod);
            }
        }
        return produits;
    }
}

