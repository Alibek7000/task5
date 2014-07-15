package com.epam.kozhanbergenov.shop.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Map;

public class Order {
    private int id;
    private Client client;
    private Map<Item, Integer> itemIntegerMap;
    Date orderDate;
    boolean sent = false;
    BigDecimal amount;

    public Order() {
    }

    public double getAmount() {
        return amount.setScale(2, RoundingMode.UP).doubleValue();
    }

    public void setAmount(double amount) {
        this.amount = new BigDecimal(amount);
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }


    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Map<Item, Integer> getItemIntegerMap() {
        return itemIntegerMap;
    }

    public void setItemIntegerMap(Map<Item, Integer> itemIntegerMap) {
        this.itemIntegerMap = itemIntegerMap;
    }
}
