package com.epam.kozhanbergenov.shop.entity;

public class Category {
    private int id;
    private String name;
    private int parentId;
    private String ruName;

    public Category() {
    }

    public Category(String name, int parentId, String description) {
        this.name = name;
        this.parentId = parentId;
        this.ruName = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getRuName() {
        return ruName;
    }

    public void setRuName(String ruName) {
        this.ruName = ruName;
    }

    @Override
    public String toString() {
        return "Category " +
                "id=" + id;
    }

}
