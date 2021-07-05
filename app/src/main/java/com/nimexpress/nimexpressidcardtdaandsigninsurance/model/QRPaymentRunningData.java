package com.nimexpress.nimexpressidcardtdaandsigninsurance.model;

public class QRPaymentRunningData {

    private Long id;

    private String date_doc;

    private String time_doc;

    private String data_dc;

    private String data_running;

    private boolean flag_confirm;

    private Long bill_id;

    private String bill_no;

    private float total_net;

    private double amount_qr;

    private String tmp_qrpayment_type;
    private String tmp_qrpayment_type_code;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate_doc() {
        return date_doc;
    }

    public void setDate_doc(String date_doc) {
        this.date_doc = date_doc;
    }

    public String getTime_doc() {
        return time_doc;
    }

    public void setTime_doc(String time_doc) {
        this.time_doc = time_doc;
    }

    public String getData_dc() {
        return data_dc;
    }

    public void setData_dc(String data_dc) {
        this.data_dc = data_dc;
    }

    public String getData_running() {
        return data_running;
    }

    public void setData_running(String data_running) {
        this.data_running = data_running;
    }

    public boolean isFlag_confirm() {
        return flag_confirm;
    }

    public void setFlag_confirm(boolean flag_confirm) {
        this.flag_confirm = flag_confirm;
    }

    public Long getBill_id() {
        return bill_id;
    }

    public void setBill_id(Long bill_id) {
        this.bill_id = bill_id;
    }

    public String getBill_no() {
        return bill_no;
    }

    public void setBill_no(String bill_no) {
        this.bill_no = bill_no;
    }

    public float getTotal_net() {
        return total_net;
    }

    public void setTotal_net(float total_net) {
        this.total_net = total_net;
    }

    public double getAmount_qr() {
        return amount_qr;
    }

    public void setAmount_qr(double amount_qr) {
        this.amount_qr = amount_qr;
    }

    public String getTmp_qrpayment_type() {
        return tmp_qrpayment_type;
    }

    public void setTmp_qrpayment_type(String tmp_qrpayment_type) {
        this.tmp_qrpayment_type = tmp_qrpayment_type;
    }

    public String getTmp_qrpayment_type_code() {
        return tmp_qrpayment_type_code;
    }

    public void setTmp_qrpayment_type_code(String tmp_qrpayment_type_code) {
        this.tmp_qrpayment_type_code = tmp_qrpayment_type_code;
    }

}
