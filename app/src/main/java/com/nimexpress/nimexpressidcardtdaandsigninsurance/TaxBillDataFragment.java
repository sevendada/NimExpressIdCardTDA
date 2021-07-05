package com.nimexpress.nimexpressidcardtdaandsigninsurance;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import androidx.appcompat.app.AlertDialog; import androidx.fragment.app.Fragment;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.nimexpress.nimexpressidcardtdaandsigninsurance.model.BillModel;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;

public class TaxBillDataFragment extends Fragment {

    CheckBox chk_is_tax;

    RadioGroup radioGroupTaxType;
    RadioButton radio_tax_type1;
    RadioButton radio_tax_type2;
    RadioButton radio_tax_type3;

    EditText text_input_tax_payment_name;
    EditText text_input_tax_payment_address;
    EditText text_input_tax_no;
    EditText text_input_tax_id_card;
    EditText text_input_tax_ref_no;
    EditText text_input_tax_amount;

    BillModel billModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tax_bill_data, container, false);

        getActivity().setTitle("ข้อมูลภาษีหัก ณ ที่จ่าย");

        chk_is_tax = (CheckBox)view.findViewById(R.id.chk_is_tax);

        radioGroupTaxType = (RadioGroup)view.findViewById(R.id.radioGroupTaxType);
        radio_tax_type1 = (RadioButton)view.findViewById(R.id.radio_tax_type1);
        radio_tax_type2 = (RadioButton)view.findViewById(R.id.radio_tax_type2);
        radio_tax_type3 = (RadioButton)view.findViewById(R.id.radio_tax_type3);

        text_input_tax_payment_name = (EditText)view.findViewById(R.id.text_input_tax_payment_name);
        text_input_tax_payment_address = (EditText)view.findViewById(R.id.text_input_tax_payment_address);
        text_input_tax_no = (EditText)view.findViewById(R.id.text_input_tax_no);
        text_input_tax_id_card = (EditText)view.findViewById(R.id.text_input_tax_id_card);
        text_input_tax_ref_no = (EditText)view.findViewById(R.id.text_input_tax_ref_no);
        text_input_tax_amount = (EditText)view.findViewById(R.id.text_input_tax_amount);

        text_input_tax_amount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        text_input_tax_amount.setKeyListener(new DigitsKeyListener(false, true));

        billModel = DroidPrefs.get(getContext(),BillDataFragment.KEY_RECEIVE_BILL_DATA,BillModel.class);

        EnableControl(false);

        chk_is_tax.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) {

                    EnableControl(true);

                    if(radio_tax_type1.isChecked()) {

                        String fullNameSender = billModel.getSend_company();
                        String fullAddressSender = billModel.getSend_full_address();

                        text_input_tax_payment_name.setText(fullNameSender);
                        text_input_tax_payment_address.setText(fullAddressSender);
                        text_input_tax_no.setText("");
                        text_input_tax_id_card.setText("");
                        text_input_tax_ref_no.setText("");

                    }else {

                        text_input_tax_payment_name.setText("");
                        text_input_tax_payment_address.setText("");
                        text_input_tax_no.setText("");
                        text_input_tax_id_card.setText("");
                        text_input_tax_ref_no.setText("");

                    }

                    DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
                    double totalNet = billModel.getTotal_net() - billModel.getAmount_cod_fee();
                    double codFee = billModel.getAmount_cod_fee();

                    double taxDeduce = totalNet * 0.01;
                    double taxDeduce2 = ((codFee*100)/107) * 0.03;

                    text_input_tax_amount.setText(decimalFormat.format(taxDeduce+taxDeduce2));

                }else {

                    EnableControl(false);

                }

            }
        });

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

        if (item.getItemId() == R.id.menu_action_done) {

            String taxAmount = text_input_tax_amount.getText().toString();
            String taxPaymentName = text_input_tax_payment_name.getText().toString();
            String taxPaymentAddress = text_input_tax_payment_address.getText().toString();
            String taxNo = text_input_tax_no.getText().toString();
            String taxIdCardNo = text_input_tax_id_card.getText().toString();
            String taxRefNo = text_input_tax_ref_no.getText().toString();

            boolean isPassData = true;

            if(chk_is_tax.isChecked()) {

                if (StringUtils.isBlank(taxAmount) ||
                        StringUtils.isBlank(taxPaymentName) ||
                        StringUtils.isBlank(taxPaymentAddress) ||
                        StringUtils.isBlank(taxNo) ||
                        StringUtils.isBlank(taxIdCardNo) ||
                        StringUtils.isBlank(taxRefNo)) {

                    isPassData = false;

                }

            }

            if(isPassData) {

                if (billModel != null) {


                    if (StringUtils.isNotBlank(taxAmount)) {

                        billModel.setFlag_cal_tax(true);

                        if(radio_tax_type1.isChecked()) {
                            billModel.setPayment_name_type_code("01");
                        }else if(radio_tax_type3.isChecked()) {
                            billModel.setPayment_name_type_code("03");
                        }

                        billModel.setPayment_name(taxPaymentName);
                        billModel.setPayment_address1(taxPaymentAddress);
                        billModel.setCust_tax_no(taxNo);
                        billModel.setCust_idcard_no(taxIdCardNo);
                        billModel.setRef_tax(taxRefNo);

                        billModel.setAmountTax(Double.parseDouble(taxAmount));
                        DroidPrefs.commit(getContext(), BillDataFragment.KEY_RECEIVE_BILL_DATA, billModel);

                    } else {

                        billModel.setFlag_cal_tax(false);

                        billModel.setPayment_name_type_code("");

                        billModel.setPayment_name("");
                        billModel.setPayment_address1("");
                        billModel.setCust_tax_no("");
                        billModel.setCust_idcard_no("");
                        billModel.setRef_tax("");

                        billModel.setAmountTax(0);

                        DroidPrefs.commit(getContext(), BillDataFragment.KEY_RECEIVE_BILL_DATA, billModel);
                    }

                }

                //next step
                ReceiveBillActivity receiveBillActivity = (ReceiveBillActivity) getActivity();

                receiveBillActivity.changeFragment("4", false);

            }else{

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(false);
                builder.setMessage("กรุณากรอหข้อมูลให้ครบถ้วน");
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }

        }

        return super.onOptionsItemSelected(item);

    }

    public void EnableControl(boolean isEnable) {

        if(isEnable) {

            radioGroupTaxType.setEnabled(true);
            radio_tax_type1.setEnabled(true);
            radio_tax_type2.setEnabled(true);
            radio_tax_type3.setEnabled(true);

            text_input_tax_payment_name.setEnabled(true);
            text_input_tax_payment_address.setEnabled(true);
            text_input_tax_no.setEnabled(true);
            text_input_tax_id_card.setEnabled(true);
            text_input_tax_ref_no.setEnabled(true);
            text_input_tax_amount.setEnabled(true);

            text_input_tax_payment_name.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.input_style));
            text_input_tax_payment_address.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.input_style));
            text_input_tax_no.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.input_style));
            text_input_tax_id_card.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.input_style));
            text_input_tax_ref_no.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.input_style));
            text_input_tax_amount.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.input_style));

        }else {

            radioGroupTaxType.setEnabled(false);
            radio_tax_type1.setEnabled(false);
            radio_tax_type2.setEnabled(false);
            radio_tax_type3.setEnabled(false);

            text_input_tax_payment_name.setEnabled(false);
            text_input_tax_payment_address.setEnabled(false);
            text_input_tax_no.setEnabled(false);
            text_input_tax_id_card.setEnabled(false);
            text_input_tax_ref_no.setEnabled(false);
            text_input_tax_amount.setEnabled(false);

            text_input_tax_payment_name.setText("");
            text_input_tax_payment_address.setText("");
            text_input_tax_no.setText("");
            text_input_tax_id_card.setText("");
            text_input_tax_ref_no.setText("");
            text_input_tax_amount.setText("");

            text_input_tax_payment_name.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.input_disable_style));
            text_input_tax_payment_address.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.input_disable_style));
            text_input_tax_no.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.input_disable_style));
            text_input_tax_id_card.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.input_disable_style));
            text_input_tax_ref_no.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.input_disable_style));
            text_input_tax_amount.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.input_disable_style));

        }

    }

}
