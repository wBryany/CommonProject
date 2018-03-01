package com.project.common_basic.hybrid.jscall;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.project.common_basic.hybrid.BaseJsCallProcessor;
import com.project.common_basic.hybrid.BaseJsCallResponse;
import com.project.common_basic.hybrid.JsCallData;
import com.project.common_basic.hybrid.NativeComponentProvider;
import com.project.common_basic.hybrid.WVJBWebViewClient;

import timber.log.Timber;

/**
 * Js调用定位接口处理
 *
 * @author yamlee
 */
public class LocationProcessor extends BaseJsCallProcessor {
    public static final String FUNC_NAME = "getLocation";
    private AMapLocationClient mLocationClient;
    private Context context;

    public LocationProcessor(NativeComponentProvider componentProvider) {
        super(componentProvider);
        context = componentProvider.provideApplicationContext();
    }

    @Override
    public boolean onHandleJsQuest(JsCallData callData) {
        if (FUNC_NAME.equals(callData.getFunc())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onResponse(WVJBWebViewClient.WVJBResponseCallback callback) {
        startLocation(callback);
        return true;
    }

    private void startLocation(final WVJBWebViewClient.WVJBResponseCallback callback) {
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(context);
            AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
            mLocationOption.setOnceLocationLatest(true);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        }
        //设置定位监听
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if(aMapLocation.getErrorCode() == 0) {
                        double longitude = aMapLocation.getLongitude();
                        double latitude = aMapLocation.getLatitude();
                        String cityName = aMapLocation.getCity();
                        LocationResponse response = new LocationResponse(longitude + "", latitude + "", cityName);
                        callback.callback(convertObjectString(response));
                    } else {
                        BaseJsCallResponse baseJsCallResponse = new BaseJsCallResponse();
                        baseJsCallResponse.ret = "error: " + aMapLocation.getErrorCode();
                        callback.callback(convertObjectString(baseJsCallResponse));
                        Timber.e("location error : errCode is %s, errInfo is %s", aMapLocation.getErrorCode(), aMapLocation.getErrorInfo());
                    }
                    mLocationClient.unRegisterLocationListener(this);
                }
            }
        });
        mLocationClient.startLocation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocation();
    }

    private void stopLocation() {
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    @Override
    public String getFuncName() {
        return FUNC_NAME;
    }

    private class LocationResponse extends BaseJsCallResponse {
        public String longitude;
        public String latitude;
        public String cityname;

        public LocationResponse(String longitude, String latitude, String cityname) {
            this.longitude = longitude;
            this.latitude = latitude;
            this.cityname = cityname;
        }
    }

}
