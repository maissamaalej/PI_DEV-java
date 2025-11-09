package Controllers.Models;

import Models.User;

public class InvestisseurProduit extends User {
    private String nom_entreprise;
    private String description;
    private String adresse;
    private String telephone;
    private byte certificat_valide;

    public InvestisseurProduit() {
    }

    public InvestisseurProduit(int id, String nom, String prenom, String image, String email, String MDP, String nom_entreprise, String description, String adresse, String telephone, byte certificat_valide) {
        super(id, nom, prenom, image, email, MDP);
        this.nom_entreprise = nom_entreprise;
        this.description = description;
        this.adresse = adresse;
        this.telephone = telephone;
        this.certificat_valide = certificat_valide;
    }

    public InvestisseurProduit(String nom, String prenom, String image, String email, String MDP,String discr, String nom_entreprise, String description, String adresse, String telephone, byte certificat_valide) {
        super(nom, prenom, image, email, MDP, discr);
        this.nom_entreprise = nom_entreprise;
        this.description = description;
        this.adresse = adresse;
        this.telephone = telephone;
        this.certificat_valide = certificat_valide;
    }
    public String getNom_entreprise() {
        return nom_entreprise;
    }
    public void setNom_entreprise(String nom_entreprise) {
        this.nom_entreprise = nom_entreprise;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getAdresse() {
        return adresse;
    }
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
    public String getTelephone() {
        return telephone;
    }
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    public byte getCertificat_valide() {
        return certificat_valide;
    }
    public void setCertificat_valide(byte certificat_valide) {
        this.certificat_valide = certificat_valide;
    }

    @Override
    public String toString() {
        return "investisseurproduit{" +
                super.toString() +
                "nom_entreprise='" + nom_entreprise + '\'' +
                ", description='" + description + '\'' +
                ", adresse='" + adresse + '\'' +
                ", telephone=" + telephone +
                ", certificat_valide=" + certificat_valide +
                '}';
    }
}
