package controllers;

import services.ServiceEmail;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import services.ServiceUser;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class forgetpassword {
    @FXML
    private TextField passwordField;
    @FXML
    private TextField passwordField1;
    @FXML
    private Button ConfirmPasswordBtn;

    @FXML
    private TextField emailField;
    private ServiceUser serviceForgetPassword = new ServiceUser();

    public void initialize(){
        passwordField.setVisible(false);
        passwordField1.setVisible(false);
        ConfirmPasswordBtn.setVisible(false);
    }

    public void submitForgetPassword(ActionEvent actionEvent) {
        String email = emailField.getText();

        if (serviceForgetPassword.doesEmailExist(email)) {
            String verificationCode = generateVerificationCode();
            ServiceEmail.sendEmail(emailField.getText(),verificationCode,"Your verifcatrion code is "+verificationCode);
            System.out.println(verificationCode);
            boolean isCodeCorrect = showVerificationPrompt(verificationCode);

            if (isCodeCorrect) {
                passwordField.setVisible(true);
                passwordField1.setVisible(true);
                ConfirmPasswordBtn.setVisible(true);
            } else {
                showAlert("Incorrect Code", "The verification code entered is incorrect.");
            }
        } else {
            showAlert("Email Not Found", "The provided email does not exist.");
        }
    }

    public void goBackToLogin(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        try {
            Parent root = loader.load();
            emailField.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private String generateVerificationCode() {
        return String.valueOf((int) (Math.random() * 900000 + 100000));
    }

    private boolean showVerificationPrompt(String verificationCode) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Verification Code");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter the verification code sent to your email:");

        Optional<String> result = dialog.showAndWait();
        return result.isPresent() && result.get().equals(verificationCode);
    }

    public void ConfirmPassword(ActionEvent actionEvent) {
        String newPassword = passwordField.getText();
        String confirmPassword = passwordField1.getText();
        if (!newPassword.equals(confirmPassword)) {
            showAlert("Password Mismatch", "The entered passwords do not match.");
            return;
        }
        String email = emailField.getText();
        serviceForgetPassword.changePassword(email, newPassword);
        showAlert("Password Changed", "Your password has been successfully changed.");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        try {
            Parent root = loader.load();
            emailField.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println(e.getMessage());      }
    }
}