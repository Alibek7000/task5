package com.epam.kozhanbergenov.shop.action.adminSide;

import com.epam.kozhanbergenov.shop.action.Action;
import com.epam.kozhanbergenov.shop.action.ActionResult;
import com.epam.kozhanbergenov.shop.dao.CategoryDAO;
import com.epam.kozhanbergenov.shop.dao.DAOFactory;
import com.epam.kozhanbergenov.shop.dao.ItemDAO;
import com.epam.kozhanbergenov.shop.entity.Category;
import com.epam.kozhanbergenov.shop.entity.Client;
import com.epam.kozhanbergenov.shop.entity.Item;
import com.epam.kozhanbergenov.shop.entity.User;
import com.epam.kozhanbergenov.shop.util.ConfigurationManager;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.*;
import java.util.List;
import java.util.Map;

public class AddItem implements Action {
    private static final Logger log = Logger.getLogger(AddItem.class);
    private static ConfigurationManager configurationManager = new ConfigurationManager("shopConfiguration.properties");
    private static final String PATH_TO_IMAGES = configurationManager.getValue("pathToImages");
    private static final int IMAGE_MAX_SIZE = Integer.parseInt(configurationManager.getValue("imageMaxSizeInMegabytes"));


    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        try {
            HttpSession httpSession = req.getSession();
            User user = (User) httpSession.getAttribute("user");
            if (user == null || user instanceof Client) {
                return new ActionResult("/WEB-INF/jsp/errorPage.jsp?errorMessage=error.permissionDenied");
            }
            List<Category> categories = null;
            CategoryDAO categoryDAO =  DAOFactory.getDAOFactory(DAOFactory.H2).getCategoryDao();
            categories = categoryDAO.getAll();
            String name = req.getParameter("name");
            String description = req.getParameter("description");
            if (name == null && description == null) {
                httpSession.setAttribute("categories", categories);
                return new ActionResult("/WEB-INF/jsp/addItem.jsp");
            }
            ItemDAO itemDAO = DAOFactory.getDAOFactory(DAOFactory.H2).getItemDao();
            String quantityString = req.getParameter("quantity");
            String priceString = req.getParameter("price");
            int categoryId = 0;
            String em1 = "";
            String em2 = "";
            String em3 = "";
            String em4 = "";
            if (name.isEmpty()) em1 = "error.emptyField";
            if (description.isEmpty()) em2 = "error.emptyField";
            if (quantityString.isEmpty()) em3 = "error.emptyField";
            if (priceString.isEmpty()) em4 = "error.emptyField";

            if (!req.getParameter("categoryId").isEmpty())
                categoryId = Integer.parseInt(req.getParameter("categoryId"));

            if (name.isEmpty() || description.isEmpty() || quantityString.isEmpty() || priceString.isEmpty())
                return new ActionResult("/WEB-INF/jsp/addItem.jsp?em1="
                        + em1 + "&em2=" + em2 + "&em3=" + em3 + "&em4=" + em4);

            int quantity = 0;
            double price = 0;

            try {
                quantity = new Integer(req.getParameter("quantity"));
            } catch (Exception e) {
                log.error(e);
                em3 = "error.integer";
                return new ActionResult("/WEB-INF/jsp/addItem.jsp?em1="
                        + em1 + "&em2=" + em2 + "&em3=" + em3 + "&em4=" + em4);
            }

            try {
                price = new Double(req.getParameter("price").replace(",", "."));
            } catch (Exception e) {
                log.error(e);
                em4 = "error.doubleField";
                return new ActionResult("/WEB-INF/jsp/addItem.jsp?em1="
                        + em1 + "&em2=" + em2 + "&em3=" + em3 + "&em4=" + em4);
            }

            if (quantity < 0) {
                em3 = "error.integer";
                return new ActionResult("/WEB-INF/jsp/editItem.jsp?em1="
                        + em1 + "&em2=" + em2 + "&em3=" + em3 + "&em4=" + em4);
            }
            if (price < 0) {
                em4 = "error.doubleField";
                return new ActionResult("/WEB-INF/jsp/addItem.jsp?em1="
                        + em1 + "&em2=" + em2 + "&em3=" + em3 + "&em4=" + em4);
            }

            Map<Item, Integer> items = null;
            items = itemDAO.getAll(0, 0, false, true, true);
            for (Item item : items.keySet()) {
                if (item.getName().equals(name)) {
                    itemDAO.returnConnection();
                    em1 = "error.usedName";
                    return new ActionResult("/WEB-INF/jsp/addItem.jsp?em1="
                            + em1 + "&em2=" + em2 + "&em3=" + em3 + "&em4=" + em4);
                }

            }

            Item item = new Item(name, description, price);
            item.setCategory(categoryId);
            int id = 0;
            id = itemDAO.create(item, quantity);
            Part filePart = null;
            try {
                if (req.getPart("file").getSize() > 0) {
                    filePart = req.getPart("file");
                    log.debug("filePart.getSize()" + filePart.getSize());
                    if (filePart.getSize() > IMAGE_MAX_SIZE/1024/1000/8/1000) {
                        return new ActionResult("WEB-INF/jsp/errorPage.jsp?errorMessage=File is too big! Should be lower than 2Mb!");
                    }
                }
            } catch (IOException e) {
                log.error(e);
            } catch (ServletException e) {
                e.printStackTrace();
            }

            InputStream fileContent = null;
            try {
                if (filePart != null) {
                    log.debug("filePart!=null");
                    fileContent = filePart.getInputStream();
                }
            } catch (IOException e) {
                log.error(e);
            }

            OutputStream outputStream = null;
            if (fileContent != null) {
                log.debug("fileContent != null");
                try {
                    log.debug(PATH_TO_IMAGES);
                    outputStream = new FileOutputStream(new File(PATH_TO_IMAGES + "/" + id + ".png"));

                } catch (FileNotFoundException e) {
                    log.error(e);
                }
                int read = 0;
                byte[] bytes = new byte[1024];
                try {
                    while ((read = fileContent.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, read);
                    }
                } catch (IOException e) {
                    log.error(e);
                }
            }
            itemDAO.returnConnection();
            return new ActionResult("/index.jsp", true);
        } catch (Exception e) {
            log.error(e);
            return new ActionResult("WEB-INF/jsp/errorPage.jsp");
        }
    }
}








