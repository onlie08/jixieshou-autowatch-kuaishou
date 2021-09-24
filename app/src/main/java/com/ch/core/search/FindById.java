package com.ch.core.search;

import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.ch.application.MyApplication;
import com.ch.core.search.node.Dumper;
import com.ch.core.search.node.NodeInfo;
import com.ch.core.search.node.TreeInfo;
import com.ch.core.utils.Constant;
import com.ch.core.utils.Utils;

public class FindById {
    static String TAG = "FindById";
    public static NodeInfo find(String id) {
        AccessibilityNodeInfo[] roots = MyApplication.getAppInstance().getAccessbilityService().getRoots();
        if (roots == null) {
            Log.i(Utils.tag, "roots is null.");
        }

        Log.i(Utils.tag, "roots size: " + roots.length);
        for (int i = 0; i < roots.length; i++) {
            AccessibilityNodeInfo root = roots[i];
            if (root != null) {
                Log.i(Utils.tag, String.format("%d. root package: %s", i + 1, Utils.getRootPackageName(root)));
            } else {
                Log.e(Utils.tag, "error: root is null, index: " + i);
            }
        }

        TreeInfo treeInfo = new Dumper(roots).dump();

        if (treeInfo != null && treeInfo.getRects() != null) {
            for (NodeInfo rect : treeInfo.getRects()) {
                if (isMatch(rect, id)) {
                    return rect;
                }
            }
        }
        return null;
    }

    public static boolean setViewText(String viewIds,String text) {
        AccessibilityNodeInfo[] roots = MyApplication.getAppInstance().getAccessbilityService().getRoots();
        if (roots == null) {
            Log.i(Utils.tag, "roots is null.");
        }

        Log.i(Utils.tag, "roots size: " + roots.length);
        for (int i = 0; i < roots.length; i++) {
            AccessibilityNodeInfo root = roots[i];
            if (root != null) {
                Log.i(Utils.tag, String.format("%d. root package: %s", i + 1, Utils.getRootPackageName(root)));
            } else {
                Log.e(Utils.tag, "error: root is null, index: " + i);
            }
        }

        boolean hasId = false;
        TreeInfo treeInfo = new Dumper(roots).dump();

        if (treeInfo != null && treeInfo.getRects() != null) {
            for (NodeInfo rect : treeInfo.getRects()) {
                if (isMatch(rect, viewIds)) {
                    hasId = true;
                }
            }
        }
        if(hasId){
            for(int i=0;i<roots.length;i++){
                if(roots[i].getPackageName().toString().equals(Constant.PN_AI_QI_YI)){
                    Bundle arguments = new Bundle();
                    arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
                    roots[i].performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                    roots[i].performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isMatch(NodeInfo nodeInfo, String id) {
        if (nodeInfo == null) {
            return false;
        }
        String rid = nodeInfo.getId();
        Log.d(TAG,"isMatch:"+rid);
        return Utils.textMatch(id, rid);
    }

}
