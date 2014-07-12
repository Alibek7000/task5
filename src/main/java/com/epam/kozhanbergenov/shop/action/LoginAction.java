package com.epam.kozhanbergenov.shop.action;

import com.epam.kozhanbergenov.shop.dao.h2Dao.H2UserDao;
import com.epam.kozhanbergenov.shop.dao.UserDao;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import com.epam.kozhanbergenov.shop.entity.User;
import com.epam.kozhanbergenov.shop.util.PasswordHashing;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

public class LoginAction implements Action {
    private static final Logger log = Logger.getLogger(LoginAction.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("LoginAction was started");

        String login = req.getParameter("login");
        log.debug("Login is " + login);
        String password = req.getParameter("password");
        log.debug("Password is " + password);

        if (login == null || password == null) {
            return new ActionResult("/WEB-INF/loginPage.jsp");
        }
        password = PasswordHashing.getHashValue(password);
        UserDao userDao = new H2UserDao(ConnectionPool.getConnection());
        List<User> users = null;
        try {
            users = userDao.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        userDao.returnConnection();
        String login2;
        String password2;

        if (users != null) {
            for (User user : users) {
                if (user != null) {
                    login2 = user.getLogin();
                    password2 = user.getPassword();
                    if (login.equals(login2) && password.equals(password2)) {
                        HttpSession httpSession = req.getSession();
                        httpSession.setAttribute("user", user);
                        log.debug("login and password is correct");
                        log.debug(user.getClass().getSimpleName());
                        return new ActionResult("controller?action=welcome", true);
                    }

                }
            }
        }

        log.debug("login and password is incorrect");
        return new ActionResult("/WEB-INF/loginPage.jsp?errorMessage=loginIncorrect");
    }
}


