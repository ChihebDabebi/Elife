package utils;

import entities.Message;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class MessageCell extends ListCell<Message> {
    @Override
    protected void updateItem(Message item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            VBox vbox = new VBox();
            Text text = new Text(item.getEmetteur().getNom() + "\n" + item.getContenu() );
            Text text2 = new Text(" "+ item.getTimeStamp_envoi());
            vbox.getChildren().add(text);

            Image image = item.getImage();
            ImageView imageView = new ImageView(image);
            vbox.getChildren().add(imageView);
            vbox.getChildren().add(text2);

            setGraphic(vbox);
        }
    }
    }
