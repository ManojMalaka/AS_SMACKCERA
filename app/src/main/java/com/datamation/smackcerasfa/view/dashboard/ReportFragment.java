package com.datamation.smackcerasfa.view.dashboard;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.datamation.smackcerasfa.R;
import com.datamation.smackcerasfa.adapter.OrderAdapter;
import com.datamation.smackcerasfa.controller.CustomerController;
import com.datamation.smackcerasfa.controller.DayExpDetController;
import com.datamation.smackcerasfa.controller.DayExpHedController;
import com.datamation.smackcerasfa.controller.DayNPrdDetController;
import com.datamation.smackcerasfa.controller.DayNPrdHedController;
import com.datamation.smackcerasfa.controller.OrderController;
import com.datamation.smackcerasfa.controller.OrderDetailController;
import com.datamation.smackcerasfa.controller.ReceiptController;
import com.datamation.smackcerasfa.controller.ReportController;
import com.datamation.smackcerasfa.model.DayExpDet;
import com.datamation.smackcerasfa.model.DayExpHed;
import com.datamation.smackcerasfa.model.DayNPrdDet;
import com.datamation.smackcerasfa.model.DayNPrdHed;
import com.datamation.smackcerasfa.model.Order;
import com.datamation.smackcerasfa.model.OrderDetail;
import com.datamation.smackcerasfa.model.ReceiptDet;
import com.datamation.smackcerasfa.model.ReceiptHed;
import com.datamation.smackcerasfa.model.Target;
import com.datamation.smackcerasfa.reports.ExpenseReportAdapter;
import com.datamation.smackcerasfa.reports.ExpenseDaySummaryAdapter;
import com.datamation.smackcerasfa.reports.NonProductiveDaySummaryAdapter;
import com.datamation.smackcerasfa.reports.PreSalesReportAdapter;
import com.datamation.smackcerasfa.reports.TargetVsAchievementAdapter;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/* *************** ** New Moodifications by MMS 2021/01 ** ************************** */
public class ReportFragment extends Fragment {

    View view;
    private Spinner spnOther;
    ExpandableListView expandListView;
    ListView reportDataListview;
    RelativeLayout exNpHeaders;
    RelativeLayout presaleHeaders,presaleHeaders2;
    RelativeLayout filterParams;
    RelativeLayout targetReportHeaders;
    RelativeLayout expenseHeaders;
    Button searchBtn;
    public Calendar Report_Calender;
    public DatePickerDialog datePickerDialogfrom, datePickerDialogTo;
    int year, month, day, fromyear, frommonth, fromday, Toyear, Today, Tomonth;
    public ImageView btnFromDate, btnToDate;
    public TextView fromDate, toDate;

    TransactionDetailsFragment.ExpandablePreListAdapter listPreAdapter;

    NonProductiveDaySummaryAdapter listNPAdapter;
    List<DayNPrdHed> listNPDataHeader;
    HashMap<DayNPrdHed, List<DayNPrdDet>> listNPDataChild;

    ExpenseDaySummaryAdapter listDEAdapter;
    List<DayExpHed> listDEDataHeader;
    HashMap<DayExpHed, List<DayExpDet>> listDEDataChild;

    TargetVsAchievementAdapter targetVsActualAdapter;
    ArrayList<Target> targetVsActuals;

    ExpenseReportAdapter expenseReportAdapter;
    ArrayList<DayExpDet> expenseData;

    PreSalesReportAdapter preSalesSummaryAdapter;
    ArrayList<OrderDetail> presaleData;

    OrderAdapter orderAdapter;
    ArrayList<Order> listOrdaHeader;
    HashMap<Order, List<OrderDetail>> listOrdataChild;

    ExpandableReceiptListAdapter receiptAdapter;
    ArrayList<ReceiptHed> listRecHeader;
    HashMap<ReceiptHed, List<ReceiptDet>> listRecdataChild;

    private NumberFormat numberFormat = NumberFormat.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private long timeInMillis;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Report_Calender = Calendar.getInstance();

        year = Report_Calender.get(Calendar.YEAR);
        month = Report_Calender.get(Calendar.MONTH);
        day = Report_Calender.get(Calendar.DAY_OF_MONTH);
        timeInMillis = System.currentTimeMillis();
        view = inflater.inflate(R.layout.fragment_otherdetail_test, container, false);

        spnOther = (Spinner) view.findViewById(R.id.spnOtherTrans);
        exNpHeaders = (RelativeLayout) view.findViewById(R.id.fragment_expense_np_details_header_container);
        presaleHeaders = (RelativeLayout) view.findViewById(R.id.presale_headers);
        presaleHeaders2 = (RelativeLayout) view.findViewById(R.id.presale_headers2);
        filterParams = (RelativeLayout) view.findViewById(R.id.fragment_invoice_details_rl_filter_params);
        targetReportHeaders = (RelativeLayout) view.findViewById(R.id.fragment_listview_header);
        searchBtn = (Button) view.findViewById(R.id.fragment_report_search_btn);
        expenseHeaders = (RelativeLayout) view.findViewById(R.id.expense_headers);

        //noDataLayout = (LinearLayout)view.findViewById(R.id.no_item_layout);

        ArrayList<String> otherList = new ArrayList<String>();
        otherList.add("Target Vs Achievement");
        otherList.add("Product Wise Summary");
        otherList.add("Expense");
        otherList.add("Pre Sale");
        otherList.add("Receipt");
        otherList.add("Non Productive");


        final ArrayAdapter<String> otherAdapter = new ArrayAdapter<String>(getActivity(), R.layout.reason_spinner_item, otherList);
        otherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnOther.setAdapter(otherAdapter);

        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setGroupingUsed(true);

        expandListView = (ExpandableListView) view.findViewById(R.id.lvExp);
        reportDataListview = (ListView) view.findViewById(R.id.report_listview);
        btnFromDate = (ImageView) view.findViewById(R.id.image_view_date_select_from);
        btnToDate = (ImageView) view.findViewById(R.id.image_view_date_select_to);
        toDate = (TextView) view.findViewById(R.id.fragment_invoice_details_tv_filter_params_date_to);
        fromDate = (TextView) view.findViewById(R.id.fragment_invoice_details_tv_filter_params_date_from);
        toDate.setText(dateFormat.format(new Date(timeInMillis)));
        fromDate.setText(dateFormat.format(new Date(timeInMillis)));
        final int[] prevExpandPosition = {-1};
        //Lisview on group expand listner... to close other expanded headers...
        expandListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int i) {
                if (prevExpandPosition[0] >= 0) {
                    expandListView.collapseGroup(prevExpandPosition[0]);
                }
                prevExpandPosition[0] = i;
            }
        });

        btnFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                datePickerDialogfrom = new DatePickerDialog();
                datePickerDialogfrom.setThemeDark(false);
                datePickerDialogfrom.showYearPickerFirst(false);
                datePickerDialogfrom.setAccentColor(Color.parseColor("#0072BA"));
                datePickerDialogfrom.show(getActivity().getFragmentManager(), "DatePickerDialog");
                datePickerDialogfrom.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        // String date =  "Select Date : " + day + " - " + month + " - " + year;
                        String datesaveFrom = "";
                        if (String.valueOf(monthOfYear + 1).length() < 2 && String.valueOf(dayOfMonth).length() < 2) {
                            datesaveFrom = "" + year + "-" + "0" + (monthOfYear + 1) + "-" + "0" + dayOfMonth;
                        } else {
                            if (String.valueOf(monthOfYear + 1).length() < 2) {
                                datesaveFrom = "" + year + "-" + "0" + (monthOfYear + 1) + "-" + dayOfMonth;
                            } else if (String.valueOf(dayOfMonth).length() < 2) {
                                datesaveFrom = "" + year + "-" + (monthOfYear + 1) + "-" + "0" + dayOfMonth;
                            } else {
                                datesaveFrom = "" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                            }

                        }
                        fromDate.setText("" + datesaveFrom);
                    }
                });

            }
        });

        btnToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialogTo = new DatePickerDialog();
                datePickerDialogTo.setThemeDark(false);
                datePickerDialogTo.showYearPickerFirst(false);
                datePickerDialogTo.setAccentColor(Color.parseColor("#0072BA"));
                datePickerDialogTo.show(getActivity().getFragmentManager(), "DatePickerDialog");
                datePickerDialogTo.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        //String date =  "Select Date : " + day + " - " + month + " - " + year;
                        String datesaveTo = "";
                        if (String.valueOf(monthOfYear + 1).length() < 2 && String.valueOf(dayOfMonth).length() < 2) {
                            datesaveTo = "" + year + "-" + "0" + (monthOfYear + 1) + "-" + "0" + dayOfMonth;
                        } else {
                            if (String.valueOf(monthOfYear + 1).length() < 2) {
                                datesaveTo = "" + year + "-" + "0" + (monthOfYear + 1) + "-" + dayOfMonth;
                            } else if (String.valueOf(dayOfMonth).length() < 2) {
                                datesaveTo = "" + year + "-" + (monthOfYear + 1) + "-" + "0" + dayOfMonth;
                            } else {
                                datesaveTo = "" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                            }
                        }
                        toDate.setText("" + datesaveTo);
                    }
                });

            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spnOther.getSelectedItemPosition() == 0) {//target vs actual
                    prepareTargetVsActual(fromDate.getText().toString().trim(), toDate.getText().toString().trim());
                } else if (spnOther.getSelectedItemPosition() == 1) {
                    preparePreSaleReportData(fromDate.getText().toString().trim(), toDate.getText().toString().trim());
                } else if (spnOther.getSelectedItemPosition() == 2) {
                    prepareExpenseReportData(fromDate.getText().toString().trim(), toDate.getText().toString().trim());
                } else if (spnOther.getSelectedItemPosition() == 3) {
                    preparePreSaleData(fromDate.getText().toString().trim(), toDate.getText().toString().trim());
                } else if (spnOther.getSelectedItemPosition() == 4) {
                    prepareReceiptData(fromDate.getText().toString().trim(), toDate.getText().toString().trim());
                } else {
                    prepareNonProductiveData();
                }
            }
        });

        expandListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                String itemName = listNPDataChild.get(listNPDataHeader.get(groupPosition)).get(childPosition).getNONPRDDET_REASON_CODE();
                Toast.makeText(getActivity(), "You selected : " + itemName, Toast.LENGTH_SHORT).show();
                Log.e("Child", listNPDataChild.get(listNPDataHeader.get(groupPosition)).get(childPosition).getNONPRDDET_REASON_CODE());
                return false;
            }
        });

        spnOther.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                if (position == 5) {

                    reportDataListview.setAdapter(null);
                    filterParams.setVisibility(View.GONE);
                    reportDataListview.setVisibility(View.GONE);
                    targetReportHeaders.setVisibility(View.GONE);
                    presaleHeaders.setVisibility(View.GONE);
                    presaleHeaders2.setVisibility(View.GONE);
                    expenseHeaders.setVisibility(View.GONE);
                    exNpHeaders.setVisibility(View.VISIBLE);
                    expandListView.setVisibility(View.GONE);
                    expandListView.clearTextFilter();
                    prepareNonProductiveData();
                }else if(position == 3 || position == 4){
                    reportDataListview.setAdapter(null);
                    exNpHeaders.setVisibility(View.GONE);
                    expandListView.setVisibility(View.VISIBLE);
                    targetReportHeaders.setVisibility(View.GONE);
                    presaleHeaders.setVisibility(View.GONE);
                    presaleHeaders2.setVisibility(View.VISIBLE);
                    expenseHeaders.setVisibility(View.GONE);
                    filterParams.setVisibility(View.VISIBLE);
                    reportDataListview.setVisibility(View.GONE);
                } else if (position == 2 ) {
                    reportDataListview.setAdapter(null);
                    exNpHeaders.setVisibility(View.GONE);
                    expandListView.setVisibility(View.GONE);
                    targetReportHeaders.setVisibility(View.GONE);
                    presaleHeaders2.setVisibility(View.GONE);
                    presaleHeaders.setVisibility(View.GONE);
                    expenseHeaders.setVisibility(View.VISIBLE);
                    filterParams.setVisibility(View.VISIBLE);
                    reportDataListview.setVisibility(View.VISIBLE);
                } else if (position == 1) {
                    reportDataListview.setAdapter(null);
                    exNpHeaders.setVisibility(View.GONE);
                    expandListView.setVisibility(View.GONE);
                    targetReportHeaders.setVisibility(View.GONE);
                    presaleHeaders2.setVisibility(View.GONE);
                    expenseHeaders.setVisibility(View.GONE);
                    presaleHeaders.setVisibility(View.VISIBLE);
                    filterParams.setVisibility(View.VISIBLE);
                    reportDataListview.setVisibility(View.VISIBLE);
                } else if (position == 0) {//position ==0
                    reportDataListview.setAdapter(null);
                    exNpHeaders.setVisibility(View.GONE);
                    expandListView.setVisibility(View.GONE);
                    presaleHeaders2.setVisibility(View.GONE);
                    presaleHeaders.setVisibility(View.GONE);
                    expenseHeaders.setVisibility(View.GONE);
                    filterParams.setVisibility(View.VISIBLE);
                    targetReportHeaders.setVisibility(View.VISIBLE);
                    reportDataListview.setVisibility(View.VISIBLE);
                } else {
                    Log.d("Cannot be happen", "errrrr");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        return view;
    }

    private void prepareNonProductiveData() {
        listNPDataHeader = new DayNPrdHedController(getActivity()).getTodayNPHeds();

        if (listNPDataHeader.size() == 0) {
            Toast.makeText(getActivity(), "No data to display", Toast.LENGTH_LONG).show();
            //noDataLayout.setVisibility(View.VISIBLE);
        } else {
            listNPDataChild = new HashMap<DayNPrdHed, List<DayNPrdDet>>();

            for (DayNPrdHed free : listNPDataHeader) {
                listNPDataChild.put(free, new DayNPrdDetController(getActivity()).getTodayNPDets(free.getNONPRDHED_REFNO()));
            }

            listNPAdapter = new NonProductiveDaySummaryAdapter(getActivity(), listNPDataHeader, listNPDataChild);
            expandListView.setAdapter(listNPAdapter);
        }

    }

    private void preparePreSaleData(String from,String to) {
        listOrdaHeader = new OrderController(getActivity()).getAllNonActiveOrdHed(from,to);
        if (listOrdaHeader.size() == 0) {
            Toast.makeText(getActivity(), "No data to display", Toast.LENGTH_LONG).show();
        } else {
            listOrdataChild = new HashMap<Order, List<OrderDetail>>();

            for (Order ord : listOrdaHeader) {
                listOrdataChild.put(ord, ord.getOrdDet());
            }

            orderAdapter = new OrderAdapter(getActivity(), listOrdaHeader,listOrdataChild);
            expandListView.setAdapter(orderAdapter);
        }
    }

    private void prepareReceiptData(String from,String to) {
        listRecHeader = new ReceiptController(getActivity()).getAllNonActiveRecHeds(from,to);
        if (listRecHeader.size() == 0) {
            Toast.makeText(getActivity(), "No data to display", Toast.LENGTH_LONG).show();
        } else {
            listRecdataChild = new HashMap<ReceiptHed, List<ReceiptDet>>();

            for (ReceiptHed rec : listRecHeader) {
                listRecdataChild.put(rec, rec.getRecDetList());
            }

            receiptAdapter = new ExpandableReceiptListAdapter(getActivity(), listRecHeader,listRecdataChild);
            expandListView.setAdapter(receiptAdapter);
        }
    }

    private void prepareExpenseData() {
        listDEDataHeader = new DayExpHedController(getActivity()).getTodayDEHeds();

        if (listDEDataHeader.size() == 0) {
            Toast.makeText(getActivity(), "No data to display", Toast.LENGTH_LONG).show();
            //noDataLayout.setVisibility(View.VISIBLE);
        } else {
            listDEDataChild = new HashMap<DayExpHed, List<DayExpDet>>();

            for (DayExpHed free : listDEDataHeader) {
                listDEDataChild.put(free, new DayExpDetController(getActivity()).getTodayDEDets(free.getEXP_REFNO()));
            }

            listDEAdapter = new ExpenseDaySummaryAdapter(getActivity(), listDEDataHeader, listDEDataChild);
            expandListView.setAdapter(listDEAdapter);
        }

    }

    private void prepareTargetVsActual(String from, String to) {
        targetVsActuals = new ReportController(getActivity()).getTargetVsActuals(from, to);
        if (targetVsActuals.size() > 0) {
            targetVsActualAdapter = new TargetVsAchievementAdapter(getActivity(), targetVsActuals);
            reportDataListview.setAdapter(targetVsActualAdapter);
        } else {
            Toast.makeText(getActivity(), "No data to display", Toast.LENGTH_LONG).show();
        }

    }

    private void prepareExpenseReportData(String from, String to) {
        expenseData = new ReportController(getActivity()).getDaExpenseData(from, to);
        if (expenseData.size() > 0) {
            expenseReportAdapter = new ExpenseReportAdapter(getActivity(), expenseData);
            reportDataListview.setAdapter(expenseReportAdapter);
        } else {
            Toast.makeText(getActivity(), "No data to display", Toast.LENGTH_LONG).show();
        }


    }

    private void preparePreSaleReportData(String from, String to) {
        presaleData = new ReportController(getActivity()).getPreSaleData(from, to);
        if (presaleData.size() > 0) {
            preSalesSummaryAdapter = new PreSalesReportAdapter(getActivity(), presaleData);
            reportDataListview.setAdapter(preSalesSummaryAdapter);
        } else {
            Toast.makeText(getActivity(), "No data to display", Toast.LENGTH_LONG).show();
        }


    }

    // adapter for receipt

    public class ExpandableReceiptListAdapter extends BaseExpandableListAdapter {

        private Context _context;
        private List<ReceiptHed> _listDataHeader; // header titles
        // child data in format of header title, child title
        private HashMap<ReceiptHed, List<ReceiptDet>> _listDataChild;

        public ExpandableReceiptListAdapter(Context context, List<ReceiptHed> listDataHeader, HashMap<ReceiptHed, List<ReceiptDet>> listChildData) {
            this._context = context;
            this._listDataHeader = listDataHeader;
            this._listDataChild = listChildData;
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View grpview, ViewGroup parent) {

            final ReceiptDet childText = (ReceiptDet) getChild(groupPosition, childPosition);

            if (grpview == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                grpview = infalInflater.inflate(R.layout.list_items, null);
            }

            TextView txtListChild = (TextView) grpview.findViewById(R.id.itemcode);
            TextView txtListChild1 = (TextView) grpview.findViewById(R.id.qty);
            TextView txtListChild2 = (TextView) grpview.findViewById(R.id.amount);

            txtListChild.setText("RefNo - "+childText.getFPRECDET_REFNO1());
            txtListChild1.setVisibility(View.INVISIBLE);
            txtListChild2.setText("Amount - "+numberFormat.format(Double.parseDouble(childText.getFPRECDET_AMT())));
            return grpview;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this._listDataHeader.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this._listDataHeader.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            final ReceiptHed headerTitle = (ReceiptHed) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.transaction_details_list_group, null);
                //convertView = infalInflater.inflate(R.layout.list_group, null);
            }

            TextView lblListHeader = (TextView) convertView
                    .findViewById(R.id.refno);
            TextView deb = (TextView) convertView.findViewById(R.id.debname);
            //TextView deb = (TextView) convertView.findViewById(R.id.debcode);
            TextView date = (TextView) convertView.findViewById(R.id.date);
            TextView tot = (TextView) convertView.findViewById(R.id.total);
            TextView stats = (TextView) convertView.findViewById(R.id.status);
            TextView delete = (TextView) convertView.findViewById(R.id.type);

            stats.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);

            lblListHeader.setTypeface(null, Typeface.BOLD);
            lblListHeader.setText(headerTitle.getFPRECHED_REFNO());
            CustomerController cus = new CustomerController(_context);
            deb.setText(cus.getSelectedCustomerByCode(headerTitle.getFPRECHED_DEBCODE()).getCusName());
            date.setText(headerTitle.getFPRECHED_ADDDATE());
            tot.setText(headerTitle.getFPRECHED_TOTALAMT());


            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }


    }

}
