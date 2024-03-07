package controllers;

import entities.Facture;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.shape.Circle;
import services.ServiceStatistique;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;

public class Statistics {
    private ServiceStatistique serviceStatistique = new ServiceStatistique();

    @FXML
    private ComboBox<String> typeFactureComboBox;
    @FXML
    private TableView<StatRow> tableView;

    @FXML
    private TableColumn<StatRow, String> dateColumn;

    @FXML
    private TableColumn<StatRow, Float> valueColumn;

    @FXML
    private Circle circle1;

    @FXML
    private Circle circle2;

    @FXML
    private DatePicker dateDebutPicker;

    @FXML
    private DatePicker dateFinPicker;

    @FXML
    private TextField nbrEtagesTextField;


    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private Button floorButton;

    @FXML
    private Button allApartmentsButton;

    @FXML
    private LineChart<String, Number> BarChart;


    public void initialiserStatistiques() throws SQLException {
        // Initialize the statistics with default values
    }

    @FXML
    private void displayStatisticsForFloor() {
        try {
            int floorNumber = Integer.parseInt(nbrEtagesTextField.getText());
            LocalDate startDate = dateDebutPicker.getValue();
            LocalDate endDate = dateFinPicker.getValue();
            String selectedType = typeFactureComboBox.getValue();
            Map<String, Float> floorStatistics = serviceStatistique.getStatisticsByFloor(floorNumber, startDate, endDate, Facture.Type.valueOf(selectedType));

            // Mise à jour du graphique
            updateLineChart(floorStatistics);
            updateTableView(floorStatistics);

        } catch (NumberFormatException e) {
            System.out.println("Veuillez saisir un nombre valide pour le nombre d'étages.");
        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        }
    }

    @FXML
    private void displayStatisticsForAllApartments() {
        try {
            LocalDate startDate = dateDebutPicker.getValue();
            LocalDate endDate = dateFinPicker.getValue();
            String selectedType = typeFactureComboBox.getValue();
            Map<String, Float> allApartmentsStatistics = serviceStatistique.getStatisticsForAllApartments(startDate, endDate, Facture.Type.valueOf(selectedType));

            // Mise à jour du graphique
            updateLineChart(allApartmentsStatistics);
            updateTableView(allApartmentsStatistics);

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
        ObservableList<StatRow> data = FXCollections.observableArrayList();
        for (Map.Entry<String, Float> entry : statistics.entrySet()) {
            // Calculez le montant en utilisant la méthode calculateConsumptionCost
            float montant = serviceStatistique.calculateConsumptionCost(entry.getValue(), Facture.Type.valueOf(typeFactureComboBox.getValue()));
            // Ajoutez un nouvel objet StatRow avec le montant calculé
            data.add(new StatRow(entry.getKey(), entry.getValue(), montant));
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

}






