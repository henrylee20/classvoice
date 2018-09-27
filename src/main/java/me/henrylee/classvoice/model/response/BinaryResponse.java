package me.henrylee.classvoice.model.response;

public class BinaryResponse extends Response {
    private byte[] payload;

    public BinaryResponse(ErrMsg errMsg) {
        super(errMsg);
    }

    public BinaryResponse(ErrMsg errMsg, byte[] payload) {
        super(errMsg);
        this.payload = payload;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }
}
