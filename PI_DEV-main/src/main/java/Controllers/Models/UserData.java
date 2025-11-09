package Controllers.Models;

import Models.GenreG;
import Models.NiveauA;
import Models.ObjP;
import Models.SpecialiteC;
import Models.User;

public class UserData extends User {
    // Attributs spécifiques à l'adhérent
    private float poids;
    private float taille;
    private int age;
    private GenreG genre;
    private ObjP objectifPersonnel;
    private Models.NiveauA niveauActivite;

    // Attributs spécifiques au coach
    private int anneeExperience;
    private boolean certificatValideCoach;
    private Models.SpecialiteC specialite;
    private int note;

    // Attributs spécifiques à l'investisseur de produit
    private String nomEntreprise;
    private String descriptionInvestisseur;
    private String adresseInvestisseur;
    private String telephoneInvestisseur;
    private boolean certificatValideInvestisseur;

    // Attributs spécifiques au créateur d'événements
    private String nomOrganisation;
    private String descriptionCreateur;
    private String adresseCreateur;
    private String telephoneCreateur;
    private boolean certificatValideCreateur;

    // Getters et Setters pour tous les attributs spécifiques
    public float getPoids() { return poids; }
    public void setPoids(float poids) { this.poids = poids; }

    public float getTaille() { return taille; }
    public void setTaille(float taille) { this.taille = taille; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public GenreG getGenre() { return genre; }
    public void setGenre(GenreG genre) { this.genre = genre; }

    public ObjP getObjectifPersonnel() { return objectifPersonnel; }
    public void setObjectifPersonnel(ObjP objectifPersonnel) { this.objectifPersonnel = objectifPersonnel; }

    public Models.NiveauA getNiveauActivite() { return niveauActivite; }
    public void setNiveauActivite(Models.NiveauA niveauActivite) { this.niveauActivite = niveauActivite; }

    public int getAnneeExperience() { return anneeExperience; }
    public void setAnneeExperience(int anneeExperience) { this.anneeExperience = anneeExperience; }

    public boolean isCertificatValideCoach() { return certificatValideCoach; }
    public void setCertificatValideCoach(boolean certificatValideCoach) { this.certificatValideCoach = certificatValideCoach; }

    public Models.SpecialiteC getSpecialite() { return specialite; }
    public void setSpecialite(Models.SpecialiteC specialite) { this.specialite = specialite; }

    public int getNote() { return note; }
    public void setNote(int note) { this.note = note; }

    public String getNomEntreprise() { return nomEntreprise; }
    public void setNomEntreprise(String nomEntreprise) { this.nomEntreprise = nomEntreprise; }

    public String getDescriptionInvestisseur() { return descriptionInvestisseur; }
    public void setDescriptionInvestisseur(String descriptionInvestisseur) { this.descriptionInvestisseur = descriptionInvestisseur; }

    public String getAdresseInvestisseur() { return adresseInvestisseur; }
    public void setAdresseInvestisseur(String adresseInvestisseur) { this.adresseInvestisseur = adresseInvestisseur; }

    public String getTelephoneInvestisseur() { return telephoneInvestisseur; }
    public void setTelephoneInvestisseur(String telephoneInvestisseur) { this.telephoneInvestisseur = telephoneInvestisseur; }

    public boolean isCertificatValideInvestisseur() { return certificatValideInvestisseur; }
    public void setCertificatValideInvestisseur(boolean certificatValideInvestisseur) { this.certificatValideInvestisseur = certificatValideInvestisseur; }

    public String getNomOrganisation() { return nomOrganisation; }
    public void setNomOrganisation(String nomOrganisation) { this.nomOrganisation = nomOrganisation; }

    public String getDescriptionCreateur() { return descriptionCreateur; }
    public void setDescriptionCreateur(String descriptionCreateur) { this.descriptionCreateur = descriptionCreateur; }

    public String getAdresseCreateur() { return adresseCreateur; }
    public void setAdresseCreateur(String adresseCreateur) { this.adresseCreateur = adresseCreateur; }

    public String getTelephoneCreateur() { return telephoneCreateur; }
    public void setTelephoneCreateur(String telephoneCreateur) { this.telephoneCreateur = telephoneCreateur; }

    public boolean isCertificatValideCreateur() { return certificatValideCreateur; }

    public void setCertificatValideCreateur(boolean certificatValideCreateur) { this.certificatValideCreateur = certificatValideCreateur; }

    public UserData(int id, String nom, String prenom, String image, String email, String MDP, float taille, int age, float poids, GenreG genre, ObjP objectifPersonnel, Models.NiveauA niveauActivite, int anneeExperience, boolean certificatValideCoach, int note, Models.SpecialiteC specialite, String nomEntreprise, String descriptionInvestisseur, String adresseInvestisseur, String telephoneInvestisseur, boolean certificatValideInvestisseur, String nomOrganisation, String descriptionCreateur, String adresseCreateur, String telephoneCreateur, boolean certificatValideCreateur) {
        super(id, nom, prenom, image, email, MDP);
        this.taille = taille;
        this.age = age;
        this.poids = poids;
        this.genre = genre;
        this.objectifPersonnel = objectifPersonnel;
        this.niveauActivite = niveauActivite;
        this.anneeExperience = anneeExperience;
        this.certificatValideCoach = certificatValideCoach;
        this.note = note;
        this.specialite = specialite;
        this.nomEntreprise = nomEntreprise;
        this.descriptionInvestisseur = descriptionInvestisseur;
        this.adresseInvestisseur = adresseInvestisseur;
        this.telephoneInvestisseur = telephoneInvestisseur;
        this.certificatValideInvestisseur = certificatValideInvestisseur;
        this.nomOrganisation = nomOrganisation;
        this.descriptionCreateur = descriptionCreateur;
        this.adresseCreateur = adresseCreateur;
        this.telephoneCreateur = telephoneCreateur;
        this.certificatValideCreateur = certificatValideCreateur;
    }

    public UserData(String nom, String prenom, String image, String email, String MDP, String discr , float poids, float taille, int age, GenreG genre, ObjP objectifPersonnel, NiveauA niveauActivite, int anneeExperience, boolean certificatValideCoach, SpecialiteC specialite, int note, String nomEntreprise, String descriptionInvestisseur, String adresseInvestisseur, String telephoneInvestisseur, boolean certificatValideInvestisseur, String nomOrganisation, String descriptionCreateur, String adresseCreateur, String telephoneCreateur, boolean certificatValideCreateur) {
        super(nom, prenom, image, email, MDP, discr);
        this.poids = poids;
        this.taille = taille;
        this.age = age;
        this.genre = genre;
        this.objectifPersonnel = objectifPersonnel;
        this.niveauActivite = niveauActivite;
        this.anneeExperience = anneeExperience;
        this.certificatValideCoach = certificatValideCoach;
        this.specialite = specialite;
        this.note = note;
        this.nomEntreprise = nomEntreprise;
        this.descriptionInvestisseur = descriptionInvestisseur;
        this.adresseInvestisseur = adresseInvestisseur;
        this.telephoneInvestisseur = telephoneInvestisseur;
        this.certificatValideInvestisseur = certificatValideInvestisseur;
        this.nomOrganisation = nomOrganisation;
        this.descriptionCreateur = descriptionCreateur;
        this.adresseCreateur = adresseCreateur;
        this.telephoneCreateur = telephoneCreateur;
        this.certificatValideCreateur = certificatValideCreateur;
    }

    public UserData() {
    }

    @Override
    public String toString() {
        return "UserData{" +
                "poids=" + poids +
                ", taille=" + taille +
                ", age=" + age +
                ", genre=" + genre +
                ", objectifPersonnel=" + objectifPersonnel +
                ", niveauActivite=" + niveauActivite +
                ", anneeExperience=" + anneeExperience +
                ", certificatValideCoach=" + certificatValideCoach +
                ", specialite=" + specialite +
                ", note=" + note +
                ", nomEntreprise='" + nomEntreprise + '\'' +
                ", descriptionInvestisseur='" + descriptionInvestisseur + '\'' +
                ", adresseInvestisseur='" + adresseInvestisseur + '\'' +
                ", telephoneInvestisseur=" + telephoneInvestisseur +
                ", certificatValideInvestisseur=" + certificatValideInvestisseur +
                ", nomOrganisation='" + nomOrganisation + '\'' +
                ", descriptionCreateur='" + descriptionCreateur + '\'' +
                ", adresseCreateur='" + adresseCreateur + '\'' +
                ", telephoneCreateur=" + telephoneCreateur +
                ", certificatValideCreateur=" + certificatValideCreateur +
                '}';
    }

}
