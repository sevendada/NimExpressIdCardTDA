package com.nimexpress.nimexpressidcardtdaandsigninsurance;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nimexpress.nimexpressidcardtdaandsigninsurance.model.BarcodeModel;
import com.nimexpress.nimexpressidcardtdaandsigninsurance.model.BillModel;
import com.nimexpress.nimexpressidcardtdaandsigninsurance.model.BoxNimExpress;
import com.nimexpress.nimexpressidcardtdaandsigninsurance.model.ImageSignInsurance;
import com.nimexpress.nimexpressidcardtdaandsigninsurance.service.ConnectionService;
import com.nimexpress.nimexpressidcardtdaandsigninsurance.service.ParameterService;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BarcodeInputDataFragment extends Fragment implements BarcodeInputDataAdapter.EventAdapterListener {

    ProgressDialog progressDialog;

    RecyclerView recyclerView;

    BillModel billData;

    List<BarcodeModel> barcodeModelList = new ArrayList<>();

    public static String KEY_RECEIVE_ฺBILL_BARCODE_DATA = "RECEIVE_ฺBILL_BARCODE_DATA";

    String BS_PACKAGE = "com.google.zxing.client.android.SCAN";
    static int Activity_BarCodeScan = 1;

    int postionScanBarcodeBox = 0;

    BarcodeInputDataAdapter barcodeInputDataAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_barcode_input_data, container, false);

        billData = DroidPrefs.get(getContext(),BillDataFragment.KEY_RECEIVE_BILL_DATA,BillModel.class);

        getActivity().setTitle("ชั่งน้ำหนักและวัดขนาดสินค้า");

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        setHasOptionsMenu(true);

        TaskBarcodeDataList taskBarcodeDataList = new TaskBarcodeDataList();
        taskBarcodeDataList.setBillId(billData.getId());
        taskBarcodeDataList.setEventAdapterListener(this);
        taskBarcodeDataList.execute();

        return view;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_receive_bill, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_action_done) {

            DroidPrefs.commit(getContext(),KEY_RECEIVE_ฺBILL_BARCODE_DATA,barcodeModelList);

            boolean isValidatePass = true;

            int indexPosition = 1;
            for (BarcodeModel barcode : barcodeModelList) {

                    boolean isCompleteVolume = true;

                    if(barcode.getSize_width() <= 0) {
                        isCompleteVolume = false;
                    }else if(barcode.getSize_long() <= 0) {
                        isCompleteVolume = false;
                    }else if(barcode.getSize_height() <= 0) {
                        isCompleteVolume = false;
                    }else if(barcode.getWeight_kg() <= 0) {
                        isCompleteVolume = false;
                    }

                    if(StringUtils.defaultString(billData.getBill_type()).contains("CHILLED") ||
                            StringUtils.defaultString(billData.getBill_type()).contains("FROZEN")) {

                        if(StringUtils.isBlank(barcode.getTemperature())) {
                            isCompleteVolume = false;
                        }

                    }

                    if(!isCompleteVolume) {

                        isValidatePass = false;

                        if(StringUtils.defaultString(billData.getBill_type()).contains("CHILLED") ||
                                StringUtils.defaultString(billData.getBill_type()).contains("FROZEN")) {

                            //???
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setCancelable(false);
                            builder.setMessage(getString(R.string.error_receive_barcode_1,String.valueOf(indexPosition)));
                            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();

                        }else{

                            //???
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setCancelable(false);
                            builder.setMessage(getString(R.string.error_receive_barcode_2,String.valueOf(indexPosition)));
                            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();

                        }

                        break;
                    }

                indexPosition++;

            }

            ////////
            if(isValidatePass) {

                TaskBillCalPrice taskBillCalPrice = new TaskBillCalPrice();
                taskBillCalPrice.execute();

            }


        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onEvent(BarcodeModel barcodeModel,int position) {

        postionScanBarcodeBox = position;

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

            Intent intentBarcodeScanner = new Intent(BS_PACKAGE);
            intentBarcodeScanner.putExtra("SCAN_FORMATS", "PRODUCT_MODE");
            startActivityForResult(intentBarcodeScanner, Activity_BarCodeScan);

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Activity_BarCodeScan) {

            if(resultCode == getActivity().RESULT_OK) {

                String contents = data.getStringExtra("SCAN_RESULT");
                System.out.println(contents);

                ClassDet classDetMobileKeyCodeDC = DroidPrefs.get(getContext(),SplashScreenViewActivity.key_data_mobilekeycode_dc,ClassDet.class);

                TaskBoxNimExpress taskBoxNimExpress = new TaskBoxNimExpress();
                taskBoxNimExpress.setBarcodeBox(contents);
                taskBoxNimExpress.setDcCode(classDetMobileKeyCodeDC.getSub_class_value());
                taskBoxNimExpress.execute();

            }

        }

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

    public class TaskBillCalPrice extends AsyncTask<Void,Void,ResponseService> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getContext(), getString(R.string.dialog_loading_data), getString(R.string.dialog_loading_data_message), true, false);
        }

        @Override
        protected ResponseService doInBackground(Void... voids) {

            ResponseService responseService = new ResponseService();

            try {

                BillModel billData = DroidPrefs.get(getContext(),BillDataFragment.KEY_RECEIVE_BILL_DATA,BillModel.class);

                ImageSignInsurance imageSignInsurance = DroidPrefs.get(getContext(),SignInsuranceActivity.key_keep_data_insurance,ImageSignInsurance.class);
                String valueInsurance = DroidPrefs.get(getContext(),ValueInsuranceDataFragment.KEY_VALUE_INSURANCE_DATA,String.class);

                String keepUser = DroidPrefs.get(getContext(),LoginViewActivity.KEY_DATA_LOGIN_USER,String.class);

                ClassDet classDetMobileKeyCodeDC = DroidPrefs.get(getContext(),SplashScreenViewActivity.key_data_mobilekeycode_dc,ClassDet.class);

                List<ParameterService> parameterServiceList = new ArrayList<>();
                parameterServiceList.add(new ParameterService("action","recalPrice"));
                parameterServiceList.add(new ParameterService("billId",String.valueOf(billData.getId())));
                parameterServiceList.add(new ParameterService("mobile_key_code",DeviceId.getManufacturerSerialNumber()));
                parameterServiceList.add(new ParameterService("dcCode",classDetMobileKeyCodeDC.getSub_class_value()));
                parameterServiceList.add(new ParameterService("usercode",keepUser));
                parameterServiceList.add(new ParameterService("jsonString",new Gson().toJson(barcodeModelList)));
                parameterServiceList.add(new ParameterService("signInsurance_ID",imageSignInsurance.getId().toString()));
                parameterServiceList.add(new ParameterService("insuranceValue",valueInsurance));

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

            if (responseService.isError()) {

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

            } else {

                BillModel billDataRecal = new Gson().fromJson(responseService.getJsonStringResponse(),BillModel.class);

                BillModel billData = DroidPrefs.get(getContext(),BillDataFragment.KEY_RECEIVE_BILL_DATA,BillModel.class);

                billData.setTotal_net(billDataRecal.getTotal_net());
                billData.setAmount_mobile_delivery(billDataRecal.getAmount_mobile_delivery());
                billData.setAmount_cod_fee(billDataRecal.getAmount_cod_fee());
                billData.setAmount_selfinsurance(billDataRecal.getAmount_selfinsurance());

                DroidPrefs.commit(getContext(),BillDataFragment.KEY_RECEIVE_BILL_DATA,billData);

                //next step
                ReceiveBillActivity receiveBillActivity = (ReceiveBillActivity) getActivity();

                receiveBillActivity.changeFragment("3", false);

            }

        }
    }

    public class TaskBoxNimExpress extends AsyncTask<Void,Void,ResponseService> {

        String barcodeBox;
        String dcCode;

        public void setBarcodeBox(String barcodeBox) {
            this.barcodeBox = barcodeBox;
        }

        public void setDcCode(String dcCode) {
            this.dcCode = dcCode;
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
                parameterServiceList.add(new ParameterService("action","validBarcodeBox"));
                parameterServiceList.add(new ParameterService("barcode_box",barcodeBox));
                parameterServiceList.add(new ParameterService("dc_code",dcCode));

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

            if (responseService.isError()) {

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

            } else {

                try {

                    BoxNimExpress boxNimExpress = new Gson().fromJson(responseService.getJsonStringResponse(),BoxNimExpress.class);

                    if(boxNimExpress != null) {

                        //if(boxNimExpress.isFlag_use()) {

                            BarcodeModel barcodeData = barcodeModelList.get(postionScanBarcodeBox);
                            barcodeData.setBox_nimexpress_bc(boxNimExpress.getBarcode());
                            barcodeData.setBox_product_id(boxNimExpress.getProduct_id());
                            barcodeData.setBox_product_code(boxNimExpress.getProduct_code());
                            barcodeData.setSize_width(Double.parseDouble(boxNimExpress.getSize_width()));
                            barcodeData.setSize_long(Double.parseDouble(boxNimExpress.getSize_long()));
                            barcodeData.setSize_height(Double.parseDouble(boxNimExpress.getSize_height()));

                            barcodeInputDataAdapter.notifyDataSetChanged();

                        //}

                    }

                }catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }

    }

    public class TaskBarcodeDataList extends AsyncTask<Void,Void,ResponseService> {

        Long billId;

        BarcodeInputDataAdapter.EventAdapterListener eventAdapterListener;

        public void setBillId(Long billId) {
            this.billId = billId;
        }

        public void setEventAdapterListener(BarcodeInputDataAdapter.EventAdapterListener eventAdapterListener) {
            this.eventAdapterListener = eventAdapterListener;
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
                parameterServiceList.add(new ParameterService("action","listBarcode"));
                parameterServiceList.add(new ParameterService("billId",billId.toString()));

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

            if (responseService.isError()) {

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

                barcodeModelList = new Gson().fromJson(responseService.getJsonStringResponse(),new TypeToken<List<BarcodeModel>>(){}.getType());

                barcodeInputDataAdapter = new BarcodeInputDataAdapter();
                barcodeInputDataAdapter.setBarcodeModelList(barcodeModelList);
                barcodeInputDataAdapter.setEventAdapterListener(eventAdapterListener);
                barcodeInputDataAdapter.setDataBilLTpye(billData.getBill_type());
                recyclerView.setAdapter(barcodeInputDataAdapter);

            }

        }

    }

}
