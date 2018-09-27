package me.henrylee.classvoice.model.response;

import me.henrylee.classvoice.model.Class;

import java.util.List;

public class ClassListResponse extends Response {
    private List<Class> classes;

    public ClassListResponse(ErrMsg errMsg) {
        super(errMsg);
    }

    public ClassListResponse(ErrMsg errMsg, List<Class> classes) {
        super(errMsg);
        this.classes = classes;
    }

    public List<Class> getClasses() {
        return classes;
    }

    public void setClasses(List<Class> classes) {
        this.classes = classes;
    }
}
