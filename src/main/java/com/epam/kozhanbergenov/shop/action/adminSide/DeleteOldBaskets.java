package com.epam.kozhanbergenov.shop.action.adminSide;

import com.epam.kozhanbergenov.shop.action.Action;
import com.epam.kozhanbergenov.shop.action.ActionResult;
import com.epam.kozhanbergenov.shop.dao.BasketDAO;
import com.epam.kozhanbergenov.shop.dao.DAOFactory;
import com.epam.kozhanbergenov.shop.dao.exception.DAOException;
import com.epam.kozhanbergenov.shop.dao.h2Dao.H2BasketDAO;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import com.epam.kozhanbergenov.shop.util.ConfigurationManager;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteOldBaskets implements Action {
    private static final Logger log = Logger.getLogger(DeleteOldBaskets.class);
    private ConfigurationManager configurationManager = new ConfigurationManager("database.properties");
    private final int BASKET_SAVE_DAYS = new Integer(configurationManager.getValue("Baskets.saveDays"));

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("DeleteOldBaskets action was started");
        BasketDAO basketDAO =  DAOFactory.getDAOFactory(DAOFactory.H2).getBasketDao();
        try {
            basketDAO.deleteOld(BASKET_SAVE_DAYS);
        } catch (DAOException e) {
            log.error(e);
        }
        return new ActionResult("controller?action=showAdminPanel&message=oldBasketsRemoved", true);
    }
}
