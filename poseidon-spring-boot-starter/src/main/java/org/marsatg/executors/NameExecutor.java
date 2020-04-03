package org.marsatg.executors;

import org.marsatg.http.ResponseHolder;

public interface NameExecutor {

   ResponseHolder invoke(String consumerName, String serviceName, String methodName, Object... args);
}
