package com.epam.kozhanbergenov.shop.action;

import com.epam.kozhanbergenov.shop.DAO.DAOFactory;
import com.epam.kozhanbergenov.shop.DAO.UserDAO;
import com.epam.kozhanbergenov.shop.DAO.exception.DAOException;
import com.epam.kozhanbergenov.shop.DAO.H2DAO.H2UserDAO;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import com.epam.kozhanbergenov.shop.entity.User;
import com.epam.kozhanbergenov.shop.util.PasswordHashing;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

public class Login implements Action {
    private static final Logger log = Logger.getLogger(Login.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("Login was started");

        String login = req.getParameter("login");
        log.debug("Login is " + login);
        String password = req.getParameter("password");
        log.debug("Password is " + password);

        if (login == null || password == null) {
            return new ActionResult("/WEB-INF/jsp/loginPage.jsp");
        }
        password = PasswordHashing.getHashValue(password);
        UserDAO userDAO = DAOFactory.getDAOFactory(DAOFactory.H2).getUserDao();
        List<User> users = null;
        try {
            users = userDAO.getAll();
        } catch (DAOException e) {
            log.error(e);
        }

        userDAO.returnConnection();
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
        return new ActionResult("/WEB-INF/jsp/loginPage.jsp?errorMessage=loginIncorrect");
    }
}


