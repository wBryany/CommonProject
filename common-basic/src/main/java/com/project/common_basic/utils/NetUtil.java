package com.project.common_basic.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import okhttp3.Cookie;

/**
 * 网络状态工具类
 *
 * @author yamlee
 */
public class NetUtil {
    public static List<Cookie> ALL_COOKIES = new ArrayList<>();

    /**
     * 检查网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetAvailable(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        return !(networkInfo == null
                || !networkInfo.isConnectedOrConnecting());
    }

    /**
     * 检查wifi是否连接
     *
     * @return
     */
    public static boolean isWifiConnect(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager == null) {
            return false;
        }
        NetworkInfo mWifi = connManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    /**
     * 检查3g网络是否连接
     *
     * @return
     */
    public static boolean is3GConnect(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager.getActiveNetworkInfo() != null
                && !connManager.getActiveNetworkInfo().isAvailable()) {
            return false;
        }
        NetworkInfo info = connManager.getActiveNetworkInfo();
        if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
            if (info.getSubtype() >= 5) {
                return info.isConnected();
            }
        }
        return false;
    }


    /**
     * 检测网络连接是否可用
     *
     * @param ctx
     * @return true 可用; false 不可用
     */
    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        try {
            NetworkInfo[] netinfo = cm.getAllNetworkInfo();
            if (netinfo == null) {
                return false;
            }
            for (NetworkInfo element : netinfo) {
                if (element.isConnected()) {
                    return true;
                }
            }
        } catch (Exception ex) {

        }
        return false;
    }

    public static int getNetwork(Context context) {

        if (isWifiConnect(context)) {
            return -1;
        } else {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                return tm.getNetworkType();
            }

        }

        return TelephonyManager.NETWORK_TYPE_UNKNOWN;
    }

    public static String getNetworkTypeName(Context context) {
        if (context != null) {
            ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectMgr != null) {
                NetworkInfo info = connectMgr.getActiveNetworkInfo();
                if (info != null) {
                    switch (info.getType()) {
                        case ConnectivityManager.TYPE_WIFI:
                            //noinspection HardCodedStringLiteral
                            return "WIFI";
                        case ConnectivityManager.TYPE_MOBILE:
                            return getNetworkTypeName(info.getSubtype());
                    }
                }
            }
        }
        return getNetworkTypeName(TelephonyManager.NETWORK_TYPE_UNKNOWN);
    }

    /**
     * 获取网络类型  1、wifi 2、mobile
     *
     * @param context
     * @return
     */
    public static int getNetworkType(Context context) {
        if (context != null) {
            ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectMgr != null) {
                NetworkInfo info = connectMgr.getActiveNetworkInfo();
                if (info != null) {
                    return info.getType();
                }
            }
        }
        return -1;
    }

    @SuppressWarnings("HardCodedStringLiteral")
    public static String getNetworkTypeName(int type) {
        switch (type) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "GPRS";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "EDGE";
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "UMTS";
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return "HSUPA";
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "HSPA";
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return "CDMA";
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return "CDMA - EvDo rev. 0";
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return "CDMA - EvDo rev. A";
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return "CDMA - EvDo rev. B";
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return "CDMA - 1xRTT";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "LTE";
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return "CDMA - eHRPD";
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "iDEN";
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "HSPA+";
            default:
                return "UNKNOWN";
        }
    }

    /**
     * 获取设备的ip地址
     *
     * @return
     */
    public static String getLocalIp() {
        String localIp = null;

        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface networkInterface = en.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface
                        .getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress instanceof Inet4Address) {
                        localIp = inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return localIp;
    }

    /**
     * 获取设备连接的网关的ip地址
     *
     * @param context
     * @return
     */
    public static String getGateWayIp(Context context) {
        String gatewayIp = null;
        DhcpInfo dhcpInfo = getDhcpInfo(context);
        if (dhcpInfo != null) {
            gatewayIp = Int2String(dhcpInfo.gateway);
        }

        return gatewayIp;
    }

    public static WifiManager getWifiManager(Context context) {
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        return wifiManager;
    }

    public static DhcpInfo getDhcpInfo(Context context) {
        WifiManager wifiManager = getWifiManager(context);
        if (wifiManager != null) {
            DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
            return dhcpInfo;
        } else
            return null;
    }

    public final static String Int2String(int IP) {
        String ipStr = "";

        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            ipStr += String.valueOf(0xFF & IP);
            ipStr += ".";
            ipStr += String.valueOf(0xFF & IP >> 8);
            ipStr += ".";
            ipStr += String.valueOf(0xFF & IP >> 16);
            ipStr += ".";
            ipStr += String.valueOf(0xFF & IP >> 24);
        } else {

            ipStr += String.valueOf(0xFF & IP >> 24);
            ipStr += ".";
            ipStr += String.valueOf(0xFF & IP >> 16);
            ipStr += ".";
            ipStr += String.valueOf(0xFF & IP >> 8);
            ipStr += ".";
            ipStr += String.valueOf(0xFF & IP);
        }

        return ipStr;
    }

    /**
     * 是否ping通
     *
     * @param ip
     * @return
     */
    @SuppressWarnings("HardCodedStringLiteral")
    public static boolean isPingOk(String ip) {
        try {
            Process p = Runtime.getRuntime().exec("/system/bin/ping -c 10 -w 4 " + ip);
            if (p == null) {
                return false;
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.contains("bytes from")) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 该ip的端口某些端口是否打开
     *
     * @param ip
     * @return
     */
    public static boolean isAnyPortOk(String ip) {
        int portArray[] = {9100};

        Selector selector;
        try {
            selector = Selector.open();
            for (int aPortArray : portArray) {
                SocketChannel channel = SocketChannel.open();
                SocketAddress address = new InetSocketAddress(ip, aPortArray);
                channel.configureBlocking(false);
                channel.connect(address);
                channel.register(selector, SelectionKey.OP_CONNECT, address);
                if (selector.select(1500) != 0) {
                    selector.close();
                    return true;
                } else {
                    selector.close();
                    return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
