package com.afweb.process;

import com.afweb.model.*;
import com.afweb.model.ssns.*;

import java.util.ArrayList;

import java.util.logging.Logger;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author eddy
 */
public class SsnsDataImp {

    protected static Logger logger = Logger.getLogger("SsnsDataImp");

    private SsnsDataDB ssnsdb = new SsnsDataDB();

    public void setDataSource(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        ssnsdb.setJdbcTemplate(jdbcTemplate);
        ssnsdb.setDataSource(dataSource);
    }

    public int updateSQLArrayList(ArrayList SQLTran) {
        return ssnsdb.updateSQLArrayList(SQLTran);
    }

//////////////////////////////////////    
    public String getAllLockDBSQL(String sql) {
        return ssnsdb.getAllLockDBSQL(sql);
    }

    public String getAllSsnsDataDBSQL(String sql, int length) {
        return ssnsdb.getAllSsnsDataDBSQL(sql, length);
    }

    ////////////////////////////////
    public ArrayList getAllLock() {
        return ssnsdb.getAllLock();
    }

    public int setLockName(String name, int type, long lockDateValue, String comment) {
        return ssnsdb.setLockName(name, type, lockDateValue, comment);
    }

    public AFLockObject getLockName(String name, int type) {
        return ssnsdb.getLockName(name, type);
    }

    public int setRenewLock(String name, int type, long lockDateValue) {
        return ssnsdb.setRenewLock(name, type, lockDateValue);
    }

    public int removeLock(String name, int type) {
        return ssnsdb.removeLock(name, type);
    }

    public int updateSsnsDataOpenStatus(String app) {
        return ssnsdb.updateSsnsDataOpenStatus(app);
    }

    public int updateSsnsDataAllOpenStatus() {
        return ssnsdb.updateSsnsDataAllOpenStatus();
    }

    public int deleteSsnsAccApp(String app) {
        return ssnsdb.deleteSsnsAccApp(app);
    }

    public int deleteSsnsDataApp(String app) {
        return ssnsdb.deleteSsnsDataApp(app);
    }

    public int deleteSsnsData(String name) {
        return ssnsdb.deleteSsnsData(name);
    }

    public int updatSsReportDataStatusTypeRetById(int id, String data, int status, int type, String ret) {
        return ssnsdb.updatSsReportDataStatusTypeRetById(id, data, status, type, ret);
    }

   
    public int updatSsReportDataStatusTypeById(int id, String data, int status, int type) {
        return ssnsdb.updatSsReportDataStatusTypeById(id, data, status, type);
    }

    public int updatSsnsAccNameStatusTypeById(int id, String name, int status, int type) {
        return ssnsdb.updatSsnsAccNameStatusTypeById(id, name, status, type);
    }

    public int insertSsnsAccObject(SsnsAcc nData) {
        return ssnsdb.insertSsnsAccObject(nData);
    }

    public int insertSsReportObject(SsReport nData) {
        return ssnsdb.insertSsReportObject(nData);
    }

    public int updatSsnsDataStatusById(int id, int status) {
        return ssnsdb.updatSsnsDataStatusById(id, status);
    }

    public ArrayList<SsnsData> getSsnsDataObjByUUIDList(String uid) {
        ArrayList<SsnsData> ssnsObjList = ssnsdb.getSsnsDataObjByUUIDList(uid);
        return ssnsObjList;
    }

    public SsnsData getSsnsDataObjByID(int id) {
        ArrayList<SsnsData> ssnsObjList = ssnsdb.getSsnsDataObjListByID(id);
        if (ssnsObjList != null) {
            if (ssnsObjList.size() > 0) {
                return ssnsObjList.get(0);
            }
        }
        return null;
    }

    public SsnsAcc getSsnsAccObjByID(int id) {
        ArrayList<SsnsAcc> ssnsObjList = ssnsdb.getSsnsAccObjListByID(id);
        if (ssnsObjList != null) {
            if (ssnsObjList.size() > 0) {
                return ssnsObjList.get(0);
            }
        }
        return null;
    }

    public ArrayList<SsnsData> getSsnsDataObjListByUid(String app, String uid) {
        return ssnsdb.getSsnsDataObjListByUid(app, uid);
    }

    public ArrayList getSsnsDataIDList(String app, String ret, int status, int length) {
        return ssnsdb.getSsnsDataIDList(app, ret, status, length);
    }

    public ArrayList<SsnsData> getSsnsDataObjList(String app, String ret, int status, int length) {
        return ssnsdb.getSsnsDataObjList(app, ret, status, length);
    }

    public SsnsData getSsnsDataObj(String name) {
        ArrayList<SsnsData> ssnsObjList = ssnsdb.getSsnsDataObj(name, 1);
        if (ssnsObjList != null) {
            if (ssnsObjList.size() > 0) {
                return ssnsObjList.get(0);
            }
        }
        return null;
    }

    public ArrayList<SsnsData> getSsnsDataObjList(String name, int length) {
        return ssnsdb.getSsnsDataObj(name, length);
    }

    public ArrayList<SsnsData> getSsnsDataObjList(String name, int type, long updatedatel) {
        return ssnsdb.getSsnsDataObj(name, type, updatedatel);
    }

    public String getSsReportObjListByFeatureOperCnt(String name, String oper) {
        return ssnsdb.getSsReportObjListByFeatureCnt(name, oper);
    }

    public String getSsnsAccObjListByFeatureCnt(String name) {
        return ssnsdb.getSsnsAccObjListByFeatureCnt(name);
    }

    public ArrayList<String> getSsReportByFeatureOperIdListName(String name, String app, String oper) {
        return ssnsdb.getSsReportByFeatureOperIdListName(name, app, oper);
    }

    public ArrayList<SsReport> getSsReportByFeatureOperIdList(String name, String app, String oper, int length) {
        return ssnsdb.getSsReportByFeatureOperIdList(name, app, oper, length);
    }

    public ArrayList<String> getSsReportObjListByFeatureOper(String name, String app) {
        return ssnsdb.getSsReportObjListByFeatureOper(name, app);
    }

    public ArrayList<String> getSsnsAccObjListByFeature(String app) {
        return ssnsdb.getSsnsAccObjListByFeature(app);
    }

    public ArrayList<SsnsAcc> getSsnsAccObjListByFeature(String app, String name, int length) {
        return ssnsdb.getSsnsAccObjListByFeature(app, name, length);
    }

    public ArrayList<SsnsAcc> getSsnsAccObjListByID(String app, String pid) {
        return ssnsdb.getSsnsAccObjListByID(app, pid);
    }

//    public ArrayList<ProdSummary> getSsnsAccObjSummaryListByApp(String app, int length) {
//        return ssnsdb.getSsnsAccObjSummaryListByApp(app, length);
//    }
    public ArrayList<SsnsAcc> getSsnsAccObjListByApp(String app, int length) {
        return ssnsdb.getSsnsAccObjListByApp(app, length);
    }

    public ArrayList<SsnsAcc> getSsnsAccObjListByBan(String name, String banid) {
        return ssnsdb.getSsnsAccObjListByBan(name, banid);
    }

    public ArrayList<SsnsAcc> getSsnsAccObjListByCust(String name, String custid) {
        return ssnsdb.getSsnsAccObjListByCust(name, custid);
    }

    public ArrayList<SsnsAcc> getSsnsAccObjListByTiid(String name, String tiid) {
        return ssnsdb.getSsnsAccObjListByTiid(name, tiid);
    }

    public ArrayList<SsnsAcc> getSsnsAccObjList(String name, String uid) {
        return ssnsdb.getSsnsAccObjList(name, uid);
    }

    public ArrayList<SsReport> getSsReportAll() {
        ArrayList<SsReport> ssReportObjList = ssnsdb.getSsReportObjList(10);
        return ssReportObjList;
    }

    public SsReport getSsReportByID(int id) {
        ArrayList<SsReport> ssReportObjList = ssnsdb.getSsReportObjListByID(id);
        if (ssReportObjList != null) {
            if (ssReportObjList.size() > 0) {
                return ssReportObjList.get(0);
            }
        }
        return null;
    }

    public int DeleteSsReportObjByID(int id) {
        return ssnsdb.DeleteSsReportObjByID(id);
    }

    public int DeleteSsReportObjListByUid(String name, String uid) {
        return ssnsdb.DeleteSsReportObjListByUid(name, uid);
    }

//    public ArrayList<ProdSummary> getSsReportSummaryObjListByUid(String name, String uid) {
//        return ssnsdb.getSsReportSummaryObjListByUid(name, uid);
//    }
    public ArrayList<SsReport> getSsReportObjListByUidDesc(String name, String uid, int length) {
        return ssnsdb.getSsReportObjListByUidDesc(name, uid, length);
    }

    public int deleteAllSsReport(int month) {
        return ssnsdb.deleteAllSsReport(month);
    }

    public int deleteAllSsnsAcc(int month) {
        return ssnsdb.deleteAllSsnsAcc(month);
    }

    public int deleteAllSsnsData(int month) {
        return ssnsdb.deleteAllSsnsData(month);
    }

    public SsnsData getSsnsDataObjapp_uuid_datel(SsnsData item) {
        ArrayList<SsnsData> ssnsObjList = ssnsdb.getSsnsDataObjapp_uuid_datel(item);
        if (ssnsObjList != null) {
            if (ssnsObjList.size() > 0) {
                return ssnsObjList.get(0);
            }
        }
        return null;
    }

    ////////////////////////////////
    public boolean restSsnsDataDB() {
        return ssnsdb.restSsnsDataDB();
    }

    public boolean cleanSsnsDataDB() {
        return ssnsdb.cleanSsnsDataDB();
    }

    public int deleteAllLock() {
        return ssnsdb.deleteAllLock();
    }

    public int testSsnsDataDB() {
        try {
            int result = ssnsdb.testSsnsDataDB();
        } catch (Exception ex) {
        }
        return -1;  // DB error
    }

    // 0 - new db, 1 - db already exist, -1 db error
    public int initSsnsDataDB() {
        try {
            int result = ssnsdb.initSsnsDataDB();
            return result;
        } catch (Exception ex) {
        }
        return -1;  // DB error
    }

    public ArrayList getAllNameSQL(String sql) {
        return ssnsdb.getAllNameSQL(sql);
    }

    public String getRemoteMYSQL(String sql) {
        try {
            return ssnsdb.getRemoteMYSQL(sql);
        } catch (Exception ex) {
            logger.info("> getRemoteMYSQL exception " + ex.getMessage());
            return null;
        }
    }

    public int updateRemoteMYSQL(String sql) {
        try {
            return ssnsdb.updateRemoteMYSQL(sql);
        } catch (Exception ex) {
            logger.info("> getRemoteMYSQL exception " + ex.getMessage());
            return 0;
        }
    }

}
