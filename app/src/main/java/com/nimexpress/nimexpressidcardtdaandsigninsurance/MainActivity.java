package com.nimexpress.nimexpressidcardtdaandsigninsurance;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

import info.hoang8f.widget.FButton;
import rd.TDA.TDA;

public class MainActivity extends AppCompatActivity {

    Context context = this;

    TDA tDA;

    Button button_read_data;
    Button button_setting;

    LinearLayout panel_result;
    Button button_send_data;
    ImageView image_photo_idcard;
    TextView text_result;
    TextView text_result2;
    TextView text_result3;
    TextView text_result4;
    TextView text_result5;
    TextView text_result6;
    EditText text_input_phone_data;

    String dataResult;
    byte[] imageResult;

    ProgressDialog progressDialog;

    public static String fileImageIdCardData = "image_idcard_data.jpg";

    public static  int requestCodeSignInsurance = 1;

    String textPhone = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getString(R.string.activity_main_title));

        tDA = new TDA(this);

        button_read_data = (Button) findViewById(R.id.button_read_data);
        button_setting = (Button) findViewById(R.id.button_setting);

        panel_result = (LinearLayout) findViewById(R.id.panel_result);
        button_send_data = (Button) findViewById(R.id.button_send_data);
        image_photo_idcard = (ImageView)findViewById(R.id.image_photo_idcard);
        text_result = (TextView) findViewById(R.id.text_result);
        text_result2 = (TextView) findViewById(R.id.text_result2);
        text_result3 = (TextView) findViewById(R.id.text_result3);
        text_result4 = (TextView) findViewById(R.id.text_result4);
        text_result5 = (TextView) findViewById(R.id.text_result5);
        text_result6 = (TextView) findViewById(R.id.text_result6);
        text_input_phone_data = (EditText) findViewById(R.id.text_input_phone_data);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_default_loading));
        progressDialog.setMessage(getString(R.string.app_default_loading_message));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        if (!tDA.isPackageInstalled(this)) {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.error_not_install_service))
                    .setPositiveButton(getString(R.string.app_default_ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int which) {
                                    dialog.dismiss();
                                }
                            })
                    .show();
        }

        button_read_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskCheckConnectReader taskCheckConnectReader = new TaskCheckConnectReader();
                taskCheckConnectReader.execute();
            }
        });

        button_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSetting = new Intent(context,SettingActivity.class);
                startActivity(intentSetting);
            }
        });

        textPhone = text_input_phone_data.getText().toString();

        button_send_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textPhone = text_input_phone_data.getText().toString();

                if(StringUtils.isBlank(textPhone)) {
                    new AlertDialog.Builder(context)
                            .setMessage("กรุณากรอกเบอร์โทรศัพท์!")
                            .setPositiveButton(getString(R.string.app_default_ok),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int which) {
                                            dialog.dismiss();
                                        }
                                    })
                            .show();
                }else{

                    String mode = DroidPrefs.get(context,SelectModeActivity.key_ismode_data,String.class);

                    String imageBase64 = Base64.encodeToString(imageResult, Base64.DEFAULT);

                    String dataComplete = dataResult+"#"+imageBase64+"#"+textPhone;

                    if(mode.equals("null")) {

                        DroidPrefs.commit(context,SignInsuranceActivity.key_idcard_from_data,"01");
                        DroidPrefs.commit(context,SignInsuranceActivity.key_idcard_data,dataComplete);
                        Intent intent = new Intent(context,SignInsuranceActivity.class);
                        startActivityForResult(intent,requestCodeSignInsurance);

                    }else{

                        if(mode.equals("send")) {

                            DroidPrefs.commit(context,SignInsuranceActivity.key_idcard_from_data,"01");
                            DroidPrefs.commit(context,SignInsuranceActivity.key_idcard_data,dataComplete);
                            Intent intent = new Intent(context,SignInsuranceActivity.class);
                            startActivityForResult(intent,requestCodeSignInsurance);

                        }

                        if(mode.equals("rec")) {
                            TaskSendDataResult taskSendDataResult = new TaskSendDataResult();
                            taskSendDataResult.execute();
                        }

                    }

                }

            }
        });

        image_photo_idcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File imageFile = new File(Environment.getExternalStorageDirectory(), fileImageIdCardData);
                if(imageFile.exists()) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(imageFile), "image/*");
                    startActivity(intent);
                }
            }
        });

        panel_result.setVisibility(View.GONE);
        text_result.setVisibility(View.GONE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == requestCodeSignInsurance) {

            if(resultCode == RESULT_OK) {

                File imageFile = new File(Environment.getExternalStorageDirectory(), fileImageIdCardData);
                if(imageFile.exists()) {
                    imageFile.delete();
                }

                panel_result.setVisibility(View.GONE);
                text_result.setText("");
                text_result.setText("");
                text_result.setText("");
                text_result.setText("");
                image_photo_idcard.setImageDrawable(null);
                text_input_phone_data.setText("");
                dataResult = null;
                imageResult = null;

            }

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //if(tDA != null)tDA.serviceTA("0");
    }

    class TaskSendDataResult extends  AsyncTask<Void,Void,ResponseService> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected ResponseService doInBackground(Void... params) {

            ResponseService responseService = new ResponseService();

            try {

                ClassDet classDet = DroidPrefs.get(context,SettingDcActivity.key_data_setting_dc,ClassDet.class);

                String imageBase64 = Base64.encodeToString(imageResult, Base64.DEFAULT);



                String dataComplete = dataResult+"#"+imageBase64+"#"+textPhone;

                String urlService = MainUrl.url+"/AddIdCardDataTDAService.htm?typeCard=01&mobileKeyCode="+DeviceId.getManufacturerSerialNumber()+"&dcAndSubDcFix="+StringUtils.defaultString(classDet.getSub_class_id());

                URL url = new URL(urlService);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type","text/plain");
                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                byte[] outputInBytes = dataComplete.getBytes("UTF-8");
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(outputInBytes);
                outputStream.close();

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

                responseService = new Gson().fromJson(returnData,ResponseService.class);

                System.out.println(returnData);

            }catch (Exception e) {
                e.printStackTrace();
                responseService.setError(true);
                responseService.setMessageResponse("ERROR");
                responseService.setContentResponse(e.getMessage());
            }

            return responseService;

        }

        @Override
        protected void onPostExecute(final ResponseService responseService) {
            super.onPostExecute(responseService);
            progressDialog.dismiss();

            if(responseService.isError()) {

                if(responseService.getErrorCode().equals("00")) {

                    new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.app_default_title_alert_dialog))
                            .setMessage(context.getString(R.string.error_save1))
                            .setPositiveButton(context.getString(R.string.app_default_ok_alert_dialog), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();

                }else if(responseService.getErrorCode().equals("01")) {

                    new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.app_default_title_alert_dialog))
                            .setMessage(context.getString(R.string.error_save2))
                            .setPositiveButton(context.getString(R.string.app_default_ok_alert_dialog), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();

                }else {

                    new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.app_default_title_alert_dialog))
                            .setMessage(responseService.getContentResponse())
                            .setPositiveButton(context.getString(R.string.app_default_ok_alert_dialog), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();

                }

            }else{

                final Dialog dialog = new Dialog(context);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setContentView(R.layout.popup_sucess_layout);
                dialog.setTitle(context.getString(R.string.app_default_title_alert_dialog));
                TextView text_ref_code = (TextView) dialog.findViewById(R.id.text_ref_code);
                ImageView image_barcode_data = (ImageView)dialog.findViewById(R.id.image_barcode_data);
                FButton button_dialog_ok = (FButton) dialog.findViewById(R.id.button_dialog_ok);

                text_ref_code.setText(context.getString(R.string.app_default_ok_save_data_success_dialog2,responseService.getContentResponse()));

                try {
                    image_barcode_data.setImageBitmap(encodeAsBitmap(responseService.getContentResponse(), BarcodeFormat.CODE_128, 350, 100));
                }catch (Exception e) {
                    e.printStackTrace();
                }

                button_dialog_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();

                        File imageFile = new File(Environment.getExternalStorageDirectory(), fileImageIdCardData);
                        if(imageFile.exists()) {
                            imageFile.delete();
                        }

                        panel_result.setVisibility(View.GONE);
                        text_result.setText("");
                        text_result.setText("");
                        text_result.setText("");
                        text_result.setText("");
                        image_photo_idcard.setImageDrawable(null);
                        text_input_phone_data.setText("");
                        dataResult = null;
                        imageResult = null;

                        String mode = DroidPrefs.get(context,SelectModeActivity.key_ismode_data,String.class);

                        if(StringUtils.defaultString(mode).equals("rec")) {
                            //***
                            new AlertDialog.Builder(context)
                                    .setTitle(context.getString(R.string.app_default_title_alert_dialog))
                                    .setMessage(context.getString(R.string.confirm_customer_receive_data))
                                    .setPositiveButton(context.getString(R.string.app_default_ok_alert_dialog), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();

                                            //***
                                            ClassDet classDetMobileKeyCodeDC = DroidPrefs.get(context, SplashScreenViewActivity.key_data_mobilekeycode_dc, ClassDet.class);

                                            Intent intent = new Intent("com.nssex.imgsignandpiccapture.MainActivity");
                                            intent.setPackage("com.nssex.imgsignandpiccapture");
                                            intent.putExtra("idCardRunningData", responseService.getContentResponse());
                                            intent.putExtra("dcData", classDetMobileKeyCodeDC.getSub_class_value());
                                            intent.putExtra("MobileKeyCodeFix", DeviceId.getManufacturerSerialNumber());
                                            startActivityForResult(intent, 0);

                                        }
                                    })
                                    .setNegativeButton(context.getString(R.string.app_default_cancel_data_alert_dialog), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                        }

                    }
                });

                dialog.show();

            }

        }

    }

    class TaskCheckConnectReader extends AsyncTask<Void,Void,Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            return isStartRead();
        }

        @Override
        protected void onPostExecute(Boolean isConnect) {
            super.onPostExecute(isConnect);
            progressDialog.dismiss();

            if(isConnect) {

                TaskReadDataInIdCard taskReadDataInIdCard = new TaskReadDataInIdCard();
                taskReadDataInIdCard.execute();

            }else{

                new AlertDialog.Builder(context)
                        .setTitle(context.getString(R.string.app_default_title_alert_dialog))
                        .setMessage(context.getString(R.string.setting_is_not_connect_reader_go_settting))
                        .setPositiveButton(context.getString(R.string.app_default_ok_alert_dialog), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intentSetting = new Intent(context,SettingActivity.class);
                                startActivity(intentSetting);
                            }
                        }).show();

            }

        }
    }

    public boolean isStartRead() {

        String resultConnectReader = tDA.infoTA("3");

        if(resultConnectReader.equals("00")) {
            return  false;
        }else {
            if(resultConnectReader.startsWith("-")) {
                return  false;
            }else if (resultConnectReader.contains("3")) {
                return  false;
            } else {
                return  true;
            }
        }

    }

    public String getErrorByCode(String errorCode) {

        if(errorCode.startsWith("-")) {
            if(errorCode.equals("-1")) {
                return getString(R.string.error_result_1);
            }else if(errorCode.equals("-2")) {
                return getString(R.string.error_result_2);
            }else if(errorCode.equals("-3")) {
                return getString(R.string.error_result_3);
            }else if(errorCode.equals("-4")) {
                return getString(R.string.error_result_4);
            }else if(errorCode.equals("-5")) {
                return getString(R.string.error_result_5);
            }else if(errorCode.equals("-6")) {
                return getString(R.string.error_result_6);
            }else if(errorCode.equals("-7")) {
                return getString(R.string.error_result_7);
            }else if(errorCode.equals("-8")) {
                return getString(R.string.error_result_8);
            }else if(errorCode.equals("-9")) {
                return getString(R.string.error_result_9);
            }else if(errorCode.equals("-10")) {
                return getString(R.string.error_result_10);
            }else if(errorCode.equals("-11")) {
                return getString(R.string.error_result_11);
            }else if(errorCode.equals("-12")) {
                return getString(R.string.error_result_12);
            }else if(errorCode.equals("-13")) {
                return getString(R.string.error_result_13);
            }else if(errorCode.equals("-14")) {
                return getString(R.string.error_result_14);
            }else if(errorCode.equals("-15")) {
                return getString(R.string.error_result_15);
            }else if(errorCode.equals("-16")) {
                return getString(R.string.error_result_16);
            }else if(errorCode.equals("-17")) {
                return getString(R.string.error_result_17);
            }else if(errorCode.equals("-18")) {
                return getString(R.string.error_result_18);
            }else{
                return  "UNKOWN ERROR";
            }
        }else{
            return  "UNKOWN ERROR";
        }

    }


    class  TaskReadDataInIdCard extends AsyncTask<Void,Void,Object[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Object[] doInBackground(Void... params) {

            String data = tDA.nidTextTA("0");
            byte[] dataPhoto = tDA.nidPhotoTA("0");

            return new Object[]{data,dataPhoto};

        }

        @Override
        protected void onPostExecute(Object[] result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            text_result.setVisibility(View.VISIBLE);

            if(((String)result[0]).startsWith("-")) {

                panel_result.setVisibility(View.GONE);
                dataResult = null;

                new AlertDialog.Builder(context)
                        .setTitle(context.getString(R.string.app_default_title_alert_dialog))
                        .setMessage(getErrorByCode((String)result[0]))
                        .setPositiveButton(context.getString(R.string.app_default_ok_alert_dialog), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

            }else{

                String[] resultData = ((String)result[0]).split("#");

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                int currentDate = Integer.parseInt(sdf.format(new Date()));

                int brithdayYear = (Integer.parseInt(covertDateData(resultData[18]).substring(6,10))-543);

                text_result.setText(getString(R.string.label_id_card_data,resultData[0]));
                text_result2.setText(getString(R.string.label_name_data,mixFullName(resultData)));
                text_result3.setText(getString(R.string.label_address_data,mixFullAddress(resultData)));
                text_result4.setText(getString(R.string.label_exprie_card_data,covertDateData(resultData[21])));
                text_result5.setText(getString(R.string.label_brithday_card_data,covertDateData(resultData[18]),String.valueOf(currentDate-brithdayYear)));
                text_result6.setText(getString(R.string.label_createdby_card_data,covertDateData(resultData[20])));
                panel_result.setVisibility(View.VISIBLE);
                dataResult = ((String)result[0]);

                text_input_phone_data.requestFocus();

            }

            if(((byte[])result[1]).length > 2) {
                Bitmap bmp = BitmapFactory.decodeByteArray((byte[]) result[1], 0, ((byte[])result[1]).length);
                image_photo_idcard.setImageBitmap(Bitmap.createScaledBitmap(bmp, 250,290, false));
                imageResult = ((byte[]) result[1]);

                try {

                    File imageFile = new File(Environment.getExternalStorageDirectory(), fileImageIdCardData);
                    if (imageFile.exists()) imageFile.delete();
                    FileOutputStream fos=new FileOutputStream(imageFile.getPath());
                    fos.write(imageResult);
                    fos.close();

                }catch (Exception e) {
                    e.printStackTrace();
                }

            }else{
                image_photo_idcard.setImageDrawable(null);
                imageResult = null;
            }

        }
    }

    String mixFullName(String[] dataArray) {

        StringBuffer fullNameMix = new StringBuffer();
        fullNameMix.append(StringUtils.defaultString(dataArray[1]));
        fullNameMix.append(StringUtils.isNotBlank(dataArray[2]) ? (" "+dataArray[2]) : "");
        fullNameMix.append(StringUtils.isNotBlank(dataArray[3]) ? (" "+dataArray[3]) : "");
        fullNameMix.append(StringUtils.isNotBlank(dataArray[4]) ? (" "+dataArray[4]) : "");

        return  fullNameMix.toString();

    }

    String mixFullAddress(String[] dataArray) {

        StringBuffer fullAddressMix = new StringBuffer();
        fullAddressMix.append(StringUtils.defaultString(dataArray[9]));
        fullAddressMix.append(StringUtils.isNotBlank(dataArray[10]) ? (" "+dataArray[10]) : "");
        fullAddressMix.append(StringUtils.isNotBlank(dataArray[11]) ? (" "+dataArray[11]) : "");
        fullAddressMix.append(StringUtils.isNotBlank(dataArray[12]) ? (" "+dataArray[12]) : "");
        fullAddressMix.append(StringUtils.isNotBlank(dataArray[13]) ? (" "+dataArray[13]) : "");
        fullAddressMix.append(StringUtils.isNotBlank(dataArray[14]) ? (" "+dataArray[14]) : "");
        fullAddressMix.append(StringUtils.isNotBlank(dataArray[15]) ? (" "+dataArray[15]) : "");
        fullAddressMix.append(StringUtils.isNotBlank(dataArray[16]) ? (" "+dataArray[16]) : "");

        return  fullAddressMix.toString();

    }

    String covertDateData(String data) {

        String yearData = data.substring(0,4);
        String monthData = data.substring(4,6);
        String dayData = data.substring(6,8);

        return  dayData+"/"+monthData+"/"+yearData;

    }

    public static Bitmap getBitmap(String barcode, int barcodeType, int width, int height)
    {
        Bitmap barcodeBitmap = null;
        BarcodeFormat barcodeFormat = convertToZXingFormat(barcodeType);
        try
        {
            barcodeBitmap = encodeAsBitmap(barcode, barcodeFormat, width, height);
        }
        catch (WriterException e)
        {
            e.printStackTrace();
        }
        return barcodeBitmap;
    }

    private static BarcodeFormat convertToZXingFormat(int format)
    {
        switch (format)
        {
            case 8:
                return BarcodeFormat.CODABAR;
            case 1:
                return BarcodeFormat.CODE_128;
            case 2:
                return BarcodeFormat.CODE_39;
            case 4:
                return BarcodeFormat.CODE_93;
            case 32:
                return BarcodeFormat.EAN_13;
            case 64:
                return BarcodeFormat.EAN_8;
            case 128:
                return BarcodeFormat.ITF;
            case 512:
                return BarcodeFormat.UPC_A;
            case 1024:
                return BarcodeFormat.UPC_E;
            //default 128?
            default:
                return BarcodeFormat.CODE_128;
        }
    }


    /**************************************************************
     * getting from com.google.zxing.client.android.encode.QRCodeEncoder
     *
     * See the sites below
     * http://code.google.com/p/zxing/
     * http://code.google.com/p/zxing/source/browse/trunk/android/src/com/google/zxing/client/android/encode/EncodeActivity.java
     * http://code.google.com/p/zxing/source/browse/trunk/android/src/com/google/zxing/client/android/encode/QRCodeEncoder.java
     */

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    private static Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException
    {
        if (contents == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contents);
        if (encoding != null) {
            hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contents, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        return bitmap;

    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

}
