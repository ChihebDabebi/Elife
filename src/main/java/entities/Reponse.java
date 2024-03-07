package entities;
import entities.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Date;

public class Reponse {
    private String userNom;
    private int idReponse;
    private Reclamation reclamation;
    private String contenu;
    private Date dateReponse;
    private User user;

    public Reponse() {
    }
    public Reponse(Reclamation reclamation) {
        this.reclamation = reclamation;
    }


    public Reponse(Reclamation reclamation, String contenu, Date dateReponse) {
        this.reclamation = reclamation;
        this.contenu = contenu;
        this.dateReponse = dateReponse;
    }

    public Reponse(int idReponse, Reclamation reclamation, String contenu, Date dateReponse) {
        this.idReponse = idReponse;
        this.reclamation = reclamation;
        this.contenu = contenu;
        this.dateReponse = dateReponse;
    }

    public int getIdReponse() {
        return idReponse;
    }

    public void setIdReponse(int idReponse) {
        this.idReponse = idReponse;
    }

    public Reclamation getReclamation() {
        return reclamation;
    }

    public void setReclamation(Reclamation reclamation) {
        this.reclamation = reclamation;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Date getDateReponse() {
        return dateReponse;
    }

    public void setDateReponse(Date dateReponse) {
        this.dateReponse = dateReponse;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Reponse{" +
                "reclamation=" + reclamation +
                ", contenu='" + contenu + '\'' +
                ", dateReponse=" + dateReponse +
                '}';
    }
    public String getUserNom() {
        return userNom;
    }

    public void setUserNom(String userNom) {
        this.userNom = userNom;
    }

    // Ajoutez un accesseur pour la propriété activiteNom
    public StringProperty userNomProperty() {
        return new SimpleStringProperty(getUserNom());
    }

}