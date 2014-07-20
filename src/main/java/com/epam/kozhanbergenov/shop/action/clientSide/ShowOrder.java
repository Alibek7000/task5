package com.epam.kozhanbergenov.shop.action.clientSide;

import com.epam.kozhanbergenov.shop.action.Action;
import com.epam.kozhanbergenov.shop.action.ActionResult;
import com.epam.kozhanbergenov.shop.DAO.OrderDAO;
import com.epam.kozhanbergenov.shop.DAO.H2DAO.H2OrderDAO;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import com.epam.kozhanbergenov.shop.entity.Item;
import com.epam.kozhanbergenov.shop.entity.Order;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

public class ShowOrder implements Action {
    private static final Logger log = Logger.getLogger(ShowOrder.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("ShowOrder action was started");
        try {
            int id = new Integer(req.getParameter("id"));
            log.debug("Order id = " + id);
            String message = req.getParameter("message");
            OrderDAO orderDAO = new H2OrderDAO(ConnectionPool.getConnection());
            Order order = null;
            order = orderDAO.read(id);
            Map<Item, Integer> orderItems = order.getItemIntegerMap();
            orderDAO.returnConnection();
            HttpSession httpSession = req.getSession();
            log.debug(orderItems);
            httpSession.setAttribute("orderItems", orderItems);
            return new ActionResult("/WEB-INF/jsp/showOrder.jsp?message=" + message + "&id" + id);
        } catch (Exception e) {
            log.error(e);
            return new ActionResult("WEB-INF/jsp/errorPage.jsp");
        }
    }
}
