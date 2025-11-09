package Models;

import java.sql.Date;
import java.sql.Time;

public class Seance {
    private int id;
    private String Titre;
    private String Description;
    private String LienVideo;
    private Date Date;
    private int idCoach;
    private int idAdherent;
    private int Planning_id;
    private Type Type;
    private Time heureDebut;
    private Time heureFin;

    public Seance() {
    }

    public Seance(int id, String Titre, String Description, Date Date, int idCoach, int idAdherent, Type Type, String LienVideo, int Planning_id, Time heureDebut, Time heureFin) {
        this.Planning_id = Planning_id;
        this.id = id;
        this.Titre = Titre;
        this.Description = Description;
        this.LienVideo = LienVideo;
        this.Date = Date;
        this.idCoach = idCoach;
        this.idAdherent = idAdherent;
        this.Type = Type;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
    }

    public Seance(String Titre, String Description, Date Date, int idCoach, int idAdherent, Type Type, String LienVideo, int Planning_id, Time heureDebut, Time heureFin) {
        this.Planning_id = Planning_id;
        this.Titre = Titre;
        this.Description = Description;
        this.LienVideo = LienVideo;
        this.Date = Date;
        this.idCoach = idCoach;
        this.idAdherent = idAdherent;
        this.Type = Type;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return this.Titre;
    }

    public void setTitre(String Titre) {
        this.Titre = Titre;
    }

    public String getDescription() {
        return this.Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public Date getDate() {
        return this.Date;
    }

    public void setDate(Date Date) {
        this.Date = Date;
    }

    public int getIdCoach() {
        return this.idCoach;
    }

    public void setIdCoach(int idCoach) {
        this.idCoach = idCoach;
    }

    public int getIdAdherent() {
        return this.idAdherent;
    }

    public void setIdAdherent(int idAdherent) {
        this.idAdherent = idAdherent;
    }

    public Type getType() {
        return this.Type;
    }

    public void setType(Type Type) {
        this.Type = Type;
    }

    public Time getHeureDebut() {
        return this.heureDebut;
    }

    public void setHeureDebut(Time heureDebut) {
        this.heureDebut = heureDebut;
    }

    public Time getHeureFin() {
        return this.heureFin;
    }

    public void setHeureFin(Time heureFin) {
        this.heureFin = heureFin;
    }

    public String getLienVideo() {
        return this.LienVideo;
    }

    public void setLienVideo(String LienVideo) {
        this.LienVideo = LienVideo;
    }

    public int getPlanningId() {
        return this.Planning_id;
    }

    public void setPlanningId(int planning_id) {
        this.Planning_id = planning_id;
    }

    public String toString() {

        return "Seance{id=" + this.id + ", Titre='" + this.Titre + "', Description='" + this.Description + "', LienVideo='" + this.LienVideo + "', Date=" + String.valueOf(this.Date) + ", idCoach=" + this.idCoach + ", idAdherent=" + this.idAdherent + ", Planning_id=" + this.Planning_id + ", Type=" + String.valueOf(this.Type) + ", heureDebut=" + String.valueOf(this.heureDebut) + ", heureFin=" + String.valueOf(this.heureFin) + "}";
    }
}
