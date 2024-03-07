package controllers;

import entities.Reclamation;
import entities.Reponse;
import entities.User;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import services.ServiceReclamation;
import services.ServiceReponse;
import services.ServiceUser;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AfficherReclamationAdmin {
    private final ServiceReclamation rs = new ServiceReclamation();
    private final ServiceUser su = new ServiceUser();
    private Stage stage; // Reference to the stage
    @FXML
    private ListView<Reclamation> listViewRec;
    @FXML
    private Button ajouterReponseButton;
    public void initialize() {
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
            List<Reclamation> reclamations = rs.getAll();
            User userAdd = su.getOneById(2); // Assuming the user ID is fixed or obtained from somewhere
            List<Reclamation> filteredReclamations = new ArrayList<>();

            for (Reclamation reclamation : reclamations) {
                if (reclamation.getUser().equals(userAdd)) {
                    filteredReclamations.add(reclamation);
                }
            }

            listViewRec.setItems(FXCollections.observableArrayList(filteredReclamations));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve reclamations:\n" + e.getMessage());
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
    void goToStatistiquesReclamation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/StatistiquesReclamation.fxml")); // Adjust the path to your StatistiquesReclamation FXML file
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Statistiques des Reclamations");
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open statistiques view:\n" + e.getMessage());
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
}
