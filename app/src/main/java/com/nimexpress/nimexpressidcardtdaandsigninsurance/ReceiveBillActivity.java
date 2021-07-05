package com.nimexpress.nimexpressidcardtdaandsigninsurance;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.FrameLayout;

import org.apache.commons.lang3.StringUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class ReceiveBillActivity extends AppCompatActivity {

    Context context = this;

    public static String key_step_code_data = "step_code_data";

    FrameLayout frame_fragment;

    public String stepCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_receive_bill);

        frame_fragment = (FrameLayout)findViewById(R.id.frame_fragment);

        stepCode = DroidPrefs.get(context,key_step_code_data,String.class);

        changeFragment(stepCode,true);

    }

    public void changeFragment(String stepCode,boolean isInit) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if(StringUtils.defaultString(stepCode).equals("0")) {
            fragmentTransaction.replace(R.id.frame_fragment,new ValueInsuranceDataFragment());
        }else if(StringUtils.defaultString(stepCode).equals("1")) {
            fragmentTransaction.replace(R.id.frame_fragment,new BillDataFragment());
        }else if(StringUtils.defaultString(stepCode).equals("2")) {
            fragmentTransaction.replace(R.id.frame_fragment,new BarcodeInputDataFragment());
        }else if(StringUtils.defaultString(stepCode).equals("3")) {
            fragmentTransaction.replace(R.id.frame_fragment,new TaxBillDataFragment());
        }else if(StringUtils.defaultString(stepCode).equals("4")) {
            fragmentTransaction.replace(R.id.frame_fragment,new SummaryBillDataFragment());
        }else if(StringUtils.defaultString(stepCode).equals("5")) {
            fragmentTransaction.replace(R.id.frame_fragment,new PaymentBillDataFragment());
        }

        if(!isInit) {
            fragmentTransaction.addToBackStack(null).commit();
        }else{
            fragmentTransaction.commit();
        }

    }

}
