package com.epam.kozhanbergenov.shop.action.adminSide;

import com.epam.kozhanbergenov.shop.action.Action;
import com.epam.kozhanbergenov.shop.action.ActionResult;
import com.epam.kozhanbergenov.shop.dao.UserDao;
import com.epam.kozhanbergenov.shop.dao.h2Dao.H2UserDao;
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
                return new ActionResult("/WEB-INF/errorPage.jsp?errorMessage=error.permissionDenied");
            }

            int id = new Integer(req.getParameter("id"));
            boolean value = Boolean.parseBoolean(req.getParameter("value"));

            UserDao userDao = new H2UserDao(ConnectionPool.getConnection());

            User client = null;
            client = userDao.read(id);
            ((Client) client).setBanned(value);
            userDao.update(client);
            userDao.returnConnection();
            log.debug("isBanned " + ((Client) client).isBanned());
            return new ActionResult("controller?action=showUsers", true);
        } catch (Exception e) {
            log.error(e);
            return new ActionResult("WEB-INF/errorPage.jsp");
        }
    }
}


