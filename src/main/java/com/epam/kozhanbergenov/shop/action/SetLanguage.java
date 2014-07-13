package com.epam.kozhanbergenov.shop.action;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class SetLanguage implements Action {
    private static final Logger log = Logger.getLogger(SetLanguage.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("SetLanguage action was started");
        try {
            log.debug(req.getLocale());
            String region = "";
            String language = req.getParameter("language");
            log.debug("language = " + language);
            if (language.equals("en")) {
                region = "US";
            }
            if (language.equals("ru")) {
                region = "RU";
            }
            Locale locale = new Locale(language, region);
            req.getSession().setAttribute("language", locale);
            return new ActionResult(req.getHeader("referer"), true);
        } catch (Exception e) {
            log.error(e);
            return new ActionResult("WEB-INF/jsp/errorPage.jsp");
        }
    }
}
