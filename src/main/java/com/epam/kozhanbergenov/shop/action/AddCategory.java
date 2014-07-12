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
import java.util.List;

public class AddCategory implements Action {
    private static final Logger log = Logger.getLogger(AddCategory.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        try {
            HttpSession httpSession = req.getSession();
            User user = (User) httpSession.getAttribute("user");
            if (user == null || user instanceof Client) {
                return new ActionResult("/WEB-INF/errorPage.jsp?errorMessage=You have not permissions access this page.");
            }
            CategoryDao categoryDao = new H2CategoryDao(ConnectionPool.getConnection());
            List<Category> categories = null;
            try {
                categories = categoryDao.getAll();
            } catch (SQLException e) {
                log.error(e);
            }
            String name = req.getParameter("name");
            if (name == null) {
                httpSession.setAttribute("categories", categories);
                for (Category category : categories) {
                    log.debug(category);
                }
                return new ActionResult("/WEB-INF/addCategory.jsp");
            }
            String description = req.getParameter("ruName");
            int parentId = 0;
            if (!req.getParameter("parentId").isEmpty())
                parentId = Integer.parseInt(req.getParameter("parentId"));
            if (name.isEmpty())
                return new ActionResult("/WEB-INF/addCategory.jsp?errorMessage=error.incorrectField");
            for (Category category : categories) {
                if (category.getName().equals(name)) {
                    categoryDao.returnConnection();
                    return new ActionResult("/WEB-INF/addCategory.jsp?errorMessage=" +
                            "error.usedName");
                }
            }
            Category category = new Category(name, parentId, description);
            try {
                categoryDao.create(category);
            } catch (SQLException e) {
                log.error(e);
            }
            categoryDao.returnConnection();
            return new ActionResult("controller?action=showCategories", true);
        } catch (Exception e) {
            log.error(e);
            return new ActionResult("WEB-INF/errorPage.jsp");
        }
    }

}
