package com.epam.kozhanbergenov.shop.action;

import com.epam.kozhanbergenov.shop.DAO.BasketItems;
import com.epam.kozhanbergenov.shop.entity.Item;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Basket implements Action {
    private static final Logger log = Logger.getLogger(Basket.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("BasketAction was started");
//        try {
            HttpSession httpSession = req.getSession();
            Item item = (Item) httpSession.getAttribute("item");
            log.debug(item);
            int quantity = (int) httpSession.getAttribute("quantity");
            log.debug("quantity " + quantity);
            BasketItems basketItems = new BasketItems(req, resp);
            basketItems.addItem(item, quantity);
            httpSession.removeAttribute("items");
            httpSession.setAttribute("items", basketItems.getBasketItems());
            return new ActionResult("controller?action=showBasket", true);
//        } catch (Exception e) {
//            log.error(e);
//            return new ActionResult("WEB-INF/jsp/errorPage.jsp");
//        }
    }
}
