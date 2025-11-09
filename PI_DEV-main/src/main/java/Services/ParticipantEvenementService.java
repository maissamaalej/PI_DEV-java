package Services;

import Models.*;
import Utils.MyDb;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ParticipantEvenementService implements Crud <ParticipantEvenement>{
    static Connection conn;
    public ParticipantEvenementService() throws SQLException {
        this.conn= MyDb.getInstance().getConn();
    }
   @Override
    public boolean create(ParticipantEvenement obj) throws Exception {
       SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
       String formattedDate = dateFormat.format(obj.getDateInscription());
        String sql="insert into participantevenement(evenementId,userId,date_inscription,etat_paiement)" +
                " values('"+obj.getIdEvenement()+"','"+obj.getIdParticipant()+"'," +
                "'"+formattedDate +"','"+obj.getEtatPaiement()+"')";
        try{
        Statement st = conn.createStatement();
        int res = st.executeUpdate(sql);
        if (res > 0) {
            System.out.println("Ajout partispant avec succès !");
            return true ;
        } else {
            System.out.println("Aucune ajout de partispant à effectuée ");
        }}catch (Exception e){
        System.out.println(e.getMessage());
        return false ;
        }
        return false;
    }


    @Override
    public void update(ParticipantEvenement obj) {
        String req = "UPDATE participantevenement SET  userId=?, evenementId=?, date_inscription=?, etat_paiement=?  WHERE id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(req)) {
            pstmt.setInt(1, obj.getIdParticipant());
            pstmt.setInt(2, obj.getIdEvenement());
            pstmt.setDate(3, new java.sql.Date(obj.getDateInscription().getTime()));
            pstmt.setString(4, obj.getEtatPaiement().name());
            pstmt.setInt(5, obj.getId());


            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Modification ParticipantEvenement effectuée avec succès !");
            } else {
                System.out.println("Vérifier l' id de ParticipantEvenement");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
//@Override
//public void update(ParticipantEvenement obj) throws SQLException {
//    String req = "UPDATE participantevenement SET  userId=?, evenementId=?, date_inscription=?, etat_paiement=?  WHERE id= ?";
//   PreparedStatement pstmt = conn.prepareStatement(req);
//
//        pstmt.setInt(1, obj.getIdParticipant());
//        pstmt.setInt(2, obj.getIdEvenement());
//        pstmt.setDate(3, new java.sql.Date(obj.getDateInscription().getTime()));
//        pstmt.setString(4, obj.getEtatPaiement().name());
//        pstmt.setInt(5, obj.getId());
//
//
//         pstmt.executeUpdate();
//
//
//}

@Override
    public void delete(int id) throws Exception {
        String req = "DELETE FROM participantevenement WHERE `id`=?";
        try (PreparedStatement pstmt = conn.prepareStatement(req)) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();


            if (rowsAffected > 0) {
                System.out.println("Suppression partispant effectuée avec succès !");
            } else {
                System.out.println("Aucune ligne supprimée a participantevenement. Vérifiez l'ID .");
            }
        } catch (SQLException e) {
            // Handle the exception more gracefully, e.g., log the error or display a user-friendly message
            e.printStackTrace();
        }
    }


    @Override
    public List<ParticipantEvenement> getAll() throws Exception {
        String sql = "select * from participantevenement";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        List<ParticipantEvenement> participantEvenements = new ArrayList<>();
        while (rs.next()) {
            ParticipantEvenement participant = new ParticipantEvenement();
            participant.setIdEvenement(rs.getInt("evenementId"));
            participant.setIdParticipant(rs.getInt("userId"));
            participant.setDateInscription(rs.getDate("date_inscription"));
            participant.setEtatPaiement(etatPaiement.valueOf(rs.getString("etat_paiement")));
            participant.setId(rs.getInt("id"));
            participantEvenements.add(participant);

        }
        return participantEvenements;
    }
    @Override
    public ParticipantEvenement getById(int id) {
        String req = "SELECT * FROM participantevenement WHERE id = ?";
        ParticipantEvenement post = null;
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setInt(1, id); // Set the value of the id parameter
            ResultSet res = ps.executeQuery();

            if (res.next()) {


                int idParticipant = res.getInt("userId");
                Models.etatPaiement etatPaiement   = Models.etatPaiement.valueOf(res.getString("etat_paiement"));
                Date  dateInscription     = res.getDate("date_inscription");

                int idEvenement = res.getInt("evenementId");



                post = new ParticipantEvenement(id,idParticipant,  dateInscription,  etatPaiement,  idEvenement);
                return post;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return post;

    }








    public List<ParticipantEvenement> getParticipantsByEvent(int eventId) {
        List<ParticipantEvenement> participants = new ArrayList<>();

        // Query to fetch participants for the event
        String query = "SELECT * FROM participantevenement WHERE evenementId = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            cancelExpiredPendingPayments();
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Get participant id, etatPaiement, and datePaiement from the result set
                int idParticipant = rs.getInt("userId");
                etatPaiement etatPaiement = Models.etatPaiement.valueOf(rs.getString("etat_paiement"));
                Date datePaiement = Date.valueOf(rs.getString("date_inscription"));

                // Create a ParticipantEvenement object, which will internally fetch the User object using the id
                ParticipantEvenement participantEvenement = new ParticipantEvenement(idParticipant, etatPaiement, datePaiement);

                participants.add(participantEvenement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return participants;
    }

    // Add this to ParticipantEvenementService class
    public void updatePaymentStatus(int userId, int eventId) {
        String query = "UPDATE participantevenement SET etat_paiement = ? WHERE userId = ? AND evenementId = ?";
        try (
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, etatPaiement.PAYE.name());
            pstmt.setInt(2, userId);
            pstmt.setInt(3, eventId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating payment status: " + e.getMessage());
        }
    }





    public static void cancelExpiredPendingPayments() {
        String sql = "UPDATE participantevenement " +
                "SET etat_paiement = 'ANNULER' " +
                "WHERE etat_paiement = 'EN_ATTENTE' " +
                "AND date_inscription < CURRENT_DATE";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {


            int rowsUpdated = pstmt.executeUpdate();
            System.out.println("[DEBUG] Canceled " + rowsUpdated + " expired pending payments.");

        } catch (SQLException e) {
            System.err.println("[ERROR] SQL Exception: " + e.getMessage());
        }
    }














    public List<Evenement> getEventsByParticipant(int participantId) {
        List<Evenement> events = new ArrayList<>();

        // Query to fetch events for the participant
        String query = "SELECT * FROM participantevenement WHERE userId = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, participantId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Get event id from the result set
                int eventId = rs.getInt("evenementId");

                // Create an Evenement object based on the eventId (assuming you have a method to fetch event details)
                Evenement event = EvenementService.getById(eventId); // Implement this method to retrieve event details by eventId

                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return events;
    }

//    // Assuming you have a method like this to get event details by eventId
//    public Evenement getEventById(int eventId) {
//        Evenement event = null;
//        String query = "SELECT * FROM evenement WHERE id = ?";
//
//        try (PreparedStatement stmt = conn.prepareStatement(query)) {
//            stmt.setInt(1, eventId);
//            ResultSet rs = stmt.executeQuery();
//
//            if (rs.next()) {
//                // Retrieve event details from result set
//                int id = rs.getInt("id");
//                String name = rs.getString("name");
//                // Add other attributes as needed
//                event = new Evenement(id, name); // Customize the constructor as per your event model
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return event;
//    }

}




