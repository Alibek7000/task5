package com.epam.kozhanbergenov.shop.action.adminSide;

import com.epam.kozhanbergenov.shop.action.Action;
import com.epam.kozhanbergenov.shop.action.ActionResult;
import com.epam.kozhanbergenov.shop.DAO.ItemDAO;
import com.epam.kozhanbergenov.shop.DAO.H2DAO.H2ItemDAO;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import com.epam.kozhanbergenov.shop.entity.Client;
import com.epam.kozhanbergenov.shop.entity.Item;
import com.epam.kozhanbergenov.shop.entity.User;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ShowEditItemPage implements Action {
    private static final Logger log = Logger.getLogger(ShowEditItemPage.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("ShowEditItemPage action was started");
        try {
            HttpSession httpSession = req.getSession();
            User user = (User) httpSession.getAttribute("user");
            if (user == null || user instanceof Client) {
                return new ActionResult("/WEB-INF/jsp/errorPage.jsp?errorMessage=error.permissionDenied");
            }
            int id = new Integer(req.getParameter("id"));
            Item editItem = null;
            int editItemQuantity = 0;
            ItemDAO itemDAO = new H2ItemDAO(ConnectionPool.getConnection());
            editItem = itemDAO.read(id);
            editItemQuantity = itemDAO.getQuantityById(id);
            log.error(editItem);
            httpSession.setAttribute("editItem", editItem);
            httpSession.setAttribute("editItemQuantity", editItemQuantity);
            itemDAO.returnConnection();
            return new ActionResult("controller?action=editItem", true);
        } catch (Exception e) {
            log.error(e);
            return new ActionResult("WEB-INF/jsp/errorPage.jsp");
        }
    }
}
