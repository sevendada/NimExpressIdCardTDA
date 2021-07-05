package com.nimexpress.nimexpressidcardtdaandsigninsurance.model;

public class BoxNimExpress {

    boolean flag_use;
    String barcode;
    Long product_id;
    String product_code;
    String size_width;
    String size_long;
    String size_height;

    public boolean isFlag_use() {
        return flag_use;
    }

    public void setFlag_use(boolean flag_use) {
        this.flag_use = flag_use;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Long getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Long product_id) {
        this.product_id = product_id;
    }

    public String getProduct_code() {
        return product_code;
    }

    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

    public String getSize_width() {
        return size_width;
    }

    public void setSize_width(String size_width) {
        this.size_width = size_width;
    }

    public String getSize_long() {
        return size_long;
    }

    public void setSize_long(String size_long) {
        this.size_long = size_long;
    }

    public String getSize_height() {
        return size_height;
    }

    public void setSize_height(String size_height) {
        this.size_height = size_height;
    }
}
