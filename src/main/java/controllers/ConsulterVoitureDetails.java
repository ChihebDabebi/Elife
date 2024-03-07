package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;
import javafx.event.ActionEvent;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import javax.imageio.ImageIO;




public class ConsulterVoitureDetails {

    @FXML
    private ImageView qrCodeImageView;
    @FXML
    private TextField textFieldMarque; // Ajout de l'annotation @FXML
    @FXML
    private TextField textFieldModele; // Ajout de l'annotation @FXML
    @FXML
    private TextField textFieldCouleur; // Ajout de l'annotation @FXML
    @FXML
    private TextField textFieldMatricule; // Ajout de l'annotation @FXML



    @FXML
    private Button ajouterQRCodeButton;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        // Initialisation du contrôleur
    }

    @FXML
    public void initData(Image qrCodeImage, File qrCodeImageFile) {
        qrCodeImageView.setImage(qrCodeImage);
        // Décoder le contenu du code QR et extraire les détails de la voiture
        String qrCodeContent = decodeQRCode(qrCodeImageFile);
        String[] voitureDetails = qrCodeContent.split(";"); // Supposons que le contenu soit séparé par des points-virgules

        // Vérifier si le contenu du code QR est valide et contient les détails attendus
        if (voitureDetails.length == 4) { // Supposons que nous attendons 4 détails : marque, modèle, couleur, matricule
            textFieldMarque.setText(voitureDetails[0]); // Premier détail : marque
            textFieldModele.setText(voitureDetails[1]); // Deuxième détail : modèle
            textFieldCouleur.setText(voitureDetails[2]); // Troisième détail : couleur
            textFieldMatricule.setText(voitureDetails[3]); // Quatrième détail : matricule
        } else {
            // Afficher un message d'erreur si le contenu du code QR est invalide ou ne contient pas les détails attendus
            System.out.println("Contenu du code QR invalide.");
        }
    }


    @FXML
    private void handleAjouterQRCodeButton() {
        // Ouvrir une boîte de dialogue pour sélectionner l'image du QR code
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image du QR Code");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(qrCodeImageView.getScene().getWindow());

        if (selectedFile != null) {
            // Charger l'image du QR code sélectionnée
            Image qrCodeImage = new Image(selectedFile.toURI().toString());
            qrCodeImageView.setImage(qrCodeImage);

            // Passer l'image et le fichier sélectionnés à la méthode initData
            initData(qrCodeImage, selectedFile);
        }
    }

    private String decodeQRCode(File qrCodeImageFile) {
        try {
            BufferedImage bufferedImage = ImageIO.read(qrCodeImageFile);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(bufferedImage)));
            Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap);
            return qrCodeResult.getText();
        } catch (NotFoundException | IOException e) {
            e.printStackTrace();
            return "Erreur lors du décodage du QR code.";
        }
    }
    @FXML
    private void handleModifierButton(ActionEvent event) {
        // Récupérer les nouvelles valeurs des détails de la voiture à partir des champs de texte
        String nouvelleMarque = textFieldMarque.getText();
        String nouveauModele = textFieldModele.getText();
        String nouvelleCouleur = textFieldCouleur.getText();
        String nouveauMatricule = textFieldMatricule.getText();

        // Effectuer les modifications nécessaires dans la base de données ou ailleurs selon vos besoins
        // Vous pouvez appeler les méthodes appropriées de votre service pour mettre à jour les détails de la voiture

        // Par exemple, vous pouvez afficher un message de confirmation après la modification
        System.out.println("Détails de la voiture modifiés avec succès !");
    }

}
