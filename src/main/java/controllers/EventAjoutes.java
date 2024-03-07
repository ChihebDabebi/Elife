package controllers;

import entities.Espace;
import entities.Event;
import entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import services.ServiceEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Set;

public class EventAjoutes {
    private Event event;


    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @FXML private TextField textFieldTitre;
    @FXML private TextField textFieldNbrPersonne;
    @FXML private ComboBox<String> comboBoxEspace;
    @FXML private TextArea textFieldListeInvites; // Modifier le type en TextArea
    @FXML private TextField textFieldDate;

    @FXML private ListView<Event> listViewEvents;
    private ObservableList<Event> eventsObservableList = FXCollections.observableArrayList(); // Initialisation de la liste observable

    private User currentUser;
    private final ServiceEvent serviceEvent = new ServiceEvent();

    public void initialize() {
        // Créer une cellule de rendu personnalisée pour le ComboBox

    }

    public void initData(User user) {
        this.currentUser = user;
        afficherEvenementsUtilisateur();
    }

    private void afficherEvenementsUtilisateur() {
        try {
            Set<Event> events = serviceEvent.getEventsByUserId(currentUser);
            eventsObservableList.setAll(events); // Utiliser setAll pour mettre à jour la liste observable
            listViewEvents.setItems(eventsObservableList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void afficherDetailsEvent(MouseEvent event) {
        if (event.getClickCount() == 1) {
            Event selectedEvent = listViewEvents.getSelectionModel().getSelectedItem();
            if (selectedEvent != null) {
                // Afficher les détails de l'événement
                afficherDetails(selectedEvent);
                // Initialiser l'attribut event avec l'événement sélectionné
                this.event = selectedEvent;
            }
        }
    }

    private void afficherDetails(Event event) {
        textFieldTitre.setText(event.getTitle());
        textFieldNbrPersonne.setText(String.valueOf(event.getNbrPersonne()));
        textFieldListeInvites.setText(event.getListeInvites());
        textFieldDate.setText(dateFormat.format(event.getDate()));
        comboBoxEspace.setValue(event.getEspace().getName()); // Utilisation du nom de l'espace
    }

    @FXML
    private void modifier() {
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

                    String espaceName = comboBoxEspace.getValue(); // Récupérer le nom de l'espace
                    if (espaceName == null) {
                        throw new IllegalArgumentException("Veuillez sélectionner un espace.");
                    }

                    Espace espace = serviceEvent.getEspaceByName(espaceName); // Récupérer l'espace par son nom
                    if (espace == null) {
                        throw new IllegalArgumentException("L'espace sélectionné n'existe pas.");
                    }


                    // Si toutes les validations sont passées, vous pouvez effectuer la modification
                    event.setTitle(titre);
                    event.setNbrPersonne(nbrPersonne);
                    event.setEspace(espace);
                    event.setListeInvites(listeInvites);
                    event.setDate(new java.sql.Date(date.getTime()));

                    serviceEvent.modifier(event);

                    refreshList();

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


    @FXML
    void supprimerEvent() {
        Event selectedEvent = listViewEvents.getSelectionModel().getSelectedItem();
        if (selectedEvent != null) {
            try {
                serviceEvent.supprimer(selectedEvent.getIdEvent());
                // Rafraîchir la liste des événements
                afficherEvenementsUtilisateur();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public void refreshList() {
        try {
            Set<Event> events = serviceEvent.getEventsByUserId(currentUser);
            eventsObservableList.setAll(events); // Utiliser setAll pour mettre à jour la liste observable
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    private void afficherAlerteErreurEvent(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
}
