package Models;
import Utils.MyDb;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ParticipantEvenement {
    private int id;
    private int idParticipant;
    private Date dateInscription;
    private Models.etatPaiement etatPaiement;
    private int idEvenement;
    private User participant;

    public ParticipantEvenement() {}
    public ParticipantEvenement(int idParticipant, Date dateInscription, Models.etatPaiement etatPaiement, int idEvenement) {

        this.idParticipant = idParticipant;
        this.dateInscription = dateInscription;
        this.etatPaiement = etatPaiement;
        this.idEvenement = idEvenement;
    }
    public ParticipantEvenement(int id,int idParticipant,Date dateInscription, Models.etatPaiement etatPaiement, int idEvenement) {
        this.id = id;
        this.idParticipant = idParticipant;
        this.dateInscription = dateInscription;
        this.etatPaiement = etatPaiement;
        this.idEvenement = idEvenement;
    }
    public ParticipantEvenement(int idParticipant, Models.etatPaiement etatPaiement, Date dateInscription) {
        this.idParticipant = idParticipant;
        this.etatPaiement = etatPaiement;
        this.dateInscription = dateInscription;
        this.participant = fetchUserById(idParticipant); // Fetch User based on id
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdParticipant() {
        return idParticipant;
    }

    public void setIdParticipant(int idParticipant) {
        this.idParticipant = idParticipant;
    }

    public Date getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(Date dateInscription) {
        this.dateInscription = dateInscription;
    }

    public Models.etatPaiement getEtatPaiement() {
        return etatPaiement;
    }

    public void setEtatPaiement(Models.etatPaiement etatPaiement) {
        this.etatPaiement = etatPaiement;
    }

    public int getIdEvenement() {
        return idEvenement;
    }

    public void setIdEvenement(int idEvenement) {
        this.idEvenement = idEvenement;
    }

    private User fetchUserById(int id) {
        // This method fetches the user from the database based on the idParticipant
        User user = null;
        String query = "SELECT * FROM user WHERE id = ?";
        try (PreparedStatement stmt = MyDb.getInstance().getConn().prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User(
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("image")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    // Getters for participant, etatPaiement, and datePaiement
    public User getParticipant() {
        return participant;
    }

    @Override
    public String toString() {
        return "ParticipantEvenement{" +
                "id=" + id +
                ", idParticipant=" + idParticipant +
                ", dateInscription=" + dateInscription +
                ", etatPaiement=" + etatPaiement +
                ", idEvenement=" + idEvenement +
                '}';
    }
}


