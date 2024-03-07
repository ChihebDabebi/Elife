package entities;

public class Concierge extends User {


    public Concierge() {

    }


    public Concierge(int id, String nom, String prenom, int number, String mail, String password, Role role) {
        super(id, nom, prenom, number, mail, password,role);

    }

    public Concierge(String nom, String prenom, int number, String mail, String password,Role role) {
        super(nom, prenom, number, mail, password,role);
    }

    @Override
    public String toString() {
        return "Concierge{" +
        "nom='" + getNom() + '\'' +
                ", prenom='" + getPrenom() + '\'' + '}';
    }
}
