package com.epam.kozhanbergenov.shop.dao.h2Dao;

import com.epam.kozhanbergenov.shop.dao.BasketDao;
import com.epam.kozhanbergenov.shop.dao.ItemDao;
import com.epam.kozhanbergenov.shop.dao.exception.DaoException;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import com.epam.kozhanbergenov.shop.entity.Item;
import com.sun.xml.internal.ws.policy.sourcemodel.ModelNode;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class H2BasketDao implements BasketDao {
    private Connection connection;
    private static final Logger log = Logger.getLogger(H2BasketDao.class);

    public H2BasketDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public UUID create(int userId) throws DaoException {
        UUID uuid = UUID.randomUUID();
        log.debug("creating new basket. Uuid is " + uuid);
        try {
            String sql;
            java.util.Date utilDate = new java.util.Date();
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            sql = "INSERT INTO BASKET(ID, CLIENT_ID, BASKET_DATE) VALUES(?, ?, ?);";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setObject(1, uuid);
            stm.setDate(3, sqlDate);
            if (userId != 0)
                stm.setInt(2, userId);
            else
                stm.setNull(2, Types.NULL);
            stm.executeUpdate();
        } catch (SQLException e) {
            log.error(e);
            throw new DaoException(e);

        } finally {
            if (connection != null) {
                ConnectionPool.returnConnection(connection);
            }
        }
        return uuid;
    }

    @Override
    public Map<Item, Integer> read(UUID uuid) throws DaoException {
        try {
            String sql = "SELECT * FROM ITEM_BASKET WHERE BASKET_ID = ?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setObject(1, uuid);
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
            log.debug(Arrays.toString(map.entrySet().toArray()));
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
            UUID uuid = UUID.fromString(rs.getString("ID"));
            map = read(uuid);
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
    public UUID getBasketIdByUserId(int id) throws DaoException {
        try {
            UUID uuid = null;
            String sql = "SELECT ID FROM BASKET WHERE CLIENT_ID = ?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();
            rs.next();
            uuid = (UUID) rs.getObject("ID");
            if (stm != null) {
                stm.close();
            }
            ConnectionPool.returnConnection(connection);
            return uuid;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void addItem(UUID uuid, Item item, int quantity) throws DaoException {
        try {
            String sql = "INSERT INTO ITEM_BASKET(BASKET_ID,  ITEM_ID, QUANTITY) VALUES(?, ?, ?);";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setObject(1, uuid);
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
    public void update(UUID uuid, int userId) throws DaoException {
        try {
            String sql = "UPDATE BASKET SET CLIENT_ID =?, BASKET_DATE=? WHERE ID = ?;";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, userId);
            java.util.Date utilDate = new java.util.Date();
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            stm.setDate(2, sqlDate);
            stm.setObject(3, uuid);
            stm.executeUpdate();
            log.debug("updating UserId in basket! UserId =  " + userId + "basketId = " + uuid);
            if (stm != null) {
                stm.close();
            }
            ConnectionPool.returnConnection(connection);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void deleteItemBasket(UUID uuid, int itemId) throws DaoException {
        try {
            String sql = "DELETE FROM ITEM_BASKET WHERE BASKET_ID = ? AND ITEM_ID = ?;";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setObject(1, uuid);
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
    public int getUserIdByBasketId(UUID uuid) throws DaoException {
        try {
            int id = 0;
            if (uuid !=  null) {
                String sql = "SELECT CLIENT_ID FROM BASKET WHERE ID = ?";
                PreparedStatement stm = connection.prepareStatement(sql);
                stm.setObject(1, uuid);
                ResultSet rs = stm.executeQuery();
                rs.next();
                id = rs.getInt("CLIENT_ID");
                if (stm != null) {
                    stm.close();
                }
                ConnectionPool.returnConnection(connection);
            }
            return id;
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
