package com.ch.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class SchemeJumpUtil {

//    weixin://    此代码可以从浏览器进入微信
//    weixin://dl/scan    微信 扫一扫
//    weixin://dl/feedback  微信 设置
//    weixin://dl/notifications 微信 消息通知设置
//    weixin://dl/general  微信 通用设置
//    weixin://dl/officialaccounts 微信 公众号
//    weixin://dl/games 微信 游戏
//    weixin://dl/help 微信 反馈
//    weixin://dl/profile 微信 个人信息
//    weixin://dl/features 微信 功能

//    小红书Scheme全网最全
//    xhsdiscover://account/bind/’,//账号与安全
//    xhsdiscover://choose_share_user’,//分享给用户
//    xhsdiscover://dark_mode_setting/’,//深色设置
 //    xhsdiscover://general_setting/’,//通用设置
//    xhsdiscover://hey_home_feed/’,//记录我的日常
//    xhsdiscover://hey_post/’,//发布语音
//    xhsdiscover://home’,//主页
//    xhsdiscover://home/explore’,//发现列表
//    xhsdiscover://home/follow’,//关注列表
//    xhsdiscover://home/localfeed’,//同城列表
//    xhsdiscover://home/note’,//关注列表
//    xhsdiscover://home/store’,//商城
//    xhsdiscover://instore_search/result’,//商品搜索
//    xhsdiscover://instore_search/result?keyword=’,//商品搜索关键词
//    xhsdiscover://item/id’, //文字作品页
//    xhsdiscover://item/id?type=normal’, //文字作品页
//    xhsdiscover://item/id?type=video’,//视频作品页
//    xhsdiscover://search/result?keyword=’,//搜索关键词
//    xhsdiscover://me/profile’,//编辑资料
//    xhsdiscover://message/collections’,//收到的赞和收藏
//    xhsdiscover://message/comments’,//收到的评论和@
//    xhsdiscover://message/followers’,//新增关注
//    xhsdiscover://message/notifications’,//系统通知
//    xhsdiscover://message/strangers/’,//陌生人消息
//    xhsdiscover://messages’,//消息
//    xhsdiscover://notification_setting/’,//通知设置
//    xhsdiscover://post’,//发布作品-相册
//    xhsdiscover://post_note’,//发布笔记
//    xhsdiscover://post_video’,//发布视频
//    xhsdiscover://post_video_album’,//发布视频-全部相册
//    xhsdiscover://profile’,//我的个人页面
//    xhsdiscover://instore_search/recommend’,//商品搜索
//    xhsdiscover://recommend/contacts’,//通讯录好友
//    xhsdiscover://recommend/user’,//推荐用户
//    xhsdiscover://search/result’,//搜索
//    xhsdiscover://store’,//商城
//    xhsdiscover://system_settings/’,//开发者模式,可以修改登陆账号
//    xhsdiscover://topic/v2/keyword’,//话题
//    xhsdiscover://user/user_id’, //用户主页
//    xhsdiscover://user/id/followers’,//TA的粉丝
//snssdk1128://user/profile/1327807758079032 抖音用户主页
    /**
     * scheme跳转到小红书用户页
     * @param context
     * @param userId  like:58974fb56a6a69268012a35d
     */
    private void jumpXhsUser(Context context, String userId) {
        Uri uri = Uri.parse("xhsdiscover://user/"+userId);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        context.startActivity(intent);
    }

    public static void jumpWX(Context context) {
        Uri uri = Uri.parse("weixin://");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        context.startActivity(intent);
    }
}
