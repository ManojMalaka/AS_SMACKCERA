package com.datamation.smackcerasfa.model;

import org.json.JSONException;
import org.json.JSONObject;

public class SalRep {

    private String CONSOLE_DB;
    private String DIST_DB;
    private String RepCode;
    private String NAME;
    private String REPID;
    private String ADDMACH;
    private String ADDUSER;
    private String MOBILE;
    private String STATUS;
    private String PREFIX;
    private String TELE;
    private String LOCCODE;
    private String EMAIL;
    private String AREACODE;
    private String DEALCODE;
    private String MACID;
    private String ISSYNC;
    private String REPTCODE;
    private String IS_ZERO_QOH_ALLOW;
    private String firebaseTokenID;
    private String IsApplyQOHexdVldtn;
    private String chkOrdUpload;

    private String ASE_CODE;
    private String CASHFLOT;
    private String PASSWORD;
    private String RECORDID;
    private String SALESFLAG;

    public String getASE_CODE() {
        return ASE_CODE;
    }

    public void setASE_CODE(String ASE_CODE) {
        this.ASE_CODE = ASE_CODE;
    }

    public String getCASHFLOT() {
        return CASHFLOT;
    }

    public void setCASHFLOT(String CASHFLOT) {
        this.CASHFLOT = CASHFLOT;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

    public void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }

    public String getRECORDID() {
        return RECORDID;
    }

    public void setRECORDID(String RECORDID) {
        this.RECORDID = RECORDID;
    }

    public String getSALESFLAG() {
        return SALESFLAG;
    }

    public void setSALESFLAG(String SALESFLAG) {
        this.SALESFLAG = SALESFLAG;
    }

    public String getChkOrdUpload() {
        return chkOrdUpload;
    }

    public void setChkOrdUpload(String chkOrdUpload) {
        this.chkOrdUpload = chkOrdUpload;
    }

    public String getIsApplyQOHexdVldtn() {
        return IsApplyQOHexdVldtn;
    }

    public void setIsApplyQOHexdVldtn(String isApplyQOHexdVldtn) {
        IsApplyQOHexdVldtn = isApplyQOHexdVldtn;
    }

    public String getCONSOLE_DB() {
        return CONSOLE_DB;
    }

    public void setCONSOLE_DB(String CONSOLE_DB) {
        this.CONSOLE_DB = CONSOLE_DB;
    }

    public String getDIST_DB() {
        return DIST_DB;
    }

    public void setDIST_DB(String DIST_DB) {
        this.DIST_DB = DIST_DB;
    }

    public String getREPTCODE() {
        return REPTCODE;
    }

    public void setREPTCODE(String REPTCODE) {
        this.REPTCODE = REPTCODE;
    }

    public String getISSYNC() {
        return ISSYNC;
    }

    public void setISSYNC(String ISSYNC) {
        this.ISSYNC = ISSYNC;
    }

    public String getMACID() {
        return MACID;
    }

    public void setMACID(String MACID) {
        this.MACID = MACID;
    }

    public String getDEALCODE() {
        return DEALCODE;
    }

    public void setDEALCODE(String DEALCODE) {
        this.DEALCODE = DEALCODE;
    }

    public String getAREACODE() {
        return AREACODE;
    }

    public void setAREACODE(String AREACODE) {
        this.AREACODE = AREACODE;
    }

    public String getEMAIL() {

        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public String getLOCCODE() {
        return LOCCODE;
    }

    public void setLOCCODE(String LOCCODE) {
        this.LOCCODE = LOCCODE;
    }


    public String getADDMACH() {
        return ADDMACH;
    }

    public void setADDMACH(String aDDMACH) {
        ADDMACH = aDDMACH;
    }

    public String getADDUSER() {
        return ADDUSER;
    }

    public void setADDUSER(String aDDUSER) {
        ADDUSER = aDDUSER;
    }

    public String getRepCode() {
        return RepCode;
    }

    public void setRepCode(String repCode) {
        RepCode = repCode;
    }

    public String getREPID() {
        return REPID;
    }

    public void setREPID(String rEPID) {
        REPID = rEPID;
    }

    public String getMOBILE() {
        return MOBILE;
    }

    public void setMOBILE(String mOBILE) {
        MOBILE = mOBILE;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String nAME) {
        NAME = nAME;
    }

    public String getPREFIX() {
        return PREFIX;
    }

    public void setPREFIX(String pREFIX) {
        PREFIX = pREFIX;
    }

    public String getTELE() {
        return TELE;
    }

    public void setTELE(String tELE) {
        TELE = tELE;
    }

    public String getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(String sTATUS) {
        STATUS = sTATUS;
    }

    public String getIS_ZERO_QOH_ALLOW() {
        return IS_ZERO_QOH_ALLOW;
    }

    public void setIS_ZERO_QOH_ALLOW(String IS_ZERO_QOH_ALLOW) {
        this.IS_ZERO_QOH_ALLOW = IS_ZERO_QOH_ALLOW;
    }

    public String getFirebaseTokenID() {
        return firebaseTokenID;
    }

    public void setFirebaseTokenID(String firebaseTokenID) {
        this.firebaseTokenID = firebaseTokenID;
    }

    public static SalRep parseUser(JSONObject instance) throws JSONException, NumberFormatException {

        if (instance != null) {
            SalRep user = new SalRep();
            user.setRepCode(instance.getString("RepCode"));
            user.setNAME(instance.getString("RepName"));
            user.setADDMACH(instance.getString("AddMach"));
            user.setADDUSER(instance.getString("AddUser"));
            user.setMOBILE(instance.getString("RepMob"));
            user.setDEALCODE(instance.getString("DealCode").trim());
            user.setSTATUS(instance.getString("Status"));
            user.setPREFIX(instance.getString("RepPrefix"));
            user.setTELE(instance.getString("RepTele"));
            user.setEMAIL(instance.getString("RepEMail"));
            user.setREPTCODE(instance.getString("RepTCode"));
            user.setASE_CODE(instance.getString("ASECode"));
            user.setAREACODE(instance.getString("AreaCode"));
            user.setCASHFLOT(instance.getString("CashFlot"));
            user.setPASSWORD(instance.getString("Password"));
            user.setRECORDID(instance.getString("RecordId"));
            user.setSALESFLAG(instance.getString("SalesFlg"));

            return user;
        }

        return null;
    }

    @Override
    public String toString() {
        return "SalRep{" +
                "CONSOLE_DB='" + CONSOLE_DB + '\'' +
                ", DIST_DB='" + DIST_DB + '\'' +
                ", NAME='" + NAME + '\'' +
                ", REPID='" + REPID + '\'' +
                ", ADDMACH='" + ADDMACH + '\'' +
                ", ADDUSER='" + ADDUSER + '\'' +
                ", MOBILE='" + MOBILE + '\'' +
                ", STATUS='" + STATUS + '\'' +
                ", PREFIX='" + PREFIX + '\'' +
                ", TELE='" + TELE + '\'' +
                ", LOCCODE='" + LOCCODE + '\'' +
                ", EMAIL='" + EMAIL + '\'' +
                ", AREACODE='" + AREACODE + '\'' +
                ", DEALCODE='" + DEALCODE + '\'' +
                ", MACID='" + MACID + '\'' +
                ", ISSYNC='" + ISSYNC + '\'' +
                ", REPTCODE='" + REPTCODE + '\'' +
                ", REPCODE='" + RepCode + '\'' +
                ", FirebaseTokenID='" + firebaseTokenID + '\'' +

                '}';
    }

}
