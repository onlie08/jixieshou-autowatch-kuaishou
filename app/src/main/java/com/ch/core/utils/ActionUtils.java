package com.ch.core.utils;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Build;

import com.ch.application.MyApplication;
import com.ch.core.executor.builder.SwipStepBuilder;
import com.ch.core.search.node.NodeInfo;

public class ActionUtils {

    /**
     * 点击某点
     *
     * @param x
     * @param y
     * @return
     */
    public static boolean click(int x, int y) {
        if (Build.VERSION.SDK_INT >= 24) {
            GestureDescription.Builder builder = new GestureDescription.Builder();
            Path path = new Path();
            path.moveTo(x, y);
//            GestureDescription gestureDescription = builder
//                    .addStroke(new GestureDescription.StrokeDescription(path, 20, 20))
//                    .build();
            GestureDescription gestureDescription = builder
                    .addStroke(new GestureDescription.StrokeDescription(path, 100, 50))
                    .build();
            return MyApplication.getAppInstance().getAccessbilityService().dispatchGesture(gestureDescription,
                    new AccessibilityService.GestureResultCallback() {
                        @Override
                        public void onCompleted(GestureDescription gestureDescription) {
                            super.onCompleted(gestureDescription);
                        }
                    }, null);
        }
        return false;
    }
    public static boolean longPress(int x, int y) {
        if (Build.VERSION.SDK_INT >= 24) {
            GestureDescription.Builder builder = new GestureDescription.Builder();
            Path path = new Path();
            path.moveTo(x, y);
//            GestureDescription gestureDescription = builder
//                    .addStroke(new GestureDescription.StrokeDescription(path, 20, 20))
//                    .build();
            GestureDescription gestureDescription = builder
                    .addStroke(new GestureDescription.StrokeDescription(path, 100, 1500))
                    .build();
            return MyApplication.getAppInstance().getAccessbilityService().dispatchGesture(gestureDescription,
                    new AccessibilityService.GestureResultCallback() {
                        @Override
                        public void onCompleted(GestureDescription gestureDescription) {
                            super.onCompleted(gestureDescription);
                        }
                    }, null);
        }
        return false;
    }

    /**
     * 点击某个区域的中间位置
     *
     * @param rect
     */
    public static boolean click(NodeInfo rect) {
        return click(rect.getRect().centerX(), rect.getRect().centerY());
    }
    public static boolean longPress(NodeInfo rect) {
        return longPress(rect.getRect().centerX(), rect.getRect().centerY());
    }

    /**
     * 从某点滑动到某点
     *
     * @param fromX
     * @param fromY
     * @param toX
     * @param toY
     */
    public static boolean swipe(int fromX, int fromY, int toX, int toY, int steps) {
        if (Build.VERSION.SDK_INT >= 24) {
            GestureDescription.Builder builder = new GestureDescription.Builder();
            Path path = new Path();
            path.moveTo(fromX, fromY);
            path.lineTo(toX, toY);
            GestureDescription gestureDescription = builder
                    .addStroke(new GestureDescription.StrokeDescription(path, 100, 500))
                    .build();
            return MyApplication.getAppInstance().getAccessbilityService().dispatchGesture(gestureDescription,
                    new AccessibilityService.GestureResultCallback() {
                        @Override
                        public void onCompleted(GestureDescription gestureDescription) {
                            super.onCompleted(gestureDescription);
                        }
                    }, null);
        }
        return true;
    }

    /**
     * 按一次返回键
     *
     * @return
     */
    public static boolean pressBack() {
        return MyApplication.getAppInstance().getAccessbilityService()
                .performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }
    public static boolean pressHome() {
        return MyApplication.getAppInstance().getAccessbilityService()
                .performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
    }

    public static void shanghua(){
        int x = MyApplication.getAppInstance().getScreenWidth() / 2 + (int)(Math.random()*100);
        int margin = 100+ (int)(Math.random()*100);
        int fromY = MyApplication.getAppInstance().getScreenHeight() - margin;
        int toY = margin;
        new SwipStepBuilder().setPoints(new Point(x, toY), new Point(x, fromY)).get().execute();
    }

    public static void xiahua(){
        int x = MyApplication.getAppInstance().getScreenWidth() / 2 + (int)(Math.random()*100);
        int margin = 100+ (int)(Math.random()*100);
        int fromY = MyApplication.getAppInstance().getScreenHeight() - margin;
        int toY = margin;
        new SwipStepBuilder().setPoints(new Point(x, fromY), new Point(x, toY)).get().execute();
    }

    public static void zuohua(){
        int x = MyApplication.getAppInstance().getScreenWidth()  - 150;
        int margin = 100+ (int)(Math.random()*100);
        int fromY = MyApplication.getAppInstance().getScreenHeight()/2;
        int toX = 200;
        new SwipStepBuilder().setPoints(new Point(x, fromY), new Point(toX, fromY)).get().execute();
    }
}
