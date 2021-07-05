package com.nimexpress.nimexpressidcardtdaandsigninsurance.model;

public class ImageSignInsurance {

    private Long id;

    private String code;

    private String data_date;

    private String data_dc;

    private String data_running;

    private Long signature_id;

    private Long ref_id;

    private String ref_code;

    private String ref_class;

    private boolean flag_use;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getData_date() {
        return data_date;
    }

    public void setData_date(String data_date) {
        this.data_date = data_date;
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

    public Long getSignature_id() {
        return signature_id;
    }

    public void setSignature_id(Long signature_id) {
        this.signature_id = signature_id;
    }

    public Long getRef_id() {
        return ref_id;
    }

    public void setRef_id(Long ref_id) {
        this.ref_id = ref_id;
    }

    public String getRef_code() {
        return ref_code;
    }

    public void setRef_code(String ref_code) {
        this.ref_code = ref_code;
    }

    public String getRef_class() {
        return ref_class;
    }

    public void setRef_class(String ref_class) {
        this.ref_class = ref_class;
    }

    public boolean isFlag_use() {
        return flag_use;
    }

    public void setFlag_use(boolean flag_use) {
        this.flag_use = flag_use;
    }

}
