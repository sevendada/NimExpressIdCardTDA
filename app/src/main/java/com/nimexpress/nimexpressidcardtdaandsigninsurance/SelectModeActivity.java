package com.nimexpress.nimexpressidcardtdaandsigninsurance;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.nimexpress.nimexpressidcardtdaandsigninsurance.model.MobileKeyFixSqlModel;

public class SelectModeActivity extends AppCompatActivity {

    Context context = this;

    public static String key_ismode_data = "ismode_data";

    TextView text_app_version_data;
    TextView text_dc_data;
    Button button_send;
    Button button_rec_dc;
    Button button_rec_bill_1_only;
    Button button_logout;
    int count_click_fix_m = 1;
    SQLiteDatabase sqLiteDatabase;
    DBHelperMobileKey dbHelperMobileKey;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_mode);

        setTitle(getString(R.string.select_mode_activity_title));

        text_app_version_data = (TextView)findViewById(R.id.text_app_version_data);
        text_dc_data = (TextView)findViewById(R.id.text_dc_data);
        button_send = (Button)findViewById(R.id.button_send);
        button_rec_dc = (Button)findViewById(R.id.button_rec_dc);
        button_rec_bill_1_only = (Button)findViewById(R.id.button_rec_bill_1_only);
        button_logout = (Button)findViewById(R.id.button_logout);
        dbHelperMobileKey = new DBHelperMobileKey(context);
        //fix
        button_logout.setVisibility(View.GONE);

        String isDroppoint = DroidPrefs.get(context,SplashScreenViewActivity.key_data_is_droppoint2,String.class);

        if(isDroppoint.equals("false")) {
            button_rec_dc.setText("รับเองที่ DC");
            button_rec_bill_1_only.setVisibility(View.VISIBLE);
            //button_logout.setVisibility(View.VISIBLE);
        }else{
            button_rec_dc.setText("รับเองที่ DP");
            button_rec_bill_1_only.setVisibility(View.GONE);
            //button_logout.setVisibility(View.GONE);
        }

        text_app_version_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count_click_fix_m == 3) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SelectModeActivity.this);
                    android.app.AlertDialog dialog;
                    View layout = getLayoutInflater().inflate(R.layout.pop_up_pass_unlock_service, null);
                    builder.setView(layout);
                    final EditText pass_unlock = (EditText) layout.findViewById(R.id.ed_pass_unlock);
                    builder.setCancelable(false);
                    builder.setPositiveButton("UnLock", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (pass_unlock.getText().toString().equals("2637")) {
                                android.app.AlertDialog.Builder builder_edit_service = new android.app.AlertDialog.Builder(SelectModeActivity.this);
                                View layout_edit = getLayoutInflater().inflate(R.layout.pop_up_edit_service_other, null);
                                builder_edit_service.setCancelable(false);
                                builder_edit_service.setView(layout_edit);
                                final EditText ed_fix_mobile = (EditText) layout_edit.findViewById(R.id.ed_service_url);
                                Button btn_mobile_key_cancel = (Button)layout_edit.findViewById(R.id.btn_mobile_key_cancel);
                                Button btn_get_key = (Button)layout_edit.findViewById(R.id.btn_get_key);
                                ed_fix_mobile.setHint("Enter Fix Mobile Key Code");
                                builder_edit_service.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (ed_fix_mobile.length() == 0) {
                                            Toast.makeText(SelectModeActivity.this, "Your Input MobileKey is empty ", Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(SelectModeActivity.this, "Your Input MobileKey is : " + ed_fix_mobile.getText().toString(), Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(SelectModeActivity.this, "ModesetMobileKey is off ", Toast.LENGTH_SHORT).show();
                                        ReActivity();
                                    }
                                });
                            } else {
                                //DeviceId.ModesetMobileKey = false;
                                dialog.dismiss();
                                Toast.makeText(SelectModeActivity.this, "Incorrect password!!", Toast.LENGTH_SHORT).show();
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

        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DroidPrefs.commit(context,key_ismode_data,"send");
                DroidPrefs.commit(context,SignInsuranceActivity.key_receive_bill_data,null);
                Intent intent = new Intent(context,SelectedTypeCardActivity.class);
                startActivity(intent);
            }
        });

        button_rec_bill_1_only.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DroidPrefs.commit(context,key_ismode_data,"send");
                DroidPrefs.commit(context,SignInsuranceActivity.key_receive_bill_data,"Y");
                Intent intent = new Intent(context,SelectedTypeCardActivity.class);
                startActivity(intent);
            }
        });

        button_rec_dc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DroidPrefs.commit(context,key_ismode_data,"rec");
                DroidPrefs.commit(context,SignInsuranceActivity.key_receive_bill_data,null);
                Intent intent = new Intent(context,SelectedTypeCardActivity.class);
                startActivity(intent);
            }
        });

        button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DroidPrefs.commit(context,LoginViewActivity.KEY_DATA_LOGIN_KEEP,null);
                DroidPrefs.commit(context,LoginViewActivity.KEY_DATA_LOGIN_USER,null);
                finish();
                Intent intent = new Intent(context, LoginViewActivity.class);
                startActivity(intent);

            }
        });

        ClassDet classDetMobileKeyCodeDC = DroidPrefs.get(context,SplashScreenViewActivity.key_data_mobilekeycode_dc,ClassDet.class);
        ClassDet classDetDcSetting = DroidPrefs.get(context,SettingDcActivity.key_data_setting_dc,ClassDet.class);

        if(classDetMobileKeyCodeDC != null) {

            text_dc_data.setText(getString(R.string.activity_dc_data_capture,classDetMobileKeyCodeDC.getSub_class_value()+" "+classDetMobileKeyCodeDC.getSub_class_name()));

        }else{

            if(classDetDcSetting != null) {
                text_dc_data.setText(getString(R.string.activity_dc_data_capture, classDetDcSetting.getSub_class_value() + " " + classDetDcSetting.getSub_class_name()));
            }else{
                text_dc_data.setText("ไม่ได้ระบุ");
            }

        }

/*        int appVersionData = getVersion();
        if(appVersionData > 0) {
            text_app_version_data.setText("version : " + String.valueOf(appVersionData));
        }*/
        String verApp = "Ver." + getAppVersion(getPackageName());
        if (DeviceId.ModesetMobileKey) {
            text_app_version_data.setText(verApp + "\n ModeFixMobileKey On->" + DeviceId.MobileCustom);
        } else {
            text_app_version_data.setText(verApp);
        }
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this,permissions, 1);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
        Intent i = new Intent(SelectModeActivity.this, SplashScreenViewActivity.class);
        finish();
        overridePendingTransition(0, 0);
        startActivity(i);
        overridePendingTransition(0, 0);
    }

    private int getAppVersion(String packageName) {

        PackageInfo packageInfo = getPackageInfoData(packageName);

        if (packageInfo == null) {
            return 0;
        } else {
            return packageInfo.versionCode;
        }

    }

    private PackageInfo getPackageInfoData(String packageName) {
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pInfo;
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

}
