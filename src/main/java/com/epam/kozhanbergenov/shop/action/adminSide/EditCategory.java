package com.epam.kozhanbergenov.shop.action.adminSide;

import com.epam.kozhanbergenov.shop.action.Action;
import com.epam.kozhanbergenov.shop.action.ActionResult;
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

public class EditCategory implements Action {
    private static final Logger log = Logger.getLogger(EditCategory.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        try {
            HttpSession httpSession = req.getSession();
            User user = (User) httpSession.getAttribute("user");
            if (user == null || user instanceof Client) {
                return new ActionResult("/WEB-INF/jsp/errorPage.jsp?errorMessage=error.permissionDenied");
            }
            CategoryDao categoryDao = new H2CategoryDao(ConnectionPool.getConnection());
            String name = "";
            String description = "";
            Category editCategory = null;
            int parentId = 0;
            int id = 0;
            try {
                name = req.getParameter("name");
                description = req.getParameter("ruName");
                editCategory = (Category) httpSession.getAttribute("editCategory");
                parentId = new Integer(req.getParameter("parentId"));
            } catch (Exception e) {
                log.error(e);
                return new ActionResult("WEB-INF/jsp/errorPage.jsp");
            }

            if (name == null && description == null) {
                return new ActionResult("/WEB-INF/editCategory.jsp");
            }

            if (editCategory != null) id = editCategory.getId();
            log.debug("parent ID = " + parentId);
            Category category = new Category(name, parentId, description);
            category.setId(id);
            categoryDao.update(category);
            categoryDao.returnConnection();
            return new ActionResult("controller?action=showCategories", true);
        } catch (Exception e) {
            log.error(e);
            return new ActionResult("WEB-INF/jsp/errorPage.jsp");
        }
    }

}
