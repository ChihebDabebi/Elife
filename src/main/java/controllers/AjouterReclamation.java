package controllers;



import entities.Reclamation;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import services.ServiceReclamation;
import services.ServiceUser;

public class AjouterReclamation {
    private final ServiceReclamation rs = new ServiceReclamation();
    private final ServiceUser su = new ServiceUser();
    private String captchaChallenge;


    @FXML
    private TextField CategorieRecTF;

    @FXML
    private TextField descriRecTF;
    @FXML
    private ImageView imageView;

    @FXML
    private VBox dragDropBox;

    private File imageFile;
    @FXML
    private TextField captchaInput;
    public Label captchaLabel;

    @FXML
    public void initialize() {
        captchaChallenge = generateCaptcha();
        captchaLabel.setText(captchaChallenge);
    }

    @FXML
    void ajouter(ActionEvent event) throws SQLException {
        if (CategorieRecTF.getText().isEmpty() || descriRecTF.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please fill in all fields.");
            return;
        }
        // Validate CAPTCHA
        if (!validateCaptcha()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid CAPTCHA. Please try again.");
            return;
        }

        Reclamation r = new Reclamation();
        r.setCategorieRec(CategorieRecTF.getText());
        r.setStatutRec("En cours");
        r.setDescriRec(descriRecTF.getText());

        r.setUser(su.getOneById(2)); // Assuming the user ID is obtained dynamically in real scenarios
        r.setDateRec(new Date(System.currentTimeMillis()));

        try {
            // If an image file is selected
            if (imageFile != null) {
                // Read the image file and convert it to a byte array
                byte[] imageData = readImage(imageFile);
                // Set the image data in the reclamation object
                r.setImageData(imageData);
            }

            // Save the reclamation to the database
            rs.ajouter(r);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Reclamation added successfully.");
            // Send email notification to the user
            String userEmail = "oussema.chebi@gmail.com"; // Replace with the user's email
            String emailSubject = "Reclamation Added";
            String emailBody = "Your reclamation has been successfully added.";
            EmailUtil.sendReclamationNotification(userEmail, emailSubject, emailBody);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to read image file:\n" + e.getMessage());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add reclamation:\n" + e.getMessage());
        }
    }

    // Method to read image file and convert it to byte array
    private byte[] readImage(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] imageData = new byte[(int) file.length()];
            fis.read(imageData);
            return imageData;
        }
    }

    @FXML
    void handleDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

    @FXML
    void handleDragDropped(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        boolean success = false;
        if (dragboard.hasFiles()) {
            imageFile = dragboard.getFiles().get(0);
            // Load the image into the ImageView
            imageView.setImage(new Image(imageFile.toURI().toString()));
            success = true;
        }
        event.setDropCompleted(success);
        event.consume();
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
    // Method to validate the entered CAPTCHA against the generated challenge
    private boolean validateCaptcha() {
        String userInput = captchaInput.getText().trim();
        return userInput.equals(captchaChallenge);
    }

    private void showAlert(Alert.AlertType alertType, String title, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
    @FXML
    private void back(ActionEvent event) throws IOException {
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