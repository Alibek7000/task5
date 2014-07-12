package com.epam.kozhanbergenov.shop.dao;

import com.epam.kozhanbergenov.shop.dao.exception.DaoException;
import com.epam.kozhanbergenov.shop.entity.Category;

import java.util.List;

public interface CategoryDao {
    void create(Category category) throws DaoException;

    Category read(int id) throws DaoException;

    void update(Category category) throws DaoException;

    void delete(Category category);

    List<Category> getAll() throws DaoException;

    List<Category> getAllByParentId(int id) throws DaoException;

    void returnConnection();
}
