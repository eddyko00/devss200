/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.afweb.model;

import com.afweb.util.CKey;

/**
 *
 * @author eddy
 */
public class ServerObj {

    /**
     * @return the autoNNCnt
     */
    public int getAutoNNCnt() {
        return autoNNCnt;
    }

    /**
     * @param autoNNCnt the autoNNCnt to set
     */
    public void setAutoNNCnt(int autoNNCnt) {
        this.autoNNCnt = autoNNCnt;
    }

    private String serverName;
    private String srvProjName;
    private String verString;
    private String lastServUpdateESTdate;
    private long lastServUpdateTimer;
    private String timerMsg;
    private String timerThreadMsg;

    private boolean localDBservice;
    private boolean sysMaintenance;

    private int timerCnt;
    private int timerQueueCnt;
    private boolean timerInit;

    private int cntRESTrequest;
    private int cntRESTexception;

    private int cntInterRequest;
    private int cntInterException;

    private int cntControRequest;
    private int cntControlResp;

    private int processTimerCnt = 0;
    private boolean uidisplayonly;
    private int autoNNCnt = 0;
    
    
    public ServerObj() {
        serverName = "Server";
        srvProjName = "project";
        lastServUpdateESTdate = "";
        timerCnt = 0;
        timerQueueCnt = 0;
        timerMsg = "";
        timerInit = false;
        lastServUpdateTimer = 0;
        timerThreadMsg = "";
        localDBservice = true;
        sysMaintenance = false;
        verString = ConstantKey.VERSION;


        cntRESTrequest = 0;
        cntRESTexception = 0;
        cntInterRequest = 0;
        cntInterException = 0;
        cntControRequest = 0;
        cntControlResp = 0;

        processTimerCnt = 0;
        uidisplayonly = CKey.UI_ONLY;
        autoNNCnt=0;
    }

    /**
     * @return the serverName
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * @param serverName the serverName to set
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    /**
     * @return the srvProjName
     */
    public String getSrvProjName() {
        return srvProjName;
    }

    /**
     * @param srvProjName the srvProjName to set
     */
    public void setSrvProjName(String srvProjName) {
        this.srvProjName = srvProjName;
    }

    /**
     * @return the verString
     */
    public String getVerString() {
        return verString;
    }

    /**
     * @param verString the verString to set
     */
    public void setVerString(String verString) {
        this.verString = verString;
    }

    /**
     * @return the lastServUpdateESTdate
     */
    public String getLastServUpdateESTdate() {
        return lastServUpdateESTdate;
    }

    /**
     * @param lastServUpdateESTdate the lastServUpdateESTdate to set
     */
    public void setLastServUpdateESTdate(String lastServUpdateESTdate) {
        this.lastServUpdateESTdate = lastServUpdateESTdate;
    }

    /**
     * @return the lastServUpdateTimer
     */
    public long getLastServUpdateTimer() {
        return lastServUpdateTimer;
    }

    /**
     * @param lastServUpdateTimer the lastServUpdateTimer to set
     */
    public void setLastServUpdateTimer(long lastServUpdateTimer) {
        this.lastServUpdateTimer = lastServUpdateTimer;
    }

    /**
     * @return the timerMsg
     */
    public String getTimerMsg() {
        return timerMsg;
    }

    /**
     * @param timerMsg the timerMsg to set
     */
    public void setTimerMsg(String timerMsg) {
        this.timerMsg = timerMsg;
    }

    /**
     * @return the timerThreadMsg
     */
    public String getTimerThreadMsg() {
        return timerThreadMsg;
    }

    /**
     * @param timerThreadMsg the timerThreadMsg to set
     */
    public void setTimerThreadMsg(String timerThreadMsg) {
        this.timerThreadMsg = timerThreadMsg;
    }

    /**
     * @return the localDBservice
     */
    public boolean isLocalDBservice() {
        return localDBservice;
    }

    /**
     * @param localDBservice the localDBservice to set
     */
    public void setLocalDBservice(boolean localDBservice) {
        this.localDBservice = localDBservice;
    }

    /**
     * @return the sysMaintenance
     */
    public boolean isSysMaintenance() {
        return sysMaintenance;
    }

    /**
     * @param sysMaintenance the sysMaintenance to set
     */
    public void setSysMaintenance(boolean sysMaintenance) {
        this.sysMaintenance = sysMaintenance;
    }

    /**
     * @return the timerCnt
     */
    public int getTimerCnt() {
        return timerCnt;
    }

    /**
     * @param timerCnt the timerCnt to set
     */
    public void setTimerCnt(int timerCnt) {
        this.timerCnt = timerCnt;
    }

    /**
     * @return the timerQueueCnt
     */
    public int getTimerQueueCnt() {
        return timerQueueCnt;
    }

    /**
     * @param timerQueueCnt the timerQueueCnt to set
     */
    public void setTimerQueueCnt(int timerQueueCnt) {
        this.timerQueueCnt = timerQueueCnt;
    }

    /**
     * @return the timerInit
     */
    public boolean isTimerInit() {
        return timerInit;
    }

    /**
     * @param timerInit the timerInit to set
     */
    public void setTimerInit(boolean timerInit) {
        this.timerInit = timerInit;
    }

    /**
     * @return the cntRESTrequest
     */
    public int getCntRESTrequest() {
        return cntRESTrequest;
    }

    /**
     * @param cntRESTrequest the cntRESTrequest to set
     */
    public void setCntRESTrequest(int cntRESTrequest) {
        this.cntRESTrequest = cntRESTrequest;
    }

    /**
     * @return the cntRESTexception
     */
    public int getCntRESTexception() {
        return cntRESTexception;
    }

    /**
     * @param cntRESTexception the cntRESTexception to set
     */
    public void setCntRESTexception(int cntRESTexception) {
        this.cntRESTexception = cntRESTexception;
    }

    /**
     * @return the cntInterRequest
     */
    public int getCntInterRequest() {
        return cntInterRequest;
    }

    /**
     * @param cntInterRequest the cntInterRequest to set
     */
    public void setCntInterRequest(int cntInterRequest) {
        this.cntInterRequest = cntInterRequest;
    }

    /**
     * @return the cntInterException
     */
    public int getCntInterException() {
        return cntInterException;
    }

    /**
     * @param cntInterException the cntInterException to set
     */
    public void setCntInterException(int cntInterException) {
        this.cntInterException = cntInterException;
    }

    /**
     * @return the cntControRequest
     */
    public int getCntControRequest() {
        return cntControRequest;
    }

    /**
     * @param cntControRequest the cntControRequest to set
     */
    public void setCntControRequest(int cntControRequest) {
        this.cntControRequest = cntControRequest;
    }

    /**
     * @return the cntControlResp
     */
    public int getCntControlResp() {
        return cntControlResp;
    }

    /**
     * @param cntControlResp the cntControlResp to set
     */
    public void setCntControlResp(int cntControlResp) {
        this.cntControlResp = cntControlResp;
    }

    /**
     * @return the processTimerCnt
     */
    public int getProcessTimerCnt() {
        return processTimerCnt;
    }

    /**
     * @param processTimerCnt the processTimerCnt to set
     */
    public void setProcessTimerCnt(int processTimerCnt) {
        this.processTimerCnt = processTimerCnt;
    }

    /**
     * @return the uidisplayonly
     */
    public boolean isUidisplayonly() {
        uidisplayonly = CKey.UI_ONLY;
        return uidisplayonly;
    }

    /**
     * @param uidisplayonly the uidisplayonly to set
     */
    public void setUidisplayonly(boolean uidisplayonly) {
        this.uidisplayonly = uidisplayonly;
    }

}
