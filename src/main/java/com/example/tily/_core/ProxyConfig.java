package com.example.tily._core.config;

import com.example.tily._core.RestTemplateLoggingRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.Charset;
import java.time.Duration;

@Configuration
@Profile("deploy")
public class ProxyConfig {

    @Value("krmp-proxy.9rum.cc")
    private String proxyHost;

    @Value("3128")
    private int proxyPort;

    @Bean
    public RestTemplate restTemplateWithProxy() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
        factory.setProxy(proxy);
        factory.setConnectTimeout(6000);
        factory.setReadTimeout(6000);
        return new RestTemplate(factory);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                // 로깅 인터셉터에서 Stream을 소비하므로 BufferingClientHttpRequestFactory 을 꼭 써야한다.
                .requestFactory(() -> new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
                // 타임아웃 설정 (ms 단위)
                .setConnectTimeout(Duration.ofDays(30000))
                .setReadTimeout(Duration.ofDays(30000))
                // UTF-8 인코딩으로 메시지 컨버터 추가
                .additionalMessageConverters(new StringHttpMessageConverter(Charset.forName("UTF-8")))
                // 로깅 인터셉터 설정
                .additionalInterceptors(new RestTemplateLoggingRequestInterceptor())
                .build();
    }
}
