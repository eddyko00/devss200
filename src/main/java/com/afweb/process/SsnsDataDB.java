/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.afweb.process;

import com.afweb.model.*;
import com.afweb.model.ssns.ProdSummary;

import com.afweb.service.ServiceAFweb;
import com.afweb.service.ServiceRemoteDB;
import com.afweb.service.db.Pram7RDB;

import com.afweb.util.*;
import static com.afweb.util.CKey.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import java.util.TimeZone;
import javax.sql.DataSource;
import java.util.logging.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author eddy
 *
 * mysql -u sa -p show databases; use db_sample; show tables; drop table
 * lockObject;
 *
 *
 *
 */
public class SsnsDataDB {

    protected static Logger logger = Logger.getLogger("SsnsdDataDB");

    static public int MaxMinuteAdminSignalTrading = 90;
    static public int Max2HAdmin = 120;
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

    public static boolean checkCallRemoveSQL_Mysql() {
        boolean ret = false;
        if (CKey.SQL_DATABASE == CKey.REMOTE_MYSQL) {
            ret = true;
        }
        return ret;
    }

    private boolean ExecuteSQLArrayList(ArrayList SQLTran) {
        String SQL = "";
        try {
            for (int i = 0; i < SQLTran.size(); i++) {
                SQL = (String) SQLTran.get(i);
                logger.info("> ExecuteSQLArrayList " + SQL);
                processExecuteDB(SQL);
            }
            return true;
        } catch (Exception e) {
            logger.info("> ExecuteSQLArrayList exception " + e.getMessage());
        }
        return false;

    }

    public int updateSQLArrayList(ArrayList SQLTran) {

        if (checkCallRemoveSQL_Mysql() == true) {
            // just for testing
//            if (CKey.SQL_DATABASE == CKey.REMOTE_MYSQL) {
//                boolean result = ExecuteSQLArrayList(SQLTran);
//                if (result == true) {
//                    return 1;
//                }
//                return 0;
//            }

            int ret = remoteDB.getExecuteRemoteListDB_Mysql(SQLTran);
            if (ret == 0) {
                return 0;
            }
            return 1;
        }

        try {
            for (int i = 0; i < SQLTran.size(); i++) {
                String SQL = (String) SQLTran.get(i);
                getJdbcTemplate().update(SQL);
                if ((i % 100) == 0) {
                    ServiceAFweb.AFSleep();
                }
            }
            ServiceAFweb.AFSleep();
            return 1;
        } catch (Exception e) {
            logger.info("> UpdateSQLlList exception " + e.getMessage());
        }
        return 0;

    }

    ///////////
    public int getCountRowsInTable(JdbcTemplate jdbcTemplate, String tableName) throws Exception {
        if (checkCallRemoveSQL_Mysql() == true) {
            int count = remoteDB.getCountRowsRemoteDB_RemoteMysql(tableName);
            return count;
        }

        Integer result = jdbcTemplate.queryForObject("select count(0) from " + tableName, Integer.class
        );
        return (result != null ? result : 0);
    }

    public int processUpdateDB(String sqlCMD) throws Exception {
        if (checkCallRemoveSQL_Mysql() == true) {
            int ret = remoteDB.postExecuteRemoteDB_RemoteMysql(sqlCMD);
            return ret;
        }
//        logger.info("> processUpdateDB " + sqlCMD);
        getJdbcTemplate().update(sqlCMD);
        return 1;
    }

    public void processExecuteDB(String sqlCMD) throws Exception {
//        logger.info("> processExecuteDB " + sqlCMD);

        if (checkCallRemoveSQL_Mysql() == true) {
            int count = remoteDB.postExecuteRemoteDB_RemoteMysql(sqlCMD);
            return;
        }

        getJdbcTemplate().execute(sqlCMD);
    }

    public boolean restSsnsDataDB() {
        boolean status = true;
        try {
            processExecuteDB("drop table if exists dummy1");
        } catch (Exception e) {
            logger.info("> restSsnsDataDB Table exception " + e.getMessage());
            status = false;
        }
        return status;
    }

    public boolean cleanSsnsDataDB() {
        try {
            processExecuteDB("drop table if exists ssnsdummy");
            int result = initSsnsDataDB();
            if (result == -1) {
                return false;
            }
            processExecuteDB("drop table if exists ssnsdummy");
            return true;
        } catch (Exception e) {
            logger.info("> cleanSsnsDataDB Table exception " + e.getMessage());
        }
        return false;
    }

    public static String createDummytable() {
        String sqlCMD = "";
        if ((CKey.SQL_DATABASE == CKey.MYSQL) || (CKey.SQL_DATABASE == CKey.REMOTE_MYSQL) || (CKey.SQL_DATABASE == CKey.LOCAL_MYSQL)) {
            if (CKey.DB == CKey.POSTGRESQLDB) {
                sqlCMD = "create table ssnsdummy (id int not null primary key)";
            } else if (CKey.DB == CKey.MYSQLDB) {
                sqlCMD = "create table ssnsdummy (id int(10) not null auto_increment, primary key (id))";
            }
        }
        return sqlCMD;
    }

    public int testSsnsDataDB() {

        int total = 0;
        logger.info(">>>>> testSsnsDataDB");
        try {
            total = getCountRowsInTable(getJdbcTemplate(), "ssnsdummy");
            if (total >= 0) {
                return 1;  // already exist
            }
        } catch (Exception e) {
            logger.info("> testSsnsDataDB Table exception " + e.getMessage());
        }
        return -1;
    }

    public int initSsnsDataDB() {

        int total = 0;
        logger.info(">>>>> initSsnsDataDB Table creation");
        try {
//            processExecuteDB("create table ssnsdummy (id int not null primary key)");

            boolean initDBflag = false;
            if (initDBflag == true) {
                processExecuteDB("drop table if exists ssnsdummy");
            }
            total = getCountRowsInTable(getJdbcTemplate(), "ssnsdummy");
        } catch (Exception e) {
            logger.info("> initSsnsDataDB Table exception");
            total = -1;
        }
        if (total >= 0) {
            return 1;  // already exist
        }

        try {

            processExecuteDB("drop table if exists ssnsdummy1");
            String sqlCMD = "";
            if (CKey.DB == CKey.POSTGRESQLDB) {
                sqlCMD = "create table ssnsdummy1 (id int not null primary key)";
            } else if (CKey.DB == CKey.MYSQLDB) {
                sqlCMD = "create table ssnsdummy1 (id int(10) not null auto_increment, primary key (id))";
            }
            processExecuteDB(sqlCMD);
            total = getCountRowsInTable(getJdbcTemplate(), "ssnsdummy1");
            if (total == -1) {
                return -1;
            }

// sequency is important
            ArrayList dropTableList = new ArrayList();
            dropTableList.add("drop table if exists ssnsdummy");
            dropTableList.add("drop table if exists ssnslock");
            dropTableList.add("drop table if exists cust");
            dropTableList.add("drop table if exists ssnscomm");
            dropTableList.add("drop table if exists ssnsdata");
            dropTableList.add("drop table if exists ssnsacc");
            dropTableList.add("drop table if exists ssreport");
            boolean resultDrop = ExecuteSQLArrayList(dropTableList);
//            int resultDropList = updateSQLArrayList(dropTableList);

            ArrayList createTableList = new ArrayList();

            if (CKey.DB == CKey.POSTGRESQLDB) {
                //https://www.postgresql.org/docs/9.2/datatype.html
                createTableList.add("create table ssnsdummy (id int not null primary key)");
                ExecuteSQLArrayList(createTableList);
                createTableList.clear();
                //https://kb.objectrocket.com/postgresql/autoincrement-in-postgres-using-serial-1288
                createTableList.add("CREATE SEQUENCE ssnslockIdSeq");
                createTableList.add("create table ssnslock (id int not null primary key DEFAULT NEXTVAL('ssnslockIdSeq'), lockname varchar(255) not null unique, type int not null, lockdatedisplay date, lockdatel bigint, comment varchar(255))");
                createTableList.add("ALTER SEQUENCE ssnslockIdSeq OWNED BY ssnslock.id");
                ExecuteSQLArrayList(createTableList);
                createTableList.clear();

                createTableList.add("CREATE SEQUENCE custIdSeq");
                createTableList.add("create table cust (id int not null primary key DEFAULT NEXTVAL('custIdSeq'), username varchar(255) not null unique, password varchar(255) not null, type int not null, status int not null, substatus int not null, startdate date, firstname varchar(255), lastname varchar(255), email varchar(255), updatedatedisplay date, updatedatel bigint not null)");
                createTableList.add("ALTER SEQUENCE custIdSeq OWNED BY cust.id");
                ExecuteSQLArrayList(createTableList);
                createTableList.clear();

                createTableList.add("CREATE SEQUENCE ssnscommIdSeq");
                createTableList.add("create table ssnscomm (id int not null primary key DEFAULT NEXTVAL('ssnscommIdSeq'), name varchar(255) not null, type int not null, status int not null, substatus int not null, updatedatedisplay date, updatedatel bigint not null, data text, accountid int not null, customerid int not null)");
                createTableList.add("ALTER SEQUENCE ssnscommIdSeq OWNED BY ssnscomm.id");
                ExecuteSQLArrayList(createTableList);
                createTableList.clear();

                createTableList.add("CREATE SEQUENCE ssnsdataIdSeq");
                createTableList.add("create table ssnsdata (id int not null primary key DEFAULT NEXTVAL('ssnsdataIdSeq'), name varchar(255) not null, status int not null, type int not null,"
                        + " uid varchar(255), cusid varchar(255), banid varchar(255), tiid varchar(255) ,app varchar(255), oper varchar(255), down varchar(255), ret varchar(255), exec  bigint, "
                        + "data text,  updatedatedisplay date, updatedatel bigint not null)");
                createTableList.add("ALTER SEQUENCE ssnsdataIdSeq OWNED BY ssnsdata.id");
                ExecuteSQLArrayList(createTableList);
                createTableList.clear();

                createTableList.add("CREATE SEQUENCE ssnsaccIdSeq");
                createTableList.add("create table ssnsacc (id int not null primary key DEFAULT NEXTVAL('ssnsaccIdSeq'), name varchar(255) not null, status int not null, type int not null,"
                        + " uid varchar(255), cusid varchar(255), banid varchar(255), tiid varchar(255) ,app varchar(255), oper varchar(255), down varchar(255), ret varchar(255), exec  bigint, "
                        + "data text,  updatedatedisplay date, updatedatel bigint not null)");
                createTableList.add("ALTER SEQUENCE ssnsaccIdSeq OWNED BY ssnsacc.id");
                ExecuteSQLArrayList(createTableList);
                createTableList.clear();

                createTableList.add("CREATE SEQUENCE ssreportIdSeq");
                createTableList.add("create table ssreport (id int not null primary key DEFAULT NEXTVAL('ssreportIdSeq'), name varchar(255) not null, status int not null, type int not null,"
                        + " uid varchar(255), cusid varchar(255), banid varchar(255), tiid varchar(255) ,app varchar(255), oper varchar(255), down varchar(255), ret varchar(255), exec  bigint, "
                        + "data text,  updatedatedisplay date, updatedatel bigint not null)");
                createTableList.add("ALTER SEQUENCE ssreportIdSeq OWNED BY ssreport.id");
                ExecuteSQLArrayList(createTableList);
                createTableList.clear();

            } else if (CKey.DB == CKey.MYSQLDB) {
                createTableList.add("create table ssnsdummy (id int(10) not null auto_increment, primary key (id))");
                createTableList.add("create table ssnslock (id int(10) not null auto_increment, lockname varchar(255) not null unique, type int(10) not null, lockdatedisplay date, lockdatel bigint(20), comment varchar(255), primary key (id))");
                createTableList.add("create table cust (id int(10) not null auto_increment, username varchar(255) not null unique, password varchar(255) not null, type int(10) not null, status int(10) not null, substatus int(10) not null, startdate date, firstname varchar(255), lastname varchar(255), email varchar(255), updatedatedisplay date, updatedatel bigint(20) not null, primary key (id))");
                createTableList.add("create table ssnscomm (id int(10) not null auto_increment, name varchar(255) not null, type int(10) not null, status int(10) not null, substatus int(10) not null, updatedatedisplay date, updatedatel bigint(20) not null, data text, accountid int(10) not null, customerid int(10) not null, primary key (id))");
                createTableList.add("create table ssnsdata (id int(10) not null auto_increment, name varchar(255) not null, status int(10) not null, type int(10) not null,"
                        + " uid varchar(255), cusid varchar(255), banid varchar(255), tiid varchar(255) ,app varchar(255), oper varchar(255), down varchar(255), ret varchar(255), exec  bigint(20), "
                        + "data text,  updatedatedisplay date, updatedatel bigint(20) not null, primary key (id))");
                createTableList.add("create table ssnsacc (id int(10) not null auto_increment, name varchar(255) not null, status int(10) not null, type int(10) not null,"
                        + " uid varchar(255), cusid varchar(255), banid varchar(255), tiid varchar(255) ,app varchar(255), oper varchar(255), down varchar(255), ret varchar(255), exec  bigint(20), "
                        + "data text,  updatedatedisplay date, updatedatel bigint(20) not null, primary key (id))");
                createTableList.add("create table ssreport (id int(10) not null auto_increment, name varchar(255) not null, status int(10) not null, type int(10) not null,"
                        + " uid varchar(255), cusid varchar(255), banid varchar(255), tiid varchar(255) ,app varchar(255), oper varchar(255), down varchar(255), ret varchar(255), exec  bigint(20), "
                        + "data text,  updatedatedisplay date, updatedatel bigint(20) not null, primary key (id))");

            }
            boolean resultCreate = ExecuteSQLArrayList(createTableList);

            logger.info("> initSsnsDataDB Done - result " + resultCreate);
            total = getCountRowsInTable(getJdbcTemplate(), "ssnsdummy");
            return 0;  // new database

        } catch (Exception e) {
            logger.info("> initSsnsDataDB Table exception " + e.getMessage());
        }
        return -1;
    }

    ////////////////////////
    public int deleteAllLock() {

        try {
            String deleteSQL = "delete from ssnslock";
            processExecuteDB(deleteSQL);
            return 1;
        } catch (Exception e) {
            logger.info("> DeleteAllLock exception " + e.getMessage());
        }
        return 0;
    }

    public String getAllLockDBSQL(String sql) {
        try {
            ArrayList<AFLockObject> entries = getAllLockObjSQL(sql);
            String nameST = new ObjectMapper().writeValueAsString(entries);
            return nameST;
        } catch (JsonProcessingException ex) {
        }
        return null;
    }

    public ArrayList getAllLock() {
        String sql = "select * from ssnslock";
        ArrayList entries = getAllLockObjSQL(sql);
        return entries;
    }

    public AFLockObject getLockName(String name, int type) {
        String sql = "select * from ssnslock where lockname='" + name + "' and type=" + type;
        ArrayList entries = getAllLockObjSQL(sql);
        if (entries != null) {
            if (entries.size() == 1) {
                AFLockObject lock = (AFLockObject) entries.get(0);
                return lock;
            }
        }
        return null;
    }

    private ArrayList getAllLockObjSQL(String sql) {
        if (checkCallRemoveSQL_Mysql() == true) {
            ArrayList lockList;
            try {
                lockList = remoteDB.getAllLockSqlRemoteDB_RemoteMysql(sql);
                return lockList;
            } catch (Exception ex) {

            }
            return null;
        }

        try {
            List<AFLockObject> entries = new ArrayList<>();
            entries.clear();
            entries = this.jdbcTemplate.query(sql, new RowMapper() {
                public AFLockObject mapRow(ResultSet rs, int rowNum) throws SQLException {
                    AFLockObject lock = new AFLockObject();
                    lock.setLockname(rs.getString("lockname"));
                    lock.setType(rs.getInt("type"));
                    lock.setLockdatedisplay(new java.sql.Date(rs.getDate("lockdatedisplay").getTime()));
                    lock.setLockdatel(Long.parseLong(rs.getString("lockdatel")));
                    lock.setId(rs.getInt("id"));
                    lock.setComment(rs.getString("comment"));

                    String tzid = "America/New_York"; //EDT
                    TimeZone tz = TimeZone.getTimeZone(tzid);
                    Date d = new Date(lock.getLockdatel());
                    DateFormat format = new SimpleDateFormat("M/dd/yyyy hh:mm a z");
                    format.setTimeZone(tz);
                    String ESTdate = format.format(d);
                    lock.setUpdateDateD(ESTdate);

                    return lock;
                }
            });
            return (ArrayList) entries;
        } catch (Exception e) {
            logger.info("> getAllLockObjSQL exception " + e.getMessage());
        }
        return null;
    }

    public int setRenewLock(String name, int type, long lockDateValue) {

        try {
            AFLockObject lock = getLockName(name, type);

            if (lock == null) {
                return 0;
            }
            String sqlCMD = "update ssnslock set lockdatedisplay='" + new java.sql.Date(lockDateValue) + "', lockdatel=" + lockDateValue + " where id=" + lock.getId();
            return processUpdateDB(sqlCMD);

        } catch (Exception ex) {
            logger.info("> setRenewLock exception " + ex.getMessage());
        }
        return 0;
    }

    private int setLockObject(String name, int type, long lockDateValue, String comment) {
        try {
            String sqlCMD = "insert into ssnslock (lockname, type, lockdatedisplay, lockdatel, comment) VALUES "
                    + "('" + name + "'," + type + ",'" + new java.sql.Date(lockDateValue) + "'," + lockDateValue + ",'" + comment + "')";
            return processUpdateDB(sqlCMD);

        } catch (Exception e) {
            logger.info("> setLockObject exception " + name + " - " + e.getMessage());
        }
        return 0;
    }

    public int setLockName(String name, int type, long lockDateValue, String comment) {

        try {
            AFLockObject lock = getLockName(name, type);
            if (lock == null) {
                return setLockObject(name, type, lockDateValue, comment);
            }

            int allowTime = 3; // default 3 minutes

            if (type == ConstantKey.SRV_LOCKTYPE) {
                allowTime = 10; // 10 minutes 
            } else if (type == ConstantKey.ETL_LOCKTYPE) {
                allowTime = MaxMinuteAdminSignalTrading; // 90 minutes 
            } else if (type == ConstantKey.H2_LOCKTYPE) {
                allowTime = Max2HAdmin; // 100 minutes 

            }
            long lockDate = lock.getLockdatel();
            long lockDate10Min = TimeConvertion.addMinutes(lockDate, allowTime);

            if (lockDate10Min > lockDateValue) {
                return 0;
            }
            removeLock(name, type);

        } catch (Exception ex) {
            logger.info("> SetLockName exception " + ex.getMessage());
        }
        return 0;
    }

    public int removeLock(String name, int type) {

        try {
            String sqlDelete = "delete from ssnslock where lockname='" + name + "' and type=" + type;
            this.processExecuteDB(sqlDelete);
            return 1;
        } catch (Exception ex) {
            logger.info("> removeLock exception " + ex.getMessage());
        }
        return 0;
    }

    ///////////////
    public int deleteSsnsAcc(String name) {
        try {
            String deleteSQL = "delete from ssnsacc where name='" + name + "'";
            return processUpdateDB(deleteSQL);
        } catch (Exception e) {
            logger.info("> deleteSsnsData exception " + e.getMessage());
        }
        return 0;
    }

    public int deleteSsnsAccApp(String app) {
        try {
            String deleteSQL = "delete from ssnsacc where app='" + app + "'";
            return processUpdateDB(deleteSQL);
        } catch (Exception e) {
            logger.info("> deleteSsnsAccApp exception " + e.getMessage());
        }
        return 0;
    }

    public int deleteSsnsDataApp(String app) {
        try {
            String deleteSQL = "delete from ssnsdata where app='" + app + "'";
            return processUpdateDB(deleteSQL);
        } catch (Exception e) {
            logger.info("> deleteSsnsData exception " + e.getMessage());
        }
        return 0;
    }

    public int updateSsnsDataOpenStatus(String app) {
        try {
            String sqlCMD = "update ssnsdata set status=" + ConstantKey.OPEN
                    + " where app='" + app + "'";
            return processUpdateDB(sqlCMD);
        } catch (Exception e) {
            logger.info("> updateSsnsDataOpenStatus exception " + e.getMessage());
        }
        return 0;
    }

    public int updateSsnsDataAllOpenStatus() {
        try {
            String sqlCMD = "update ssnsdata set status=" + ConstantKey.OPEN
                    + " where id>0";
            return processUpdateDB(sqlCMD);
        } catch (Exception e) {
            logger.info("> updateSsnsDataAllOpenStatus exception " + e.getMessage());
        }
        return 0;
    }

    public int deleteSsnsData(String name) {
        try {
            String deleteSQL = "delete from ssnsdata where name='" + name + "'";
            return processUpdateDB(deleteSQL);
        } catch (Exception e) {
            logger.info("> deleteSsnsData exception " + e.getMessage());
        }
        return 0;
    }

//    public static String insertSsReport(String table, SsReport newN) {
//        String dataSt = newN.getData();
//
//        dataSt = dataSt.replaceAll("|", "");
//        dataSt = dataSt.replaceAll("'", "");
//        dataSt = dataSt.replaceAll("\"", "#");
//        newN.setUpdatedatedisplay(new java.sql.Date(newN.getUpdatedatel()));
//        String sqlCMD = "insert into " + table + " (name, status, type, uid,cusid,banid,tiid,app,oper,down,ret,exec, data, updatedatedisplay, updatedatel, id) VALUES "
//                + "('" + newN.getName() + "'," + newN.getStatus() + "," + newN.getType()
//                + ",'" + newN.getUid() + "','" + newN.getCusid() + "','" + newN.getBanid() + "','" + newN.getTiid() + "','" + newN.getApp() + "','" + newN.getOper() + "','" + newN.getDown() + "','" + newN.getRet() + "'," + newN.getExec()
//                + ",'" + dataSt + "'"
//                + ",'" + newN.getUpdatedatedisplay() + "'," + newN.getUpdatedatel() + "," + newN.getId() + ")";
//        return sqlCMD;
//    }
//    
    public static String insertSsReportObjectSQL(SsReport nData) {
        String dataSt = nData.getData();
        dataSt = dataSt.replaceAll("|", "");
        dataSt = dataSt.replaceAll("'", "");
        dataSt = dataSt.replaceAll("\"", "#");
        String sqlCMD = "insert into ssreport (name, status, type, uid,cusid,banid,tiid,app,oper,down,ret,exec, data, updatedatedisplay, updatedatel) VALUES "
                + "('" + nData.getName() + "'," + nData.getStatus() + "," + nData.getType()
                + ",'" + nData.getUid() + "','" + nData.getCusid() + "','" + nData.getBanid() + "','" + nData.getTiid() + "','" + nData.getApp() + "','" + nData.getOper() + "','" + nData.getDown() + "','" + nData.getRet() + "'," + nData.getExec()
                + ",'" + dataSt + "'"
                + ",'" + new java.sql.Date(nData.getUpdatedatel()) + "'," + nData.getUpdatedatel() + ")";
        return sqlCMD;
    }

    public static String insertSsnsDataAcc(String table, SsnsData newN) {
        String dataSt = newN.getData();

        dataSt = dataSt.replaceAll("|", "");
        dataSt = dataSt.replaceAll("'", "");
        dataSt = dataSt.replaceAll("\"", "#");
        newN.setUpdatedatedisplay(new java.sql.Date(newN.getUpdatedatel()));
        String sqlCMD = "insert into " + table + " (name, status, type, uid,cusid,banid,tiid,app,oper,down,ret,exec, data, updatedatedisplay, updatedatel, id) VALUES "
                + "('" + newN.getName() + "'," + newN.getStatus() + "," + newN.getType()
                + ",'" + newN.getUid() + "','" + newN.getCusid() + "','" + newN.getBanid() + "','" + newN.getTiid() + "','" + newN.getApp() + "','" + newN.getOper() + "','" + newN.getDown() + "','" + newN.getRet() + "'," + newN.getExec()
                + ",'" + dataSt + "'"
                + ",'" + newN.getUpdatedatedisplay() + "'," + newN.getUpdatedatel() + "," + newN.getId() + ")";
        return sqlCMD;
    }

    public static String insertSsnsDataObjectSQL(SsnsData nData) {
        String dataSt = nData.getData();
        dataSt = dataSt.replaceAll("|", "");
        dataSt = dataSt.replaceAll("'", "");
        dataSt = dataSt.replaceAll("\"", "#");
        String sqlCMD = "insert into ssnsdata (name, status, type, uid,cusid,banid,tiid,app,oper,down,ret,exec, data, updatedatedisplay, updatedatel) VALUES "
                + "('" + nData.getName() + "'," + nData.getStatus() + "," + nData.getType()
                + ",'" + nData.getUid() + "','" + nData.getCusid() + "','" + nData.getBanid() + "','" + nData.getTiid() + "','" + nData.getApp() + "','" + nData.getOper() + "','" + nData.getDown() + "','" + nData.getRet() + "'," + nData.getExec()
                + ",'" + dataSt + "'"
                + ",'" + new java.sql.Date(nData.getUpdatedatel()) + "'," + nData.getUpdatedatel() + ")";
        return sqlCMD;
    }

    public static String insertSsnsAccObjectSQL(SsnsAcc nData) {
        String dataSt = nData.getData();
        dataSt = dataSt.replaceAll("|", "");
        dataSt = dataSt.replaceAll("'", "");
        dataSt = dataSt.replaceAll("\"", "#");
        String sqlCMD = "insert into ssnsacc (name, status, type, uid,cusid,banid,tiid,app,oper,down,ret,exec, data, updatedatedisplay, updatedatel) VALUES "
                + "('" + nData.getName() + "'," + nData.getStatus() + "," + nData.getType()
                + ",'" + nData.getUid() + "','" + nData.getCusid() + "','" + nData.getBanid() + "','" + nData.getTiid() + "','" + nData.getApp() + "','" + nData.getOper() + "','" + nData.getDown() + "','" + nData.getRet() + "'," + nData.getExec()
                + ",'" + dataSt + "'"
                + ",'" + new java.sql.Date(nData.getUpdatedatel()) + "'," + nData.getUpdatedatel() + ")";
        return sqlCMD;
    }

    public int updatSsnsDataStatusById(int id, int status) {
        try {
            String sqlCMD = "update ssnsdata set status=" + status
                    + " where id=" + id;
            return processUpdateDB(sqlCMD);
        } catch (Exception e) {
            logger.info("> updatSsnsDataStatusById exception " + e.getMessage());
        }
        return 0;
    }

    public int insertSsnsAccObject(SsnsAcc nData) {
        try {
            String sqlCMD = insertSsnsAccObjectSQL(nData);
            return processUpdateDB(sqlCMD);

        } catch (Exception e) {
            logger.info("> insertSsnsAccObject exception " + nData.getName() + " - " + e.getMessage());
        }
        return 0;
    }

    public int insertSsReportObject(SsReport nData) {
        try {
            String sqlCMD = insertSsReportObjectSQL(nData);
            return processUpdateDB(sqlCMD);

        } catch (Exception e) {
            logger.info("> insertSsnsAccObject exception " + nData.getName() + " - " + e.getMessage());
        }
        return 0;
    }

    public int updatSsReportDataStatusTypeRetById(int id, String dataSt, int status, int type, String ret) {
        try {

            dataSt = dataSt.replaceAll("|", "");
            dataSt = dataSt.replaceAll("'", "");
            dataSt = dataSt.replaceAll("\"", "#");
            String sqlCMD = "update ssreport set data='" + dataSt + "', status=" + status + ", type=" + type
                    + ", ret='" + ret + "'"
                    + " where id=" + id;
            return processUpdateDB(sqlCMD);
        } catch (Exception e) {
            logger.info("> updatSsReportDataStatusTypeById exception " + e.getMessage());
        }
        return 0;
    }

    public int updatSsReportDataStatusTypeById(int id, String dataSt, int status, int type) {
        try {

            dataSt = dataSt.replaceAll("|", "");
            dataSt = dataSt.replaceAll("'", "");
            dataSt = dataSt.replaceAll("\"", "#");
            String sqlCMD = "update ssreport set data='" + dataSt + "', status=" + status + ", type=" + type
                    + " where id=" + id;
            return processUpdateDB(sqlCMD);
        } catch (Exception e) {
            logger.info("> updatSsReportDataStatusTypeById exception " + e.getMessage());
        }
        return 0;
    }

    public int updatSsnsAccNameStatusTypeById(int id, String name, int status, int type) {
        try {
            String sqlCMD = "update ssnsacc set name='" + name + "', status=" + status + ", type=" + type
                    + " where id=" + id;
            return processUpdateDB(sqlCMD);
        } catch (Exception e) {
            logger.info("> updatSsnsAccNameStatusById exception " + e.getMessage());
        }
        return 0;
    }

    public String getAllSsnsDataDBSQL(String sql, int length) {
        try {
            ArrayList<SsnsData> entries = getAllSsnsDataSQL(sql, length);
            String nameST = new ObjectMapper().writeValueAsString(entries);
            return nameST;
        } catch (JsonProcessingException ex) {
        }
        return null;
    }

    private ArrayList<SsnsAcc> getAllSsnsAccSQL(String sql, int length) {
        sql = ServiceAFweb.getSQLLengh(sql, length);
        if (checkCallRemoveSQL_Mysql() == true) {
            ArrayList nnList;
            try {
                nnList = remoteDB.getAllSsnsAccSqlRemoteDB_RemoteMysql(sql);
                return nnList;
            } catch (Exception ex) {
            }
            return null;
        }

        try {
            List<SsnsAcc> entries = new ArrayList<>();
            entries.clear();
            entries = this.jdbcTemplate.query(sql, new RowMapper() {
                public SsnsAcc mapRow(ResultSet rs, int rowNum) throws SQLException {
                    SsnsAcc nn = new SsnsAcc();
                    nn.setId(rs.getInt("id"));
                    nn.setName(rs.getString("name"));
                    nn.setStatus(rs.getInt("status"));
                    nn.setType(rs.getInt("type"));

                    nn.setUid(rs.getString("uid"));
                    nn.setCusid(rs.getString("cusid"));
                    nn.setBanid(rs.getString("banid"));
                    nn.setTiid(rs.getString("tiid"));

                    nn.setApp(rs.getString("app"));
                    nn.setOper(rs.getString("oper"));
                    nn.setDown(rs.getString("down"));
                    nn.setRet(rs.getString("ret"));
                    nn.setExec(rs.getLong("exec"));

                    String stData = rs.getString("data");
                    stData = stData.replaceAll("#", "\"");
                    nn.setData(stData);

                    nn.setUpdatedatedisplay(new java.sql.Date(rs.getDate("updatedatedisplay").getTime()));
                    nn.setUpdatedatel(rs.getLong("updatedatel"));

                    return nn;
                }
            });
            return (ArrayList) entries;
        } catch (Exception e) {
            logger.info("> getAllSsnsDataSQL exception " + e.getMessage());
        }
        return null;
    }

    private ArrayList<SsReport> getAllSsReportSQL(String sql, int length) {
        sql = ServiceAFweb.getSQLLengh(sql, length);
        if (checkCallRemoveSQL_Mysql() == true) {
            ArrayList nnList;
            try {
                nnList = remoteDB.getAllSsReportSqlRemoteDB_RemoteMysql(sql);
                return nnList;
            } catch (Exception ex) {
            }
            return null;
        }

        try {
            List<SsReport> entries = new ArrayList<>();
            entries.clear();
            entries = this.jdbcTemplate.query(sql, new RowMapper() {
                public SsReport mapRow(ResultSet rs, int rowNum) throws SQLException {
                    SsReport nn = new SsReport();
                    nn.setId(rs.getInt("id"));
                    nn.setName(rs.getString("name"));
                    nn.setStatus(rs.getInt("status"));
                    nn.setType(rs.getInt("type"));

                    nn.setUid(rs.getString("uid"));
                    nn.setCusid(rs.getString("cusid"));
                    nn.setBanid(rs.getString("banid"));
                    nn.setTiid(rs.getString("tiid"));

                    nn.setApp(rs.getString("app"));
                    nn.setOper(rs.getString("oper"));
                    nn.setDown(rs.getString("down"));
                    nn.setRet(rs.getString("ret"));
                    nn.setExec(rs.getLong("exec"));

                    String stData = rs.getString("data");
                    stData = stData.replaceAll("#", "\"");
                    nn.setData(stData);

                    nn.setUpdatedatedisplay(new java.sql.Date(rs.getDate("updatedatedisplay").getTime()));
                    nn.setUpdatedatel(rs.getLong("updatedatel"));

                    return nn;
                }
            });
            return (ArrayList) entries;
        } catch (Exception e) {
            logger.info("> getAllSsReportSQL exception " + e.getMessage());
        }
        return null;
    }

    private ArrayList<SsnsData> getAllSsnsDataSQL(String sql, int length) {
        sql = ServiceAFweb.getSQLLengh(sql, length);
        if (checkCallRemoveSQL_Mysql() == true) {
            ArrayList nnList;
            try {
                nnList = remoteDB.getAllSsnsDataSqlRemoteDB_RemoteMysql(sql);
                return nnList;
            } catch (Exception ex) {
            }
            return null;
        }

        try {
            List<SsnsData> entries = new ArrayList<>();
            entries.clear();
            entries = this.jdbcTemplate.query(sql, new RowMapper() {
                public SsnsData mapRow(ResultSet rs, int rowNum) throws SQLException {
                    SsnsData nn = new SsnsData();
                    nn.setId(rs.getInt("id"));
                    nn.setName(rs.getString("name"));
                    nn.setStatus(rs.getInt("status"));
                    nn.setType(rs.getInt("type"));

                    nn.setUid(rs.getString("uid"));
                    nn.setCusid(rs.getString("cusid"));
                    nn.setBanid(rs.getString("banid"));
                    nn.setTiid(rs.getString("tiid"));

                    nn.setApp(rs.getString("app"));
                    nn.setOper(rs.getString("oper"));
                    nn.setDown(rs.getString("down"));
                    nn.setRet(rs.getString("ret"));
                    nn.setExec(rs.getLong("exec"));

                    String stData = rs.getString("data");
                    stData = stData.replaceAll("#", "\"");
                    nn.setData(stData);

                    nn.setUpdatedatedisplay(new java.sql.Date(rs.getDate("updatedatedisplay").getTime()));
                    nn.setUpdatedatel(rs.getLong("updatedatel"));

                    return nn;
                }
            });
            return (ArrayList) entries;
        } catch (Exception e) {
            logger.info("> getAllSsnsDataSQL exception " + e.getMessage());
        }
        return null;
    }

    public int deleteAllSsReport(int month) {
        try {
            String deleteSQL = "delete from ssreport";
            if (month == 0) {
                ;
            } else {
                Calendar dateNow = TimeConvertion.getCurrentCalendar();
                long dateNowLong = dateNow.getTimeInMillis();
                long monthAge = TimeConvertion.addMonths(dateNowLong, -month);
                deleteSQL = "delete from ssreport where updatedatel < " + monthAge;
            }
            processExecuteDB(deleteSQL);
            return 1;
        } catch (Exception e) {
            logger.info("> deleteAllSsReport exception " + e.getMessage());
        }
        return 0;
    }

    public int deleteAllSsnsAcc(int month) {
        try {
            String deleteSQL = "delete from ssnsacc";
            if (month == 0) {
                ;
            } else {
                Calendar dateNow = TimeConvertion.getCurrentCalendar();
                long dateNowLong = dateNow.getTimeInMillis();
                long monthAge = TimeConvertion.addMonths(dateNowLong, -month);
                deleteSQL = "delete from ssnsacc where updatedatel < " + monthAge;
            }
            processExecuteDB(deleteSQL);
            return 1;
        } catch (Exception e) {
            logger.info("> deleteAllSsnsAcc exception " + e.getMessage());
        }
        return 0;
    }

    public int deleteAllSsnsData(int month) {
        try {
            String deleteSQL = "delete from ssnsdata";
            if (month == 0) {
                ;
            } else {
                Calendar dateNow = TimeConvertion.getCurrentCalendar();
                long dateNowLong = dateNow.getTimeInMillis();
                long monthAge = TimeConvertion.addMonths(dateNowLong, -month);
                deleteSQL = "delete from ssnsdata where updatedatel < " + monthAge;
            }
            processExecuteDB(deleteSQL);
            return 1;
        } catch (Exception e) {
            logger.info("> DeleteAllLock exception " + e.getMessage());
        }
        return 0;
    }

    public ArrayList getSsnsDataObjapp_uuid_datel(SsnsData item) {
        String sql = "select * from ssnsdata where app='" + item.getApp() + "' and uid='" + item.getUid() + "'"
                + " and updatedatel=" + item.getUpdatedatel();
        ArrayList entries = getAllSsnsDataSQL(sql, 0);
        return entries;
    }

    public ArrayList getSsnsDataObj(String name, int type, long updatedatel) {
        String sql = "select * from ssnsdata where name='" + name + "' and type=" + type + " and updatedatel=" + updatedatel;
        ArrayList entries = getAllSsnsDataSQL(sql, 0);
        return entries;
    }

    public ArrayList<SsnsAcc> getSsnsAccObjListByFeature(String app, String name, int length) {
        String sql = "select * from ssnsacc where app='" + app + "' and name='" + name + "'";
        sql = ServiceAFweb.getSQLLengh(sql, length);
        ArrayList entries = getAllSsnsAccSQL(sql, 0);
        return entries;
    }

    public String getSsReportObjListByFeatureCnt(String name, String oper) {

        String sql = "SELECT COUNT(oper) as name FROM ssreport where name='" + name + "' and oper='" + oper + "' GROUP BY oper";
        ArrayList array = getAllNameSQL(sql);
        String cnt = "0";
        if (array != null) {
            if (array.size() > 0) {
                cnt = (String) array.get(0);
            }
        }
        return cnt;
    }

    public String getSsnsAccObjListByFeatureCnt(String name) {

        String sql = "SELECT COUNT(name) as name FROM ssnsacc where name='" + name + "' GROUP BY name";
        ArrayList array = getAllNameSQL(sql);
        String cnt = "0";
        if (array != null) {
            if (array.size() > 0) {
                cnt = (String) array.get(0);
            }
        }
        return cnt;
    }

    public ArrayList<SsReport> getSsReportByFeatureOperIdList(String name, String app, String oper, int length) {
        String sql = "select * from ssreport where name='" + name + "' and app='" + app + "' and oper='" + oper + "'";
        sql = ServiceAFweb.getSQLLengh(sql, length);
        ArrayList entries = getAllSsReportSQL(sql, 0);
        return entries;
    }

    public ArrayList<String> getSsReportObjListByFeatureOper(String name, String app) {
        String sql = "select DISTINCT oper as name from ssreport where name='" + name + "' and app='" + app + "' order by oper asc";

        if ((CKey.SQL_DATABASE == CKey.REMOTE_MYSQL) && (CKey.SQL_RemoveServerDB == true)) {
            sql = "select DISTINCT oper from ssreport where name='" + name + "' and app='" + app + "' order by oper asc";
        }
        ArrayList array = getAllNameSQL(sql);
        return array;
    }

    public ArrayList<String> getSsnsAccObjListByFeature(String app) {
        String sql = "select DISTINCT name as name from ssnsacc where app='" + app + "' order by name asc";
        if ((CKey.SQL_DATABASE == CKey.REMOTE_MYSQL) && (CKey.SQL_RemoveServerDB == true)) {
            sql = "select DISTINCT name from ssnsacc where app='" + app + "' order by name asc";
        }
        ArrayList array = getAllNameSQL(sql);
        return array;
    }

    public ArrayList<SsnsAcc> getSsnsAccObjListByID(String app, String id) {
        String sql = "select * from ssnsacc where app='" + app + "' and id='" + id + "'";
        ArrayList entries = getAllSsnsAccSQL(sql, 0);

        return entries;
    }

    public int DeleteSsReportObjByID(int id) {
        try {
            String deleteSQL = "delete from ssreport where id=" + id;
            processExecuteDB(deleteSQL);
            return 1;
        } catch (Exception e) {
            logger.info("> DeleteAllLock exception " + e.getMessage());
        }
        return 0;

    }

    public int DeleteSsReportObjListByUid(String name, String uid) {
        try {
            String deleteSQL = "delete from ssreport "
                    + " where name='" + name + "' and uid='" + uid + "'";
            processExecuteDB(deleteSQL);
            return 1;
        } catch (Exception e) {
            logger.info("> DeleteAllLock exception " + e.getMessage());
        }
        return 0;

    }

//    public ArrayList<ProdSummary> getSsReportSummaryObjListByUid(String name, String uid) {
//        String sql = "select id as parm1, cusid as parm2, banid as parm3, tiid as parm4,"
//                + " oper as parm5, exec as parm6, ret as parm7  from ssreport "
//                + " where name='" + name + "' and uid='" + uid + "'";
//        sql += " order by updatedatel asc";
//        ArrayList<Pram7RDB> entries = getAll7ParamSQL(sql);
//        ArrayList<ProdSummary> sumList = new ArrayList();
//        if (entries != null) {
//            if (entries.size() > 0) {
//
//                for (int i = 0; i < entries.size(); i++) {
//                    Pram7RDB parm = entries.get(i);
//                    ProdSummary sum = new ProdSummary();
//                    sum.setId(Integer.parseInt(parm.getParm1()));
//                    sum.setCusid(parm.getParm2());
//                    sum.setBanid(parm.getParm3());
//                    sum.setTiid(parm.getParm4());
//                    sum.setOper(parm.getParm5());
//                    sum.setDown(parm.getParm6());
//                    sum.setRet(parm.getParm7());
//                    sumList.add(sum);
//                }
//            }
//            return sumList;
//        }
//        return null;
//    }

//    public ArrayList<ProdSummary> getSsnsAccObjSummaryListByApp(String app, int length) {
//        String sql = "select id as parm1, cusid as parm2, banid as parm3, tiid as parm4,"
//                + " oper as parm5, down as parm6, ret as parm7  from ssnsacc "
//                + "where app='" + app + "'";
//        ArrayList<Pram7RDB> entries = getAll7ParamSQL(sql);
//        if (entries != null) {
//            if (entries.size() > 0) {
//                ArrayList<ProdSummary> sumList = new ArrayList();
//                for (int i = 0; i < entries.size(); i++) {
//                    Pram7RDB parm = entries.get(i);
//                    ProdSummary sum = new ProdSummary();
//                    sum.setId(Integer.parseInt(parm.getParm1()));
//                    sum.setCusid(parm.getParm2());
//                    sum.setBanid(parm.getParm3());
//                    sum.setTiid(parm.getParm4());
//                    sum.setOper(parm.getParm5());
//                    sum.setDown(parm.getParm6());
//                    sum.setRet(parm.getParm7());
//                    sumList.add(sum);
//                }
//                return sumList;
//            }
//        }
//        return null;
//    }

    public ArrayList<SsnsAcc> getSsnsAccObjListByApp(String app, int length) {
        String sql = "select * from ssnsacc where app='" + app + "'";
        ArrayList entries = getAllSsnsAccSQL(sql, length);
        return entries;
    }

    public ArrayList<SsnsAcc> getSsnsAccObjListByBan(String name, String banid) {
        String sql = "select * from ssnsacc where name='" + name + "' and banid='" + banid + "'";
        ArrayList entries = getAllSsnsAccSQL(sql, 0);
        return entries;
    }

    public ArrayList<SsnsAcc> getSsnsAccObjListByCust(String name, String custid) {
        String sql = "select * from ssnsacc where name='" + name + "' and cusid='" + custid + "'";
        ArrayList entries = getAllSsnsAccSQL(sql, 0);
        return entries;
    }

    public ArrayList<SsnsAcc> getSsnsAccObjListByTiid(String name, String tiid) {
        String sql = "select * from ssnsacc where name='" + name + "' and tiid='" + tiid + "'";
        ArrayList entries = getAllSsnsAccSQL(sql, 0);
        return entries;
    }

    public ArrayList<SsReport> getSsReportObjListByUidDesc(String name, String uid) {
        String sql = "select * from ssreport where name='" + name + "' and uid='" + uid + "'";
        if ((name == null) || (name.length() == 0)) {
            sql = "select * from ssreport where uid='" + uid + "' ";
        }
        sql += " order by updatedatel desc";

        ArrayList entries = getAllSsReportSQL(sql, 0);
        return entries;
    }

    public ArrayList<SsnsAcc> getSsnsAccObjList(String name, String uid) {
        String sql = "select * from ssnsacc where name='" + name + "' and uid='" + uid + "'";
        ArrayList entries = getAllSsnsAccSQL(sql, 0);
        return entries;
    }

    public ArrayList<SsnsData> getSsnsDataObjListByUid(String app, String uid) {
        String sql = "select * from ssnsdata where app='" + app + "' and uid='" + uid + "'"
                + " order by updatedatel asc";
        ArrayList entries = getAllSsnsDataSQL(sql, 0);
        return entries;
    }

    public ArrayList getSsnsDataIDList(String app, String ret, int status, int length) {
        String sql = "select id as name from ssnsdata where app='" + app + "' and ret='" + ret + "' and status=" + status
                + " order by updatedatel asc";

        sql = ServiceAFweb.getSQLLengh(sql, length);
        ArrayList array = getAllNameSQL(sql);
        return array;
    }

    public ArrayList<SsnsData> getSsnsDataObjByUUIDList(String uid) {
        String sql = "select * from ssnsdata where uid='" + uid + "'"
                + " order by updatedatel asc";
        ArrayList entries = getAllSsnsDataSQL(sql, 0);
        return entries;
    }

    public ArrayList<SsReport> getSsReportListByID(int id) {
        String sql = "select * from ssreport where id=" + id
                + " order by updatedatel asc";
        ArrayList entries = getAllSsReportSQL(sql, 0);
        return entries;
    }

    public ArrayList<SsnsAcc> getSsnsAccObjListByID(int id) {
        String sql = "select * from ssnsacc where id=" + id
                + " order by updatedatel asc";
        ArrayList entries = getAllSsnsAccSQL(sql, 0);
        return entries;
    }

    public ArrayList<SsnsData> getSsnsDataObjListByID(int id) {
        String sql = "select * from ssnsdata where id=" + id
                + " order by updatedatel asc";
        ArrayList entries = getAllSsnsDataSQL(sql, 0);
        return entries;
    }

    public ArrayList<SsnsData> getSsnsDataObjList(String app, String ret, int status, int length) {
        String sql = "select * from ssnsdata where app='" + app + "' and ret='" + ret + "' and status=" + status
                + " order by updatedatel asc";
        ArrayList entries = getAllSsnsDataSQL(sql, length);
        return entries;
    }

    public ArrayList<SsnsData> getSsnsDataObj(String name, int length) {
        String sql = "select * from ssnsdata where name='" + name + "'" + " order by updatedatel asc";
        ArrayList entries = getAllSsnsDataSQL(sql, length);
        return entries;
    }

    public ArrayList<SsReport> getSsReportObjList(int length) {
        String sql = "select * from ssreport order by updatedatel asc";
        ArrayList entries = getAllSsReportSQL(sql, length);
        return entries;
    }

    public ArrayList<SsReport> getSsReportObjListByID(int id) {
        String sql = "select * from ssreport where id=" + id
                + " order by updatedatel asc";
        ArrayList entries = getAllSsReportSQL(sql, 0);
        return entries;
    }

    public ArrayList<SsReport> getSsReportObjList(String app, String ret, int status, int length) {
        String sql = "select * from ssreport where app='" + app + "' and ret='" + ret + "' and status=" + status
                + " order by updatedatel asc";
        ArrayList entries = getAllSsnsDataSQL(sql, length);
        return entries;
    }

    public ArrayList<SsReport> getSsReportObj(String name, int length) {
        String sql = "select * from ssreport where name='" + name + "'" + " order by updatedatel asc";
        ArrayList entries = getAllSsnsDataSQL(sql, length);
        return entries;
    }

    public ArrayList getAllNameTXTSQL(String sql) {
        if (checkCallRemoveSQL_Mysql() == true) {
            ArrayList nnList;
            try {
                nnList = remoteDB.getAllNameTXTSqlRemoteDB_RemoteMysql(sql);
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
                    String name = rs.getString("nametxt");
                    return name;
                }
            });
            return (ArrayList) entries;
        } catch (Exception e) {
            logger.info("> getAllNameTXTSQL exception " + e.getMessage());
        }
        return null;
    }

    public ArrayList getAllNameSQL(String sql) {
        if (checkCallRemoveSQL_Mysql() == true) {
            ArrayList nnList;
            try {
                nnList = remoteDB.getAllNameSqlRemoteDB_RemoteMysql(sql);
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
                    String name = rs.getString("name");
                    return name;
                }
            });
            return (ArrayList) entries;
        } catch (Exception e) {
            logger.info("> getAllNameSQL exception " + e.getMessage());
        }
        return null;
    }

    public ArrayList<Pram7RDB> getAll7ParamSQL(String sql) {
        if (checkCallRemoveSQL_Mysql() == true) {
            ArrayList<Pram7RDB> nnList;
            try {
                nnList = remoteDB.getAll7ParamSqlRemoteDB_RemoteMysql(sql);
                return nnList;
            } catch (Exception ex) {
            }
            return null;
        }
        try {
            List<Pram7RDB> entries = new ArrayList<>();
            entries.clear();
            entries = this.jdbcTemplate.query(sql, new RowMapper() {
                public Pram7RDB mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Pram7RDB nn = new Pram7RDB();
                    nn.setParm1(rs.getString("parm1"));
                    nn.setParm2(rs.getString("parm2"));
                    nn.setParm3(rs.getString("parm3"));
                    nn.setParm4(rs.getString("parm4"));
                    nn.setParm5(rs.getString("parm5"));
                    nn.setParm6(rs.getString("parm6"));
                    nn.setParm7(rs.getString("parm7"));
                    return nn;
                }
            });
            return (ArrayList) entries;
        } catch (Exception e) {
            logger.info("> getAll5ParamSQL exception " + e.getMessage());
        }
        return null;
    }

    public int updateRemoteMYSQL(String sqlCMD) throws Exception {
//        logger.info("> updateRemoteMYSQL " + sqlCMD);
        try {
            getJdbcTemplate().execute(sqlCMD);
            return 1;
        } catch (Exception e) {
            logger.info("> updateRemoteMYSQL exception " + e.getMessage());
        }
        return 0;
    }

    public String getRemoteMYSQL(String sql) throws SQLException {
        Statement stmt = null;
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery(sql);
            // The ResultSetMetaData is where all metadata related information
            // for a result set is stored.
            ResultSetMetaData metadata = resultSet.getMetaData();
            int columnCount = metadata.getColumnCount();

            // To get the column names we do a loop for a number of column count
            // returned above. And please remember a JDBC operation is 1-indexed
            // so every index begin from 1 not 0 as in array.
            ArrayList<String> columns = new ArrayList<String>();
            for (int i = 1; i < columnCount + 1; i++) {
                String columnName = metadata.getColumnName(i);
                columns.add(columnName);
            }
            // Later we use the collected column names to get the value of the
            // column it self.
            StringBuilder retString = new StringBuilder();
            int firstList = 0;
            while (resultSet.next()) {
                if (firstList > 0) {
                    retString.append("~");
                }
                firstList++;
                int firstColumn = 0;
                for (String columnName : columns) {
                    if (firstColumn > 0) {
                        retString.append("~");
                    }
                    firstColumn++;
                    String value = resultSet.getString(columnName);
                    retString.append(value);
                }
            }
            String ret = retString.toString();
            return ret;
        } catch (SQLException e) {
            logger.info("> getRemoteMYSQL exception " + e.getMessage());
        } finally {
            if (con != null) {
                con.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }
        return "";
    }

//    public String getRemoteMYSQL(String sql) throws SQLException {
//        Statement stmt = null;
//        Connection con = null;
//        try {
//            con = getDataSource().getConnection();
//            stmt = con.createStatement();
//            ResultSet resultSet = stmt.executeQuery(sql);
//            // The ResultSetMetaData is where all metadata related information
//            // for a result set is stored.
//            ResultSetMetaData metadata = resultSet.getMetaData();
//            int columnCount = metadata.getColumnCount();
//
//            // To get the column names we do a loop for a number of column count
//            // returned above. And please remember a JDBC operation is 1-indexed
//            // so every index begin from 1 not 0 as in array.
//            ArrayList<String> columns = new ArrayList<String>();
//            for (int i = 1; i < columnCount + 1; i++) {
//                String columnName = metadata.getColumnName(i);
//                columns.add(columnName);
//            }
//
//            // Later we use the collected column names to get the value of the
//            // column it self.
//            StringBuilder retString = new StringBuilder();
//            retString.append("[");
//
//            int firstList = 0;
//            while (resultSet.next()) {
//                if (firstList > 0) {
//                    retString.append(",");
//                }
//                firstList++;
//                retString.append("{");
//                int firstColumn = 0;
//                for (String columnName : columns) {
//                    if (firstColumn > 0) {
//                        retString.append(",");
//                    }
//                    firstColumn++;
//                    String value = resultSet.getString(columnName);
//                    retString.append("\"" + columnName + "\"" + ":" + "\"" + value + "\"");
//                }
//                retString.append("}");
//            }
//            retString.append("]");
//            String ret = retString.toString();
//            return ret;
//        } catch (SQLException e) {
//            logger.info("> getRemoteMYSQL exception " + e.getMessage());
//
//        } finally {
//            if (con != null) {
//                con.close();
//            }
//            if (stmt != null) {
//                stmt.close();
//            }
//        }
//        return "";
//    }
}
