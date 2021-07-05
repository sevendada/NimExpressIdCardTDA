package com.nimexpress.nimexpressidcardtdaandsigninsurance;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import androidx.appcompat.app.AlertDialog; import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.nimexpress.nimexpressidcardtdaandsigninsurance.service.ConnectionService;
import com.nimexpress.nimexpressidcardtdaandsigninsurance.service.ParameterService;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class LoginViewActivity extends AppCompatActivity {

    Context context = this;

    public static String KEY_DATA_LOGIN_KEEP = "DATA_LOGIN_KEEP";
    public static String KEY_DATA_LOGIN_USER = "DATA_LOGIN_USER";

    EditText text_user_data;
    EditText text_password_data;

    Button button_login_data;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("เข้าสู่ระบบ");

        text_user_data = (EditText)findViewById(R.id.text_user_data);
        text_password_data = (EditText)findViewById(R.id.text_password_data);

        button_login_data = (Button)findViewById(R.id.button_login_data);

        button_login_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textUserName = text_user_data.getText().toString();
                String textPassword = text_password_data.getText().toString();

                if(StringUtils.isBlank(textUserName) || StringUtils.isBlank(textPassword)) {

                    new AlertDialog.Builder(context)
                            .setTitle(getString(R.string.app_default_title_alert_dialog))
                            .setMessage(getString(R.string.error_login_1))
                            .setPositiveButton(getString(R.string.app_default_ok_alert_dialog),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int which) {
                                            dialog.dismiss();
                                        }
                                    })
                            .show();

                }else{

                    TaskCheckUserLogin taskCheckUserLogin = new TaskCheckUserLogin();
                    taskCheckUserLogin.setUsername(textUserName);
                    taskCheckUserLogin.setPassword(textPassword);
                    taskCheckUserLogin.execute();

                }

            }
        });

    }

    public class TaskCheckUserLogin extends AsyncTask<Void,Void,ResponseService> {

        String username;
        String password;

        public void setUsername(String username) {
            this.username = username;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, getString(R.string.dialog_loading_data), getString(R.string.dialog_loading_data_message), true, false);
        }

        @Override
        protected ResponseService doInBackground(Void... voids) {

            ResponseService responseService = new ResponseService();

            try {

                List<ParameterService> parameterServiceList = new ArrayList<>();
                parameterServiceList.add(new ParameterService("action","checkUser"));
                parameterServiceList.add(new ParameterService("username",username));
                parameterServiceList.add(new ParameterService("password",password));

                ConnectionService connectionService = new ConnectionService(MainUrl.url+"/MobileServiceFastBill.htm");

                String responseString = connectionService.callService(parameterServiceList);

                responseService = new Gson().fromJson(responseString,ResponseService.class);

            }catch (Exception e) {
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

            if(responseService.isError()) {

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context)
                        .setTitle(getString(R.string.app_default_title_alert_dialog))
                        .setPositiveButton(getString(R.string.app_default_ok_alert_dialog),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int which) {
                                        dialog.dismiss();
                                    }
                                });

                if(responseService.getErrorCode().equals("E01")) {

                    alertBuilder.setMessage(getString(R.string.error_login_2));

                }else if(responseService.getErrorCode().equals("E02")) {

                    alertBuilder.setMessage(getString(R.string.error_login_3));

                }else{

                    alertBuilder.setMessage(responseService.getMessageResponse());

                }

                alertBuilder.create().show();

            }else{

                DroidPrefs.commit(context,KEY_DATA_LOGIN_KEEP,"Y");
                DroidPrefs.commit(context,KEY_DATA_LOGIN_USER,username);

                finish();
                Intent intent = new Intent(context, SelectModeActivity.class);
                startActivity(intent);

            }

        }

    }

}
