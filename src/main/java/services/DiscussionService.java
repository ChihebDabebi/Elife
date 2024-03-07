package services;

import entities.Discussion;
import entities.User;
import utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiscussionService implements IService<Discussion> {

    Connection connexion ;

    public DiscussionService(){
        connexion = DataSource.getInstance().getConnexion();
    }

    @Override
    public void ajouter(Discussion discussion) throws SQLException {
        String req = "INSERT INTO `discussion` (`titre`, `date_creation`, `createur_id`,`description`,`color_code`)"
                + "VALUES (?,?,?,?,?)";
        try{
            PreparedStatement pst = connexion.prepareStatement(req);
            pst.setString(1, discussion.getTitre());
            Timestamp currentTimestamp = new Timestamp( System.currentTimeMillis());
            pst.setTimestamp(2, currentTimestamp);
            pst.setInt(3, discussion.getCreateur().getId());
            pst.setString(4,discussion.getDescription());
            pst.setString(5,discussion.getColor());
            pst.executeUpdate();


        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifier(Discussion p) throws SQLException {
        String req = "UPDATE discussion" +
                " SET titre = ?, description = ?, color_code = ?" +
                "WHERE id = ?;";
        try{
            PreparedStatement ps = connexion.prepareStatement(req);
            ps.setString(1,p.getTitre());
            ps.setInt(4,p.getId());
            ps.setString(2,p.getDescription());
            ps.setString(3,p.getColor());
            ps.executeUpdate();

        }catch(SQLException e ){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        try {
            PreparedStatement pre = connexion.prepareStatement("Delete from discussion where id=? ;");
            pre.setInt(1, id);
            if (pre.executeUpdate() != 0) {
                System.out.println("discussion Deleted");

            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public Discussion getOneById(int id) throws SQLException {
        String req = "SELECT discussion.titre, discussion.date_creation, discussion.description, discussion.id, user.nom , user.id,discussion.color_code" +
                " FROM discussion" +
                " INNER JOIN user ON discussion.createur_id = user.id" +
                " WHERE discussion.id = ?";
        PreparedStatement pst = connexion.prepareStatement(req);
        pst.setInt(1,id);
        ResultSet rst = pst.executeQuery();
        Discussion discussion = new Discussion();

        while(rst.next()){
            String titre = rst.getString(1);
            Timestamp date = rst.getTimestamp(2);
            String description = rst.getString(3);
            int identifiant = rst.getInt(4);
            String nom =  rst.getString(5);
            int ident = rst.getInt(6);
            String color = rst.getString(7);
            User user = new User(ident,nom);
            discussion.setId(identifiant);
            discussion.setTitre(titre);
            discussion.setTimeStampCreation(date);
            discussion.setDescription(description);
            discussion.setCreateur(user);
            discussion.setColor(color);
        }
        return discussion;
    }



    @Override
    public List<Discussion> getAll() throws SQLException {
        List<Discussion> discussions  = new ArrayList<>();
        String req = "SELECT d.id,d.titre, d.date_creation, u.nom , u.id " +
                "FROM discussion d " +
                "JOIN user u ON u.id = d.createur_id;";


            Statement stm = connexion.createStatement();
            ResultSet rst = stm.executeQuery(req);

            while (rst.next()) {
              int id =  rst.getInt(1);
              String title =  rst.getString(2);
              Timestamp date = rst.getTimestamp(3);
              User user1 = new User(rst.getInt(5),rst.getString(4));
            Discussion discussion = new Discussion(id,title,date,user1);
            discussions.add(discussion);
            }
        return discussions;
    }
}
