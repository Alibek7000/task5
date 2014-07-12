package com.epam.kozhanbergenov.shop.dao;

import com.epam.kozhanbergenov.shop.dao.exception.DaoException;
import com.epam.kozhanbergenov.shop.dao.h2Dao.H2BasketDao;
import com.epam.kozhanbergenov.shop.database.ConnectionPool;
import com.epam.kozhanbergenov.shop.entity.Item;
import com.epam.kozhanbergenov.shop.entity.User;
import com.epam.kozhanbergenov.shop.util.ConfigurationManager;
import org.apache.log4j.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class BasketItems {
    private static final Logger log = Logger.getLogger(BasketItems.class);
    private ConfigurationManager configurationManager = new ConfigurationManager("database.properties");
    private final int BASKET_SAVE_DAYS = new Integer(configurationManager.getValue("Baskets.saveDays"));
    private Map<Item, Integer> basketItems = new HashMap<>();
    HttpServletRequest req;
    HttpServletResponse resp;
    private int basketId = 0;
    private int userId = 0;
    private int cookieBasketId = 0;

    public BasketItems(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("BasketItems started");
        this.req = req;
        this.resp = resp;
        userId = getUserId();
        BasketDao basketDao = new H2BasketDao(ConnectionPool.getConnection());
        if (userId != 0) {
            try {
                basketId = basketDao.getBasketIByUserId(userId);
            } catch (DaoException e) {
                log.error(e);
            }
            if (basketId == 0) {
                log.debug("There is no basket for this user!");
                try {
                    basketId = basketDao.create(userId);
                } catch (DaoException e) {
                    log.error(e);
                }
            }
            log.debug("basketId " + basketId);
            cookieBasketId = getCookieBasketId();

        } else {
            basketId = getCookieBasketId();
            cookieBasketId = basketId;
        }

    }

    public Map<Item, Integer> getBasketItems() {
        log.debug("Trying to getBasketItems");
        log.debug("UserId = " + userId);
        BasketDao basketDao = new H2BasketDao(ConnectionPool.getConnection());
        if (userId != 0) {
            try {
                basketItems = basketDao.readByUserId(userId);
            } catch (DaoException e) {
                log.error(e);
            }
            log.debug("basketItems " + basketItems);
            log.debug("basketItems.size() " + basketItems.size());
            Map<Item, Integer> cookieBasket = null;
            try {
                log.debug("cookieBasketId = " + cookieBasketId);
                cookieBasket = basketDao.read(cookieBasketId);
            } catch (DaoException e) {
                log.error(e);
            }

            if (cookieBasket != null && cookieBasketId != basketId) {
                log.debug("Cookie basket have something...");
                Iterator<Map.Entry<Item, Integer>> iterator = cookieBasket.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Item, Integer> entry = iterator.next();
                    log.debug("Adding item form cookieBasket " + entry.getKey() + " quantity " + entry.getValue());
                    basketItems.put(entry.getKey(), entry.getValue());
                    addItem(entry.getKey(), entry.getValue());
                    iterator.remove();
                }
            }
            if (cookieBasket == null) {
                log.debug("cookieBasket == null");
            }
            try {
                basketId = basketDao.getBasketIByUserId(userId);
            } catch (DaoException e) {
                log.error(e);
            }
            addBasketIdToCookie(basketId);
        } else if (userId == 0) {
            try {
                log.debug("User id is 0");
                basketItems = basketDao.read(basketId);
            } catch (DaoException e) {
                log.error(e);
            }
        }
        return basketItems;
    }

    public void addItem(Item item, Integer quantity) {
        log.debug("Trying to addItem");
        int newQuantity = quantity;

        BasketDao basketDao = new H2BasketDao(ConnectionPool.getConnection());
        Map<Item, Integer> basketItems = null;

        try {
            if (userId != 0)
                basketItems = basketDao.readByUserId(userId);
            else
                basketItems = basketDao.read(basketId);
        } catch (DaoException e) {
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
                if (basketId == 0) {
                    basketId = basketDao.create(userId);
                    log.debug("Basket id = " + basketId);
                }
                basketDao.update(basketId, userId);
                addBasketIdToCookie(basketId);

            } catch (DaoException e) {
                log.error(e);
            }
        } else if (userId == 0) {
            log.debug("There is no user!");
            log.debug("Basket id = " + basketId);
            if (basketId == 0) {
                try {
                    basketId = basketDao.create(0);
                    log.debug("Basket id = " + basketId);
                } catch (DaoException e) {
                    log.error(e);
                }
                addBasketIdToCookie(basketId);
                cookieBasketId = basketId;
            }
        }
        try {
            basketDao.addItem(basketId, item, newQuantity);
        } catch (DaoException e) {
            log.error(e);
        }
        log.debug(String.valueOf(item) + " quantity " + String.valueOf(newQuantity));
    }

    private void addBasketIdToCookie(int basketId) {
        Cookie cookie = new Cookie("basketId", String.valueOf(basketId));
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
        BasketDao basketDao = new H2BasketDao(ConnectionPool.getConnection());
        try {
            basketDao.deleteItemBasket(basketId, itemId);
        } catch (DaoException e) {
            log.error(e);
        }

    }


    public void releaseBasket() {
        for (Item item : getBasketItems().keySet()) {
            removeItem(item.getId());
        }
    }

    public int getCookieBasketId() {
        int basketId = 0;
        Cookie[] cookies = req.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("basketId")) {
                basketId = new Integer(cookie.getValue());
            }
        }

        return basketId;
    }
}

