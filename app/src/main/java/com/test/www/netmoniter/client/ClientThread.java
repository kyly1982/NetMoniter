package com.test.www.netmoniter.client;

import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by kyly on 2016/6/20.
 */
public class ClientThread implements Runnable {
    private Socket socket;
    private Handler handler;
    private boolean isSend = false;
    private boolean isActivited = true;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public ClientThread(Socket socket, Handler handler) throws IOException {
        this.handler = handler;
        this.socket = socket;
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void requestData() {
        isSend = true;
    }

    public void close() {
        isActivited = false;
    }

    @Override
    public void run() {
        if (null != socket) {

                try {
                    String content = null;
                    while (isActivited) {
                        if (isSend) {
                            bufferedWriter.write("RequestData");
                        } else {
                            content = bufferedReader.readLine();
                            if (null != content && null != handler){
                                Message msg = handler.obtainMessage(919);
                                msg.obj = content;
                                handler.sendMessage(msg);
                            }
                        }
                        wait((long) 100);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

}
