package org.marsatg;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NodeMethod {

    private String uniqueName;

    private String urlName;

    private Method method;

    private Object api;

    private Annotation[][] annotationArray;

    private Class<?>[] parameterTypes;

    public String getUniqueName() {
        return uniqueName;
    }

    public String getUrlName() {
        return urlName;
    }

    public Method getMethod() {
        return method;
    }

    public Annotation[][] getAnnotationArray() {
        return annotationArray;
    }

    public Object getApi() {
        return api;
    }

    public NodeMethod(Object api, String uniqueName, String urlName, Method method, Annotation[][] annotationArray,Class<?>[] parameterTypes) {
        this.api = api;
        this.uniqueName = uniqueName;
        this.urlName = urlName;
        this.method = method;
        this.annotationArray = annotationArray;
        this.parameterTypes = parameterTypes;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public Object invoke(Object...args) throws InvocationTargetException, IllegalAccessException {
        return this.method.invoke(this.api,args);
    }
}
