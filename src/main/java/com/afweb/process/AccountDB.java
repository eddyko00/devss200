/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.afweb.process;

import com.afweb.model.*;

import com.afweb.service.ServiceAFweb;
import com.afweb.service.ServiceRemoteDB;
import com.afweb.util.CKey;
import com.afweb.util.TimeConvertion;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author eddy
 */
public class AccountDB {

    protected static Logger logger = Logger.getLogger("AccountDB");

    private static JdbcTemplate jdbcTemplate;
    private static DataSource dataSource;
    private ServiceRemoteDB remoteDB = new ServiceRemoteDB();

    /**
     * @return the dataSource
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * @return the jdbcTemplate
     */
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    /**
     * @param jdbcTemplate the jdbcTemplate to set
     */
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * @param dataSource the dataSource to set
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ArrayList getExpiredCustomerList(int length) {
        Calendar dateNow = TimeConvertion.getCurrentCalendar();
        long dateNowLong = dateNow.getTimeInMillis();
        long cust2DayAgo = TimeConvertion.addDays(dateNowLong, -2); // 2 day ago and no update
        String sql = "select * from cust where updatedatel < " + cust2DayAgo;
        sql += " and type=" + CustomerObj.INT_CLIENT_BASIC_USER;
        return getCustomerListSQL(sql, length);
    }

    public String getAllCustomerDBSQL(String sql) {
        try {
            ArrayList<CustomerObj> entries = getCustomerListSQL(sql, 0);
            String nameST = new ObjectMapper().writeValueAsString(entries);
            return nameST;
        } catch (JsonProcessingException ex) {
        }
        return null;
    }

    public ArrayList getCustomerList(int length) {
        String sql = "select * from cust";
        return this.getCustomerListSQL(sql, length);
    }

    public static boolean checkCallRemoteSQL_Mysql() {
        boolean ret = false;
        if (CKey.SQL_DATABASE == CKey.REMOTE_MYSQL) {
            ret = true;
        }
        return ret;
    }

    private ArrayList getCustomerListSQL(String sql, int length) {

        try {
            sql = ServiceAFweb.getSQLLengh(sql, length);

            if (checkCallRemoteSQL_Mysql() == true) {
                ArrayList custList = remoteDB.getCustomerListSqlRemoteDB_RemoteMysql(sql);
                return custList;
            }

            List<CustomerObj> entries = new ArrayList<>();
            entries.clear();
            entries = this.jdbcTemplate.query(sql, new RowMapper() {
                public CustomerObj mapRow(ResultSet rs, int rowNum) throws SQLException {
                    CustomerObj customer = new CustomerObj();
                    customer.setId(rs.getInt("id"));
                    customer.setUsername(rs.getString("username"));
                    customer.setPassword(rs.getString("password"));
                    customer.setType(rs.getInt("type"));
                    customer.setStatus(rs.getInt("status"));
                    customer.setSubstatus(rs.getInt("substatus"));
                    customer.setStartdate(new java.sql.Date(rs.getDate("startdate").getTime()));
                    customer.setFirstname(rs.getString("firstname"));
                    customer.setLastname(rs.getString("lastname"));
                    customer.setEmail(rs.getString("email"));

                    customer.setUpdatedatel(rs.getLong("updatedatel"));
                    //entrydatedisplay not reliable. should use entrydatel
                    customer.setUpdatedatedisplay(new java.sql.Date(customer.getUpdatedatel()));

                    String tzid = "America/New_York"; //EDT
                    TimeZone tz = TimeZone.getTimeZone(tzid);
                    Date d = new Date(customer.getUpdatedatel());
                    DateFormat format = new SimpleDateFormat("M/dd/yyyy hh:mm a z");
                    format.setTimeZone(tz);
                    String ESTdate = format.format(d);
                    customer.setUpdateDateD(ESTdate);

                    return customer;
                }
            });

            return (ArrayList) entries;
        } catch (Exception e) {
            logger.info("> getCustomerList exception " + e.getMessage());
        }
        return null;
    }

    public CustomerObj getCustomer(String UserName, String Password) {
        CustomerObj customer = null;
        try {
            String sql = "select * from cust where username = '" + UserName + "'";

            if (Password != null) {
                sql += " and password = '" + Password + "'";
            }

            ArrayList<CustomerObj> entries = getCustomerListSQL(sql, 1);
            if (entries == null) {
                return null;
            }
            if (entries.size() != 0) {
                customer = entries.get(0);
                return customer;
            }
        } catch (Exception e) {
            logger.info("> getCustomer exception " + UserName + " - " + e.getMessage());
        }
        return null;
    }

    public static String SQLUupdateCustAllStatus(CustomerObj acc) {
        String sqlCMD = "update cust set status=" + acc.getStatus() + ",substatus=" + acc.getSubstatus()
                + " where id=" + acc.getId();
        return sqlCMD;
    }

    public int updateCustAllStatus(CustomerObj custObj) {
        String sqlCMD = SQLUupdateCustAllStatus(custObj);
        try {
            processUpdateDB(sqlCMD);
            return 1;
        } catch (Exception ex) {
            logger.info("> updateCustAllStatus exception " + ex.getMessage());
        }
        return 0;
    }

    public int updateCustStatus(CustomerObj custObj) {

        if (custObj == null) {
            return 0;
        }
        try {
            String sqlCMD = "update cust set status=" + custObj.getStatus() + ", substatus=" + custObj.getSubstatus() + " where id=" + custObj.getId();
            processUpdateDB(sqlCMD);
            return 1;
        } catch (Exception e) {
            logger.info("> updateCustStatus exception " + e.getMessage());
        }
        return 0;
    }

    public int updateCustomerUpdateDate(CustomerObj custObj) {

        if (custObj == null) {
            return 0;
        }
        if (custObj.getStatus() != ConstantKey.OPEN) {
            return 0;
        }
        if (custObj.getUsername().equals(CKey.ADMIN_USERNAME)) {
            return 0;
        }
        try {
            String sqlCMD = "update cust set updatedatedisplay='" + new java.sql.Date(custObj.getUpdatedatel()) + "', updatedatel=" + custObj.getUpdatedatel() + " where id=" + custObj.getId();
            processUpdateDB(sqlCMD);
            return 1;
        } catch (Exception e) {
            logger.info("> updateCustomerUpdateDate exception " + e.getMessage());
        }
        return 0;
    }

    public static String insertCustomer(CustomerObj newC) {

        String firstname = newC.getFirstname();
        if (firstname == null) {
            firstname = "";
        } else if (firstname.equals(ConstantKey.nullSt)) {
            firstname = "";
        }
        String lastname = newC.getLastname();
        if (lastname == null) {
            lastname = "";
        } else if (lastname.equals(ConstantKey.nullSt)) {
            lastname = "";
        }
        String email = newC.getEmail();
        if (email == null) {
            email = "";
        } else if (email.equals(ConstantKey.nullSt)) {
            email = "";
        }
        newC.setUpdatedatedisplay(new java.sql.Date(newC.getUpdatedatel()));
        String sqlCMD
                = "insert into cust(username, password, type, status, substatus, startdate, firstname, lastname,"
                + " email, updatedatedisplay, updatedatel, id) values "
                + "('" + newC.getUsername() + "','" + newC.getPassword() + "'," + newC.getType()
                + "," + newC.getStatus() + "," + newC.getSubstatus() + ",'" + newC.getStartdate() + "'"
                + ",'" + firstname + "','" + lastname + "'"
                + ",'" + email + "','" + newC.getUpdatedatedisplay() + "'," + newC.getUpdatedatel() + "," + newC.getId() + ")";
        return sqlCMD;
    }

    public int addCustomer(CustomerObj newCustomer) {
        try {

            Calendar dateNow = TimeConvertion.getCurrentCalendar();
            long dateNowLong = dateNow.getTimeInMillis();
            String userN = newCustomer.getUsername();
            userN = userN.toUpperCase();
            CustomerObj customer = getCustomer(userN, null);
            if (customer != null) {
                int status = customer.getStatus();
                // just for testing
//                status = ConstantKey.INITIAL;
                // just for testing
                if (status != ConstantKey.OPEN) {
                    customer.setStatus(ConstantKey.OPEN);
                    customer.setSubstatus(newCustomer.getSubstatus());
                    this.updateCustAllStatus(customer);

                }

                newCustomer.setUpdatedatedisplay(new java.sql.Date(dateNowLong));
                newCustomer.setUpdatedatel(dateNowLong);

                updateCustomerUpdateDate(customer);
                return ConstantKey.EXISTED;
            }

            String sqlCMD
                    = "insert into cust(username, password, type, status, substatus, startdate, firstname, lastname,"
                    + " email, updatedatedisplay, updatedatel) values "
                    + "('" + newCustomer.getUsername() + "','" + newCustomer.getPassword() + "'," + newCustomer.getType()
                    + "," + ConstantKey.OPEN + "," + newCustomer.getSubstatus() + ",'" + new java.sql.Date(dateNowLong) + "'"
                    + ",'" + newCustomer.getFirstname() + "','" + newCustomer.getLastname() + "'"
                    + ",'" + newCustomer.getEmail() + "','" + new java.sql.Date(dateNowLong) + "'," + dateNowLong + ")";

            processUpdateDB(sqlCMD);
            return ConstantKey.NEW;
        } catch (Exception e) {
            logger.info("> addCustomer exception " + e.getMessage());

        }
        return 0;
    }

///////////////////////////////////////////////////////
    public ArrayList getUserNamebyAccountID(int accountID) {
        String sql = "select username as username from account inner join cust on account.customerid = customer.id"
                + " where account.id =" + accountID;
        return getAllUserNameSQL(sql);
    }


    public ArrayList<CommObj> getComObjByName(int accountID, String name) {
        String sql = "select * from ssnscomm where accountid=" + accountID + " and name='" + name + "' ";
        sql += " order by updatedatel";
        return getCommBySQL(sql);
    }

    public ArrayList<CommObj> getComObjByCustAccountID(int customerID) {
        String sql = "select * from ssnscomm where customerid=" + customerID;
        sql += " order by updatedatel";
        return getCommBySQL(sql);
    }

    public ArrayList<CommObj> getComObjByID(int id) {
        String sql = "select * from ssnscomm where id=" + id;
        sql += " order by updatedatel";
        return getCommBySQL(sql);
    }

    private ArrayList getCommBySQL(String sql) {
        try {
            if (checkCallRemoteSQL_Mysql() == true) {
                ArrayList accList = remoteDB.getCommListSqlRemoteDB_RemoteMysql(sql);
                return accList;
            }
            List<CommObj> entries = new ArrayList<>();
            entries.clear();
            entries = this.jdbcTemplate.query(sql, new RowMapper() {
                public CommObj mapRow(ResultSet rs, int rowNum) throws SQLException {
                    CommObj comm = new CommObj();

                    comm.setId(rs.getInt("id"));
                    comm.setName(rs.getString("name"));
                    comm.setType(rs.getInt("type"));
                    comm.setStatus(rs.getInt("status"));
                    comm.setSubstatus(rs.getInt("substatus"));
                    comm.setUpdatedatel(rs.getLong("updatedatel"));
                    //entrydatedisplay not reliable. should use entrydatel
                    comm.setUpdatedatedisplay(new java.sql.Date(comm.getUpdatedatel()));

                    comm.setData(rs.getString("data"));
                    comm.setAccountid(rs.getInt("accountid"));
                    comm.setCustomerid(rs.getInt("customerid"));

                    return comm;
                }
            });

            return (ArrayList) entries;

        } catch (Exception e) {
            logger.info("> getCommBySQL exception " + sql + " - " + e.getMessage());

        }
        return null;
    }

    public String getAllSQLqueryDBSQL(String sql) {
        try {
            List retList = null;
            if (checkCallRemoteSQL_Mysql() == true) {
                String retST = remoteDB.getAllSQLqueryRemoteDB_RemoteMysql(sql);
                return retST;
            }

            retList = this.jdbcTemplate.queryForList(sql);
            String retST = new ObjectMapper().writeValueAsString(retList);
            return retST;

        } catch (Exception e) {
            logger.info("> getAllSQLqueryDBSQL " + e.getMessage());

        }
        return null;
    }

    public String getAllCommDBSQL(String sql) {
        try {
            ArrayList<CommObj> entries = getCommBySQL(sql);
            String nameST = new ObjectMapper().writeValueAsString(entries);
            return nameST;
        } catch (JsonProcessingException ex) {
        }
        return null;
    }

    public int DeleteCustomer(CustomerObj custObj) {

        try {
            String deleteSQL = "delete from customer where id=" + custObj.getId();
            processExecuteDB(deleteSQL);
            return 1;
        } catch (Exception e) {
            logger.info("> removeAccount exception " + e.getMessage());
        }
        return 0;
    }


    public int removeCustAccountCommByCommID(int id) {
        try {
            String deleteSQL = "delete from ssnscomm where id=" + id;
            processExecuteDB(deleteSQL);
            return 1;
        } catch (Exception e) {
            logger.info("> removeAccountCommByID exception " + e.getMessage());
        }
        return 0;
    }

    public int removeCustAccountComm(int CustID) {
        try {
            String deleteSQL = "delete from ssnscomm where customerid=" + CustID + " and type=" + ConstantKey.INT_COM_SIGNAL;
            processExecuteDB(deleteSQL);
            return 1;
        } catch (Exception e) {
            logger.info("> removeAccountComm exception " + e.getMessage());
        }
        return 0;
    }

    public int insertAccountCommData(CommObj newA) {

        String sqlCMD
                = "insert into ssnscomm( name, type, status, substatus, updatedatedisplay, updatedatel, data, accountid, customerid) values "
                + "('" + newA.getName() + "'," + newA.getType() + "," + newA.getStatus() + "," + newA.getSubstatus()
                + ",'" + newA.getUpdatedatedisplay() + "'," + newA.getUpdatedatel()
                + ",'" + newA.getData() + "'"
                + "," + newA.getAccountid() + "," + newA.getCustomerid() + ")";

        try {
            processExecuteDB(sqlCMD);
            return 1;
        } catch (Exception e) {
            logger.info("> updateAccountCommData exception " + e.getMessage());
        }
        return 0;
    }

    public int updateAccountCommData(CommObj newA) {
        String sqlCMD = "update ssnscomm set updatedatedisplay='" + new java.sql.Date(newA.getUpdatedatel()) + "', updatedatel=" + newA.getUpdatedatel() + ",data='" + newA.getData() + "' where id=" + newA.getId();
        try {
            processExecuteDB(sqlCMD);
            return 1;
        } catch (Exception e) {
            logger.info("> updateAccountCommData exception " + e.getMessage());
        }
        return 0;
    }

    public static String insertCommObj(CommObj newA) {
        newA.setUpdatedatedisplay(new java.sql.Date(newA.getUpdatedatel()));

        String sqlCMD
                = "insert into ssnscomm( name, type, status, substatus, updatedatedisplay, updatedatel, data, accountid, customerid, id) values "
                + "('" + newA.getName() + "'," + newA.getType() + "," + newA.getStatus() + "," + newA.getSubstatus()
                + ",'" + newA.getUpdatedatedisplay() + "'," + newA.getUpdatedatel()
                + ",'" + newA.getData() + "'"
                + "," + newA.getAccountid() + "," + newA.getCustomerid() + "," + newA.getId() + ")";

        return sqlCMD;
    }

    public ArrayList getAllIdSQL(String sql) {
        if (checkCallRemoteSQL_Mysql() == true) {
            ArrayList nnList;
            try {
                nnList = remoteDB.getAllIdSqlRemoteDB_RemoteMysql(sql);
                return nnList;
            } catch (Exception ex) {
            }
            return null;
        }

        try {
            List<String> entries = new ArrayList<>();
            entries.clear();
            entries = this.jdbcTemplate.query(sql, new RowMapper() {
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    String name = rs.getString("id");
                    return name;
                }
            });
            return (ArrayList) entries;
        } catch (Exception e) {
            logger.info("> getAllIdSQL exception " + e.getMessage());
        }
        return null;
    }

    public ArrayList getAllUserNameSQL(String sql) {
        if (checkCallRemoteSQL_Mysql() == true) {
            ArrayList nnList;
            try {
                nnList = remoteDB.getAllUserNameSqlRemoteDB_RemoteMysql(sql);
                return nnList;
            } catch (Exception ex) {
            }
            return null;
        }

        try {
            List<String> entries = new ArrayList<>();
            entries.clear();
            entries = this.jdbcTemplate.query(sql, new RowMapper() {
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    String name = rs.getString("username");
                    return name;
                }
            });
            return (ArrayList) entries;
        } catch (Exception e) {
            logger.info("> getAllUserNameSQL exception " + e.getMessage());
        }
        return null;
    }

    public int updateTransactionOrder(ArrayList transSQL) throws SQLException {
        if ((transSQL == null) || (transSQL.size() == 0)) {
            return 0;
        }
        if (true) {
            int ret = 1;
            for (int i = 0; i < transSQL.size(); i++) {
                String sql = (String) transSQL.get(i);
                try {
//                    logger.info("> updateTransactionOrder " + sql);

                    processUpdateDB(sql);
                } catch (Exception ex) {
                    ret = 0;
                    break;
                }
            }
            return ret;
        }

        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;
        try {
            dbConnection = this.dataSource.getConnection();
            dbConnection.setAutoCommit(false);

            for (int i = 0; i < transSQL.size(); i++) {
                String sql = (String) transSQL.get(i);
                preparedStatement = dbConnection.prepareStatement(sql);
                preparedStatement.executeUpdate();
            }
            dbConnection.commit();
            return 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            dbConnection.rollback();
            return 0;
        } finally {

            if (preparedStatement != null) {
                preparedStatement.close();
            }

            if (dbConnection != null) {
                dbConnection.close();
            }
        }
    }

    public void processUpdateDB(String sqlCMD) throws Exception {
        if (checkCallRemoteSQL_Mysql() == true) {
            int count = remoteDB.postExecuteRemoteDB_RemoteMysql(sqlCMD);
            return;
        }

//        logger.info("> processUpdateDB " + sqlCMD);
        getJdbcTemplate().update(sqlCMD);
    }

    public void processExecuteDB(String sqlCMD) throws Exception {
//       logger.info("> processExecuteDB " + sqlCMD);
        if (checkCallRemoteSQL_Mysql() == true) {
            int count = remoteDB.postExecuteRemoteDB_RemoteMysql(sqlCMD);
            return;
        }

        getJdbcTemplate().execute(sqlCMD);
    }

}
