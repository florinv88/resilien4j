package com.fnkcode.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.Collections;

@Configuration
public class HttpClientConfig {


    @Bean
    public RestClient restClientShortTimeout() {
        //3s per request timeout
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));

        return RestClient.builder()
                .requestFactory(new BufferingClientHttpRequestFactory(shortTimeoutHttpComponentsClientHttpRequestFactory()))
                .messageConverters(converters -> {
                    converters.add(jsonConverter);
                })
                .build();
    }

    @Bean
    public RestClient restClient() {
        //5s per request timeout
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));

        return RestClient.builder()
                .requestFactory(new BufferingClientHttpRequestFactory(HttpComponentsClientHttpRequestFactory()))
                .messageConverters(converters -> {
                    converters.add(jsonConverter);
                })
                .build();
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory shortTimeoutHttpComponentsClientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectionRequestTimeout(Duration.ofSeconds(3));
        factory.setReadTimeout(Duration.ofSeconds(3));
        return factory;
    }
    @Bean
    public HttpComponentsClientHttpRequestFactory HttpComponentsClientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectionRequestTimeout(Duration.ofSeconds(5));
        factory.setReadTimeout(Duration.ofSeconds(5));
        return factory;
    }
}
