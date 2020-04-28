/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.afweb.service;

import com.afweb.model.*;
import com.afweb.service.db.*;
import com.afweb.util.CKey;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.text.DateFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import static org.apache.http.protocol.HTTP.USER_AGENT;

/**
 *
 * @author eddy
 */
public class ServiceRemoteDB {

    private static ServiceAFweb serviceAFWeb = null;

    public static Logger log = Logger.getLogger("ServiceRemoteDB");
    public static String CMD = "cmd";
    public static String CMDPOST = "sqlreq";

    public static String WEBPOST = "";
    private static String URL_PATH = "";

    /**
     * @return the URL_PATH
     */
    public static String getURL_PATH() {
        return URL_PATH;
    }

    /**
     * @param aURL_PATH the URL_PATH to set
     */
    public static void setURL_PATH(String aURL_PATH) {
        URL_PATH = aURL_PATH;
    }

    /**
     * @return the serviceAFWeb
     */
    public static ServiceAFweb getServiceAFWeb() {
        return serviceAFWeb;
    }

    /**
     * @param aServiceAFWeb the serviceAFWeb to set
     */
    public static void setServiceAFWeb(ServiceAFweb aServiceAFWeb) {
        serviceAFWeb = aServiceAFWeb;
    }

    public int getExecuteRemoteListDB_Mysql(ArrayList<String> sqlCMDList) {
//        log.info("postExecuteListRemoteDB_Mysql sqlCMDList " + sqlCMDList.size());
        String postSt = "";
        int MAXPostSize = 20;
        int postSize = 0;
        for (int i = 0; i < sqlCMDList.size(); i++) {

            postSize++;
            if ((postSize > MAXPostSize) || (postSt.length() > 2000)) {
                try {
                    int ret = postExecuteListRemoteDB_Mysql(postSt);
                    if (ret == 0) {
                        return ret;
                    }
                    postSize = 0;
                    postSt = "";
                } catch (Exception ex) {
                    log.info("postExecuteListRemoteDB_Mysql exception " + ex);
                    return 0;
                }
            }

            if (postSt.length() == 0) {
                postSt = sqlCMDList.get(i);
                continue;
            }
            postSt += "~" + sqlCMDList.get(i);
        }
        try {
            int ret = postExecuteListRemoteDB_Mysql(postSt);
            return ret;
        } catch (Exception ex) {
            log.info("postExecuteListRemoteDB_Mysql exception " + ex);
        }
        return 0;

    }

    private int postExecuteListRemoteDB_Mysql(String sqlCMDList) throws Exception {
        ServiceAFweb.getServerObj().setCntRESTrequest(ServiceAFweb.getServerObj().getCntRESTrequest() + 1);
//        log.info("postExecuteListRemoteDB_Mysql " + sqlCMDList);
        try {
            String subResourcePath = WEBPOST;
            HashMap newmap = new HashMap();
            newmap.put(CMD, "3");

            HashMap newbodymap = new HashMap();
            newbodymap.put(CMDPOST, sqlCMDList);

            String output = sendRequest_remotesql(METHOD_POST, subResourcePath, newmap, newbodymap);

            int beg = output.indexOf("~~ ");
            int end = output.indexOf(" ~~");

            if ((beg >= end) || (beg == -1)) {
                log.info("postExecuteListRemoteDB_Mysql " + sqlCMDList);
                return -1;
            }
            output = output.substring(beg + 3, end);
//            String[] dataArray = output.split("~");
            String[] dataArray = splitIncludeEmpty(output, '~');
            output = dataArray[0];
            if (output == null) {
                log.info("postExecuteListRemoteDB_Mysql " + sqlCMDList);
                return 0;
            }
            if (output.length() == 0) {
                return 0;
            }
            return Integer.parseInt(output);

        } catch (Exception ex) {
            log.info("postExecuteListRemoteDB_Mysql exception " + ex);

            ServiceAFweb.getServerObj().setCntRESTexception(ServiceAFweb.getServerObj().getCntRESTexception() + 1);
            throw ex;
        }
    }

    public int postExecuteRemoteDB_RemoteMysql(String sqlCMD) throws Exception {

        ServiceAFweb.getServerObj().setCntRESTrequest(ServiceAFweb.getServerObj().getCntRESTrequest() + 1);

//        log.info("postExecuteRemoteDB_RemoteMysql " + sqlCMD);
        try {
            String subResourcePath = WEBPOST;
            HashMap newmap = new HashMap();
            newmap.put(CMD, "2");

            HashMap newbodymap = new HashMap();
            newbodymap.put(CMDPOST, sqlCMD);

            String output = sendRequest_remotesql(METHOD_POST, subResourcePath, newmap, newbodymap);

            int beg = output.indexOf("~~ ");
            int end = output.indexOf(" ~~");

            if ((beg >= end) || (beg == -1)) {
                log.info("postExecuteRemoteDB_RemoteMysql fail " + sqlCMD);
                return -1;
            }
            output = output.substring(beg + 3, end);
            if (output.length() > 2) {
                log.info("postExecuteRemoteDB_RemoteMysql output " + output);
            }
            String[] dataArray = splitIncludeEmpty(output, '~');
            output = dataArray[0];
            if (output == null) {
                log.info("postExecuteRemoteDB_RemoteMysql fail" + sqlCMD);
                return 0;
            }
            if (output.length() == 0) {
                return 0;
            }
            return Integer.parseInt(output);

        } catch (Exception ex) {
            log.info("postExecuteRemoteDB_Mysql exception " + ex);
            ServiceAFweb.getServerObj().setCntRESTexception(ServiceAFweb.getServerObj().getCntRESTexception() + 1);
            throw ex;
        }

    }

    public int getCountRowsRemoteDB_RemoteMysql(String sqlTable) throws Exception {

        ServiceAFweb.getServerObj().setCntRESTrequest(ServiceAFweb.getServerObj().getCntRESTrequest() + 1);
        try {
            String subResourcePath = WEBPOST;
            // create hash map
            HashMap newmap = new HashMap();
            newmap.put(CMD, "1");

            HashMap newbodymap = new HashMap();
            String sqlcmd = "SELECT COUNT(0) AS c FROM " + sqlTable;
            newbodymap.put(CMDPOST, sqlcmd);

            String output = sendRequest_remotesql(METHOD_POST, subResourcePath, newmap, newbodymap);

            int beg = output.indexOf("~~ ");
            int end = output.indexOf(" ~~");

            if ((beg >= end) || (beg == -1)) {
                return -1;
            }
            output = output.substring(beg + 3, end);

//            String[] dataArray = output.split("~");
            String[] dataArray = splitIncludeEmpty(output, '~');
            output = "[";
            int recSize = 1;
            for (int i = 0; i < dataArray.length; i += recSize) {
                output += "{";
                output += "\"c\":\"" + dataArray[i] + "\"";
                if (i + recSize >= dataArray.length) {
                    output += "}";
                } else {
                    output += "},";
                }
            }
            output += "]";

//            log.info("getCountRowsInTable output " + output);
            ArrayList<CountRowsRDB> arrayDB = null;
            try {
                CountRowsRDB[] arrayItem = new ObjectMapper().readValue(output, CountRowsRDB[].class);
                List<CountRowsRDB> listItem = Arrays.<CountRowsRDB>asList(arrayItem);
                arrayDB = new ArrayList<CountRowsRDB>(listItem);
            } catch (IOException ex) {
                log.info("getCountRowsInTable exception " + output);
                return -1;
            }
            int countR = arrayDB.get(0).getCount();
            return countR;

        } catch (Exception ex) {
            log.info("getCountRowsInTable exception " + ex);
            ServiceAFweb.getServerObj().setCntRESTexception(ServiceAFweb.getServerObj().getCntRESTexception() + 1);
            throw ex;
        }

    }

    public ArrayList<CustomerObj> getCustomerListSqlRemoteDB_RemoteMysql(String sqlCMD) throws Exception {

        ServiceAFweb.getServerObj().setCntRESTrequest(ServiceAFweb.getServerObj().getCntRESTrequest() + 1);
//        log.info("getCustomerListSqlRemoteDB_RemoteMysql " + sqlCMD);
        try {
            String subResourcePath = WEBPOST;
            HashMap newmap = new HashMap();
            newmap.put(CMD, "1");

            HashMap newbodymap = new HashMap();
            newbodymap.put(CMDPOST, sqlCMD);

            String output = sendRequest_remotesql(METHOD_POST, subResourcePath, newmap, newbodymap);

            int beg = output.indexOf("~~ ");
            int end = output.indexOf(" ~~");
            // create hash map
            if (beg > end) {
                return null;
            }
            output = output.substring(beg + 3, end);
            if (output.length() == 0) {
                return null;
            }
//            String[] dataArray = output.split("~");
            String[] dataArray = splitIncludeEmpty(output, '~');
            output = "[";

            int recSize = 12;
            for (int i = 0; i < dataArray.length; i += recSize) {
                output += "{";
                output += "\"id\":\"" + dataArray[i] + "\",";
                output += "\"username\":\"" + dataArray[i + 1] + "\",";
                output += "\"password\":\"" + dataArray[i + 2] + "\",";
                output += "\"type\":\"" + dataArray[i + 3] + "\",";
                output += "\"status\":\"" + dataArray[i + 4] + "\",";
                output += "\"substatus\":\"" + dataArray[i + 5] + "\",";
                output += "\"startdate\":\"" + dataArray[i + 6] + "\",";
                output += "\"firstname\":\"" + dataArray[i + 7] + "\",";
                output += "\"lastname\":\"" + dataArray[i + 8] + "\",";
                output += "\"email\":\"" + dataArray[i + 9] + "\",";
                output += "\"updatedatedisplay\":\"" + dataArray[i + 10] + "\",";
                output += "\"updatedatel\":\"" + dataArray[i + 11] + "\"";

                if (i + recSize >= dataArray.length) {
                    output += "}";
                } else {
                    output += "},";
                }
            }
            output += "]";
            return getCustomerListSqlRemoteDB_Process(output);

        } catch (Exception ex) {
            log.info("getCustomerListSqlRemoteDB exception " + ex);
            ServiceAFweb.getServerObj().setCntRESTexception(ServiceAFweb.getServerObj().getCntRESTexception() + 1);
            throw ex;
        }
    }

    private ArrayList<CustomerObj> getCustomerListSqlRemoteDB_Process(String output) {
        if (output.equals("")) {
            return null;
        }
        ArrayList<CustomerRDB> arrayDB = null;
        ArrayList<CustomerObj> arrayReturn = new ArrayList();
        try {
            CustomerRDB[] arrayItem = new ObjectMapper().readValue(output, CustomerRDB[].class);
            List<CustomerRDB> listItem = Arrays.<CustomerRDB>asList(arrayItem);
            arrayDB = new ArrayList<CustomerRDB>(listItem);

            for (int i = 0; i < arrayDB.size(); i++) {
                CustomerRDB rs = arrayDB.get(i);
                CustomerObj customer = new CustomerObj();
                customer.setId(Integer.parseInt(rs.getId()));
                customer.setUsername(rs.getUsername());
                customer.setPassword(rs.getPassword());
                customer.setType(Integer.parseInt(rs.getType()));
                customer.setStatus(Integer.parseInt(rs.getStatus()));
                customer.setSubstatus(Integer.parseInt(rs.getSubstatus()));

                DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date dayObj = sdf.parse(rs.getStartdate());
                customer.setStartdate(new java.sql.Date(dayObj.getTime()));
                String FN = rs.getFirstname();
                if (FN.equals(ConstantKey.nullSt)) {
                    FN = null;
                }
                String LN = rs.getLastname();
                if (LN.equals(ConstantKey.nullSt)) {
                    LN = null;
                }
                customer.setFirstname(FN);
                customer.setLastname(LN);
                customer.setEmail(rs.getEmail());
                customer.setUpdatedatel(Long.parseLong(rs.getUpdatedatel()));
                customer.setUpdatedatedisplay(new java.sql.Date(customer.getUpdatedatel()));

                String tzid = "America/New_York"; //EDT
                TimeZone tz = TimeZone.getTimeZone(tzid);
                Date d = new Date(customer.getUpdatedatel());
                DateFormat format = new SimpleDateFormat("M/dd/yyyy hh:mm a z");
                format.setTimeZone(tz);
                String ESTdate = format.format(d);
                customer.setUpdateDateD(ESTdate);

                arrayReturn.add(customer);
            }
            return arrayReturn;
        } catch (Exception ex) {
            log.info("getCustomerListSqlRemoteDB exception " + ex);
            return null;
        }
    }

    public ArrayList getCommListSqlRemoteDB_RemoteMysql(String sqlCMD) throws Exception {

        ServiceAFweb.getServerObj().setCntRESTrequest(ServiceAFweb.getServerObj().getCntRESTrequest() + 1);
//        log.info("getAccountListSqlRemoteDB_RemoteMysql " + sqlCMD);
        try {
            String subResourcePath = WEBPOST;
            HashMap newmap = new HashMap();
            newmap.put(CMD, "1");

            HashMap newbodymap = new HashMap();
            newbodymap.put(CMDPOST, sqlCMD);

            String output = sendRequest_remotesql(METHOD_POST, subResourcePath, newmap, newbodymap);

            int beg = output.indexOf("~~ ");
            int end = output.indexOf(" ~~");
            // create hash map
            if (beg > end) {
                return null;
            }
            output = output.substring(beg + 3, end);
            if (output.length() == 0) {
                return null;
            }
//            String[] dataArray = output.split("~");
            String[] dataArray = splitIncludeEmpty(output, '~');
            output = "[";

            int recSize = 10;
            for (int i = 0; i < dataArray.length; i += recSize) {
                output += "{";
                output += "\"id\":\"" + dataArray[i] + "\",";
                output += "\"name\":\"" + dataArray[i + 1] + "\",";
                output += "\"type\":\"" + dataArray[i + 2] + "\",";
                output += "\"status\":\"" + dataArray[i + 3] + "\",";
                output += "\"substatus\":\"" + dataArray[i + 4] + "\",";
                output += "\"updatedatedisplay\":\"" + dataArray[i + 5] + "\",";
                output += "\"updatedatel\":\"" + dataArray[i + 6] + "\",";
                output += "\"data\":\"" + dataArray[i + 7] + "\",";
                output += "\"accountid\":\"" + dataArray[i + 8] + "\",";
                output += "\"customerid\":\"" + dataArray[i + 9] + "\"";

                if (i + recSize >= dataArray.length) {
                    output += "}";
                } else {
                    output += "},";
                }
            }
            output += "]";
            return getCommListSqlRemoteDB_Process(output);

        } catch (Exception ex) {
            log.info("getCommListSqlRemoteDB_RemoteMysql exception " + ex);
            ServiceAFweb.getServerObj().setCntRESTexception(ServiceAFweb.getServerObj().getCntRESTexception() + 1);
            throw ex;
        }
    }

    private ArrayList<CommObj> getCommListSqlRemoteDB_Process(String output) {
        if (output.equals("")) {
            return null;
        }
        ArrayList<CommRDB> arrayDB = null;
        ArrayList<CommObj> arrayReturn = new ArrayList();
        try {
            CommRDB[] arrayItem = new ObjectMapper().readValue(output, CommRDB[].class);
            List<CommRDB> listItem = Arrays.<CommRDB>asList(arrayItem);
            arrayDB = new ArrayList<CommRDB>(listItem);

            for (int i = 0; i < arrayDB.size(); i++) {
                CommRDB rs = arrayDB.get(i);
                CommObj comm = new CommObj();

                comm.setId(Integer.parseInt(rs.getId()));
                comm.setName(rs.getName());
                comm.setType(Integer.parseInt(rs.getType()));
                comm.setStatus(Integer.parseInt(rs.getStatus()));
                comm.setSubstatus(Integer.parseInt(rs.getSubstatus()));
                comm.setUpdatedatel(Long.parseLong(rs.getUpdatedatel()));
                comm.setUpdatedatedisplay(new java.sql.Date(comm.getUpdatedatel()));

                comm.setData(rs.getData());
                comm.setAccountid(Integer.parseInt(rs.getAccountid()));
                comm.setCustomerid(Integer.parseInt(rs.getCustomerid()));

                arrayReturn.add(comm);
            }
            return arrayReturn;
        } catch (Exception ex) {
            log.info("getCommListSqlRemoteDB_Process exception " + output);
            return null;
        }
    }

    public ArrayList getAllLockSqlRemoteDB_RemoteMysql(String sqlCMD) throws Exception {

        ServiceAFweb.getServerObj().setCntRESTrequest(ServiceAFweb.getServerObj().getCntRESTrequest() + 1);
//        log.info("getAllLockSqlRemoteDB_RemoteMysql " + sqlCMD);
        try {
            String subResourcePath = WEBPOST;
            HashMap newmap = new HashMap();
            newmap.put(CMD, "1");

            HashMap newbodymap = new HashMap();
            newbodymap.put(CMDPOST, sqlCMD);

            String output = sendRequest_remotesql(METHOD_POST, subResourcePath, newmap, newbodymap);

            int beg = output.indexOf("~~ ");
            int end = output.indexOf(" ~~");
            // create hash map
            if (beg > end) {
                return null;
            }
            output = output.substring(beg + 3, end);
            if (output.length() == 0) {
                return null;
            }
//            String[] dataArray = output.split("~");
            String[] dataArray = splitIncludeEmpty(output, '~');
            output = "[";

            int recSize = 6;
            for (int i = 0; i < dataArray.length; i += recSize) {
                output += "{";
                output += "\"id\":\"" + dataArray[i] + "\",";
                output += "\"lockname\":\"" + dataArray[i + 1] + "\",";
                output += "\"type\":\"" + dataArray[i + 2] + "\",";
                output += "\"lockdatedisplay\":\"" + dataArray[i + 3] + "\",";
                output += "\"lockdatel\":\"" + dataArray[i + 4] + "\",";
                output += "\"comment\":\"" + dataArray[i + 5] + "\"";

                if (i + recSize >= dataArray.length) {
                    output += "}";
                } else {
                    output += "},";
                }
            }
            output += "]";
            return getAllLockSqlRemoteDB_Process(output);

        } catch (Exception ex) {
            log.info("getAllLockSqlRemoteDB_RemoteMysql exception " + ex);
            ServiceAFweb.getServerObj().setCntRESTexception(ServiceAFweb.getServerObj().getCntRESTexception() + 1);
            throw ex;
        }
    }

    private ArrayList<AFLockObject> getAllLockSqlRemoteDB_Process(String output) {
        if (output.equals("")) {
            return null;
        }
        ArrayList<LockObjectRDB> arrayDB = null;
        ArrayList<AFLockObject> arrayReturn = new ArrayList();
        try {
            LockObjectRDB[] arrayItem = new ObjectMapper().readValue(output, LockObjectRDB[].class);
            List<LockObjectRDB> listItem = Arrays.<LockObjectRDB>asList(arrayItem);
            arrayDB = new ArrayList<LockObjectRDB>(listItem);

            for (int i = 0; i < arrayDB.size(); i++) {
                LockObjectRDB rs = arrayDB.get(i);

                AFLockObject lock = new AFLockObject();
                lock.setLockname(rs.getLockname());
                lock.setType(Integer.parseInt(rs.getType()));
                lock.setLockdatel(Long.parseLong(rs.getLockdatel()));
                lock.setLockdatedisplay(new java.sql.Date(lock.getLockdatel()));

                lock.setId(Integer.parseInt(rs.getId()));
                lock.setComment(rs.getComment());

                String tzid = "America/New_York"; //EDT
                TimeZone tz = TimeZone.getTimeZone(tzid);
                Date d = new Date(lock.getLockdatel());
                DateFormat format = new SimpleDateFormat("M/dd/yyyy hh:mm a z");
                format.setTimeZone(tz);
                String ESTdate = format.format(d);
                lock.setUpdateDateD(ESTdate);

                arrayReturn.add(lock);
            }
            return arrayReturn;
        } catch (IOException ex) {
            log.info("getAllLockSqlRemoteDB_Process exception " + output);
            return null;
        }

    }

    public ArrayList<SsnsAcc> getAllSsnsAccSqlRemoteDB_RemoteMysql(String sqlCMD) throws Exception {

        ServiceAFweb.getServerObj().setCntRESTrequest(ServiceAFweb.getServerObj().getCntRESTrequest() + 1);
        try {
            String subResourcePath = WEBPOST;
            HashMap newmap = new HashMap();
            newmap.put(CMD, "1");

            HashMap newbodymap = new HashMap();
            newbodymap.put(CMDPOST, sqlCMD);

            String output = sendRequest_remotesql(METHOD_POST, subResourcePath, newmap, newbodymap);

            int beg = output.indexOf("~~ ");
            int end = output.indexOf(" ~~");
            // create hash map
            if (beg > end) {
                return null;
            }
            output = output.substring(beg + 3, end);
            if (output.length() == 0) {
                return null;
            }
//            String[] dataArray = output.split("~");
            String[] dataArray = splitIncludeEmpty(output, '~');
            output = "[";

            int recSize = 16;
            for (int i = 0; i < dataArray.length; i += recSize) {
                output += "{";
                output += "\"id\":\"" + dataArray[i] + "\",";
                output += "\"name\":\"" + dataArray[i + 1] + "\",";
                output += "\"status\":\"" + dataArray[i + 2] + "\",";
                output += "\"type\":\"" + dataArray[i + 3] + "\",";

                output += "\"uid\":\"" + dataArray[i + 4] + "\",";
                output += "\"cusid\":\"" + dataArray[i + 5] + "\",";
                output += "\"banid\":\"" + dataArray[i + 6] + "\",";
                output += "\"tiid\":\"" + dataArray[i + 7] + "\",";

                output += "\"app\":\"" + dataArray[i + 8] + "\",";
                output += "\"oper\":\"" + dataArray[i + 9] + "\",";
                output += "\"down\":\"" + dataArray[i + 10] + "\",";
                output += "\"ret\":\"" + dataArray[i + 11] + "\",";
                output += "\"exec\":\"" + dataArray[i + 12] + "\",";

                output += "\"data\":\"" + dataArray[i + 13] + "\",";
                output += "\"updatedatedisplay\":\"" + dataArray[i + 14] + "\",";
                output += "\"updatedatel\":\"" + dataArray[i + 15] + "\"";

                if (i + recSize >= dataArray.length) {
                    output += "}";
                } else {
                    output += "},";
                }
            }
            output += "]";
            ArrayList SsnsAccList = getAllSsnsAccSqlRemoteDB_Process(output);;
            return SsnsAccList;

        } catch (Exception ex) {
            log.info("getAllSsnsDataSqlRemoteDB_RemoteMysql exception " + ex);
            ServiceAFweb.getServerObj().setCntRESTexception(ServiceAFweb.getServerObj().getCntRESTexception() + 1);
            throw ex;
        }
    }

    private ArrayList<SsnsAcc> getAllSsnsAccSqlRemoteDB_Process(String output) {
        if (output.equals("")) {
            return null;
        }
        ArrayList<SsnsAccRDB> arrayDB = null;
        ArrayList<SsnsAcc> arrayReturn = new ArrayList();
        try {
            SsnsAccRDB[] arrayItem = new ObjectMapper().readValue(output, SsnsAccRDB[].class);
            List<SsnsAccRDB> listItem = Arrays.<SsnsAccRDB>asList(arrayItem);
            arrayDB = new ArrayList<SsnsAccRDB>(listItem);

            for (int i = 0; i < arrayDB.size(); i++) {
                SsnsAccRDB rs = arrayDB.get(i);

                SsnsAcc nn = new SsnsAcc();
                nn.setId(Integer.parseInt(rs.getId()));
                nn.setName(rs.getName());
                nn.setStatus(Integer.parseInt(rs.getStatus()));
                nn.setType(Integer.parseInt(rs.getType()));

                nn.setUid(rs.getUid());
                nn.setCusid(rs.getCusid());
                nn.setBanid(rs.getBanid());
                nn.setTiid(rs.getTiid());

                nn.setApp(rs.getApp());
                nn.setOper(rs.getOper());
                nn.setDown(rs.getDown());
                nn.setRet(rs.getRet());
                nn.setExec(Long.parseLong(rs.getExec()));

                String stData = rs.getData();
                stData = stData.replaceAll("#", "\"");
                nn.setData(stData);

                nn.setUpdatedatel(Long.parseLong(rs.getUpdatedatel()));
                nn.setUpdatedatedisplay(new java.sql.Date(nn.getUpdatedatel()));

                arrayReturn.add(nn);
            }
            return arrayReturn;
        } catch (IOException ex) {
            log.info("getAllSsnsDataSqlRemoteDB_Process exception " + output);
            return null;
        }
    }

    public ArrayList<SsReport> getAllSsReportSqlRemoteDB_RemoteMysql(String sqlCMD) throws Exception {

        ServiceAFweb.getServerObj().setCntRESTrequest(ServiceAFweb.getServerObj().getCntRESTrequest() + 1);
        try {
            String subResourcePath = WEBPOST;
            HashMap newmap = new HashMap();
            newmap.put(CMD, "1");

            HashMap newbodymap = new HashMap();
            newbodymap.put(CMDPOST, sqlCMD);

            String output = sendRequest_remotesql(METHOD_POST, subResourcePath, newmap, newbodymap);

            int beg = output.indexOf("~~ ");
            int end = output.indexOf(" ~~");
            // create hash map
            if (beg > end) {
                return null;
            }
            output = output.substring(beg + 3, end);
            if (output.length() == 0) {
                return null;
            }
//            String[] dataArray = output.split("~");
            String[] dataArray = splitIncludeEmpty(output, '~');
            output = "[";

            int recSize = 16;
            for (int i = 0; i < dataArray.length; i += recSize) {
                output += "{";
                output += "\"id\":\"" + dataArray[i] + "\",";
                output += "\"name\":\"" + dataArray[i + 1] + "\",";
                output += "\"status\":\"" + dataArray[i + 2] + "\",";
                output += "\"type\":\"" + dataArray[i + 3] + "\",";

                output += "\"uid\":\"" + dataArray[i + 4] + "\",";
                output += "\"cusid\":\"" + dataArray[i + 5] + "\",";
                output += "\"banid\":\"" + dataArray[i + 6] + "\",";
                output += "\"tiid\":\"" + dataArray[i + 7] + "\",";

                output += "\"app\":\"" + dataArray[i + 8] + "\",";
                output += "\"oper\":\"" + dataArray[i + 9] + "\",";
                output += "\"down\":\"" + dataArray[i + 10] + "\",";
                output += "\"ret\":\"" + dataArray[i + 11] + "\",";
                output += "\"exec\":\"" + dataArray[i + 12] + "\",";

                output += "\"data\":\"" + dataArray[i + 13] + "\",";
                output += "\"updatedatedisplay\":\"" + dataArray[i + 14] + "\",";
                output += "\"updatedatel\":\"" + dataArray[i + 15] + "\"";

                if (i + recSize >= dataArray.length) {
                    output += "}";
                } else {
                    output += "},";
                }
            }
            output += "]";

            return getAllSsReportSqlRemoteDB_Process(output);

        } catch (Exception ex) {
            log.info("getAllSsReportSqlRemoteDB_RemoteMysql exception " + ex);
            ServiceAFweb.getServerObj().setCntRESTexception(ServiceAFweb.getServerObj().getCntRESTexception() + 1);
            throw ex;
        }
    }

    private ArrayList<SsReport> getAllSsReportSqlRemoteDB_Process(String output) {
        if (output.equals("")) {
            return null;
        }
        ArrayList<SsReportRDB> arrayDB = null;
        ArrayList<SsReport> arrayReturn = new ArrayList();
        try {
            SsReportRDB[] arrayItem = new ObjectMapper().readValue(output, SsReportRDB[].class);
            List<SsReportRDB> listItem = Arrays.<SsReportRDB>asList(arrayItem);
            arrayDB = new ArrayList<SsReportRDB>(listItem);

            for (int i = 0; i < arrayDB.size(); i++) {
                SsReportRDB rs = arrayDB.get(i);

                SsReport nn = new SsReport();
                nn.setId(Integer.parseInt(rs.getId()));
                nn.setName(rs.getName());
                nn.setStatus(Integer.parseInt(rs.getStatus()));
                nn.setType(Integer.parseInt(rs.getType()));

                nn.setUid(rs.getUid());
                nn.setCusid(rs.getCusid());
                nn.setBanid(rs.getBanid());
                nn.setTiid(rs.getTiid());

                nn.setApp(rs.getApp());
                nn.setOper(rs.getOper());
                nn.setDown(rs.getDown());
                nn.setRet(rs.getRet());
                nn.setExec(Long.parseLong(rs.getExec()));

                String stData = rs.getData();
                stData = stData.replaceAll("#", "\"");
                nn.setData(stData);

                nn.setUpdatedatel(Long.parseLong(rs.getUpdatedatel()));
                nn.setUpdatedatedisplay(new java.sql.Date(nn.getUpdatedatel()));

                arrayReturn.add(nn);
            }
            return arrayReturn;
        } catch (IOException ex) {
            log.info("getAllSsReportSqlRemoteDB_Process exception " + output);
            return null;
        }
    }

    public ArrayList<Pram7RDB> getAll7ParamSqlRemoteDB_RemoteMysql(String sqlCMD) throws Exception {

        ServiceAFweb.getServerObj().setCntRESTrequest(ServiceAFweb.getServerObj().getCntRESTrequest() + 1);
        try {
            String subResourcePath = WEBPOST;
            HashMap newmap = new HashMap();
            newmap.put(CMD, "1");

            HashMap newbodymap = new HashMap();
            newbodymap.put(CMDPOST, sqlCMD);

            String output = sendRequest_remotesql(METHOD_POST, subResourcePath, newmap, newbodymap);

            int beg = output.indexOf("~~ ");
            int end = output.indexOf(" ~~");
            // create hash map
            if (beg > end) {
                return null;
            }
            output = output.substring(beg + 3, end);
            if (output.length() == 0) {
                return null;
            }
//            String[] dataArray = output.split("~");
            String[] dataArray = splitIncludeEmpty(output, '~');
            output = "[";

            int recSize = 7;
            for (int i = 0; i < dataArray.length; i += recSize) {
                output += "{";
                output += "\"parm1\":\"" + dataArray[i] + "\",";
                output += "\"parm2\":\"" + dataArray[i + 1] + "\",";
                output += "\"parm3\":\"" + dataArray[i + 2] + "\",";
                output += "\"parm4\":\"" + dataArray[i + 3] + "\",";
                output += "\"parm5\":\"" + dataArray[i + 4] + "\",";
                output += "\"parm6\":\"" + dataArray[i + 5] + "\",";
                output += "\"parm7\":\"" + dataArray[i + 6] + "\",";

                if (i + recSize >= dataArray.length) {
                    output += "}";
                } else {
                    output += "},";
                }
            }
            output += "]";

            return getAll7ParamSqlRemoteDB_RemoteMysql_Process(output);

        } catch (Exception ex) {
            log.info("getAll7ParamSqlRemoteDB_RemoteMysql exception " + ex);
            ServiceAFweb.getServerObj().setCntRESTexception(ServiceAFweb.getServerObj().getCntRESTexception() + 1);
            throw ex;
        }
    }

    public ArrayList<SsnsData> getAllSsnsDataSqlRemoteDB_RemoteMysql(String sqlCMD) throws Exception {

        ServiceAFweb.getServerObj().setCntRESTrequest(ServiceAFweb.getServerObj().getCntRESTrequest() + 1);
        try {
            String subResourcePath = WEBPOST;
            HashMap newmap = new HashMap();
            newmap.put(CMD, "1");

            HashMap newbodymap = new HashMap();
            newbodymap.put(CMDPOST, sqlCMD);

            String output = sendRequest_remotesql(METHOD_POST, subResourcePath, newmap, newbodymap);

            int beg = output.indexOf("~~ ");
            int end = output.indexOf(" ~~");
            // create hash map
            if (beg > end) {
                return null;
            }
            output = output.substring(beg + 3, end);
            if (output.length() == 0) {
                return null;
            }
//            String[] dataArray = output.split("~");
            String[] dataArray = splitIncludeEmpty(output, '~');
            output = "[";

            int recSize = 16;
            for (int i = 0; i < dataArray.length; i += recSize) {
                output += "{";
                output += "\"id\":\"" + dataArray[i] + "\",";
                output += "\"name\":\"" + dataArray[i + 1] + "\",";
                output += "\"status\":\"" + dataArray[i + 2] + "\",";
                output += "\"type\":\"" + dataArray[i + 3] + "\",";

                output += "\"uid\":\"" + dataArray[i + 4] + "\",";
                output += "\"cusid\":\"" + dataArray[i + 5] + "\",";
                output += "\"banid\":\"" + dataArray[i + 6] + "\",";
                output += "\"tiid\":\"" + dataArray[i + 7] + "\",";

                output += "\"app\":\"" + dataArray[i + 8] + "\",";
                output += "\"oper\":\"" + dataArray[i + 9] + "\",";
                output += "\"down\":\"" + dataArray[i + 10] + "\",";
                output += "\"ret\":\"" + dataArray[i + 11] + "\",";
                output += "\"exec\":\"" + dataArray[i + 12] + "\",";

                output += "\"data\":\"" + dataArray[i + 13] + "\",";
                output += "\"updatedatedisplay\":\"" + dataArray[i + 14] + "\",";
                output += "\"updatedatel\":\"" + dataArray[i + 15] + "\"";

                if (i + recSize >= dataArray.length) {
                    output += "}";
                } else {
                    output += "},";
                }
            }
            output += "]";

            return getAllSsnsDataSqlRemoteDB_Process(output);

        } catch (Exception ex) {
            log.info("getAllSsnsDataSqlRemoteDB_RemoteMysql exception " + ex);
            ServiceAFweb.getServerObj().setCntRESTexception(ServiceAFweb.getServerObj().getCntRESTexception() + 1);
            throw ex;
        }
    }

    private ArrayList<SsnsData> getAllSsnsDataSqlRemoteDB_Process(String output) {
        if (output.equals("")) {
            return null;
        }
        ArrayList<SsnsDataRDB> arrayDB = null;
        ArrayList<SsnsData> arrayReturn = new ArrayList();
        try {
            SsnsDataRDB[] arrayItem = new ObjectMapper().readValue(output, SsnsDataRDB[].class);
            List<SsnsDataRDB> listItem = Arrays.<SsnsDataRDB>asList(arrayItem);
            arrayDB = new ArrayList<SsnsDataRDB>(listItem);

            for (int i = 0; i < arrayDB.size(); i++) {
                SsnsDataRDB rs = arrayDB.get(i);

                SsnsData nn = new SsnsData();
                nn.setId(Integer.parseInt(rs.getId()));
                nn.setName(rs.getName());
                nn.setStatus(Integer.parseInt(rs.getStatus()));
                nn.setType(Integer.parseInt(rs.getType()));

                nn.setUid(rs.getUid());
                nn.setCusid(rs.getCusid());
                nn.setBanid(rs.getBanid());
                nn.setTiid(rs.getTiid());

                nn.setApp(rs.getApp());
                nn.setOper(rs.getOper());
                nn.setDown(rs.getDown());
                nn.setRet(rs.getRet());
                nn.setExec(Long.parseLong(rs.getExec()));

                String stData = rs.getData();
                stData = stData.replaceAll("#", "\"");
                nn.setData(stData);

                nn.setUpdatedatel(Long.parseLong(rs.getUpdatedatel()));
                nn.setUpdatedatedisplay(new java.sql.Date(nn.getUpdatedatel()));

                arrayReturn.add(nn);
            }
            return arrayReturn;
        } catch (IOException ex) {
            log.info("getAllSsnsDataSqlRemoteDB_Process exception " + output);
            return null;
        }
    }

    public ArrayList getAllNameTXTSqlRemoteDB_RemoteMysql(String sqlCMD) throws Exception {

        ServiceAFweb.getServerObj().setCntRESTrequest(ServiceAFweb.getServerObj().getCntRESTrequest() + 1);
//        log.info("getAllNameSqlRemoteDB_RemoteMysql " + sqlCMD);
        try {
            String subResourcePath = WEBPOST;
            HashMap newmap = new HashMap();
            newmap.put(CMD, "1");

            HashMap newbodymap = new HashMap();
            newbodymap.put(CMDPOST, sqlCMD);

            String output = sendRequest_remotesql(METHOD_POST, subResourcePath, newmap, newbodymap);

            int beg = output.indexOf("~~ ");
            int end = output.indexOf(" ~~");
            // create hash map
            if (beg > end) {
                return null;
            }
            output = output.substring(beg + 3, end);
            ArrayList<String> retArray = new ArrayList();
            if (output.length() == 0) {
                return retArray;
            }

//            String[] dataArray = output.split("~");
            String[] dataArray = splitIncludeEmpty(output, '~');
            output = "[";
            int recSize = 1;
            for (int i = 0; i < dataArray.length; i += recSize) {
                output += "{";
                output += "\"nametxt\":\"" + dataArray[i] + "\"";
                if (i + recSize >= dataArray.length) {
                    output += "}";
                } else {
                    output += "},";
                }
            }
            output += "]";
            return getAllNameTXTSqlRemoteDB_Process(output);

        } catch (Exception ex) {
            log.info("getAllNameSqlRemoteDB exception " + ex);
            ServiceAFweb.getServerObj().setCntRESTexception(ServiceAFweb.getServerObj().getCntRESTexception() + 1);
            throw ex;
        }
    }

    private ArrayList<String> getAllNameTXTSqlRemoteDB_Process(String output) {
        if (output.equals("")) {
            return null;
        }
        ArrayList<NameTXTRDB> arrayDB = null;
        ArrayList<String> arrayReturn = new ArrayList();
        try {
            NameTXTRDB[] arrayItem = new ObjectMapper().readValue(output, NameTXTRDB[].class);
            List<NameTXTRDB> listItem = Arrays.<NameTXTRDB>asList(arrayItem);
            arrayDB = new ArrayList<NameTXTRDB>(listItem);

            for (int i = 0; i < arrayDB.size(); i++) {
                NameTXTRDB nameRDB = arrayDB.get(i);
                arrayReturn.add(nameRDB.getNametxt());
            }
            return arrayReturn;
        } catch (IOException ex) {
            log.info("getAllNameTXTSqlRemoteDB_Process exception " + output);
            return null;
        }
    }
        
        
    public ArrayList getAllNameSqlRemoteDB_RemoteMysql(String sqlCMD) throws Exception {

        ServiceAFweb.getServerObj().setCntRESTrequest(ServiceAFweb.getServerObj().getCntRESTrequest() + 1);
//        log.info("getAllNameSqlRemoteDB_RemoteMysql " + sqlCMD);
        try {
            String subResourcePath = WEBPOST;
            HashMap newmap = new HashMap();
            newmap.put(CMD, "1");

            HashMap newbodymap = new HashMap();
            newbodymap.put(CMDPOST, sqlCMD);

            String output = sendRequest_remotesql(METHOD_POST, subResourcePath, newmap, newbodymap);

            int beg = output.indexOf("~~ ");
            int end = output.indexOf(" ~~");
            // create hash map
            if (beg > end) {
                return null;
            }
            output = output.substring(beg + 3, end);
            ArrayList<String> retArray = new ArrayList();
            if (output.length() == 0) {
                return retArray;
            }

//            String[] dataArray = output.split("~");
            String[] dataArray = splitIncludeEmpty(output, '~');
            output = "[";
            int recSize = 1;
            for (int i = 0; i < dataArray.length; i += recSize) {
                output += "{";
                output += "\"name\":\"" + dataArray[i] + "\"";
                if (i + recSize >= dataArray.length) {
                    output += "}";
                } else {
                    output += "},";
                }
            }
            output += "]";
            return getAllNameSqlRemoteDB_Process(output);

        } catch (Exception ex) {
            log.info("getAllNameSqlRemoteDB exception " + ex);
            ServiceAFweb.getServerObj().setCntRESTexception(ServiceAFweb.getServerObj().getCntRESTexception() + 1);
            throw ex;
        }
    }

    private ArrayList<String> getAllNameSqlRemoteDB_Process(String output) {
        if (output.equals("")) {
            return null;
        }
        ArrayList<NameRDB> arrayDB = null;
        ArrayList<String> arrayReturn = new ArrayList();
        try {
            NameRDB[] arrayItem = new ObjectMapper().readValue(output, NameRDB[].class);
            List<NameRDB> listItem = Arrays.<NameRDB>asList(arrayItem);
            arrayDB = new ArrayList<NameRDB>(listItem);

            for (int i = 0; i < arrayDB.size(); i++) {
                NameRDB nameRDB = arrayDB.get(i);
                arrayReturn.add(nameRDB.getName());
            }
            return arrayReturn;
        } catch (IOException ex) {
            log.info("getAllNameSqlRemoteDB exception " + output);
            return null;
        }
    }

    public ArrayList getAllIdSqlRemoteDB_RemoteMysql(String sqlCMD) throws Exception {

        ServiceAFweb.getServerObj().setCntRESTrequest(ServiceAFweb.getServerObj().getCntRESTrequest() + 1);
//        log.info("getAllIdSqlRemoteDB_RemoteMysql " + sqlCMD);
        try {
            String subResourcePath = WEBPOST;
            HashMap newmap = new HashMap();
            newmap.put(CMD, "1");

            HashMap newbodymap = new HashMap();
            newbodymap.put(CMDPOST, sqlCMD);

            String output = sendRequest_remotesql(METHOD_POST, subResourcePath, newmap, newbodymap);

            int beg = output.indexOf("~~ ");
            int end = output.indexOf(" ~~");
            // create hash map
            if (beg > end) {
                return null;
            }
            output = output.substring(beg + 3, end);
            ArrayList<String> retArray = new ArrayList();
            if (output.length() == 0) {
                return retArray;
            }

//            String[] dataArray = output.split("~");
            String[] dataArray = splitIncludeEmpty(output, '~');
            output = "[";
            int recSize = 1;
            for (int i = 0; i < dataArray.length; i += recSize) {
                output += "{";
                output += "\"id\":\"" + dataArray[i] + "\"";
                if (i + recSize >= dataArray.length) {
                    output += "}";
                } else {
                    output += "},";
                }
            }
            output += "]";
            return getAllIdSqlRemoteDB_Process(output);

        } catch (Exception ex) {
            log.info("getAllIdSqlRemoteDB_RemoteMysql exception " + ex);
            ServiceAFweb.getServerObj().setCntRESTexception(ServiceAFweb.getServerObj().getCntRESTexception() + 1);
            throw ex;
        }
    }

    private ArrayList<String> getAllIdSqlRemoteDB_Process(String output) {
        if (output.equals("")) {
            return null;
        }
        ArrayList<IdRDB> arrayDB = null;
        ArrayList<String> arrayReturn = new ArrayList();
        try {
            IdRDB[] arrayItem = new ObjectMapper().readValue(output, IdRDB[].class);
            List<IdRDB> listItem = Arrays.<IdRDB>asList(arrayItem);
            arrayDB = new ArrayList<IdRDB>(listItem);

            for (int i = 0; i < arrayDB.size(); i++) {
                IdRDB nameRDB = arrayDB.get(i);
                arrayReturn.add(nameRDB.getId());
            }
            return arrayReturn;
        } catch (IOException ex) {
            log.info("getAllIdSqlRemoteDB exception " + output);
            return null;
        }
    }

    public ArrayList getAllUserNameSqlRemoteDB_RemoteMysql(String sqlCMD) throws Exception {

        ServiceAFweb.getServerObj().setCntRESTrequest(ServiceAFweb.getServerObj().getCntRESTrequest() + 1);
//        log.info("getAllUserNameSqlRemoteDB_RemoteMysql " + sqlCMD);
        try {
            String subResourcePath = WEBPOST;
            HashMap newmap = new HashMap();
            newmap.put(CMD, "1");

            HashMap newbodymap = new HashMap();
            newbodymap.put(CMDPOST, sqlCMD);

            String output = sendRequest_remotesql(METHOD_POST, subResourcePath, newmap, newbodymap);

            int beg = output.indexOf("~~ ");
            int end = output.indexOf(" ~~");
            // create hash map
            if (beg > end) {
                return null;
            }
            output = output.substring(beg + 3, end);
            ArrayList<String> retArray = new ArrayList();
            if (output.length() == 0) {
                return retArray;
            }

//            String[] dataArray = output.split("~");
            String[] dataArray = splitIncludeEmpty(output, '~');
            output = "[";
            int recSize = 1;
            for (int i = 0; i < dataArray.length; i += recSize) {
                output += "{";
                output += "\"username\":\"" + dataArray[i] + "\"";
                if (i + recSize >= dataArray.length) {
                    output += "}";
                } else {
                    output += "},";
                }
            }
            output += "]";
            return getAllUserNameSqlRemoteDB_Process(output);

        } catch (Exception ex) {
            log.info("getAllNameSqlRemoteDB exception " + ex);
            ServiceAFweb.getServerObj().setCntRESTexception(ServiceAFweb.getServerObj().getCntRESTexception() + 1);
            throw ex;
        }
    }

    private ArrayList<String> getAllUserNameSqlRemoteDB_Process(String output) {
        if (output.equals("")) {
            return null;
        }
        ArrayList<UserNameRDB> arrayDB = null;
        ArrayList<String> arrayReturn = new ArrayList();
        try {
            UserNameRDB[] arrayItem = new ObjectMapper().readValue(output, UserNameRDB[].class);
            List<UserNameRDB> listItem = Arrays.<UserNameRDB>asList(arrayItem);
            arrayDB = new ArrayList<UserNameRDB>(listItem);

            for (int i = 0; i < arrayDB.size(); i++) {
                UserNameRDB nameRDB = arrayDB.get(i);
                arrayReturn.add(nameRDB.getUsername());
            }
            return arrayReturn;
        } catch (IOException ex) {
            log.info("getAllNameSqlRemoteDB exception " + output);
            return null;
        }
    }

    public String getAllSQLqueryRemoteDB_RemoteMysql(String sqlCMD) throws Exception {

        ServiceAFweb.getServerObj().setCntRESTrequest(ServiceAFweb.getServerObj().getCntRESTrequest() + 1);
//        log.info("getAccountStockTransactionListRemoteDB_RemoteMysql " + sqlCMD);
        try {
            String subResourcePath = WEBPOST;
            HashMap newmap = new HashMap();
            newmap.put(CMD, "1");

            HashMap newbodymap = new HashMap();
            newbodymap.put(CMDPOST, sqlCMD);

            String output = sendRequest_remotesql(METHOD_POST, subResourcePath, newmap, newbodymap);

            int beg = output.indexOf("~~ ");
            int end = output.indexOf(" ~~");
            // create hash map
            if (beg > end) {
                return null;
            }
            output = output.substring(beg + 3, end);
            if (output.length() == 0) {
                return null;
            }

            return output;

        } catch (Exception ex) {
            log.info("getAllSQLqueryRemoteDB_RemoteMysql exception " + ex);
            ServiceAFweb.getServerObj().setCntRESTexception(ServiceAFweb.getServerObj().getCntRESTexception() + 1);
            throw ex;
        }
    }

    private ArrayList<Pram7RDB> getAll7ParamSqlRemoteDB_RemoteMysql_Process(String output) {
        if (output.equals("")) {
            return null;
        }
        ArrayList<Pram7RDB> arrayDB = null;
        ArrayList<Pram7RDB> arrayReturn = new ArrayList();
        try {
            Pram7RDB[] arrayItem = new ObjectMapper().readValue(output, Pram7RDB[].class);
            List<Pram7RDB> listItem = Arrays.<Pram7RDB>asList(arrayItem);
            arrayDB = new ArrayList<Pram7RDB>(listItem);

            for (int i = 0; i < arrayDB.size(); i++) {
                Pram7RDB rs = arrayDB.get(i);

                Pram7RDB nn = new Pram7RDB();
                nn.setParm1(rs.getParm1());
                nn.setParm2(rs.getParm2());
                nn.setParm3(rs.getParm3());
                nn.setParm4(rs.getParm4());
                nn.setParm5(rs.getParm5());
                nn.setParm6(rs.getParm6());
                nn.setParm7(rs.getParm7());

                arrayReturn.add(nn);
            }
            return arrayReturn;
        } catch (IOException ex) {
            log.info("getAll7ParamSqlRemoteDB_RemoteMysql_Process exception " + output);
            return null;
        }
    }

    /////////////////////////////////////////////////////////////
    // operations names constants
    private static final String METHOD_POST = "post";
    private static final String METHOD_GET = "get";

    private String sendRequest_remotesql(String method, String subResourcePath, Map<String, String> queryParams, Map<String, String> bodyParams) throws Exception {
        String response = null;
        for (int i = 0; i < 4; i++) {
            try {
                response = sendRequest_Process_Mysql(method, subResourcePath, queryParams, bodyParams);
                if (response != null) {
                    return response;
                }
            } catch (Exception ex) {
                // retry
//                log.info("sendRequest " + bodyElement);
                log.info("sendRequest " + method + " Rety " + (i + 1));
            }
        }
        response = sendRequest_Process_Mysql(method, subResourcePath, queryParams, bodyParams);
        return response;
    }

    private String sendRequest_Process_Mysql(String method, String subResourcePath, Map<String, String> queryParams, Map<String, String> bodyParams)
            throws Exception {
        try {

            boolean RemoteCallflag = CKey.SQL_RemoveServerDB;  // using remote server not PHP
            if (RemoteCallflag == true) {
                ServiceAFwebREST remoteREST = new ServiceAFwebREST();
                RequestObj sqlObj = new RequestObj();
                String cmd = "";
                if (queryParams != null && !queryParams.isEmpty()) {
                    for (String key : queryParams.keySet()) {
                        cmd = queryParams.get(key);
                        break;
                    }
                }
                if (cmd.equals("1")) {
                    ;
                } else if (cmd.equals("2")) {
                    ;
                } else if (cmd.equals("3")) {
                    ;
                } else {
                    return "";
                }
                sqlObj.setCmd(cmd);
                String sql = "";
                if (bodyParams != null && !bodyParams.isEmpty()) {
                    String bodyTmp = "";
                    for (String key : bodyParams.keySet()) {
                        bodyTmp = bodyParams.get(key);
                        sql = bodyTmp;
                    }
                }
                sqlObj.setReq(sql);
                String resp = remoteREST.getSQLRequestRemote(sqlObj);
                if (resp != null) {
                    resp = "~~ " + resp + " ~~";
                }
                return resp;
            }
            if (method.equals(METHOD_POST)) {
                String URLPath = getURL_PATH() + subResourcePath;

                String webResourceString = "";
                // assume only one param
                if (queryParams != null && !queryParams.isEmpty()) {
                    for (String key : queryParams.keySet()) {
                        webResourceString = "?" + key + "=" + queryParams.get(key);
                    }
                }

                String bodyElement = "";

                if (bodyParams != null && !bodyParams.isEmpty()) {
                    String bodyTmp = "";
                    for (String key : bodyParams.keySet()) {
                        bodyTmp = bodyParams.get(key);
                        bodyTmp = bodyTmp.replaceAll("&", "-");
                        bodyTmp = bodyTmp.replaceAll("%", "%25");
                        bodyElement = key + "=" + bodyTmp;
                    }
                }

                URLPath += webResourceString;
                URL request = new URL(URLPath);
                //just for testing
//                log.info("Request:: " +URLPath);     
                boolean flagD = true;
                if (bodyElement.indexOf("select * from stockinfo where") == -1) {
                    flagD = false;
                }
                if (bodyElement.indexOf("select * from stock where") == -1) {
                    flagD = false;
                }
                if (flagD == true) {
                    System.out.println("Request Code:: " + bodyElement);
                }
                HttpURLConnection con = null; //(HttpURLConnection) request.openConnection();
                if (CKey.PROXY == true) {
                    //////Add Proxy 
                    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ServiceAFweb.PROXYURL, 8080));
                    con = (HttpURLConnection) request.openConnection(proxy);
                    //////Add Proxy 
                } else {
                    con = (HttpURLConnection) request.openConnection();
                }
                if (method.equals(METHOD_POST)) {
                    con.setRequestMethod("POST");
                } else {
                    con.setRequestMethod("GET");
                }
                con.setRequestProperty("User-Agent", USER_AGENT);
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
//                con.setRequestProperty("Content-Type", "application/json; utf-8");

                if (method.equals(METHOD_POST)) {
                    // For POST only - START
                    con.setDoOutput(true);
                    OutputStream os = con.getOutputStream();
                    byte[] input = bodyElement.getBytes("utf-8");
                    os.write(input, 0, input.length);
                    os.flush();
                    os.close();
                    // For POST only - END
                }

                int responseCode = con.getResponseCode();
                if (responseCode != 200) {
                    System.out.println("Response Code:: " + responseCode);
                }
                if (responseCode >= 200 && responseCode < 300) {
                    ;
                } else {
                    System.out.println("Response Code:: " + responseCode);
                    System.out.println("bodyElement :: " + bodyElement);
                    return null;
                }
                if (responseCode == HttpURLConnection.HTTP_OK) { //success
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    // print result
                    return response.toString();
                } else {
                    log.info("POST request not worked");
                }
            }
        } catch (Exception e) {
            log.info("Error sending REST request:" + e);
            throw e;
        }
        return null;
    }

    //////////////////////////
    ////////
    public static String[] splitIncludeEmpty(String inputStr, char delimiter) {
        if (inputStr == null) {
            return null;
        }
        if (inputStr.charAt(inputStr.length() - 1) == delimiter) {
            // the 000webhostapp always add extra ~ at the end see the source
            inputStr += "End";
            String[] tempString = inputStr.split("" + delimiter);
            int size = tempString.length - 1;
            String[] outString = new String[size];
            for (int i = 0; i < size; i++) {
                outString[i] = tempString[i];
            }
            return outString;
        }
        return inputStr.split("" + delimiter);
    }

}
