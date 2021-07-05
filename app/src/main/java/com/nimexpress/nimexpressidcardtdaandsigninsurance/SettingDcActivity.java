package com.nimexpress.nimexpressidcardtdaandsigninsurance;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import androidx.appcompat.app.AlertDialog; import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SettingDcActivity extends AppCompatActivity {

    public static String key_data_setting_dc = "settting_dc";

    Context context = this;

    ProgressDialog progressDialog;
    TextView text_dc_display;
    Button button_selected_data;
    Button button_save_data_dc;

    List<ClassDet> classDetList = new ArrayList<>();
    ClassDet classDetSelected = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_dc);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getString(R.string.activity_setting_dc_title));

        classDetSelected = DroidPrefs.get(context,key_data_setting_dc,ClassDet.class);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_default_loading));
        progressDialog.setMessage(getString(R.string.app_default_loading_message));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        text_dc_display = (TextView) findViewById(R.id.text_dc_display);
        button_selected_data = (Button)findViewById(R.id.button_selected_data);
        button_save_data_dc = (Button)findViewById(R.id.button_save_data_dc);

        button_selected_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
                builderSingle.setTitle("เลือกข้อมูล DC หรือ SubDc");
                ArrayAdapter<ClassDet> arrayAdapter = new ArrayAdapter<ClassDet>(context, android.R.layout.select_dialog_singlechoice);
                arrayAdapter.addAll(classDetList);
                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        classDetSelected = classDetList.get(which);
                        text_dc_display.setText(classDetSelected.toString());
                    }
                });
                builderSingle.show();
            }
        });


        button_save_data_dc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(classDetSelected != null) {
                    DroidPrefs.commit(context,key_data_setting_dc,classDetSelected);
                }
                finish();
            }
        });

        TaskGetDcAndSubDcData taskGetDcAndSubDcData = new TaskGetDcAndSubDcData(context);
        taskGetDcAndSubDcData.execute();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    class TaskGetDcAndSubDcData extends AsyncTask<Void,Void,List<ClassDet>> {

        TaskGetDcAndSubDcData(Context context){

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected List<ClassDet> doInBackground(Void... voids) {

            String urlService = MainUrl.url+"/MobileDCAndSubDcDataService.htm?action=getData";

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

                Type type = new ListParameterizedType(ClassDet.class);

                List<ClassDet> classDetList = new Gson().fromJson(returnData,type);

                return  classDetList;

            }catch (Exception e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(List<ClassDet> classDets) {
            super.onPostExecute(classDets);
            progressDialog.dismiss();

            if(classDets != null) {

                ClassDet classDetNotSelected = new ClassDet();
                classDetNotSelected.setSub_class_id("");
                classDetNotSelected.setSub_class_name("ไม่ระบุ");
                classDets.add(0,classDetNotSelected);

                classDetList = new ArrayList<>(classDets);

                if(classDetSelected != null) {
                    text_dc_display.setText(classDetSelected.toString());
                }

//                dropdown_list_dc_data.setItems(classDets);
//
//                if(classDetSelected != null) {
//
//                    for (int i = 1;i<classDets.size();i++){
//
//                        ClassDet classDet = classDets.get(i);
//
//                        if(classDet.getSub_class_id().equals(classDetSelected.getSub_class_id())) {
//
//                            dropdown_list_dc_data.setSelectedIndex(i);
//                            break;
//
//                        }
//
//                    }
//
//                }

            }

        }
    }

    private static class ListParameterizedType implements ParameterizedType {

        private Type type;

        private ListParameterizedType(Type type) {
            this.type = type;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[] {type};
        }

        @Override
        public Type getRawType() {
            return ArrayList.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }

    }


}
