package com.epam.kozhanbergenov.shop.action;

import com.epam.kozhanbergenov.shop.entity.Administrator;
import com.epam.kozhanbergenov.shop.entity.Client;
import com.epam.kozhanbergenov.shop.entity.User;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class WelcomeAction implements Action {
    private static final Logger log = Logger.getLogger(WelcomeAction.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("WelcomeAction was started");
        HttpSession httpSession = req.getSession();
        User user = (User) httpSession.getAttribute("user");
        if (user instanceof Administrator) {
            return new ActionResult("/WEB-INF/jsp/admin.jsp");
        }
        try {
            log.debug("User role is..." + user.getClass().getSimpleName());
            return new ActionResult("/WEB-INF/jsp/successfulLogin.jsp");
        } catch (Exception e) {
            log.error(e);
            return new ActionResult("WEB-INF/jsp/errorPage.jsp");
        }
    }
}
