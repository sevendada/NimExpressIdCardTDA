package com.nimexpress.nimexpressidcardtdaandsigninsurance;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import androidx.appcompat.app.AlertDialog; import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;

public class ValueInsuranceDataFragment extends Fragment {

    public static String KEY_VALUE_INSURANCE_DATA = "VALUE_INSURANCE_DATA";

    EditText text_value_insurance;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_value_insurance_data, container, false);

        getActivity().setTitle("รายละเอียดมูลค่าสินค้า");

        text_value_insurance = (EditText)view.findViewById(R.id.text_value_insurance);

        setHasOptionsMenu(true);

        return view;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_receive_bill, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.menu_action_done) {

            String textValueInsurance = text_value_insurance.getText().toString();

            if(StringUtils.isBlank(textValueInsurance)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(false);
                builder.setMessage("กรุณาระบุมูลค่าสินค้า");
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }else {

                DroidPrefs.commit(getContext(),KEY_VALUE_INSURANCE_DATA,textValueInsurance);

                ReceiveBillActivity receiveBillActivity = (ReceiveBillActivity) getActivity();

                receiveBillActivity.changeFragment("1", false);

                return true;

            }

        }

        return super.onOptionsItemSelected(item);

    }

}
