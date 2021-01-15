package com.zubisofts.menuqrgenerator.model;

public class Response {

    public Response(boolean error, Object data) {
        this.error = error;
        this.data = data;
    }

    private boolean error;
    private Object data;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
