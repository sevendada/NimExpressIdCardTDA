package com.nimexpress.nimexpressidcardtdaandsigninsurance.model;

import java.util.List;

public class BillModel {

    Long id;
    String bill_no;
    String bill_date;
    String bill_time;
    String src_code;
    String src_name;
    String dest_code;
    String dest_name;
    String send_code;
    String send_full_address;
    String send_name;
    String send_company;
    String send_numtel;
    String send_mobile;
    String rec_code;
    String rec_full_address;
    String rec_name;
    String rec_company;
    String rec_numtel;
    String rec_mobile;
    String status_code;
    String bill_type;
    double total_qty;
    double total_net;

    double amount_mobile_delivery;
    double amount_cod_fee;

    double amount_selfinsurance;

    boolean flag_cal_tax;
    String payment_name_type_code;
    String payment_name;
    String payment_address1;
    String cust_tax_no;
    String cust_idcard_no;
    String ref_tax;
    double amountTax;

    List<BillDetailModel> detailList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBill_no() {
        return bill_no;
    }

    public void setBill_no(String bill_no) {
        this.bill_no = bill_no;
    }

    public String getBill_date() {
        return bill_date;
    }

    public void setBill_date(String bill_date) {
        this.bill_date = bill_date;
    }

    public String getBill_time() {
        return bill_time;
    }

    public void setBill_time(String bill_time) {
        this.bill_time = bill_time;
    }

    public String getSrc_code() {
        return src_code;
    }

    public void setSrc_code(String src_code) {
        this.src_code = src_code;
    }

    public String getSrc_name() {
        return src_name;
    }

    public void setSrc_name(String src_name) {
        this.src_name = src_name;
    }

    public String getDest_code() {
        return dest_code;
    }

    public void setDest_code(String dest_code) {
        this.dest_code = dest_code;
    }

    public String getDest_name() {
        return dest_name;
    }

    public void setDest_name(String dest_name) {
        this.dest_name = dest_name;
    }

    public String getSend_code() {
        return send_code;
    }

    public void setSend_code(String send_code) {
        this.send_code = send_code;
    }

    public String getSend_full_address() {
        return send_full_address;
    }

    public void setSend_full_address(String send_full_address) {
        this.send_full_address = send_full_address;
    }

    public String getSend_name() {
        return send_name;
    }

    public void setSend_name(String send_name) {
        this.send_name = send_name;
    }

    public String getSend_company() {
        return send_company;
    }

    public void setSend_company(String send_company) {
        this.send_company = send_company;
    }

    public String getSend_numtel() {
        return send_numtel;
    }

    public void setSend_numtel(String send_numtel) {
        this.send_numtel = send_numtel;
    }

    public String getSend_mobile() {
        return send_mobile;
    }

    public void setSend_mobile(String send_mobile) {
        this.send_mobile = send_mobile;
    }

    public String getRec_code() {
        return rec_code;
    }

    public void setRec_code(String rec_code) {
        this.rec_code = rec_code;
    }

    public String getRec_full_address() {
        return rec_full_address;
    }

    public void setRec_full_address(String rec_full_address) {
        this.rec_full_address = rec_full_address;
    }

    public String getRec_name() {
        return rec_name;
    }

    public void setRec_name(String rec_name) {
        this.rec_name = rec_name;
    }

    public String getRec_company() {
        return rec_company;
    }

    public void setRec_company(String rec_company) {
        this.rec_company = rec_company;
    }

    public String getRec_numtel() {
        return rec_numtel;
    }

    public void setRec_numtel(String rec_numtel) {
        this.rec_numtel = rec_numtel;
    }

    public String getRec_mobile() {
        return rec_mobile;
    }

    public void setRec_mobile(String rec_mobile) {
        this.rec_mobile = rec_mobile;
    }

    public String getStatus_code() {
        return status_code;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }

    public String getBill_type() {
        return bill_type;
    }

    public void setBill_type(String bill_type) {
        this.bill_type = bill_type;
    }

    public double getTotal_qty() {
        return total_qty;
    }

    public void setTotal_qty(double total_qty) {
        this.total_qty = total_qty;
    }

    public double getTotal_net() {
        return total_net;
    }

    public void setTotal_net(double total_net) {
        this.total_net = total_net;
    }

    public List<BillDetailModel> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<BillDetailModel> detailList) {
        this.detailList = detailList;
    }

    public boolean isFlag_cal_tax() {
        return flag_cal_tax;
    }

    public void setFlag_cal_tax(boolean flag_cal_tax) {
        this.flag_cal_tax = flag_cal_tax;
    }

    public String getPayment_name_type_code() {
        return payment_name_type_code;
    }

    public void setPayment_name_type_code(String payment_name_type_code) {
        this.payment_name_type_code = payment_name_type_code;
    }

    public String getPayment_name() {
        return payment_name;
    }

    public void setPayment_name(String payment_name) {
        this.payment_name = payment_name;
    }

    public String getPayment_address1() {
        return payment_address1;
    }

    public void setPayment_address1(String payment_address1) {
        this.payment_address1 = payment_address1;
    }

    public String getCust_tax_no() {
        return cust_tax_no;
    }

    public void setCust_tax_no(String cust_tax_no) {
        this.cust_tax_no = cust_tax_no;
    }

    public String getCust_idcard_no() {
        return cust_idcard_no;
    }

    public void setCust_idcard_no(String cust_idcard_no) {
        this.cust_idcard_no = cust_idcard_no;
    }

    public String getRef_tax() {
        return ref_tax;
    }

    public void setRef_tax(String ref_tax) {
        this.ref_tax = ref_tax;
    }

    public double getAmountTax() {
        return amountTax;
    }

    public void setAmountTax(double amountTax) {
        this.amountTax = amountTax;
    }

    public double getAmount_cod_fee() {
        return amount_cod_fee;
    }

    public void setAmount_cod_fee(double amount_cod_fee) {
        this.amount_cod_fee = amount_cod_fee;
    }

    public double getAmount_mobile_delivery() {
        return amount_mobile_delivery;
    }

    public void setAmount_mobile_delivery(double amount_mobile_delivery) {
        this.amount_mobile_delivery = amount_mobile_delivery;
    }

    public double getAmount_selfinsurance() {
        return amount_selfinsurance;
    }

    public void setAmount_selfinsurance(double amount_selfinsurance) {
        this.amount_selfinsurance = amount_selfinsurance;
    }

}
