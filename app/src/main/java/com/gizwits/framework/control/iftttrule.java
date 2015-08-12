package com.gizwits.framework.control;

import java.io.Serializable;

/**
 * Created by water.zhou on 8/7/2015.
 */
public class iftttrule implements Serializable {
    private static final long serialVersionUID = -5435670920302756945L;

    private String input = "";
    private String output = "";
    //cluster id + cluster index + attribute id + condition
    /*On_off: cluster id:0x0001  attribute id: 0x0000
    Level: cluster id:0x0002  attribute id: 0x0000
    Hue: cluster id:0x0301     attribute id: 0x0000
    Saturation: cluster id:0x0301 attribute id: 0x0001*/
    private String condition = "";
    //cluster index
    private byte id = 0;
    private byte enable = 0;

    public iftttrule() {
        this.setInput("");
        this.setOutput("");
        this.setCondition("");
        this.setId((byte)0);
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public byte getEnable() {
        return enable;
    }

    public void setEnable(Byte enable) {
        this.enable = enable;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}

