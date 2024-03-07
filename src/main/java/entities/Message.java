package entities;

import javafx.scene.image.Image;

import java.sql.Timestamp;
import java.util.Arrays;

public class Message {
    private int id ;
    private String contenu;
    private Timestamp TimeStamp_envoi;
    private User emetteur ;
    private Discussion discussion;
    private Image image;
    public Message(){}

    public Message(int id, String contenu, Timestamp TimeStamp_envoi, User emetteur, Discussion discussion, Image image) {
        this.id = id;
        this.contenu = contenu;
        this.TimeStamp_envoi = TimeStamp_envoi;
        this.emetteur = emetteur;
        this.discussion = discussion;
        this.image = image;
    }
    public Message(int id, String contenu, Timestamp TimeStamp_envoi, User emetteur, Image image) {
        this.id = id;
        this.contenu = contenu;
        this.TimeStamp_envoi = TimeStamp_envoi;
        this.emetteur = emetteur;
        this.image = image;
    }
    public Message(String contenu, Timestamp TimeStamp_envoi, User emetteur, Discussion discussion, Image image) {
        this.contenu = contenu;
        this.TimeStamp_envoi = TimeStamp_envoi;
        this.emetteur = emetteur;
        this.discussion = discussion;
        this.image = image;
    }
    public Message(String contenu, Timestamp TimeStamp_envoi, User emetteur, Discussion discussion) {
        this.contenu = contenu;
        this.TimeStamp_envoi = TimeStamp_envoi;
        this.emetteur = emetteur;
        this.discussion = discussion;
    }
    public Message(String contenu, Timestamp TimeStamp_envoi, User emetteur) {
        this.contenu = contenu;
        this.TimeStamp_envoi = TimeStamp_envoi;
        this.emetteur = emetteur;
    }
    public Message(int id, String contenu, Timestamp TimeStamp_envoi, User emetteur) {
        this.id = id;
        this.contenu = contenu;
        this.TimeStamp_envoi = TimeStamp_envoi;
        this.emetteur = emetteur;
    }
    public Message(int id, String contenu) {
        this.id = id;
        this.contenu = contenu;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Timestamp getTimeStamp_envoi() {
        return TimeStamp_envoi;
    }

    public void setTimeStamp_envoi(Timestamp TimeStamp_envoi) {
        this.TimeStamp_envoi = TimeStamp_envoi;
    }

    public User getEmetteur() {
        return emetteur;
    }

    public void setEmetteur(User emetteur) {
        this.emetteur = emetteur;
    }

    public Discussion getDiscussion() {
        return discussion;
    }

    public void setDiscussion(Discussion discussion) {
        this.discussion = discussion;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Message{" +

                ", contenu='" + contenu + '\'' +
                ", TimeStamp_envoi=" + TimeStamp_envoi +
                ", emetteur=" + emetteur +
                ", discussion=" + discussion +

                '}';
    }
}
