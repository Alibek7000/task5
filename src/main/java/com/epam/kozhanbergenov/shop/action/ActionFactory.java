package com.epam.kozhanbergenov.shop.action;


import com.epam.kozhanbergenov.shop.action.adminSide.*;
import com.epam.kozhanbergenov.shop.action.clientSide.*;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public final class ActionFactory {
    private static final Logger log = Logger.getLogger(ActionFactory.class);
    private Map<String, Action> actions = new HashMap<>();
    private static final ActionFactory INSTANCE = new ActionFactory();

    private ActionFactory() {
        actions.put("welcome", new WelcomeAction());
        actions.put("login", new Login());
        actions.put("registration", new Registration());
        actions.put("showItems", new ShowItems());
        actions.put("aboutItem", new AboutItem());
        actions.put("buyItemButton", new BuyItemButton());
        actions.put("basket", new Basket());
        actions.put("pay", new Pay());
        actions.put("showBasket", new ShowBasket());
        actions.put("showOrder", new ShowOrder());
        actions.put("removeItem", new RemoveItem());
        actions.put("addItem", new AddItem());
        actions.put("showUsers", new ShowUsers());
        actions.put("baningClient", new BaningClient());
        actions.put("logOut", new LogOut());
        actions.put("showError", new ShowError());
        actions.put("showAdminPanel", new ShowAdminPanel());
        actions.put("removeItemFromBase", new RemoveItemFromBase());
        actions.put("showEditItemPage", new ShowEditItemPage());
        actions.put("editItem", new EditItem());
        actions.put("addCategory", new AddCategory());
        actions.put("showCategories", new ShowCategories());
        actions.put("removeCategory", new RemoveCategory());
        actions.put("showCategoryItems", new ShowCategoryItems());
        actions.put("showEditCategoryPage", new ShowEditCategoryPage());
        actions.put("editCategory", new EditCategory());
        actions.put("showOrders", new ShowOrders());
        actions.put("sendingOrder", new SendingOrder());
        actions.put("deleteOldBaskets", new DeleteOldBaskets());
        actions.put("setLanguage", new SetLanguage());
    }

    public static ActionFactory getInstance() {
        return INSTANCE;
    }

    public Action getAction(HttpServletRequest req) {
        String actionName = req.getParameter("action");
        log.debug("actionName " + actionName);
        Action action = null;
        if (actions.containsKey(actionName)) {
            action = actions.get(actionName);

        } else {
            action = actions.get("showError");
        }
        return action;
    }
}
