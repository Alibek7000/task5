package com.epam.kozhanbergenov.shop.dao;

import com.epam.kozhanbergenov.shop.dao.exception.DaoException;
import com.epam.kozhanbergenov.shop.entity.Order;

import java.util.List;

public interface OrderDao {
    int create(Order order) throws DaoException;

    Order read(int id) throws DaoException;

    List<Order> getAll() throws DaoException;

    void setSent(Order order, boolean sent) throws DaoException;

    void returnConnection();
}
