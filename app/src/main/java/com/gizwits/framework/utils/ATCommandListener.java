package com.gizwits.framework.utils;

/**
 * Created by water.zhou on 7/8/2015.
 */
public interface ATCommandListener {

    public void onEnterCMDMode(boolean success);
    public void onExitCMDMode(boolean success, NetworkProtocol protocol);
    public void onSendFile(boolean success);
    public void onReload(boolean success);
    public void onReset(boolean success);
    public void onResponse(String response);
    public void onResponseOfSendFile(String response);
    public void onConfigWifiMode(boolean success,final int cmd_type);
}
