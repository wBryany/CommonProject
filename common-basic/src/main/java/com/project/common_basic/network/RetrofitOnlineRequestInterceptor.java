package com.project.common_basic.network;

import android.content.Context;

import retrofit.RequestInterceptor;

/**
 * Created by yamlee on 15/10/27.
 */
@SuppressWarnings("HardCodedStringLiteral")
public class RetrofitOnlineRequestInterceptor implements RequestInterceptor {
    private static volatile RetrofitOnlineRequestInterceptor INSTANCE;

    private RetrofitOnlineRequestInterceptor() {
    }

    public static RetrofitOnlineRequestInterceptor getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (RetrofitOnlineRequestInterceptor.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RetrofitOnlineRequestInterceptor();
                }
            }
        }

        return INSTANCE;
    }

    @Override
    public void intercept(RequestFacade request) {
        request.addHeader("sessionid", "e7dd81dc-fa06-448d-95dd-407942864c14");
    }
}
