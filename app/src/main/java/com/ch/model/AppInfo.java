package com.ch.model;

import java.util.UUID;

public class AppInfo {

    private String uuid = UUID.randomUUID().toString();

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 包名
     */
    private String pkgName;

    /**
     * 执行时长
     */
    private long period;

    /**
     * 是否免费
     */
    private boolean isFree;

    /**
     * 推荐指数
     */
    private String star;

    /**
     * 标识今天任务是否完成
     */
    private boolean todayDone;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }


    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public boolean isTodayDone() {
        return todayDone;
    }

    public void setTodayDone(boolean todayDone) {
        this.todayDone = todayDone;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }
}
