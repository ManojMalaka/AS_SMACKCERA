package com.datamation.smackcerasfa.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;


import com.datamation.smackcerasfa.R;

import com.datamation.smackcerasfa.model.Order;
import com.datamation.smackcerasfa.model.OrderDetail;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class OrderAdapter extends BaseExpandableListAdapter {
    Context _context;
    private ArrayList<Order> _listDataHeader; // header titles
    private HashMap<Order, List<OrderDetail>> _listDataChild;
    private NumberFormat numberFormat = NumberFormat.getInstance();

    public OrderAdapter(Context context, ArrayList<Order> listOrdHed, HashMap<Order, List<OrderDetail>> listOrdDet) {

        this._context = context;
        this._listDataHeader = listOrdHed;
        this._listDataChild = listOrdDet;

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

        final OrderDetail childText = (OrderDetail) getChild(groupPosition, childPosition);

        if (grpview == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            grpview = infalInflater.inflate(R.layout.list_items, null);
        }

        TextView txtListChild = (TextView) grpview.findViewById(R.id.itemcode);
        TextView txtListChild1 = (TextView) grpview.findViewById(R.id.qty);
        TextView txtListChild2 = (TextView) grpview.findViewById(R.id.amount);

        txtListChild.setText("ItemCode - "+childText.getFORDERDET_ITEMCODE());
        txtListChild1.setText("Qty - "+childText.getFORDERDET_QTY());
        txtListChild2.setText("Amount - "+numberFormat.format(Double.parseDouble(childText.getFORDERDET_AMT())));
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
        final Order headerTitle = (Order) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.transaction_details_list_group, null);
        }

        TextView refno = (TextView) convertView.findViewById(R.id.refno);
        TextView deb = (TextView) convertView.findViewById(R.id.debname);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView tot = (TextView) convertView.findViewById(R.id.total);
        TextView stats = (TextView) convertView.findViewById(R.id.status);
        TextView delete = (TextView) convertView.findViewById(R.id.type);
        refno.setTypeface(null, Typeface.BOLD);

        refno.setText(headerTitle.getORDER_REFNO());
        deb.setText(headerTitle.getORDER_DEBNAME());
        date.setText(headerTitle.getORDER_TXNDATE());
        tot.setText(headerTitle.getORDER_TOTALAMT());

        stats.setVisibility(View.GONE);
        delete.setVisibility(View.GONE);

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

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        double grossTotal = 0;

        List<Order> searchingDetails = _listDataHeader;

        for (Order invoice : searchingDetails) {

            if (invoice != null) {
                grossTotal += Double.parseDouble(invoice.getORDER_TOTALAMT());

            }
        }

    }
}
