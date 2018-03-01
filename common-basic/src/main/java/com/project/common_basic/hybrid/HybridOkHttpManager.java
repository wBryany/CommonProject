package com.project.common_basic.hybrid;

import android.content.Context;
import android.support.annotation.NonNull;

import com.project.common_basic.R;
import com.project.common_basic.hybrid.entity.HybridUpdateEntity;
import com.project.common_basic.hybrid.jscall.HttpRequestProcessor;
import com.project.common_basic.network.HRetrofitCreator;
import com.project.common_basic.network.OkHttp3Creator;
import com.project.common_basic.network.header.RequestHeader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import timber.log.Timber;

/**
 * Created by fengruicong on 16-3-23.
 *
 * @author fengruicong
 * @author yamlee
 */
public class HybridOkHttpManager {

    public static String doRequest(Context context, HybridUpdateEntity entity) throws IOException {
        OkHttpClient okHttpClient = OkHttp3Creator.instance(context).getOkHttp3Client();
        Request.Builder builder = new Request.Builder();
        builder = addHeader(builder);
        if (isPost(entity)) {
            //noinspection HardCodedStringLiteral
            Timber.d("---->HYBRID  HTTP POST " + entity.getPath());
            builder.url(entity.getPath()).post(generatePostParam(entity));
        } else {
            builder.url(generateGetUrl(entity));
        }
        Request request = builder.build();
        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            String responseStr = response.body().string();
            //noinspection HardCodedStringLiteral
            Timber.d("h5接口请求返回数据:" + responseStr);
            return responseStr;
        } else {
            throw new IOException(context.getString(R.string.get_data_err));
        }
    }

    public static String doRequest(Context context,
                                   HttpRequestProcessor.HybridHttpRequest request) throws IOException {
        OkHttpClient okHttpClient = OkHttp3Creator.instance(context).getOkHttp3Client();
        Request.Builder builder = new Request.Builder();
        builder = addHeader(builder);
        if (HttpRequestProcessor.HybridHttpRequest.Method.POST.equals(request.method)) {
            //noinspection HardCodedStringLiteral
            Timber.d("---->HYBRID  HTTP POST " + request.url);
            builder.url(request.url)
                    .post(generatePostParam(request.jsonParams));
        } else {
            builder.url(generateGetUrl(request.url, request.jsonParams));
        }
        Request okHttpRequest = builder.build();
        Response response = okHttpClient.newCall(okHttpRequest).execute();
        if (response.isSuccessful()) {
            String responseStr = response.body().string();
            //noinspection HardCodedStringLiteral
            Timber.d("h5接口请求返回数据:" + responseStr);
            return responseStr;
        } else {
            throw new IOException(context.getString(R.string.get_data_err));
        }
    }


    private static Request.Builder addHeader(Request.Builder builder) {
        List<RequestHeader> requestHeaders = HRetrofitCreator.getHeaders();
        if (requestHeaders != null) {
            for (int i = 0; i < requestHeaders.size(); i++) {
                RequestHeader requestHeader = requestHeaders.get(i);
                builder.addHeader(requestHeader.key(), requestHeader.value());
            }
        }
        return builder;
    }

    @NonNull
    private static RequestBody generatePostParam(HybridUpdateEntity entity) {
        return generatePostParam(entity.getJsonParams().toString());
    }

    private static RequestBody generatePostParam(String jsonParams) {
        FormBody.Builder build = new FormBody.Builder();
        JSONObject paramJsonObj = null;
        try {
            paramJsonObj = new JSONObject(jsonParams);
            Iterator<String> keys = paramJsonObj.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = paramJsonObj.optString(key);
                build.add(key, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return build.build();
    }

    private static String generateGetUrl(HybridUpdateEntity entity) {
        return generateGetUrl(entity.getPath(), entity.getJsonParams().toString());
    }

    private static String generateGetUrl(String url, String jsonParams) {
        StringBuilder pathBuilder = new StringBuilder(url);
        pathBuilder.append("?");
        JSONObject paramJsonObj = null;
        try {
            paramJsonObj = new JSONObject(jsonParams);
            Iterator<String> keys = paramJsonObj.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                pathBuilder.append(key).append("=").append(paramJsonObj.optString(key)).append("&");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String result = pathBuilder.toString();
        if (result.endsWith("&")) {
            result = result.substring(0, result.length() - 1);
        }
        //noinspection HardCodedStringLiteral
        Timber.d("---->HYBRID  HTTP GET " + result);
        return result;
    }

    /**
     * 判断请求方式
     */
    private static boolean isPost(HybridUpdateEntity entity) {
        //请求方式
        String action = entity.getAction();
        if (HybridUpdateValue.VALUE_ACTION_GET.equalsIgnoreCase(action)) {
            return false;
        } else if (HybridUpdateValue.VALUE_ACTION_POST.equalsIgnoreCase(action)) {
            return true;
        }
        //默认是get
        return false;
    }
}
