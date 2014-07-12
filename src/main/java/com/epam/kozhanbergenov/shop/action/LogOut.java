package com.epam.kozhanbergenov.shop.action;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LogOut implements Action {
    private static final Logger log = Logger.getLogger(LogOut.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("LogOut action was started");
        HttpSession httpSession = req.getSession();
        httpSession.removeAttribute("user");
        return new ActionResult("index.jsp", true);
    }
}


