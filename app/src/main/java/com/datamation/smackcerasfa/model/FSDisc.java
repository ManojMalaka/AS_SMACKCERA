package com.datamation.smackcerasfa.model;

import org.json.JSONException;
import org.json.JSONObject;

public class FSDisc {
    //Created by ************* MMS - 2021/01/12 **********
    private String FSDISC_ID;
    private String FSDISC_ADD_DATE;
    private String FSDISC_DEB_CODE;
    private String FSDISC_DIS_PER;
    private String FSDISC_GROUP_CODE;
    private String FSDISC_ITEM_CODE;
    private String FSDISC_PRIORITY;

    public String getFSDISC_ID() {
        return FSDISC_ID;
    }

    public void setFSDISC_ID(String FSDISC_ID) {
        this.FSDISC_ID = FSDISC_ID;
    }

    public String getFSDISC_ADD_DATE() {
        return FSDISC_ADD_DATE;
    }

    public void setFSDISC_ADD_DATE(String FSDISC_ADD_DATE) {
        this.FSDISC_ADD_DATE = FSDISC_ADD_DATE;
    }

    public String getFSDISC_DEB_CODE() {
        return FSDISC_DEB_CODE;
    }

    public void setFSDISC_DEB_CODE(String FSDISC_DEB_CODE) {
        this.FSDISC_DEB_CODE = FSDISC_DEB_CODE;
    }

    public String getFSDISC_DIS_PER() {
        return FSDISC_DIS_PER;
    }

    public void setFSDISC_DIS_PER(String FSDISC_DIS_PER) {
        this.FSDISC_DIS_PER = FSDISC_DIS_PER;
    }

    public String getFSDISC_GROUP_CODE() {
        return FSDISC_GROUP_CODE;
    }

    public void setFSDISC_GROUP_CODE(String FSDISC_GROUP_CODE) {
        this.FSDISC_GROUP_CODE = FSDISC_GROUP_CODE;
    }

    public String getFSDISC_ITEM_CODE() {
        return FSDISC_ITEM_CODE;
    }

    public void setFSDISC_ITEM_CODE(String FSDISC_ITEM_CODE) {
        this.FSDISC_ITEM_CODE = FSDISC_ITEM_CODE;
    }

    public String getFSDISC_PRIORITY() {
        return FSDISC_PRIORITY;
    }

    public void setFSDISC_PRIORITY(String FSDISC_PRIORITY) {
        this.FSDISC_PRIORITY = FSDISC_PRIORITY;
    }

    public static FSDisc parseFSDisc(JSONObject instance) throws JSONException {

        if (instance != null) {
            FSDisc fsDisc = new FSDisc();

            fsDisc.setFSDISC_ADD_DATE(instance.getString("AddDate"));
            fsDisc.setFSDISC_DEB_CODE(instance.getString("DebCode"));
            fsDisc.setFSDISC_DIS_PER(instance.getString("DisPer"));
            fsDisc.setFSDISC_GROUP_CODE(instance.getString("GroupCode"));
            fsDisc.setFSDISC_ITEM_CODE(instance.getString("ItemCode"));
            fsDisc.setFSDISC_PRIORITY(instance.getString("Priority"));
//            fsDisc.setFSDISC_ID(instance.getString(""));

            return fsDisc;
        }

        return null;
    }
}
