package com.test.www.netmoniter.server;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.test.www.netmoniter.ServerInfo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MyService extends Service {
    private int port;
    private ArrayList<Socket> clientConnect;
    private ArrayList<ServerInfo> infos;
    private static ServerSocket mServerSocket;

    private boolean isServerSocketMonitor = false;

    public MyService() {
    }

    public MyService(String host, int port) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        port = intent.getIntExtra("PORT",0);
        if (1024 < port){
            initServer();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (null != handler){
            handler.sendEmptyMessage(3);
        }
        isServerSocketMonitor = false;
        closeServerSocket();
        closeClientConnect();
        super.onDestroy();
    }

    private void initServer(){
        generateData();
        if (null == clientConnect){
            clientConnect = new ArrayList<>(3);
        }
        try {
            mServerSocket = new ServerSocket(port);
            assert (null != mServerSocket);
            isServerSocketMonitor = true;
            new Thread(new ServerThread(mServerSocket)).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
        closeServerSocket();
        closeClientConnect();
        isServerSocketMonitor = false;
    }

    private void generateData(){
        if (null == infos){
            infos = new ArrayList<>(10);
        }

        for (int i = 0;i < 10;i++){
            ServerInfo info = new ServerInfo("第"+i+1+"个",""+Math.random()*100);
        }
        handler.sendEmptyMessageDelayed(0,2000);
    }

    private void closeServerSocket(){
        if (null != mServerSocket){
            try {
                mServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mServerSocket = null;
        }
    }

    private void closeClientConnect(){
        if (null != clientConnect && !clientConnect.isEmpty()){
            for (Socket socket:clientConnect){
                closeSocket(socket);
            }
        }
    }

    private void closeSocket(Socket socket){
        if (socket.isClosed()){
            return;
        }
        try {
            if (null != socket.getOutputStream()) {
                socket.getOutputStream().close();
            }
            if (null != socket.getInputStream()) {
                socket.getInputStream().close();
            }
            socket.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 3:
                    handler.removeMessages(0);
                    break;
                case 0:
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
                    handler.sendEmptyMessageDelayed(0,2000);
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }

        }
    };


    public class ServerThread implements Runnable {
        private ServerSocket serverSocket;

        public ServerThread(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }

        @Override
        public void run() {
            //在未收到停止消息时，监听客户端接入
            while (isServerSocketMonitor){
                try {
                    Socket socket = serverSocket.accept();
                    if (null != socket) {
                        clientConnect.add(socket);
                        new Thread(new ProcessThread(socket,infos)).start();
                    }

                } catch (Exception e){

                }
            }

        }
    }
}
