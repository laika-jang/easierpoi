package com.epoi.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Service
public class NaverSearchApi {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${naver.search.api.client-id}") // Access Key ID
    private String clientId;

    @Value("${naver.search.api.client-key}") // Secret Key
    private String clientSecret;

    public String searchPlace(String address) {
        // 1. 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 2. URL 빌드
        // 정렬을 'comment'로 하면 업체명 순, 'random'은 정확도 순
        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com/v1/search/local.json")
                .queryParam("query", address)
                .queryParam("display", 5)
                .queryParam("sort", "random")
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        // 3. 호출
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            return response.getBody(); // 결과 JSON 반환
        } catch (Exception e) {
            logger.error(e.getMessage());
            return "An exception occur. Please check logs";
        }
    }
}
