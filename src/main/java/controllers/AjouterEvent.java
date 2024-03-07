
package controllers;

import entities.Espace;
import entities.Event;
import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.ServiceEvent;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Set;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.*;
import java.io.File;
import java.io.IOException;


public class AjouterEvent {

    private final ServiceEvent serviceEvent = new ServiceEvent();


    @FXML private DatePicker datePicker;
    @FXML private TextArea listInvitesField;
    @FXML private TextField nbrPersonneField;
    @FXML private TextField titleField;
    @FXML private ComboBox<String> espaceComboBox;
    @FXML private Button boutonPDF;
    private Event addedEvent;

    @FXML
    void initialize() {
        try {
            Set<String> espacesNames = serviceEvent.getAllEspacesNames();
            espaceComboBox.getItems().addAll(espacesNames);
        } catch (SQLException e) {
            afficherAlerteErreurEvent("Erreur SQL", "Erreur lors de la récupération des noms d'espaces : " + e.getMessage());
        }

        boutonPDF.setOnAction(event -> {
            genererPDF();
        });
    }

    private User currentUser;

    public void setCurrentUser(User currentUser) {
        this.currentUser = Login.ConnectedUser;
    }
    public AjouterEvent() {
    }

    @FXML
    void ajouterEvent(ActionEvent event) {
        try {

            // Contrôle de saisie pour le titre
            String title = titleField.getText().trim();
            if (!title.matches("[a-zA-Z ]+")) {
                throw new IllegalArgumentException("Le titre doit contenir uniquement des lettres et des espaces.");
            }

            // Contrôle de saisie pour la date
            LocalDate date = datePicker.getValue();
            if (date == null) {
                throw new IllegalArgumentException("Veuillez sélectionner une date.");
            }

            // Contrôle de saisie pour le nombre de personnes
            int nbrPersonne = Integer.parseInt(nbrPersonneField.getText().trim());
            if (nbrPersonne <= 0 || nbrPersonne > 50) {
                throw new IllegalArgumentException("Le nombre de personnes doit être un entier compris entre 1 et 50.");
            }

            String listeInvites = listInvitesField.getText().trim();
            if (listeInvites.isEmpty()) {
                throw new IllegalArgumentException("La liste des invités ne peut pas être vide.");
            }


            // Récupérer le nom de l'espace sélectionné
            String espaceName = espaceComboBox.getValue();
            if (espaceName == null) {
                throw new IllegalArgumentException("Veuillez sélectionner un espace.");
            }

            // Rechercher l'objet Espace correspondant au nom sélectionné
            Espace espaceObj = serviceEvent.getEspaceByName(espaceName);

            // Vérifier si l'espace existe
            if (espaceObj == null) {
                throw new IllegalArgumentException("L'espace sélectionné n'existe pas.");
            }
            // Créer l'objet Event et l'ajouter
            Event eventObj = new Event(title, Date.valueOf(date), nbrPersonne, listeInvites, espaceObj, currentUser);
            serviceEvent.ajouter(eventObj);

            // Stocker l'événement ajouté dans la variable addedEvent
            this.addedEvent = eventObj;

            // Afficher une confirmation
            afficherAlerteConfirmationEvent("Validation", "Événement ajouté avec succès");
        } catch (NumberFormatException e) {
            afficherAlerteErreurEvent("Erreur de format", "Veuillez saisir un nombre valide pour le nombre de personnes.");
        } catch (IllegalArgumentException e) {
            afficherAlerteErreurEvent("Erreur de saisie", e.getMessage());
        } catch (SQLException e) {
            afficherAlerteErreurEvent("Erreur SQL", "Erreur lors de l'ajout de l'événement : " + e.getMessage());
        } catch (Exception e) {
            afficherAlerteErreurEvent("Erreur", e.getMessage());
        }
    }

    @FXML
    private void genererPDF() {
        if (addedEvent != null) {
            try {
                PDDocument document = new PDDocument();
                PDPage page = new PDPage();
                document.addPage(page);

                PDPageContentStream contentStream = new PDPageContentStream(document, page);

                PDType0Font font = PDType0Font.load(document, getClass().getResourceAsStream("/fonts/CairoPlay-VariableFont_slnt,wght.ttf"));

                float margin = 0;

                PDImageXObject borderImage = PDImageXObject.createFromFile("src/main/resources/image/BORDD.png", document);
                contentStream.drawImage(borderImage, margin, margin, page.getMediaBox().getWidth() - 2 * margin, page.getMediaBox().getHeight() - 2 * margin);

                PDImageXObject logoImage = PDImageXObject.createFromFile("src/main/resources/image/logo.png", document);
                float logoWidth = 125;
                float logoHeight = logoWidth * logoImage.getHeight() / logoImage.getWidth();

                contentStream.drawImage(logoImage, page.getMediaBox().getWidth() - margin - logoWidth - 15, page.getMediaBox().getHeight() - margin - logoHeight - 15, logoWidth, logoHeight);

                float titleFontSize = 25;
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.setFont(font, titleFontSize);
                float titleX = (page.getMediaBox().getWidth() - font.getStringWidth("Détails de l'événement") / 1000 * titleFontSize) / 2 + 40;
                float titleY = page.getMediaBox().getHeight() - 3 * (margin + 30);
                contentStream.setNonStrokingColor(new Color(0, 0, 139));
                writeText(contentStream, "Détails de l'événement", titleX, titleY, font);

                float normalFontSize = 14;
                contentStream.setFont(font, normalFontSize);

                float infoX = (margin + 30) * 3;
                float infoY = titleY - normalFontSize * 6;
                float infoSpacing = normalFontSize * 2;

                contentStream.setNonStrokingColor(Color.BLACK);
                writeText(contentStream, "Titre : " + addedEvent.getTitle(), infoX, infoY, font);
                infoY -= infoSpacing;
                writeText(contentStream, "Espace : " + addedEvent.getEspace().getName(), infoX, infoY, font);
                infoY -= infoSpacing;
                writeText(contentStream, "Liste des invités : " + addedEvent.getListeInvites(), infoX, infoY, font);

                contentStream.close();

                // Utiliser le nom de l'événement pour nommer le fichier PDF
                File file = new File(addedEvent.getTitle() + ".pdf");
                document.save(file);
                document.close();

                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void writeText(PDPageContentStream contentStream, String text, float x, float y, PDType0Font font) throws IOException {
        String[] lines = text.split("\n");
        float fontSize = 14; // Adjust the font size as needed
        float leading = 1.5f * fontSize; // Adjust the line spacing as needed

        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset(x, y);

        for (String line : lines) {
            contentStream.showText(line);
            contentStream.newLineAtOffset(0, -leading);
        }

        contentStream.endText();
    }

    @FXML
    void consulterMesEvents(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EventAjoutes.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur AjouterEvent
            EventAjoutes controller = loader.getController();
            if (controller != null) {
                controller.initData(currentUser);

                // Afficher la scène
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.show();

                // Obtenir la scène actuelle à partir de l'événement

                // Définir la nouvelle scène
                stage.setScene(scene);
                stage.show();

            }

        } catch (IOException e) {
            e.printStackTrace();
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


    private void afficherAlerteConfirmationEvent(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void afficherAlerteErreurEvent(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }
}