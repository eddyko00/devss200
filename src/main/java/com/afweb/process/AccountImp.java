/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.afweb.process;

import com.afweb.model.*;
import com.afweb.service.ServiceAFweb;
import com.afweb.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author eddy
 */
public class AccountImp {

    protected static Logger logger = Logger.getLogger("AccountImp");
    public AccountDB accountdb = new AccountDB();

    public void setDataSource(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        accountdb.setJdbcTemplate(jdbcTemplate);
        accountdb.setDataSource(dataSource);
    }

    public ArrayList getExpiredCustomerList(int length) {
        ArrayList customerList = new ArrayList();

        ArrayList customerDBList = accountdb.getExpiredCustomerList(length);
        if (customerDBList != null && customerDBList.size() > 0) {
            if (length == 0) {
                // all customer
                return customerDBList;
            }
            if (length > customerDBList.size()) {
                length = customerDBList.size();
            }
            for (int i = 0; i < length; i++) {
                CustomerObj cust = (CustomerObj) customerDBList.get(i);
                customerList.add(cust);
            }
        }
        return customerList;
    }

    public ArrayList getAllIdSQL(String sql) {
        return accountdb.getAllIdSQL(sql);
    }

    public ArrayList getAllUserNameSQL(String sql) {
        return accountdb.getAllUserNameSQL(sql);
    }

    public String getAllCustomerDBSQL(String sql) {
        return accountdb.getAllCustomerDBSQL(sql);
    }

    public String getAllCommDBSQL(String sql) {
        return accountdb.getAllCommDBSQL(sql);
    }

    public String getAllSQLquery(String sql) {
        return accountdb.getAllSQLqueryDBSQL(sql);
    }

    public ArrayList getCustomerList(int length) {
        ArrayList customerList = new ArrayList();

        ArrayList customerDBList = accountdb.getCustomerList(0);
        if (customerDBList != null && customerDBList.size() > 0) {
            if (length == 0) {
                // all customer
                return customerDBList;
            }
            if (length > customerDBList.size()) {
                length = customerDBList.size();
            }
            for (int i = 0; i < length; i++) {
                CustomerObj cust = (CustomerObj) customerDBList.get(i);
                customerList.add(cust);
            }
        }
        return customerList;
    }

    public int updateCustStatus(CustomerObj custObj) {
        return accountdb.updateCustStatus(custObj);
    }

    public CustomerObj getCustomerStatus(String UserName, String Password) {
//        logger.info("> getCustomerPassword  " + UserName);
        UserName = UserName.toUpperCase();
        CustomerObj customer = accountdb.getCustomer(UserName, Password);
        if (customer != null) {
            return customer;
        }
        return null;
    }

    public CustomerObj getCustomerPassword(String UserName, String Password) {
//        logger.info("> getCustomerPassword  " + UserName);
        UserName = UserName.toUpperCase();
        CustomerObj customer = accountdb.getCustomer(UserName, Password);
        if (customer != null) {
            if (customer.getStatus() == ConstantKey.OPEN) {
                Calendar dateNow = TimeConvertion.getCurrentCalendar();
                long dateNowLong = dateNow.getTimeInMillis();
                customer.setUpdatedatedisplay(new java.sql.Date(dateNowLong));
                customer.setUpdatedatel(dateNowLong);

                accountdb.updateCustomerUpdateDate(customer);
                return customer;
            }
        }
        return null;
    }

    public int removeCustAccountCommByCommID(int id) {
        return accountdb.removeCustAccountCommByCommID(id);
    }

    public int removeCustAccountComm(CustomerObj custObj) {
        if (custObj == null) {
            return 0;
        }
        return accountdb.removeCustAccountComm(custObj.getId());
    }

    public int removeCustomer(CustomerObj custObj) {
        if (custObj == null) {
            return 0;
        }
        removeCustAccountComm(custObj);
        return accountdb.DeleteCustomer(custObj);
    }

    public int addCustomer(CustomerObj newCustomer) {

        if (newCustomer == null) {
            return 0;
        }
        String userN = newCustomer.getUsername();
        userN = userN.toUpperCase();
        NameObj nameObj = new NameObj(userN);
        String UserName = nameObj.getNormalizeName();
        
        newCustomer.setUsername(UserName);
        logger.info("> addCustomer  " + newCustomer.getUsername());
        int result = 0;
        try {
            result = accountdb.addCustomer(newCustomer);
        } catch (Exception ex) {
            logger.info("> addCustomer exception " + newCustomer.getUsername() + " - " + ex.getMessage());
        }
        return result;
    }

    public int updateCustAllStatus(String UserName, int substatus) {

        CustomerObj customer = getCustomerPassword(UserName, null);
        if (customer != null) {
            customer.setSubstatus(substatus);
            return accountdb.updateCustAllStatus(customer);
        }
        return 0;
    }

    public ArrayList<CommObj> getCommByCustomerAccountID(String UserName, String Password, int custID) {

        CustomerObj customer = getCustomerPassword(UserName, Password);
        if (customer != null) {
            return accountdb.getComObjByCustAccountID(custID);
        }
        return null;
    }

    public int addCommByCustomerAccountID(String UserName, String Password, int customerID, String data) {

        CustomerObj customer = getCustomerPassword(UserName, Password);
        if (customer != null) {
            return addCustAccountMessage(customer, data);
        }
        return 0;
    }

    public int removeCustAccountCommByID(String UserName, String Password, int custID, int commid) {

        CustomerObj customer = getCustomerPassword(UserName, Password);
        if (customer != null) {
            return accountdb.removeCustAccountCommByCommID(commid);

        }
        return 0;
    }

    public int updateTransactionOrder(ArrayList transSQL) {
        try {
            return accountdb.updateTransactionOrder(transSQL);
        } catch (SQLException ex) {
            logger.info("> updateTransactionOrder exception " + ex.getMessage());
        }
        return 0;
    }

    ///////
    public ArrayList<CommObj> getComObjByName(int accountID, String name) {
        return accountdb.getComObjByName(accountID, name);
    }

    public ArrayList<CommObj> getComObjByCustAccountID(int custID) {
        return accountdb.getComObjByCustAccountID(custID);
    }

    public CommData getCommDataObj(CommObj commObj) {
        if (commObj.getType() == ConstantKey.INT_COM_SPLIT) {
            String name = commObj.getData();
            if ((name != null) && (name.length() > 0)) {
                name = name.replaceAll("#", "\"");
                try {
                    CommData commData = new ObjectMapper().readValue(name, CommData.class);
                    return commData;
                } catch (IOException ex) {
                }
            }
        }
        return null;
    }

    public CommObj getCommObjByID(int commID) {
        ArrayList<CommObj> commObjList = accountdb.getComObjByID(commID);
        if (commObjList != null) {
            if (commObjList.size() == 1) {
                CommObj commObj = commObjList.get(0);
                return commObj;
            }
        }
        return null;
    }

    public int addCustAccountCommMessage(CustomerObj custObj, CommData commDataObj) {
        if (custObj == null) {
            return -1;
        }

        CommObj message = new CommObj();
        message.setCustomerid(custObj.getId());
        message.setAccountid(custObj.getId());
        message.setName(ConstantKey.COM_SPLIT);
        message.setType(ConstantKey.INT_COM_SPLIT);

        Calendar dateNow = TimeConvertion.getCurrentCalendar();
        long dateNowLong = dateNow.getTimeInMillis();
        message.setUpdatedatedisplay(new java.sql.Date(dateNowLong));
        message.setUpdatedatel(dateNowLong);
        String msg = "";
        try {
            msg = new ObjectMapper().writeValueAsString(commDataObj);
        } catch (JsonProcessingException ex) {
        }
        msg = msg.replaceAll("\"", "#");
        message.setData(msg);
        return accountdb.insertAccountCommData(message);
    }

    public int addCustAccountMessage(CustomerObj custObj, String msg) {
        if (custObj == null) {
            return -1;
        }

        CommObj message = new CommObj();
        message.setCustomerid(custObj.getId());
        message.setAccountid(custObj.getId());
        message.setName(ConstantKey.COM_SIGNAL);
        message.setType(ConstantKey.INT_COM_SIGNAL);

        Calendar dateNow = TimeConvertion.getCurrentCalendar();
        long dateNowLong = dateNow.getTimeInMillis();
        message.setUpdatedatedisplay(new java.sql.Date(dateNowLong));
        message.setUpdatedatel(dateNowLong);
        message.setData(msg);
        return accountdb.insertAccountCommData(message);
    }

    //////////////////////////////////////////////
    private ArrayList<String> getDBDataTableId(ServiceAFweb serviceAFWeb, String table) {
        try {
            RequestObj sqlObj = new RequestObj();
            sqlObj.setCmd(ServiceAFweb.AllId + "");
            String sql = "select id from " + table + " order by id asc";
            sqlObj.setReq(sql);

            RequestObj sqlObjresp = serviceAFWeb.SystemSQLRequest(sqlObj);
            String output = sqlObjresp.getResp();
            ArrayList<String> array = null;

            String[] arrayItem = new ObjectMapper().readValue(output, String[].class);
            List<String> listItem = Arrays.<String>asList(arrayItem);
            array = new ArrayList<String>(listItem);
            return array;

        } catch (IOException ex) {
            logger.info("> getDBDataTableId " + ex);
        }
        return null;
    }

    public boolean downloadDBData(ServiceAFweb serviceAFWeb) {
        saveDBcustomer(serviceAFWeb);
        saveSsnsDataAcc(serviceAFWeb, "ssnsacc");
        saveSsnsDataAcc(serviceAFWeb, "ssreport");
        saveSsnsDataAcc(serviceAFWeb, "ssnsdata");        
        saveDBcomm(serviceAFWeb);
        return true;
    }

    private int saveDBcomm(ServiceAFweb serviceAFWeb) {

        String tableName = "ssnscomm";
        ArrayList<String> idList = getDBDataTableId(serviceAFWeb, tableName);
        int len = idList.size();
        ArrayList<String> writeArray = new ArrayList();
        int fileCont = 0;
        int loopCnt = 0;
        if (len > 0) {
            for (int id = 0; id < len; id += 500) {
                String first = idList.get(id);
                if ((id + 500) < len) {
                    String last = idList.get(id - 1 + 500);
                    int ret = saveDBcomm(serviceAFWeb, tableName, first, last, writeArray);
                    if (ret == 0) {
                        return 0;
                    }
                    ServiceAFweb.AFSleep();
                }
                if ((id + 500) >= len) {
                    String last = idList.get(len - 1);
                    int ret = saveDBcomm(serviceAFWeb, tableName, first, last, writeArray);
                    if (ret == 0) {
                        return 0;
                    }
                    break;
                }
                loopCnt++;
                if (loopCnt > 5) {
                    FileUtil.FileWriteTextArray(ServiceAFweb.FileLocalPath + tableName + "_" + fileCont + ".txt", writeArray);
                    fileCont++;
                    loopCnt = 0;
                    writeArray.clear();
                }
            }
            FileUtil.FileWriteTextArray(ServiceAFweb.FileLocalPath + tableName + "_" + fileCont + ".txt", writeArray);
            return 1;
        }
        return 0;

    }

    private int saveDBcomm(ServiceAFweb serviceAFWeb, String tableName, String first, String last, ArrayList<String> writeArray) {

        try {
            logger.info("> saveDBcomm - " + first + " " + last);
            RequestObj sqlObj = new RequestObj();
            sqlObj.setCmd(ServiceAFweb.AllComm + "");
            String sql = "select * from " + tableName + " where id >= " + first + " and id <= " + last + " order by id asc";
            if (first.equals(last)) {
                sql = "select * from " + tableName + " where id = " + first;
            }
            sqlObj.setReq(sql);

            RequestObj sqlObjresp = serviceAFWeb.SystemSQLRequest(sqlObj);
            String output = sqlObjresp.getResp();
            if (output == null) {
                return 0;
            }
            if (output.equals("null")) {
                return 0;
            }

            ArrayList<CommObj> array = null;
            CommObj[] arrayItem = new ObjectMapper().readValue(output, CommObj[].class);
            List<CommObj> listItem = Arrays.<CommObj>asList(arrayItem);
            array = new ArrayList<CommObj>(listItem);

            for (int i = 0; i < array.size(); i++) {
                CommObj obj = array.get(i);
                String st = new ObjectMapper().writeValueAsString(obj);
                writeArray.add(st);
            }
            return 1;

        } catch (Exception ex) {
            logger.info("> saveDBcomm " + ex);
        }
        return 0;
    }

    private int saveSsnsDataAcc(ServiceAFweb serviceAFWeb, String tableName) {

        ArrayList<String> idList = getDBDataTableId(serviceAFWeb, tableName);
        int len = idList.size();
        ArrayList<String> writeArray = new ArrayList();
        int fileCont = 0;
        int loopCnt = 0;
        if (len > 0) {
            for (int id = 0; id < len; id += 50) {
                String first = idList.get(id);
                if ((id + 50) < len) {
                    String last = idList.get(id - 1 + 50);
                    int ret = saveSsnsDataProcess(serviceAFWeb, tableName, first, last, writeArray);
                    if (ret == 0) {
                        return 0;
                    }
                    ServiceAFweb.AFSleep();
                }
                if ((id + 50) >= len) {
                    String last = idList.get(len - 1);
                    int ret = saveSsnsDataProcess(serviceAFWeb, tableName, first, last, writeArray);
                    if (ret == 0) {
                        return 0;
                    }
                    break;
                }
                loopCnt++;
                if (loopCnt > 200) {
                    FileUtil.FileWriteTextArray(ServiceAFweb.FileLocalPath + tableName + "_" + fileCont + ".txt", writeArray);
                    logger.info("> saveSsnsDataAcc " + tableName + "_" + fileCont + " " + writeArray.size());
                    fileCont++;
                    loopCnt = 0;
                    writeArray.clear();
                }
            }

            FileUtil.FileWriteTextArray(ServiceAFweb.FileLocalPath + tableName + "_" + fileCont + ".txt", writeArray);
            return 1;
        }

        return 0;
    }

    private int saveSsnsDataProcess(ServiceAFweb serviceAFWeb, String tableName, String first, String last, ArrayList<String> writeArray) {
        try {
            logger.info("> saveDBSsnsData - " + tableName + " " + first + " " + last);

            RequestObj sqlObj = new RequestObj();
            sqlObj.setCmd(ServiceAFweb.AllSsnsData + "");
            String sql = "select * from " + tableName + " where id >= " + first + " and id <= " + last + " order by id asc";
            if (first.equals(last)) {
                sql = "select * from " + tableName + " where id = " + first;
            }
            sqlObj.setReq(sql);

            RequestObj sqlObjresp = serviceAFWeb.SystemSQLRequest(sqlObj);
            String output = sqlObjresp.getResp();
            if (output == null) {
                return 0;
            }
            ArrayList<SsnsData> array = null;
            SsnsData[] arrayItem = new ObjectMapper().readValue(output, SsnsData[].class);
            List<SsnsData> listItem = Arrays.<SsnsData>asList(arrayItem);
            array = new ArrayList<SsnsData>(listItem);

            for (int i = 0; i < array.size(); i++) {
                SsnsData obj = array.get(i);
                String st = new ObjectMapper().writeValueAsString(obj);
                writeArray.add(st);
            }
            return 1;

        } catch (Exception ex) {
            logger.info("> saveDBSsnsData " + ex);
        }
        return 0;
    }

    private int saveDBcustomer(ServiceAFweb serviceAFWeb) {
        try {
            String tableName = "cust";
            ArrayList<String> idList = getDBDataTableId(serviceAFWeb, tableName);
            int len = idList.size();
            if (len > 0) {
                String first = idList.get(0);
                String last = idList.get(len - 1);
                logger.info("> saveDBcustomer - " + first + " " + last);
                RequestObj sqlObj = new RequestObj();
                sqlObj.setCmd(ServiceAFweb.AllCustomer + "");
                String sql = "select * from " + tableName + " where id >= " + first + " and id <= " + last + " order by id asc";
                if (first.equals(last)) {
                    sql = "select * from " + tableName + " where id = " + first;
                }
                sqlObj.setReq(sql);

                RequestObj sqlObjresp = serviceAFWeb.SystemSQLRequest(sqlObj);
                String output = sqlObjresp.getResp();
                if (output == null) {
                    return 0;
                }
                ArrayList<CustomerObj> array = null;
                CustomerObj[] arrayItem = new ObjectMapper().readValue(output, CustomerObj[].class);
                List<CustomerObj> listItem = Arrays.<CustomerObj>asList(arrayItem);
                array = new ArrayList<CustomerObj>(listItem);

                ArrayList<String> writeArray = new ArrayList();
                for (int i = 0; i < array.size(); i++) {
                    CustomerObj obj = array.get(i);
                    String st = new ObjectMapper().writeValueAsString(obj);
                    writeArray.add(st);
                }
                FileUtil.FileWriteTextArray(ServiceAFweb.FileLocalPath + tableName + ".txt", writeArray);
                return 1;
            }
        } catch (Exception ex) {
            logger.info("> saveDBcustomer " + ex);
        }
        return 0;
    }

    public boolean restoreSsnsDataDB(ServiceAFweb serviceAFWeb) {
        restoreDBSsnsDataAcc(serviceAFWeb, "ssnsdata");
        return true;

    }

    public boolean restoreSsnsAccDB(ServiceAFweb serviceAFWeb) {
        restoreDBSsnsDataAcc(serviceAFWeb, "ssnsacc");
        return true;

    }
    public boolean restoreSsReportDB(ServiceAFweb serviceAFWeb) {
        restoreDBSsnsDataAcc(serviceAFWeb, "ssreport");
        return true;

    }
    public boolean restoreDBData(ServiceAFweb serviceAFWeb) {

        if (FileUtil.FileTest(ServiceAFweb.FileLocalPath + "cust.txt") == false) {
            return false;
        }

        int ret = restoreDBcustomer(serviceAFWeb);
        if (ret == 0) {
            return false;
        }

        restoreDBSsnsDataAcc(serviceAFWeb, "ssnsdata");
        restoreDBSsnsDataAcc(serviceAFWeb, "ssnsacc");
        restoreDBSsnsDataAcc(serviceAFWeb, "ssreport");
        restoreDBcomm(serviceAFWeb);
        restoreDBdummy(serviceAFWeb);
        return true;
    }

    private int restoreDBdummy(ServiceAFweb serviceAFWeb) {

        logger.info("> restoreDBdummy ");
        ArrayList<String> writeSQLArray = new ArrayList();
        String sql = SsnsDataDB.createDummytable();
        writeSQLArray.add(sql);
        try {
            RequestObj sqlObj = new RequestObj();
            sqlObj.setCmd(ServiceAFweb.UpdateSQLList + "");
            String st = new ObjectMapper().writeValueAsString(writeSQLArray);
            sqlObj.setReq(st);
            RequestObj sqlObjresp = serviceAFWeb.SystemSQLRequest(sqlObj);
            String output = sqlObjresp.getResp();
            if (output == null) {
                return 0;
            }
            return 1;
        } catch (JsonProcessingException ex) {
            logger.info("> sendRequestObj - exception " + ex);
        }
        return 0;
    }

    private int restoreDBcomm(ServiceAFweb serviceAFWeb) {
        int fileCont = 0;
        String tableName = "ssnscomm";
        int ret = 0;
        while (true) {
            String fileName = ServiceAFweb.FileLocalPath + tableName + "_" + fileCont + ".txt";
            if (FileUtil.FileTest(fileName) == false) {
                break;
            }
            ret = restoreDBcommProcess(serviceAFWeb, fileName, fileCont);
            fileCont++;
        }
        return ret;
    }

    private int restoreDBcommProcess(ServiceAFweb serviceAFWeb, String tableName, int fileCont) {

        try {
            ArrayList<String> writeArray = new ArrayList();
            String fileName = ServiceAFweb.FileLocalPath + tableName + "_" + fileCont + ".txt";
            if (FileUtil.FileTest(fileName) == false) {
                return 0;
            }
            FileUtil.FileReadTextArray(fileName, writeArray);
            ArrayList<String> writeSQLArray = new ArrayList();
            logger.info("> restoreDBcommProcess " + tableName + "_" + fileCont + " " + writeArray.size());
            int index = 0;
            for (int i = 0; i < writeArray.size(); i++) {
                String output = writeArray.get(i);
                CommObj item = new ObjectMapper().readValue(output, CommObj.class);
                String sql = AccountDB.insertCommObj(item);
                writeSQLArray.add(sql);
                index++;
                if (i % 500 == 0) {
                    logger.info("> restoreDBcommProcess " + tableName + "_" + fileCont + " " + i);
                }
                if (index > 5) {
                    index = 0;
                    int ret = serviceAFWeb.sendRequestObj(writeSQLArray);
                    if (ret == 0) {
                        return 0;
                    }
                    writeSQLArray.clear();
                    logger.info("> restoreDBcomm " + fileName + " total=" + writeArray.size() + " index=" + i);

                    ServiceAFweb.AFSleep();
                }

            }
            return serviceAFWeb.sendRequestObj(writeSQLArray);

        } catch (IOException ex) {
            logger.info("> restoreDBcomm - exception " + ex);
        }
        return 0;
    }

    private int restoreDBSsnsDataAcc(ServiceAFweb serviceAFWeb, String tableName) {
        int fileCont = 0;

        int ret = 0;
        while (true) {
            String fileName = ServiceAFweb.FileLocalPath + tableName + "_" + fileCont + ".txt";
            if (FileUtil.FileTest(fileName) == false) {
                break;
            }
            ret = restoreDBSsnsDataProcess(serviceAFWeb, tableName, fileCont);
            fileCont++;
        }
        return ret;
    }

    private int restoreDBSsnsDataProcess(ServiceAFweb serviceAFWeb, String tableName, int fileCont) {

        try {
            ArrayList<String> writeArray = new ArrayList();

            String fName = ServiceAFweb.FileLocalPath + tableName + "_" + fileCont + ".txt";
            if (FileUtil.FileTest(fName) == false) {
                return 0;
            }
            FileUtil.FileReadTextArray(fName, writeArray);
            ArrayList<String> writeSQLArray = new ArrayList();
            logger.info("> restoreDBSsnsDataProcess " + tableName + "_" + fileCont + " " + writeArray.size());
            int index = 0;
            for (int i = 0; i < writeArray.size(); i++) {
                String output = writeArray.get(i);
                SsnsData item = new ObjectMapper().readValue(output, SsnsData.class);
                String sql = SsnsDataDB.insertSsnsDataAcc(tableName, item);
                writeSQLArray.add(sql);
                index++;
                if (i % 1000 == 0) {
                    logger.info("> restoreDBSsnsDataProcess " + tableName + "_" + fileCont + " " + i);
                }
                if (index > 5) {// 5) {
                    index = 0;
                    int ret = serviceAFWeb.sendRequestObj(writeSQLArray);
                    if (ret == 0) {
                        return 0;
                    }
                    writeSQLArray.clear();
                    ServiceAFweb.AFSleep();
                }

            }
            return serviceAFWeb.sendRequestObj(writeSQLArray);

        } catch (IOException ex) {
            logger.info("> restoreDBSsnsDataProcess - exception " + ex);
        }
        return 0;
    }

    private int restoreDBcustomer(ServiceAFweb serviceAFWeb) {
        String tableName = "cust";
        try {

            ArrayList<String> writeArray = new ArrayList();
            FileUtil.FileReadTextArray(ServiceAFweb.FileLocalPath + tableName + ".txt", writeArray);
            ArrayList<String> writeSQLArray = new ArrayList();
            logger.info("> restoreDBcustomer " + writeArray.size());
            for (int i = 0; i < writeArray.size(); i++) {
                String output = writeArray.get(i);
                CustomerObj item = new ObjectMapper().readValue(output, CustomerObj.class);
                String sql = AccountDB.insertCustomer(item);
                writeSQLArray.add(sql);
            }
            return serviceAFWeb.sendRequestObj(writeSQLArray);

        } catch (IOException ex) {
            logger.info("> restoreDBcustomer - exception " + ex);
        }
        return 0;
    }

///////
}
