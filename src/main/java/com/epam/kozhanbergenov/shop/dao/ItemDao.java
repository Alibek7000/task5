package com.epam.kozhanbergenov.shop.dao;

import com.epam.kozhanbergenov.shop.dao.exception.DaoException;
import com.epam.kozhanbergenov.shop.entity.Item;


import java.util.Map;

public interface ItemDao {
    int create(Item item, int quantity) throws DaoException;

    Item read(int id) throws DaoException;

    void update(Item item, int quantity) throws DaoException;

    Map<Item, Integer> getAll(int offset, int noOfRecords, boolean sortingByName, boolean sortingByPrice, boolean sortingUp) throws DaoException;

    int getNoOfRecords(int categoryId) throws DaoException;

    void delete(Item item);

    int getQuantityById(int id) throws DaoException;

    void updateAll(Map<Item, Integer> items) throws DaoException;

    boolean enoughQuantity(Map<Item, Integer> items) throws DaoException;

    Map<Item, Integer> getAllByCategory(int categoryId, int offset, int noOfRecords, boolean sortingByName, boolean sortingByPrice, boolean sortingUp) throws DaoException;

    Map<Item, Integer> getAllByParentCategory(int categoryId, int offset, int noOfRecords, boolean sortingByName, boolean sortingByPrice, boolean sortingUp) throws DaoException;

    void returnConnection();
}
