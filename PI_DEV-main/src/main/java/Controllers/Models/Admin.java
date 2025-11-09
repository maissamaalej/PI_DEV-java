package Controllers.Models;

import Models.User;

public class Admin extends User {
    public Admin() {
    }

    public Admin(int id, String nom, String prenom, String image, String email, String MDP , String discr) {
        super(id, nom, prenom, image, email, MDP, discr);
    }

    public Admin(String nom, String prenom, String image, String email, String MDP, String discr) {
        super(nom, prenom, image, email, MDP, discr );
    }


    @Override
    public String toString() {
        return "admin{" +
                super.toString() +
                '}';
    };
    }

