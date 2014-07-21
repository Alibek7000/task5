package com.epam.kozhanbergenov.shop.action.adminSide;

import com.epam.kozhanbergenov.shop.action.Action;
import com.epam.kozhanbergenov.shop.action.ActionResult;
import com.epam.kozhanbergenov.shop.dao.DAOFactory;
import com.epam.kozhanbergenov.shop.dao.ItemDAO;
import com.epam.kozhanbergenov.shop.dao.h2Dao.H2ItemDAO;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import com.epam.kozhanbergenov.shop.entity.Client;
import com.epam.kozhanbergenov.shop.entity.User;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;

public class RemoveItemFromBase implements Action {
    private static final Logger log = Logger.getLogger(RemoveItemFromBase.class);

    @Override
    public ActionResult execute(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("RemoveItemFromBase action was started");
        try {
            HttpSession httpSession = req.getSession();
            User user = (User) httpSession.getAttribute("user");
            if (user == null || user instanceof Client) {
                return new ActionResult("/WEB-INF/jsp/errorPage.jsp?errorMessage=error.permissionDenied");
            }
            int id = new Integer(req.getParameter("id"));
            ItemDAO itemDAO =  DAOFactory.getDAOFactory(DAOFactory.H2).getItemDao();
            itemDAO.delete(itemDAO.read(id));
            File file = new File(req.getSession().getServletContext().getRealPath("/") + "/images/items/" + id + ".png");
            if (file.delete()) {
                log.debug(file.getName() + " is deleted!");
            } else {
                log.debug("Picture delete operation is failed.");
            }
            itemDAO.returnConnection();
            return new ActionResult("/index.jsp", true);
        } catch (Exception e) {
            log.error(e);
            return new ActionResult("WEB-INF/jsp/errorPage.jsp");
        }
    }
}