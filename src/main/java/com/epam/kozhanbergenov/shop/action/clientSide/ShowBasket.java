package com.epam.kozhanbergenov.shop.action.clientSide;

import com.epam.kozhanbergenov.shop.action.Action;
import com.epam.kozhanbergenov.shop.action.ActionResult;
import com.epam.kozhanbergenov.shop.dao.BasketItems;
import com.epam.kozhanbergenov.shop.entity.Item;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public class ShowBasket implements Action {
    private static final Logger log = Logger.getLogger(ShowBasket.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("ShowBasket action was started");
            Map<Item, Integer> items = new BasketItems(req, resp).getBasketItems();
            double totalSum = 0;
            for (Map.Entry<Item, Integer> entry : items.entrySet()) {
                totalSum += entry.getKey().getPrice() * entry.getValue();
            }
            totalSum = new BigDecimal(totalSum).setScale(2, RoundingMode.UP).doubleValue();
            HttpSession httpSession = req.getSession();
            httpSession.setAttribute("items", items);
            httpSession.setAttribute("totalSum", totalSum);
            log.debug("items quantity = " + items.size());
            log.debug("totalSum = " + totalSum);
            return new ActionResult("/WEB-INF/jsp/basket.jsp");
//            return new ActionResult("WEB-INF/jsp/errorPage.jsp");
//        }
    }
}
