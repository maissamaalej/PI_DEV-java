package Models;

import java.time.LocalDateTime;

public class panierProduit {
    private int id;
    private int panierId;
    private int produitId;
    private int quantite;
    private LocalDateTime date;
    private float montant;
    private etatP etat_paiement;
    public panierProduit() {}
    public panierProduit(int id,int panierId,int produitId,int quantite,LocalDateTime date,float montant,etatP etat_paiement) {
        this.id = id;
        this.panierId = panierId;
        this.produitId = produitId;
        this.quantite = quantite;
        this.date = date;
        this.montant = montant;
        this.etat_paiement = etat_paiement;
    }
    public panierProduit(int panierId,int produitId,int quantite,LocalDateTime date,float montant,etatP etat_paiement) {
        this.panierId = panierId;
        this.produitId = produitId;
        this.quantite = quantite;
        this.date = date;
        this.montant = montant;
        this.etat_paiement = etat_paiement;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPanierId() {
        return panierId;
    }

    public void setPanierId(int panierId) {
        this.panierId = panierId;
    }

    public int getProduitId() {
        return produitId;
    }

    public void setProduitId(int produitId) {
        this.produitId = produitId;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public float getMontant() {
        return montant;
    }

    public void setMontant(float montant) {
        this.montant = montant;
    }

    public etatP getEtat_paiement() {
        return etat_paiement;
    }

    public void setEtat_paiement(etatP etat_paiement) {
        this.etat_paiement = etat_paiement;
    }

    @Override
    public String toString() {
        return "panierProduit{" +
                "id=" + id +
                ", panierId=" + panierId +
                ", produitId=" + produitId +
                ", quantite=" + quantite +
                ", date=" + date +
                ", montant=" + montant +
                ", etat_paiement=" + etat_paiement +
                '}';
    }
}

