package controllers;

import entities.Espace;
import entities.Event;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.IService;
import services.ServiceEspace;
import services.ServiceEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AjouterEspace {

    @FXML private TextField txtNom;
    @FXML private ComboBox<Espace.Etat> cbEtat;
    @FXML private TextField txtCapacite;
    @FXML private TextField txtDescription;

    private Espace espace;
    private final IService<Espace> serviceEspace = new ServiceEspace();

    @FXML
    void initialize() {
        cbEtat.getItems().addAll(Espace.Etat.LIBRE, Espace.Etat.RESERVE);
        cbEtat.setValue(Espace.Etat.LIBRE); // Définir l'état par défaut comme "Libre"
    }

    // Méthode appelée pour initialiser les données de l'espace à modifier
    public void initData(Espace espace) {
        this.espace = espace;
        remplirChamps(espace);
    }

    private void remplirChamps(Espace espace) {
        txtNom.setText(espace.getName());
        cbEtat.setValue(espace.getEtat());
        txtCapacite.setText(String.valueOf(espace.getCapacite()));
        txtDescription.setText(espace.getDescription());
    }

    // ouvrir fenetre afficherespace
    @FXML
    void afficherListeEspaces() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherEspace.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Espaces");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void gererEvenements() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherEvent.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Gérer les événements");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void clearFields() {
        txtNom.clear();
        cbEtat.getSelectionModel().clearSelection();
        txtCapacite.clear();
        txtDescription.clear();
    }

    @FXML
    void ajouterEspace() {
        try {
            // Contrôle de saisie pour le titre
            String nom = txtNom.getText().trim();
            if (nom.isEmpty()) {
                throw new IllegalArgumentException("Le nom de l'espace ne peut pas être vide.");
            }
            if (!nom.matches("[a-zA-Z ]+")) {
                throw new IllegalArgumentException("Le nom doit contenir uniquement des lettres et des espaces.");
            }

            // Contrôle de saisie pour le nombre de personnes
            int capacite = Integer.parseInt(txtCapacite.getText().trim());
            if (capacite <= 0 || capacite > 50) {
                throw new IllegalArgumentException("La capacite de l'espace doit être un entier compris entre 1 et 50.");
            }

            // Contrôle de saisie pour la description
            String description = txtDescription.getText().trim();
            if (description.isEmpty()) {
                throw new IllegalArgumentException("La description ne peut pas être vide.");
            }

            // Contrôle de saisie pour l'état
            if (cbEtat.getValue() == null) {
                throw new IllegalArgumentException("Veuillez sélectionner un état pour l'espace.");
            }

            // Créer l'objet Espace et l'ajouter
            Espace nouvelEspace = new Espace(nom, cbEtat.getValue(), capacite, description);
            serviceEspace.ajouter(nouvelEspace);

            clearFields(); // Efface les champs après l'ajout

            afficherAlerteConfirmationEvent("Validation", "Espace ajouté avec succès");
        } catch (NumberFormatException e) {
            afficherAlerteErreurEvent("Erreur de format", "Veuillez saisir un nombre valide pour la capacité.");
        } catch (IllegalArgumentException e) {
            afficherAlerteErreurEvent("Erreur de saisie", e.getMessage());
        } catch (SQLException e) {
            afficherAlerteErreurEvent("Erreur SQL", "Erreur lors de l'ajout de l'espace : " + e.getMessage());
        } catch (Exception e) {
            afficherAlerteErreurEvent("Erreur", e.getMessage());
        }
    }



    private void afficherAlerteConfirmationEvent(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void retournerVersAcceuil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/BtnReservation.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void afficherAlerteErreurEvent(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }
}