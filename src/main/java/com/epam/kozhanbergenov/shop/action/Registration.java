package com.epam.kozhanbergenov.shop.action;

import com.epam.kozhanbergenov.shop.dao.DAOFactory;
import com.epam.kozhanbergenov.shop.dao.UserDAO;
import com.epam.kozhanbergenov.shop.dao.h2Dao.H2UserDAO;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import com.epam.kozhanbergenov.shop.entity.Administrator;
import com.epam.kozhanbergenov.shop.entity.Client;
import com.epam.kozhanbergenov.shop.util.ConfigurationManager;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registration implements Action {

    private static final Logger log = Logger.getLogger(Registration.class);
    private static final ConfigurationManager configurationManager = new ConfigurationManager("shopConfiguration.properties");
    private static final CharSequence SECRET_WORD = configurationManager.getValue("secretWordForAdminsRegister");
    String sentencePartSeparatorRegex = configurationManager.getValue("phoneNumberRegex");

    private String em1 = "*";
    private String em2 = "*";
    private String em3 = "*";
    private String em4 = "*";
    private String em5 = "*";
    private String em6 = "*";
    private String name;
    private String surname;
    private String address;
    private String phoneNumber;

    public String getReturnPage() {
        return "/WEB-INF/jsp/registration.jsp?em1=" + em1 + "&em2=" + em2 + "&em3=" + em3 + "&em4=" +
                em4 + "&em5=" + em5 + "&em6=" + em6 + "&name=" + name + "&surname=" + surname + "&address=" +
                address + "&phoneNumber=" + phoneNumber;
    }

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("Registration action was started");

        try {
            String login = req.getParameter("login");
            log.debug("Login is " + login);
            String password = req.getParameter("password");
            log.debug("Password is " + password);
            name = req.getParameter("name");
            surname = req.getParameter("surname");
            address = req.getParameter("address");
            phoneNumber = req.getParameter("phoneNumber");


            UserDAO userDAO =  DAOFactory.getDAOFactory(DAOFactory.H2).getUserDao();

            if (login == null && password == null && name == null && surname == null && address == null) {
                return new ActionResult("/WEB-INF/jsp/registration.jsp?em1=" + em1 + "&em2=" + em2 + "&em3=" + em3 + "&em4=" +
                        em4 + "&em5=" + em5 + "&em6=" + em6);
            }
            if (login.isEmpty()) em1 = "error.emptyField";
            if (password.isEmpty()) em2 = "error.emptyField";
            if (name.isEmpty()) em3 = "error.emptyField";
            if (surname.isEmpty()) em4 = "error.emptyField";
            if (address.isEmpty()) em5 = "error.emptyField";
            if (phoneNumber.isEmpty()) em6 = "error.emptyField";



            if (login.isEmpty() || password.isEmpty() || name.isEmpty() || surname.isEmpty() || address.isEmpty()) {
                userDAO.returnConnection();
                return new ActionResult(getReturnPage());
            }

            if (login.length() < 6) {
                em1 = "error.shortLogin";
                em3 = "*";
                em4 = "*";
                em5 = "*";
                return new ActionResult(getReturnPage());
            }

            if (password.length() < 6) {
                em1 = "*";
                em2 = "error.shortPassword";
                em3 = "*";
                em4 = "*";
                em5 = "*";
                return new ActionResult(getReturnPage());
            }

            Pattern p = Pattern.compile(sentencePartSeparatorRegex);
            Matcher matcher = p.matcher(phoneNumber);
            if (!matcher.matches()) {
                em1 = "*";
                em2 = "*";
                em3 = "*";
                em4 = "*";
                em5 = "*";
                em6 = "error.phoneNumber";
                return new ActionResult(getReturnPage());
            }


            if (!login.contains(SECRET_WORD)) {
                if (userDAO.checkLogin(login)) {
                    Client client = new Client(login, password, name, surname, address, phoneNumber);
                    userDAO.create(client);
                    userDAO.returnConnection();
                    HttpSession httpSession = req.getSession();
                    httpSession.setAttribute("user", client);
                } else {
                    userDAO.returnConnection();
                    em1 = "error.usedLogin";
                    em2 = "*";
                    em3 = "*";
                    em4 = "*";
                    em5 = "*";
                    return new ActionResult(getReturnPage());
                }
            } else {
                log.debug(login);
                login = login.replaceAll(SECRET_WORD.toString(), "");
                if (userDAO.checkLogin(login)) {
                    log.debug(login);
                    Administrator administrator = new Administrator(login, password);
                    userDAO.create(administrator);
                    userDAO.returnConnection();
                    HttpSession httpSession = req.getSession();
                    httpSession.setAttribute("user", administrator);
                } else {
                    userDAO.returnConnection();
                    em1 = "error.usedLogin";
                    em2 = "*";
                    em3 = "*";
                    em4 = "*";
                    em5 = "*";
                    return new ActionResult(getReturnPage());
                }
            }
            return new ActionResult("controller?action=welcome", true);

        } catch (Exception e) {
            log.error(e);
            return new ActionResult("WEB-INF/jsp/errorPage.jsp");
        }
    }

}
