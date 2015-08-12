package com.gizwits.smartlight.activity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.gizwits.framework.activity.BaseActivity;
import com.gizwits.framework.config.Configs;
import com.gizwits.framework.control.Group;
import com.gizwits.smartlight.R;
import com.xpg.common.device.DensityUtils;
import com.xtremeprog.xpgconnect.XPGWifiGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class EditGroupActivity extends BaseActivity implements OnClickListener {
    private final String TAG = "EditGroupActivity";
	/** The iv back. */
	private ImageView ivBack;
	/** The ll scroll layout. */
	private LinearLayout ll_scroll;
	/** The ll already select led */
	private LinearLayout ll_select_led;
	/** The et Edit group name */
	private EditText et_group_name;
	/** The iv Right confirm btn */
	private ImageView ivConfirm;
	/** The exit led list */
	private List<String> ledList = new ArrayList<String>();
	/** The already select led list */
	private List<String> selectLedList = new ArrayList<String>();
	/** The group exit LedList */
	private List<String> exitLedList = new ArrayList<String>();
	/** The exit group name */
	private String groupName;
	/** The subLight base centerCtrl did */
	private String did;
	/** The select wifi Group */
	private Group mXpgWifiGroup;
	/** The boolean group is Exit */
	private boolean groupIsExit = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_group);
		ledList=  getIntent().getStringArrayListExtra("ledList");
		exitLedList=  getIntent().getStringArrayListExtra("groupList");
		
		did= getIntent().getStringExtra("did");
		Log.i(TAG, "ledList=" + ledList +"     exitLedList="+exitLedList);

		initViews();
		initEvent();
		
		groupIsExit = false;
		//判断该分组是否已经存在，如果存在则放入组名，设置已选灯
		if (exitLedList != null && exitLedList.size() != 0) {
			groupIsExit = true;
			groupName= getIntent().getStringExtra("groupName");
			for (int i = 0; i < grouplist.size(); i++) {
				if (grouplist.get(i).groupName.equals(groupName)) {
					mXpgWifiGroup = grouplist.get(i);
				}
			}
			et_group_name.setText(groupName);
			et_group_name.setSelection(groupName.length());
			selectLedList.addAll(exitLedList);
			setItemToSelectScroll();
		}
		setItemTollScroll();
	}

	private void initViews() {
		ivBack = (ImageView) findViewById(R.id.ivBack);
		ll_scroll = (LinearLayout) findViewById(R.id.ll_scroll);
		ll_select_led = (LinearLayout) findViewById(R.id.ll_select_led);
		et_group_name = (EditText) findViewById(R.id.et_group_name);
		ivConfirm = (ImageView) findViewById(R.id.ivConfirm);
	}

	private void initEvent() {
		ivBack.setOnClickListener(this);
		ivConfirm.setOnClickListener(this);
	}

	// 设置滑动layout中灯泡
	private void setItemTollScroll() {
		if (ledList.size() == 0) {
			return;
		}
		ll_scroll.removeAllViewsInLayout();
        for (int i = 0; i < ledList.size(); i++) {
            TextView textview=new TextView(EditGroupActivity.this);
            textview.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            textview.setText("Bulb" + ledList.get(i));
            textview.setTag(ledList.get(i));
            textview.setGravity(Gravity.CENTER);
            textview.setTextColor(Color.parseColor("#FFFFFF")); 
            if (selectLedList.contains(ledList.get(i))) {
                Drawable drawable=EditGroupActivity.this.getResources().getDrawable(R.drawable.lampy_framey_b); 
    			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                textview.setCompoundDrawables(null,drawable,null,null);
			}else{
				Drawable drawable=EditGroupActivity.this.getResources().getDrawable(R.drawable.lampw_framew_b); 
    			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                textview.setCompoundDrawables(null,drawable,null,null);
			}
            textview.setOnClickListener(ledClick);
            if (i > 0) {
                textview.setPadding(18, 0, 0, 0);
			}
            ll_scroll.addView(textview); 
		}
	}
	
	//设置已选择的灯
	private void setItemToSelectScroll(){
		ll_select_led.removeAllViewsInLayout();
		LinearLayout llview=new LinearLayout(EditGroupActivity.this);
		if (selectLedList.size() == 0) {
			ll_select_led.addView(llview);
			return;
		}
		for (int i = 0; i < selectLedList.size(); i++) {
        	if (i % 4 == 0) {
        		llview=new LinearLayout(EditGroupActivity.this);
        		llview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        		llview.setPadding(0, DensityUtils.dp2px(EditGroupActivity.this, 12f), 0, DensityUtils.dp2px(EditGroupActivity.this, 12f));
        		ll_select_led.addView(llview);
			}
            TextView textview=new TextView(EditGroupActivity.this);
            LayoutParams params=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1);
            params.setMargins(0, DensityUtils.dp2px(EditGroupActivity.this, 7f), 0, 0);
            params.gravity = Gravity.CENTER;
            textview.setLayoutParams(params);
            textview.setText("Bulb"+selectLedList.get(i));
            textview.setGravity(Gravity.CENTER);
            textview.setTextColor(Color.parseColor("#3398CC")); 
            textview.setOnClickListener(selectLedClick);
            textview.setTag(selectLedList.get(i));
            Drawable select_led = this.getResources().getDrawable(R.drawable.icon_select_led); 
            select_led.setBounds(0, 0, select_led.getMinimumWidth(), select_led.getMinimumHeight());
            textview.setCompoundDrawables(null,select_led,null,null);
            llview.addView(textview); 
            
            if (selectLedList.size() % 4 > 0 && i == selectLedList.size() - 1) {
            	//如果最后一组小于4个灯，则放入隐藏的tv
            	for (int j = 0; j <= (4 - (selectLedList.size() % 4) - 1); j++) {
					TextView textviews=new TextView(this);
		            textviews.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
		            params.setMargins(0, DensityUtils.dp2px(EditGroupActivity.this, 7f), 0, 0);
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ivConfirm:
			if (et_group_name.getText().toString().trim().equals("")) {
				Toast.makeText(this, "Please input group name", Toast.LENGTH_SHORT).show();
				return;
			}else if (selectLedList.size() == 0) {
				Toast.makeText(this, "Please choose at least one Bulb", Toast.LENGTH_SHORT).show();
				return;
			} else if (et_group_name.getText().toString().length() > 8) {
                Toast.makeText(this, "Length of group name should less than 8", Toast.LENGTH_SHORT).show();
                return;
            }
			if (groupIsExit) {
				for (int i = 0; i < grouplist.size(); i++) {
					if (grouplist.get(i).groupName.equals(et_group_name.getText().toString().trim()) && !et_group_name.getText().toString().trim().equals(groupName)) {
						Toast.makeText(this, "Group name shouldn't be the same", Toast.LENGTH_SHORT).show();
						return;
					}
				}
				editGroup();
			}else{
				for (int i = 0; i < grouplist.size(); i++) {
					if (grouplist.get(i).groupName.equals(et_group_name.getText().toString().trim())) {
						Toast.makeText(this, "Group name shouldn't be the same", Toast.LENGTH_SHORT).show();
						return;
					}
				}
				saveGroup();
			}
			onBackPressed();
			break;
		case R.id.ivBack:
			onBackPressed();
			break;
		}
	}

//	private void addSubDeviceToGroup() {
//		for (int i = 0; i < selectLedList.size(); i++) {
//			boolean isOk = false;
//			for (int j = 0; j < exitLedList.size(); j++) {
//				if (exitLedList.get(j).equals(selectLedList.get(i))) {
//					isOk = true;
//				}
//				if (!isOk && exitLedList.size()-1 == j) {
//					Log.e("exit add", ""+selectLedList.get(i));
//					mCenter.cAddToGroup(mXpgWifiGroup, did, selectLedList.get(i));
//				}
//			}
//		}
//	}
//
//	private void removeSubDeviceFromGroup() {
//		for (int j = 0; j < exitLedList.size(); j++) {
//			boolean isOk = false;
//			for (int i = 0; i < selectLedList.size(); i++) {
//				if (exitLedList.get(j).equals(selectLedList.get(i))) {
//					isOk = true;
//				}
//				if (!isOk && selectLedList.size() - 1 == i) {
//					Log.e("exit Del", ""+exitLedList.get(j));
//					mCenter.cRemoveFromGroup(mXpgWifiGroup, did, exitLedList.get(j));
//				}
//			}
//		}
//	}
	
	//已有组，修改
	private void editGroup(){
		List<ConcurrentHashMap<String, String>> groupMaps = new ArrayList<ConcurrentHashMap<String,String>>();
		for (int i = 0; i < selectLedList.size(); i++) {
			ConcurrentHashMap<String, String> groupMap = new ConcurrentHashMap<String, String>();
			groupMap.put("did", did);
			groupMap.put("sdid", selectLedList.get(i));
			groupMaps.add(groupMap);
		}
		mCenter.cEditGroup(setmanager.getUid(), setmanager.getToken(), mXpgWifiGroup.groupName, et_group_name.getText().toString(), groupMaps);
	}


	//保存新的分组
	private void saveGroup() {
		List<ConcurrentHashMap<String, String>> groupMaps = new ArrayList<ConcurrentHashMap<String,String>>();
		for (int i = 0; i < selectLedList.size(); i++) {
			ConcurrentHashMap<String, String> groupMap = new ConcurrentHashMap<String, String>();
			groupMap.put("did", did);
			groupMap.put("sdid", selectLedList.get(i));
			groupMaps.add(groupMap);
		}
		mCenter.cAddGroup(setmanager.getUid(), setmanager.getToken(), Configs.PRODUCT_KEY_Sub, et_group_name.getText().toString(), groupMaps);

	}
	
	OnClickListener ledClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			TextView tv = (TextView) v;
			String subDid = v.getTag().toString();
			if (selectLedList.contains(subDid)) {
				Drawable drawable=EditGroupActivity.this.getResources().getDrawable(R.drawable.lampw_framew_b); 
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
				tv.setCompoundDrawables(null,drawable,null,null);
				selectLedList.remove(subDid);
			}else{
				Drawable drawable=EditGroupActivity.this.getResources().getDrawable(R.drawable.lampy_framey_b); 
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
				tv.setCompoundDrawables(null,drawable,null,null);
				selectLedList.add(subDid);
			}
			runOnUiThread(new Runnable() {
				public void run() {
					setItemToSelectScroll();
				}
			});
		}
	};
	
	OnClickListener selectLedClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String subDid = v.getTag().toString();
			if (selectLedList.contains(subDid)) {
				selectLedList.remove(subDid);

				runOnUiThread(new Runnable() {
					public void run() {
						setItemTollScroll();
						setItemToSelectScroll();
					}
				});
			}
		}
	};
}