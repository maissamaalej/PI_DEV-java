package Services;

import Models.Categorie;
import Utils.MyDb;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class categorieService implements Crud<Categorie> {
    Connection conn;
    public categorieService() throws SQLException {
        this.conn = MyDb.getInstance().getConn();
    }
    @Override
    public boolean create(Categorie obj) throws Exception {
        String sql = "insert into Categorie (nom,image) values ('" +
                 obj.getNom() + "','" + obj.getImage() +"')";
        try{
            Statement st = conn.createStatement();
            int res = st.executeUpdate(sql);
            if (res > 0) {
                System.out.println("Ajout categorie avec succès !");
                return true ;
            } else {
                System.out.println("Aucune ajout de categorie à effectuée ");
            }}catch (Exception e){
            System.out.println(e.getMessage());
            return false ;
        }
        return false;
    }
    @Override
    public void update(Categorie obj) throws Exception {
        String sql = "update Categorie set nom = ?,image= ? where id = ? ";
        try (PreparedStatement stmt = conn.prepareStatement(sql)){
        stmt.setString(1, obj.getNom());
        stmt.setString(2, obj.getImage());
        stmt.setInt(3, obj.getId());

        int rowsUpdated = stmt.executeUpdate();

        if (rowsUpdated > 0) {
            System.out.println("Modification categorie effectuée avec succès !");
        } else {
            System.out.println("Vérifier l'id de categorie");
        }
    } catch (Exception e) {
        System.out.println(e.getMessage());
    }
    }
    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM Categorie WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1,id);
            // Vérifier si un categorie a été supprimé
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new Exception("Aucun Categorie trouvé avec l'ID "+ id);
            }
            System.out.println("categorie supprimé avec succès !");
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la suppression de categorie: " + e.getMessage());
        }
    }
    @Override
    public List<Categorie> getAll() throws Exception {
        String sql = "select * from Categorie";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        List<Categorie> categs = new ArrayList<>();
        while (rs.next()) {
            Categorie categ = new Categorie();
            categ.setId(rs.getInt("id"));
            categ.setNom(rs.getString("nom"));
            categ.setImage(rs.getString("image"));
            System.out.println("Categorie chargée : " + categ.getId() + ", " + categ.getNom() + ", " + categ.getImage());
            categs.add(categ);
        }
        return categs;
    }
    @Override
    public Categorie getById(int id) throws Exception
    {
        String sql = "select * from Categorie where id=?";
        Categorie obj = null;
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            String nom= rs.getString("nom");
            String image = rs.getString("image");
            obj=new Categorie(id,nom,image);
            return obj;
        }
        return obj;
    }
    public List<Integer> getCategoryId() throws SQLException {
        String sql = "SELECT id FROM Categorie";
        List<Integer> categoryIds = new ArrayList<>();

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categoryIds.add(rs.getInt("id"));
            }
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de l'exécution de la requête pour récupérer les IDs des catégories", e);
        }
        return categoryIds;
    }

    public String getNomCategorieById(int idCategorie) {
        String query = "SELECT nom FROM categorie WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idCategorie);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("nom");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Inconnu";
    }

}

