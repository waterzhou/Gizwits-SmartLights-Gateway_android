package com.gizwits.framework.utils;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by water.zhou on 7/8/2015.
 */
public class UdpUnicast implements INetworkTransmission{

    private static final String TAG = "UdpUnicast";
    private static final int BUFFER_SIZE = 1460;//2048;

    private String ip;
    private int port = 48899;
    private DatagramSocket socket;
    private DatagramPacket packetToSend;
    private InetAddress inetAddress;
    private ReceiveData receiveData;
    private UdpUnicastListener listener;
    private byte[] buffer = new byte[BUFFER_SIZE];

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
     * @param listener the listener to set
     */
    public void setListener(UdpUnicastListener listener) {
        this.listener = listener;
    }

    /**
     * @return the listener
     */
    public UdpUnicastListener getListener() {
        return listener;
    }

    public UdpUnicast(String ip, int port) {
        super();
        this.ip = ip;
        this.port = port;
    }

//    public UdpUnicast() {
//        super();
//    }

    /**
     * Open udp socket
     */
    public synchronized boolean open() {

        try {
            inetAddress = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        }

        try {
            socket = new DatagramSocket(port);
            socket.setBroadcast(true);
        } catch (SocketException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Close udp socket
     */
    public synchronized void close() {
        stopReceive();
        if (socket != null) {
            socket.close();
        }
    }

    public synchronized boolean isClosed() {
        Log.d(TAG, "isClosed()");
        if (socket == null) {
            return true;
        }else if(socket.isClosed()){
            return true;
        }else
            return false;

    }


    /**
     * send message
     * @param text
     * 			the message to broadcast
     */
    public synchronized boolean send(String text) {

        Log.d(TAG, "send:" + text);
//        if (receiveData.isStoped()) {
//            receiveData.restart();
//        }
        if (socket == null) {
            return false;
        }

        if (text == null) {
            return true;
        }
        text.trim();
        packetToSend = new DatagramPacket(
                text.getBytes(), text.getBytes().length, inetAddress, port);

        //remove the data in read chanel
        DatagramPacket packet = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
        while (true) {
            try {
                socket.setSoTimeout(100);
                socket.receive(packet);
            } catch (Exception e) {
                break;
            }
        }



        //send data
        try {
            socket.setSoTimeout(6000);
            socket.send(packetToSend);

            Log.d(TAG, "packet sent");

        } catch (IOException e) {
            e.printStackTrace();
        }

        //receive response
        receiveData = new ReceiveData();
        receiveData.start();

        return true;
    }

    /**
     * Stop to receive
     */
    public void stopReceive() {

        if (receiveData!=null && !receiveData.isStoped()) {
            receiveData.stop();
        }
    }

    public interface UdpUnicastListener {
        public void onReceived(byte[] data, int length);
    }

    private class ReceiveData implements Runnable {

        private boolean stop;
        private Thread thread;

        private ReceiveData() {
            thread = new Thread(this);
        }

        @Override
        public void run() {
            long time = System.currentTimeMillis();
            while (System.currentTimeMillis() - time < 15000 && !stop) {
                try {

                    DatagramPacket packetToReceive = new DatagramPacket(buffer, BUFFER_SIZE);
                    socket.receive(packetToReceive);
                    onReceive(buffer, packetToReceive.getLength());
                } catch (SocketTimeoutException e) {
                    Log.w(TAG, "Receive packet timeout!");
                }catch (IOException e1) {
                    Log.w(TAG, "Socket is closed!");
                }
            }
        }

        void start() {
            thread.start();
        }

        void stop() {
            stop = true;
        }
        void restart() { stop = false;}
        boolean isStoped() {
            return stop;
        }
    }

    @Override
    public void setParameters(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void onReceive(byte[] buffer, int length) {
        Log.d(TAG, new String(buffer, 0, length));
        if (listener != null) {
            listener.onReceived(buffer, length);
        }
    }
}

