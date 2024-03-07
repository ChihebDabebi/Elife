package controllers;

import entities.Parking;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.ServiceParking;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.SQLException;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AjouterParking {
    @FXML
    private TextField nomTextField;

    @FXML
    private TextField capaciteTextField;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TextField nombreActuellesTextField;

    @FXML
    private Button boutonAjouter;

    private AfficherParking afficherParking;
    private ServiceParking serviceParking;

    public AjouterParking() {
        serviceParking = new ServiceParking();
    }

    @FXML
    public void initialize() {
        typeComboBox.getItems().addAll("Sous-sol","Pleine air","Couverte");
        // Initialiser le champ "Nombre Actuelles" à 0
        nombreActuellesTextField.setText("0");
        // Rendre le champ "Nombre Actuelles" non modifiable
        nombreActuellesTextField.setEditable(false);
    }

    public void setAfficherParking(AfficherParking afficherParking) {
        this.afficherParking = afficherParking;
    }

    public void ajouterParking() {
        String nom = nomTextField.getText().trim(); // Supprimer les espaces avant et après
        String capaciteText = capaciteTextField.getText();
        String type = typeComboBox.getValue();
        String nombreActuellesText = nombreActuellesTextField.getText();

        // Validation du champ Nom
        if (nom.isEmpty()) {
            afficherMessage("Erreur", "Veuillez saisir un nom pour le parking.");
            return;
        }

        // Validation du champ Capacité
        int capacite;
        try {
            capacite = Integer.parseInt(capaciteText);
            if (capacite <= 0 || capacite > 100) {
                afficherMessage("Erreur", "La capacité doit être un entier entre 1 et 100.");
                return;
            }
        } catch (NumberFormatException e) {
            afficherMessage("Erreur", "Veuillez saisir La capacité .");
            return;
        }

        // Validation du champ Type
        if (type == null) {
            afficherMessage("Erreur", "Veuillez sélectionner un type pour le parking.");
            return;
        }

        // Validation du champ Nombre Actuelles
        int nombreActuelles;
        try {
            nombreActuelles = Integer.parseInt(nombreActuellesText);
            if (nombreActuelles < 0 || nombreActuelles > capacite) {
                afficherMessage("Erreur", "Le nombre actuel de voitures doit être un entier entre 0 et 100.");
                return;
            }
        } catch (NumberFormatException e) {
            afficherMessage("Erreur", "Veuillez saisir Le nombre actuel de voitures .");
            return;
        }

        // Si toutes les validations sont passées, ajoutez le parking
        Parking nouveauParking = new Parking(nom, capacite, type, nombreActuelles);
        try {
            serviceParking.ajouter(nouveauParking);
            afficherMessage("Parking ajouté", "Le parking a été ajouté avec succès.");
            if (afficherParking != null) {
                afficherParking.refreshList();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            afficherMessage("Erreur", "Une erreur est survenue lors de l'ajout du parking.");
        }
    }

    private void afficherMessage(String titre, String contenu) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }





    public void retournerVersAcceuil(javafx.event.ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/BtnParking.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        } }
    @FXML
    void retournerPagePrecedente(ActionEvent actionEvent) {
        // Récupérer la source de l'événement
        Node source = (Node) actionEvent.getSource();
        // Récupérer la scène de la source
        Scene scene = source.getScene();
        // Récupérer la fenêtre parente de la scène
        Stage stage = (Stage) scene.getWindow();
        // Fermer la fenêtre parente pour revenir à la page précédente
        stage.close();
    }
}
