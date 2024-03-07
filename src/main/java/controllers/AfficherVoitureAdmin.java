package controllers;

import entities.Voiture;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.input.MouseButton;
import services.ServiceVoiture;

import javafx.event.ActionEvent;
import java.io.IOException;
import java.sql.SQLException;

public class AfficherVoitureAdmin {

    @FXML
    private TextField textFieldMarque;

    @FXML
    private TextField textFieldModele;

    @FXML
    private TextField textFieldCouleur;

    @FXML
    private TextField textFieldMatricule;

    private Voiture voiture;
    private ServiceVoiture serviceVoiture;


    @FXML
    private ListView<Voiture> listeVoitures;

    @FXML
    private Button boutonGererParking;

    @FXML
    private TextField searchField;

    private ObservableList<Voiture> voituresObservableList;

    private static AfficherVoitureAdmin instance;

    public static AfficherVoitureAdmin getInstance() {
        if (instance == null) {
            instance = new AfficherVoitureAdmin();
        }
        return instance;
    }
    public void initData(Voiture voiture, AfficherVoitureAdmin afficherVoitureAdmin) {
        this.voiture = voiture;
        textFieldMarque.setText(voiture.getMarque());
        textFieldModele.setText(voiture.getModel());
        textFieldCouleur.setText(voiture.getCouleur());
        textFieldMatricule.setText(voiture.getMatricule());

        serviceVoiture = new ServiceVoiture();
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
        } else if (!marque.matches("[a-zA-Z]{1,20}")) {
            afficherErreur("Erreur", "La marque doit contenir uniquement des lettres et avoir au maximum 20 caractères.");
            return false;
        }
        return true;
    }

    private boolean validerModele(String modele) {
        if (modele.isEmpty()) {
            afficherErreur("Erreur", "Veuillez saisir un modèle pour la voiture.");
            return false;
        } else if (!modele.matches("[a-zA-Z0-9]{1,20}")) {
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
                        // Réinitialiser les champs
                        clearFields();
                        // Fermer la fenêtre de détails de voiture
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
    private void clearFields() {
        // Vider les champs
        // Supposons que textFieldMarque, textFieldModele, etc., sont les champs que vous souhaitez vider
        textFieldMarque.setText("");
        textFieldModele.setText("");
        // Répétez pour les autres champs
    }

    public AfficherVoitureAdmin() {
        serviceVoiture = new ServiceVoiture();
        voituresObservableList = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        try {
            voituresObservableList.addAll(serviceVoiture.getAll());
            listeVoitures.setItems(voituresObservableList);
            // Ajout d'un écouteur d'événements pour détecter les modifications dans le champ de recherche
            searchField.textProperty().addListener((observable, oldValue, newValue) -> rechercher());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        boutonGererParking.setOnAction(this::ouvrirAfficherParkingAdmin);

        // Gestionnaire d'événements pour le double-clic sur un élément de la ListView
        listeVoitures.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
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
    void rechercher() {
        String recherche = searchField.getText().toLowerCase();
        ObservableList<Voiture> voituresFiltrees = FXCollections.observableArrayList();
        for (Voiture voiture : voituresObservableList) {
            if (voiture.getMarque().toLowerCase().contains(recherche)) {
                voituresFiltrees.add(voiture);
            }
        }
        listeVoitures.setItems(voituresFiltrees);
    }

    @FXML
    void ouvrirAfficherParkingAdmin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherParking.fxml"));
            Parent root = loader.load();
            AfficherParking controller = loader.getController();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Gérer les parkings");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void refreshList() {
        try {
            voituresObservableList.clear();
            voituresObservableList.addAll(serviceVoiture.getAll());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
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

}
