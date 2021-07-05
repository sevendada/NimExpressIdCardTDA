package com.nimexpress.nimexpressidcardtdaandsigninsurance;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import androidx.appcompat.app.AlertDialog; import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import rd.TDA.TDA;

/**
 * Created by mac on 1/26/2018 AD.
 */

public class SettingActivity extends AppCompatActivity {

    Context context = this;

    TDA tDA;

    Button button_check_connection;
    Button button_connect_to_device;
    Button button_stop_connect_to_reader;

    TextView text_data_tda_info1;
    TextView text_data_tda_info3;
    TextView text_data_tda_info4;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getString(R.string.activity_setting_title));

        tDA = new TDA(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_default_loading));
        progressDialog.setMessage(getString(R.string.app_default_loading_message));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        button_check_connection = (Button)findViewById(R.id.button_check_connection);
        button_connect_to_device = (Button)findViewById(R.id.button_connect_to_device);
        button_stop_connect_to_reader = (Button)findViewById(R.id.button_stop_connect_to_reader);

        text_data_tda_info1 = (TextView)findViewById(R.id.text_data_tda_info1);
        text_data_tda_info3 = (TextView)findViewById(R.id.text_data_tda_info3);
        text_data_tda_info4 = (TextView)findViewById(R.id.text_data_tda_info4);

        button_check_connection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkBlueTooth()) {

                    new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.app_default_title_alert_dialog))
                            .setMessage(context.getString(R.string.setting_is_connect_bluetooth_or_use))
                            .setPositiveButton(context.getString(R.string.app_default_ok_alert_dialog), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();

                }else{

                    new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.app_default_title_alert_dialog))
                            .setMessage(context.getString(R.string.setting_not_connect_bluetooth_or_use))
                            .setPositiveButton(context.getString(R.string.app_default_ok_alert_dialog), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();

                }

            }
        });

        button_connect_to_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String resultInfo = tDA.infoTA("3");

                if(StringUtils.defaultString(resultInfo).contains("-")) {
                    TaskInitData taskInitData = new TaskInitData();
                    taskInitData.execute();
                }else if(!StringUtils.defaultString(resultInfo).equals("00")) {
                    new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.app_default_title_alert_dialog))
                            .setMessage("เชื่อมต่อเรื่องอ่านบัตรอยู่แล้ว")
                            .setPositiveButton(context.getString(R.string.app_default_ok_alert_dialog), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }else{
                    TaskInitData taskInitData = new TaskInitData();
                    taskInitData.execute();
                }

            }
        });

        button_stop_connect_to_reader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String resultInfo = tDA.infoTA("3");

                if(StringUtils.defaultString(resultInfo).contains("-")) {
                    new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.app_default_title_alert_dialog))
                            .setMessage("ยังไม่ได้เชื่อมต่อเครื่องอ่านบัตร")
                            .setPositiveButton(context.getString(R.string.app_default_ok_alert_dialog), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }else if(!StringUtils.defaultString(resultInfo).equals("00")) {
                    new AlertDialog.Builder(context)
                            .setTitle(getString(R.string.app_default_title_alert_dialog))
                            .setMessage(getString(R.string.app_default_dialog_disconect_card_reader))
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    TaskDisConnectReader taskDisConnectReader = new TaskDisConnectReader();
                                    taskDisConnectReader.execute();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }else{
                    new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.app_default_title_alert_dialog))
                            .setMessage("ยังไม่ได้เชื่อมต่อเครื่องอ่านบัตร")
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

    @Override
    protected void onResume() {
        super.onResume();
        TaskCheckAndShowInfo taskCheckAndShowInfo = new TaskCheckAndShowInfo();
        taskCheckAndShowInfo.execute();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public boolean checkBlueTooth() {

        boolean isBluetoothSupported = getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);

        if(isBluetoothSupported) {

            if(isBluetoothEnabled()) {
                return  true;
            }else{
                return  false;
            }

        }else{
            return  false;
        }

    }

    public boolean isBluetoothEnabled()
    {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter.isEnabled();

    }

    public enum ResultInitData {
        CerDataWantDownload,
        TDAServiceNotAvailable,
        Success,
        Error
    }

    class TaskInitData extends AsyncTask<Void,Void,ResultInitData> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected ResultInitData doInBackground(Void... params) {

            try {

                boolean isServiceOn = false;
                int countSec = 0;

                String resultForCheckService = tDA.serviceTA("9");

                if(resultForCheckService.equals("01")) {
                    return  ResultInitData.Success;
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
                            return ResultInitData.CerDataWantDownload;
                        } else {
                            return ResultInitData.Success;
                        }

                    } else {
                        return ResultInitData.TDAServiceNotAvailable;
                    }

                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return  ResultInitData.Error;

        }

        @Override
        protected void onPostExecute(ResultInitData resultData) {
            super.onPostExecute(resultData);
            progressDialog.dismiss();

            if(resultData == ResultInitData.Success) {

                String resultCheckConnectionDevice = tDA.infoTA("3");

                if(StringUtils.defaultString(resultCheckConnectionDevice).equals("00") || StringUtils.defaultString(resultCheckConnectionDevice).contains("-")) {
                    TaskConnectReader taskConnectReader = new TaskConnectReader();
                    taskConnectReader.execute();
                }else{
                    TaskCheckAndShowInfo taskCheckAndShowInfo = new TaskCheckAndShowInfo();
                    taskCheckAndShowInfo.execute();
                }

            }else{

                if(resultData == ResultInitData.TDAServiceNotAvailable) {
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

                if(resultData == ResultInitData.CerDataWantDownload) {
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
            progressDialog.show();
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
            progressDialog.dismiss();

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

                        TaskCheckAndShowInfo taskCheckAndShowInfo = new TaskCheckAndShowInfo();
                        taskCheckAndShowInfo.execute();

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

    private class TaskCheckAndShowInfo extends AsyncTask<Void,Void,String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String[] doInBackground(Void... params) {

            String info1 = tDA.infoTA("1");
            String info2 = tDA.infoTA("2");
            String info4 = tDA.infoTA("4");

            return new String[]{info1,info2,info4};

        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            progressDialog.dismiss();

            text_data_tda_info1.setText(strings[1].equals("#")?"NOT FOUND":strings[1]);
            text_data_tda_info3.setText(strings[2]);
            text_data_tda_info4.setText(strings[0]);


        }
    }

    private class TaskDisConnectReader extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            if(tDA != null){

                String resultCheckServiceOn = tDA.serviceTA("9");

                if(resultCheckServiceOn.equals("00")) return  null;

                tDA.readerTA("3");

                while (true) {

                    String result = tDA.infoTA("3");

                    if(result.equals("00")) {
                        break;
                    }

                }

            }

            return  null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            TaskCheckAndShowInfo taskCheckAndShowInfo = new TaskCheckAndShowInfo();
            taskCheckAndShowInfo.execute();

        }
    }

}
