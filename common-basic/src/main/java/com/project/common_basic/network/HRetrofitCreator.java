package com.project.common_basic.network;


import android.content.Context;

import com.jakewharton.retrofit.Ok3Client;
import com.project.common_basic.exception.NearLogger;
import com.project.common_basic.network.header.RequestHeader;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit.ErrorHandler;
import retrofit.RestAdapter;

/**
 * 简单的 Retrofit Service 创建者
 * <p>
 * Created by LiFZhe on 3/11/15.
 */
public class HRetrofitCreator {
    private RestAdapter mRestAdapter;
    private static Context mContext;
    private static HErrorHandler mErrorHandler;
    @SuppressWarnings("HardCodedStringLiteral")
    private static final String TAG = "HRetrofitCreator";
    private static boolean mDebug = false;
    private static List<RequestHeader> mHeaders = new ArrayList<>(0);
    private static OkHttp3Creator okHttp3Creator;

    /**
     * 初始化方法，在Application初始化时调用
     */
    public static void init(Context context, HErrorHandler errorHandler) {
        mContext = context;
        mErrorHandler = errorHandler;
    }

    public static void setDebug(boolean debug) {
        mDebug = debug;
    }


    /**
     * 设置请求头
     */
    public static void setHeaders(List<RequestHeader> headers) {
        mHeaders.clear();
        mHeaders.addAll(headers);
    }

    /**
     * 获取请求头
     */
    public static List<RequestHeader> getHeaders() {
        return mHeaders;
    }

    /**
     * 获取OkHttpClient
     */
    public static OkHttp3Creator getOkHttpClientCreater() {
        return okHttp3Creator;
    }

    /**
     * 获取ErrorHandler
     */
    public static ErrorHandler getErrorHandler() {
        if (mContext == null) {
            //noinspection HardCodedStringLiteral
            throw new RuntimeException("Context can not be null!");
        }
        if (mErrorHandler == null) {
            mErrorHandler = new HErrorHandler(mContext);
        }
        return mErrorHandler;
    }


    public HRetrofitCreator(String endPoint, ErrorHandler errorHandler) {
        if (mContext == null) {
            //noinspection HardCodedStringLiteral
            throw new RuntimeException("please call init method in application onCreate method" +
                    " when application create");
        }
        RetrofitRequestInterceptor interceptor = RetrofitRequestInterceptor.getInstance(mContext);
        interceptor.setHeader(mHeaders);
        RestAdapter.LogLevel logLevel = RestAdapter.LogLevel.FULL;

        okHttp3Creator = OkHttp3Creator.instance(mContext);
        OkHttpClient okHttpClient = okHttp3Creator.getOkHttp3Client();
        RestAdapter.Log log = new RestAdapter.Log() {
            @Override
            public void log(String message) {
                NearLogger.i(TAG, message);
            }
        };

        if (mDebug) {
            mRestAdapter = new RestAdapter.Builder()
                    .setEndpoint(endPoint)
                    .setRequestInterceptor(interceptor)
                    .setErrorHandler(errorHandler)
                    .setClient(new Ok3Client(okHttpClient))
                    .setLogLevel(logLevel)
                    .setLog(log)
                    .build();
        } else {
            mRestAdapter = new RestAdapter.Builder()
                    .setEndpoint(endPoint)
                    .setRequestInterceptor(interceptor)
                    .setErrorHandler(errorHandler)
                    .setClient(new Ok3Client(okHttpClient))
                    .setLogLevel(RestAdapter.LogLevel.NONE)
                    .build();
        }
    }

    /**
     * 七牛测试模拟线上session
     */
    public HRetrofitCreator(ErrorHandler errorHandler, String serverUrl) {
        if (mContext == null) {
            //noinspection HardCodedStringLiteral
            throw new RuntimeException("please call init method in application onCreate method" +
                    " when application create");
        }
        RetrofitOnlineRequestInterceptor onlineRequestInterceptor = RetrofitOnlineRequestInterceptor.getInstance(mContext);
        RestAdapter.LogLevel logLevel = RestAdapter.LogLevel.NONE;

        OkHttpClient okHttpClient = OkHttp3Creator.instance(mContext).getOkHttp3Client();
        RestAdapter.Log log = new RestAdapter.Log() {
            @Override
            public void log(String message) {
                NearLogger.i(TAG, message);
            }
        };

        mRestAdapter = new RestAdapter.Builder()
                .setEndpoint(serverUrl)
                .setRequestInterceptor(onlineRequestInterceptor)
                .setErrorHandler(errorHandler)
                .setClient(new Ok3Client(okHttpClient))
                .setLogLevel(logLevel)
                .setLog(log)
                .build();

    }

    public <T> T getService(Class<T> serviceClazz) {
        return mRestAdapter.create(serviceClazz);
    }
}
