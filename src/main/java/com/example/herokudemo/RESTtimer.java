/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.herokudemo;

import com.afweb.service.ServiceAFweb;
import com.afweb.util.*;

import java.util.logging.Logger;

/**
 *
 * @author eddy
 */
public class RESTtimer {

    protected static Logger logger = Logger.getLogger("service");

    private static String timerMsg = null;

    private static long lastTimer = 0;
    private static long timerServ = 0;

    public static String serverURL_0 = "";

    public static void SetRestTimerHandler0(String urlStr) {
        serverURL_0 = urlStr;
    }

    public void RestTimerHandler() {
        RestTimerHandler0(ServiceAFweb.SERVERDB_URL);
    }

    private static int timerCnt3 = 0;
    private static int timerExceptionCnt3 = 0;
    private static long lastTimer3 = 0;
    private static long timerServ3 = 0;

    public void RestTimerHandler0(String urlStr) {
        if (serverURL_0.equals("stop")) {
            return;
        }
        if (timerServ3 == 0) {
            timerServ3 = System.currentTimeMillis();
        }
        timerCnt3++;
        if (timerCnt3 < 0) {
            timerCnt3 = 0;
        }

        if (timerExceptionCnt3 > 2) {
            long currentTime = System.currentTimeMillis();
            long lockDate1Min = TimeConvertion.addMinutes(lastTimer3, 1); // add 1 minutes
            if (lockDate1Min < currentTime) {
                timerExceptionCnt3 = 0;
            }
            return;
        }
        lastTimer3 = System.currentTimeMillis();
        timerMsg = "timerThreadServ=" + timerServ3 + "-timerCnt=" + timerCnt3 + "-ExceptionCnt=" + timerExceptionCnt3;

        try {

            // Create Client
            String url = "";

            if (serverURL_0.length() == 0) {
                url = urlStr + "/timerhandler?resttimerMsg=" + timerMsg;
                if (getEnv.checkLocalPC() == true) {
                    url = AFwebService.localTimerURL + AFwebService.webPrefix + "/timerhandler?resttimerMsg=" + timerMsg;
                }
            } else {
                url = serverURL_0 + "/timerhandler?resttimerMsg=" + timerMsg;
            }
            RESTtimerREST restAPI = new RESTtimerREST();
            String ret = restAPI.sendRequest(RESTtimerREST.METHOD_GET, url, null, null, false);
            timerExceptionCnt3--;
            if (timerExceptionCnt3 < 0) {
                timerExceptionCnt3 = 0;
            }
        } catch (Exception ex) {
            if (CKey.NN_DEBUG == true) {
//                logger.info("RestTimerHandler0 Failed with HTTP Error ");
            }
        }
        timerExceptionCnt3++;
    }

}
