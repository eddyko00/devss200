/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.afweb.process;

import com.afweb.model.*;
import com.afweb.model.ssns.*;
import com.afweb.service.ServiceAFweb;

import com.afweb.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.Proxy;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.Map;

import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.codec.binary.Base64;
import static org.apache.http.protocol.HTTP.USER_AGENT;

/**
 *
 * @author koed
 */
public class SsnsService {

    protected static Logger logger = Logger.getLogger("SsnsService");

    public static String APP_WIFI = "wifi";
    public static String APP_APP = "app";
    public static String APP_PRODUCT = "prod";
    public static String APP_TTVC = "ttv";

    public static String APP_TTVSUB = "ttvsub";  // ETL name
    public static String APP_TTVREQ = "ttvreq";  // ETL name
    //

    public static String APP_FEAT_TYPE_TTV = "TTV";
    public static String APP_FEAT_TYPE_HSIC = "HSIC";
    public static String APP_FEATT_TYPE_SING = "SING";
    public static String APP_FEAT_TYPE_APP = "APP";
    public static String APP_FEAT_TYPE_WIFI = "WIFI";
    public static String APP_FEAT_TYPE_TTVCL = "TTVCL";

    public static String APP_GET_APP = "getAppointment";
    public static String APP_CAN_APP = "cancelAppointment";
    public static String APP_GET_TIMES = "searchTimeSlot";
    public static String APP_UPDATE = "updateAppointment";

    public static String PROD_GET_PROD = "getProductList";
    public static String PROD_GET_BYID = "getProductById";

    public static String WI_GetDeviceStatus = "getDeviceStatus";
    public static String WI_Callback = "callbackNotification";
    public static String WI_GetDevice = "getDevices";
    public static String WI_config = "configureDeviceStatus";

    public static String TT_GetSub = "getCustomerTvSubscription";
    public static String TT_Vadulate = "validateWithAuth";
    public static String TT_Quote = "quotewithauth";
    public static String TT_SaveOrder = "saveOrder";

    private SsnsDataImp ssnsDataImp = new SsnsDataImp();

////////////////////////////////////////////    
    public String getFeatureSsnsTTVC(SsnsData dataObj) {
        String feat = "";
        try {
            feat = getFeatureSsnsTTVCProcess(dataObj);
        } catch (Exception ex) {
            logger.info("> getFeatureSsnsTTVC Exception " + ex.getMessage());
        }
        getSsnsDataImp().updatSsnsDataStatusById(dataObj.getId(), ConstantKey.COMPLETED);
        return feat;
    }

    public String getFeatureSsnsTTVCProcess(SsnsData dataObj) {
        ProductData pData = new ProductData();
        ArrayList<String> cmd = new ArrayList();
        if (dataObj == null) {
            return "";
        }

        String banid = "";
        String prodid = "";

        String postParm = "";

        String dataSt = "";
        try {
            String oper = dataObj.getOper();
            if ((oper.equals(TT_Vadulate) || oper.equals(TT_Quote) || oper.equals(TT_SaveOrder))) { //"updateAppointment")) {

                dataSt = dataObj.getData();
                dataSt = ServiceAFweb.replaceAll("\"", "", dataSt);
                dataSt = ServiceAFweb.replaceAll("[", "", dataSt);
                dataSt = ServiceAFweb.replaceAll("]", "", dataSt);
                dataSt = ServiceAFweb.replaceAll("{", "", dataSt);
                dataSt = ServiceAFweb.replaceAll("}", "", dataSt);
                String[] operList = dataSt.split(",");
                if (operList.length > 1) {
                    banid = operList[0];
                    prodid = operList[1];

                    if (operList.length > 5) {
                        dataSt = dataObj.getData();

                        int beg = dataSt.indexOf("{");
                        if (beg != -1) {
                            postParm = dataSt.substring(beg);
                            postParm += "}";
                        }
                    }
                }
                cmd.add("get customer ttv subscription");
                cmd.add(TT_GetSub);
                cmd.add("ttv validate");
                cmd.add(TT_Vadulate);
                cmd.add("ttv quotation");
                cmd.add(TT_Quote);
                pData.setCmd(cmd);

            } else if (oper.equals(TT_GetSub)) {
                dataSt = dataObj.getData();
                dataSt = ServiceAFweb.replaceAll("\"", "", dataSt);
                dataSt = ServiceAFweb.replaceAll("[", "", dataSt);
                dataSt = ServiceAFweb.replaceAll("]", "", dataSt);
                dataSt = ServiceAFweb.replaceAll("{", "", dataSt);
                dataSt = ServiceAFweb.replaceAll("}", "", dataSt);
                String[] operList = dataSt.split(",");
                if (operList.length > 1) {
                    banid = operList[0];
                    prodid = operList[1];
                }
                cmd.add("get customer ttv subscription");
                cmd.add(TT_GetSub);
                pData.setCmd(cmd);

            } else {
                logger.info("> getFeatureSsnsTTVCProcess Other oper " + oper);
                return "";
            }

            if (prodid.equals("")) {
                return "";
            }

//            logger.info(dataSt);
/////////////
            //call devop to get customer id
            SsnsAcc NAccObj = new SsnsAcc();
            NAccObj.setDown("splunkflow");
            boolean stat = this.updateSsnsTTVC(oper, banid, prodid, postParm, pData, dataObj, NAccObj);
            if (stat == true) {
                ArrayList<SsnsAcc> ssnsAccObjList = getSsnsDataImp().getSsnsAccObjList(NAccObj.getName(), NAccObj.getUid());
                boolean exist = false;
                if (ssnsAccObjList != null) {
                    if (ssnsAccObjList.size() != 0) {
                        SsnsAcc ssnsObj = ssnsAccObjList.get(0);
                        if (ssnsObj.getDown().equals("splunkflow")) {
                            exist = true;
                        }
                    }
                }
                if (exist == false) {
                    ssnsAccObjList = getSsnsDataImp().getSsnsAccObjListByBan(NAccObj.getName(), NAccObj.getBanid());
                    if (ssnsAccObjList != null) {
                        if (ssnsAccObjList.size() > 3) {
                            exist = true;
                        }
                    }
                }
                if (exist == false) {
                    int ret = getSsnsDataImp().insertSsnsAccObject(NAccObj);
                }
            }
            return NAccObj.getName();
        } catch (Exception ex) {
            logger.info("> getFeatureSsnsTTVCProcess Exception " + ex.getMessage());
        }
        return "";
    }

    public boolean updateSsnsTTVC(String oper, String banid, String prodid, String postParm, ProductData pData, SsnsData dataObj, SsnsAcc NAccObj) {
        try {
            String featTTV = "";
            int legacyDiscount = 0;
            int XQException = 0;
            if (oper.equals(TT_GetSub) || oper.equals(TT_Vadulate) || oper.equals(TT_Quote) || oper.equals(TT_SaveOrder)) {
                if ((banid.length() == 0) && (prodid.length() == 0)) {
                    return false;
                } else {
                    String outputSt = null;
                    outputSt = SendSsnsTTVC(ServiceAFweb.URL_PRODUCT, TT_GetSub, banid, prodid, postParm, null);
                    if (outputSt == null) {
                        return false;
                    }
                    if (outputSt.length() == 0) {
                        return false;
                    }
//                    if (outputSt.length() < 80) {  // or test 
//                        return false;
//                    }

                    if (outputSt.indexOf("Legacy Discount") != -1) {
                        String dataSt = ServiceAFweb.replaceAll("\"", "", outputSt);
                        if (dataSt.indexOf("statusCd:400") != -1) {
                            legacyDiscount = 1;
                        }
                    }

                    if (outputSt.indexOf("responseCode:400500") != -1) {
                        if (outputSt.indexOf("XQException") != -1) {
                            XQException = 1;
                        } else {
                            return false;
                        }
                    }
                    featTTV = parseTTVCFeature(outputSt, oper, postParm);
                }
            } else {
                return false;
            }

//            logger.info("> updateSsnsTTVC feat " + featTTV);
/////////////TTV   
            int failure = 0;
            if (NAccObj.getDown().equals("splunkflow")) {

                ArrayList<String> flow = new ArrayList();
                failure = getSsnsFlowTrace(dataObj, flow);
                if (flow == null) {
                    logger.info("> updateSsnsTTVC skip no flow");
                    return false;
                }
                pData.setFlow(flow);

            }

            if (legacyDiscount == 1) {
                featTTV += ":legacyDisc:failed";
                if (failure == 0) {
                    featTTV += ":splunkfailed";
                }
            }
            if (XQException == 1) {
                featTTV += ":XQException";
                if (failure == 0) {
                    featTTV += ":splunkfailed";
                }
            }
            if (failure == 1) {
                featTTV += ":splunkfailed";
            }

            logger.info("> updateSsnsTTVC feat " + featTTV);
            pData.setPostParam(postParm);
            NAccObj.setName(featTTV);
            NAccObj.setBanid(banid);
            NAccObj.setCusid(dataObj.getCusid());

            NAccObj.setTiid(prodid);

            NAccObj.setUid(dataObj.getUid());
            NAccObj.setApp(APP_TTVC);
            NAccObj.setOper(oper);

//          NAccObj.setDown(""); // set by NAccObj
            NAccObj.setRet(dataObj.getRet());
            NAccObj.setExec(dataObj.getExec());

            String nameSt = new ObjectMapper().writeValueAsString(pData);
            NAccObj.setData(nameSt);

            NAccObj.setUpdatedatel(dataObj.getUpdatedatel());
            NAccObj.setUpdatedatedisplay(new java.sql.Date(dataObj.getUpdatedatel()));

            return true;
        } catch (Exception ex) {
            logger.info("> updateSsnsAppointment Exception " + ex.getMessage());
        }
        return false;
    }

    public static String parseTTVCFeature(String outputSt, String oper, String postParm) {

        if (outputSt == null) {
            return "";
        }

        int packCd = 0;
        int channelCd = 0;
        int discountCd = 0;
        int add = 0;
        int remove = 0;

        String geomarket = "";
        String offer = "noOfferCd";
        String collectionCd = "";

        ArrayList<String> outputList = ServiceAFweb.prettyPrintJSON(outputSt);
        for (int j = 0; j < outputList.size(); j++) {
            String inLine = outputList.get(j);
//            logger.info("" + inLine);
            if (inLine.indexOf("geoTargetMarket") != -1) {
                String valueSt = inLine;
                valueSt = ServiceAFweb.replaceAll("\"", "", valueSt);
                valueSt = ServiceAFweb.replaceAll("geoTargetMarket:", "", valueSt);
                valueSt = ServiceAFweb.replaceAll(",", "", valueSt);
                geomarket = valueSt;
            }
            if (inLine.indexOf("offerCd") != -1) {
                String valueSt = inLine;
                valueSt = ServiceAFweb.replaceAll("\"", "", valueSt);
                valueSt = ServiceAFweb.replaceAll("offerCd:", "", valueSt);
                valueSt = ServiceAFweb.replaceAll(",", "", valueSt);

                if (valueSt.indexOf("MediaroomTV-HS2.0") != -1) {
                    offer = "Mediaroom20";
                    continue;
                } else if (valueSt.indexOf("MediaroomTV-HS") != -1) {
                    offer = "Mediaroom";
                    continue;
                } else if (valueSt.indexOf("TVX") != -1) {
                    offer = "TVX";
                }
                continue;
            }

            if (inLine.indexOf("collectionCd") != -1) {
                String valueSt = inLine;
                valueSt = ServiceAFweb.replaceAll("\"", "", valueSt);
                valueSt = ServiceAFweb.replaceAll("collectionCd:", "", valueSt);
                valueSt = ServiceAFweb.replaceAll(",", "", valueSt);
                collectionCd = valueSt;
            }

            if (inLine.indexOf("discountCd") != -1) {
                discountCd++;
                continue;
            }
            if (inLine.indexOf("packCd") != -1) {
                packCd++;
                continue;
            }
            if (inLine.indexOf("channelCd") != -1) {
                channelCd++;
                continue;
            }
        }

        if (postParm != null) {
            if (postParm.length() > 0) {
                outputList = ServiceAFweb.prettyPrintJSON(postParm);
                for (int j = 0; j < outputList.size(); j++) {
                    String inLine = outputList.get(j);
                    if (inLine.indexOf("ADD") != -1) {
                        add++;
                        continue;
                    }
                    if (inLine.indexOf("REMOVE") != -1) {
                        remove++;
                        continue;
                    }
                }
            }
        }

        String featTTV = APP_FEAT_TYPE_TTVCL;
        featTTV += ":" + oper;

        String gm = geomarket;
        if (gm.length() == 0) {
            gm = "noGeoMarket";
        }
        featTTV += ":" + gm;
        featTTV += ":" + offer;
        String coll = collectionCd;
        if (coll.length() == 0) {
            coll = "Essential";
        }
        featTTV += ":" + coll;
        featTTV += ":Pack_" + packCd;
        featTTV += ":Channel_" + channelCd;
        featTTV += ":Disc_" + discountCd;
        if (add == 0) {
            featTTV += ":NAdd";
        } else if (add < 3) {
            featTTV += ":Add";
        } else {
            featTTV += ":Add_" + add;
        }
        if (remove == 0) {
            featTTV += ":NRemove";
        } else if (remove < 3) {
            featTTV += ":Remove";
        } else {
            featTTV += ":Remove_" + remove;
        }
        return featTTV;
    }

    public String SendSsnsTTVC(String ProductURL, String oper, String banid, String prodid, String postParm, ArrayList<String> inList) {
        String url = "";

        try {
            if (oper.equals(TT_GetSub)) {
                url = ProductURL + "/v1/cmo/selfmgmt/tvsubscription/account/" + banid
                        + "/productinstance/" + prodid
                        + "/subscription";

                if (inList != null) {
                    inList.add(url);
                }
                // calculate elapsed time in milli seconds
                long startTime = TimeConvertion.currentTimeMillis();

                String output = this.sendRequest_Ssns(METHOD_GET, url, null, null);

                long endTime = TimeConvertion.currentTimeMillis();
                long elapsedTime = endTime - startTime;
//            System.out.println("Elapsed time in milli seconds: " + elapsedTime);
                if (inList != null) {
                    inList.add("elapsedTime:" + elapsedTime);
                    inList.add("output:");
                }
                return output;
            } else if (oper.equals(TT_Quote) || oper.equals(TT_SaveOrder)) {
                url = ProductURL + "/v1/cmo/selfmgmt/tv/requisition/account/" + banid
                        + "/productinstance/" + prodid
                        + "/quotation";

                if (inList != null) {
                    inList.add(url);
                }
                // calculate elapsed time in milli seconds
                long startTime = TimeConvertion.currentTimeMillis();
                String st = ServiceAFweb.replaceAll("\":\",", "\":\"\",", postParm);
                st = st.substring(0, st.length() - 2);

                Map<String, String> map = new ObjectMapper().readValue(st, Map.class);
                map.remove("customerEmail");

                String output = this.sendRequest_Ssns(METHOD_POST, url, null, map);

                long endTime = TimeConvertion.currentTimeMillis();
                long elapsedTime = endTime - startTime;
//            System.out.println("Elapsed time in milli seconds: " + elapsedTime);
                if (inList != null) {
                    inList.add("elapsedTime:" + elapsedTime);

                    String bodyElement = new ObjectMapper().writeValueAsString(map);
                    inList.add("bodyElement:" + bodyElement);
                    inList.add("output:");

                }
                return output;

            } else if (oper.equals(TT_Vadulate)) {
                url = ProductURL + "/v1/cmo/selfmgmt/tv/requisition/account/" + banid
                        + "/productinstance/" + prodid
                        + "/quotation";

                if (inList != null) {
                    inList.add(url);
                }
                // calculate elapsed time in milli seconds
                long startTime = TimeConvertion.currentTimeMillis();
                String st = ServiceAFweb.replaceAll("\":\",", "\":\"\",", postParm);
                st = st.substring(0, st.length() - 2);

                Map<String, String> map = new ObjectMapper().readValue(st, Map.class);
                map.remove("customerEmail");

                String output = this.sendRequest_Ssns(METHOD_POST, url, null, map);

                long endTime = TimeConvertion.currentTimeMillis();
                long elapsedTime = endTime - startTime;
//            System.out.println("Elapsed time in milli seconds: " + elapsedTime);
                if (inList != null) {
                    inList.add("elapsedTime:" + elapsedTime);

                    String bodyElement = new ObjectMapper().writeValueAsString(map);
                    inList.add("bodyElement:" + bodyElement);
                    inList.add("output:");
                }
                return output;
            }
        } catch (Exception ex) {
            logger.info("> SendSsnsTTVC exception " + ex.getMessage());
        }
        return null;
    }

    public String TestFeatureSsnsProdTTVC(SsnsAcc dataObj, ArrayList<String> outputList, String oper, String LABURL) {
        if (dataObj == null) {
            return "";
        }

        dataObj.getData();
        String banid = dataObj.getBanid();
        String appTId = dataObj.getTiid();
        if (appTId.length() == 0) {
            return "";
        }
        if (LABURL.length() == 0) {
            LABURL = ServiceAFweb.URL_PRODUCT;
        }

        String outputSt = null;
        ArrayList<String> inList = new ArrayList();
        if (oper.equals(TT_SaveOrder) || oper.equals(TT_Vadulate) || oper.equals(TT_Quote) || oper.equals(TT_SaveOrder)) {
            outputSt = SendSsnsTTVC(LABURL, TT_GetSub, banid, appTId, null, inList);
            if (outputSt == null) {
                return "";
            }
            ////special char #, need to ignore for this system
            outputSt = outputSt.replaceAll("#", "");
            outputSt = outputSt.replaceAll("~", "");
            outputSt = outputSt.replaceAll("^", "");

            ArrayList<String> outList = ServiceAFweb.prettyPrintJSON(outputSt);
            String feat = parseTTVCFeature(outputSt, oper, null);
            if (outputSt.indexOf("responseCode:400500") != -1) {
                feat += ":testfailed";
            }
            outputList.add(feat);

            ProductData pData = null;
            String output = dataObj.getData();
            try {
                pData = new ObjectMapper().readValue(output, ProductData.class);
            } catch (IOException ex) {
            }
            if (pData == null) {
                return "";
            }
            inList.clear();

            String postParamSt = ProductDataHelper.getPostParamRestore(pData.getPostParam());
            outputSt = SendSsnsTTVC(LABURL, oper, banid, appTId, postParamSt, inList);
            outputList.addAll(inList);
            outputList.addAll(outList);

            return feat;
        } else if (oper.equals(TT_GetSub)) {

            outputSt = SendSsnsTTVC(LABURL, oper, banid, appTId, null, inList);;
            if (outputSt == null) {
                return "";
            }
            ////special char #, need to ignore for this system
            outputSt = outputSt.replaceAll("#", "");
            outputSt = outputSt.replaceAll("~", "");
            outputSt = outputSt.replaceAll("^", "");

            ArrayList<String> outList = ServiceAFweb.prettyPrintJSON(outputSt);
            String feat = parseTTVCFeature(outputSt, oper, null);
            if (outputSt.indexOf("responseCode:400500") != -1) {
                feat += ":testfailed";
            }
            outputList.add(feat);
            outputList.addAll(inList);
            outputList.addAll(outList);

            return feat;
        }

        return "";
    }

////////////////////////////////////////////    
    public String getFeatureSsnsWifi(SsnsData dataObj) {
        String feat = "";
        try {
            feat = getFeatureSsnsWifiProcess(dataObj);
        } catch (Exception ex) {
            logger.info("> getFeatureSsnsWifi Exception " + ex.getMessage());
        }
        getSsnsDataImp().updatSsnsDataStatusById(dataObj.getId(), ConstantKey.COMPLETED);
        return feat;
    }

    public String getFeatureSsnsWifiProcess(SsnsData dataObj) {
        ProductData pData = new ProductData();
        ArrayList<String> cmd = new ArrayList();
        if (dataObj == null) {
            return "";
        }

        String banid = "";
        String uniquid = "";
        String prodClass = "";
        String serialid = "";
        String parm = "";
        String postParm = "";
        int async = 0;
        String dataSt = "";
        try {
            String oper = dataObj.getOper();
            if (oper.equals(WI_GetDeviceStatus)) { //"updateAppointment")) {

                dataSt = dataObj.getData();
                dataSt = ServiceAFweb.replaceAll("\"", "", dataSt);
                dataSt = ServiceAFweb.replaceAll("[", "", dataSt);
                dataSt = ServiceAFweb.replaceAll("]", "", dataSt);
                dataSt = ServiceAFweb.replaceAll("{", "", dataSt);
                dataSt = ServiceAFweb.replaceAll("}", "", dataSt);
                String[] operList = dataSt.split(",");
                if (operList.length > 3) {
                    banid = operList[0];
                    uniquid = operList[1];
                    prodClass = operList[2];
                    serialid = operList[3];
                    parm = operList[4];
                    if (operList.length > 5) {
                        dataSt = dataObj.getData();
                        dataSt = ServiceAFweb.replaceAll("\"", "", dataSt);
                        if (dataSt.indexOf("asynchronousRequest") == -1) {
                            async = 1;
                            int beg = dataSt.indexOf("{");
                            if (beg != -1) {
                                postParm = dataSt.substring(beg, dataSt.length() - 1);
                            }
                        } else {
                            int beg = dataSt.indexOf("{");
                            if (beg != -1) {
                                postParm = dataSt.substring(beg);
                                postParm += "]";
                            }
                        }
                    }
                }
                cmd.add("get wifi device"); // description
                cmd.add(WI_GetDevice);   // cmd
                cmd.add("get wifi devicestatus");
                cmd.add(WI_GetDeviceStatus);
                pData.setCmd(cmd);
            } else if (oper.equals(WI_config)) {
                dataSt = dataObj.getData();
                dataSt = ServiceAFweb.replaceAll("\"", "", dataSt);
                dataSt = ServiceAFweb.replaceAll("[", "", dataSt);
                dataSt = ServiceAFweb.replaceAll("]", "", dataSt);
                dataSt = ServiceAFweb.replaceAll("{", "", dataSt);
                dataSt = ServiceAFweb.replaceAll("}", "", dataSt);
                String[] operList = dataSt.split(",");
                if (operList.length > 3) {
                    banid = operList[0];
                    uniquid = operList[1];
                    prodClass = operList[2];
                    serialid = operList[3];
                    parm = operList[4];

                    if (operList.length > 5) {
                        dataSt = dataObj.getData();
                        dataSt = ServiceAFweb.replaceAll("\"", "", dataSt);
                        if (dataSt.indexOf("asynchronousRequest") != -1) {
                            async = 1;
                            int beg = dataSt.indexOf("{");
                            if (beg != -1) {
                                postParm = dataSt.substring(beg, dataSt.length() - 1);
                            }
                        } else {
                            int beg = dataSt.indexOf("{");
                            if (beg != -1) {
                                postParm = dataSt.substring(beg);
                                postParm += "}";
                            }
                        }
                    }
                }
                cmd.add("get wifi device"); // description
                cmd.add(WI_GetDevice);   // cmd
                cmd.add("get wifi devicestatus");
                cmd.add(WI_GetDeviceStatus);
                pData.setCmd(cmd);
            } else if (oper.equals(WI_Callback)) {//"cancelAppointment")) {
                dataSt = dataObj.getData();
                dataSt = ServiceAFweb.replaceAll("\"", "", dataSt);
                dataSt = ServiceAFweb.replaceAll("[", "", dataSt);
                dataSt = ServiceAFweb.replaceAll("]", "", dataSt);
                dataSt = ServiceAFweb.replaceAll("{", "", dataSt);
                dataSt = ServiceAFweb.replaceAll("}", "", dataSt);
                // skip no information

            } else {
                logger.info("> getFeatureSsnsAppointment Other oper " + oper);
                return "";
            }
            if (oper.equals(WI_GetDevice)) {
                // for testing ignore WI_Getdev becase always no info
                return "";
                // for testing
            } else {
                if (serialid.equals("")) {
                    return "";
                }
            }
//            logger.info(dataSt);
/////////////
            //call devop to get customer id
            SsnsAcc NAccObj = new SsnsAcc();
            NAccObj.setDown("splunkflow");
            boolean stat = this.updateSsnsWifi(oper, banid, uniquid, prodClass, serialid, parm, postParm, pData, dataObj, NAccObj);
            if (stat == true) {
                if (async == 1) {
                    String feat = NAccObj.getName() + ":Async";
                    NAccObj.setName(feat);
                }

                ArrayList<SsnsAcc> ssnsAccObjList = getSsnsDataImp().getSsnsAccObjList(NAccObj.getName(), NAccObj.getUid());
                boolean exist = false;
                if (ssnsAccObjList != null) {
                    if (ssnsAccObjList.size() != 0) {
                        SsnsAcc ssnsObj = ssnsAccObjList.get(0);
                        if (ssnsObj.getDown().equals("splunkflow")) {
                            exist = true;
                        }
                    }
                }
                if (exist == false) {

                    ssnsAccObjList = getSsnsDataImp().getSsnsAccObjListByTiid(NAccObj.getName(), NAccObj.getTiid());
                    if (ssnsAccObjList != null) {
                        if (ssnsAccObjList.size() > 3) {
                            exist = true;
                        }
                    }
                }
                if (exist == false) {
                    int ret = getSsnsDataImp().insertSsnsAccObject(NAccObj);
                }
            }
            return NAccObj.getName();
        } catch (Exception ex) {
            logger.info("> getFeatureSsnsWifiProcess Exception " + ex.getMessage());
        }
        return "";
    }

    public boolean updateSsnsWifi(String oper, String banid, String uniquid, String prodClass, String serialid, String parm, String postParm, ProductData pData, SsnsData dataObj, SsnsAcc NAccObj) {
        try {
            String featTTV = "";
            int connectDevice = 0;
            int boost = 0;
            int extender = 0;
            if (oper.equals(WI_GetDeviceStatus) || oper.equals(WI_Callback) || oper.equals(WI_config)) {
                if ((banid.length() == 0) && (serialid.length() == 0)) {
                    return false;
                } else {
                    String outputSt = null;
                    if (oper.equals(WI_GetDeviceStatus)) {
                        outputSt = SendSsnsWifi(ServiceAFweb.URL_PRODUCT, oper, banid, uniquid, prodClass, serialid, "", null);
                        if (outputSt == null) {
                            return false;
                        }
                        if (parm.length() > 0) {
                            String outputStConnect = SendSsnsWifi(ServiceAFweb.URL_PRODUCT, oper, banid, uniquid, prodClass, serialid, parm, null);
                            if (outputStConnect.indexOf("macAddressTxt") != -1) {
                                connectDevice = 1;
                            }

                        }

                    } else if (oper.equals(WI_config)) {
                        outputSt = SendSsnsWifi(ServiceAFweb.URL_PRODUCT, WI_GetDeviceStatus, banid, uniquid, prodClass, serialid, parm, null);
                        if (outputSt == null) {
                            return false;
                        }
                        String outputDeviceSt = SendSsnsWifi(ServiceAFweb.URL_PRODUCT, WI_GetDevice, banid, uniquid, prodClass, serialid, parm, null);
                        if (outputDeviceSt.indexOf("Boost Device") != -1) {
                            boost = 1;
                        }
                        if (outputDeviceSt.indexOf("WirelessExtender") != -1) {
                            extender = 1;
                        }
                    }
                    if (outputSt == null) {
                        return false;
                    }
                    if (outputSt.length() == 0) {
                        return false;
                    }
//                    if (outputSt.length() < 80) {  // or test 
//                        return false;
//                    }
                    if (outputSt.indexOf("responseCode:400500") != -1) {
                        return false;
                    }
                    featTTV = parseWifiFeature(outputSt, oper, prodClass);

                    if (connectDevice == 1) {
                        featTTV += ":" + parm;
                    }
                    if (boost == 1) {
                        featTTV += ":BoostD";
                    }
                    if (extender == 1) {
                        featTTV += ":ExtenderD";
                    }

                }
            } else if (oper.equals(APP_CAN_APP)) {   //"cancelAppointment";
                featTTV = APP_FEAT_TYPE_APP;
                featTTV += ":" + oper;
//                featTTV += ":" + host;
//                if ((banid.length() == 0) && (cust.length() == 0)) {
//                    featTTV += ":ContactEng";
//                }
            } else {
                return false;
            }
            if (banid.length() >= 10) {
                featTTV += ":NotaBan";
            }
//            logger.info("> updateSsnsWifi feat " + featTTV);
/////////////TTV   
            if (NAccObj.getDown().equals("splunkflow")) {
                ArrayList<String> callback = new ArrayList();
                int faulureCall = getSsnsFlowTraceWifiCallback(dataObj, callback, postParm);
                if (faulureCall == 0) {
                    pData.setCallback(callback);
                }
                ArrayList<String> flow = new ArrayList();
                int faulure = getSsnsFlowTrace(dataObj, flow);
                if (flow == null) {
                    logger.info("> updateSsnsAppointment skip no flow");
                    return false;
                }
                pData.setFlow(flow);

                if (faulure == 1) {
                    featTTV += ":splunkfailed";
                }
            }
            logger.info("> updateSsnsWifi feat " + featTTV);
            pData.setPostParam(postParm);
            NAccObj.setName(featTTV);
            NAccObj.setBanid(banid);
            NAccObj.setCusid(dataObj.getCusid());

            String deviceInfo = uniquid + ":" + prodClass + ":" + serialid + ":" + parm + ":end";
            NAccObj.setTiid(deviceInfo);

            NAccObj.setUid(dataObj.getUid());
            NAccObj.setApp(dataObj.getApp());
            NAccObj.setOper(oper);

//          NAccObj.setDown(""); // set by NAccObj
            NAccObj.setRet(dataObj.getRet());
            NAccObj.setExec(dataObj.getExec());

            String nameSt = new ObjectMapper().writeValueAsString(pData);
            NAccObj.setData(nameSt);

            NAccObj.setUpdatedatel(dataObj.getUpdatedatel());
            NAccObj.setUpdatedatedisplay(new java.sql.Date(dataObj.getUpdatedatel()));

            return true;
        } catch (Exception ex) {
            logger.info("> updateSsnsAppointment Exception " + ex.getMessage());
        }
        return false;
    }

    public static String parseWifiFeature(String outputSt, String oper, String prodClass) {

        if (outputSt == null) {
            return null;
        }

        int smartInit = 0;
        int catInit = 0;
        int cujoInit = 0;

        String smartSteering = "";
        int guestDevice = 0;
        String frequency = "";
        int cujoAgent = 0;

        ArrayList<String> outputList = ServiceAFweb.prettyPrintJSON(outputSt);
        for (int j = 0; j < outputList.size(); j++) {
            String inLine = outputList.get(j);
//            logger.info("" + inLine);

            if (inLine.indexOf("smartSteeringEnabledInd") != -1) {
                if (catInit == 1) {
                    continue;
                }
                catInit = 1;
                String valueSt = inLine;
                valueSt = ServiceAFweb.replaceAll("\"", "", valueSt);
                valueSt = ServiceAFweb.replaceAll("smartSteeringEnabledInd:", "", valueSt);
                valueSt = ServiceAFweb.replaceAll(",", "", valueSt);
                smartSteering = valueSt;

                continue;
            }
            if (inLine.indexOf("guestDevice") != -1) {
                if (smartInit == 1) {
                    continue;
                }
                smartInit = 1;
                guestDevice = 1;
                continue;
            }
            if (inLine.indexOf("wirelessRadioFrequencyTxt") != -1) {

                String valueSt = inLine;
                valueSt = ServiceAFweb.replaceAll("\"", "", valueSt);
                valueSt = ServiceAFweb.replaceAll("wirelessRadioFrequencyTxt:", "", valueSt);
                valueSt = ServiceAFweb.replaceAll(",", "", valueSt);
                String freq = frequency;
                if (freq.length() == 0) {
                    freq += valueSt;
                } else {
                    freq += "_" + valueSt;
                }
                frequency = freq;
                continue;
            }
            if (inLine.indexOf("cujoAgentEnabledInd") != -1) {
                if (cujoInit == 1) {
                    continue;
                }
                cujoInit = 1;
                cujoAgent = 1;;
                continue;
            }
        }

        String featTTV = APP_FEAT_TYPE_WIFI;
        featTTV += ":" + oper;
        featTTV += ":" + prodClass;

        String sm = smartSteering;
        if (sm.length() == 0) {
            sm = "noSSteering";
        } else {
            sm = "SSteering_" + sm;
        }
        featTTV += ":" + sm;

        String freq = frequency;
        if (freq.length() == 0) {
            freq = "noFreq";
        } else {
            freq = "Freq_" + freq;
        }
        featTTV += ":" + freq;

        String guest = "noGuestD";
        if (guestDevice == 1) {
            guest = "GuestD";
        }
        featTTV += ":" + guest;
        String cujo = "noCujo";
        if (cujoAgent == 1) {
            cujo = "Cujo";
        }
        featTTV += ":" + cujo;
        return featTTV;
    }

    // 1 faulure, 0 = success
    public int getSsnsFlowTraceWifiCallback(SsnsData dataObj, ArrayList<String> flow, String postParm) {
        if (postParm == null) {
            return 1;
        }
        if (postParm.length() == 1) {
            return 1;
        }
        String newUid = "";

        if (postParm.indexOf("asynchronousRequest") != -1) {
            String[] operList = postParm.split(",");
            for (int j = 0; j < operList.length; j++) {
                String inLine = operList[j];
                if (inLine.indexOf("operationId") != -1) {
                    String valueSt = inLine;
                    valueSt = ServiceAFweb.replaceAll("\"", "", valueSt);
                    valueSt = ServiceAFweb.replaceAll("operationId:", "", valueSt);
                    newUid = valueSt;
                    break;
                }
            }
        }

        if (newUid.length() == 0) {
            return 1;
        }

        String uid = newUid;

        ArrayList<SsnsData> ssnsList = getSsnsDataImp().getSsnsDataObjListByUid(dataObj.getApp(), uid);
        if (ssnsList != null) {
//            logger.info("> ssnsList " + ssnsList.size());
            for (int i = 0; i < ssnsList.size(); i++) {
                SsnsData data = ssnsList.get(i);
                String flowSt = data.getDown();
                if (flowSt.length() == 0) {
                    flowSt = data.getOper();
                }
                flowSt += ":" + data.getExec();
                String dataTxt = data.getData();
                if (dataTxt.indexOf("[tocpresp,") != -1) {
                    try {
                        String valueSt = ServiceAFweb.replaceAll("[tocpresp,{node:", "", dataTxt);
                        valueSt = valueSt.substring(0, valueSt.length() - 2);
                        String filteredStr = valueSt.replaceAll(" ", "");
                        String[] filteredList = filteredStr.split("><");

                        flow.add(postParm);
                        for (int k = 0; k < filteredList.length; k++) {
                            String ln = filteredList[k];
                            if (k == 0) {
                                ln = ln + ">";
                            } else if (k == filteredList.length - 1) {
                                ln = "<" + ln;
                            } else {
                                ln = "<" + ln + ">";
                            }
                            flow.add(ln);
                        }
                        return 0;

                    } catch (Exception ex) {
                        logger.info(ex.getMessage());
                    }
                }

            }
        }
        return 1;

//        if (dataObj.getOper().equals(WI_config)) {
//            String dataSt = dataObj.getData();
//            dataSt = ServiceAFweb.replaceAll("\"", "", dataSt);
//            dataSt = ServiceAFweb.replaceAll("[", "", dataSt);
//            dataSt = ServiceAFweb.replaceAll("]", "", dataSt);
//            dataSt = ServiceAFweb.replaceAll("{", "", dataSt);
//            dataSt = ServiceAFweb.replaceAll("}", "", dataSt);
//            String[] dataList = dataSt.split(",");
//            String callUid = "";
//            for (int i = 0; i < dataList.length; i++) {
//                String inLine = dataList[i];
//                if (inLine.indexOf("operationId") != -1) {
//                    String valueSt = inLine;
//                    valueSt = ServiceAFweb.replaceAll("\"", "", valueSt);
//                    valueSt = ServiceAFweb.replaceAll("operationId:", "", valueSt);
//                    if (valueSt.length() >= 36) {
//                        callUid = valueSt.substring(0, 36);  // overrid uuid for call back
//                        break;
//                    }
//                }
//            }
//            if (callUid.length() > 0) {
//                ssnsList = getSsnsDataImp().getSsnsDataObjListByUid(dataObj.getApp(), callUid);
//                if (ssnsList != null) {
//                    for (int i = 0; i < ssnsList.size(); i++) {
//                        SsnsData data = ssnsList.get(i);
//                        String flowSt = data.getDown();
//                        if (flowSt.length() == 0) {
//                            flowSt = data.getOper();
//                        }
//                        flowSt += ":" + data.getExec();
//                        flowSt += ":" + data.getData();
//                        flow.add(flowSt);
//                    }
//                }
//            }
    }

    public String SendSsnsTestURL(String ProductURL, ArrayList<String> inList) {
        String url = ProductURL;
        try {
            if (inList != null) {
                inList.add(url);
            }
            // calculate elapsed time in milli seconds
            long startTime = TimeConvertion.currentTimeMillis();

            String output = this.sendRequest_Ssns(METHOD_GET, url, null, null);

            long endTime = TimeConvertion.currentTimeMillis();
            long elapsedTime = endTime - startTime;
//            System.out.println("Elapsed time in milli seconds: " + elapsedTime);
            if (inList != null) {
                inList.add("elapsedTime:" + elapsedTime);
                inList.add("output:");
            }

            return output;
        } catch (Exception ex) {
            logger.info("> SsnsAppointment exception " + ex.getMessage());
        }
        return null;
    }

    public String SendSsnsWifi(String ProductURL, String oper, String banid, String uniquid, String prodClass, String serialid, String parm, ArrayList<String> inList) {
        String url = "";
        if (banid.length() >= 10) {
            logger.info("> SendSsnsWifi Bandid is Phonenumber " + banid);
        }
        if (oper.equals(WI_GetDevice)) {
            url = ProductURL + "/v1/cmo/selfmgmt/wifimanagement/account/" + banid
                    + "/device";
        } else if (oper.equals(WI_GetDeviceStatus)) {

            url = ProductURL + "/v1/cmo/selfmgmt/wifimanagement/account/" + banid
                    + "/device/organizationuniqueid/" + uniquid
                    + "/productclass/" + prodClass
                    + "/serialnumber/" + serialid
                    + "/status";

            if (parm.indexOf("connectdevicelist") != -1) {
                url += "?fields=connectDeviceList&connectdevicelist.statuscd=pause";
            }

        } else {
            return "";
        }
        try {
            if (inList != null) {
                inList.add(url);
            }
            // calculate elapsed time in milli seconds
            long startTime = TimeConvertion.currentTimeMillis();

            String output = this.sendRequest_Ssns(METHOD_GET, url, null, null);

            long endTime = TimeConvertion.currentTimeMillis();
            long elapsedTime = endTime - startTime;
//            System.out.println("Elapsed time in milli seconds: " + elapsedTime);
            if (inList != null) {
                inList.add("elapsedTime:" + elapsedTime);
                inList.add("output:");
            }

            return output;
        } catch (Exception ex) {
            logger.info("> SsnsAppointment exception " + ex.getMessage());
        }
        return null;
    }

    public String TestFeatureSsnsProdWifi(SsnsAcc dataObj, ArrayList<String> outputList, String Oper, String LABURL) {
        if (dataObj == null) {
            return "";
        }
        if (LABURL.length() == 0) {
            LABURL = ServiceAFweb.URL_PRODUCT;
        }
        dataObj.getData();
        String banid = dataObj.getBanid();
        String appTId = dataObj.getTiid();
        if (appTId.length() == 0) {
            return "";
        }
        String WifiparL[] = appTId.split(":");

        String uniquid = WifiparL[0];
        String prodClass = WifiparL[1];
        String serialid = WifiparL[2];
        String parm = "";

        if (WifiparL.length > 3) {
            parm = WifiparL[3];
        }

        String outputSt = null;
        int connectDevice = 0;
        ArrayList<String> inList = new ArrayList();
        if (Oper.equals(WI_GetDeviceStatus)) {
            outputSt = SendSsnsWifi(LABURL, Oper, banid, uniquid, prodClass, serialid, "", inList);
            if (parm.length() > 0) {
                String outputStConnect = SendSsnsWifi(LABURL, Oper, banid, uniquid, prodClass, serialid, parm, null);
                if (outputStConnect.indexOf("macAddressTxt") != -1) {
                    connectDevice = 1;
                }

            }
            if (outputSt == null) {

                return "";
            }
            ////special char #, need to ignore for this system
            outputSt = outputSt.replaceAll("#", "");
            outputSt = outputSt.replaceAll("~", "");
            outputSt = outputSt.replaceAll("^", "");

            ArrayList<String> outList = ServiceAFweb.prettyPrintJSON(outputSt);
            String feat = parseWifiFeature(outputSt, Oper, prodClass);
            if (outputSt.indexOf("responseCode:400500") != -1) {
                feat += ":testfailed";
            }
            if (connectDevice == 1) {
                feat += ":" + parm;
            }
            outputList.add(feat);
            outputList.addAll(inList);
            outputList.addAll(outList);

            return feat;
        } else if (Oper.equals(WI_GetDevice)) {

            outputSt = SendSsnsWifi(LABURL, Oper, banid, uniquid, prodClass, serialid, Oper, inList);
            if (outputSt == null) {
                return "";
            }
            ////special char #, need to ignore for this system
            outputSt = outputSt.replaceAll("#", "");
            outputSt = outputSt.replaceAll("~", "");
            outputSt = outputSt.replaceAll("^", "");

            ArrayList<String> outList = ServiceAFweb.prettyPrintJSON(outputSt);

            String feat = dataObj.getName();
            for (int i = 0; i < outList.size(); i++) {
                String inLine = outList.get(i);
                inLine = ServiceAFweb.replaceAll("\"", "", inLine);
                inLine = ServiceAFweb.replaceAll(",", "", inLine);

                if (inLine.indexOf("deviceTypeCd") != -1) {
                    String dCd = ServiceAFweb.replaceAll("deviceTypeCd:", "", inLine);
                    feat += ":" + dCd;
                }
                if (inLine.indexOf("productClassId") != -1) {
                    String dCd = ServiceAFweb.replaceAll("productClassId:", "", inLine);
                    feat += ":" + dCd;
                }
            }
            if (outputSt.indexOf("responseCode:400500") != -1) {
                feat += ":testfailed";
            }
            outputList.add(feat);
            outputList.addAll(inList);
            outputList.addAll(outList);
            return feat;
        }

        return "";
    }

////////////////////////////////////////////    
    public String getFeatureSsnsAppointment(SsnsData dataObj) {
        String feat = "";
        try {
            feat = getFeatureSsnsAppointmentProcess(dataObj);
        } catch (Exception ex) {
            logger.info("> getFeatureSsnsAppointment Exception " + ex.getMessage());
        }
        getSsnsDataImp().updatSsnsDataStatusById(dataObj.getId(), ConstantKey.COMPLETED);
        return feat;
    }

    public String getFeatureSsnsAppointmentProcess(SsnsData dataObj) {
        ProductData pData = new ProductData();
        ArrayList<String> cmd = new ArrayList();
        if (dataObj == null) {
            return "";
        }

        String appTId = "";
        String banid = "";
        String cust = "";
        String host = "";
        String dataSt = "";
        int devOPflag = 0;
        try {
            String oper = dataObj.getOper();
            if (oper.equals(APP_UPDATE)) { //"updateAppointment")) {
                dataSt = dataObj.getData();
                dataSt = ServiceAFweb.replaceAll("\"", "", dataSt);
                dataSt = ServiceAFweb.replaceAll("[", "", dataSt);
                dataSt = ServiceAFweb.replaceAll("]", "", dataSt);
                dataSt = ServiceAFweb.replaceAll("{", "", dataSt);
                dataSt = ServiceAFweb.replaceAll("}", "", dataSt);
                String[] operList = dataSt.split(",");
                if (operList.length > 3) {
                    appTId = operList[0];
                    banid = operList[1];
                    banid = banid.replace("ban:", "");
                    cust = operList[2];
                    cust = cust.replace("customerId:", "");
                    for (int k = 0; k < operList.length; k++) {
                        String inLine = operList[k];

                        if (inLine.indexOf("hostSystemCd:") != -1) {
                            host = inLine;
                            host = host.replace("hostSystemCd:", "");
                        }
                    }
                }
                if ((banid.length() == 0) && (cust.length() == 0)) {
                    ;
                } else {
                    cmd.add("get appointment"); // cmd
                    cmd.add(APP_GET_APP);  // descriptoin
                }
                cmd.add("search timeslot");
                cmd.add(APP_GET_TIMES);
                pData.setCmd(cmd);

            } else if (oper.equals(APP_GET_TIMES)) { //"timeslot")) {
                dataSt = dataObj.getData();
                dataSt = ServiceAFweb.replaceAll("\"", "", dataSt);
                dataSt = ServiceAFweb.replaceAll("[", "", dataSt);
                dataSt = ServiceAFweb.replaceAll("]", "", dataSt);
                dataSt = ServiceAFweb.replaceAll("{", "", dataSt);
                dataSt = ServiceAFweb.replaceAll("}", "", dataSt);
                String[] operList = dataSt.split(",");
                if (operList.length > 3) {
                    int custInti = 0;
                    for (int k = 0; k < operList.length; k++) {
                        String inLine = operList[k];
                        if (inLine.indexOf("ban:") != -1) {
                            banid = inLine;
                            banid = host.replace("ban:", "");
                            continue;
                        }
                        if (inLine.indexOf("customerId:") != -1) {

                            cust = inLine;
                            cust = host.replace("customerId:", "");
                            continue;
                        }
                        if (inLine.indexOf("id:") != -1) {
                            if (custInti == 1) {
                                continue;
                            }
                            custInti = 1;
                            appTId = inLine;
                            appTId = appTId.replace("id:", "");
                            continue;
                        }
                        if (inLine.indexOf("hostSystemCd:") != -1) {
                            host = inLine;
                            host = host.replace("hostSystemCd:", "");
                            continue;
                        }
                    }
                    if ((banid.length() == 0) && (cust.length() == 0)) {
                        ;
                    } else {
                        cmd.add("get appointment");
                        cmd.add(APP_GET_APP);
                    }
                    cmd.add("search timeslot");
                    cmd.add(APP_GET_TIMES);
                    pData.setCmd(cmd);
                }
            } else if (oper.equals(APP_GET_APP)) { //"getAppointment")) {
                dataSt = dataObj.getData();
                dataSt = ServiceAFweb.replaceAll("\"", "", dataSt);
                dataSt = ServiceAFweb.replaceAll("[", "", dataSt);
                dataSt = ServiceAFweb.replaceAll("]", "", dataSt);
                dataSt = ServiceAFweb.replaceAll("{", "", dataSt);
                dataSt = ServiceAFweb.replaceAll("}", "", dataSt);
                String[] operList = dataSt.split(",");
                if (operList.length > 3) {
                    banid = operList[0];
                    cust = operList[1];
                }
                cmd.add("get appointment");
                cmd.add(APP_GET_APP);
                pData.setCmd(cmd);
            } else if (oper.equals(APP_CAN_APP)) {//"cancelAppointment")) {
                dataSt = dataObj.getData();
                dataSt = ServiceAFweb.replaceAll("\"", "", dataSt);
                dataSt = ServiceAFweb.replaceAll("[", "", dataSt);
                dataSt = ServiceAFweb.replaceAll("]", "", dataSt);
                dataSt = ServiceAFweb.replaceAll("{", "", dataSt);
                dataSt = ServiceAFweb.replaceAll("}", "", dataSt);
                String[] operList = dataSt.split(",");
                if (operList.length > 3) {
                    appTId = operList[0];
                    banid = operList[1];
                    if (banid.equals("null")) {
                        banid = "";
                    }
                    cust = operList[2];
                    if (cust.equals("null")) {
                        cust = "";
                    }
                    host = operList[3];
                }
            } else {
                logger.info("> getFeatureSsnsAppointmentProcess Other oper " + oper);
            }
            if (oper.equals(APP_GET_APP)) {
                // for testing ignore APP_GET_APP becase alwasy no info
                return "";
                // for testing
            } else {
                if (appTId.equals("")) {
                    return "";
                }
            }
//            logger.info(dataSt);
/////////////
            //call devop to get customer id
            if ((banid.length() == 0) && (cust.length() == 0)) {
                if (CKey.DEVOP == true) {
                    if (host.equals("FIFA") || host.equals("LYNX")) {
                        String custid = getCustIdAppointmentDevop(ServiceAFweb.URL_PRODUCT, appTId, banid, cust, host);
                        if (custid.length() != 0) {
                            cust = custid;
                            dataObj.setCusid(custid);
                            devOPflag = 1;
                            logger.info("> getFeatureSsnsAppointmentProcess found Ticket to custid " + cust);
                        }
                    }
                }
            }
            SsnsAcc NAccObj = new SsnsAcc();
            NAccObj.setDown("splunkflow");

            boolean stat = this.updateSsnsAppointment(oper, appTId, banid, cust, host, pData, dataObj, NAccObj);
            if (stat == true) {
//                if (devOPflag == 1) {
//                    String feat = NAccObj.getName() + ":TicktoCust";
//                    NAccObj.setName(feat);
//                }

                ArrayList<SsnsAcc> ssnsAccObjList = getSsnsDataImp().getSsnsAccObjList(NAccObj.getName(), NAccObj.getUid());
                boolean exist = false;
                if (ssnsAccObjList != null) {
                    if (ssnsAccObjList.size() != 0) {
                        SsnsAcc ssnsObj = ssnsAccObjList.get(0);
                        if (ssnsObj.getDown().equals("splunkflow")) {
                            exist = true;
                        }
                    }
                }
                if (exist == false) {
                    ssnsAccObjList = getSsnsDataImp().getSsnsAccObjListByTiid(NAccObj.getName(), NAccObj.getTiid());
                    if (ssnsAccObjList != null) {
                        if (ssnsAccObjList.size() > 3) {
                            exist = true;
                        }
                    }
                }
                if (exist == false) {
                    int ret = getSsnsDataImp().insertSsnsAccObject(NAccObj);
                }

            }
            return NAccObj.getName();
        } catch (Exception ex) {
            logger.info("> getFeatureSsnsAppointmentProcess Exception " + ex.getMessage());
        }
        return "";
    }

    public boolean updateSsnsAppointment(String oper, String appTId, String banid, String cust, String host, ProductData pData, SsnsData dataObj, SsnsAcc NAccObj) {
        try {
            String featTTV = "";
            String outputSt = null;
            if (oper.equals(APP_UPDATE) || oper.equals(APP_GET_TIMES) || oper.equals(APP_GET_APP)) {
                if ((banid.length() == 0) && (cust.length() == 0)) {

                    outputSt = SendSsnsAppointmentGetTimeslot(ServiceAFweb.URL_PRODUCT, appTId, banid, cust, host, null);
                    if (outputSt == null) {
                        return false;
                    }
                    if (outputSt.length() < 80) {
                        // special case for no appointment {"status":{"statusCd":"200","statusTxt":"OK"},"appointmentList":[]}
                        return false;
                    }
                    if (outputSt.indexOf("responseCode:400500") != -1) {
                        return false;
                    }
                    featTTV = parseAppointmentTimeSlotFeature(outputSt, oper, host);

                } else {
                    if (oper.equals(APP_GET_TIMES)) {
                        outputSt = SendSsnsAppointmentGetTimeslot(ServiceAFweb.URL_PRODUCT, appTId, banid, cust, host, null);
                        if (outputSt == null) {
                            return false;
                        }
                        if (outputSt.length() < 80) {
                            // special case for no appointment {"status":{"statusCd":"200","statusTxt":"OK"},"appointmentList":[]}
                            return false;
                        }
                        if (outputSt.indexOf("responseCode:400500") != -1) {
                            return false;
                        }
                        featTTV = parseAppointmentTimeSlotFeature(outputSt, oper, host);
                    } else {
                        outputSt = SendSsnsAppointmentGetApp(ServiceAFweb.URL_PRODUCT, appTId, banid, cust, host, null);
                        if (outputSt == null) {
                            return false;
                        }
                        if (outputSt.length() < 80) {
                            // special case for no appointment {"status":{"statusCd":"200","statusTxt":"OK"},"appointmentList":[]}
                            return false;
                        }
                        if (outputSt.indexOf("responseCode:400500") != -1) {
                            return false;
                        }
                        featTTV = parseAppointmentFeature(outputSt, oper);
                    }

                }

            } else if (oper.equals(APP_CAN_APP)) {   //"cancelAppointment";
                featTTV = APP_FEAT_TYPE_APP;
                featTTV += ":" + oper;
                featTTV += ":" + host;
                if ((banid.length() == 0) && (cust.length() == 0)) {
                    featTTV += ":ContactEng";
                } else {
                    featTTV += ":TD";
                }
            } else {
                return false;
            }

//            logger.info("> updateSsnsAppointment feat " + featTTV);
/////////////TTV   
            if (NAccObj.getDown().equals("splunkflow")) {

                ArrayList<String> flow = new ArrayList();
                int faulure = getSsnsFlowTrace(dataObj, flow);
                if (flow == null) {
                    logger.info("> updateSsnsAppointment skip no flow");
                    return false;
                }
                pData.setFlow(flow);

                if (faulure == 1) {
                    featTTV += ":splunkfailed";
                }
            }
            logger.info("> updateSsnsAppointment feat " + featTTV);
            NAccObj.setName(featTTV);
            NAccObj.setBanid(banid);
            NAccObj.setCusid(cust);
            NAccObj.setTiid(appTId);
            NAccObj.setUid(dataObj.getUid());
            NAccObj.setApp(dataObj.getApp());
            NAccObj.setOper(oper);

//          NAccObj.setDown(""); // set by NAccObj
            NAccObj.setRet(host);
            NAccObj.setExec(dataObj.getExec());

            String nameSt = new ObjectMapper().writeValueAsString(pData);
            NAccObj.setData(nameSt);

            NAccObj.setUpdatedatel(dataObj.getUpdatedatel());
            NAccObj.setUpdatedatedisplay(new java.sql.Date(dataObj.getUpdatedatel()));

            return true;
        } catch (Exception ex) {
            logger.info("> updateSsnsAppointment Exception " + ex.getMessage());
        }
        return false;
    }

    public static String parseAppointmentTimeSlotFeature(String outputSt, String oper, String host) {

        if (outputSt == null) {
            return "";
        }
        ArrayList<String> outList = ServiceAFweb.prettyPrintJSON(outputSt);
        String feat = APP_FEAT_TYPE_APP;
        feat += ":" + oper;
        feat += ":" + host;
        feat += ":ticketID";

        int NumofStart = 0;
        for (int i = 0; i < outList.size(); i++) {
            String inLine = outList.get(i);
            inLine = ServiceAFweb.replaceAll("\"", "", inLine);
            if (inLine.indexOf("startDate") != -1) {
                NumofStart++;
            }
        }
        if (NumofStart > 0) {
            feat += ":startdate";
        } else {
            feat += ":nostartdate";
        }

        return feat;
    }

    public static String parseAppointmentFeature(String outputSt, String oper) {

        if (outputSt == null) {
            return "";
        }

        int catCdInit = 0;
        int statInit = 0;
        int catInit = 0;
        int hostInit = 0;
        String category = "";
        String statusCd = "";
        String categoryCd = "";
        String host = "";

        ArrayList<String> outputList = ServiceAFweb.prettyPrintJSON(outputSt);
        for (int j = 0; j < outputList.size(); j++) {
            String inLine = outputList.get(j);
//            logger.info("" + inLine);

            if (inLine.indexOf("category") != -1) {
                if (catInit == 1) {
                    continue;
                }
                catInit = 1;
                String valueSt = inLine;
                valueSt = ServiceAFweb.replaceAll("\"", "", valueSt);
                valueSt = ServiceAFweb.replaceAll("category:", "", valueSt);
                valueSt = ServiceAFweb.replaceAll(",", "", valueSt);
                category = valueSt;

                continue;
            }
            if (inLine.indexOf("statusCd") != -1) {
                if (catInit == 0) {
                    continue;
                }
                if (statInit == 1) {
                    continue;
                }

                statInit = 1;
                String valueSt = inLine;
                valueSt = ServiceAFweb.replaceAll("\"", "", valueSt);
                valueSt = ServiceAFweb.replaceAll("statusCd:", "", valueSt);
                valueSt = ServiceAFweb.replaceAll(",", "", valueSt);
                statusCd = valueSt;
                continue;
            }
            if (inLine.indexOf("productCategoryCd") != -1) {

                if (catCdInit == 1) {
                    continue;
                }
                catCdInit = 1;
                String valueSt = inLine;
                valueSt = ServiceAFweb.replaceAll("\"", "", valueSt);
                valueSt = ServiceAFweb.replaceAll("productCategoryCd:", "", valueSt);
                valueSt = ServiceAFweb.replaceAll(",", "", valueSt);
                categoryCd = valueSt;
                continue;
            }
            if (inLine.indexOf("hostSystemCd") != -1) {

                if (hostInit == 1) {
                    continue;
                }
                hostInit = 1;
                String valueSt = inLine;
                valueSt = ServiceAFweb.replaceAll("\"", "", valueSt);
                valueSt = ServiceAFweb.replaceAll("hostSystemCd:", "", valueSt);
                valueSt = ServiceAFweb.replaceAll(",", "", valueSt);
                host = valueSt;
                continue;
            }
        }

        String featTTV = APP_FEAT_TYPE_APP;
        featTTV += ":" + oper;
        featTTV += ":" + host;
        featTTV += ":" + category;
        featTTV += ":" + statusCd;
        featTTV += ":" + categoryCd;

        return featTTV;
    }

    public String getCustIdAppointmentDevop(String ProductURL, String appTId, String banid, String cust, String host) {
        String url = "http://localhost:8080/v2/cmo/selfmgmt/appointmentmanagement/devop/searchtimeslot";
        HashMap newbodymap = new HashMap();
        newbodymap.put("customerId", cust);
        newbodymap.put("id", appTId);
        newbodymap.put("hostSystemCd", host);
        try {
            String custid = "";
            String output = this.sendRequest_Ssns(METHOD_POST, url, null, newbodymap);

            if (output == null) {
                return "";
            }
            if (output.indexOf("responseCode:400500") != -1) {
                return "";
            }
            ArrayList arrayItem = new ObjectMapper().readValue(output, ArrayList.class);
            if (arrayItem.size() < 1) {
                return "";
            }
            output = (String) arrayItem.get(1);
            output = ServiceAFweb.replaceAll("\"", "", output);
            output = ServiceAFweb.replaceAll("\\", "", output);
            String[] oList = output.split(",");
            for (int i = 0; i < oList.length; i++) {
                String line = oList[i];
                if (line.indexOf("customerId:") != -1) {
                    custid = ServiceAFweb.replaceAll("customerId:", "", line);
                    if (custid.equals("null")) {
                        return "";
                    }
                    return custid;
                }
            }

            return "";
        } catch (Exception ex) {
//            logger.info("> getCustIdAppointmentDevop exception " + ex.getMessage());
        }
        return "";
    }

    public String SendSsnsAppointmentGetTimeslot(String ProductURL, String appTId, String banid, String cust, String host, ArrayList<String> inList) {

        String url = ProductURL + "/v2/cmo/selfmgmt/appointmentmanagement/searchtimeslot";

        HashMap newbodymap = new HashMap();
        if (cust.length() > 0) {
            newbodymap.put("customerId", cust);
        }
        newbodymap.put("id", appTId);
        newbodymap.put("hostSystemCd", host);
        try {
            if (inList != null) {
                inList.add(url);
            }
            // calculate elapsed time in milli seconds
            long startTime = TimeConvertion.currentTimeMillis();

            String output = this.sendRequest_Ssns(METHOD_POST, url, null, newbodymap);

            long endTime = TimeConvertion.currentTimeMillis();
            long elapsedTime = endTime - startTime;
//            System.out.println("Elapsed time in milli seconds: " + elapsedTime);
            if (inList != null) {
                inList.add("elapsedTime:" + elapsedTime);
                inList.add("output:");
            }

            return output;
        } catch (Exception ex) {
            logger.info("> SsnsAppointment exception " + ex.getMessage());
        }
        return null;
    }

    public String TestFeatureSsnsProdApp(SsnsAcc dataObj, ArrayList<String> outputList, String Oper, String LABURL) {
        if (dataObj == null) {
            return "";
        }
        if (LABURL.length() == 0) {
            LABURL = ServiceAFweb.URL_PRODUCT;
        }
        dataObj.getData();
        String banid = dataObj.getBanid();
        String appTId = dataObj.getTiid();
        String cust = dataObj.getCusid();
        String host = dataObj.getRet();
        String outputSt = null;
        ArrayList<String> inList = new ArrayList();
        if (Oper.equals(APP_GET_APP)) {

            outputSt = SendSsnsAppointmentGetApp(LABURL, appTId, banid, cust, host, inList);
            if (outputSt == null) {
                return "";
            }
            ////special char #, need to ignore for this system
            outputSt = outputSt.replaceAll("#", "");
            outputSt = outputSt.replaceAll("~", "");
            outputSt = outputSt.replaceAll("^", "");

            ArrayList<String> outList = ServiceAFweb.prettyPrintJSON(outputSt);
            String feat = parseAppointmentFeature(outputSt, Oper);
            if (outputSt.indexOf("responseCode:400500") != -1) {
                feat += ":testfailed";
            }
            outputList.add(feat);
            outputList.addAll(inList);
            outputList.addAll(outList);

            return feat;
        } else if (Oper.equals(APP_GET_TIMES)) {
            outputSt = SendSsnsAppointmentGetTimeslot(LABURL, appTId, banid, cust, host, inList);
            if (outputSt == null) {
                return "";
            }
            ////special char #, need to ignore for this system
            outputSt = outputSt.replaceAll("#", "");
            outputSt = outputSt.replaceAll("~", "");
            outputSt = outputSt.replaceAll("^", "");

            ArrayList<String> outList = ServiceAFweb.prettyPrintJSON(outputSt);
            String feat = parseAppointmentTimeSlotFeature(outputSt, Oper, host);

            if (outputSt.indexOf("responseCode:400500") != -1) {
                feat += ":testfailed";
            }
            outputList.add(feat);
            outputList.addAll(inList);
            outputList.addAll(outList);
            return feat;
        }

        return "";
    }

    public String SendSsnsAppointmentGetApp(String ProductURL, String appTId, String banid, String cust, String host, ArrayList<String> inList) {
        if (host.length() > 0) {
            host = host.replace("9", ""); // remove OMS9
            host = host.replace("6", ""); // remove OMS9
        }
        String url = ProductURL + "/v2/cmo/selfmgmt/appointmentmanagement/appointment?customerid=" + cust;
        if (banid.length() > 0) {
            url = ProductURL + "/v2/cmo/selfmgmt/appointmentmanagement/appointment?ban=" + banid + "&customerid=" + cust;
            if (host.length() > 0) {
                url += "&appointmentlist.hostsystemcd.in=" + host;
            }
        }
        try {
            if (inList != null) {
                inList.add(url);
            }
            // calculate elapsed time in milli seconds
            long startTime = TimeConvertion.currentTimeMillis();

            String output = this.sendRequest_Ssns(METHOD_GET, url, null, null);

            long endTime = TimeConvertion.currentTimeMillis();
            long elapsedTime = endTime - startTime;
//            System.out.println("Elapsed time in milli seconds: " + elapsedTime);
            if (inList != null) {
                inList.add("elapsedTime:" + elapsedTime);
                inList.add("output:");
            }
            return output;
        } catch (Exception ex) {
            logger.info("> SsnsAppointment exception " + ex.getMessage());
        }
        return null;
    }

    public String TestFeatureSsnsProductInventory(SsnsAcc dataObj, ArrayList<String> outputList, String oper, String LABURL) {
        if (dataObj == null) {
            return "";
        }
        if (LABURL.length() == 0) {
            LABURL = ServiceAFweb.URL_PRODUCT;
        }
        String banid = dataObj.getBanid();
        String prodid = dataObj.getTiid();
        ArrayList<String> inList = new ArrayList();
        String outputSt = SendSsnsProdiuctInventory(LABURL, banid, prodid, oper, inList);
        if (outputSt == null) {
            return "";
        }
        String featTTV = "";
        ////special char #, need to ignore for this system
        outputSt = outputSt.replaceAll("#", "");
        outputSt = outputSt.replaceAll("~", "");
        outputSt = outputSt.replaceAll("^", "");
        ArrayList<String> outList = ServiceAFweb.prettyPrintJSON(outputSt);
        if (oper.equals(APP_FEAT_TYPE_HSIC)) {
            featTTV = parseProductInternetFeature(outputSt, dataObj.getOper());

        } else if (oper.equals(APP_FEAT_TYPE_TTV)) {
            featTTV = parseProductTtvFeature(outputSt, dataObj.getOper());

        } else if (oper.equals(APP_FEATT_TYPE_SING)) {
            featTTV = parseProductPhoneFeature(outputSt, dataObj.getOper());

        }
        if (outputSt.indexOf("responseCode:400500") != -1) {
            featTTV += ":testfailed";
        }
        outputList.add(featTTV);
        outputList.addAll(inList);
        outputList.addAll(outList);

        return featTTV;
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////    
    public String getFeatureSsnsProdiuctInventory(SsnsData dataObj) {
        String feat = "";
        try {
            feat = getFeatureSsnsProdiuctInventoryProcess(dataObj);
        } catch (Exception ex) {
            logger.info("> getFeatureSsnsAppointment Exception " + ex.getMessage());
        }
        getSsnsDataImp().updatSsnsDataStatusById(dataObj.getId(), ConstantKey.COMPLETED);
        return feat;
    }

    public String getFeatureSsnsProdiuctInventoryProcess(SsnsData dataObj) {

        ProductData pData = new ProductData();
        ArrayList<String> cmd = new ArrayList();
        if (dataObj == null) {
            return "";
        }
        String prodid = "";
        String banid = "";
        try {

            String oper = dataObj.getOper();
            String daSt = dataObj.getData();
            //["xxx","xxx","product.characteristic.channelInfoList",null,null,null]
            daSt = ServiceAFweb.replaceAll("[", "", daSt);
            daSt = ServiceAFweb.replaceAll("]", "", daSt);
            daSt = ServiceAFweb.replaceAll("\"", "", daSt);
            String[] daList = daSt.split(",");
            if (daList.length < 3) {
                return "";
            }
            if (oper.equals(PROD_GET_BYID)) {
                prodid = daList[0];
                banid = daList[1];
            } else if (oper.equals(PROD_GET_PROD)) {
                banid = daList[0];
            } else {
                logger.info("> getFeatureSsnsProdiuctInventory Other oper " + oper);
            }
            if (banid.equals("null")) {
                return "";
            }

//            logger.info(daSt);
/////////////
            if (oper.equals(PROD_GET_BYID)) {

                String outputSt = SendSsnsProdiuctInventoryByProdId(ServiceAFweb.URL_PRODUCT, banid, prodid, null);
                if (outputSt == null) {
                    return "";
                }
                ArrayList<String> outputList = ServiceAFweb.prettyPrintJSON(outputSt);
                String valueSt = "";
                for (int j = 0; j < outputList.size(); j++) {
                    String inLine = outputList.get(outputList.size() - 1 - j);
                    if (inLine.indexOf("productType") != -1) {
                        valueSt = inLine;
                        valueSt = ServiceAFweb.replaceAll("\"", "", valueSt);
                        valueSt = ServiceAFweb.replaceAll("productType:", "", valueSt);
                        break;
                    }
                }
// 
                if (valueSt.length() == 0) {
                    return "";
                }
                String PIoper = valueSt;

                SsnsAcc NAccObj = new SsnsAcc();
                NAccObj.setTiid(prodid);
                NAccObj.setRet(PIoper);
                NAccObj.setDown("splunkflow");
//    public static String APP_PRODUCT_TYPE_TTV = "TTV";
//    public static String APP_PRODUCT_TYPE_HSIC = "HSIC";
//    public static String APP_PRODUCT_TYPE_SING = "SING";                

                cmd.add("get " + PIoper + " productby id"); // description
                cmd.add(PROD_GET_BYID); // cmd
                pData.setCmd(cmd);

                boolean stat = this.updateSsnsProdiuctInventoryByProdId(PIoper, banid, prodid, pData, dataObj, NAccObj);
                if (stat == true) {
                    ArrayList<SsnsAcc> ssnsAccObjList = getSsnsDataImp().getSsnsAccObjList(NAccObj.getName(), NAccObj.getUid());
                    boolean exist = false;
                    if (ssnsAccObjList != null) {
                        if (ssnsAccObjList.size() != 0) {
                            SsnsAcc ssnsObj = ssnsAccObjList.get(0);
                            if (ssnsObj.getDown().equals("splunkflow")) {
                                exist = true;
                            }
                        }
                    }
                    if (exist == false) {
                        ssnsAccObjList = getSsnsDataImp().getSsnsAccObjListByBan(NAccObj.getName(), NAccObj.getBanid());
                        if (ssnsAccObjList != null) {
                            if (ssnsAccObjList.size() > 3) {
                                exist = true;
                            }
                        }
                    }
                    if (exist == false) {
                        int ret = getSsnsDataImp().insertSsnsAccObject(NAccObj);
                    }
                }
            }
//            
            SsnsAcc NAccObj = new SsnsAcc();
            if (oper.equals(PROD_GET_PROD)) {

                NAccObj.setTiid(prodid);
                NAccObj.setRet(APP_FEATT_TYPE_SING);
                NAccObj.setDown("splunkflow");
                String PIoper = APP_FEATT_TYPE_SING;

                cmd.add("get " + PIoper + " productby id"); // description
                cmd.add(PROD_GET_BYID); // cmd
                pData.setCmd(cmd);

                boolean stat = this.updateSsnsProdiuctInventory(PIoper, banid, prodid, pData, dataObj, NAccObj);
                if (stat == true) {
                    ArrayList<SsnsAcc> ssnsAccObjList = getSsnsDataImp().getSsnsAccObjList(NAccObj.getName(), NAccObj.getUid());
                    boolean exist = false;
                    if (ssnsAccObjList != null) {
                        if (ssnsAccObjList.size() != 0) {
                            SsnsAcc ssnsObj = ssnsAccObjList.get(0);
                            if (ssnsObj.getDown().equals("splunkflow")) {
                                exist = true;
                            }
                        }
                    }
                    if (exist == false) {
                        ssnsAccObjList = getSsnsDataImp().getSsnsAccObjListByBan(NAccObj.getName(), NAccObj.getBanid());
                        if (ssnsAccObjList != null) {
                            if (ssnsAccObjList.size() > 3) {
                                exist = true;
                            }
                        }
                    }
                    if (exist == false) {
                        int ret = getSsnsDataImp().insertSsnsAccObject(NAccObj);
                    }
                }
                NAccObj = new SsnsAcc();
                NAccObj.setTiid(prodid);
                NAccObj.setRet(APP_FEAT_TYPE_HSIC);
                NAccObj.setDown("splunkflow");
                PIoper = APP_FEAT_TYPE_HSIC;

                cmd.add("get " + PIoper + " productby id"); // description
                cmd.add(PROD_GET_BYID); // cmd
                pData.setCmd(cmd);

                stat = this.updateSsnsProdiuctInventory(PIoper, banid, prodid, pData, dataObj, NAccObj);
                if (stat == true) {
                    ArrayList<SsnsAcc> ssnsAccObjList = getSsnsDataImp().getSsnsAccObjList(NAccObj.getName(), NAccObj.getUid());
                    boolean exist = false;
                    if (ssnsAccObjList != null) {
                        if (ssnsAccObjList.size() != 0) {
                            SsnsAcc ssnsObj = ssnsAccObjList.get(0);
                            if (ssnsObj.getDown().equals("splunkflow")) {
                                exist = true;
                            }
                        }
                    }
                    if (exist == false) {
                        ssnsAccObjList = getSsnsDataImp().getSsnsAccObjListByBan(NAccObj.getName(), NAccObj.getBanid());
                        if (ssnsAccObjList != null) {
                            if (ssnsAccObjList.size() > 3) {
                                exist = true;
                            }
                        }
                    }
                    if (exist == false) {
                        int ret = getSsnsDataImp().insertSsnsAccObject(NAccObj);
                    }
                }

                NAccObj = new SsnsAcc();
                NAccObj.setTiid(prodid);
                NAccObj.setRet(APP_FEAT_TYPE_TTV);
                NAccObj.setDown("splunkflow");
                PIoper = APP_FEAT_TYPE_TTV;

                cmd.add("get " + PIoper + " productby id"); // description
                cmd.add(PROD_GET_BYID); // cmd
                pData.setCmd(cmd);

                stat = this.updateSsnsProdiuctInventory(PIoper, banid, prodid, pData, dataObj, NAccObj);
                if (stat == true) {
                    ArrayList<SsnsAcc> ssnsAccObjList = getSsnsDataImp().getSsnsAccObjList(NAccObj.getName(), NAccObj.getUid());
                    boolean exist = false;
                    if (ssnsAccObjList != null) {
                        if (ssnsAccObjList.size() != 0) {
                            SsnsAcc ssnsObj = ssnsAccObjList.get(0);
                            if (ssnsObj.getDown().equals("splunkflow")) {
                                exist = true;
                            }
                        }
                    }
                    if (exist == false) {
                        ssnsAccObjList = getSsnsDataImp().getSsnsAccObjListByBan(NAccObj.getName(), NAccObj.getBanid());
                        if (ssnsAccObjList != null) {
                            if (ssnsAccObjList.size() > 3) {
                                exist = true;
                            }
                        }
                    }
                    if (exist == false) {
                        int ret = getSsnsDataImp().insertSsnsAccObject(NAccObj);
                    }
                }
            }
            return NAccObj.getName();
        } catch (Exception ex) {
            logger.info("> getFeatureSsnsProdiuctInventoryProcess Exception " + ex.getMessage());
        }
        return "";
    }

    public static String parseProductPhoneFeature(String outputSt, String oper) {
        if (outputSt == null) {
            return "";
        }
        try {

            int quotaAmtInit = 0;
            int fifaInit = 0;
            int planInit = 0;
            int vmInit = 0;
            int LocalLine = 0;

            int isFIFA = 0;
            String PrimaryPricePlan = "";
            String CallControl = "";
            int voicemail = 0;
            ArrayList<String> outputList = ServiceAFweb.prettyPrintJSON(outputSt);

            for (int j = 0; j < outputList.size(); j++) {
                String inLine = outputList.get(outputList.size() - 1 - j);
//            logger.info("" + inLine);
                //"name":"isFIFA",
                if (inLine.indexOf("isFIFA") != -1) {
                    if (fifaInit == 1) {
                        continue;
                    }
                    fifaInit = 1;
                    String valueSt = outputList.get(outputList.size() - 1 - j + 1);
                    if (valueSt.indexOf("false") != -1) {
                        isFIFA = 0;
                    }
                    if (valueSt.indexOf("true") != -1) {
                        isFIFA = 1;
                    }
                    continue;
                }

                if (inLine.indexOf("hasVoicemail") != -1) {
                    if (vmInit == 1) {
                        continue;
                    }
                    vmInit = 1;
                    String valueSt = outputList.get(outputList.size() - 1 - j + 1);
                    if (valueSt.indexOf("false") != -1) {
                        voicemail = 0;
                    }
                    if (valueSt.indexOf("true") != -1) {
                        voicemail = 1;
                    }
                    continue;
                }

                if (inLine.indexOf("CallControl") != -1) {

                    if (quotaAmtInit == 1) {
                        continue;
                    }
                    quotaAmtInit = 1;
                    String valueSt = checkProductNm(j, outputList);
                    if (valueSt.length() != 0) {
                        CallControl = valueSt;
                    }
                    continue;
                }

                if (inLine.indexOf("LocalLine") != -1) {
                    if (planInit == 1) {
                        continue;
                    }
                    LocalLine = 1;
                    continue;
                }

                if (inLine.indexOf("HomePhoneBundle") != -1) {
                    if (planInit == 1) {
                        continue;
                    }
                    planInit = 1;
                    String valueSt = checkProductOfferingProductNm(j, outputList);
                    if (valueSt.length() != 0) {
                        PrimaryPricePlan = valueSt;
                    }
                    continue;
                }
            }
            String featTTV = APP_FEATT_TYPE_SING;
            featTTV += ":" + oper;
            String fifa = "fifa";
            if (isFIFA == 0) {
                fifa = "comp";
            }
            featTTV += ":" + fifa;
            String vm = "voicemail";
            if (voicemail == 0) {
                vm = "noVoliceMail";
            }
            featTTV += ":" + vm;
            String plan = PrimaryPricePlan;
            if (plan.length() == 0) {
                plan = "noPlan";
                if (LocalLine == 1) {
                    plan = "LocalLine";
                }
            }
            featTTV += ":" + plan;
            String callC = CallControl;
            if (callC.length() == 0) {
                callC = "noCallControl";
            }
            featTTV += ":" + callC;

            return featTTV;
        } catch (Exception ex) {

        }
        return "";
    }

    public static String checkProductRelationshipProductNm(int j, ArrayList<String> outputList) {
        for (int k = j; k <= outputList.size(); k++) {
            String inL = outputList.get(outputList.size() - 1 - k);
            if (inL.indexOf("productRelationship") != -1) {
                for (int m = k; m <= outputList.size(); m++) {
                    String inLL = outputList.get(outputList.size() - 1 - m);
                    if (inLL.indexOf("productNm") != -1) {
                        String valueSt = outputList.get(outputList.size() - 1 - m + 1);
                        valueSt = ServiceAFweb.replaceAll("\"", "", valueSt);
                        valueSt = ServiceAFweb.replaceAll("value:", "", valueSt);
                        valueSt = ServiceAFweb.replaceAll(" ", "_", valueSt);
                        return valueSt;
                    }
                }
            }
        }
        return "";
    }

    public static String checkProductNm(int j, ArrayList<String> outputList) {
        for (int k = j; k <= outputList.size(); k++) {
            String inL = outputList.get(outputList.size() - 1 - k);
            if (inL.indexOf("productNm") != -1) {
                String valueSt = outputList.get(outputList.size() - 1 - k + 1);
                valueSt = ServiceAFweb.replaceAll("\"", "", valueSt);
                valueSt = ServiceAFweb.replaceAll("value:", "", valueSt);
                valueSt = ServiceAFweb.replaceAll(" ", "_", valueSt);
                return valueSt;
            }
        }
        return "";
    }

    public static String checkProductOfferingProductNm(int j, ArrayList<String> outputList) {
        for (int k = j; k <= outputList.size(); k++) {
            String inL = outputList.get(outputList.size() - 1 - k);
            if (inL.indexOf("productOffering") != -1) {
                for (int m = k; m <= outputList.size(); m++) {
                    String inLL = outputList.get(outputList.size() - 1 - m);
                    if (inLL.indexOf("productNm") != -1) {
                        String valueSt = outputList.get(outputList.size() - 1 - m + 1);
                        valueSt = ServiceAFweb.replaceAll("\"", "", valueSt);
                        valueSt = ServiceAFweb.replaceAll("value:", "", valueSt);
                        valueSt = ServiceAFweb.replaceAll(" ", "_", valueSt);
                        return valueSt;
                    }
                }
            }
        }
        return "";
    }

    public static String parseProductInternetFeature(String outputSt, String oper) {

        if (outputSt == null) {
            return "";
        }
        try {

            int quotaAmtInit = 0;
            int fifaInit = 0;
            int planInit = 0;
            int fifaFlag = 0;
            int isFIFA = 0;

            String SecurityBundle = "";
            String EmailFeatures = "";
            String UnlimitedUsage = "";
            String PrimaryPricePlan = "";

            ArrayList<String> outputList = ServiceAFweb.prettyPrintJSON(outputSt);

            for (int j = 0; j < outputList.size(); j++) {
                String inLine = outputList.get(j);
//                        logger.info("" + inLine);
                //"name":"isFIFA",
                if (inLine.indexOf("isFIFA") != -1) {
                    if (fifaInit == 1) {
                        continue;
                    }
                    fifaInit = 1;
                    String valueSt = outputList.get(j + 1);
                    if (valueSt.indexOf("false") != -1) {
                        isFIFA = 0;
                        fifaFlag = 0;
                    }
                    if (valueSt.indexOf("true") != -1) {
                        isFIFA = 1;
                        fifaFlag = 1;
                    }
                    break;
                }
            }

            for (int j = 0; j < outputList.size(); j++) {
                String inLine = outputList.get(outputList.size() - 1 - j);
//            logger.info("" + inLine);

                if (inLine.indexOf("isFIFA") != -1) {
                    if (fifaInit == 1) {
                        continue;
                    }
                    fifaInit = 1;
                    String valueSt = outputList.get(j - 1);
                    if (valueSt.indexOf("false") != -1) {
                        isFIFA = 0;
                    }
                    if (valueSt.indexOf("true") != -1) {
                        isFIFA = 1;
                    }
                    continue;
                }
                if ((inLine.indexOf("SecurityBundle") != -1)
                        || (inLine.indexOf("TELUSOnlineSec") != -1)) {

                    if (quotaAmtInit == 1) {
                        continue;
                    }
                    quotaAmtInit = 1;
                    boolean exit = false;

                    String valueSt = checkProductNm(j, outputList);
                    valueSt = valueSt.replaceAll("_", "");
                    SecurityBundle = valueSt;

                    continue;
                }

                if (inLine.indexOf("PrimaryPricePlan") != -1) {
                    if (planInit == 1) {
                        continue;
                    }

                    planInit = 1;
                    if (fifaFlag == 0) {
                        String valueSt = checkProductRelationshipProductNm(j, outputList);
                        PrimaryPricePlan = valueSt;

                    } else if (fifaFlag == 1) {
                        String valueSt = checkProductNm(j, outputList);
                        if (valueSt.length() != 0) {
                            PrimaryPricePlan = valueSt;
                        }
                    }
                    continue;
                }
                if (inLine.indexOf("EmailFeatures") != -1) {
                    EmailFeatures = "EmailFeatures";
                    continue;
                }
                if (inLine.indexOf("UnlimitedUsage") != -1) {
                    UnlimitedUsage = "UnlimitedUsage";
                    continue;
                }
            }
            String featTTV = APP_FEAT_TYPE_HSIC;
            featTTV += ":" + oper;
            String fifa = "fifa";
            if (isFIFA == 0) {
                fifa = "comp";
            }
            featTTV += ":" + fifa;
            featTTV += ":" + PrimaryPricePlan;
            String security = SecurityBundle;
            if (security.length() == 0) {
                security = "noSecurity";
            }
            featTTV += ":" + security;

            String mail = EmailFeatures;
            if (mail.length() == 0) {
                mail = "noEmail";
            } else {
                mail = "Email";
            }
            featTTV += ":" + mail;

            String unlimit = UnlimitedUsage;
            if (unlimit.length() == 0) {
                unlimit = "noUnlimitedU";
            } else {
                unlimit = "UnlimitedU";
            }
            featTTV += ":" + unlimit;

            return featTTV;
        } catch (Exception ex) {

        }
        return "";
    }

    public boolean updateSsnsProdiuctInventoryByProdId(String oper, String banid, String prodid, ProductData pData, SsnsData dataObj, SsnsAcc NAccObj) {
        try {

            String featTTV = "";
            String outputSt = null;

            outputSt = SendSsnsProdiuctInventory(ServiceAFweb.URL_PRODUCT, banid, prodid, oper, null);
            if (outputSt == null) {
                return false;
            }
            if (outputSt.indexOf("responseCode:400500") != -1) {
                return false;
            }

            if (oper.equals(APP_FEAT_TYPE_HSIC)) {
                featTTV = parseProductInternetFeature(outputSt, dataObj.getOper());

            } else if (oper.equals(APP_FEAT_TYPE_TTV)) {
                featTTV = parseProductTtvFeature(outputSt, dataObj.getOper());

            } else if (oper.equals(APP_FEATT_TYPE_SING)) {
                featTTV = parseProductPhoneFeature(outputSt, dataObj.getOper());

            }

//            logger.info("> updateSsnsProdiuctInventory feat " + featTTV);
/////////////TTV   
            ArrayList<String> flow = new ArrayList();
            int faulure = getSsnsFlowTrace(dataObj, flow);
            if (flow == null) {
                logger.info("> updateSsnsProdiuctInventory skip no flow");
                return false;
            }

            pData.setFlow(flow);
            pData.setFlow(flow);
            if (faulure == 1) {
                featTTV += ":failed";
            }
            logger.info("> updateSsnsProdiuctInventory feat " + featTTV);
            NAccObj.setName(featTTV);
            NAccObj.setBanid(banid);
            NAccObj.setCusid(dataObj.getCusid());
            NAccObj.setUid(dataObj.getUid());
            NAccObj.setApp(dataObj.getApp());
            NAccObj.setTiid(dataObj.getTiid());
            NAccObj.setOper(dataObj.getOper());

            NAccObj.setDown(NAccObj.getDown());
            NAccObj.setRet(NAccObj.getRet());

            NAccObj.setExec(dataObj.getExec());

            String nameSt = new ObjectMapper().writeValueAsString(pData);
            NAccObj.setData(nameSt);

            NAccObj.setUpdatedatel(dataObj.getUpdatedatel());
            NAccObj.setUpdatedatedisplay(new java.sql.Date(dataObj.getUpdatedatel()));

            return true;
        } catch (Exception ex) {
            logger.info("> updateSsnsProdiuctInventory Exception " + ex.getMessage());
        }
        return false;
    }

    public boolean updateSsnsProdiuctInventory(String oper, String banid, String prodid, ProductData pData, SsnsData dataObj, SsnsAcc NAccObj) {
        try {

            String outputSt = null;

            outputSt = SendSsnsProdiuctInventory(ServiceAFweb.URL_PRODUCT, banid, prodid, oper, null);
            if (outputSt == null) {
                return false;
            }
            if (outputSt.indexOf("responseCode:400500") != -1) {
                return false;
            }
            String feat = "";
            if (oper.equals(SsnsService.APP_FEAT_TYPE_HSIC)) {
                feat = parseProductInternetFeature(outputSt, dataObj.getOper());

            } else if (oper.equals(SsnsService.APP_FEAT_TYPE_TTV)) {
                feat = parseProductTtvFeature(outputSt, dataObj.getOper());

            } else if (oper.equals(SsnsService.APP_FEATT_TYPE_SING)) {
                feat = parseProductPhoneFeature(outputSt, dataObj.getOper());
            }

            if (feat == null) {
                return false;
            }

//            logger.info("> updateSsnsProdiuctInventory feat " + featTTV);
/////////////TTV  
            ArrayList<String> flow = new ArrayList();
            int faulure = getSsnsFlowTrace(dataObj, flow);
            if (flow == null) {
                logger.info("> updateSsnsProdiuctInventory skip no flow");
                return false;
            }

            pData.setFlow(flow);

            if (faulure == 1) {
                feat += ":failed";
            }
            logger.info("> updateSsnsProdiuctInventory feat " + feat);
            NAccObj.setName(feat);
            NAccObj.setBanid(banid);
            NAccObj.setCusid(dataObj.getCusid());
            NAccObj.setUid(dataObj.getUid());
            NAccObj.setApp(dataObj.getApp());
            NAccObj.setTiid(dataObj.getTiid());
            NAccObj.setOper(dataObj.getOper());

            NAccObj.setDown(NAccObj.getDown());
            NAccObj.setRet(NAccObj.getRet());

            NAccObj.setExec(dataObj.getExec());

            String nameSt = new ObjectMapper().writeValueAsString(pData);
            NAccObj.setData(nameSt);

            NAccObj.setUpdatedatel(dataObj.getUpdatedatel());
            NAccObj.setUpdatedatedisplay(new java.sql.Date(dataObj.getUpdatedatel()));

            return true;
        } catch (Exception ex) {
            logger.info("> updateSsnsProdiuctInventory Exception " + ex.getMessage());
        }
        return false;
    }

    public static String parseProductTtvFeature(String outputSt, String oper) {

        if (outputSt == null) {
            return "";
        }

        int productCdInit = 0;
        int ChannelListInit = 0;
        int offerInit = 0;
        int fifaInit = 0;
        int regionInit = 0;

        int isFIFA = 0;
        String offer = "noOfferCd";
        String productCd = "Essentials";
        int ChannelList = 0;
        String region = "";

        ArrayList<String> outputList = ServiceAFweb.prettyPrintJSON(outputSt);
        for (int j = 0; j < outputList.size(); j++) {
            String inLine = outputList.get(j);
//                        logger.info("" + inLine);
            //"name":"isFIFA",
            if (inLine.indexOf("isFIFA") != -1) {
                if (fifaInit == 1) {
                    continue;
                }
                fifaInit = 1;
                String valueSt = outputList.get(j + 1);
                if (valueSt.indexOf("false") != -1) {
                    isFIFA = 0;
                }
                if (valueSt.indexOf("true") != -1) {
                    isFIFA = 1;
                }
                continue;
            }
            if (inLine.indexOf("offer") != -1) {
                if (offerInit == 1) {
                    continue;
                }
                offerInit = 1;
                String valueSt = outputList.get(j + 1);
                if (valueSt.indexOf("MediaroomTV-HS2.0") != -1) {
                    offer = "Mediaroom20";
                    continue;
                }
                if (valueSt.indexOf("MediaroomTV-HS") != -1) {
                    offer = "Mediaroom";
                    continue;
                }
                if (valueSt.indexOf("TVX") != -1) {
                    offer = "TVX";
                }
                continue;
            }

            if (inLine.indexOf("region") != -1) {
                if (regionInit == 1) {
                    continue;
                }
                regionInit = 1;
                String valueSt = outputList.get(j + 1);
                valueSt = ServiceAFweb.replaceAll("\"", "", valueSt);
                valueSt = ServiceAFweb.replaceAll("value:", "", valueSt);
                region = valueSt;
                continue;
            }
            if (inLine.indexOf("productCd") != -1) {
                if (productCdInit == 1) {
                    continue;
                }
                productCdInit = 1;
                String valueSt = outputList.get(j + 1);
                valueSt = ServiceAFweb.replaceAll("\"", "", valueSt);
                valueSt = ServiceAFweb.replaceAll("value:", "", valueSt);
                productCd = valueSt;
                continue;
            }
            if (inLine.indexOf("ChannelList") != -1) {
                if (ChannelListInit == 1) {
                    continue;
                }
                ChannelListInit = 1;
                String valueSt = outputList.get(j + 3);
                if (valueSt.indexOf("channelId") != -1) {
                    ChannelList = (1);
                } else {
                    ChannelList = (0);
                }
                continue;
            }
        }

        String featTTV = APP_FEAT_TYPE_TTV;
        featTTV += ":" + oper;
        String fifa = "fifa";
        if (isFIFA == 0) {
            fifa = "comp";
        }
        featTTV += ":" + fifa;
//        featTTV += ":" + region;
        featTTV += ":" + offer;
        featTTV += ":" + productCd;

        String chann = "ChListfailed";
        if (ChannelList == 1) {
            chann = "ChannelList";
        }
        featTTV += ":" + chann;

        if (region.length() == 0) {
            featTTV += ":noRegion";
        }
        return featTTV;
    }

    public String SendSsnsProdiuctInventoryByProdId(String ProductURL, String ban, String prodid, ArrayList<String> inList) {
        String url = ProductURL + "/v1/cmo/selfmgmt/productinventory/product/" + prodid + "?billingAccount.id=" + ban;
        try {
            if (inList != null) {
                inList.add(url);
            }
            // calculate elapsed time in milli seconds
            long startTime = TimeConvertion.currentTimeMillis();

            String output = this.sendRequest_Ssns(METHOD_GET, url, null, null);

            long endTime = TimeConvertion.currentTimeMillis();
            long elapsedTime = endTime - startTime;
//            System.out.println("Elapsed time in milli seconds: " + elapsedTime);
            if (inList != null) {
                inList.add("elapsedTime:" + elapsedTime);
                inList.add("output:");
            }
            return output;
        } catch (Exception ex) {
            logger.info("> SsnsProdiuctInventory exception " + ex.getMessage());
        }
        return null;
    }

    public String SendSsnsProdiuctInventory(String ProductURL, String ban, String prodid, String productType, ArrayList<String> inList) {
        String url = "";
        if (prodid.length() == 0) {
            url = ProductURL + "/v1/cmo/selfmgmt/productinventory/product?billingAccount.id=" + ban
                    + "&productType=" + productType;
            if (productType.equals(APP_FEAT_TYPE_TTV)) {
                url += "&fields=product.characteristic.channelInfoList";
            } else if (productType.equals(APP_FEATT_TYPE_SING)) {
                url += "&fields=product.characteristic.voicemail";
            }
        } else {
            url = ProductURL + "/v1/cmo/selfmgmt/productinventory/product/" + prodid + "?billingAccount.id=" + ban;
            if (productType.equals(APP_FEAT_TYPE_TTV)) {
                url += "&fields=product.characteristic.channelInfoList";
            } else if (productType.equals(APP_FEATT_TYPE_SING)) {
                url += "&fields=product.characteristic.voicemail";
            }
        }

        try {
            if (inList != null) {
                inList.add(url);
            }
            // calculate elapsed time in milli seconds
            long startTime = TimeConvertion.currentTimeMillis();

            String output = this.sendRequest_Ssns(METHOD_GET, url, null, null);

            long endTime = TimeConvertion.currentTimeMillis();
            long elapsedTime = endTime - startTime;
//            System.out.println("Elapsed time in milli seconds: " + elapsedTime);
            if (inList != null) {
                inList.add("elapsedTime:" + elapsedTime);
                inList.add("output:");
            }

            return output;
        } catch (Exception ex) {
            logger.info("> SsnsProdiuctInventory exception " + ex.getMessage());
        }
        return null;
    }

    // 1 faulure, 0 = success
    public int getSsnsFlowTrace(SsnsData dataObj, ArrayList<String> flow) {

        String uid = dataObj.getUid();
        int failure = 0;

        ArrayList<SsnsData> ssnsList = getSsnsDataImp().getSsnsDataObjListByUid(dataObj.getApp(), uid);
        if (ssnsList != null) {
//            logger.info("> ssnsList " + ssnsList.size());
            for (int i = 0; i < ssnsList.size(); i++) {
                SsnsData data = ssnsList.get(i);
                String flowSt = data.getDown();
                if (flowSt.length() == 0) {
                    flowSt = data.getOper();
                }
                flowSt += ":" + data.getExec();
                String dataTxt = data.getData();
                if (dataTxt.indexOf("stacktrace") != -1) {
                    failure = 1;
                } else {
                    dataTxt = data.getRet();
                    if (dataTxt.indexOf("httpCd=500") != -1) {
                        failure = 1;
                    }
                }
//                logger.info("> flow " + flowSt);
                if (failure == 1) {
                    flowSt += ":failed:" + data.getData();
                }
                flow.add(flowSt);
            }
        }
        return failure;
    }

    /////////////////////////////////////////////////////////////
    // operations names constants
    private static final String METHOD_POST = "post";
    private static final String METHOD_GET = "get";

    private String sendRequest_Ssns(String method, String subResourcePath, Map<String, String> queryParams,
            Map<String, String> bodyParams) throws Exception {
        String response = null;
        for (int i = 0; i < 4; i++) {

            try {

                response = sendRequest_Process_Ssns(method, subResourcePath, queryParams, bodyParams);
                if (response != null) {
                    return response;
                }
                ServiceAFweb.AFSleep1Sec(i);
            } catch (Exception ex) {
                logger.info("sendRequest " + method + " Rety " + (i + 1));
            }
        }

        response = sendRequest_Process_Ssns(method, subResourcePath, queryParams, bodyParams);
        return response;
    }

    private String sendRequest_Process_Ssns(String method, String subResourcePath, Map<String, String> queryParams, Map<String, String> bodyParams) {

        try {
//            if (CKey.SQL_Devop == true) {
//                if (subResourcePath.indexOf("DEVOP") != -1) {
//                    // send to devop client
//
//                    return sendRequest_Process_Devop(method, subResourcePath, queryParams, bodyParams);
//                }
//            }
            if (subResourcePath.indexOf("https") != -1) {
                return this.https_sendRequest_Process_Ssns(method, subResourcePath, queryParams, bodyParams);
            }
            return this.http_sendRequest_Process_Ssns(method, subResourcePath, queryParams, bodyParams);
        } catch (Exception ex) {
//            Logger.getLogger(SsnsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

//    private String sendRequest_Process_Devop(String method, String subResourcePath, Map<String, String> queryParams, Map<String, String> bodyParams)
//            throws Exception {
//        try {
//
//            String URLPath = subResourcePath;
//
//            String webResourceString = "";
//            // assume only one param
//            if (queryParams != null && !queryParams.isEmpty()) {
//                for (String key : queryParams.keySet()) {
//                    webResourceString = "?" + key + "=" + queryParams.get(key);
//                }
//            }
//
//            String bodyElement = "";
//            if (bodyParams != null) {
//                bodyElement = new ObjectMapper().writeValueAsString(bodyParams);
//            }
//            URLPath += webResourceString;
//
//            ServiceAFwebREST remoteREST = new ServiceAFwebREST();
//            RequestObj sqlObj = new RequestObj();
//            String cmd = "99";
//            sqlObj.setCmd(cmd);
//            sqlObj.setReq(method);
//            sqlObj.setReq1(URLPath);
//            sqlObj.setReq2(bodyElement);
//            String resp = remoteREST.getSQLRequestRemote(sqlObj);
//            return resp;
//
//        } catch (Exception e) {
////            logger.info("Error sending REST request:" + e);
//            throw e;
//        }
//    }
    private String https_sendRequest_Process_Ssns(String method, String subResourcePath, Map<String, String> queryParams, Map<String, String> bodyParams)
            throws Exception {
        try {

            String URLPath = subResourcePath;

            String webResourceString = "";
            // assume only one param
            if (queryParams != null && !queryParams.isEmpty()) {
                for (String key : queryParams.keySet()) {
                    webResourceString = "?" + key + "=" + queryParams.get(key);
                }
            }

            String bodyElement = "";
            if (bodyParams != null) {
                bodyElement = new ObjectMapper().writeValueAsString(bodyParams);
            }

            URLPath += webResourceString;
            URL request = new URL(URLPath);

            HttpsURLConnection con = null; //(HttpURLConnection) request.openConnection();

//            if (CKey.PROXY == true) {
//                //////Add Proxy 
//                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ServiceAFweb.PROXYURL, 8080));
//                con = (HttpsURLConnection) request.openConnection(proxy);
//                //////Add Proxy 
//            } else {
            con = (HttpsURLConnection) request.openConnection(Proxy.NO_PROXY);
//            }

//            if (URLPath.indexOf(":8080") == -1) {
            String authStr = "APP_SELFSERVEUSGBIZSVC" + ":" + "soaorgid";
            // encode data on your side using BASE64
            byte[] bytesEncoded = Base64.encodeBase64(authStr.getBytes());
            String authEncoded = new String(bytesEncoded);
            con.setRequestProperty("Authorization", "Basic " + authEncoded);
//            }

            if (method.equals(METHOD_POST)) {
                con.setRequestMethod("POST");
            } else if (method.equals(METHOD_GET)) {
                con.setRequestMethod("GET");
            }
            con.setRequestProperty("User-Agent", USER_AGENT);
//            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");

            if (method.equals(METHOD_POST)) {

//                con.setRequestMethod("POST");
//                con.addRequestProperty("Accept", "application/json");
//                con.addRequestProperty("Connection", "close");
//                con.addRequestProperty("Content-Encoding", "gzip"); // We gzip our request
//                con.addRequestProperty("Content-Length", String.valueOf(bodyElement.length()));
//                con.setRequestProperty("Content-Type", "application/json"); // We send our data in JSON format
                con.setDoInput(true);
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

//                if ((responseCode == 400) || (responseCode == 500)) {
                InputStream inputstream = null;
                inputstream = con.getErrorStream();

                StringBuffer response = new StringBuffer();
                response.append(URLPath);
                BufferedReader in = new BufferedReader(new InputStreamReader(inputstream));
                String line;
                response.append("responseCode:400500");
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                System.out.println(response.toString());
                return response.toString();
//                }
            }
            if (responseCode >= 200 && responseCode < 300) {
                ;
            } else {
                System.out.println("Response Code:: " + responseCode);
//                System.out.println("bodyElement :: " + bodyElement);
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
                logger.info("POST request not worked");
            }

        } catch (Exception e) {
            logger.info("Error sending REST request:" + e);
            throw e;
        }
        return null;
    }

    private String http_sendRequest_Process_Ssns(String method, String subResourcePath, Map<String, String> queryParams, Map<String, String> bodyParams)
            throws Exception {
        try {

            String URLPath = subResourcePath;

            String webResourceString = "";
            // assume only one param
            if (queryParams != null && !queryParams.isEmpty()) {
                for (String key : queryParams.keySet()) {
                    webResourceString = "?" + key + "=" + queryParams.get(key);
                }
            }

            String bodyElement = "";
            if (bodyParams != null) {
                bodyElement = new ObjectMapper().writeValueAsString(bodyParams);
            }

            URLPath += webResourceString;
            URL request = new URL(URLPath);

            HttpURLConnection con = null; //(HttpURLConnection) request.openConnection();

//            if (CKey.PROXY == true) {
//                //////Add Proxy 
//                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ServiceAFweb.PROXYURL, 8080));
//                con = (HttpURLConnection) request.openConnection(proxy);
//                //////Add Proxy 
//            } else {
            con = (HttpURLConnection) request.openConnection(Proxy.NO_PROXY);
//            }

//            if (URLPath.indexOf(":8080") == -1) {
            String authStr = "APP_SELFSERVEUSGBIZSVC" + ":" + "soaorgid";
            // encode data on your side using BASE64
            byte[] bytesEncoded = Base64.encodeBase64(authStr.getBytes());
            String authEncoded = new String(bytesEncoded);
            con.setRequestProperty("Authorization", "Basic " + authEncoded);
//            }

            if (method.equals(METHOD_POST)) {
                con.setRequestMethod("POST");
            } else if (method.equals(METHOD_GET)) {
                con.setRequestMethod("GET");
            }
            con.setRequestProperty("User-Agent", USER_AGENT);
//            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
//            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");

            if (method.equals(METHOD_POST)) {
                con.setDoOutput(true);
                try (OutputStream os = con.getOutputStream()) {
                    byte[] input = bodyElement.getBytes("utf-8");
                    os.write(input, 0, input.length);
                    os.flush();
                    os.close();
                }

            }

            int responseCode = con.getResponseCode();
            if (responseCode != 200) {
//                System.out.println("Response Code:: " + responseCode);
//                if ((responseCode == 400) || (responseCode == 500)) {
                InputStream inputstream = null;
                inputstream = con.getErrorStream();

                StringBuffer response = new StringBuffer();
                response.append(URLPath);
                BufferedReader in = new BufferedReader(new InputStreamReader(inputstream));
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                System.out.println(response.toString());
                return response.toString();
//                }
            }
            if (responseCode >= 200 && responseCode < 300) {
                ;
            } else {
//                System.out.println("Response Code:: " + responseCode);
//                System.out.println("bodyElement :: " + bodyElement);
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
                logger.info("POST request not worked");
            }

        } catch (Exception e) {
//            logger.info("Error sending REST request:" + e);
            throw e;
        }
        return null;
    }

    ////////
    public static String[] splitIncludeEmpty(String inputStr, char delimiter) {
        if (inputStr == null) {
            return null;
        }
        if (inputStr.charAt(inputStr.length() - 1) == delimiter) {

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

//https://self-learning-java-tutorial.blogspot.com/2018/03/pretty-print-xml-string-in-java.html
//    public static String getPrettyXMLString(String xmlData, int indent) throws Exception {
//        TransformerFactory transformerFactory = TransformerFactory.newInstance();
//        transformerFactory.setAttribute("indent-number", indent);
//
//        Transformer transformer = transformerFactory.newTransformer();
//        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//
//        StringWriter stringWriter = new StringWriter();
//        StreamResult xmlOutput = new StreamResult(stringWriter);
//
//        Source xmlInput = new StreamSource(new StringReader(xmlData));
//        transformer.transform(xmlInput, xmlOutput);
//
//        return xmlOutput.getWriter().toString();
//    }
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

}
