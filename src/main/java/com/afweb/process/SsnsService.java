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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

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
    public static Set<String> set = new HashSet<>();

    public static String APP_WIFI = "wifi";
    public static String APP_APP = "app";
    public static String APP_PRODUCT = "prod";
    public static String APP_TTVC = "ttv";
    public static String APP_WLNPRO = "wlnpro";

    public static String APP_TTVSUB = "ttvsub";  // ETL name
    public static String APP_TTVREQ = "ttvreq";  // ETL name
    //
    public static String APP_GET_DOWNURL = "downloadurl";
    public static String APP_FEAT_TYPE_WLNP = "WLNP";

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

    public static String parseWLNProFeature(String outputSt, String oper, String bundleName) {

        if (outputSt == null) {
            return "";
        }
        String serviceTypeCd = "";
        String skuTxt = "";
        String licenceCount = "";

        ArrayList<String> outputList = ServiceAFweb.prettyPrintJSON(outputSt);
        for (int j = 0; j < outputList.size(); j++) {
            String inLine = outputList.get(j);
//            logger.info("" + inLine);
            if (inLine.indexOf("serviceTypeCd") != -1) {
                String valueSt = inLine;
                valueSt = ServiceAFweb.replaceAll("\"", "", valueSt);
                valueSt = ServiceAFweb.replaceAll("serviceTypeCd:", "", valueSt);
                valueSt = ServiceAFweb.replaceAll(",", "", valueSt);
                serviceTypeCd = valueSt;
                continue;
            }
            if (inLine.indexOf("skuTxt") != -1) {
                String valueSt = inLine;
                valueSt = ServiceAFweb.replaceAll("\"", "", valueSt);
                valueSt = ServiceAFweb.replaceAll("skuTxt:", "", valueSt);
                skuTxt = ServiceAFweb.replaceAll(",", "", valueSt);
                continue;
            }

            if (inLine.indexOf("licenceCount") != -1) {
                String valueSt = inLine;
                valueSt = ServiceAFweb.replaceAll("\"", "", valueSt);
                valueSt = ServiceAFweb.replaceAll("licenceCount:", "", valueSt);
                valueSt = ServiceAFweb.replaceAll(",", "", valueSt);
                licenceCount = valueSt;
            }
        }

        String featTTV = APP_FEAT_TYPE_WLNP;
        featTTV += ":" + oper;
        featTTV += ":" + bundleName;
        featTTV += ":" + serviceTypeCd + ":" + skuTxt + ":licenceCount_" + licenceCount;
        return featTTV;
    }

    public String SendSsnsWLNPro(String ProductURL, String oper, String custid, String serviceType, String skuP, ArrayList<String> inList) {
        String url = "";
        try {
            url = ProductURL + "/v2/cmo/selfmgmt/wirelineprotectionsubscriptionservice/customer/" + custid
                    + "/protection/email/download-urls";

            String postParm = "{"
                    + "  \"customerId\": \"xxcustid\","
                    + "  \"languageCd\": \"EN\","
                    + "  \"emailAddressList\": [],"
                    + "  \"auditInfo\": {"
                    + "    \"originatorApplicationId\": \"APP_SELFSERVEUSGBIZSVC\""
                    + "  },"
                    + "  \"componentDetailList\": ["
                    + "    {"
                    + "      \"serviceTypeCd\": \"xxserv\","
                    + "      \"skuTxt\": \"xxsku\""
                    + "    }"
                    + "  ]"
                    + "}";
            postParm = postParm.replace("xxcustid", custid);
            postParm = postParm.replace("xxserv", serviceType);
            postParm = postParm.replace("xxsku", skuP);

//            HashMap newbodymap = new ObjectMapper().readValue(postParm, HashMap.class);
            if (inList != null) {
                inList.add(url);
            }
            // calculate elapsed time in milli seconds
            long startTime = TimeConvertion.currentTimeMillis();

            String output = this.sendRequest_Ssns(METHOD_POST, url, null, null, postParm);

            long endTime = TimeConvertion.currentTimeMillis();
            long elapsedTime = endTime - startTime;
//            System.out.println("Elapsed time in milli seconds: " + elapsedTime);
            if (inList != null) {
                String tzid = "America/New_York"; //EDT
                TimeZone tz = TimeZone.getTimeZone(tzid);
                Date d = new Date(startTime);
                // timezone symbol (z) included in the format pattern 
                DateFormat format = new SimpleDateFormat("M/dd/yyyy hh:mm a z");
                // format date in target timezone
                format.setTimeZone(tz);
                String ESTdate = format.format(d);

                inList.add(ESTdate + " elapsedTime:" + elapsedTime);
                inList.add("output:");
            }
            return output;

        } catch (Exception ex) {
            logger.info("> SendSsnsWLNPro exception " + ex.getMessage());
        }
        return null;
    }

    public String TestFeatureSsnsProdWLNPro(SsnsAcc dataObj, ArrayList<String> outputList, String oper, String LABURL) {
        if (dataObj == null) {
            return "";
        }
        dataObj.getData();
        String custid = dataObj.getCusid();
        String appTId = dataObj.getTiid();
        if (appTId.length() == 0) {
            return "";
        }
        String WifiparL[] = appTId.split(":");
        String serviceType = WifiparL[1];
        String skuP = WifiparL[2];

        if (LABURL.length() == 0) {
            LABURL = ServiceAFweb.URL_PRODUCT_PR;
        }

        String outputSt = null;
        ArrayList<String> inList = new ArrayList();
        if (oper.equals(APP_GET_DOWNURL)) {
            outputSt = SendSsnsWLNPro(ServiceAFweb.URL_PRODUCT_PR, APP_GET_DOWNURL, custid, serviceType, skuP, inList);

            if (outputSt == null) {
                return "";
            }
            ////special char #, need to ignore for this system
            outputSt = outputSt.replaceAll("#", "");
            outputSt = outputSt.replaceAll("~", "");
            outputSt = outputSt.replaceAll("^", "");

            ArrayList<String> outList = ServiceAFweb.prettyPrintJSON(outputSt);
            String feat = parseWLNProFeature(outputSt, oper, null);

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

                String output = this.sendRequest_Ssns(METHOD_GET, url, null, null, null);

                long endTime = TimeConvertion.currentTimeMillis();
                long elapsedTime = endTime - startTime;
//            System.out.println("Elapsed time in milli seconds: " + elapsedTime);
                if (inList != null) {
                    String tzid = "America/New_York"; //EDT
                    TimeZone tz = TimeZone.getTimeZone(tzid);
                    Date d = new Date(startTime);
                    // timezone symbol (z) included in the format pattern 
                    DateFormat format = new SimpleDateFormat("M/dd/yyyy hh:mm a z");
                    // format date in target timezone
                    format.setTimeZone(tz);
                    String ESTdate = format.format(d);

                    inList.add(ESTdate + " elapsedTime:" + elapsedTime);
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

                String output = this.sendRequest_Ssns(METHOD_POST, url, null, map, null);

                long endTime = TimeConvertion.currentTimeMillis();
                long elapsedTime = endTime - startTime;
//            System.out.println("Elapsed time in milli seconds: " + elapsedTime);
                if (inList != null) {
                    String tzid = "America/New_York"; //EDT
                    TimeZone tz = TimeZone.getTimeZone(tzid);
                    Date d = new Date(startTime);
                    // timezone symbol (z) included in the format pattern 
                    DateFormat format = new SimpleDateFormat("M/dd/yyyy hh:mm a z");
                    // format date in target timezone
                    format.setTimeZone(tz);
                    String ESTdate = format.format(d);

                    inList.add(ESTdate + " elapsedTime:" + elapsedTime);

                    String bodyElement = new ObjectMapper().writeValueAsString(map);
                    inList.add("bodyElement:" + bodyElement);
                    inList.add("output:");

                }
                return output;

            } else if (oper.equals(TT_Vadulate)) {
                url = ProductURL + "/v1/cmo/selfmgmt/tv/requisition/account/" + banid
                        + "/productinstance/" + prodid
                        + "/validation";

                if (inList != null) {
                    inList.add(url);
                }
                // calculate elapsed time in milli seconds
                long startTime = TimeConvertion.currentTimeMillis();
                String st = ServiceAFweb.replaceAll("\":\",", "\":\"\",", postParm);
                st = st.substring(0, st.length() - 2);

                Map<String, String> map = new ObjectMapper().readValue(st, Map.class);
                map.remove("customerEmail");

                String output = this.sendRequest_Ssns(METHOD_POST, url, null, map, null);

                long endTime = TimeConvertion.currentTimeMillis();
                long elapsedTime = endTime - startTime;
//            System.out.println("Elapsed time in milli seconds: " + elapsedTime);
                if (inList != null) {
                    String tzid = "America/New_York"; //EDT
                    TimeZone tz = TimeZone.getTimeZone(tzid);
                    Date d = new Date(startTime);
                    // timezone symbol (z) included in the format pattern 
                    DateFormat format = new SimpleDateFormat("M/dd/yyyy hh:mm a z");
                    // format date in target timezone
                    format.setTimeZone(tz);
                    String ESTdate = format.format(d);

                    inList.add(ESTdate + " elapsedTime:" + elapsedTime);

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
            LABURL = ServiceAFweb.URL_PRODUCT_PR;
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
            outList = ServiceAFweb.prettyPrintJSON(outputSt);
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

            String output = this.sendRequest_Ssns(METHOD_GET, url, null, null, null);

            long endTime = TimeConvertion.currentTimeMillis();
            long elapsedTime = endTime - startTime;
//            System.out.println("Elapsed time in milli seconds: " + elapsedTime);
            if (inList != null) {
                String tzid = "America/New_York"; //EDT
                TimeZone tz = TimeZone.getTimeZone(tzid);
                Date d = new Date(startTime);
                // timezone symbol (z) included in the format pattern 
                DateFormat format = new SimpleDateFormat("M/dd/yyyy hh:mm a z");
                // format date in target timezone
                format.setTimeZone(tz);
                String ESTdate = format.format(d);

                inList.add(ESTdate + " elapsedTime:" + elapsedTime);
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

            String output = this.sendRequest_Ssns(METHOD_GET, url, null, null, null);

            long endTime = TimeConvertion.currentTimeMillis();
            long elapsedTime = endTime - startTime;
//            System.out.println("Elapsed time in milli seconds: " + elapsedTime);
            if (inList != null) {
                String tzid = "America/New_York"; //EDT
                TimeZone tz = TimeZone.getTimeZone(tzid);
                Date d = new Date(startTime);
                // timezone symbol (z) included in the format pattern 
                DateFormat format = new SimpleDateFormat("M/dd/yyyy hh:mm a z");
                // format date in target timezone
                format.setTimeZone(tz);
                String ESTdate = format.format(d);

                inList.add(ESTdate + " elapsedTime:" + elapsedTime);
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
            LABURL = ServiceAFweb.URL_PRODUCT_PR;
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
            String output = this.sendRequest_Ssns(METHOD_POST, url, null, newbodymap, null);

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

            String output = this.sendRequest_Ssns(METHOD_POST, url, null, newbodymap, null);

            long endTime = TimeConvertion.currentTimeMillis();
            long elapsedTime = endTime - startTime;
//            System.out.println("Elapsed time in milli seconds: " + elapsedTime);
            if (inList != null) {
                String tzid = "America/New_York"; //EDT
                TimeZone tz = TimeZone.getTimeZone(tzid);
                Date d = new Date(startTime);
                // timezone symbol (z) included in the format pattern 
                DateFormat format = new SimpleDateFormat("M/dd/yyyy hh:mm a z");
                // format date in target timezone
                format.setTimeZone(tz);
                String ESTdate = format.format(d);

                inList.add(ESTdate + " elapsedTime:" + elapsedTime);
                String bodyElement = new ObjectMapper().writeValueAsString(newbodymap);
                inList.add("bodyElement:" + bodyElement);
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
            LABURL = ServiceAFweb.URL_PRODUCT_PR;
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

            String output = this.sendRequest_Ssns(METHOD_GET, url, null, null, null);

            long endTime = TimeConvertion.currentTimeMillis();
            long elapsedTime = endTime - startTime;
//            System.out.println("Elapsed time in milli seconds: " + elapsedTime);
            if (inList != null) {
                String tzid = "America/New_York"; //EDT
                TimeZone tz = TimeZone.getTimeZone(tzid);
                Date d = new Date(startTime);
                // timezone symbol (z) included in the format pattern 
                DateFormat format = new SimpleDateFormat("M/dd/yyyy hh:mm a z");
                // format date in target timezone
                format.setTimeZone(tz);
                String ESTdate = format.format(d);

                inList.add(ESTdate + " elapsedTime:" + elapsedTime);
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
            LABURL = ServiceAFweb.URL_PRODUCT_PR;
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

    public static String parseProductPhoneFeature(String outputSt, String oper) {
        if (outputSt == null) {
            return "";
        }
        try {

            int callCInit = 0;
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

                if (inLine.indexOf("CallControl") != -1) {

                    if (callCInit == 1) {
                        continue;
                    }
                    callCInit = 1;
                    String valueSt = checkProductNm(j, outputList);
                    if (valueSt.length() != 0) {
                        CallControl = valueSt;
                    }
                    continue;
                }
                if (inLine.indexOf("VoiceMail") != -1) {

                    if (vmInit == 1) {
                        continue;
                    }
                    vmInit = 1;
                    String valueSt = checkProductNm(j, outputList);
                    if (valueSt.length() != 0) {
                        voicemail = 1;
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
                    } else {
                        valueSt = checkProductNm(j, outputList);
                        if (valueSt.length() != 0) {
                            PrimaryPricePlan = valueSt;
                        }
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
        for (int k = j; k < outputList.size(); k++) {
            String inL = outputList.get(outputList.size() - 1 - k);
            if (inL.indexOf("productRelationship") != -1) {
                for (int m = k; m < outputList.size(); m++) {
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
        for (int k = j; k < outputList.size(); k++) {
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
        try {
            for (int k = j; k < outputList.size(); k++) {
                String inL = outputList.get(outputList.size() - 1 - k);
                if (inL.indexOf("productOffering") != -1) {
                    for (int m = k; m < outputList.size(); m++) {
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
        } catch (Exception ex) {
            logger.info("> checkProductOfferingProductNm " + ex.getMessage());
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

            String output = this.sendRequest_Ssns(METHOD_GET, url, null, null, null);

            long endTime = TimeConvertion.currentTimeMillis();
            long elapsedTime = endTime - startTime;
//            System.out.println("Elapsed time in milli seconds: " + elapsedTime);
            if (inList != null) {
                String tzid = "America/New_York"; //EDT
                TimeZone tz = TimeZone.getTimeZone(tzid);
                Date d = new Date(startTime);
                // timezone symbol (z) included in the format pattern 
                DateFormat format = new SimpleDateFormat("M/dd/yyyy hh:mm a z");
                // format date in target timezone
                format.setTimeZone(tz);
                String ESTdate = format.format(d);

                inList.add(ESTdate + " elapsedTime:" + elapsedTime);
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
//            } else if (productType.equals(APP_FEATT_TYPE_SING)) {
//                url += "&fields=product.characteristic.voicemail";
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

            String output = this.sendRequest_Ssns(METHOD_GET, url, null, null, null);

            long endTime = TimeConvertion.currentTimeMillis();
            long elapsedTime = endTime - startTime;
//            System.out.println("Elapsed time in milli seconds: " + elapsedTime);
            if (inList != null) {
                String tzid = "America/New_York"; //EDT
                TimeZone tz = TimeZone.getTimeZone(tzid);
                Date d = new Date(startTime);
                // timezone symbol (z) included in the format pattern 
                DateFormat format = new SimpleDateFormat("M/dd/yyyy hh:mm a z");
                // format date in target timezone
                format.setTimeZone(tz);
                String ESTdate = format.format(d);

                inList.add(ESTdate + " elapsedTime:" + elapsedTime);
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
                String down = data.getDown();
                if (down.length() == 0) {
                    continue;
                }
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
            Map<String, String> bodyParams, String postParmSt) throws Exception {
        String response = null;
        for (int i = 0; i < 4; i++) {

            try {

                response = sendRequest_Process_Ssns(method, subResourcePath, queryParams, bodyParams, postParmSt);
                if (response != null) {
                    return response;
                }
                ServiceAFweb.AFSleep1Sec(i);
            } catch (Exception ex) {
                logger.info("sendRequest " + method + " Rety " + (i + 1));
            }
        }

        response = sendRequest_Process_Ssns(method, subResourcePath, queryParams, bodyParams, postParmSt);
        return response;
    }

    private String sendRequest_Process_Ssns(String method, String subResourcePath, Map<String, String> queryParams, Map<String, String> bodyParams, String postParmSt) {

        try {
//            if (CKey.SQL_Devop == true) {
//                if (subResourcePath.indexOf("DEVOP") != -1) {
//                    // send to devop client
//
//                    return sendRequest_Process_Devop(method, subResourcePath, queryParams, bodyParams);
//                }
//            }
            if (subResourcePath.indexOf("https") != -1) {
                return this.https_sendRequest_Process_Ssns(method, subResourcePath, queryParams, bodyParams, postParmSt);
            }
            return this.http_sendRequest_Process_Ssns(method, subResourcePath, queryParams, bodyParams, postParmSt);
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
    private String https_sendRequest_Process_Ssns(String method, String subResourcePath, Map<String, String> queryParams,
            Map<String, String> bodyParams, String postParmSt)
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
            if (postParmSt != null) {
                bodyElement = postParmSt;
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

    private String http_sendRequest_Process_Ssns(String method, String subResourcePath, Map<String, String> queryParams,
            Map<String, String> bodyParams, String postParmSt)
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
            if (postParmSt != null) {
                bodyElement = postParmSt;
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
