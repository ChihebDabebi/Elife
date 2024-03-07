package controllers;

import entities.Parking;
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
import services.ServiceParking;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
// import java.util.Comparator;

public class AfficherParking {


    @FXML
    private TextField textFieldNom;

    @FXML
    private TextField textFieldCapacite;

    @FXML
    private ComboBox<String> comboBoxType;

    @FXML
    private TextField textFieldNombreActuelles;

    private AfficherParking afficherParking;
    private Parking parking;
    private ServiceParking serviceParking;
    @FXML
    private ListView<Parking> listeParkings;

    @FXML
    private Button boutonAjouter;

    @FXML
    private Button boutonGererVoitures;

    @FXML
    private TextField searchField;
    @FXML
    private Button boutonTriCapacite;

    private boolean triAscendant = true; // Par défaut, tri ascendant

    public void setAfficherParking(AfficherParking afficherParking) {
        this.afficherParking = afficherParking;
    }

    private ObservableList<Parking> parkingsObservableList;


    @FXML
    public void initialize() {
        try {
            // Initialisation de la liste des parkings...
            parkingsObservableList.addAll(serviceParking.getAll());
            listeParkings.setItems(parkingsObservableList);
            chargerTypes();
            this.afficherParking = this; // Initialisation de afficherParking




            listeParkings.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    parking = newValue; // Mettre à jour la référence au parking sélectionné
                    // Remplir les champs avec les données du parking sélectionné
                    textFieldNom.setText(parking.getNom());
                    textFieldCapacite.setText(Integer.toString(parking.getCapacite()));
                    textFieldNombreActuelles.setText(Integer.toString(parking.getNombreActuelles()));
                    // Sélectionner le type du parking actuel dans la ComboBox
                    comboBoxType.setValue(parking.getType());
                }
            });


            // Ajout d'un écouteur d'événements pour détecter les modifications dans le champ de recherche
            searchField.textProperty().addListener((observable, oldValue, newValue) -> rechercher());
          /*  // Initialiser le nombre actuel de voitures à 0 pour chaque parking
            for (Parking parking : parkingsObservableList) {
                parking.setNombreActuelles(0);
            }

           */
          /*  // Tri des parkings par capacité
            parkingsObservableList.sort(Comparator.comparingInt(Parking::getCapacite));

            listeParkings.setItems(parkingsObservableList);

           */
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        boutonAjouter.setOnAction(event -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterParking.fxml"));
            try {
                Parent root = loader.load();
                AjouterParking controller = loader.getController();
                controller.setAfficherParking(this);
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Ajouter un parking");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        boutonGererVoitures.setOnAction(event -> {
            ouvrirAffichervoiture();
        });

        // Gestionnaire d'événements pour le double-clic sur un élément de la ListView
    }

    public void initData(Parking parking) {
        this.parking = parking;
        // Remplir les champs avec les détails du parking passé en paramètre
        textFieldNom.setText(parking.getNom());
        textFieldCapacite.setText(Integer.toString(parking.getCapacite()));
        textFieldNombreActuelles.setText(Integer.toString(parking.getNombreActuelles()));

        // Charger les types disponibles dans la ComboBox
        chargerTypes();

        // Sélectionner le type du parking actuel dans la ComboBox
        comboBoxType.setValue(parking.getType());

        serviceParking = new ServiceParking(); // Initialisation du serviceParking
    }
    public AfficherParking() {
        serviceParking = new ServiceParking();
        parkingsObservableList = FXCollections.observableArrayList();
    }
    @FXML
    private void modifierParking() {
        if (parking != null) {
            try {
                String nom = textFieldNom.getText().trim();
                String capaciteText = textFieldCapacite.getText();
                String type = comboBoxType.getValue();
                String nombreActuellesText = textFieldNombreActuelles.getText();

                // Validation des champs
                if (nom.isEmpty()) {
                    afficherMessage("Erreur", "Veuillez entrer un nom pour le parking.");
                    return;
                }

                int capacite;
                try {
                    capacite = Integer.parseInt(capaciteText);
                    if (capacite <= 0 || capacite > 100) {
                        afficherMessage("Erreur", "La capacité doit être un entier entre 1 et 100.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    afficherMessage("Erreur", "Veuillez saisir une capacité valide.");
                    return;
                }

                if (type == null) {
                    afficherMessage("Erreur", "Veuillez sélectionner un type de parking.");
                    return;
                }

                int nombreActuelles;
                try {
                    nombreActuelles = Integer.parseInt(nombreActuellesText);
                    if (nombreActuelles < 0 || nombreActuelles > 100) {
                        afficherMessage("Erreur", "Le nombre actuel de voitures doit être un entier entre 0 et 100.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    afficherMessage("Erreur", "Veuillez saisir un nombre actuel de voitures valide.");
                    return;
                }

                // Mettre à jour les détails du parking avec les valeurs des champs de texte
                parking.setNom(nom);
                parking.setCapacite(capacite);
                parking.setType(type);
                parking.setNombreActuelles(nombreActuelles);

                // Appeler le service pour modifier le parking dans la base de données
                serviceParking.modifier(parking);

                // Actualiser la liste dans la vue principale
                afficherParking.refreshList();

               /* // Fermer la fenêtre de modification
                Stage stage = (Stage) textFieldNom.getScene().getWindow();
                stage.close();

                */

            } catch (SQLException e) {
                e.printStackTrace();
                // Gérer l'erreur de modification
            }
        } else {
            afficherMessage("Erreur", "Veuillez sélectionner un parking à modifier.");
        }
    }

    @FXML
    private void supprimerParking() {
        if (parking != null) {
            try {
                serviceParking.supprimer(parking.getIdParking());
                afficherParking.refreshList();
                // Réinitialiser les champs
                clearFields();
                /*Stage stage = (Stage) textFieldNom.getScene().getWindow();
                stage.close();

                 */
            } catch (SQLException e) {
                e.printStackTrace();
                // Gérer l'erreur de suppression
            }
        }
    }
    private void clearFields() {
        // Vider les champs
        textFieldNom.setText("");
        textFieldCapacite.setText("");
        comboBoxType.getSelectionModel().clearSelection();
        textFieldNombreActuelles.setText("");
    }

    private void chargerTypes() {
        // Vous pouvez obtenir les types à partir de votre source de données, ici je les ai définis manuellement
        List<String> types = Arrays.asList("sous-sol", "pleine air", "couverte");

        ObservableList<String> typeOptions = FXCollections.observableArrayList(types);
        comboBoxType.setItems(typeOptions);
    }
    private void afficherMessage(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }


    @FXML
    void ouvrirAffichervoiture() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherVoitureAdmin.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des voitures");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void rechercher() {
        String recherche = searchField.getText().toLowerCase();
        ObservableList<Parking> parkingsFiltres = FXCollections.observableArrayList();
        for (Parking parking : parkingsObservableList) {
            if (parking.getNom().toLowerCase().contains(recherche)) {
                parkingsFiltres.add(parking);
            }
        }
        listeParkings.setItems(parkingsFiltres);
    }




    public void refreshList() {
        try {
            parkingsObservableList.clear();
            parkingsObservableList.addAll(serviceParking.getAll());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    @FXML
    void trierParCapacite() {
        // Changer le sens du tri
        triAscendant = !triAscendant;

        // Changer le texte du bouton en fonction du sens du tri
        boutonTriCapacite.setText(triAscendant ? "Tri Capacité ▲" : "Tri Capacité ▼");

        // Tri des parkings par capacité
        parkingsObservableList.sort(triAscendant ? Comparator.comparingInt(Parking::getCapacite) : Comparator.comparingInt(Parking::getCapacite).reversed());

        // Mettre à jour la ListView
        listeParkings.setItems(parkingsObservableList);
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
