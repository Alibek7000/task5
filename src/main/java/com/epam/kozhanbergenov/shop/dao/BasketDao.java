package com.epam.kozhanbergenov.shop.dao;

import com.epam.kozhanbergenov.shop.dao.exception.DaoException;
import com.epam.kozhanbergenov.shop.entity.Item;

import java.util.Map;

public interface BasketDao {
    int create(int userId) throws DaoException;

    Map<Item, Integer> read(int basketId) throws DaoException;

    Map<Item, Integer> readByUserId(int userId) throws DaoException;

    int getBasketIByUserId(int id) throws DaoException;

    void addItem(int basketId, Item item, int quantity) throws DaoException;

    void update(int basketId, int userId) throws DaoException;

    void deleteItemBasket(int basketId, int itemId) throws DaoException;

    void deleteOld(int basketSaveDays) throws DaoException;

    void returnConnection();
}
