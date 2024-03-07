package controllers;

import entities.Espace;
import entities.Event;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import services.ServiceEspace;
import services.ServiceEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.input.MouseButton;
import java.io.IOException;
import java.sql.SQLException;
import java.awt.Desktop;
import java.io.File;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.awt.Color;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Set;
import  services.ServiceEvent;

import org.apache.pdfbox.pdmodel.font.PDType0Font;

public class AfficherEvent {
    public void setAfficherEvent(AfficherEvent afficherEvent) {
        this.afficherEvent = afficherEvent;
    }

    @FXML private Button boutonGererEspace;
    @FXML private TextField txtRechercheNom;
    @FXML private ListView<Event> listeEvents;
    @FXML private ChoiceBox<String> triChoiceBox;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private AfficherEvent afficherEvent;
    private Event event;
    private ServiceEvent serviceEvent;

    @FXML private TextField textFieldTitre;
    @FXML private TextField textFieldNbrPersonne;
    @FXML private ComboBox<Espace> comboBoxEspace;
    @FXML private TextArea textFieldListeInvites; // Modifier le type en TextArea
    @FXML private TextField textFieldDate;

    private ObservableList<Event> eventsObservableList;

    // Constructeur
    public AfficherEvent() {
        serviceEvent = new ServiceEvent();
        eventsObservableList = FXCollections.observableArrayList();
    }

    // Méthode initialize()
    @FXML
    public void initialize() {
        try {
            chargerEvents();
            afficherEvent = this;


            listeEvents.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    event = newValue; // Mise à jour de l'événement sélectionné
                    eventSelected(newValue);
                }
            });

            triChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    if (newValue.equals("Nom")) {
                        trierParNom();
                    } else if (newValue.equals("Date")) {
                        trierParDate();
                    }
                }
            });

            txtRechercheNom.textProperty().addListener((observable, oldValue, newValue) -> {
                rechercherParNom(newValue.trim());
            });

            boutonGererEspace.setOnAction(event -> ouvrirAjouterEspace());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    void eventSelected(Event event) {
        textFieldTitre.setText(event.getTitle());
        textFieldNbrPersonne.setText(String.valueOf(event.getNbrPersonne()));
        textFieldListeInvites.setText(event.getListeInvites());
        textFieldDate.setText(dateFormat.format(event.getDate()));
        comboBoxEspace.setValue(event.getEspace());
    }
    void trierParNom() {
        ObservableList<Event> events = listeEvents.getItems();
        events.sort((event1, event2) -> event1.getTitle().compareToIgnoreCase(event2.getTitle()));
        listeEvents.setItems(events);
    }

    void trierParDate() {
        ObservableList<Event> events = listeEvents.getItems();
        events.sort(Comparator.comparing(Event::getDate));
        listeEvents.setItems(events);
    }

    void rechercherParNom(String nomRecherche) {
        if (!nomRecherche.isEmpty()) {
            ObservableList<Event> resultats = FXCollections.observableArrayList();
            for (Event event : eventsObservableList) {
                if (event.getTitle().toLowerCase().contains(nomRecherche.toLowerCase())) {
                    resultats.add(event);
                }
            }
            listeEvents.setItems(resultats);
        } else {
            // Si le champ de recherche est vide, affichez tous les événements
            listeEvents.setItems(eventsObservableList);
        }
    }

    @FXML
    private void ouvrirAjouterEspace() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterEspace.fxml"));
            Parent root = loader.load();
            AjouterEspace controller = loader.getController();
            controller.initData(new Espace());

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un espace");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void modifierEvent() {
        {
            if (event != null) {
                try {
                    // Contrôle de saisie pour le titre
                    String titre = textFieldTitre.getText().trim();
                    if (titre.isEmpty() || !titre.matches("[a-zA-Z ]+")) {
                        throw new IllegalArgumentException("Le titre ne peut pas être vide et doit contenir uniquement des lettres et des espaces.");
                    }

                    // Contrôle de saisie pour le nombre de personnes
                    String nbrPersonneText = textFieldNbrPersonne.getText().trim();
                    if (nbrPersonneText.isEmpty()) {
                        throw new IllegalArgumentException("Veuillez saisir un nombre pour le nombre de personnes.");
                    }
                    int nbrPersonne = Integer.parseInt(nbrPersonneText);
                    if (nbrPersonne <= 0 || nbrPersonne > 50) {
                        throw new IllegalArgumentException("Le nombre de personnes doit être compris entre 1 et 50.");
                    }

                    // Contrôle de saisie pour la listeInvites
                    String listeInvites = textFieldListeInvites.getText().trim();
                    if (listeInvites.isEmpty()) {
                        throw new IllegalArgumentException("La listeInvites ne peut pas être vide.");
                    }

                    // Contrôle de saisie pour la date
                    String dateString = textFieldDate.getText().trim();
                    if (dateString.isEmpty()) {
                        throw new IllegalArgumentException("Veuillez saisir une date.");
                    }
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = dateFormat.parse(dateString);

                    // Contrôle de saisie pour l'espace
                    Espace espace = comboBoxEspace.getValue();
                    if (espace == null) {
                        throw new IllegalArgumentException("Veuillez sélectionner un espace.");
                    }

                    // Si toutes les validations sont passées, vous pouvez effectuer la modification
                    event.setTitle(titre);
                    event.setNbrPersonne(nbrPersonne);
                    event.setEspace(espace);
                    event.setListeInvites(listeInvites);
                    event.setDate(new java.sql.Date(date.getTime()));

                    serviceEvent.modifier(event);

                    afficherEvent.refreshList();

                } catch (NumberFormatException e) {
                    afficherAlerteErreurEvent("Erreur de format", "Veuillez saisir un nombre valide pour le nombre de personnes.");
                } catch (IllegalArgumentException | ParseException e) {
                    afficherAlerteErreurEvent("Erreur de saisie", e.getMessage());
                } catch (SQLException e) {
                    afficherAlerteErreurEvent("Erreur SQL", "Erreur lors de la modification de l'événement : " + e.getMessage());
                } catch (Exception e) {
                    afficherAlerteErreurEvent("Erreur", e.getMessage());
                }

            }}}

        private void chargerEvents() throws SQLException {
        // Charger la liste des événements
        eventsObservableList.addAll(serviceEvent.getAll());
        listeEvents.setItems(eventsObservableList);

        // Personnaliser l'affichage des éléments dans la ListView
        listeEvents.setCellFactory(param -> new ListCell<Event>() {
            @Override
            protected void updateItem(Event item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
            }
        });
    }

            private void afficherAlerteErreurEvent(String titre, String message) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(titre);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }

    public void refreshList() {
        try {
            eventsObservableList.clear();
            eventsObservableList.addAll(serviceEvent.getAll());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    @FXML
    private void supprimerEvent() {
        if (event != null) {
            try {
                serviceEvent.supprimer(event.getIdEvent());
                afficherEvent.refreshList();
            } catch (SQLException e) {
                e.printStackTrace();
                // Gérer l'erreur de suppression
            }
        }
    }
    @FXML
    private void retournerVersAcceuil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/BtnReservation.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
