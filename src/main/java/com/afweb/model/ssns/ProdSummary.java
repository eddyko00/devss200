/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.afweb.model.ssns;

import java.util.ArrayList;

/**
 *
 * @author koed
 */
public class ProdSummary {

    /**
     * @return the ret
     */
    public String getRet() {
        return ret;
    }

    /**
     * @param ret the ret to set
     */
    public void setRet(String ret) {
        this.ret = ret;
    }

    /**
     * @return the down
     */
    public String getDown() {
        return down;
    }

    /**
     * @param down the down to set
     */
    public void setDown(String down) {
        this.down = down;
    }
    private int id;
    private String cusid;
    private String banid;
    private String tiid;
    private String oper;
    private String down;
    private String ret;  

    /**
     * @return the banid
     */
    public String getBanid() {
        return banid;
    }

    /**
     * @param banid the banid to set
     */
    public void setBanid(String banid) {
        this.banid = banid;
    }

    /**
     * @return the tiid
     */
    public String getTiid() {
        return tiid;
    }

    /**
     * @param tiid the tiid to set
     */
    public void setTiid(String tiid) {
        this.tiid = tiid;
    }

    /**
     * @return the oper
     */
    public String getOper() {
        return oper;
    }

    /**
     * @param oper the oper to set
     */
    public void setOper(String oper) {
        this.oper = oper;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the cusid
     */
    public String getCusid() {
        return cusid;
    }

    /**
     * @param cusid the cusid to set
     */
    public void setCusid(String cusid) {
        this.cusid = cusid;
    }
}
