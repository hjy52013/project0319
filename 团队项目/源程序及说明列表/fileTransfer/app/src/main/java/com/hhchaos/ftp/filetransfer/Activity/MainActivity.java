package com.hhchaos.ftp.filetransfer.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hhchaos.ftp.filetransfer.Client.FTPServerService;
import com.hhchaos.ftp.filetransfer.R;
import com.hhchaos.ftp.filetransfer.Server.Globals;
import com.hhchaos.ftp.filetransfer.Server.MyLog;
import com.hhchaos.ftp.filetransfer.Util.Defaults;
import com.hhchaos.ftp.filetransfer.Util.GetIpUtil;
import com.hhchaos.ftp.filetransfer.Util.WifiAdmin;
import com.hhchaos.ftp.filetransfer.Util.WifiApAdmin;

import java.io.File;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button btn_send, btn_receive, btn_receive_dir, btn_usage;
    private WifiAdmin mWifiAdmin;
    public static final String TAG = "MainActivity";

    protected MyLog myLog = new MyLog(this.getClass().getName());
    protected Context activityContext = this;

    public final static String USERNAME = "username";
    public final static String PASSWORD = "password";
    public final static String PORTNUM = "portNum";
    public final static String CHROOTDIR = "chrootDir";
    public final static String ACCEPT_WIFI = "allowWifi";
    public final static String ACCEPT_NET = "allowNet";
    public final static String STAY_AWAKE = "stayAwake";
    public String FILE_PATH = "";
    public String END_PATH = "";
    private String connectIp="";
    private boolean isWifi;

    private final int SEND_REQUESTCODE = 1;
    private final int RECEIVE_REQUESTCODE = 2;
    private final int CHOOSEFILE_REQUESTCODE = 3;
    private final int SHOW_LAST_IP =9;
    private final int MSG_APOPENED = 4;
    private final int MSG_WIFICONNECTED = 5;
    private final int MSG_NOTFOUND = 6;
    private final int MSG_GETFILEURI = 7;
    private final int RECEIVE_REQUETIP = 8;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_APOPENED:
                    //成功建立wifi热点时的操作
                    //TODO 可以在此处进行连接ftp进行文件接收或者实现一个ftp客户端的Activity，然后跳转到该页面进行文件接收操作
                    Toast.makeText(getApplicationContext(), "正在连接到发送方！", Toast.LENGTH_SHORT).show();
                    receiveClient();
                    break;
                case MSG_WIFICONNECTED:
                    //成功连接wifi时的操作
                    //TODO 可以在此处打开文件选择器进行文件传输或者实现一个发送文件的Activity，然后跳转到该页面进行文件发送操作
                    break;
                case MSG_NOTFOUND:
                    //连接wifi超时时或者无法找到指定wifi时的操作
                    //检测连接成功或者失败，大概有5s延迟（等待wifi状态稳定时间，强制设置的，否则返回的信息是错的）
                    //超时设置时间为20s，20s内找不到指定网络就会停止线程
                    Toast.makeText(getApplicationContext(), "已超时，连接失败！请重试！", Toast.LENGTH_SHORT).show();
                    redoFiles();
                    mWifiAdmin.closeWifi();
                    break;
                case MSG_GETFILEURI:
                    Context context = getApplicationContext();
                    ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    if(!wifiNetworkInfo.isConnected()){
                        isWifi = false;
                        startActivityForResult(new Intent(getApplicationContext(), setLinkCodeActivity.class), SEND_REQUESTCODE);
                    }else{
                        isWifi=true;
                        showDialog(SHOW_LAST_IP);
                    }
                    break;

            }
        }
    };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case SHOW_LAST_IP:
                return createShowIpDialog();
            default:
                return null;
        }
    }
    private Dialog createShowIpDialog(){
        String[]temp = GetIpUtil.getLocalIpAddress(getApplicationContext()).split("\\.");
        String lastip=temp[temp.length-1];
        View rootLoadView = getLayoutInflater().inflate(
                R.layout.show_last_ip, null);
        TextView lastIpView =(TextView) rootLoadView.findViewById(R.id.lastIp);
        Button iKnow = (Button)rootLoadView.findViewById(R.id.iKnow);
        final Dialog showip = new AlertDialog.Builder(this).setView(rootLoadView).setCancelable(false).create();

        iKnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showip.dismiss();
                sendClient();
            }
        });
        lastIpView.setText(lastip);
        return showip;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Request no title bar on our window
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Set the application-wide context global, if not already set
        Context myContext = Globals.getContext();
        if (myContext == null) {
            myContext = getApplicationContext();
            if (myContext == null) {
                throw new NullPointerException("Null context!?!?!?");
            }
            Globals.setContext(myContext);
        }
        makeSendDir();
        setContentView(R.layout.activity_main);

        btn_send = (Button) findViewById(R.id.btn_send);
        btn_receive = (Button) findViewById(R.id.btn_receive);
        btn_receive_dir = (Button) findViewById(R.id.receiveDir);
        btn_usage = (Button) findViewById(R.id.usage);
        btn_send.setOnClickListener(this);
        btn_receive.setOnClickListener(this);
        btn_receive_dir.setOnClickListener(this);
        btn_usage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                try {
                    startActivityForResult(Intent.createChooser(intent, "请选择一个文件"), CHOOSEFILE_REQUESTCODE);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getApplicationContext(), "找不到文件管理器", Toast.LENGTH_SHORT).show();
                }
                break;
                // startActivityForResult(new Intent(getApplicationContext(),setLinkCodeActivity.class),SEND_REQUESTCODE);
            case R.id.btn_receive:
                Context context = getApplicationContext();
                ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                //已经连接到无线网络：
                if(wifiNetworkInfo.isConnected()){
                    Toast.makeText(getApplicationContext(), "已经连接到无线网络，启动有网传输！", Toast.LENGTH_SHORT).show();
                    startActivityForResult(new Intent(getApplicationContext(), setIpActivity.class), RECEIVE_REQUETIP);
                    break;
                }
                //没有连接到
                else{
                    Toast.makeText(getApplicationContext(), "没有连接到无线网络，启动无网传输！", Toast.LENGTH_SHORT).show();
                    startActivityForResult(new Intent(getApplicationContext(), setLinkCodeActivity.class), RECEIVE_REQUESTCODE);
                    break;
                }



            case R.id.receiveDir:
                startActivity(new Intent(MainActivity.this, DirActivity.class));
                break;
            case R.id.usage:
                startActivity(new Intent(MainActivity.this, UsageActivity.class));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SEND_REQUESTCODE:
                    //连接wifi,在新线程中实现
                    Toast.makeText(getApplicationContext(), "正在寻找接收端！", Toast.LENGTH_SHORT).show();
                    connectWifi connect = new connectWifi(data.getStringExtra("linkcode"));
                    connect.start();
                    break;
                case RECEIVE_REQUESTCODE:
                    connectIp="";
                    String linkcode = data.getStringExtra("linkcode");
                    //创建wifi热点,在新线程中实现
                    Toast.makeText(getApplicationContext(), "正在进行文件传输前的准备工作！", Toast.LENGTH_SHORT).show();
                    openWifiAP openAp = new openWifiAP(linkcode);
                    openAp.start();
                    break;
                case CHOOSEFILE_REQUESTCODE:
                    Uri uri = data.getData();
                    moveFiles(uri);
                    handler.sendEmptyMessage(MSG_GETFILEURI);
                    // Toast.makeText(getApplicationContext(),"choose a file\n"+uri.toString(),Toast.LENGTH_LONG).show();
                    break;
                case RECEIVE_REQUETIP:
                    String ip = data.getStringExtra("ip");
                    Toast.makeText(getApplicationContext(), "正在进行文件传输前的准备工作！", Toast.LENGTH_SHORT).show();
                    String[]temp = GetIpUtil.getLocalIpAddress(getApplicationContext()).split("\\.");
                    StringBuilder sb = new StringBuilder();
                    for(int i=0;i<temp.length;i++){
                        if(i!=temp.length-1){
                            sb.append(temp[i]).append(".");
                        }
                        else{
                            sb.append(ip);
                        }
                    }
                    connectIp = sb.toString();
                    handler.sendEmptyMessage(MSG_APOPENED);

            }
        }
    }

    private void receiveClient() {
        Intent intent = new Intent(MainActivity.this, FtpMainActivity.class);
        intent.putExtra("myip",connectIp);
        startActivity(intent);
    }

    private void setSendConfig() {
        File sdDir = Environment.getExternalStorageDirectory();
        SharedPreferences settings = null;
        settings = getSharedPreferences(Defaults.getSettingsName(), Defaults.getSettingsMode());
        String username = settings.getString(USERNAME, "username");
        String password = settings.getString(PASSWORD, "password");
        int portNum = settings.getInt(PORTNUM, Defaults.getPortNumber());
        String chroot = sdDir.getPath() + "/transferNote/send";
        boolean acceptWifi = Defaults.acceptWifi;
        boolean acceptNet = Defaults.acceptNet;
        boolean stayAwake = Defaults.stayAwake;
        File chrootTest = new File(chroot);
        if (!chrootTest.isDirectory() || !chrootTest.canRead()) {
            chroot = "/";
        }

        SharedPreferences.Editor editor = settings.edit();
        editor.putString(USERNAME, username);
        editor.putString(PASSWORD, password);
        editor.putInt(PORTNUM, portNum);
        editor.putString(CHROOTDIR, chroot);
        editor.putBoolean(ACCEPT_WIFI, acceptWifi);
        editor.putBoolean(ACCEPT_NET, acceptNet);
        editor.putBoolean(STAY_AWAKE, stayAwake);
        editor.commit();
    }

    private void sendClient() {
        Context context = getApplicationContext();
        WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        Intent intent = new Intent(context, FTPServerService.class);
        if (!FTPServerService.isRunning()) {
            setSendConfig();
            context.startService(intent);
        }
//      waiting
        Intent intentActive = new Intent(MainActivity.this, Waiting.class);
        intentActive.putExtra("endpath", END_PATH);
        intentActive.putExtra("filepath", FILE_PATH);
        intentActive.putExtra("isWifi",isWifi);
        startActivity(intentActive);
    }

    private void warnIfNoExternalStorage() {
        String storageState = Environment.getExternalStorageState();
        if (!storageState.equals(Environment.MEDIA_MOUNTED)) {
            myLog.i("Warning due to storage state " + storageState);
            Toast toast = Toast.makeText(this, "warning",
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    private class connectWifi extends Thread {
        private String linkcode;

        public connectWifi(String linkcode) {
            this.linkcode = linkcode;
        }

        public void run() {
            mWifiAdmin = new WifiAdmin(getApplicationContext()) {

                @Override
                public void myUnregisterReceiver(BroadcastReceiver receiver) {
                    // TODO Auto-generated method stub
                    MainActivity.this.unregisterReceiver(receiver);
                }

                @Override
                public Intent myRegisterReceiver(BroadcastReceiver receiver,
                                                 IntentFilter filter) {
                    // TODO Auto-generated method stub
                    MainActivity.this.registerReceiver(receiver, filter);
                    return null;
                }

                @Override
                public void onNotifyWifiConnected() {
                    // TODO Auto-generated method stub
                    //连接成功时发送成功信息
                    Log.v(TAG, "have connected success!");
                    Log.v(TAG, "###############################");

                }

                @Override
                public void onNotifyWifiConnectFailed() {
                    // TODO Auto-generated method stub
                    //连接失败时发送失败信息
                    handler.sendEmptyMessage(MSG_NOTFOUND);
                    Log.v(TAG, "have connected failed!");
                    Log.v(TAG, "###############################");
                }
            };
            mWifiAdmin.openWifi();
            while (!mWifiAdmin.isWifiOpened()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            boolean b = mWifiAdmin.isWifiExsits("filetransfer" + linkcode);

            int i = 0;
            while (!b) {
                Log.v(TAG, "cant find this wifi!");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i++;
                if (i > 40) {
                    //20s内找不到接收端，则自动停止线程，并发送失败信息
                    handler.sendEmptyMessage(MSG_NOTFOUND);
                    break;
                }
                b = mWifiAdmin.isWifiExsits("transfer" + linkcode);
            }
            if (i < 40) {
                mWifiAdmin.addNetwork(mWifiAdmin.createWifiInfo("transfer" + linkcode, linkcode + linkcode, WifiAdmin.TYPE_WPA));
                sendClient();
            }
        }
    }

    private class openWifiAP extends Thread {
        private String linkcode;

        public openWifiAP(String linkcode) {
            this.linkcode = linkcode;
        }

        public void run() {
            WifiApAdmin wifiAp = new WifiApAdmin(getApplicationContext()) {
                @Override
                public void onWifiApOpened() {
                    handler.sendEmptyMessage(MSG_APOPENED);
                }
            };
            wifiAp.startWifiAp("transfer" + linkcode, linkcode + linkcode);
        }
    }

    //创建目录/transferNote,/send用于存放发送的文件 /receive 用于存放接收到的文件
    private void makeSendDir() {
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            File sd = Environment.getExternalStorageDirectory();
            String path = sd.getPath() + "/transferNote";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }
            path = sd.getPath() + "/transferNote";
            path += "/send";
            file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }
            path = sd.getPath() + "/transferNote";
            path += "/receive";
            file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }
        }
    }

    public void moveFiles(Uri uri) {
        String destDirName = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "transferNote/send";
        File srcFile = getFileByUri(uri);
        if (!srcFile.exists() || !srcFile.isFile()) {
            return;
        }

        File destDir = new File(destDirName);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        File destFile = new File(destDirName + File.separator + srcFile.getName());
        END_PATH = destFile.getPath();
        srcFile.renameTo(destFile);
    }

    public void redoFiles() {
        if (!END_PATH.equals("") && !FILE_PATH.equals("")) {
            File srcFile = new File(END_PATH);
            File destFile = new File(FILE_PATH);
            srcFile.renameTo(destFile);
            FILE_PATH = "";
            END_PATH = "";
        }
    }

    public File getFileByUri(Uri uri) {
        String path = null;
        if ("file".equals(uri.getScheme())) {
            path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = this.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=").append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA}, buff.toString(), null, null);
                int index = 0;
                int dataIdx = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    index = cur.getInt(index);
                    dataIdx = cur.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    path = cur.getString(dataIdx);
                }
                cur.close();
                if (index == 0) {
                } else {
                    Uri u = Uri.parse("content://media/external/images/media/" + index);
                    System.out.println("temp uri is :" + u);
                }
            }
            if (path != null) {
                return new File(path);
            }
        } else if ("content".equals(uri.getScheme())) {
            // 4.2.2以后
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = this.getContentResolver().query(uri, proj, null, null, null);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(columnIndex);
            }
            cursor.close();
            FILE_PATH = path;  //获取路径
            return new File(path);
        } else {
            Log.i(TAG, "Uri Scheme:" + uri.getScheme());
        }
        return null;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        redoFiles();
        WifiApAdmin.closeWifiAp(getApplicationContext());
        super.onDestroy();
    }
}
