package com.epam.kozhanbergenov.shop.action;

import com.epam.kozhanbergenov.shop.dao.UserDao;
import com.epam.kozhanbergenov.shop.dao.h2Dao.H2UserDao;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import com.epam.kozhanbergenov.shop.entity.Client;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Registration implements Action {

    private static final Logger log = Logger.getLogger(Registration.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("Registration action was started");
        try {
            String login = req.getParameter("login");
            log.debug("Login is " + login);
            String password = req.getParameter("password");
            log.debug("Password is " + password);
            String name = req.getParameter("name");
            String surname = req.getParameter("surname");
            String address = req.getParameter("address");
            String phoneNumber = req.getParameter("phoneNumber");
            if (login == null && password == null && name == null && surname == null && address == null) {
                return new ActionResult("/WEB-INF/jsp/registration.jsp");
            }
            String em1 = "";
            String em2 = "";
            String em3 = "";
            String em4 = "";
            String em5 = "";
            String em6 = "";
            UserDao userDao = new H2UserDao(ConnectionPool.getConnection());

            if (login.isEmpty()) em1 = "error.emptyField";
            if (password.isEmpty()) em2 = "error.emptyField";
            if (name.isEmpty()) em3 = "error.emptyField";
            if (surname.isEmpty()) em4 = "error.emptyField";
            if (address.isEmpty()) em5 = "error.emptyField";
            if (phoneNumber.isEmpty()) em6 = "error.emptyField";

            if (login.isEmpty() || password.isEmpty() || name.isEmpty() || surname.isEmpty() || address.isEmpty()) {
                userDao.returnConnection();
                return new ActionResult("/WEB-INF/jsp/registration.jsp?em1="
                        + em1 + "&em2=" + em2 + "&em3=" + em3 + "&em4=" + em4 + "&em5=" + em5 + "&em6=" + em6);
            }

            if (password.length() < 6) {
                em2 = "error.shortPassword";
                return new ActionResult("/WEB-INF/jsp/registration.jsp?em1="
                        + em1 + "&em2=" + em2 + "&em3=" + em3 + "&em4=" + em4 + "&em5=" + em5 + "&em6=" + em6);
            }
            if (userDao.checkLogin(login)) {
                Client client = new Client(login, password, name, surname, address, phoneNumber);

                userDao.create(client);

                userDao.returnConnection();
                HttpSession httpSession = req.getSession();
                httpSession.setAttribute("user", client);
                return new ActionResult("controller?action=welcome", true);
            } else {
                userDao.returnConnection();
                return new ActionResult("/WEB-INF/jsp/registration.jsp?em1=error.usedLogin");
            }
        } catch (Exception e) {
            log.error(e);
            return new ActionResult("WEB-INF/jsp/errorPage.jsp");
        }
    }

}
