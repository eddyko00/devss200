/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.afweb.model.ssns;

/**
 *
 * @author koed
 */
public class testData {

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }
    private int  accid;
    private int type;
    private String testurl="";
    private String username="";

    /**
     * @return the accid
     */
    public int getAccid() {
        return accid;
    }

    /**
     * @param accid the accid to set
     */
    public void setAccid(int accid) {
        this.accid = accid;
    }

    /**
     * @return the testurl
     */
    public String getTesturl() {
        return testurl;
    }

    /**
     * @param testurl the testurl to set
     */
    public void setTesturl(String testurl) {
        this.testurl = testurl;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
}
