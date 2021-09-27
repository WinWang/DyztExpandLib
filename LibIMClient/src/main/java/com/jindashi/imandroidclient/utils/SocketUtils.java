package com.jindashi.imandroidclient.utils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Project on 2017/3/9.
 */
public class SocketUtils {
    /**
     * int to byte[] 支持 1或者 4 个字节
     *
     * @param i   需要转字节的数
     * @param len 多少字节
     * @return
     */
    public static byte[] intToByte(int i, int len) {
        byte[] abyte = null;
        if (len == 1) {
            abyte = new byte[len];
            abyte[0] = (byte) (0xff & i);
        } else {
            abyte = new byte[len];
            abyte[0] = (byte) ((i >>> 24) & 0xff);
            abyte[1] = (byte) ((i >>> 16) & 0xff);
            abyte[2] = (byte) ((i >>> 8) & 0xff);
            abyte[3] = (byte) (i & 0xff);
        }
        return abyte;
    }

    /**
     * 字节数组和整型的转换
     *
     * @param bytes 字节数组
     * @return 整型
     */
    public static int bytesToInt(byte[] bytes) {
        int addr = 0;
        if (bytes.length == 1) {
            addr = bytes[0] & 0xFF;
        } else {
            addr = bytes[0] & 0xFF;
            addr = (addr << 8) | (bytes[1] & 0xff);
            addr = (addr << 8) | (bytes[2] & 0xff);
            addr = (addr << 8) | (bytes[3] & 0xff);
        }

        return addr;
    }

    public static void writeTo(OutputStream out, String msg) throws IOException {
        byte[] bytes = msg.getBytes();
        int len = bytes.length;
        out.write(SocketUtils.intToByte(len, 4));
        out.write(msg.getBytes("utf-8"));
    }


    /**
     * 获取左侧时间，60倍数。
     *
     * @param time
     * @return
     */
    public static long getLeftTime(long time) {
        long yushu = time % 60;//余数
        long lefttime = yushu != 0 ? (time - yushu) : time;
        return lefttime;
    }

    /**
     * 获取右侧时间，60倍数。
     *
     * @param time
     * @return
     */
    public static long getRightTime(long time) {
        long yushu = time % 60;//余数
        long righttime = yushu != 0 ? (time - yushu + 60) : time;
        return righttime;
    }
}
