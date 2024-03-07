package controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.util.HashMap;
import java.util.Map;

public class emojis {
    @FXML
    private GridPane grid;
    private Map<String, Integer> emojiClicks = new HashMap<>();

    public void initialize() {

        for (Node node : grid.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                button.setOnAction(event -> {
                    String emoji = button.getText();
                    emojiClicks.put(emoji, emojiClicks.getOrDefault(emoji, 0) + 1);
                    printEmojiClicks();
                });
            }
        }
    }

    private void printEmojiClicks() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : emojiClicks.entrySet()) {
            sb.append(entry.getKey().repeat(entry.getValue()));
        }
        MessageController.emojis = sb.toString();
    }

}
