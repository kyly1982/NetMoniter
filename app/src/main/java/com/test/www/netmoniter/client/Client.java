package com.test.www.netmoniter.client;

import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by kyly on 2016/6/16.
 */
public class Client {
    private Socket mSocket;
    private String host;
    private int port;
    private OnResponseListener mListener;
    private ClientThread clientThread;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 919:
                    if (null != mListener && null != msg.obj){
                        String content = (String) msg.obj;
                        if (null != content && !content.isEmpty()) {
                            mListener.onServerResponse(content);
                        }
                    }
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

    public interface OnResponseListener{
        void onServerResponse(String infos);
    }

    public Client(String host,String port,OnResponseListener listener) {
        this.port = Integer.valueOf(port);
        this.host = host;
        this.mListener = listener;
        initSocket();
    }

    public void close(){
        if (null != clientThread){
            clientThread.close();
        }
        if (null != mSocket){
            try {
                if (null != mSocket.getInputStream()){
                    mSocket.getInputStream().close();
                }
                if (null != mSocket.getOutputStream()) {
                    mSocket.getOutputStream().close();
                }
                mSocket.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public boolean isConnected(){
        return mSocket.isConnected();
    }

    public void setOnResponseListener(OnResponseListener listener){
        this.mListener = listener;
    }

    public void sendInstructions(){

    }

    private void initSocket(){
        try {
            mSocket = new Socket(host,port);
            mSocket.setKeepAlive(true);
            clientThread = new ClientThread(mSocket,handler);
            Thread thread = new Thread(clientThread);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
