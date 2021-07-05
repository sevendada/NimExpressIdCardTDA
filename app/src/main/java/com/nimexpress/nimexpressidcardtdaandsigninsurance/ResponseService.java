package com.nimexpress.nimexpressidcardtdaandsigninsurance;

/**
 * Created by mac on 2/27/2018 AD.
 */

public class ResponseService {

    boolean isError = false;
    String errorCode = "";
    String messageResponse = "";
    String messageResponseTh = "";
    String errorFocus = "";
    String contentResponse = "";
    String jsonStringResponse = "";
    String jsonStringResponse2 = "";

    public boolean isError() {
        return isError;
    }
    public void setError(boolean isError) {
        this.isError = isError;
    }
    public String getErrorCode() {
        return errorCode;
    }
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    public String getMessageResponse() {
        return messageResponse;
    }
    public void setMessageResponse(String messageResponse) {
        this.messageResponse = messageResponse;
    }
    public String getContentResponse() {
        return contentResponse;
    }
    public void setContentResponse(String contentResponse) {
        this.contentResponse = contentResponse;
    }
    public String getJsonStringResponse() {
        return jsonStringResponse;
    }
    public void setJsonStringResponse(String jsonStringResponse) {
        this.jsonStringResponse = jsonStringResponse;
    }
    public String getMessageResponseTh() {
        return messageResponseTh;
    }
    public void setMessageResponseTh(String messageResponseTh) {
        this.messageResponseTh = messageResponseTh;
    }
    public String getErrorFocus() {
        return errorFocus;
    }
    public void setErrorFocus(String errorFocus) {
        this.errorFocus = errorFocus;
    }

    public String getJsonStringResponse2() {
        return jsonStringResponse2;
    }

    public void setJsonStringResponse2(String jsonStringResponse2) {
        this.jsonStringResponse2 = jsonStringResponse2;
    }

}
