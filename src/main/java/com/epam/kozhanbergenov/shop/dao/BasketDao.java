package com.epam.kozhanbergenov.shop.dao;

import com.epam.kozhanbergenov.shop.dao.exception.DaoException;
import com.epam.kozhanbergenov.shop.entity.Item;

import java.util.Map;
import java.util.UUID;

public interface BasketDao {
    UUID create(int userId) throws DaoException;

    Map<Item, Integer> read(UUID basketId) throws DaoException;

    Map<Item, Integer> readByUserId(int userId) throws DaoException;

    UUID getBasketIdByUserId(int id) throws DaoException;

    void addItem(UUID basketId, Item item, int quantity) throws DaoException;

    void update(UUID basketId, int userId) throws DaoException;

    void deleteItemBasket(UUID basketId, int itemId) throws DaoException;

    void deleteOld(int basketSaveDays) throws DaoException;

    int getUserIdByBasketId(UUID uuid) throws DaoException;

    void returnConnection();
}
