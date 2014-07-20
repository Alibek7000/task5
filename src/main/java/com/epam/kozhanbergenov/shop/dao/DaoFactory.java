package com.epam.kozhanbergenov.shop.dao;

public abstract class DaoFactory {

    public static final int H2 = 1;

    public abstract ItemDao getItemDao();
    public abstract UserDao getUserDao();
    public abstract OrderDao getOrderDao();
    public abstract CategoryDao getCategoryDao();
    public abstract BasketDao getBasketDao();

    public static DaoFactory getDAOFactory(int whichFactory) {

        switch (whichFactory) {
            case H2:
                return new H2DaoFactory();
            default:
                return null;
        }
    }
}
