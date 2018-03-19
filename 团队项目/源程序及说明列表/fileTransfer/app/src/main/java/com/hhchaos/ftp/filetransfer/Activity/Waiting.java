package com.hhchaos.ftp.filetransfer.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.*;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hhchaos.ftp.filetransfer.R;
import com.hhchaos.ftp.filetransfer.Util.GetIpUtil;
import com.hhchaos.ftp.filetransfer.Util.WifiAdmin;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class Waiting extends Activity {
    public String End_path = "";
    public String File_path = "";
    public String ipAdress = "";
    public static final String WAG = "Waiting";
    private TextView IPaddress = null;
    private  boolean isWifi;
    private  Button stopConnect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);
        IPaddress = (TextView) findViewById(R.id.IPaddress);
        Intent intent = getIntent();
        End_path = intent.getStringExtra("endpath");
        File_path = intent.getStringExtra("filepath");
        isWifi = intent.getBooleanExtra("isWifi",true);
        stopConnect=(Button)findViewById(R.id.stopConnect);
        stopConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDestroy();
            }
        });
        //ipAdress = "ftp://" + getIPAddress(true) + ":2121";
        String[]temp= GetIpUtil.getLocalIpAddress(getApplicationContext()).split("\\.");

        IPaddress.setText(temp[temp.length-1]);
    }


    @Override
    protected void onDestroy() {
        redoFiles();
        if(!isWifi){
          WifiManager  wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
            wifiManager.setWifiEnabled(false);
        }
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        redoFiles();
        super.onStop();
    }

    //    public void redoFiles(){
//        if(End_path.equals("")||File_path.equals("")){
//            return;
//        }
//        File srcFile = new File(End_path);
//        File destFile = new File(File_path);
//        srcFile.renameTo(destFile);
//        End_path = "";
//        File_path = "";
//    }
    public void redoFiles() {
        if (!End_path.equals("") && !File_path.equals("")) {
            File srcFile = new File(End_path);
            srcFile.renameTo(new File(File_path));
            End_path = "";
            File_path = "";
        }
    }


}
