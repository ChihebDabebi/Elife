package entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Appartement {
    private int idAppartement;
    private int numAppartement;
    private String nomResident;
    private String taille;
    private int nbrEtage;
    private statutAppartement statutAppartement;
    private List<Facture> factures = new ArrayList<>();
  User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public enum statutAppartement{
        Occupee , Libre
    }
    public Appartement(){}
    /*public Appartement(int numAppartement){
        this.numAppartement = numAppartement;
    } */

    public Appartement(int idAppartement, int numAppartement, String nomResident, String taille, int nbrEtage, Appartement.statutAppartement statutAppartement, List<Facture> factures) {
        this.idAppartement = idAppartement;
        this.numAppartement = numAppartement;
        this.nomResident = nomResident;
        this.taille = taille;
        this.nbrEtage = nbrEtage;
        this.statutAppartement = statutAppartement;
        this.factures = factures;
    }

    public Appartement(int numAppartement, String nomResident, String taille, int nbrEtage, Appartement.statutAppartement statutAppartement) {
        this.numAppartement = numAppartement;
        this.nomResident = nomResident;
        this.taille = taille;
        this.nbrEtage = nbrEtage;
        this.statutAppartement = statutAppartement;
    }

    public Appartement(int numAppartement, String nomResident, String taille, int nbrEtage, statutAppartement statutAppartement, List<Facture> factures) {
        this.numAppartement = numAppartement;
        this.nomResident = nomResident;
        this.taille = taille;
        this.nbrEtage = nbrEtage;
        this.statutAppartement = statutAppartement;
        this.factures = factures;
    }

    public int getIdAppartement() {
        return idAppartement;
    }

    public void setIdAppartement(int idAppartement) {
        this.idAppartement = idAppartement;
    }

    public int getNumAppartement() {
        return numAppartement;
    }

    public void setNumAppartement(int numAppartement) {
        this.numAppartement = numAppartement;
    }

    public String getNomResident() {
        return nomResident;
    }

    public void setNomResident(String nomResident) {
        this.nomResident = nomResident;
    }

    public String getTaille() {
        return taille;
    }

    public void setTaille(String taille) {
        this.taille = taille;
    }

    public int getNbrEtage() {
        return nbrEtage;
    }

    public void setNbrEtage(int nbrEtage) {
        this.nbrEtage = nbrEtage;
    }

    public Appartement.statutAppartement getStatutAppartement() {
        return statutAppartement;
    }

    public void setStatutAppartement(Appartement.statutAppartement statutAppartement) {
        this.statutAppartement = statutAppartement;
    }

    public List<Facture> getFactures() {
        return factures;
    }

    public void setFactures(List<Facture> factures) {
        this.factures = factures;
    }

    @Override
    public String toString() {
        return "Numéro d'appartement : '" + getNumAppartement() + "',\n" +
                "Nom du résident : '" + getNomResident() + "',\n" +
                "Taille : '" + getTaille() + "',\n" +
                "Étage : '" + getNbrEtage() + "',\n" +
                "Statut de l'appartement : '" + getStatutAppartement() + "',\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Appartement that = (Appartement) o;
        return numAppartement == that.numAppartement && nbrEtage == that.nbrEtage;
    }


    @Override
    public int hashCode() {
        return Objects.hash(numAppartement, nbrEtage);
    }
}
