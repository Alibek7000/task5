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
        BasketDao basketDao = new H2BasketDao(ConnectionPool.getConnection());
        if (userId != 0) {
            try {
                uuid = basketDao.getBasketIdByUserId(userId);
            } catch (DaoException e) {
                log.error(e);
            }
            if (uuid == null) {
                log.debug("There is no basket for this user!");
                try {
                    uuid = basketDao.create(userId);
                } catch (DaoException e) {
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
                log.debug("uuid = " + uuid);
                log.debug("cookieBasketUuid = " + cookieBasketUuid);
                    cookieBasket = basketDao.read(cookieBasketUuid);
            } catch (DaoException e) {
                log.error(e);
            }
            boolean diffUsers = false;
            try {
                diffUsers = basketDao.getUserIdByBasketId(cookieBasketUuid) == userId;
                log.debug("userId = "  + userId + "oldUserId = " + basketDao.getUserIdByBasketId(cookieBasketUuid));
                log.debug(diffUsers);
            } catch (DaoException e) {
                log.error(e);
            }
            if (cookieBasket != null && !(cookieBasketUuid == null) && !cookieBasketUuid.equals(uuid) && diffUsers) {
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
                uuid = basketDao.getBasketIdByUserId(userId);
            } catch (DaoException e) {
                log.error(e);
            }
            addBasketIdToCookie(uuid);
        } else if (userId == 0) {
            try {
                log.debug("User id is 0");
                basketItems = basketDao.read(uuid);
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
                basketItems = basketDao.read(uuid);
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
                if (uuid == null) {
                    uuid = basketDao.create(userId);
                    log.debug("Basket id = " + uuid);
                }
                basketDao.update(uuid, userId);
                addBasketIdToCookie(uuid);

            } catch (DaoException e) {
                log.error(e);
            }
        } else if (userId == 0) {
            log.debug("There is no user!");
            log.debug("Basket id = " + uuid);
            if (uuid == null) {
                try {
                    uuid = basketDao.create(0);
                    log.debug("Basket id = " + uuid);
                } catch (DaoException e) {
                    log.error(e);
                }
                addBasketIdToCookie(uuid);
                cookieBasketUuid = uuid;
            }
        }
        try {
            basketDao.addItem(uuid, item, newQuantity);
        } catch (DaoException e) {
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
        BasketDao basketDao = new H2BasketDao(ConnectionPool.getConnection());
        try {
            basketDao.deleteItemBasket(uuid, itemId);
        } catch (DaoException e) {
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

