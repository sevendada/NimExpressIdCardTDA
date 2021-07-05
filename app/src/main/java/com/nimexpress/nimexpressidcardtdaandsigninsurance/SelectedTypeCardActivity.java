package com.nimexpress.nimexpressidcardtdaandsigninsurance;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import androidx.appcompat.app.AlertDialog; import androidx.appcompat.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import rd.TDA.TDA;

/**
 * Created by MacPro on 3/9/18.
 */

public class SelectedTypeCardActivity extends AppCompatActivity {

    Context context = this;

    TextView text_dc_data;
    TextView text_status_connect_reader;
    Button button_type_idcard;
    Button button_type_other;
    Button button_setting_card_reader;

    ProgressDialog progressDialog;
    ProgressDialog progressConnectChecking;

    boolean isHasDc = false;

    TDA tDA;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_typecard);
        setTitle(R.string.activity_selected_card_type_title);

        tDA = new TDA(this);

        text_dc_data = (TextView)findViewById(R.id.text_dc_data);
        text_status_connect_reader = (TextView)findViewById(R.id.text_status_connect_reader);
        button_type_idcard = (Button)findViewById(R.id.button_type_idcard);
        button_type_other = (Button)findViewById(R.id.button_type_other);
        button_setting_card_reader = (Button)findViewById(R.id.button_setting_card_reader);

        String isDroppoint = DroidPrefs.get(context,SplashScreenViewActivity.key_data_is_droppoint,String.class);
        if(isDroppoint.equals("false")) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_default_loading));
        progressDialog.setMessage(getString(R.string.app_default_loading_message));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        progressConnectChecking = new ProgressDialog(this);
        progressConnectChecking.setTitle(getString(R.string.app_default_connect_auto_title));
        progressConnectChecking.setMessage(getString(R.string.app_default_connect_auto_message));
        progressConnectChecking.setIndeterminate(false);
        progressConnectChecking.setCancelable(false);
        progressConnectChecking.setCanceledOnTouchOutside(false);

        button_type_idcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isHasDc) {
                    new AlertDialog.Builder(context)
                            .setTitle(getString(R.string.app_default_title_alert_dialog))
                            .setMessage(getString(R.string.error_save7))
                            .setPositiveButton(getString(R.string.app_default_ok_alert_dialog),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int which) {
                                            dialog.dismiss();
                                        }
                                    })
                            .show();
                }else {
                    Intent intentMainActivity = new Intent(context, MainActivity.class);
                    startActivity(intentMainActivity);
                }
            }
        });

        button_type_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isHasDc) {
                    new AlertDialog.Builder(context)
                            .setTitle(getString(R.string.app_default_title_alert_dialog))
                            .setMessage(getString(R.string.error_save7))
                            .setPositiveButton(getString(R.string.app_default_ok_alert_dialog),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int which) {
                                            dialog.dismiss();
                                        }
                                    })
                            .show();
                }else {
                    Intent intentCardOtherActivity = new Intent(context, CardOtherActivity.class);
                    startActivity(intentCardOtherActivity);
                }
            }
        });

        button_setting_card_reader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentSetting = new Intent(context,SettingActivity.class);
                startActivity(intentSetting);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        /*TaskGetDcAndSubDcData taskGetDcAndSubDcData = new TaskGetDcAndSubDcData(context);
        taskGetDcAndSubDcData.execute();*/
        ClassDet classDetMobileKeyCodeDC = DroidPrefs.get(context,SplashScreenViewActivity.key_data_mobilekeycode_dc,ClassDet.class);
        ClassDet classDetDcSetting = DroidPrefs.get(context,SettingDcActivity.key_data_setting_dc,ClassDet.class);
        if(classDetMobileKeyCodeDC != null) {
            text_dc_data.setText(getString(R.string.activity_dc_data_capture,classDetMobileKeyCodeDC.getSub_class_value()+" "+classDetMobileKeyCodeDC.getSub_class_name()));
            isHasDc = true;
        }else{
            if(classDetDcSetting != null) {
                text_dc_data.setText(getString(R.string.activity_dc_data_capture, classDetDcSetting.getSub_class_value() + " " + classDetDcSetting.getSub_class_name()));
                isHasDc = true;
            }else{
                text_dc_data.setText("ไม่ได้ระบุ");
            }
        }
        TaskCheckConnection taskCheckConnection = new TaskCheckConnection();
        taskCheckConnection.execute();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.settings_menu, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            case R.id.setting_action:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.title_dialog_password));

                // Set up the input
                final EditText input_password = new EditText(this);
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

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class TaskCheckConnection extends  AsyncTask<Void,Void,Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressConnectChecking.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            boolean isConnect = false;

            try {

                String resultInfo = tDA.infoTA("3");

                if(!resultInfo.equals("00") && !resultInfo.startsWith("-")) {
                    isConnect = true;
                }

            }catch (Exception e) {
                e.printStackTrace();
            }

            return isConnect;

        }

        @Override
        protected void onPostExecute(Boolean isConnect) {
            super.onPostExecute(isConnect);
            progressConnectChecking.dismiss();
            if(isConnect) {

                TaskInfoConnect taskInfoConnect = new TaskInfoConnect();
                taskInfoConnect.execute();

            }else{

                new AlertDialog.Builder(context)
                        .setTitle(getString(R.string.app_default_title_alert_dialog))
                        .setMessage(getString(R.string.app_default_not_connect_message))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                TaskInitData taskInitData = new TaskInitData();
                                taskInitData.execute();
                            }})
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                TaskInfoConnect taskInfoConnect = new TaskInfoConnect();
                                taskInfoConnect.execute();
                            }

                        }).show();

            }

        }
    }

    class TaskInitData extends AsyncTask<Void,Void,SettingActivity.ResultInitData> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressConnectChecking.show();
        }

        @Override
        protected SettingActivity.ResultInitData doInBackground(Void... params) {

            try {

                boolean isServiceOn = false;
                int countSec = 0;

                String resultForCheckService = tDA.serviceTA("9");

                if(resultForCheckService.equals("01")) {
                    return  SettingActivity.ResultInitData.Success;
                }else {

                    tDA.serviceTA("0");
                    tDA.serviceTA("1,NimExpressIdCardTDA");

                    while (true) {

                        String result = tDA.serviceTA("9");

                        if (result.equals("01")) {
                            isServiceOn = true;
                            break;
                        }

                        countSec++;
                        System.out.println(countSec);

                        if (countSec == 10) {
                            break;
                        }

                        Thread.sleep(10000);

                    }

                    if (isServiceOn) {

                        String resultUpdateCer = tDA.infoTA("4");

                        if (resultUpdateCer.equals("-2") || resultUpdateCer.equals("-12")) {
                            return SettingActivity.ResultInitData.CerDataWantDownload;
                        } else {
                            return SettingActivity.ResultInitData.Success;
                        }

                    } else {
                        return SettingActivity.ResultInitData.TDAServiceNotAvailable;
                    }

                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return  SettingActivity.ResultInitData.Error;

        }

        @Override
        protected void onPostExecute(SettingActivity.ResultInitData resultData) {
            super.onPostExecute(resultData);
            progressConnectChecking.dismiss();

            if(resultData == SettingActivity.ResultInitData.Success) {

                String resultCheckConnectionDevice = tDA.infoTA("3");

                //Toast.makeText(context,"resultCheckConnectionDevice -> " + resultCheckConnectionDevice,Toast.LENGTH_LONG).show();

                if(StringUtils.defaultString(resultCheckConnectionDevice).equals("00") || StringUtils.defaultString(resultCheckConnectionDevice).contains("-")) {
                    TaskConnectReader taskConnectReader = new TaskConnectReader();
                    taskConnectReader.execute();
                }else{
                    TaskInfoConnect taskInfoConnect = new TaskInfoConnect();
                    taskInfoConnect.execute();
                }

            }else{

                if(resultData == SettingActivity.ResultInitData.TDAServiceNotAvailable) {
                    new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.app_default_title_alert_dialog))
                            .setMessage(context.getString(R.string.error_not_call_service))
                            .setPositiveButton(context.getString(R.string.app_default_ok_alert_dialog), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }

                if(resultData == SettingActivity.ResultInitData.CerDataWantDownload) {
                    TaskUpdateCerDevice taskUpdateCerDevice = new TaskUpdateCerDevice();
                    taskUpdateCerDevice.execute();
                }

            }

        }
    }

    class TaskUpdateCerDevice extends AsyncTask<Void,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = tDA.serviceTA("2");

            return result;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();

            TaskConnectReader taskConnectReader = new TaskConnectReader();
            taskConnectReader.execute();

        }

    }

    private class TaskConnectReader extends AsyncTask<Void,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressConnectChecking.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            try {

                String resultConnectReader = tDA.infoTA("3");

                if(resultConnectReader.equals("00")) {

                    tDA.readerTA("1");

                    boolean isConnect = false;
                    int countSec = 0;

                    while (true) {

                        String result = tDA.readerTA("9");

                        if(result.equals("00")) {
                            break;
                        }else
                            continue;

                    }

                    return "SUCCESS";

                }else{

                    if(resultConnectReader.contains("3")) {
                        return  "ERROR_CER";
                    }else{
                        return "SUCCESS";
                    }

                }

            }catch (Exception e) {
                e.printStackTrace();
                return  "ERROR_EXCEPTION " + e.getStackTrace()[0].getLineNumber() + " -> " + e.getMessage();
            }

        }

        @Override
        protected void onPostExecute(String isConnectReader) {
            super.onPostExecute(isConnectReader);
            progressConnectChecking.dismiss();

            if(isConnectReader.equals("SUCCESS")) {

                String resultConnectReader = tDA.infoTA("3");

                if(resultConnectReader.equals("00")) {

                    new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.app_default_title_alert_dialog))
                            .setMessage(context.getString(R.string.error_not_connect_reader))
                            .setPositiveButton(context.getString(R.string.app_default_ok_alert_dialog), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();

                }else{

                    if(resultConnectReader.contains("3")) {
                        new AlertDialog.Builder(context)
                                .setTitle(context.getString(R.string.app_default_title_alert_dialog))
                                .setMessage(context.getString(R.string.error_cer))
                                .setPositiveButton(context.getString(R.string.app_default_ok_alert_dialog), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                    }else{

                        TaskInfoConnect taskInfoConnect = new TaskInfoConnect();
                        taskInfoConnect.execute();

                    }

                }

            }else{
                if(isConnectReader.equals("ERROR_CER")) {
                    new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.app_default_title_alert_dialog))
                            .setMessage(context.getString(R.string.error_cer))
                            .setPositiveButton(context.getString(R.string.app_default_ok_alert_dialog), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }else{
                    new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.app_default_title_alert_dialog))
                            .setMessage(isConnectReader)
                            .setPositiveButton(context.getString(R.string.app_default_ok_alert_dialog), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }

            }

        }
    }

    class TaskInfoConnect extends AsyncTask<Void,Void,Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressConnectChecking.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            boolean isConnect = false;

            try {

                String resultInfo = tDA.infoTA("3");

                if(!resultInfo.equals("00") && !resultInfo.startsWith("-")) {
                    isConnect = true;
                }

            }catch (Exception e) {
                e.printStackTrace();
            }

            return isConnect;

        }

        @Override
        protected void onPostExecute(Boolean isConnect) {
            super.onPostExecute(isConnect);
            progressConnectChecking.dismiss();

            if(isConnect) {

                text_status_connect_reader.setText(getString(R.string.activity_connect_reader,"เชื่อมต่อแล้ว"));
                text_status_connect_reader.setTextColor(getResources().getColor(R.color.green_success));

            }else{

                text_status_connect_reader.setText(getString(R.string.activity_connect_reader,"ไม่ได้เชื่อมต่อ"));
                text_status_connect_reader.setTextColor(getResources().getColor(R.color.red_error));

            }

        }
    }

    /*class TaskGetDcAndSubDcData extends AsyncTask<Void,Void,ClassDet> {

        TaskGetDcAndSubDcData(Context context){

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
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
            progressDialog.dismiss();

            if(classDet != null) {
                text_dc_data.setText(getString(R.string.activity_dc_data_capture,classDet.getSub_class_value()+" "+classDet.getSub_class_name()));
                isHasDc = true;
            }else{

                ClassDet dcSetting = DroidPrefs.get(context,SettingDcActivity.key_data_setting_dc,ClassDet.class);

                if(dcSetting != null) {
                    if(StringUtils.isNotBlank(dcSetting.getSub_class_id())) {
                        text_dc_data.setText(getString(R.string.activity_dc_data_capture,dcSetting.getSub_class_value()+" "+dcSetting.getSub_class_name()));
                        isHasDc = true;
                    }else{
                        text_dc_data.setText(getString(R.string.activity_dc_data_capture,"ไม่ได้ระบุ"));
                        isHasDc = false;
                    }
                }else{
                    text_dc_data.setText(getString(R.string.activity_dc_data_capture,"ไม่ได้ระบุ"));
                    isHasDc = false;
                }

            }

            TaskCheckConnection taskCheckConnection = new TaskCheckConnection();
            taskCheckConnection.execute();

        }
    }*/

}
