package Services;

import Models.EtatPaiementPlan;
import Utils.MyDb;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class PaiementPlanningService {
    Connection conn = MyDb.getInstance().getConn();

public PaiementPlanningService() throws SQLException {}


    public void updatePaymentStatus(int id_adherent, int PlanningId) {
        String query = "UPDATE paiement_planning SET etat_paiement = ? WHERE id_adherent = ? AND id_planning = ?";
        try (
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, EtatPaiementPlan.PAYE.name());
            pstmt.setInt(2, id_adherent);
            pstmt.setInt(3, PlanningId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating payment status: " + e.getMessage());
        }
    }

    public void reserverPlanning(int idAdherent, int idPlanning) {
        String sql = "INSERT INTO paiement_planning (id_adherent, id_planning, etat_paiement, date_paiement) VALUES (?, ?, 'EN_ATTENTE', ?)";

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idAdherent);
            stmt.setInt(2, idPlanning);
            stmt.setDate(3, Date.valueOf(LocalDate.now())); // Utilisation correcte pour LocalDate
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


        public List<Integer> getAdherentsPayeByPlanning(int idPlanning) {
            List<Integer> adherents = new ArrayList<>();
            String query = "SELECT DISTINCT id_adherent FROM paiement_planning " +
                    "WHERE id_planning = ? AND etat_paiement = 'payé'"; // Assurez-vous que 'payé' correspond à la valeur exacte dans la BDD

            try {
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setInt(1, idPlanning);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    adherents.add(rs.getInt("id_adherent"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return adherents;
        }

}




