package com.daejong.seoulpharm.util;

import android.content.Context;

import com.daejong.seoulpharm.model.AddressResponse;
import com.daejong.seoulpharm.model.MedicineInfo;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Hyunwoo on 2016. 10. 1..
 */
public class NetworkManager {

    AsyncHttpClient client;
    Gson gson;

    private static NetworkManager instance;

    public static NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }

    private NetworkManager() {
        client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(1, 3000);
        gson = new Gson();
    }


    public interface OnResultListener<T> {
        public void onSuccess(T result);

        public void onFail(int code, String response);
    }

    public interface OnSpecificResultListener<T> {
        public void onSuccess(T result, MedicineInfo medicineInfo);

        public void onFail(int code, String response);
    }

    // 좌표 > 주소 변환
    public static final String CONVERT_TO_ADDRESS_URL = "https://openapi.naver.com/v1/map/reversegeocode";
    public void getAddress(Context context, double latitude, double longtitude, final OnResultListener<String> listener) {
        RequestParams params = new RequestParams();
        params.put("query", longtitude + "," + latitude);

        client.addHeader("Content-Type", "application/xml");
        client.addHeader("X-Naver-Client-Id", "VTse802YjiIWqbRNxuIl");
        client.addHeader("X-Naver-Client-Secret", "THzJyx3ZHT");

        client.get(context, CONVERT_TO_ADDRESS_URL, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onFail(statusCode, responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                AddressResponse response = gson.fromJson(responseString, AddressResponse.class);
                listener.onSuccess(response.getResult().getItems().get(0).getAddress());
            }
        });
    }

    // google translator
    public static final String TRANSLATION_URL = "https://www.googleapis.com/language/translate/v2?key=AIzaSyDAjhx7PrL2slXLmN30c7eOCvcrgKhHmlc";
    public void getTranslation(Context context, String source, String target, ArrayList<String> components, final OnResultListener<String> listener) {
        RequestParams params = new RequestParams();
//        &source=ko&q=이부루&target=en

//        JsonElement jelement = new JsonParser().parse(jsonLine);
//        JsonObject  jobject = jelement.getAsJsonObject();
//        jobject = jobject.getAsJsonObject("data");
//        JsonArray jarray = jobject.getAsJsonArray("translations");
//        jobject = jarray.get(0).getAsJsonObject();
//        String result = jobject.get("translatedText").toString();
//        return result;

        String query = "";
        for(String component :components){
            query +="&q="+component;
        }

        client.get(context, TRANSLATION_URL + "&source=" + source + "&target=" + target + query, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onFail(statusCode, responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                listener.onSuccess(responseString);
            }
        });
    }

    // get Drug info
    public static final String DRUG_MFDS_URL = "http://drug.mfds.go.kr/admin/openapi/detailSearch.do";
    public void getComponentByBarcode(Context context, String barcode, final OnResultListener<String> listener) {
        RequestParams params = new RequestParams();
//        params.put("bc", barcode);

        client.get(context, DRUG_MFDS_URL + "?bc=" + barcode, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onFail(statusCode, responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                AddressResponse response = gson.fromJson(responseString, AddressResponse.class);
                listener.onSuccess(responseString);
            }
        });
    }

    // get specific info
    public static final String DRUG_SPECIFIC_MFDS_URL = "http://drug.mfds.go.kr/html/bxsSearchDrugProduct.jsp?item_Seq=";
    public void getMedicineSpecific(Context context, final MedicineInfo medicineInfo, final OnSpecificResultListener<String> listener) {
        RequestParams params = new RequestParams();


        client.get(context, DRUG_SPECIFIC_MFDS_URL + medicineInfo.getItemSeq(), params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onFail(statusCode, responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                listener.onSuccess(responseString, medicineInfo);
            }
        });
    }
}
