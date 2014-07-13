package com.epam.kozhanbergenov.shop.action.clientSide;

import com.epam.kozhanbergenov.shop.action.Action;
import com.epam.kozhanbergenov.shop.action.ActionResult;
import com.epam.kozhanbergenov.shop.dao.BasketItems;
import com.epam.kozhanbergenov.shop.entity.Administrator;
import com.epam.kozhanbergenov.shop.entity.Client;
import com.epam.kozhanbergenov.shop.entity.User;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class RemoveItem implements Action {
    private static final Logger log = Logger.getLogger(RemoveItem.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("RemoveItem action was started");
        try {
            HttpSession httpSession = req.getSession();
            User user = (User) httpSession.getAttribute("user");
            if (user == null || user instanceof Administrator || ((Client) user).isBanned()) {
                return new ActionResult("/WEB-INF/jsp/errorPage.jsp?errorMessage=error.permissionDenied");
            }
            int id = new Integer(req.getParameter("id"));
            BasketItems basketItems = new BasketItems(req, resp);

            basketItems.removeItem(id);
            httpSession.removeAttribute("items");
            httpSession.setAttribute("items", basketItems.getBasketItems());
            return new ActionResult("controller?action=showBasket", true);
        } catch (Exception e) {
            log.error(e);
            return new ActionResult("WEB-INF/jsp/errorPage.jsp");
        }
    }
}
