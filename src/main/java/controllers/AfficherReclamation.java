package controllers;


import entities.Reclamation;
import entities.Reponse;
import entities.User;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import services.ServiceReclamation;
import services.ServiceReponse;
import services.ServiceUser;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AfficherReclamation {

    private final ServiceReclamation rs = new ServiceReclamation();
    private final ServiceUser su = new ServiceUser();
    private Stage stage; // Reference to the stage

    @FXML
    private TextField CategorieRecTF;


    @FXML
    private ListView<Reclamation> listViewRec;

    @FXML
    private TextField descriRecTF;
    @FXML
    private TextField searchTextField;
    @FXML
    private Button ajouterButton;

    @FXML
    private Button modifierButton;

    @FXML
    private Button supprimerButton;


    @FXML
    private Button consulterReponseButton;

    public void initialize() {
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchReclamations();// Appeler la méthode rechercherParNom lorsque le texte dans le champ de recherche change
        });
        showReclamations();
    }
    private User currentUser;

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void showReclamations() {
        try {
            // Assuming getAll() returns a List<Reclamation>
            List<Reclamation> reclamations = rs.getAll();

            if (reclamations != null && !reclamations.isEmpty()) {
                ObservableList<Reclamation> observableReclamations = FXCollections.observableArrayList(reclamations);
                listViewRec.setItems(observableReclamations);
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Information", "No reclamations found.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve reclamations: " + e.getMessage());
        }
    }


    private void searchReclamations() {
        String nomRecherche = searchTextField.getText().trim().toLowerCase();

        if (!nomRecherche.isEmpty()) {
            // Créer un prédicat pour filtrer les espaces dont le nom contient la chaîne de recherche
                Predicate<Reclamation> nomPredicate = reclamation -> reclamation.getCategorieRec().toLowerCase().contains(nomRecherche);

            // Créer un FilteredList avec le prédicat
            FilteredList<Reclamation> filteredList = new FilteredList<>(listViewRec.getItems(), nomPredicate);

            // Mettre à jour la liste affichée dans la ListView avec le FilteredList filtré
            listViewRec.setItems(filteredList);
        }
    }



    @FXML
    void ajouter(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/AjouterReclamation.fxml")); // Assurez-vous que le chemin est correct
        Parent ajoutReclamationView = loader.load();

        // Scène pour l'ajout de réclamation
        Scene ajoutReclamationScene = new Scene(ajoutReclamationView);

        // Obtention de la scène actuelle (stage) à partir du bouton
        Stage window = (Stage) ajouterButton.getScene().getWindow();

        // Changement de la scène actuelle pour afficher la vue d'ajout de réclamation
        window.setScene(ajoutReclamationScene);
        window.show();
    }


    @FXML
    void modifier(ActionEvent event) {
        Reclamation selectedReclamation = listViewRec.getSelectionModel().getSelectedItem();
        if (selectedReclamation != null) {
            selectedReclamation.setCategorieRec(CategorieRecTF.getText());
            selectedReclamation.setDescriRec(descriRecTF.getText());

            try {
                rs.modifier(selectedReclamation);
                showReclamations();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Reclamation modified successfully.");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to modify reclamation:\n" + e.getMessage());
            }
        }
    }

    @FXML
    void supprimer(ActionEvent event) {
        Reclamation selectedReclamation = listViewRec.getSelectionModel().getSelectedItem();
        if (selectedReclamation != null) {
            try {
                rs.supprimer(selectedReclamation.getIdRec());
                listViewRec.getItems().remove(selectedReclamation);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Reclamation deleted successfully.");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete reclamation:\n" + e.getMessage());
            }
        }
    }

    @FXML
    void goToAjouterReponse(ActionEvent event) throws IOException {
        Reclamation selectedReclamation = listViewRec.getSelectionModel().getSelectedItem();
        if (selectedReclamation != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterReponse.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            AjouterReponse ajouterReponseController = loader.getController();
            ajouterReponseController.setReclamation(selectedReclamation);
            stage.setTitle("Ajouter Réponse");
            stage.show();
        } else {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a reclamation.");
        }
    }

    @FXML
    void consulterReponse(ActionEvent event) {
        Reclamation selectedReclamation = listViewRec.getSelectionModel().getSelectedItem();
        if (selectedReclamation != null) {
            try {
                // Retrieve responses associated with the selected reclamation
                List<Reponse> reponses = ServiceReponse.getInstance().getAllByReclamationId(selectedReclamation.getIdRec());

                // Show responses in a dialog or new window
                showReponsesDialog(reponses);
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve responses:\n" + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a reclamation.");
        }
    }

    private void showReponsesDialog(List<Reponse> reponses) {
        // Create a dialog to display responses
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Reponses");
        alert.setHeaderText("Responses to selected reclamation:");

        // Create a string to display responses
        StringBuilder responseText = new StringBuilder();
        for (Reponse reponse : reponses) {
            responseText.append("Date: ").append(reponse.getDateReponse()).append("\n");
            responseText.append("Content: ").append(reponse.getContenu()).append("\n\n");
        }

        // Set the content of the dialog with the string of responses
        TextArea textArea = new TextArea(responseText.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        // Add the text area to the dialog
        alert.getDialogPane().setContent(textArea);

        // Show the dialog
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType alertType, String title, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
    @FXML
    private void acceuil(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/ReclamationOu.fxml"));

        // Create a Scene with the root node
        Scene scene = new Scene(root);

        // Create a new Stage
        Stage stage = new Stage();

        // Set the Scene to the Stage
        stage.setScene(scene);

        // Set the title of the Stage
        stage.setTitle("FXML Example");

        // Show the Stage
        stage.show();
    }
}