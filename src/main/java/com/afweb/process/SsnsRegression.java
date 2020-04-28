/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.afweb.process;

import com.afweb.model.*;

import com.afweb.model.ssns.*;
import com.afweb.service.ServiceAFweb;
import static com.afweb.service.ServiceAFweb.AFSleep;

import com.afweb.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Logger;

/**
 *
 * @author koed
 */
public class SsnsRegression {

    /**
     * @return the ssnsDataImp
     */
    public SsnsDataImp getSsnsDataImp() {
        return ssnsDataImp;
    }

    /**
     * @param ssnsDataImp the ssnsDataImp to set
     */
    public void setSsnsDataImp(SsnsDataImp ssnsDataImp) {
        this.ssnsDataImp = ssnsDataImp;
    }
    
    private SsnsDataImp ssnsDataImp = new SsnsDataImp();
    protected static Logger logger = Logger.getLogger("SsnsRegression");
    
    public static String R_PASS = "pass";
    public static String R_FAIL = "fail";
    
    public static String REPORT_USER = "user";
    public static String REPORT_MOMITOR = "monitor";
    public static String REPORT_REPORT = "report";
    public static String REPORT_TESE_CASE = "test";
    
    public int stopMonitor(ServiceAFweb serviceAFweb, String name) { //CKey.ADMIN_USERNAME) {
        try {
            //creat monitor

            //check if outstanding testing
            SsReport userReportObj = null;
            ArrayList<SsReport> ssReportObjList = getSsnsDataImp().getSsReportObjListByUidDesc(name, REPORT_USER);
            if (ssReportObjList != null) {
                if (ssReportObjList.size() > 0) {
                    userReportObj = ssReportObjList.get(0);
                    userReportObj.setStatus(ConstantKey.COMPLETED);
                    userReportObj.setType(ConstantKey.OPEN);
                    Calendar dateNow = TimeConvertion.getCurrentCalendar();
                    long ctime = dateNow.getTimeInMillis();
                    userReportObj.setUpdatedatel(ctime);
                    userReportObj.setUpdatedatedisplay(new java.sql.Date(ctime));
                    int ret = getSsnsDataImp().updatSsReportDataStatusTypeById(userReportObj.getId(), userReportObj.getData(),
                            userReportObj.getStatus(), userReportObj.getType());
                }
            }
            return 1;
        } catch (Exception ex) {
            logger.info("> stopMonitor Exception " + ex.getMessage());
        }
        return 0;
    }
    
    public int startMonitor(ServiceAFweb serviceAFweb, String name) { //CKey.ADMIN_USERNAME) {
        return startMonitorRegression(serviceAFweb, name, "");
    }
    
    public int startMonitorRegression(ServiceAFweb serviceAFweb, String name, String labURL) { //CKey.ADMIN_USERNAME) {
        try {
            //creat monitor
            ArrayList<String> testIdList = new ArrayList();
            ArrayList<String> testFeatList = new ArrayList();

            //check if outstanding testing
            SsReport userReportObj = null;
            ArrayList<SsReport> ssReportObjList = getSsnsDataImp().getSsReportObjListByUidDesc(name, REPORT_USER);
            if (ssReportObjList != null) {
                if (ssReportObjList.size() > 0) {
                    userReportObj = ssReportObjList.get(0);
                    if (userReportObj.getStatus() == ConstantKey.INITIAL) {
                        
                        return 2; // report already running
                    }
                }
            }
            int exitTest = 500;  // 1000000;
            int added = 0;
            ReportData reportdata = new ReportData();
            ArrayList<String> servList = serviceAFweb.getSsnsprodAll(name, null, 0);
            for (int i = 0; i < servList.size(); i += 2) {
                String servProd = servList.get(i);
                ArrayList<String> featallList = serviceAFweb.getSsnsprodByFeature(name, null, servProd);
                
                for (int j = 0; j < featallList.size(); j += 2) {
                    String featN = featallList.get(j);
                    if (featN.indexOf("failed") != -1) {
                        continue;
                    }
                    testFeatList.add(featN);
                    
                    ArrayList<SsnsAcc> SsnsAcclist = getSsnsDataImp().getSsnsAccObjListByFeature(servProd, featN, 15);
                    if (SsnsAcclist != null) {
                        for (int k = 0; k < SsnsAcclist.size(); k++) {
                            SsnsAcc accObj = SsnsAcclist.get(k);
                            
                            if (accObj.getType() > 10) {  // testfailed will increment this type
                                continue;
                            }
                            testData tObj = new testData();
                            tObj.setAccid(accObj.getId());
                            tObj.setUsername(name);
                            tObj.setTesturl(labURL);
                            String st = new ObjectMapper().writeValueAsString(tObj);
                            st = st.replace('"', '^');
                            testIdList.add(st);
                            added++;
////////////////////////////////////////////////
                            if (added > exitTest) {
                                break;
                            }
////////////////////////////////////////////////
                        }
                    }
                    if (added > exitTest) {
                        break;
                    }
                }
            }
            logger.info("> startMonitor " + name + " TC added " + added);
            testData tObj = new testData();
            tObj.setAccid(0);
            tObj.setType(ConstantKey.INITIAL);
            tObj.setUsername(name);
            tObj.setTesturl("");
            String st = new ObjectMapper().writeValueAsString(tObj);
            st = st.replace('"', '^');
            testIdList.add(0, st);  // add front

            tObj.setAccid(0);
            tObj.setType(ConstantKey.COMPLETED);
            tObj.setUsername(name);
            tObj.setTesturl("");
            st = new ObjectMapper().writeValueAsString(tObj);
            st = st.replace('"', '^');
            testIdList.add(st);
            
            SsReport reportObj = new SsReport();
            reportObj.setName(name);
            reportObj.setStatus(ConstantKey.INITIAL);
            reportObj.setUid(REPORT_REPORT);  // 

//            reportdata.setFeatList(testFeatList);
            reportdata.setTestListObj(testIdList);
            String dataSt = new ObjectMapper().writeValueAsString(reportdata);
            reportObj.setData(dataSt);
            
            Calendar dateNow = TimeConvertion.getCurrentCalendar();
            long ctime = dateNow.getTimeInMillis();
            reportObj.setUpdatedatel(ctime);
            reportObj.setUpdatedatedisplay(new java.sql.Date(ctime));

            // create report
            int ret = getSsnsDataImp().insertSsReportObject(reportObj);

            //update userReportObj to start
            reportdata = new ReportData();
            if (userReportObj == null) {
                userReportObj = new SsReport();
                userReportObj.setName(name);
                userReportObj.setStatus(ConstantKey.INITIAL);
                userReportObj.setType(ConstantKey.OPEN);
                userReportObj.setUid(REPORT_USER);  // 

                reportdata.setFeatList(testFeatList);
//            reportdata.setTestListObj(testIdList);            
                dataSt = new ObjectMapper().writeValueAsString(reportdata);
                userReportObj.setData(dataSt);
                
                String tzid = "America/New_York"; //EDT
                TimeZone tz = TimeZone.getTimeZone(tzid);
                Date d = new Date();
                // timezone symbol (z) included in the format pattern 
                DateFormat format = new SimpleDateFormat("M/dd/yyyy hh:mm a z");
                // format date in target timezone
                format.setTimeZone(tz);
                String ESTdate = format.format(d);
                
                userReportObj.setRet("start:" + ESTdate);
                userReportObj.setUpdatedatel(ctime);
                userReportObj.setUpdatedatedisplay(new java.sql.Date(ctime));

                // create report
                ret = getSsnsDataImp().insertSsReportObject(userReportObj);
                
            } else {
                userReportObj.setStatus(ConstantKey.INITIAL);
                userReportObj.setType(ConstantKey.OPEN);
                userReportObj.setUpdatedatel(ctime);
                
                reportdata.setFeatList(testFeatList);
//            reportdata.setTestListObj(testIdList);       
                dataSt = new ObjectMapper().writeValueAsString(reportdata);
                userReportObj.setData(dataSt);
                
                userReportObj.setUpdatedatedisplay(new java.sql.Date(ctime));
                ret = getSsnsDataImp().updatSsReportDataStatusTypeById(userReportObj.getId(), userReportObj.getData(),
                        userReportObj.getStatus(), userReportObj.getType());
                
            }

            ///// remove report except the last 3
            return 1;
        } catch (Exception ex) {
            logger.info("> startMonitor Exception " + ex.getMessage());
        }
        return 0;
    }
    
    public ArrayList<String> getMoniterNameList(String name) {
        ArrayList<String> reportNameL = new ArrayList();
        try {
            //Start process
            String uid = REPORT_REPORT;
            ArrayList<SsReport> ssReportObjList = getSsnsDataImp().getSsReportObjListByUidDesc(name, uid);
            if (ssReportObjList != null) {
                for (int i = 0; i < ssReportObjList.size(); i++) {
                    SsReport reportObj = ssReportObjList.get(i);
                    if (reportObj.getStatus() == ConstantKey.INITIAL) {
                        reportNameL.add(reportObj.getName());
                    }
                }
                return reportNameL;
            }
        } catch (Exception ex) {
            logger.info("> getMoniterNameList Exception " + ex.getMessage());
        }
        return reportNameL;
    }
    
    public ArrayList<String> getMoniterIDList(SsReport reportObj) {
        try {
            if (reportObj.getStatus() == ConstantKey.INITIAL) {
                String dataSt = reportObj.getData();
                if (dataSt.length() > 0) {
                    ReportData reportdata = new ObjectMapper().readValue(dataSt, ReportData.class);
                    ArrayList<String> testIdList = reportdata.getTestListObj();
                    return testIdList;
                }
            }
        } catch (Exception ex) {
            logger.info("> getMoniterIDList Exception " + ex.getMessage());
        }
        return null;
    }

    ////////////////////////////////
    ArrayList<String> moniterNameArray = new ArrayList();
    
    private ArrayList updateMonitorNameArray() {
        if (moniterNameArray != null && moniterNameArray.size() > 0) {
            return moniterNameArray;
        }
        ArrayList moniterNameArrayTemp = getMoniterNameList("");
        if (moniterNameArrayTemp != null) {
            moniterNameArray = moniterNameArrayTemp;
        }
        return moniterNameArray;
    }
    
    public int processMonitorTesting(ServiceAFweb serviceAFweb) {
        
        updateMonitorNameArray();
        if ((moniterNameArray == null) || (moniterNameArray.size() == 0)) {
            return 0;
        }
        if (moniterNameArray.size() == 0) {
            return 0;
        }
        int result = 0;
        
        try {
            ArrayList<String> idList = new ArrayList();
            
            String name = moniterNameArray.get(0);
            moniterNameArray.remove(0);
//
            SsReport reportReportObj = null;
            String uid = REPORT_REPORT;
            ArrayList<SsReport> ssReportObjList = getSsnsDataImp().getSsReportObjListByUidDesc(name, uid);
            if (ssReportObjList != null) {
                if (ssReportObjList.size() > 0) {
                    reportReportObj = ssReportObjList.get(0);
                }
            }
            
            if (reportReportObj == null) {
                return 0;
            }
            idList = getMoniterIDList(reportReportObj);
            if (idList == null) {
                
            } else {
                
                String LockName = "ETL_MONITOR_" + name;
                Calendar dateNow = TimeConvertion.getCurrentCalendar();
                long lockDateValue = dateNow.getTimeInMillis();
                
                int lockReturn = serviceAFweb.setLockNameProcess(LockName, ConstantKey.MON_LOCKTYPE, lockDateValue, ServiceAFweb.getServerObj().getSrvProjName() + " processMonitorTesting");
                if (CKey.NN_DEBUG == true) {
                    lockReturn = 1;
                }
                if (lockReturn == 0) {
                    return 0;
                }
                
                logger.info("processMonitorTesting for 1 minutes " + name + " size " + idList.size());
                
                long currentTime = System.currentTimeMillis();
                long lockDate1Min = TimeConvertion.addMinutes(currentTime, 1);
                
                SsReport userReportObj = null;
                ArrayList<SsReport> ssReportUserObjList = getSsnsDataImp().getSsReportObjListByUidDesc(name, REPORT_USER);
                if (ssReportUserObjList != null) {
                    if (ssReportUserObjList.size() > 0) {
                        userReportObj = ssReportUserObjList.get(0);
                    }
                }
                while (idList.size() > 0) {
                    currentTime = System.currentTimeMillis();
                    if (lockDate1Min < currentTime) {
                        break;
                    }
                    String tObjSt = idList.get(0);
                    idList.remove(0);
                    
                    execMonitorTesting(serviceAFweb, tObjSt, reportReportObj, userReportObj);
                    AFSleep();
                }
                
                logger.info("processMonitorTesting done " + name + " size " + idList.size());
                if (reportReportObj != null) {
                    String dataSt = reportReportObj.getData();
                    if (dataSt.length() > 0) {
                        ReportData reportdata = new ObjectMapper().readValue(dataSt, ReportData.class);
                        reportdata.setTestListObj(idList);
                        dataSt = new ObjectMapper().writeValueAsString(reportdata);
                        reportReportObj.setData(dataSt);
                    }
                    dateNow = TimeConvertion.getCurrentCalendar();
                    long ctime = dateNow.getTimeInMillis();
                    reportReportObj.setUpdatedatel(ctime);
                    reportReportObj.setUpdatedatedisplay(new java.sql.Date(ctime));
                    int ret = getSsnsDataImp().updatSsReportDataStatusTypeById(reportReportObj.getId(), reportReportObj.getData(),
                            reportReportObj.getStatus(), reportReportObj.getType());

                    // update report statistic
                    reportMoniter(serviceAFweb, name);
                }
                serviceAFweb.removeNameLock(LockName, ConstantKey.MON_LOCKTYPE);
            }
            result = 1;
        } catch (Exception ex) {
            logger.info("> processMonitorTesting Exception " + ex.getMessage());
        }
        
        return result;
        
    }
    
    public void execMonitorTesting(ServiceAFweb serviceAFweb, String tObjSt, SsReport reportReportObj, SsReport usreReportObj) {
        try {
            
            tObjSt = tObjSt.replace('^', '"');
            testData tObj = new ObjectMapper().readValue(tObjSt, testData.class);
            if (tObj.getType() == ConstantKey.INITIAL) {
                // send communication to start
                if (reportReportObj != null) {
                    
                    Calendar dateNow = TimeConvertion.getCurrentCalendar();
                    long ctime = dateNow.getTimeInMillis();
                    reportReportObj.setUpdatedatel(ctime);
                    reportReportObj.setUpdatedatedisplay(new java.sql.Date(ctime));
                    
                    String tzid = "America/New_York"; //EDT
                    TimeZone tz = TimeZone.getTimeZone(tzid);
                    Date d = new Date();
                    // timezone symbol (z) included in the format pattern 
                    DateFormat format = new SimpleDateFormat("M/dd/yyyy hh:mm a z");
                    // format date in target timezone
                    format.setTimeZone(tz);
                    String ESTdate = format.format(d);
                    String retStat = " start:" + ESTdate;
                    reportReportObj.setRet(retStat);
                    logger.info("> execMonitorTesting " + reportReportObj.getName() + " " + reportReportObj.getRet());
                    int ret = getSsnsDataImp().updatSsReportDataStatusTypeRetById(reportReportObj.getId(), reportReportObj.getData(),
                            reportReportObj.getStatus(), reportReportObj.getType(), reportReportObj.getRet());
                    
                    usreReportObj.setRet(retStat);
                    ret = getSsnsDataImp().updatSsReportDataStatusTypeRetById(usreReportObj.getId(), usreReportObj.getData(),
                            usreReportObj.getStatus(), usreReportObj.getType(), usreReportObj.getRet());
                    
                }
                return;
            }
            if (tObj.getType() == ConstantKey.COMPLETED) {
                // send communication to completed

                if (reportReportObj != null) {
                    
                    reportReportObj.setStatus(ConstantKey.COMPLETED);
                    reportReportObj.setType(ConstantKey.OPEN);
                    
                    ReportData reportdata = new ReportData();
                    String dataSt = new ObjectMapper().writeValueAsString(reportdata);
                    reportReportObj.setData(dataSt);
                    
                    Calendar dateNow = TimeConvertion.getCurrentCalendar();
                    long ctime = dateNow.getTimeInMillis();
                    reportReportObj.setUpdatedatel(ctime);
                    reportReportObj.setUpdatedatedisplay(new java.sql.Date(ctime));
                    String retStat = reportReportObj.getRet();
                    String tzid = "America/New_York"; //EDT
                    TimeZone tz = TimeZone.getTimeZone(tzid);
                    Date d = new Date();
                    // timezone symbol (z) included in the format pattern 
                    DateFormat format = new SimpleDateFormat("M/dd/yyyy hh:mm a z");
                    // format date in target timezone
                    format.setTimeZone(tz);
                    String ESTdate = format.format(d);
                    retStat += " complete:" + ESTdate;
                    reportReportObj.setRet(retStat);
                    logger.info("> execMonitorTesting " + reportReportObj.getName() + " " + reportReportObj.getRet());
                    int ret = getSsnsDataImp().updatSsReportDataStatusTypeRetById(reportReportObj.getId(), reportReportObj.getData(),
                            reportReportObj.getStatus(), reportReportObj.getType(), reportReportObj.getRet());
                    
                    usreReportObj.setStatus(ConstantKey.COMPLETED);
                    usreReportObj.setRet(retStat);
                    ret = getSsnsDataImp().updatSsReportDataStatusTypeRetById(usreReportObj.getId(), usreReportObj.getData(),
                            usreReportObj.getStatus(), usreReportObj.getType(), usreReportObj.getRet());
                }
                return;
            }
            
            if (usreReportObj != null) {
                if (usreReportObj.getStatus() != ConstantKey.INITIAL) {
                    return;
                }
            }
            
            int id = tObj.getAccid();
            String LABURL = tObj.getTesturl();  // " empty for monitor, not empay for regression

            SsnsAcc accObj = getSsnsDataImp().getSsnsAccObjByID(id);
            String dataSt = accObj.getData();
            ProductData pData = new ObjectMapper().readValue(dataSt, ProductData.class);
            if (pData != null) {
                ArrayList<String> cmdList = pData.getCmd();
                if (cmdList != null) {
                    for (int j = 0; j < cmdList.size(); j += 2) {
                        String oper = cmdList.get(j + 1);
                        String passSt = "";
                        long exec = 0;
                        ArrayList<String> response = new ArrayList();
                        ArrayList<String> labResponse = new ArrayList();
                        if (LABURL.length() == 0) {
                            response = serviceAFweb.testSsnsprodPRocessByIdRT(CKey.ADMIN_USERNAME, null, accObj.getId() + "", accObj.getApp(), oper, LABURL);
                            if (response != null) {
                                if (response.size() > 3) {
                                    String feat = response.get(0);
                                    String execSt = response.get(2);
                                    execSt = ServiceAFweb.replaceAll("elapsedTime:", "", execSt);
                                    exec = Long.parseLong(execSt);
                                    passSt = R_FAIL;
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
                                    passSt = accObj.getName() + ":" + passSt;
                                } else {
                                    String PR = "";
                                    response = serviceAFweb.testSsnsprodPRocessByIdRT(CKey.ADMIN_USERNAME, null, accObj.getId() + "", accObj.getApp(), oper, PR);
                                    labResponse = serviceAFweb.testSsnsprodPRocessByIdRT(CKey.ADMIN_USERNAME, null, accObj.getId() + "", accObj.getApp(), oper, LABURL);
                                    boolean result = compareArraySame(response, labResponse);
                                    if (result = true) {
                                        passSt = R_PASS;
                                    } else {
                                        passSt = R_FAIL;
                                    }
                                    passSt = accObj.getName() + ":" + passSt;
                                    
                                }
                                SsReport reportObj = new SsReport();
                                String nameRepId = reportReportObj.getName() + "_" + reportReportObj.getId();
                                reportObj.setName(nameRepId);
                                reportObj.setStatus(ConstantKey.OPEN);
                                reportObj.setRet(passSt);
                                reportObj.setExec(exec);
                                
                                reportObj.setApp(accObj.getApp());
                                reportObj.setCusid(accObj.getCusid());
                                reportObj.setBanid(accObj.getBanid());
                                reportObj.setOper(accObj.getOper());
                                reportObj.setTiid(accObj.getTiid());
                                
                                reportObj.setType(reportReportObj.getId()); // reference to report test case
                                reportObj.setUid(REPORT_TESE_CASE);
                                
                                ProductData pDataNew = new ProductData();
                                pDataNew.setPostParam(pData.getPostParam());
                                pDataNew.setFlow(response);
                                pDataNew.setDetailResp(labResponse);
                                
                                String nameSt = new ObjectMapper().writeValueAsString(pDataNew);
                                reportObj.setData(nameSt);
                                
                                Calendar dateNow = TimeConvertion.getCurrentCalendar();
                                long ctime = dateNow.getTimeInMillis();
                                reportObj.setUpdatedatel(ctime);
                                reportObj.setUpdatedatedisplay(new java.sql.Date(ctime));
                                int ret = getSsnsDataImp().insertSsReportObject(reportObj);
                            }
                        }
                        AFSleep();
                    }
                }
            }
        } catch (Exception ex) {
            logger.info("> execMonitorTesting Exception " + ex.getMessage());
        }
    }
    
    public boolean compareArraySame(ArrayList<String> listOne, ArrayList<String> listTwo) {
        boolean ret = false;
        //https://howtodoinjava.com/java/collections/arraylist/compare-two-arraylists/           
        //remove all elements of second list
//      response.removeAll(labResponse);
//      labResponse.removeAll(response);

        int sizeOne = listOne.size();
        int sizeTwo = listTwo.size();
        if (sizeOne != sizeTwo) {
            return false;
        }
        if ((sizeOne < 5) || (sizeTwo < 5)) {
            return false;
        }
        
        ArrayList<String> listOneTmp = new ArrayList();
        for (int i = 4; i < listOne.size(); i++) {
            listOneTmp.add(listOne.get(i));
        }
        ArrayList<String> listTwoTmp = new ArrayList();
        for (int i = 4; i < listTwo.size(); i++) {
            listTwoTmp.add(listTwo.get(i));
        }
        listOneTmp.removeAll(listTwoTmp);
        if (listOneTmp.size() != 0) {
            return false;
        }
        
        listOneTmp = new ArrayList();
        for (int i = 4; i < listOne.size(); i++) {
            listOneTmp.add(listOne.get(i));
        }
        listTwoTmp = new ArrayList();
        for (int i = 4; i < listTwo.size(); i++) {
            listTwoTmp.add(listTwo.get(i));
        }
        listTwoTmp.removeAll(listOneTmp);
        if (listTwoTmp.size() != 0) {
            return false;
        }
        
        return true;
    }
    
    public void reportMoniter(ServiceAFweb serviceAFweb, String name) {
        // report
        try {
            String uid = REPORT_REPORT;
            ArrayList<SsReport> reportObjList = getSsnsDataImp().getSsReportObjListByUidDesc(name, uid);
            
            if (reportObjList == null) {
                return;
            }
            if (reportObjList.size() == 0) {
                return;
            }
            
            SsReport reportReportObj = reportObjList.get(0);
            ArrayList<String> overviewList = new ArrayList();
            ArrayList<String> testRList = new ArrayList();
            String nameRepId = name + "_" + reportReportObj.getId();
            
            this.getReportStat(serviceAFweb, nameRepId, SsnsService.APP_PRODUCT, testRList, overviewList);
            this.getReportStat(serviceAFweb, nameRepId, SsnsService.APP_WIFI, testRList, overviewList);
            this.getReportStat(serviceAFweb, nameRepId, SsnsService.APP_APP, testRList, overviewList);
            this.getReportStat(serviceAFweb, nameRepId, SsnsService.APP_TTVC, testRList, overviewList);
            logger.info("> reportList  " + testRList.size());
            
            if (reportReportObj.getStatus() == ConstantKey.COMPLETED) {
                ReportData reportdata = new ReportData();
                
                String dataSt = reportReportObj.getData();
                if (dataSt.length() > 0) {
                    reportdata = new ObjectMapper().readValue(dataSt, ReportData.class);
                }
                reportdata.setReportList(testRList);
                dataSt = new ObjectMapper().writeValueAsString(reportdata);
                reportReportObj.setData(dataSt);
                
                Calendar dateNow = TimeConvertion.getCurrentCalendar();
                long ctime = dateNow.getTimeInMillis();
                reportReportObj.setUpdatedatel(ctime);
                reportReportObj.setUpdatedatedisplay(new java.sql.Date(ctime));
                int ret = getSsnsDataImp().updatSsReportDataStatusTypeById(reportReportObj.getId(), reportReportObj.getData(),
                        reportReportObj.getStatus(), reportReportObj.getType());
            }
////////////////
/////////// put back to the main user
            uid = REPORT_USER;
            reportObjList = getSsnsDataImp().getSsReportObjListByUidDesc(name, uid);
            
            if (reportObjList == null) {
                return;
            }
            if (reportObjList.size() == 0) {
                return;
            }
            SsReport userReportObj = reportObjList.get(0);
            
            ReportData reportdata = new ReportData();
            
            String dataSt = userReportObj.getData();
            if (dataSt.length() > 0) {
                reportdata = new ObjectMapper().readValue(dataSt, ReportData.class);
            }
            reportdata.setReportList(overviewList);
            dataSt = new ObjectMapper().writeValueAsString(reportdata);
            userReportObj.setData(dataSt);
            
            Calendar dateNow = TimeConvertion.getCurrentCalendar();
            long ctime = dateNow.getTimeInMillis();
            userReportObj.setUpdatedatel(ctime);
            userReportObj.setUpdatedatedisplay(new java.sql.Date(ctime));
            int ret = getSsnsDataImp().updatSsReportDataStatusTypeById(userReportObj.getId(), userReportObj.getData(),
                    userReportObj.getStatus(), userReportObj.getType());
            
        } catch (Exception ex) {
            
        }
    }
    
    public void getReportStat(ServiceAFweb serviceAFweb, String nameRepId, String app, ArrayList<String> testRList, ArrayList<String> overviewList) {
        int Pass = 0;
        int Fail = 0;
        float exec = 0;
        
        ArrayList<String> namelist = getSsnsDataImp().getSsReportObjListByFeatureOper(nameRepId, app);
        
        ArrayList<String> retlist = new ArrayList();
        if (namelist != null) {
            if (namelist.size() == 0) {
                return;
            }
            for (int i = 0; i < namelist.size(); i++) {
                String oper = namelist.get(i);
                retlist.add(oper);
                String cnt = getSsnsDataImp().getSsReportObjListByFeatureOperCnt(nameRepId, oper);
                retlist.add(cnt);
            }
        }
        namelist = getSsnsDataImp().getSsReportObjListByFeatureOper(nameRepId, app);
        
        ArrayList<String> operList = new ArrayList();
        if (namelist != null) {
            for (int i = 0; i < namelist.size(); i++) {
                String oper = namelist.get(i);
                operList.add(oper);
                String cnt = getSsnsDataImp().getSsReportObjListByFeatureOperCnt(nameRepId, oper);
                operList.add(cnt);
            }
        }
        
        if (operList != null) {
            for (int j = 0; j < operList.size(); j += 2) {
                String oper = operList.get(j);
                String reportLine = app + "," + oper + ",size," + operList.get(j + 1);
                testRList.add(reportLine);
                
                ArrayList<SsReport> reportOperList = getSsnsDataImp().getSsReportByFeatureOperIdList(nameRepId, app, oper, 0);
                if (reportOperList != null) {
                    for (int k = 0; k < reportOperList.size(); k++) {
                        SsReport rObj = reportOperList.get(k);
                        reportLine = rObj.getId() + "," + rObj.getCusid() + "," + rObj.getBanid() + "," + rObj.getTiid() + "," + rObj.getRet() + "," + rObj.getExec();
                        testRList.add(reportLine);
                        
                        if (rObj.getRet().indexOf(R_PASS) != -1) {
                            Pass++;
                            exec += rObj.getExec();
                        } else {
                            Fail++;
                        }
                    }
                }
            }
            float execAvg = 0;
            if (Pass > 0) {
                execAvg = exec / Pass;
            }
            String reportLine = app + ",result," + "pass," + Pass + ",fail," + Fail + ",exec," + execAvg;
            overviewList.add(reportLine);
            testRList.add(reportLine);
            logger.info("getReportStat " + nameRepId + " " + reportLine);
        }
    }

    /////
}
