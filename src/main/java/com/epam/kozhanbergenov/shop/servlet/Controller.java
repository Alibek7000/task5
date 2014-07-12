package com.epam.kozhanbergenov.shop.servlet;


import com.epam.kozhanbergenov.shop.action.Action;
import com.epam.kozhanbergenov.shop.action.ActionFactory;
import com.epam.kozhanbergenov.shop.action.ActionResult;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/upload")
@MultipartConfig
public class Controller extends HttpServlet {
    private static final Logger log = Logger.getLogger(Controller.class);

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("Controller was started");
        req.setCharacterEncoding("UTF-8");
        ActionFactory actionFactory = ActionFactory.getInstance();
        Action action = actionFactory.getAction(req);
        ActionResult actionResult = action.execute(req, resp);
        resultExecute(req, resp, actionResult);
    }

    protected void resultExecute(HttpServletRequest req, HttpServletResponse resp, ActionResult actionResult) {
        if (actionResult.isRedirect()) {
            try {
                resp.sendRedirect(actionResult.getPage());
                log.debug("sendRedirect to " + actionResult.getPage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                req.getRequestDispatcher(actionResult.getPage()).forward(req, resp);
                log.debug("forward to " + actionResult.getPage());
            } catch (ServletException e) {
                log.error(e);
            } catch (IOException e) {
                log.error(e);
            }
        }
    }
}
