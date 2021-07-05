package com.nimexpress.nimexpressidcardtdaandsigninsurance.model;

public class QRPaymentTransaction {

    private Long id;

    private String date_doc;

    private String time_doc;

    private Long bill_id;

    private String reference1;

    private String reference2;

    private boolean flag_confirm;

    private String data_type;

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

    public Long getBill_id() {
        return bill_id;
    }

    public void setBill_id(Long bill_id) {
        this.bill_id = bill_id;
    }

    public String getReference1() {
        return reference1;
    }

    public void setReference1(String reference1) {
        this.reference1 = reference1;
    }

    public String getReference2() {
        return reference2;
    }

    public void setReference2(String reference2) {
        this.reference2 = reference2;
    }

    public boolean isFlag_confirm() {
        return flag_confirm;
    }

    public void setFlag_confirm(boolean flag_confirm) {
        this.flag_confirm = flag_confirm;
    }

    public String getData_type() {
        return data_type;
    }

    public void setData_type(String data_type) {
        this.data_type = data_type;
    }

}
