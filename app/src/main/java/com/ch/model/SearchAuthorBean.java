package com.ch.model;

public class SearchAuthorBean {
    private int type;//1:蜡笔新
    private String taskName;
    private String searchKey;
    private String author;

    public SearchAuthorBean() {
    }

    public SearchAuthorBean(int type, String taskName, String searchKey, String author) {
        this.type = type;
        this.taskName = taskName;
        this.searchKey = searchKey;
        this.author = author;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
}
