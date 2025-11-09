package Models;

public class Adherent extends User {

    private float poids;
    private float taille;
    private int age;
    private GenreG genre;
    private ObjP objectif_personnelle;
    private NiveauA niveau_activites;

    public Adherent() {
    }

    public Adherent(int id, String nom, String prenom, String image, String email, String MDP,String discr, NiveauA niveau_activites, ObjP objectif_personnelle, GenreG genre, int age, float taille, float poids) {
        super(id, nom, prenom, image, email, MDP, discr);
        this.niveau_activites = niveau_activites;
        this.objectif_personnelle = objectif_personnelle;
        this.genre = genre;
        this.age = age;
        this.taille = taille;
        this.poids = poids;
    }

    public Adherent(String nom, String prenom, String image, String email, String MDP,String discr, float poids, float taille, int age, GenreG genre, ObjP objectif_personnelle, NiveauA niveau_activites) {
        super(nom, prenom, image, email, MDP, discr ); // Appel du constructeur de la classe parente User
        this.poids = poids;
        this.taille = taille;
        this.age = age;
        this.genre = genre;
        this.objectif_personnelle = objectif_personnelle;
        this.niveau_activites = niveau_activites;
    }
    // Getters et Setters pour chaque attribut
    public float getPoids() {
        return poids;
    }

    public void setPoids(float poids) {
        this.poids = poids;
    }

    public float getTaille() {
        return taille;
    }

    public void setTaille(float taille) {
        this.taille = taille;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
    public GenreG getGenre() {
        return genre;
    }

    public void setGenre(GenreG genre) {
        this.genre = genre;
    }

    public ObjP getObjectif_personnelle() {
        return objectif_personnelle;
    }

    public void setObjectif_personnelle(ObjP objectif_personnelle) {
        this.objectif_personnelle = objectif_personnelle;
    }

    public NiveauA getNiveau_activites() {
        return niveau_activites;
    }
    public void setNiveau_activites(NiveauA niveau_activites) {
        this.niveau_activites = niveau_activites;
    }

    @Override
    public String toString() {
        return "adherent{" +
                super.toString() +
                "poids=" + poids +
                ", taille=" + taille +
                ", age=" + age +
                ", genre='" + genre + '\'' +
                ", objectif_personnelle=" + objectif_personnelle +
                ", niveau_activites=" + niveau_activites +
                '}';
    }
}
