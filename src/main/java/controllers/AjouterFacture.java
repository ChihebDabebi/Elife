package controllers;

import entities.Appartement;
import entities.Facture;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import services.ServiceAppartemment;
import services.ServiceFacture;
import java.sql.Date;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class AjouterFacture {

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField montantField;

    @FXML
    private TextField NumFacture;

    @FXML
    private TextField consommationField;
    @FXML
    private Text title;

    @FXML
    private ComboBox<String> typeComboBox;
    ServiceAppartemment serviceAppartemment = new ServiceAppartemment();

    private final ServiceFacture PS = new ServiceFacture();
    private Appartement appartementSelectionne;
    public void setAppartementSelectionne(Appartement appartement) {
        this.appartementSelectionne = appartement;
    }
    @FXML
    void Ajouter(ActionEvent event ) {
        try {
            LocalDate date = datePicker.getValue();
            if (date == null) {
                throw new IllegalArgumentException("Please select a date.");
            }

            // Vérifier que les autres champs sont remplis
            if (NumFacture.getText().isEmpty()) {
                throw new IllegalArgumentException("NumFacture is null or empty");
            }

            int Numfact = Integer.parseInt(NumFacture.getText());
            String typeString = typeComboBox.getValue();
            Facture.Type type = Facture.Type.valueOf(typeString);
            float montant = Float.parseFloat(montantField.getText());
            float consommation = Float.parseFloat(consommationField.getText());

            String description = descriptionField.getText();
            if (Numfact <= 0) {
                throw new IllegalArgumentException("NumFacture doit être un nombre entier positif.");
            }

            // Vérifier si Montant est un nombre décimal positif
            if (montant <= 0) {
                throw new IllegalArgumentException("Montant doit être un nombre décimal positif.");
            }
            // Récupérer l'idAppartement correspondant au numAppartement de la facture

            if (appartementSelectionne == null) {
                throw new IllegalArgumentException("No apartment selected.");
            }
            // Créer l'objet Facture
            Facture facture = new Facture(Numfact, Date.valueOf(date), type, montant, description, consommation, appartementSelectionne);

            // Appeler la méthode pour ajouter la facture
            PS.ajouter(facture);

            // Afficher une confirmation à l'utilisateur
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Validation");
            alert.setContentText("Facture added successfully");
            alert.showAndWait();
        } catch (NumberFormatException e) {
            // Gérer l'erreur pour le format de numéro invalide
            afficherAlerteErreur("Format Error", "Please enter a valid number.");
        } catch (IllegalArgumentException e) {
            // Gérer l'erreur pour la date ou NumFacture nulle
            afficherAlerteErreur("Input Error", e.getMessage());
        } catch (SQLException e) {
            // Gérer l'exception SQL
            afficherAlerteErreur("SQL Exception", e.getMessage());
        } catch (Exception e) {
            // Gérer les autres exceptions
            afficherAlerteErreur("Error", e.getMessage());
        }

    }
    // Méthode pour récupérer l'identifiant d'un appartement en fonction de son numéro




    private void afficherAlerteErreur(String error, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(error);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public   @FXML
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
