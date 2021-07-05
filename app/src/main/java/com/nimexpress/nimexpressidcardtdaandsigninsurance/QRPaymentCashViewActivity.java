package com.nimexpress.nimexpressidcardtdaandsigninsurance;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.nimexpress.nimexpressidcardtdaandsigninsurance.model.QRPaymentRunningData;
import com.nimexpress.nimexpressidcardtdaandsigninsurance.model.QRPaymentTransaction;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.AppCompatActivity;

public class QRPaymentCashViewActivity extends AppCompatActivity {

    Context context = this;

    ImageView image_qr_code_view;
    Button button_cancel_check_qrpayment;

    ProgressDialog progressDialog;

    Timer timer;
    TimerTask timerTask;

    public static String keyQRPaymentRunningData = "QRPaymentRunningData";
    public static String keyQRPaymentTransactionCash = "QRPaymentTransactionCash";

    CheckQRPaymentTask checkQRPaymentTask = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setFinishOnTouchOutside(false);

        setContentView(R.layout.activity_image_qrpayment_cash_view);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;

        this.getWindow().setAttributes(params);

        image_qr_code_view = (ImageView)findViewById(R.id.image_qr_code_view);
        button_cancel_check_qrpayment = (Button)findViewById(R.id.button_cancel_check_qrpayment);

        button_cancel_check_qrpayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timerTask.cancel();
                timer.cancel();
                timer = null;
                finish();
            }
        });

        DownloadImageTask downloadImageTask = new DownloadImageTask();
        downloadImageTask.setBmImage(image_qr_code_view);
        downloadImageTask.execute();

    }

    @Override
    public void onBackPressed() {

    }

    private class CheckQRPaymentTask extends AsyncTask<Void,Void,ResponseService> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ResponseService doInBackground(Void... voids) {

            try {

                QRPaymentRunningData qrPaymentRunningData = DroidPrefs.get(context,keyQRPaymentRunningData,QRPaymentRunningData
                        .class);

                URL url = new URL(MainUrl.url+"/CheckQRPaymentTransactionCash.htm?action=checkQRPaymentTransaction&bill_id="+qrPaymentRunningData.getBill_id()+"&bill_no="+StringUtils.defaultString(qrPaymentRunningData.getBill_no()) +"&dc_code="+qrPaymentRunningData.getData_dc()+"&qrpaymentRunningId="+qrPaymentRunningData.getId()+"&qrpaymentType="+qrPaymentRunningData.getTmp_qrpayment_type()+"&qrpaymentTypeCode="+qrPaymentRunningData.getTmp_qrpayment_type_code()+"&mobileKeyCode="+DeviceId.getManufacturerSerialNumber());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                StringBuilder textBuilder = new StringBuilder();
                try {
                    Reader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    int c = 0;
                    while ((c = reader.read()) != -1) {
                        textBuilder.append((char) c);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }

                String returnData = textBuilder.toString();

                ResponseService responseService = new Gson().fromJson(returnData,ResponseService.class);

                return  responseService;

            }catch (Exception e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(ResponseService responseService) {
            super.onPostExecute(responseService);

            isPass(responseService);

        }

    }

    public void isPass(ResponseService responseService) {
        if(!responseService.isError()) {
            timerTask.cancel();
            timer.cancel();
            timer = null;
            DroidPrefs.commit(context,keyQRPaymentTransactionCash, new Gson().fromJson(responseService.getJsonStringResponse(), QRPaymentTransaction.class));
            setResult(RESULT_OK);
            finish();
        }
    }

    private class DownloadImageTask extends AsyncTask<Void, Void, Bitmap> {

        ImageView bmImage;

        public void setBmImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, getResources().getString(R.string.messageWaiting), getResources().getString(R.string.Preloading), true);
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {

            Bitmap imageData = null;
            try {

                QRPaymentRunningData qrPaymentRunningData = DroidPrefs.get(context,keyQRPaymentRunningData,QRPaymentRunningData.class);

                InputStream in = new java.net.URL(MainUrl.url + "/PrintQRPayment.htm?type="+qrPaymentRunningData.getTmp_qrpayment_type()+"&bill_no=" + qrPaymentRunningData.getBill_no() + "&dcCode=" + qrPaymentRunningData.getData_dc() + "&mobileKeyCode=" + DeviceId.getManufacturerSerialNumber()+"&qrPaymentRunningDataId="+qrPaymentRunningData.getId()).openStream();
                imageData = BitmapFactory.decodeStream(in);

                return imageData;

            }catch (Exception e) {
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            bmImage.setImageBitmap(result);

            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("RUN CHECK QRPAYMENT");
                            checkQRPaymentTask = new CheckQRPaymentTask();
                            checkQRPaymentTask.execute();
                        }
                    });
                }
            };
            timer.schedule(timerTask,0,2000);

        }

    }

}
