package entities;

import java.util.List;
import java.util.Objects;

public class Parking {
    private int idParking;
    private String nom;
    private int capacite;
    private String type;
    private int nombreActuelles;
    private List<Voiture> voitures; // Added list of Voitures

    public Parking() {
    }

    public Parking(String nom, int capacite, String type, int nombreActuelles) {
        this.nom = nom;
        this.capacite = capacite;
        this.type = type;
        this.nombreActuelles = nombreActuelles;
    }

    public int getIdParking() {
        return idParking;
    }

    public void setIdParking(int idParking) {
        this.idParking = idParking;
    }

    public String getNom() {
        return this.nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getNombreActuelles() {
        return nombreActuelles;
    }

    public void setNombreActuelles(int nombreActuelles) {
        this.nombreActuelles = nombreActuelles;
    }

    public List<Voiture> getVoitures() {
        return voitures;
    }

    public void setVoitures(List<Voiture> voitures) {
        this.voitures = voitures;
    }

    @Override
    public String toString() {
        return
                "NOM :" + nom +'\n' +
                "CAPACITE :" + capacite +'\n' +
                "TYPE DE PARKING :" + type +'\n' +
                "NOMBRE ACTUELLES DE VOITURE :" + nombreActuelles +'\n'
                ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Parking)) return false;
        Parking parking = (Parking) o;
        return idParking == parking.idParking &&
                capacite == parking.capacite &&
                nombreActuelles == parking.nombreActuelles &&
                Objects.equals(nom, parking.nom) &&
                Objects.equals(type, parking.type) &&
                Objects.equals(voitures, parking.voitures);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idParking, nom, capacite, type, nombreActuelles, voitures);
    }
}
