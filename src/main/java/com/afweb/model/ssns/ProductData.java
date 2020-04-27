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
public class ProductData {

    /**
     * @return the cmd
     */
    public ArrayList<String> getCmd() {
        return cmd;
    }

    /**
     * @param cmd the cmd to set
     */
    public void setCmd(ArrayList<String> cmd) {
        this.cmd = cmd;
    }

    /**
     * @return the detailResp
     */


    public ArrayList<String> getDetailResp() {
        return detailResp;
    }

    /**
     * @param detailResp the detailResp to set
     */
    public void setDetailResp(ArrayList<String> detailResp) {
        if (detailResp != null) {
            ArrayList<String> detailRespNew = new ArrayList();
            for (int i = 0; i < detailResp.size(); i++) {
                String flowSt = detailResp.get(i);
                flowSt = ServiceAFweb.replaceAll("\"", "^", flowSt);
                detailRespNew.add(flowSt);
            }
            this.detailResp = detailRespNew;
            return;
        }
        this.detailResp = detailResp;
    }

    /**
     * @return the callback
     */
   
    public ArrayList<String> getCallback() {
        return callback;
    }

    /**
     * @param callback the callback to set
     */
    public void setCallback(ArrayList<String> callback) {
        if (callback != null) {
            ArrayList<String> callbackNew = new ArrayList();
            for (int i = 0; i < callback.size(); i++) {
                String flowSt = callback.get(i);
                flowSt = ServiceAFweb.replaceAll("\"", "^", flowSt);
                callbackNew.add(flowSt);
            }
            this.callback = callbackNew;
            return;
        }
        this.callback = callback;
    }

    /**
     * @return the postParam
     */

    public String getPostParam() {
        return postParam;
    }

    /**
     * @param postParam the postParam to set
     */
    public void setPostParam(String postParam) {
        postParam = ServiceAFweb.replaceAll("\"", "^", postParam);
        this.postParam = postParam;
    }

    /**
     * @return the flow
     */

    public ArrayList<String> getFlow() {
        return flow;
    }

    /**
     * @param flow the flow to set
     */
    public void setFlow(ArrayList<String> flow) {
        if (flow != null) {
            ArrayList<String> flowNew = new ArrayList();
            for (int i = 0; i < flow.size(); i++) {
                String flowSt = flow.get(i);
                flowSt = ServiceAFweb.replaceAll("\"", "^", flowSt);
                flowNew.add(flowSt);
            }
            this.flow = flowNew;
            return;
        }
        this.flow = flow;

    }
    private String postParam = "";
    private ArrayList<String> cmd;

    private ArrayList<String> flow;
    private ArrayList<String> callback;
    private ArrayList<String> detailResp;
}
