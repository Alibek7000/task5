package com.epam.kozhanbergenov.shop.dao.h2Dao;

import com.epam.kozhanbergenov.shop.dao.ItemDao;
import com.epam.kozhanbergenov.shop.dao.exception.DaoException;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import com.epam.kozhanbergenov.shop.entity.Item;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class H2ItemDao implements ItemDao {
    private Connection connection;
    private static final Logger log = Logger.getLogger(H2ItemDao.class);

    public H2ItemDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public int create(Item item, int quantity) throws DaoException {
        int id = 0;
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        try {
            String name = item.getName();
            String description = item.getDescription();
            double price = item.getPrice();
            int categoryId = item.getCategory();
            String sql = "INSERT INTO ITEM(NAME,  DESCRIPTION, QUANTITY, PRICE, CATEGORY_ID) " +
                    "VALUES(?, ?, ?, ?, ?)";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, name);
            stm.setString(2, description);
            stm.setInt(3, quantity);
            stm.setDouble(4, price);

            if (categoryId != 0)
                stm.setInt(5, categoryId);
            else
                stm.setNull(5, Types.NULL);

            stm.executeUpdate();
            ResultSet rs = connection.createStatement().executeQuery("SELECT LAST_INSERT_ID() FROM ITEM");
            rs.next();
            id = rs.getInt(1);
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
        return id;
    }

    @Override
    public Item read(int id) throws DaoException {
        try {
            String sql = "SELECT * FROM ITEM WHERE ID = ?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();
            Item item = null;
            rs.next();
            item = new Item();
            item.setId(rs.getInt("id"));
            item.setName(rs.getString("name"));
            item.setDescription(rs.getString("description"));
            item.setPrice(rs.getDouble("price"));
            item.setCategory(rs.getInt("category_id"));
            if (stm != null)
                stm.close();
            ConnectionPool.returnConnection(connection);
            return item;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void update(Item item, int quantity) throws DaoException {
        try {
            int id = item.getId();
            log.debug("id = " + id);
            String name = item.getName();
            String description = item.getDescription();
            int categoryId = item.getCategory();
            double price = item.getPrice();
            String sql;
            if (categoryId == 0)
                sql = "UPDATE ITEM SET CATEGORY_ID = NULL, QUANTITY=" + quantity + ", NAME ='" + name
                        + "', DESCRIPTION='" + description + "', PRICE=" + price + "WHERE ID=" + id + ";";
            else
                sql = "UPDATE ITEM SET QUANTITY=" + quantity + ", NAME ='" + name
                        + "', DESCRIPTION='" + description + "', PRICE=" + price + ", CATEGORY_ID=" + categoryId + " WHERE ID=" + id + ";";
            Statement stm = connection.createStatement();
            stm.executeUpdate(sql);
            if (stm != null)
                stm.close();
            ConnectionPool.returnConnection(connection);
        } catch (SQLException e) {
            throw new DaoException(e);
        }

    }

    @Override
    public Map<Item, Integer> getAll(int offset, int noOfRecords, boolean sortingByName, boolean sortingByPrice, boolean sortingUp) throws DaoException {
        try {
            log.debug("offset=" + offset + " noOfRecords=" + noOfRecords);
            String sql = "";
            String orderBy;
            String direction;
            if (offset == 0 && noOfRecords == 0) {
                sql = "SELECT * FROM ITEM";
            } else {
                if (sortingByName)
                    orderBy = "NAME";
                else orderBy = "PRICE";
                if (sortingUp)
                    direction = "ASC";
                else direction = "DESC";
                sql = "SELECT * FROM ITEM  ORDER BY " + orderBy + " " + direction + " LIMIT " + offset + "," + noOfRecords + ";";
            }
            Statement stm = connection.createStatement();
            ResultSet rs = stm.executeQuery(sql);

            Map<Item, Integer> itemIntegerMap = new LinkedHashMap<>();
            while (rs.next()) {
                log.debug("Found item..");
                Item item = new Item();
                item.setId(rs.getInt("id"));
                item.setName(rs.getString("name"));
                item.setDescription(rs.getString("description"));
                item.setPrice(rs.getDouble("price"));
                item.setCategory(rs.getInt("category_id"));
                if (getQuantityById(rs.getInt("id")) > 0)
                    itemIntegerMap.put(item, rs.getInt("quantity"));
            }
            if (stm != null) stm.close();
            ConnectionPool.returnConnection(connection);
            return itemIntegerMap;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public int getNoOfRecords(int categoryId) throws DaoException {
        try {
            int noOfRecords = 0;
            Statement stm = connection.createStatement();
            ResultSet rs = null;
            log.debug("categoryId=" + categoryId);
            if (categoryId == 0) {
                rs = stm.executeQuery("SELECT COUNT (*) FROM ITEM");
            } else {
                rs = stm.executeQuery("SELECT COUNT (*) FROM ITEM WHERE CATEGORY_ID=" + categoryId + " OR  CATEGORY_ID IN (SELECT ID FROM CATEGORY WHERE PARENT_ID=" + categoryId + ")");
            }
            if (!rs.next()) {
                log.debug("!rs.next()");
            }
            noOfRecords = rs.getInt(1);
            log.debug("noOfRecords=" + noOfRecords);
            if (stm != null) {
                stm.close();
            }
            ConnectionPool.returnConnection(connection);
            return noOfRecords;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void delete(Item item) {
        int id = item.getId();
        String sql = "DELETE FROM ITEM_ORDER WHERE ITEM_ID=" + id + ";" +
                "DELETE FROM ITEM_BASKET WHERE ITEM_ID=" + id + ";" +
                "DELETE FROM ITEM WHERE ID=" + id + ";";
        try {
            Statement stm = connection.createStatement();
            stm.executeUpdate(sql);
            if (stm != null)
                stm.close();
        } catch (SQLException e) {
            log.error(e);
        }
        ConnectionPool.returnConnection(connection);
    }

    @Override
    public int getQuantityById(int id) throws DaoException {
        try {
            String sql = "SELECT * FROM ITEM WHERE ID = ?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();
            rs.next();
            int quantity = rs.getInt("quantity");
            if (stm != null)
                stm.close();
            ConnectionPool.returnConnection(connection);
            return quantity;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void updateAll(Map<Item, Integer> items) throws DaoException {
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        try {
            for (Map.Entry<Item, Integer> entry : items.entrySet()) {
                int quantity = getQuantityById(entry.getKey().getId()) - entry.getValue();

                String sql = "UPDATE ITEM SET QUANTITY=? WHERE ID=?;";
                PreparedStatement stm = connection.prepareStatement(sql);
                stm.setInt(1, quantity);
                stm.setInt(2, entry.getKey().getId());
                stm.executeUpdate();
                connection.commit();
                if (stm != null)
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
    }

    @Override
    public boolean enoughQuantity(Map<Item, Integer> items) throws DaoException {
        for (Map.Entry<Item, Integer> entry : items.entrySet()) {
            int availableQuantity = getQuantityById(entry.getKey().getId());
            ConnectionPool.returnConnection(connection);
            if (availableQuantity < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Map<Item, Integer> getAllByCategory(int categoryId, int offset, int noOfRecords, boolean sortingByName, boolean sortingByPrice, boolean sortingUp) throws DaoException {
        try {
            String sql = "";
            String orderBy;
            String direction;
            if (sortingByName)
                orderBy = "NAME";
            else orderBy = "PRICE";
            if (sortingUp)
                direction = "ASC";
            else direction = "DESC";
            sql = "SELECT * FROM ITEM WHERE CATEGORY_ID = ? ORDER BY " + orderBy + " " + direction + " LIMIT " + offset + ", " + noOfRecords;
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, categoryId);
            ResultSet rs = stm.executeQuery();
            Map<Item, Integer> itemIntegerMap = new LinkedHashMap<>();
            while (rs.next()) {
                log.debug("Found item..");
                Item item = new Item();
                item.setId(rs.getInt("id"));
                item.setName(rs.getString("name"));
                item.setDescription(rs.getString("description"));
                item.setPrice(rs.getDouble("price"));
                item.setCategory(rs.getInt("category_id"));
                log.debug("item in this category" + item);
                itemIntegerMap.put(item, rs.getInt("quantity"));
            }
            if (stm != null)
                stm.close();
            ConnectionPool.returnConnection(connection);
            return itemIntegerMap;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Map<Item, Integer> getAllByParentCategory(int categoryId, int offset, int noOfRecords, boolean sortingByName, boolean sortingByPrice, boolean sortingUp) throws DaoException {
        try {
            String sql = "";
            String orderBy;
            String direction;
            if (sortingByName)
                orderBy = "NAME";
            else orderBy = "PRICE";
            if (sortingUp)
                direction = "ASC";
            else direction = "DESC";
            sql = "SELECT * FROM ITEM WHERE CATEGORY_ID=? OR CATEGORY_ID IN (SELECT ID FROM CATEGORY WHERE PARENT_ID= ?) ORDER BY  " + orderBy + " " + direction + " LIMIT " + offset + ", " + noOfRecords;
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, categoryId);
            stm.setInt(2, categoryId);
            ResultSet rs = stm.executeQuery();
            Map<Item, Integer> itemIntegerMap = new LinkedHashMap<>();
            if (!rs.next()) {
                log.debug("Category has no children");
                itemIntegerMap = getAllByCategory(categoryId, offset, noOfRecords, sortingByName, sortingByPrice, sortingUp);
            } else do {
                log.debug("Found item..");
                Item item = new Item();
                item.setId(rs.getInt("id"));
                item.setName(rs.getString("name"));
                item.setDescription(rs.getString("description"));
                item.setPrice(rs.getDouble("price"));
                item.setCategory(rs.getInt("category_id"));
                log.debug("item in this category" + item);
                itemIntegerMap.put(item, rs.getInt("quantity"));
            }
            while (rs.next());
            if (stm != null)
                stm.close();
            ConnectionPool.returnConnection(connection);
            return itemIntegerMap;
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
