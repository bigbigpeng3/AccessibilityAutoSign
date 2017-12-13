package com.pengzhangdemo.com.accessibilityautosign;

import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.pengzhangdemo.com.accessibilityautosign.utils.BaseAccessibilityService;

import java.util.Calendar;
import java.util.Date;


public class AutoSignAccessibilityService extends BaseAccessibilityService {

    public static final String TAG = "AutoSign";

    private String mPackageName;
    private AccessibilityNodeInfo mQian;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        mPackageName = event.getPackageName().toString();
        Log.e(TAG, "onAccessibilityEvent: " + mPackageName);
        if ("com.jingdong.app.mall".equals(mPackageName)) {
            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                gotoGetBeansActivity();
            }
        } else if ("com.taobao.mobile.dipei".equals(mPackageName)) {
//            gotoKouBeiPage();
            mHandler.sendEmptyMessageDelayed(MSG_GOTO_KOU_BEI_ACTIVITY,5000);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_CLICK_GET_BEANS_BTN:
                    clickSignInGetBeansBtn();
                    break;
                case MSG_PERFORM_DOWN_SCROLL:
                    performDownScroll();
                    break;
                case MSG_GOTO_KOU_BEI_ACTIVITY:
                    gotoKouBeiPage();
                    break;
            }
        }
    };


    //--------------------------支付宝------------------------------------//

    public static final int MSG_GOTO_KOU_BEI_ACTIVITY = -150;
    private long gotoKouBeiActivityTime = 0;
    private int gotoKouBeiCount = 0;
    private boolean successfulKouBei = false;

    private void gotoKouBeiPage() {
        if (gotoKouBeiCount < 3 && !successfulKouBei) {
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - gotoKouBeiActivityTime > 5000) {
                gotoKouBeiActivityTime = currentTimeMillis;
            } else {
                return;
            }
            AccessibilityNodeInfo ling = findViewByText("签到");
            Log.e(TAG, "gotoKouBeiPage: " + ling);
            if (ling != null) {
                GestureDescription.Builder builder = new GestureDescription.Builder();
                Path path = new Path();
                Rect outBounds = new Rect();
                ling.getParent().getBoundsInScreen(outBounds);
                path.moveTo(outBounds.centerX(), outBounds.centerY());
                GestureDescription gestureDescription = builder
                        .addStroke(new GestureDescription.StrokeDescription(path, 100, 50))
                        .build();
                dispatchGesture(gestureDescription, null, null);
                successfulKouBei = true;
            } else {
                mHandler.sendEmptyMessageDelayed(MSG_GOTO_KOU_BEI_ACTIVITY, 5000);
            }
            gotoKouBeiCount++;
        }

    }

    //--------------------------支付宝------------------------------------//

    //--------------------------京东--------------------------------------//

    private long gotoGetBeansActivityTime = 0;

    private void gotoGetBeansActivity() {
        if (!isSuccessfulSignInBeans) {
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - gotoGetBeansActivityTime > 5000) {
                gotoGetBeansActivityTime = currentTimeMillis;
            } else {
                return;
            }
            AccessibilityNodeInfo ling = findViewByText("领京豆");
            if (ling != null) {
                ling.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                mHandler.sendEmptyMessageDelayed(MSG_CLICK_GET_BEANS_BTN, 2000);
            }
        }
    }


    public static final int MSG_CLICK_GET_BEANS_BTN = -151;
    private boolean isSuccessfulSignInBeans = false;
    private long successfulSignInBeansTime = 0;

    private void clickSignInGetBeansBtn() {
        if (successfulSignInBeansTime == 0) {
            successfulSignInBeansTime = getLongSP(SIGN_BEANS_NAME, 0);
        }
        if (successfulSignInBeansTime == 0 || isTodayEnd(successfulSignInBeansTime)) {
            AccessibilityNodeInfo qian = findViewByText("签到领京豆");
            if (qian != null) {
                GestureDescription.Builder builder = new GestureDescription.Builder();
                Path path = new Path();
                Rect outBounds = new Rect();
                qian.getBoundsInScreen(outBounds);
                path.moveTo(outBounds.centerX(), outBounds.centerY());
                GestureDescription gestureDescription = builder
                        .addStroke(new GestureDescription.StrokeDescription(path, 100, 50))
                        .build();
                dispatchGesture(gestureDescription, null, null);
                isSuccessfulSignInBeans = true;
                successfulSignInBeansTime = System.currentTimeMillis();
                saveLongSP(SIGN_BEANS_NAME, successfulSignInBeansTime);
            } else {
                isSuccessfulSignInBeans = true;
            }
        }
        performBackClick();
        mHandler.sendEmptyMessageDelayed(MSG_PERFORM_DOWN_SCROLL, 2000);
    }

    public static final int MSG_PERFORM_DOWN_SCROLL = -152;

    private void performDownScroll() {
        GestureDescription.Builder builder = new GestureDescription.Builder();
        Path path = new Path();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int y = displayMetrics.heightPixels / 2;
        int x = displayMetrics.widthPixels / 2;
        path.moveTo(x, y);
        path.lineTo(x, y + 300 * displayMetrics.density);
        Log.e(TAG, "performDownScroll: " + x + "|" + y);
        GestureDescription gestureDescription = builder
                .addStroke(new GestureDescription.StrokeDescription(path, 10, 500))
                .build();
        dispatchGesture(gestureDescription, new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                Log.e(TAG, "onCompleted: " + gestureDescription);
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                Log.e(TAG, "onCancelled: " + gestureDescription);
            }
        }, null);
    }

    private Calendar calendar = Calendar.getInstance();

    public boolean isTodayEnd(long currTime) {
        long currentTimeMillis = System.currentTimeMillis();
        calendar.setTime(new Date(currTime));
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(year, month, day, 23, 59, 59);
        return currentTimeMillis > calendar.getTimeInMillis();
    }

    public static final String SIGN_BEANS_NAME = "SIGN_BEANS_NAME";


    //--------------------------京东--------------------------------------//

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
