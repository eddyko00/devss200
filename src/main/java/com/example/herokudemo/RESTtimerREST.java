/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.herokudemo;

import com.afweb.service.ServiceAFweb;
import com.afweb.util.CKey;
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
 * @author eddy
 */
public class RESTtimerREST {

    public static Logger log = Logger.getLogger("ServiceAFwebREST");
    // operations names constants
    public static final String METHOD_POST = "post";
    public static final String METHOD_GET = "get";

////////////////////////////////////////
    public String sendRequest(String method, String URLPath, Map<String, String> queryParams, String bodyElement, boolean proxyFlag)
            throws Exception {
        try {
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

            if (proxyFlag == true) {
                //////Add Proxy 
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ServiceAFweb.PROXYURL, 8080));
                con = (HttpURLConnection) request.openConnection(proxy);
                //////Add Proxy 
            } else {
                con = (HttpURLConnection) request.openConnection();
            }
            con.setConnectTimeout(1500);
            con.setReadTimeout(1500);
            if (method.equals(METHOD_POST)) {
                con.setRequestMethod("POST");
            } else {
                con.setRequestMethod("GET");
            }
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Content-Type", "application/json; utf-8");

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
//                log.info("POST request not worked");
            }

        } catch (Exception e) {
            throw e;
        }
        return null;
    }

}
