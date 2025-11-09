package Controllers.Models;

import Models.Etato;
import Models.Offre;

import java.util.Date;

public class OffreCoach extends Offre {
    private int idCoach;
    private double nouveauTarif;
    private int reservationActuelle;
    private int reservationMax;



    public OffreCoach() {
        // Initialiser avec des valeurs par défaut si nécessaire
    }


    // Constructeur complet
    public OffreCoach(int id, String nom, String description, Date duree_validite, Etato etat,
                      int idCoach, double nouveauTarif, int reservationActuelle, int reservationMax) {
        super(id,nom, description, duree_validite, etat);// Appel du constructeur de Offre
        this.idCoach = idCoach;
        this.nouveauTarif = nouveauTarif;
        this.reservationActuelle = reservationActuelle;
        this.reservationMax = reservationMax;
    }

    // Getter et Setter
    public int getIdCoach() {
        return idCoach;
    }

    public void setIdCoach(int idCoach) {
        this.idCoach = idCoach;
    }

    public double getNouveauTarif() {
        return nouveauTarif;
    }

    public void setNouveauTarif(double nouveauTarif) {
        this.nouveauTarif = nouveauTarif;
    }

    public int getReservationActuelle() {
        return reservationActuelle;
    }

    public void setReservationActuelle(int reservationActuelle) {
        this.reservationActuelle = reservationActuelle;
    }

    public int getReservationMax() {
        return reservationMax;
    }

    public void setReservationMax(int reservationMax) {
        this.reservationMax = reservationMax;
    }

    // Méthode toString()
    @Override
    public String toString() {
        return "OffreCoach{" +
                "id=" + getId() +
                ", nom='" + getNom() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", duree_validite=" + getDuree_validite() +
                ", etat=" + getEtat() +
                ", idCoach=" + idCoach +
                ", nouveauTarif=" + nouveauTarif +
                ", reservationActuelle=" + reservationActuelle +
                ", reservationMax=" + reservationMax +
                '}';
    }
}

