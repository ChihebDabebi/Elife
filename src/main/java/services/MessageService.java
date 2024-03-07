package services;

import controllers.MessageController;
import entities.Discussion;
import entities.Message;
import entities.User;
import javafx.scene.image.Image;
import utils.DataSource;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageService implements IService<Message>{
    Connection connexion;
    private FileInputStream fis ;
    public static File file ;
    public MessageService(){
        connexion = DataSource.getInstance().getConnexion();
    }
    public int getDisId(String titre) throws SQLException {
        String req = "SELECT id FROM discussion WHERE titre = ?";
        try (PreparedStatement statement = connexion.prepareStatement(req)) {
            statement.setString(1, titre);

            try  {
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                } else {
                    throw new IllegalArgumentException("Discussion not found for titre: " + titre);
                }
            }catch(Exception e ){
                System.out.println(e.getMessage());
            }
        } catch (SQLException e) {
            // Log or re-throw the exception
            throw new SQLException("Error finding discussion ID: " + e.getMessage(), e);
        }
        return 0 ;
    }
    @Override
    public void ajouter(Message message) throws SQLException {
        String req = "INSERT INTO `message` (`contenu`, `date_envoi`, `emetteur_id`,`discussion_id`,`image`)"
                + "VALUES (?,?,?,?,?)";
        try{
            PreparedStatement pst = connexion.prepareStatement(req);
            pst.setString(1, message.getContenu());
            pst.setTimestamp(2, message.getTimeStamp_envoi());
            pst.setInt(3,message.getEmetteur().getId());
            pst.setInt(4, MessageController.discuId);
            if(file == null){

                pst.setNull(5, java.sql.Types.BLOB);

            }else{
                fis = new FileInputStream(file);

                pst.setBinaryStream(5, fis);

            }
            pst.executeUpdate();
        }catch(SQLException e){
            System.out.println(e.getMessage());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void modifier(Message p) throws SQLException {
        String req = "UPDATE message" +
                "                SET contenu = ?" +
                "               WHERE id = ?";
        PreparedStatement pst = connexion.prepareStatement(req);
        pst.setString(1,p.getContenu());
        pst.setInt(2,p.getId());
        pst.executeUpdate();

    }

    @Override
    public void supprimer(int id) throws SQLException {
        try {
            PreparedStatement pre = connexion.prepareStatement("Delete from message where id=? ;");
            pre.setInt(1, id);
            if (pre.executeUpdate() != 0) {
                System.out.println("message Deleted");

            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public Message getOneById(int id) throws SQLException {
        return null;
    }

    @Override
    public List<Message> getAll() throws SQLException {
        List<Message> messages  = new ArrayList<>();
        String req = "SELECT message.contenu, message.date_envoi, user.nom AS emetteur  " +
                "FROM message" +
                " JOIN user ON message.emetteur_id = user.id " +
                "JOIN discussion ON message.discussion_id = discussion.id;";


        Statement stm = connexion.createStatement();
        ResultSet rst = stm.executeQuery(req);

        while (rst.next()) {
            String contenu =  rst.getString(1);
            Timestamp date = rst.getTimestamp(2);
            User user1 = new User(rst.getString(3));

            Message message = new Message(contenu,date,user1);
            messages.add(message);
        }
        return messages;
    }
    public List<Message> afficherByDiscussionId(int id)  {
        List<Message> messages  = new ArrayList<>();
        String req = "SELECT message.contenu, message.date_envoi, user.nom AS emetteur ,message.id,message.image,user.id  " +
                "FROM message" +
                " JOIN user ON message.emetteur_id = user.id " +
                "Where message.discussion_id = ?;";
        try {
        PreparedStatement pre = connexion.prepareStatement(req);
        pre.setInt(1, id);
        ResultSet rst = pre.executeQuery();

        while (rst.next()) {
            String contenu = rst.getString(1);
            Timestamp date = rst.getTimestamp(2);
            int idUser = rst.getInt(6);
            User user1 = new User(idUser,rst.getString(3));
            int ident = rst.getInt(4);


            InputStream is = rst.getBinaryStream(5);
            if (is != null) {
                OutputStream os = new FileOutputStream(new File("photo.jpg"));
                byte[] content = new byte[1024];
                int size = 0;

                while ((size = is.read(content)) != -1) {
                    os.write(content, 0, size);
                }
                os.close();
                is.close();
                Image image = new Image("file:photo.jpg",100,150,true,true);
                Message message1 = new Message(ident, contenu, date, user1,image);
                messages.add(message1);
            }else{

                    System.out.println("null");
                    Message message = new Message(ident, contenu, date, user1);
                    messages.add(message);
                }}
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }catch(FileNotFoundException e ){
        System.out.println(e.getMessage());
        }catch(IOException e ){
        System.out.println(e.getMessage());
    }

        return messages;

    }

}
