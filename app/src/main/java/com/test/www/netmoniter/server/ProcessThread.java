package com.test.www.netmoniter.server;

import com.google.gson.Gson;
import com.test.www.netmoniter.ServerInfo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by kyly on 2016/6/16.
 */
public class ProcessThread implements Runnable {
    private Socket socket;
    private ArrayList<ServerInfo> serverInfos;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private Gson gson;

    public ProcessThread(Socket socket, ArrayList<ServerInfo> serverInfos) throws IOException {
        this.socket = socket;
        this.serverInfos = serverInfos;
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        gson  = new Gson();
    }

    @Override
    public void run() {
        String content = null;
        try {
            do {
                content = bufferedReader.readLine();
                if (null != content && content.equals("RequestData")){
                    String data = gson.toJson(serverInfos);
                    bufferedWriter.write(data);
                }
            } while (null != content);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
