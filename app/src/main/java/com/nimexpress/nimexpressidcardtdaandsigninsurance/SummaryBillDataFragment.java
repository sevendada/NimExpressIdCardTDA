package com.nimexpress.nimexpressidcardtdaandsigninsurance;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nimexpress.nimexpressidcardtdaandsigninsurance.model.BillDetailModel;
import com.nimexpress.nimexpressidcardtdaandsigninsurance.model.BillModel;
import com.nimexpress.nimexpressidcardtdaandsigninsurance.model.IdCardDataTDA;
import com.nimexpress.nimexpressidcardtdaandsigninsurance.model.ImageSignInsurance;
import com.nimexpress.nimexpressidcardtdaandsigninsurance.service.ConnectionService;
import com.nimexpress.nimexpressidcardtdaandsigninsurance.service.ParameterService;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SummaryBillDataFragment extends Fragment {

    ProgressDialog progressDialog;

    TextView text_bill_and_barcode_count;
    TextView text_amount_cod;
    TextView text_amount_cod_fee;
    TextView text_amount_net;
    TextView text_amount_insurance;
    TextView text_amount_tax;
    TextView taxt_amount_total;

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

    BillModel billData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_summary_bill_data, container, false);

        getActivity().setTitle("สรุปข้อมูลการรับบิล Mobile/Web");

        text_bill_and_barcode_count = (TextView)view.findViewById(R.id.text_bill_and_barcode_count);
        text_amount_cod = (TextView)view.findViewById(R.id.text_amount_cod);
        text_amount_cod_fee = (TextView)view.findViewById(R.id.text_amount_cod_fee);
        text_amount_net = (TextView)view.findViewById(R.id.text_amount_net);
        text_amount_insurance = (TextView)view.findViewById(R.id.text_amount_insurance);
        text_amount_tax = (TextView)view.findViewById(R.id.text_amount_tax);
        taxt_amount_total = (TextView)view.findViewById(R.id.taxt_amount_total);

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

        billData = DroidPrefs.get(getContext(),BillDataFragment.KEY_RECEIVE_BILL_DATA,BillModel.class);

        if(billData != null) {

            text_bill_and_barcode_count.setText("1 บิล / " + Math.round(billData.getTotal_qty()) + " บาร์โค้ด");

            DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

            String amountCod = billData.getAmount_mobile_delivery() > 0 ? decimalFormat.format(billData.getAmount_mobile_delivery()) : "0.00";
            String amountCodFee = billData.getAmount_cod_fee() > 0 ? decimalFormat.format(billData.getAmount_cod_fee()) : "0.00";
            String amountNet = decimalFormat.format(billData.getTotal_net() - (billData.getAmount_cod_fee() + billData.getAmount_selfinsurance() + billData.getAmountTax()));
            String amountInsurance = decimalFormat.format(billData.getAmount_selfinsurance());
            String amountTax = decimalFormat.format(billData.getAmountTax());
            String amountTotal = decimalFormat.format(billData.getTotal_net() - (billData.getAmountTax()));

            text_amount_cod.setText(StringUtils.isNotBlank(amountCod) ? amountCod + " บาท" : "-");
            text_amount_cod_fee.setText(StringUtils.isNotBlank(amountCodFee) ? amountCodFee + " บาท" : "-");
            text_amount_net.setText(StringUtils.isNotBlank(amountNet) ? amountNet + " บาท" : "-");
            text_amount_insurance.setText(StringUtils.isNotBlank(amountInsurance) ? amountInsurance + " บาท" : "-");
            text_amount_tax.setText(StringUtils.isNotBlank(amountTax) ? amountTax + " บาท" : "-");
            taxt_amount_total.setText(StringUtils.isNotBlank(amountTotal) ? amountTotal + " บาท" : "-");

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

                LayoutInflater inflater2 = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View viewProduct = inflater2.inflate(R.layout.view_product_item_data, null, false);

                TextView text_data_bill_product_name = (TextView) viewProduct.findViewById(R.id.text_data_bill_product_name);
                TextView text_data_bill_product_amount = (TextView) viewProduct.findViewById(R.id.text_data_bill_product_amount);

                text_data_bill_product_name.setText(StringUtils.defaultString(billDetail.getProduct_desc()));
                text_data_bill_product_amount.setText(String.valueOf(Math.round(billDetail.getQty())) + " " + StringUtils.defaultString(billDetail.getUnit()));

                panel_product_item_data.addView(viewProduct);

            }

        }else {

            panel_bill_data.setVisibility(View.GONE);

        }

        setHasOptionsMenu(true);

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

            TaskBillSaveChangeData taskBillSaveChangeData = new TaskBillSaveChangeData();
            taskBillSaveChangeData.execute();

        }

        return super.onOptionsItemSelected(item);

    }

    public class TaskBillSaveChangeData extends AsyncTask<Void,Void,ResponseService> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getContext(), getString(R.string.dialog_loading_data), getString(R.string.dialog_loading_data_message), true, false);
        }

        @Override
        protected ResponseService doInBackground(Void... voids) {

            ResponseService responseService = new ResponseService();

            try {

                ClassDet classDetMobileKeyCodeDC = DroidPrefs.get(getContext(),SplashScreenViewActivity.key_data_mobilekeycode_dc,ClassDet.class);

                IdCardDataTDA idCardDataTDA = DroidPrefs.get(getContext(),SignInsuranceActivity.key_keep_data_idcard,IdCardDataTDA.class);
                ImageSignInsurance imageSignInsurance = DroidPrefs.get(getContext(),SignInsuranceActivity.key_keep_data_insurance,ImageSignInsurance.class);
                String valueInsurance = DroidPrefs.get(getContext(),ValueInsuranceDataFragment.KEY_VALUE_INSURANCE_DATA,String.class);

                String keepUser = DroidPrefs.get(getContext(),LoginViewActivity.KEY_DATA_LOGIN_USER,String.class);

                List<ParameterService> parameterServiceList = new ArrayList<>();
                parameterServiceList.add(new ParameterService("action","saveChangeDCBill"));
                parameterServiceList.add(new ParameterService("billId",String.valueOf(billData.getId())));
                parameterServiceList.add(new ParameterService("mobile_key_code",DeviceId.getManufacturerSerialNumber()));
                parameterServiceList.add(new ParameterService("dcCode",classDetMobileKeyCodeDC.getSub_class_value()));
                parameterServiceList.add(new ParameterService("usercode",keepUser));
                parameterServiceList.add(new ParameterService("idCardIDA_ID",idCardDataTDA.getId().toString()));
                parameterServiceList.add(new ParameterService("signInsurance_ID",imageSignInsurance.getId().toString()));
                parameterServiceList.add(new ParameterService("insuranceValue",valueInsurance));
                parameterServiceList.add(new ParameterService("billJsonTmp",new Gson().toJson(billData)));

                ConnectionService connectionService = new ConnectionService(MainUrl.url+"/MobileServiceFastBill.htm");

                String responseString = connectionService.callService(parameterServiceList);

                responseService = new Gson().fromJson(responseString,ResponseService.class);

            } catch (Exception e) {
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

                BillModel billDataSaveChange = new Gson().fromJson(responseService.getJsonStringResponse(),BillModel.class);

                DroidPrefs.commit(getContext(),BillDataFragment.KEY_RECEIVE_BILL_DATA,billDataSaveChange);

                //next step
                ReceiveBillActivity receiveBillActivity = (ReceiveBillActivity) getActivity();

                receiveBillActivity.changeFragment("5", false);

            }

        }

    }

}
