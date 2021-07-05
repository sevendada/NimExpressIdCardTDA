package com.nimexpress.nimexpressidcardtdaandsigninsurance;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.nimexpress.nimexpressidcardtdaandsigninsurance.model.IdCardDataTDA;
import com.nimexpress.nimexpressidcardtdaandsigninsurance.model.ImageSignInsurance;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

import info.hoang8f.widget.FButton;

public class SignInsuranceActivity extends AppCompatActivity {

    public static String key_choice_ins_data = "choice_ins_data";
    public static String key_idcard_from_data = "idcard_from_data";
    public static String key_idcard_data = "idcard_data";
    public static String key_receive_bill_data = "receive_bill_data";

    public static String key_keep_data_idcard = "keep_data_idcard";
    public static String key_keep_data_insurance = "keep_data_insurance";

    static int ActivityPerMission = 1;
    static int ActivityCanera = 2;
    static int ActivitySignOnScreen = 3;
    static int ActivityReceiveBill = 10;

    Context context = this;

    RadioGroup radio_group;
    RadioButton radio_choice_ins;
    RadioButton radio_choice_no_ins;
    RadioButton radio_choice_product_broken;
    RadioButton radio_choice_product_motorcycle;
    TextView txt_header;
    TextView txt_detail;
    ImageView img_show_sign;
    Button btn_confrim;

    public static String FolderImageTemp = "SIGN_DATA/Temp";
    public static String FolderImageCapture = "SIGN_DATA/ImageCapture";

    public static String fileImageCaptureName = "ImageCapture_.jpg";
    public static String fileTempName = "Temp_.jpg";

    ProgressDialog progressDialog;

    String isDroppoint;

    String isReceiveBill;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signinsurance);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getString(R.string.signinsurance_activity_title));

        radio_group = (RadioGroup)findViewById(R.id.radio_group);
        radio_choice_ins = (RadioButton)findViewById(R.id.radio_choice_ins);
        radio_choice_no_ins = (RadioButton)findViewById(R.id.radio_choice_no_ins);
        radio_choice_product_broken = (RadioButton)findViewById(R.id.radio_choice_product_broken);
        radio_choice_product_motorcycle = (RadioButton)findViewById(R.id.radio_choice_product_motorcycle);
        txt_header = (TextView)findViewById(R.id.txt_header);
        txt_detail = (TextView)findViewById(R.id.txt_detail);
        img_show_sign = (ImageView)findViewById(R.id.img_show_sign);
        btn_confrim = (Button)findViewById(R.id.btn_confrim);

        isDroppoint = DroidPrefs.get(context,SplashScreenViewActivity.key_data_is_droppoint2,String.class);
        if(isDroppoint.equals("true")) {

            radio_choice_ins.setVisibility(View.GONE);
            radio_choice_no_ins.setChecked(true);

            radio_choice_product_motorcycle.setVisibility(View.GONE);

        }else{

            radio_choice_ins.setVisibility(View.VISIBLE);
            radio_choice_ins.setChecked(true);

            radio_choice_product_motorcycle.setVisibility(View.VISIBLE);

        }

        isReceiveBill = DroidPrefs.get(context,key_receive_bill_data,String.class);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_default_loading));
        progressDialog.setMessage(getString(R.string.app_default_loading_message));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        if(radio_choice_ins.isChecked()) {

            txt_header.setText(getString(R.string.choice_ins_title));
            txt_detail.setText(getString(R.string.choice_ins));

            DroidPrefs.commit(context,key_choice_ins_data,"0");

        }else if(radio_choice_no_ins.isChecked()) {

            txt_header.setText(getString(R.string.choice_no_ins_title));
            txt_detail.setText(getString(R.string.choice_no_ins));

            DroidPrefs.commit(context,key_choice_ins_data,"1");

        }else if(radio_choice_product_broken.isChecked()) {

            txt_header.setText(getString(R.string.choice_product_broken_title));
            txt_detail.setText(getString(R.string.choice_product_broken));

            DroidPrefs.commit(context,key_choice_ins_data,"2");

        }else if(radio_choice_product_motorcycle.isChecked()) {

            txt_header.setText(getString(R.string.choice_product_motorcycle_title));
            txt_detail.setText(getString(R.string.choice_motorcycle_broken));

            DroidPrefs.commit(context,key_choice_ins_data,"3");

        }

        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                if(R.id.radio_choice_ins == checkedId) {

                    txt_header.setText(getString(R.string.choice_ins_title));
                    txt_detail.setText(getString(R.string.choice_ins));

                    DroidPrefs.commit(context,key_choice_ins_data,"0");

                }else if(R.id.radio_choice_no_ins == checkedId) {

                    txt_header.setText(getString(R.string.choice_no_ins_title));
                    txt_detail.setText(getString(R.string.choice_no_ins));

                    DroidPrefs.commit(context,key_choice_ins_data,"1");

                }else if(R.id.radio_choice_product_broken == checkedId) {

                    txt_header.setText(getString(R.string.choice_product_broken_title));
                    txt_detail.setText(getString(R.string.choice_product_broken));

                    DroidPrefs.commit(context,key_choice_ins_data,"2");

                }else if(R.id.radio_choice_product_motorcycle == checkedId) {

                    txt_header.setText(getString(R.string.choice_product_motorcycle_title));
                    txt_detail.setText(getString(R.string.choice_motorcycle_broken));

                    DroidPrefs.commit(context,key_choice_ins_data,"3");

                }

            }
        });

        img_show_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                final LayoutInflater inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
                View dialogLayout = inflater.inflate(R.layout.activity_dialog_choice_action,null);
                builder.setView(dialogLayout);
                builder.setTitle(getString(R.string.choice_ins_action_title));
                final AlertDialog dialogChoinceAction = builder.show();

                Button button_sign_activity = (Button) dialogLayout.findViewById(R.id.button_sign_activity);
                Button button_sign_capture = (Button)dialogLayout.findViewById(R.id.button_sign_capture);

                button_sign_activity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogChoinceAction.dismiss();

                        Intent intent = new Intent(context,SignOnScreenActivity.class);
                        startActivityForResult(intent,ActivitySignOnScreen);

                    }
                });

                button_sign_capture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogChoinceAction.dismiss();

                        try {

                            //CAMERA
                            if (ContextCompat.checkSelfPermission((Activity)context,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                                // No explanation needed; request the permission
                                ActivityCompat.requestPermissions((Activity)context,new String[]{Manifest.permission.CAMERA}, ActivityPerMission);

                                return;

                            } else {
                                // Permission has already been granted
                            }


                            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                            StrictMode.setVmPolicy(builder.build());

                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(getFolderImageTemp(), fileTempName)));
                            startActivityForResult(cameraIntent, ActivityCanera);

                        }catch (Exception e) {
                            e.printStackTrace();
                            new AlertDialog.Builder(context)
                                    .setTitle(context.getString(R.string.app_default_title_alert_dialog))
                                    .setMessage(e.getMessage())
                                    .setPositiveButton(context.getString(R.string.app_default_ok_alert_dialog), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                        }

                    }
                });

            }
        });

        btn_confrim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File fileImageCapture = new File(getFolderImageCapture(), fileImageCaptureName);
                if(fileImageCapture.exists()) {
                    TaskSendDataResult taskSendDataResult = new TaskSendDataResult();
                    taskSendDataResult.execute();
                }else{
                    new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.app_default_title_alert_dialog))
                            .setMessage("ไม่พบลายเซ็น!")
                            .setPositiveButton(context.getString(R.string.app_default_ok_alert_dialog), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }

            }
        });

        File fileImageCapture = new File(getFolderImageCapture(), fileImageCaptureName);
        if(fileImageCapture.exists()){
            fileImageCapture.delete();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        //READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission((Activity)context,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed; request the permission
            ActivityCompat.requestPermissions((Activity)context,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},ActivityPerMission);

        } else {
            // Permission has already been granted
        }

        setImageCaptureToView();

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(ActivityCanera == requestCode){

            if(resultCode == RESULT_OK){

                File photoTemp = null;
                File fileImageCapture = null;

                try{
                    photoTemp = new File(getFolderImageTemp(),fileTempName);
                    fileImageCapture = new File(getFolderImageCapture(), fileImageCaptureName);
                    reSizeImage(photoTemp,fileImageCapture);
                }catch(Exception ex){
                    ex.printStackTrace();
                    return;
                }

            }

        }else if(ActivitySignOnScreen == requestCode) {

            if(resultCode == RESULT_OK) {

                File photoTemp = null;
                File fileImageCapture = null;

                try{
                    photoTemp = new File(getFolderImageTemp(),fileTempName);
                    fileImageCapture = new File(getFolderImageCapture(), fileImageCaptureName);
                    reSizeImage(photoTemp,fileImageCapture);
                }catch(Exception ex){
                    ex.printStackTrace();
                    return;
                }

            }

        }else if(ActivityReceiveBill == requestCode) {

            if (resultCode == RESULT_OK) {

                setResult(RESULT_OK);
                finish();

            }

        }

    }

    class TaskSendDataResult extends AsyncTask<Void,Void,ResponseService> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected ResponseService doInBackground(Void... params) {

            ResponseService responseService = new ResponseService();

            try {

                String signInsType = DroidPrefs.get(context,key_choice_ins_data,String.class);
                String data_from = DroidPrefs.get(context,SignInsuranceActivity.key_idcard_from_data,String.class);
                String data = DroidPrefs.get(context,SignInsuranceActivity.key_idcard_data,String.class);
                String dataComplete = null;

                ClassDet classDet = DroidPrefs.get(context,SettingDcActivity.key_data_setting_dc,ClassDet.class);
                //ClassDet classDetMobileKeyCodeDC = DroidPrefs.get(context, SplashScreenViewActivity.key_data_mobilekeycode_dc, ClassDet.class);

                String signImageBase64 = null;
                File fileImageCapture = new File(getFolderImageCapture(), fileImageCaptureName);
                if(fileImageCapture.exists()){
                    try {
                        signImageBase64 = Base64.encodeToString(FileUtils.readFileToByteArray(fileImageCapture),Base64.DEFAULT);
                        dataComplete = data+"#"+StringUtils.defaultString(signImageBase64);
                    }catch (Exception e) {
                        e.printStackTrace();
                        signImageBase64 = null;
                    }
                }

                String urlService = MainUrl.url+"/AddIdCardDataTDAAndSignInsuranceService.htm?typeCard="+data_from+"&mobileKeyCode="+DeviceId.getManufacturerSerialNumber()+"&dcAndSubDcFix="+ StringUtils.defaultString(classDet.getSub_class_id()+"&signInsType="+StringUtils.defaultString(signInsType));

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

            return  responseService;

        }

        @Override
        protected void onPostExecute(ResponseService responseService) {
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

                if(StringUtils.isBlank(isReceiveBill)) {

                    final Dialog dialog = new Dialog(context);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setCancelable(false);
                    dialog.setContentView(R.layout.popup_sucess_layout);
                    dialog.setTitle(context.getString(R.string.app_default_title_alert_dialog));
                    TextView text_ref_code = (TextView) dialog.findViewById(R.id.text_ref_code);
                    ImageView image_barcode_data = (ImageView) dialog.findViewById(R.id.image_barcode_data);
                    FButton button_dialog_ok = (FButton) dialog.findViewById(R.id.button_dialog_ok);

                    text_ref_code.setText(context.getString(R.string.app_default_ok_save_data_success_dialog2, responseService.getContentResponse()));

                    try {
                        image_barcode_data.setImageBitmap(encodeAsBitmap(responseService.getContentResponse(), BarcodeFormat.CODE_128, 350, 100));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    File fileImageCapture = new File(getFolderImageCapture(), fileImageCaptureName);
                    if (fileImageCapture.exists()) {
                        fileImageCapture.delete();
                    }
                    setImageCaptureToView();
                    button_dialog_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialog.dismiss();

                            setResult(RESULT_OK);
                            finish();

                        }
                    });

                    dialog.show();

                }else{

                    String choiceInsData = DroidPrefs.get(context,key_choice_ins_data,String.class);

                    try {

                        if(StringUtils.isNotBlank(responseService.getJsonStringResponse())) {

                            IdCardDataTDA idCardDataTDA = new Gson().fromJson(responseService.getJsonStringResponse(),IdCardDataTDA.class);
                            DroidPrefs.commit(context,key_keep_data_idcard,idCardDataTDA);

                        }

                        if(StringUtils.isNotBlank(responseService.getJsonStringResponse2())) {

                            ImageSignInsurance imageSignInsurance = new Gson().fromJson(responseService.getJsonStringResponse2(),ImageSignInsurance.class);
                            DroidPrefs.commit(context,key_keep_data_insurance,imageSignInsurance);

                        }

                    }catch (Exception e) {
                        e.printStackTrace();
                    }

                    if(choiceInsData.equals("0")) {
                        DroidPrefs.commit(context,ReceiveBillActivity.key_step_code_data,"0");
                    }else{
                        DroidPrefs.commit(context,ReceiveBillActivity.key_step_code_data,"1");
                    }

                    Intent intent = new Intent(context,ReceiveBillActivity.class);
                    startActivityForResult(intent,ActivityReceiveBill);

                }

            }

        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;

    }

    public static int size_avg_200KB = 1;
    public static int size_avg_500KB = 2;

    public static File resizeImage(File sourceImage,int size_avg,int quality) throws Exception {

        int resizeHeight = 0;
        int resizeWidth = 0;

        int resizeA = 0;
        int resizeB = 0;

        if(size_avg == size_avg_200KB) {
            resizeA = 640;
            resizeB = 480;
        }else if(size_avg == size_avg_500KB) {
            resizeA = 1280;
            resizeB = 960;
        }

        //convert file image to byte array.
        byte[] imageByteData = IOUtils.toByteArray(new FileInputStream(sourceImage));

        //decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(imageByteData, 0, imageByteData.length,options);

        //check image is horizontal or vertical for fix size image.
        if(options.outWidth > options.outHeight) {
            //this image horizontal.
            resizeWidth = resizeA;
            resizeHeight = resizeB;
        }else if(options.outWidth < options.outHeight) {
            //this image vertical.
            resizeWidth = resizeB;
            resizeHeight = resizeA;
        }

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, resizeWidth, resizeHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        //decode byte array of image to bitmap.
        Bitmap sourceBitmap = BitmapFactory.decodeByteArray(imageByteData, 0, imageByteData.length,options);
        //create new bitmap with fix size.
        Bitmap resizeBitmap = Bitmap.createScaledBitmap(sourceBitmap, resizeWidth, resizeHeight, true);

        File fileComplate = new File(sourceImage.getPath());
        FileOutputStream out = new FileOutputStream(fileComplate);
        resizeBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);

        //clear memory.
        out.flush();
        out.close();
        sourceBitmap.recycle();
        resizeBitmap.recycle();

        return fileComplate;

    }

    /* new function deduce memory use. */
    private void reSizeImage(File fileImageSource,File fileDestination){

        try{

            File fileResize = resizeImage(fileImageSource, size_avg_200KB,80);

            //create file to destination from  Bitmap
            if(!fileDestination.exists())fileDestination.createNewFile();

            FileOutputStream fos = new FileOutputStream(fileDestination);
            fos.write(IOUtils.toByteArray(new FileInputStream(fileResize)));

            //close Buffer
            fos.close();

        }catch(Exception ex) {
            ex.printStackTrace();
            Toast.makeText(context, "reSizeImage Error -> " + ex.toString(), Toast.LENGTH_LONG).show();
        }

    }

    private void setImageCaptureToView(){
        File fileImageCapture = new File(getFolderImageCapture(), fileImageCaptureName);
        if(fileImageCapture.exists()){
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            Uri url = Uri.fromFile(fileImageCapture);
            img_show_sign.setImageDrawable(null);
            img_show_sign.setImageURI(url);
        }else{
            img_show_sign.setImageDrawable(getResources().getDrawable(R.drawable.signature));
        }
    }

    private File getFolderImageCapture() {
        File sdFolderImageCapture = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File folderImageCapture = new File(sdFolderImageCapture, FolderImageCapture);
        try {
            if (!folderImageCapture.exists()) folderImageCapture.mkdirs();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return folderImageCapture;
    }

    private File getFolderImageTemp() {
        File sdFolderTemp = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File folderTmp = new File(sdFolderTemp, FolderImageTemp);
        try {
            if (!folderTmp.exists()) folderTmp.mkdirs();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return folderTmp;
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
