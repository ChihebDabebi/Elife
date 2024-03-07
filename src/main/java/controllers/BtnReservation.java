package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class BtnReservation {

    @FXML
    private void allerVersAjouterEspace(ActionEvent event) {
        chargerVue("/AjouterEspace.fxml", event);
    }

    @FXML
    private void allerVersAfficherEspace(ActionEvent event) {
        chargerVue("/AfficherEspace.fxml", event);
    }

    @FXML
    private void allerVersAfficherEvents(ActionEvent event) {
        chargerVue("/AfficherEvent.fxml", event);
    }

    private void chargerVue(String cheminFxml, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(cheminFxml));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void retournerVersAcceuil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AcceuilBack.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
