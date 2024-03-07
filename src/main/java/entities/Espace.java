package entities;

import java.util.Objects;

public class Espace {
    public enum Etat {
        RESERVE, LIBRE
    }
    private int idEspace;
    private String name;
    private Etat etat;
    private int capacite;
    private String description;



    public Espace() {}

    public Espace(int idEspace, String name, Etat etat, int capacite, String description) {
        this.idEspace = idEspace;
        this.name = name;
        this.etat = etat;
        this.capacite = capacite;
        this.description = description;
    }
    public Espace( String name, Etat etat, int capacite, String description) {
        this.name = name;
        this.etat = etat;
        this.capacite = capacite;
        this.description = description;
    }

    public int getIdEspace() {
        return idEspace;
    }

    public void setIdEspace(int idEspace) {
        this.idEspace = idEspace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Etat getEtat() {
        return etat;
    }

    public void setEtat(Etat etat) {
        this.etat = etat;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Espace : " +
                  getName() + "\n" +
                " etat : " + getEtat() + "\n" +
                " capacite : " + getCapacite() + "\n" +
                " description : " + getDescription()
                ;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Espace espace = (Espace) o;
        return idEspace == espace.idEspace;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEspace);
    }
}