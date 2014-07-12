package com.epam.kozhanbergenov.shop.action;

public class ActionResult {
    private String page;
    private boolean redirect = false;

    public ActionResult(String page) {
        this.page = page;
    }

    public ActionResult(String page, boolean redirect) {
        this.page = page;
        this.redirect = redirect;
    }

    public boolean isRedirect() {
        return redirect;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }


}
