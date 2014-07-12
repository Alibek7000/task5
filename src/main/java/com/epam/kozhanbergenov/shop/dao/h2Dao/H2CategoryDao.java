package com.epam.kozhanbergenov.shop.dao.h2Dao;

import com.epam.kozhanbergenov.shop.dao.CategoryDao;
import com.epam.kozhanbergenov.shop.dao.exception.DaoException;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import com.epam.kozhanbergenov.shop.entity.Category;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class H2CategoryDao implements CategoryDao {
    private Connection connection;
    private static final Logger log = Logger.getLogger(H2CategoryDao.class);

    public H2CategoryDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(Category category) throws DaoException {
        try {
            String name = category.getName();
            String ruName = category.getRuName();
            int parentId = category.getParentId();
            String sql = "INSERT INTO CATEGORY(NAME,  PARENT_ID, RUNAME) VALUES(?, ?, ?)";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, name);
            stm.setString(3, ruName);
            if (parentId != 0)
                stm.setInt(2, parentId);
            else
                stm.setNull(2, Types.NULL);
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
    public Category read(int id) throws DaoException {
        try {
            String sql = "SELECT * FROM CATEGORY WHERE ID = ?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();
            Category category = null;
            rs.next();
            category = new Category();
            category.setId(rs.getInt("id"));
            category.setName(rs.getString("name"));
            category.setRuName(rs.getString("runame"));
            category.setParentId(rs.getInt("parent_id"));
            if (stm != null) {
                stm.close();
            }
            ConnectionPool.returnConnection(connection);
            return category;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void update(Category category) throws DaoException {
        try {
            int id = category.getId();
            log.debug("id = " + id);
            String name = category.getName();
            String ruName = category.getRuName();
            int parentId = category.getParentId();
            String sql;
            if (parentId == 0)
                sql = "UPDATE CATEGORY SET PARENT_ID = NULL, NAME ='" + name
                        + "', RUNAME='" + ruName + "' WHERE ID=" + id + ";";
            else
                sql = "UPDATE CATEGORY SET NAME ='" + name
                        + "', RUNAME='" + ruName + "', PARENT_ID=" + parentId + " WHERE ID=" + id + ";";
            Statement stm = connection.createStatement();
            stm.executeUpdate(sql);

            String sql1 = "UPDATE CATEGORY SET NAME ='" + name
                    + "', RUNAME='" + ruName + "' WHERE ID=" + id + ";";
            stm.executeUpdate(sql1);
            if (stm != null)
                stm.close();
            ConnectionPool.returnConnection(connection);
        } catch (SQLException e) {
            throw new DaoException(e);
        }

    }

    @Override
    public void delete(Category category) {
        int id = category.getId();
        String sql = "DELETE FROM ITEM WHERE CATEGORY_ID=" + id + ";" +
                "DELETE FROM CATEGORY WHERE ID=" + id + ";";
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
    public List<Category> getAll() throws DaoException {
        try {
            String sql = "SELECT * FROM CATEGORY;";
            Statement stm = connection.createStatement();
            ResultSet rs = stm.executeQuery(sql);
            List<Category> list = new ArrayList<>();
            while (rs.next()) {
                Category category = new Category();
                category.setId(rs.getInt("id"));
                category.setName(rs.getString("name"));
                category.setRuName(rs.getString("runame"));
                category.setParentId(rs.getInt("parent_id"));
                list.add(category);
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
    public List<Category> getAllByParentId(int id) throws DaoException {
        try {
            String sql;
            if (id == 0)
                sql = "SELECT * FROM CATEGORY WHERE PARENT_iD IS NULL;";
            else
                sql = "SELECT * FROM CATEGORY WHERE PARENT_iD=" + id + ";";
            Statement stm = connection.createStatement();
            ResultSet rs = stm.executeQuery(sql);
            List<Category> list = new ArrayList<>();
            while (rs.next()) {
                Category category = new Category();
                category.setId(rs.getInt("id"));
                category.setName(rs.getString("name"));
                category.setRuName(rs.getString("runame"));
                category.setParentId(rs.getInt("parent_id"));
                list.add(category);
            }
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
