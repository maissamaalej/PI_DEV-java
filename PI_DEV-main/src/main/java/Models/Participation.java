package Models;

public class Participation {
    private int id;
    private int idParticipant;
    private int idEvenement;


    public Participation() { }

    public Participation(int id, int idParticipant, int idEvenement ) {
        this.id = id;
        this.idParticipant = idParticipant;
        this.idEvenement = idEvenement;

    }

    public Participation(int idParticipant, int idEvenement) {
        this.idParticipant = idParticipant;
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

    public int getIdEvenement() {
        return idEvenement;
    }

    public void setIdEvenement(int idEvenement) {
        this.idEvenement = idEvenement;
    }



    @Override
    public String toString() {
        return "Participation{" +
                "id=" + id +
                ", idParticipant=" + idParticipant +
                ", idEvenement=" + idEvenement +

                '}';
    }
}
