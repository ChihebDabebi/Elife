package controllers;
import entities.Discussion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import services.DiscussionService;

import java.io.IOException;
import java.sql.SQLException;

public class ModifierTitre {
        @FXML
        private TextField titreMod;
        @FXML
        private TextField descriptionMod;
        @FXML
        private Label error;
        @FXML
        private ColorPicker color;

        @FXML
        private Label error2;
        boolean isTitreValid = true;
        boolean isDescriptionValid = true;
        DiscussionService ds = new DiscussionService();

        public void initialize() throws SQLException {
            Discussion discussion = ds.getOneById(MessageController.discuId);
            titreMod.setText(discussion.getTitre());
            descriptionMod.setText(discussion.getDescription());
                color.setValue(Color.web(discussion.getColor()));
        }
        @FXML
        void modifierTitre(ActionEvent event) throws SQLException {
            String titre = titreMod.getText();
            String description = descriptionMod.getText();
            int id = MessageController.discuId;
            Color c = color.getValue();
            String colorAsRgb = String.format("#%02X%02X%02X",
                    (int)(c.getRed() * 255),
                    (int)(c.getGreen() * 255),
                    (int)(c.getBlue() * 255));
            if (titre.isEmpty()) {
                error.setText("Le champ titre est vide !");
                isTitreValid = false;
            } else if (titre.length() > 10) {
                error.setText("Le titre ne doit pas dépasser 10 caractères !");
                isTitreValid = false;
            }else{
                error.setText("");
                isTitreValid = true;
            }
            if (description.isEmpty()) {
                error2.setText("Le champ description est vide !");
                isDescriptionValid = false;
            }else{
                error2.setText("");
                isDescriptionValid = true;
            }
            if(AjoutDiscussion.titreExist(titre)){
                error.setText("Le titre exist !");

            }
            if(!AjoutDiscussion.titreValide(description)){
                error2.setText(" la description contient des mots inapropriés");
            }
            if(!AjoutDiscussion.titreValide(titre)){
                error.setText("Le titre contient des mots inapropriés !");

            }
            if(AjoutDiscussion.titreValide(titre) && !AjoutDiscussion.titreExist(titre) && isTitreValid && isDescriptionValid) {
                Discussion discussion = new Discussion(id, titre, description,colorAsRgb);
                try {
                    ds.modifier(discussion);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("success ");
                    alert.setContentText("discussion updated successfully");
                    alert.showAndWait();
                    changeScene("/ListeDiscussion.fxml");
                } catch (SQLException e) {
                    Alert alert2 = new Alert(Alert.AlertType.WARNING);
                    alert2.setTitle("error ");
                    alert2.setContentText(e.getMessage());
                    alert2.showAndWait();
                    changeScene("/ListeDiscussion.fxml");

                }
            }
        }
    public void changeScene(String s) {
        try {
            // Chargez le fichier FXML pour la nouvelle scène
            Parent root = FXMLLoader.load(getClass().getResource(s));
            titreMod.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void retour() {
        changeScene("/Message.fxml");
    }



}
