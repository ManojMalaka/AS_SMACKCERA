package com.datamation.smackcerasfa.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.datamation.smackcerasfa.R;
import com.datamation.smackcerasfa.model.Order;

import java.util.ArrayList;


public class OrderAdapter extends ArrayAdapter<Order> {
    Context context;
    ArrayList<Order> list;


    public OrderAdapter(Context context, ArrayList<Order> list) {
        super(context, R.layout.row_cus_view, list);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = null;
        View row = null;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        row = inflater.inflate(R.layout.row_cus_view, parent, false);

        TextView refNoTxt = (TextView) row.findViewById(R.id.row_refno);
        TextView cusCodeTxt = (TextView) row.findViewById(R.id.row_cus_name);
        TextView addDateTxt = (TextView) row.findViewById(R.id.addeddate);
        TextView amountTxt = (TextView) row.findViewById(R.id.addeddate);


    //    list = new OrderController(context).getAllOrders();

//       if(list.get(position).getORDHED_IS_SYNCED().equalsIgnoreCase("1")){
//           sts.setBackgroundResource(R.drawable.status_synced);// synced
//       }else{
//           sts.setBackgroundResource(R.drawable.status_line);//not synced
//       }
//
        for(Order ord : list){
            refNoTxt.setText(ord.getORDER_REFNO());
            cusCodeTxt.setText(ord.getORDER_DEBNAME());
            addDateTxt.setText(ord.getORDER_ADDDATE());
            amountTxt.setText(ord.getORDER_TOTALAMT());

        }

        return row;
    }
}
