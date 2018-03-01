package com.project.common_basic.network;

import android.content.Context;

import com.project.common_basic.network.header.RequestHeader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.RequestInterceptor;

//import okhttp3.Interceptor;
//import okhttp3.Request;
//import okhttp3.Response;

/**
 * Retrofit请求过滤器
 * Created by yamlee on 15/10/27.
 *
 * @author yamlee
 */
public class RetrofitRequestInterceptor implements RequestInterceptor {
    private static volatile RetrofitRequestInterceptor INSTANCE;
    private List<RequestHeader> mHeaders = new ArrayList<>(0);

    public void setHeader(List<RequestHeader> headers) {
        mHeaders.clear();
        mHeaders.addAll(headers);
    }

    private RetrofitRequestInterceptor() {
    }

    public static RetrofitRequestInterceptor getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (RetrofitRequestInterceptor.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RetrofitRequestInterceptor();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void intercept(RequestFacade request) {
        List<RequestHeader> requestHeaders = mHeaders;
        if (requestHeaders != null) {
            for (int i = 0; i < requestHeaders.size(); i++) {
                RequestHeader requestHeader = requestHeaders.get(i);
                request.addHeader(requestHeader.key(), requestHeader.value());
            }
        }
    }


    /**
     * implements okhttp3.Interceptor
     * @param chain
     * @return
     * @throws IOException
     */
//    @Override
//    public Response intercept(Interceptor.Chain chain) throws IOException {
//        if (headerBuilder == null) {
//            Request request = chain.request();
//            return chain.proceed(request);
//        }
//        headerBuilder.setIsDebug(isDebug);
//        Request.Builder build = chain.request().newBuilder();
//        List<RequestHeader> requestHeaders = headerBuilder.build();
//        if (requestHeaders != null) {
//            for (int i = 0; i < requestHeaders.size(); i++) {
//                RequestHeader requestHeader = requestHeaders.get(i);
//                build.addHeader(requestHeader.key(), requestHeader.value());
//            }
//        }
//        Request request = build.build();
//        return chain.proceed(request);
//    }
}
