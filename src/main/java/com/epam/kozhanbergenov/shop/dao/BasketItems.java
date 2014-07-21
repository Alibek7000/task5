package com.epam.kozhanbergenov.shop.dao;

import com.epam.kozhanbergenov.shop.dao.exception.DAOException;
import com.epam.kozhanbergenov.shop.dao.h2Dao.H2BasketDAO;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import com.epam.kozhanbergenov.shop.entity.Item;
import com.epam.kozhanbergenov.shop.entity.User;
import com.epam.kozhanbergenov.shop.util.ConfigurationManager;
import org.apache.log4j.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public final class BasketItems {
    private static final Logger log = Logger.getLogger(BasketItems.class);
    private ConfigurationManager configurationManager = new ConfigurationManager("database.properties");
    private final int BASKET_SAVE_DAYS = new Integer(configurationManager.getValue("Baskets.saveDays"));
    private Map<Item, Integer> basketItems = new HashMap<>();
    HttpServletRequest req;
    HttpServletResponse resp;
    private UUID uuid = null;
    private int userId = 0;
    private UUID cookieBasketUuid = null;

    public BasketItems(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("BasketItems started");
        this.req = req;
        this.resp = resp;
        userId = getUserId();
        BasketDAO basketDAO = new H2BasketDAO(ConnectionPool.getConnection());
        if (userId != 0) {
            try {
                uuid = basketDAO.getBasketIdByUserId(userId);
            } catch (DAOException e) {
                log.error(e);
            }
            if (uuid == null) {
                log.debug("There is no basket for this user!");
                try {
                    uuid = basketDAO.create(userId);
                } catch (DAOException e) {
                    log.error(e);
                }
            }
            log.debug("uuid " + uuid);
            cookieBasketUuid = getCookieBasketUuid();

        } else {
            uuid = getCookieBasketUuid();
            cookieBasketUuid = uuid;
        }

    }

    public Map<Item, Integer> getBasketItems() {
        log.debug("Trying to getBasketItems");
        log.debug("UserId = " + userId);
        BasketDAO basketDAO = new H2BasketDAO(ConnectionPool.getConnection());
        if (userId != 0) {
            try {
                basketItems = basketDAO.readByUserId(userId);
            } catch (DAOException e) {
                log.error(e);
            }
            log.debug("basketItems " + basketItems);
            log.debug("basketItems.size() " + basketItems.size());
            Map<Item, Integer> cookieBasket = null;

            try {
                uuid = basketDAO.getBasketIdByUserId(userId);
            } catch (DAOException e) {
                log.error(e);
            }
            try {
                log.debug("uuid = " + uuid);
                log.debug("cookieBasketUuid = " + cookieBasketUuid);
                    cookieBasket = basketDAO.read(cookieBasketUuid);
            } catch (DAOException e) {
                log.error(e);
            }
            boolean isBaseHasSameUser = false;
            boolean isInBaseOurUser = false;
            boolean isItAlienBasket = false;
            try {
                isBaseHasSameUser = (basketDAO.getUserIdByBasketId(cookieBasketUuid) != 0) ;
                isInBaseOurUser = (basketDAO.getUserIdByBasketId(cookieBasketUuid) == userId);
                isItAlienBasket = isBaseHasSameUser && !cookieBasketUuid.equals(uuid);
                log.debug("userId = "  + userId + " DBUserId = " + basketDAO.getUserIdByBasketId(cookieBasketUuid));
                log.debug(" isBaseHasSameUser " + isBaseHasSameUser + " isInBaseOurUser "+ isInBaseOurUser + " isItAlienBasket " + isItAlienBasket);
            } catch (DAOException e) {
                log.error(e);
            }
            if (cookieBasket != null && cookieBasketUuid != null
                    && !cookieBasketUuid.equals(uuid)
                   && !isItAlienBasket
                    ) {
                log.debug("Cookie basket have something...");
                Iterator<Map.Entry<Item, Integer>> iterator = cookieBasket.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Item, Integer> entry = iterator.next();
                    log.debug("Adding item from cookieBasket " + entry.getKey() + " quantity " + entry.getValue());
                            basketItems.put(entry.getKey(), entry.getValue());
                            addItem(entry.getKey(), entry.getValue());
                    iterator.remove();
                }
            }
            if (cookieBasket == null) {
                log.debug("cookieBasket == null");
            }
            try {
                uuid = basketDAO.getBasketIdByUserId(userId);
            } catch (DAOException e) {
                log.error(e);
            }
            addBasketIdToCookie(uuid);
        } else if (userId == 0) {
            try {
                log.debug("User id is 0");
                basketItems = basketDAO.read(uuid);
            } catch (DAOException e) {
                log.error(e);
            }
        }
        return basketItems;
    }

    public void addItem(Item item, Integer quantity) {
        log.debug("Trying to addItem");
        int newQuantity = quantity;

        BasketDAO basketDAO = new H2BasketDAO(ConnectionPool.getConnection());
        Map<Item, Integer> basketItems = null;

        try {
            if (userId != 0)
                basketItems = basketDAO.readByUserId(userId);
            else
                basketItems = basketDAO.read(uuid);
        } catch (DAOException e) {
            log.error(e);
        }

        Iterator<Map.Entry<Item, Integer>> iterator = basketItems.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Item, Integer> entry = iterator.next();
            if (entry.getKey().equals(item)) {
                log.debug("basket containsKey!");
                newQuantity += entry.getValue();
                removeItem(item.getId());
            }
        }
        if (userId != 0) {
            log.debug("User is exist!");
            try {
                if (uuid == null) {
                    uuid = basketDAO.create(userId);
                    log.debug("Basket id = " + uuid);
                }
                basketDAO.update(uuid, userId);
                addBasketIdToCookie(uuid);

            } catch (DAOException e) {
                log.error(e);
            }
        } else if (userId == 0) {
            log.debug("There is no user!");
            log.debug("Basket id = " + uuid);
            if (uuid == null) {
                try {
                    uuid = basketDAO.create(0);
                    log.debug("Basket id = " + uuid);
                } catch (DAOException e) {
                    log.error(e);
                }
                addBasketIdToCookie(uuid);
                cookieBasketUuid = uuid;
            }
        }
        try {
            basketDAO.addItem(uuid, item, newQuantity);
        } catch (DAOException e) {
            log.error(e);
        }
        log.debug(String.valueOf(item) + " quantity " + String.valueOf(newQuantity));
    }

    private void addBasketIdToCookie(UUID basketId) {
        Cookie cookie = new Cookie("uuid", String.valueOf(basketId));
        cookie.setMaxAge(BASKET_SAVE_DAYS * 24 * 60 * 60);
        resp.addCookie(cookie);
    }


    private int getUserId() {
        User user = (User) req.getSession().getAttribute("user");
        int id = 0;
        if (user != null) {
            id = user.getId();
        }
        return id;
    }

    public void removeItem(int itemId) {
        BasketDAO basketDAO = new H2BasketDAO(ConnectionPool.getConnection());
        try {
            basketDAO.deleteItemBasket(uuid, itemId);
        } catch (DAOException e) {
            log.error(e);
        }

    }


    public void releaseBasket() {
        for (Item item : getBasketItems().keySet()) {
            removeItem(item.getId());
        }
    }

    public UUID getCookieBasketUuid() {
        UUID uuid1 = null;
        Cookie[] cookies = req.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("uuid") && !cookie.getValue().equals("null")) {
                uuid1 = UUID.fromString(cookie.getValue());
            }
        }
        return uuid1;
    }
}

