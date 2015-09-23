package com.gizwits.smartlight.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.gizwits.framework.config.Configs;
import com.gizwits.framework.control.Group;
import com.gizwits.framework.entity.GroupDevice;
import com.gizwits.framework.utils.DialogManager;
import com.gizwits.smartlight.R;
import com.gizwits.smartlight.activity.AddSubDeviceActivity;
import com.gizwits.smartlight.activity.MainListActivity;
import com.xpg.common.device.DensityUtils;
import com.xtremeprog.xpgconnect.XPGWifiGroup;
import com.xtremeprog.xpgconnect.XPGWifiSubDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressLint("ResourceAsColor")
public class GroupAdapter extends BaseAdapter {
    private final String TAG = "GroupAdapter";
    private MainListActivity mainListActivity;      //Activity
    private Map<String, List<GroupDevice>> mapList; //分组中子设备信息集合
    private List<String> list;                      //list分组
    private LayoutInflater listContainer;           //视图容器   
    LinearLayout llview;                            //item项中
    private Dialog delDialog;                       //删除弹窗

    public final class ListItemView {                //自定义控件集合
        public LinearLayout ll_item;//item中设置子灯LinearLayout
        public TextView groupNameTv;//item名称
        public LinearLayout ll_item_bottom;//item底部展开条
        public View v_line;//item名称下直线
        public ImageView ivDel;//item删除按钮
        public LinearLayout item_bg;//item背景设置LinearLayout
    }

    public static int height;

    public GroupAdapter(MainListActivity context, Map<String, List<GroupDevice>> mapList, List<String> list) {
        this.list = list;
        this.mainListActivity = context;
        listContainer = LayoutInflater.from(context);   //创建视图容器并设置上下文   
        this.mapList = mapList;
    }

    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        if (list.isEmpty() || list.size()<arg0 + 1) {
            return "";
        }else {
            return mapList.get(list.get(arg0));
        }
    }

    public long getItemId(int arg0) {
        // TODO Auto-generated method stub   
        return 0;
    }

    @Override
    public boolean isEnabled(int position) {
        // TODO Auto-generated method stub
        if (position == 0 || position == 1 || position == 2) {//前三项item不可点击
            return false;
        }
        return true;
    }

    /**
     * ListView Item设置
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Log.i(TAG, "getView position=" + position);

        //自定义视图
        ListItemView listItemView = null;
        try {
            if (list.get(position).equals("light") || list.get(position).equals("group") || list.get(position).equals("switch")) {//如为light||group展示分组名（我的LED与我的分组）item
                convertView = listContainer.inflate(R.layout.item_device_tag, null);
                TextView tv = (TextView) convertView.findViewById(R.id.tv_name);
                if (list.get(position).equals("light")) {
                    //tv.setText("我的LED");
                    tv.setText("Lamp List");
                } else if (list.get(position).equals("group")) {
                    //tv.setText("我的分组");
                    tv.setText("Group List");
                } else if (list.get(position).equals("switch")) {
                    //tv.setText("我的分组");
                    tv.setText("Controller List");
                }
                return convertView;
            } else if (list.get(position).equals("addGroup")) {//如果addGroup展示添加分组item
                convertView = listContainer.inflate(R.layout.item_device_add, null);
                return convertView;
            } else {
                listItemView = new ListItemView();
                convertView = listContainer.inflate(R.layout.item_device_list, null);
            }
            //**********************************item参数**********************************
            listItemView.item_bg = (LinearLayout) convertView.findViewById(R.id.item_bg);
            listItemView.ll_item = (LinearLayout) convertView.findViewById(R.id.ll_item);
            listItemView.ll_item.removeAllViewsInLayout();
            listItemView.groupNameTv = (TextView) convertView.findViewById(R.id.tv_name);
            Log.i(TAG, "group name " + list.get(position));
            listItemView.groupNameTv.setText(list.get(position));
            listItemView.ll_item_bottom = (LinearLayout) convertView.findViewById(R.id.ll_item_bottom);
            listItemView.v_line = convertView.findViewById(R.id.v_line);
            listItemView.ivDel = (ImageView) convertView.findViewById(R.id.ivDel);
            //****************************************************************************
            List<LinearLayout> llviews = new ArrayList<LinearLayout>();//保存多于4个灯的llview，用于显示隐藏

            //设置item上方Text&line是否显示(若为我的LED隐藏，其余显示)
            Log.i(TAG, "list.get(position) is " + list.get(position));

            if (list.get(position).equals("我的LED") || list.get(position).equals("MyController")) {
                listItemView.item_bg.setBackgroundResource(R.drawable.kuang);
                listItemView.v_line.setVisibility(View.GONE);
                listItemView.groupNameTv.setVisibility(View.GONE);
                if (mapList.get(list.get(position)).size() == 0) {
                    //如我的Led中灯为空，设置Add按钮
                    llview = new LinearLayout(mainListActivity);
                    llview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                    llview.setPadding(0, 0, 0, DensityUtils.dp2px(mainListActivity, 10f));
                    listItemView.ll_item.addView(llview);
                    llview.addView(getAddButton(true));
                    llview.addView(getAddButton(false));
                    llview.addView(getAddButton(false));
                    llview.addView(getAddButton(false));
                }
            } else {
                //其他Item设置删除按钮，是否可用
                convertView.setTag(list.get(position));
                convertView.setOnClickListener(groupCtrl);
                listItemView.ivDel.setTag(list.get(position));
                listItemView.ivDel.setOnClickListener(deleteGroupClick);
                if (mainListActivity.ivEdit.getTag().toString().equals("0")) {
                    listItemView.ivDel.setVisibility(View.VISIBLE);
                }
                mainListActivity.ivDels.add(listItemView.ivDel);
            }
            Log.i(TAG, "maplist for this position size=" + mapList.get(list.get(position)).size());
            //根据设备自动生成LedView，循环设置入Item
            for (int i = 0; i < mapList.get(list.get(position)).size(); i++) {
                if (i % 4 == 0) {
                    //每4盏灯新建一个llview，防止灯view
                    llview = new LinearLayout(mainListActivity);
                    llview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                    llview.setPadding(0, 0, 0, DensityUtils.dp2px(mainListActivity, 10f));
                    if (i >= 4) {
                        //将需要隐藏显示的llview放入view数组
                        llviews.add(llview);
                        if (!mainListActivity.showItemDevices.contains(list.get(position))) {
                            //若已展开显示全部view,若未展开则只显示一排view
                            llview.setVisibility(View.GONE);
                        }
                    }
                    listItemView.ll_item.addView(llview);//将设置灯的LinearLayout放入Item
                }
                FrameLayout flayout = new FrameLayout(mainListActivity);//每一个LedFlayout
                flayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));

                if (list.get(position).equals("我的LED") || list.get(position).equals("MyController")) {
                    //设置Led灯状态
                    if (list.get(position).equals("我的LED")) {
                        if (mapList.get(list.get(position)).get(i).isOnOff()) {
                            if (mainListActivity.selecttv != null) {
                                //若该Led灯被选中则设置绿边图片，与底部案件状态一同修改
                                if (mainListActivity.selecttv.getText().equals("Bulb" + mapList.get(list.get(position)).get(i).getSubDevice().getSubDid())) {
                                    Log.i(TAG, "mainListActivity.selecttv = " + mainListActivity.selecttv.getText());
                                    mainListActivity.switchOn();
                                    mainListActivity.sbLightness.setProgress(mapList.get(list.get(position)).get(i).getLightness());
                                    mainListActivity.sbColor.setProgress(mapList.get(list.get(position)).get(i).getColor());
                                    mainListActivity.sbSaturation.setProgress(mapList.get(list.get(position)).get(i).getSaturation());
                                    mainListActivity.selecttv = getLedView("Bulb" + mapList.get(list.get(position)).get(i).getSubDevice().getSubDid(), mainListActivity.yLightSelect,
                                            true, mapList.get(list.get(position)).get(i));
                                    flayout.addView(mainListActivity.selecttv);
                                } else {
                                    Log.i(TAG, "mapList subdid=" + mapList.get(list.get(position)).get(i).getSubDevice().getSubDid());
                                    flayout.addView(getLedView("Bulb" + mapList.get(list.get(position)).get(i).getSubDevice().getSubDid(), mainListActivity.yLight,
                                            true, mapList.get(list.get(position)).get(i)));
                                }
                            } else {
                                flayout.addView(getLedView("Bulb" + mapList.get(list.get(position)).get(i).getSubDevice().getSubDid(), mainListActivity.yLight,
                                        true, mapList.get(list.get(position)).get(i)));
                            }

                        } else {
                            if (mainListActivity.selecttv != null) {
                                if (mainListActivity.selecttv.getText().equals("Bulb" + mapList.get(list.get(position)).get(i).getSubDevice().getSubDid())) {
                                    mainListActivity.switchOff();
                                    mainListActivity.sbLightness.setProgress(mapList.get(list.get(position)).get(i).getLightness());
                                    mainListActivity.sbColor.setProgress(mapList.get(list.get(position)).get(i).getColor());
                                    mainListActivity.sbSaturation.setProgress(mapList.get(list.get(position)).get(i).getSaturation());
                                    mainListActivity.selecttv = getLedView("Bulb" + mapList.get(list.get(position)).get(i).getSubDevice().getSubDid(), mainListActivity.wLightSelect,
                                            true, mapList.get(list.get(position)).get(i));
                                    flayout.addView(mainListActivity.selecttv);
                                } else {
                                    flayout.addView(getLedView("Bulb" + mapList.get(list.get(position)).get(i).getSubDevice().getSubDid(), mainListActivity.wLight,
                                            true, mapList.get(list.get(position)).get(i)));
                                }
                            } else {
                                flayout.addView(getLedView("Bulb" + mapList.get(list.get(position)).get(i).getSubDevice().getSubDid(), mainListActivity.wLight,
                                        true, mapList.get(list.get(position)).get(i)));
                            }
                        }
                    }
                    if (list.get(position).equals("MyController")) {

                        Log.i(TAG, "add controller here ==" + mapList.get(list.get(position)).get(i).isOnOff());
                        if (mapList.get(list.get(position)).get(i).isOnOff()) {
                            flayout.addView(getLedView("Controller" + mapList.get(list.get(position)).get(i).getSubDevice().getSubDid(), mainListActivity.wControllerOn,
                                    false, mapList.get(list.get(position)).get(i)));
                        } else {
                            flayout.addView(getLedView("Controller" + mapList.get(list.get(position)).get(i).getSubDevice().getSubDid(), mainListActivity.wController,
                                    false, mapList.get(list.get(position)).get(i)));
                        }
                    }
                } else {
                    //设置分组中的Led灯状态，只选取本地Led灯组中含有的本组第一盏灯状态，设置整组灯状态
                    boolean isopenLight = false;
                    boolean isIntoCheck = false;
                    for (int j = 0; j < mapList.get(list.get(position)).size(); j++) {
                        for (int j2 = 0; j2 < mainListActivity.ledList.size(); j2++) {
                            if (mainListActivity.ledList.get(j2).getSubDevice().getSubDid().equals("" + mapList.get(list.get(position)).get(j).getSdid())) {
                                if (mainListActivity.ledList.get(j2).isOnOff()) {
                                    isopenLight = true;
                                } else {
                                    isopenLight = false;
                                }
                                isIntoCheck = true;
                                break;
                            }
                        }
                        if (isIntoCheck) {
                            break;
                        }
                    }
                    Log.i(TAG, "isopenLight=" + isopenLight);
                    if (isopenLight) {
                        flayout.addView(getLedView("Bulb" + mapList.get(list.get(position)).get(i).getSdid(), mainListActivity.yLight,
                                false, mapList.get(list.get(position)).get(i)));
                    } else {
                        flayout.addView(getLedView("Bulb" + mapList.get(list.get(position)).get(i).getSdid(), mainListActivity.wLight,
                                false, mapList.get(list.get(position)).get(i)));
                    }
                }
               /* if (list.get(position).equals("我的LED") || list.get(position).equals("MyController")) {
                    //如果分组为我的Led灯，则放入删除按钮
                    ImageView ivX = new ImageView(mainListActivity);
                    if (mainListActivity.ivEdit.getTag().toString().equals("0")) {
                        ivX.setVisibility(View.VISIBLE);
                    } else {
                        ivX.setVisibility(View.INVISIBLE);
                    }
                    ivX.setTag(mapList.get(list.get(position)).get(i).getSubDevice());
                    ivX.setImageResource(R.drawable.icon_15);
                    android.widget.FrameLayout.LayoutParams param = new android.widget.FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    param.gravity = Gravity.RIGHT;
                    ivX.setLayoutParams(param);
                    ivX.setOnClickListener(deleteLedClick);
                    mainListActivity.ivDels.add(ivX);
                    flayout.addView(ivX);
                }*/
                llview.addView(flayout);

                //*******************************在我的led分组中，添加add按键*****************************
                if (list.get(position).equals("我的LED") || list.get(position).equals("MyController")) {
                    if (mapList.get(list.get(position)).size() % 4 != 0 && (i + 1) == mapList.get(list.get(position)).size()) {
                        llview.addView(getAddButton(true));
                    } else if (mapList.get(list.get(position)).size() % 4 == 0 && (i + 1) == mapList.get(list.get(position)).size()) {
                        //如果灯泡刚好求余4为0，则新建一行llview，放入add按钮，并且设置隐藏view
                        llview = new LinearLayout(mainListActivity);
                        llview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                        llview.setPadding(0, 10, 0, 10);
                        if (!mainListActivity.showItemDevices.contains("我的LED")) {
                            llview.setVisibility(View.GONE);
                        }
                        listItemView.ll_item.addView(llview);
                        llview.addView(getAddButton(true));
                        llview.addView(getAddButton(false));
                        llview.addView(getAddButton(false));
                        llview.addView(getAddButton(false));
                        llviews.add(llview);
                    }
                }
                //**********************************设置结算后最后一个llview中少于4盏灯的隐藏view******************************
                if (mapList.get(list.get(position)).size() % 4 > 0 && i == mapList.get(list.get(position)).size() - 1) {
                    if (list.get(position).equals("我的LED") || list.get(position).equals("MyController")) {
                        //由于Led灯中多一个Add按键，则相应减少一个隐藏Button
                        for (int j = 0; j <= (4 - (mapList.get(list.get(position)).size() % 4) - 2); j++) {
                            llview.addView(getAddButton(false));
                        }
                        ;
                    } else {
                        for (int j = 0; j <= (4 - (mapList.get(list.get(position)).size() % 4) - 1); j++) {
                            llview.addView(getAddButton(false));
                        }
                        ;
                    }
                }
                //***********************************************************************************
            }
            listItemView.ll_item_bottom.setTag(llviews);//将需要展开的llview，保存于Tag中，方便获取
            ImageView iv_bottom = (ImageView) listItemView.ll_item_bottom.findViewById(R.id.item_bottom);
            if (mainListActivity.showItemDevices.contains(list.get(position))) {
                iv_bottom.setImageResource(R.drawable.icon_close);
            }
            listItemView.ll_item_bottom.setOnClickListener(showItemClick);
            return convertView;
        } catch(Exception e){
            Log.i(TAG, "getView error===================");
            //mainListActivity.onBackPressed();
            return convertView;
        }
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
       // Log.i(TAG, "Group adapter get count================" + mapList.size());
        return mapList.size();
    }

    /**
     * 展开item项
     */
    OnClickListener showItemClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            List<LinearLayout> llviews = (List<LinearLayout>) v.getTag();
            ImageView iv_bottom = (ImageView) v.findViewById(R.id.item_bottom);
            if (llviews.size() == 0) {
                return;
            }
            FrameLayout ll = (FrameLayout) v.getParent();
            if (llviews.get(0).getVisibility() == View.GONE) {
                //保存已展开项
                mainListActivity.showItemDevices.add(((TextView) ll.findViewById(R.id.tv_name)).getText().toString());
                for (LinearLayout linearLayout : llviews) {
                    linearLayout.setVisibility(View.VISIBLE);
                }
                iv_bottom.setImageResource(R.drawable.icon_close);
            } else {
                mainListActivity.showItemDevices.remove(((TextView) ll.findViewById(R.id.tv_name)).getText().toString());
                for (LinearLayout linearLayout : llviews) {
                    linearLayout.setVisibility(View.GONE);
                }
                iv_bottom.setImageResource(R.drawable.icon_open);
            }
        }
    };

    /**
     * 删除分组点击事件
     */
    OnClickListener deleteGroupClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            final String groupName = v.getTag().toString();
            delDialog = DialogManager.getDeleteDialog(mainListActivity, new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    for (int i = 0; i < mainListActivity.grouplist.size(); i++) {
                        Group group = mainListActivity.grouplist.get(i);
                        if (group.groupName.equals(groupName)) {
                            String groupMap = "";
                            int size = mainListActivity.groupMapList.get(groupName).size();
                            for (int j = 0; j < size; j++)
                                groupMap += mainListActivity.groupMapList.get(groupName).get(j) + ",";

                            Log.i(TAG, "deleteGroupClick confirmed=" + group + " sdid=" + groupMap.substring(0, groupMap.length() - 1));
                            mainListActivity.grouplist.remove(i);
                            mainListActivity.groupMapList.remove(groupName);

                            mainListActivity.mCenter.cDeleteGroups(mainListActivity.setmanager.getUid(),
                                   mainListActivity.setmanager.getToken(), group, groupMap.substring(0, groupMap.length() - 1));
                        }
                    }
                    DialogManager.dismissDialog(mainListActivity, delDialog);
                }
            }, groupName);
            DialogManager.showDialog(mainListActivity, delDialog);
        }
    };

    /**
     * 删除led灯点击事件
     */
    OnClickListener deleteLedClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            final XPGWifiSubDevice led = (XPGWifiSubDevice) v.getTag();
            delDialog = DialogManager.getDeleteDialog(mainListActivity, new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    mainListActivity.mCenter.cDeleteSubDevice(mainListActivity.centralControlDevice, led);
                    mainListActivity.mCenter.cGetSubDevicesList(mainListActivity.centralControlDevice);
                    DialogManager.dismissDialog(mainListActivity, delDialog);
                }
            }, "Bulb" + led.getSubDid());
            DialogManager.showDialog(mainListActivity, delDialog);
        }
    };

    /**
     * 单灯控制弹出点击事件
     */
    OnClickListener ledCtrl = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mainListActivity.ivEdit.getTag().toString().equals("0")) {//若开启编辑模式，不可点击led项
                return;
            }

            GroupDevice gSelectDevice = (GroupDevice) v.getTag();
            if (gSelectDevice.isOnOff()) {
                ((TextView) v).setCompoundDrawables(null, mainListActivity.yLightSelect, null, null);
            } else {
                ((TextView) v).setCompoundDrawables(null, mainListActivity.wLightSelect, null, null);
            }
            mainListActivity.selecttv = ((TextView) v);
            mainListActivity.selectSubDevice = gSelectDevice.getSubDevice();
            if (gSelectDevice.isOnOff()) {
                Log.i(TAG, "Single lamp switch on");
                mainListActivity.switchOn();
            } else {
                Log.i(TAG, "Single lamp switch off");
                mainListActivity.switchOff();
            }

            Log.i(TAG, "light:saturation:color=" + gSelectDevice.getLightness() + ":" + gSelectDevice.getSaturation() + ":" + gSelectDevice.getColor());
            //mainListActivity.etGroup.setVisibility(View.INVISIBLE);
            mainListActivity.addSceneButton.setVisibility(View.INVISIBLE);
            mainListActivity.sceneLayout.setVisibility(View.INVISIBLE);

            mainListActivity.sbLightness.setProgress(gSelectDevice.getLightness());
            mainListActivity.sbSaturation.setProgress(gSelectDevice.getSaturation());
            mainListActivity.sbColor.setProgress(gSelectDevice.getColor());
            mainListActivity.tvLName.setText(mainListActivity.selecttv.getText().toString());
            mainListActivity.bottomShow();


        }
    };

    /**
     * 分组控制底部菜单弹出点击事件
     */
    OnClickListener groupCtrl = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mainListActivity.ivEdit.getTag().toString().equals("0")) {//若开启编辑模式，不可点击item项
                return;
            }
            String groupName = v.getTag().toString();
            Log.i(TAG, "group click listener:" + groupName);
            //First add get scene list cmd
            mainListActivity.mCenter.cGetScenes(groupName, mainListActivity.setmanager.getUid(),
                    mainListActivity.setmanager.getToken(), Configs.PRODUCT_KEY_Sub);
            boolean isShowOk = false;
            //分组控制中底部菜单弹出,并且获取现有的ledlist中包含group的第一设备状态
            for (int j = 0; j < mainListActivity.groupMapList.get(groupName).size(); j++) {
                for (int i = 0; i < mainListActivity.ledList.size(); i++) {
                    if (mainListActivity.groupMapList.get(groupName).get(j).equals(mainListActivity.ledList.get(i).getSubDevice().getSubDid())) {
                        GroupDevice gSelectDevice = (GroupDevice) mainListActivity.ledList.get(i);
                        mainListActivity.selectSubDevice = gSelectDevice.getSubDevice();
                        mainListActivity.mCenter.cGetSubStatus(mainListActivity.selectSubDevice);
                        mainListActivity.selectGroup = groupName;
                        if (gSelectDevice.isOnOff()) {
                            Log.i(TAG, "group first device is on");
                            mainListActivity.switchOn();
                        } else {
                            Log.i(TAG, "group first device is off");
                            mainListActivity.switchOff();
                        }
                        mainListActivity.tvLName.setText(groupName);
                        //  mainListActivity.etGroup.setVisibility(View.VISIBLE);
                        mainListActivity.addSceneButton.setVisibility(View.VISIBLE);
                        mainListActivity.sceneLayout.setVisibility(View.VISIBLE);
                        //   mainListActivity.etGroup.setTag(groupName);
                        mainListActivity.sbLightness.setProgress(gSelectDevice.getLightness());
                        mainListActivity.sbSaturation.setProgress(gSelectDevice.getSaturation());
                        mainListActivity.sbColor.setProgress(gSelectDevice.getColor());
                        isShowOk = true;
                        break;
                    }
                }
                if (isShowOk) {
                    break;
                }
            }
            if (isShowOk) {
                mainListActivity.bottomShow();

            } else {
                Toast.makeText(mainListActivity, "该组中无可用LED灯", Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * 添加按钮点击事件
     */
    OnClickListener addClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mainListActivity.ivEdit.getTag().toString().equals("0")) {
                return;
            }

            Intent intent = new Intent(mainListActivity, AddSubDeviceActivity.class);
            mainListActivity.startActivity(intent);
        }
    };

    /**
     * 生成Add按钮&充当占位按钮
     *
     * @param visible view是否可见
     * @return
     */
    public TextView getAddButton(boolean visible) {
        TextView add = new TextView(mainListActivity);
        add.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
        add.setPadding(0, DensityUtils.dp2px(mainListActivity, 10f), 0, 0);
        add.setText("Add");
        add.setGravity(Gravity.CENTER);
        add.setTextColor(Color.parseColor("#FFFFFF"));
        add.setCompoundDrawables(null, mainListActivity.add, null, null);
        add.setOnClickListener(addClick);
        if (!visible) {
            add.setVisibility(View.INVISIBLE);
        }
        return add;
    }

    /**
     * 生成Led按钮
     *
     * @param name      灯名
     * @param drawable  设置view上方图片
     * @param clickAble 是否可单击
     * @param gDevice   view tag GroupDevice
     * @return
     */
    public TextView getLedView(String name, Drawable drawable, Boolean clickAble, GroupDevice gDevice) {
        TextView textview = new TextView(mainListActivity);
        android.widget.FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(0, DensityUtils.dp2px(mainListActivity, 7f), 0, 0);
        params.gravity = Gravity.CENTER;
        textview.setLayoutParams(params);
        textview.setText(name);
        textview.setGravity(Gravity.CENTER);
        textview.setTextColor(Color.parseColor("#FFFFFF"));
        textview.setOnClickListener(ledCtrl);
        textview.setTag(gDevice);
        textview.setCompoundDrawables(null, drawable, null, null);
        textview.setClickable(clickAble);
        return textview;
    }
}  