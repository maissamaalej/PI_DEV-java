package Services;

import Models.ParticipantEvenement;
import Models.Participation;
import Models.etatPaiement;
import Utils.MyDb;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParticpationService {
    Connection conn;

    public ParticpationService() throws SQLException {
        this.conn = MyDb.getInstance().getConn();
    }


    public boolean create(Participation obj) throws Exception {
        String sql = "insert into participation(userId,evenementId)" +
                " values('" + obj.getIdEvenement() + "','" + obj.getIdParticipant() + "')";

        try {
            Statement st = conn.createStatement();
            int res = st.executeUpdate(sql);
            if (res > 0) {
                System.out.println("Ajout participation avec succès !");
                return true;
            } else {
                System.out.println("Aucune ajout de participation à effectuée ");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return false;
    }


    public void delete(int id) throws Exception {
        String req = "DELETE FROM participation WHERE `id`=?";
        try (PreparedStatement pstmt = conn.prepareStatement(req)) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Suppression participation effectuée avec succès !");
            } else {
                System.out.println("Aucune ligne supprimée a participation. Vérifiez l'ID .");
            }
        } catch (SQLException e) {
            // Handle the exception more gracefully, e.g., log the error or display a user-friendly message
            e.printStackTrace();
        }
    }


    public List<Participation> getAll() throws Exception {
        String sql = "select * from participation";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        List<Participation> participations = new ArrayList<>();
        while (rs.next()) {
            Participation participation = new Participation();
            participation.setId(rs.getInt("id"));
            participation.setIdEvenement(rs.getInt("evenementId"));
            participation.setIdParticipant(rs.getInt("userId"));
            participations.add(participation);

        }
        return participations;
    }
}



