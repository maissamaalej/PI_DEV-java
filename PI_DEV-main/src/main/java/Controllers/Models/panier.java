package Controllers.Models;

public class panier {

    private int id;
    private Integer adherentId;
    private Integer coachId;

    public panier() {}
    public panier(Integer adherentId, Integer coachId) {
        this.adherentId = adherentId;
        this.coachId = coachId;
    }
    public panier(int id, Integer adherentId, Integer coachId) {
        this.id = id;
        this.adherentId = adherentId;
        this.coachId = coachId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getAdherentId() {
        return adherentId;
    }

    public void setAdherentId(Integer adherentId) {
        this.adherentId = adherentId;
    }

    public Integer getCoachId() {
        return coachId;
    }

    public void setCoachId(Integer coachId) {
        this.coachId = coachId;
    }

    @Override
    public String toString() {
        return "panier{" +
                "id=" + id +
                ", adherentId=" + adherentId +
                ", coachId=" + coachId +
                '}';
    }
}
