package entities;

import javafx.scene.control.Tooltip;

import java.util.Date;
import java.util.Objects;

public class Facture {
    public int idFacture;
    private int numFacture;
    private Date date;
    private Type type;
    private float montant;
    private String descriptionFacture;
    private float consomation ;

    // La clé étrangère
    private Appartement appartement;


    public enum Type {
        Eau, Gaz, Electricite,  Dechets
    }

    public Facture() {

    }

    public Facture(int idFacture) {
        this.idFacture = idFacture;
    }

    public Facture(int idFacture, int numFacture, Date date, Type type, float montant, String descriptionFacture,float consomation, Appartement appartement) {
        this.idFacture = idFacture;
        this.numFacture = numFacture;
        this.date = date;
        this.type = type;
        this.montant = montant;
        this.descriptionFacture = descriptionFacture;
        this.consomation = consomation ;
        this.appartement = appartement;
    }


    public Facture(int numFacture, Date date, Type type, float montant, String descriptionFacture,float consomation) {
        this.numFacture = numFacture;
        this.date = date;
        this.type = type;
        this.montant = montant;
        this.descriptionFacture = descriptionFacture;
        this.consomation = consomation ;


    }

    public Facture(int idFacture, int numFacture, Date date, Type type, float montant, String descriptionFacture,  float  consomation) {
        this.idFacture = idFacture;
        this.numFacture = numFacture;
        this.date = date;
        this.type = type;
        this.montant = montant;
        this.descriptionFacture = descriptionFacture;
        this.consomation = consomation ;


    }

    public Facture(int numFacture, Date date, Type type, float montant, String descriptionFacture,float consomation, Appartement appartement) {
        this.numFacture = numFacture;
        this.date = date;
        this.type = type;
        this.montant = montant;
        this.descriptionFacture = descriptionFacture;
        this.consomation = consomation ;
        this.appartement = appartement;

    }

    public int getNumFacture() {
        return numFacture;
    }

    public void setNumFacture(int numFacture) {
        this.numFacture = numFacture;
    }


    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public float getMontant() {
        return montant;
    }

    public void setMontant(float montant) {
        this.montant = montant;
    }

    public String getDescriptionFacture() {
        return descriptionFacture;
    }

    public void setDescriptionFacture(String descriptionFacture) {
        this.descriptionFacture = descriptionFacture;
    }

    public Appartement getAppartement() {
        return appartement;
    }

    public void setAppartement(Appartement appartement) {
        this.appartement = appartement;
    }

    public int getIdFacture() {
        return idFacture;
    }


    public void setIdFacture(int idFacture) {
        this.idFacture = idFacture;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getConsomation() {
        return consomation;
    }

    public void setConsomation(float consomation) {
        this.consomation = consomation;
    }

    @Override
    public String toString() {
        return "Numéro de Facture : '" + getNumFacture() + "',\n" +
                "Type de Facture : '" + getType() + "',\n" +
                "Montant : '" + getMontant() + "',\n" +
                "Consomation : '" + getConsomation() + "',\n" +
                "Date : '" + getDate() + "',\n" +
                "Description: '" + getDescriptionFacture() + "',\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Facture facture = (Facture) o;
        return numFacture == facture.numFacture && type == facture.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numFacture, type);
    }
}
