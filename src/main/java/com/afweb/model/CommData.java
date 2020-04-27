/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.afweb.model;

/**
 *
 * @author eddyko
 */
public class CommData {

    private String msg;
    private int type;
    private java.sql.Date entrydatedisplay;
    private long entrydatel;
    private float split;
    private float newclose;
    private float oldclose;
    private String symbol;

    /**
     * @return the symbol
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * @param symbol the symbol to set
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @param msg the msg to set
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * @return the newclose
     */
    public float getNewclose() {
        return newclose;
    }

    /**
     * @param newclose the newclose to set
     */
    public void setNewclose(float newclose) {
        this.newclose = newclose;
    }

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

    /**
     * @return the entrydatedisplay
     */
    public java.sql.Date getEntrydatedisplay() {
        return entrydatedisplay;
    }

    /**
     * @param entrydatedisplay the entrydatedisplay to set
     */
    public void setEntrydatedisplay(java.sql.Date entrydatedisplay) {
        this.entrydatedisplay = entrydatedisplay;
    }

    /**
     * @return the entrydatel
     */
    public long getEntrydatel() {
        return entrydatel;
    }

    /**
     * @param entrydatel the entrydatel to set
     */
    public void setEntrydatel(long entrydatel) {
        this.entrydatel = entrydatel;
    }

    /**
     * @return the split
     */
    public float getSplit() {
        return split;
    }

    /**
     * @param split the split to set
     */
    public void setSplit(float split) {
        this.split = split;
    }

    /**
     * @return the oldclose
     */
    public float getOldclose() {
        return oldclose;
    }

    /**
     * @param oldclose the oldclose to set
     */
    public void setOldclose(float oldclose) {
        this.oldclose = oldclose;
    }

}
