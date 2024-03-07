package controllers;

import entities.Espace;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.control.cell.PropertyValueFactory;
import services.ServiceEspace;
import javafx.fxml.FXML;
import javafx.scene.chart.NumberAxis;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

public class StatisticsController {

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private PieChart pieChart;

    @FXML
    private TableView<Espace> tableView;

    @FXML
    private TableColumn<Espace, String> spaceColumn;

    private ServiceEspace serviceEspace;

    public void initialize() {
        serviceEspace = new ServiceEspace();
        spaceColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        displayEspaceStatistics();
    }

    public void displayEspaceStatistics() {
        try {
            List<Espace> espaces = serviceEspace.getAll();

            Map<Espace, Integer> reservationsPerEspace = new HashMap<>();
            for (Espace espace : espaces) {
                int reservationCount = serviceEspace.getReservationCountForEspace(espace.getIdEspace());
                reservationsPerEspace.put(espace, reservationCount);
            }

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            for (Espace espace : reservationsPerEspace.keySet()) {
                series.getData().add(new XYChart.Data<>(espace.getName(), reservationsPerEspace.get(espace)));
            }
            barChart.getData().add(series);

            // Définir le tickUnit de l'axe Y sur 1 pour afficher uniquement des nombres entiers
            ((NumberAxis) barChart.getYAxis()).setTickUnit(1);

            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            for (Espace espace : reservationsPerEspace.keySet()) {
                pieChartData.add(new PieChart.Data(espace.getName(), reservationsPerEspace.get(espace)));
            }
            pieChart.setData(pieChartData);

            tableView.setItems(FXCollections.observableArrayList(espaces));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
