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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

/**
 * Created by MacPro on 3/9/18.
 */

public class CardOtherActivity extends AppCompatActivity {

    Context context = this;

    EditText text_input_type_card;
    EditText text_input_card_no;
    EditText text_input_name_data;
    EditText text_input_address_data;
    EditText text_input_remark_data;
    EditText text_input_phone_data;
    ImageView image_capture_card_data;
    Button button_send_data;

    ProgressDialog progressDialog;

    static int ActivityCanera = 1;
    static int ActivityPerMission = 2;
    static int ActivitySignInsurance = 3;

    static String FolderImageTemp = "CARD_DATA/Temp";
    static String FolderImageCapture = "CARD_DATA/ImageCapture";

    static String fileImageCaptureName = "ImageCapture_.jpg";
    static String fileTempName = "Temp_.jpg";

    String textTypeCard = "";
    String textIDNo = "";
    String textName = "";
    String textAddress = "";
    String textRemark = "";
    String textPhone = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_other);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getString(R.string.activity_card_other_title));

        text_input_type_card = (EditText)findViewById(R.id.text_input_type_card);
        text_input_card_no = (EditText)findViewById(R.id.text_input_card_no);
        text_input_name_data = (EditText)findViewById(R.id.text_input_name_data);
        text_input_address_data = (EditText)findViewById(R.id.text_input_address_data);
        text_input_remark_data = (EditText)findViewById(R.id.text_input_remark_data);
        text_input_phone_data = (EditText)findViewById(R.id.text_input_phone_data_card_other);
        image_capture_card_data = (ImageView)findViewById(R.id.image_capture_card_data);
        button_send_data = (Button)findViewById(R.id.button_send_data);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_default_loading));
        progressDialog.setMessage(getString(R.string.app_default_loading_message));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        image_capture_card_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    //CAMERA
                    if (ContextCompat.checkSelfPermission((Activity)context,
                            Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {

                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions((Activity)context,
                                new String[]{Manifest.permission.CAMERA},
                                ActivityPerMission);

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

        button_send_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidFormData();
            }
        });

        File fileImageCapture = new File(getFolderImageCapture(), fileImageCaptureName);
        if(fileImageCapture.exists()){
            fileImageCapture.delete();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

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
    protected void onResume() {
        super.onResume();

        try {

            //READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission((Activity)context,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions((Activity)context,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        ActivityPerMission);

            } else {
                // Permission has already been granted
            }

        }catch (Exception e) {

            e.printStackTrace();

        }

        setImageCaptureToView();

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

        }if(requestCode == ActivitySignInsurance) {

            if (resultCode == RESULT_OK) {

                text_input_card_no.setText("");
                text_input_name_data.setText("");
                text_input_address_data.setText("");
                text_input_type_card.setText("");
                text_input_remark_data.setText("");
                text_input_phone_data.setText("");

                File fileImageCapture = new File(getFolderImageCapture(), fileImageCaptureName);
                if(fileImageCapture.exists()){
                    fileImageCapture.delete();
                    setImageCaptureToView();
                }

                text_input_type_card.requestFocus();

            }

        }

    }

    public void ValidFormData() {

         textTypeCard = text_input_type_card.getText().toString();
         textIDNo = text_input_card_no.getText().toString();
         textName = text_input_name_data.getText().toString();
         textAddress = text_input_address_data.getText().toString();
         textPhone = text_input_phone_data.getText().toString();
         textRemark = text_input_remark_data.getText().toString();

        if(StringUtils.isBlank(textTypeCard) || StringUtils.isBlank(textIDNo) || StringUtils.isBlank(textName) || StringUtils.isBlank(textAddress) || StringUtils.isBlank(textPhone)) {
            new AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.app_default_title_alert_dialog))
                    .setMessage(context.getString(R.string.error_save3))
                    .setPositiveButton(context.getString(R.string.app_default_ok_alert_dialog), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }else{

            File fileImageCapture = new File(getFolderImageCapture(), fileImageCaptureName);
            if(fileImageCapture.exists()){

                String mode = DroidPrefs.get(context,SelectModeActivity.key_ismode_data,String.class);

                byte[] imageCapture = null;
                String imageBase64 = "";

                try {

                    imageCapture = FileUtils.readFileToByteArray(fileImageCapture);
                    imageBase64 = Base64.encodeToString(imageCapture, Base64.DEFAULT);

                }catch (Exception e) {
                    e.printStackTrace();
                }

                String dataForm = textIDNo+"#"+textName+"#"+textAddress+"#"+textTypeCard+"#"+imageBase64+"#"+StringUtils.defaultString(textRemark)+"#"+StringUtils.defaultString(textPhone);

                if(mode.equals("null")) {

                    DroidPrefs.commit(context,SignInsuranceActivity.key_idcard_from_data,"02");
                    DroidPrefs.commit(context,SignInsuranceActivity.key_idcard_data,dataForm);
                    Intent intent = new Intent(context,SignInsuranceActivity.class);
                    startActivityForResult(intent,ActivitySignInsurance);

                }else{

                    if(mode.equals("send")) {

                        DroidPrefs.commit(context,SignInsuranceActivity.key_idcard_from_data,"02");
                        DroidPrefs.commit(context,SignInsuranceActivity.key_idcard_data,dataForm);
                        Intent intent = new Intent(context,SignInsuranceActivity.class);
                        startActivityForResult(intent,ActivitySignInsurance);

                    }

                    if(mode.equals("rec")) {
                        TaskSaveCardDataOther taskSaveCardDataOther = new TaskSaveCardDataOther();
                        taskSaveCardDataOther.execute();
                    }

                }


            }else{
                new AlertDialog.Builder(context)
                        .setTitle(context.getString(R.string.app_default_title_alert_dialog))
                        .setMessage(context.getString(R.string.error_save4))
                        .setPositiveButton(context.getString(R.string.app_default_ok_alert_dialog), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }

        }

    }

    class TaskSaveCardDataOther extends AsyncTask<Void,Void,ResponseService> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected ResponseService doInBackground(Void... voids) {

            ResponseService responseService = new ResponseService();

            try {

                ClassDet classDet = DroidPrefs.get(context,SettingDcActivity.key_data_setting_dc,ClassDet.class);

                byte[] imageCapture = null;
                String imageBase64 = "";

                File fileImageCapture = new File(getFolderImageCapture(), fileImageCaptureName);
                if(fileImageCapture.exists()){
                    imageCapture = FileUtils.readFileToByteArray(fileImageCapture);
                    imageBase64 = Base64.encodeToString(imageCapture, Base64.DEFAULT);
                }


                String dataForm = textIDNo+"#"+textName+"#"+textAddress+"#"+textTypeCard+"#"+imageBase64+"#"+StringUtils.defaultString(textRemark)+"#"+StringUtils.defaultString(textPhone);

                String urlService = MainUrl.url+"/AddIdCardDataTDAService.htm?typeCard=02&mobileKeyCode="+DeviceId.getManufacturerSerialNumber()+"&dcAndSubDcFix="+StringUtils.defaultString(classDet.getSub_class_id());

                URL url = new URL(urlService);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type","text/plain");
                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                byte[] outputInBytes = dataForm.getBytes("UTF-8");
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

                        text_input_card_no.setText("");
                        text_input_name_data.setText("");
                        text_input_address_data.setText("");
                        text_input_type_card.setText("");
                        text_input_remark_data.setText("");
                        text_input_phone_data.setText("");

                        File fileImageCapture = new File(getFolderImageCapture(), fileImageCaptureName);
                        if(fileImageCapture.exists()){
                            fileImageCapture.delete();
                            setImageCaptureToView();
                        }

                        text_input_type_card.requestFocus();

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

    private void setImageCaptureToView(){
        File fileImageCapture = new File(getFolderImageCapture(), fileImageCaptureName);
        if(fileImageCapture.exists()){
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            Uri url = Uri.fromFile(fileImageCapture);
            image_capture_card_data.setImageDrawable(null);
            image_capture_card_data.setImageURI(url);
        }else{
            image_capture_card_data.setImageDrawable(getResources().getDrawable(R.drawable.camera));
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
