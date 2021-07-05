package com.nimexpress.nimexpressidcardtdaandsigninsurance;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by MacPro on 3/13/18.
 */

public class ClassDet {

    String sub_class_id;
    String sub_class_name;
    String sub_class_value;
    String remark1;
    String remark2;
    String remark3;
    String remark4;
    String remark5;

    public String getRemark1() {
        return remark1;
    }

    public void setRemark1(String remark1) {
        this.remark1 = remark1;
    }

    public String getRemark2() {
        return remark2;
    }

    public void setRemark2(String remark2) {
        this.remark2 = remark2;
    }

    public String getRemark3() {
        return remark3;
    }

    public void setRemark3(String remark3) {
        this.remark3 = remark3;
    }

    public String getRemark4() {
        return remark4;
    }

    public void setRemark4(String remark4) {
        this.remark4 = remark4;
    }

    public String getRemark5() {
        return remark5;
    }

    public void setRemark5(String remark5) {
        this.remark5 = remark5;
    }

    public String getSub_class_id() {
        return sub_class_id;
    }

    public void setSub_class_id(String sub_class_id) {
        this.sub_class_id = sub_class_id;
    }

    public String getSub_class_name() {
        return sub_class_name;
    }

    public void setSub_class_name(String sub_class_name) {
        this.sub_class_name = sub_class_name;
    }

    public String getSub_class_value() {
        return sub_class_value;
    }

    public void setSub_class_value(String sub_class_value) {
        this.sub_class_value = sub_class_value;
    }

    @Override
    public String toString() {
        if(StringUtils.isBlank(sub_class_id)) {
            return "";
        }else {
            return sub_class_id + " " + sub_class_name;
        }
    }
}
