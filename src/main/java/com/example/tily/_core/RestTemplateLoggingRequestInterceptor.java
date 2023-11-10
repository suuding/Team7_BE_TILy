package com.example.tily._core;

import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

@Slf4j
public class RestTemplateLoggingRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        // 사용자 정보 추출
        String userId = "aaaa";

        // request log
        URI uri = request.getURI();
        traceRequest(request, body, userId);

        // execute
        ClientHttpResponse response = execution.execute(request, body);

        // response log
        traceResponse(response, userId, uri);
        return response;
    }

    private void traceRequest(HttpRequest request, byte[] body, String userId) throws IOException {
        StringBuilder reqLog = new StringBuilder();
        reqLog.append("[REQUEST] ");
        reqLog.append("Uri : " + request.getURI());
        reqLog.append(", Method : " + request.getMethod());
        reqLog.append(", Request Body : " + new String(body, StandardCharsets.UTF_8));
        reqLog.append(", UserId : " + userId);
        log.info(reqLog.toString());
    }

    private void traceResponse(ClientHttpResponse response, String userId, URI uri) throws IOException {
        InputStream is = response.getBody();
        byte[] bodyData = IOUtils.toByteArray(is);

        StringBuilder resLog = new StringBuilder();
        resLog.append("[RESPONSE] ");
        resLog.append("Uri : " + uri);
        resLog.append(", Status code : " + response.getStatusCode());
        resLog.append(", Response Body : " + new String(bodyData, StandardCharsets.UTF_8));
        resLog.append(", UserId : " + userId);
        log.info(resLog.toString());
    }
}