package com.ch.common.leancloud;

import cn.leancloud.LCObject;

public interface AVListener {

    void success(LCObject obj);

    void fail(String error);
}
