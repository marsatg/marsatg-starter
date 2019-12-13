package org.marsatg.http;

import java.io.Serializable;

public class Request implements Serializable{

    private String consumerName;

    private String serviceName;

    private String methodName;

    private Object args[];

    public String getConsumerName() {
        return consumerName;
    }

    public void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Request() {
    }

    public Request(String consumerName, String serviceName, String methodName, Object[] args) {
        this.consumerName = consumerName;
        this.serviceName = serviceName;
        this.methodName = methodName;
        this.args = args;
    }
}
