package com.epam.kozhanbergenov.shop.dao;

import com.epam.kozhanbergenov.shop.dao.h2Dao.*;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import org.apache.log4j.Logger;

public class H2DaoFactory extends DaoFactory {
    private static final Logger log = Logger.getLogger(H2DaoFactory.class);

    H2DaoFactory() {
    }

    private static ItemDao itemDao = null;
    private static UserDao userDao = null;
    private static OrderDao orderDao = null;
    private static CategoryDao categoryDao = null;
    private static BasketDao basketDao = null;


    static {
        itemDao = new H2ItemDao(ConnectionPool.getConnection());
        userDao = new H2UserDao(ConnectionPool.getConnection());
        orderDao = new H2OrderDao(ConnectionPool.getConnection());
        categoryDao = new H2CategoryDao(ConnectionPool.getConnection());
        basketDao = new H2BasketDao(ConnectionPool.getConnection());
    }

    @Override
    public ItemDao getItemDao() {
        return itemDao;
    }
    @Override
    public UserDao getUserDao() {
        return userDao;
    }
    @Override
    public OrderDao getOrderDao() {
        return orderDao;
    }
    @Override
    public CategoryDao getCategoryDao() {
        return categoryDao;
    }
    @Override
    public BasketDao getBasketDao() {
        return basketDao;
    }
}
