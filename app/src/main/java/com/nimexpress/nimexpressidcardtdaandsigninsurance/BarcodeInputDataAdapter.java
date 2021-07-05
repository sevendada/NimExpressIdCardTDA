package com.nimexpress.nimexpressidcardtdaandsigninsurance;


import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.nimexpress.nimexpressidcardtdaandsigninsurance.model.BarcodeModel;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import info.hoang8f.widget.FButton;

public class BarcodeInputDataAdapter extends RecyclerView.Adapter<BarcodeInputDataAdapter.BarcodeInputDataViewHolder> {

    List<BarcodeModel> barcodeModelList = new ArrayList<>();
    String dataBilLTpye = "";

    public void setBarcodeModelList(List<BarcodeModel> barcodeModelList) {
        this.barcodeModelList = barcodeModelList;
    }

    public void setDataBilLTpye(String dataBilLTpye) {
        this.dataBilLTpye = dataBilLTpye;
    }

    EventAdapterListener eventAdapterListener;

    public interface EventAdapterListener {
        void onEvent(BarcodeModel barcodeModel,int position);
    }

    public void setEventAdapterListener(EventAdapterListener eventAdapterListener) {
        this.eventAdapterListener = eventAdapterListener;
    }

    @Override
    public BarcodeInputDataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_barcode_item_data, parent, false);

        return new BarcodeInputDataViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final BarcodeInputDataViewHolder holder, final int position) {

        final BarcodeModel barcodeData = barcodeModelList.get(position);

        holder.text_data_barcode.setText("บาร์โค้ด : " + StringUtils.defaultString(barcodeData.getBc_no()));
        holder.text_data_barcode_runno.setText(StringUtils.defaultString(barcodeData.getBc_run_no()));
        holder.text_data_product.setText("สินค้า : " + StringUtils.defaultString(barcodeData.getProduct_desc()));

        holder.text_input_width.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    Double double1 = new Double(s.toString());
                    barcodeData.setSize_width(double1);
                } catch (Exception e) {
                    barcodeData.setSize_width(0);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}

        });

        holder.text_input_width.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    holder.text_input_long.requestFocus();
                }

                return false;

            }
        });

        holder.text_input_long.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    Double double1 = new Double(s.toString());
                    barcodeData.setSize_long(double1);
                } catch (Exception e) {
                    barcodeData.setSize_long(0);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}

        });

        holder.text_input_height.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    Double double1 = new Double(s.toString());
                    barcodeData.setSize_height(double1);
                } catch (Exception e) {
                    barcodeData.setSize_height(0);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}

        });

        holder.text_input_weight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    Double double1 = new Double(s.toString());
                    barcodeData.setWeight_kg(double1);
                } catch (Exception e) {
                    barcodeData.setWeight_kg(0);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}

        });

        holder.text_input_temperature.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                barcodeData.setTemperature(StringUtils.defaultString(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable editable) {}

        });

        holder.bt_confirm_box_nimex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                eventAdapterListener.onEvent(barcodeData,position);

            }
        });

        holder.bt_cancel_box_nimex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                barcodeData.setBox_nimexpress_bc("");
                barcodeData.setBox_product_id(null);
                barcodeData.setBox_product_code("");

                barcodeData.setSize_width(0);
                barcodeData.setSize_long(0);
                barcodeData.setSize_height(0);

                notifyDataSetChanged();

            }
        });

        if(StringUtils.isBlank(barcodeData.getBox_nimexpress_bc())) {

            holder.text_input_width.setEnabled(true);
            holder.text_input_long.setEnabled(true);
            holder.text_input_height.setEnabled(true);
            holder.text_input_weight.setEnabled(true);
            holder.text_input_temperature.setEnabled(true);

            holder.text_input_width.setBackgroundDrawable(ContextCompat.getDrawable(holder.text_input_width.getContext(), R.drawable.input_style));
            holder.text_input_long.setBackgroundDrawable(ContextCompat.getDrawable(holder.text_input_long.getContext(), R.drawable.input_style));
            holder.text_input_height.setBackgroundDrawable(ContextCompat.getDrawable(holder.text_input_height.getContext(), R.drawable.input_style));
            holder.text_input_weight.setBackgroundDrawable(ContextCompat.getDrawable(holder.text_input_weight.getContext(), R.drawable.input_style));
            holder.text_input_temperature.setBackgroundDrawable(ContextCompat.getDrawable(holder.text_input_temperature.getContext(), R.drawable.input_style));

            holder.bt_confirm_box_nimex.setEnabled(true);
            holder.bt_confirm_box_nimex.setButtonColor(holder.bt_confirm_box_nimex.getContext().getResources().getColor(R.color.fbutton_color_peter_river));
            holder.bt_confirm_box_nimex.setShadowColor(holder.bt_confirm_box_nimex.getContext().getResources().getColor(R.color.fbutton_color_belize_hole));

            holder.bt_cancel_box_nimex.setEnabled(false);
            holder.bt_cancel_box_nimex.setButtonColor(holder.bt_cancel_box_nimex.getContext().getResources().getColor(R.color.fbutton_color_concrete));
            holder.bt_cancel_box_nimex.setShadowColor(holder.bt_cancel_box_nimex.getContext().getResources().getColor(R.color.fbutton_color_asbestos));

            holder.text_barcode_box.setText("บาร์โค้ดกล่อง NiMExpress");

        }else{

            holder.text_input_width.setEnabled(false);
            holder.text_input_long.setEnabled(false);
            holder.text_input_height.setEnabled(false);
            //holder.text_input_weight.setEnabled(false);
            //holder.text_input_temperature.setEnabled(false);

            holder.text_input_width.setBackgroundDrawable(ContextCompat.getDrawable(holder.text_input_width.getContext(), R.drawable.input_disable_style));
            holder.text_input_long.setBackgroundDrawable(ContextCompat.getDrawable(holder.text_input_long.getContext(), R.drawable.input_disable_style));
            holder.text_input_height.setBackgroundDrawable(ContextCompat.getDrawable(holder.text_input_height.getContext(), R.drawable.input_disable_style));
            //holder.text_input_weight.setBackgroundDrawable(ContextCompat.getDrawable(holder.text_input_weight.getContext(), R.drawable.input_disable_style));
            //holder.text_input_temperature.setBackgroundDrawable(ContextCompat.getDrawable(holder.text_input_temperature.getContext(), R.drawable.input_disable_style));

            holder.bt_confirm_box_nimex.setEnabled(false);
            holder.bt_confirm_box_nimex.setButtonColor(holder.bt_confirm_box_nimex.getContext().getResources().getColor(R.color.fbutton_color_concrete));
            holder.bt_confirm_box_nimex.setShadowColor(holder.bt_confirm_box_nimex.getContext().getResources().getColor(R.color.fbutton_color_asbestos));

            holder.bt_cancel_box_nimex.setEnabled(true);
            holder.bt_cancel_box_nimex.setButtonColor(holder.bt_cancel_box_nimex.getContext().getResources().getColor(R.color.fbutton_color_alizarin));
            holder.bt_cancel_box_nimex.setShadowColor(holder.bt_cancel_box_nimex.getContext().getResources().getColor(R.color.fbutton_color_pumpkin));

            holder.text_barcode_box.setText(barcodeData.getBox_nimexpress_bc());

        }

        holder.text_input_width.setText(convertDouble(barcodeData.getSize_width()));
        holder.text_input_long.setText(convertDouble(barcodeData.getSize_long()));
        holder.text_input_height.setText(convertDouble(barcodeData.getSize_height()));
        holder.text_input_weight.setText(convertDouble(barcodeData.getWeight_kg()));
        holder.text_input_temperature.setText(StringUtils.defaultString(barcodeData.getTemperature()));

        if(StringUtils.defaultString(dataBilLTpye).contains("CHILLED") ||
                StringUtils.defaultString(dataBilLTpye).contains("FROZEN")) {

            holder.text_input_temperature.setVisibility(View.VISIBLE);

        }else{

            holder.text_input_temperature.setVisibility(View.GONE);

        }

    }

    @Override
    public int getItemCount() {
        return barcodeModelList.size();
    }

    public String convertDouble(double value) {
        if(value <= 0) {
            return "";
        }else{
            if ((value == Math.floor(value)) && !Double.isInfinite(value)) {
                // integer type
                return String.valueOf(new Double(value).intValue());
            }else{
                return String.valueOf(value);
            }
        }
    }

    public class BarcodeInputDataViewHolder extends RecyclerView.ViewHolder {

        TextView text_data_barcode;
        TextView text_data_barcode_runno;
        TextView text_data_product;

        EditText text_input_width;
        EditText text_input_long;
        EditText text_input_height;
        EditText text_input_weight;
        EditText text_input_temperature;

        TextView text_barcode_box;
        FButton bt_confirm_box_nimex;
        FButton bt_cancel_box_nimex;

        public BarcodeInputDataViewHolder(View itemView) {

            super(itemView);

            text_data_barcode = (TextView)itemView.findViewById(R.id.text_data_barcode);
            text_data_barcode_runno = (TextView)itemView.findViewById(R.id.text_data_barcode_runno);
            text_data_product = (TextView)itemView.findViewById(R.id.text_data_product);

            text_input_width = (EditText)itemView.findViewById(R.id.text_input_width);
            text_input_long = (EditText)itemView.findViewById(R.id.text_input_long);
            text_input_height = (EditText)itemView.findViewById(R.id.text_input_height);
            text_input_weight = (EditText)itemView.findViewById(R.id.text_input_weight);
            text_input_temperature = (EditText)itemView.findViewById(R.id.text_input_temperature);

            text_barcode_box = (TextView)itemView.findViewById(R.id.text_barcode_box);
            bt_confirm_box_nimex = (FButton)itemView.findViewById(R.id.bt_confirm_box_nimex);
            bt_cancel_box_nimex = (FButton)itemView.findViewById(R.id.bt_cancel_box_nimex);

        }

    }

}
