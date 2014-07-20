package com.epam.kozhanbergenov.shop.dao;

import com.epam.kozhanbergenov.shop.dao.exception.DaoException;
import com.epam.kozhanbergenov.shop.entity.Client;
import com.epam.kozhanbergenov.shop.entity.User;

import java.util.List;

public interface UserDao {
    void create(User user) throws DaoException;

    User read(int id) throws DaoException;

    void update(User user) throws DaoException;

    boolean checkLogin(String login);

    List<User> getAll() throws DaoException;

    void returnConnection();
}
