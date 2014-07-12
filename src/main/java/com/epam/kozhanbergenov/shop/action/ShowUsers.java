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
import java.util.List;

public class ShowUsers implements Action {
    private static final Logger log = Logger.getLogger(ShowUsers.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("ShowUsers action was started");
        try {
            HttpSession httpSession = req.getSession();
            User user = (User) httpSession.getAttribute("user");
            if (user == null || user instanceof Client) {
                return new ActionResult("/WEB-INF/errorPage.jsp?errorMessage=You have not permissions access this page.");
            }
            UserDao userDao = new H2UserDao(ConnectionPool.getConnection());
            List<User> users = null;
            try {
                users = userDao.getAll();
            } catch (SQLException e) {
                log.error(e);
            }
            userDao.returnConnection();
            if (users != null) httpSession.setAttribute("users", users);
            return new ActionResult("/WEB-INF/users.jsp");
        } catch (Exception e) {
            log.error(e);
            return new ActionResult("WEB-INF/errorPage.jsp");
        }
    }

}
