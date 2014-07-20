package com.epam.kozhanbergenov.shop.action.adminSide;

import com.epam.kozhanbergenov.shop.action.Action;
import com.epam.kozhanbergenov.shop.action.ActionResult;
import com.epam.kozhanbergenov.shop.DAO.UserDAO;
import com.epam.kozhanbergenov.shop.DAO.H2DAO.H2UserDAO;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import com.epam.kozhanbergenov.shop.entity.Client;
import com.epam.kozhanbergenov.shop.entity.User;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class BaningClient implements Action {
    private static final Logger log = Logger.getLogger(BaningClient.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("BaningClient action was started");
        try {
            HttpSession httpSession = req.getSession();
            User user = (User) httpSession.getAttribute("user");
            if (user == null || user instanceof Client) {
                return new ActionResult("/WEB-INF/jsp/errorPage.jsp?errorMessage=error.permissionDenied");
            }

            int id = new Integer(req.getParameter("id"));
            boolean value = Boolean.parseBoolean(req.getParameter("value"));

            UserDAO userDAO = new H2UserDAO(ConnectionPool.getConnection());

            User client = null;
            client = userDAO.read(id);
            ((Client) client).setBanned(value);
            userDAO.update(client);
            userDAO.returnConnection();
            log.debug("isBanned " + ((Client) client).isBanned());
            return new ActionResult("controller?action=showUsers", true);
        } catch (Exception e) {
            log.error(e);
            return new ActionResult("WEB-INF/jsp/errorPage.jsp");
        }
    }
}


