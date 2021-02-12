package com.datamation.smackcerasfa.model;

import com.google.gson.annotations.SerializedName;

public class OrderDisc {

  //  @SerializedName("RefNo")
    private String ORDDISC_REFNO;
   // @SerializedName("TxnDate")
    private String ORDDISC_TXNDATE;
   // @SerializedName("RefNo1")
    private String ORDDISC_REFNO1;
   // @SerializedName("ItemCode")
    private String ORDDISC_ITEMCODE;
   // @SerializedName("DisAmt")
    private String ORDDISC_DISAMT;
  //  @SerializedName("DisPer")
    private String ORDDISC_DISPER;

    public String getRefNo() {
        return ORDDISC_REFNO;
    }

    public void setRefNo(String refNo) {
        ORDDISC_REFNO = refNo;
    }

    public String getTxnDate() {
        return ORDDISC_TXNDATE;
    }

    public void setTxnDate(String txnDate) {
        ORDDISC_TXNDATE = txnDate;
    }

    public String getRefNo1() {
        return ORDDISC_REFNO1;
    }

    public void setRefNo1(String refNo1) {
        ORDDISC_REFNO1 = refNo1;
    }

    public String getItemCode() {
        return ORDDISC_ITEMCODE;
    }

    public void setItemCode(String itemCode) {
        ORDDISC_ITEMCODE = itemCode;
    }

    public String getDisAmt() {
        return ORDDISC_DISAMT;
    }

    public void setDisAmt(String disAmt) {
        ORDDISC_DISAMT = disAmt;
    }

    public String getDisPer() {
        return ORDDISC_DISPER;
    }

    public void setDisPer(String disPer) {
        ORDDISC_DISPER = disPer;
    }


}
