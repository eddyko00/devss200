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
                    logger.info("> Debug end ");
                }

            }
        }
        if (CKey.UI_ONLY == true) {
//            if ((getServerObj().getProcessTimerCnt() % 5) == 0) {
//                //// process monitor
//                SsnsRegression regression = new SsnsRegression();
//                regression.processMonitorTesting(this);
//            }
            return;
        }


        try {
            if (getServerObj().getProcessTimerCnt() < 0) {
                getServerObj().setProcessTimerCnt(0);
            }
            getServerObj().setProcessTimerCnt(getServerObj().getProcessTimerCnt() + 1);

            if (getEnv.checkLocalPC() == true) {
                if (CKey.NN_DEBUG == true) {
                    if ((getServerObj().getProcessTimerCnt() % 3) == 0) {
                        //10 Sec * 5 ~ 1 minutes
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

            } else if ((getServerObj().getProcessTimerCnt() % 11) == 0) {

            } else if ((getServerObj().getProcessTimerCnt() % 7) == 0) {
                //////require to save memory
                System.gc();
                //////require to save memory

            } else if ((getServerObj().getProcessTimerCnt() % 5) == 0) {

            } else if ((getServerObj().getProcessTimerCnt() % 3) == 0) {

            } else if ((getServerObj().getProcessTimerCnt() % 2) == 0) {

            } else {

            }

        } catch (Exception ex) {
            logger.info("> processTimer Exception" + ex.getMessage());
        }

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

/////////////////////////////////
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
