package controllers;

import entities.Appartement;
import entities.User;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import services.ServiceAppartemment;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class AjouterAppartement {
    @FXML
    private ListView<User> residentListView;

    @FXML
    private TextField nbrEtageField;


    @FXML
    private TextField numAppartementField;

    @FXML
    private ComboBox<?> statutComboBox; // Modifié le type de ComboBox

    @FXML
    private TextField tailleField;

    @FXML
    private Text title;

    private final ServiceAppartemment serviceAppartement = new ServiceAppartemment();
    @FXML
    void initialize() {
        // Initialize residentListView with the list of residents
        try {
            List<User> residents = serviceAppartement.getAllResidents();
            residentListView.setItems(FXCollections.observableArrayList(residents));
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle SQLException appropriately
        }
    }

    @FXML
    void AjouterAppartement(ActionEvent event) {

        try {
            User selectedResident = residentListView.getSelectionModel().getSelectedItem(); // Récupérer le résident sélectionné
            System.out.println(selectedResident);
            if (selectedResident != null) { // Vérifier si un résident est sélectionné
                int numAppartement = Integer.parseInt(numAppartementField.getText());

                String taille = tailleField.getText();
                int nbrEtage = Integer.parseInt(nbrEtageField.getText());
                String statut = (String) statutComboBox.getSelectionModel().getSelectedItem();

                // Créer un nouvel objet Appartement avec les valeurs récupérées
                Appartement appartement = new Appartement();
                appartement.setNumAppartement(numAppartement);
                appartement.setNomResident(selectedResident.getNom());
                appartement.setTaille(taille);
                appartement.setNbrEtage(nbrEtage);
                appartement.setStatutAppartement(Appartement.statutAppartement.valueOf(statut));
                System.out.println(selectedResident.getId());
                if (appartement.getUser() == null) {
                    appartement.setUser(selectedResident);
                }
                appartement.getUser().setId(selectedResident.getId());
                // Appeler la méthode d'ajout d'appartement du service approprié
                serviceAppartement.ajouter(appartement);

                // Afficher une confirmation à l'utilisateur
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Confirmation");

                alert.setContentText("Appartement ajoute avec succes");

                alert.showAndWait();

                // Effacer les champs après l'ajout réussi
                effacerChamps();
            } else {
                // Afficher un message si aucun résident n'est sélectionné
                afficherAlerteErreur("Aucun résident sélectionné", "Veuillez sélectionner un résident.");
            }
        } catch (NumberFormatException e) {
            // Gérer les erreurs de format numérique
            afficherAlerteErreur("Erreur de format", "Veuillez entrer des valeurs numériques valides.");
        } catch (Exception e) {
            e.printStackTrace(); // Ajoutez cette ligne pour imprimer la trace de l'exception
            afficherAlerteErreur("Erreur", "Une erreur s'est produite lors de l'ajout de l'appartement : " + e.getMessage());
        }
    }

    // Méthode utilitaire pour effacer les champs après l'ajout réussi
    private void effacerChamps() {
        numAppartementField.clear();
        tailleField.clear();
        nbrEtageField.clear();
        statutComboBox.getSelectionModel().clearSelection();
    }

    // Méthode utilitaire pour afficher une alerte d'erreur
    private void afficherAlerteErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
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
}