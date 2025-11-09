package Controllers.Models;
import java.util.Date;

public class ParticipantEvenement {
    private int id;
    private int idParticipant;
    private Date dateInscription;
    private Models.etatPaiement etatPaiement;
    private int idEvenement;

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
