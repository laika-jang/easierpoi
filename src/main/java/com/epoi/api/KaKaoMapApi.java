package com.epoi.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class KaKaoMapApi {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${kakao.api.restapi-key}") // 환경변수에서 RestAPI Key
    private String restApiKey;

    @Value("${kakao.api.admin-key}") // 환경변수에서 Admin Key
    private String adminKey;
}
