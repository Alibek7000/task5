package com.epam.kozhanbergenov.shop.action;

import com.epam.kozhanbergenov.shop.dao.CategoryDao;
import com.epam.kozhanbergenov.shop.dao.h2Dao.H2CategoryDao;
import com.epam.kozhanbergenov.shop.dao.h2Dao.H2ItemDao;
import com.epam.kozhanbergenov.shop.dao.ItemDao;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import com.epam.kozhanbergenov.shop.entity.Item;
import com.epam.kozhanbergenov.shop.util.ConfigurationManager;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
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
            if (req.getParameter("sortingUp") != null)
                sortingUp = new Boolean(req.getParameter("sortingUp"));
            int noOfRecords = 0;
            int page = 1;
            if (req.getParameter("page") != null)
                page = Integer.parseInt(req.getParameter("page"));

            try {
                noOfRecords = itemDao.getNoOfRecords(categoryId);
            } catch (SQLException e) {
                log.debug(e);
            }
            int noOfPages = (int) Math.ceil(noOfRecords * 1.0 / RECORDS_PER_PAGE);
            req.setAttribute("noOfPages", noOfPages);
            req.setAttribute("currentPage", page);

            if (parentId != 0)
                try {
                    categoryItems = itemDao.getAllByCategory(categoryId, (page - 1) * RECORDS_PER_PAGE, RECORDS_PER_PAGE, sortingUp);
                } catch (SQLException e) {
                    log.error(e);
                }
            else {
                try {
                    categoryItems = itemDao.getAllByParentCategory(categoryId, (page - 1) * RECORDS_PER_PAGE, RECORDS_PER_PAGE, sortingUp);
                } catch (SQLException e) {
                    log.error(e);
                }
            }
            itemDao.returnConnection();
            CategoryDao categoryDao = new H2CategoryDao(ConnectionPool.getConnection());

            String categoryName = null;
            String categoryRuName = null;
            try {
                categoryName = categoryDao.read(categoryId).getName();
                categoryRuName = categoryDao.read(categoryId).getRuName();
            } catch (SQLException e) {
                log.error(e);
            }
            HttpSession httpSession = req.getSession();
            if (categoryItems != null) {
                httpSession.setAttribute("categoryItems", categoryItems);
                httpSession.setAttribute("categoryId", categoryId);
                httpSession.setAttribute("parentId", parentId);
                httpSession.setAttribute("categoryName", categoryName);
                httpSession.setAttribute("categoryRuName", categoryRuName);
            }
            return new ActionResult("/WEB-INF/categoryItems.jsp");
        } catch (Exception e) {
            log.error(e);
            return new ActionResult("WEB-INF/errorPage.jsp");
        }
    }
}