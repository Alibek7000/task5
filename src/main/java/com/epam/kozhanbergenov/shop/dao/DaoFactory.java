package com.epam.kozhanbergenov.shop.dao;

import com.epam.kozhanbergenov.shop.dao.h2Dao.*;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import org.apache.log4j.Logger;

public class DaoFactory {
    private static final Logger log = Logger.getLogger(DaoFactory.class);
    private DaoFactory() {}

    private static ItemDao itemDao = null;
    private static UserDao userDao = null;
    private static OrderDao orderDao = null;
    private static CategoryDao categoryDao = null;
    private static BasketDao basketDao = null;


    public static ItemDao getItemDao() {
        return itemDao;
    }
    public static UserDao getUserDao() {
        return userDao;
    }
    public static OrderDao getOrderDao() {
        return orderDao;
    }
    public static CategoryDao getCategoryDao() {
        return categoryDao;
    }
    public static BasketDao getBasketDao() {
        return basketDao;
    }

    static {
        itemDao = new H2ItemDao(ConnectionPool.getConnection());
        userDao = new H2UserDao(ConnectionPool.getConnection());
        orderDao = new H2OrderDao(ConnectionPool.getConnection());
        categoryDao = new H2CategoryDao(ConnectionPool.getConnection());
        basketDao = new H2BasketDao(ConnectionPool.getConnection());
    }
}
