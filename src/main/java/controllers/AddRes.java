package controllers;

import entities.Role;
import entities.User;
import services.ServiceUser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

public class AddRes {

    @FXML
    private GridPane root;

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField phoneField;

    @FXML
    private ChoiceBox<Role> roleChoiceBox;


    @FXML
    private void handleAddUserButtonClick() {
        try {
            // validate input fields
            String nom = nomField.getText();
            String prenom = prenomField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();
            String phone = phoneField.getText();
            Role role = roleChoiceBox.getValue();

            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty() || role == null) {
                throw new Exception("All fields are required.");
            }

            if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                throw new Exception("Invalid email address.");
            }

            if (!phone.matches("^[2|5|9][0-9]{7}$")) {
                throw new Exception("Invalid phone number. Must start with 2, 5, or 9 and have 8 digits.");
            }

            // create a new User object based on the input fields
            User user = new User(
                    nom,
                    prenom,
                    Integer.parseInt(phone),
                    email,
                    password,
                    role
            );

            // create a new ServiceUser object
            ServiceUser serviceUser = new ServiceUser();

            // add the User object to the database
            serviceUser.ajouter(user);

            // show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("User added successfully");
            alert.showAndWait();

            // clear input fields
            nomField.clear();
            prenomField.clear();
            emailField.clear();
            passwordField.clear();
            phoneField.clear();
            roleChoiceBox.getSelectionModel().select(Role.RESIDENT);

            //conciergeAttributes.setVisible(false);
            // residentAttributes.setVisible(false);

        } catch (Exception e) {
            // show error message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error adding user");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void initialize() throws SQLException {
        // Initialize role choice box
        roleChoiceBox.getSelectionModel().select(Role.RESIDENT);
        roleChoiceBox.setVisible(false);
        roleChoiceBox.getItems().addAll(Role.values());
        // roleChoiceBox.getSelectionModel().select(Role.RESIDENT);
    }
    @FXML
    private void backtolog(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));

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



  /*  @FXML
    private void handleShowcaseUserButtonClick(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/showcaseUser.fxml"));

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
    }*/
}