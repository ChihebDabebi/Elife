package controllers;

import javafx.fxml.Initializable;
import services.ServiceEmailUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class EmailUtil implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Any initialization code can be added here
        System.out.println("EmailUtil initialized");
    }

    // Method to send email notification to the user
    public static void sendReclamationNotification(String userEmail, String emailSubject, String emailBody) {
        String header = "Reclamation Submitted";
        String body = "Your reclamation has been successfully submitted. Thank you.";

        // Call the sendEmail method from ServiceEmailUtil class
        ServiceEmailUtil.sendEmail(userEmail, header, body);
    }
}