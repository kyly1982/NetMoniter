package com.test.www.netmoniter.server;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by kyly on 2016/6/23.
 *
 * handle用来返回serversocket的状态
 */
public class ServerThread implements Runnable {
    private int port;
    private Handler handler;
//    private ArrayList<ServerInfo> infos;

    private ServerSocket mServerSocket;
    private ArrayList<Socket> mClientSocket;

    private boolean isServerSocketActivited = false;//服务端socket正常
    private boolean isServerSocketMonitor = false;//服务端是否处理请求

    public static final int STATUS_INVALID = 0;                    //无效
    public static final int STATUS_INIT_SERVERSOCKET_SUCCESS = 1;  //初始化serversocket成功
    public static final int STATUS_INIT_SERVERSOCKET_FAIL = -1;    //初始化serversocket失败
    public static final int STATUS_CLOSE_SERVERSOCKET_SUCCESS = 2; //关闭serversocket成功
    public static final int STATUS_CLOSE_SERVERSOCKET_FAIL = -2;   //关闭serversocket失败

    public static final int STATUS_ACEPTCLIENT= 3;                 //新客户端接入

    public static final int STATUS_INIT_CLIENTSOCKET_SUCCESS = 4;  //初始化clientsocket成功
    public static final int STATUS_INIT_CLIENTSOCKET_FAIL = -4;    //初始化clientsocket失败
    public static final int STATUS_CLOSE_CLIENTSOCKET_SUCCESS = 5; //关闭clientsocket成功
    public static final int STATUS_CLOSE_CLIENTSOCKET_FAIL = -5;   //关闭clientsocket失败

    public ServerThread(int port, Handler handler) {
        this.handler = handler;
        this.port = port;
    }

    public boolean isClosed(){
        return null == mServerSocket ? true:mServerSocket.isClosed();
    }

    public void close() {
        if (null != mServerSocket) {
            isServerSocketMonitor = false;//让run中的循环退出
        }
        while (isServerSocketActivited){
            //等待run中不再处理连接请求
        }

        try {
            mServerSocket.close();
            closeClientSocket();
        } catch (Exception e){
            e.printStackTrace();
            handler.sendEmptyMessage(STATUS_CLOSE_SERVERSOCKET_FAIL);
            return;
        }
        handler.sendEmptyMessage(STATUS_CLOSE_SERVERSOCKET_SUCCESS);

    }

    @Override
    public void run() {
        if (null == mServerSocket) {
            initServerSocket();
            isServerSocketActivited = true;
            isServerSocketMonitor = true;
            mClientSocket = new ArrayList<>();
        }

        while (isServerSocketMonitor) {
            Socket socket = null;
            try {
                socket = mServerSocket.accept();
                handler.sendEmptyMessage(STATUS_ACEPTCLIENT);
            } catch (IOException e) {
                e.printStackTrace();
                if (null != socket){
                    Log.e("ST","关闭链接");
                    try {
                        socket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    handler.sendEmptyMessage(STATUS_INIT_CLIENTSOCKET_FAIL);
                }
                return;
            }
            if (null != socket){
                mClientSocket.add(socket);
                try {
                    new Thread(new ProcessThread(socket,null)).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(STATUS_INIT_CLIENTSOCKET_SUCCESS);
            }
        }
        isServerSocketActivited = false;
    }

    private void initServerSocket() {
        try {
            mServerSocket = new ServerSocket();
            mServerSocket.setReuseAddress(true);
            mServerSocket.bind(new InetSocketAddress(port));
        } catch (Exception e){
            e.printStackTrace();
            handler.sendEmptyMessage(STATUS_INIT_SERVERSOCKET_FAIL);
            return;
        }
        handler.sendEmptyMessage(STATUS_INIT_SERVERSOCKET_SUCCESS);
    }

    private void closeClientSocket()  {
        if (null != mClientSocket && !mClientSocket.isEmpty()){
                try {
                    for (Socket socket:mClientSocket){
                        if (!socket.isClosed()){
                            socket.close();
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    handler.sendEmptyMessage(STATUS_CLOSE_CLIENTSOCKET_FAIL);
                    //return;
                }
                handler.sendEmptyMessage(STATUS_CLOSE_CLIENTSOCKET_SUCCESS);
        }
    }



}
