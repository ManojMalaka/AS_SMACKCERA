package com.datamation.smackcerasfa.receipt;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.datamation.smackcerasfa.R;
import com.datamation.smackcerasfa.api.ApiCllient;
import com.datamation.smackcerasfa.api.ApiInterface;
import com.datamation.smackcerasfa.controller.OrderController;
import com.datamation.smackcerasfa.controller.ReceiptController;
import com.datamation.smackcerasfa.helpers.NetworkFunctions;
import com.datamation.smackcerasfa.helpers.UploadTaskListener;
import com.datamation.smackcerasfa.model.Debtor;
import com.datamation.smackcerasfa.model.ReceiptHed;
import com.datamation.smackcerasfa.model.SalRep;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadReceipt extends AsyncTask<ArrayList<ReceiptHed>, Integer, ArrayList<ReceiptHed>> {

    Context context;
    ProgressDialog dialog;
    private Handler mHandler;
    UploadTaskListener taskListener;
    NetworkFunctions networkFunctions;
    List<String> resultListPreSale;
    int totalRecords;
    ArrayList<ReceiptHed> fReciptList = new ArrayList<>();

    public static final String SETTINGS = "SETTINGS";
    public static SharedPreferences localSP;

    public UploadReceipt(Context context, UploadTaskListener taskListener, ArrayList<ReceiptHed> recList) {
        resultListPreSale = new ArrayList<>();
        this.context = context;
        this.taskListener = taskListener;
        mHandler = new Handler(Looper.getMainLooper());
        localSP = context.getSharedPreferences(SETTINGS, 0);
        fReciptList.addAll(recList);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.show();
    }

    @Override
    protected ArrayList<ReceiptHed> doInBackground(ArrayList<ReceiptHed>... params) {

        int recordCount = 0;
        publishProgress(recordCount);

        final  ArrayList<ReceiptHed> RCSList = fReciptList;
        totalRecords = RCSList.size();
        networkFunctions = new NetworkFunctions(context);
        final String sp_url = localSP.getString("URL", "").toString();
        String URL = "http://" + sp_url;

        for (final ReceiptHed c : RCSList) {

            try {
                String content_type = "application/json";
                ApiInterface apiInterface = ApiCllient.getClient(context).create(ApiInterface.class);
                JsonParser jsonParser = new JsonParser();
                String recJson = new Gson().toJson(c);
                JsonObject objectFromString = jsonParser.parse(recJson).getAsJsonObject();
                JsonArray jsonArray = new JsonArray();
                jsonArray.add(objectFromString);
                Call<String> resultCall = apiInterface.uploadReceipt(jsonArray, content_type);
                resultCall.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        int status = response.code();
                        Log.d(">>>response code", ">>>res " + status);
                        Log.d(">>>response message", ">>>res " + response.message());

                        String resmsg = "" + response.body().toString();
                        if (status == 200 && !resmsg.equals("") && !resmsg.equals(null)) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    c.setFPRECHED_ISSYNCED("1");
                                    addRefNoResults(c.getFPRECHED_REFNO() +" --> Success\n",RCSList.size());
                                    new ReceiptController(context).updateIsSyncedReceipt(c.getFPRECHED_REFNO(), "1");
                                }
                            });
                        } else {

                            c.setFPRECHED_ISSYNCED("0");
                            addRefNoResults(c.getFPRECHED_REFNO() +" --> Failed\n",RCSList.size());
                            new ReceiptController(context).updateIsSyncedReceipt(c.getFPRECHED_REFNO(), "0");
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(context, "Error response " + t.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            ++recordCount;
            publishProgress(recordCount);
        }

        return RCSList;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        dialog.setMessage("Uploading.. Receipt Record " + values[0] + "/" + totalRecords);
    }

    @Override
    protected void onPostExecute(ArrayList<ReceiptHed> RCSList) {
        super.onPostExecute(RCSList);
        List<String> list = new ArrayList<>();

        if (RCSList.size() > 0) {
            list.add("\nRECEIPT");
            list.add("------------------------------------\n");
        }

        int i = 1;
		for (ReceiptHed c : RCSList) {

			if (c.getFPRECHED_ISSYNCED().equals("1")) {
				list.add(i + ". " + c.getFPRECHED_REFNO()+ " --> Success\n");
			} else {
				list.add(i + ". " + c.getFPRECHED_REFNO() + " --> Failed\n");
			}
			i++;
		}

        dialog.dismiss();
        taskListener.onTaskCompleted(list);
    }

    private void addRefNoResults(String ref, int count) {
        resultListPreSale.add(ref);
        if(count == resultListPreSale.size()) {
            mUploadResult(resultListPreSale);
        }
    }

    public void mUploadResult(List<String> messages) {
        String msg = "";
        for (String s : messages) {
            msg += s;
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(msg);
        alertDialogBuilder.setTitle("Upload Receipt Summary");

        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
        alertD.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

}


