package com.gizwits.framework.utils;

import android.os.Bundle;
import android.os.Message;

import java.util.regex.Pattern;

/**
 * Created by water.zhou on 7/8/2015.
 */
public final class Utils {

    private static final String ENTER = "\r";
    public static int COMMAND = 1;

    public static String gernerateCMD(String text) {

        if (text == null) {
            return null;
        }

        return text + ENTER;
    }



    public static String appendCharacters(String oldStr, String append, int count) {
        if ((oldStr==null && append==null) || count<0) {
            return null;
        }

        if (count == 0) {
            return new String(oldStr);
        }

        StringBuffer sb;

        if (oldStr == null) {
            sb = new StringBuffer();
        }else {
            sb = new StringBuffer(oldStr);
        }
        for (int i = 0; i < count; i++) {
            sb.append(append);
        }

        return sb.toString();
    }


    public static boolean isIP(String str) {
        Pattern pattern = Pattern.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])" +
                "\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\." +
                "((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\." +
                "((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
        return pattern.matcher(str).matches();
    }

    public static boolean isMAC(String str) {

        str = str.trim();
        if (str.length() != 12) {
            return false;
        }

        char[] chars = new char[12];
        str.getChars(0, 12, chars, 0);
        for (int i = 0; i < chars.length; i++) {
            if (!((chars[i]>='0' && chars[i]<='9') || (chars[i]>='A' && chars[i]<='F') || (chars[i]>='a' && chars[i]<='f'))) {
                return false;
            }
        }
        return true;
    }

    public synchronized static NetworkProtocol decodeProtocol(String response) {

        if (response == null) {
            return null;
        }

        String[] array = response.split(",");
        if (array == null) {
            return null;
        }

        //look for ip and port
        int index = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals("TCP") || array[i].equals("UDP")) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return null;
        }

        try {
            if (!isIP(array[index+3])) {
                return null;
            }

            NetworkProtocol protocol = new NetworkProtocol();
            protocol.setProtocol(array[0]);
            protocol.setServer(array[1]);
            protocol.setPort(Integer.valueOf(array[index+2]));
            protocol.setIp(array[index+3]);

            return protocol;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static float get_hsl_h(int r, int g, int b)
    {
        float hsl_h=0.0f;
        float max,min;
        max = (float)Math.max(r,g);
        max = (float)Math.max(max,b);
        min = (float)Math.min(r, g);
        min = (float)Math.min(min, b);

        if(max == min)
            hsl_h = 0.0f;
        else if(max ==r && (g>=b))
            hsl_h = 60.0f*(g-b)/(max-min);
        else if(max == r && (g<b))
            hsl_h = 60.0f*(g-b)/(max-min)+360.0f;
        else if(max == g)
            hsl_h = 60.0f*(b-r)/(max-min)+120.0f;
        else if(max == b)
            hsl_h = 60.0f*(r-g)/(max-min)+240.0f;
        else
            hsl_h = 0.0f;

        return hsl_h;
    }

    public static float get_hsl_s(int r, int g, int b)
    {
        float hsl_s=0.0f,hsl_l;
        float max,min;
        hsl_l = get_hsl_l(r,g,b);
        max = (float)Math.max(r,g);
        max = (float)Math.max(max,b);
        min = (float)Math.min(r, g);
        min = (float)Math.min(min, b);

        if((hsl_l == 0) || (max==min))
            hsl_s = 0;
        else if(hsl_l>0 && hsl_l<0.5)
            hsl_s = (max-min)/(max+min);
        else if(hsl_l>0.5)
            hsl_s=(max-min)/(2*255-(max+min));
        return hsl_s;

    }

    public static float get_hsl_l(int r, int g, int b)
    {
        float hsl_l=0.0f;
        float max,min;
        max = (float)Math.max(r,g);
        max = (float)Math.max(max,b);
        min = (float)Math.min(r, g);
        min = (float)Math.min(min, b);

        hsl_l = (max+min)/2.0f;

        return hsl_l;

    }



    public static short bytesToShort(byte[] b, int offset) {
        return (short) (b[offset] & 0xff | (b[offset+1] & 0xff) << 8);
    }



    public static Message generate_string_msg(int msg_value, String msg_text)
    {
        Message msg = new Message();
        msg.what = msg_value;
        Bundle bundle = new Bundle();
        bundle.putString(SmartConnectContants.STRING_MESSAGE ,msg_text);
        msg.setData(bundle);
        return msg;
    }
}
