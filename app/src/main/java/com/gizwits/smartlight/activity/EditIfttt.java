package com.gizwits.smartlight.activity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gizwits.framework.activity.BaseActivity;
import com.gizwits.framework.config.Configs;
import com.gizwits.framework.control.ItemListBaseAdapter;
import com.gizwits.framework.control.RuleListBaseAdapter;
import com.gizwits.framework.control.iftttrule;
import com.gizwits.framework.control.scene;
import com.gizwits.smartlight.R;
import com.xpg.common.device.DensityUtils;
import com.xtremeprog.xpgconnect.XPGWifiCentralControlDevice;
import com.xtremeprog.xpgconnect.XPGWifiCentralControlDeviceListener;
import com.xtremeprog.xpgconnect.XPGWifiDevice;
import com.xtremeprog.xpgconnect.XPGWifiGroup;
import com.xtremeprog.xpgconnect.XPGWifiSubDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by water.zhou on 8/6/2015.
 */
public class EditIfttt extends BaseActivity implements View.OnClickListener {
    private final String TAG = "EditIfttt";
    /** The iv back. */
    private ImageView ivBack;
    /** The ll scroll layout. */
    private LinearLayout ll_scroll;
    private LinearLayout ll_scroll_controller;
    /** The ll already select led */
    private LinearLayout ll_select_led;
    private LinearLayout ll_select_controller;
    /** The et Edit group name */
    private EditText et_ifttt_name;
    /** The iv Right confirm btn */
    private ImageView ivConfirm;

    private Spinner condition_spinner;
    private ArrayAdapter<String> condition_adapter;
    /** The exit led list */
    private List<String> ledList = new ArrayList<String>();
    /** The controller list */
    private List<String> ControllerList = new ArrayList<String>();
    /** The already select led list */
    private List<String> selectLedList = new ArrayList<String>();
    private List<String> selectControllerList = new ArrayList<String>();
    /** The group exit LedList */
    private RuleListBaseAdapter mRuleAdapter;

    public  ListView ruleListView;
    private TextView ivIftttRule;
    public ArrayList<iftttrule> rule_details = new ArrayList<iftttrule>();
    /** The exit group name */
    private String ruleCondition;
    /** The subLight base centerCtrl did */
    private String did;
    /** The select wifi Group */
    private XPGWifiGroup mXpgWifiGroup;
    /** The boolean group is Exit */
    /**
     * The XPGWifiCentralControlDevice centralControlDevice
     */
    public XPGWifiCentralControlDevice centralControlDevice;
    public byte[] mIftttCmd;


    private static final String[] conditionArray={"Onoff = Off","Onoff = On","Level >= Value0","Level < Value0","others"};
    private int conditionIndex;

    public enum Conditon{
        CONDITION_GT,     /* ">"*/
        CONDITION_GE,     // ">="
        CONDITION_LT,     // "<"
        CONDITION_LE,     // "<="
        CONDITION_EQ,     // "="
        CONDITION_NQ      // "!="
    };

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ifttt);
        ledList= getIntent().getStringArrayListExtra("sceneList");
        ControllerList= getIntent().getStringArrayListExtra("ControllerList");
        did= getIntent().getStringExtra("did");
        Log.i(TAG, "ledList=" + ledList + "    ControllerList=" + ControllerList);
        initViews();
        initEvent();
        setItemTollScroll();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.gizwits.centercontrolled.activity.BaseActivity#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        //中控监听
        Log.d(TAG, "centralControlsetListener");
        centralControlDevice = (XPGWifiCentralControlDevice) mXpgWifiDevice;
        centralControlDevice.setListener(xpgWifiCentralControlDeviceListener);
        mCenter.cSetXPGWifiCentralControlDevice(centralControlDevice);
        mCenter.cSetDid(centralControlDevice.getDid());

        // Get rules from gateway firstly
        mCenter.cGetIftttRuleList();
    }

    protected XPGWifiCentralControlDeviceListener xpgWifiCentralControlDeviceListener = new XPGWifiCentralControlDeviceListener() {


        public void didReceiveData(XPGWifiDevice device,
                                   ConcurrentHashMap<String, Object> dataMap, int result) {
            Log.i(TAG, "Ifttt didReceiveData:" + dataMap.toString());
            if (dataMap.get("binary") != null) {
                mIftttCmd = (byte[]) dataMap.get("binary");
                Log.i(TAG, "Binary data:" + bytesToHex(mIftttCmd));
                // Get ifttt rule list
                if (mIftttCmd[0] == 0x03 && mIftttCmd[1] == 0x41) {
                    rule_details.clear();
                    byte ruleNum = mIftttCmd[2];
                    if (ruleNum == 0) {
                        return;
                    }
                    iftttrule tempRule = new iftttrule();
                    byte ruleId;
                    byte enable;
                    byte[] srcAddr= new byte[2];
                    byte[] srcClusterId = new byte[2];
                    byte srcClusterIndex;
                    byte[] srcAttributeId = new byte[2];
                    byte condition;
                    byte[] value = new byte[4];
                    byte sceneAddr;
                    ruleId = mIftttCmd[3];
                    tempRule.setId(ruleId);
                    enable = mIftttCmd[4];
                }
            }
        }
    };

    private void initViews() {
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ll_scroll = (LinearLayout) findViewById(R.id.ll_scroll);
        ll_scroll_controller = (LinearLayout) findViewById(R.id.ll_scroll_controller);
        ll_select_led = (LinearLayout) findViewById(R.id.ll_select_led);
        ll_select_controller = (LinearLayout) findViewById(R.id.ll_select_controller);
        et_ifttt_name = (EditText) findViewById(R.id.et_ifttt_name);
        ivConfirm = (ImageView) findViewById(R.id.ivConfirm);
        ivIftttRule = (TextView) findViewById(R.id.ivRule);

        condition_spinner = (Spinner) findViewById(R.id.Spinner_condition);
        //将可选内容与ArrayAdapter连接起来
        condition_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,conditionArray);
        //设置下拉列表的风格
        condition_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将adapter 添加到spinner中
        condition_spinner.setAdapter(condition_adapter);
        //设置默认值
        condition_spinner.setVisibility(View.VISIBLE);

        mRuleAdapter = new RuleListBaseAdapter(this, R.layout.list_ifttt_rule, rule_details);
        ruleListView = (ListView)findViewById(R.id.listView_Rule);
        ruleListView.setAdapter(mRuleAdapter);
    }

    private void initEvent() {
        ivBack.setOnClickListener(this);
        ivConfirm.setOnClickListener(this);
        //添加事件Spinner事件监听
        condition_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                       long arg3) {
                Log.i(TAG, "Your choice is " + conditionArray[arg2]);
                ruleCondition = conditionArray[arg2];
                conditionIndex = arg2;
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    // 设置滑动layout中灯泡
    private void setItemTollScroll() {
        if (ledList.size() == 0) {
            return;
        }
        ll_scroll.removeAllViewsInLayout();
        ll_scroll_controller.removeAllViewsInLayout();
        for (int i = 0; i < ledList.size(); i++) {
            TextView textview=new TextView(EditIfttt.this);
            textview.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            textview.setText("scene" + ledList.get(i));
            textview.setTag(ledList.get(i));
            textview.setGravity(Gravity.CENTER);
            textview.setTextColor(Color.parseColor("#FFFFFF"));
            if (selectLedList.contains(ledList.get(i))) {
                Drawable drawable=EditIfttt.this.getResources().getDrawable(R.drawable.lampy_framey_b);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                textview.setCompoundDrawables(null,drawable,null,null);
            }else{
                Drawable drawable=EditIfttt.this.getResources().getDrawable(R.drawable.lampw_framew_b);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                textview.setCompoundDrawables(null,drawable,null,null);
            }
            textview.setOnClickListener(ledClick);
            if (i > 0) {
                textview.setPadding(18, 0, 0, 0);
            }
            ll_scroll.addView(textview);
        }

        for (int i = 0; i < ControllerList.size(); i++) {
            TextView textview=new TextView(EditIfttt.this);
            textview.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            textview.setText("Switch" + ControllerList.get(i));
            textview.setTag(ControllerList.get(i));
            textview.setGravity(Gravity.CENTER);
            textview.setTextColor(Color.parseColor("#FFFFFF"));
            if (selectControllerList.contains(ControllerList.get(i))) {
                Drawable drawable=EditIfttt.this.getResources().getDrawable(R.drawable.socketon);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                textview.setCompoundDrawables(null,drawable,null,null);
            }else{
                Drawable drawable=EditIfttt.this.getResources().getDrawable(R.drawable.socket);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                textview.setCompoundDrawables(null,drawable,null,null);
            }
            textview.setOnClickListener(ControllerClick);
            if (i > 0) {
                textview.setPadding(18, 0, 0, 0);
            }
            ll_scroll_controller.addView(textview);
        }
    }

    //设置已选择的灯
    private void setLedItemToSelectScroll(){
        ll_select_led.removeAllViewsInLayout();
        LinearLayout llview=new LinearLayout(EditIfttt.this);
        if (selectLedList.size() == 0) {
            ll_select_led.addView(llview);
            return;
        }
        for (int i = 0; i < selectLedList.size(); i++) {
            if (i % 4 == 0) {
                llview=new LinearLayout(EditIfttt.this);
                llview.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                llview.setPadding(0, DensityUtils.dp2px(EditIfttt.this, 12f), 0, DensityUtils.dp2px(EditIfttt.this, 12f));
                ll_select_led.addView(llview);
            }
            TextView textview=new TextView(EditIfttt.this);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            params.setMargins(0, DensityUtils.dp2px(EditIfttt.this, 7f), 0, 0);
            params.gravity = Gravity.CENTER;
            textview.setLayoutParams(params);
            textview.setText(selectLedList.get(i));
            textview.setGravity(Gravity.CENTER);
            textview.setTextColor(Color.parseColor("#3398CC"));
            textview.setOnClickListener(selectLedClick);
            textview.setTag(selectLedList.get(i));
            Drawable select_led = this.getResources().getDrawable(R.drawable.icon_select_led);
            select_led.setBounds(0, 0, select_led.getMinimumWidth(), select_led.getMinimumHeight());
            textview.setCompoundDrawables(null, select_led, null, null);
            llview.addView(textview);

            if (selectLedList.size() % 4 > 0 && i == selectLedList.size() - 1) {
                //如果最后一组小于4个灯，则放入隐藏的tv
                for (int j = 0; j <= (4 - (selectLedList.size() % 4) - 1); j++) {
                    TextView textviews=new TextView(this);
                    textviews.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                    params.setMargins(0, DensityUtils.dp2px(EditIfttt.this, 7f), 0, 0);
                    textviews.setLayoutParams(params);
                    textviews.setText("Bulb0000");
                    textviews.setGravity(Gravity.CENTER);
                    textviews.setVisibility(View.INVISIBLE);
                    textviews.setCompoundDrawables(null,select_led,null,null);
                    llview.addView(textviews);
                };
            }
        }
    }

    //设置已选择的灯
    private void setControllerItemToSelectScroll(){
        ll_select_controller.removeAllViewsInLayout();
        LinearLayout llview=new LinearLayout(EditIfttt.this);
        if (selectControllerList.size() == 0) {
            ll_select_controller.addView(llview);
            return;
        }
        for (int i = 0; i < selectControllerList.size(); i++) {
            if (i % 4 == 0) {
                llview=new LinearLayout(EditIfttt.this);
                llview.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                llview.setPadding(0, DensityUtils.dp2px(EditIfttt.this, 12f), 0, DensityUtils.dp2px(EditIfttt.this, 12f));
                ll_select_controller.addView(llview);
            }
            TextView textview=new TextView(EditIfttt.this);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            params.setMargins(0, DensityUtils.dp2px(EditIfttt.this, 7f), 0, 0);
            params.gravity = Gravity.CENTER;
            textview.setLayoutParams(params);
            textview.setText(selectControllerList.get(i));
            textview.setGravity(Gravity.CENTER);
            textview.setTextColor(Color.parseColor("#3398CC"));
            textview.setOnClickListener(selectControllerClick);
            textview.setTag(selectControllerList.get(i));
            Drawable select_led = this.getResources().getDrawable(R.drawable.icon_select_controller);
            select_led.setBounds(0, 0, select_led.getMinimumWidth(), select_led.getMinimumHeight());
            textview.setCompoundDrawables(null,select_led,null,null);
            llview.addView(textview);

            if (selectControllerList.size() % 4 > 0 && i == selectControllerList.size() - 1) {
                //如果最后一组小于4个灯，则放入隐藏的tv
                for (int j = 0; j <= (4 - (selectControllerList.size() % 4) - 1); j++) {
                    TextView textviews=new TextView(this);
                    textviews.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                    params.setMargins(0, DensityUtils.dp2px(EditIfttt.this, 7f), 0, 0);
                    textviews.setLayoutParams(params);
                    textviews.setText("Controller0000");
                    textviews.setGravity(Gravity.CENTER);
                    textviews.setVisibility(View.INVISIBLE);
                    textviews.setCompoundDrawables(null,select_led,null,null);
                    llview.addView(textviews);
                };
            }
        }
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.ivConfirm:
                if (et_ifttt_name.getText().toString().trim().equals("")) {
                    Toast.makeText(this, "Please input group name", Toast.LENGTH_SHORT).show();
                    return;
                }else if (selectLedList.size() == 0) {
                    Toast.makeText(this, "Please choose at least one Bulb", Toast.LENGTH_SHORT).show();
                    return;
                } else if (et_ifttt_name.getText().toString().length() > 8) {
                    Toast.makeText(this, "Length of group name should less than 8", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, "add rule=" + selectControllerList.get(0) + ":" + selectLedList.get(0));
                iftttrule tempRule = new iftttrule();
                tempRule.setInput(selectControllerList.get(0));
                tempRule.setOutput(selectLedList.get(0));
                tempRule.setCondition(ruleCondition);

                rule_details.add(tempRule);
                mRuleAdapter.notifyDataSetChanged();
                long clusterId = 0x0001;
                long clusterIndex = 0x0000;
                long attributeId = 0x0000;
                String payload = tempRule.getInput() + "," + Long.toString(clusterId) + ","+ Long.toString(clusterIndex) + "," + Long.toString(attributeId)
                        + "," + Integer.toString(Conditon.CONDITION_EQ.ordinal()) + "," + Integer.toString(conditionIndex) +","+ tempRule.getOutput();
                mCenter.cAddIftttRule(payload);
                //onBackPressed();
                break;
            case R.id.ivBack:
                onBackPressed();
                break;
        }
    }


    public void buttonRuleOnClickHandler(View v) {
        iftttrule itemToApply = (iftttrule)v.getTag();
        Log.i(TAG, "switch iftttrule on " + itemToApply.getId());
        ivIftttRule.setText("RuleID=" + itemToApply.getId());
        long clusterId = 0x0001;
        long attributeId = 0x0000;
        long clusterIndex = 0x0000;
        String payload = itemToApply.getInput() + "," + Long.toString(clusterId) +"," + Long.toString(clusterIndex) + ","+ Long.toString(attributeId)
                + ","+Integer.toString(Conditon.CONDITION_EQ.ordinal()) + "," + Integer.toString(conditionIndex) +","+ itemToApply.getOutput();
        mCenter.cApplyIftttRule(payload);
    }

    public void removeRuleOnClickHandler(View v) {
        iftttrule itemToApply = (iftttrule)v.getTag();
        Log.i(TAG, "Remove iftttrule on " + itemToApply.getId());
        mCenter.cRemoveIftttRule(Integer.toString(itemToApply.getId()));
    }


    View.OnClickListener ControllerClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            TextView tv = (TextView) v;
            String subDid = v.getTag().toString();
            Log.i(TAG, "controller click=" + subDid);
            // Make sure only one be selected
            if (selectControllerList.size() > 0)
                return;
            if (selectControllerList.contains(subDid)) {
                Drawable drawable=EditIfttt.this.getResources().getDrawable(R.drawable.socketon);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tv.setCompoundDrawables(null,drawable,null,null);
                selectControllerList.remove(subDid);
            }else{
                Drawable drawable=EditIfttt.this.getResources().getDrawable(R.drawable.socketon);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tv.setCompoundDrawables(null,drawable,null,null);
                selectControllerList.add(subDid);
            }
            runOnUiThread(new Runnable() {
                public void run() {
                    setControllerItemToSelectScroll();
                }
            });
        }
    };

    View.OnClickListener ledClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            TextView tv = (TextView) v;
            String subDid = v.getTag().toString();
            Log.i(TAG, "led click=" + subDid);
            //Make sure only one be selected
            if (selectLedList.size() > 0)
                return;
            if (selectLedList.contains(subDid)) {
                Drawable drawable=EditIfttt.this.getResources().getDrawable(R.drawable.lampw_framew_b);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tv.setCompoundDrawables(null,drawable,null,null);
                selectLedList.remove(subDid);
            }else{
                Drawable drawable=EditIfttt.this.getResources().getDrawable(R.drawable.lampy_framey_b);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tv.setCompoundDrawables(null,drawable,null,null);
                selectLedList.add(subDid);
            }
            runOnUiThread(new Runnable() {
                public void run() {
                    setLedItemToSelectScroll();
                }
            });
        }
    };

    View.OnClickListener selectLedClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            String subDid = v.getTag().toString();
            Log.i(TAG, "selectLedClick click=" + subDid);
            if (selectLedList.contains(subDid)) {
                selectLedList.remove(subDid);

                runOnUiThread(new Runnable() {
                    public void run() {
                        setItemTollScroll();
                        setLedItemToSelectScroll();
                    }
                });
            }
        }
    };

    View.OnClickListener selectControllerClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            String subDid = v.getTag().toString();
            Log.i(TAG, "selectControllerClick click=" + subDid);
            if (selectControllerList.contains(subDid)) {
                selectControllerList.remove(subDid);

                runOnUiThread(new Runnable() {
                    public void run() {
                        setItemTollScroll();
                        setControllerItemToSelectScroll();
                    }
                });
            }
        }
    };


}
