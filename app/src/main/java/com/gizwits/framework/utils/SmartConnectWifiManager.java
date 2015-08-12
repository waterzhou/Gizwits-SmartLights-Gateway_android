package com.gizwits.framework.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import java.util.List;

public class SmartConnectWifiManager {

    private static final String TAG = "SmartConnectWifiManager:";
    /**
     * Default wifimanager instance
     */
    private WifiManager mWifiManager = null;
    private ConnectivityManager mConnManager = null;

    /**
     * Wifi info instance
     */
    private WifiInfo mWifiInfo = null;
    /**
     * Called activity context
     */
    private Context mContext = null;

    /**
     * Integer constant to check if this build is currently running under Jellybean and above
     */
    private static final int BUILD_VERSION_JELLYBEAN = 17;

    /**
     * Constructor for custom alert dialog.accepting context of called activity
     *
     * @param mContext
     */
    public SmartConnectWifiManager(Context mContext) {
        this.mContext = mContext;
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        mConnManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public boolean scan() { return mWifiManager.startScan(); }

    public List<ScanResult> getScanResults() { return mWifiManager.getScanResults(); }

    public WifiInfo getWifiInfo() {
        return mWifiManager.getConnectionInfo();
    }
    /**
     * returns current  ssid  connected to
     *
     * @return current ssid
     */
    public String getCurrentSSID() {
        return removeSSIDQuotes(mWifiManager.getConnectionInfo().getSSID());
    }

    /**
     * method to check wifi
     *
     * @return true if wifi is connected in our device else false
     */
    public boolean isWifiConnected() {
        return (mConnManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).isConnected();
    }

    /**
     * Returns the current IP address connected to
     *
     * @return
     */
    public String getCurrentIpAddressConnected() {
        int ipval = mWifiManager.getConnectionInfo().getIpAddress();
        return (String.format("%d.%d.%d.%d", (ipval & 0xff), (ipval >> 8 & 0xff), (ipval >> 16 & 0xff), (ipval >> 24 & 0xff))).toString();
    }

    /**
     * Returns the current GatewayIP address connected to
     *
     * @return
     */
    public String getGatewayIpAddress() {
        int gatwayVal = mWifiManager.getDhcpInfo().gateway;
        return (String.format("%d.%d.%d.%d", (gatwayVal & 0xff), (gatwayVal >> 8 & 0xff), (gatwayVal >> 16 & 0xff), (gatwayVal >> 24 & 0xff))).toString();
    }

    /**
     * Filters the double Quotations occuring in Jellybean and above devices.
     * This is only occuring in SDK 17 and above this is documented in SDK as   http://developer.android.com/reference/android/net/wifi/WifiConfiguration.html#SSID
     *
     * @param connectedSSID
     * @return
     */
    public static String removeSSIDQuotes(String connectedSSID) {
        int currentVersion = Build.VERSION.SDK_INT;
        if (currentVersion >= BUILD_VERSION_JELLYBEAN) {
            if (connectedSSID.startsWith("\"") && connectedSSID.endsWith("\"")) {
                connectedSSID = connectedSSID.substring(1, connectedSSID.length() - 1);
            }
        }
        return connectedSSID;
    }

    public boolean disableWifi() {
        return mWifiManager.setWifiEnabled(false);
    }
    public boolean enableWifi() {
        return mWifiManager.setWifiEnabled(true);
    }
    public boolean isWifiEnabled() {
        return mWifiManager.isWifiEnabled();
    }

    public  boolean  WaitForWiFiDisable() {
        int timeout = 100;
        Log.d(TAG, "WaitForWiFiDisable()");
        while(isWifiEnabled () && timeout!=0)

        {
            try{
                Thread.currentThread();
                Thread.sleep(100);
                timeout--;
            }
            catch(InterruptedException ie){
            }
        }

        if(timeout==0){
            Log.d(TAG, "WiFi disable timeout");
            return false;
        }else{
            Log.d(TAG, "WiFi disabled");
            return true;
        }
    }

    public boolean WaitForWiFiEnable()
    {
        int timeout = 100;
        Log.d(TAG, "WaitForWiFiEnable()");

        while(!isWifiEnabled () && timeout!=0)

        {
            try{
                Thread.currentThread();
                Thread.sleep(100);
                timeout--;
            }
            catch(InterruptedException ie){
            }
        }

        if(timeout==0){
            Log.d(TAG, "WiFi enable timeout");
            return false;
        }else{
            Log.d(TAG, "WiFi enabled");
            return true;
        }

    }

    private WifiConfiguration IsExsits(String SSID)
    {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        if (existingConfigs == null)
            return null;
        for (WifiConfiguration existingConfig : existingConfigs)
        {
            if (existingConfig.SSID.equals("\""+SSID+"\""))
            {
                return existingConfig;
            }
        }
        return null;
    }

    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type)
    {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = this.IsExsits(SSID);
        if(tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }

        if(Type == 1) //WIFICIPHER_NOPASS
        {
            //config.wepKeys[0] = "";

            ///config.wepKeys[0] = "\"" + "\"";
            config.hiddenSSID = true;
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            //config.wepTxKeyIndex = 0;

        }
        if(Type == 2) //WIFICIPHER_WEP
        {
            config.hiddenSSID = true;
            config.wepKeys[0]= "\""+Password+"\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if(Type == 3) //WIFICIPHER_WPA
        {
            config.preSharedKey = "\""+Password+"\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }


    public boolean addNetwork(WifiConfiguration wcg) {
        int wcgID = mWifiManager.addNetwork(wcg);
        boolean b =  mWifiManager.enableNetwork(wcgID, true);
        Log.d(TAG, "Add network " + wcgID);

        if(wcgID==-1)
            return false;
        return b;
    }

    public boolean WaitForWiFiConnect()
    {
        //this timeout depends on wifi node performance
        int timeout = 200;
        Log.d(TAG, "WaitForWiFiConnect()");

        while(!isWifiConnected() &&  timeout!=0)

        {
            try{
                Thread.currentThread();
                Thread.sleep(100);
                timeout--;
            }
            catch(InterruptedException ie){
            }
        }

        if(timeout==0){
            Log.d(TAG, "WiFi connect timeout");
            return false;
        }else{
            Log.d(TAG, "WiFi connected");
            return true;
        }
    }
}

