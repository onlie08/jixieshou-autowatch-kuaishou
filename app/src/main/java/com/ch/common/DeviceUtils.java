package com.ch.common;

import android.os.Build;

import com.ch.core.utils.Logger;
import com.ch.core.utils.StringUtil;

import java.lang.reflect.Method;
import java.util.Calendar;

public class DeviceUtils {
    public static String getDeviceSN() {
        String serial = Build.SERIAL;
        if (!StringUtil.isEmpty(serial)) {
            return serial;
        }
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception e) {
            Logger.i("通过反射获取设备序列号失败");
        }
        return serial;
    }

    public static String getToday() {
        Calendar calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH));
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        return year + month + day;
    }
}
