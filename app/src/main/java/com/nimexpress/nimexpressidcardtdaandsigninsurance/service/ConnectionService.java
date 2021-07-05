package com.nimexpress.nimexpressidcardtdaandsigninsurance.service;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class ConnectionService {

    String mainUrl;

    public ConnectionService(String mainUrl) {
        this.mainUrl = mainUrl;
    }

    public String callService(List<ParameterService> parameterServiceList) {

        try {

            URL url = new URL(mainUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            byte[] outputInBytes = encodeParameter(parameterServiceList).getBytes("UTF-8");
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(outputInBytes);
            outputStream.flush();
            outputStream.close();

            StringBuilder textBuilder = new StringBuilder();
            try {
                Reader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                int c = 0;
                while ((c = reader.read()) != -1) {
                    textBuilder.append((char) c);
                }
            }catch (Exception e) {
                e.printStackTrace();
            }

            String returnData = textBuilder.toString();

            return  returnData;

        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public String encodeParameter(List<ParameterService> parameterServiceList) {

        String stringParameter = "";

        for (ParameterService parameterService: parameterServiceList) {

            stringParameter += parameterService.getKey()+"="+StringUtils.defaultString(parameterService.getValue()) + "&";

        }

        return  stringParameter.substring(0,stringParameter.length() -1);

    }

}
