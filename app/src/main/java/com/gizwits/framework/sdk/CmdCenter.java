/**
 * Project Name:XPGSdkV4AppBase
 * File Name:CmdCenter.java
 * Package Name:com.gizwits.framework.sdk
 * Date:2015-1-27 14:47:19
 * Copyright (c) 2014~2015 Xtreme Programming Group, Inc.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.gizwits.framework.sdk;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.gizwits.framework.config.Configs;
import com.gizwits.framework.config.JsonKeys;
import com.gizwits.framework.control.Group;
import com.xtremeprog.xpgconnect.XPGWifiCentralControlDevice;
import com.xtremeprog.xpgconnect.XPGWifiDevice;
import com.xtremeprog.xpgconnect.XPGWifiGroup;
import com.xtremeprog.xpgconnect.XPGWifiSDK;
import com.xtremeprog.xpgconnect.XPGWifiSDK.XPGWifiConfigureMode;
import com.xtremeprog.xpgconnect.XPGWifiSubDevice;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// TODO: Auto-generated Javadoc

/**
 * ClassName: Class CmdCenter. <br/>
 * 控制指令类 <br/>
 * date: 2014-12-15 12:09:02 <br/>
 *
 * @author Lien
 */
public class CmdCenter {

    /**
     * The Constant TAG.
     */
    private static final String TAG = "CmdCenter";

    /**
     * The xpg wifi sdk.
     */
    private static XPGWifiSDK xpgWifiGCC;

    /**
     * The m center.
     */
    private static CmdCenter mCenter;

    /**
     * The m setting manager.
     */
    private SettingManager mSettingManager;

    private XPGWifiCentralControlDevice mCentralControlDevice;

    private static final String KEY_ACTION = "entity0";
    private static String mDid;

    /**
     * Instantiates a new cmd center.
     *
     * @param c the c
     */
    private CmdCenter(Context c) {
        if (mCenter == null) {
            init(c);
        }
    }

    /**
     * Gets the single instance of CmdCenter.
     *
     * @param c the c
     * @return single instance of CmdCenter
     */
    public static CmdCenter getInstance(Context c) {
        if (mCenter == null) {
            mCenter = new CmdCenter(c);
        }
        return mCenter;
    }

    /**
     * Inits the.
     *
     * @param c the c
     */
    private void init(Context c) {
        mSettingManager = new SettingManager(c);

        xpgWifiGCC = XPGWifiSDK.sharedInstance();

    }

    /**
     * Gets the XPG wifi sdk.
     *
     * @return the XPG wifi sdk
     */
    public XPGWifiSDK getXPGWifiSDK() {
        return xpgWifiGCC;
    }

    // =================================================================
    //
    // 关于账号的指令
    //
    // =================================================================

    /**
     * 注册账号.
     *
     * @param phone    注册手机号
     * @param code     验证码
     * @param password 注册密码
     */
    public void cRegisterPhoneUser(String phone, String code, String password) {
        xpgWifiGCC.registerUserByPhoneAndCode(phone, password, code);
    }

    /**
     * C register mail user.
     *
     * @param mailAddr the mail addr
     * @param password the password
     */
    public void cRegisterMailUser(String mailAddr, String password) {
        xpgWifiGCC.registerUserByEmail(mailAddr, password);
    }

    /**
     * 匿名登录
     * <p/>
     * 如果一开始不需要直接注册账号，则需要进行匿名登录.
     */
    public void cLoginAnonymousUser() {
        xpgWifiGCC.userLoginAnonymous();
    }

    /**
     * 账号注销.
     */
    public void cLogout() {
        Log.e(TAG, "cLogout:uesrid=" + mSettingManager.getUid());
        xpgWifiGCC.userLogout(mSettingManager.getUid());
    }

    /**
     * 账号登陆.
     *
     * @param name 用户名
     * @param psw  密码
     */
    public void cLogin(String name, String psw) {
        xpgWifiGCC.userLoginWithUserName(name, psw);
    }

    /**
     * 忘记密码.
     *
     * @param phone       手机号
     * @param code        验证码
     * @param newPassword the new password
     */
    public void cChangeUserPasswordWithCode(String phone, String code,
                                            String newPassword) {
        xpgWifiGCC.changeUserPasswordByCode(phone, code, newPassword);
    }

    /**
     * 修改密码.
     *
     * @param token  令牌
     * @param oldPsw 旧密码
     * @param newPsw 新密码
     */
    public void cChangeUserPassword(String token, String oldPsw, String newPsw) {
        xpgWifiGCC.changeUserPassword(token, oldPsw, newPsw);
    }

    /**
     * 根据邮箱修改密码.
     *
     * @param email 邮箱地址
     */
    public void cChangePassworfByEmail(String email) {
        xpgWifiGCC.changeUserPasswordByEmail(email);
    }

    /**
     * 请求向手机发送验证码.
     *
     * @param phone 手机号
     */
    public void cRequestSendVerifyCode(String phone) {
        xpgWifiGCC.requestSendVerifyCode(phone);
    }

    /**
     * 发送airlink广播，把需要连接的wifi的ssid和password发给模块。.
     *
     * @param wifi     wifi名字
     * @param password wifi密码
     */
    public void cSetAirLink(String wifi, String password) {
        xpgWifiGCC.setDeviceWifi(wifi, password,
                XPGWifiConfigureMode.XPGWifiConfigureModeAirLink, 60);
    }

    /**
     * softap，把需要连接的wifi的ssid和password发给模块。.
     *
     * @param wifi     wifi名字
     * @param password wifi密码
     */
    public void cSetSoftAp(String wifi, String password) {
        xpgWifiGCC.setDeviceWifi(wifi, password,
                XPGWifiConfigureMode.XPGWifiConfigureModeSoftAP, 30);
    }

    /**
     * 绑定后刷新设备列表，该方法会同时获取本地设备以及远程设备列表.
     *
     * @param uid   用户名
     * @param token 令牌
     */
    public void cGetBoundDevices(String uid, String token) {
        xpgWifiGCC.getBoundDevices(uid, token, Configs.PRODUCT_KEY, Configs.PRODUCT_KEY_Sub);
        // xpgWifiSdk.getBoundDevices(uid, token);
    }

    /**
     * 绑定设备.
     *
     * @param uid      用户名
     * @param token    密码
     * @param did      did
     * @param passcode passcode
     * @param remark   备注
     */
    public void cBindDevice(String uid, String token, String did,
                            String passcode, String remark) {

        xpgWifiGCC.bindDevice(uid, token, did, passcode, remark);
    }

    /**
     * 发送指令。格式为json。
     * <p/>
     * 例如 {"entity0":{"attr2":74},"cmd":1}
     * 其中entity0为gokit所代表的实体key，attr2代表led灯红色值，cmd为1时代表写入
     * 。以上命令代表改变gokit的led灯红色值为74.
     *
     * @param key   数据点对应的的json的key
     * @param value 需要改变的值
     * @throws JSONException the JSON exception
     */
    private void sendJson(String key, Object value) throws JSONException {

        String sendvalue = new String(Base64.encode(value.toString().getBytes(), 0));
        final JSONObject jsonsend = new JSONObject();
        JSONObject jsonparam = new JSONObject();
        jsonsend.put("cmd", 1);
        jsonparam.put(key, sendvalue);
        jsonsend.put(KEY_ACTION, jsonparam);
        Log.i("sendjson", jsonsend.toString());
        mCenter.cGetXPGWifiCentralControlDevice().write(jsonsend.toString());

    }
    // =================================================================
    //
    // 关于控制设备的指令
    //
    // =================================================================

    /**
     * 发送指令.
     *
     * @param xpgWifiDevice the xpg wifi device
     * @param key           the key
     * @param value         the value
     */
    public void cWrite(XPGWifiDevice xpgWifiDevice, String key, Object value) {

        try {
            final JSONObject jsonsend = new JSONObject();
            JSONObject jsonparam = new JSONObject();
            jsonsend.put("cmd", 1);
            jsonparam.put(key, value);
            jsonsend.put(JsonKeys.KEY_ACTION, jsonparam);
            Log.i("sendjson", jsonsend.toString());
            xpgWifiDevice.write(jsonsend.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 发送指令.
     *
     * @param xpgWifiDevice the xpg wifi device
     * @param key           the key
     * @param value         the value
     */
    public void cSubWrite(XPGWifiSubDevice xpgWifiSubDevice, String key, Object value) {

        try {
            final JSONObject jsonsend = new JSONObject();
            JSONObject jsonparam = new JSONObject();
            jsonsend.put("cmd", 1);
            jsonparam.put(key, value);
            jsonsend.put(JsonKeys.KEY_ACTION, jsonparam);
            Log.i("sendjson", jsonsend.toString());
            xpgWifiSubDevice.write(jsonsend.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取子设备状态.
     *
     * @param xpgWifiSubDevice the xpg wifi sub device
     */
    public void cGetSubStatus(XPGWifiSubDevice xpgWifiSubDevice) {
        JSONObject json = new JSONObject();
        try {
            json.put("cmd", 2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        xpgWifiSubDevice.write(json.toString());
    }

//	/**
//	 * 获取设备状态.
//	 * 
//	 * @param xpgWifiDevice
//	 *            the xpg wifi device
//	 */
//	public void cGetStatus(XPGWifiDevice xpgWifiDevice) {
//		JSONObject json = new JSONObject();
//		try {
//			json.put("cmd", 2);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		xpgWifiDevice.write(json.toString());
//	}

//	/**
//	 * 获取设备状态.
//	 * 
//	 * @param xpgWifiDevice
//	 *            the xpg wifi device
//	 */
//	public void cSubGetStatus(XPGWifiSubDevice xpgWifiDevice) {
//		JSONObject json = new JSONObject();
//		try {
//			json.put("cmd", 2);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		xpgWifiDevice.write(json.toString());
//	}

    /**
     * 断开连接.
     *
     * @param xpgWifiDevice the xpg wifi device
     */
    public void cDisconnect(XPGWifiDevice xpgWifiDevice) {
        xpgWifiDevice.disconnect();
    }

    /**
     * 解除绑定.
     *
     * @param uid      the uid
     * @param token    the token
     * @param did      the did
     * @param passCode the pass code
     */
    public void cUnbindDevice(String uid, String token, String did,
                              String passCode) {
        xpgWifiGCC.unbindDevice(uid, token, did, passCode);
    }

    /**
     * 更新备注.
     *
     * @param uid      the uid
     * @param token    the token
     * @param did      the did
     * @param passCode the pass code
     * @param remark   the remark
     */
    public void cUpdateRemark(String uid, String token, String did,
                              String passCode, String remark) {
        xpgWifiGCC.bindDevice(uid, token, did, passCode, remark);
    }

    // =================================================================
    //
    // 中控网关相关
    //
    // =================================================================

    /**
     * 获取子设备列表.
     *
     * @param centralControlDevice 中控设备类
     */
    public void cGetSubDevicesList(
            XPGWifiCentralControlDevice centralControlDevice) {
        centralControlDevice.getSubDevices();
    }

    /**
     * 添加子设备.
     *
     * @param centralControlDevice 中控设备类
     */
    public void cAddSubDevice(XPGWifiCentralControlDevice centralControlDevice) {
        centralControlDevice.addSubDevice();
    }

    /**
     * 删除子设备.
     *
     * @param centralControlDevice 中控设备类
     * @param subDevice            子设备类
     */
    public void cDeleteSubDevice(
            XPGWifiCentralControlDevice centralControlDevice,
            XPGWifiSubDevice subDevice) {
        centralControlDevice.deleteSubDevice(subDevice.getSubDid());
    }

    /**
     * 添加分组
     *
     * @param uid             用户id
     * @param token           授权令牌
     * @param productKey      指定productkey
     * @param groupName       分组名称
     * @param specilalDevices 指定specilalDevices
     */
    public void cAddGroup(String uid, String token,
                          String productKey, String groupName, List<ConcurrentHashMap<String, String>> specialDevices) {
        // Need to update group info in DB
        //xpgWifiGCC.addGroup(uid, token, productKey, groupName, specialDevices);
        byte cmd = 0x14;
        String groupMap = groupName + ":";
        int i = 0;
        for (Iterator<ConcurrentHashMap<String, String>> iterator = specialDevices.iterator(); iterator.hasNext(); ) {
            ConcurrentHashMap<String, String> dataMap;
            dataMap = iterator.next();
            int start = dataMap.toString().indexOf("sdid=");
            int end = dataMap.toString().indexOf(",");
            if (i > 0)
                groupMap += ",";
            groupMap += dataMap.toString().substring(start + 5, end);
            i++;
        }
        Log.i(TAG, "cAddGroup=" + groupMap);
        try {
            sendJson("group", cmd + "=" + groupMap);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void cSetDid(String temp) {
        mDid = temp;
    }

    /**
     * 刷新分组
     *
     * @param uid             用户id
     * @param token           授权令牌
     * @param productKey      指定productkey
     * @param groupName       分组名称
     * @param specilalDevices 指定specilalDevices
     */
    public void cFlushGroup(String uid, String token,
                            String productKey, byte[] specialDevices, List<XPGWifiGroup> grouplist) {

        String groupName;
        boolean bIgnore = false;
        int subcmd = specialDevices[1];
        int groupNum = specialDevices[2];
        if (groupNum == 0)
            return;
        int groupAddr = specialDevices[3];
        Log.i(TAG, "get group addr=" + groupAddr);
        int index = 0;
        int subdevNum = 0;
        /*For first group, it is special*/
        groupName = new String(specialDevices, 4, 8);
        groupName = groupName.trim();
        Log.i(TAG, "get group name=" + groupName);
        List<ConcurrentHashMap<String, String>> groupMaps = new ArrayList<ConcurrentHashMap<String, String>>();
        subdevNum = specialDevices[12];//cmd(1) + subcmd(1) + groupNum(1) + group addr(1) + group name(8) + sub device Num(1)  + sub device addr(N) + Sum(1)
        Log.i(TAG, "sub dev Num=" + subdevNum);
        for (int h = 0; h < subdevNum; h++) {
            ConcurrentHashMap<String, String> groupMap = new ConcurrentHashMap<String, String>();
            groupMap.put("did", mDid);
            groupMap.put("sdid", Byte.toString(specialDevices[13 + h]));
            Log.i(TAG, "did=" + mDid + " sdid=" + Byte.toString(specialDevices[13 + h]));
            groupMaps.add(groupMap);
        }
        index = 12 + subdevNum + 1;
        for (int i = 0; i < grouplist.size(); i++) {
            if ((grouplist.get(i).groupName.equals(groupName))) {
                Log.i(TAG, "Ignore to add group");
                bIgnore = true;
            }
        }
        if (!bIgnore)
            xpgWifiGCC.addGroup(uid, token, productKey, groupName, groupMaps);

        /*First group is not regular, but later will be OK*/
        Log.i(TAG, "group num is " + groupNum + "index=" + index);
        for (int j = 0; j < groupNum - 1; j++) {
            groupAddr = specialDevices[index];
            groupName = new String(specialDevices, index + 1, 8);
            groupName = groupName.trim();
            Log.i(TAG, "get group name=" + groupName);
            subdevNum = specialDevices[index + 1 + 8];//cmd(1) + subcmd(1) +groupNum(1) + group addr(1) + group name(8) + sub device Num(1)  + sub device addr(N) + Sum(1)
            Log.i(TAG, "subdevNum=" + subdevNum);
            List<ConcurrentHashMap<String, String>> subgroupMaps = new ArrayList<ConcurrentHashMap<String, String>>();
            for (int w = 0; w < subdevNum; w++) {
                ConcurrentHashMap<String, String> groupMap2 = new ConcurrentHashMap<String, String>();
                groupMap2.put("did", mDid);
                groupMap2.put("sdid", Byte.toString(specialDevices[index + 10 + w]));
                Log.i(TAG, "did=" + mDid + " sdid=" + Byte.toString(specialDevices[index + 10 + w]));
                subgroupMaps.add(groupMap2);
            }
            Log.i(TAG,"subgroupMaps=" + subgroupMaps.toString());
            index  += (10 + subdevNum);
            for (int i = 0; i < grouplist.size(); i++) {
                if ((grouplist.get(i).groupName.equals(groupName))) {
                    bIgnore = true;
                }
            }
            if (!bIgnore)
                xpgWifiGCC.addGroup(uid, token, productKey, groupName, subgroupMaps);
        }

        //xpgWifiGCC.getGroups(uid, token, productKey);
    }



    /**
     * 修改分组
     *
     * @param uid             用户id
     * @param token           授权令牌
     * @param gid             指定group id
     * @param groupName       分组名称
     * @param specilalDevices 指定specilalDevices
     */
    public void cEditGroup(String uid, String token,
                           String gid, String groupName, List<ConcurrentHashMap<String, String>> specilalDevices) {
        xpgWifiGCC.editGroup(uid, token, gid, groupName, specilalDevices);
    }

    /**
     * 获取分组列表
     *
     * @param uid               用户id
     * @param token             授权令牌
     * @param specialProductKey 指定productkey
     */
    public void cGetGroups(String uid, String token,
                           String... specialProductKey) {
       // xpgWifiGCC.getGroups(uid, token, specialProductKey);

        byte cmd = 0x10;
        Log.i(TAG, "get group information from gateway");
        try {
            sendJson("group", Byte.toString(cmd));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取场景列表
     *
     * @param uid               用户id
     * @param token             授权令牌
     * @param specialProductKey 指定productkey
     */
    public void cGetScenes( String groupName, String uid, String token,
                           String... specialProductKey) {
        // xpgWifiGCC.getGroups(uid, token, specialProductKey);

        byte cmd = 0x30;
        Log.i(TAG, "get scene information from gateway");
        try {
            sendJson("group", cmd + ":" + groupName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取所有场景列表
     *
     * @param uid               用户id
     * @param token             授权令牌
     * @param specialProductKey 指定productkey
     */
    public void cGetAllScenes(String uid, String token,
                            String... specialProductKey) {
        byte cmd = 0x3A;
        Log.i(TAG, "get all scene information from gateway");
        try {
            sendJson("group", cmd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取分组列表
     *
     * @param uid               用户id
     * @param token             授权令牌
     * @param specialProductKey 指定productkey
     */
    public void cGetGroupsFromDB(String uid, String token,
                           String... specialProductKey) {
        xpgWifiGCC.getGroups(uid, token, specialProductKey);
    }
    /**
     * 删除分组
     *
     * @param uid          用户id
     * @param token        授权令牌
     * @param xpgWifiGroup 分组类
     */
    public void cDeleteGroups(String uid, String token,
                              Group xpgWifiGroup, String groupSub) {
        //Need to remove group from DB.
        ///xpgWifiGCC.removeGroup(uid, token, xpgWifiGroup.gid);
        byte cmd = 0x16;
        String groupMap = xpgWifiGroup.groupName + ":" + groupSub;
        Log.i(TAG, "cDeleteGroups " + groupMap);
        try {
            sendJson("group", cmd + "=" + groupMap);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void cDeleteGroupsFromDB(String uid, String token,
                              XPGWifiGroup xpgWifiGroup) {
        xpgWifiGCC.removeGroup(uid, token, xpgWifiGroup.gid);

    }

    /**
     * 添加子设备到分组
     *
     * @param xpgWifiGroup     组
     * @param xpgWifiSubDevice 子设备
     */
    public void cAddToGroup(XPGWifiGroup xpgWifiGroup,
                            String did, String subDid) {
        xpgWifiGroup.addDevice(did, subDid);
    }

    /**
     * 从分组删除子设备
     *
     * @param xpgWifiGroup     组
     * @param xpgWifiSubDevice 子设备
     */
    public void cRemoveFromGroup(XPGWifiGroup xpgWifiGroup,
                                 String did, String subDid) {
        xpgWifiGroup.removeDevice(did, subDid);
    }

    /**
     * 从分组获取子设备
     *
     * @param xpgWifiGroup 组
     */
    public void cGetGroupDevices(XPGWifiGroup xpgWifiGroup) {
        xpgWifiGroup.getDevices();
    }

    // =================================================================
    //
    // 智能灯网关控制相关
    //
    // =================================================================

    /**
     * C switch on.
     *
     * @param xpgWifiDevice the xpg wifi device
     * @param isOn          the is on
     */
    public void cSwitchOn(XPGWifiSubDevice xpgWifiDevice, boolean isOn) {
        cSubWrite(xpgWifiDevice, JsonKeys.ON_OFF, isOn);
    }


    /**
     * C switch on.
     *
     * @param xpgWifiDevice the xpg wifi device
     * @param isOn          the is on
     */
    public void cSwitchOnGroup(String groupName, boolean isOn) {
        byte cmd = 0x18;
        String groupMap = groupName + ":" + isOn;
        Log.i(TAG, "cSwitchOnGroup" + groupMap);
        try {
            sendJson("group", cmd + "=" + groupMap);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * lightness.
     *
     * @param xpgWifiDevice the xpg wifi device
     * @param lightness     亮度级别
     */
    public void cLightness(XPGWifiSubDevice xpgWifiDevice, int lightness) {
        cSubWrite(xpgWifiDevice, JsonKeys.LIGHTNESS, lightness);
    }

    /**
     * group lightness.
     *
     * @param group control
     * @param lightness     亮度级别
     */
    public void cLightnessGroup(String selectGroup, int lightness) {
        byte cmd = 0x20;
        String groupMap = selectGroup + ":" + lightness;
        Log.i(TAG, "cLightnessGroup=" + groupMap);
        try {
            sendJson("group", cmd + "=" + groupMap);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * saturation.
     *
     * @param xpgWifiDevice the xpg wifi device
     * @param saturation     亮度级别
     */
    public void cSaturation(XPGWifiSubDevice xpgWifiDevice, int saturation) {
        cSubWrite(xpgWifiDevice, JsonKeys.SATURATION, saturation);
    }

    /**
     * group saturation.
     *
     * @param group control
     * @param saturation     亮度级别
     */
    public void cSaturationGroup(String selectGroup, int saturation) {
        byte cmd = 0x24;
        String groupMap = selectGroup + ":" + saturation;
        Log.i(TAG, "cSaturationGroup=" + groupMap);
        try {
            sendJson("group", cmd + "=" + groupMap);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * color.
     *
     * @param xpgWifiDevice the xpg wifi device
     * @param color     亮度级别
     */
    public void cColor(XPGWifiSubDevice xpgWifiDevice, int color) {
        cSubWrite(xpgWifiDevice, JsonKeys.COLORNESS, color);
    }

    /**
     * group color.
     *
     * @param group control
     * @param color     颜色级别
     */
    public void cColorGroup(String selectGroup, int color) {
        byte cmd = 0x22;
        String groupMap = selectGroup + ":" + color;
        Log.i(TAG, "cColorGroup=" + groupMap);
        try {
            sendJson("group", cmd + "=" + groupMap);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add scene control.
     *
     * @param group control
     * @param color     颜色级别
     */
    public void cAddScene(String selectGroup, String payload) {
        byte cmd = 0x34;
        String groupMap = selectGroup + ":" + payload;
        Log.i(TAG, "cAddScene=" + groupMap);
        try {
            sendJson("group", cmd + "=" + groupMap);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * Add scene control.
     *
     * @param group control
     * @param color     颜色级别
     */
    public void cApplyScene(String sceneName, String sceneId) {
        byte cmd = 0x38;
        String groupMap = sceneName + ":" + sceneId;
        Log.i(TAG, "cApplyScene=" + groupMap);
        try {
            sendJson("group", cmd + "=" + groupMap);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Apply ifttt rule.
     *
     * @param payload
     */
    public void cApplyIftttRule(String payload) {
        byte cmd = 0x48;
        Log.i(TAG, "cApplyIftttRule=" + payload);
        try {
            sendJson("group", cmd + "=" + payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * Add ifttt rule.
     *
     * @param payload
     */
    public void cAddIftttRule(String payload) {
        byte cmd = 0x42;
        Log.i(TAG, "cAddIftttRule=" + payload);
        try {
            sendJson("group", cmd + "=" + payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove ifttt rule.
     *
     * @param payload
     */
    public void cRemoveIftttRule(String payload) {
        byte cmd = 0x46;
        Log.i(TAG, "cRemoveIftttRule=" + payload);
        try {
            sendJson("group", cmd + "=" + payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove ifttt rule.
     *
     */
    public void cGetIftttRuleList() {
        byte cmd = 0x40;
        Log.i(TAG, "cGetIftttRuleList");
        try {
            sendJson("group", cmd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * Remove scene control.
     *
     * @param sceneName
     * @param sceneId
     */
    public void cRemoveScene(String sceneName, String sceneId) {
        byte cmd = 0x36;
        String groupMap = sceneName + ":" + sceneId;
        Log.i(TAG, "cRemoveScene=" + groupMap);
        try {
            sendJson("group", cmd + "=" + groupMap);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public XPGWifiCentralControlDevice cGetXPGWifiCentralControlDevice() {
        return mCentralControlDevice;
    }

    public void cSetXPGWifiCentralControlDevice(XPGWifiCentralControlDevice device) {
        mCentralControlDevice = device;
    }
}
