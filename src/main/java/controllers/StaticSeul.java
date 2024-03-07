package controllers;



import entities.Appartement;
import entities.Facture;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import services.ServiceStatistique;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;

public class StaticSeul {
    private ServiceStatistique serviceStatistique = new ServiceStatistique();

    @FXML
    private ComboBox<String> typeFactureComboBox;
    @FXML
    private TableView<Statistics.StatRow> tableView;

    @FXML
    private TableColumn<Statistics.StatRow, String> dateColumn;

    @FXML
    private TableColumn<Statistics.StatRow, Float> valueColumn;


    @FXML
    private DatePicker dateDebutPicker;

    @FXML
    private DatePicker dateFinPicker;
    Appartement appartementSelectionne;


    @FXML
    private LineChart<String, Number> BarChart;
    public void initData(Appartement appartement) throws SQLException {
        if (appartement == null) {
            System.out.println("L'objet appartementSelectionne est null !");
        }
        this.appartementSelectionne = appartement;
        System.out.println("Appartement sélectionné : " + appartementSelectionne);

    }

    public void displayStatisticsForAppartement(ActionEvent actionEvent) {
        try {
            LocalDate startDate = dateDebutPicker.getValue();
            LocalDate endDate = dateFinPicker.getValue();
            String selectedType = typeFactureComboBox.getValue();
            Map<String, Float> apartmentStatistics = serviceStatistique.getStatisticsForApartment(startDate, endDate, Facture.Type.valueOf(selectedType), appartementSelectionne.getIdAppartement());
            // Mise à jour du graphique
            updateLineChart(apartmentStatistics);
            updateTableView(apartmentStatistics);

        } catch (NumberFormatException e) {
            System.out.println("Veuillez saisir un nombre valide pour le nombre d'étages.");
        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        }
    }

    private void updateLineChart(Map<String, Float> statistics) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        for (Map.Entry<String, Float> entry : statistics.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        BarChart.getData().clear();
        BarChart.getData().add(series);
    }

    private void updateTableView(Map<String, Float> statistics) {
        ObservableList<Statistics.StatRow> data = FXCollections.observableArrayList();
        for (Map.Entry<String, Float> entry : statistics.entrySet()) {
            // Calculez le montant en utilisant la méthode calculateConsumptionCost
            float montant = serviceStatistique.calculateConsumptionCost(entry.getValue(), Facture.Type.valueOf(typeFactureComboBox.getValue()));
            // Ajoutez un nouvel objet StatRow avec le montant calculé
            data.add(new Statistics.StatRow(entry.getKey(), entry.getValue(), montant));
        }
        tableView.setItems(data);
    }

    // Ajoutez un nouveau champ montant dans la classe StatRow
    public static class StatRow {
        private final String date;
        private final Float value;
        private final Float montant; // Nouveau champ montant

        public StatRow(String date, Float value, Float montant) {
            this.date = date;
            this.value = value;
            this.montant = montant; // Affectez la valeur du montant calculé
        }

        public String getDate() {
            return date;
        }

        public Float getValue() {
            return value;
        }

        public Float getMontant() {
            return montant;
        }
    }
    @FXML
    void retournerPagePrecedente(ActionEvent actionEvent) {
        // Récupérer la source de l'événement
        Node source = (Node) actionEvent.getSource();
        // Récupérer la scène de la source
        Scene scene = source.getScene();
        // Récupérer la fenêtre parente de la scène
        Stage stage = (Stage) scene.getWindow();
        // Fermer la fenêtre parente pour revenir à la page précédente
        stage.close();
    }

}