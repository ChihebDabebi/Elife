package entities;

import java.util.Objects;

public class Voiture {
    private int idVoiture;
    private int id; // Assuming this is the foreign key referring to the resident
    private String marque;
    private String model;
    private String couleur;
    private String matricule;
    private Parking parking; // Changed type from String to Parking

    private User user; // Added reference to User


    public Voiture() {
    }

    public Voiture(int id, String marque, String model, String couleur, String matricule, Parking parking ,User user) {
        this.id = id;
        this.marque = marque;
        this.model = model;
        this.couleur = couleur;
        this.matricule = matricule;
        this.parking = parking;
        this.user = user; // Initialisation de l'utilisateur

    }
    public Voiture( String marque, String model, String couleur, String matricule, Parking parking ,User user) {
        this.marque = marque;
        this.model = model;
        this.couleur = couleur;
        this.matricule = matricule;
        this.parking = parking;
        this.user = user; // Initialisation de l'utilisateur

    }
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getIdVoiture() {
        return idVoiture;
    }

    public void setIdVoiture(int idVoiture) {
        this.idVoiture = idVoiture;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCouleur() {
        return couleur;
    }

    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public Parking getParking() {
        return parking;
    }

    public void setParking(Parking parking) {
        this.parking = parking;
    }


    @Override
    public String toString() {
        return "Voiture : " +'\n' +
                "MARQUE : " + marque + '\n' +
                " MODEL : " + model + '\n' +
                " COULEUR : " + couleur + '\n' +
                " MATRICULE : " + matricule + '\n' +
                " PLACE : " + parking.getNom()
                ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Voiture voiture = (Voiture) o;
        return idVoiture == voiture.idVoiture &&
                id == voiture.id &&
                Objects.equals(marque, voiture.marque) &&
                Objects.equals(model, voiture.model) &&
                Objects.equals(couleur, voiture.couleur) &&
                Objects.equals(matricule, voiture.matricule) &&
                Objects.equals(parking, voiture.parking);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idVoiture, id, marque, model, couleur, matricule, parking);
    }
}
