package com.gizwits.framework.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.gizwits.smartlight.R;

/**
 * Created by water.zhou on 7/8/2015.
 */
public class SmartConnectDialogManager implements DialogInterface.OnClickListener
{
    /**
     * Called activity context
     */
    private Context mContext=null;
    /**
     * Alert dialog instance
     */
    private AlertDialog.Builder mAlertDialog=null;

    /**
     * Constructor for custom alert dialog.accepting context of called activity
     * @param mContext
     */
    public SmartConnectDialogManager(Context mContext)
    {
        this.mContext=mContext;
    }

    public void showCustomAlertDialog(int dialogType)
    {

        mAlertDialog=new AlertDialog.Builder(mContext);
        switch (dialogType)
        {

            case SmartConnectContants.DLG_NO_WIFI_AVAILABLE:
                mAlertDialog.setTitle(mContext.getResources().getString(R.string.alert_smartlink_title));
                mAlertDialog.setMessage(mContext.getResources().getString(R.string.alert_no_network_title));

                break;

            case SmartConnectContants.DLG_CONNECTION_SUCCESS:
                mAlertDialog.setTitle(mContext.getResources().getString(R.string.alert_smartlink_title));
                mAlertDialog.setMessage(mContext.getResources().getString(R.string.alert_successfully_connected));

                break;

            case SmartConnectContants.DLG_CONNECTION_FAILURE:
                mAlertDialog.setTitle(mContext.getResources().getString(R.string.alert_smartlink_title));
                mAlertDialog.setMessage(mContext.getResources().getString(R.string.alert_connection_failed));

                break;

            /*case SmartConnectContants.DLG_CONNECTION_TIMEOUT:
                mAlertDialog.setTitle(mContext.getResources().getString(R.string.alert_smartlink_title));
                mAlertDialog.setMessage(mContext.getResources().getString(R.string.alert_connection_timeout));

                break;*/


            case SmartConnectContants.DLG_SSID_INVALID:
                mAlertDialog.setTitle(mContext.getResources().getString(R.string.alert_invalid_input_title));
                mAlertDialog.setMessage(mContext.getResources().getString(R.string.alert_no_network_title));
                break;

            case SmartConnectContants.DLG_GATEWAY_IP_INVALID:
                mAlertDialog.setTitle(mContext.getResources().getString(R.string.alert_invalid_input_title));
                mAlertDialog.setMessage(mContext.getResources().getString(R.string.alert_no_network_title));
                break;

            case SmartConnectContants.DLG_KEY_INVALID:
                mAlertDialog.setTitle(mContext.getResources().getString(R.string.alert_invalid_input_title));
                mAlertDialog.setMessage(mContext.getResources().getString(R.string.alert_invalid_key_mesg));
                break;

            case SmartConnectContants.DLG_PASSWORD_INVALID:
                mAlertDialog.setTitle(mContext.getResources().getString(R.string.alert_invalid_input_title));
                mAlertDialog.setMessage(mContext.getResources().getString(R.string.alert_no_network_title));
                break;

        }
        mAlertDialog.setPositiveButton((mContext.getResources().getString(R.string.smartlink_string_ok)).toUpperCase(), this);
        mAlertDialog.show();

    }


    @Override
    public void onClick(DialogInterface dialog, int dialogType)
    {

        if(dialogType==DialogInterface.BUTTON_POSITIVE)
        {
            dialog.dismiss();
        }
    }
}

