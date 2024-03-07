package controllers;

import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;


import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;

public class AcceuilFront {
    @FXML
    private Button eventButton;
    @FXML
    private Button messagerie;

    private User currentUser;

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }



    // Méthode pour gérer le clic sur le bouton "Event"
    @FXML
    private void handleEventButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterEvent.fxml"));
            Parent root = loader.load();

            AjouterEvent controller = loader.getController();

            if (controller != null) {
                controller.setCurrentUser(currentUser);

                // Afficher la scène
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.show();

                // Obtenir la scène actuelle à partir de l'événement

                // Définir la nouvelle scène
                stage.setScene(scene);
                stage.show();

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void allerVersBtnMessagerie(ActionEvent event) {
        chargerVue("/ListeDiscussion.fxml", event);
    }
    @FXML
    private void allerVersBtnFacture(ActionEvent event) {
        System.out.println("Attempting to load AfficherAppartResident.fxml for resident");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherAppartResident.fxml"));
        try {
            Parent root = loader.load();
            AfficherAppartResident controller = loader.getController();
            if (controller != null) {

                User currentUser = Login.ConnectedUser;
                if (currentUser != null) {
                    System.out.println("Initializing data for resident: " + currentUser);
                    controller.initData(currentUser);

                    Scene scene = new Scene(root);
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.show();

                    // Fermer la fenêtre de connexion
                } else {
                    System.out.println("Failed to get current user from the database");
                }
            } else {
                System.out.println("Failed to get controller instance for AfficherAppartResident.fxml");
            }
        } catch (IOException e) {
            System.out.println("Failed to load AfficherAppartResident.fxml");
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    private void chargerVue(String cheminFxml, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(cheminFxml));

            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void allerVersBtnParking(ActionEvent event) {
        System.out.println("Attempting to load AfficherAppartResident.fxml for resident");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterVoiture.fxml"));
        try {
            Parent root = loader.load();
            AjouterVoiture controller = loader.getController();
            if (controller != null) {

                User currentUser = Login.ConnectedUser;
                if (currentUser != null) {
                    System.out.println("Initializing data for resident: " + currentUser);
                    controller.initData(currentUser);

                    Scene scene = new Scene(root);
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.show();
                    // Fermer la fenêtre de connexion
                } else {
                    System.out.println("Failed to get current user from the database");
                }
            } else {
                System.out.println("Failed to get controller instance for AfficherAppartResident.fxml");
            }
        } catch (IOException e) {
            System.out.println("Failed to load AfficherAppartResident.fxml");
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    private void handleReclamationButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherReclamation.fxml"));
            Parent root = loader.load();

            AfficherReclamation controller = loader.getController();

            if (controller != null) {
                controller.setCurrentUser(currentUser);

                // Afficher la scène
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.show();

                // Obtenir la scène actuelle à partir de l'événement

                // Définir la nouvelle scène
                stage.setScene(scene);
                stage.show();

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
