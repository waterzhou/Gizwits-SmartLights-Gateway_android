/**
 * lampW_frameG.png
 * lampW_frameW.png
 * lampY_frameG.png
 * lampY_frameY.png * Project Name:XPGSdkV4AppBase
 * File Name:MainControlActivity.java
 * Package Name:com.gizwits.centercontrolled.activity.control
 * Date:2015-1-27 14:44:17
 * Copyright (c) 2014~2015 Xtreme Programming Group, Inc.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.gizwits.smartlight.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.gizwits.framework.activity.BaseActivity;
import com.gizwits.framework.activity.account.UserManageActivity;
import com.gizwits.framework.activity.device.DeviceListActivity;
import com.gizwits.framework.activity.device.DeviceManageListActivity;
import com.gizwits.framework.activity.help.AboutActivity;
import com.gizwits.framework.activity.help.HelpActivity;
import com.gizwits.framework.adapter.MenuDeviceAdapter;
import com.gizwits.framework.config.Configs;
import com.gizwits.framework.config.JsonKeys;
import com.gizwits.framework.control.ColorSelectSeekBar;
import com.gizwits.framework.control.Group;
import com.gizwits.framework.control.ItemListBaseAdapter;
import com.gizwits.framework.control.TextMoveLayout;
import com.gizwits.framework.control.scene;
import com.gizwits.framework.entity.GroupDevice;
import com.gizwits.framework.utils.DensityUtil;
import com.gizwits.framework.utils.DialogManager;
import com.gizwits.framework.widget.RefreshableListView;
import com.gizwits.framework.widget.RefreshableListView.OnRefreshListener;
import com.gizwits.framework.widget.SlidingMenu;
import com.gizwits.smartlight.R;
import com.gizwits.smartlight.adapter.GroupAdapter;
import com.xpg.common.system.IntentUtils;
import com.xtremeprog.xpgconnect.XPGWifiCentralControlDevice;
import com.xtremeprog.xpgconnect.XPGWifiCentralControlDeviceListener;
import com.xtremeprog.xpgconnect.XPGWifiDevice;
import com.xtremeprog.xpgconnect.XPGWifiGroup;
import com.xtremeprog.xpgconnect.XPGWifiSubDevice;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

// TODO: Auto-generated Javadoc

/**
 * Created by Lien on 14/12/21.
 * <p/>
 * 设备主控界面
 *
 * @author Lien
 */
public class MainListActivity extends BaseActivity implements OnClickListener {

    /**
     * The tag.
     */
    private final String TAG = "MainControlActivity";
    // private XPGWifiDevice device;

    /**
     * The scl content.
     */
    private RefreshableListView sclContent;

    /**
     * The m view.
     */
    private SlidingMenu mView;

    /**
     * the btn alpha bg
     */
    private Button alpha_bg;

    /**
     * the ll foot.
     */
    private LinearLayout llFooter;

    /**
     * The ll bottom.
     */
    private LinearLayout llBottom;

    /**
     * The iv Edit.
     */
    public ImageView ivEdit;

    /**
     * The iv menu.
     */
    private ImageView ivMenu;

    /**
     * The tv title.
     */
    private TextView tvTitle;

    /**
     * the TextVIew light_name
     */
    public TextView tvLName;


    /**
     * the TextVIew scene_name
     */
    public TextView tvSceneName;

    private EditText tvEditSceneName;
    /**
     * the iv bottom edit_group Btn
     */
    public ImageView etGroup;


    /**
     * the TextView turn on off
     */
    private TextView btnSwitch;

    /**
     * the sb light adjust
     */
    public SeekBar sbLightness;

    /**
     * the sb light adjust
     */
    public SeekBar sbSaturation;

    /**
     * the sb color adjust
     */
    public ColorSelectSeekBar sbColor;

    public Button addSceneButton;
    public Button iftttButton;
    public RelativeLayout sceneLayout;
    /**
     * the move text following  seekbar
     */
    private TextMoveLayout textMoveLayout;
    private ViewGroup.LayoutParams layoutParams_light;
    private ViewGroup.LayoutParams layoutParams_hue;
    private ViewGroup.LayoutParams layoutParams_saturation;
    private TextView text_light, text_hue, text_saturation;

    /**
     * 托动条的移动步调
     */
    private float moveStep1 = 0;//For lightness
    private float moveStep2 = 0;//For hue
    private float moveStep3 = 0;//For saturation

    private int mLightness = 0;
    private int mHue = 0;
    private int mSaturation = 0;
    /**
     * 屏幕宽度
     */
    private int screenWidth;
    /**
     * The m adapter.
     */
    private MenuDeviceAdapter mAdapter;

    private ItemListBaseAdapter mSceneAdapter;

    public ListView sceneListView;

    public ArrayList<scene> scene_details = new ArrayList<scene>();
    public ArrayList<scene> sceneList = new ArrayList<scene>();


    /**
     * The lv device.
     */
    private ListView lvDevice;

    /**
     * the pdialog getStatusProgress
     */
    private ProgressDialog getStatusProgress;

    /**
     * The device data map.
     */
    private ConcurrentHashMap<String, Object> deviceDataMap;

    /**
     * The statu map.
     */
    private Map<String, Object> statuMap;

    /**
     * The progress dialog.
     */
    private ProgressDialog progressDialog;

    /**
     * The disconnect dialog.
     */
    private Dialog mDisconnectDialog;

    /**
     * The XPGWifiCentralControlDevice centralControlDevice
     */
    public XPGWifiCentralControlDevice centralControlDevice;

    /**
     * the list device
     * the key listitem name
     * the value listitem contains ledlist sdid
     */
    public Map<String, List<GroupDevice>> mapList = new HashMap<String, List<GroupDevice>>();
    /**
     * the list groupList
     * the key group name
     * the value group contains ledlist sdid
     */
    //保存每一个组中包含的灯的状态
    public Map<String, List<String>> groupMapList = new HashMap<String, List<String>>();

    //public  List<String> grouplist2 = new ArrayList<String>();
    /**
     * the list ledList
     */
    // 保存所有灯的设备状态
    public List<GroupDevice> ledList = new ArrayList<GroupDevice>();

    /**
     * the list ControllerList
     */
    // 保存所有灯的设备状态
    public List<GroupDevice> ControllerList = new ArrayList<GroupDevice>();
    /**
     * the listview item namelist
     */
    public List<String> list = new ArrayList<String>();
    /**
     * the list Delete Btn list
     */
    public List<ImageView> ivDels = new ArrayList<ImageView>();

    public byte[] mCmd;
    /**
     * the wifisubdevice status subDevice
     */
    XPGWifiSubDevice subDevice;

    /**
     * the Drawable pic white light
     */
    public Drawable wController;

    /**
     * the Drawable pic white light
     */
    public Drawable wControllerOn;
    /**
     * the Drawable pic white light
     */
    public Drawable wLight;
    /**
     * the Drawable pic yellow light
     */
    public Drawable yLight;
    /**
     * the Drawable pic white light select
     */
    public Drawable wLightSelect;
    /**
     * the Drawable pic yellow light select
     */
    public Drawable yLightSelect;
    /**
     * the Drawable pic add
     */
    public Drawable add;
    /**
     * the Drawable pic power_on
     */
    public Drawable power_on;
    /**
     * the Drawable pic power_off
     */
    public Drawable power_off;

    /**
     * the groupadapter devicelist
     */
    public GroupAdapter mGroupAdapter;

    /**
     * the tv selecting tv
     */
    public TextView selecttv;

    /**
     * the XPGWifiSubDevice selcetSubDevice
     */
    public XPGWifiSubDevice selectSubDevice;

    /**
     * the String select group name
     */
    public String selectGroup = "";

    private boolean bGroupOnff = false;

    /**
     * the device select show Item
     */
    public List<String> showItemDevices = new ArrayList<String>();

    /**
     * 是否超时标志位
     */
    private boolean isTimeOut = false;

    /**
     * 底部控制延时
     */
    private long switchTime = 0;

    /**
     * ClassName: Enum handler_key. <br/>
     * <br/>
     * date: 2014-11-26 17:51:10 <br/>
     *
     * @author Lien
     */
    private enum handler_key {

        /**
         * 更新UI界面
         */
        UPDATE_UI,
        REBOOT_MCU,

        /**
         * 显示警告
         */
        ALARM,

        /**
         * 设备断开连接
         */
        DISCONNECTED,

        GROUP_UPDATE_UI,
        /**
         * 接收到设备的数据
         */
        RECEIVED,

        /**
         * The login start.
         */
        LOGIN_START,

        /**
         * The login success.
         */
        LOGIN_SUCCESS,

        /**
         * The login fail.
         */
        LOGIN_FAIL,

        /**
         * The login timeout.
         */
        LOGIN_TIMEOUT,

        /**
         * The device get status
         */
        DEVICE_GETSTATUS,

        /**
         * The device get status
         */
        CMD_SWITCH,

        /**
         * The device get status
         */
        CMD_LIGHT,
    }


    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 3];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 3] = hexArray[v >>> 4];
            hexChars[j * 3 + 1] = hexArray[v & 0x0F];
            hexChars[j * 3 + 2] = ' ';
        }
        return new String(hexChars);
    }

    protected XPGWifiCentralControlDeviceListener xpgWifiCentralControlDeviceListener = new XPGWifiCentralControlDeviceListener() {
        public void didDiscovered(int error,
                                  List<XPGWifiSubDevice> subDeviceList) {
            MainListActivity.this.didSubDiscovered(error, subDeviceList);
        }

        public void didReceiveData(XPGWifiDevice device,
                                   ConcurrentHashMap<String, Object> dataMap, int result) {
            Log.i(TAG, "Central control:" + selectGroup + " didReceiveData:" + dataMap.toString());
            if (dataMap.get("binary") != null) {
                mCmd = (byte[]) dataMap.get("binary");
                Log.i(TAG, "Binary data:" + bytesToHex(mCmd));
                // Response from get group list cmd
                if (mCmd[0] == 0x03 && mCmd[1] == 0x11) {
                    grouplist.clear();
                    groupMapList.clear();
                    if (mCmd[2] > 0x0) {
                        String groupName;
                        boolean bIgnore = false;
                        int index = 0;
                        int subdevNum = 0;

                        int groupNum = mCmd[2];
                        Log.i(TAG, "group number is " + groupNum);
                        if (groupNum == 0)
                            return;
                        // Gid
                        Group tempGroup = new Group();
                        int groupAddr = mCmd[3];
                        tempGroup.gid = Integer.toString(groupAddr);
                        //tempGroup.gid = Integer.toString(groupAddr);
                        Log.i(TAG, "Group id=" + groupAddr);
                        //GroupName
                         /*For first group, it is special*/
                        groupName = new String(mCmd, 4, 8);
                        groupName = groupName.trim();
                        Log.i(TAG, "Group name=" + groupName);
                        tempGroup.groupName = groupName;
                        for (int i = 0; i < grouplist.size(); i++) {
                            if ((grouplist.get(i).gid.equals(tempGroup.gid))) {
                                Log.i(TAG, "Ignore to add group");
                                bIgnore = true;
                            }
                        }
                        if (!bIgnore)
                            grouplist.add(tempGroup);
                        subdevNum = mCmd[12];//cmd(1) + subcmd(1) + groupNum(1) + group addr(1) + group name(8) + sub device Num(1)  + sub device addr(N) + Sum(1)
                        Log.i(TAG, "sub dev Num=" + subdevNum);
                        List<String> strings = new ArrayList<String>();
                        for (int h = 0; h < subdevNum; h++) {
                            strings.add(Byte.toString(mCmd[13 + h]));
                        }
                        groupMapList.put(tempGroup.groupName, strings);

                        index = 12 + subdevNum + 1;
                        // for more than one group
                        for (int j = 0; j < groupNum - 1; j++) {
                            Group tempGroup2 = new Group();
                            tempGroup2.gid = Integer.toString(mCmd[index]);
                            Log.i(TAG, "Group id=" + tempGroup2.gid);
                            groupName = new String(mCmd, index + 1, 8);
                            groupName = groupName.trim();
                            Log.i(TAG, "group name=" + groupName);
                            tempGroup2.groupName = groupName;
                            for (int i = 0; i < grouplist.size(); i++) {
                                if ((grouplist.get(i).gid.equals(tempGroup2.gid))) {
                                    Log.i(TAG, "Ignore to add group");
                                    bIgnore = true;
                                }
                            }
                            if (!bIgnore)
                                grouplist.add(tempGroup2);
                            subdevNum = mCmd[index + 1 + 8];//cmd(1) + subcmd(1) +groupNum(1) + group addr(1) + group name(8) + sub device Num(1)  + sub device addr(N) + Sum(1)
                            Log.i(TAG, "sub dev Num=" + subdevNum);
                            List<String> strings2 = new ArrayList<String>();
                            for (int w = 0; w < subdevNum; w++) {
                                strings2.add(Byte.toString(mCmd[index + 10 + w]));
                            }
                            groupMapList.put(groupName, strings2);
                            index += (10 + subdevNum);
                        }

                        Log.i(TAG, "groupMapList=" + groupMapList.toString());

                        Log.i(TAG, "grouplist=" + grouplist.toString());

                    }
                    putStatusToViewList(null, null);
                    handler.sendEmptyMessage(handler_key.UPDATE_UI.ordinal());
                    //myGroupAdapterNotify();
                }
                // Delete group cmd
                if (mCmd[0] == 0x03 && mCmd[1] == 0x17) {
                    if (mCmd[2] == 0x01) {
                        Log.i(TAG, "Delete group successfully");
                        putStatusToViewList(null, null);
                        handler.sendEmptyMessage(handler_key.UPDATE_UI.ordinal());
                    }
                }
                // Add group cmd
                if (mCmd[0] == 0x03 && mCmd[1] == 0x15) {
                    if (mCmd[2] == 0x01) {
                        Log.i(TAG, "Add group successfully");
                    }
                }
                //Response from control group on/off
                if (mCmd[0] == 0x03 && mCmd[1] == 0x19) {
                    // Stand for success
                    if (mCmd[2] == 0x01) {
                        boolean isOk = false;
                        if (selectGroup != "") {
                            Log.i(TAG, "Group on/off selectGroup: " + selectGroup);
                            for (int j = 0; j < groupMapList.get(selectGroup).size(); j++) {
                                for (int i = 0; i < ledList.size(); i++) {
                                    if (ledList.get(i).getSubDevice().getSubDid().equals(groupMapList.get(selectGroup).get(j))) {
                                        if (ledList.get(i).isOnOff()) {
                                            Log.i(TAG, "will revert switch pic off i=" + i);
                                            //status revert first, this status change will affect the UI change, but UI change can't be call here.
                                            ledList.get(i).setOnOff(false);
                                        } else {
                                            Log.i(TAG, "will revert switch pic on i" + i);
                                            ledList.get(i).setOnOff(true);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // Should send to main activity to update UI instead of
                    handler.sendEmptyMessage(handler_key.GROUP_UPDATE_UI.ordinal());
                    getLedStatus();
                }
                //Response from group level control
                if (mCmd[0] == 0x03 && (mCmd[1] == 0x21 || mCmd[1] == 0x23 || mCmd[1] == 0x25)) {
                    if (mCmd[2] == 0x01) {
                        getLedStatus();
                        Log.i(TAG, "Level/hue/saturation group successfully");
                    }
                }

                //Response from all scene get list
                if (mCmd[0] == 0x03 && mCmd[1] == 0x3b) {
                    sceneList.clear();
                    int sceneNum = mCmd[2];
                    if (sceneNum == 0)
                        return;
                    scene tempScene = new scene("", "", "", 0);
                    String sceneName;
                    int sceneId;
                    int index;
                    sceneId = mCmd[3];
                    sceneName = new String(mCmd, 4, 8);
                    sceneName = sceneName.trim();
                    if (sceneName.isEmpty()) {
                        if (sceneList.isEmpty())
                            tempScene.setName("scene0");
                        else
                            tempScene.setName("scene" + (sceneList.size()));
                    } else {
                        tempScene.setName(sceneName);
                    }
                    Log.i(TAG, "scene name=" + tempScene.getName());
                    tempScene.setId(sceneId);
                    sceneList.add(tempScene);

                    index = 2 + 1 + 1 + 8 + 1;
                   /*First scene is not regular, but later will be OK*/
                    Log.i(TAG, "scene num is " + sceneNum + " index=" + index);
                    for (int j = 0; j < sceneNum - 1; j++) {
                        scene tempScene2 = new scene("", "", "", 0);
                        sceneId = mCmd[index];
                        sceneName = new String(mCmd, index + 1, 8);
                        sceneName = sceneName.trim();
                        if (sceneName.isEmpty()) {
                            Log.i(TAG, "scene Name is empty");
                            if (sceneList.isEmpty())
                                tempScene2.setName("scene0");
                            else
                                tempScene2.setName("scene" + (sceneList.size()));
                        } else {
                            tempScene2.setName(sceneName);
                        }
                        tempScene2.setId(sceneId);
                        Log.i(TAG, "scene name=" + tempScene2.getName());
                        sceneList.add(tempScene2);
                        index += 10;
                    }
                    Log.i(TAG, "Get all scene successfully");
                    iftttButton.setClickable(true);
                }
                //Response from group scene get list
                if (mCmd[0] == 0x03 && mCmd[1] == 0x31) {
                    int sceneNum = mCmd[2];
                    if (sceneNum == 0)
                        return;
                    scene tempScene = new scene("", "", "", 0);
                    String sceneName;
                    int sceneId;
                    int index;
                    sceneId = mCmd[3];
                    sceneName = new String(mCmd, 4, 8);
                    sceneName = sceneName.trim();
                    if (sceneName.isEmpty()) {
                        if (scene_details.isEmpty())
                            tempScene.setName("scene0");
                        else
                            tempScene.setName("scene" + (scene_details.size()));
                    } else {
                        tempScene.setName(sceneName);
                    }
                    Log.i(TAG, "scene name=" + tempScene.getName());
                    tempScene.setId(sceneId);
                    tempScene.setGroup(selectGroup);
                    scene_details.add(tempScene);
                    if (isBottomShow())
                        handler.sendEmptyMessage(handler_key.UPDATE_UI.ordinal());
                    index = 2 + 1 + 1 + 8 + 1;
                   /*First scene is not regular, but later will be OK*/
                    Log.i(TAG, "scene num is " + sceneNum + " index=" + index);
                    for (int j = 0; j < sceneNum - 1; j++) {
                        scene tempScene2 = new scene("", "", "", 0);
                        sceneId = mCmd[index];
                        sceneName = new String(mCmd, index + 1, 8);
                        sceneName = sceneName.trim();
                        if (sceneName.isEmpty()) {
                            Log.i(TAG, "scene Name is empty");
                            if (scene_details.isEmpty())
                                tempScene2.setName("scene0");
                            else
                                tempScene2.setName("scene" + (scene_details.size()));
                        } else {
                            tempScene2.setName(sceneName);
                        }
                        tempScene2.setId(sceneId);
                        tempScene2.setGroup(selectGroup);
                        Log.i(TAG, "scene name=" + tempScene2.getName());
                        scene_details.add(tempScene2);
                        if (isBottomShow())
                            handler.sendEmptyMessage(handler_key.UPDATE_UI.ordinal());
                        index += 10;
                    }
                    Log.i(TAG, "Get scene successfully");
                }
                // Response from Add scene
                if (mCmd[0] == 0x03 && mCmd[1] == 0x35) {
                    if (mCmd[3] == 0x01) {
                        Log.i(TAG, "Add scene successfully");
                        scene tempS = new scene("", "", "", 0);
                        tempS.setId(mCmd[2]);
                        tempS.setGroup(selectGroup);
                        if (tvEditSceneName.getText().toString().trim().isEmpty()) {
                            if (scene_details.isEmpty())
                                tempS.setName("scene0");
                            else
                                tempS.setName("scene" + (scene_details.size()));
                        } else {
                            tempS.setName(tvEditSceneName.getText().toString());
                        }
                        scene_details.add(tempS);
                        handler.sendEmptyMessage(handler_key.UPDATE_UI.ordinal());
                        tvSceneName.setText(tempS.getName());
                    }
                }
                // Response from factory reset zigbee
                if(mCmd[0] == 0x03 && mCmd[1] == 0x51)
                {
                    if (mCmd[2] == 0x01)
                    {
                        Log.i(TAG, "factory reset zigbee successfully");
                        handler.sendEmptyMessage(handler_key.REBOOT_MCU.ordinal());
                        //mCenter.cRebootGroups(setmanager.getUid(), setmanager.getToken(), Configs.PRODUCT_KEY_Sub);//获取组
                        //mCenter.cGetSubDevicesList(centralControlDevice);
                    }
                }
                if(mCmd[0] == 0x03 && mCmd[1] == 0x53)
                {
                    mCenter.cDisconnect(mXpgWifiDevice);
                    DisconnectOtherDevice();
                    IntentUtils.getInstance().startActivity(MainListActivity.this,
                            DeviceListActivity.class);
                    finish();
                }


            } else {
                MainListActivity.this.didSubReceiveData((XPGWifiSubDevice) device,
                        dataMap, result);
            }
        }

        @Override
        public void didDisconnected(XPGWifiDevice device) {
            MainListActivity.this.didDisconnected(device);
        }
    };
    /**
     * The handler.
     */
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handler_key key = handler_key.values()[msg.what];
            switch (key) {
                case REBOOT_MCU:
                    mCenter.cRebootGroups(setmanager.getUid(), setmanager.getToken(), Configs.PRODUCT_KEY_Sub);//获取组

                    break;
                case DEVICE_GETSTATUS:
                    XPGWifiSubDevice subObj = (XPGWifiSubDevice) msg.obj;
                    mCenter.cGetSubStatus(subObj);
                    break;
                case RECEIVED:
                    Log.i(TAG, "receive data:");
                    try {
                        if (deviceDataMap.get("data") != null) {
                            inputDataToMaps(statuMap,
                                    (String) deviceDataMap.get("data"));

                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    //break;
                case UPDATE_UI:
                    //状态置入本地LedList状态列表
                    Log.i(TAG, "Update UI==================================================");
                    if (subDevice != null) {
                        Log.i(TAG, "UI subkey=" + subDevice.getSubProductKey());
                        if (subDevice.getSubProductKey().equals(Configs.PRODUCT_KEY_Sub_Controller)) {
                            for (int i = 0; i < ControllerList.size(); i++) {
                                try {
                                    if (ControllerList.get(i).getSubDevice().getSubDid().equals(subDevice.getSubDid())) {
                                        Log.i(TAG, "Controller subDevice=" + subDevice.getSubDid() + " " + statuMap.get(JsonKeys.ON_OFF));
                                        ControllerList.get(i).setOnOff((Boolean) statuMap.get(JsonKeys.ON_OFF));
                                    }
                                } catch (Exception e) {
                                    // TODO: handle exception
                                    e.printStackTrace();
                                }
                            }

                        } else {
                            for (int i = 0; i < ledList.size(); i++) {
                                try {
                                    if (ledList.get(i).getSubDevice().getSubDid().equals(subDevice.getSubDid())) {
                                        Log.i(TAG, "subDevice=" + subDevice.getSubDid());
                                        ledList.get(i).setOnOff((Boolean) statuMap.get(JsonKeys.ON_OFF));
                                        ledList.get(i).setLightness(Integer.parseInt(statuMap.get(JsonKeys.LIGHTNESS).toString()));
                                        ledList.get(i).setColor(Integer.parseInt(statuMap.get(JsonKeys.COLORNESS).toString()));
                                        ledList.get(i).setSaturation(Integer.parseInt(statuMap.get(JsonKeys.SATURATION).toString()));
                                    }
                                } catch (Exception e) {
                                    // TODO: handle exception
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    myGroupAdapterNotify();
                    break;
                case GROUP_UPDATE_UI:
                    boolean isOk = false;
                    if (selectGroup != "") {
                        Log.i(TAG, "selectGroup=" + selectGroup.toString());
                        for (int j = 0; j < groupMapList.get(selectGroup).size(); j++) {
                            for (int i = 0; i < ledList.size(); i++) {
                                if (ledList.get(i).getSubDevice().getSubDid().equals(groupMapList.get(selectGroup).get(j))) {
                                    if (ledList.get(i).isOnOff()) {
                                        Log.i(TAG, "switch on i=" + i);
                                        switchOn();
                                    } else {
                                        switchOff();
                                        Log.i(TAG, "switch off i=" + i);
                                    }
                                    isOk = true;
                                    break;
                                }
                            }
                            if (isOk) {
                                break;
                            }
                        }
                    }
                    //********************************************************************************
                    myGroupAdapterNotify();
                    break;
                case DISCONNECTED:
                    Log.e(TAG, "disconnnect");
                    DialogManager.showDialog(MainListActivity.this, mDisconnectDialog);
                    break;
                case LOGIN_SUCCESS:
                    handler.removeMessages(handler_key.LOGIN_TIMEOUT.ordinal());
                    progressDialog.cancel();
                    if (mView.isOpen()) {
                        mView.toggle();
                    }
                    onResume();
                    break;
                case LOGIN_FAIL:
                    handler.removeMessages(handler_key.LOGIN_TIMEOUT.ordinal());
                    handler.sendEmptyMessage(handler_key.DISCONNECTED.ordinal());
                    break;
                case LOGIN_TIMEOUT:
                    Log.e(TAG, "timeout");
                    isTimeOut = true;
                    progressDialog.cancel();
                    handler.sendEmptyMessage(handler_key.DISCONNECTED.ordinal());
                    if (mXpgWifiDevice != null && mXpgWifiDevice.isConnected()) {
                        mCenter.cDisconnect(mXpgWifiDevice);
                        DisconnectOtherDevice();
                    }
                    break;
                case CMD_SWITCH:
                    mCenter.cSwitchOn((XPGWifiSubDevice) msg.obj, msg.arg1 == 0);
                    break;
                case CMD_LIGHT:
                    mCenter.cLightness((XPGWifiSubDevice) msg.obj, msg.arg1);
                    break;
            }
        }
    };

    /*
     * (non-Javadoc)
     *
     * @see
     * com.gizwits.centercontrolled.activity.BaseActivity#onCreate(android.os
     * .Bundle )
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_control);
        initViews();
        initEvents();
        initParams();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.gizwits.centercontrolled.activity.BaseActivity#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        //refreshMenu();
        //中控监听
        Log.d(TAG, "centralControlsetListener");
        centralControlDevice = (XPGWifiCentralControlDevice) mXpgWifiDevice;
        centralControlDevice.setListener(xpgWifiCentralControlDeviceListener);
        mCenter.cSetXPGWifiCentralControlDevice(centralControlDevice);
        mCenter.cSetDid(centralControlDevice.getDid());

        //First get group information then subdeviceList
        mCenter.cGetGroups(setmanager.getUid(), setmanager.getToken(), Configs.PRODUCT_KEY_Sub);//获取组
        mCenter.cGetSubDevicesList(centralControlDevice);//获取子设备
        mCenter.cGetAllScenes(setmanager.getUid(), setmanager.getToken(), Configs.PRODUCT_KEY_Sub);//获取组

        // bottomClose();
        ledList.clear();
        ControllerList.clear();
        showItemDevices.clear();

        //展开3秒状态获取Loadding框
        getStatusProgress.show();
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                getStatusProgress.cancel();
                timer.cancel();
            }
        }, 3000);
    }


    /**
     * 更新菜单界面.
     *
     * @return void
     */
    private void refreshMenu() {
        initBindList();
        mAdapter.setChoosedPos(-1);
        for (int i = 0; i < bindlist.size(); i++) {
            if (bindlist.get(i).getDid()
                    .equalsIgnoreCase(mXpgWifiDevice.getDid()))
                mAdapter.setChoosedPos(i);
        }

        //当前绑定列表没有当前操作设备
        if (mAdapter.getChoosedPos() == -1) {
            mAdapter.setChoosedPos(0);
            mXpgWifiDevice = mAdapter.getItem(0);
        }

        mAdapter.notifyDataSetChanged();

        int px = DensityUtil.dip2px(this, mAdapter.getCount() * 50);
        lvDevice.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, px));
    }

    /**
     * Inits the params.
     */
    private void initParams() {
        //初始化获取Drawable图片
        wController = this.getResources().getDrawable(R.drawable.socket);
        wController.setBounds(0, 0, wController.getMinimumWidth(), wController.getMinimumHeight());
        wControllerOn = this.getResources().getDrawable(R.drawable.socketon);
        wControllerOn.setBounds(0, 0, wControllerOn.getMinimumWidth(), wControllerOn.getMinimumHeight());

        wLight = this.getResources().getDrawable(R.drawable.lampw_framew);
        wLight.setBounds(0, 0, wLight.getMinimumWidth(), wLight.getMinimumHeight());
        yLight = this.getResources().getDrawable(R.drawable.lampy_framey);
        yLight.setBounds(0, 0, yLight.getMinimumWidth(), yLight.getMinimumHeight());
        wLightSelect = this.getResources().getDrawable(R.drawable.lampw_frameg);
        wLightSelect.setBounds(0, 0, wLightSelect.getMinimumWidth(), wLightSelect.getMinimumHeight());
        yLightSelect = this.getResources().getDrawable(R.drawable.lampy_frameg);
        yLightSelect.setBounds(0, 0, yLightSelect.getMinimumWidth(), yLightSelect.getMinimumHeight());
        power_on = this.getResources().getDrawable(R.drawable.icon_power);
        power_on.setBounds(0, 0, power_on.getMinimumWidth(), power_on.getMinimumHeight());
        power_off = this.getResources().getDrawable(R.drawable.icon_power_off);
        power_off.setBounds(0, 0, power_on.getMinimumWidth(), power_on.getMinimumHeight());
        add = this.getResources().getDrawable(R.drawable.icon_add);
        add.setBounds(0, 0, add.getMinimumWidth(), add.getMinimumHeight());

        statuMap = new ConcurrentHashMap<String, Object>();

        listViewSetNormal();

        mGroupAdapter = new GroupAdapter(this, mapList, list);
        sclContent.setAdapter(mGroupAdapter);

        getStatusProgress = new ProgressDialog(this);
        getStatusProgress.setMessage("Getting status...");
        getStatusProgress.setCancelable(false);

        mDisconnectDialog = DialogManager.getDisconnectDialog(this,
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogManager.dismissDialog(MainListActivity.this,
                                mDisconnectDialog);
                        IntentUtils.getInstance().startActivity(
                                MainListActivity.this,
                                DeviceListActivity.class);
                        finish();
                    }
                }
        );
    }

    /**
     * Inits the views.
     */
    private void initViews() {
        mView = (SlidingMenu) findViewById(R.id.main_layout);

        llFooter = (LinearLayout) findViewById(R.id.llFooter);
        alpha_bg = (Button) findViewById(R.id.black_alpha_bg);
        tvLName = (TextView) findViewById(R.id.show_led_name);
        tvSceneName = (TextView) findViewById(R.id.show_scene_name);
        tvEditSceneName = (EditText) findViewById(R.id.scene_name);
        //etGroup = (ImageView) findViewById(R.id.edit_group);
        llBottom = (LinearLayout) findViewById(R.id.llBottom);
        ivMenu = (ImageView) findViewById(R.id.ivMenu);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        ivEdit = (ImageView) findViewById(R.id.ivEdit);
        ivEdit.setTag("1");

        sclContent = (RefreshableListView) findViewById(R.id.sclContent);

        btnSwitch = (TextView) findViewById(R.id.btnSwitch);
        sbLightness = (SeekBar) findViewById(R.id.sbLightness);
        sbSaturation = (SeekBar) findViewById(R.id.sbSaturation);
        sbColor = (ColorSelectSeekBar) findViewById(R.id.sbcolor);
        addSceneButton = (Button) findViewById(R.id.btn_addscene);
        sceneLayout = (RelativeLayout) findViewById(R.id.relativeLayout2);
        //sceneRemove = (ImageView) findViewById(R.id.scene_remove);
        iftttButton = (Button) findViewById(R.id.ifttt);


        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        //For lightness
        text_light = new TextView(this);
        text_light.setBackgroundColor(Color.rgb(254, 254, 254));
        text_light.setTextColor(Color.rgb(0, 161, 229));
        text_light.setTextSize(12);
        layoutParams_light = new ViewGroup.LayoutParams(screenWidth, 50);
        textMoveLayout = (TextMoveLayout) findViewById(R.id.textLayout1);
        textMoveLayout.addView(text_light, layoutParams_light);
        text_light.layout(0, 20, screenWidth, 40);
        //For hue
        text_hue = new TextView(this);
        text_hue.setBackgroundColor(Color.rgb(254, 254, 254));
        text_hue.setTextColor(Color.rgb(0, 161, 229));
        text_hue.setTextSize(12);
        layoutParams_hue = new ViewGroup.LayoutParams(screenWidth, 50);
        textMoveLayout = (TextMoveLayout) findViewById(R.id.textLayout2);
        textMoveLayout.addView(text_hue, layoutParams_hue);
        text_hue.layout(0, 20, screenWidth, 40);
        //For saturation
        text_saturation = new TextView(this);
        text_saturation.setBackgroundColor(Color.rgb(254, 254, 254));
        text_saturation.setTextColor(Color.rgb(0, 161, 229));
        text_saturation.setTextSize(12);
        layoutParams_saturation = new ViewGroup.LayoutParams(screenWidth, 50);
        textMoveLayout = (TextMoveLayout) findViewById(R.id.textLayout3);
        textMoveLayout.addView(text_saturation, layoutParams_saturation);
        text_saturation.layout(0, 20, screenWidth, 40);


        mAdapter = new MenuDeviceAdapter(this, bindlist);
        lvDevice = (ListView) findViewById(R.id.lvDevice);
        lvDevice.setAdapter(mAdapter);

        progressDialog = new ProgressDialog(MainListActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Device connecting,waiting.....");

        mSceneAdapter = new ItemListBaseAdapter(this, R.layout.list_scene, scene_details);
        sceneListView = (ListView) findViewById(R.id.listView_Scene);
        sceneListView.setAdapter(mSceneAdapter);
    }

    /**
     * Inits the events.
     */
    private void initEvents() {
        ivMenu.setOnClickListener(this);
        tvTitle.setOnClickListener(this);
        llFooter.setOnClickListener(this);
        tvEditSceneName.setOnClickListener(this);
        iftttButton.setOnClickListener(this);
        iftttButton.setClickable(false);
        // etGroup.setOnClickListener(this);
        //For lightness
        moveStep1 = (float) (((float) screenWidth / (float) 254) * 0.8);
        //For Hue
        moveStep2 = (float) (((float) screenWidth / (float) 65279) * 0.8);
        //For saturation
        moveStep3 = (float) (((float) screenWidth / (float) 254) * 0.8);

        lvDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (!mAdapter.getItem(position).isOnline())
                    return;

                if (mAdapter.getChoosedPos() == position) {
                    mView.toggle();
                    return;
                }

                mAdapter.setChoosedPos(position);
                mXpgWifiDevice = bindlist.get(position);
                loginDevice(mXpgWifiDevice);
            }
        });
        sbLightness.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        sbLightness.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        sbLightness.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });
        sbLightness.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                mLightness = seekBar.getProgress();
                text_light.layout((int) (mLightness * moveStep1), 20, screenWidth, 80);
                text_light.setText(Integer.toString(mLightness));
                if (!selectGroup.equals("") && selectGroup != null) {
                    mCenter.cLightnessGroup(selectGroup, seekBar.getProgress());

                } else {
                    mCenter.cLightness(selectSubDevice, seekBar.getProgress());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
            }
        });

        sbSaturation.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        sbSaturation.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        sbSaturation.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });
        sbSaturation.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                mSaturation = seekBar.getProgress();
                text_saturation.layout((int) (mSaturation * moveStep3), 20, screenWidth, 80);
                text_saturation.setText(Integer.toString(mSaturation));

                if (!selectGroup.equals("") && selectGroup != null) {
                    mCenter.cSaturationGroup(selectGroup, seekBar.getProgress());
                } else {
                    mCenter.cSaturation(selectSubDevice, seekBar.getProgress());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
            }
        });

        sbColor.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        sbColor.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        sbColor.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });
        sbColor.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                mHue = seekBar.getProgress();
                text_hue.layout((int) (mHue * moveStep2), 20, screenWidth, 80);
                text_hue.setText(Integer.toString(mHue));
                if (!selectGroup.equals("") && selectGroup != null) {
                    mCenter.cColorGroup(selectGroup, seekBar.getProgress());
                } else {
                    mCenter.cColor(selectSubDevice, seekBar.getProgress());
                }
            }

            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar bar) {

            }
        });
        alpha_bg.setOnClickListener(this);
        ivEdit.setOnClickListener(this);
        sclContent.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                if (position == list.size() - 1) {
                    Intent intent = new Intent(MainListActivity.this, EditGroupActivity.class);
                    intent.putStringArrayListExtra("ledList", GroupDevice.getAllName(ledList));
                    intent.putExtra("did", "" + centralControlDevice.getDid());
                    startActivity(intent);
                }
            }
        });

        sclContent.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh(RefreshableListView listView) {
                // TODO Auto-generated method stub
                Log.i(TAG, "slip down to refresh.........");
                mCenter.cGetGroups(setmanager.getUid(), setmanager.getToken(), Configs.PRODUCT_KEY_Sub);//获取组
                mCenter.cGetSubDevicesList(centralControlDevice);

                final Timer timer = new Timer();
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        runOnUiThread(new Runnable() {
                            public void run() {
                                sclContent.completeRefreshing();
                            }
                        });
                        timer.cancel();
                    }
                }, 2000);
            }
        });
        btnSwitch.setOnClickListener(this);
        addSceneButton.setOnClickListener(this);

        sceneListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = sceneListView.getItemAtPosition(position);
                scene obj_itemDetails = (scene) o;
                Log.i(TAG, "click now is " + obj_itemDetails.getName() + " " + obj_itemDetails.getValue());
            }
        });
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
            case R.id.ivMenu:
                Log.i(TAG, "back key is pressed");
                mView.toggle();
                break;
            case R.id.btnSwitch:
                //每0.6秒智能操控一次，避免频繁操控--600
                if (switchTime + 1000 > System.currentTimeMillis()) {
                    return;
                }
                switchTime = System.currentTimeMillis();
                if (!selectGroup.equals("") && selectGroup != null) {
                    Log.i(TAG, "operate on group=" + selectGroup);
                    //获取ledList中存在组的设备进行控制
                    if (btnSwitch.getText().toString().equals("close")) {
                        mCenter.cSwitchOnGroup(selectGroup, false);
                    } else {
                        mCenter.cSwitchOnGroup(selectGroup, true);
                    }
                } else {
                    //单设备控制
                    if (btnSwitch.getText().toString().equals("close")) {
                        mCenter.cSwitchOn(selectSubDevice, false);
                    } else {
                        mCenter.cSwitchOn(selectSubDevice, true);
                    }
                }
                break;
            case R.id.ifttt:
                //底部菜单组编辑按钮
                Intent intent = new Intent(MainListActivity.this, EditIfttt.class);
                intent.putStringArrayListExtra("sceneList", GroupDevice.getAllSceneName(sceneList));
                intent.putStringArrayListExtra("ControllerList", GroupDevice.getAllName(ControllerList));
                intent.putExtra("did", "" + mXpgWifiDevice.getDid());
                startActivity(intent);
                break;
            case R.id.black_alpha_bg:
                bottomClose();
                break;
            case R.id.ivEdit:
                //右上角编辑按钮，点开展示所有删除按钮
                Log.i("showDel", "" + ivDels.size());
                bottomClose();

                if (ivEdit.getTag().toString().equals("1")) {
                    ivEdit.setImageResource(R.drawable.icon_confirm);
                    ivEdit.setTag("0");
                } else {
                    ivEdit.setImageResource(R.drawable.icon_edit_w);
                    ivEdit.setTag("1");
                }

                if (ivDels.size() < 1) {
                    return;
                }

                if (ivEdit.getTag().toString().equals("0")) {
                    for (ImageView ivDel : ivDels) {
                        ivDel.setVisibility(View.VISIBLE);
                    }
                } else {
                    for (ImageView ivDel : ivDels) {
                        ivDel.setVisibility(View.INVISIBLE);
                    }
                }
                break;
            case R.id.btn_addscene:
                String payload = "";
                scene newScene;
                boolean isIgnore = false;
                Log.i(TAG, "Click scene button");
                newScene = new scene("", "", "", 0);
                newScene.setValue(Integer.toString(mLightness) + ":" + Integer.toString(mHue) + ":" + Integer.toString(mSaturation));
                if (tvEditSceneName.getText().toString().trim().isEmpty()) {
                    if (scene_details.isEmpty())
                        newScene.setName("scene0");
                    else
                        newScene.setName("scene" + (scene_details.size()));
                } else {
                    newScene.setName(tvEditSceneName.getText().toString());
                }
                //If same should not add, need add this logic here....
                for (int i = 0; i < scene_details.size(); i++) {
                    if (scene_details.get(i).getValue().equals(newScene.getValue())) {
                        isIgnore = true;
                        Toast.makeText(this, "You have already add the same scene before!", Toast.LENGTH_LONG).show();
                        Log.i(TAG, "Already have such scene so ignore this action");
                    }
                }
                if (isIgnore)
                    break;

                // Also send cmd to gateway
                Log.i(TAG, "Send scene add cmd to gateway");
                payload += newScene.getName();
                payload += ":";

                if (!selectGroup.equals("") && selectGroup != null) {
                    for (int i = 0; i < groupMapList.get(selectGroup).size(); i++) {
                        if (i > 0)
                            payload += ",";
                        payload += groupMapList.get(selectGroup).get(i);
                    }
                    payload += ":";
                    payload += newScene.getValue();
                    mCenter.cAddScene(selectGroup, payload);
                }
                break;
        }
    }


    public void removeSceneOnClickHandler(View v) {
        scene itemToRemove = (scene) v.getTag();
        scene_details.remove(itemToRemove);
        mSceneAdapter.notifyDataSetChanged();
        mCenter.cRemoveScene(itemToRemove.getName(), String.valueOf(itemToRemove.getId()));
    }

    public void buttonSceneOnClickHandler(View v) {
        scene itemToRemove = (scene) v.getTag();
        Log.i(TAG, "switch scene on " + itemToRemove.getId());
        tvSceneName.setText(itemToRemove.getName());
        mCenter.cApplyScene(itemToRemove.getName(), String.valueOf(itemToRemove.getId()));
    }

    /**
     * 展开底部控制栏
     */
    public void bottomShow() {
        llBottom.setVisibility(View.VISIBLE);
        alpha_bg.setVisibility(View.VISIBLE);

    }

    /**
     * 底部控制栏是否在现
     */
    public boolean isBottomShow() {
        return llBottom.isShown();
    }

    /**
     * 关闭底部控制栏
     */
    public void bottomClose() {
        llBottom.setVisibility(View.GONE);
        alpha_bg.setVisibility(View.GONE);
        if (selecttv != null) {
            if (((GroupDevice) selecttv.getTag()).isOnOff()) {
                selecttv.setCompoundDrawables(null, yLight, null, null);
            } else {
                selecttv.setCompoundDrawables(null, wLight, null, null);
            }
        }
        selectSubDevice = null;
        selectGroup = "";
        selecttv = null;
        Log.i(TAG, "Clear scene_details===========");
        scene_details.clear();
        // Here need to notify to clear it or it will bring to single bulb's UI
        //myGroupAdapterNotify();
        handler.sendEmptyMessage(handler_key.UPDATE_UI.ordinal());
    }

    /**
     * 开灯状态下，底部按钮switch
     */
    public void switchOn() {
        Log.i(TAG, "btnSwitch on");
        btnSwitch.setText("close");
        btnSwitch.setTextColor(getResources().getColor(R.color.text_blue));
        btnSwitch.setCompoundDrawables(null, power_on, null, null);
    }

    /**
     * 关灯状态下，底部按钮switch
     */
    public void switchOff() {
        Log.i(TAG, "btnSwitch off");
        btnSwitch.setText("open");
        btnSwitch.setTextColor(getResources().getColor(R.color.text_gray));
        btnSwitch.setCompoundDrawables(null, power_off, null, null);
    }

    /**
     * 左侧菜单栏点击时间
     *
     * @param view 点击item项
     */
    public void onClickSlipBar(View view) {
        switch (view.getId()) {
            case R.id.rlDevice:
                IntentUtils.getInstance().startActivity(MainListActivity.this,
                        DeviceManageListActivity.class);
                break;
            case R.id.rlAbout:
                IntentUtils.getInstance().startActivity(MainListActivity.this,
                        AboutActivity.class);
                break;
            case R.id.rlAccount:
                IntentUtils.getInstance().startActivity(MainListActivity.this,
                        UserManageActivity.class);
                break;
            case R.id.rlHelp:
                IntentUtils.getInstance().startActivity(MainListActivity.this,
                        HelpActivity.class);
                break;
            case R.id.btnDeviceList:
                mCenter.cDisconnect(mXpgWifiDevice);
                DisconnectOtherDevice();
                IntentUtils.getInstance().startActivity(MainListActivity.this,
                        DeviceListActivity.class);
                finish();
                break;
            case R.id.btnFactoryReset:
                Log.i(TAG, "Zigbee factory reset");
                mCenter.cFactoryResetZigbee(setmanager.getUid(), setmanager.getToken(), Configs.PRODUCT_KEY_Sub);

                break;
        }
    }

    /**
     * Login device.
     *
     * @param xpgWifiDevice the xpg wifi device
     */
    private void loginDevice(XPGWifiDevice xpgWifiDevice) {
        mXpgWifiDevice = xpgWifiDevice;
        mXpgWifiDevice.setListener(deviceListener);
        DisconnectOtherDevice();
        mXpgWifiDevice.login(setmanager.getUid(), setmanager.getToken());
        progressDialog.show();
        isTimeOut = false;
        handler.sendEmptyMessageDelayed(handler_key.LOGIN_TIMEOUT.ordinal(),
                3000);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.gizwits.framework.activity.BaseActivity#didLogin(com.xtremeprog.
     * xpgconnect.XPGWifiDevice, int)
     */
    @Override
    protected void didLogin(XPGWifiDevice device, int result) {
        if (isTimeOut)
            return;

        if (result == 0) {
            handler.sendEmptyMessage(handler_key.LOGIN_SUCCESS.ordinal());
        } else {
            handler.sendEmptyMessage(handler_key.LOGIN_FAIL.ordinal());
        }

    }

    /**
     * 获取子设备灯状态
     */
    public void getLedStatus() {
        for (int i = 0; i < ledList.size(); i++) {
            Message msg = new Message();
            msg.what = handler_key.DEVICE_GETSTATUS.ordinal();
            msg.obj = ledList.get(i).getSubDevice();
            handler.sendMessage(msg);
        }
    }

    /**
     * 设置灯数据,每一次获取子设备，都重新设置maplist,list
     *
     * @param subDeviceList 如为空则不重置ledList
     */
    public void putStatusToViewList(List<XPGWifiSubDevice> subDeviceListLamp, List<XPGWifiSubDevice> subDeviceListController) {
        list.clear();
        mapList.clear();

        mapList.put("light", ledList);
        list.add("light");

        if (subDeviceListLamp != null) {
            ledList = GroupDevice.getGroupDeviceByList(subDeviceListLamp);
        }
        if (subDeviceListController != null) {
            ControllerList = GroupDevice.getGroupDeviceByList(subDeviceListController);
        }
        Log.i(TAG, "putStatusToViewList ledList=" + ledList.toString() + "ControllerList=" + ControllerList.toString());
        mapList.put("我的LED", ledList);
        list.add("我的LED");

        mapList.put("switch", ControllerList);
        //mapList.put("switch", ledList);
        list.add("switch");

        mapList.put("MyController", ControllerList);
        // mapList.put("MyController", ledList);
        list.add("MyController");

        mapList.put("group", ledList);
        list.add("group");


        for (int i = 0; i < grouplist.size(); i++) {
            list.add(grouplist.get(i).groupName);
            List<GroupDevice> gDevices = new ArrayList<GroupDevice>();
            for (int j = 0; j < groupMapList.get(grouplist.get(i).groupName).size(); j++) {
                GroupDevice gDevice = new GroupDevice();
                gDevice.setSdid(Integer.parseInt(groupMapList.get(grouplist.get(i).groupName).get(j)));
                gDevices.add(gDevice);
            }
            Log.i(TAG, "mapList new member " + i + " " + grouplist.get(i).groupName);
            mapList.put(grouplist.get(i).groupName, gDevices);
        }
        mapList.put("addGroup", ledList);
        list.add("addGroup");
    }

    /**
     * 重置ListView数据
     */
    public void listViewSetNormal() {
        list.clear();
        mapList.clear();
        mapList.put("light", ledList);
        list.add("light");
        mapList.put("我的LED", ledList);
        list.add("我的LED");
        mapList.put("switch", ControllerList);
        //mapList.put("switch", ledList);
        list.add("switch");
        mapList.put("MyController", ControllerList);
        //mapList.put("MyController", ledList);
        list.add("MyController");
        mapList.put("group", ledList);
        list.add("group");
        mapList.put("addGroup", ledList);
        list.add("addGroup");
    }

    /**
     * 更新Adapter状态
     */
    public void myGroupAdapterNotify() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (ivDels.size() > 0) {
                    ivDels.clear();
                }
                Log.i(TAG, "GroupAdapter Notify...");
                mGroupAdapter.notifyDataSetChanged();
                mSceneAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 检查出了选中device，其他device有没有连接上
     *
     * @return the XPG wifi device
     */
    private void DisconnectOtherDevice() {
        for (XPGWifiDevice theDevice : bindlist) {
            if (theDevice.isConnected()
                    && !theDevice.getDid().equalsIgnoreCase(
                    mXpgWifiDevice.getDid()))
                mCenter.cDisconnect(theDevice);
        }
        for (GroupDevice theDevice : ledList) {
            if (theDevice.getSubDevice().isConnected()
                    && !theDevice.getSubDevice().getDid().equalsIgnoreCase(
                    mXpgWifiDevice.getDid()))
                mCenter.cDisconnect(theDevice.getSubDevice());
        }
        for (GroupDevice theDevice : ControllerList) {
            if (theDevice.getSubDevice().isConnected()
                    && !theDevice.getSubDevice().getDid().equalsIgnoreCase(
                    mXpgWifiDevice.getDid()))
                mCenter.cDisconnect(theDevice.getSubDevice());
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.gizwits.centercontrolled.activity.BaseActivity#didReceiveData(com
     * .xtremeprog .xpgconnect.XPGWifiDevice,
     * java.util.concurrent.ConcurrentHashMap, int)
     */
    @Override
    protected void didReceiveData(XPGWifiDevice device,
                                  ConcurrentHashMap<String, Object> dataMap, int result) {

        subDevice = (XPGWifiSubDevice) device;
        deviceDataMap = dataMap;
        Log.i(TAG, "subDevice didReceiveData:" + dataMap.toString() + subDevice.getSubProductKey());
        handler.sendEmptyMessage(handler_key.RECEIVED.ordinal());

    }

    @Override
    protected void didSubDiscovered(int error,
                                    List<XPGWifiSubDevice> subDeviceList) {
        // TODO Auto-generated method stub
        super.didSubDiscovered(error, subDeviceList);
        Log.i(TAG, "didSubDiscovered size : " + subDeviceList.size());
        // Here different two types of subdevice
        List<XPGWifiSubDevice> subDeviceLamp = new ArrayList<XPGWifiSubDevice>();
        List<XPGWifiSubDevice> subDeviceController = new ArrayList<XPGWifiSubDevice>();
        for (int i = 0; i < subDeviceList.size(); i++) {
            if (subDeviceList.get(i).getSubProductKey().equals(Configs.PRODUCT_KEY_Sub)) {
                subDeviceLamp.add(subDeviceList.get(i));
                Log.i(TAG, "sub lamp add " + subDeviceList.get(i).getSubDid());
            }
            if (subDeviceList.get(i).getSubProductKey().equals(Configs.PRODUCT_KEY_Sub_Controller)) {
                subDeviceController.add(subDeviceList.get(i));
                Log.i(TAG, "sub controller add " + subDeviceList.get(i).getSubDid());
            }
        }

        putStatusToViewList(subDeviceLamp, subDeviceController);
        myGroupAdapterNotify();

        for (int i = 0; i < subDeviceList.size(); i++) {
            subDeviceList.get(i).setListener(deviceListener);
        }
        getLedStatus();
    }



    /*
         * (non-Javadoc)
         *
         * @see android.app.Activity#onBackPressed()
         */
    @Override
    public void onBackPressed() {
        Log.i(TAG, "=================================Back=====================");
        if (llBottom.isShown()) {
            bottomClose();
            return;
        }
        if (mView.isOpen()) {
            Log.i(TAG, "=================================Toggle=====================");
            mView.toggle();
        } else {
            Log.i(TAG, "=================================Exit=====================");
            if (mXpgWifiDevice != null && mXpgWifiDevice.isConnected()) {
                mCenter.cDisconnect(mXpgWifiDevice);
                DisconnectOtherDevice();
            }
            finish();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.gizwits.centercontrolled.activity.BaseActivity#didDisconnected(com
     * .xtremeprog .xpgconnect.XPGWifiDevice)
     */
    @Override
    protected void didDisconnected(XPGWifiDevice device) {
        Log.e(TAG, "disconnect");
        if (!device.getDid().equalsIgnoreCase(mXpgWifiDevice.getDid()))
            return;

        handler.sendEmptyMessage(handler_key.DISCONNECTED.ordinal());
    }

    /**
     * 把状态信息存入表
     *
     * @param map  the map
     * @param json the json
     * @throws JSONException the JSON exception
     */
    private void inputDataToMaps(Map<String, Object> map, String json) throws JSONException {
        Log.i(TAG, "inputDataToMaps(json):" + json);
        JSONObject receive = new JSONObject(json);
        Iterator actions = receive.keys();
        while (actions.hasNext()) {

            String action = actions.next().toString();
            Log.i(TAG, "action=" + action);
            // 忽略特殊部分
            if (action.equals("cmd") || action.equals("qos")
                    || action.equals("seq") || action.equals("version")) {
                continue;
            }
            JSONObject params = receive.getJSONObject(action);
            Log.i(TAG, "params=" + params);
            Iterator it_params = params.keys();
            while (it_params.hasNext()) {
                String param = it_params.next().toString();
                Object value = params.get(param);
                map.put(param, value);
                Log.i(TAG, "Key:" + param + ";value " + value);
            }
        }
        Log.i(TAG, "statusMap=" + map.toString());
        handler.sendEmptyMessage(handler_key.UPDATE_UI.ordinal());
    }

    //出现discovered后，重置存储中匹配的mXpgWifiDevice，重新设置Listener
    @Override
    protected void didDiscovered(int error, List<XPGWifiDevice> devicesList) {
        // TODO Auto-generated method stub
        super.didDiscovered(error, devicesList);
        for (int i = 0; i < devicesList.size(); i++) {
            if (devicesList.get(i).getDid().equals(mXpgWifiDevice.getDid())) {
                mXpgWifiDevice = devicesList.get(i);
                centralControlDevice = (XPGWifiCentralControlDevice) mXpgWifiDevice;
                centralControlDevice.setListener(xpgWifiCentralControlDeviceListener);

                return;
            }
        }
    }
}
