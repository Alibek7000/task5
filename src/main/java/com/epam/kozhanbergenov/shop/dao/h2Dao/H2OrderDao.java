package com.epam.kozhanbergenov.shop.dao.h2Dao;

import com.epam.kozhanbergenov.shop.dao.H2DaoFactory;
import com.epam.kozhanbergenov.shop.dao.ItemDao;
import com.epam.kozhanbergenov.shop.dao.OrderDao;
import com.epam.kozhanbergenov.shop.dao.UserDao;
import com.epam.kozhanbergenov.shop.dao.exception.DaoException;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import com.epam.kozhanbergenov.shop.entity.Client;
import com.epam.kozhanbergenov.shop.entity.Item;
import com.epam.kozhanbergenov.shop.entity.Order;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class H2OrderDao implements OrderDao {
    private Connection connection;
    private static final Logger log = Logger.getLogger(H2OrderDao.class);

    public H2OrderDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public int create(Order order) throws DaoException {
        int orderId = 0;
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        try {
            Map<Item, Integer> items = order.getItemIntegerMap();
            int clientId = order.getClient().getId();
            log.debug("clientId= " + clientId);
            double totalSum = 0;
            for (Map.Entry<Item, Integer> entry : items.entrySet()) {
                totalSum += entry.getKey().getPrice() * entry.getValue();
            }
            log.debug("Trying to create order..");
            log.debug("amount = " + totalSum);
            java.util.Date utilDate = new java.util.Date();
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            String sql = "INSERT INTO ORDERX(CLIENT_ID,  AMOUNT, ORDER_DATE) VALUES(?, ?, ?)";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, clientId);
            stm.setDouble(2, totalSum);
            stm.setDate(3, sqlDate);
            stm.executeUpdate();
            ResultSet rs = connection.createStatement().executeQuery("SELECT LAST_INSERT_ID() FROM ORDERX");
            rs.next();
            orderId = rs.getInt(1);
            for (Map.Entry<Item, Integer> entry : items.entrySet()) {
                String sql2 = "INSERT INTO ITEM_ORDER(ORDER_ID,  ITEM_ID, QUANTITY) VALUES(?, ?, ?)";
                PreparedStatement stm2 = connection.prepareStatement(sql2);
                stm2.setInt(1, orderId);
                stm2.setInt(2, entry.getKey().getId());
                stm2.setInt(3, entry.getValue());
                stm2.executeUpdate();
                log.debug("Item id " + entry.getKey().getId());
                log.debug("quantity " + entry.getValue());
            }
            ItemDao itemDao = new H2ItemDao(connection);
            itemDao.updateAll(order.getItemIntegerMap());
            connection.commit();
            if (stm != null)
                stm.close();

        } catch (SQLException e) {
            log.error(e);
            if (connection != null) {
                try {
                    log.error("Transaction is being rolled back");
                    connection.rollback();
                } catch (SQLException e1) {
                    log.error(e1);
                }
            }
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    throw new DaoException(e);
                }
                ConnectionPool.returnConnection(connection);
            }
        }
        return orderId;
    }

    @Override
    public Order read(int id) throws DaoException {
        try {

            String sql = "SELECT * FROM ORDERX WHERE ID = ?;";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();
            rs.next();
            Order order = new Order();
            order.setId(rs.getInt("id"));
            int clientId = rs.getInt("client_id");
            order.setSent(rs.getBoolean("sent"));
            order.setAmount(rs.getDouble("amount"));
            UserDao userDao = new H2UserDao(ConnectionPool.getConnection());
            order.setClient((Client) userDao.read(clientId));

            String sql2 = "SELECT * FROM ITEM_ORDER WHERE ORDER_ID=?;";
            PreparedStatement stm2 = connection.prepareStatement(sql2);
            stm2.setInt(1, order.getId());
            ResultSet rs2 = stm2.executeQuery();
            Map<Item, Integer> items = new HashMap<>();
            ItemDao itemDao = H2DaoFactory.getItemDao();
            while (rs2.next()) {
                int itemId = rs2.getInt("item_id");
                int quantity = rs2.getInt("quantity");
                Item item = itemDao.read(itemId);
                items.put(item, quantity);
            }
            order.setItemIntegerMap(items);
            java.sql.Date sqlDate = new java.sql.Date(rs.getDate("order_date").getDate());
            order.setOrderDate(new Date(sqlDate.getDate()));
            if (stm != null)
                stm.close();
            if (stm2 != null)
                stm2.close();
            ConnectionPool.returnConnection(connection);
            return order;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<Order> getAll() throws DaoException {
        try {
            String sql = "SELECT * FROM ORDERX;";
            Statement stm = connection.createStatement();
            ResultSet rs = stm.executeQuery(sql);
            List<Order> list = new ArrayList<>();
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                int clientId = rs.getInt("client_id");
                order.setSent(rs.getBoolean("sent"));
                order.setAmount(rs.getDouble("amount"));
                UserDao userDao = new H2UserDao(ConnectionPool.getConnection());
                order.setClient((Client) userDao.read(clientId));

                String sql2 = "SELECT * FROM ITEM_ORDER WHERE ORDER_ID=" + order.getId() + ";";
                Statement stm2 = connection.createStatement();
                ResultSet rs2 = stm2.executeQuery(sql2);
                Map<Item, Integer> items = new HashMap<>();
                ItemDao itemDao = new H2ItemDao(ConnectionPool.getConnection());
                while (rs2.next()) {
                    int itemId = rs2.getInt("item_id");
                    int quantity = rs2.getInt("quantity");
                    Item item = itemDao.read(itemId);
                    items.put(item, quantity);
                }
                order.setItemIntegerMap(items);
                java.sql.Date sqlDate = rs.getDate("order_date");
                order.setOrderDate(sqlDate);
                list.add(order);
                if (stm2 != null)
                    stm2.close();
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
    public void setSent(Order order, boolean sent) throws DaoException {
        try {
            int id = order.getId();
            String sql = "UPDATE ORDERX SET  SENT=? WHERE ID = ?;";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setBoolean(1, sent);
            stm.setInt(2, id);
            stm.executeUpdate();
            if (stm != null)
                stm.close();
            ConnectionPool.returnConnection(connection);
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
