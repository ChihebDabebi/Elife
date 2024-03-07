package controllers;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import javafx.scene.layout.FlowPane;

import entities.Espace;
import entities.Event;
import services.ServiceEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class CalendrierController implements Initializable {

    @FXML private Text year;
    @FXML private Text month;
    @FXML private FlowPane calendar;

    private ServiceEvent eventService;
    private Espace selectedEspace;
    private YearMonth currentYearMonth;

    // Méthode pour sélectionner un espace
    public void selectEspace(Espace espace) {
        this.selectedEspace = espace;
        drawCalendar(); // Redessiner le calendrier pour l'espace sélectionné
    }

    // Méthode d'initialisation du contrôleur
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        eventService = new ServiceEvent(); // Initialiser le service d'événement
        currentYearMonth = YearMonth.now(); // Définir l'année et le mois actuels
        drawCalendar(); // Dessiner le calendrier
    }

    // Méthode pour reculer d'un mois
    @FXML
    void backOneMonth(ActionEvent event) {
        currentYearMonth = currentYearMonth.minusMonths(1);
        drawCalendar(); // Redessiner le calendrier
    }

    // Méthode pour avancer d'un mois
    @FXML
    void forwardOneMonth(ActionEvent event) {
        currentYearMonth = currentYearMonth.plusMonths(1);
        drawCalendar(); // Redessiner le calendrier
    }

    // Méthode pour dessiner le calendrier
    private void drawCalendar() {
        year.setText(String.valueOf(currentYearMonth.getYear()));
        month.setText(String.valueOf(currentYearMonth.getMonth()));

        int daysInMonth = currentYearMonth.lengthOfMonth();

        calendar.getChildren().clear();
        Map<LocalDate, List<Event>> eventMap = getAllEventsForMonth(currentYearMonth);

        for (int day = 1; day <= daysInMonth; day++) {
            StackPane stackPane = new StackPane();

            Rectangle rectangle = new Rectangle(40, 40);
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setStroke(Color.BLACK);
            stackPane.getChildren().add(rectangle);
            Text dayText = new Text(String.valueOf(day));
            stackPane.getChildren().add(dayText);
            LocalDate currentDate = currentYearMonth.atDay(day);
            List<Event> eventsForDate = eventMap.getOrDefault(currentDate, new ArrayList<>());
            if (!eventsForDate.isEmpty()) {
                rectangle.setFill(Color.LIGHTBLUE);
            }
            calendar.getChildren().add(stackPane); // Ajouter le stackPane au FlowPane
        }
    }

    // Méthode pour obtenir tous les événements pour un mois donné
    private Map<LocalDate, List<Event>> getAllEventsForMonth(YearMonth yearMonth) {
        Map<LocalDate, List<Event>> eventMap = new HashMap<>();
        try {
            if (selectedEspace != null) { // Vérifier si selectedEspace n'est pas nul
                Set<Event> events = eventService.getEventsForMonth(selectedEspace.getIdEspace(), yearMonth);
                for (Event event : events) {
                    LocalDate eventDate = event.getDate().toLocalDate();
                    if (eventDate.getMonth() == yearMonth.getMonth()) {
                        eventMap.computeIfAbsent(eventDate, k -> new ArrayList<>()).add(event);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to fetch events from the database.");
        }
        return eventMap;
    }

    // Méthode pour afficher une alerte
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}