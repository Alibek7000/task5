package com.epam.kozhanbergenov.shop.action;

import com.epam.kozhanbergenov.shop.dao.CategoryDao;
import com.epam.kozhanbergenov.shop.dao.h2Dao.H2CategoryDao;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import com.epam.kozhanbergenov.shop.entity.Category;
import com.epam.kozhanbergenov.shop.entity.Client;
import com.epam.kozhanbergenov.shop.entity.User;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;

public class ShowEditCategoryPage implements Action {
    private static final Logger log = Logger.getLogger(ShowEditCategoryPage.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("ShowEditCategoryPage action was started");
        try {
            HttpSession httpSession = req.getSession();
            User user = (User) httpSession.getAttribute("user");
            if (user == null || user instanceof Client) {
                return new ActionResult("/WEB-INF/errorPage.jsp?errorMessage=error.permissionDenied");
            }
            int id = new Integer(req.getParameter("id"));
            Category editCategory = null;
            CategoryDao categoryDao = new H2CategoryDao(ConnectionPool.getConnection());
            try {
                editCategory = categoryDao.read(id);
            } catch (SQLException e) {
                log.error(e);
            }
            log.error(editCategory);
            httpSession.setAttribute("editCategory", editCategory);
            categoryDao.returnConnection();
            return new ActionResult("controller?action=editCategory", true);
        } catch (Exception e) {
            log.error(e);
            return new ActionResult("WEB-INF/errorPage.jsp");
        }
    }
}
