package com.epam.kozhanbergenov.shop.action;

import com.epam.kozhanbergenov.shop.dao.*;
import com.epam.kozhanbergenov.shop.dao.h2Dao.H2ItemDao;
import com.epam.kozhanbergenov.shop.dao.h2Dao.H2OrderDao;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import com.epam.kozhanbergenov.shop.entity.*;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.Map;

public class Pay implements Action {

    private static final Logger log = Logger.getLogger(Pay.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("Pay action was started");
        try {
            HttpSession httpSession = req.getSession();
            User user = (User) httpSession.getAttribute("user");
            if (user == null || user instanceof Administrator || ((Client) user).isBanned()) {
                return new ActionResult("/WEB-INF/errorPage.jsp?errorMessage=error.permissionDenied.");
            }
            boolean enough = true;
            ItemDao itemDao = new H2ItemDao(ConnectionPool.getConnection());
            Map<Item, Integer> items = (Map<Item, Integer>) httpSession.getAttribute("items");
            try {
                if (!itemDao.enoughQuantity(items)) {
                    enough = false;
                }
            } catch (SQLException e) {
                log.debug(e);
            }
            itemDao.returnConnection();
            if (!enough) {
                return new ActionResult("controller?action=showBasket&errorMessage=error.outOfStock");
            } else {
                Order order = new Order();
                order.setClient((Client) httpSession.getAttribute("user"));
                order.setItemIntegerMap(items);
                log.debug(order.getClient());
                int orderId = 0;
                OrderDao orderDao = new H2OrderDao(ConnectionPool.getConnection());
                try {
                    orderId = orderDao.create(order);
                    try {
                        if (orderDao.read(orderId).getId() != 0) {
                            new BasketItems(req, resp).releaseBasket();
                        }
                    } catch (SQLException e) {
                        log.error(e);
                    }
                    return new ActionResult("controller?action=showOrder&message=orderAccepted&id=" + orderId, true);
                } catch (SQLException e) {
                    log.error(e);
                }
                return new ActionResult("controller?action=showError&errorMessage=error.orderNotAccepted", true);
            }
        } catch (Exception e) {
            log.error(e);
            return new ActionResult("WEB-INF/errorPage.jsp");
        }
    }
}