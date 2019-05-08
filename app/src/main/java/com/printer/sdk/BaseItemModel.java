package com.printer.sdk;

public class BaseItemModel {

    private String text;

    private long id;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "BaseItemModel{" +
                "text='" + text + '\'' +
                ", id=" + id +
                '}';
    }
}
