package me.henrylee.classvoice.model;

import org.springframework.data.annotation.Id;

public class User {

    @Id
    protected String id;
    protected String openid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }
}
