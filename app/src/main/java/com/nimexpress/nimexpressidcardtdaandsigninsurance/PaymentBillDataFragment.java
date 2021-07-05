package com.nimexpress.nimexpressidcardtdaandsigninsurance;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import androidx.appcompat.app.AlertDialog; import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nimexpress.nimexpressidcardtdaandsigninsurance.model.BillModel;
import com.nimexpress.nimexpressidcardtdaandsigninsurance.model.QRPaymentRunningData;
import com.nimexpress.nimexpressidcardtdaandsigninsurance.service.ConnectionService;
import com.nimexpress.nimexpressidcardtdaandsigninsurance.service.ParameterService;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PaymentBillDataFragment extends Fragment {

    ProgressDialog progressDialog;

    TextView text_bill_no_data;
    TextView text_bill_and_barcode_count;
    TextView text_amount_cod;
    TextView text_amount_cod_fee;
    TextView text_amount_net;
    TextView text_amount_insurance;
    TextView text_amount_tax;
    TextView taxt_amount_total;

    TextView text_cash_price;
    TextView text_qr_payment_price;

    LinearLayout button_pay_cash;
    LinearLayout button_pay_qr_payment;

    BillModel billData;

    int requestActivityQRPayment = 1;
    int requestActivityQRPaymentResult = 2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_payment_bill_data, container, false);

        getActivity().setTitle("ข้อมูลการชำระเงิน");

        text_bill_no_data = (TextView)view.findViewById(R.id.text_bill_no_data);
        text_bill_and_barcode_count = (TextView)view.findViewById(R.id.text_bill_and_barcode_count);
        text_amount_cod = (TextView)view.findViewById(R.id.text_amount_cod);
        text_amount_cod_fee = (TextView)view.findViewById(R.id.text_amount_cod_fee);
        text_amount_net = (TextView)view.findViewById(R.id.text_amount_net);
        text_amount_insurance = (TextView)view.findViewById(R.id.text_amount_insurance);
        text_amount_tax = (TextView)view.findViewById(R.id.text_amount_tax);
        taxt_amount_total = (TextView)view.findViewById(R.id.taxt_amount_total);

        text_cash_price = (TextView)view.findViewById(R.id.text_cash_price);
        text_qr_payment_price = (TextView)view.findViewById(R.id.text_qr_payment_price);

        button_pay_cash = (LinearLayout)view.findViewById(R.id.button_pay_cash);
        button_pay_qr_payment = (LinearLayout)view.findViewById(R.id.button_pay_qr_payment);

        billData = DroidPrefs.get(getContext(),BillDataFragment.KEY_RECEIVE_BILL_DATA,BillModel.class);

        if(billData != null) {

            text_bill_no_data.setText(StringUtils.defaultString(billData.getBill_no()));

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

            text_cash_price.setText(amountTotal + " บาท");
            text_qr_payment_price.setText(amountTotal + " บาท");

            button_pay_cash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    TaskSaveBillStatusEmptyTo00 taskSaveBillStatusEmptyTo00 = new TaskSaveBillStatusEmptyTo00();
                    taskSaveBillStatusEmptyTo00.execute();

                }
            });

            button_pay_qr_payment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    TaskCheckQRPaymentDataRunning taskCheckQRPaymentDataRunning = new TaskCheckQRPaymentDataRunning();
                    taskCheckQRPaymentDataRunning.execute();

                }
            });

        }

        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == requestActivityQRPayment) {

            if(resultCode == getActivity().RESULT_OK) {

                Intent intent = new Intent(getContext(),QRpaymentResultViewActivity.class);
                startActivityForResult(intent,requestActivityQRPaymentResult);

            }

        }else if(requestCode == requestActivityQRPaymentResult) {

            if(resultCode == getActivity().RESULT_OK) {

                TaskSaveBillStatusEmptyTo00 taskSaveBillStatusEmptyTo00 = new TaskSaveBillStatusEmptyTo00();
                taskSaveBillStatusEmptyTo00.execute();

            }

        }

    }

    public class TaskCheckQRPaymentDataRunning extends AsyncTask<Void,Void,ResponseService> {

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

                List<ParameterService> parameterServiceList = new ArrayList<>();
                parameterServiceList.add(new ParameterService("action","checkQRPaymentDataRunning"));
                parameterServiceList.add(new ParameterService("billId",String.valueOf(billData.getId())));
                parameterServiceList.add(new ParameterService("mobile_key_code",DeviceId.getManufacturerSerialNumber()));
                parameterServiceList.add(new ParameterService("dcCode",classDetMobileKeyCodeDC.getSub_class_value()));

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

                QRPaymentRunningData qrPaymentRunningData = new Gson().fromJson(responseService.getJsonStringResponse(),QRPaymentRunningData.class);

                DroidPrefs.commit(getContext(),QRPaymentCashViewActivity.keyQRPaymentRunningData,qrPaymentRunningData);

                Intent intentQRPaymentView = new Intent(getContext(),QRPaymentCashViewActivity.class);
                startActivityForResult(intentQRPaymentView,requestActivityQRPayment);

            }

        }

    }

    public class TaskSaveBillStatusEmptyTo00 extends AsyncTask<Void,Void,ResponseService> {

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
                parameterServiceList.add(new ParameterService("action","billStatusEmptyTo00"));
                parameterServiceList.add(new ParameterService("billId",String.valueOf(billData.getId())));

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

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(false);
                builder.setMessage("บันทึกข้อมูลเรียบร้อยแล้ว");
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();

                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().finish();

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }

        }

    }

}
