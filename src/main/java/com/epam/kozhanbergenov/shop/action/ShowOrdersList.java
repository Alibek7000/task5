package com.epam.kozhanbergenov.shop.action;

import com.epam.kozhanbergenov.shop.dao.h2Dao.H2OrderDao;
import com.epam.kozhanbergenov.shop.dao.OrderDao;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import com.epam.kozhanbergenov.shop.entity.Client;
import com.epam.kozhanbergenov.shop.entity.Order;
import com.epam.kozhanbergenov.shop.entity.User;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

public class ShowOrdersList implements Action {
    private static final Logger log = Logger.getLogger(ShowOrdersList.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("ShowOrdersList action was started");
        HttpSession httpSession = req.getSession();
        User user = (User) httpSession.getAttribute("user");
        if (user == null || user instanceof Client) {
            return new ActionResult("/WEB-INF/errorPage.jsp?errorMessage=You have not permissions access this page.");
        }
        OrderDao orderDao = new H2OrderDao(ConnectionPool.getConnection());
        List<Order> orders = null;
        try {
            orders = orderDao.getAll();
        } catch (SQLException e) {
            log.error(e);
        }
        orderDao.returnConnection();
        if (orders != null) httpSession.setAttribute("orders", orders);
        return new ActionResult("/WEB-INF/orders.jsp");
    }

}
