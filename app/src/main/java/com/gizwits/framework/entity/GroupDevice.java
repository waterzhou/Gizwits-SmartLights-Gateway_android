package com.gizwits.framework.entity;

import com.gizwits.framework.control.scene;
import com.xtremeprog.xpgconnect.XPGWifiGroup;
import com.xtremeprog.xpgconnect.XPGWifiSubDevice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupDevice implements Serializable {
	public GroupDevice(){
		
	}
	public GroupDevice(XPGWifiSubDevice subDevice, boolean onOff, int lightness, int color, int saturation){
		this.subDevice = subDevice;
		this.onOff = onOff;
		this.lightness = lightness;
        this.colorness = color;
        this.saturation = saturation;
	}
	
	private XPGWifiSubDevice subDevice;
	private boolean onOff = false;
	private int lightness = 0;
    private int colorness = 0;
    private int saturation = 0;
	private int sdid;
	public XPGWifiSubDevice getSubDevice() {
		return subDevice;
	}
	public void setSubDevice(XPGWifiSubDevice subDevice) {
		this.subDevice = subDevice;
	}
	public boolean isOnOff() {
		return onOff;
	}
	public void setOnOff(boolean onOff) {
		this.onOff = onOff;
	}
	public int getLightness() {
		return lightness;
	}
    public int getColor() {
        return colorness;
    }
    public int getSaturation() {
        return saturation;
    }
	public void setLightness(int lightness) {
		this.lightness = lightness;
	}
    public void setColor(int colorness) {
        this.colorness = colorness;
    }
    public void setSaturation(int saturation) {
        this.saturation = saturation;
    }
	public int getSdid() {
		return sdid;
	}
	public void setSdid(int sdid) {
		this.sdid = sdid;
	}
	
	
	public static List<GroupDevice> getGroupDeviceByList(List<XPGWifiSubDevice> subDevices){
		List<GroupDevice> gDevices = new ArrayList<GroupDevice>();
		for (int i = 0; i < subDevices.size(); i++) {
			GroupDevice gDevice = new GroupDevice();
			gDevice.setSubDevice(subDevices.get(i));
			gDevices.add(gDevice);
		}
		return gDevices;
	}
	
	public static ArrayList<String> getAllName(List<GroupDevice> groupDevices){
		ArrayList<String> sdids = new ArrayList<String>();
		for (int i = 0; i < groupDevices.size(); i++) {
			sdids.add(groupDevices.get(i).getSubDevice().getSubDid());
		}
		return sdids;
	}

	public static ArrayList<String> getAllSceneName(List<scene> scene_details){
		ArrayList<String> names = new ArrayList<String>();
		for (int i = 0; i < scene_details.size(); i++) {
			names.add(Integer.toString(scene_details.get(i).getId()));
		}
		return names;
	}
	
}
