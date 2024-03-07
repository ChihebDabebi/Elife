package controllers;
import entities.Discussion;
import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import services.DiscussionService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class AjoutDiscussion {

        @FXML
        private TextField titre ;
        @FXML
        private ColorPicker color;

        @FXML
        private TextField description;
        @FXML
        private Label errorMessage;

        @FXML
        private Label errorMessage2;


        User user1 = new User(2,"koussay");


        boolean isTitreValid = true;
        boolean isDescriptionValid = true;
        @FXML
        void ajouterEvent() throws SQLException {
                String title = titre.getText();
                String desc = description.getText();
                Timestamp currentTimestamp = new Timestamp( System.currentTimeMillis());
                Color c = color.getValue();
                String colorAsRgb = String.format("#%02X%02X%02X",
                        (int)(c.getRed() * 255),
                        (int)(c.getGreen() * 255),
                        (int)(c.getBlue() * 255));
                Discussion discussion = new Discussion(title,currentTimestamp,Login.ConnectedUser,desc,colorAsRgb);
                if (title.isEmpty()) {
                        errorMessage.setText("Le champ titre est vide !");
                        isTitreValid = false;
                } else if (title.length() > 10) {
                        errorMessage.setText("Le titre ne doit pas dépasser 10 caractères !");
                        isTitreValid = false;
                }else{
                        errorMessage.setText("");
                        isTitreValid = true;
                }
                if (desc.isEmpty()) {
                        errorMessage2.setText("Le champ description est vide !");
                        isDescriptionValid = false;
                }else{
                        errorMessage2.setText("");
                        isDescriptionValid = true;
                }
                if(titreExist(title)){
                        errorMessage.setText("Le titre exist !");

                }
                if(!titreValide(desc)){
                        errorMessage2.setText(" la description contient des mots inapropriés");
                }
                if(!titreValide(title)){
                        errorMessage.setText("Le titre contient des mots inapropriés !");

                }


                        if(titreValide(title) && !titreExist(title) && isTitreValid && isDescriptionValid){
                                try {

                                        DiscussionService ds = new DiscussionService();
                                        ds.ajouter(discussion);
                                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                        alert.setTitle("Success");
                                        alert.setContentText("Discussion added successfully!");

                                        alert.showAndWait();
                                        changeScene();


                                }catch(Exception e){
                                        System.out.println(e.getMessage());
                                }

                }



        }
        public void changeScene() {
                try {
                        // Chargez le fichier FXML pour la nouvelle scène
                        Parent root = FXMLLoader.load(getClass().getResource("/ListeDiscussion.fxml"));
                        titre.getScene().setRoot(root);
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }
        public static boolean titreValide(String titre) {
                List<String> motsInterdits = null;
                try {
                        motsInterdits = Files.readAllLines(Paths.get("src/main/java/utils/motsinap.txt"));
                } catch (IOException e) {
                        System.out.println("Erreur lors de la lecture du fichier de mots inappropriés");
                        System.out.println(e.getMessage());
                }
                if (motsInterdits == null) {
                        return true;
                }

                // Convertir le titre en minuscules pour une comparaison insensible à la casse
                String titreMinuscules = titre.toLowerCase();

                // Vérifier si le titre contient un mot interdit
                for (String mot : motsInterdits) {
                        if (titreMinuscules.contains(mot)) {
                                return false;
                        }
                }

                return true;
        }
        public static boolean titreExist(String titre) throws SQLException {
                DiscussionService ds = new DiscussionService();
                List<Discussion> discussions = ds.getAll();
                for(Discussion discussion:discussions){
                        if(discussion.getTitre().equalsIgnoreCase(titre))

                                return true ;
                }

                return false;

        }


}
