/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.afweb.model;

import com.vpumlmodel.afweb.customer;

/**
 *
 * @author eddy
 */
public class CustomerObj extends customer {

// Customer - type
    public static final int INT_GUEST_USER = 0;
    public static final int INT_CLIENT_BASIC_USER = 10;
    public static final int INT_FUND_USER = 20;
    public static final int INT_ADMIN_USER = 99;
// Customer - type

    private String updateDateD = "";

    /**
     * @return the updateDateD
     */
    public String getUpdateDateD() {
        return updateDateD;
    }

    /**
     * @param updateDateD the updateDateD to set
     */
    public void setUpdateDateD(String updateDateD) {
        this.updateDateD = updateDateD;
    }
}
