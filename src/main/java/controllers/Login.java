package controllers;

import entities.Role;
import entities.User;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import utils.DataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class Login {
    public TextField captchaInput;
    public Label captchaLabel;
    @FXML
    private Button cancelbtn;

    @FXML
    private TextField emailfield;

    @FXML
    private Button loginbtn;

    @FXML
    private PasswordField passwordfield;
     @FXML
     private Label loginmessagelabel;
    @FXML
    private ToggleButton toggleButton;

    @FXML
    private Label ShownPassword;
    public static User ConnectedUser ;

    private String captchaChallenge;
    @FXML
    public void initialize() {
        captchaChallenge = generateCaptcha();
        captchaLabel.setText(captchaChallenge);
    }

    // Method to generate a simple CAPTCHA challenge
    private String generateCaptcha() {
        int length = 6; // Length of the CAPTCHA challenge
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder captcha = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            captcha.append(chars.charAt(random.nextInt(chars.length())));
        }
        return captcha.toString();
    }
    public void cancelbtnonaction(ActionEvent event)
    {
        Stage stage =(Stage) cancelbtn.getScene().getWindow();
        stage.close();
    }
    public void loginbtnonaction(ActionEvent event) throws SQLException {

        if(emailfield.getText().isBlank()==false && passwordfield.getText().isBlank()==false){
           validateLogin(event);
            ConnectedUser = getCurrentUserFromDatabase(emailfield.getText());

        }
        else {
            loginmessagelabel.setText("fill all the spots");
        }

    }
    public User getCurrentUserFromDatabase(String email) throws SQLException {
        Connection cnx = DataSource.getInstance().getConnexion();
        String query = "SELECT * FROM user WHERE mail=?";

        try (PreparedStatement statement = cnx.prepareStatement(query)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("nom");
                    // Vous pouvez récupérer d'autres informations de l'utilisateur à partir de la base de données
                    // selon votre modèle de données

                    // Créer un objet User avec les informations récupérées
                    User currentUser = new User(id, name, email); // Supposons que votre User a un constructeur avec ces champs

                    return currentUser;
                }
            }
        }

        return null; // Si aucun utilisateur n'est trouvé avec l'email spécifié
    }
    public void validateLogin(ActionEvent event) {
        try {
            // Connect to the database
            Connection cnx = DataSource.getInstance().getConnexion();

            // Prepare the login verification query
            String verifyLogin = "SELECT * FROM user WHERE mail='" + emailfield.getText() + "' AND password='" + passwordfield.getText() + "'";

            // Create a prepared statement for security (preventing SQL injection)
            PreparedStatement statement = cnx.prepareStatement(verifyLogin);

            // Execute the query
            ResultSet queryResult = statement.executeQuery();

            // Check if a user exists with the provided credentials
            if (queryResult.next()) {
                String roleString = queryResult.getString("role");

                Role role = Role.valueOf(roleString.toUpperCase());
                String userInput = captchaInput.getText().trim();
                if (userInput.equals(captchaChallenge)) {

                    if (role == Role.RESIDENT) {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AcceuilFront.fxml"));
                            Parent root = loader.load();

                            AcceuilFront controller = loader.getController();
                            if (controller != null) {
                                String currentUserEmail = emailfield.getText();
                                User currentUser = getCurrentUserFromDatabase(currentUserEmail);
                                controller.setCurrentUser(currentUser);

                                // Afficher la scène
                                Scene scene = new Scene(root);
                                Stage stage = new Stage();
                                stage.setScene(scene);
                                stage.show();

                                // Fermer la fenêtre de connexion
                                Stage loginStage = (Stage) loginbtn.getScene().getWindow();
                                loginStage.close();
                            } else {
                                System.out.println("Failed to retrieve AjouterEvent controller");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }  else if (role == Role.CONCIERGE) {
                        // Logique pour les concierges
                        // Par exemple, ouvrir une interface spécifique pour les concierges.
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AcceuilBack.fxml"));
                            Parent root = loader.load();

                            AcceuilBack controller = loader.getController();
                            if (controller != null) {
                                String currentUserEmail = emailfield.getText();
                                User currentUser = getCurrentUserFromDatabase(currentUserEmail);
                                controller.setCurrentUser(currentUser);

                                // Afficher la scène
                                Scene scene = new Scene(root);
                                Stage stage = new Stage();
                                stage.setScene(scene);
                                stage.show();

                                // Fermer la fenêtre de connexion
                                Stage loginStage = (Stage) loginbtn.getScene().getWindow();
                                loginStage.close();
                            } else {
                                System.out.println("Failed to retrieve InterfaceConcierge controller");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (role == Role.ADMIN) {
                        // Logique pour les concierges
                        // Par exemple, ouvrir une interface spécifique pour les concierges.
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/add.fxml"));
                            Parent root = loader.load();

                            AddUser controller = loader.getController();
                            if (controller != null) {
                                String currentUserEmail = emailfield.getText();
                                User currentUser = getCurrentUserFromDatabase(currentUserEmail);
                                controller.setCurrentUser(currentUser);

                                // Afficher la scène
                                Scene scene = new Scene(root);
                                Stage stage = new Stage();
                                stage.setScene(scene);
                                stage.show();

                                // Fermer la fenêtre de connexion
                                Stage loginStage = (Stage) loginbtn.getScene().getWindow();
                                loginStage.close();
                            } else {
                                System.out.println("Failed to retrieve InterfaceConcierge controller");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        loginmessagelabel.setText("Retry");
                    }
                } else {
                    showAlert("Login Failed", "Invalid CAPTCHA. Please try again.");
                }
            } else {  // User not found
                System.out.println("Invalid credentials.");  // Display error message
                // ... handle incorrect credentials (e.g., display error message to user)
            }
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
        }
    }
    @FXML
    private void handlesignup(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/addr.fxml"));

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
    void toggleButton(ActionEvent event) {
        if(toggleButton.isSelected()){
            ShownPassword.setVisible(true);
            ShownPassword.textProperty().bind(Bindings.concat(passwordfield.getText()));
            toggleButton.setText("Hide");
        }else {
            ShownPassword.setVisible(false);
            toggleButton.setText("Show");
        }
    }
    public void OnForget(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ForgetPassword.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    void opt(MouseEvent event) {

    }

}
