package com.gizwits.framework.utils;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by water.zhou on 7/8/2015.
 */
public abstract class udpbroadcast {

    private static final String TAG = "UdpBroadcast";
    private static final int BUFFER_SIZE = 100;

    private int port = 6666;
    private static DatagramSocket socket;
    private DatagramPacket packetToSend;
    private InetAddress inetAddress;
    private ReceiveData receiveData;

    public void setPort(int port) {
        this.port = port;
    }

    public udpbroadcast() {
        super();

        try {
            inetAddress = InetAddress.getByName("255.255.255.255");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * Open udp socket
     */
    public void open() {

        try {
            Log.d(TAG, "open()");
            socket = new DatagramSocket(port);
            socket.setBroadcast(true);
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Close udp socket
     */
    public void close() {
        Log.d(TAG,"close()");
        stopReceive();
        if (socket != null) {
            socket.close();
        }
    }

    public boolean isClosed() {
        Log.d(TAG,"isClosed()");
        if (socket == null) {
            return true;
        }else if(socket.isClosed()){
            return true;
        }else
            return false;
    }

    public void sendDatagramPacket(final DatagramPacket packet) {
        new Thread() {
            @Override
            public void run() {
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    /**
     * broadcast message
     * @param text
     * 			the message to broadcast
     */
    public void send(String text) {
        if (socket == null || text == null) {
            return;
        }

        text = text.trim();
        Log.d(TAG,"UDP broadcasting:" + text + " in " + inetAddress + " " + port);
        packetToSend = new DatagramPacket(
                text.getBytes(), text.getBytes().length, inetAddress, port);

        try {
            socket.setSoTimeout(200);
            stopReceive();

            new Thread() {
                @Override
                public void run() {

                    //remove the data in read chanel
                    DatagramPacket packet = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
                    while (true) {
                        try {
                            socket.receive(packet);
                        } catch (Exception e) {
                            break;
                        }
                    }
                    //send data
                    try {
                        socket.setSoTimeout(6000);
                        socket.send(packetToSend);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //receive response
                    receiveData = new ReceiveData();
                    receiveData.start();
                }
            }.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void receive( ) {
        if (socket == null ) {
            return;
        }
        try {
            socket.setSoTimeout(200);
            new Thread() {
                @Override
                public void run() {
                    //receive
                    receiveData = new ReceiveData();
                    receiveData.start();
                }
            }.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Stop to receive
     */
    public void stopReceive() {

        if (receiveData!=null && !receiveData.isStoped()) {
            receiveData.stop();
        }
    }

    public abstract void onReceived(List<DatagramPacket> packets);

    private class ReceiveData implements Runnable {

        private boolean stop;
        private Thread thread;
        private List<DatagramPacket> packets;

        private ReceiveData() {
            thread = new Thread(this);
            packets = new ArrayList<DatagramPacket>();
        }

        @Override
        public void run() {

            long time = System.currentTimeMillis();

            while (System.currentTimeMillis() - time < 15000 && !stop) {
                Log.d(TAG, "try to receive");
                try {
                    DatagramPacket packetToReceive = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
                    socket.receive(packetToReceive);
                    packets.add(packetToReceive);
                } catch (SocketTimeoutException e) {
                    Log.w(TAG, "Receive packet timeout!");
                    break;
                }catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            if (!stop) {
                stop = true;
                onReceived(packets);
            }
        }

        void start() {
            thread.start();
        }

        void stop() {
            stop = true;
        }

        boolean isStoped() {
            return stop;
        }
    }
}

