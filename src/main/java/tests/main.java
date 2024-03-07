package tests;

import controllers.AjoutDiscussion;
import controllers.ListeDiscussion;
import entities.*;
import services.DiscussionService;
import services.MessageService;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class main {

    public static void main(String[] args) throws SQLException {
        User User1 = new User(1,"chiheb");
        User User2 = new User(2,"ali");
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        MessageService messageService = new MessageService();
        DiscussionService discussionService = new DiscussionService();
        Discussion discussion = new Discussion(18,"update discussion", currentTimestamp, User1);
        Message message = new Message(19,"how are you",currentTimestamp,User2);
        // ajouter discussion
       /* try {
            // Insert the discussion into the database
            discussionService.ajouter(discussion);
            System.out.println("Discussion created successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to create discussion: " + e.getMessage());
        }*/
        // supprimer discussion

   /* try{
        discussionService.supprimer(2);

    }catch(Exception e){
        System.out.println("Failed to delete discussion");
    }*/

        /// afficher tout ldes discussions
       /* try{
            System.out.println(discussionService.afficher());
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }*/

/*
        try {
            // Insert the message into the database
            messageService.ajouter(message);
            System.out.println("MEssage sent successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to send message: " + e.getMessage());
        }
*/
       /* try {
            messageService.supprimer(5);
            messageService.supprimer(6);
            messageService.supprimer(7);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }*/

        System.out.println(messageService.afficherByDiscussionId(1));
    }

}









