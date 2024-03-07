package controllers;
import entities.Discussion;
import entities.Message;
import entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import services.DiscussionService;
import services.MessageService;
import utils.MessageCell;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

public class MessageController {
    @FXML
    private Button attachButton;

    @FXML
    private TextField messageField;

    @FXML
    private ListView<Message> messageList;

    @FXML
    private Button sendButton;
    @FXML
    private Button emojiButton;
    @FXML
    private Label error;
    @FXML
    private ImageView imageView;
    @FXML
    private VBox vb;
    @FXML
    private Button delete;
    @FXML
    private Button update;

    public void setMessageField(TextField messageField) {
        this.messageField = messageField;
    }

    public static int discuId ;
    public static String emojis = "";


    User user1 = new User(2,"koussay");
    User user2 = new User(1,"koussay");
    User userConnected = user1;
    MessageService ms = new MessageService();
    DiscussionService ds = new DiscussionService();


    public void initialize() throws SQLException {
        ObservableList<Message> messages = FXCollections.observableList(ms.afficherByDiscussionId(discuId));
        Discussion d1 = ds.getOneById(discuId);
        if(!Login.ConnectedUser.equals(d1.getCreateur())){
            delete.setVisible(false);
            update.setVisible(false);
        }
        vb.setStyle("-fx-background-color: " + d1.getColor() + ";");

        messageList.setItems(messages);
        messageList.setCellFactory(param -> new MessageCell());
        System.out.println(messages);
        sendButton.setOnAction(e -> {
            try {
                sendMessage();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        attachButton.setOnAction(e->{
            MessageService.file = fileChooser.showOpenDialog(stage);
            if(MessageService.file!=null){
                System.out.println(MessageService.file);
                Image image = new Image(MessageService.file.toURI().toString(),100,150,true,true);
                System.out.println(image);
                imageView.setImage(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(150);
                imageView.setPreserveRatio(true);

            }

        });
        imageView.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.SECONDARY) {
                        ContextMenu contextMenu = new ContextMenu();

                        // Create menu items
                        MenuItem deleteMenuItem = new MenuItem("Delete");
                        deleteMenuItem.setOnAction(e->{
                            imageView.setImage(null);
                            imageView.setFitWidth(0);
                            imageView.setFitHeight(0);
                            MessageService.file=null ;
                        });
                        contextMenu.getItems().addAll( deleteMenuItem);

                        // Show context menu at the mouse location
                        contextMenu.show(messageList, event.getScreenX(), event.getScreenY());

                    }
                });
        attachContextMenuToListView(messageList);


    }
    public void supprimerDiscussion()  {
      try {
          Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
          alert.setTitle("Confirmation ");
          alert.setHeaderText("Look, a Confirmation Dialog");
          alert.setContentText("Are you ok with deleting the discussion?");
          Optional<ButtonType> result = alert.showAndWait();
          if (result.get() == ButtonType.OK){
              ds.supprimer(discuId);
              Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
              alert1.setTitle("success ");
              alert1.setContentText("discussion deleted successfully");
              alert1.showAndWait();
              changeScene("/ListeDiscussion.fxml");
          }
      }catch(SQLException e){
          System.out.println(e.getMessage());
      }
    }
    public void retour() {
      changeScene("/ListeDiscussion.fxml");
    }
    public void changeScene(String s) {
        try {
            // Chargez le fichier FXML pour la nouvelle scène
            Parent root = FXMLLoader.load(getClass().getResource(s));
            messageList.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void sendMessage() throws SQLException {
        String mess = messageField.getText();
        if(mess.isEmpty() && MessageService.file == null){
            error.setText("Le message est vide !");

        }else{
            // Get the text from the messageField
            error.setText("");

            String text = messageField.getText();
            Timestamp currentTimestamp = new Timestamp( System.currentTimeMillis());

            // Create a new Message object
            Message message = new Message(text,currentTimestamp,Login.ConnectedUser);
            // add message to the database
            ms.ajouter(message);
            imageView.setImage(null);
            imageView.setFitWidth(0);
            imageView.setFitHeight(0);
            MessageService.file=null;
            // Add the message to the messageList
            messageList.getItems().add(message);
            initialize();

            // Clear the messageField
            messageField.clear();
        }

    }
    public void modifierTitre(){
        changeScene("/ModifierTitre.fxml");
    }

    Stage newStage = new Stage();

    public void emojiPopup() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/emojis.fxml"));
        Scene scene = new Scene(root);
        newStage.setScene(scene);
        newStage.show();

        newStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                String currentText = messageField.getText();
                messageField.setText(currentText +" "+ emojis);
                emojis = "";
            }
        });
    }

    private void attachContextMenuToListView(ListView<Message> listView) {
        listView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) { // Right-click detected
                Message selectedItem = listView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    int messageId = selectedItem.getId();

                    // Create the context menu
                    ContextMenu contextMenu = new ContextMenu();

                    // Create menu items
                    MenuItem updateMenuItem = new MenuItem("Update");
                    MenuItem deleteMenuItem = new MenuItem("Delete");
                    System.out.println(Login.ConnectedUser.equals(selectedItem.getEmetteur()));
                    System.out.println(userConnected);
                    System.out.println(selectedItem);

                    if(Login.ConnectedUser.equals(selectedItem.getEmetteur())) {
                        updateMenuItem.setOnAction(updateEvent -> {
                            int id = selectedItem.getId();
                            String contenu = selectedItem.getContenu();
                            messageField.setText(contenu);
                            sendButton.setOnAction(actionEvent -> {
                                if (messageField.getText().isEmpty()) {
                                    error.setText("Le message est vide !");
                                } else {
                                    Message message = new Message(id, messageField.getText());
                                    try {
                                        ms.modifier(message);
                                        messageField.clear();
                                        initialize();
                                    } catch (SQLException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });
                            System.out.println("Update message with ID: " + messageId);
                        });

                        // Handle delete menu item
                        deleteMenuItem.setOnAction(deleteEvent -> {
                            try {
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                alert.setTitle("Confirmation ");
                                alert.setHeaderText("Look, a Confirmation Dialog");
                                alert.setContentText("Are you ok with deleting the message?");
                                Optional<ButtonType> result = alert.showAndWait();
                                if (result.get() == ButtonType.OK) {
                                    ms.supprimer(messageId);
                                    listView.getItems().remove(selectedItem);
                                }
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                            System.out.println("Delete message with ID: " + messageId);
                        });
                    }
                    // Add menu items to context menu
                    contextMenu.getItems().addAll(updateMenuItem, deleteMenuItem);

                    // Show context menu at the mouse location
                    contextMenu.show(listView, event.getScreenX(), event.getScreenY());
                }
            }
        });

    }



}
