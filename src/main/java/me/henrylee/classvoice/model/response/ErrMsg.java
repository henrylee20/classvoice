package me.henrylee.classvoice.model.response;

public enum ErrMsg {

    OK(0, "ok"),

    USER_UNREGISTER(1, "unregister user"),
    USER_REGISTER_ERR(2, "cannot register this user"),
    USER_TOKEN_ERR(3, "token auth failed"),
    USER_PERMISSION_NOT_ALLOWED(4, "user do not have permission"),

    WX_AUTH_ERR(10, "cannot auth from wx server"),

    DB_OP_ERR(20, "error happened while operate DB"),
    DB_DATA_NOT_FOUND(21, "data not found"),

    FILE_EMPTY(30, "empty file"),

    SERVER_ERR(500, "Server Error");


    ErrMsg(int id, String msg) {
        this.id = id;
        this.msg = msg;
    }

    private int id;
    private String msg;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return this.msg;
    }
}
