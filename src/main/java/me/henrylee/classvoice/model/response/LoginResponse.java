package me.henrylee.classvoice.model.response;

public class LoginResponse extends Response {

    private String userType;
    private String openid;
    private String token;

    public LoginResponse(ErrMsg errMsg) {
        super(errMsg);
        this.userType = "";
        this.openid = "";
        this.token = "";
    }

    public LoginResponse(ErrMsg errMsg, String userType, String openid, String token) {
        super(errMsg);
        this.userType = userType;
        this.openid = openid;
        this.token = token;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
