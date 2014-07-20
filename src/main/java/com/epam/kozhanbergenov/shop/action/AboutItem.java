package com.epam.kozhanbergenov.shop.action;

import com.epam.kozhanbergenov.shop.DAO.ItemDAO;
import com.epam.kozhanbergenov.shop.DAO.exception.DAOException;
import com.epam.kozhanbergenov.shop.DAO.H2DAO.H2ItemDAO;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import com.epam.kozhanbergenov.shop.entity.Item;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AboutItem implements Action {

    private static final Logger log = Logger.getLogger(AboutItem.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("AboutItem action was started");
        ItemDAO itemDAO = new H2ItemDAO(ConnectionPool.getConnection());
        int id = 0;
        try {
            id = new Integer(req.getParameter("id"));
        } catch (NumberFormatException e) {
            log.error(e);
            return new ActionResult("WEB-INF/jsp/errorPage.jsp");
        }
        Item item = null;
        try {
            item = itemDAO.read(id);
        } catch (DAOException e) {
            log.error(e);
        }
        itemDAO.returnConnection();
        HttpSession httpSession = req.getSession();
        if (item != null) httpSession.setAttribute("item", item);
        return new ActionResult("/WEB-INF/jsp/aboutItem.jsp");
    }

}
