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
import java.util.Map;

@Service
public class NaverMapApi {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${naver.map.api.client-id}") // 환경변수에서 Access Key ID
    private String accessKey;

    @Value("${naver.map.api.client-key}") // 환경변수에서 Secret Key
    private String secretKey;

    // 주소를 코드로 변환
    public String geocode(String addr) {
        String url = "https://maps.apigw.ntruss.com/map-geocode/v2/geocode";

        try {
            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-ncp-apigw-api-key-id", accessKey);
            headers.set("x-ncp-apigw-api-key", secretKey);
            headers.setContentType(MediaType.IMAGE_JPEG);

            // 주소 인코딩
            URI uri = UriComponentsBuilder
                    .fromUriString(url)
                    .queryParam("query", addr)
                    .encode(StandardCharsets.UTF_8)
                    .build()
                    .toUri();

            // 호출
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

            return response.getBody();
        } catch (Exception e) {
            return "API 호출 실패: " + e.getMessage();
        }
    }

    // 정적 이미지 반환
    public byte[] raster(Map<String, String> param) {
        logger.info(param.toString());
        String url = "https://maps.apigw.ntruss.com/map-static/v2/raster";

        try {
            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-ncp-apigw-api-key-id", accessKey);
            headers.set("x-ncp-apigw-api-key", secretKey);
            headers.setContentType(MediaType.IMAGE_JPEG);

            // 주소 인코딩
            URI uri = UriComponentsBuilder
                    .fromUriString(url)
                    .queryParam("w", "442")
                    .queryParam("h", "360")
                    .queryParam("level", "16")
                    .queryParam("markers", "pos:127.1054221%2037.3591614")
                    .encode(StandardCharsets.UTF_8)
                    .build()
                    .toUri();

            String marker1 = String.format("type:n|size:tiny|color:blue|pos:%s %s", param.get("addr1x"), param.get("addr1y"));
            String marker2 = String.format("type:d|size:tiny|color:orange|pos:%s %s", param.get("addr2x"), param.get("addr2y"));
            String fullUrl = String.format(url + "?w=%s&h=%s&markers=%s&markers=%s", "442", "360", marker1, marker2);

            // 호출
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<byte[]> entity = new HttpEntity<>(headers);
            ResponseEntity<byte[]> response = restTemplate.exchange(fullUrl, HttpMethod.GET, entity, byte[].class);

            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }
}
