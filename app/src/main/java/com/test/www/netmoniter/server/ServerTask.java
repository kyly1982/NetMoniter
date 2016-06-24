package com.test.www.netmoniter.server;

import android.os.AsyncTask;

import com.test.www.netmoniter.ServerInfo;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by kyly on 2016/6/24.
 */
public class ServerTask extends AsyncTask {
    private String host;
    private int port;
    private ServerSocket mServerSocket;
    private ArrayList<Socket> mClientSockets;
    private ArrayList<ServerInfo> infos;

    public ServerTask(int port) {
        super();
        this.port = port;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        if (null == mServerSocket){
            try {
                mServerSocket = new ServerSocket();
                mServerSocket.setSoTimeout(10000);
                mServerSocket.setReuseAddress(true);
                mServerSocket.bind(new InetSocketAddress(port));
            } catch (Exception e){
                e.printStackTrace();
            }

        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }


    @Override
    protected void onCancelled() {
        if (null != mServerSocket){
            closeSocket(mServerSocket);
        }

        if (null != mClientSockets && !mClientSockets.isEmpty()){
            for (Socket socket:mClientSockets){
                if (!socket.isClosed()){
                    closeSocket(socket);
                }
            }
        }
        super.onCancelled();
    }

    private <T> boolean closeSocket(T objects){
        if (objects instanceof ServerSocket){
            try {
                ((ServerSocket) objects).close();
                return true;
            } catch (Exception e){
                e.printStackTrace();
            }
            return false;
        }

        if (objects instanceof Socket){
            try {
                ((Socket) objects).close();
                return true;
            } catch (Exception e){
                e.printStackTrace();
            }
            return false;
        }
        return false;
    }


}
