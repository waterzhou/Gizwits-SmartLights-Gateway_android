package com.gizwits.framework.utils;

/**
 * Created by water.zhou on 7/8/2015.
 */
public interface INetworkTransmission {

    public void setParameters(String ip, int port);
    public boolean open();
    public void close();
    public boolean send(String text);
    public void onReceive(byte[] buffer, int length);
}

