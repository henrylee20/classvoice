package me.henrylee.classvoice.model;

import java.util.List;

public class Student extends User {

    private String name;
    private String nick;
    private String no;
    private List<String> classIds;
    private List<String> voiceIds;
    private String className;
    private String phone;
    private String pwd;
    private String date;
    private boolean valid;
    private String memo;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getNick() {
        return nick;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getNo() {
        return no;
    }

    public void setClassIds(List<String> classIds) {
        this.classIds = classIds;
    }

    public List<String> getClassIds() {
        return classIds;
    }

    public void setVoiceIds(List<String> voiceIds) {
        this.voiceIds = voiceIds;
    }

    public List<String> getVoiceIds() {
        return voiceIds;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getPwd() {
        return pwd;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isValid() {
        return valid;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getMemo() {
        return memo;
    }

    public boolean checkBaseInfo() {
        return (name != null && no != null && className != null);
    }
}
