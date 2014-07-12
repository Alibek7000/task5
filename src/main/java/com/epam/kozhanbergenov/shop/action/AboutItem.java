package com.epam.kozhanbergenov.shop.action;

import com.epam.kozhanbergenov.shop.dao.exception.DaoException;
import com.epam.kozhanbergenov.shop.dao.h2Dao.H2ItemDao;
import com.epam.kozhanbergenov.shop.dao.ItemDao;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import com.epam.kozhanbergenov.shop.entity.Item;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;

public class AboutItem implements Action {

    private static final Logger log = Logger.getLogger(AboutItem.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("AboutItem action was started");
        ItemDao itemDao = new H2ItemDao(ConnectionPool.getConnection());
        int id = 0;
        try {
            id = new Integer(req.getParameter("id"));
        } catch (NumberFormatException e) {
            log.error(e);
            return new ActionResult("WEB-INF/errorPage.jsp");
        }
        Item item = null;
        try {
            item = itemDao.read(id);
        } catch (DaoException e) {
            log.error(e);
        }
        itemDao.returnConnection();
        HttpSession httpSession = req.getSession();
        if (item != null) httpSession.setAttribute("item", item);
        return new ActionResult("/WEB-INF/aboutItem.jsp");
    }

}
