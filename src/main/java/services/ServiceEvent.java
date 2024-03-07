package services;

import entities.Espace;
import entities.Event;
import  entities.User;
import utils.DataSource;

import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ServiceEvent implements IService<Event> {
    Connection cnx = DataSource.getInstance().getConnexion();

    public void ajouter(Event event) throws SQLException {
        // Vérifier si l'espace est occupé dans cette date
        if (isEspaceOccupied(event.getDate(), event.getEspace().getIdEspace())) {
            throw new IllegalArgumentException("L'espace est déjà occupé à cette date.");
        }

        String req = "INSERT INTO event (title, date, nbrPersonne, listeInvites, idEspace, Id) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, event.getTitle());
            pstmt.setTimestamp(2, new java.sql.Timestamp(event.getDate().getTime()));
            pstmt.setInt(3, event.getNbrPersonne());
            pstmt.setString(4, event.getListeInvites());
            pstmt.setInt(5, event.getEspace().getIdEspace());
            pstmt.setInt(6, event.getUser().getId()); // Ajout de l'ID de l'utilisateur

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                while (rs.next()) {
                    event.setIdEvent(rs.getInt(1));
                }
                ServiceEspace.marquerEspaceCommeReserve(event.getEspace().getIdEspace());
                System.out.println("Event added !");
            } else {
                System.out.println("Failed to insert Event.");
            }
        }
    }
/*
    public Set<Event> getEventsByUserId(User user) throws SQLException {
        Set<Event> events = new HashSet<>();
        String req = "SELECT * from event WHERE  id = ";

        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
            pstmt.setInt(1, user.getId()); // Supposons que l'identifiant de l'utilisateur est accessible via getId()
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Event event = new Event();
                    event.setIdEvent(rs.getInt("idEvent"));
                    event.setTitle(rs.getString("title"));
                    event.setDate(rs.getDate("date"));
                    event.setNbrPersonne(rs.getInt("nbrPersonne"));
                    event.setListeInvites(rs.getString("listeInvites"));

                    // Création de l'objet Espace et assignation au nouvel événement
                    Espace espace = new Espace();
                    espace.setIdEspace(rs.getInt("idEspace"));
                    espace.setName(rs.getString("name"));
                    event.setEspace(espace);

                    events.add(event);
                }
            }
        }
        return events;
    }

*/public Set<Event> getEventsByUserId(User user) throws SQLException {
    Set<Event> events = new HashSet<>();

    // Assurez-vous que l'objet utilisateur n'est pas null avant de l'utiliser
    if (user == null) {
        System.out.println("User is null.");
        return events; // ou lancez une exception appropriée selon vos besoins
    }

    String req = "SELECT e.*, es.name AS espace_name FROM event e " +
            "INNER JOIN espace es ON e.idEspace = es.idEspace " +
            "WHERE e.id = ?";

    try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
        pstmt.setInt(1, user.getId()); // Supposons que l'identifiant de l'utilisateur est accessible via getId()
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Event event = new Event();
                event.setIdEvent(rs.getInt("idEvent"));
                event.setTitle(rs.getString("title"));
                event.setDate(rs.getDate("date"));
                event.setNbrPersonne(rs.getInt("nbrPersonne"));
                event.setListeInvites(rs.getString("listeInvites"));

                // Création de l'objet Espace et assignation au nouvel événement
                Espace espace = new Espace();
                espace.setIdEspace(rs.getInt("idEspace"));
                espace.setName(rs.getString("espace_name")); // Récupération du nom de l'espace

                //    espace.setName(rs.getString("name"));
                event.setEspace(espace);

                events.add(event);
            }
        }
    }

    return events;
}



    private boolean isEspaceOccupied(java.util.Date date, int idEspace) throws SQLException {
        String sql = "SELECT COUNT(*) FROM event WHERE date = ? AND idEspace = ?";
        try (PreparedStatement statement = cnx.prepareStatement(sql)) {
            statement.setTimestamp(1, new Timestamp(date.getTime()));
            statement.setInt(2, idEspace);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public Espace getEspaceByName(String name) throws SQLException {
        String req = "SELECT * FROM espace WHERE name = ?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Espace espace = new Espace();
                    espace.setIdEspace(rs.getInt("idEspace"));
                    espace.setName(rs.getString("name"));
                    // Ajoutez ici d'autres attributs de l'espace si nécessaire
                    return espace;
                }
            }
        }
        return null; // Retourne null si aucun espace avec ce nom n'est trouvé
    }

    public Set<String> getAllEspacesNames() throws SQLException {
        Set<String> espacesNames = new HashSet<>();
        String req = "SELECT name FROM espace";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                espacesNames.add(rs.getString("name"));
            }
        }

        return espacesNames;
    }

    @Override
    public List<Event> getAll() throws SQLException {
        List<Event> events = new ArrayList<>();
        String req = "SELECT e.*, espace.name AS name " +
                "FROM event e " +
                "INNER JOIN espace ON e.idEspace = espace.idEspace";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                Event event = new Event();
                event.setIdEvent(rs.getInt("idEvent"));
                event.setTitle(rs.getString("title"));
                event.setDate(rs.getDate("date"));
                event.setNbrPersonne(rs.getInt("nbrPersonne"));
                event.setListeInvites(rs.getString("listeInvites"));

                // Création de l'objet Espace et assignation au nouvel événement
                Espace espace = new Espace();
                espace.setIdEspace(rs.getInt("idEspace"));
                espace.setName(rs.getString("name"));
                event.setEspace(espace);

                events.add(event);
            }
        }

        return events;
    }

    @Override
    public void modifier(Event event) throws SQLException {
        if (isEspaceOccupied(event.getDate(), event.getEspace().getIdEspace())) {
            throw new IllegalArgumentException("L'espace est déjà occupé à cette date.");
        }
        String req = "UPDATE event SET title = ?, date = ?, nbrPersonne = ?, listeInvites = ? WHERE idEvent = ?";
        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
            pstmt.setString(1, event.getTitle());
            pstmt.setTimestamp(2, new Timestamp(event.getDate().getTime())); // Utilisation de Timestamp pour java.sql.Date
            pstmt.setInt(3, event.getNbrPersonne());
            pstmt.setString(4, event.getListeInvites());
            pstmt.setInt(5, event.getIdEvent());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Event updated successfully.");
            } else {
                System.out.println("Failed to update event.");
            }
        }
    }

    @Override
    public void supprimer(int idEvent) throws SQLException {
        String req = "DELETE FROM event WHERE idEvent = ?";
        try (PreparedStatement st = cnx.prepareStatement(req)) {
            st.setInt(1, idEvent);
            int rowsAffected = st.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Event deleted !");
            } else {
                System.out.println("No event found with ID: " + idEvent);
            }
        } catch (SQLException e) {
            throw new SQLException("Error deleting event", e);
        }
    }

    @Override
    public Event getOneById(int id) throws SQLException {
        Event event = null;
        String req = "SELECT e.*, espace.name AS espace_name " +
                "FROM event e " +
                "INNER JOIN espace ON e.idEspace = espace.idEspace " +
                "WHERE e.idEvent = ?";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    event = new Event();
                    event.setIdEvent(rs.getInt("idEvent"));
                    event.setTitle(rs.getString("title"));
                    event.setDate(rs.getDate("date"));
                    event.setNbrPersonne(rs.getInt("nbrPersonne"));
                    event.setListeInvites(rs.getString("listeInvites"));

                    // Création de l'objet Espace et assignation à l'événement
                    Espace espace = new Espace();
                    espace.setIdEspace(rs.getInt("idEspace"));
                    espace.setName(rs.getString("espace_name"));
                    event.setEspace(espace);

                }
            }
        }

        return event;
    }

    public Set<Event> getEventsForMonth(int idEspace, YearMonth yearMonth) throws SQLException {
        Set<Event> eventsForMonth = new HashSet<>();
        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();

        String req = "SELECT * FROM event WHERE idEspace = ? AND date BETWEEN ? AND ?";
        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
            pstmt.setInt(1, idEspace);
            pstmt.setDate(2, Date.valueOf(firstDayOfMonth));
            pstmt.setDate(3, Date.valueOf(lastDayOfMonth));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Event event = new Event();
                    event.setIdEvent(rs.getInt("idEvent"));
                    event.setTitle(rs.getString("title"));
                    event.setDate(rs.getDate("date"));
                    event.setNbrPersonne(rs.getInt("nbrPersonne"));
                    event.setListeInvites(rs.getString("listeInvites"));
                    // Créez un objet Espace et définissez son ID à partir de la colonne de la base de données
                    Espace espace = new Espace();
                    espace.setIdEspace(rs.getInt("idEspace"));
                    event.setEspace(espace);

                    eventsForMonth.add(event);
                }
            }
        }
        return eventsForMonth;
    }
}
