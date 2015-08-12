package com.gizwits.framework.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by water.zhou on 7/8/2015.
 */
public final class AppPreferences {
    /** Preference keys */
    public static final String KEY_IP_ADDRESS        = "host";
    public static final String KEY_TCP_PORT          = "port";
    public static final String KEY_AUTO_CONNECT_MODE = "auto_connect_mode";
    public static final String KEY_BRIDGE_LAST_SSID  = "bridge_last_wifi_ssid";
    public static final String KEY_ROUTER_LAST_SSID  = "router_last_wifi_ssid";
    public static final String KEY_ROUTER_LAST_KEY   = "router_last_wifi_key";
    public static final String KEY_BRIDGE_LAST_MAC   = "bridge_last_mac";

    /** Preference members */
    private SharedPreferences mAppSharedPrefs;
    private SharedPreferences.Editor mAppPrefsEditor;

    public AppPreferences(Context context) {
        mAppSharedPrefs = context.getSharedPreferences("SmartConnectPreferences", 0);
        mAppPrefsEditor = mAppSharedPrefs.edit();
    }

    /** IP Address of the ZLL Bridge */
    public void setIpAddress(String ipAddress) {
        Log.d("AppPreferences", "Changing IP Address to " + ipAddress);
        mAppPrefsEditor.putString(KEY_IP_ADDRESS, ipAddress);
        mAppPrefsEditor.commit();
    }

    public void setParameter(String para1, String para2) {
        Log.d("AppPreferences", "set parameter");
        mAppPrefsEditor.putString(para1,para2);
        mAppPrefsEditor.commit();
    }

    public String getParameter(String para1) {
        Log.d("AppPreferences", "get parameter");
        return mAppSharedPrefs.getString(para1, null);
    }
    public String getIpAddress() {
        return mAppSharedPrefs.getString(KEY_IP_ADDRESS, null);
    }

    /** Port number */
    public void setPortNumber(String portNumber) {
        Log.d("AppPreferences","Changing Port number to " + portNumber);
        mAppPrefsEditor.putString(KEY_TCP_PORT, portNumber);
        mAppPrefsEditor.commit();
    }

    public String getPortNumber() {
        return mAppSharedPrefs.getString(KEY_TCP_PORT, null);
    }

    public void setBridgeWiFiSSID(String newSSID) {
        Log.d("AppPreferences","Changing AP SSID to " + newSSID);
        mAppPrefsEditor.putString(KEY_BRIDGE_LAST_SSID, newSSID);
        mAppPrefsEditor.commit();
    }

    public String getBridgeWiFiSSID() {
        return mAppSharedPrefs.getString(KEY_BRIDGE_LAST_SSID, "HF-LPB100");
    }

    public void setRouterWiFiSSID(String ssid) {
        Log.d("AppPreferences","Changing STA SSID to " + ssid);
        mAppPrefsEditor.putString(KEY_ROUTER_LAST_SSID, ssid);
        mAppPrefsEditor.commit();
    }


    public String getRouterWiFiSSID() {
        return mAppSharedPrefs.getString(KEY_ROUTER_LAST_SSID, "");
    }

    public void setRouterWiFiKey(String key) {
        mAppPrefsEditor.putString(KEY_ROUTER_LAST_KEY, key);
        mAppPrefsEditor.commit();
    }

    public String getRouterWiFiKey() {
        return mAppSharedPrefs.getString(KEY_ROUTER_LAST_KEY, "");
    }

    public void setBridgeMAC(String key) {
        mAppPrefsEditor.putString(KEY_BRIDGE_LAST_MAC, key);
        mAppPrefsEditor.commit();
    }

    public String getBridgeMAC() {
        return mAppSharedPrefs.getString(KEY_BRIDGE_LAST_MAC, "");
    }

    public String getAutoConnectMode() {
        return mAppSharedPrefs.getString(KEY_AUTO_CONNECT_MODE, "");
    }

}
