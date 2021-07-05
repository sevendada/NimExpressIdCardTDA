package com.nimexpress.nimexpressidcardtdaandsigninsurance.model;

import android.provider.BaseColumns;

public class MobileKeyFixSqlModel {

    public static final String DATABASE_NAME = "mobile_key_sql.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE = "mobile_key_sql";

    public class Column {
        public static final String ID = BaseColumns._ID;
        public static final String KEY_CODE = "KEY_CODE";
        public static final String IS_FIX_MODE = "IS_FIX_MODE";
    }

    private int id;
    private String KEY_CODE;
    private String IS_FIX_MODE;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKEY_CODE() {
        return KEY_CODE;
    }

    public void setKEY_CODE(String KEY_CODE) {
        this.KEY_CODE = KEY_CODE;
    }

    public String getIS_FIX_MODE() {
        return IS_FIX_MODE;
    }

    public void setIS_FIX_MODE(String IS_FIX_MODE) {
        this.IS_FIX_MODE = IS_FIX_MODE;
    }
}
