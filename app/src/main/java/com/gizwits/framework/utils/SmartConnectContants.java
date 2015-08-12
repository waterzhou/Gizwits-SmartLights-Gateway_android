package com.gizwits.framework.utils;

/**
 * Created by water.zhou on 7/8/2015.
 */
public class SmartConnectContants {
    /**
     * Dialog ID constant for wifi available
     */
    public static final int DLG_NO_WIFI_AVAILABLE=1;
    /**
     * Dialog ID constant for SSID invalid
     */
    public static final int DLG_SSID_INVALID=2;
    /**
     * Dialog ID constant for password invalid
     */
    public static final int DLG_PASSWORD_INVALID=3;
    /**
     * Dialog ID constant for gateway ip invalid
     */
    public static final int DLG_GATEWAY_IP_INVALID=4;
    /**
     * Dialog ID constant for encryption key invalid
     */
    public static final int DLG_KEY_INVALID=5;
    /**
     * Dialog ID constant for success callback alert
     */
    public static final int DLG_CONNECTION_SUCCESS=6;
    /**
     * Dialog ID constant for failure callback alert
     */
    public static final int DLG_CONNECTION_FAILURE=7;
    /**
     * Dialog ID constant for timeout alert
     */
    public static final int MSG_SAVE_MODULES=8;


    public static final int MSG_CONNECT_BRIDGE_WIFI = 9;

    public static final int MSG_CONNECT_BRIDGE_WIFI_SUCCESS = 10;

    public static final int MSG_CONNECT_ROUTE_CMD = 11;
    public static final int MSG_CONFIG_ROUTER_SSID = 12;
    public static final int MSG_CONFIG_ROUTER_KEY = 13;
    public static final int MSG_NODE_RESET = 14;
    public static final int MSG_CONFIG_BRIDGE_SUCCESS = 15;
    public static final int MSG_CONNECT_ROUTER_WIFI = 16;
    public static final int MSG_CONFIG_BRIDGE_FAIL           = 17;
    public static final int MSG_CONNECT_ROUTER_WIFI_SUCCESS           = 18;


    public static final String CMD_TEST = "AT+\r";
    public static final String RESPONSE_OK = "+ok";
    public static final String STRING_MESSAGE = "string_message";
    public static final String CMD_NETWORK_PROTOCOL = "AT+NETP\r";
    public static final String RESPONSE_OK_OPTION = "+ok=";
    public static final String CMD_TRANSPARENT_TRANSMISSION = "AT+ENTM\r";
    public static final String CMD_EXIT_CMD_MODE = "AT+Q\r";
    public static final String CMD_RELOAD = "AT+RELD\r";
    public static final String RESPONSE_REBOOT_OK = "+ok=rebooting";
    public static final String CMD_RESET = "AT+Z\r";
    public static final String CMD_SCAN_MODULES = "HF-A11ASSISTHREAD";
    public static final String CMD_ENTER_CMD_MODE = "+ok";
    public static final String CMD_SSID = "AT+SSID=";
    public static final String CMD_END ="\r";
    public static final String CMD_KEY = "AT+KEY=";

}
