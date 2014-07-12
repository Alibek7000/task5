package com.epam.kozhanbergenov.shop.action;

import com.epam.kozhanbergenov.shop.dao.BasketDao;
import com.epam.kozhanbergenov.shop.dao.h2Dao.H2BasketDao;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import com.epam.kozhanbergenov.shop.util.ConfigurationManager;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

public class DeleteOldBaskets implements Action {
    private static final Logger log = Logger.getLogger(DeleteOldBaskets.class);
    private ConfigurationManager configurationManager = new ConfigurationManager("database.properties");
    private final int BASKET_SAVE_DAYS = new Integer(configurationManager.getValue("Baskets.saveDays"));

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("DeleteOldBaskets action was started");
        BasketDao basketDao = new H2BasketDao(ConnectionPool.getConnection());
        try {
            basketDao.deleteOld(BASKET_SAVE_DAYS);
        } catch (SQLException e) {
            log.error(e);
        }
        return new ActionResult("controller?action=showAdminPanel&message=oldBasketsRemoved", true);
    }
}
