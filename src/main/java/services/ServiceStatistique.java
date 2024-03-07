package services;

import entities.Facture;
import utils.DataSource;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ServiceStatistique {
    // Coût par unité d'eau consommée
    public static final float WATER_COST_PER_UNIT = 0.1f;

    // Coût par unité de gaz consommée
    public static final float GAS_COST_PER_UNIT = 0.2f;

    // Coût par unité d'électricité consommée
    public static final float ELECTRICITY_COST_PER_UNIT = 0.15f;

    // Coût par unité de déchets gérés
    public static final float WASTE_MANAGEMENT_COST_PER_UNIT = 0.05f;
    Connection cnx = DataSource.getInstance().getConnexion();

    public Map<String, Float> getStatisticsByFloor(int floorNumber, LocalDate startDate, LocalDate endDate, Facture.Type type) throws SQLException, SQLException {
        Map<String, Float> statistics = new HashMap<>();

        String sql = "SELECT COUNT(a.idAppartement) AS NombreAppartements, " +
                "SUM(f.montant) AS MontantTotal " +
                "FROM facture f " +
                "JOIN appartement a ON f.idAppartement = a.idAppartement " +
                "WHERE a.nbrEtage = ? AND f.date BETWEEN ? AND ? AND f.type = ?";

        try (PreparedStatement statement = cnx.prepareStatement(sql)) {
            statement.setInt(1, floorNumber);
            statement.setDate(2, convertToLocalDateViaSqlDate(startDate));
            statement.setDate(3, convertToLocalDateViaSqlDate(endDate));
            statement.setString(4, type.toString());

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    int numberOfApartments = rs.getInt("NombreAppartements");
                    float totalConsumption = rs.getFloat("MontantTotal");

                    statistics.put("Number of Apartments", (float) numberOfApartments);
                    statistics.put("Total Consumption", totalConsumption);
                } else {
                    // Handle case when no data available
                }
            }
        }
        return statistics;
    }

    public Map<String, Float> getStatisticsForAllApartments(LocalDate startDate, LocalDate endDate, Facture.Type type) throws SQLException {
        Map<String, Float> statistics = new HashMap<>();

        String sql = "SELECT COUNT(a.idAppartement) AS NombreAppartements, " +
                "SUM(f.montant) AS MontantTotal " +
                "FROM facture f " +
                "JOIN appartement a ON f.idAppartement = a.idAppartement " +
                "WHERE f.date BETWEEN ? AND ? AND f.type = ?";

        try (PreparedStatement statement = cnx.prepareStatement(sql)) {
            statement.setDate(1, convertToLocalDateViaSqlDate(startDate));
            statement.setDate(2, convertToLocalDateViaSqlDate(endDate));
            statement.setString(3, type.toString());

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    int numberOfApartments = rs.getInt("NombreAppartements");
                    float totalConsumption = rs.getFloat("MontantTotal");

                    statistics.put("Number of Apartments", (float) numberOfApartments);
                    statistics.put("Total Consumption", totalConsumption);
                } else {
                    // Handle case when no data available
                }
            }
        }
        return statistics;
    }
    public float calculateConsumptionCost(float consumptionValue, Facture.Type type) {
        float cost = 0.0f;

        // Calculer le montant en fonction de la valeur de la consommation et du type de facture
        switch (type) {
            case Eau:
                // Calculer le montant pour la consommation d'eau
                cost = consumptionValue * WATER_COST_PER_UNIT;
                break;
            case Gaz:
                // Calculer le montant pour la consommation de gaz
                cost = consumptionValue * GAS_COST_PER_UNIT;
                break;
            case Electricite:
                // Calculer le montant pour la consommation d'électricité
                cost = consumptionValue * ELECTRICITY_COST_PER_UNIT;
                break;
            case Dechets:
                // Calculer le montant pour la gestion des déchets
                cost = consumptionValue * WASTE_MANAGEMENT_COST_PER_UNIT;
                break;
            default:
                // Gérer le cas par défaut ou les types de facture inconnus
                break;
        }

        return cost;
    }


    private Date convertToLocalDateViaSqlDate(LocalDate dateToConvert) {
        return java.sql.Date.valueOf(dateToConvert);
    }
    public Map<String, Float> getStatisticsForApartment(LocalDate startDate, LocalDate endDate, Facture.Type type, int apartmentId) throws SQLException {
        Map<String, Float> statistics = new HashMap<>();

        String sql = "SELECT COUNT(f.idFacture) AS NombreFactures, " +
                "SUM(f.montant) AS MontantTotal " +
                "FROM facture f " +
                "WHERE f.date BETWEEN ? AND ? AND f.type = ? AND f.idAppartement = ?";

        try (PreparedStatement statement = cnx.prepareStatement(sql)) {
            statement.setDate(1, convertToLocalDateViaSqlDate(startDate));
            statement.setDate(2, convertToLocalDateViaSqlDate(endDate));
            statement.setString(3, type.toString());
            statement.setInt(4, apartmentId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    int numberOfInvoices = rs.getInt("NombreFactures");
                    float totalAmount = rs.getFloat("MontantTotal");

                    statistics.put("Number of Invoices", (float) numberOfInvoices);
                    statistics.put("Total Amount", totalAmount);
                } else {
                    // Handle case when no data available
                }
            }
        }
        return statistics;
    }
}
