package entities;

import java.sql.Date; // Utilisation de java.sql.Date au lieu de java.util.Date
import java.util.Objects;
import java.time.LocalDate;
import java.time.ZoneId;

public class Event {

    private Integer idEvent;
    private String title;
    private Date date;
    private int nbrPersonne;
    private String listeInvites; // Renamed variable
    private Espace espace;
    private User user; // Ajout de la référence à l'utilisateur


    public Event() {
    }

    public Event(Integer idEvent, String title, Date date, int nbrPersonne, String listeInvites, Espace espace, User user) {
        this.idEvent = idEvent;
        this.title = title;
        this.date = date;
        this.nbrPersonne = nbrPersonne;
        this.listeInvites = listeInvites;
        this.espace = espace;
        this.user = user; // Initialisation de l'utilisateur
    }

    public Event(String title, Date date, int nbrPersonne, String listeInvites, Espace espace, User user) {
        this.title = title;
        this.date = date;
        this.nbrPersonne = nbrPersonne;
        this.listeInvites = listeInvites;
        this.espace = espace;
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public Integer getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(Integer idEvent) {
        this.idEvent = idEvent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getNbrPersonne() {
        return nbrPersonne;
    }

    public void setNbrPersonne(int nbrPersonne) {
        this.nbrPersonne = nbrPersonne;
    }

    public String getListeInvites() { // Getter renamed
        return listeInvites;
    }

    public void setListeInvites(String listeInvites) { // Setter renamed
        this.listeInvites = listeInvites;
    }
    public Espace getEspace() {
        return espace;
    }

    public void setEspace(Espace espace) {
        this.espace = espace;
    }

    @Override
    public String toString() {
        return "Event : " +
                getTitle() + "\n" +
                " date : " + getDate() + "\n" +
                " nbrPersonne : " + getNbrPersonne() + "\n" +
                " listeInvites : " + getListeInvites() + "\n" +// Updated variable name in the output
                " espace : " + getEspace().getName()  + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return nbrPersonne == event.nbrPersonne &&
                Objects.equals(idEvent, event.idEvent) &&
                Objects.equals(title, event.title) &&
                Objects.equals(date, event.date) &&
                Objects.equals(listeInvites, event.listeInvites) && // Updated variable name
                Objects.equals(espace, event.espace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEvent, title, date, nbrPersonne, listeInvites, espace);
    }
}