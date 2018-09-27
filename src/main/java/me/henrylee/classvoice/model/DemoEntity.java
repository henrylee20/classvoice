package me.henrylee.classvoice.model;

import org.springframework.data.annotation.Id;

public class DemoEntity {

    @Id
    private String id;
    private String key;
    private String val;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }
}
