package com.epam.kozhanbergenov.shop.action.adminSide;

import com.epam.kozhanbergenov.shop.action.Action;
import com.epam.kozhanbergenov.shop.action.ActionResult;
import com.epam.kozhanbergenov.shop.dao.OrderDao;
import com.epam.kozhanbergenov.shop.dao.h2Dao.H2OrderDao;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import com.epam.kozhanbergenov.shop.entity.Client;
import com.epam.kozhanbergenov.shop.entity.Order;
import com.epam.kozhanbergenov.shop.entity.User;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SendingOrder implements Action {
    private static final Logger log = Logger.getLogger(SendingOrder.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("SendingOrder action was started");
        try {
            HttpSession httpSession = req.getSession();
            User user = (User) httpSession.getAttribute("user");
            if (user == null || user instanceof Client) {
                return new ActionResult("/WEB-INF/jsp/errorPage.jsp?errorMessage=error.permissionDenied");
            }

            int id = new Integer(req.getParameter("id"));
            boolean value = Boolean.parseBoolean(req.getParameter("value"));

            OrderDao orderDao = new H2OrderDao(ConnectionPool.getConnection());

            Order order = null;

            order = orderDao.read(id);

            order.setSent(value);
            orderDao.setSent(order, value);

            orderDao.returnConnection();
            log.debug("is sent " + order.isSent());
            return new ActionResult("controller?action=showOrders", true);
        } catch (Exception e) {
            log.error(e);
            return new ActionResult("WEB-INF/jsp/errorPage.jsp");
        }
    }
}


