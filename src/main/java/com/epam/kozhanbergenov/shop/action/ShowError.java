package com.epam.kozhanbergenov.shop.action;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ShowError implements Action {
    private static final Logger log = Logger.getLogger(ShowError.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("ShowError action was started");
        return new ActionResult("WEB-INF/errorPage.jsp");
    }
}
