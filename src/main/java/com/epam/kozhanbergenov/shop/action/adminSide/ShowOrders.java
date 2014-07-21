package com.epam.kozhanbergenov.shop.action.adminSide;

import com.epam.kozhanbergenov.shop.action.Action;
import com.epam.kozhanbergenov.shop.action.ActionResult;
import com.epam.kozhanbergenov.shop.dao.DAOFactory;
import com.epam.kozhanbergenov.shop.dao.OrderDAO;
import com.epam.kozhanbergenov.shop.dao.exception.DAOException;
import com.epam.kozhanbergenov.shop.dao.h2Dao.H2OrderDAO;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import com.epam.kozhanbergenov.shop.entity.Client;
import com.epam.kozhanbergenov.shop.entity.Order;
import com.epam.kozhanbergenov.shop.entity.User;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

public class ShowOrders implements Action {
    private static final Logger log = Logger.getLogger(ShowOrders.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("ShowOrders action was started");
        HttpSession httpSession = req.getSession();
        User user = (User) httpSession.getAttribute("user");
        if (user == null || user instanceof Client) {
            return new ActionResult("/WEB-INF/jsp/errorPage.jsp?errorMessage=error.permissionDenied");
        }
        OrderDAO orderDAO =  DAOFactory.getDAOFactory(DAOFactory.H2).getOrderDao();
        List<Order> orders = null;
        try {
            orders = orderDAO.getAll();
        } catch (DAOException e) {
            log.error(e);
        }

        orderDAO.returnConnection();
        if (orders != null) httpSession.setAttribute("orders", orders);
        return new ActionResult("/WEB-INF/jsp/orders.jsp");
    }

}
