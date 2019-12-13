package org.marsatg.annotation;

import org.springframework.beans.BeansException;

public class ClientInitException extends BeansException {
    public ClientInitException(String msg) {
        super(msg);
    }

    public ClientInitException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
