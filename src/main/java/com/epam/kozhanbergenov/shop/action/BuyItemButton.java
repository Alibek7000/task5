package com.epam.kozhanbergenov.shop.action;

import com.epam.kozhanbergenov.shop.dao.h2Dao.H2ItemDao;
import com.epam.kozhanbergenov.shop.dao.ItemDao;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import com.epam.kozhanbergenov.shop.entity.Item;
import com.epam.kozhanbergenov.shop.entity.User;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;

public class BuyItemButton implements Action {
    private static final Logger log = Logger.getLogger(BuyItemButton.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("BuyItemButton action was started");
        try {
            HttpSession httpSession = req.getSession();
            User user = (User) httpSession.getAttribute("user");

            ItemDao itemDao = new H2ItemDao(ConnectionPool.getConnection());
            int id = new Integer(req.getParameter("id"));
            String requestedQuantityString = req.getParameter("quantity");
            Item item = null;
            try {
                item = itemDao.read(id);
            } catch (SQLException e) {
                log.error(e);
            }
            itemDao.returnConnection();
            if (requestedQuantityString.isEmpty()) {
                return new ActionResult("controller?action=aboutItem&id=" + item.getId()
                        + "&errorMessage=error.integer");
            }
            int requestedQuantity = 0;
            try {
                requestedQuantity = new Integer(req.getParameter("quantity"));
            } catch (Exception e) {
                log.error(e);
                return new ActionResult("controller?action=aboutItem&id=" + item.getId()
                        + "&errorMessage=error.integer");
            }
            if (requestedQuantity > 0) {
                httpSession.setAttribute("item", item);
                httpSession.setAttribute("quantity", requestedQuantity);
                return new ActionResult("controller?action=basket", true);
            } else
                return new ActionResult("controller?action=aboutItem&id="
                        + item.getId() + "&errorMessage=error.integer");
        } catch (Exception e) {
            log.error(e);
            return new ActionResult("WEB-INF/errorPage.jsp");
        }
    }
}