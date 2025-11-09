//
//package Models;
//
//
//
//import java.time.LocalDate;
//
//public class Evenement {
//    private int id;
//    private String titre;
//    private String description;
//    private LocalDate dateDebut;
//    private LocalDate dateFin;
//    private String lieu;
//    private byte[] image;
//    private double prix;
//    private EtatEvenement etat;
//    private String type;
//    private String organisateur;
//    private int capaciteMaximale;
//
//    public Evenement() {
//    }
//
//    public Evenement(int id, String titre, String description, LocalDate dateDebut, LocalDate dateFin, String lieu, byte[] image, double prix, EtatEvenement etat, String type, String organisateur, int capaciteMaximale) {
//        this.id = id;
//        this.titre = titre;
//        this.description = description;
//        this.dateDebut = dateDebut;
//        this.dateFin = dateFin;
//        this.lieu = lieu;
//        this.image = image;
//        this.prix = prix;
//        this.etat = etat;
//        this.type = type;
//        this.organisateur = organisateur;
//        this.capaciteMaximale = capaciteMaximale;
//    }
//
//    public Evenement(String titre, String description, LocalDate dateDebut, LocalDate dateFin, String lieu, byte[] image, double prix, EtatEvenement etat, String type, String organisateur, int capaciteMaximale) {
//        this.titre = titre;
//        this.description = description;
//        this.dateDebut = dateDebut;
//        this.dateFin = dateFin;
//        this.lieu = lieu;
//        this.image = image;
//        this.prix = prix;
//        this.etat = etat;
//        this.type = type;
//        this.organisateur = organisateur;
//        this.capaciteMaximale = capaciteMaximale;
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public String getTitre() {
//        return titre;
//    }
//
//    public void setTitre(String titre) {
//        this.titre = titre;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public LocalDate getDateFin() {
//        return dateFin;
//    }
//
//    public void setDateFin(LocalDate dateFin) {
//        this.dateFin = dateFin;
//    }
//
//    public LocalDate getDateDebut() {
//        return dateDebut;
//    }
//
//    public void setDateDebut(LocalDate dateDebut) {
//        this.dateDebut = dateDebut;
//    }
//
//    public String getLieu() {
//        return lieu;
//    }
//
//    public void setLieu(String lieu) {
//        this.lieu = lieu;
//    }
//
//    public double getPrix() {
//        return prix;
//    }
//
//    public void setPrix(double prix) {
//        if (prix < 0) {
//            throw new IllegalArgumentException("Price cannot be negative");
//        }
//        this.prix = prix;
//    }
//    public byte[] getImage() {
//        return image;
//    }
//
//    public void setImage(byte[] image) {
//       this.image = image;
//    }
//
//    public EtatEvenement getEtat() {
//        return etat;
//    }
//
//    public void setEtat(EtatEvenement etat) {
//        this.etat = etat;
//    }
//
//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }
//
//    public String getOrganisateur() {
//        return organisateur;
//    }
//
//    public void setOrganisateur(String organisateur) {
//        this.organisateur = organisateur;
//    }
//
//    public int getCapaciteMaximale() {
//        return capaciteMaximale;
//    }
//
//    public void setCapaciteMaximale(int capaciteMaximale) {
//        this.capaciteMaximale = capaciteMaximale;
//    }
//
//
//    @Override
//    public String toString() {
//        return "Evenement{" +
//                "id=" + id +
//                ", titre='" + titre + '\'' +
//                ", description='" + description + '\'' +
//                ", dateDebut=" + dateDebut +
//                ", dateFin=" + dateFin +
//                ", lieu='" + lieu + '\'' +
//                ", image=" + image +
//                ", prix=" + prix +
//                ", etat=" + etat +
//                ", type='" + type + '\'' +
//                ", organisateur='" + organisateur + '\'' +
//                ", capaciteMaximale=" + capaciteMaximale +
//                '}';
//    }
//}





package Models;

import java.time.LocalDate;

public class Evenement {
    private int id;
    private String titre;
    private String description;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String lieu;
    private byte[] image;
    private double prix;
    private EtatEvenement etat;
    private String type;
    private String organisateur;
    private int capaciteMaximale;
    private int idCreateurEvenement; // New attribute

    public Evenement() {
    }

    public Evenement(int id, String titre, String description, LocalDate dateDebut, LocalDate dateFin, String lieu, byte[] image, double prix, EtatEvenement etat, String type, String organisateur, int capaciteMaximale, int idCreateurEvenement) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.lieu = lieu;
        this.image = image;
        this.prix = prix;
        this.etat = etat;
        this.type = type;
        this.organisateur = organisateur;
        this.capaciteMaximale = capaciteMaximale;
        this.idCreateurEvenement = idCreateurEvenement;
    }

    public Evenement(String titre, String description, LocalDate dateDebut, LocalDate dateFin, String lieu, byte[] image, double prix, EtatEvenement etat, String type, String organisateur, int capaciteMaximale, int idCreateurEvenement) {
        this.titre = titre;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.lieu = lieu;
        this.image = image;
        this.prix = prix;
        this.etat = etat;
        this.type = type;
        this.organisateur = organisateur;
        this.capaciteMaximale = capaciteMaximale;
        this.idCreateurEvenement = idCreateurEvenement;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public EtatEvenement getEtat() {
        return etat;
    }

    public void setEtat(EtatEvenement etat) {
        this.etat = etat;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrganisateur() {
        return organisateur;
    }

    public void setOrganisateur(String organisateur) {
        this.organisateur = organisateur;
    }

    public int getCapaciteMaximale() {
        return capaciteMaximale;
    }

    public void setCapaciteMaximale(int capaciteMaximale) {
        this.capaciteMaximale = capaciteMaximale;
    }

    public int getIdCreateurEvenement() {
        return idCreateurEvenement;
    }

    public void setIdCreateurEvenement(int idCreateurEvenement) {
        this.idCreateurEvenement = idCreateurEvenement;
    }

    @Override
    public String toString() {
        return "Evenement{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", lieu='" + lieu + '\'' +
                ", image=" + image +
                ", prix=" + prix +
                ", etat=" + etat +
                ", type='" + type + '\'' +
                ", organisateur='" + organisateur + '\'' +
                ", capaciteMaximale=" + capaciteMaximale +
                ", idCreateurEvenement=" + idCreateurEvenement +
                '}';
    }

}