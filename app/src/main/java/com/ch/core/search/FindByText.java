package com.ch.core.search;

import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.ch.application.MyApplication;
import com.ch.core.search.node.Dumper;
import com.ch.core.search.node.NodeInfo;
import com.ch.core.search.node.TreeInfo;
import com.ch.core.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class FindByText {

//    public static NodeInfo findAllPageByContent(String text,boolean totalMatch) {
//        AccessibilityNodeInfo[] roots = MyApplication.getAppInstance().getAccessbilityService().getRoots();
//        if (roots == null) {
//            Log.i(Utils.tag, "roots is null.");
//        }
//        TreeInfo treeInfo = new Dumper(roots).withIncludeOutsideSceenControl(true).dump();
//        if (treeInfo != null && treeInfo.getRects() != null) {
//            for (NodeInfo rect : treeInfo.getRects()) {
//                if(totalMatch){
//                    if (isTotalMatch(rect, text)) {
//                        return rect;
//                    }
//                }else {
//                    if (isMatch(rect, text)) {
//                        return rect;
//                    }
//                }
//            }
//        }
//        return null;
//    }

    public static NodeInfo find(String text) {
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

        TreeInfo treeInfo = new Dumper(roots).withIncludeOutsideSceenControl(false).dump();

        if (treeInfo != null && treeInfo.getRects() != null) {
            for (NodeInfo rect : treeInfo.getRects()) {
                if (isMatch(rect, text)) {
                    return rect;
                }
            }
        }
        return null;
    }

    public static List<NodeInfo> findNodeInfos(String text) {
        List<NodeInfo> nodeInfoList = new ArrayList<>();
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

        TreeInfo treeInfo = new Dumper(roots).withIncludeOutsideSceenControl(false).dump();

        if (treeInfo != null && treeInfo.getRects() != null) {
            for (NodeInfo rect : treeInfo.getRects()) {
                if (isMatch(rect, text)) {
                    nodeInfoList.add(rect);
                }
            }
        }
        return nodeInfoList;
    }

    private static boolean isMatch(NodeInfo nodeInfo, String text) {
        if (nodeInfo == null) {
            return false;
        }
        return Utils.textMatch(text, nodeInfo.getText());
    }

    public static NodeInfo findTotalMatch(String text) {
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

        TreeInfo treeInfo = new Dumper(roots).withIncludeOutsideSceenControl(false).dump();

        if (treeInfo != null && treeInfo.getRects() != null) {
            for (NodeInfo rect : treeInfo.getRects()) {
                if (isTotalMatch(rect, text)) {
                    return rect;
                }
            }
        }
        return null;
    }

    private static boolean isTotalMatch(NodeInfo nodeInfo, String text) {
        if (nodeInfo == null) {
            return false;
        }
        return Utils.textTotalMatch(text, nodeInfo.getText());
    }

    public static List<NodeInfo> findAllTotalMatch(String text) {
        List<NodeInfo> nodeInfoList = new ArrayList<>();
        AccessibilityNodeInfo[] roots = MyApplication.getAppInstance().getAccessbilityService().getRoots();
        if (roots == null) {
            Log.i(Utils.tag, "roots is null.");
        }
        TreeInfo treeInfo = new Dumper(roots).withIncludeOutsideSceenControl(false).dump();
        if (treeInfo != null && treeInfo.getRects() != null) {
            for (NodeInfo rect : treeInfo.getRects()) {
                if (isTotalMatch(rect, text)) {
                    nodeInfoList.add(rect);
                }
            }
        }
        return nodeInfoList.isEmpty()?null:nodeInfoList;
    }

    public static List<NodeInfo> findPageByContent(String text,boolean totalMatch) {
        List<NodeInfo> nodeInfoList = new ArrayList<>();
        AccessibilityNodeInfo[] roots = MyApplication.getAppInstance().getAccessbilityService().getRoots();
        if (roots == null) {
            Log.i(Utils.tag, "roots is null.");
        }
        TreeInfo treeInfo = new Dumper(roots).withIncludeOutsideSceenControl(true).dump();
        if (treeInfo != null && treeInfo.getRects() != null) {
            for (NodeInfo rect : treeInfo.getRects()) {
                if(totalMatch){
                    if (isTotalMatch(rect, text)) {
                        nodeInfoList.add(rect);
                    }
                }else {
                    if(isMatch(rect,text)){
                        nodeInfoList.add(rect);
                    }
                }
            }
        }
        return nodeInfoList.isEmpty()?null:nodeInfoList;
    }
}
