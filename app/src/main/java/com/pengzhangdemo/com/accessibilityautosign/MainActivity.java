package com.pengzhangdemo.com.accessibilityautosign;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.pengzhangdemo.com.accessibilityautosign.utils.BaseAccessibilityService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private PackageManager mPackageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BaseAccessibilityService.getInstance().init(this);
        findViewById(R.id.jd).setOnClickListener(this);
        findViewById(R.id.ali).setOnClickListener(this);
        mPackageManager = this.getPackageManager();
    }

    public void goAccess(View view) {
        BaseAccessibilityService.getInstance().goAccess();
    }


    public void autoSign(View view) {
        Intent intent = mPackageManager.getLaunchIntentForPackage("com.jingdong.app.mall");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void autoSignAlipay(View view) {
        Intent intent = mPackageManager.getLaunchIntentForPackage("com.taobao.mobile.dipei/com.eg.android.AlipayGphone.AlipayLogin");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.jd:
                autoSign(v);
                break;
            case R.id.ali:
                autoSignAlipay(v);
                break;
        }
    }
}
