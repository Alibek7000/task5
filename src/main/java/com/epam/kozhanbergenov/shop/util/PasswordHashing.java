package com.epam.kozhanbergenov.shop.util;

import org.apache.log4j.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class PasswordHashing {
    private static final Logger log = Logger.getLogger(PasswordHashing.class);
    private PasswordHashing() {
    }

    public static String getHashValue(String password) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            log.error(e);
        }
        md.update(password.getBytes());
        byte byteData[] = md.digest();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        log.debug("Hex format : " + sb.toString());
        return sb.toString();
    }
}
