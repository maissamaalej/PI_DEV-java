package Controllers.Models;

import Models.EtatPaiementPlan;

import java.time.LocalDate;

public class PaiementPlanning {

    private int id_paiement;
    private int id_adherent;
    private int id_planning;
    private Models.EtatPaiementPlan etatPaiementPlan;
    private LocalDate date_paiement;

    public PaiementPlanning() {}

    public PaiementPlanning(int id_paiement, int id_planning, int id_adherent, Models.EtatPaiementPlan etatPaiementPlan, LocalDate date_paiement) {
        this.id_paiement = id_paiement;
        this.id_planning = id_planning;
        this.id_adherent = id_adherent;
        this.etatPaiementPlan = etatPaiementPlan;
        this.date_paiement = date_paiement;

    }
    public PaiementPlanning(int id_planning, int id_adherent, Models.EtatPaiementPlan etatPaiementPlan, LocalDate date_paiement) {
        this.id_planning = id_planning;
        this.id_adherent = id_adherent;
        this.etatPaiementPlan = etatPaiementPlan;
        this.date_paiement = date_paiement;

    }
    public int getIdPaiement(){ return id_paiement; }
    public int getIdAdherent(){ return id_adherent; }
    public int getIdPlanning(){ return id_planning; }
    public Models.EtatPaiementPlan getEtatPaiementPlan(){ return etatPaiementPlan; }
    public LocalDate getDatePaiement(){ return date_paiement; }

    public void setEtatPaiementPlan(EtatPaiementPlan etatPaiementPlan) {
        this.etatPaiementPlan = etatPaiementPlan;
    }

    public void setId_paiement(int id_paiement) {
        this.id_paiement = id_paiement;
    }
    public void setIdAdherent(int id_adherent) {
        this.id_adherent = id_adherent;
    }
    public void setIdPlanning(int id_planning) {
        this.id_planning = id_planning;
    }
    public void setDate_paiement(LocalDate date_paiement) {
        this.date_paiement = date_paiement;
    }

}
