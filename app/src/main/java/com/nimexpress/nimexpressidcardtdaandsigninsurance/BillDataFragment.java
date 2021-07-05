package com.nimexpress.nimexpressidcardtdaandsigninsurance;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import androidx.appcompat.app.AlertDialog; import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nimexpress.nimexpressidcardtdaandsigninsurance.model.BillDetailModel;
import com.nimexpress.nimexpressidcardtdaandsigninsurance.model.BillModel;
import com.nimexpress.nimexpressidcardtdaandsigninsurance.service.ConnectionService;
import com.nimexpress.nimexpressidcardtdaandsigninsurance.service.ParameterService;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import info.hoang8f.widget.FButton;

public class BillDataFragment extends Fragment {

    EditText text_receive_bill_no;
    ImageView img_action_sacan_barcode;

    FButton bt_confirm_receive_bill;
    FButton bt_cancel_receive_bill;

    LinearLayout panel_bill_data;

    TextView text_data_bill_no;
    TextView text_data_bill_date;
    TextView text_data_bill_src_name;
    TextView text_data_bill_dest_name;

    TextView text_data_bill_send_name;
    TextView text_data_bill_send_address;
    TextView text_data_send_numtel;

    TextView text_data_bill_rec_name;
    TextView text_data_bill_rec_address;
    TextView text_data_bill_rec_numtel;

    LinearLayout panel_product_item_data;

    String BS_PACKAGE = "com.google.zxing.client.android.SCAN";
    static int Activity_BarCodeScan = 1;

    ProgressDialog progressDialog;

    public static String KEY_RECEIVE_BILL_DATA = "RECEIVE_BILL_DATA";

    BillModel billData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_scan_bill_data, container, false);

        getActivity().setTitle("รับบิลจาก Mobile/Web");

        text_receive_bill_no = (EditText)view.findViewById(R.id.text_receive_bill_no);
        img_action_sacan_barcode = (ImageView)view.findViewById(R.id.img_action_sacan_barcode);

        bt_confirm_receive_bill = (FButton)view.findViewById(R.id.bt_confirm_receive_bill);
        bt_cancel_receive_bill = (FButton)view.findViewById(R.id.bt_cancel_receive_bill);

        panel_bill_data = (LinearLayout)view.findViewById(R.id.panel_bill_data);

        text_data_bill_no = (TextView)view.findViewById(R.id.text_data_bill_no);
        text_data_bill_date = (TextView)view.findViewById(R.id.text_data_bill_date);
        text_data_bill_src_name = (TextView)view.findViewById(R.id.text_data_bill_src_name);
        text_data_bill_dest_name = (TextView)view.findViewById(R.id.text_data_bill_dest_name);

        text_data_bill_send_name = (TextView)view.findViewById(R.id.text_data_bill_send_name);
        text_data_bill_send_address = (TextView)view.findViewById(R.id.text_data_bill_send_address);
        text_data_send_numtel = (TextView)view.findViewById(R.id.text_data_send_numtel);

        text_data_bill_rec_name = (TextView)view.findViewById(R.id.text_data_bill_rec_name);
        text_data_bill_rec_address = (TextView)view.findViewById(R.id.text_data_bill_rec_address);
        text_data_bill_rec_numtel = (TextView)view.findViewById(R.id.text_data_bill_rec_numtel);

        panel_product_item_data = (LinearLayout)view.findViewById(R.id.panel_product_item_data);

        img_action_sacan_barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!findTargetAppPackage()) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setCancelable(false);
                    builder.setMessage(getString(R.string.lb_barcode_app_not_found));
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }else {

                    if (billData == null) {

                        Intent intentBarcodeScanner = new Intent(BS_PACKAGE);
                        intentBarcodeScanner.putExtra("SCAN_FORMATS", "PRODUCT_MODE");
                        startActivityForResult(intentBarcodeScanner, Activity_BarCodeScan);

                    }

                }
            }
        });

        bt_confirm_receive_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String billNo = text_receive_bill_no.getText().toString();

                if(StringUtils.isBlank(billNo)) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setCancelable(false);
                    builder.setMessage(R.string.error_receive_bill_1);
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }else{

                    TaskReceiveBillData taskReceiveBillData = new TaskReceiveBillData();
                    taskReceiveBillData.setBillNo(billNo);
                    taskReceiveBillData.execute();

                }

            }
        });

        bt_cancel_receive_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                billData = null;

                text_receive_bill_no.setText("");
                text_receive_bill_no.setEnabled(true);
                text_receive_bill_no.requestFocus();

                bt_confirm_receive_bill.setEnabled(true);
                bt_confirm_receive_bill.setButtonColor(getResources().getColor(R.color.fbutton_color_peter_river));
                bt_confirm_receive_bill.setShadowColor(getResources().getColor(R.color.fbutton_color_belize_hole));

                panel_bill_data.setVisibility(View.GONE);

            }
        });

        if(billData == null) {

            text_receive_bill_no.setText("");
            panel_bill_data.setVisibility(View.GONE);

        }else{

            text_receive_bill_no.setText(billData.getBill_no());
            panel_bill_data.setVisibility(View.VISIBLE);

            setViewBillData(billData);

        }

        setHasOptionsMenu(true);

        return  view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Activity_BarCodeScan) {

            if(resultCode == getActivity().RESULT_OK) {

                String contents = data.getStringExtra("SCAN_RESULT");
                text_receive_bill_no.setText(contents);

            }

        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_receive_bill, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.menu_action_done) {

            if(billData == null) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(false);
                builder.setMessage(R.string.error_receive_bill_99);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

                return  false;

            }else {

                DroidPrefs.commit(getContext(),KEY_RECEIVE_BILL_DATA,billData);

                ReceiveBillActivity receiveBillActivity = (ReceiveBillActivity) getActivity();

                receiveBillActivity.changeFragment("2",false);

                return true;

            }

        }

        return super.onOptionsItemSelected(item);

    }

    private boolean findTargetAppPackage() {
        String packageStr = "com.google.zxing.client.android";
        Intent intentScan = new Intent(BS_PACKAGE);
        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> availableApps = pm.queryIntentActivities(intentScan, PackageManager.MATCH_DEFAULT_ONLY);
        if (availableApps != null) {
            for (ResolveInfo availableApp : availableApps) {
                String packageName = availableApp.activityInfo.packageName;
                if (packageStr.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setViewBillData(BillModel billData) {

        try {

            if(billData != null) {

                text_receive_bill_no.setEnabled(false);
                bt_confirm_receive_bill.setEnabled(false);
                bt_cancel_receive_bill.setEnabled(true);

                bt_confirm_receive_bill.setButtonColor(getResources().getColor(R.color.fbutton_color_concrete));
                bt_confirm_receive_bill.setShadowColor(getResources().getColor(R.color.fbutton_color_asbestos));

                //set data
                text_data_bill_no.setText(StringUtils.defaultString(billData.getBill_no()));
                text_data_bill_date.setText(StringUtils.defaultString(billData.getBill_date()) + "  " + StringUtils.defaultString(billData.getBill_time()));
                text_data_bill_src_name.setText(StringUtils.defaultString(billData.getSrc_code()) + " " + StringUtils.defaultString(billData.getSrc_name()));
                text_data_bill_dest_name.setText(StringUtils.defaultString(billData.getDest_code()) + " " + StringUtils.defaultString(billData.getDest_name()));

                text_data_bill_send_name.setText(StringUtils.defaultString(billData.getSend_name()) + " / " + StringUtils.defaultString(billData.getSend_company()));
                text_data_bill_send_address.setText(StringUtils.defaultString(billData.getSend_full_address()));
                text_data_send_numtel.setText(StringUtils.defaultString(billData.getSend_numtel()) + "," + StringUtils.defaultString(billData.getSend_mobile()));

                text_data_bill_rec_name.setText(StringUtils.defaultString(billData.getRec_name()) + " / " + StringUtils.defaultString(billData.getRec_company()));
                text_data_bill_rec_name.setText(StringUtils.defaultString(billData.getRec_name()) + " / " + StringUtils.defaultString(billData.getRec_company()));
                text_data_bill_rec_address.setText(StringUtils.defaultString(billData.getRec_full_address()));
                text_data_bill_rec_numtel.setText(StringUtils.defaultString(billData.getRec_numtel()) + "," + StringUtils.defaultString(billData.getRec_mobile()));

                //add detail
                panel_product_item_data.removeAllViews();
                for (BillDetailModel billDetail : billData.getDetailList()) {

                    LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.view_product_item_data, null, false);

                    TextView text_data_bill_product_name = (TextView) view.findViewById(R.id.text_data_bill_product_name);
                    TextView text_data_bill_product_amount = (TextView) view.findViewById(R.id.text_data_bill_product_amount);

                    text_data_bill_product_name.setText(StringUtils.defaultString(billDetail.getProduct_desc()));
                    text_data_bill_product_amount.setText(String.valueOf(Math.round(billDetail.getQty())) + " " + StringUtils.defaultString(billDetail.getUnit()));

                    panel_product_item_data.addView(view);

                }

            }else{

                panel_bill_data.setVisibility(View.GONE);

            }

        }catch (Exception e) {
            e.printStackTrace();
            panel_bill_data.setVisibility(View.GONE);
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

    public class TaskReceiveBillData extends AsyncTask<Void,Void,ResponseService> {

        String billNo;

        public void setBillNo(String billNo) {
            this.billNo = billNo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getContext(), getString(R.string.dialog_loading_data), getString(R.string.dialog_loading_data_message), true, false);
        }

        @Override
        protected ResponseService doInBackground(Void... voids) {

            ResponseService responseService = new ResponseService();

            try {

                List<ParameterService> parameterServiceList = new ArrayList<>();
                parameterServiceList.add(new ParameterService("action","checkBill"));
                parameterServiceList.add(new ParameterService("bill_no",billNo));

                ConnectionService connectionService = new ConnectionService(MainUrl.url+"/MobileServiceFastBill.htm");

                String responseString = connectionService.callService(parameterServiceList);

                responseService = new Gson().fromJson(responseString,ResponseService.class);

            }catch (Exception e) {
                e.printStackTrace();
                responseService.setError(true);
                responseService.setErrorCode("E99");
                responseService.setContentResponse(e.getMessage());
            }

            return  responseService;

        }

        @Override
        protected void onPostExecute(ResponseService responseService) {
            super.onPostExecute(responseService);
            progressDialog.dismiss();

            if(responseService.isError()) {

                if(responseService.getErrorCode().equals("E98") || responseService.getErrorCode().equals("E99")) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setCancelable(false);
                    builder.setMessage(responseService.getContentResponse());
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }else{

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setCancelable(false);

                    if(responseService.getErrorCode().equals("E00")) {
                        builder.setMessage(getString(R.string.error_receive_bill_2));
                    }else if(responseService.getErrorCode().equals("E01") || responseService.getErrorCode().equals("E02")) {
                        builder.setMessage(getString(R.string.error_receive_bill_3));
                    }else if(responseService.getErrorCode().equals("E03")) {
                        builder.setMessage(getString(R.string.error_receive_bill_4));
                    }else if(responseService.getErrorCode().equals("E04")) {
                        builder.setMessage(getString(R.string.error_receive_bill_5));
                    }else{
                        builder.setMessage(responseService.getContentResponse());
                    }

                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }

                panel_bill_data.setVisibility(View.GONE);

            }else{

                panel_bill_data.setVisibility(View.VISIBLE);

                try {

                    billData = new Gson().fromJson(responseService.getJsonStringResponse(),BillModel.class);

                    setViewBillData(billData);

                }catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }

            }

        }
    }

}
