package services;

import entities.Appartement;
import entities.Facture;
import utils.DataSource;
import java.sql.Date;
import java.time.LocalDate;

import javax.management.Query;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;
import java.time.LocalDate;
import java.util.List;


public class ServiceFacture implements IService<Facture>{
    Connection cnx = DataSource.getInstance().getConnexion();
    @Override
    public void ajouter(Facture p) throws SQLException {
        if (p.getAppartement() == null) {
            throw new IllegalArgumentException("Appartement object is null in the provided Facture object");
        }
        if (p == null) {
            throw new IllegalArgumentException("Facture object is null");
        }

        if (p.getDate() == null) {
            throw new IllegalArgumentException("Date is null in the provided Facture object");
        }

        // Récupérer l'idAppartement correspondant au numAppartement de la facture
        int idAppartement = getIdAppartementByNumAppartement(p.getAppartement().getNumAppartement());

        if (idAppartement == -1) {
            throw new IllegalArgumentException("Appartement with numAppartement " + p.getAppartement().getNumAppartement() + " does not exist.");
        }

        String sql = "INSERT INTO facture (numFacture, type, montant, descriptionFacture, date, idAppartement, consommation) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, p.getNumFacture());
            statement.setString(2, p.getType().toString());
            statement.setFloat(3, p.getMontant());
            statement.setString(4, p.getDescriptionFacture());
            statement.setDate(5, new java.sql.Date(p.getDate().getTime()));
            // Ne pas spécifier idAppartement ici, il sera déduit de la relation de clé étrangère
            statement.setInt(6, idAppartement); // Cela peut être commenté ou supprimé
            statement.setFloat(7, p.getConsomation());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = statement.getGeneratedKeys();
                while (rs.next()) {
                    p.setIdFacture(rs.getInt(1));
                }
                System.out.println("Facture added successfully.");
            } else {
                System.out.println("Failed to insert Facture.");
            }
        }
    }
    private int getIdAppartementByNumAppartement(int numAppartement) throws SQLException {
        String sql = "SELECT idAppartement FROM appartement WHERE numAppartement = ?";
        try (PreparedStatement statement = cnx.prepareStatement(sql)) {
            statement.setInt(1, numAppartement);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idAppartement");
                }
            }
        }
        return -1; // Retourne -1 si aucun appartement correspondant n'est trouvé
    }


    @Override
    public void modifier(Facture p) throws SQLException {
        String req = "UPDATE `facture` SET " +
                "`NumFacture`='" + p.getNumFacture() + "'," +
                "`date`='" + p.getDate() + "'," +
                "`type`='" + p.getType() + "'," +
                "`montant`='" + p.getMontant() + "'," +
                "`consommation`='" + p.getConsomation() + "', " + // Ajout de la virgule ici
                "`descriptionFacture`='" + p.getDescriptionFacture() + "' " +
                "WHERE `idFacture`=" + p.getIdFacture();

        Statement st = cnx.createStatement();
        st.executeUpdate(req);
        System.out.println("Facture modifié !");
    }


    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM facture WHERE idFacture = ?";

        try (PreparedStatement st = cnx.prepareStatement(req)) {
            st.setInt(1, id);
            int rowsAffected = st.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Facture supprimée !");
            } else {
                System.out.println("Aucune facture n'a été supprimée. Vérifiez l'ID de la facture.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de la facture : " + e.getMessage());
            throw e; // Propager l'exception pour la gérer dans la couche supérieure si nécessaire
        }
    }



    @Override
    public Facture getOneById(int id) throws SQLException {
        Facture facture = null;
        String req = "SELECT * FROM `facture` WHERE `idFacture`=?";

        try (PreparedStatement statement = cnx.prepareStatement(req)) {
            statement.setInt(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    facture = new Facture();

                    facture.setIdFacture(id);
                    facture.setNumFacture(rs.getInt("numFacture"));
                    facture.setDate(rs.getDate("date"));
                    facture.setType(Facture.Type.valueOf(rs.getString("type")));
                    facture.setMontant(rs.getFloat("montant"));
                    facture.setDescriptionFacture(rs.getString("descriptionFacture"));
                }
            }
        }

        return facture;
    }






    @Override
    public List<Facture> getAll() throws SQLException {
        List<Facture> factures = new ArrayList<>();
        String req = "SELECT f.*, a.numAppartement " +
                "FROM facture f " +
                "JOIN appartement a ON f.idAppartement = a.idAppartement";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                Facture facture = new Facture();
                facture.setIdFacture(rs.getInt("idFacture"));
                facture.setNumFacture(rs.getInt("numFacture"));
                facture.setDate(rs.getDate("date"));
                Facture.Type typeFacture = Facture.Type.valueOf(rs.getString("type"));
                facture.setType(typeFacture);
                facture.setMontant(rs.getFloat("montant"));
                facture.setMontant(rs.getFloat("consommation"));

                facture.setDescriptionFacture(rs.getString("descriptionFacture"));

                Appartement appartement = new Appartement();
                appartement.setNumAppartement(rs.getInt("numAppartement"));

                facture.setAppartement(appartement);

                factures.add(facture);
            }
        }

        return factures;
    }


    public Set<Facture> getAllForAppartement(Appartement appartement) throws SQLException {
        Set<Facture> factures = new HashSet<>();
        // Assurez-vous que l'objet appartement n'est pas null avant de l'utiliser
        if (appartement == null) {
            System.out.println("Appartement is null.");
            return factures; // ou lancez une exception appropriée selon vos besoins
        }

        String req = "SELECT f.*, a.numAppartement " +
                "FROM facture f " +
                "JOIN appartement a ON f.idAppartement = a.idAppartement " +
                "WHERE a.idAppartement = ?";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, appartement.getIdAppartement());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Facture facture = new Facture();
                    facture.setIdFacture(rs.getInt("idFacture"));
                    facture.setNumFacture(rs.getInt("numFacture"));
                    facture.setDate(rs.getDate("date"));
                    Facture.Type typeFacture = Facture.Type.valueOf(rs.getString("type"));
                    facture.setType(typeFacture);
                    facture.setMontant(rs.getFloat("montant"));
                    facture.setDescriptionFacture(rs.getString("descriptionFacture"));
                    facture.setMontant(rs.getFloat("consommation"));

                    Appartement appartementFacture = new Appartement();
                    appartementFacture.setNumAppartement(rs.getInt("numAppartement"));

                    facture.setAppartement(appartementFacture);
                    factures.add(facture);
                }
            } catch (SQLException ex) {
                ex.printStackTrace(); // Ajoutez cette ligne pour afficher les erreurs SQL
            }
        }

        return factures;
    }








}


























