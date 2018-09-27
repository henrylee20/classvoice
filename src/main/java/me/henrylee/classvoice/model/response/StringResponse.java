package me.henrylee.classvoice.model.response;

public class StringResponse extends Response {
    private String data;

    public StringResponse(ErrMsg errMsg) {
        super(errMsg);
    }

    public StringResponse(ErrMsg errMsg, String data) {
        super(errMsg);
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
