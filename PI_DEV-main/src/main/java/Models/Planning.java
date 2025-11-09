package Models;

public class Planning {
    private int id;
    private int idCoach;
    private String titre;
    private double tarif;

    public Planning() {
    }

    public Planning(int idPlanning, int idcoach, String titre, double tarif) {
        this.id = idPlanning;
        this.idCoach = idcoach;
        this.tarif = tarif;
        this.titre = titre;
    }

    public Planning(int id_coach, String titre, double tarif) {
        this.tarif = tarif;
        this.titre = titre;
        this.idCoach = id_coach;
    }

    public int getIdPlanning() {
        return this.id;
    }

    public void setIdPlanning(int idPlanning) {
        this.id = idPlanning;
    }

    public int getIdcoach() {
        return this.idCoach;
    }

    public void setIdcoach(int idcoach) {
        this.idCoach = idcoach;
    }

    public String getTitre() {
        return this.titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public double getTarif() {
        return this.tarif;
    }

    public void setTarif(double tarif) {
        this.tarif = tarif;
    }

    public String toString() {
        return "Planning{idPlanning=" + this.id + ", idcoach=" + this.idCoach + ", titre='" + this.titre + "', tarif=" + this.tarif + "}";
    }
}
