package com.datamation.smackcerasfa.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.datamation.smackcerasfa.helpers.DatabaseHelper;
import com.datamation.smackcerasfa.model.Bank;
import com.datamation.smackcerasfa.model.FSDisc;

import java.util.ArrayList;

public class FSDiscController {
    //Created by ************* MMS - 2021/01/12 **********
    Context context;
    private SQLiteDatabase dB;
    private DatabaseHelper dbHelper;
    private String TAG = "FSDisc ";

    // table
    public static final String TABLE_FSDISC = "FSDisc";
    // table attributes
    public static final String FSDISC_ID = "disID";
    public static final String FSDISC_ADD_DATE = "AddDate";
    public static final String FSDISC_DEB_CODE = "DebCode";
    public static final String FSDISC_DIS_PER = "DisPer";
    public static final String FSDISC_GROUP_CODE = "GroupCode";
    public static final String FSDISC_ITEM_CODE = "ItemCode";
    public static final String FSDISC_PRIORITY = "Priority";


    // create String
    public static final String CREATE_FSDISC_TABLE = "CREATE  TABLE IF NOT EXISTS " + TABLE_FSDISC + " (" + FSDISC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FSDISC_ADD_DATE + " TEXT, " + FSDISC_DEB_CODE + " TEXT, " + FSDISC_DIS_PER + " TEXT, " + FSDISC_GROUP_CODE + " TEXT, " + FSDISC_ITEM_CODE + " TEXT, " + FSDISC_PRIORITY + " TEXT); ";



    public FSDiscController(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        dB = dbHelper.getWritableDatabase();
    }

    public int createOrUpdateFSDisc(ArrayList<FSDisc> list) {

        int count = 0;

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        try {

            for (FSDisc fsDisc : list) {

                Cursor cursor = dB.rawQuery("SELECT * FROM " + TABLE_FSDISC + " WHERE " + FSDISC_ID + "='" + fsDisc.getFSDISC_ID() + "'", null);

                ContentValues values = new ContentValues();

                values.put(FSDISC_ADD_DATE, fsDisc.getFSDISC_ADD_DATE());
                values.put(FSDISC_DEB_CODE, fsDisc.getFSDISC_DEB_CODE());
                values.put(FSDISC_DIS_PER, fsDisc.getFSDISC_DIS_PER());
                values.put(FSDISC_GROUP_CODE, fsDisc.getFSDISC_GROUP_CODE());
                values.put(FSDISC_ITEM_CODE, fsDisc.getFSDISC_ITEM_CODE());
                values.put(FSDISC_PRIORITY, fsDisc.getFSDISC_PRIORITY());


                if (cursor.getCount() > 0) {
                    dB.update(TABLE_FSDISC, values, FSDISC_ID + "=?", new String[]{fsDisc.getFSDISC_ID() .toString()});
                    Log.v(TAG, "Updated");
                } else {
                    count = (int) dB.insert(TABLE_FSDISC, null, values);
                    Log.v(TAG, "Inserted " + count);
                }
                cursor.close();
            }

        } catch (Exception e) {
            Log.v(TAG, e.toString());
        } finally {
            dB.close();
        }
        return count;

    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public int deleteAll() {

        int count = 0;

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cursor = null;
        try {

            cursor = dB.rawQuery("SELECT * FROM " + TABLE_FSDISC, null);
            count = cursor.getCount();
            if (count > 0) {
                int success = dB.delete(TABLE_FSDISC, null, null);
                Log.v("Success", success + "");
            }
        } catch (Exception e) {

            Log.v(TAG + " Exception", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return count;

    }

    public String getPriority1Discount(String ItemCode, String CustomerCode, String GroupCode) {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        Cursor cursor = null;

        try {
            String selectQuery = "SELECT * FROM " +TABLE_FSDISC + " WHERE " + FSDISC_ITEM_CODE + "='" + ItemCode + "' AND " + FSDISC_DEB_CODE + "='" + CustomerCode+"' AND " + FSDISC_GROUP_CODE + "='"+GroupCode+"'";

            cursor = dB.rawQuery(selectQuery, null);

            while (cursor.moveToNext()) {

                return cursor.getString(cursor.getColumnIndex(FSDISC_DIS_PER));

            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return "0.00";

    }



}
