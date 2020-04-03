package org.marsatg.http;

import java.io.Serializable;

public class ResponseHolder implements Serializable{

    private int state = 200;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public ResponseHolder(Object data,int state) {
        this.data = data;
        this.state = state;
    }

    public ResponseHolder() {
    }

    public static ResponseHolder getErrorResponse(Object data){
        return new ResponseHolder(data,1000);
    }

    public static ResponseHolder getSuccessResponse(Object data){
        return new ResponseHolder(data,200);
    }
}
