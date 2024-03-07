package controllers;

import com.itextpdf.text.pdf.GrayColor;
import entities.Appartement;
import entities.Facture;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.ServiceFacture;

import java.time.LocalDate;
import java.util.*;

import java.io.IOException;
import java.sql.SQLException;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

public class AfficherFacture {
    private ObservableList<Facture> factureList; // Correction 1
    private FilteredList<Facture> filteredFactureList;
    private final ServiceFacture serviceFacture = new ServiceFacture();
    @FXML
    private TextField searchTF;

    private Facture factureSelectionnee ;
    private Appartement appartementSelectionne;
    @FXML
    private ListView<Facture> listViewFacture;
    @FXML
    private Button boutonPDF;
    @FXML
    private ComboBox typeFactureComboBox ;


    public void initData(Appartement appartement) throws SQLException {
        if (appartement == null) {
            System.out.println("L'objet appartementSelectionne est null !");
        }
        this.appartementSelectionne = appartement;
        System.out.println("Appartement sélectionné : " + appartementSelectionne);
        initialize();
    }

    void afficherFactures() throws SQLException {
        // Obtenez les factures pour l'appartement sélectionné
        if (appartementSelectionne == null) {
            System.out.println("L'appartement sélectionné est null !");
            return;
        }
        Set<Facture> factures = serviceFacture.getAllForAppartement(appartementSelectionne);
        ObservableList<Facture> observableList = FXCollections.observableArrayList(factures);
        listViewFacture.setItems(observableList); // Correction 3
        factureList = FXCollections.observableArrayList(factures); // Correction 1
        listViewFacture.refresh();
    }


    @FXML
    void initialize() {
        try {
            afficherFactures(); // Actualiser la table après chaque modification
            // Add a ChangeListener to the ListView's selection model
            listViewFacture.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Facture>() {
                @Override
                public void changed(ObservableValue<? extends Facture> observable, Facture oldValue, Facture newValue) {
                    if (newValue != null) {
                        System.out.println("Facture selected: " + newValue);
                    }
                }
            });
            typeFactureComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    if (newValue.equals("Numéro")) {
                        trierParNom();
                    } else if (newValue.equals("Date")) {
                        trierParDate();
                    }
                }
            });
            searchTF.textProperty().addListener((observable, oldValue, newValue) -> {
                searchFacture(newValue);
            });


        } catch (SQLException e) {
            e.printStackTrace(); // Gérer SQLException de manière appropriée
        }
        listViewFacture.refresh();
    }
    private void trierParNom() {
        ObservableList<Facture> factures = listViewFacture.getItems();
        factures.sort(Comparator.comparingInt(Facture::getNumFacture));
        listViewFacture.setItems(factures);
    }

    private void trierParDate() {
        ObservableList<Facture> factures = listViewFacture.getItems();
        factures.sort(Comparator.comparing(Facture::getDate));
        listViewFacture.setItems(factures);
    }



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



    @FXML
    void supprimerFacture() {
        factureSelectionnee = listViewFacture.getSelectionModel().getSelectedItem();
        if (factureSelectionnee != null) {
            try {
                System.out.println("Facture sélectionnée : " + factureSelectionnee); // Check the selected invoice
                System.out.println("ID de la facture sélectionnée : " + factureSelectionnee.getIdFacture());

                int idFacture = factureSelectionnee.getIdFacture();
                System.out.println("ID de la facture à supprimer : " + idFacture); // Check the ID of the invoice
                // Add more debug statements as needed...

                serviceFacture.supprimer(idFacture);
                System.out.println("Facture supprimée avec succès !");

                listViewFacture.getItems().remove(factureSelectionnee);

                afficherAlerteErreur("Suppression réussie", "La facture a été supprimée avec succès.");
            } catch (SQLException e) {
                System.out.println("Erreur SQL : " + e.getMessage());
                afficherAlerteErreur("Erreur de suppression", "Impossible de supprimer la facture : des contraintes de clé étrangère sont violées.");
            } catch (Exception e) {
                System.out.println("Erreur : " + e.getMessage());
                afficherAlerteErreur("Erreur", "Une erreur s'est produite lors de la suppression de la facture : " + e.getMessage());
            }
        } else {
            System.out.println("Aucune facture sélectionnée.");
            afficherAlerteErreur("Sélection requise", "Veuillez sélectionner une facture à supprimer.");
        }
        listViewFacture.refresh();
    }

    @FXML
    void modifierFacture(ActionEvent actionEvent) {
        Facture factureSelectionne = listViewFacture.getSelectionModel().getSelectedItem();
        System.out.println("Facture sélectionnée pour modification : " + factureSelectionne);
        if (factureSelectionne != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierFacture.fxml"));
                Parent root = loader.load();
                System.out.println("FXML file loaded successfully.");
                ModifierFacture controller = loader.getController();
                System.out.println("Controller initialized.");

                controller.setFactureSelectionne(factureSelectionne);
                System.out.println("Data initialized in controller.");

                Stage stage = new Stage();
                stage.setTitle("Liste des Factures");
                stage.setScene(new Scene(root));

                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        listViewFacture.refresh();
    }

    @FXML
    public void ajouterFacture(ActionEvent actionEvent) {
        if (appartementSelectionne != null) {
            System.out.println("Appartement selected: " + appartementSelectionne);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterFacture.fxml"));
                Parent root = loader.load();

                // Get the controller and pass the selected Appartement
                AjouterFacture controller = loader.getController();
                controller.setAppartementSelectionne(appartementSelectionne);
                // Create a new stage
                Stage stage = new Stage();
                stage.setTitle("Ajouter Facture");
                stage.setScene(new Scene(root));

                // Show the new stage
                System.out.println("Showing new stage...");
                stage.show();
                System.out.println("New stage should be visible now.");
            } catch (IOException e) {
                System.out.println("IOException occurred:");
                e.printStackTrace();
            }
        } else {
            System.out.println("No Appartement selected.");
        }
        listViewFacture.refresh();
    }


    private void afficherAlerteErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void searchFacture(String searchText) {
        List<Facture> searchResult = factureList.stream()
                .filter(facture -> {
                    String numFactureString = String.valueOf(facture.getNumFacture());
                    return numFactureString.contains(searchText.toLowerCase());
                })
                .collect(Collectors.toList());

        filteredFactureList = new FilteredList<>(FXCollections.observableArrayList(searchResult));
        listViewFacture.setItems(filteredFactureList);
    }


    @FXML
    void actualiser() {
        try {
            listViewFacture.refresh();
            afficherFactures(); // Actualiser la liste des factures depuis la base de données
            initialize(); // Réinitialiser l'interface utilisateur
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }





}