package com.nimexpress.nimexpressidcardtdaandsigninsurance;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.nimexpress.nimexpressidcardtdaandsigninsurance.model.MobileKeyFixSqlModel;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class SplashScreenViewActivity extends AppCompatActivity {

    Context context = this;

    TextView text_message_error;
    Button button_download_app;
    ProgressBar progress_check_version;
    LinearLayout panel_download;
    Button button_setting_dc;
    ImageView img_logo;

    public static String key_data_mobilekeycode_dc = "mobilekeycode_dc";
    public static String key_data_is_droppoint = "is_droppoint";
    public static String key_data_is_droppoint2 = "is_droppoint2";

    public static int ActivityPerMission = 1;
    int count_click_fix_m = 1;
    SQLiteDatabase sqLiteDatabase;
    DBHelperMobileKey dbHelperMobileKey;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        text_message_error = (TextView)findViewById(R.id.text_message_error);
        button_download_app = (Button) findViewById(R.id.button_download_app);
        progress_check_version = (ProgressBar)findViewById(R.id.progress_check_version);
        panel_download = (LinearLayout)findViewById(R.id.panel_download);
        button_setting_dc = (Button)findViewById(R.id.button_setting_dc);
        img_logo = (ImageView)findViewById(R.id.img_logo);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        dbHelperMobileKey = new DBHelperMobileKey(context);
        boolean isTableDBHelperMobileKey =  isTableMobileKeyExists();
        if (isTableDBHelperMobileKey){
            MobileKeyFixSqlModel mobileKeyFixSqlModel = dbHelperMobileKey.getMobileKeyFixSQLData();
            String data = "ID ->"+mobileKeyFixSqlModel.getId()+"" +
                    "KEY_CODE ->"+mobileKeyFixSqlModel.getKEY_CODE()+"" +
                    "IS_FIX_MODE ->"+mobileKeyFixSqlModel.getIS_FIX_MODE();
            Log.d("DBHelperMobileKey",data);
            if (mobileKeyFixSqlModel.getIS_FIX_MODE().equals("1")){
                DeviceId.MobileCustom = mobileKeyFixSqlModel.getKEY_CODE();
                DeviceId.ModesetMobileKey = true;
            }else {
                DeviceId.MobileCustom ="";
                DeviceId.ModesetMobileKey = false;
            }
        }else {
            DeviceId.MobileCustom ="";
            DeviceId.ModesetMobileKey = false;
            Log.d("DBHelperMobileKey","Table DBHelperMobileKey is null");
        }

        button_download_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskDownloadApp taskDownloadApp = new TaskDownloadApp();
                taskDownloadApp.execute();
            }
        });

        img_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count_click_fix_m == 3) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SplashScreenViewActivity.this);
                    android.app.AlertDialog dialog;
                    View layout = getLayoutInflater().inflate(R.layout.pop_up_pass_unlock_service, null);
                    builder.setView(layout);
                    final EditText pass_unlock = (EditText) layout.findViewById(R.id.ed_pass_unlock);
                    builder.setCancelable(false);
                    builder.setPositiveButton("UnLock", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (pass_unlock.getText().toString().equals("2637")) {
                                android.app.AlertDialog.Builder builder_edit_service = new android.app.AlertDialog.Builder(SplashScreenViewActivity.this);
                                View layout_edit = getLayoutInflater().inflate(R.layout.pop_up_edit_service_other, null);
                                builder_edit_service.setCancelable(false);
                                builder_edit_service.setView(layout_edit);
                                final EditText ed_fix_mobile = (EditText) layout_edit.findViewById(R.id.ed_service_url);
                                Button btn_get_key = (Button)layout_edit.findViewById(R.id.btn_get_key);
                                Button btn_mobile_key_cancel = (Button)layout_edit.findViewById(R.id.btn_mobile_key_cancel);
                                ed_fix_mobile.setHint("Enter Fix Mobile Key Code");
                                builder_edit_service.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (ed_fix_mobile.length() == 0) {
                                            Toast.makeText(SplashScreenViewActivity.this, "Your Input MobileKey is empty ", Toast.LENGTH_SHORT).show();
                                        } else {
                                            /*DeviceId.ModesetMobileKey = true;
                                            DeviceId.MobileCustom = ed_fix_mobile.getText().toString();*/

                                            if(isTableMobileKeyExists()){
                                                MobileKeyFixSqlModel mobileKeyFixSqlModel = dbHelperMobileKey.getMobileKeyFixSQLData();
                                                mobileKeyFixSqlModel.setKEY_CODE(ed_fix_mobile.getText().toString());
                                                mobileKeyFixSqlModel.setIS_FIX_MODE("1");
                                                dbHelperMobileKey.update_data_to_sql(mobileKeyFixSqlModel);
                                            }else{
                                                MobileKeyFixSqlModel mobileKeyFixSqlModel = new MobileKeyFixSqlModel();
                                                mobileKeyFixSqlModel.setKEY_CODE(ed_fix_mobile.getText().toString());
                                                mobileKeyFixSqlModel.setIS_FIX_MODE("1");
                                                dbHelperMobileKey.add_data_to_sql(mobileKeyFixSqlModel);
                                            }
                                            Toast.makeText(SplashScreenViewActivity.this, "Your Input MobileKey is : " + ed_fix_mobile.getText().toString(), Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                            ReActivity();
                                        }
                                    }
                                });
                                builder_edit_service.create();
                                builder_edit_service.show();
                                btn_get_key.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String getKeystr = DeviceId.getManufacturerSerialNumber();
                                        if(getKeystr.equals("unknown")){
                                            ShowMsg("Can't get Mobile Key Code");
                                        }else{
                                            ed_fix_mobile.setText(getKeystr);
                                        }
                                    }
                                });
                                btn_mobile_key_cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        MobileKeyFixSqlModel mobileKeyFixSqlModel = dbHelperMobileKey.getMobileKeyFixSQLData();
                                        //DeviceId.ModesetMobileKey = false;
                                        if (mobileKeyFixSqlModel!=null){
                                            dbHelperMobileKey.delete_data_to_sql(mobileKeyFixSqlModel);
                                        }
                                        Toast.makeText(SplashScreenViewActivity.this, "ModesetMobileKey is off ", Toast.LENGTH_SHORT).show();
                                        ReActivity();
                                    }
                                });
                            } else {
                                //DeviceId.ModesetMobileKey = false;
                                dialog.dismiss();
                                Toast.makeText(SplashScreenViewActivity.this, "Incorrect password!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create();
                    builder.show();
                } else if (count_click_fix_m < 3) {
                    count_click_fix_m++;
                }
            }
        });

        button_setting_dc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(getString(R.string.title_dialog_password));

                // Set up the input
                final EditText input_password = new EditText(context);
                input_password.setInputType(InputType.TYPE_CLASS_NUMBER);
                input_password.setTransformationMethod(PasswordTransformationMethod.getInstance());

                builder.setView(input_password);

                // Set up the buttons
                builder.setPositiveButton(getString(R.string.title_dialog_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String password = input_password.getText().toString();

                        if(StringUtils.isNotBlank(password)) {

                            if(password.equals("25819")) {
                                Intent intentSettingDcActivity = new Intent(context, SettingDcActivity.class);
                                startActivity(intentSettingDcActivity);
                            }else{

                                new AlertDialog.Builder(context)
                                        .setTitle(context.getString(R.string.app_default_title_alert_dialog))
                                        .setMessage(context.getString(R.string.error_save6))
                                        .setPositiveButton(context.getString(R.string.app_default_ok_alert_dialog), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();

                            }

                        }else{

                            new AlertDialog.Builder(context)
                                    .setTitle(context.getString(R.string.app_default_title_alert_dialog))
                                    .setMessage(context.getString(R.string.error_save5))
                                    .setPositiveButton(context.getString(R.string.app_default_ok_alert_dialog), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).show();

                        }


                    }
                });

                builder.setNegativeButton(getString(R.string.title_dialog_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);

                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                dialog.show();

            }
        });


            TaskGetClassDet taskGetClassDet = new TaskGetClassDet();
            taskGetClassDet.execute();



    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private boolean isTableMobileKeyExists() {
        DBHelperMobileKey dbHelperMobileKey = new DBHelperMobileKey(context);
        sqLiteDatabase = dbHelperMobileKey.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query
                (MobileKeyFixSqlModel.TABLE, null, null, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                return true;
            }
        }
        return false;
    }

    private void ShowMsg(String msg) {
        Toast.makeText(getApplicationContext(), msg,
                Toast.LENGTH_SHORT).show();
    }

    private void ReActivity() {
        Intent i = new Intent(SplashScreenViewActivity.this, SplashScreenViewActivity.class);
        finish();
        overridePendingTransition(0, 0);
        startActivity(i);
        overridePendingTransition(0, 0);
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
                ActivityCompat.requestPermissions((Activity)context,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},ActivityPerMission);

            } else {
                // Permission has already been granted
            }

        }catch (Exception e) {

            e.printStackTrace();

        }
    }

    ProgressDialog mProgressDialog;

    public class TaskDownloadApp extends AsyncTask<Void, Integer, String> {

        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;

        File folderData = null;
        File fileData = null;
        String fileName_APK = "NIMEX_IDCARD_TDA.apk";

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mProgressDialog.setProgress(values[0]);
            System.out.println(values[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // instantiate it within the onCreate method
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage(getString(R.string.download_app_waiting));
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setProgress(0);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {

                folderData = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/FOLDER_IDCARD_TDA/");
                if(!folderData.exists()) folderData.mkdirs();
                fileData = new File(folderData, fileName_APK);
                if(!fileData.exists()) fileData.createNewFile();

                URL url = new URL(MainUrl.url+"/MobileServiceMaster.htm?action=getByCodeHead&codeHead=MOBILE_DOWNLOAD_LINK");
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

                String jsonResult = textBuilder.toString();

                Type type = new ListParameterizedType(ClassDet.class);
                List<ClassDet> listData = new Gson().fromJson(jsonResult, type);

                ClassDet classDetAppDownload = null;

                for (ClassDet classDet : listData) {

                    if(classDet.sub_class_id.equals("NIMEX_IDCARD_TDA")) {
                        classDetAppDownload = classDet;
                        break;
                    }

                }

                if(classDetAppDownload != null) {

                    URL urlDownloadApp = new URL(classDetAppDownload.getSub_class_value());
                    connection = (HttpURLConnection) urlDownloadApp.openConnection();
                    connection.connect();

                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        if (connection != null) connection.disconnect();
                        return null;
                    }else{

                        // this will be useful to display download percentage
                        // might be -1: server did not report the length
                        int fileLength = connection.getContentLength();

                        // download the file
                        input = connection.getInputStream();
                        output = new FileOutputStream(fileData);

                        byte data[] = new byte[4096];
                        long total = 0;
                        int count;
                        while ((count = input.read(data)) != -1) {
                            // allow canceling with back button
                            if (isCancelled()) {
                                input.close();
                                return null;
                            }
                            total += count;
                            // publishing the progress....
                            if (fileLength > 0) // only if total length is known
                                publishProgress((int) (total * 100 / fileLength));
                            output.write(data, 0, count);
                        }

                        return "SUCCESS";

                    }

                }else{
                    return null;
                }

            }catch (Exception e) {
                e.printStackTrace();
                return null;
            }finally {

                try {
                    if (output != null) output.close();
                    if (input != null) input.close();
                } catch (IOException ignored) {
                    ignored.printStackTrace();
                }

                if (connection != null) connection.disconnect();

            }

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            mProgressDialog.dismiss();

            if(StringUtils.isBlank(result)) {

                Toast.makeText(context, "DOWNLOAD FAIL!", Toast.LENGTH_LONG).show();

            }else{

                if(fileData != null) {
                    if(fileData.exists()) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(fileData), "application/vnd.android.package-archive");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
                        context.startActivity(intent);
                    }
                }

            }

        }
    }

    private class TaskGetClassDet extends AsyncTask<Void,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress_check_version.setVisibility(View.VISIBLE);
            panel_download.setVisibility(View.GONE);
            button_download_app.setVisibility(View.GONE);
            button_setting_dc.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {

                URL url = new URL(MainUrl.url+"/MobileServiceMaster.htm?action=getByCodeHead&codeHead=MOBILE_APP_VERSION");
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

                return  returnData;

            }catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progress_check_version.setVisibility(View.GONE);

            if(StringUtils.isBlank(result)) {
                if (!isNetworkConnected()){
                    text_message_error.setText(getString(R.string.error_update_app2,"ไม่พบสัญญาณอินเทอร์เน็ต"));
                }else {
                    text_message_error.setText(getString(R.string.error_update_app2,"RESULT IS BLANK!"));
                }
                panel_download.setVisibility(View.VISIBLE);

            }else{

                try {

                    Type type = new ListParameterizedType(ClassDet.class);
                    List<ClassDet> listData = new Gson().fromJson(result, type);

                    ClassDet classDetVersion = null;

                    for (ClassDet classDet : listData) {

                        if(classDet.sub_class_id.equals("NIMEX_IDCARD_TDA")) {
                            classDetVersion = classDet;
                            if (StringUtils.isNoneBlank(classDet.getRemark5())){
                                MainUrl.url = classDet.getRemark5();
                            }
                            break;
                        }

                    }

                    if(classDetVersion != null) {

                        int fixAppVersion = Integer.parseInt(classDetVersion.sub_class_value);
                        int currentAppVersion = getVersion();

                        if(currentAppVersion < 0) {

                            text_message_error.setText(getString(R.string.error_update_app2,"VERSION APP ERROR!"));
                            panel_download.setVisibility(View.VISIBLE);

                        }else{

                            if(fixAppVersion > currentAppVersion) {

                                text_message_error.setText(getString(R.string.error_update_app));
                                panel_download.setVisibility(View.VISIBLE);
                                button_download_app.setVisibility(View.VISIBLE);

                            }else{

                                /*finish();
                                Intent intent = new Intent(context,SelectedTypeCardActivity.class);
                                startActivity(intent);*/

                                TaskGetDcAndSubDcData taskGetDcAndSubDcData = new TaskGetDcAndSubDcData(context);
                                taskGetDcAndSubDcData.execute();

                            }

                        }

                    }else{

                        text_message_error.setText(getString(R.string.error_update_app2,"VERSION NOT FOUND!"));
                        panel_download.setVisibility(View.VISIBLE);

                    }

                }catch (Exception e) {
                    e.printStackTrace();
                    text_message_error.setText(getString(R.string.error_update_app2,e.getMessage()));
                    panel_download.setVisibility(View.VISIBLE);
                }

            }

        }
    }

    private int getVersion(){
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            int version = pInfo.versionCode;
            return  version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return  -1;
        }
    }

    private class TaskGetDcAndSubDcData extends AsyncTask<Void,Void,ClassDet> {

        TaskGetDcAndSubDcData(Context context){

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress_check_version.setVisibility(View.VISIBLE);
        }

        @Override
        protected ClassDet doInBackground(Void... voids) {

            String urlService = MainUrl.url+"/MobileDCAndSubDcDataService.htm?action=getDataByCode&mobile_key_code="+DeviceId.getManufacturerSerialNumber();

            try {

                URL url = new URL(urlService);
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

                ClassDet classDetDcData = new Gson().fromJson(returnData,ClassDet.class);

                return  classDetDcData;

            }catch (Exception e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(ClassDet classDet) {
            super.onPostExecute(classDet);
            progress_check_version.setVisibility(View.GONE);

            if(classDet == null) {

                ClassDet dcSetting = DroidPrefs.get(context,SettingDcActivity.key_data_setting_dc,ClassDet.class);

                if(StringUtils.isBlank(dcSetting.getSub_class_id())) {
                    if (DeviceId.ModesetMobileKey) {
                        text_message_error.setText(getString(R.string.error_update_app2,"DC NOT SETTING!")+"\n ModeFixMobileKey On->" + DeviceId.MobileCustom);
                    } else {
                        text_message_error.setText(getString(R.string.error_update_app2,"DC NOT SETTING!"));
                    }
                    panel_download.setVisibility(View.VISIBLE);
                    //button_setting_dc.setVisibility(View.VISIBLE);
                }else{
                    TaskCheckDroppointData taskCheckDroppointData = new TaskCheckDroppointData(context);
                    taskCheckDroppointData.setDcAndSubDcFix(dcSetting.getSub_class_id());
                    taskCheckDroppointData.execute();
                }

            }else{

                DroidPrefs.commit(context,SplashScreenViewActivity.key_data_mobilekeycode_dc,classDet);

                TaskCheckDroppointData taskCheckDroppointData = new TaskCheckDroppointData(context);
                taskCheckDroppointData.execute();

            }

        }
    }

    private class TaskCheckDroppointData extends AsyncTask<Void,Void,ResponseService> {

        String dcAndSubDcFix;

        public void setDcAndSubDcFix(String dcAndSubDcFix) {
            this.dcAndSubDcFix = dcAndSubDcFix;
        }

        TaskCheckDroppointData(Context context) {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress_check_version.setVisibility(View.VISIBLE);
        }

        @Override
        protected ResponseService doInBackground(Void... voids) {

            String urlService = MainUrl.url + "/MobileDCAndSubDcDataService.htm?action=checkIsDroppoint&mobile_key_code=" + DeviceId.getManufacturerSerialNumber()+"&dcAndSubDcFIx="+StringUtils.defaultString(dcAndSubDcFix);

            try {

                URL url = new URL(urlService);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                StringBuilder textBuilder = new StringBuilder();
                try {
                    Reader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    int c = 0;
                    while ((c = reader.read()) != -1) {
                        textBuilder.append((char) c);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String returnData = textBuilder.toString();

                ResponseService responseService = new Gson().fromJson(returnData, ResponseService.class);

                return responseService;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(ResponseService responseService) {
            super.onPostExecute(responseService);
            progress_check_version.setVisibility(View.GONE);

            if(responseService.isError()) {
                text_message_error.setText(getString(R.string.error_update_app2,responseService.getErrorCode()+" - "+responseService.getMessageResponse()));
                panel_download.setVisibility(View.VISIBLE);
            }else{
                /*
                if(responseService.getContentResponse().equals("true")) {
                    DroidPrefs.commit(context,SplashScreenViewActivity.key_data_is_droppoint,"true");
                    DroidPrefs.commit(context,SelectModeActivity.key_ismode_data,"null");
                    finish();
                    Intent intent = new Intent(context,SelectedTypeCardActivity.class);
                    startActivity(intent);
                }else{
                    DroidPrefs.commit(context,SplashScreenViewActivity.key_data_is_droppoint,"false");
                    DroidPrefs.commit(context,SelectModeActivity.key_ismode_data,"null");
                    finish();
                    Intent intent = new Intent(context,SelectModeActivity.class);
                    startActivity(intent);
                }
                */
                DroidPrefs.commit(context,SplashScreenViewActivity.key_data_is_droppoint,"false");
                DroidPrefs.commit(context,SelectModeActivity.key_ismode_data,"null");

                boolean isDroppoint = false;

                if(responseService.getContentResponse().equals("true")) {
                    DroidPrefs.commit(context,SplashScreenViewActivity.key_data_is_droppoint2,"true");
                    isDroppoint = true;
                }else{
                    DroidPrefs.commit(context,SplashScreenViewActivity.key_data_is_droppoint2,"false");
                    isDroppoint = false;
                }

                if(isDroppoint) {
                    finish();
                    Intent intent = new Intent(context, SelectModeActivity.class);
                    startActivity(intent);
                }else{

                    finish();
                    Intent intent = new Intent(context, SelectModeActivity.class);
                    startActivity(intent);

                    /*
                    String keepLogin = DroidPrefs.get(context,LoginViewActivity.KEY_DATA_LOGIN_KEEP,String.class);
                    String keepUser = DroidPrefs.get(context,LoginViewActivity.KEY_DATA_LOGIN_USER,String.class);

                    if(StringUtils.isBlank(keepLogin) || StringUtils.isBlank(keepUser)) {

                        finish();
                        Intent intent = new Intent(context, LoginViewActivity.class);
                        startActivity(intent);

                    }else{

                        finish();
                        Intent intent = new Intent(context, SelectModeActivity.class);
                        startActivity(intent);

                    }
                    */

                }

            }

        }

    }

}
