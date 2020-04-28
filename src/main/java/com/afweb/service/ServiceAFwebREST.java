/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.afweb.service;

import com.afweb.model.RequestObj;
import com.afweb.util.CKey;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.Map;
import java.util.logging.Logger;
import static org.apache.http.protocol.HTTP.USER_AGENT;

/**
 *
 * @author koed
 */
public class ServiceAFwebREST {

    // operations names constants
    public static Logger logger = Logger.getLogger("ServiceAFwebREST");
    private static final String METHOD_POST = "post";
    private static final String METHOD_GET = "get";
    private static int sendNum = 0;

    public String getSQLRequestRemote(RequestObj sqlObj) {
        ServiceAFweb.getServerObj().setCntRESTrequest(ServiceAFweb.getServerObj().getCntRESTrequest() + 1);
        String subResourcePath = ServiceAFweb.SERVERDB_URL + "/cust/" + CKey.ADMIN_USERNAME + "/sys/mysql";
        if (sqlObj.getReq().length() < 3) {
            logger.info("getSQLRequest not correct num " + sendNum + " sql " + sqlObj.getReq());
            return "";
        }
        try {
            String sqlSt = new ObjectMapper().writeValueAsString(sqlObj);

            String output = sendRequest_2(METHOD_POST, subResourcePath, null, sqlSt);
            sendNum++;
            if ((sendNum % 200) == 0) {
                logger.info("getSQLRequest sendNum " + sendNum);
            }
            return output;
        } catch (Exception ex) {
            logger.info("getSQLRequest exception " + ex);
            ServiceAFweb.getServerObj().setCntRESTexception(ServiceAFweb.getServerObj().getCntRESTexception() + 1);
        }
        return null;
    }

    private String sendRequest_2(String method, String subResourcePath, Map<String, String> queryParams, String bodyParams) throws Exception {
        String response = null;
        for (int i = 0; i < 4; i++) {
            try {
                response = sendRequest_Process_2(method, subResourcePath, queryParams, bodyParams);
                if (response != null) {
                    return response;
                }
            } catch (Exception ex) {

            }

            System.out.println("sendRequest " + subResourcePath + " Rety " + (i + 1));
        }
        response = sendRequest_Process_2(method, subResourcePath, queryParams, bodyParams);
        return response;
    }

    private String sendRequest_Process_2(String method, String subResourcePath, Map<String, String> queryParams, String bodyElement)
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

            URLPath += webResourceString;
            URL request = new URL(URLPath);
            HttpURLConnection con = null; //(HttpURLConnection) request.openConnection();
//            System.out.println("Request Code:: " + URLPath);
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
            con.setRequestProperty("Content-Type", "text/xml");
//            con.setRequestProperty("Content-Type", "application/json; utf-8");

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
//            System.out.println("Response Code:: " + responseCode);

            if (responseCode >= 200 && responseCode < 300) {
                ;
            } else {
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
                logger.info("POST request not worked");
            }

        } catch (Exception e) {
            logger.info("Error sending REST request:" + e);
            throw e;
        }
        return null;
    }

}
