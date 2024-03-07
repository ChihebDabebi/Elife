package controllers;

import entities.Discussion;
import entities.User;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import services.ServiceUser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class showcaseUser {
    private final ServiceUser su = new ServiceUser();
    private ServiceUser serviceUser;
    @FXML
    private Button boutonPDF;
    @FXML
    private AnchorPane anchor;


    @FXML
    private ListView<User> listeUsers;

    @FXML
    private Button boutonGerer;
    @FXML
    private Button boutonGererEspace;
    @FXML
    private TextField txtRechercheNom;
    private ObservableList<User> eventsObservableList;


/*
    public showcaseUser() throws SQLException {
        listeUsers = su.getAll();
        serviceUser = new ServiceUser();
        eventsObservableList = FXCollections.observableArrayList();
    }
*/

    private void attachContextMenuToListView(ListView<User> listView) {
        listView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) { // Right-click detected
                User selectedItem = listView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    int userid = selectedItem.getId();

                    // Create the context menu
                    ContextMenu contextMenu = new ContextMenu();

                    // Create menu items
                    //MenuItem updateMenuItem = new MenuItem("Update");
                    MenuItem deleteMenuItem = new MenuItem("Delete");


                    // Handle delete menu item
                    deleteMenuItem.setOnAction(deleteEvent -> {
                        try {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Confirmation ");
                            alert.setHeaderText("Look, a Confirmation Dialog");
                            alert.setContentText("Are you ok with deleting the user?");
                            Optional<ButtonType> result = alert.showAndWait();
                            if (result.get() == ButtonType.OK) {
                                su.supprimer(userid);
                                listView.getItems().remove(selectedItem);
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println("Delete message with ID: " + userid);
                    });

                    // Add menu items to context menu
                    contextMenu.getItems().addAll(deleteMenuItem);

                    // Show context menu at the mouse location
                    contextMenu.show(listView, event.getScreenX(), event.getScreenY());
                }
            }
        });

    }

    @FXML
    public void initialize() throws SQLException {
        ServiceUser serviceUser = new ServiceUser();
        List<User> users = serviceUser.getAll();
        System.out.println(users);
        ObservableList<User> eventsObservableList = FXCollections.observableArrayList(users);
        listeUsers.setItems(eventsObservableList);
        txtRechercheNom.textProperty().addListener((observable, oldValue, newValue) -> {
            rechercherParUser();// Appeler la méthode rechercherParNom lorsque le texte dans le champ de recherche change
        });

        // Customize how items are rendered in the ListView
        listeUsers.setCellFactory(param -> new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Set the text to display for each list item
                    setText(item.getNom() + " - " + item.getPrenom() + " - " + item.getMail() + " - " + item.getNumber() + " - " + item.getPassword() + " - " + item.getRole());
                }
            }
        });
        attachContextMenuToListView(listeUsers);
    }

public void showcaseUser(ListView<User> listView) {
    try {
        listeUsers = (ListView<User>) su.getAll();
        ObservableList<User> observableList = FXCollections.observableArrayList();
        listView.setItems(observableList);
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
    @FXML
    private void handleModifyUserButtonClick(ActionEvent event) throws IOException {
        User selectedUser = listeUsers.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Modify.fxml"));
            Parent root = loader.load();
            ModifyUser modifyUserController = loader.getController();
            modifyUserController.setUser(selectedUser); // Pass selected user to modifyUserController

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modify User");
            stage.show();
        } else {
            // Handle no user selected case (e.g., show message)
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No User Selected");
            alert.setHeaderText("Please select a user to modify.");
            alert.showAndWait();
        }
    }
    @FXML
    public void handleshowcaseuserbuttonclick() {
        try {
            eventsObservableList.addAll(serviceUser.getAll());
            listeUsers.setItems(eventsObservableList);
            // Personnalisez la façon dont les éléments sont rendus dans la ListView
            listeUsers.setCellFactory(param -> new ListCell<User>() {
                @Override
                protected void updateItem(User item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        // Définissez le texte à afficher pour chaque élément de la liste
                        setText(item.getNom() + " - " + item.getPrenom()+ " - " + item.getMail()+ " - " + item.getNumber()+ " - " + item.getPassword()+ " - " + item.getRole());
                    }
                }
            });
        } catch (Exception throwables) {

            throwables.printStackTrace();
        }
        /*boutonGerer.setOnAction(event -> {
            User selectedUser = listeUsers.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                ouvrirDetailsEvent(selectedUser);
            } else {
                // Afficher un message d'erreur ou une notification si aucun événement n'est sélectionné
            }
        });

        listeUsers.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                User selectedEvent = listeUsers.getSelectionModel().getSelectedItem();
                if (selectedEvent != null) {
                    ouvrirDetailsEvent(selectedEvent);
                }
            }
        });
        boutonGererEspace.setOnAction(event -> {
            ouvrirAjouterEspace(); // Méthode pour ouvrir la vue d'ajout d'espace
        });*/

    }
    public void changeScene(String s) {
        try {
            // Chargez le fichier FXML pour la nouvelle scène
            Parent root = FXMLLoader.load(getClass().getResource(s));
            anchor.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleShowcaseUserButtonClick(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/modifyuser.fxml"));

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
    @FXML
    private void yarjaaliladd(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/add.fxml"));

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
    public void rechercherParUser(){
        String nomRecherche = txtRechercheNom.getText().trim().toLowerCase();

        if (!nomRecherche.isEmpty()) {
            // Créer un prédicat pour filtrer les espaces dont le nom contient la chaîne de recherche
            Predicate<User> nomPredicate = user -> user.getNom().toLowerCase().contains(nomRecherche);

            // Créer un FilteredList avec le prédicat
            FilteredList<User> filteredList = new FilteredList<>(listeUsers.getItems(), nomPredicate);

            // Mettre à jour la liste affichée dans la ListView avec le FilteredList filtré
            listeUsers.setItems(filteredList);
        }else {
            // Si le champ de recherche est vide, afficher tous les espaces
            changeScene("/showcaseUser.fxml");
        }
    }
}
