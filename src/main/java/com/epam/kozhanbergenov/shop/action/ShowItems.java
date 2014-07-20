package com.epam.kozhanbergenov.shop.action;

import com.epam.kozhanbergenov.shop.DAO.CategoryDAO;
import com.epam.kozhanbergenov.shop.DAO.ItemDAO;
import com.epam.kozhanbergenov.shop.DAO.H2DAO.H2CategoryDAO;
import com.epam.kozhanbergenov.shop.DAO.H2DAO.H2ItemDAO;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import com.epam.kozhanbergenov.shop.entity.Category;
import com.epam.kozhanbergenov.shop.entity.Item;
import com.epam.kozhanbergenov.shop.util.ConfigurationManager;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

public class ShowItems implements Action {

    private static final Logger log = Logger.getLogger(ShowItems.class);
    private static final ConfigurationManager configurationManager = new ConfigurationManager("shopConfiguration.properties");
    private final static int RECORDS_PER_PAGE = new Integer(configurationManager.getValue("itemsPerPage"));
    private static final String PATH_TO_IMAGES = configurationManager.getValue("pathToImages");

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("ShowItems action was started");
        try {
            HttpSession httpSession = req.getSession();

            List<Category> categories = null;
            CategoryDAO categoryDAO = new H2CategoryDAO(ConnectionPool.getConnection());
            categories = categoryDAO.getAll();
            httpSession.setAttribute("categories", categories);
            ItemDAO itemDAO = new H2ItemDAO(ConnectionPool.getConnection());
            boolean sortingUp = false;
            boolean sortingByName = false;
            boolean sortingByPrice = false;
            if (req.getParameter("sortingUp") != null)
                sortingUp = new Boolean(req.getParameter("sortingUp"));
            sortingByName = new Boolean(req.getParameter("sortingByName"));
            sortingByPrice = new Boolean(req.getParameter("sortingByPrice"));

            int noOfRecords = 0;
            int page = 1;
            if (req.getParameter("page") != null)
                page = Integer.parseInt(req.getParameter("page"));

            Map<Item, Integer> itemIntegerMap = itemDAO.getAll((page - 1) * RECORDS_PER_PAGE, RECORDS_PER_PAGE, sortingByName, sortingByPrice, sortingUp);
            noOfRecords = itemDAO.getNoOfRecords(0);

            int noOfPages = (int) Math.ceil(noOfRecords * 1.0 / RECORDS_PER_PAGE);
            req.setAttribute("noOfPages", noOfPages);
            req.setAttribute("currentPage", page);

            itemDAO.returnConnection();
            httpSession.removeAttribute("availableItems");
            if (itemIntegerMap != null) httpSession.setAttribute("availableItems", itemIntegerMap);
            httpSession.setAttribute("pathToImages", PATH_TO_IMAGES);
            return new ActionResult("WEB-INF/jsp/catalog.jsp");
        } catch (Exception e) {
            log.error(e);
            return new ActionResult("WEB-INF/jsp/errorPage.jsp");
        }
    }

}
