package com.epam.kozhanbergenov.shop.dao.h2Dao;

import com.epam.kozhanbergenov.shop.dao.UserDao;
import com.epam.kozhanbergenov.shop.dao.exception.DaoException;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import com.epam.kozhanbergenov.shop.entity.Administrator;
import com.epam.kozhanbergenov.shop.entity.Client;
import com.epam.kozhanbergenov.shop.entity.User;
import com.epam.kozhanbergenov.shop.util.PasswordHashing;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class H2UserDao implements UserDao {
    private Connection connection;
    private static final Logger log = Logger.getLogger(H2UserDao.class);

    public H2UserDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(User user) throws DaoException {
        try {
            String login = user.getLogin();
            String password = user.getPassword();
            password = PasswordHashing.getHashValue(password);
            PreparedStatement stm = null;
            String sql;
            if (user instanceof Client) {
                String name = ((Client) user).getName();
                String surname = ((Client) user).getSurname();
                String address = ((Client) user).getAddress();
                String phoneNumber = ((Client) user).getPhoneNumber();
                boolean ban = ((Client) user).isBanned();
                sql = "INSERT INTO USER(LOGIN, PASSWORD, NAME,  SURNAME, ADDRESS, PHONENUMBER, ROLE, BAN) " +
                        "VALUES(?, ?, ?, ?, ?, ?, 'Client', ?)";
                stm = connection.prepareStatement(sql);
                stm.setString(1, login);
                stm.setString(2, password);
                stm.setString(3, name);
                stm.setString(4, surname);
                stm.setString(5, address);
                stm.setString(6, phoneNumber);
                stm.setBoolean(7, ban);
            }
            else {
                sql = "INSERT INTO USER(LOGIN, PASSWORD, NAME,  SURNAME, ADDRESS, PHONENUMBER, ROLE, BAN) " +
                        "VALUES(?, ?, NULL, NULL, NULL, NULL, 'Administrator', NULL)";
                stm = connection.prepareStatement(sql);
                stm.setString(1, login);
                stm.setString(2, password);
            }
            stm.executeUpdate();
            if (stm != null)
                stm.close();
            ConnectionPool.returnConnection(connection);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public User read(int id) throws DaoException {
        try {
            String sql = "SELECT * FROM USER WHERE ID = ?;";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();
            rs.next();
            User user;
            if (rs.getString("role").equals("Administrator")) {
                user = new Administrator();
            } else {
                user = new Client();
                ((Client) user).setName(rs.getString("name"));
                ((Client) user).setSurname(rs.getString("surname"));
                ((Client) user).setAddress(rs.getString("address"));
                ((Client) user).setPhoneNumber(rs.getString("phoneNumber"));
                ((Client) user).setBanned(rs.getBoolean("ban"));
            }
            user.setId(rs.getInt("id"));
            user.setLogin(rs.getString("login"));
            user.setPassword(rs.getString("password"));
            if (stm != null)
                stm.close();
            ConnectionPool.returnConnection(connection);
            return user;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void update(User user) throws DaoException {
        try {
            int id = user.getId();
            String login = user.getLogin();
            String password = user.getPassword();
            String name = null;
            String surname = null;
            String address = null;
            String phoneNumber = null;
            boolean ban = false;
            if (user instanceof Client) {
                name = ((Client) user).getName();
                surname = ((Client) user).getSurname();
                address = ((Client) user).getAddress();
                phoneNumber = ((Client) user).getPhoneNumber();
                ban = ((Client) user).isBanned();
            }
            String sql = "UPDATE USER SET LOGIN =?, PASSWORD=?, NAME=?, SURNAME=?, ADDRESS=?, PHONENUMBER=?, BAN=? WHERE ID = ?;";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, login);
            stm.setString(2, password);
            stm.setString(3, name);
            stm.setString(4, surname);
            stm.setString(5, address);
            stm.setString(6, phoneNumber);
            stm.setBoolean(7, ban);
            stm.setInt(8, id);
            stm.executeUpdate();
            if (stm != null)
                stm.close();
            ConnectionPool.returnConnection(connection);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean checkLogin(String login) {
        String sql = "SELECT * FROM User where login= ?;";
        ResultSet rs = null;
        try {
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, login);
            rs = stm.executeQuery();
            while (rs.next()) {
                return false;
            }
            if (stm != null)
                stm.close();
        } catch (SQLException e) {
            log.error(e);
        }
        ConnectionPool.returnConnection(connection);
        return true;
    }

    @Override
    public List<User> getAll() throws DaoException {
        try {
            String sql = "SELECT * FROM USER;";
            Statement stm = connection.createStatement();
            ResultSet rs = stm.executeQuery(sql);
            List<User> list = new ArrayList<>();
            while (rs.next()) {
                User user;
                if (rs.getString("role").equals("Administrator")) {
                    user = new Administrator();
                } else {
                    user = new Client();
                    ((Client) user).setName(rs.getString("name"));
                    ((Client) user).setSurname(rs.getString("surname"));
                    ((Client) user).setAddress(rs.getString("address"));
                    ((Client) user).setPhoneNumber(rs.getString("phoneNumber"));
                    ((Client) user).setBanned(rs.getBoolean("ban"));
                }

                user.setId(rs.getInt("id"));
                user.setLogin(rs.getString("login"));
                user.setPassword(rs.getString("password"));
                list.add(user);
            }
            if (stm != null)
                stm.close();
            ConnectionPool.returnConnection(connection);
            return list;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void returnConnection() {
        if (connection != null)
            ConnectionPool.returnConnection(connection);
    }

}
