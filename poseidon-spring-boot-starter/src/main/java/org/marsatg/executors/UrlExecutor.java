package org.marsatg.executors;

import org.marsatg.http.ResponseHolder;

public interface UrlExecutor {

   ResponseHolder invoke(String url, Object... args);
}
