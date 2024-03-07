package services;

import entities.Espace;
import utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ServiceEspace implements IService<Espace> {
    Connection cnx = DataSource.getInstance().getConnexion();

    @Override
    public void ajouter(Espace espace) {
        String req = "INSERT INTO `espace` (`name`, `etat`, `capacite`, `description`) " +
                "VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement st = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
            st.setString(1, espace.getName());
            st.setString(2, espace.getEtat().name()); // Utiliser le nom de l'état de l'énumération
            st.setInt(3, espace.getCapacite());
            st.setString(4, espace.getDescription());
            st.executeUpdate();

            // Récupérer l'ID généré automatiquement
            ResultSet generatedKeys = st.getGeneratedKeys();
            if (generatedKeys.next()) {
                int idEspace = generatedKeys.getInt(1);
                espace.setIdEspace(idEspace);
            }

            System.out.println("Espace ajouté !");
        } catch (SQLException e) {
            System.out.println(" cc" +e.getMessage());
        }
    }
    public static void marquerEspaceCommeReserve(int idEspace) throws SQLException {
        ServiceEspace serviceEspace = new ServiceEspace(); // Créer une instance du serviceEspace
        Espace espace = serviceEspace.getOneById(idEspace); // Utiliser l'instance pour appeler la méthode
        if (espace != null) {
            espace.setEtat(Espace.Etat.RESERVE);
            serviceEspace.modifier(espace);
        }
    }


    public List<Espace> getAll() throws SQLException {
        List<Espace> espaces = new ArrayList<>() {
        };

        String req = "SELECT * FROM espace";

        Statement st = cnx.createStatement();
        ResultSet res = st.executeQuery(req);
        while (res.next()) {
            int idEspace = res.getInt("idEspace");
            String name = res.getString("name");
            String etat = res.getString("etat");
            int capacite = res.getInt("capacite");
            String description = res.getString("description");

            Espace e = new Espace(name, Espace.Etat.valueOf(etat), capacite, description); // Convertir le nom de l'état en enum
            e.setIdEspace(idEspace);
            espaces.add(e);
        }

        return espaces;
    }

    @Override
    public void modifier(Espace e) throws SQLException {
        String req = "UPDATE `espace` SET " +
                "`name`=?," +
                "`etat`=?," +
                "`capacite`=?," +
                "`description`=?" +
                " WHERE `idEspace`=?";
        try {
            PreparedStatement st = cnx.prepareStatement(req);
            st.setString(1, e.getName());
            st.setString(2, e.getEtat().name()); // Utiliser le nom de l'état de l'énumération
            st.setInt(3, e.getCapacite());
            st.setString(4, e.getDescription());
            st.setInt(5, e.getIdEspace());
            st.executeUpdate();
            System.out.println("Espace mis à jour !");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public Espace getOneById(int id) throws SQLException {
        Espace espaceById = null;

        PreparedStatement PS = cnx.prepareStatement("SELECT * FROM espace WHERE idEspace=?");
        PS.setInt(1, id);
        ResultSet res = PS.executeQuery();

        if (res.next()) {
            String name = res.getString("name");
            String etat = res.getString("etat");
            int capacite = res.getInt("capacite");
            String description = res.getString("description");
            espaceById = new Espace(name, Espace.Etat.valueOf(etat), capacite, description); // Convertir le nom de l'état en enum
            espaceById.setIdEspace(id);
        }

        return espaceById;
    }

    @Override
    public void supprimer(int idEspace) throws SQLException {
        PreparedStatement PS = cnx.prepareStatement("DELETE FROM espace WHERE idEspace=?");
        PS.setInt(1, idEspace);
        PS.executeUpdate();
        System.out.println("Espace supprimé !");
    }
    public int getReservationCountForEspace(int idEspace) throws SQLException {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM event WHERE idEspace = ?";
        try (PreparedStatement statement = cnx.prepareStatement(sql)) {
            statement.setInt(1, idEspace);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        }
        return count;
    }


}