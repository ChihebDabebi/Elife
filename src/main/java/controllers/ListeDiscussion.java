package controllers;

import entities.Discussion;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import services.DiscussionService;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.function.Predicate;

public class ListeDiscussion {
    @FXML
    private TableView<Discussion> table;
    @FXML
    private TextField rechercheField;
    @FXML
    private TableColumn<Discussion, String> titre = new TableColumn<>("titre");
    @FXML
    private TableColumn<Discussion, String> createur = new TableColumn<>("createur");
    @FXML
    private TableColumn<Discussion, Timestamp> dateCreation= new TableColumn<>("TimeStampCreation");


    public void initialize() throws SQLException {
        DiscussionService ds = new DiscussionService();
        ObservableList<Discussion> discussions = FXCollections.observableList(ds.getAll());
        titre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        createur.setCellValueFactory(cellData -> {
            String creatorName = cellData.getValue().getCreateur().getNom();
            return new SimpleStringProperty(creatorName);
        });

        rechercheField.textProperty().addListener((observable, oldValue, newValue) -> {
            rechercherParUser();// Appeler la méthode rechercherParNom lorsque le texte dans le champ de recherche change
        });
        dateCreation.setCellValueFactory(new PropertyValueFactory<>("TimeStampCreation"));
        table.setItems(discussions);
        onClick();
    }
    public void onClick(){
        table.setRowFactory(tv -> {
            TableRow<Discussion> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (! row.isEmpty() && event.getButton()== MouseButton.PRIMARY
                        && event.getClickCount() == 2) {

                    Discussion clickedRow = row.getItem();
                    // Récupérez l'ID de la discussion
                    int discussionId = clickedRow.getId();
                    MessageController.discuId = discussionId;
                    changeScene("/Message.fxml");

                }
            });
            return row ;
        });
    }
    public void changeScene(String s) {
        try {
            // Chargez le fichier FXML pour la nouvelle scène
            Parent root = FXMLLoader.load(getClass().getResource(s));
            table.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void redirectToAjout(){
        changeScene("/AjouterDiscussion.fxml");
    }

    public void rechercherParUser(){
        String nomRecherche = rechercheField.getText().trim().toLowerCase();

        if (!nomRecherche.isEmpty()) {
            // Créer un prédicat pour filtrer les espaces dont le nom contient la chaîne de recherche
            Predicate<Discussion> nomPredicate = discussion -> discussion.getCreateur().getNom().toLowerCase().contains(nomRecherche);

            // Créer un FilteredList avec le prédicat
            FilteredList<Discussion> filteredList = new FilteredList<>(table.getItems(), nomPredicate);

            // Mettre à jour la liste affichée dans la ListView avec le FilteredList filtré
            table.setItems(filteredList);
        } else {
            // Si le champ de recherche est vide, afficher tous les espaces
            changeScene("/ListeDiscussion.fxml");
        }
    }
    @FXML
    private void retournerVersAcceuil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AcceuilFront.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

