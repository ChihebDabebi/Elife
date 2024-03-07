package services;
import entities.Admin;
import entities.User;
import entities.Concierge;
import entities.Resident;
import services.IService;
import utils.DataSource;
import entities.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceUser implements IService<User> {

    Connection cnx = DataSource.getInstance().getConnexion();


    @Override
    public void ajouter(User r) throws SQLException {
        if (!r.getMail().contains("@")) {
            System.out.println("Erreur : L'email doit contenir un '@'.");
            return;
        }

        String sql = "INSERT INTO user (id, nom, prenom, mail, password, number, role) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = cnx.prepareStatement(sql)) {
            pstmt.setInt(1, r.getId());
            pstmt.setString(2, r.getNom());
            pstmt.setString(3, r.getPrenom());
            pstmt.setString(4, r.getMail());
            pstmt.setString(5, r.getPassword());
            pstmt.setInt(6, r.getNumber());
            pstmt.setString(7, r.getRole().toString());



            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public  void modifier(User r)throws SQLException {
        try {
            String requete = "UPDATE utilisateurs SET nom=?, prenom=?, mot_de_passe=?, email=?  WHERE id=?";
            PreparedStatement pst = cnx.prepareStatement(requete);
            pst.setString(1, r.getNom());
            pst.setString(2, r.getPrenom());
            pst.setString(3, r.getPassword());
            pst.setString(4, r.getMail());
            pst.setInt(5, r.getId());  // Utilisation de l'ID pour identifier l'utilisateur à mettre à jour
            pst.executeUpdate();
            System.out.println("Mise à jour avec succès");
        } catch (SQLException ex) {
            System.out.println("Erreur lors de la mise à jour : " + ex.getMessage());
        }
    }


    @Override
    public void supprimer(int id)throws SQLException {
        String sql = "DELETE FROM user WHERE id = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User deleted successfully!");
            } else {
                System.out.println("No user found with the given id.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }


    public User getOneById(int id) throws SQLException{
        User user = null;
        String sql = "SELECT * FROM user WHERE id = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                if ("Admin".equals(role)) {
                    user = new Admin();
                } else if ("Resident".equals(role)) {
                    user = new Resident();
                } else if ("Concierge".equals(role)) {
                    user = new Concierge();
                } else {
                    user = new User();
                }
                user.setId(rs.getInt("id"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setMail(rs.getString("mail"));
                user.setPassword(rs.getString("password"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return user;
    }

    @Override
    public List<User> getAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user";

        try (PreparedStatement pstmt = cnx.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setMail(rs.getString("mail"));
                user.setPassword(rs.getString("password"));
                user.setNumber(rs.getInt("number"));

                //Role role = Role.valueOf();
                //rs.getString("role")
                user.setRole(Role.valueOf(rs.getString("role")));
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return users;
    }
    public void changePassword(String mail, String newPassword) {
        String query = "UPDATE user SET password=? WHERE mail=?";
        try {
            PreparedStatement preparedStatement = cnx.prepareStatement(query);
            preparedStatement.setString(1, newPassword);
            preparedStatement.setString(2, mail);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Mot de passe modifié avec succès");
            } else {
                System.out.println("Aucun utilisateur trouvé avec cet email");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean doesEmailExist(String mail) {
        String query = "SELECT COUNT(*) AS count FROM user WHERE mail=?";
        try {
            PreparedStatement preparedStatement = cnx.prepareStatement(query);
            preparedStatement.setString(1, mail);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

}