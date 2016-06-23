package com.test.www.netmoniter.client;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by kyly on 2016/6/20.
 */
public class ClientThread implements Runnable {
    private Socket mSocket;
    private String host;
    private int port;

    private Handler handler;
    private boolean isSend = false;
    private boolean isActivited = false;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public ClientThread(String host, int port, Handler handler) {
        this.host = host;
        this.port = port;
        this.handler = handler;
    }

    public void requestData() {
        isSend = true;
    }

    public void close() {
        isActivited = false;
        if (null != bufferedReader) {
            try {
                wait(300);
                bufferedReader.close();
                bufferedWriter.close();
                mSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


    public boolean isConnected() {
        return mSocket.isConnected();
    }

    @Override
    public void run() {
        if (null == mSocket) {
            initSocket();
        }

        try {
            String content = null;
            while (isActivited) {
                if (isSend) {
                    bufferedWriter.write("RequestData");
                    isSend = false;
                } else {
                    content = bufferedReader.readLine();
                    if (null != content && null != handler) {
                        Message msg = handler.obtainMessage(919);
                        msg.obj = content;
                        handler.sendMessage(msg);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initSocket() {
        try {
            mSocket = new Socket();
            SocketAddress address = new InetSocketAddress(host, port);
            mSocket.connect(address, 10000);
            if (mSocket.isConnected()) {
                bufferedReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));
                isActivited = true;
                Log.e("CT", "已经连接上服务端");
            } else {
                Log.e("CT", "连接服务器失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("CT", "初始化套接字失败,原因：" + e.getLocalizedMessage());
            close();
        }
    }


}
