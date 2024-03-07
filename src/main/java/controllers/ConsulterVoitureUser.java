package controllers;

import entities.User;
import entities.Voiture;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.ServiceVoiture;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class ConsulterVoitureUser {
    @FXML
    private TextField textFieldMarque;

    @FXML
    private TextField textFieldModele;

    @FXML
    private TextField textFieldCouleur;

    @FXML
    private TextField textFieldMatricule;


    @FXML
    private ListView<Voiture> voituresListView;

    private User currentUser;

    private Voiture voiture;
    private ServiceVoiture serviceVoiture;



    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        System.out.println(currentUser);
        afficherVoituresUtilisateur();
    }

    private void afficherVoituresUtilisateur() {
        try {
            if (currentUser != null) {

                ServiceVoiture serviceVoiture = new ServiceVoiture();
                System.out.println(currentUser);
                Set<Voiture> voitures = serviceVoiture.getAllByUserId(currentUser.getId());
                System.out.println(voitures);
                ObservableList<Voiture> observableVoitures = FXCollections.observableArrayList(voitures);
                voituresListView.setItems(observableVoitures);

            } else {
                System.out.println(currentUser);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
        // Initialisez l'objet serviceVoiture
        serviceVoiture = new ServiceVoiture();

        // Gestionnaire d'événements pour le double-clic sur un élément de la ListView
        voituresListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                voiture = newValue; // Mettre à jour la référence à la voiture sélectionnée
                // Remplir les champs avec les données de la voiture sélectionnée
                textFieldMarque.setText(voiture.getMarque());
                textFieldModele.setText(voiture.getModel());
                textFieldCouleur.setText(voiture.getCouleur());
                textFieldMatricule.setText(voiture.getMatricule());
            }
        });
    }


    @FXML
    private void modifierVoiture(ActionEvent event) {
        if (voiture != null) {
            String nouvelleMarque = textFieldMarque.getText().trim();
            String nouveauModele = textFieldModele.getText().trim();
            String nouvelleCouleur = textFieldCouleur.getText().trim();
            String nouveauMatricule = textFieldMatricule.getText().trim();
            if (validerChamps(nouvelleMarque, nouveauModele, nouvelleCouleur, nouveauMatricule)) {
                // Créer une boîte de dialogue de confirmation
                Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
                confirmationDialog.setTitle("Confirmation");
                confirmationDialog.setHeaderText("Modifier la voiture ?");
                confirmationDialog.setContentText("Êtes-vous sûr de vouloir modifier cette voiture ?");

                // Afficher la boîte de dialogue et attendre la réponse de l'utilisateur
                confirmationDialog.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        // L'utilisateur a cliqué sur OK, modifier la voiture
                        voiture.setMarque(nouvelleMarque);
                        voiture.setModel(nouveauModele);
                        voiture.setCouleur(nouvelleCouleur);
                        voiture.setMatricule(nouveauMatricule);

                        try {
                            serviceVoiture.modifier(voiture);
                            refreshList();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // L'utilisateur a annulé, ne rien faire
                    }
                });
            }
        }
    }
    private boolean validerChamps(String marque, String modele, String couleur, String matricule) {
        return validerMarque(marque) && validerModele(modele) && validerCouleur(couleur) && validerMatricule(matricule);
    }
    private boolean validerMarque(String marque) {
        if (marque.isEmpty()) {
            afficherErreur("Erreur", "Veuillez saisir une marque pour la voiture.");
            return false;
        } else if (!marque.matches("[a-zA-Z ]{1,20}")) {
            afficherErreur("Erreur", "La marque doit contenir uniquement des lettres et avoir au maximum 20 caractères.");
            return false;
        }
        return true;
    }

    private boolean validerModele(String modele) {
        if (modele.isEmpty()) {
            afficherErreur("Erreur", "Veuillez saisir un modèle pour la voiture.");
            return false;
        } else if (!modele.matches("[a-zA-Z0-9 ]{1,20}")) {
            afficherErreur("Erreur", "Le modèle doit contenir des lettres et des chiffres et avoir au maximum 20 caractères.");
            return false;
        }
        return true;
    }

    private boolean validerCouleur(String couleur) {
        if (couleur.isEmpty()) {
            afficherErreur("Erreur", "Veuillez saisir une couleur pour la voiture.");
            return false;
        } else if (!couleur.matches("[a-zA-Z]{1,20}")) {
            afficherErreur("Erreur", "La couleur doit contenir uniquement des lettres et avoir au maximum 20 caractères.");
            return false;
        }
        return true;
    }

    private boolean validerMatricule(String matricule) {
        if (matricule.isEmpty()) {
            afficherErreur("Erreur", "Veuillez saisir un matricule pour la voiture.");
            return false;
        } else if (!matricule.matches("[a-zA-Z0-9]{6,15}")) {
            afficherErreur("Erreur", "Le matricule doit contenir des lettres et des chiffres et avoir entre 6 et 15 caractères.");
            return false;
        }
        return true;
    }

    private void afficherErreur(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
    @FXML
    private void supprimerVoiture(ActionEvent event) {
        if (voiture != null) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmation de suppression");
            confirmationAlert.setHeaderText(null);
            confirmationAlert.setContentText("Voulez-vous vraiment supprimer cette voiture ?");

            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        serviceVoiture.supprimer(voiture.getIdVoiture());
                        refreshList();
                        // Réinitialiser les champs après la suppression
                        resetFields();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
    private void resetFields() {
        // Réinitialiser les champs en les vidant
        // Par exemple :
        textFieldMarque.clear();
        textFieldModele.clear();
        textFieldCouleur.clear();
        textFieldMatricule.clear();
        // Assurez-vous de réinitialiser tous les autres champs si nécessaire
    }


    /*public void retournerVersAcceuil(javafx.event.ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterVoiture.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            ConsulterVoitureUser controller = loader.getController();
            System.out.println(currentUser);
            controller.setCurrentUser(currentUser);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     */
    @FXML
    private void retournerVersAcceuil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterVoiture.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void refreshList() {
        try {
            if (currentUser != null) {
                ServiceVoiture serviceVoiture = new ServiceVoiture();
                Set<Voiture> voitures = serviceVoiture.getAllByUserId(currentUser.getId());
                ObservableList<Voiture> observableVoitures = FXCollections.observableArrayList(voitures);
                voituresListView.setItems(observableVoitures);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du rafraîchissement de la liste des voitures", e);
        }
    }




}
