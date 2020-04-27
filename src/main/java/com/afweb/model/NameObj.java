/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.afweb.model;

import java.util.regex.Pattern;

/**
 *
 * @author eddy
 */
public class NameObj {

    private String normalizeName;

    static String strReplace(String str, String pattern, String replace) {
        int s = 0;
        int e = 0;
        StringBuffer result = new StringBuffer();

        while ((e = str.indexOf(pattern, s)) >= 0) {
            result.append(str.substring(s, e));
            result.append(replace);
            s = e + pattern.length();
        }
        result.append(str.substring(s));
        return result.toString();
    }

    public NameObj(String strName) {
        strName = strName.trim().toUpperCase();
        strName = strReplace(strName, ".", "-");
        strName = strReplace(strName, "@", "-");
        strName = strReplace(strName, " ", "");
        setNormalizeName(strName);
    }

    public static boolean isEmailValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."
                + "[a-zA-Z0-9_+&*-]+)*@"
                + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
                + "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null) {
            return false;
        }
        return pat.matcher(email).matches();
    }

    /**
     * @return the normalizeName
     */
    public String getNormalizeName() {
        return normalizeName;
    }

    /**
     * @param normalizeName the normalizeName to set
     */
    public void setNormalizeName(String normalizeName) {
        this.normalizeName = normalizeName;
    }
}
