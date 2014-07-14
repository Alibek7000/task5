package com.epam.kozhanbergenov.shop.action;

import com.epam.kozhanbergenov.shop.dao.CategoryDao;
import com.epam.kozhanbergenov.shop.dao.ItemDao;
import com.epam.kozhanbergenov.shop.dao.h2Dao.H2CategoryDao;
import com.epam.kozhanbergenov.shop.dao.h2Dao.H2ItemDao;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import com.epam.kozhanbergenov.shop.entity.Item;
import com.epam.kozhanbergenov.shop.util.ConfigurationManager;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

public class ShowCategoryItems implements Action {
    private static final Logger log = Logger.getLogger(ShowCategoryItems.class);
    private static final ConfigurationManager configurationManager = new ConfigurationManager("shopConfiguration.properties");
    private final static int RECORDS_PER_PAGE = new Integer(configurationManager.getValue("itemsPerPage"));

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("ShowCategoryItems action was started");
        try {
            int categoryId = new Integer(req.getParameter("categoryId"));
            int parentId = new Integer(req.getParameter("parentId"));
            log.debug("categoryId = " + categoryId);
            log.debug("ParentId = " + parentId);
            ItemDao itemDao = new H2ItemDao(ConnectionPool.getConnection());
            Map<Item, Integer> categoryItems = null;
            boolean sortingUp = false;
            boolean sortingByName = false;
            boolean sortingByPrice = false;
            if (req.getParameter("sortingUp") != null)
                sortingUp = new Boolean(req.getParameter("sortingUp"));
            if (req.getParameter("sortingByName") != null)
                sortingByName = new Boolean(req.getParameter("sortingByName"));
            if (req.getParameter("sortingByPrice") != null)
                sortingByPrice = new Boolean(req.getParameter("sortingByPrice"));
            int page = 1;
            if (req.getParameter("page") != null)
                page = Integer.parseInt(req.getParameter("page"));

            int noOfRecords = itemDao.getNoOfRecords(categoryId);
            int noOfPages = (int) Math.ceil(noOfRecords * 1.0 / RECORDS_PER_PAGE);
            req.setAttribute("noOfPages", noOfPages);
            req.setAttribute("currentPage", page);

            if (parentId != 0)
                categoryItems = itemDao.getAllByCategory(categoryId, (page - 1) * RECORDS_PER_PAGE, RECORDS_PER_PAGE, sortingByName, sortingByPrice, sortingUp);
            else
                categoryItems = itemDao.getAllByParentCategory(categoryId, (page - 1) * RECORDS_PER_PAGE, RECORDS_PER_PAGE, sortingByName, sortingByPrice, sortingUp);
            itemDao.returnConnection();
            CategoryDao categoryDao = new H2CategoryDao(ConnectionPool.getConnection());

            String categoryName = null;
            String categoryRuName = null;

            categoryName = categoryDao.read(categoryId).getName();
            categoryRuName = categoryDao.read(categoryId).getRuName();

            HttpSession httpSession = req.getSession();
            if (categoryItems != null) {
                httpSession.setAttribute("categoryItems", categoryItems);
                httpSession.setAttribute("categoryId", categoryId);
                httpSession.setAttribute("parentId", parentId);
                httpSession.setAttribute("categoryName", categoryName);
                httpSession.setAttribute("categoryRuName", categoryRuName);
            }
            return new ActionResult("/WEB-INF/jsp/categoryItems.jsp");
        } catch (Exception e) {
            log.error(e);
            return new ActionResult("WEB-INF/jsp/errorPage.jsp");
        }
    }
}
