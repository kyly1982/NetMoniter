package com.test.www.netmoniter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.test.www.netmoniter.client.Client;
import com.test.www.netmoniter.server.Server;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TextWatcher, View.OnClickListener, TextView.OnEditorActionListener,Client.OnResponseListener,Server.OnStatusChangeListener {
    private AppCompatEditText edt_host, edt_port;
    private AppCompatButton btn_save, btn_connect, btn_create;
    private ListView lv_data;
    private AppCompatTextView tv_status;

    private DataAdapter mAdapter;
    private ArrayList<ServerInfo> serverInfos;

    private Server mServer;
    private Client mClient;
    private Gson gson;

    private int clientCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (null == lv_data){
            initView();
        }

        if (null == edt_host.getText()) {
            readProfile();
        }


    }

    @Override
    protected void onDestroy() {
        if (null != mServer) {
            mServer.close();
        }

        if (null != mClient) {
            mClient.close();
        }

        while (null != mServer && null != mClient){

        }
        super.onDestroy();
    }

    private void initView() {
        if (null == lv_data) {
            edt_host = (AppCompatEditText) findViewById(R.id.edt_host);
            edt_port = (AppCompatEditText) findViewById(R.id.edt_port);
            btn_save = (AppCompatButton) findViewById(R.id.btn_save);
            btn_connect = (AppCompatButton) findViewById(R.id.btn_connect);
            lv_data = (ListView) findViewById(R.id.lv_data);
            btn_create = (AppCompatButton) findViewById(R.id.btn_create);
            tv_status = (AppCompatTextView) findViewById(R.id.tv_status);

            edt_host.addTextChangedListener(this);
            edt_port.addTextChangedListener(this);
            btn_save.setOnClickListener(this);
            btn_connect.setOnClickListener(this);
            btn_create.setOnClickListener(this);
            edt_port.setOnEditorActionListener(this);
            edt_host.setOnEditorActionListener(this);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void readProfile() {

    }

    private void saveProfile() {

    }

    private void createServer() {
        if (checkHostInput() && checkPortInput()) {
            mServer = new Server(Integer.valueOf(edt_port.getText().toString()),this);

        } else {
            Toast.makeText(this,"未输入host或者host输入不正确",Toast.LENGTH_LONG).show();
        }
    }

    private void createClient() {
        if (checkHostInput() && checkPortInput()){
            mClient = new Client(edt_host.getText().toString(),edt_port.getText().toString(),this);
        }
    }

 /*   private boolean checkInput(){
        return false;
    }*/

    private boolean checkHostInput(){
        //只接受xxx.xxx.xxx.xxx这种类型的主机地址
        if (null != edt_host.getText() && edt_host.getText().length() >6){
            String content = edt_host.getText().toString();
            String [] host = content.split("\\.");
            int length = host.length;
            if (4 == host.length){
                for (int i = 0;i < 4;i++){
                    try {
                        int part = Integer.valueOf(host[i]);
                        //检查第一段和第四段的取值范围是否正确
                        if (0 == i || 3 == i){
                            if (1 > part || 254 < part){
                                return false;
                            }
                        }else {
                            //检查第二段和第三段取值范围是否正确
                            if (0 > part || 255 < part){
                                return false;
                            }
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    private boolean checkPortInput(){
        if (null != edt_port.getText() && 0 < edt_port.getText().length()){
            try{
                int port = Integer.valueOf(edt_port.getText().toString());
                if (1024 < port && 65536 > port){
                    return true;
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 显示获取到的数据
     */
    private void showData() {
        if (null == mAdapter) {
            mAdapter = new DataAdapter(this);
            lv_data.setAdapter(mAdapter);
        }
        mAdapter.setData(serverInfos);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_connect:
                if (btn_connect.getText().equals(getString(R.string.connectToServer))) {
                    createClient();
                } else {
                    if (null != mClient){
                        mClient.close();
                    }
                }
                break;
            case R.id.btn_save:
                saveProfile();
                break;
            case R.id.btn_create:
                if (btn_create.getText().equals(R.string.createServer)){
                    createServer();
                } else {
                    if (null != mServer){
                        mServer.close();
                    }
                }

                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch (v.getId()) {
            case R.id.edt_host:
                if (actionId == KeyEvent.ACTION_UP || actionId == EditorInfo.IME_ACTION_NEXT)
                    edt_port.setFocusable(true);
                edt_port.setFocusableInTouchMode(true);
                edt_port.requestFocus();
                edt_port.requestFocusFromTouch();
                break;
            case R.id.edt_port:

                break;
        }
        return false;
    }

    @Override
    public void onServerResponse(String infos) {
        if (null == gson){
            gson = new Gson();
        }
        serverInfos = gson.fromJson(infos,new TypeToken<ArrayList<ServerInfo>>(){}.getType());
        showData();
    }

    @Override
    public void onAcpetedClient() {
        tv_status.setText("有新客户端接入！"+clientCount+"个客户端已连接");
    }

    @Override
    public void onCreateServer(boolean isSuccess) {
        if (isSuccess){
            btn_create.setText(R.string.closeServer);
            tv_status.setText("服务器创建成功，可以接受其它客户端连接了！");
        } else {
            btn_create.setText(R.string.createServer);
            tv_status.setText("创建服务器失败！");
        }
    }

    @Override
    public void onCloseServer(boolean isSuccess) {
        if (isSuccess){
            mServer = null;
            btn_create.setText(R.string.createServer);
            tv_status.setText("服务器已关闭！");
        } else {
            btn_create.setText(R.string.closeServer);
            tv_status.setText("关闭服务器失败！");
        }
    }

    @Override
    public void onConnectClient(boolean isSuccess) {
        if (isSuccess){
            clientCount++;
            tv_status.setText("连接客户端成功！当前客户端数量："+clientCount);
        }else {
            tv_status.setText("连接客户端失败！当前客户端数量："+clientCount);
        }
    }

    @Override
    public void onCloseClient(boolean isSuccess) {
        if (isSuccess){
            mClient = null;
            btn_connect.setText(R.string.connectToServer);
        } else {
            btn_connect.setText(R.string.closeConnect);
        }
    }
}
