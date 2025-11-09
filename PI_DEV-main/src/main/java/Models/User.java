package Models;

public class User {
    private int id;
    private String nom, prenom  , email , MDP , discr ;
    private String image;


    public User() {
    }

    public User(int id, String nom, String prenom, String image, String email, String MDP , String discr) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.image = image;
        this.email = email;
        this.MDP = MDP;
        this.discr = discr;

    }

    public User(String nom, String prenom, String image, String email, String MDP , String discr) {
        this.nom = nom;
        this.prenom = prenom;
        this.image = image;
        this.email = email;
        this.MDP = MDP;
        this.discr = discr;

    }

    public User(int userId, String nom, String prenom, String email, String mdp , String discr) {
        this.id = userId;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.MDP = mdp;
        this.discr = discr;
    }

    public User(String nom, String prenom, String email, String image) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.image = image;

    }

    // Getter et Setter pour id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter et Setter pour nom
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
    // Getter et Setter pour prenom
    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    // Getter et Setter pour image
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    // Getter et Setter pour email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getter et Setter pour MDP
    public String getMDP() {
        return MDP;
    }

    public void setMDP(String MDP) {
        this.MDP = MDP;
    }

    public String getDiscr() {
        return discr;
    }
    public void setDiscr(String discr) {
        this.discr = discr;
    }


    // Méthode toString pour afficher les infos de l'utilisateur
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", image='" + image + '\'' +
                ", email='" + email + '\'' +
                ", MDP='" + MDP + '\'' +
                ", discr='" + discr + '\'' +
                '}';
    }


}