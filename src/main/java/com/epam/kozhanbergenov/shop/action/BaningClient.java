package com.epam.kozhanbergenov.shop.action;

import com.epam.kozhanbergenov.shop.dao.h2Dao.H2UserDao;
import com.epam.kozhanbergenov.shop.dao.UserDao;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import com.epam.kozhanbergenov.shop.entity.Client;
import com.epam.kozhanbergenov.shop.entity.User;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;

public class BaningClient implements Action {
    private static final Logger log = Logger.getLogger(BaningClient.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("BaningClient action was started");
        try {
            HttpSession httpSession = req.getSession();
            User user = (User) httpSession.getAttribute("user");
            if (user == null || user instanceof Client) {
                return new ActionResult("/WEB-INF/errorPage.jsp?errorMessage=You have not permissions access this page.");
            }

            int id = new Integer(req.getParameter("id"));
            boolean value = Boolean.parseBoolean(req.getParameter("value"));

            UserDao userDao = new H2UserDao(ConnectionPool.getConnection());

            User client = null;
            try {
                client = userDao.read(id);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            ((Client) client).setBanned(value);
            try {
                userDao.update(client);
            } catch (SQLException e) {
                log.error(e);
            }
            userDao.returnConnection();
            log.debug("isBanned " + ((Client) client).isBanned());
            return new ActionResult("controller?action=showUsers", true);
        } catch (Exception e) {
            log.error(e);
            return new ActionResult("WEB-INF/errorPage.jsp");
        }
    }
}


