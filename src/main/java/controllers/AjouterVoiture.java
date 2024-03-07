package controllers;

import com.google.zxing.common.BitMatrix;
import entities.Parking;
import entities.User;
import entities.Voiture;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import services.ServiceParking;
import services.ServiceVoiture;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AjouterVoiture {


    @FXML
    private Label placesDispoLabel;

    @FXML
    private TextField marqueField;

    @FXML
    private TextField modeleField;

    @FXML
    private TextField couleurField;

    @FXML
    private TextField matriculeField;

    @FXML
    private Button telechargerButton;

    @FXML
    private Button ajouterButton;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private ListView<String> parkingsListView;

    @FXML
    private ImageView qrCodeImageView;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Timeline timeline;

    private User currentUser;
    private Stage stage; // Gardez une référence à la fenêtre précédente

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    private Parking selectedParking;

    private int idVoitureAjoutee;

    private ServiceParking serviceParking;


    private AfficherVoitureAdmin afficherVoitureAdmin;





    public void setSelectedParking(Parking parking) {
        System.out.println("Parking sélectionné : " + parking); // Afficher le parking sélectionné
        this.selectedParking = parking;
    }
    public void initData(User user) throws SQLException {
        if (currentUser == null) {
            System.out.println("user n'existe pas");
        }
        this.currentUser = user;
        System.out.println("user connectee : " + currentUser);
        initialize();
    }


    public void setAfficherVoitureAdmin(AfficherVoitureAdmin afficherVoitureAdmin) {
        this.afficherVoitureAdmin = afficherVoitureAdmin;
    }

    @FXML
    public void initialize() {
        serviceParking = new ServiceParking();
        progressBar.setVisible(false); // Désactiver la barre de progression
        progressBar.setProgress(0); // Initialiser la progression à 0

        timeline = new Timeline(new KeyFrame(Duration.seconds(10), e -> {
            progressBar.setVisible(false);
            generateQRCode(matriculeField.getText());
        }));
        timeline.setCycleCount(1);
        chargerTypes();
        // Ajouter un écouteur sur le changement de sélection du ComboBox
        typeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                chargerParkings(newValue);
            }
        });
    }


    @FXML
    private void handleAjouterButton(ActionEvent event) {

        String marque = marqueField.getText();
        String modele = modeleField.getText();
        String couleur = couleurField.getText();
        String matricule = matriculeField.getText();
        String email = currentUser.getMail();


        if (!validateMarque(marque) || !validateModele(modele) || !validateCouleur(couleur) || !validateMatricule(matricule)) {
            ajouterButton.setDisable(false);
            return;
        }

        if (selectedParking == null) {
            afficherMessageErreur("Veuillez sélectionner un parking avant d'ajouter une voiture.");
            return;
        }

        ajouterButton.setDisable(true);
        progressBar.setVisible(true); // Activer la barre de progression
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        Timeline delayTimeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
            try {
                ServiceVoiture serviceVoiture = new ServiceVoiture();
                if (serviceVoiture.existeMatricule(matricule)) {
                    afficherMessageErreur("La matricule de la voiture existe déjà dans le parking.");
                    ajouterButton.setDisable(false);
                    return;
                }

                ServiceParking serviceParking = new ServiceParking();
                selectedParking = serviceParking.getOneById(selectedParking.getIdParking());
                if (selectedParking == null) {
                    afficherMessageErreur("Aucun parking sélectionné.");
                    ajouterButton.setDisable(false);
                    return;
                }

                if (selectedParking.getNombreActuelles() >= selectedParking.getCapacite()) {
                    afficherMessageErreur("Le parking est plein. Impossible d'ajouter plus de voitures.");
                    ajouterButton.setDisable(false);
                    return;
                }

                Voiture voiture = new Voiture(selectedParking.getIdParking(), marque, modele, couleur, matricule, selectedParking,currentUser);

                serviceVoiture.ajouter(voiture);

                if (idVoitureAjoutee != -1) {
                    generateQRCode(matricule);
                    progressBar.setVisible(false);
                    progressBar.setProgress(0);

                    selectedParking.setNombreActuelles(selectedParking.getNombreActuelles() + 1);
                    serviceParking.modifier(selectedParking);

                    afficherQRCode(matricule); // Affichage du code QR après le délai
                    // Activer la visibilité du bouton Télécharger QR Code
                    telechargerButton.setVisible(true);
                    LocalDateTime currentDateTime = LocalDateTime.now();

                    // Définit un format pour afficher la date et l'heure
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                    // Formate la date et l'heure actuelles en utilisant le format spécifié
                    String formattedDateTime = currentDateTime.format(formatter);
                    afficherMessageSucces("Voiture ajoutée avec succès!");
                    String subject = "Voiture ";
                    String message = "Votre voiture a été ajouter au parking le  : " + formattedDateTime;
                    Gmailer gmailer = new Gmailer();
                    gmailer.sendMail(email, subject, message); // Utiliser l'adresse e-mail saisie comme destinataire
                   // afficherMessageSucces("Succès");

                } else {
                    // Traitement en cas d'échec de l'ajout de la voiture
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            } finally {
                ajouterButton.setDisable(false);
                progressBar.setVisible(false); // Désactiver la barre de progression
                progressBar.setProgress(0); // Remettre la progression à 0
            }

            System.out.println("Capacité du parking après ajout : " + selectedParking.getNombreActuelles() + "/" + selectedParking.getCapacite());
        }
        ));
        delayTimeline.setCycleCount(1);
        delayTimeline.play();

    }

    @FXML
    private void afficherNombrePlacesDispo(String parkingName) {
        String selectedParking = parkingsListView.getSelectionModel().getSelectedItem();
        if (selectedParking != null) {
            try {
                ServiceParking serviceParking = new ServiceParking();
                Parking parking = serviceParking.getParkingByName(selectedParking);
                int capaciteTotale = parking.getCapacite();
                int nombreActuel = parking.getNombreActuelles();
                int placesDispo = capaciteTotale - nombreActuel;

                placesDispoLabel.setText(String.valueOf(placesDispo));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            placesDispoLabel.setText("");
        }
    }




    private void chargerTypes() {
        ServiceParking serviceParking = new ServiceParking();
        try {
            List<String> types = serviceParking.getTypes();
            typeComboBox.getItems().addAll(types);
            typeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    chargerParkings(newValue);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void chargerParkings(String type) {
        ServiceParking serviceParking = new ServiceParking();
        try {
            List<String> parkings = serviceParking.getParkingsByType(type);
            parkingsListView.getItems().clear();
            parkingsListView.getItems().addAll(parkings);

            // Mettre à jour le nombre de places disponibles pour chaque parking
            for (String parkingName : parkings) {
                afficherNombrePlacesDispo(parkingName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }





    private void generateQRCode(String matricule) {
        int width = 300;
        int height = 300;
        String fileType = "png";
        String filePath = "C:\\Users\\LENOVO\\Desktop\\qrcode";

        String data = "Matricule: " + matricule;

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height, hints);

            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, fileType, byteArrayOutputStream);
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();

            File qrFile = new File(filePath + "QRCode_" + matricule + "." + fileType);
            ImageIO.write(bufferedImage, fileType, qrFile);

            System.out.println("QR code généré avec succès.");

        } catch (WriterException | IOException e) {
            System.out.println("xxxx");
            e.printStackTrace();
        }
    }

    private void afficherQRCode(String matricule) {
        String filePath = "C:\\Users\\LENOVO\\Desktop\\qrcode";
        String fileName = "QRCode_" + matricule + ".png";

        File file = new File(filePath + fileName);

        if (file.exists()) {
            try {
                // Charger l'image à partir du fichier
                BufferedImage bufferedImage = ImageIO.read(file);
                Image qrCodeImage = new Image(file.toURI().toString());
                qrCodeImageView.setImage(qrCodeImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Le fichier QR code n'existe pas : " + fileName);
        }
    }


    @FXML
    private void handleSupprimerButton(ActionEvent event) {
        if (idVoitureAjoutee != -1) {
            try {
                ServiceVoiture serviceVoiture = new ServiceVoiture();
                serviceVoiture.supprimer(idVoitureAjoutee);
                afficherMessageSucces("Voiture supprimée avec succès!");

                selectedParking.setNombreActuelles(selectedParking.getNombreActuelles() - 1);

                ServiceParking serviceParking = new ServiceParking();
                serviceParking.modifier(selectedParking);

                marqueField.clear();
                modeleField.clear();
                couleurField.clear();
                matriculeField.clear();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            afficherMessageErreur("Aucune voiture ajoutée récemment pour être supprimée.");
        }
    }

    private void afficherMessageErreur(String message) {
        Platform.runLater(() -> {

            Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        });

    }

    private void afficherMessageSucces(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    private boolean validateMarque(String marque) {
        if (marque.isEmpty()) {
            afficherMessageErreur("Veuillez saisir la marque de la voiture.");
            return false;
        }
        if (marque.length() > 20) {
            afficherMessageErreur("La marque ne peut pas dépasser 20 caractères.");
            return false;
        }
        if (!Pattern.matches("^[a-zA-Z ]*$", marque)) {
            afficherMessageErreur("La marque doit contenir uniquement des lettres.");
            return false;
        }
        return true;
    }

    private boolean validateModele(String modele) {
        if (modele.isEmpty()) {
            afficherMessageErreur("Veuillez saisir le modèle de la voiture.");
            return false;
        }
        if (modele.length() > 20) {
            afficherMessageErreur("Le modèle ne peut pas dépasser 20 caractères.");
            return false;
        }
        if (!modele.matches("^[a-zA-Z0-9\\s]+$")) {
            afficherMessageErreur("Le modèle doit contenir uniquement des lettres, des espaces ou des chiffres.");
            return false;
        }

        return true;
    }

    private boolean validateCouleur(String couleur) {
        if (couleur.isEmpty()) {
            afficherMessageErreur("Veuillez saisir la couleur de la voiture.");
            return false;
        }
        if (couleur.length() > 20) {
            afficherMessageErreur("La couleur ne peut pas dépasser 20 caractères.");
            return false;
        }
        if (!Pattern.matches("^[a-zA-Z]*$", couleur)) {
            afficherMessageErreur("La couleur doit contenir uniquement des lettres.");
            return false;
        }
        return true;
    }

    private boolean validateMatricule(String matricule) {
        if (matricule.isEmpty()) {
            afficherMessageErreur("Veuillez saisir le numéro de matricule de la voiture.");
            return false;
        }
        if (matricule.length() < 6 || matricule.length() > 15) {
            afficherMessageErreur("Le numéro de matricule doit contenir entre 6 et 15 caractères.");
            return false;
        }
        if (!Pattern.matches("^[a-zA-Z0-9]*$", matricule)) {
            afficherMessageErreur("Le numéro de matricule doit contenir uniquement des lettres et des chiffres.");
            return false;
        }
        return true;
    }
    @FXML
    private void handleParkingSelection(MouseEvent event) {
        String selectedParkingName = parkingsListView.getSelectionModel().getSelectedItem();
        if (selectedParkingName != null) {
            try {
                Parking selectedParking = serviceParking.getParkingByName(selectedParkingName);
                setSelectedParking(selectedParking);
                afficherNombrePlacesDispo(selectedParkingName);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    @FXML
    private void handleTelechargerButton(ActionEvent event) {
        String matricule = matriculeField.getText();
        if (!matricule.isEmpty()) {
            String fileName = "QRCode_" + matricule + ".png";
            String filePath = "C:\\Users\\LENOVO\\Desktop\\qrcode" + fileName;

            File file = new File(filePath);
            if (file.exists()) {
                // Télécharger le fichier
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Le fichier QR code n'existe pas : " + fileName);
            }
        } else {
            afficherMessageErreur("Veuillez générer d'abord le code QR avant de le télécharger.");
        }
    }
    public void modifierVoiture(int idVoiture, String nouvelleMarque, String nouveauModele, String nouvelleCouleur, String nouveauMatricule) {
        // Implémentez la logique de modification de la voiture ici
        try {
            ServiceVoiture serviceVoiture = new ServiceVoiture();
            Voiture voiture = serviceVoiture.getOneById(idVoiture);
            if (voiture != null) {
                voiture.setMarque(nouvelleMarque);
                voiture.setModel(nouveauModele);
                voiture.setCouleur(nouvelleCouleur);
                voiture.setMatricule(nouveauMatricule);

                // Enregistrez les modifications dans la base de données
                serviceVoiture.modifier(voiture);

                // Affichez un message de succès ou faites toute autre action nécessaire après la modification
                afficherMessageSucces("Voiture modifiée avec succès!");
            } else {
                afficherMessageErreur("La voiture à modifier n'a pas été trouvée.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            afficherMessageErreur("Une erreur s'est produite lors de la modification de la voiture.");
        }
    }

    @FXML
    private void handleConsulterButton(ActionEvent event) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ConsulterVoitureUser.fxml"));
            Parent root = loader.load();

            /*   Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            */

            // Utilisez la référence à l'objet event pour obtenir la source
            Node sourceNode = (Node) event.getSource();

            Stage stage = (Stage) sourceNode.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            ConsulterVoitureUser controller = loader.getController();
            System.out.println(currentUser);
            controller.setCurrentUser(currentUser);






        } catch (IOException e) {
            e.printStackTrace();
        }
    }





}
