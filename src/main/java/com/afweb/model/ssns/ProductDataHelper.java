/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.afweb.model.ssns;

import com.afweb.service.ServiceAFweb;
import java.util.ArrayList;

/**
 *
 * @author koed
 */
public class ProductDataHelper {

    public static ArrayList<String> getDetailRespRestore(ArrayList<String> detailResp) {
        if (detailResp != null) {
            ArrayList<String> detailRespNew = new ArrayList();
            for (int i = 0; i < detailResp.size(); i++) {
                String flowSt = detailResp.get(i);
                flowSt = flowSt.replace('^', '"');
                detailRespNew.add(flowSt);
            }
            return detailRespNew;
        }
        return detailResp;
    }

    public static ArrayList<String> getCallbackRestore(ArrayList<String> callback) {
        if (callback != null) {
            ArrayList<String> callbackNew = new ArrayList();
            for (int i = 0; i < callback.size(); i++) {
                String flowSt = callback.get(i);
                flowSt = flowSt.replace('^', '"');
                callbackNew.add(flowSt);
            }
            return callbackNew;
        }
        return callback;
    }

    public static String getPostParamRestore( String postParam) {
        String postParamSt = postParam.replace('^', '"');
        return postParamSt;
    }

    public static ArrayList<String> getFlowRestore(ArrayList<String> flow) {
        if (flow != null) {
            ArrayList<String> flowNew = new ArrayList();
            for (int i = 0; i < flow.size(); i++) {
                String flowSt = flow.get(i);
                flowSt = flowSt.replace('^', '"');
                flowNew.add(flowSt);
            }
            return flowNew;
        }
        return flow;
    }
}
