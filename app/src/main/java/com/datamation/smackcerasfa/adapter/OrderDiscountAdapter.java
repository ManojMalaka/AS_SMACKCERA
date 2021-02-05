package com.datamation.smackcerasfa.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.datamation.smackcerasfa.R;
import com.datamation.smackcerasfa.controller.FreeHedController;
import com.datamation.smackcerasfa.controller.ItemController;
import com.datamation.smackcerasfa.controller.TaxDetController;
import com.datamation.smackcerasfa.model.FreeHed;
import com.datamation.smackcerasfa.model.OrderDetail;
import com.datamation.smackcerasfa.model.OrderDisc;

import java.util.ArrayList;
/***@Auther - Manoj**/
/* *************** ** Newly created by MMS 2021/02/05 ** ************************** */

public class OrderDiscountAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    ArrayList<OrderDisc> list;
    ArrayList<FreeHed> arrayList;
    Context context;
    String debCode;

    public OrderDiscountAdapter(Context context, ArrayList<OrderDisc> list, String debCode){
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
        this.debCode = debCode;
    }
    @Override
    public int getCount() {
        if (list != null) return list.size();
        return 0;
    }
    @Override
    public OrderDisc getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.row_order_details, parent, false);
            viewHolder.lblItem = (TextView) convertView.findViewById(R.id.row_item);
            viewHolder.lblQty = (TextView) convertView.findViewById(R.id.row_cases);
            viewHolder.lblCase = (TextView) convertView.findViewById(R.id.row_pieces);
            viewHolder.lblAMt = (TextView) convertView.findViewById(R.id.row_piece);
            viewHolder.showStatus = (TextView) convertView.findViewById(R.id.row_free_status);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.lblItem.setText(list.get(position).getItemCode() + " - " + new ItemController(convertView.getContext()).getItemNameByCode(list.get(position).getItemCode()));

        String sArray[] = new TaxDetController(context).calculateTaxForwardFromDebTax(debCode, list.get(position).getItemCode(), Double.parseDouble(list.get(position).getDisAmt()));
        String amt = String.format("%.2f", Double.parseDouble(sArray[0]));
        viewHolder.lblAMt.setText(amt);

        return convertView;
    }
    private  static  class  ViewHolder{
        TextView lblItem;
        TextView lblQty;
        TextView lblCase;
        TextView lblAMt;
        TextView showStatus;

    }

}
