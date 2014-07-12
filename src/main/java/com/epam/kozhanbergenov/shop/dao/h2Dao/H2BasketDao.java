package com.epam.kozhanbergenov.shop.dao.h2Dao;

import com.epam.kozhanbergenov.shop.dao.BasketDao;
import com.epam.kozhanbergenov.shop.dao.ItemDao;
import com.epam.kozhanbergenov.shop.dao.exception.DaoException;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import com.epam.kozhanbergenov.shop.entity.Item;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class H2BasketDao implements BasketDao {
    private Connection connection;
    private static final Logger log = Logger.getLogger(H2BasketDao.class);

    public H2BasketDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public int create(int userId) throws DaoException {
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        int basketId = 0;
        try {
            String sql;
            java.util.Date utilDate = new java.util.Date();
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            if (userId != 0)
                sql = "INSERT INTO BASKET(CLIENT_ID, BASKET_DATE) VALUES('" + userId + "','" + sqlDate + "');";
            else
                sql = "INSERT INTO BASKET(CLIENT_ID, BASKET_DATE) VALUES(NULL,'" + sqlDate + "');";
            Statement stm = connection.createStatement();
            try {
                stm.executeUpdate(sql);
            } catch (SQLException e) {
                log.error(e);
            }
            ResultSet rs = stm.executeQuery("SELECT LAST_INSERT_ID() FROM BASKET");
            rs.next();
            basketId = rs.getInt(1);
            connection.commit();
            if (stm != null) {
                stm.close();
            }
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
        return basketId;
    }

    @Override
    public Map<Item, Integer> read(int basketId) throws DaoException {
        try {
            String sql = "SELECT * FROM ITEM_BASKET WHERE BASKET_ID = ?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, basketId);
            ResultSet rs = stm.executeQuery();
            Map<Item, Integer> map = new HashMap<>();
            while (rs.next()) {
                ItemDao itemDao = new H2ItemDao(ConnectionPool.getConnection());
                int itemId = rs.getInt("ITEM_ID");
                Item item = itemDao.read(itemId);
                map.put(item, rs.getInt("QUANTITY"));
            }
            if (stm != null) {
                stm.close();
            }
            ConnectionPool.returnConnection(connection);
            return map;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Map<Item, Integer> readByUserId(int userId) throws DaoException {
        try {
            String sql = "SELECT * FROM BASKET WHERE CLIENT_ID = ?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, userId);
            ResultSet rs = stm.executeQuery();
            Map<Item, Integer> map;
            rs.next();
            int basketId = rs.getInt("ID");
            map = read(basketId);
            if (stm != null) {
                stm.close();
            }
            ConnectionPool.returnConnection(connection);
            return map;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public int getBasketIByUserId(int id) throws DaoException {
        try {
            int basketId = 0;
            String sql = "SELECT ID FROM BASKET WHERE CLIENT_ID = ?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();
            rs.next();
            basketId = rs.getInt("ID");
            if (stm != null) {
                stm.close();
            }
            ConnectionPool.returnConnection(connection);
            return basketId;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void addItem(int basketId, Item item, int quantity) throws DaoException {
        try {
            String sql = "INSERT INTO ITEM_BASKET(BASKET_ID,  ITEM_ID, QUANTITY) VALUES(?, ?, ?);";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, basketId);
            stm.setInt(2, item.getId());
            stm.setInt(3, quantity);
            stm.executeUpdate();
            if (stm != null) {
                stm.close();
            }
            ConnectionPool.returnConnection(connection);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void update(int basketId, int userId) throws DaoException {
        try {
            String sql = "UPDATE BASKET SET CLIENT_ID =?, BASKET_DATE=? WHERE ID = ?;";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, userId);
            java.util.Date utilDate = new java.util.Date();
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            stm.setDate(2, sqlDate);
            stm.setInt(3, basketId);
            stm.executeUpdate();
            log.debug("updating UserId in basket! UserId =  " + userId + "basketId = " + basketId);
            if (stm != null) {
                stm.close();
            }
            ConnectionPool.returnConnection(connection);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void deleteItemBasket(int basketId, int itemId) throws DaoException {
        try {

            String sql = "DELETE FROM ITEM_BASKET WHERE BASKET_ID = ? AND ITEM_ID = ?;";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, basketId);
            stm.setInt(2, itemId);
            stm.executeUpdate();
            if (stm != null) {
                stm.close();
            }
            ConnectionPool.returnConnection(connection);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void deleteOld(int basketSaveDays) throws DaoException {
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        try {
            String sql = "DELETE FROM BASKET WHERE DATEDIFF(DAY, BASKET_DATE,GETDATE())>?;";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, basketSaveDays);
            stm.executeUpdate();

            String sql2 = "DELETE ITEM_BASKET WHERE EXISTS (SELECT ITEM_BASKET.* FROM ITEM_BASKET LEFT JOIN BASKET " +
                    "ON ITEM_BASKET.BASKET_id = BASKET.id " +
                    "WHERE BASKET.id IS NULL);";
            PreparedStatement stm2 = connection.prepareStatement(sql2);
            stm2.executeUpdate();
            connection.commit();
            if (stm != null)
                stm.close();
            if (stm2 != null)
                stm2.close();
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

    }

    @Override
    public void returnConnection() {
        if (connection != null)
            ConnectionPool.returnConnection(connection);
    }
}
