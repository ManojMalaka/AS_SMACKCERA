package com.datamation.smackcerasfa.presale;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.widget.SearchView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.datamation.smackcerasfa.R;
import com.datamation.smackcerasfa.adapter.FreeIssueAdapter;
import com.datamation.smackcerasfa.adapter.OrderDetailsAdapterNew;
import com.datamation.smackcerasfa.adapter.OrderFreeItemAdapter;
import com.datamation.smackcerasfa.adapter.PreOrderAdapter;
import com.datamation.smackcerasfa.controller.FSDiscController;
import com.datamation.smackcerasfa.controller.ItemController;
import com.datamation.smackcerasfa.controller.ItemPriController;
import com.datamation.smackcerasfa.controller.OrdFreeIssueController;
import com.datamation.smackcerasfa.controller.OrderController;
import com.datamation.smackcerasfa.controller.OrderDetailController;
import com.datamation.smackcerasfa.controller.OrderDiscController;
import com.datamation.smackcerasfa.controller.PreProductController;
import com.datamation.smackcerasfa.controller.ProductController;
import com.datamation.smackcerasfa.controller.ReasonController;
import com.datamation.smackcerasfa.dialog.CustomProgressDialog;
import com.datamation.smackcerasfa.discount.Discount;
import com.datamation.smackcerasfa.freeissue.FreeIssueModified;
import com.datamation.smackcerasfa.helpers.PreSalesResponseListener;
import com.datamation.smackcerasfa.helpers.SharedPref;
import com.datamation.smackcerasfa.model.Customer;
import com.datamation.smackcerasfa.model.FreeItemDetails;
import com.datamation.smackcerasfa.model.ItemFreeIssue;
import com.datamation.smackcerasfa.model.OrdFreeIssue;
import com.datamation.smackcerasfa.model.Order;
import com.datamation.smackcerasfa.model.OrderDetail;
import com.datamation.smackcerasfa.model.OrderDisc;
import com.datamation.smackcerasfa.model.PreProduct;
import com.datamation.smackcerasfa.model.Reason;
import com.datamation.smackcerasfa.settings.ReferenceNum;
import com.datamation.smackcerasfa.utils.GPSTracker;
import com.datamation.smackcerasfa.view.PreSalesActivity;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class OrderDetailFragment extends Fragment {

    /* *************** ** New Moodifications by MMS 2021/01 ** ************************** */

    private static final String TAG = "OrderDetailFragment";
    public View view;
    public SharedPref mSharedPref;
    int totPieces = 0;
    int seqno = 0;
    ListView lv_order_det, lvFree;
    ArrayList<PreProduct> productList = null, selectedItemList = null;
    ImageButton ibtProduct,ibtDiscount;

    LinearLayout reasonLayout;
    SweetAlertDialog pDialog;
    PreSalesResponseListener preSalesResponseListener;
    int clickCount = 0;
    PreSalesActivity mainActivity;
    ArrayList<OrderDetail> orderList;
    String address = "No Address";
    double latitude = 0.0;
    double longitude = 0.0;
    GPSTracker gpsTracker;
    String isQohZeroAllow = "0", qohStatus = "1";//qohStatus = 1 (show qoh > 0) else  all items check only isQohZeroAllow = 1 (2019-12-20 rashmi)
    private String RefNo, locCoe;
    private MyReceiver r;
    private Order tmpsoHed = null;  //from re oder creation
    private double totAmt = 0.0;
    private Customer debtor;
    private Spinner spnTxn, spnReason, spnQOH;

    //PreSalesResponseListener preSalesResponseListener;
    public OrderDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.pre_sale_details_new_responsive_layout, container, false);
        //view = inflater.inflate(R.layout.sales_management_pre_sales_details_new, container, false);
        seqno = 0;
        totPieces = 0;
        mSharedPref = SharedPref.getInstance(getActivity());
        lv_order_det = (ListView) view.findViewById(R.id.lvProducts_pre);
        lvFree = (ListView) view.findViewById(R.id.lvFreeIssue_Inv);
        ibtDiscount = (ImageButton) view.findViewById(R.id.ibtDisc);
        ibtProduct = (ImageButton) view.findViewById(R.id.ibtProduct);
        mainActivity = (PreSalesActivity) getActivity();
        reasonLayout = (LinearLayout) view.findViewById(R.id.reason_layout);
        RefNo = new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.NumVal));
        spnTxn = (Spinner) view.findViewById(R.id.spnTxn);
        spnQOH = (Spinner) view.findViewById(R.id.spnQOH);
        spnReason = (Spinner) view.findViewById(R.id.spnReason);
//        RefNo = mainActivity.selectedPreHed.getORDER_REFNO();
        tmpsoHed = new Order();
        ArrayList<Reason> mrReasons;
        showData();
        gpsTracker = new GPSTracker(getActivity());
        Log.v("Latitude>>>>>", mSharedPref.getGlobalVal("Latitude"));
        Log.v("Longi>>>>>", mSharedPref.getGlobalVal("Longitude"));
        mSharedPref.setGlobalVal("preKeyIsFreeClicked", "0");
        ArrayList<String> strList = new ArrayList<String>();
        strList.add("SA-PRE SALE");
        strList.add("FI-FREE ISSUE");
        strList.add("UR-USABLE RETURN");
        strList.add("MR-MARKET RETURN");

        ArrayList<String> qohSttsList = new ArrayList<String>();

        qohSttsList.add("VIEW QOH ITEMS");
        qohSttsList.add("VIEW ALL");

        mSharedPref.setDiscountClicked("0");
        clickCount = 0;

        Log.d("order_detail", "clicked_count" + clickCount);

//        isQohZeroAllow = new SalRepController(getActivity()).isQohZeroAllow();

        final ArrayAdapter<String> txnTypeAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.return_spinner_item, strList);
        txnTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTxn.setAdapter(txnTypeAdapter);
        final ArrayAdapter<String> qohAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.return_spinner_item, qohSttsList);
        qohAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnQOH.setAdapter(qohAdapter);
        spnTxn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 3) {//if market return selected
                    reasonLayout.setVisibility(View.VISIBLE);
                    ArrayList<String> mrReasons = new ReasonController(getActivity()).getAllReasonsByType("RT01");
                    final ArrayAdapter<String> reasonAdapter = new ArrayAdapter<String>(getActivity(),
                            R.layout.return_spinner_item, mrReasons);
                    reasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnReason.setAdapter(reasonAdapter);
                } else {
                    reasonLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnQOH.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    qohStatus = "1";
                } else {
                    qohStatus = "0";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                SharedPref.getInstance(getActivity()).setGlobalVal("reason", spnReason.getSelectedItem().toString().split("-")[0]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ibtProduct.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (new OrderController(getActivity()).IsSavedHeader(RefNo) > 0) {
                    mSharedPref.setGlobalVal("preKeyIsFreeClicked", "0");
                    mSharedPref.setDiscountClicked("0");
                    clickCount = 0;

                    int position = spnTxn.getSelectedItemPosition();
                    Toast.makeText(getActivity(),
                            spnTxn.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                    if (position == 0) {
                        new LoardingProductFromDB("SA").execute();
                    } else if (position == 1) {
                        //show separate dialog to enter free issues
                        new LoardingProductFromDB("FI").execute();
                    } else if (position == 2) {
                        new LoardingProductFromDB("UR").execute();
                    } else {
                        if (spnReason.getSelectedItem().toString().equals("")) {
                            Toast.makeText(getActivity(),
                                    "Please select reason to continue..", Toast.LENGTH_SHORT).show();
                        } else {
                            new LoardingProductFromDB("MR").execute();
                        }
                    }

                } else {
                    preSalesResponseListener.moveBackToCustomer_pre(0);
                    Toast.makeText(getActivity(), "Cannot proceed,Please click arrow button to save header details...", Toast.LENGTH_LONG).show();
                }

            }
        });

        ibtDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSharedPref.setDiscountClicked("1");
                mSharedPref.setGlobalVal("preKeyIsFreeClicked", "" + clickCount);
                if (clickCount == 0) {

//                    new CalculateFree(mSharedPref.getSelectedDebCode()).execute();
//                    new CalculateDiscounts(mSharedPref.getSelectedDebCode()).execute();
                    try {

                        ibtDiscount.setClickable(false);

                        ReferenceNum referenceNum = new ReferenceNum(getActivity());

                        ArrayList<OrderDetail> dets = new OrderDetailController(getActivity()).getSAForFreeIssueCalc(referenceNum.getCurrentRefNo(getResources().getString(R.string.NumVal)));

//                        // new
                         new OrderDetailController(getActivity()).restFreeIssueData(referenceNum.getCurrentRefNo(getResources().getString(R.string.NumVal)));

                        fatchData();

                        ArrayList<OrderDisc> disItemDetails = getDiscountsBySalesItem(dets);// calc
                        // class
                        discountDialogBox(disItemDetails);

                        fatchData();
                    } catch (Exception e) {
                        Log.v("Exception", e.toString());
                    }


                    clickCount++;
                } else {
                    Toast.makeText(getActivity(), "Already clicked", Toast.LENGTH_LONG).show();
                    Log.v("Freeclick Count", mSharedPref.getGlobalVal("preKeyIsFreeClicked"));
                }

            }
        });

        lv_order_det.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mSharedPref.setDiscountClicked("0");
                clickCount = 0;
                mSharedPref.setGlobalVal("preKeyIsFreeClicked", "0");
                new OrderDetailController(getActivity()).restFreeIssueData(RefNo);
                newDeleteOrderDialog(position);
                return true;
            }
        });

//        lv_order_det.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view2, final int position, long id)
//            {
//                final OrderDetail orderDet=orderList.get(position);
//                mSharedPref.setGlobalVal("preKeyIsFreeClicked", "0");
//                if(orderDet.getFORDERDET_TYPE().equals("MR")){
//                     CustomKeypadDialogReturnAmt keypadPrice = new CustomKeypadDialogReturnAmt(getActivity(), true, new CustomKeypadDialogReturnAmt.IOnOkClickListener() {
//                        @Override
//                        public void okClicked(double value) {
//                            //price cannot be changed less than gross profit
//                            if((Double.parseDouble(orderDet.getFORDERDET_AMT())*(-1)) > value) {
//                                Log.d("EDIT>>>","EDIT>>>"+orderDet.getFORDERDET_TYPE()+" - "+orderDet.getFORDERDET_ITEMCODE()+"-"+orderDet.getFORDERDET_QTY());
//                                new ProductController(getActivity()).updateProductPrice(orderDet.getFORDERDET_ITEMCODE(), String.valueOf((-value/Double.parseDouble(orderDet.getFORDERDET_QTY()))),orderDet.getFORDERDET_TYPE());
//                                //new OrderDetailController(getActivity()).updateProductPrice(orderDet.getFORDERDET_ITEMCODE(), String.valueOf((-value/Double.parseDouble(orderDet.getFORDERDET_QTY()))),String.valueOf(value),orderDet.getFORDERDET_TYPE());
//                                //new OrderDetailController(getActivity()).updateProductPrice(orderDet.getFORDERDET_ITEMCODE(), String.valueOf((-value/Double.parseDouble(orderDet.getFORDERDET_QTY()))),orderDet.getFORDERDET_AMT(),orderDet.getFORDERDET_TYPE());
//                                showData();
//                            }else{
//                                Toast.makeText(getActivity(),"Amount cannot be change..",Toast.LENGTH_LONG).show();
//                            }
//
//                        }
//                    });
//                    keypadPrice.show();
//
//                    keypadPrice.setHeader("CHANGE AMOUNT");
////                if(preProduct.getPREPRODUCT_CHANGED_PRICE().equals("0")){
//                    keypadPrice.loadValue(Double.parseDouble(orderDet.getFORDERDET_AMT()));
//
//                }else {
//                    Log.d("EDIT>>>", "EDIT>>>" + orderDet.getFORDERDET_TYPE() + " - " + orderDet.getFORDERDET_ITEMCODE() + "-" + orderDet.getFORDERDET_QTY());
//                }
//            }
//        });

        return view;
    }


    public void fatchData() {
        try {
            lv_order_det = (ListView) view.findViewById(R.id.lv_order_det);
            lv_order_det.clearTextFilter();
//            OrdDetDS detDS = new OrdDetDS(getActivity());
//            orderList = detDS.getAllOrderDetails(activity.selectedOrdHed.getFORDHED_REFNO());
//            lv_order_det.setAdapter(new OrderDetailsAdapter(getActivity(), orderList));//            MainActivity activity = (MainActivity) getActivity();

        } catch (NullPointerException e) {
            Log.v("SA Error", e.toString());
        }
    }

    @SuppressWarnings("unused")
    private boolean discountDialogBox(ArrayList<OrderDisc> disitemDetails) {

        double totdisamt = 0.00;
        String OrdRefNo = "";
        String OrdItemCode = "";
        double OrdItemDis = 0.00;
        OrderDiscController disupd = new OrderDiscController(getActivity());

        for (OrderDisc disitem : disitemDetails) {
            totdisamt = totdisamt + Double.parseDouble(disitem.getDisAmt());
            OrdRefNo = disitem.getRefNo();
            OrdItemCode = disitem.getItemCode();
            OrdItemDis = Double.parseDouble(disitem.getDisAmt());

        }

//        disupd.restData();
        disupd.createOrUpdateOrdDisc(disitemDetails);
//        disupd.UpdateOrdDis(disitemDetails);

        Toast.makeText(getContext(),"Calculated Discount "+String.valueOf(totdisamt), Toast.LENGTH_SHORT).show();

        ibtDiscount.setClickable(true);

        return true;
    }

    //------------------------------------------------------------show ---------------Free issue Dailog box--------------------------------------------------------------------------

    private boolean freeIssueDialogBox(final FreeItemDetails itemDetails) {

        final ArrayList<ItemFreeIssue> itemFreeIssues;
        final ArrayList<String> saleItemList;
        final String FIRefNo = itemDetails.getRefno();
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.free_issues_items_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Free Issue Schemes");
        alertDialogBuilder.setView(promptView);

        final ListView listView = (ListView) promptView.findViewById(R.id.lv_free_issue);
        final ListView listViewsale = (ListView) promptView.findViewById(R.id.lv_free_issue_items);
        final TextView itemName = (TextView) promptView.findViewById(R.id.tv_free_issue_item_name);
        final TextView itemNamefree = (TextView) promptView.findViewById(R.id.tv_free_issued_item_name);
        final TextView freeQty = (TextView) promptView.findViewById(R.id.tv_free_qty);

        freeQty.setText("Free Quantity : " + itemDetails.getFreeQty());
        //itemName.setText("Products : " + new ItemController(getActivity()).getItemNameByCode(itemDetails.getFreeIssueSelectedItem()));
        // itemName.setText("Products : ");
        //itemNamefree.setText("Free Products : " + new ItemController(getActivity()).getItemNameByCode(itemDetails.getFreeIssueSelectedItem()));
        itemNamefree.setText("Free Products : ");

        final ItemController itemsDS = new ItemController(getActivity());
        itemFreeIssues = itemsDS.getAllFreeItemNameByRefno(itemDetails.getRefno());
        saleItemList = itemsDS.getFreeIssueItemDetailsByItem(itemDetails.getSaleItemList());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, saleItemList);
        listViewsale.setAdapter(adapter);
        listView.setAdapter(new FreeIssueAdapter(getActivity(), itemFreeIssues, itemDetails));


        alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                for (ItemFreeIssue itemFreeIssue : itemFreeIssues) {

                    if (Integer.parseInt(itemFreeIssue.getAlloc()) > 0) {

                        seqno++;
                        OrderDetail ordDet = new OrderDetail();
                        OrderDetailController detDS = new OrderDetailController(getActivity());
                        ArrayList<OrderDetail> ordList = new ArrayList<OrderDetail>();

                        ordDet.setFORDERDET_ID("0");
                        ordDet.setFORDERDET_AMT("0");
                        ordDet.setFORDERDET_BALQTY(itemFreeIssue.getAlloc());
                        ordDet.setFORDERDET_BAMT("0");
                        ordDet.setFORDERDET_BDISAMT("0");
                        ordDet.setFORDERDET_BPDISAMT("0");
                        String unitPrice = new ItemPriController(getActivity()).getProductPriceByCode(itemFreeIssue.getItems().getFITEM_ITEM_CODE(), mSharedPref.getSelectedDebtorPrilCode());
                        ordDet.setFORDERDET_BSELLPRICE("0");
                        ordDet.setFORDERDET_BTSELLPRICE("0.00");
                        ordDet.setFORDERDET_DISAMT("0");
                        ordDet.setFORDERDET_SCHDISPER("0");
                        ordDet.setFORDERDET_FREEQTY("0");
                        ordDet.setFORDERDET_ITEMCODE(itemFreeIssue.getItems().getFITEM_ITEM_CODE());
                        ordDet.setFORDERDET_PDISAMT("0");
                        ordDet.setFORDERDET_PRILCODE("WSP001");
                        ordDet.setFORDERDET_QTY(itemFreeIssue.getAlloc());
                        ordDet.setFORDERDET_PICE_QTY(itemFreeIssue.getAlloc());
                        ordDet.setFORDERDET_CASES("0");//because free issue not issued cases wise-2019-10-21 menaka said
                        ordDet.setFORDERDET_TYPE("FD");
                        ordDet.setFORDERDET_RECORDID("");
                        ordDet.setFORDERDET_REFNO(RefNo);
                        ordDet.setFORDERDET_SELLPRICE("0");
                        ordDet.setFORDERDET_SEQNO(seqno + "");
                        ordDet.setFORDERDET_TAXAMT("0");
                        ordDet.setFORDERDET_TAXCOMCODE(new ItemController(getActivity()).getTaxComCodeByItemCode(itemFreeIssue.getItems().getFITEM_ITEM_CODE()));
                        //ordDet.setFTRANSODET_TAXCOMCODE(new ItemsDS(getActivity()).getTaxComCodeByItemCodeFromDebTax(itemFreeIssue.getItems().getFITEM_ITEM_CODE(), mainActivity.selectedDebtor.getFDEBTOR_CODE()));
                        ordDet.setFORDERDET_TIMESTAMP_COLUMN("");
                        ordDet.setFORDERDET_TSELLPRICE("0.00");
                        ordDet.setFORDERDET_TXNDATE(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                        ordDet.setFORDERDET_TXNTYPE("27");
                        ordDet.setFORDERDET_IS_ACTIVE("1");
                        ordDet.setFORDERDET_LOCCODE(mSharedPref.getGlobalVal("PrekeyLocCode").trim());
                        ordDet.setFORDERDET_COSTPRICE(new ItemController(getActivity()).getCostPriceItemCode(itemFreeIssue.getItems().getFITEM_ITEM_CODE()));
                        ordDet.setFORDERDET_BTAXAMT("0");
                        ordDet.setFORDERDET_IS_SYNCED("0");
                        ordDet.setFORDERDET_QOH("0");
                        ordDet.setFORDERDET_SCHDISC("0");
                        ordDet.setFORDERDET_BRAND_DISC("0");
                        ordDet.setFORDERDET_BRAND_DISPER("0");
                        ordDet.setFORDERDET_COMP_DISC("0");
                        ordDet.setFORDERDET_COMP_DISPER("0");
                        ordDet.setFORDERDET_DISCTYPE("");
                        ordDet.setFORDERDET_PRICE("0.00");
                        ordDet.setFORDERDET_ORG_PRICE("0");
                        ordDet.setFORDERDET_DISFLAG("0");
                        ordDet.setFORDERDET_REACODE("");

                        /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*OrdFreeIssue table update*-*-*-*-*-*-*-*-*-*-*-*-*-*/

                        OrdFreeIssue ordFreeIssue = new OrdFreeIssue();
                        ordFreeIssue.setOrdFreeIssue_ItemCode(itemFreeIssue.getItems().getFITEM_ITEM_CODE());
                        ordFreeIssue.setOrdFreeIssue_Qty(itemFreeIssue.getAlloc());
                        ordFreeIssue.setOrdFreeIssue_RefNo(FIRefNo);
                        ordFreeIssue.setOrdFreeIssue_RefNo1(RefNo);
                        ordFreeIssue.setOrdFreeIssue_TxnDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                        new OrdFreeIssueController(getActivity()).UpdateOrderFreeIssue(ordFreeIssue);

                        /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-*/

                        ordList.add(ordDet);

                        if (detDS.createOrUpdateOrdDet(ordList) > 0) {
                            Toast.makeText(getActivity(), "Added successfully", Toast.LENGTH_SHORT).show();
                            showData();

//                            lvFree.setAdapter(null);
//                            ArrayList<OrderDetail> freeList=new OrderDetailController(getActivity()).getAllFreeIssue(RefNo);
//                            lvFree.setAdapter(new OrderFreeItemAdapter(getActivity(), freeList));
                        }
                    }
                }
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mSharedPref.setGlobalVal("preKeyIsFreeClicked", "0");
                dialog.cancel();
            }
        });

        AlertDialog alertD = alertDialogBuilder.create();

        alertD.show();
        return true;
    }

    public void mToggleTextbox() {
        gpsTracker = new GPSTracker(getActivity());
        Log.v("Latitude>>>>>", mSharedPref.getGlobalVal("Latitude"));
        Log.v("Longi>>>>>", mSharedPref.getGlobalVal("Longitude"));
        showData();


    }

    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(r);

        Log.d("order_detail", "clicked_count" + clickCount);

    }

    public void onResume() {
        super.onResume();
        r = new MyReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(r, new IntentFilter("TAG_DETAILS"));
        Log.d("order_detail", "clicked_count" + clickCount);
    }

    public void showData() {
        try {
            lv_order_det.setAdapter(null);
            orderList = new OrderDetailController(getActivity()).getAllOrderDetails(RefNo);
            lv_order_det.setAdapter(new OrderDetailsAdapterNew(getActivity(), orderList, mSharedPref.getSelectedDebCode()));//2019-07-07 till error free

            lvFree.setAdapter(null);
            ArrayList<OrderDetail> freeList = new OrderDetailController(getActivity()).getAllFreeIssue(RefNo);
            lvFree.setAdapter(new OrderFreeItemAdapter(getActivity(), freeList));

            Log.d("order_detail", "clicked_count" + clickCount);


        } catch (NullPointerException e) {
            Log.v("SA Error", e.toString());
        }
    }

    public void ProductDialogBox(final String typeInProductDialog) {

        final LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.product_dialog_layout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptView);

        final ListView lvProducts = (ListView) promptView.findViewById(R.id.lv_product_list);
        final SearchView search = (SearchView) promptView.findViewById(R.id.et_search);


        lvProducts.clearTextFilter();
        productList.clear();
        productList = new PreProductController(getActivity()).getAllItems("", typeInProductDialog, qohStatus);
        lvProducts.setAdapter(new PreOrderAdapter(getActivity(), productList, typeInProductDialog, SharedPref.getInstance(getActivity()).getGlobalVal("reason")));

        alertDialogBuilder.setCancelable(false).setNegativeButton("DONE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                selectedItemList = new PreProductController(getActivity()).getSelectedItems(typeInProductDialog);

                updateOrderDet(selectedItemList, typeInProductDialog);

                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                productList = new PreProductController(getActivity()).getAllItems(query, typeInProductDialog, qohStatus);
                lvProducts.setAdapter(new PreOrderAdapter(getActivity(), productList, typeInProductDialog, SharedPref.getInstance(getActivity()).getGlobalVal("reason")));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                productList.clear();
                productList = new PreProductController(getActivity()).getAllItems(newText, typeInProductDialog, qohStatus);
                lvProducts.setAdapter(new PreOrderAdapter(getActivity(), productList, typeInProductDialog, SharedPref.getInstance(getActivity()).getGlobalVal("reason")));
                return true;
            }
        });
    }

    /*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void updateOrderDet(final ArrayList<PreProduct> list, final String updateOrderDetType) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
//                pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
//                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
//                pDialog.setTitleText("Updating products...");
//                pDialog.setCancelable(false);
//                pDialog.show();
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {

                int i = 0;
                RefNo = new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.NumVal));
                new OrderDetailController(getActivity()).deleteRecords(RefNo, updateOrderDetType);//commented by rashmi because check duplicate issue
                ArrayList<OrderDetail> toSaveOrderDetails = new OrderDetailController(getActivity()).mUpdatePrsSales(list, RefNo);

                if (new OrderDetailController(getActivity()).createOrUpdateOrdDet(toSaveOrderDetails) > 0) {
                    Log.d("ORDER_DETAILS", "Order det saved successfully...");
                } else {
                    Log.d("ORDER_DETAILS", "Order det saved unsuccess...");
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
//                if(pDialog.isShowing()){
//                    pDialog.dismiss();
//                }

                showData();
            }

        }.execute();
    }

    public void newDeleteOrderDialog(final int dltPosition) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Confirm Deletion !");
        alertDialogBuilder.setMessage("Do you want to delete this item ?");
        alertDialogBuilder.setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                new PreProductController(getActivity()).updateProductQty(orderList.get(dltPosition).getFORDERDET_ITEMCODE(), "0", orderList.get(dltPosition).getFORDERDET_TYPE());
                new PreProductController(getActivity()).updateProductCase(orderList.get(dltPosition).getFORDERDET_ITEMCODE(), "0", orderList.get(dltPosition).getFORDERDET_TYPE());
                // new PreProductController(getActivity()).updateProductp(orderList.get(dltPosition).getFORDERDET_ITEMCODE(), "0",orderList.get(dltPosition).getFORDERDET_TYPE());
                //   new PreProductController(getActivity()).updateBalQty(orderList.get(position).getFORDERDET_ITEMCODE(), "0");
                //  new PreProductController(getActivity()).updateRefNo(RefNo, "0",orderList.get(dltPosition).getFORDERDET_TYPE());
                if (orderList.get(dltPosition).getFORDERDET_TYPE().equals("MR")) {
                    new PreProductController(getActivity()).updateReason(orderList.get(dltPosition).getFORDERDET_ITEMCODE(), "", orderList.get(dltPosition).getFORDERDET_TYPE());
                    new ProductController(getActivity()).updateProductPrice(orderList.get(dltPosition).getFORDERDET_ITEMCODE(), "0", orderList.get(dltPosition).getFORDERDET_TYPE());
                }
                new OrderDetailController(getActivity()).mDeleteRecords(RefNo, orderList.get(dltPosition).getFORDERDET_ITEMCODE(), orderList.get(dltPosition).getFORDERDET_TYPE());
                Toast.makeText(getActivity(), "Deleted successfully!", Toast.LENGTH_SHORT).show();
                showData();

            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            preSalesResponseListener = (PreSalesResponseListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onButtonPressed");
        }
    }

    public class LoardingProductFromDB extends AsyncTask<Object, Object, ArrayList<PreProduct>> {

        private String type;

        public LoardingProductFromDB(String type) {
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Fetch Data Please Wait.");
            pDialog.setCancelable(false);
            //pDialog.show();
        }

        @Override
        protected ArrayList<PreProduct> doInBackground(Object... objects) {

            if (new PreProductController(getActivity()).tableHasRecords(type)) {
                productList = new PreProductController(getActivity()).getAllItems("", type, qohStatus);
            } else {
                //productList =new ItemController(getActivity()).getAllItemForPreSales("","",RefNo, mSharedPref.getSelectedDebtorPrilCode());
                new PreProductController(getActivity()).insertIntoProductAsBulkForPre("HO", "PR001", type);//prilcode, loccode hardcode for smackcera
                //productList = new PreProductController(getActivity()).getAllItems("");

                if (tmpsoHed != null) {

                    ArrayList<OrderDetail> orderDetailArrayList = tmpsoHed.getOrdDet();
                    if (orderDetailArrayList != null) {
                        for (int i = 0; i < orderDetailArrayList.size(); i++) {
                            String tmpItemcode = orderDetailArrayList.get(i).getFORDERDET_ITEMCODE();
                            String tmpItemname = orderDetailArrayList.get(i).getFORDERDET_ITEMNAME();
                            String tmpprice = orderDetailArrayList.get(i).getFORDERDET_PRICE();
                            String tmpqoh = orderDetailArrayList.get(i).getFORDERDET_QOH();
                            String tmpQty = orderDetailArrayList.get(i).getFORDERDET_QTY();
                            String tmpcase = orderDetailArrayList.get(i).getFORDERDET_CASES();
                            String tmprefno = orderDetailArrayList.get(i).getFORDERDET_REFNO();
                            String tmpunits = new ItemController(getActivity()).getUnits(tmpItemcode);

                            //Update Qty in  fProducts_pre table
                            int count = new PreProductController(getActivity()).updateQuantities(tmpItemcode, tmpItemname, tmpprice, tmpqoh, tmpQty, tmpcase, tmprefno, tmpunits, type);
                            if (count > 0) {
                                Log.d("InsertOrUpdate", "success");
                            } else {
                                Log.d("InsertOrUpdate", "Failed");
                            }

                        }
                    }
                }
                //----------------------------------------------------------------------------
            }
            productList = new PreProductController(getActivity()).getAllItems("", type, qohStatus);
            return productList;
        }


        @Override
        protected void onPostExecute(ArrayList<PreProduct> products) {
            super.onPostExecute(products);

            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            ProductDialogBox(type);
        }
    }

    public class CalculateFree extends AsyncTask<Object, Object, ArrayList<FreeItemDetails>> {
        CustomProgressDialog pdialog;
        private String debcode;

        public CalculateFree(String debcode) {
            this.pdialog = new CustomProgressDialog(getActivity());
            this.debcode = debcode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            pdialog.setMessage("Calculating promotions.. Please Wait.");
            pdialog.show();

            //pDialog.show();
        }

        @Override
        protected ArrayList<FreeItemDetails> doInBackground(Object... objects) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pdialog.setMessage("Calculating Free...");
                }
            });

            ArrayList<OrderDetail> dets = new OrderDetailController(getActivity()).getSAForFreeIssueCalc(RefNo);
            /* CLEAR ORDERDET TABLE RECORD IF FREE ITEMS ARE ALREADY ADDED. */
            new OrderDetailController(getActivity()).restFreeIssueData(RefNo);
            /* Clear free issues in OrdFreeIss */
            new OrdFreeIssueController(getActivity()).ClearFreeIssuesForPreSale(RefNo);
            // // Menaka on 09-12-2019
            FreeIssueModified freeIssue = new FreeIssueModified(getActivity());
            // GET ARRAY OF FREE ITEMS BY PASSING IN ORDER DETAILS
            //ArrayList<FreeItemDetails> list = issue.getFreeItemsBySalesItem(dets);
            ArrayList<FreeItemDetails> list = freeIssue.getFreeItemsBySalesItem(dets);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pdialog.setMessage("Calculed Free...");
                }
            });
            return list;
        }


        @Override
        protected void onPostExecute(ArrayList<FreeItemDetails> products) {
            super.onPostExecute(products);

            if (pdialog.isShowing()) {
                pdialog.dismiss();
            }
            for (FreeItemDetails freeItemDetails : products) {
                if (freeItemDetails.getFreeQty() > 0) {
                    freeIssueDialogBox(freeItemDetails);
                }
            }
            showData();

        }
    }

    public class CalculateDiscounts extends AsyncTask<Object, Object, Boolean> {
        CustomProgressDialog pdialog;
        private String debcode;

        public CalculateDiscounts(String debcode) {
            this.pdialog = new CustomProgressDialog(getActivity());
            this.debcode = debcode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            pdialog.setMessage("Calculating discounts.. Please Wait.");
            pdialog.show();

            //pDialog.show();
        }

        @Override
        protected Boolean doInBackground(Object... objects) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pdialog.setMessage("Calculating discounts...");
                }
            });

            ArrayList<OrderDetail> exOrderDet = new OrderDetailController(getActivity()).getExOrderDetails(RefNo);
            new OrderDetailController(getActivity()).resetOrderDetWithoutDiscountData(RefNo, exOrderDet);

            //Clear discount in Orddisc
            new OrderDiscController(getActivity()).ClearDiscountForPreSale(RefNo);
            ArrayList<OrderDetail> dets = new OrderDetailController(getActivity()).getSAForFreeIssueCalc(RefNo);

            Discount issue = new Discount(getActivity());

            /* Get discounts for assorted items */
            ArrayList<ArrayList<OrderDetail>> metaOrdList = issue.SortDiscount(dets, debcode);

            Log.d("PRE_SALES_ORDER_DETAILS", "LIST_SIZE: " + metaOrdList.size());

            /* Iterate through for discounts for items */
            for (ArrayList<OrderDetail> OrderList : metaOrdList) {

                double totAmt = 0;
                String discPer, discType, discRef;
                double freeVal = Double.parseDouble(OrderList.get(0).getFORDERDET_BAMT());
                if (OrderList.get(0).getFORDERDET_SCHDISPER() != null)
                    discPer = OrderList.get(0).getFORDERDET_SCHDISPER();
                else
                    discPer = "";
                if (OrderList.get(0).getFORDERDET_DISCTYPE() != null)
                    discType = OrderList.get(0).getFORDERDET_DISCTYPE();
                else
                    discType = "";
                if (OrderList.get(0).getFORDERDET_DISC_REF() != null)
                    discRef = OrderList.get(0).getFORDERDET_DISC_REF();
                else
                    discRef = "";


                OrderList.get(0).setFORDERDET_BAMT("0");

                for (OrderDetail det : OrderList)
                    totAmt += Double.parseDouble(det.getFORDERDET_PRICE()) * (Double.parseDouble(det.getFORDERDET_QTY()));
                // commented cue to getFTRANSODET_PRICE() is not set
                //totAmt += Double.parseDouble(det.getFTRANSODET_PRICE()) * (Double.parseDouble(det.getFTRANSODET_QTY()));

                for (OrderDetail det : OrderList) {
                    det.setFORDERDET_SCHDISPER(discPer);
                    det.setFORDERDET_DISCTYPE(discType);
                    det.setFORDERDET_DISC_REF(discRef);

                    double disc;
                    /*
                     * For value, calculate amount portion & for percentage ,
                     * calculate percentage portion
                     */
                    disc = (freeVal / totAmt) * Double.parseDouble(det.getFORDERDET_PRICE()) * (Double.parseDouble(det.getFORDERDET_QTY()));

                    //commented due to
                    // disc = (Double.parseDouble(det.getFTRANSODET_AMT()) / 100) * disc not correct

                    /* Calculate discount amount from disc percentage portion */
//					if (discType != null)
//                    {
//                        if (discType.equals("P"))
////                            disc = (Double.parseDouble(det.getFTRANSODET_AMT()) / 100) * disc;
//                            disc = (Double.parseDouble(det.getFTRANSODET_AMT()) / 100);
//                    }


                    //new OrderDetailController(getActivity()).updateDiscount(det, disc, det.getFORDERDET_DISCTYPE());

                }
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pdialog.setMessage("Calculed Discounts...");
                }
            });
            return true;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (pdialog.isShowing()) {
                pdialog.dismiss();
            }

            showData();

        }
    }

    public ArrayList<OrderDisc> getDiscountsBySalesItem(ArrayList<OrderDetail> dets) {

        ArrayList<OrderDisc> discountList = new ArrayList<OrderDisc>();
        discountList.clear();

        FSDiscController dissch = new FSDiscController(getContext());

        for (OrderDetail det : dets) {
            String OrdDetRef = det.getFORDERDET_REFNO();
            String DisItemCode = det.getFORDERDET_ITEMCODE();
            String CustomerCode = new OrderController(getContext()).getRefnoByDebcode(det.getFORDERDET_REFNO());
            String GroupCode = new ItemController(getContext()).getItemGroupByCode(det.getFORDERDET_ITEMCODE());

            String Priority1 = dissch.getPriority1Discount(DisItemCode, CustomerCode, ""); // Added Group Code
            String Priority2 = dissch.getPriority1Discount("", GroupCode, CustomerCode); // Added ItemCode
            String Priority3 = dissch.getPriority1Discount(DisItemCode, "", ""); // Added Group Code and Customer Code
            String Priority4 = dissch.getPriority1Discount("", GroupCode, ""); // Added ItemCode and Customer Code

            //BigDecimal DisPer ;
            BigDecimal ItemValue = new BigDecimal(det.getFORDERDET_AMT());
            BigDecimal DiscountValue = new BigDecimal(det.getFORDERDET_DISAMT());
            BigDecimal Prt1_DisPer = new BigDecimal(Priority1);
            BigDecimal Prt2_DisPer = new BigDecimal(Priority2);
            BigDecimal Prt3_DisPer = new BigDecimal(Priority3);
            BigDecimal Prt4_DisPer = new BigDecimal(Priority4);
            BigDecimal zero_Disper = new BigDecimal("0.00");
            BigDecimal hund_Disper = new BigDecimal("100.00");

            if (Prt1_DisPer.compareTo(zero_Disper) > 0) {
                String DisAmount = "";
                OrderDisc DisItmDet = new OrderDisc();
                DisAmount = String.valueOf((((ItemValue.add(DiscountValue)).divide(hund_Disper)).multiply(Prt1_DisPer)).doubleValue());

                DisItmDet.setRefNo(OrdDetRef);
                DisItmDet.setItemCode(DisItemCode);
                DisItmDet.setDisPer(String.valueOf(Prt1_DisPer.doubleValue()));
                DisItmDet.setDisAmt(DisAmount);

                discountList.add(DisItmDet);
                //updateOrdDetDiscount(OrdDetRef,DisItemCode, DisAmount);

            } else if (Prt2_DisPer.compareTo(zero_Disper) > 0) {
                String DisAmount = "";
                OrderDisc DisItmDet = new OrderDisc();
                DisAmount = String.valueOf((((ItemValue.add(DiscountValue)).divide(hund_Disper)).multiply(Prt2_DisPer)).doubleValue());

                DisItmDet.setRefNo(det.getFORDERDET_REFNO());
                DisItmDet.setItemCode(DisItemCode);
                DisItmDet.setDisPer(String.valueOf(Prt2_DisPer.doubleValue()));
                DisItmDet.setDisAmt(DisAmount);

                discountList.add(DisItmDet);

            } else if (Prt3_DisPer.compareTo(zero_Disper) > 0){
                String DisAmount = "";
                OrderDisc DisItmDet = new OrderDisc();
                DisAmount = String.valueOf((((ItemValue.add(DiscountValue)).divide(hund_Disper)).multiply(Prt3_DisPer)).doubleValue());

                DisItmDet.setRefNo(det.getFORDERDET_REFNO());
                DisItmDet.setItemCode(DisItemCode);
                DisItmDet.setDisPer(String.valueOf(Prt3_DisPer.doubleValue()));
                DisItmDet.setDisAmt(DisAmount);

                discountList.add(DisItmDet);

            } else if (Prt4_DisPer.compareTo(zero_Disper) > 0){
                String DisAmount = "";
                OrderDisc DisItmDet = new OrderDisc();
                DisAmount = String.valueOf((((ItemValue.add(DiscountValue)).divide(hund_Disper)).multiply(Prt4_DisPer)).doubleValue());

                DisItmDet.setRefNo(det.getFORDERDET_REFNO());
                DisItmDet.setItemCode(DisItemCode);
                DisItmDet.setDisPer(String.valueOf(Prt4_DisPer.doubleValue()));
                DisItmDet.setDisAmt(DisAmount);

                discountList.add(DisItmDet);

            }


        }

        return discountList;
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            OrderDetailFragment.this.mToggleTextbox();

            Log.d("order_detail", "clicked_count" + clickCount);
        }
    }
}
