package com.epam.kozhanbergenov.shop.action;

import com.epam.kozhanbergenov.shop.dao.h2Dao.H2ItemDao;
import com.epam.kozhanbergenov.shop.dao.ItemDao;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import com.epam.kozhanbergenov.shop.entity.Client;
import com.epam.kozhanbergenov.shop.entity.Item;
import com.epam.kozhanbergenov.shop.entity.User;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;

public class ShowEditItemPage implements Action {
    private static final Logger log = Logger.getLogger(ShowEditItemPage.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("ShowEditItemPage action was started");
        try {
            HttpSession httpSession = req.getSession();
            User user = (User) httpSession.getAttribute("user");
            if (user == null || user instanceof Client) {
                return new ActionResult("/WEB-INF/errorPage.jsp?errorMessage=error.permissionDenied");
            }
            int id = new Integer(req.getParameter("id"));
            Item editItem = null;
            int editItemQuantity = 0;
            ItemDao itemDao = new H2ItemDao(ConnectionPool.getConnection());
            try {
                editItem = itemDao.read(id);
                editItemQuantity = itemDao.getQuantityById(id);
            } catch (SQLException e) {
                log.error(e);
            }
            log.error(editItem);
            httpSession.setAttribute("editItem", editItem);
            httpSession.setAttribute("editItemQuantity", editItemQuantity);
            itemDao.returnConnection();
            return new ActionResult("controller?action=editItem", true);
        } catch (Exception e) {
            log.error(e);
            return new ActionResult("WEB-INF/errorPage.jsp");
        }
    }
}
