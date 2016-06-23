package com.test.www.netmoniter.client;

import android.os.Handler;
import android.os.Message;

/**
 * Created by kyly on 2016/6/16.
 */
public class Client {
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
        initClitentTread();
    }


    public boolean isConnected(){
        return null == clientThread ? false:clientThread.isConnected();
    }

    public void setOnResponseListener(OnResponseListener listener){
        this.mListener = listener;
    }

    public void sendInstructions(){
        if (null != clientThread){
            clientThread.requestData();
        }
    }

    public void close(){
        if (null != clientThread){
            clientThread.close();
        }
    }

    private void initClitentTread(){
        clientThread = new ClientThread(host,port,handler);
        new Thread(clientThread).start();
        clientThread.requestData();
    }



}
