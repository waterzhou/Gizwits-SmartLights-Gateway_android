package com.gizwits.framework.control;

import java.io.Serializable;

/**
 * Created by water.zhou on 7/31/2015.
 */
public class  scene implements Serializable {
    private static final long serialVersionUID = -5435670920302756945L;

    private String name = "";
    private String value = "";
    private String group = "";
    private int id = 0;

    public scene(String name, String value, String group, int id) {
        this.setName(name);
        this.setValue(value);
        this.setGroup(group);
        this.setId(id);
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }
    public void setGroup(String group) { this.group = group; }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
