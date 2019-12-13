package org.marsatg.poseidon;

import org.marsatg.http.Response;

public interface PoseidonProcessorBean {

    public Response invoke(String consumerName, String serviceName, String methodName, Object...args);
}
