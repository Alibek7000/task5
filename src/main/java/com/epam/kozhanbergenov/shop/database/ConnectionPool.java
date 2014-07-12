package com.epam.kozhanbergenov.shop.database;

import com.epam.kozhanbergenov.shop.util.ConfigurationManager;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class ConnectionPool {
    private static final Logger log = Logger.getLogger(ConnectionPool.class);
    public static ConnectionPool instance;
    private  static final BlockingQueue<Connection> connections = new LinkedBlockingQueue<Connection>();
    private static final ConfigurationManager configurationManager = new ConfigurationManager("database.properties");
    private final static String DATABASE_URL = configurationManager.getValue("Database.url");
    private final static String DATABASE_LOGIN = configurationManager.getValue("Database.login");
    private final static String DATABASE_PASSWORD = configurationManager.getValue("Database.password");
    private final static int CONNECTIONS_QUANTITY = new Integer(configurationManager.getValue("Connection.quantity"));

    private ConnectionPool() {
        Connection connection;
        try {
            Class.forName("org.h2.Driver");
            for (int i = 0; i < CONNECTIONS_QUANTITY; i++) {
                connection = DriverManager.getConnection(DATABASE_URL, DATABASE_LOGIN, DATABASE_PASSWORD);
                if (connection != null) {
                    connections.add(connection);
                }
            }
        }
        catch (ClassNotFoundException e) {
            log.error(e);
        }
        catch (SQLException e) {
            log.error(e);
        }
    }

    public static ConnectionPool getInstance() {
        if (instance == null)
            instance = new ConnectionPool();
        return instance;
    }

    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = getInstance().connections.take();
        } catch (InterruptedException e) {
            log.error(e);
        }
        return connection;
    }

    public static void returnConnection(Connection connection){
        try {
            connections.put(connection);
        } catch (InterruptedException e) {
            log.error(e);
        }
    }

}