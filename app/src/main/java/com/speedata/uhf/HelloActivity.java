package com.speedata.uhf;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.speedata.libuhf.IUHFService;
import com.speedata.libuhf.utils.SharedXmlUtil;

/**
 * @author zzc
 */
public class HelloActivity extends Activity {

    private IUHFService iuhfService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //强制为竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_hello);
        final Intent it = new Intent(this, NewMainActivity.class);
        final String xinghao = SystemProperties.get("ro.product.model");
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (MyApp.getInstance().getIuhfService() != null) {
                    SystemClock.sleep(2000);
                    startActivity(it);
                } else {
                    MyApp.getInstance().setIuhfService();
                    iuhfService = MyApp.getInstance().getIuhfService();
                    try {
                        if (iuhfService != null) {
                            if (openDev()) {
                                return;
                            }
                            int i;
                            i = iuhfService.setAntennaPower(30);
                            Log.d("zzc:", "===isFirstInit===setAntennaPower:" + i);
                            i = iuhfService.setQueryTagGroup(0, 0, 0);
                            Log.d("zzc:", "===isFirstInit===setQueryTagGroup:" + i);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    SystemClock.sleep(1000);
                    if ("SD60".equals(xinghao) || "SD60RT".equals(xinghao) || xinghao.contains("KT50") || xinghao.contains("KT55")
                            || "SD55L".equals(xinghao)) {
                        Log.d("UHFService", "startService==main==");
                        startService(new Intent(HelloActivity.this, MyService.class));
                        SharedXmlUtil.getInstance(HelloActivity.this).write("server", true);
                    }
                    SystemClock.sleep(1000);
                    startActivity(it);
                }
            }
        }).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    /**
     * 上电开串口
     */
    private boolean openDev() {
        if (iuhfService.openDev() != 0) {
            Toast.makeText(this, "Open serialport failed", Toast.LENGTH_SHORT).show();
            new AlertDialog.Builder(this).setTitle(R.string.DIA_ALERT).setMessage(R.string.DEV_OPEN_ERR).setPositiveButton(R.string.DIA_CHECK, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).show();
            return true;
        }
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
