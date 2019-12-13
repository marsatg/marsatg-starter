package org.marsatg.http;

import java.io.Serializable;

public class Response implements Serializable{

    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Response(Object data) {
        this.data = data;
    }

    public Response() {
    }

    public static Response getResponse(Object data){
        return new Response(data);
    }
}
