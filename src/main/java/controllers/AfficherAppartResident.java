package controllers;

import entities.Appartement;
import entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import services.ServiceAppartemment;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;

public class AfficherAppartResident {

    private final ServiceAppartemment serviceAppartemment = new ServiceAppartemment();

    @FXML
    private ListView<Appartement> listView;


    private User currentUser;

    public void initData(User user) throws SQLException {
        if (currentUser == null) {
            System.out.println("user n'existe pas");
        }
        this.currentUser = user;
        System.out.println("user connectee : " + currentUser);
        initialize();
    }


    @FXML
    private void initialize() {
        // Assurez-vous que connectedUser n'est pas null avant d'appeler la méthode
        if (currentUser != null) {
            try {
                Set<Appartement> appartements = serviceAppartemment.getAppartementsForUser(currentUser);
                System.out.println(appartements);
                updateListView(appartements);
            } catch (SQLException e) {
                e.printStackTrace(); // Gérer l'exception selon vos besoins
            }
        }

    }


    private void updateListView(Set<Appartement> appartements) {
        try {
            ObservableList<Appartement> observableList = FXCollections.observableArrayList(appartements);
            listView.setItems(observableList);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while updating the list of apartments.");
        }
    }
    @FXML
    void gererFactureResident(ActionEvent actionEvent) {
        Appartement appartementSelectionne = listView.getSelectionModel().getSelectedItem();
        System.out.println("Appartement sélectionné pour afficher les factures : " + appartementSelectionne);

        if (appartementSelectionne != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherFactureResident.fxml"));
                Parent root = loader.load();

                System.out.println("FXML file loaded successfully.");

                AfficherFactureResident controller = loader.getController();
                System.out.println("Controller initialized.");

                controller.initData(appartementSelectionne);
                System.out.println("Data initialized in controller.");

                Stage stage = new Stage();
                stage.setTitle("Liste des Factures");
                stage.setScene(new Scene(root));

                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Aucun appartement sélectionné.");
        }
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void afficherStatistiquesAppartement() throws SQLException {
        Appartement appartementSelectionne = listView.getSelectionModel().getSelectedItem();

        if (appartementSelectionne != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/StaticSeul.fxml"));
                Parent root = loader.load();

                System.out.println("FXML file loaded successfully.");

                StaticSeul controller = loader.getController();
                System.out.println("Controller initialized.");

                controller.initData(appartementSelectionne);
                System.out.println("Data initialized in controller.");

                Stage stage = new Stage();
                stage.setTitle("Liste des Factures");
                stage.setScene(new Scene(root));

                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
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
