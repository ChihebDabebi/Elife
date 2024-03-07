package controllers;

import entities.Espace;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;

import java.util.List;
import java.util.function.Predicate;
import javafx.collections.transformation.FilteredList;


import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import services.IService;
import services.ServiceEspace;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

public class AfficherEspace {

    @FXML
    private ListView<Espace> listEspace;

    @FXML
    private Button btnModifier;

    @FXML
    private Button btnSupprimer;

    @FXML
    private TextField txtNom;

    @FXML
    private TextField txtCapacite;

    @FXML
    private TextArea txtDescription;

    @FXML
    private ComboBox<String> comboEtat;

    @FXML
    private ChoiceBox<String> choixTri;

    @FXML
    private TextField txtRechercheNom;

    private final IService<Espace> serviceEspace = new ServiceEspace();

    @FXML
    void initialize() {
        try {
            ObservableList<String> optionsTri = FXCollections.observableArrayList("Nom", "État", "Capacité");
            choixTri.setItems(optionsTri);
            choixTri.setValue("Nom");

            // Ajouter un écouteur sur les changements de sélection dans le ChoiceBox
            choixTri.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                // Appeler la méthode de tri lorsque l'utilisateur change l'option de tri
                trier(newValue);
            });
            txtRechercheNom.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    rechercherParNom(); // Appeler la méthode rechercherParNom lorsque le texte dans le champ de recherche change
                } catch (SQLException e) {
                    afficherAlerteErreur("Erreur lors de la recherche : " + e.getMessage());
                }
            });


            chargerListeEspaces();

            // Configurer la façon dont les éléments sont rendus dans la ListView
            listEspace.setCellFactory(param -> new ListCell<Espace>() {
                @Override
                protected void updateItem(Espace espace, boolean empty) {
                    super.updateItem(espace, empty);
                    if (empty || espace == null) {
                        setText(null);
                    } else {
                        setText(espace.toString());
                    }
                }
            });

            // Ajouter un écouteur d'événements pour la sélection dans la liste
            listEspace.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    // Remplir le formulaire avec les valeurs de l'espace sélectionné
                    remplirFormulaire(newValue);
                }
            });
        } catch (SQLException e) {
            afficherAlerteErreur("Erreur lors du chargement des espaces : " + e.getMessage());
        }
    }

    @FXML
    void trier(String methodeTri) {
        ObservableList<Espace> espaces = listEspace.getItems();

        // Utiliser une expression switch pour définir l'ordre de tri en fonction de la méthode sélectionnée
        switch (methodeTri) {
            case "Nom":
                espaces.sort((espace1, espace2) -> espace1.getName().compareTo(espace2.getName()));
                break;
            case "État":
                espaces.sort(this::trierParEtat);
                break;
            case "Capacité":
                espaces.sort(Comparator.comparingInt(Espace::getCapacite));
                break;
            default:
                // Aucun changement d'ordre par défaut
        }

        listEspace.setItems(espaces);
    }


    // Méthode pour trier par état (libre puis réservé)
    private int trierParEtat(Espace espace1, Espace espace2) {
        if (espace1.getEtat() == Espace.Etat.LIBRE && espace2.getEtat() == Espace.Etat.RESERVE) {
            return -1; // "libre" avant "réservé"
        } else if (espace1.getEtat() == Espace.Etat.RESERVE && espace2.getEtat() == Espace.Etat.LIBRE) {
            return 1; // "réservé" après "libre"
        } else {
            return 0; // Aucun changement d'ordre
        }
    }


    @FXML
    void rechercherParNom() throws SQLException {
        String nomRecherche = txtRechercheNom.getText().trim().toLowerCase();

        if (!nomRecherche.isEmpty()) {
            // Créer un prédicat pour filtrer les espaces dont le nom contient la chaîne de recherche
            Predicate<Espace> nomPredicate = espace -> espace.getName().toLowerCase().contains(nomRecherche);

            // Créer un FilteredList avec le prédicat
            FilteredList<Espace> filteredList = new FilteredList<>(listEspace.getItems(), nomPredicate);

            // Mettre à jour la liste affichée dans la ListView avec le FilteredList filtré
            listEspace.setItems(filteredList);
        } else {
            // Si le champ de recherche est vide, afficher tous les espaces
            chargerListeEspaces();
        }
    }


    private void remplirFormulaire(Espace espace) {
        txtNom.setText(espace.getName());
        comboEtat.setValue(espace.getEtat().toString());
        txtCapacite.setText(String.valueOf(espace.getCapacite()));
        txtDescription.setText(espace.getDescription());
    }

    @FXML
    void modifier() {
        Espace espace = listEspace.getSelectionModel().getSelectedItem();
        if (espace != null) {
            try {
                // Contrôle de saisie pour le nom
                String nom = txtNom.getText().trim();
                if (nom.isEmpty()) {
                    throw new IllegalArgumentException("Le nom de l'espace ne peut pas être vide.");
                }
                if (!nom.matches("[a-zA-Z ]+")) {
                    throw new IllegalArgumentException("Le nom doit contenir uniquement des lettres et des espaces.");
                }

                // Contrôle de saisie pour la capacité
                int capacite = Integer.parseInt(txtCapacite.getText().trim());
                if (capacite <= 0 || capacite > 50) {
                    throw new IllegalArgumentException("La capacité de l'espace doit être un entier compris entre 1 et 50.");
                }

                // Contrôle de saisie pour la description
                String description = txtDescription.getText().trim();
                if (description.isEmpty()) {
                    throw new IllegalArgumentException("La description ne peut pas être vide.");
                }

                // Mettre à jour les propriétés de l'espace avec les nouvelles valeurs
                espace.setName(nom);
                espace.setEtat(Espace.Etat.valueOf(comboEtat.getValue()));
                espace.setCapacite(capacite);
                espace.setDescription(description);

                // Appeler la méthode pour mettre à jour l'espace dans la base de données
                serviceEspace.modifier(espace);
                listEspace.refresh(); // Rafraîchir l'affichage de la liste

                afficherConfirmation("Espace modifié avec succès !");
            } catch (NumberFormatException e) {
                afficherAlerteErreur("Erreur de format", "Veuillez saisir un nombre valide pour la capacité.");
            } catch (IllegalArgumentException e) {
                afficherAlerteErreur("Erreur de saisie", e.getMessage());
            } catch (SQLException e) {
                afficherAlerteErreur("Erreur SQL", "Erreur lors de la modification de l'espace : " + e.getMessage());
            } catch (Exception e) {
                afficherAlerteErreur("Erreur", e.getMessage());
            }
        } else {
            afficherAlerteErreur("Erreur", "Veuillez sélectionner un espace à modifier.");
        }
    }

    @FXML
    void supprimer() {
        Espace espace = listEspace.getSelectionModel().getSelectedItem();
        if (espace != null) {
            if (afficherConfirmation("Êtes-vous sûr de vouloir supprimer cet espace ?")) {
                try {
                    serviceEspace.supprimer(espace.getIdEspace());
                    listEspace.getItems().remove(espace);
                } catch (SQLException e) {
                    afficherAlerteErreur("Erreur lors de la suppression de l'espace : " + e.getMessage());
                }
            }
        } else {
            afficherAlerteErreur("Veuillez sélectionner un espace à supprimer.");
        }
    }

    @FXML
    void gererEvenements() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherEvent.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Gérer les événements");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void ajouterEspace() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterEspace.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un espace");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void chargerListeEspaces() throws SQLException {
        List<Espace> espaces = serviceEspace.getAll();
        ObservableList<Espace> espaceList = FXCollections.observableArrayList(espaces);

        // Afficher la liste dans la ListView
        listEspace.setItems(espaceList);
    }

    private void afficherAlerteErreur(String message) {
        showAlert(Alert.AlertType.ERROR, "Erreur", message);
    }

    private void afficherAlerteErreur(String titre, String message) {
        showAlert(Alert.AlertType.ERROR, titre, message);
    }

    private boolean afficherConfirmation(String message) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText(message);
        Optional<ButtonType> result = confirmationAlert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    @FXML
    void afficherCalendrier(ActionEvent event) {
        Espace espace = listEspace.getSelectionModel().getSelectedItem();
        if (espace != null) {
            try {
                // Charger le fichier FXML du calendrier
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Calendrier.fxml"));
                Parent root = loader.load();

                // Passer l'espace sélectionné au contrôleur du calendrier
                CalendrierController calendrierController = loader.getController();
              calendrierController.selectEspace(espace);

                // Créer une nouvelle scène pour afficher le calendrier
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Calendrier de l'espace " + espace.getName());
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            afficherAlerteErreur("Veuillez sélectionner un espace.");
        }
    }
    @FXML
    void afficherStatistiques() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/stats.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Statistiques");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
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
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}