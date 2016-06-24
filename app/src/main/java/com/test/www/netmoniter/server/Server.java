package com.test.www.netmoniter.server;

import android.os.Handler;
import android.os.Message;

import com.test.www.netmoniter.ServerInfo;

import java.util.ArrayList;

/**
 * Created by kyly on 2016/6/23.
 */
public class Server {
    private int port;
    private OnStatusChangeListener listener;

    private ServerThread mServerThread;
    private ArrayList<ServerInfo> infos;

    private final int CHANGE_DATA= 100;

    public interface OnStatusChangeListener {
        void onCreateServer(boolean isSuccess);

        void onCloseServer(boolean isSuccess);

        void onAcpetedClient();

        void onConnectClient(boolean isSuccess);

        void onCloseClient(boolean isSuccess);
    }

    public Server( int port, OnStatusChangeListener listener) {
        this.port = port;
        this.listener = listener;
        generateData();
        initServerTherad();
    }


    public void close(){
        handler.removeMessages(CHANGE_DATA);
        if (null != mServerThread){
            mServerThread.close();
        }
    }

    private void initServerTherad() {
        mServerThread = new ServerThread(port, handler);
        new Thread(mServerThread).start();
    }

    private void generateData(){
        if (null == infos){
            infos = new ArrayList<>(10);
        }

        for (int i = 0;i < 10;i++){
            ServerInfo info = new ServerInfo("第"+i+1+"个",""+Math.random()*100);
        }
        handler.sendEmptyMessageDelayed(CHANGE_DATA,2000);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ServerThread.STATUS_INIT_SERVERSOCKET_SUCCESS:
                    listener.onCreateServer(true);
                    break;
                case ServerThread.STATUS_INIT_SERVERSOCKET_FAIL:
                    listener.onCreateServer(false);
                    break;
                case ServerThread.STATUS_INIT_CLIENTSOCKET_SUCCESS:
                    listener.onConnectClient(true);
                    break;
                case ServerThread.STATUS_INIT_CLIENTSOCKET_FAIL:
                    listener.onConnectClient(false);
                    break;
                case ServerThread.STATUS_ACEPTCLIENT:
                    listener.onAcpetedClient();
                    break;
                case ServerThread.STATUS_CLOSE_SERVERSOCKET_SUCCESS:
                    listener.onCloseServer(true);
                    break;
                case ServerThread.STATUS_CLOSE_SERVERSOCKET_FAIL:
                    listener.onCloseServer(false);
                    break;
                case ServerThread.STATUS_CLOSE_CLIENTSOCKET_SUCCESS:
                    listener.onCloseClient(true);
                    break;
                case ServerThread.STATUS_CLOSE_CLIENTSOCKET_FAIL:
                    listener.onCloseClient(false);
                    break;
                case CHANGE_DATA:
                    int index1 = (int)Math.random() * 10;
                    int index2 = (int)Math.random() * 10;
                    while (index2 == index2){
                        index2 = (int)Math.random() * 10;
                    }
                    int index3 = (int)Math.random() * 10;
                    while (index3 == index2 || index3 == index1){
                        index3 = (int)Math.random() * 10;
                    }
                    int value1 = (int)Math.random() * 200;
                    int value2 = (int)Math.random() * 300;
                    int value3 = (int)Math.random() * 400;
                    infos.get(index1).setValue(""+value1);
                    infos.get(index2).setValue(""+value2);
                    infos.get(index3).setValue(""+value3);
                    handler.sendEmptyMessageDelayed(CHANGE_DATA,2000);
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };
}
