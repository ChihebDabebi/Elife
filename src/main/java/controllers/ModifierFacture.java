package controllers;

import entities.Facture;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.ServiceFacture;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class ModifierFacture {
    @FXML
    private TextField id_consommation_modifier;


    @FXML
    private TextField id_numFacture_modifier;

    @FXML
    private TextField id_montant_modifier;

    @FXML
    private DatePicker id_date_modifier;

    @FXML
    private TextField id_description_modifier;

    @FXML
    private ComboBox<String> typeComboBox;

    private final ServiceFacture serviceFacture = new ServiceFacture();
    private Facture selectedFacture;

    public void setFactureSelectionne(Facture facture) {
        this.selectedFacture = facture;
        if (facture != null) {
            id_numFacture_modifier.setText(String.valueOf(facture.getNumFacture()));
            id_montant_modifier.setText(String.valueOf(facture.getMontant()));
            id_consommation_modifier.setText(String.valueOf(facture.getNumFacture()));

            Date sqlDate = (Date) facture.getDate();
            LocalDate localDate = sqlDate.toLocalDate();
            id_date_modifier.setValue(localDate);
            id_description_modifier.setText(facture.getDescriptionFacture());
            typeComboBox.setValue(facture.getType().toString());

        }
    }

    @FXML
    void modifierFacture(ActionEvent event) {
        if (selectedFacture != null) {
            try {
                int numFacture = Integer.parseInt(id_numFacture_modifier.getText());
                float montant = Float.parseFloat(id_montant_modifier.getText());
                LocalDate date = id_date_modifier.getValue();
                String description = id_description_modifier.getText();
                Facture.Type type = Facture.Type.valueOf(typeComboBox.getValue());
                float consommation = Float.parseFloat(id_consommation_modifier.getText());

                // Vérifier que les champs obligatoires ne sont pas vides
                if (numFacture == 0 || montant == 0 || date == null || description.isEmpty() ) {
                    afficherAlerteErreur("Erreur de saisie", "Veuillez remplir tous les champs.");
                    return;
                }

                // Vérifier la validité des données
                if (numFacture <= 0 || montant <= 0) {
                    afficherAlerteErreur("Erreur de saisie", "Veuillez saisir des valeurs numériques positives pour les champs numériques.");
                    return;
                }

                // Mettre à jour la facture
                selectedFacture.setNumFacture(numFacture);
                selectedFacture.setMontant(montant);
                selectedFacture.setDate(Date.valueOf(date));
                selectedFacture.setDescriptionFacture(description);
                selectedFacture.setType(type);
                selectedFacture.setConsomation(consommation);

                // Modifier la facture dans la base de données
                serviceFacture.modifier(selectedFacture);

                // Afficher une alerte d'information pour signaler le succès de la modification
                afficherAlerteInformation("Modification réussie", "La facture a été modifiée avec succès !");

            } catch (NumberFormatException e) {
                // Afficher une alerte d'erreur pour les valeurs numériques invalides
                afficherAlerteErreur("Erreur de saisie", "Veuillez saisir des valeurs valides pour les champs numériques !");
            } catch (SQLException e) {
                // Afficher une alerte d'erreur pour toute erreur lors de la modification de la facture
                afficherAlerteErreur("Erreur de modification", "Une erreur est survenue lors de la modification de la facture : " + e.getMessage());
            }
        } else {
            // Afficher une alerte d'erreur si aucune facture n'est sélectionnée
            afficherAlerteErreur("Aucune facture sélectionnée", "Veuillez sélectionner une facture à modifier.");
        }
    }

    // Méthode pour afficher une alerte d'erreur
    private void afficherAlerteErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode pour afficher une alerte d'information
    private void afficherAlerteInformation(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
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

