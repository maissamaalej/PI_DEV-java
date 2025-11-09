package Services;

import Models.panier;
import Utils.MyDb;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class panierService implements Crud<panier>{
    Connection conn;
    public panierService() throws SQLException {
        this.conn = MyDb.getInstance().getConn();
    }
    @Override
    public boolean create(panier obj) throws Exception {
        // Préparation de la requête SQL
        String sql = "INSERT INTO panier (id_user) VALUES (?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Vérifiez si id_user est non nul
            if (obj.getUserId() != null) {
                stmt.setInt(1, obj.getUserId());
            } else {
                // Si id_user est null, on lève une exception ou on retourne false
                throw new IllegalArgumentException("L'id_user ne peut pas être null.");
            }
            // Exécution de la requête
            int res = stmt.executeUpdate();
            if (res > 0) {
                System.out.println("Ajout réussi dans le panier !");
                return true;
            } else {
                System.out.println("Aucun ajout dans le panier n'a été effectué.");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout dans le panier : " + e.getMessage());
            return false;
        }
    }

    @Override
    public void update(panier obj) {
        throw new UnsupportedOperationException("Update not supported.");
    }
    @Override
    public void delete(int id) {
        throw new UnsupportedOperationException("Delete not supported.");
    }
    //  vider le panier d'un adherent
    @Override
    public List<panier> getAll() throws Exception {
        String sql = "select * from panier";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        List<panier> paniers = new ArrayList<>();
        while (rs.next()) {
            panier panier = new panier();
            panier.setId(rs.getInt("id"));
            panier.setUserId(rs.getInt("id_user"));
            paniers.add(panier);
        }
        return paniers;
    }
    @Override
    public panier getById(int id) throws Exception {
        String sql = "select * from panier where id=?";
        panier obj = null;
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            int idUser = rs.getInt("id_user");
            obj=new panier(id,idUser);
            return obj;
        }
        return obj;
    }
    public panier getOrCreatePanierForUser(int idUser) throws SQLException {
        // Vérifier si un panier existe déjà pour cet utilisateur
        String selectQuery = "SELECT * FROM panier WHERE id_user = ?";
        try (PreparedStatement stmt = conn.prepareStatement(selectQuery)) {
            stmt.setInt(1, idUser);
            ResultSet rs = stmt.executeQuery();

            // Si un panier est trouvé, le retourner
            if (rs.next()) {
                panier existingPanier = new panier();
                existingPanier.setId(rs.getInt("id"));
                existingPanier.setUserId(rs.getInt("id_user"));
                return existingPanier;
            }
        }
        // Si aucun panier n'existe, en créer un nouveau
        String insertQuery = "INSERT INTO panier (id_user) VALUES (?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, idUser);
            stmt.executeUpdate();

            // Récupérer l'ID du panier nouvellement créé
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                panier newPanier = new panier();
                newPanier.setId(generatedKeys.getInt(1));
                newPanier.setUserId(idUser);
                return newPanier;
            }
        }

        throw new SQLException("Impossible de créer ou récupérer un panier pour l'utilisateur ID: " + idUser);
    }

}
