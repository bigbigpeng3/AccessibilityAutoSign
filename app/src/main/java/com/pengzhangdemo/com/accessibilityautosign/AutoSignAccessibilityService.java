package com.pengzhangdemo.com.accessibilityautosign;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.pengzhangdemo.com.accessibilityautosign.utils.BaseAccessibilityService;


public class AutoSignAccessibilityService extends BaseAccessibilityService {

    public static final String TAG = "AutoSign";

    private String mPackageName;
    private AccessibilityNodeInfo mQian;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        mPackageName = event.getPackageName().toString();
        Log.e(TAG, "onAccessibilityEvent: " + mPackageName);
        if ("com.jingdong.app.mall".equals(mPackageName)) {


            AccessibilityNodeInfo ling = findViewByText("领京豆");
            if (ling != null) {
                ling.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }


            findAllView();

//            mQian = findViewByText("签到领京豆");
//            mQian = findViewByText("做任务");
//            AccessibilityNodeInfo parent = mQian.getParent();
//            if (parent != null){
//                parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                for (int i = 0; i < parent.getChildCount(); i++) {
//                    parent.getChild(i).performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                }
//            }


//                AccessibilityNodeInfo rootNode = getRootInActiveWindow();
//                goThrough(rootNode);
//            }


        }
    }

    private boolean goThrough(AccessibilityNodeInfo info) {
        if (info.getChildCount() == 0) {
            if (info.getText() != null && info.getText().toString().contains("搜索")) {
                if ("在沪江网校中搜索".equals(info.getText().toString()) && "android.widget.TextView".equals(info.getClassName())) {
                    AccessibilityNodeInfo parent = info;
                    while (parent != null) {
                        if (parent.isClickable()) {
                            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            break;
                        }
                        parent = parent.getParent();
                    }
                } else if ("输入关键字搜索".equals(info.getText().toString()) && "android.widget.EditText".equals(info.getClassName())) {
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("paste", "雅思英语");
                    clipboardManager.setPrimaryClip(clipData);
                    info.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                } else if ("搜索".equals(info.getText().toString()) && "android.widget.TextView".equals(info.getClassName())) {
                    AccessibilityNodeInfo parent = info;
                    while (parent != null) {
                        if (parent.isClickable()) {
                            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            break;
                        }
                        parent = parent.getParent();
                    }
                    return true;
                }
            }
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if (info.getChild(i) != null) {
                    goThrough(info.getChild(i));
                }
            }
        }
        return false;
    }


    public void findAllView() {
        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return;
        }
        getAllChild(accessibilityNodeInfo);
    }

    private void getAllChild(AccessibilityNodeInfo accessibilityNodeInfo) {

        if (accessibilityNodeInfo.getChildCount() < 1) {
            return;
        }

        int childCount1 = accessibilityNodeInfo.getChildCount();

        for (int i = 0; i < childCount1; i++) {

            AccessibilityNodeInfo child = accessibilityNodeInfo.getChild(i);

            if (child != null) {

                logNode(i, child);

                if (child.getText() != null && "已连续签到".equals(child.getText().toString())) {
                    AccessibilityNodeInfo parent = child.getParent();
                    Log.e(TAG, "已连续签到 parent = " + parent);
                    if (parent != null) {
                        logNode(parent);
//                        performViewClick(parent);
                    }

                }

                getAllChild(child);
            }

        }
    }


    private void logNode(int i, AccessibilityNodeInfo chlid) {
        Log.e(TAG,
                "chlid " + i + " = " + chlid.getText()
                        + "，chlid.getClassName() = " + chlid.getClassName()
                        + "，chlid.getViewIdResourceName = " + chlid.getViewIdResourceName()
                        + "，chlid.isClickAble = " + chlid.isClickable()
                        + "，childCount = " + chlid.getChildCount()

        );
    }

    private void logNode(AccessibilityNodeInfo node) {
        Log.e(TAG,
                "node " + " = " + node.getText()
                        + "，node.getClassName() = " + node.getClassName()
                        + "，node.getViewIdResourceName = " + node.getViewIdResourceName()
                        + "，node.isClickAble = " + node.isClickable()
                        + "，childCount = " + node.getChildCount()
        );
    }


}
