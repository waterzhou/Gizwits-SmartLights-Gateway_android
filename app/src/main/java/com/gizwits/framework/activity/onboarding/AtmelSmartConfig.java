package com.gizwits.framework.activity.onboarding;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.gizwits.framework.activity.device.DeviceListActivity;
import com.gizwits.framework.utils.ATCommand;
import com.gizwits.framework.utils.ATCommandListener;
import com.gizwits.framework.utils.AppPreferences;
import com.gizwits.framework.utils.NetworkProtocol;
import com.gizwits.framework.utils.SmartConnectContants;
import com.gizwits.framework.utils.SmartConnectDialogManager;
import com.gizwits.framework.utils.SmartConnectUtils;
import com.gizwits.framework.utils.SmartConnectWifiManager;
import com.gizwits.framework.utils.UdpUnicast;
import com.gizwits.framework.utils.Utils;
import com.gizwits.framework.utils.udpbroadcast;
import com.gizwits.smartlight.R;

import java.util.List;

/**
 * Created by water.zhou on 10/23/2014.
 */
public class AtmelSmartConfig extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, TextWatcher {

    private static final String TAG = "SmartConnect:";
    /**
     * The tb psw flag.
     */
    private ToggleButton tbPswFlag;
    /**
     * Wifi Manager instance which gives the network related information like
     * Wifi ,SSID etc.
     */
    private SmartConnectWifiManager mSmartWifiManager = null;

    private RelativeLayout footerView = null;

    /**
     * Sending a request to server is done onClick of this button,it interacts
     * with the smartConfig.jar library
     */
    private Button mStartConfig = null;
    private Button mChangeButton = null;

    private EditText mSSIDInputField = null;

    /**
     * The Password input field details are entered by user also called Key
     * field
     */
    private EditText mPasswordInputField = null;
    /**
     * The Encryption key field input field is entered by user
     */
    private EditText mKeyInputField = null;

    /* A check box which when checked sends the encryption key to server as a
    * input and checks if key is exactly 16chars
    */
    private CheckBox mconfig_key_checkbox = null;
    /**
     * Progressbar
     */
    private int textCount = 0;


    /**
     * Boolean to check if network is enabled or not
     */
    public boolean isNetworkConnecting = false;
    /**
     * A Dialog instance which is responsible to generate all dialogs in the app
     */
    private SmartConnectDialogManager mDialogManager = null;
    private ProgressDialog pd_config_refresh;
    private static AppPreferences mAppPreferences;
    private UdpUnicast mUdpUnicast;
    private int mUdpPort = 48899;
    private static ConnectionHandler mConnectionHandler;
    private HandlerThread mConnectionThread;
    private static ATCommand mATCommand;
    private ATCommandListener mATCommandListener;
    private boolean mIsSwitching = false;
    private static String cmd;
    private static List<ScanResult> listResult;
    private int router_cypher_type;
    private Spinner mSpinner;
    private ArrayAdapter<CharSequence> mAdapter;


    class ConnectionHandler extends Handler {

        public static final int MSG_CONNECT_BRIDGE_WIFI = 9;
        public static final int MSG_CONNECT_ROUTE_CMD = 11;
        public static final int MSG_NODE_RESET = 14;
        public static final int MSG_CONNECT_ROUTER_WIFI = 16;

        public ConnectionHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message message) {
            String current_wifi_ssid;
            String err_msg;
            switch (message.what) {
                case MSG_CONNECT_BRIDGE_WIFI: {
                    Log.d(TAG, "MSG_CONNECT_BRIDGE_WIFI");
                    showProcessDialog(5);
                    String bridge_ssid = mAppPreferences.getBridgeWiFiSSID();
                    if (mSmartWifiManager.isWifiConnected()) {
                        current_wifi_ssid = mSmartWifiManager.getCurrentSSID();//mSmartWifiManager.getWifiInfo().getSSID();
                        if (current_wifi_ssid.equals(bridge_ssid) || current_wifi_ssid.substring(1, current_wifi_ssid.length() - 1).equals(bridge_ssid)) {
                            Log.d(TAG, "bridge wifi already connected");
                            mHandler.sendEmptyMessage(SmartConnectContants.MSG_CONNECT_BRIDGE_WIFI_SUCCESS);
                            break;
                        } else {
                            //disconnect current wifi
                            Log.d(TAG, "Disable WiFi first");
                            mSmartWifiManager.disableWifi();
                            if (!mSmartWifiManager.WaitForWiFiDisable()) {
                                Log.d(TAG, "Disable WiFi timeout");
                                break;
                            }
                        }
                    }
                    //reenable wifi
                    mSmartWifiManager.enableWifi();
                    //wait for wifi enable
                    if (!mSmartWifiManager.WaitForWiFiEnable()) {
                        Log.d(TAG, "Enable WiFi timeout");
                        break;
                    }
                    Log.d(TAG, "Connecting wifi:" + bridge_ssid);
                    if (!mSmartWifiManager.addNetwork(mSmartWifiManager.CreateWifiInfo(bridge_ssid, "", 1))) {
                        Log.d(TAG, "Add wifi network failed");
                        break;
                    }
                    if (!mSmartWifiManager.WaitForWiFiConnect()) {
                        Log.d(TAG, " WaitForWiFiConnect failed");
                        prompt_confirm_dialog("No Atmel edge node found or timeout, Have a check...");
                        break;
                    }
                    mHandler.sendEmptyMessage(SmartConnectContants.MSG_CONNECT_BRIDGE_WIFI_SUCCESS);
                    break;
                }
                case MSG_CONNECT_ROUTE_CMD: {
                    Log.d(TAG, "MSG_CONNECT_ROUTE_CMD gateway=" + mSmartWifiManager.getGatewayIpAddress());
                    if (mUdpUnicast == null)
                        mUdpUnicast = new UdpUnicast(mSmartWifiManager.getGatewayIpAddress(), mUdpPort);
                    if (mUdpUnicast.isClosed()) {
                        mUdpUnicast.open();
                        mATCommand = new ATCommand(mUdpUnicast);
                        setATCmdListener();
                    }
                    router_cypher_type = checkRouterWiFiExist();
                    if (router_cypher_type != -1) {

                    } else {
                        mHandler.sendMessage(Utils.generate_string_msg(SmartConnectContants.MSG_CONFIG_BRIDGE_FAIL,
                                "WiFi:" + mAppPreferences.getRouterWiFiSSID() + " is not found, please check your network"));
                    }
                    cmd = SmartConnectContants.CMD_SSID + mAppPreferences.getRouterWiFiSSID() + "+" + mAppPreferences.getRouterWiFiKey() + SmartConnectContants.CMD_END;
                    Log.d(TAG, cmd);
                    mATCommand.configWifiMode(cmd, SmartConnectContants.MSG_CONFIG_ROUTER_SSID);
                    break;

                }
                case MSG_NODE_RESET: {
                    cmd = SmartConnectContants.CMD_RESET + SmartConnectContants.CMD_END;
                    Log.d(TAG, cmd);
                    mATCommand.configWifiMode(cmd, SmartConnectContants.MSG_NODE_RESET);
                    break;
                }
                case MSG_CONNECT_ROUTER_WIFI: {
                    Log.d(TAG, "MSG_CONNECT_ROUTER_WIFI");
                    String router_ssid = mAppPreferences.getRouterWiFiSSID();
                    String router_key = mAppPreferences.getRouterWiFiKey();
                    err_msg = "please connect to " + router_ssid + " manually";
                    //disable current connected wifi(bridge wifi)
                    mSmartWifiManager.disableWifi();
                    if (!mSmartWifiManager.WaitForWiFiDisable()) {
                        mHandler.sendMessage(Utils.generate_string_msg(SmartConnectContants.MSG_CONFIG_BRIDGE_FAIL,
                                "Disable WiFi Failed, " + err_msg));
                        break;
                    }
                    mSmartWifiManager.enableWifi();
                    //wait for wifi enable
                    if (!mSmartWifiManager.WaitForWiFiEnable()) {
                        mHandler.sendMessage(Utils.generate_string_msg(SmartConnectContants.MSG_CONFIG_BRIDGE_FAIL,
                                "Enable WiFi Failed, " + err_msg));
                        break;
                    }
                    Log.d(TAG, "connnectting wifi:" + router_ssid + " key:" + router_key + " type:" + router_cypher_type);
                    if (!mSmartWifiManager.addNetwork(mSmartWifiManager.CreateWifiInfo(router_ssid, router_key, router_cypher_type))) {
                        mHandler.sendMessage(Utils.generate_string_msg(SmartConnectContants.MSG_CONFIG_BRIDGE_FAIL,
                                "Add WiFi Network Failed, " + err_msg));
                        break;
                    }
                    if (!mSmartWifiManager.WaitForWiFiConnect()) {
                        mHandler.sendMessage(Utils.generate_string_msg(SmartConnectContants.MSG_CONFIG_BRIDGE_FAIL,
                                "Connect WiFi Failed, " + err_msg));
                        break;
                    }

                    mHandler.sendEmptyMessage(SmartConnectContants.MSG_CONNECT_ROUTER_WIFI_SUCCESS);

                    break;
                }
                default:
                    break;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SmartConnectUtils.setProtraitOrientationEnabled(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.configuration);
        mAppPreferences = new AppPreferences(getApplicationContext());

        mSpinner = (Spinner) findViewById(R.id.Spinner01);
        mAdapter = ArrayAdapter.createFromResource(this, R.array.planets_array, R.layout.spinner_item);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mAdapter);
        mSpinner.setOnItemSelectedListener(new SpinnerSelectedListener());
        mSpinner.setVisibility(View.VISIBLE);

        initViews();
        setViewClickListeners();
        initData();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        // Start another thread to solve different tasks
        mConnectionThread = new HandlerThread("ConnectionThread");
        mConnectionThread.start();
        mConnectionHandler = new ConnectionHandler(mConnectionThread.getLooper());
    }

    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            mAppPreferences.setBridgeWiFiSSID(arg0.getItemAtPosition(arg2).toString());
        }
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (broadcastReceiver != null) {
            // Unregister receiver.
            unregisterReceiver(broadcastReceiver);
            // The important bit here is to set the receiver
            // to null once it has been unregistered.
            broadcastReceiver = null;
        }
    }

    private boolean checkNetwork() {
        if (!(getWiFiManagerInstance().isWifiConnected())) {
            mDialogManager = new SmartConnectDialogManager(AtmelSmartConfig.this);
            mDialogManager.showCustomAlertDialog(SmartConnectContants.DLG_NO_WIFI_AVAILABLE);
            return false;
            // Do stuff when wifi not there.. disable start button.
        } else {
            return true;
        }

    }

    /**
     * Initialise all view components from xml
     */
    private void initViews() {
        mStartConfig = (Button) findViewById(R.id.config_start_button);
        mChangeButton = (Button) findViewById(R.id.config_change_button);
        footerView = (RelativeLayout) findViewById(R.id.config_footerview);
        mSSIDInputField = (EditText) findViewById(R.id.config_ssid_input);
        mPasswordInputField = (EditText) findViewById(R.id.config_passwd_input);
        tbPswFlag = (ToggleButton) findViewById(R.id.tbPswFlag);
    }

    /**
     * returns the Wifi Manager instance which gives the network related
     * information like Wifi ,SSID etc.
     *
     * @return Wifi Manager instance
     */
    public SmartConnectWifiManager getWiFiManagerInstance() {
        if (mSmartWifiManager == null) {
            mSmartWifiManager = new SmartConnectWifiManager(AtmelSmartConfig.this);
        }
        return mSmartWifiManager;
    }

    /**
     * Initialize all view componets in screen with input data
     */
    private void initData() {

        if (getWiFiManagerInstance().getCurrentSSID() != null
                && getWiFiManagerInstance().getCurrentSSID().length() > 0) {
            Log.d(TAG, "init data " + getWiFiManagerInstance().getCurrentSSID());
            mSSIDInputField.setText(getWiFiManagerInstance().getCurrentSSID());
            /**
             * removing the foucs of ssid when field is already configured from
             * Network
             */
            mSSIDInputField.setEnabled(false);
            mSSIDInputField.setFocusable(false);
            mSSIDInputField.setFocusableInTouchMode(false);
        }
    }

    /**
     * Init the click listeners of all required views
     */
    private void setViewClickListeners() {
        mStartConfig.setOnClickListener(this);
        mChangeButton.setOnClickListener(this);
        footerView.setOnClickListener(this);
        tbPswFlag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    mPasswordInputField.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    mPasswordInputField.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.config_start_button: {
                mAppPreferences.setRouterWiFiSSID(mSSIDInputField.getText().toString());
                mAppPreferences.setRouterWiFiKey(mPasswordInputField.getText().toString());
                Log.d(TAG, "ssid=" + mSSIDInputField.getText().toString() + "password=" + mPasswordInputField.getText().toString());
                if (checkNetwork()) {
                    mConnectionHandler.sendEmptyMessage(SmartConnectContants.MSG_CONNECT_BRIDGE_WIFI);
                }
                break;
            }
            case R.id.config_change_button: {
                Intent intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            }
        }
    }

    private void dismissProcessDialog() {
        runOnUiThread(new Runnable() {
            public void run() {
                if (pd_config_refresh != null) {
                    pd_config_refresh.dismiss();
                }
                pd_config_refresh = null;
            }
        });
    }

    /*Show process dialog*/
    private void showProcessDialog(final int progress) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (pd_config_refresh == null) {
                    pd_config_refresh = new ProgressDialog(AtmelSmartConfig.this);
                }
                pd_config_refresh.setMessage("Smart Configuring...");
                pd_config_refresh.setCancelable(false);
                pd_config_refresh.setProgress(progress);
                pd_config_refresh.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pd_config_refresh.setMax(100);
                pd_config_refresh.show();
            }
        });
    }

    private int checkRouterWiFiExist() {
        int cypher_type;
        String router_ssid = mAppPreferences.getRouterWiFiSSID();
        String router_key = mAppPreferences.getRouterWiFiKey();
        Log.d(TAG, "Router ssid:" + router_ssid + " key:" + router_key);
        if (mSmartWifiManager.scan()) {
            listResult = mSmartWifiManager.getScanResults();
            Log.d(TAG, "Found " + listResult.size() + " wifi");
        }
        for (ScanResult wifi_result : listResult) {
            if (wifi_result.SSID.equals(router_ssid)) {
                Log.d(TAG, "Found expected wifi:" + wifi_result.SSID + "-" + wifi_result.capabilities);
                if (wifi_result.capabilities.contains("WPA2")) {
                    cypher_type = 3;
                } else if (wifi_result.capabilities.contains("WPA")) {
                    cypher_type = 3;
                } else {
                    cypher_type = 1;
                }
                return cypher_type;
            }
        }
        //not found
        return -1;

    }

    /**
     * Handler class for invoking dialog when a thread notifies of FTC_SUCCESS
     * or FTC_FAILURE
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SmartConnectContants.DLG_CONNECTION_FAILURE:
                    showFailureAlert(SmartConnectContants.DLG_CONNECTION_FAILURE);
                    break;
                case SmartConnectContants.DLG_CONNECTION_SUCCESS:
                    showConnectionSuccess(SmartConnectContants.DLG_CONNECTION_SUCCESS);
                    break;

                case SmartConnectContants.MSG_SAVE_MODULES:
                    mConnectionHandler.sendEmptyMessage(SmartConnectContants.MSG_CONNECT_ROUTE_CMD);
                    break;

                case SmartConnectContants.MSG_CONNECT_BRIDGE_WIFI_SUCCESS: {
                    Log.d(TAG, "connect bridge wifi success");
                    showProcessDialog(25);
                    mConnectionHandler.sendEmptyMessage(SmartConnectContants.MSG_CONNECT_ROUTE_CMD);
                    break;
                }
                case SmartConnectContants.MSG_CONFIG_BRIDGE_SUCCESS: {
                    Log.d(TAG, "MSG_CONFIG_BRIDGE_SUCCESS");
                    showProcessDialog(98);
                    mConnectionHandler.sendEmptyMessage(SmartConnectContants.MSG_CONNECT_ROUTER_WIFI);
                    break;
                }
                case SmartConnectContants.MSG_CONNECT_ROUTER_WIFI_SUCCESS: {
                    showProcessDialog(100);
                    dismissProcessDialog();
                    closeActions();
                    prompt_confirm_dialog("Smart config done,already reconnect with"
                            + mAppPreferences.getRouterWiFiSSID());
                    break;
                }
                case SmartConnectContants.MSG_CONFIG_BRIDGE_FAIL: {
                    Log.d(TAG, "MSG_CONFIG_BRIDGE_FAIL");
                    dismissProcessDialog();
                    closeActions();
                    break;
                }

            }
        }
    };

    private void prompt_confirm_dialog(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setMessage(text);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,
                                int which) {
                Intent Search_Activity = new Intent(AtmelSmartConfig.this, DeviceListActivity.class);
                Search_Activity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(Search_Activity);
                finish();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,
                                int which) {

                System.exit(0);
            }
        })
                .create()
                .show();
    }

    private void enterCMDMode() {

        if (!mIsSwitching) {

            Log.d(TAG, "enterCMDMode");
            mIsSwitching = true;
            showProcessDialog(10);

            new Thread(new Runnable() {

                @Override
                public void run() {
                    mATCommand.enterCMDMode();
                }
            }).start();
        }

    }

    private void setATCmdListener() {
        mATCommandListener = new ATCommandListener() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse:" + response);

            }

            @Override
            public void onEnterCMDMode(boolean success) {

            }

            @Override
            public void onConfigWifiMode(boolean success, final int cmd_type) {

                Log.d(TAG, "onConfigWifiMode:" + success + " Type:" + cmd_type);
                if (success) {
                    switch (cmd_type) {

                        case SmartConnectContants.MSG_CONFIG_ROUTER_SSID:
                            showProcessDialog(80);
                            mConnectionHandler.sendEmptyMessage(SmartConnectContants.MSG_NODE_RESET);
                            break;
                        case SmartConnectContants.MSG_CONFIG_ROUTER_KEY:
                            showProcessDialog(70);
                            mConnectionHandler.sendEmptyMessage(SmartConnectContants.MSG_NODE_RESET);
                            break;
                        case SmartConnectContants.MSG_NODE_RESET:
                            showProcessDialog(95);
                            mHandler.sendEmptyMessage(SmartConnectContants.MSG_CONFIG_BRIDGE_SUCCESS);
                            break;
                        default:
                            break;
                    }
                } else {
                    mHandler.sendMessage(Utils.generate_string_msg(SmartConnectContants.MSG_CONFIG_BRIDGE_FAIL, "Configure Bridge Failure"));
                }

            }


            @Override
            public void onExitCMDMode(boolean success, NetworkProtocol protocol) {
                Log.d(TAG, "onExitCMDMode:" + success);

            }

            @Override
            public void onReload(boolean success) {
                Log.d(TAG, "onReload:" + success);

            }

            @Override
            public void onReset(boolean success) {
                Log.d(TAG, "onReset:" + success);

            }

            @Override
            public void onSendFile(boolean success) {

            }

            @Override
            public void onResponseOfSendFile(String response) {

            }
        };
        mATCommand.setListener(mATCommandListener);

    }

    private void closeActions() {

        if (mUdpUnicast != null) {
            Log.d(TAG, "Close udp socket");
            mUdpUnicast.close();

        }

    }

    /**
     * Show timeout alert
     */
    private void showConnectionTimedOut(int dialogType) {
        if (mDialogManager == null) {
            mDialogManager = new SmartConnectDialogManager(AtmelSmartConfig.this);
        }
        mDialogManager.showCustomAlertDialog(dialogType);
    }

    /**
     * Show Failure alert
     */
    private void showFailureAlert(int dialogType) {
        if (mDialogManager == null) {
            mDialogManager = new SmartConnectDialogManager(AtmelSmartConfig.this);
        }
        mDialogManager.showCustomAlertDialog(dialogType);
    }

    /**
     * Throws an alert to user stating the success message recieved after
     * configuration
     */
    private void showConnectionSuccess(int dialogType) {
        if (mDialogManager == null) {
            mDialogManager = new SmartConnectDialogManager(AtmelSmartConfig.this);
        }
        mDialogManager.showCustomAlertDialog(dialogType);
    }

    /**
     * Default listener for checkbox the encrypted key is enabled or disabled
     * based on check
     * <p/>
     * if it is checked we need to ensure the length of key is exactly 16 else
     * start is disabled.
     * <p/>
     * The start button is made semi transperent and click is disabled if above
     * case fails
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        /**
         * The start button is made semi transperent and click is disabled if
         * length is not 16chars case fails
         */
        if (isChecked) {
            if (mKeyInputField.length() != 16) {
                mStartConfig.setEnabled(false);
                mStartConfig.getBackground().setAlpha(150);

            } else {
                mStartConfig.setEnabled(false);
                mStartConfig.getBackground().setAlpha(255);
            }
            mKeyInputField.setEnabled(true);
            mKeyInputField.setFocusable(true);
            mKeyInputField.setFocusableInTouchMode(true);
            mKeyInputField.setTextColor(getResources().getColor(R.color.black));
        } else {
            mKeyInputField.setEnabled(false);
            mKeyInputField.setFocusable(false);
            mKeyInputField.setFocusableInTouchMode(false);
            mKeyInputField.setTextColor(getResources().getColor(
                    R.color.disabled_text_color));
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        // overriden method for text changed listener

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        // overriden method for text changed listener
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        /**
         * if is checked and length matches 16 the start is enabled else
         * disabled
         */
        if (mconfig_key_checkbox.isChecked()) {
            textCount = mKeyInputField.getText().length();
            if (textCount == 16) {
                mStartConfig.setEnabled(true);
                mStartConfig.getBackground().setAlpha(255);
            } else {
                mStartConfig.setEnabled(false);
                mStartConfig.getBackground().setAlpha(150);
            }
        }
    }

    /**
     * A broadcast reciever which is registered to notify the app about the
     * changes in network or Access point is switched by the Device WIfimanager
     */
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
                if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, true)) {
                    mSSIDInputField.setText("");
                }
            }
            if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo info = intent
                        .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getType() == ConnectivityManager.TYPE_WIFI && info.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                    isNetworkConnecting = true;
                    mSSIDInputField.setText(mSmartWifiManager.getCurrentSSID());
                    mSSIDInputField.setEnabled(false);
                    mSSIDInputField.setFocusable(false);
                    mSSIDInputField.setFocusableInTouchMode(false);
                }
            }
        }
    };
}
