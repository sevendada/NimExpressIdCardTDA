package com.nimexpress.nimexpressidcardtdaandsigninsurance;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.nimexpress.nimexpressidcardtdaandsigninsurance.model.QRPaymentRunningData;
import com.nimexpress.nimexpressidcardtdaandsigninsurance.model.QRPaymentTransaction;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import androidx.appcompat.app.AppCompatActivity;

public class QRpaymentResultViewActivity extends AppCompatActivity {

    Context context = this;

    ImageView image_result_view;
    Button button_close;
    Button button_share;

    ProgressDialog progressDialog;

    String filename = "imageResult.png";

    QRPaymentTransaction qrPaymentTransaction = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setFinishOnTouchOutside(false);

        setContentView(R.layout.activity_image_result_view);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;

        this.getWindow().setAttributes(params);

        image_result_view = (ImageView)findViewById(R.id.image_result_view);
        button_close = (Button) findViewById(R.id.button_close);
        button_share = (Button) findViewById(R.id.button_share);

        qrPaymentTransaction = DroidPrefs.get(context,QRPaymentCashViewActivity.keyQRPaymentTransactionCash,QRPaymentTransaction.class);

        button_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });

        button_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File folder = Environment.getExternalStorageDirectory();
                File fileDest = new File(folder, filename);

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(fileDest));
                shareIntent.setType("image/jpeg");
                startActivity(Intent.createChooser(shareIntent, "Share to ..."));

            }
        });

        DownloadImageTask downloadImageTask = new DownloadImageTask();
        downloadImageTask.setBmImage(image_result_view);
        downloadImageTask.setQrpayment_transaction_id(qrPaymentTransaction != null ? qrPaymentTransaction.getId().toString() : "0");
        downloadImageTask.execute();

    }

    @Override
    public void onBackPressed() {

    }

    private class DownloadImageTask extends AsyncTask<Void, Void, Bitmap> {

        ImageView bmImage;
        String qrpayment_transaction_id;

        public void setBmImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        public void setQrpayment_transaction_id(String qrpayment_transaction_id) {
            this.qrpayment_transaction_id = qrpayment_transaction_id;
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

                QRPaymentRunningData qrPaymentRunningData = DroidPrefs.get(context,QRPaymentCashViewActivity.keyQRPaymentRunningData,QRPaymentRunningData.class);

                InputStream in = new java.net.URL(MainUrl.url + "/PrintTransferPayment.htm?type=dc&payType=tranfer&isSuccess=1&bill_no="+StringUtils.defaultString(qrPaymentRunningData.getBill_no()) +"&qrpayment_transaction_id="+qrpayment_transaction_id+"&qrpayment_running_data_id="+qrPaymentRunningData.getId()).openStream();
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

            File folder = Environment.getExternalStorageDirectory();
            File fileDest = new File(folder, filename);

            try {
                FileOutputStream out = new FileOutputStream(fileDest);
                result.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

}
