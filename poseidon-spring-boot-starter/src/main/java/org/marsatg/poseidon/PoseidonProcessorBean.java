package org.marsatg.poseidon;

import org.marsatg.http.ResponseHolder;

public interface PoseidonProcessorBean {

    ResponseHolder invoke(String consumerName, String serviceName, String methodName, Object...args);
}
