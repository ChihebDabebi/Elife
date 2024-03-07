package controllers;


import entities.Reclamation;
import entities.Reponse;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import services.ServiceReponse;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static javafx.collections.FXCollections.observableArrayList;

public class AfficherReponse {
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
    public void initialize() {
        loadAllResponses(); // Load all responses when the view is initialized
    }

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
    private void loadAllResponses() {
        try {
            List<Reponse> allResponses = serviceReponse.getAll(); // Retrieve all responses
            listViewReponses.setItems(FXCollections.observableArrayList(allResponses)); // Set items in ListView
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exception
        }
    }




}