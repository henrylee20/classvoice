package me.henrylee.classvoice.model.response;

import me.henrylee.classvoice.model.Class;

public class ClassResponse extends Response {
    private Class clazz;

    public ClassResponse(ErrMsg errMsg) {
        super(errMsg);
    }

    public ClassResponse(ErrMsg errMsg, Class clazz) {
        super(errMsg);
        this.clazz = clazz;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }
}
