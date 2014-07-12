package com.epam.kozhanbergenov.shop.action;

import com.epam.kozhanbergenov.shop.dao.CategoryDao;
import com.epam.kozhanbergenov.shop.dao.h2Dao.H2CategoryDao;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import com.epam.kozhanbergenov.shop.entity.Category;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

public class ShowCategories implements Action {
    private static final Logger log = Logger.getLogger(ShowCategories.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("ShowCategories action was started");
        try {
            CategoryDao categoryDao = new H2CategoryDao(ConnectionPool.getConnection());
            List<Category> categoryList = null;
            categoryList = categoryDao.getAll();
            categoryDao.returnConnection();
            HttpSession httpSession = req.getSession();
            if (categoryList != null) httpSession.setAttribute("categories", categoryList);
            return new ActionResult("/WEB-INF/categories.jsp");
        } catch (Exception e) {
            log.error(e);
            return new ActionResult("WEB-INF/errorPage.jsp");
        }
    }

}
