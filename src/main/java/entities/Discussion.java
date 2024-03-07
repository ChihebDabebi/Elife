package entities;

import java.sql.Timestamp;

public class Discussion {
    private int id;
    private String titre;
    private String description;
    private String color;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private Timestamp TimeStampCreation;
    private User createur;


    public Discussion(){}
    public Discussion(int id, String titre, Timestamp TimeStampCreation, User createur) {
        this.id = id;
        this.titre = titre;
        this.TimeStampCreation = TimeStampCreation;
        this.createur = createur;
    }
    public Discussion(int id, String titre, Timestamp TimeStampCreation, User createur,String description) {
        this.id = id;
        this.titre = titre;
        this.TimeStampCreation = TimeStampCreation;
        this.createur = createur;
        this.description = description;
    }
    public Discussion(int id, String titre,String description,String color) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.color = color;
    }
    public Discussion(String titre, Timestamp TimeStampCreation, User createur,String description,String color) {
        this.titre = titre;
        this.TimeStampCreation = TimeStampCreation;
        this.createur = createur;
        this.description = description;
        this.color=color;
    }
    public Discussion(String titre, Timestamp TimeStampCreation, User createur,String description) {
        this.titre = titre;
        this.TimeStampCreation = TimeStampCreation;
        this.createur = createur;
        this.description = description;

    }
    public Discussion(int id,String titre, Timestamp TimeStampCreation) {
        this.id = id;
        this.titre = titre;
        this.TimeStampCreation = TimeStampCreation;

    }
    public Discussion(int id,String titre,String description) {
        this.id = id;
        this.titre = titre;
        this.description = description;

    }
    public Discussion(String titre, Timestamp TimeStampCreation,String description) {
        this.titre = titre;
        this.TimeStampCreation = TimeStampCreation;
        this.description = description;

    }
    public Discussion(int id,String titre) {
        this.id = id;
        this.titre = titre;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public Timestamp getTimeStampCreation() {
        return TimeStampCreation;
    }

    public void setTimeStampCreation(Timestamp TimeStampCreation) {
        this.TimeStampCreation = TimeStampCreation;
    }

    public User getCreateur() {
        return createur;
    }

    public void setCreateur(User createur) {
        this.createur = createur;
    }

    @Override
    public String toString() {
        return "Discussion{" +

                ", titre='" + titre + '\'' +
                ", TimeStampCreation=" + TimeStampCreation +
                ", createur=" + createur +
                '}';
    }
}
