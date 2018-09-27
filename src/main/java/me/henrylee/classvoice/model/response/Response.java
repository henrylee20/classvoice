package me.henrylee.classvoice.model.response;

public class Response {
    private int err;
    private String errMsg;

    Response(ErrMsg errMsg) {
        this.err = errMsg.getId();
        this.errMsg = errMsg.getMsg();
    }

    public int getErr() {
        return err;
    }

    public void setErr(int err) {
        this.err = err;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
