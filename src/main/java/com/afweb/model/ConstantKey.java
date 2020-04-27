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
public class ConstantKey {

    public static final String VERSION = "ver1.0";

    public static final int SRV_LOCKTYPE = 30;
    public static final int FE_LOCKTYPE = 20;
    public static final int ETL_LOCKTYPE = 40;    
    public static final int MON_LOCKTYPE = 50;      
    public static final int MONSTART_LOCKTYPE = 52;     
    public static final int H2_LOCKTYPE = 100;

    public static final String PP_BASIC = "BASIC";
    public static final int INT_PP_BASIC = 0;
    public static final int INT_PP_BASIC_NUM = 5;
    public static final float INT_PP_BASIC_PRICE = 10;
    public static final String PP_PREMIUM = "PREMIUM";
    public static final int INT_PP_PREMIUM = 10;
    public static final int INT_PP_REMIUM_NUM = 10;
    public static final float INT_PP_REMIUM_PRICE = 15;
    public static final String PP_DELUXE = "DELUXE";
    public static final int INT_PP_DELUXE = 20;
    public static final int INT_PP_DELUXE_NUM = 20;
    public static final float INT_PP_DELUXE_PRICE = 30;

    public static final String MSG_OPEN = "ENABLE";
    public static final int OPEN = 0;

    public static final String MSG_CLOSE = "CLOSE";
    public static final int CLOSE = 1;

    public static final String MSG_ENABLE = "ENABLE";
    public static final int ENABLE = 0;

    public static final String MSG_DISABLE = "DISABLE";
    public static final int DISABLE = 1;

    public static final String MSG_NO_ACTIVE = "NO_ACTIVATE";
    public static final int NOACT = 4;

    public static final String MSG_COMPLETED = "COMPLETED";
    public static final int COMPLETED = 5;

/////// SubStatus        
    public static final String MSG_NEW = "NEW";
    public static final int NEW = 1;

    public static final String MSG_EXISTED = "EXISTED";
    public static final int EXISTED = 2;

    public static final String MSG_INITIAL = "INITIAL";
    public static final int INITIAL = 2;

//// communication type
    public static final String COM_SIGNAL = "MSG_SIG";
    public static final int INT_COM_SIGNAL = 0;

    public static final String COM_SPLIT = "MSG_SPLIT";
    public static final int INT_COM_SPLIT = 2;

    public static final String BILLING = "BILLING";
    public static final int INT_BILLING = 10;
//// Android configuration      
    public static final String nullSt = "null";    // fix mapper object translation
}
