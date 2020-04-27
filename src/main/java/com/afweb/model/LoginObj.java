/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.afweb.model;

/**
 *
 * @author eddy
 */
public class LoginObj {
    private CustomerObj custObj;
    private WebStatus webMsg; 

    /**
     * @return the custObj
     */
    public CustomerObj getCustObj() {
        return custObj;
    }

    /**
     * @param custObj the custObj to set
     */
    public void setCustObj(CustomerObj custObj) {
        this.custObj = custObj;
    }

    /**
     * @return the webMsg
     */
    public WebStatus getWebMsg() {
        return webMsg;
    }

    /**
     * @param webMsg the webMsg to set
     */
    public void setWebMsg(WebStatus webMsg) {
        this.webMsg = webMsg;
    }
}
