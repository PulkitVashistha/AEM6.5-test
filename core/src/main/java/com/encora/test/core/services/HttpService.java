package com.encora.test.core.services;

import java.io.IOException;

public interface HttpService {
    String callServiceApi(String request, String url) throws IOException;
}
