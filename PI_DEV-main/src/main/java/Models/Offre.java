package Models;
import java.util.Date;

public  class Offre {
    private int id;
    private String nom;
    private String description;
    private Date duree_validite;
    private Etato etato;

    public Offre() {
    }
    public Offre(int id,String nom, String description, Date duree_validite, Etato etato) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.duree_validite = duree_validite;
        this.etato = etato;

    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Date getDuree_validite() {
        return duree_validite;
    }
    public void setDuree_validite(Date duree_validite) {
        this.duree_validite = duree_validite;
    }
    public Etato getEtat() {
        return etato;
    }
    public void setEtat(Etato etato) {
        this.etato = etato;
    }


    @Override
    public String toString() {
        return "Offre{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", duree_validite=" + duree_validite +
                ", etat=" + etato +
                '}';
    }




}
