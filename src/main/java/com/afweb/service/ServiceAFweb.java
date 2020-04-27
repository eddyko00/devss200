/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.afweb.service;

import com.afweb.process.*;
import com.afweb.model.*;
import com.afweb.model.ssns.*;
import static com.afweb.process.SsnsRegression.*;
import static com.afweb.process.SsnsService.*;

import com.afweb.util.*;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import java.util.logging.Logger;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 *
 * @author eddy
 */
@Service
public class ServiceAFweb {

    public static Logger logger = Logger.getLogger("AFwebService");

    private static ServerObj serverObj = new ServerObj();
    private JdbcTemplate jdbcTemplate;
    private DataSource dataSource;

    public static String serverLockName = "server";
    private static boolean initProcessTimer = false;
    private static int delayProcessTimer = 0;

    private SsnsDataImp ssnsDataImp = new SsnsDataImp();
    private AccountImp accountImp = new AccountImp();

    public static String URL_PRODUCT = "";
    public static String URL_PATH_OP_DB_PHP1 = "";
    public static String URL_PATH_OP = "";
    public static String SERVERDB_REMOTE_URL = "";
    public static String SERVERDB_URL = "";
    public static String PROXYURL = "";

    public static String URL_LOCALDB = "";
    public static String FileLocalPath = "";

    /**
     * @return the serverObj
     */
    public static ServerObj getServerObj() {
        if (serverObj.getCntRESTrequest() < 0) {
            serverObj.setCntRESTrequest(0);
        }
        if (serverObj.getCntRESTexception() < 0) {
            serverObj.setCntRESTexception(0);
        }
        if (serverObj.getCntInterRequest() < 0) {
            serverObj.setCntInterRequest(0);
        }
        if (serverObj.getCntInterException() < 0) {
            serverObj.setCntInterException(0);
        }
        if (serverObj.getCntControRequest() < 0) {
            serverObj.setCntControRequest(0);
        }
        if (serverObj.getCntControlResp() < 0) {
            serverObj.setCntControlResp(0);
        }
        return serverObj;
    }

    /**
     * @param aServerObj the serverObj to set
     */
    public static void setServerObj(ServerObj aServerObj) {
        serverObj = aServerObj;
    }

    public ArrayList getServerList() {
        ServerObj serverObj = ServiceAFweb.getServerObj();
        ArrayList serverObjList = new ArrayList();
        serverObjList.add(serverObj);
        return serverObjList;
    }

    public void initDataSource() {
        logger.info(">initDataSource ");
        //testing
        WebAppConfig webConfig = new WebAppConfig();
        dataSource = webConfig.dataSource();
        //testing        
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.dataSource = dataSource;

        String enSt = CKey.URL_PRODUCT_TMP;
        enSt = replaceAll("abc", "", enSt);
        URL_PRODUCT = enSt;

        enSt = CKey.URL_PATH_OP_DB_PHP1_TMP;
        enSt = replaceAll("abc", "", enSt);
        URL_PATH_OP_DB_PHP1 = enSt;

        enSt = CKey.URL_PATH_OP_TMP;
        enSt = replaceAll("abc", "", enSt);
        URL_PATH_OP = enSt;

        enSt = CKey.PROXYURL_TMP;
        enSt = replaceAll("abc", "", enSt);
        PROXYURL = enSt;

        SERVERDB_REMOTE_URL = URL_PATH_OP_DB_PHP1;  //LocalPCflag = false;
        SERVERDB_URL = URL_PATH_OP;

        String URL_PATH = ServiceAFweb.URL_PATH_OP_DB_PHP1 + CKey.WEBPOST_OP_PHP;
        ServiceRemoteDB.setURL_PATH(URL_PATH);

        if (FileLocalPath.length() == 0) {
            FileLocalPath = CKey.FileLocalPathTemp;
        }

    }

    //Repeat every 10 seconds
    public int timerHandler(String timerThreadMsg) {
        // too much log
//        logger.info("> timerHandler " + timerThreadMsg);

        serverObj.setTimerCnt(serverObj.getTimerCnt() + 1);
        Calendar dateNow = TimeConvertion.getCurrentCalendar();
        long lockDateValue = dateNow.getTimeInMillis();
        if (initProcessTimer == false) {
            delayProcessTimer++;
            if (delayProcessTimer > 2) {
                initProcessTimer = true;
            }
            return getServerObj().getTimerCnt();
        }

        if (getServerObj().getTimerCnt() < 0) {
            serverObj.setTimerCnt(0);
        }

        //only allow 1 thread 
        if (getServerObj().getTimerQueueCnt() > 0) {

            long currentTime = System.currentTimeMillis();
            int waitMinute = 8;
            if (getServerObj().isSysMaintenance() == true) {
                waitMinute = 3;
            }
            long lockDate5Min = TimeConvertion.addMinutes(getServerObj().getLastServUpdateTimer(), waitMinute); // add 8 minutes
            if (lockDate5Min < currentTime) {
                serverObj.setTimerQueueCnt(0);
            }
            return getServerObj().getTimerCnt();
        }

        serverObj.setLastServUpdateTimer(lockDateValue);
        serverObj.setTimerQueueCnt(serverObj.getTimerQueueCnt() + 1);
        try {
            String tzid = "America/New_York"; //EDT
            TimeZone tz = TimeZone.getTimeZone(tzid);
            Date d = new Date();
            // timezone symbol (z) included in the format pattern 
            DateFormat format = new SimpleDateFormat("M/dd/yyyy hh:mm a z");
            // format date in target timezone
            format.setTimeZone(tz);
            serverObj.setLastServUpdateESTdate(format.format(d));

            serverObj.setTimerMsg("timerHandlerServ=" + getServerObj().getServerName() + "-" + "timerCnt=" + getServerObj().getTimerCnt() + "-timerQueueCnt=" + getServerObj().getTimerQueueCnt());
//            logger.info(getServerObj().getTimerMsg());
            if (timerThreadMsg != null) {
                serverObj.setTimerThreadMsg(timerThreadMsg);
            }

            if (getServerObj().isSysMaintenance() == true) {
                return getServerObj().getTimerCnt();
            }

            if (getServerObj().isTimerInit() == false) {
                /////////////
                initDataSource();

                getSsnsDataImp().setDataSource(jdbcTemplate, dataSource);
                getAccountImp().setDataSource(jdbcTemplate, dataSource);
                // work around. must initialize for remote MYSQL
                serverObj.setTimerInit(true);
                getServerObj().setProcessTimerCnt(0);
                ////////////
                String SrvName = "ssns";
                String stlockDateValue = "" + lockDateValue;
                stlockDateValue = stlockDateValue.substring(10);

                serverObj.setServerName(SrvName + lockDateValue);
                serverObj.setVerString(ConstantKey.VERSION); // + " " + getServerObj().getLastServUpdateESTdate());
                serverObj.setSrvProjName(SrvName + stlockDateValue);

                serverLockName = ServiceAFweb.getServerObj().getServerName();

                getServerObj().setLocalDBservice(CKey.LocalPCflag);

                if (CKey.SQL_DATABASE == CKey.REMOTE_MYSQL) {
                    logger.info(">>>>> System Openshift DB1 URL:" + URL_PATH_OP_DB_PHP1);
                }
                if (CKey.SQL_DATABASE == CKey.MYSQL) {
                    String dsURL = CKey.dataSourceURL;
                    logger.info(">>>>> System Local DB URL:" + dsURL);
                }
                boolean backupFlag = false;
                if (backupFlag == true) {
                    backupSystem();
                    serverObj.setTimerQueueCnt(serverObj.getTimerQueueCnt() - 1);
                    return getServerObj().getTimerCnt();

                }
                boolean restoreFlag = false; // only work on PHP
                if (restoreFlag == true) {
                    restoreSystem();
                    serverObj.setTimerQueueCnt(serverObj.getTimerQueueCnt() - 1);
                    return getServerObj().getTimerCnt();

                }
                if (CKey.UI_ONLY == false) {

                    // make sure not request during DB initialize
                    if (getServerObj().isLocalDBservice() == true) {
                        getServerObj().setSysMaintenance(true);
                        logger.info(">>>>>>> InitDBData started.........");
                        // 0 - new db, 1 - db already exist, -1 db error
                        int ret = InitDBData();  // init DB Adding customer account

                        if (ret != -1) {

                            initProcessTimer = false;
                            delayProcessTimer = 0;

                            getServerObj().setSysMaintenance(false);
                            serverObj.setTimerInit(true);
                            logger.info(">>>>>>> InitDBData Competed.....");
                        } else {
                            serverObj.setTimerInit(false);
                            serverObj.setTimerQueueCnt(serverObj.getTimerQueueCnt() - 1);
                            logger.info(">>>>>>> InitDBData Failed.....");
                            return getServerObj().getTimerCnt();
                        }

                    }
                    serverObj.setTimerInit(true);
                    setLockNameProcess(serverLockName, ConstantKey.SRV_LOCKTYPE, lockDateValue, serverObj.getSrvProjName());

//                    boolean clearssnsflag = false;
//                    if (clearssnsflag == true) {
//                        getSsnsDataImp().deleteSsnsAccApp(SsnsService.APP_PRODUCT);
//                        getSsnsDataImp().updateSsnsDataAllOpenStatus();
//                    }
                }
                // final initialization
//                getSsnsDataImp().updateSsnsDataAllOpenStatus();
            } else {

                processTimer();
            }

        } catch (Exception ex) {
            logger.info("> timerHandler Exception" + ex.getMessage());
        }
        serverObj.setTimerQueueCnt(serverObj.getTimerQueueCnt() - 1);
        return getServerObj().getTimerCnt();
    }

    private void backupSystem() {
        if (CKey.LocalPCflag == true) {
            getServerObj().setSysMaintenance(true);
            serverObj.setTimerInit(true);
            if (CKey.NN_DEBUG == true) {
                // LocalPCflag = true; 
                // SQL_DATABASE = REMOTE_MYSQL;
                if (CKey.SQL_DATABASE == CKey.REMOTE_MYSQL) {
                    logger.info(">>>>> SystemDownloadDBData form Openshift");

                } else if (CKey.SQL_DATABASE == CKey.LOCAL_MYSQL) {
                    logger.info(">>>>> SystemDownloadDBData form local My SQL");
                }

                serverObj.setSysMaintenance(true);
                boolean retSatus = getAccountImp().downloadDBData(this);
                if (retSatus == true) {
                    serverObj.setSysMaintenance(true);
                    serverObj.setTimerInit(false);
                    serverObj.setTimerQueueCnt(0);
                    serverObj.setTimerCnt(0);
                }
                getServerObj().setSysMaintenance(true);
                logger.info(">>>>> SystemDownloadDBData done");
            }
        }
    }

    private void restoreSystem() {
        getServerObj().setSysMaintenance(true);
        serverObj.setTimerInit(true);
        if (CKey.NN_DEBUG == true) {
            if (CKey.LocalPCflag == true) {
                if (CKey.SQL_DATABASE == CKey.REMOTE_MYSQL) {
                    logger.info(">>>>> SystemRestoreDBData to Openshift");
                } else if (CKey.SQL_DATABASE == CKey.LOCAL_MYSQL) {
                    logger.info(">>>>> SystemRestoreDBData form to My SQL");
                }
                String retSt = SystemCleanDBData();
                if (retSt.equals("true")) {
                    serverObj.setSysMaintenance(true);
                    boolean retSatus = getAccountImp().restoreDBData(this);
                    if (retSatus == true) {
                        serverObj.setSysMaintenance(true);
                        serverObj.setTimerInit(false);
                        serverObj.setTimerQueueCnt(0);
                        serverObj.setTimerCnt(0);
                    }
                    getServerObj().setSysMaintenance(true);
                    logger.info(">>>>> SystemRestoreDBData done");
                }

            }
        }
    }
    //////////

    public boolean debugFlag = false;

    private void processTimer() {

        if (getEnv.checkLocalPC() == true) {
            if (CKey.NN_DEBUG == true) {
                if (debugFlag == false) {
                    debugFlag = true;

//                        
// Window -> Debugging -> Breakpoints Select all, the delete
//
///////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////
                    boolean clearssnsflag = false;
                    if (clearssnsflag == true) {
//                        getSsnsDataImp().updateSsnsDataAllOpenStatus();

//                        getSsnsDataImp().deleteSsnsAccApp(SsnsService.APP_PRODUCT);
//                        getSsnsDataImp().deleteSsnsAccApp(SsnsService.APP_APP);
//                        getSsnsDataImp().deleteSsnsDataApp(SsnsService.APP_APP);
//                        getSsnsDataImp().updateSsnsDataOpenStatus(SsnsService.APP_WIFI);
//                        getSsnsDataImp().deleteSsnsDataApp(SsnsService.APP_WIFI);
//                        getSsnsDataImp().deleteSsnsAccApp(SsnsService.APP_WIFI);
                    }

                    boolean ETLsplunkFlat = false;
                    if (ETLsplunkFlat == true) {

                        boolean clearsplunkflag = false;
                        if (clearsplunkflag == true) {
                            this.getSsnsDataImp().deleteAllSsnsData(0);
                        }

                        for (int i = 0; i < 10; i++) {
                            processETL();
                        }
                    }
/////////
/////////                    
                    boolean procallflag = false;
                    if (procallflag == true) {
                        for (int i = 0; i < 10; i++) {
                            processFeatureApp();
                            processFeatureProd();
                            processFeatureWifi();
                            processFeatureTTVC();
                        }
                    }

                    boolean restoreSsnsAccFlag = false; // work for remote d
                    if (restoreSsnsAccFlag == true) {
                        this.getSsnsDataImp().deleteAllSsnsAcc(0);
                        boolean retSatus = getAccountImp().restoreSsnsAccDB(this);
                    }

                    boolean restoreSsnsDataFlag = false; // work for remote d
                    if (restoreSsnsDataFlag == true) {
                        this.getSsnsDataImp().deleteAllSsnsData(0);
                        boolean retSatus = getAccountImp().restoreSsnsDataDB(this);
                    }
///////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////                    
                    boolean monflag = false;
                    if (monflag == true) {

//                        this.getSsnsDataImp().deleteAllSsReport(0);
                        SsnsRegression regression = new SsnsRegression();
                        String name = CKey.ADMIN_USERNAME;
                        regression.startMonitor(this, name);

                        for (int i = 0; i < 10; i++) {
                            regression.processMonitorTesting(this);
                            if (i == 1) {
                                regression.stopMonitor(this, name);
                            }
                        }
                        regression.reportMoniter(this, name);

                    }
/////////
/////////
                    boolean procflag = false;
                    if (procflag == true) {

                        SsnsService ss = new SsnsService();
                        String feature = "";
                        boolean prodflag = true;
                        if (prodflag == true) {
                            ArrayList ttvNameArrayTemp = getAllOpenProductArray();
                            if (ttvNameArrayTemp != null) {
                                for (int i = 0; i < ttvNameArrayTemp.size(); i++) {
                                    String idSt = (String) ttvNameArrayTemp.get(i);

                                    int id = Integer.parseInt(idSt);
                                    SsnsData data = getSsnsDataImp().getSsnsDataObjByID(id);
                                    feature = ss.getFeatureSsnsProdiuctInventory(data);

//                                    ArrayList<SsnsData> dataList = getSsnsDataImp().getSsnsDataObjByUUIDList("cf0adc01-588e-4717-b87b-876441d79a1e");
//                                    feature = ss.getFeatureSsnsProdiuctInventory(dataList.get(0));
                                }
                            }
                        }
                    }
/////////
/////////
                    boolean wififlag = false;
                    if (wififlag == true) {

//                        getSsnsDataImp().updateSsnsDataOpenStatus(SsnsService.APP_WIFI);
                        SsnsService ss = new SsnsService();
                        String feature = "";
                        ArrayList appNameArrayTemp = getAllOpenWifiArray();
                        if (appNameArrayTemp != null) {
                            for (int i = 0; i < appNameArrayTemp.size(); i++) {
                                String idSt = (String) appNameArrayTemp.get(i);

                                int id = Integer.parseInt(idSt);
                                SsnsData data = getSsnsDataImp().getSsnsDataObjByID(id);
                                feature = ss.getFeatureSsnsWifi(data);

//                                    ArrayList<SsnsData> dataList = getSsnsDataImp().getSsnsDataObjByUUIDList("0f8825e8-d628-405e-83d6-3ff63dd82654");
//                                    ss.getFeatureSsnsWifi(dataList.get(0));
                            }
                        }
                    }
/////////
/////////
                    boolean appflag = false;
                    if (appflag == true) {

//                        getSsnsDataImp().updateSsnsDataAllOpenStatus();
//                        getSsnsDataImp().deleteSsnsAccApp(SsnsService.APP_APP);
                        SsnsService ss = new SsnsService();
                        String feature = "";
                        ArrayList appNameArrayTemp = getAllOpenAppArray();
                        if (appNameArrayTemp != null) {
                            for (int i = 0; i < appNameArrayTemp.size(); i++) {
                                String idSt = (String) appNameArrayTemp.get(i);

                                int id = Integer.parseInt(idSt);
                                SsnsData data = getSsnsDataImp().getSsnsDataObjByID(id);
                                feature = ss.getFeatureSsnsAppointment(data);
                            }
                        }
                    }

                    boolean ttvReqflag = false;
                    if (ttvReqflag == true) {
//                        getSsnsDataImp().updateSsnsDataOpenStatus(SsnsService.APP_TTVSUB);
//                        getSsnsDataImp().updateSsnsDataOpenStatus(SsnsService.APP_TTVREQ);                        
//                        getSsnsDataImp().deleteSsnsAccApp(SsnsService.APP_TTVC);

                        for (int m = 0; m < 20; m++) {
                            SsnsService ss = new SsnsService();
                            String feature = "";
                            ArrayList appNameArrayTemp = getAllOpenTTVCArray();
                            if (appNameArrayTemp != null) {
                                for (int i = 0; i < appNameArrayTemp.size(); i++) {
                                    String idSt = (String) appNameArrayTemp.get(i);

                                    int id = Integer.parseInt(idSt);
                                    SsnsData data = getSsnsDataImp().getSsnsDataObjByID(id);
                                    feature = ss.getFeatureSsnsTTVC(data);

//                                    ArrayList<SsnsData> dataList = getSsnsDataImp().getSsnsDataObjByUUIDList("28d552b9-df3d-4bd8-8a22-3ff63dd8b337");
//                                    feature = ss.getFeatureSsnsTTVC(dataList.get(1));
                                }
                            }
                        }
                    }

///////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////  
                    logger.info("> Debug end ");
                }

            }
        }
        if (CKey.UI_ONLY == true) {
            if ((getServerObj().getProcessTimerCnt() % 5) == 0) {
                //// process monitor
                SsnsRegression regression = new SsnsRegression();
                regression.processMonitorTesting(this);
            }
            return;
        }

        String LockName = null;
        try {
            Calendar dateNow = TimeConvertion.getCurrentCalendar();
            long lockDateValue = dateNow.getTimeInMillis();

            LockName = "LOCK_" + ServiceAFweb.getServerObj().getServerName();
//            long lockReturn = setLockNameProcess(LockName, ConstantKey.SRV_LOCKTYPE, lockDateValue, "ProcessTimerCnt " + getServerObj().getProcessTimerCnt());
//
//            if (CKey.NN_DEBUG == true) {
//                lockReturn = 1;
//            }
//            if (lockReturn == 0) {
//                return;
//            }
            if (getServerObj().getProcessTimerCnt() < 0) {
                getServerObj().setProcessTimerCnt(0);
            }
            getServerObj().setProcessTimerCnt(getServerObj().getProcessTimerCnt() + 1);

//            logger.info("> processTimer " + getServerObj().getProcessTimerCnt());
            if (getEnv.checkLocalPC() == true) {
                if (CKey.NN_DEBUG == true) {
                    if ((getServerObj().getProcessTimerCnt() % 3) == 0) {
                        //10 Sec * 5 ~ 1 minutes
//                        processETL();
//                        return;
                    }
                }
            }
            if (((getServerObj().getProcessTimerCnt() % 10) == 0) || (getServerObj().getProcessTimerCnt() == 1)) {
                long result = setRenewLock(serverLockName, ConstantKey.SRV_LOCKTYPE);
                if (result == 0) {
                    Calendar dateNow1 = TimeConvertion.getCurrentCalendar();
                    long lockDateValue1 = dateNow1.getTimeInMillis();
                    setLockNameProcess(serverLockName, ConstantKey.SRV_LOCKTYPE, lockDateValue1, serverObj.getSrvProjName());
                }
            }
            if ((getServerObj().getProcessTimerCnt() % 500) == 0) {
                //30 sec per tick ~ 24h 60 s*60 *24 / 30
            }

            if ((getServerObj().getProcessTimerCnt() % 280) == 0) {
                // 30 sec per tick ~ 5 hour   60 s*60 * 4/ 30 
            }
            if ((getServerObj().getProcessTimerCnt() % 200) == 0) {
                // 30 sec per tick ~  for 3 hour   60 s*60 *3 /30 
            }
            if ((getServerObj().getProcessTimerCnt() % 13) == 0) {
                ;
                processFeatureProd();
            } else if ((getServerObj().getProcessTimerCnt() % 11) == 0) {
                ;
                processFeatureApp();
            } else if ((getServerObj().getProcessTimerCnt() % 7) == 0) {
                //////require to save memory
                System.gc();
                //////require to save memory
                processFeatureWifi();
            } else if ((getServerObj().getProcessTimerCnt() % 5) == 0) {
                ;
                processFeatureTTVC();

                //// process monitor
                SsnsRegression regression = new SsnsRegression();
                regression.processMonitorTesting(this);

            } else if ((getServerObj().getProcessTimerCnt() % 3) == 0) {
                //10 Sec * 5 ~ 1 minutes
                processETL();

            } else if ((getServerObj().getProcessTimerCnt() % 2) == 0) {

            } else {

            }

        } catch (Exception ex) {
            logger.info("> processTimer Exception" + ex.getMessage());
        }
        removeNameLock(LockName, ConstantKey.SRV_LOCKTYPE);
    }

    /**
     * A simple implementation to pretty-print JSON file.
     *
     * @param unformattedJsonString
     * @return
     */
    public static ArrayList<String> prettyPrintJSON(String unformattedJsonString) {
        StringBuilder prettyJSONBuilder = new StringBuilder();
        ArrayList<String> output = new ArrayList();
        int indentLevel = 0;
        boolean inQuote = false;
        for (char charFromUnformattedJson : unformattedJsonString.toCharArray()) {
            switch (charFromUnformattedJson) {
                case '"':
                    // switch the quoting status
                    inQuote = !inQuote;
                    prettyJSONBuilder.append(charFromUnformattedJson);
//                    output.add("" + charFromUnformattedJson);
                    break;
                case ' ':
                    // For space: ignore the space if it is not being quoted.
                    if (inQuote) {
                        prettyJSONBuilder.append(charFromUnformattedJson);
//                        output.add("" + charFromUnformattedJson);
                    }
                    break;
                case '{':
                case '[':
                    // Starting a new block: increase the indent level
                    prettyJSONBuilder.append(charFromUnformattedJson);
//                    output.add("" + charFromUnformattedJson);
                    indentLevel++;
                    appendIndentedNewLine(indentLevel, prettyJSONBuilder, output);
                    prettyJSONBuilder = new StringBuilder();
                    break;
                case '}':
                case ']':
                    // Ending a new block; decrese the indent level
                    indentLevel--;
                    appendIndentedNewLine(indentLevel, prettyJSONBuilder, output);
                    prettyJSONBuilder = new StringBuilder();
//                    output.add("" + charFromUnformattedJson);
                    prettyJSONBuilder.append(charFromUnformattedJson);
                    break;
                case ',':
                    // Ending a json item; create a new line after
                    prettyJSONBuilder.append(charFromUnformattedJson);
//                    output.add("" + charFromUnformattedJson);
                    if (!inQuote) {
                        appendIndentedNewLine(indentLevel, prettyJSONBuilder, output);
                        prettyJSONBuilder = new StringBuilder();
                    }
                    break;
                default:
                    prettyJSONBuilder.append(charFromUnformattedJson);
//                    output.add("" + charFromUnformattedJson);
            }
        }
        return output;
    }

    /**
     * Print a new line with indention at the beginning of the new line.
     *
     * @param indentLevel
     * @param stringBuilder
     */
    private static void appendIndentedNewLine(int indentLevel, StringBuilder prettyJSONBuilder, ArrayList<String> output) {
//        output.add("\n");
        output.add(prettyJSONBuilder.toString());

        for (int i = 0; i < indentLevel; i++) {
            // Assuming indention using 2 spaces
            prettyJSONBuilder.append("  ");
//            output.add("  ");
        }
    }
////////////////////////////////

    ArrayList<String> getAllOpenWifiArray() {
        String app = SsnsService.APP_WIFI;
        String ret = "parameter";
        int status = ConstantKey.OPEN;
        return getSsnsDataImp().getSsnsDataIDList(app, ret, status, 15);
    }

    ArrayList<String> wifiNameArray = new ArrayList();

    private ArrayList updateWifiNameArray() {
        if (wifiNameArray != null && wifiNameArray.size() > 0) {
            return wifiNameArray;
        }
        ArrayList ttvNameArrayTemp = getAllOpenWifiArray();
        if (ttvNameArrayTemp != null) {
            wifiNameArray = ttvNameArrayTemp;
        }
        return wifiNameArray;
    }

    public int processFeatureWifi() {

        updateWifiNameArray();
        if ((wifiNameArray == null) || (wifiNameArray.size() == 0)) {
            return 0;
        }
        int result = 0;
        Calendar dateNow = TimeConvertion.getCurrentCalendar();
        long lockDateValue = dateNow.getTimeInMillis();
        String LockName = "ETL_" + SsnsService.APP_WIFI;

        try {
            int lockReturn = setLockNameProcess(LockName, ConstantKey.ETL_LOCKTYPE, lockDateValue, ServiceAFweb.getServerObj().getSrvProjName() + " processFeatureWifi");
            if (CKey.NN_DEBUG == true) {
                lockReturn = 1;
            }
            if (lockReturn == 0) {
                return 0;
            }

            logger.info("processFeatureApp for 2 minutes size " + wifiNameArray.size());

            long currentTime = System.currentTimeMillis();
            long lockDate1Min = TimeConvertion.addMinutes(currentTime, 2);

            for (int i = 0; i < 5; i++) {
                currentTime = System.currentTimeMillis();
//                if (CKey.NN_DEBUG != true) {
                if (lockDate1Min < currentTime) {
                    break;
                }
//                }
                if (wifiNameArray.size() == 0) {
                    break;
                }

                String idSt = (String) wifiNameArray.get(0);
                wifiNameArray.remove(0);
                SsnsService ss = new SsnsService();
                int id = Integer.parseInt(idSt);
                SsnsData data = getSsnsDataImp().getSsnsDataObjByID(id);
                String feature = ss.getFeatureSsnsWifi(data);
//                logger.info("> feature " + i + " " + feature);

                AFSleep();
            }
        } catch (Exception ex) {
        }
        removeNameLock(LockName, ConstantKey.ETL_LOCKTYPE);
        return result;

    }
/////////////////////////////////

    ArrayList<String> getAllOpenTTVCArray() {
        String app = SsnsService.APP_TTVREQ;
        String ret = "parameter";
        int status = ConstantKey.OPEN;
        ArrayList<String> retList = new ArrayList();
        ArrayList<String> reqList = getSsnsDataImp().getSsnsDataIDList(app, ret, status, 15);
        app = SsnsService.APP_TTVSUB;
        ArrayList<String> subList = getSsnsDataImp().getSsnsDataIDList(app, ret, status, 15);
        retList.addAll(reqList);
        retList.addAll(subList);
        return retList;
    }

    ArrayList<String> ttvcNameArray = new ArrayList();

    private ArrayList updateTTVCNameArray() {
        if (ttvcNameArray != null && ttvcNameArray.size() > 0) {
            return ttvcNameArray;
        }
        ArrayList ttvNameArrayTemp = getAllOpenTTVCArray();
        if (ttvNameArrayTemp != null) {
            ttvcNameArray = ttvNameArrayTemp;
        }
        return ttvcNameArray;
    }

    public int processFeatureTTVC() {

        updateTTVCNameArray();
        if ((ttvcNameArray == null) || (ttvcNameArray.size() == 0)) {
            return 0;
        }
        int result = 0;
        Calendar dateNow = TimeConvertion.getCurrentCalendar();
        long lockDateValue = dateNow.getTimeInMillis();
        String LockName = "ETL_TTVC";

        try {
            int lockReturn = setLockNameProcess(LockName, ConstantKey.ETL_LOCKTYPE, lockDateValue, ServiceAFweb.getServerObj().getSrvProjName() + " processFeatureTTVC");
            if (CKey.NN_DEBUG == true) {
                lockReturn = 1;
            }
            if (lockReturn == 0) {
                return 0;
            }

            logger.info("processFeatureApp for 2 minutes size " + ttvcNameArray.size());

            long currentTime = System.currentTimeMillis();
            long lockDate1Min = TimeConvertion.addMinutes(currentTime, 2);

            for (int i = 0; i < 5; i++) {
                currentTime = System.currentTimeMillis();
//                if (CKey.NN_DEBUG != true) {
                if (lockDate1Min < currentTime) {
                    break;
                }
//                }
                if (ttvcNameArray.size() == 0) {
                    break;
                }

                String idSt = (String) ttvcNameArray.get(0);
                ttvcNameArray.remove(0);
                SsnsService ss = new SsnsService();
                int id = Integer.parseInt(idSt);
                SsnsData data = getSsnsDataImp().getSsnsDataObjByID(id);
                String feature = ss.getFeatureSsnsTTVC(data);
//                logger.info("> feature " + i + " " + feature);

                AFSleep();
            }
        } catch (Exception ex) {
        }
        removeNameLock(LockName, ConstantKey.ETL_LOCKTYPE);
        return result;

    }

/////////////////////////////////
    ArrayList<String> getAllOpenAppArray() {
        String app = SsnsService.APP_APP;
        String ret = "parameter";
        int status = ConstantKey.OPEN;
        return getSsnsDataImp().getSsnsDataIDList(app, ret, status, 15);
    }

    ArrayList<String> appNameArray = new ArrayList();

    private ArrayList updateAppNameArray() {
        if (appNameArray != null && appNameArray.size() > 0) {
            return appNameArray;
        }
        ArrayList ttvNameArrayTemp = getAllOpenAppArray();
        if (ttvNameArrayTemp != null) {
            appNameArray = ttvNameArrayTemp;
        }
        return appNameArray;
    }

    public int processFeatureApp() {

        updateAppNameArray();
        if ((appNameArray == null) || (appNameArray.size() == 0)) {
            return 0;
        }
        int result = 0;
        Calendar dateNow = TimeConvertion.getCurrentCalendar();
        long lockDateValue = dateNow.getTimeInMillis();
        String LockName = "ETL_" + SsnsService.APP_APP;

        try {
            int lockReturn = setLockNameProcess(LockName, ConstantKey.ETL_LOCKTYPE, lockDateValue, ServiceAFweb.getServerObj().getSrvProjName() + " processFeatureApp");
            if (CKey.NN_DEBUG == true) {
                lockReturn = 1;
            }
            if (lockReturn == 0) {
                return 0;
            }

            logger.info("processFeatureApp for 2 minutes size " + appNameArray.size());

            long currentTime = System.currentTimeMillis();
            long lockDate1Min = TimeConvertion.addMinutes(currentTime, 2);

            for (int i = 0; i < 5; i++) {
                currentTime = System.currentTimeMillis();
//                if (CKey.NN_DEBUG != true) {
                if (lockDate1Min < currentTime) {
                    break;
                }
//                }
                if (appNameArray.size() == 0) {
                    break;
                }

                String idSt = (String) appNameArray.get(0);
                appNameArray.remove(0);
                SsnsService ss = new SsnsService();
                int id = Integer.parseInt(idSt);
                SsnsData data = getSsnsDataImp().getSsnsDataObjByID(id);
                String feature = ss.getFeatureSsnsAppointment(data);
//                logger.info("> feature " + i + " " + feature);

                AFSleep();
            }
        } catch (Exception ex) {
        }
        removeNameLock(LockName, ConstantKey.ETL_LOCKTYPE);
        return result;

    }
////////////////////////////////////

    ArrayList<String> getAllOpenProductArray() {
        String app = SsnsService.APP_PRODUCT;
        String ret = "parameter";
        int status = ConstantKey.OPEN;
        return getSsnsDataImp().getSsnsDataIDList(app, ret, status, 15);
    }

    ArrayList<String> prodNameArray = new ArrayList();

    private ArrayList updateProdNameArray() {
        if (prodNameArray != null && prodNameArray.size() > 0) {
            return prodNameArray;
        }
        ArrayList ttvNameArrayTemp = getAllOpenProductArray();
        if (ttvNameArrayTemp != null) {
            prodNameArray = ttvNameArrayTemp;
        }
        return prodNameArray;
    }

    public int processFeatureProd() {

        updateProdNameArray();
        if ((prodNameArray == null) || (prodNameArray.size() == 0)) {
            return 0;
        }
        int result = 0;
        Calendar dateNow = TimeConvertion.getCurrentCalendar();
        long lockDateValue = dateNow.getTimeInMillis();

        String LockName = "ETL_" + SsnsService.APP_PRODUCT;
        try {
            int lockReturn = setLockNameProcess(LockName, ConstantKey.ETL_LOCKTYPE, lockDateValue, ServiceAFweb.getServerObj().getSrvProjName() + " processFeatureProd");
            if (CKey.NN_DEBUG == true) {
                lockReturn = 1;
            }
            if (lockReturn == 0) {
                return 0;
            }

            logger.info("processFeatureProd for 2 minutes size " + prodNameArray.size());

            long currentTime = System.currentTimeMillis();
            long lockDate1Min = TimeConvertion.addMinutes(currentTime, 2);

            for (int i = 0; i < 5; i++) {
                currentTime = System.currentTimeMillis();
//                if (CKey.NN_DEBUG != true) {
                if (lockDate1Min < currentTime) {
                    break;
                }
//                }
                if (prodNameArray.size() == 0) {
                    break;
                }

                String idSt = (String) prodNameArray.get(0);
                prodNameArray.remove(0);
                SsnsService ss = new SsnsService();
                int id = Integer.parseInt(idSt);
                SsnsData data = getSsnsDataImp().getSsnsDataObjByID(id);
                String feature = ss.getFeatureSsnsProdiuctInventory(data);
//                logger.info("> feature " + i + " " + feature);

                AFSleep();
            }
        } catch (Exception ex) {
        }
        removeNameLock(LockName, ConstantKey.ETL_LOCKTYPE);
        return result;

    }

    void processETL() {

        String file = FileLocalPath + "clear.txt";
        if (FileUtil.FileTest(file) == true) {
            this.getSsnsDataImp().deleteAllSsnsData(0);
            FileUtil.FileDelete(file);
            return;
        }

        String app = SsnsService.APP_WIFI; //"wifi";
        file = FileLocalPath + app + "data.csv";
        if (FileUtil.FileTest(file) == true) {
            boolean ret = processETLsplunk(app, 10000);
            if (ret == true) {
                FileUtil.FileDelete(file);
            }
            return;
        }

        app = SsnsService.APP_PRODUCT;  //"product"
        file = FileLocalPath + app + "data.csv";
        if (FileUtil.FileTest(file) == true) {
            boolean ret = processETLsplunk(app, 10000);
            if (ret == true) {
                FileUtil.FileDelete(file);
            }
            return;
        }

        app = SsnsService.APP_APP;  //"appointment"
        file = FileLocalPath + app + "data.csv";
        if (FileUtil.FileTest(file) == true) {
            boolean ret = processETLsplunk(app, 0);
            if (ret == true) {
                FileUtil.FileDelete(file);
            }
            return;
        }

        String appTTV = SsnsService.APP_TTVSUB;  //ttvsub"
        file = FileLocalPath + appTTV + "data.csv";
        if (FileUtil.FileTest(file) == true) {
            boolean ret = processETLsplunkTTV(appTTV, 0);
            if (ret == true) {
                FileUtil.FileDelete(file);
            }
            return;
        }

        appTTV = SsnsService.APP_TTVREQ; //"ttvreq";
        file = FileLocalPath + appTTV + "data.csv";
        if (FileUtil.FileTest(file) == true) {
            boolean ret = processETLsplunkTTV(appTTV, 0);
            if (ret == true) {
                FileUtil.FileDelete(file);
            }
            return;
        }
    }

    public int proceSssendRequestObj(ArrayList<String> sqlCMDList) {
        int MAXPostSize = 5;
        int postSize = 0;
        ArrayList<String> sqlSendList = new ArrayList();
        for (int i = 0; i < sqlCMDList.size(); i++) {
            postSize++;
            if (postSize > MAXPostSize) {
                try {
                    int ret = sendRequestObj(sqlSendList);
                    if (ret == 0) {
                        return ret;
                    }
                    postSize = 0;
                    sqlSendList.clear();

                } catch (Exception ex) {
                    logger.info("postExecuteListRemoteDB_Mysql exception " + ex);
                    return 0;
                }
            }
            sqlSendList.add(sqlCMDList.get(i));
        }
        try {
            int ret = sendRequestObj(sqlSendList);
            return ret;
        } catch (Exception ex) {
            logger.info("postExecuteListRemoteDB_Mysql exception " + ex);
        }
        return 0;

    }

    public boolean processETLsplunkTTV(String app, int length) {

        Calendar dateNow = TimeConvertion.getCurrentCalendar();
        long lockDateValue = dateNow.getTimeInMillis();
        String LockName = "ETL_TTVC";
        int lockReturn = setLockNameProcess(LockName, ConstantKey.ETL_LOCKTYPE, lockDateValue, ServiceAFweb.getServerObj().getSrvProjName() + " processETLsplunkTTV " + app);
        if (CKey.NN_DEBUG == true) {
            lockReturn = 1;
        }
        if (lockReturn == 0) {
            return false;
        }

        String file = FileLocalPath + app + "data.csv";
        if (FileUtil.FileTest(file) == false) {
            logger.info("> No File exist " + file);
            return false;
        }
        int numAdd = 0;
        int numFail = 0;
        int numDup = 0;
        ArrayList<String> writeArray = new ArrayList();
        FileUtil.FileReadTextArray(file, writeArray);

        ArrayList<String> writeSQLArray = new ArrayList();
        logger.info("> processETLsplunkTTV " + app + " " + writeArray.size());
        String spSt = "";
        int size = writeArray.size();
        if (length == 0) {
            size = writeArray.size();
        } else {
            if (size > length) {
                size = length;
            }
        }
        for (int i = 0; i < size; i++) {
            try {
                String daSt = "";
                long datel = 0;

                SsnsData item = new SsnsData();
                item.setApp(app);

                spSt = writeArray.get(i);
                boolean processFlag = true;
                String[] spList = spSt.split(",");
                for (int j = 0; j < spList.length; j++) {
                    if (spList.length < 3) {
                        processFlag = false;
                        break;
                    }
//                    logger.info("splunk " + j + " " + spList[j]);

                    String inLine = spList[j];
                    if (j == 0) {
                        daSt = spList[j];
                        daSt = replaceAll("\"[", "", daSt);
                        Calendar c = parseDateTimeTTV(daSt);

                        if (c == null) {
                            processFlag = false;
                            break;
                        } else {
                            datel = c.getTimeInMillis();
                            item.setUpdatedatel(datel);
                            item.setUpdatedatedisplay(new java.sql.Date(datel));
                        }
                        continue;
                    }

                    if (inLine.indexOf("operation=") != -1) {
                        int beg = spSt.indexOf("operation=");
                        String opSt = spSt.substring(beg);
                        this.processETLsplunkTTV1(item, opSt);
                        continue;
                    }
                }
                if (processFlag == false) {
                    continue;
                }
                if (item.getUid().length() == 0) {
//                    logger.info("splunk " + i + " " + spSt);
                    continue;
                }

                String key = item.getUid() + item.getUpdatedatel();
                item.setName(key);
                String sql = SsnsDataDB.insertSsnsDataObjectSQL(item);
//                logger.info("SsnsdDataDB " + i + " " + sql);

                SsnsData ssnsObj = getSsnsDataImp().getSsnsDataObj(key);
                if (ssnsObj == null) {
                    writeSQLArray.add(sql);
                    if (writeSQLArray.size() > 100) {
                        proceSssendRequestObj(writeSQLArray);
                        ServiceAFweb.AFSleep();
                        writeSQLArray.clear();
                    }
                    if ((numAdd % 500) == 0) {
                        logger.info("> ETLsplunkProcess  " + numAdd);
                    }
                    numAdd++;
                    ////////just for testing
                    if (numAdd > 3000) {
                        break;
                    }
                    ////////just for testing                    
                } else {
                    numDup++;
                }
            } catch (Exception e) {
                logger.info("> processETLsplunkTTV exception " + e.getMessage() + " " + spSt);
                numFail++;
            }
        }
        int st = proceSssendRequestObj(writeSQLArray);
        logger.info("> processETLsplunkTTV done add:" + numAdd + " fail:" + numFail + " dup:" + numDup + " file:" + file);
        removeNameLock(LockName, ConstantKey.ETL_LOCKTYPE);
        return true;
    }

    private void processETLsplunkTTV1(SsnsData item, String spSt) {
        try {
            String oper = "";
            String down = "";
            String execSt = "";
            long exec = 0;
            String uuid = "";
            String status = "";
            String ret = "";

            boolean processFlag = true;
            String[] spList = spSt.split(" ");
            for (int j = 0; j < spList.length; j++) {
                if (spList.length < 3) {
                    processFlag = false;
                    break;
                }
//                logger.info("splunk " + j + " " + spList[j]);

                String inLine = spList[j];
                if (inLine.indexOf("operation=") != -1) {
                    oper = inLine.replace("operation=", "");
                    continue;
                }
                if (inLine.indexOf("clientOperation=") != -1) {
                    down = inLine.replace("clientOperation=", "");
                    continue;
                }
                if (inLine.indexOf("executionTime=") != -1) {
                    execSt = inLine.replace("executionTime=", "");
                    if (execSt.length() > 0) {
                        exec = Long.parseLong(execSt);
                    }
                    continue;
                }

                if (inLine.indexOf("httpCd=") != -1) {
//                        ret = inLine.replace("httpCd=", "");
                    ret = inLine;
                    continue;
                }
                if (inLine.indexOf("stacktrace=") != -1) {
                    status = inLine;
                    continue;
                }
                if (inLine.indexOf("transactionId=") != -1) {
                    uuid = inLine.replace("transactionId=", "");
                    continue;
                }
                if (inLine.indexOf("status=") != -1) {
                    status = inLine.replace("status=", "");
                    int beg = spSt.indexOf("status=");
                    String temSt = spSt.substring(beg + 7, spSt.length());
                    int end = temSt.indexOf("}");
                    if (end != -1) {
                        status = temSt.substring(0, end + 1);
                        status = replaceAll("\"\"", "\"", status);
                        String[] statusList = status.split(",");
                        if (statusList.length > 1) {
                            String[] statusCdList = statusList[0].split(":");
                            ret = "httpCd=" + statusCdList[1];
                            ret = replaceAll("\"", "", ret);
                        }
                    }
                    continue;
                }
                if (inLine.indexOf("parameter=") != -1) {
                    status = inLine.replace("parameter=", "");
                    String parmSt = spSt;
                    int beg = parmSt.indexOf("parameter=");

                    String temSt = parmSt.substring(beg + 10, parmSt.length());
                    int end = temSt.indexOf(oper);
                    if (end != -1) {
                        status = temSt.substring(0, end);
                        // search the last of ]
                        int lastend = status.lastIndexOf("]");
                        status = temSt.substring(0, lastend + 1);

                    }
                    status = replaceAll("\"\"", "\"", status);
                    ret = "parameter";

                    continue;
                }

            }
            if (processFlag == false) {
                return;
            }

            if (uuid.length() == 0) {
//                    logger.info("splunk " + i + " " + spSt);
                return;
            }
            item.setUid(uuid);
            item.setOper(oper);
            item.setDown(down);
            item.setRet(ret);
            item.setExec(exec);
            status = replaceAll("|", "", status);
            item.setData(status);
        } catch (Exception e) {
            logger.info("> ETLsplunkProcess exception " + e.getMessage() + " " + spSt);
        }

    }

    public boolean processETLsplunk(String app, int length) {
        Calendar dateNow = TimeConvertion.getCurrentCalendar();
        long lockDateValue = dateNow.getTimeInMillis();
        String LockName = "ETL_" + app;
        int lockReturn = setLockNameProcess(LockName, ConstantKey.ETL_LOCKTYPE, lockDateValue, ServiceAFweb.getServerObj().getSrvProjName() + " processETLsplunk " + app);
        if (CKey.NN_DEBUG == true) {
            lockReturn = 1;
        }
        if (lockReturn == 0) {
            return false;
        }

        String file = FileLocalPath + app + "data.csv";
        if (FileUtil.FileTest(file) == false) {
            logger.info("> No File exist " + file);
            return false;
        }

        int numAdd = 0;
        int numFail = 0;
        int numDup = 0;
        ArrayList<String> writeArray = new ArrayList();
        FileUtil.FileReadTextArray(file, writeArray);

        ArrayList<String> writeSQLArray = new ArrayList();
        logger.info("> ETLsplunkProcess " + app + " " + writeArray.size());
        String spSt = "";
        int size = writeArray.size();
        if (length == 0) {
            size = writeArray.size();
        } else {
            if (size > length) {
                size = length;
            }
        }
        for (int i = 0; i < size; i++) {
            try {
                String daSt = "";
                String timeSt = "";
                String oper = "";
                String down = "";
                String execSt = "";
                long exec = 0;
                String tran = "";
                String status = "";
                String ret = "";
                long datel = 0;
                spSt = writeArray.get(i);
                boolean processFlag = true;
                String[] spList = spSt.split(" ");
                for (int j = 0; j < spList.length; j++) {
                    if (spList.length < 3) {
                        processFlag = false;
                        break;
                    }
//                    logger.info("splunk " + j + " " + spList[j]);

                    String inLine = spList[j];
                    if (j == 0) {
                        daSt = spList[j];
                        daSt = daSt.replaceAll("\"", "");
                        timeSt = spList[j + 1];
                        Calendar c = parseDateTime(daSt, timeSt);

                        if (c == null) {
                            processFlag = false;
                            break;
                        }
                        datel = c.getTimeInMillis();
                        continue;
                    }

                    if (inLine.indexOf("operation=") != -1) {
                        oper = inLine.replace("operation=", "");
                        continue;
                    }
                    if (inLine.indexOf("clientOperation=") != -1) {
                        down = inLine.replace("clientOperation=", "");
                        continue;
                    }
                    if (inLine.indexOf("executionTime=") != -1) {
                        execSt = inLine.replace("executionTime=", "");
                        if (execSt.length() > 0) {
                            exec = Long.parseLong(execSt);
                        }
                        continue;
                    }

                    if (inLine.indexOf("httpCd=") != -1) {
//                        ret = inLine.replace("httpCd=", "");
                        ret = inLine;
                        continue;
                    }
                    if (inLine.indexOf("stacktrace=") != -1) {
                        status = inLine;
                        continue;
                    }
                    if (inLine.indexOf("transactionId=") != -1) {
                        tran = inLine.replace("transactionId=", "");
                        continue;
                    }
                    if (inLine.indexOf("status=") != -1) {
                        status = inLine.replace("status=", "");
                        int beg = spSt.indexOf("status=");
                        String temSt = spSt.substring(beg + 7, spSt.length());
                        int end = temSt.indexOf("}");
                        if (end != -1) {
                            status = temSt.substring(0, end + 1);
                        }
                        continue;
                    }
                    if (inLine.indexOf("parameter=") != -1) {
                        if (app.equals(SsnsService.APP_WIFI)) {
                            if (oper.equals(SsnsService.WI_Callback)) {
                                String parmSt = spSt;
                                int beg = parmSt.indexOf("parameter=");

                                String temSt = parmSt.substring(beg + 10, parmSt.length());
                                int end = temSt.indexOf("]");
                                if (end != -1) {
                                    status = temSt.substring(0, end + 1);
                                }
                                status = replaceAll("\"", "", status);
                                status = replaceAll("\\n", "", status);
                                status = replaceAll("\\", "\"", status);
                                String[] statusL = status.split(" ");
                                String tranUid = "";
                                if (statusL.length > 0) {
                                    tranUid = getCallback(statusL);
                                }
                                if (tranUid.length() > 0) {
                                    tran = tranUid;
                                    down = "TOCP";
                                }
                                ret = "parameter";
                                continue;
                            } else {
                                String parmSt = spSt;
                                int beg = parmSt.indexOf("parameter=");

                                String temSt = parmSt.substring(beg + 10, parmSt.length());
                                int end = temSt.indexOf("]");
                                if (end != -1) {
                                    status = temSt.substring(0, end + 1);
                                }
                                status = replaceAll("\"\"", "\"", status);

                                ret = "parameter";
                                continue;
                            }
                        }
                        status = inLine.replace("parameter=", "");
                        String parmSt = spSt;
                        int beg = parmSt.indexOf("parameter=");

                        String temSt = parmSt.substring(beg + 10, parmSt.length());
                        int end = temSt.indexOf("]");
                        if (end != -1) {
                            status = temSt.substring(0, end + 1);
                        }
                        status = replaceAll("\"\"", "\"", status);
                        ret = "parameter";

                        continue;
                    }

                }
                if (processFlag == false) {
                    continue;
                }
                if (datel == 0) {
                    continue;
                }
                if (tran.length() == 0) {
//                    logger.info("splunk " + i + " " + spSt);
                    continue;
                }

                SsnsData item = new SsnsData();
                if (oper.equals(SsnsService.APP_GET_APP) || oper.equals(SsnsService.APP_CAN_APP) || oper.equals(SsnsService.APP_GET_TIMES) || oper.equals(SsnsService.APP_UPDATE)) {
                    if (app.equals(SsnsService.APP_APP) == false) {
                        logger.info("Wrong data file " + app + " detected:" + SsnsService.APP_APP);
                        return true;
                    }
                    app = SsnsService.APP_APP;
                } else if (oper.equals(SsnsService.PROD_GET_BYID) || oper.equals(SsnsService.PROD_GET_PROD)) {
                    if (app.equals(SsnsService.APP_PRODUCT) == false) {
                        logger.info("Wrong data file " + app + " detected:" + SsnsService.APP_PRODUCT);
                        return true;
                    }
                    app = SsnsService.APP_PRODUCT;
                } else if (oper.equals(SsnsService.WI_GetDeviceStatus) || oper.equals(SsnsService.WI_Callback) || oper.equals(SsnsService.WI_GetDevice) || oper.equals(SsnsService.WI_config)) {
                    if (app.equals(SsnsService.APP_WIFI) == false) {
                        logger.info("Wrong data file " + app + " detected:" + SsnsService.APP_WIFI);
                        return true;
                    }
                    app = SsnsService.APP_WIFI;
                } else {
                    continue;
                }

                item.setUid(tran);
                item.setApp(app);
                item.setOper(oper);
                item.setDown(down);
                item.setRet(ret);
                item.setExec(exec);
                item.setData(status);
                item.setUpdatedatel(datel);
                item.setUpdatedatedisplay(new java.sql.Date(datel));

                String key = item.getUid() + item.getUpdatedatel();
                item.setName(key);
                String sql = SsnsDataDB.insertSsnsDataObjectSQL(item);
//                logger.info("SsnsdDataDB " + i + " " + sql);
                SsnsData ssnsObj = getSsnsDataImp().getSsnsDataObj(key);
                if (ssnsObj == null) {
                    writeSQLArray.add(sql);
                    if (writeSQLArray.size() > 100) {
                        proceSssendRequestObj(writeSQLArray);
                        ServiceAFweb.AFSleep();
                        writeSQLArray.clear();
                    }
                    if ((numAdd % 500) == 0) {
                        logger.info("> ETLsplunkProcess  " + numAdd);
                    }
                    numAdd++;
                    ////////just for testing
                    if (numAdd > 3000) {
                        break;
                    }
                    ////////just for testing
                } else {
                    numDup++;
                }
            } catch (Exception e) {
                logger.info("> ETLsplunkProcess exception " + e.getMessage() + " " + spSt);
                numFail++;
            }
        }
        int st = proceSssendRequestObj(writeSQLArray);
        logger.info("> ETLsplunkProcess done add:" + numAdd + " fail:" + numFail + " dup:" + numDup + " file:" + file);
        removeNameLock(LockName, ConstantKey.ETL_LOCKTYPE);
        return true;
    }

    public String getCallback(String[] statusL) {
        String tranUid = "";
        for (int k = 0; k < statusL.length; k++) {
            String inL = statusL[k];
            if (inL.indexOf("CallbackID") != -1) {
                for (int m = 0; m < statusL.length; m++) {
                    String inLL = statusL[m];
                    if (inLL.indexOf("<Value>") != -1) {
                        String cUid = ServiceAFweb.replaceAll("<Value>", "", inLL);
                        if (cUid.length() >= 36) {
                            tranUid = cUid.substring(0, 36);
                            return tranUid;
                        }

                    }

                }

            }
        }
        return "";
    }

    public static String replaceAll(String oldStr, String newStr, String inString) {
        while (true) {
            int start = inString.indexOf(oldStr);
            if (start == -1) {
                return inString;
            }
            inString = replace(oldStr, newStr, inString);
        }

    }

    public static String replace(String oldStr, String newStr, String inString) {
        int start = inString.indexOf(oldStr);
        if (start == -1) {
            return inString;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(inString.substring(0, start));
        sb.append(newStr);
        sb.append(inString.substring(start + oldStr.length()));
        return sb.toString();
    }

    public static Calendar parseDateTimeTTV(String date) {
//    11 Apr 2020 14:36:47       
        String tzid = "America/New_York"; //EDT
        TimeZone tz = TimeZone.getTimeZone(tzid);
        String datetime = date;
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");

        format.setTimeZone(tz);
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(format.parse(datetime));

            return c;

        } catch (ParseException ex) {
//            logger.info("Failed to parse datetime: " + datetime + " " + ex);

        }
        return null;
    }

    public static Calendar parseDateTime(String date, String time) {
//splunk 0 "2020-04-09
//splunk 1 17:28:55.622        
        String tzid = "America/New_York"; //EDT
        TimeZone tz = TimeZone.getTimeZone(tzid);
//        String[] timeList = time.split(":");
        String datetime = date + " " + time; //+ timeList[0] + ":" + timeList[1] + ":" + timeList[2];
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        format.setTimeZone(tz);
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(format.parse(datetime));

            return c;

        } catch (ParseException ex) {
//            logger.info("Failed to parse datetime: " + datetime + " " + ex);

        }
        return null;
    }

    public int sendRequestObj(ArrayList<String> writeSQLArray) {
//        logger.info("> sendRequestObj " + writeSQLArray.size());
        try {
            RequestObj sqlObj = new RequestObj();
            sqlObj.setCmd(ServiceAFweb.UpdateSQLList + "");
            String st = new ObjectMapper().writeValueAsString(writeSQLArray);
            sqlObj.setReq(st);
            RequestObj sqlObjresp = SystemSQLRequest(sqlObj);
            String output = sqlObjresp.getResp();
            if (output == null) {
                return 0;
            }
            return 1;
        } catch (JsonProcessingException ex) {
            logger.info("> sendRequestObj - exception " + ex.getMessage());
        }
        return 0;
    }

    public static void AFSleep1Sec(int sec) {
        // delay seems causing openshif not working        
        if (true) {
            return;
        }
        try {
            if ((sec < 0) || (sec > 10)) {
                sec = 1;
            }
            Thread.sleep(1000 * sec);
        } catch (Exception ex) {
        }
    }

    public static void AFSleep() {
        // delay seems causing openshif not working        
        if (true) {
            return;
        }
        try {
            Thread.sleep(10);
        } catch (Exception ex) {
        }
    }

    private void RandomDelayMilSec(int sec) {

        // delay seems causing openshif not working
        if (true) {
            return;
        }
        try {
            int max = sec + 100;
            int min = sec;
            Random randomNum = new Random();
            int sleepRandom = min + randomNum.nextInt(max);

            if (sleepRandom < 0) {
                sleepRandom = sec;
            }

            Thread.sleep(sleepRandom);
        } catch (InterruptedException ex) {
            logger.info("> RandomDelayMilSec exception " + ex.getMessage());
        }
    }

    public static boolean checkCallRemoteMysql() {
        boolean ret = true;
        if (ServiceAFweb.getServerObj().isLocalDBservice() == true) {
            ret = false;
        }
//        if (CKey.SQL_DATABASE == CKey.REMOTE_MYSQL) {
//            ret = false;
//        }
        return ret;
    }

    //////////////////////////////////////
    public int removeCustomer(String customername) {
        if (getServerObj().isSysMaintenance() == true) {
            return 0;
        }

        CustomerObj custObj = getAccountImp().getCustomerStatus(customername, null);

        if (custObj == null) {
            return 0;
        }
        if (custObj.getStatus() == ConstantKey.OPEN) {
            return 0;
        }
        return getAccountImp().removeCustomer(custObj);
    }

//       SUCC = 1;  EXISTED = 2; FAIL =0;
    public LoginObj addCustomerPassword(String EmailUserName, String Password, String FirstName, String LastName) {
        LoginObj loginObj = new LoginObj();
        loginObj.setCustObj(null);
        WebStatus webStatus = new WebStatus();
        webStatus.setResultID(0);
        loginObj.setWebMsg(webStatus);
        loginObj.setWebMsg(webStatus);
        if (getServerObj().isSysMaintenance() == true) {
            return loginObj;
        }

        NameObj nameObj = new NameObj(EmailUserName);
        String UserName = nameObj.getNormalizeName();
        boolean validEmail = NameObj.isEmailValid(EmailUserName);
        if (validEmail == true) {
            CustomerObj newCustomer = new CustomerObj();
            newCustomer.setUsername(UserName);
            newCustomer.setPassword(Password);
            newCustomer.setType(CustomerObj.INT_CLIENT_BASIC_USER);
            newCustomer.setEmail(EmailUserName);
            newCustomer.setFirstname(FirstName);
            newCustomer.setLastname(LastName);
            int result = getAccountImp().addCustomer(newCustomer);
//
            String tzid = "America/New_York"; //EDT
            TimeZone tz = TimeZone.getTimeZone(tzid);

            Calendar dateNow = TimeConvertion.getCurrentCalendar();
            long dateNowLong = dateNow.getTimeInMillis();
            java.sql.Date d = new java.sql.Date(dateNowLong);
            DateFormat format = new SimpleDateFormat(" hh:mm a");
            format.setTimeZone(tz);
            String ESTdate = format.format(d);
            String msg = ESTdate + " " + newCustomer.getUsername() + " Cust signup Result:" + result;
//            
            webStatus.setResultID(result);
            return loginObj;
        }
        webStatus.setResultID(0);
        return loginObj;
    }

    public CustomerObj getCustomerIgnoreMaintenance(String EmailUserName, String Password) {

        NameObj nameObj = new NameObj(EmailUserName);
        String UserName = nameObj.getNormalizeName();
        return getAccountImp().getCustomerPassword(UserName, Password);
    }

    public CustomerObj getCustomerPassword(String EmailUserName, String Password) {
        if (getServerObj().isSysMaintenance() == true) {
            return null;
        }

        NameObj nameObj = new NameObj(EmailUserName);
        String UserName = nameObj.getNormalizeName();
        return getAccountImp().getCustomerPassword(UserName, Password);
    }

    public LoginObj getCustomerEmailLogin(String EmailUserName, String Password) {
        if (getServerObj().isSysMaintenance() == true) {
            return null;
        }
        CustomerObj custObj = null;
        {
            NameObj nameObj = new NameObj(EmailUserName);
            String UserName = nameObj.getNormalizeName();
            custObj = getAccountImp().getCustomerPassword(UserName, Password);
        }
        LoginObj loginObj = new LoginObj();
        loginObj.setCustObj(custObj);
        WebStatus webStatus = new WebStatus();
        webStatus.setResultID(1);
        if (custObj == null) {
            webStatus.setResultID(0);
        }
        loginObj.setWebMsg(webStatus);
        return loginObj;
    }

    public LoginObj getCustomerLogin(String EmailUserName, String Password) {
        if (getServerObj().isSysMaintenance() == true) {
            return null;
        }
        CustomerObj custObj = null;
        {
            NameObj nameObj = new NameObj(EmailUserName);
            String UserName = nameObj.getNormalizeName();
            custObj = getAccountImp().getCustomerPassword(UserName, Password);
        }
        LoginObj loginObj = new LoginObj();
        loginObj.setCustObj(custObj);
        WebStatus webStatus = new WebStatus();
        webStatus.setResultID(1);
        if (custObj == null) {
            webStatus.setResultID(0);
        }
        loginObj.setWebMsg(webStatus);
        return loginObj;

    }

    public ArrayList<SsnsAcc> getapp(String EmailUserName, String IDSt, int length) {
        if (getServerObj().isSysMaintenance() == true) {
            return null;
        }
        CustomerObj custObj = getAccountImp().getCustomerPassword(EmailUserName, null);
        if (custObj == null) {
            return null;
        }
        if (IDSt != null) {
            if (IDSt.equals(custObj.getId() + "") != true) {
                return null;
            }
        }
        ArrayList<SsnsAcc> ssnsAccObjList = getSsnsDataImp().getSsnsAccObjListByApp(SsnsService.APP_APP, length);
        return ssnsAccObjList;

    }

    public SsReport getSsReportById(String EmailUserName, String IDSt, String pidSt) {

        if (getServerObj().isSysMaintenance() == true) {
            return null;
        }
        CustomerObj custObj = getAccountImp().getCustomerPassword(EmailUserName, null);
        if (custObj == null) {
            return null;
        }
        if (IDSt != null) {
            if (IDSt.equals(custObj.getId() + "") != true) {
                return null;
            }
        }
        String name = CKey.ADMIN_USERNAME;
        int id = 0;

        if (pidSt != null) {
            id = Integer.parseInt(pidSt);
        }
        SsReport reportObj = getSsnsDataImp().getSsReportByID(id);
        return reportObj;

    }

    public int getSsReportMonStop(String EmailUserName, String IDSt) {

        if (getServerObj().isSysMaintenance() == true) {
            return 0;
        }
        CustomerObj custObj = getAccountImp().getCustomerPassword(EmailUserName, null);
        if (custObj == null) {
            return 0;
        }
        if (IDSt != null) {
            if (IDSt.equals(custObj.getId() + "") != true) {
                return 0;
            }
        }
        if (custObj.getType() != CustomerObj.INT_ADMIN_USER) {
            return 10;
        }
        String name = CKey.ADMIN_USERNAME;
        SsnsRegression regression = new SsnsRegression();
        return regression.stopMonitor(this, name);
    }

    public int getSsReportMonUpdateReport(String EmailUserName, String IDSt) {

        if (getServerObj().isSysMaintenance() == true) {
            return 0;
        }
        CustomerObj custObj = getAccountImp().getCustomerPassword(EmailUserName, null);
        if (custObj == null) {
            return 0;
        }
        if (IDSt != null) {
            if (IDSt.equals(custObj.getId() + "") != true) {
                return 0;
            }
        }
        if (custObj.getType() != CustomerObj.INT_ADMIN_USER) {
            return 10;
        }
        String name = CKey.ADMIN_USERNAME;
        SsnsRegression regression = new SsnsRegression();
        int ret = 0;
        regression.reportMoniter(this, name);
        return ret;
    }

    public int getSsReportMonStart(String EmailUserName, String IDSt) {

        if (getServerObj().isSysMaintenance() == true) {
            return 0;
        }
        CustomerObj custObj = getAccountImp().getCustomerPassword(EmailUserName, null);
        if (custObj == null) {
            return 0;
        }
        if (IDSt != null) {
            if (IDSt.equals(custObj.getId() + "") != true) {
                return 0;
            }
        }
        if (custObj.getType() != CustomerObj.INT_ADMIN_USER) {
            return 10;
        }
        String name = CKey.ADMIN_USERNAME;
        SsnsRegression regression = new SsnsRegression();

        Calendar dateNow = TimeConvertion.getCurrentCalendar();
        long lockDateValue = dateNow.getTimeInMillis();
        String LockName = "MONSTART_" + EmailUserName;

        int ret = 0;
        try {
            int lockReturn = setLockNameProcess(LockName, ConstantKey.MONSTART_LOCKTYPE, lockDateValue, ServiceAFweb.getServerObj().getSrvProjName() + " getSsReportMonStart");
            if (lockReturn == 0) {
                return 0;
            }
            ret = regression.startMonitor(this, name);
            // clear old report
            getSsReportMonClearReport(EmailUserName, IDSt);

        } catch (Exception ex) {
        }
        removeNameLock(LockName, ConstantKey.MONSTART_LOCKTYPE);
        return ret;
    }

    public int getSsReportMonClearReport(String EmailUserName, String IDSt) {

        if (getServerObj().isSysMaintenance() == true) {
            return 0;
        }
        CustomerObj custObj = getAccountImp().getCustomerPassword(EmailUserName, null);
        if (custObj == null) {
            return 0;
        }
        if (IDSt != null) {
            if (IDSt.equals(custObj.getId() + "") != true) {
                return 0;
            }
        }
        String name = CKey.ADMIN_USERNAME;

        ArrayList<SsReport> ssReportObjList = getSsnsDataImp().getSsReportObjListByUidDesc(name, SsnsRegression.REPORT_REPORT);
        if (ssReportObjList != null) {
            for (int i = 0; i < ssReportObjList.size(); i++) {
                if (i < 2) {
                    continue;
                }
                SsReport repObj = ssReportObjList.get(i);
                String nameRepId = repObj.getName() + "_" + repObj.getId();
                getSsnsDataImp().DeleteSsReportObjListByUid(nameRepId, SsnsRegression.REPORT_TESE_CASE);

                getSsnsDataImp().DeleteSsReportObjByID(repObj.getId());

            }
        }

        return 1;

    }

    public ArrayList<ProdSummary> getSsReportMonReport(String EmailUserName, String IDSt, String repIDSt) {

        if (getServerObj().isSysMaintenance() == true) {
            return null;
        }
        CustomerObj custObj = getAccountImp().getCustomerPassword(EmailUserName, null);
        if (custObj == null) {
            return null;
        }
        if (IDSt != null) {
            if (IDSt.equals(custObj.getId() + "") != true) {
                return null;
            }
        }
        String name = CKey.ADMIN_USERNAME;
        ArrayList<ProdSummary> ssReportList = new ArrayList();

        ArrayList<SsReport> ssReportObjList = getSsnsDataImp().getSsReportObjListByUidDesc(name, SsnsRegression.REPORT_REPORT);
        if (ssReportObjList != null) {
            if (ssReportObjList.size() > 0) {
                SsReport reportObj = ssReportObjList.get(0);
                String nameRepId = reportObj.getName() + "_" + reportObj.getId();
                ArrayList<ProdSummary> ssTestcaseObjList = getSsnsDataImp().getSsReportSummaryObjListByUid(nameRepId, SsnsRegression.REPORT_TESE_CASE);

                ssReportList.addAll(ssTestcaseObjList);
            }
        }

        return ssReportList;

    }

    public String getSsReportMonExec(String EmailUserName, String IDSt) {

        if (getServerObj().isSysMaintenance() == true) {
            return null;
        }
        CustomerObj custObj = getAccountImp().getCustomerPassword(EmailUserName, null);
        if (custObj == null) {
            return null;
        }
        if (IDSt != null) {
            if (IDSt.equals(custObj.getId() + "") != true) {
                return null;
            }
        }
        //// process monitor
        SsnsRegression regression = new SsnsRegression();
        regression.processMonitorTesting(this);
        return "";

    }

    public ArrayList<SsReport> getSsReportMon(String EmailUserName, String IDSt) {

        if (getServerObj().isSysMaintenance() == true) {
            return null;
        }
        CustomerObj custObj = getAccountImp().getCustomerPassword(EmailUserName, null);
        if (custObj == null) {
            return null;
        }
        if (IDSt != null) {
            if (IDSt.equals(custObj.getId() + "") != true) {
                return null;
            }
        }
        String name = CKey.ADMIN_USERNAME;
        ArrayList<SsReport> ssReportList = new ArrayList();
        ArrayList<SsReport> ssUserReportObjList = getSsnsDataImp().getSsReportObjListByUidDesc(name, SsnsRegression.REPORT_USER);
        ArrayList<SsReport> ssReportObjList = getSsnsDataImp().getSsReportObjListByUidDesc(name, SsnsRegression.REPORT_REPORT);

        ssReportList.addAll(ssUserReportObjList);
        ssReportList.addAll(ssReportObjList);
        return ssReportList;

    }

    public ArrayList<String> getSsnsprodAll(String EmailUserName, String IDSt, int length) {
        if (getServerObj().isSysMaintenance() == true) {
            return null;
        }
        CustomerObj custObj = getAccountImp().getCustomerPassword(EmailUserName, null);
        if (custObj == null) {
            return null;
        }
        if (IDSt != null) {
            if (IDSt.equals(custObj.getId() + "") != true) {
                return null;
            }
        }
        ArrayList<String> ssnsList = new ArrayList();
        ssnsList.add(SsnsService.APP_PRODUCT);
        ssnsList.add("SSNS Product Inventory");
        ssnsList.add(SsnsService.APP_APP);
        ssnsList.add("SSNS Appointment");
        ssnsList.add(SsnsService.APP_WIFI);
        ssnsList.add("SSNS Wifi Sevice");
        ssnsList.add(SsnsService.APP_TTVC);
        ssnsList.add("SSNS TTV Service");

        return ssnsList;

    }

    public static ArrayList<ProdSummary> getProdSummaryFromAccList(ArrayList<SsnsAcc> ssnsAccObjList) {
        ArrayList<ProdSummary> psummaryList = new ArrayList();
        if (ssnsAccObjList == null) {
            return null;
        }
        for (int i = 0; i < ssnsAccObjList.size(); i++) {
            SsnsAcc accObj = ssnsAccObjList.get(i);
            ProdSummary sumObj = new ProdSummary();
            if (accObj != null) {
                sumObj.setBanid(accObj.getBanid());
                sumObj.setCusid(accObj.getCusid());
                sumObj.setId(accObj.getId());
                sumObj.setOper(accObj.getOper());
                sumObj.setTiid(accObj.getTiid());
                ProductData pData = null;
                String output = accObj.getData();
                try {
                    pData = new ObjectMapper().readValue(output, ProductData.class);
                    String postParamSt = ProductDataHelper.getPostParamRestore(pData.getPostParam());
                    sumObj.setDown(postParamSt);
                    ArrayList<String> flowN = ProductDataHelper.getFlowRestore(pData.getFlow());
                    String st = new ObjectMapper().writeValueAsString(flowN);
                    sumObj.setRet(st);
                } catch (IOException ex) {
                }
            }
            psummaryList.add(sumObj);
        }

        return psummaryList;
    }

    public ArrayList<ProdSummary> getSsnsprodSummary(String EmailUserName, String IDSt, int length, String prod) {
        if (getServerObj().isSysMaintenance() == true) {
            return null;
        }
        CustomerObj custObj = getAccountImp().getCustomerPassword(EmailUserName, null);
        if (custObj == null) {
            return null;
        }
        if (IDSt != null) {
            if (IDSt.equals(custObj.getId() + "") != true) {
                return null;
            }
        }
        ArrayList<SsnsAcc> ssnsAccObjList = getSsnsDataImp().getSsnsAccObjListByApp(prod, length);
        return getProdSummaryFromAccList(ssnsAccObjList);
    }

    public ArrayList<SsnsAcc> getSsnsprod(String EmailUserName, String IDSt, int length, String prod) {
        if (getServerObj().isSysMaintenance() == true) {
            return null;
        }
        CustomerObj custObj = getAccountImp().getCustomerPassword(EmailUserName, null);
        if (custObj == null) {
            return null;
        }
        if (IDSt != null) {
            if (IDSt.equals(custObj.getId() + "") != true) {
                return null;
            }
        }
        ArrayList<SsnsAcc> ssnsAccObjList = getSsnsDataImp().getSsnsAccObjListByApp(prod, length);
        return ssnsAccObjList;

    }

    public ArrayList<ProdSummary> getSsnsprodByFeatureNameSummary(String EmailUserName, String IDSt, String name, String prod, int length) {
        if (getServerObj().isSysMaintenance() == true) {
            return null;
        }
        CustomerObj custObj = getAccountImp().getCustomerPassword(EmailUserName, null);
        if (custObj == null) {
            return null;
        }
        if (IDSt != null) {
            if (IDSt.equals(custObj.getId() + "") != true) {
                return null;
            }
        }
        ArrayList<SsnsAcc> SsnsAcclist = getSsnsDataImp().getSsnsAccObjListByFeature(prod, name, length);
        return ServiceAFweb.getProdSummaryFromAccList(SsnsAcclist);
    }

    public ArrayList<SsnsAcc> getSsnsprodByFeatureName(String EmailUserName, String IDSt, String name, String prod, int length) {
        if (getServerObj().isSysMaintenance() == true) {
            return null;
        }
        CustomerObj custObj = getAccountImp().getCustomerPassword(EmailUserName, null);
        if (custObj == null) {
            return null;
        }
        if (IDSt != null) {
            if (IDSt.equals(custObj.getId() + "") != true) {
                return null;
            }
        }
        ArrayList<SsnsAcc> SsnsAcclist = getSsnsDataImp().getSsnsAccObjListByFeature(prod, name, length);
        return SsnsAcclist;

    }

    public ArrayList<SsReport> getSsReportByFeatureOperIdList(String EmailUserName, String IDSt, String prod, String oper) {
        if (getServerObj().isSysMaintenance() == true) {
            return null;
        }
        CustomerObj custObj = getAccountImp().getCustomerPassword(EmailUserName, null);
        if (custObj == null) {
            return null;
        }
        if (IDSt != null) {
            if (IDSt.equals(custObj.getId() + "") != true) {
                return null;
            }
        }
        ArrayList<SsReport> reportList = getSsnsDataImp().getSsReportByFeatureOperIdList(EmailUserName, prod, oper, 0);

        return reportList;
    }

    public ArrayList<String> getSsReportByFeature(String EmailUserName, String IDSt, String prod) {
        if (getServerObj().isSysMaintenance() == true) {
            return null;
        }
        CustomerObj custObj = getAccountImp().getCustomerPassword(EmailUserName, null);
        if (custObj == null) {
            return null;
        }
        if (IDSt != null) {
            if (IDSt.equals(custObj.getId() + "") != true) {
                return null;
            }
        }
        ArrayList<String> namelist = getSsnsDataImp().getSsReportObjListByFeatureOper(EmailUserName, prod);

        ArrayList<String> retlist = new ArrayList();
        if (namelist != null) {
            for (int i = 0; i < namelist.size(); i++) {
                String oper = namelist.get(i);
                retlist.add(oper);
                String cnt = getSsnsDataImp().getSsReportObjListByFeatureOperCnt(EmailUserName, oper);
                retlist.add(cnt);
            }
        }
        return retlist;
    }

    public ArrayList<String> getSsnsprodByFeature(String EmailUserName, String IDSt, String prod) {
        if (getServerObj().isSysMaintenance() == true) {
            return null;
        }
        CustomerObj custObj = getAccountImp().getCustomerPassword(EmailUserName, null);
        if (custObj == null) {
            return null;
        }
        if (IDSt != null) {
            if (IDSt.equals(custObj.getId() + "") != true) {
                return null;
            }
        }
        ArrayList<String> namelist = getSsnsDataImp().getSsnsAccObjListByFeature(prod);

        ArrayList<String> retlist = new ArrayList();
        if (namelist != null) {
            for (int i = 0; i < namelist.size(); i++) {
                String name = namelist.get(i);
                retlist.add(name);
                String cnt = getSsnsDataImp().getSsnsAccObjListByFeatureCnt(name);
                retlist.add(cnt);
            }
        }
        return retlist;
    }

    public SsnsAcc getappById(String EmailUserName, String IDSt, String PIDSt) {
        if (getServerObj().isSysMaintenance() == true) {
            return null;
        }
        CustomerObj custObj = getAccountImp().getCustomerPassword(EmailUserName, null);
        if (custObj == null) {
            return null;
        }
        if (IDSt != null) {
            if (IDSt.equals(custObj.getId() + "") != true) {
                return null;
            }
        }
        ArrayList<SsnsAcc> ssnsAccObjList = getSsnsDataImp().getSsnsAccObjListByID(null, PIDSt);
        if (ssnsAccObjList != null) {
            if (ssnsAccObjList.size() > 0) {
                SsnsAcc ssnsAccObj = (SsnsAcc) ssnsAccObjList.get(0);
                return ssnsAccObj;
            }
        }
        return null;
    }

    public SsnsAcc getSsnsprodById(String EmailUserName, String IDSt, String PIDSt, String prod) {
        if (getServerObj().isSysMaintenance() == true) {
            return null;
        }
        CustomerObj custObj = getAccountImp().getCustomerPassword(EmailUserName, null);
        if (custObj == null) {
            return null;
        }
        if (IDSt != null) {
            if (IDSt.equals(custObj.getId() + "") != true) {
                return null;
            }
        }
        ArrayList<SsnsAcc> ssnsAccObjList = getSsnsDataImp().getSsnsAccObjListByID(prod, PIDSt);
        if (ssnsAccObjList != null) {
            if (ssnsAccObjList.size() > 0) {
                SsnsAcc ssnsAccObj = (SsnsAcc) ssnsAccObjList.get(0);
                return ssnsAccObj;
            }
        }
        return null;
    }

    public ArrayList<String> testSsnsprodTTVCByIdRT(String EmailUserName, String IDSt, String PIDSt, String prod, String Oper) {
        if (getServerObj().isSysMaintenance() == true) {
            return null;
        }
        CustomerObj custObj = getAccountImp().getCustomerPassword(EmailUserName, null);
        if (custObj == null) {
            return null;
        }
        if (IDSt != null) {
            if (IDSt.equals(custObj.getId() + "") != true) {
                return null;
            }
        }
        ArrayList<SsnsAcc> ssnsAccObjList = getSsnsDataImp().getSsnsAccObjListByID(prod, PIDSt);
        if (ssnsAccObjList != null) {
            if (ssnsAccObjList.size() > 0) {
                SsnsAcc ssnsAccObj = (SsnsAcc) ssnsAccObjList.get(0);
                ArrayList<String> outputList = new ArrayList();
                SsnsService ss = new SsnsService();
                String feat = "";
                if ((Oper.equals(TT_GetSub) || Oper.equals(TT_Vadulate) || Oper.equals(TT_Quote) || Oper.equals(TT_SaveOrder))) {
                    feat = ss.TestFeatureSsnsProdTTVC(ssnsAccObj, outputList, Oper);
//                    logger.info("> testSsnsprodTTVCByIdRT " + Oper + " feat " + feat);
                    if (((feat == null) || (feat.length() == 0)) || (feat.indexOf(":testfailed") != -1)) {
                        // disabled this Acc Obj
                        int type = ssnsAccObj.getType();
                        String name = ssnsAccObj.getName();
                        int status = ssnsAccObj.getStatus();
                        type = type + 1; // increate error count

                        this.getSsnsDataImp().updatSsnsAccNameStatusTypeById(ssnsAccObj.getId(), name, status, type);
                    } else {
                        String name = ssnsAccObj.getName();
                        int type = ssnsAccObj.getType();
                        int status = ssnsAccObj.getStatus();
                        if (type > 0) {
                            type = 0; // increate error count
                            this.getSsnsDataImp().updatSsnsAccNameStatusTypeById(ssnsAccObj.getId(), name, status, type);
                        }
                    }
                }
                return outputList;
            }
        }
        return null;
    }

    public ArrayList<String> testSsnsprodPRocessByIdRT(String EmailUserName, String IDSt, String PIDSt, String prod, String Oper) {
        if (prod.equals(SsnsService.APP_APP)) {
            return this.testSsnsprodAppByIdRT(EmailUserName, IDSt, PIDSt, prod, Oper);
        } else if (prod.equals(SsnsService.APP_TTVC)) {
            return this.testSsnsprodTTVCByIdRT(EmailUserName, IDSt, PIDSt, prod, Oper);
        } else if (prod.equals(SsnsService.APP_WIFI)) {
            return this.testSsnsprodWifiByIdRT(EmailUserName, IDSt, PIDSt, prod, Oper);
        } else if (prod.equals(SsnsService.APP_PRODUCT)) {
            return testSsnsprodByIdRT(EmailUserName, IDSt, PIDSt, prod, Oper);
        }
        return null;
    }

    public String testSsnsprodWifiByIdRTTtest(String EmailUserName, String IDSt, String PIDSt, String prod, String Oper) {
        if (getServerObj().isSysMaintenance() == true) {
            return "";
        }
        CustomerObj custObj = getAccountImp().getCustomerPassword(EmailUserName, null);
        if (custObj == null) {
            return "";
        }
        if (IDSt != null) {
            if (IDSt.equals(custObj.getId() + "") != true) {
                return "";
            }
        }
        ArrayList<SsnsAcc> ssnsAccObjList = getSsnsDataImp().getSsnsAccObjListByID(prod, PIDSt);
        if (ssnsAccObjList != null) {
            if (ssnsAccObjList.size() > 0) {
                SsnsAcc accObj = (SsnsAcc) ssnsAccObjList.get(0);
                ArrayList<String> response = new ArrayList();
                SsnsService ss = new SsnsService();

                if (Oper.equals(SsnsService.WI_GetDevice) || Oper.equals(SsnsService.WI_GetDeviceStatus)) {
                    String oper = accObj.getRet();
                    String featRet = ss.TestFeatureSsnsProdWifi(accObj, response, oper);
                    if (response != null) {
                        if (response.size() > 3) {
                            String feat = response.get(0);
                            String execSt = response.get(2);
                            execSt = ServiceAFweb.replaceAll("elapsedTime:", "", execSt);
                            long exec = Long.parseLong(execSt);
                            String passSt = R_FAIL;
                            if (feat.equals(accObj.getName())) {
                                passSt = R_PASS;
                            } else {
                                passSt = R_PASS;
                                String[] featL = feat.split(":");
                                String[] nameL = accObj.getName().split(":");
                                if ((featL.length > 4) && (nameL.length > 4)) {
                                    if (!featL[2].equals(nameL[2])) {
                                        passSt = R_FAIL;
                                    }
                                    if (!featL[3].equals(nameL[3])) {
                                        passSt = R_FAIL;
                                    }
                                    if (!featL[4].equals(nameL[4])) {
                                        passSt = R_FAIL;
                                    }
                                } else if ((featL.length > 3) && (nameL.length > 3)) {
                                    if (!featL[2].equals(nameL[2])) {
                                        passSt = R_FAIL;
                                    }
                                    if (!featL[3].equals(nameL[3])) {
                                        passSt = R_FAIL;
                                    }
                                }
                            }
                            passSt = feat + ":" + passSt;
                            return passSt;
                        }
                    }
                }
            }
        }
        return "";
    }

    public ArrayList<String> testSsnsprodWifiByIdRT(String EmailUserName, String IDSt, String PIDSt, String prod, String Oper) {
        if (getServerObj().isSysMaintenance() == true) {
            return null;
        }
        CustomerObj custObj = getAccountImp().getCustomerPassword(EmailUserName, null);
        if (custObj == null) {
            return null;
        }
        if (IDSt != null) {
            if (IDSt.equals(custObj.getId() + "") != true) {
                return null;
            }
        }
        ArrayList<SsnsAcc> ssnsAccObjList = getSsnsDataImp().getSsnsAccObjListByID(prod, PIDSt);
        if (ssnsAccObjList != null) {
            if (ssnsAccObjList.size() > 0) {
                SsnsAcc ssnsAccObj = (SsnsAcc) ssnsAccObjList.get(0);
                ArrayList<String> outputList = new ArrayList();
                SsnsService ss = new SsnsService();
                String feat = "";

                if (Oper.equals(SsnsService.WI_GetDevice) || Oper.equals(SsnsService.WI_GetDeviceStatus)) {
                    feat = ss.TestFeatureSsnsProdWifi(ssnsAccObj, outputList, Oper);
//                    logger.info("> getSsnsprodAppByIdRT " + Oper + " feat " + feat);
                    if (((feat == null) || (feat.length() == 0)) || (feat.indexOf(":testfailed") != -1)) {
                        // disabled this Acc Obj
                        int type = ssnsAccObj.getType();
                        String name = ssnsAccObj.getName();
                        int status = ssnsAccObj.getStatus();
                        type = type + 1; // increate error count

                        this.getSsnsDataImp().updatSsnsAccNameStatusTypeById(ssnsAccObj.getId(), name, status, type);
                    } else {
                        String name = ssnsAccObj.getName();
                        int type = ssnsAccObj.getType();
                        int status = ssnsAccObj.getStatus();
                        if (type > 0) {
                            type = 0; // clear error count
                            this.getSsnsDataImp().updatSsnsAccNameStatusTypeById(ssnsAccObj.getId(), name, status, type);
                        }
                    }
                }
                return outputList;
            }
        }
        return null;
    }

    public ArrayList<String> testSsnsprodAppByIdRT(String EmailUserName, String IDSt, String PIDSt, String prod, String Oper) {
        if (getServerObj().isSysMaintenance() == true) {
            return null;
        }
        CustomerObj custObj = getAccountImp().getCustomerPassword(EmailUserName, null);
        if (custObj == null) {
            return null;
        }
        if (IDSt != null) {
            if (IDSt.equals(custObj.getId() + "") != true) {
                return null;
            }
        }
        ArrayList<SsnsAcc> ssnsAccObjList = getSsnsDataImp().getSsnsAccObjListByID(prod, PIDSt);
        if (ssnsAccObjList != null) {
            if (ssnsAccObjList.size() > 0) {
                SsnsAcc ssnsAccObj = (SsnsAcc) ssnsAccObjList.get(0);
                ArrayList<String> outputList = new ArrayList();
                SsnsService ss = new SsnsService();
                String feat = "";

                if (Oper.equals(SsnsService.APP_GET_APP) || Oper.equals(SsnsService.APP_GET_TIMES)) {
                    feat = ss.TestFeatureSsnsProdApp(ssnsAccObj, outputList, Oper);
//                    logger.info("> getSsnsprodAppByIdRT " + Oper + " feat " + feat);
                    if (((feat == null) || (feat.length() == 0)) || (feat.indexOf(":testfailed") != -1)) {
                        // disabled this Acc Obj
                        int type = ssnsAccObj.getType();
                        String name = ssnsAccObj.getName();
                        int status = ssnsAccObj.getStatus();
                        type = type + 1; // increate error count

                        this.getSsnsDataImp().updatSsnsAccNameStatusTypeById(ssnsAccObj.getId(), name, status, type);
                    } else {
                        String name = ssnsAccObj.getName();
                        int type = ssnsAccObj.getType();
                        int status = ssnsAccObj.getStatus();
                        if (type > 0) {
                            type = 0; // clear error count
                            this.getSsnsDataImp().updatSsnsAccNameStatusTypeById(ssnsAccObj.getId(), name, status, type);
                        }
                    }
                }
                return outputList;
            }
        }
        return null;
    }

    public String testSsnsprodByIdRTtest(String EmailUserName, String IDSt, String PIDSt, String prod, String ProdOper) {
        if (getServerObj().isSysMaintenance() == true) {
            return null;
        }
        CustomerObj custObj = getAccountImp().getCustomerPassword(EmailUserName, null);
        if (custObj == null) {
            return null;
        }
        if (IDSt != null) {
            if (IDSt.equals(custObj.getId() + "") != true) {
                return null;
            }
        }

        ArrayList<SsnsAcc> ssnsAccObjList = getSsnsDataImp().getSsnsAccObjListByID(prod, PIDSt);
        if (ssnsAccObjList != null) {
            if (ssnsAccObjList.size() > 0) {
                SsnsAcc accObj = (SsnsAcc) ssnsAccObjList.get(0);
                ArrayList<String> response = new ArrayList();
                SsnsService ss = new SsnsService();

                if (prod.equals(SsnsService.APP_PRODUCT)) {
                    String oper = accObj.getRet();

                    String featRet = ss.TestFeatureSsnsProductInventory(accObj, response, oper);
                    if (response != null) {
                        if (response.size() > 3) {
                            String feat = response.get(0);
                            String execSt = response.get(2);
                            execSt = ServiceAFweb.replaceAll("elapsedTime:", "", execSt);
                            long exec = Long.parseLong(execSt);
                            String passSt = R_FAIL;
                            if (feat.equals(accObj.getName())) {
                                passSt = R_PASS;
                            } else {
                                passSt = R_PASS;
                                String[] featL = feat.split(":");
                                String[] nameL = accObj.getName().split(":");
                                if ((featL.length > 4) && (nameL.length > 4)) {
                                    if (!featL[2].equals(nameL[2])) {
                                        passSt = R_FAIL;
                                    }
                                    if (!featL[3].equals(nameL[3])) {
                                        passSt = R_FAIL;
                                    }
                                    if (!featL[4].equals(nameL[4])) {
                                        passSt = R_FAIL;
                                    }
                                } else if ((featL.length > 3) && (nameL.length > 3)) {
                                    if (!featL[2].equals(nameL[2])) {
                                        passSt = R_FAIL;
                                    }
                                    if (!featL[3].equals(nameL[3])) {
                                        passSt = R_FAIL;
                                    }
                                }
                            }
                            passSt = feat + ":" + passSt;
                            return passSt;
                        }
                    }
                }
            }
        }
        return "";
    }

    public ArrayList<String> testSsnsprodByIdRT(String EmailUserName, String IDSt, String PIDSt, String prod, String ProdOper) {
        if (getServerObj().isSysMaintenance() == true) {
            return null;
        }
        CustomerObj custObj = getAccountImp().getCustomerPassword(EmailUserName, null);
        if (custObj == null) {
            return null;
        }
        if (IDSt != null) {
            if (IDSt.equals(custObj.getId() + "") != true) {
                return null;
            }
        }
        ArrayList<SsnsAcc> ssnsAccObjList = getSsnsDataImp().getSsnsAccObjListByID(prod, PIDSt);
        if (ssnsAccObjList != null) {
            if (ssnsAccObjList.size() > 0) {
                SsnsAcc ssnsAccObj = (SsnsAcc) ssnsAccObjList.get(0);
                ArrayList<String> outputList = new ArrayList();
                SsnsService ss = new SsnsService();
                String feat = "";
                if (prod.equals(SsnsService.APP_PRODUCT)) {
                    String oper = ssnsAccObj.getRet();

                    feat = ss.TestFeatureSsnsProductInventory(ssnsAccObj, outputList, oper);
                    if (((feat == null) || (feat.length() == 0)) || (feat.indexOf(":testfailed") != -1)) {
                        // disabled this Acc Obj
                        int type = ssnsAccObj.getType();
                        String name = ssnsAccObj.getName();
                        int status = ssnsAccObj.getStatus();
                        type = type + 1; // increate error count

                        this.getSsnsDataImp().updatSsnsAccNameStatusTypeById(ssnsAccObj.getId(), name, status, type);
                    } else {
                        String name = ssnsAccObj.getName();
                        int type = ssnsAccObj.getType();
                        int status = ssnsAccObj.getStatus();
                        if (type > 0) {
                            type = 0; // clear error count
                            this.getSsnsDataImp().updatSsnsAccNameStatusTypeById(ssnsAccObj.getId(), name, status, type);
                        }
                    }

                } else {

                }

                return outputList;
            }
        }
        return null;
    }

    ////////////////////////////
    public ArrayList getExpiredCustomerList(int length) {
        ArrayList result = null;
        if (getServerObj().isSysMaintenance() == true) {
            return null;
        }
        {
            result = getAccountImp().getExpiredCustomerList(length);
        }
        return result;
    }

    public ArrayList getCustomerList(int length) {
        ArrayList result = null;
        if (getServerObj().isSysMaintenance() == true) {
            return null;
        }
        {
            result = getAccountImp().getCustomerList(length);
        }

        return result;
    }

    public int SystemUpdateSQLList(ArrayList<String> SQLlist) {
        if (getServerObj().isSysMaintenance() == true) {
            return 0;
        }
        if (checkCallRemoteMysql() == true) {
            RequestObj sqlObj = new RequestObj();
            sqlObj.setCmd(ServiceAFweb.UpdateSQLList + "");
            String st;
            try {
                st = new ObjectMapper().writeValueAsString(SQLlist);
                sqlObj.setReq(st);
                RequestObj sqlObjresp = SystemSQLRequest(sqlObj);
                String output = sqlObjresp.getResp();
                if (output == null) {
                    return 0;

                }
                int result = new ObjectMapper().readValue(output, Integer.class);
                return result;
            } catch (Exception ex) {
                logger.info("> SystemUpdateSQLList exception " + ex.getMessage());
            }
            return 0;
        }
        return getSsnsDataImp().updateSQLArrayList(SQLlist);
    }

    public ArrayList<SsnsData> SystemSsnsDataObj(String BPnameTR) {
        if (getServerObj().isSysMaintenance() == true) {
            return null;
        }
        if (checkCallRemoteMysql() == true) {
            RequestObj sqlObj = new RequestObj();
            sqlObj.setCmd(ServiceAFweb.SsnsDataObj + "");
            String st;
            try {
                sqlObj.setReq(BPnameTR + "");
                RequestObj sqlObjresp = SystemSQLRequest(sqlObj);
                String output = sqlObjresp.getResp();
                if (output == null) {
                    return null;
                }
                if (output.equals(ConstantKey.nullSt)) {
                    return null;
                }
                ArrayList<SsnsData> trArray = null;

                SsnsData[] arrayItem = new ObjectMapper().readValue(output, SsnsData[].class);
                List<SsnsData> listItem = Arrays.<SsnsData>asList(arrayItem);
                trArray = new ArrayList<SsnsData>(listItem);
                return trArray;
            } catch (Exception ex) {
                logger.info("> SystemSsnsDataObj exception " + ex.getMessage());
            }
            return null;
        }
        return getSsnsDataImp().getSsnsDataObjList(BPnameTR, 0);
    }

    public ArrayList<SsnsData> SystemSsnstDataObjType(String BPname, int type, long updatedatel) {
        if (getServerObj().isSysMaintenance() == true) {
            return null;
        }
        if (checkCallRemoteMysql() == true) {
            RequestObj sqlObj = new RequestObj();
            sqlObj.setCmd(ServiceAFweb.SsnsDataObjType + "");
            String st;
            try {
                sqlObj.setReq(BPname + "");
                sqlObj.setReq1(type + "");
                sqlObj.setReq2(updatedatel + "");
                RequestObj sqlObjresp = SystemSQLRequest(sqlObj);
                String output = sqlObjresp.getResp();
                if (output == null) {
                    return null;
                }
                if (output.equals(ConstantKey.nullSt)) {
                    return null;
                }
                ArrayList<SsnsData> trArray = null;

                SsnsData[] arrayItem = new ObjectMapper().readValue(output, SsnsData[].class);
                List<SsnsData> listItem = Arrays.<SsnsData>asList(arrayItem);
                trArray = new ArrayList<SsnsData>(listItem);
                return trArray;
            } catch (Exception ex) {
                logger.info("> SystemSsnstDataObjType exception " + ex.getMessage());
            }
            return null;
        }
        return getSsnsDataImp().getSsnsDataObjList(BPname, type, updatedatel);
    }

    public String SystemSQLquery(String SQL) {
//        if (getServerObj().isSysMaintenance() == true) {
//            return "";
//        }
        if (checkCallRemoteMysql() == true) {
            RequestObj sqlObj = new RequestObj();
            sqlObj.setCmd(ServiceAFweb.AllSQLquery + "");

            try {
                sqlObj.setReq(SQL);
                RequestObj sqlObjresp = SystemSQLRequest(sqlObj);
                String output = sqlObjresp.getResp();
                if (output == null) {
                    return "";
                }

                return output;
            } catch (Exception ex) {
                logger.info("> SystemSQLquery exception " + ex.getMessage());
            }
            return "";
        }
        return getAccountImp().getAllSQLquery(SQL);
    }

    public int SystemuUpdateTransactionOrder(ArrayList<String> transSQL) {
        if (getServerObj().isSysMaintenance() == true) {
            return 0;
        }
        if (checkCallRemoteMysql() == true) {
            RequestObj sqlObj = new RequestObj();
            sqlObj.setCmd(ServiceAFweb.UpdateTransactionOrder + "");
            String st;
            try {
                st = new ObjectMapper().writeValueAsString(transSQL);
                sqlObj.setReq(st);
                RequestObj sqlObjresp = SystemSQLRequest(sqlObj);
                String output = sqlObjresp.getResp();
                if (output == null) {
                    return 0;

                }
                int result = new ObjectMapper().readValue(output, Integer.class
                );
                return result;
            } catch (Exception ex) {
                logger.info("> SystemuUpdateTransactionOrder exception " + ex.getMessage());
            }
            return 0;
        }
        return getAccountImp().updateTransactionOrder(transSQL);
    }

    public ArrayList<CommObj> getCommByCustomerAccountID(String EmailUserName, String Password, String AccountIDSt) {
        if (getServerObj().isSysMaintenance() == true) {
            return null;
        }

        NameObj nameObj = new NameObj(EmailUserName);
        String UserName = nameObj.getNormalizeName();
        try {
            int accountid = Integer.parseInt(AccountIDSt);
            return getAccountImp().getCommByCustomerAccountID(UserName, Password, accountid);
        } catch (Exception e) {
        }
        return null;

    }

    public int addCommByCustomerAccountID(String EmailUserName, String Password, String AccountIDSt, String data) {
        if (getServerObj().isSysMaintenance() == true) {
            return 0;
        }

        NameObj nameObj = new NameObj(EmailUserName);
        String UserName = nameObj.getNormalizeName();
        try {
            int accountid = Integer.parseInt(AccountIDSt);
            return getAccountImp().addCommByCustomerAccountID(UserName, Password, accountid, data);
        } catch (Exception e) {
        }
        return 0;
    }

    public int removeCustCommByID(String EmailUserName, String Password, String AccountIDSt, String IDSt) {
        if (getServerObj().isSysMaintenance() == true) {
            return 0;
        }

        NameObj nameObj = new NameObj(EmailUserName);
        String UserName = nameObj.getNormalizeName();
        try {
            int accountid = Integer.parseInt(AccountIDSt);
            int id = Integer.parseInt(IDSt);
            return getAccountImp().removeCustAccountCommByID(UserName, Password, accountid, id);
        } catch (Exception e) {
        }
        return 0;
    }

    ////////////////////////
    public ArrayList getAllLock() {

        ArrayList result = null;
        result = getSsnsDataImp().getAllLock();
        return result;
    }

    public int setRenewLock(String symbol_acc, int type) {

        if (getServerObj().isSysMaintenance() == true) {
            return 0;
        }

        Calendar dateNow = TimeConvertion.getCurrentCalendar();
        long lockDateValue = dateNow.getTimeInMillis();

        String name = symbol_acc;
        return getSsnsDataImp().setRenewLock(name, type, lockDateValue);
    }

    public int setLockNameProcess(String name, int type, long lockdatel, String comment) {
        int resultLock = setLockName(name, type, lockdatel, comment);
        // DB will enusre the name in the lock is unique and s
        RandomDelayMilSec(200);
        AFLockObject lock = getLockName(name, type);
        if (lock != null) {
            if (lock.getLockdatel() == lockdatel) {
                return 1;
            }
        }

        return 0;
    }

    public AFLockObject getLockName(String symbol_acc, int type) {
        if (getServerObj().isSysMaintenance() == true) {
            return null;
        }

        String name = symbol_acc;
        name = name.toUpperCase();
        return getSsnsDataImp().getLockName(name, type);
    }

    public int setLockName(String symbol_acc, int type, long lockdatel, String comment) {
        if (getServerObj().isSysMaintenance() == true) {
            return 0;
        }

        String name = symbol_acc;
        name = name.toUpperCase();
        return getSsnsDataImp().setLockName(name, type, lockdatel, comment);
    }

    public int removeNameLock(String symbol_acc, int type) {
        if (getServerObj().isSysMaintenance() == true) {
            return 0;
        }

        String name = symbol_acc;
        name = name.toUpperCase();
        return getSsnsDataImp().removeLock(name, type);

    }
//////////////////

    public int updateCustAllStatus(String customername,
            String substatusSt) {
        if (getServerObj().isSysMaintenance() == true) {
            return 0;
        }

        NameObj nameObj = new NameObj(customername);
        String UserName = nameObj.getNormalizeName();
        try {

            int substatus = Integer.parseInt(substatusSt);
            return getAccountImp().updateCustAllStatus(UserName, substatus);

        } catch (Exception e) {
        }
        return 0;
    }

    public int updateCustStatusSubStatus(String customername, String statusSt, String substatusSt) {
        if (getServerObj().isSysMaintenance() == true) {
            return 0;
        }

        int status;
        int substatus;
        try {
            status = Integer.parseInt(statusSt);
            substatus = Integer.parseInt(substatusSt);
        } catch (NumberFormatException e) {
            return 0;
        }
        CustomerObj custObj = getAccountImp().getCustomerStatus(customername, null);
        custObj.setStatus(status);
        custObj.setSubstatus(substatus);
        return getAccountImp().updateCustStatus(custObj);
    }

    public WebStatus serverPing() {
        WebStatus msg = new WebStatus();

        msg.setResult(true);
        msg.setResponse("Server Ready");
        ArrayList serverlist = getServerList();
        if (serverlist == null) {
            msg.setResult(false);
            msg.setResponse("WebServer down");
            return msg;
        }
        if (serverlist.size() == 1) {
            ServerObj serverObj = (ServerObj) serverlist.get(0);
            if (serverObj.isLocalDBservice() == false) {
                msg.setResult(false);
                msg.setResponse("MasterDBServer down");
                return msg;
            }
        }
        for (int i = 0; i < serverlist.size(); i++) {
            ServerObj serverObj = (ServerObj) serverlist.get(i);
            if (serverObj.isSysMaintenance() == true) {
                msg.setResult(false);
                msg.setResponse("Server in Maintenance");
                break;
            }
        }
        return msg;
    }

    public String SystemRemoteUpdateMySQLList(String SQL) {
//        if (getServerObj().isSysMaintenance() == true) {
//            return "";
//        }

        String st = SQL;
        String[] sqlList = st.split("~");
        for (int i = 0; i < sqlList.length; i++) {
            String sqlCmd = sqlList[i];
            int ret = getSsnsDataImp().updateRemoteMYSQL(sqlCmd);
        }
        return ("" + sqlList.length);
    }

    public String SystemRemoteUpdateMySQL(String SQL) {
//        if (getServerObj().isSysMaintenance() == true) {
//            return "";
//        }

        return getSsnsDataImp().updateRemoteMYSQL(SQL) + "";
    }

    public String SystemRemoteGetMySQL(String SQL) {
//        if (getServerObj().isSysMaintenance() == true) {
//            return "";
//        }

        return getSsnsDataImp().getRemoteMYSQL(SQL);
    }
///////////////////////////
//    cannot autowire Could not autowire field:
    public static final int AllName = 200; //"1";
    public static final int AllSymbol = 201; //"1";
    public static final int AllId = 202; //"1";
    public static final int AllUserName = 203; //"1";

    public static final int AllLock = 2; //"2";

    public static final int AllCustomer = 6; //"6";

    public static final int RemoteGetMySQL = 9; //"9";
    public static final int RemoteUpdateMySQL = 10; //"10";    
    public static final int RemoteUpdateMySQLList = 11; //"11";   

    public static final int AllSQLquery = 14; //"14"; 
    public static final int AllSsnsData = 15; //"15";
    public static final int AllComm = 16; //"16";

    ////////
    public static final int UpdateSQLList = 101; //"101";

    public static final int UpdateTransactionOrder = 108; //"108";

    public static final int AddTransactionOrder = 113; //"113"; 

    public static final int SsnsDataObj = 120; //"120";     
    public static final int SsnsDataObjType = 121; //"120";   

    public RequestObj SystemSQLRequest(RequestObj sqlObj) {

        String st = "";
        String nameST = "";
        int ret;

        ArrayList<String> nameList = null;

        try {
            String typeCd = sqlObj.getCmd();
            int type = Integer.parseInt(typeCd);

            switch (type) {
                case AllName:
                    nameList = getSsnsDataImp().getAllNameSQL(sqlObj.getReq());
                    nameST = new ObjectMapper().writeValueAsString(nameList);
                    sqlObj.setResp(nameST);
                    return sqlObj;

                case AllId:
                    nameList = getAccountImp().getAllIdSQL(sqlObj.getReq());
                    nameST = new ObjectMapper().writeValueAsString(nameList);
                    sqlObj.setResp(nameST);
                    return sqlObj;
                case AllUserName:
                    nameList = getAccountImp().getAllUserNameSQL(sqlObj.getReq());
                    nameST = new ObjectMapper().writeValueAsString(nameList);
                    sqlObj.setResp(nameST);
                    return sqlObj;
                case AllLock:
                    nameST = getSsnsDataImp().getAllLockDBSQL(sqlObj.getReq());
                    sqlObj.setResp(nameST);
                    return sqlObj;

                case AllSsnsData:
                    nameST = getSsnsDataImp().getAllSsnsDataDBSQL(sqlObj.getReq(), 0);
                    sqlObj.setResp(nameST);
                    return sqlObj;
                case AllCustomer:
                    nameST = getAccountImp().getAllCustomerDBSQL(sqlObj.getReq());
                    sqlObj.setResp(nameST);
                    return sqlObj;

                case RemoteGetMySQL:  //RemoteGetMySQL = 9; //"9"; 
                    st = sqlObj.getReq();
                    nameST = getSsnsDataImp().getRemoteMYSQL(st);
                    sqlObj.setResp("" + nameST);
                    return sqlObj;

                case RemoteUpdateMySQL:  //RemoteUpdateMySQL = 10; //"10"; 
                    st = sqlObj.getReq();
                    ret = getSsnsDataImp().updateRemoteMYSQL(st);
                    sqlObj.setResp("" + ret);

                    return sqlObj;
                case RemoteUpdateMySQLList:  //RemoteUpdateMySQLList = 11; //"11"; 
                    st = sqlObj.getReq();
                    String[] sqlList = st.split("~");
                    for (int i = 0; i < sqlList.length; i++) {
                        String sqlCmd = sqlList[i];
                        ret = getSsnsDataImp().updateRemoteMYSQL(sqlCmd);
                    }
                    sqlObj.setResp("" + sqlList.length);
                    return sqlObj;

                case AllSQLquery: //AllSQLreq = 14; //"14";  
                    nameST = getAccountImp().getAllSQLquery(sqlObj.getReq());
                    sqlObj.setResp(nameST);
                    return sqlObj;

                case AllComm: //AllComm = 16; //"16";
                    nameST = getAccountImp().getAllCommDBSQL(sqlObj.getReq());
                    sqlObj.setResp(nameST);
                    return sqlObj;

/////////////////////////
                case UpdateSQLList:  //UpdateSQLList = "101";
                    ArrayList<String> SQLArray = new ArrayList();

                    try {
                        SQLArray = new ObjectMapper().readValue(sqlObj.getReq(), ArrayList.class);
                        int result = getSsnsDataImp().updateSQLArrayList(SQLArray);
                        sqlObj.setResp("" + result);

                    } catch (Exception ex) {
                    }
                    return sqlObj;

                case UpdateTransactionOrder:  //UpdateTransactionOrder = "108";
                    try {
                        st = sqlObj.getReq();
                        ArrayList transSQL = new ObjectMapper().readValue(st, ArrayList.class);
                        ret = this.getAccountImp().updateTransactionOrder(transSQL);
                        sqlObj.setResp("" + ret);

                    } catch (Exception ex) {
                    }
                    return sqlObj;

                case SsnsDataObj: //SsnsDataObj = 120; //"120";      
                    try {
                        String BPname = sqlObj.getReq();
                        ArrayList<SsnsData> retArray = getSsnsDataImp().getSsnsDataObjList(BPname, 0);
                        nameST = new ObjectMapper().writeValueAsString(retArray);
                        sqlObj.setResp("" + nameST);

                    } catch (Exception ex) {
                    }
                    return sqlObj;

                case SsnsDataObjType: //SsnsDataObjType = 121; //"121";        
                    try {
                        String BPname = sqlObj.getReq();

                        String stockID = sqlObj.getReq1();
                        int stockId121 = Integer.parseInt(stockID);

                        String updatedateSt = sqlObj.getReq2();
                        long updatedatel = Long.parseLong(updatedateSt);

                        ArrayList<SsnsData> retArray = getSsnsDataImp().getSsnsDataObjList(BPname, stockId121, updatedatel);
                        nameST = new ObjectMapper().writeValueAsString(retArray);
                        sqlObj.setResp("" + nameST);
                    } catch (Exception ex) {
                    }
                    return sqlObj;

                /////
            }
        } catch (Exception ex) {
            logger.info("> SystemSQLRequest exception " + sqlObj.getCmd() + " - " + ex.getMessage());
        }
        return null;
    }

    ///// Restore DB need the following
    ////  SystemStop
    ////  SystemCleanDBData
    ////  SystemUploadDBData
    ///// Restore DB need the following    
    public String SystemStop() {
        boolean retSatus = true;
        serverObj.setSysMaintenance(true);

        return "sysMaintenance " + retSatus;
    }

    public String SystemCleanDBData() {
        boolean retSatus = false;
        if (getServerObj().isLocalDBservice() == true) {
            serverObj.setSysMaintenance(true);
            retSatus = getSsnsDataImp().cleanSsnsDataDB();
        }
        return "" + retSatus;
    }

    public String SystemReOpenData() {
        int retSatus = 0;
        if (getServerObj().isLocalDBservice() == true) {
            retSatus = getSsnsDataImp().updateSsnsDataAllOpenStatus();
            logger.info("> SystemReOpenData .. done");
        }
        return "" + retSatus;
    }

    public String SystemClearLock() {
        int retSatus = 0;
        if (getServerObj().isLocalDBservice() == true) {
            retSatus = getSsnsDataImp().deleteAllLock();
        }
        return "" + retSatus;
    }

    public String SystemRestDBData() {
        boolean retSatus = false;
        if (getServerObj().isLocalDBservice() == true) {
            // make sure the system is stopped first
            retSatus = getSsnsDataImp().restSsnsDataDB();
        }
        return "" + retSatus;
    }

    public String SystemStart() {
        boolean retSatus = true;
        serverObj.setSysMaintenance(false);
        serverObj.setTimerInit(false);
        serverObj.setTimerQueueCnt(0);
        serverObj.setTimerCnt(0);
        return "sysMaintenance " + retSatus;
    }

    public int testDBData() {
        logger.info(">testDBData ");
        int retSatus = getSsnsDataImp().testSsnsDataDB();
        return retSatus;
    }

    public int InitDBData() {
        logger.info(">InitDBData ");
        // 0 - new db, 1 - db already exist, -1 db error
        int retSatus = getSsnsDataImp().initSsnsDataDB();

        if (retSatus >= 0) {
            logger.info(">InitDB Usert ");
            CustomerObj newUser = new CustomerObj();
            newUser.setUsername(CKey.ADMIN_USERNAME);
            newUser.setPassword("abc123");
            newUser.setType(CustomerObj.INT_ADMIN_USER);
            getAccountImp().addCustomer(newUser);

            newUser = new CustomerObj();
            newUser.setUsername("admin@admin.com");
            newUser.setPassword("abc123");
            newUser.setType(CustomerObj.INT_ADMIN_USER);
            getAccountImp().addCustomer(newUser);

            newUser = new CustomerObj();
            newUser.setUsername("GUEST");
            newUser.setPassword("guest");
            newUser.setType(CustomerObj.INT_GUEST_USER);
            getAccountImp().addCustomer(newUser);

            newUser = new CustomerObj();
            newUser.setUsername("EDDY");
            newUser.setPassword("pass");
            newUser.setType(CustomerObj.INT_CLIENT_BASIC_USER);
            getAccountImp().addCustomer(newUser);
        }
        return retSatus;

    }

    public void InitSystemFund(String portfolio) {

    }

    public void InitSystemData() {
        logger.info(">InitDB InitSystemData Stock to account ");

    }

    public static String getSQLLengh(String sql, int length) {
        //https://www.petefreitag.com/item/59.cfm
        //SELECT TOP 10 column FROM table - Microsoft SQL Server
        //SELECT column FROM table LIMIT 10 - PostgreSQL and MySQL
        //SELECT column FROM table WHERE ROWNUM <= 10 - Oracle
        if (length != 0) {
            if (length == 1) {
                sql += " limit 1 ";
            } else {
                sql += " limit " + length + " ";
            }
        }
        return sql;
    }

////////////////////////////////
    @Autowired
    public void setDataSource(DataSource dataSource) {
        //testing
        WebAppConfig webConfig = new WebAppConfig();
        dataSource = webConfig.dataSource();
        //testing        
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.dataSource = dataSource;
    }

    public SsnsDataImp getSsnsDataImp() {
//    private StockImp getStockImp() {
        return ssnsDataImp;
    }

    public void setSsnsDataImp(SsnsDataImp stockImp) {
        this.ssnsDataImp = stockImp;
    }

    /**
     * @return the accountImp
     */
    public AccountImp getAccountImp() {
        return accountImp;
    }

    /**
     * @param accountImp the accountImp to set
     */
    public void setAccountImp(AccountImp accountImp) {
        this.accountImp = accountImp;
    }

}
