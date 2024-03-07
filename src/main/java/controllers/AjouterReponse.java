package controllers;


import entities.Reclamation;
import entities.Reponse;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.ListView;
import services.ServiceReponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class AjouterReponse {
    private final ServiceReponse serviceReponse = new ServiceReponse();
    private Reclamation selectedReclamation;

    @FXML
    private TextField comTF;

    @FXML
    private ListView<Reponse> listViewReponses;

    @FXML
    private Button modifier;

    @FXML
    private Button supprimer;

    public void setReclamation(Reclamation reclamation) {
        this.selectedReclamation = reclamation;
        loadResponses();
    }

    private void loadResponses() {
        if (selectedReclamation != null) {
            try {
                List<Reponse> reponses = serviceReponse.getAllByReclamationId(selectedReclamation.getIdRec());
                listViewReponses.setItems(FXCollections.observableArrayList(reponses));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void ajouter(ActionEvent event) {
        if (selectedReclamation != null && !comTF.getText().isEmpty()) {
            Reponse reponse = new Reponse();
            reponse.setReclamation(selectedReclamation);
            reponse.setContenu(comTF.getText());
            reponse.setDateReponse(new Date());

            try {
                serviceReponse.ajouter(reponse);
                loadResponses(); // Refresh the list view
                comTF.clear(); // Clear the input field after adding
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No reclamation selected or content is empty.");
        }
    }

    @FXML
    void modifier(ActionEvent event) {
        Reponse selectedReponse = listViewReponses.getSelectionModel().getSelectedItem();
        if (selectedReponse != null && !comTF.getText().isEmpty()) {
            Reclamation reclamation = selectedReponse.getReclamation();
            if (reclamation != null) {
                selectedReponse.setContenu(comTF.getText());
                selectedReponse.setDateReponse(new Date());
                try {
                    serviceReponse.modifier(selectedReponse);
                    loadResponses(); // Refresh the list view
                    comTF.clear(); // Clear the input field
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Reclamation associated with the response is null.");
            }
        } else {
            System.out.println("No response selected or content is empty.");
        }
    }

    @FXML
    void supprimer(ActionEvent event) {
        Reponse selectedReponse = listViewReponses.getSelectionModel().getSelectedItem();
        if (selectedReponse != null) {
            try {
                serviceReponse.supprimer(selectedReponse.getIdReponse());
                loadResponses(); // Refresh the list view
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}