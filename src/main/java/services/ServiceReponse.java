package services;



import entities.Reclamation;
import entities.Reponse;
import utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ServiceReponse implements IService<Reponse> {
    private final Connection cnx;
    private PreparedStatement pst;
    private static ServiceReponse instance;

    public ServiceReponse() {
        cnx = DataSource.getInstance().getConnexion();
    }

    public static ServiceReponse getInstance() {
        if (instance == null) {
            instance = new ServiceReponse();
        }
        return instance;
    }

    @Override
    public void ajouter(Reponse r) throws SQLException {
        String requete = "INSERT INTO reponse (IdRec, contenu, dateReponse) VALUES (?, ?, ?)";
        pst = cnx.prepareStatement(requete);
        pst.setInt(1, r.getReclamation().getIdRec());
        pst.setString(2, r.getContenu());
        pst.setDate(3, new java.sql.Date(r.getDateReponse().getTime()));
        pst.executeUpdate();
        System.out.println("Reponse ajoutée !");
    }

    @Override
    public void modifier(Reponse r) throws SQLException {
        String requete = "UPDATE reponse SET IdRec = ?, contenu = ?, dateReponse = ? WHERE idReponse = ?";
        pst = cnx.prepareStatement(requete);
        pst.setInt(1, r.getReclamation().getIdRec());
        pst.setString(2, r.getContenu());
        pst.setDate(3, new java.sql.Date(r.getDateReponse().getTime()));
        pst.setInt(4, r.getIdReponse());
        pst.executeUpdate();
        System.out.println("Reponse modifiée !");
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String requete = "DELETE FROM reponse WHERE idReponse = ?";
        pst = cnx.prepareStatement(requete);
        pst.setInt(1, id);
        pst.executeUpdate();
        System.out.println("Reponse supprimée !");
    }

    @Override
    public Reponse getOneById(int id) throws SQLException {
        Reponse r = null;
        String requete = "SELECT * FROM reponse WHERE idReponse = ?";
        pst = cnx.prepareStatement(requete);
        pst.setInt(1, id);
        ResultSet rst = pst.executeQuery();
        if (rst.next()) {
            r = new Reponse();
            r.setIdReponse(rst.getInt("idReponse"));
            r.setReclamation(ServiceReclamation.getInstance().getOneById(rst.getInt("IdRec")));
            r.setContenu(rst.getString("contenu"));
            r.setDateReponse(rst.getDate("dateReponse"));
        }
        return r;
    }
    public List<Reponse> getAllByReclamationId(int reclamationId) throws SQLException {
        List<Reponse> reponses = new ArrayList<>();
        String req = "SELECT * FROM reponse WHERE idRec = ?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, reclamationId);
            ResultSet rst = ps.executeQuery();
            while (rst.next()) {
                Reponse r = new Reponse();
                r.setIdReponse(rst.getInt("idReponse"));
                r.setContenu(rst.getString("contenu"));
                r.setDateReponse(rst.getDate("dateReponse"));
                // Assuming you have a method to retrieve Reclamation object by ID
                // r.setReclamation(serviceReclamation.getOneById(rst.getInt("idRec")));
                reponses.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reponses;
    }

    private Reclamation getReclamationById(int idRec) throws SQLException {
        Reclamation reclamation = null;
        String query = "SELECT * FROM reclamation WHERE idRec = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, idRec);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                reclamation = new Reclamation();
                // Populate Reclamation object
            }
        }
        return reclamation;
    }


    @Override
    public List<Reponse> getAll() throws SQLException {
        List<Reponse> reponses = new ArrayList<>();
        String requete = "SELECT * FROM reponse";
        pst = cnx.prepareStatement(requete);
        ResultSet rst = pst.executeQuery();
        while (rst.next()) {
            Reponse r = new Reponse();
            r.setIdReponse(rst.getInt("idReponse"));
            r.setReclamation(ServiceReclamation.getInstance().getOneById(rst.getInt("IdRec")));
            r.setContenu(rst.getString("contenu"));
            r.setDateReponse(rst.getDate("dateReponse"));
            reponses.add(r);
        }
        return (List<Reponse>)  reponses;
    }
}