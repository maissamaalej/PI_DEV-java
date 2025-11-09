package Controllers.Models;

import Models.Etato;
import Models.Offre;

import java.util.Date;

public class OffreProduit extends Offre {
    private int idProduit;
    private double nouveauPrix;
    private int quantiteMax;
    private int quantiteVendue;

    public OffreProduit() {}//Constructeur

    //  Constructeur
    public OffreProduit(int id, String nom, String description, Date duree_validite, Etato etat,
                        int idProduit, double nouveauPrix, int quantiteMax, int quantiteVendue) {
        super(id, nom, description, duree_validite, etat); // Appel du constructeur de Offre
        this.idProduit = idProduit;
        this.nouveauPrix = nouveauPrix;
        this.quantiteMax = quantiteMax;
        this.quantiteVendue = quantiteVendue;
    }

    // Getters et Setters
    public int getIdProduit() {
        return idProduit;
    }

    public void setIdProduit(int idProduit) {
        this.idProduit = idProduit;
    }

    public double getNouveauPrix() {
        return nouveauPrix;
    }

    public void setNouveauPrix(double nouveauPrix) {
        this.nouveauPrix = nouveauPrix;
    }

    public int getQuantiteMax() {
        return quantiteMax;
    }

    public void setQuantiteMax(int quantiteMax) {
        this.quantiteMax = quantiteMax;
    }

    public int getQuantiteVendue() {
        return quantiteVendue;
    }

    public void setQuantiteVendue(int quantiteVendue) {
        this.quantiteVendue = quantiteVendue;
    }

    // toString()
    @Override
    public String toString() {
        return "OffreProduit{" +
                "id=" + getId() +
                ", nom='" + getNom() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", duree_validite=" + getDuree_validite() +
                ", etat=" + getEtat() +
                ", idProduit=" + idProduit +
                ", nouveauPrix=" + nouveauPrix +
                ", quantiteMax=" + quantiteMax +
                ", quantiteVendue=" + quantiteVendue +
                '}';
    }
}

