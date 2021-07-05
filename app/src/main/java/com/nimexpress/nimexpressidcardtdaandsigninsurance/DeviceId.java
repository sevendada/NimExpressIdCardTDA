package com.nimexpress.nimexpressidcardtdaandsigninsurance;

import android.os.Build;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;

public class DeviceId {
    public static boolean ModesetMobileKey = false;
    public static String MobileCustom = "";

    public static String getManufacturerSerialNumber() {

        String UNKNOWN = "unknown";

        String serial = UNKNOWN;
        String manufacturer = "";

        try {

            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);

            //testing ... not use.
            //manufacturer = (String) get.invoke(c, "ro.product.manufacturer", UNKNOWN); // Manufacturer of Company.

            if(serial.equals(UNKNOWN)) {
                serial = (String) get.invoke(c, "sys.serialnumber", UNKNOWN); // key for SAMSUNG.
            }

            if(serial.equals(UNKNOWN)) {
                serial = (String) get.invoke(c, "ril.serialnumber", UNKNOWN); // key for SAMSUNG.
            }

            if(serial.equals(UNKNOWN)) {
                serial = (String) get.invoke(c, "ro.serialno", UNKNOWN); // Standard Android API LV-9 (SerialNo).
            }

            if(serial.equals(UNKNOWN)) {
                serial = Build.SERIAL;
            }

        }catch(Exception ignored) {

            ignored.printStackTrace();

            serial = UNKNOWN;

        }

        if (ModesetMobileKey){
            if (!StringUtils.isBlank(MobileCustom)){
                return removeKeyFail(MobileCustom);
            }else {
                return removeKeyFail(serial);
            }
        }else {
            return removeKeyFail(serial);

        }
        //return removeKeyFail("RF8K106T4BX");


    }

    public static String removeKeyFail(String str) {

        try {

            String stringReturn = str.replace("\n", "");

            return stringReturn;

        } catch (Exception e) {
            e.printStackTrace();
            return getManufacturerSerialNumber();
        }

    }

}
