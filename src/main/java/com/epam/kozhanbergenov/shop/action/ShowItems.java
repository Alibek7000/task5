package com.epam.kozhanbergenov.shop.action;

import com.epam.kozhanbergenov.shop.dao.CategoryDao;
import com.epam.kozhanbergenov.shop.dao.h2Dao.H2CategoryDao;
import com.epam.kozhanbergenov.shop.dao.h2Dao.H2ItemDao;
import com.epam.kozhanbergenov.shop.dao.ItemDao;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import com.epam.kozhanbergenov.shop.entity.Category;
import com.epam.kozhanbergenov.shop.entity.Item;
import com.epam.kozhanbergenov.shop.util.ConfigurationManager;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ShowItems implements Action {

    private static final Logger log = Logger.getLogger(ShowItems.class);
    private static final ConfigurationManager configurationManager = new ConfigurationManager("shopConfiguration.properties");
    private final static int RECORDS_PER_PAGE = new Integer(configurationManager.getValue("itemsPerPage"));

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("ShowItems action was started");
        try {
            HttpSession httpSession = req.getSession();

            List<Category> categories = null;
            CategoryDao categoryDao = new H2CategoryDao(ConnectionPool.getConnection());
            try {
                categories = categoryDao.getAll();
            } catch (SQLException e) {
                log.error(e);
            }
            httpSession.setAttribute("categories", categories);
            ItemDao itemDao = new H2ItemDao(ConnectionPool.getConnection());
            boolean sortingUp = false;
            if (req.getParameter("sortingUp") != null)
                sortingUp = new Boolean(req.getParameter("sortingUp"));

            int noOfRecords = 0;
            int page = 1;
            if (req.getParameter("page") != null)
                page = Integer.parseInt(req.getParameter("page"));

            Map<Item, Integer> itemIntegerMap = null;
            try {
                itemIntegerMap = itemDao.getAll((page - 1) * RECORDS_PER_PAGE, RECORDS_PER_PAGE, sortingUp);
            } catch (SQLException e) {
                log.error(e);
            }

            try {
                noOfRecords = itemDao.getNoOfRecords(0);
            } catch (SQLException e) {
                log.debug(e);
            }
            int noOfPages = (int) Math.ceil(noOfRecords * 1.0 / RECORDS_PER_PAGE);
            req.setAttribute("noOfPages", noOfPages);
            req.setAttribute("currentPage", page);

            itemDao.returnConnection();
            if (itemIntegerMap != null) httpSession.setAttribute("availableItems", itemIntegerMap);
            return new ActionResult("/WEB-INF/catalog.jsp");
        } catch (Exception e) {
            log.error(e);
            return new ActionResult("WEB-INF/errorPage.jsp");
        }
    }

}
