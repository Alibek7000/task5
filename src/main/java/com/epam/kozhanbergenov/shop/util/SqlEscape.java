package com.epam.kozhanbergenov.shop.util;

public final class SqlEscape {
    private SqlEscape() {
    }

    public static String getClearString(String text) {
        return text.replace("'", " ");
    }
}
