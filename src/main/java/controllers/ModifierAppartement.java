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
import javafx.stage.Stage;
import services.ServiceAppartemment;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class ModifierAppartement {
    @FXML
    private ListView<User> residentListView;



    @FXML
    private TextField id_numAppartement_modifier;

    @FXML
    private TextField id_taille_modifier;

    @FXML
    private ComboBox<String> typeComboBox;

    private final ServiceAppartemment serviceAppartement = new ServiceAppartemment();

    private Appartement appartementSelectionne;

    public void setAppartementSelectionne(Appartement appartement) {
        this.appartementSelectionne = appartement;
        if (appartement != null) {
            // Afficher les données de l'appartement sélectionné dans les champs de texte et de ComboBox

            id_numAppartement_modifier.setText(String.valueOf(appartement.getNumAppartement()));
            id_taille_modifier.setText(appartement.getTaille());
            // Assurez-vous que le statut de l'appartement est sélectionné dans le ComboBox
            typeComboBox.setValue(appartement.getStatutAppartement().toString());
        }

    }
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
    void modifierAppartement(ActionEvent event) {
        User selectedResident = residentListView.getSelectionModel().getSelectedItem(); // Récupérer le résident sélectionné

        if (appartementSelectionne != null) {
            try {
                // Récupérer les valeurs des champs
                String nomResident = selectedResident.getNom();
                String taille = id_taille_modifier.getText().trim();
                String statutValue = typeComboBox.getValue();

                // Vérifier que les champs obligatoires ne sont pas vides
                if (nomResident.isEmpty() || taille.isEmpty() || statutValue == null) {
                    afficherAlerteErreur("Erreur de saisie", "Veuillez remplir tous les champs.");
                    return;
                }

                // Vérifier que le statut est valide
                Appartement.statutAppartement statut;
                try {
                    statut = Appartement.statutAppartement.valueOf(statutValue);
                } catch (IllegalArgumentException e) {
                    afficherAlerteErreur("Erreur de saisie", "Statut d'appartement invalide.");
                    return;
                }

                // Mettre à jour les attributs de l'appartement sélectionné
                appartementSelectionne.setNomResident(nomResident);
                appartementSelectionne.setTaille(taille);
                appartementSelectionne.setStatutAppartement(statut);

                // Appeler la méthode pour modifier l'appartement dans la base de données
                serviceAppartement.modifier(appartementSelectionne);

                // Afficher une alerte d'information pour signaler le succès de la modification
                afficherAlerteInformation("Modification réussie", "L'appartement a été modifié avec succès !");
            } catch (SQLException e) {
                // Afficher une alerte d'erreur pour toute erreur lors de la modification de l'appartement
                afficherAlerteErreur("Erreur SQL", "Une erreur SQL est survenue lors de la modification de l'appartement : " + e.getMessage());
            }
        } else {
            // Afficher une alerte d'erreur si aucun appartement n'est sélectionné
            afficherAlerteErreur("Aucune sélection", "Aucun appartement sélectionné.");
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