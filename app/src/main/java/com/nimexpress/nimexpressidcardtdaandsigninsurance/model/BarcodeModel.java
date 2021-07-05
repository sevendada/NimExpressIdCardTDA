package com.nimexpress.nimexpressidcardtdaandsigninsurance.model;

public class BarcodeModel {

    Long id;
    String bc_no;
    String bc_run_no;
    Long parent_id;
    String product_desc;
    Long tmp_product_id;

    double size_width;
    double size_long;
    double size_height;
    double weight_kg;
    String temperature;

    String box_nimexpress_bc;
    Long box_product_id;
    String box_product_code;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBc_no() {
        return bc_no;
    }

    public void setBc_no(String bc_no) {
        this.bc_no = bc_no;
    }

    public String getBc_run_no() {
        return bc_run_no;
    }

    public void setBc_run_no(String bc_run_no) {
        this.bc_run_no = bc_run_no;
    }

    public Long getParent_id() {
        return parent_id;
    }

    public void setParent_id(Long parent_id) {
        this.parent_id = parent_id;
    }

    public String getProduct_desc() {
        return product_desc;
    }

    public void setProduct_desc(String product_desc) {
        this.product_desc = product_desc;
    }

    public Long getTmp_product_id() {
        return tmp_product_id;
    }

    public void setTmp_product_id(Long tmp_product_id) {
        this.tmp_product_id = tmp_product_id;
    }

    public double getSize_width() {
        return size_width;
    }

    public void setSize_width(double size_width) {
        this.size_width = size_width;
    }

    public double getSize_long() {
        return size_long;
    }

    public void setSize_long(double size_long) {
        this.size_long = size_long;
    }

    public double getSize_height() {
        return size_height;
    }

    public void setSize_height(double size_height) {
        this.size_height = size_height;
    }

    public double getWeight_kg() {
        return weight_kg;
    }

    public void setWeight_kg(double weight_kg) {
        this.weight_kg = weight_kg;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getBox_nimexpress_bc() {
        return box_nimexpress_bc;
    }

    public void setBox_nimexpress_bc(String box_nimexpress_bc) {
        this.box_nimexpress_bc = box_nimexpress_bc;
    }

    public Long getBox_product_id() {
        return box_product_id;
    }

    public void setBox_product_id(Long box_product_id) {
        this.box_product_id = box_product_id;
    }

    public String getBox_product_code() {
        return box_product_code;
    }

    public void setBox_product_code(String box_product_code) {
        this.box_product_code = box_product_code;
    }

}
