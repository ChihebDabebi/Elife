package controllers;

import entities.Role;
import entities.User;
import services.ServiceUser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utils.DataSource;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class ModifyUser {

    @FXML
    private TextField ftemail;

    @FXML
    private TextField ftmot_de_passe;

    @FXML
    private TextField ftnom;

    @FXML
    private TextField ftprenom;
    @FXML
    private TextField ftnumber;
    File selectedFile;

    private User user; // Added to receive the selected user

    public void setUser(User user) {
        this.user = user;
        // Set the text fields with the user's data
        ftnom.setText(user.getNom());
        ftprenom.setText(user.getPrenom());
        ftmot_de_passe.setText(user.getPassword());
        ftemail.setText(user.getMail());
        ftnumber.setText(String.valueOf(user.getNumber()));
    }
    @FXML
    void modifier(ActionEvent event) throws IOException {

        Connection connection = DataSource.getInstance().getConnexion();  // Get connection
        String nom = ftnom.getText();
        String prenom = ftprenom.getText();
        String mot_de_passe = ftmot_de_passe.getText();
        String email = ftemail.getText();
        int number = Integer.parseInt(ftnumber.getText());
        // Vérifier si les champs ne sont pas vides
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || mot_de_passe.isEmpty()) {
            // Afficher un message d'erreur si des champs sont vides
            showAlert(Alert.AlertType.ERROR, "Champs vides", "Veuillez remplir tous les champs.");
            return;
        }

        // Vérifier si le nom est une chaîne de caractères
        if (!nom.matches("[a-zA-Z]+")) {
            showAlert(Alert.AlertType.ERROR, "Nom invalide", "Le nom doit contenir uniquement des lettres.");
            return;
        }

        // Vérifier si l'email est valide
        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.ERROR, "Email invalide", "Veuillez entrer un email valide.");
            return;
        }

        // Créer un objet Utilisateurs avec les nouvelles informations
        User utilisateurs = new User(nom, prenom,number, mot_de_passe, email);


        // Mettre à jour l'utilisateur dans la base de données
        ServiceUser su = new ServiceUser();
        try {
            su.modifier(utilisateurs);
            // Afficher un message de succès
            showAlert(Alert.AlertType.INFORMATION, "Mise à jour réussie", "Les informations ont été mises à jour avec succès.");
        } catch (RuntimeException e) {
            // Afficher un message d'erreur en cas d'échec de la mise à jour
            showAlert(Alert.AlertType.ERROR, "Erreur de mise à jour", "Erreur lors de la mise à jour des informations. Veuillez réessayer.");
            e.printStackTrace(); // Afficher la stack trace de l'exception pour le débogage
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Méthode pour afficher une boîte de dialogue d'alerte
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode pour valider un email
    private boolean isValidEmail(String email) {
        // Vous pouvez implémenter une validation plus détaillée selon vos besoins
        return email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    }
   /* @FXML
    void supprimer(ActionEvent event)  {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Utilisateur supprimé");
        alert.setContentText("Utilisateur supprimé");
        alert.show();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            // Fermer la fenêtre actuelle si nécessaire
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}