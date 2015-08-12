package com.gizwits.framework.utils;

import java.io.Serializable;

/**
 * Created by water.zhou on 7/8/2015.
 */
public class NetworkProtocol implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int PROTOCAL_TCP_SERVER = 1;
    public static final int PROTOCAL_TCP_CLIENT = 2;
    public static final int PROTOCAL_UDP = 3;
    private String protocol;
    private String server;
    private int port;
    private String ip;

    /**
     * @return the protocol
     */
    public String getProtocol() {
        return protocol;
    }
    /**
     * @param protocol the protocol to set
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }
    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }
    /**
     * @return the ip
     */
    public String getIp() {
        return ip;
    }
    /**
     * @param ip the ip to set
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * @return the server
     */
    public String getServer() {
        return server;
    }
    /**
     * @param server the server to set
     */
    public void setServer(String server) {
        this.server = server;
    }
    public NetworkProtocol() {
        super();
    }

    public NetworkProtocol(String protocol, String server, String ip, int port) {
        super();
        this.protocol = protocol;
        this.server = server;
        this.ip = ip;
        this.port = port;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return String.format("Name:%s Server:%s IP:%s Port:%d", protocol, server, ip, port);
    }

    public int getType() {

        if (protocol == null) {
            return 0;
        }

        if (protocol.equals("TCP")) {

            if (server == null) {
                return 0;
            }else if (server.equals("Server")) {
                return PROTOCAL_TCP_SERVER;
            }else if (server.equals("Client")) {
                return PROTOCAL_TCP_CLIENT;
            }else {
                return 0;
            }
        }else if (protocol.equals("UDP")) {
            return PROTOCAL_UDP;
        }else {
            return 0;
        }
    }
}
