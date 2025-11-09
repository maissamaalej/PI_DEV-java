package Controllers.Models;

import Models.User;

public class CreateurEvenement extends User {
    private String nom_organisation;
    private String description;
    private String adresse;
    private String telephone;
    private byte certificat_valide;

    public CreateurEvenement() {
    }

    public CreateurEvenement(int id, String nom, String prenom, String image, String email, String MDP, String nom_organisation, String description, String adresse, String telephone, byte certificat_valide) {
        super(id, nom, prenom, image, email, MDP);
        this.nom_organisation = nom_organisation;
        this.description = description;
        this.adresse = adresse;
        this.telephone = telephone;
        this.certificat_valide = certificat_valide;
    }

    public CreateurEvenement(String nom, String prenom, String image, String email, String MDP,String discr, String nom_organisation, String description, String adresse, String telephone, byte certificat_valide) {
        super(nom, prenom, image, email, MDP, discr);
        this.nom_organisation = nom_organisation;
        this.description = description;
        this.adresse = adresse;
        this.telephone = telephone;
        this.certificat_valide = certificat_valide;
    }
    public String getNom_organisation() {
        return nom_organisation;
    }
    public void setNom_organisation(String nom_organisation) {}
    public String getDescription() {
        return "";
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
        return "createurevenement{" +
                super.toString() +
                "nom_organisation='" + nom_organisation + '\'' +
                ", description='" + description + '\'' +
                ", adresse='" + adresse + '\'' +
                ", telephone=" + telephone +
                ", certificat_valide=" + certificat_valide +
                '}';
    }
}
