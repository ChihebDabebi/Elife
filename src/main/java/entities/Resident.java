package entities;


import javafx.scene.control.DatePicker;

import java.util.Date;

public class Resident extends User {

    @Override
    public String toString() {
        return "Resident{" +
                '\'' + "nom='" + getNom() + '\'' +
                ", prenom='" + getPrenom() + '\'' +
                '}';
    }


    public Resident()
    {

    }
    public Resident(int id, String nom, String prenom, int number, String mail, String password, Role role)
    {
        super(id, nom, prenom, number, mail, password,role);
    }

    public Resident(String nom, String prenom, int number, String mail, String password, Role role) {
        super(nom, prenom, number, mail, password,role);
    }
}
