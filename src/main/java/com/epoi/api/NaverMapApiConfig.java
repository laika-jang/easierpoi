package com.epoi.api;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.nio.charset.StandardCharsets;

@Service
public class NaverMapApiConfig {

    @Value("${naver.map.api.client-id}") // 환경변수에서 Access Key ID
    private String accessKey;

    @Value("${naver.map.api.client-key}") // 환경변수에서 Secret Key
    private String secretKey;

    public String getCoordsByAddress(String address) {
        String host = "https://naveropenapi.apigw.ntruss.com";
        String url = "/map-geocode/v2/geocode"; // 호출할 API의 엔드포인트
        String timestamp = String.valueOf(System.currentTimeMillis());

        try {
            // 1. 시그니처 생성 (도장 찍기)
            String signature = makeSignature("GET", url + "?query=" + address, timestamp);

            // 2. 헤더 설정 (네이버 클라우드 전용 헤더들)
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-NCP-APIGW-API-KEY-ID", accessKey);
            headers.set("X-NCP-APIGW-API-KEY", secretKey); // 일부 API는 SecretKey를 직접 요구하기도 함
            headers.set("X-NCP-APIGW-TIMESTAMP", timestamp);
            headers.set("X-NCP-APIGW-SIGNATURE-V2", signature);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 3. URI 빌드 (주소 인코딩)
            URI uri = UriComponentsBuilder
                    .fromUriString(host + url)
                    .queryParam("query", address)
                    .encode(StandardCharsets.UTF_8)
                    .build()
                    .toUri();

            // 4. 호출
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

            return response.getBody();

        } catch (Exception e) {
            e.printStackTrace();
            return "API 호출 실패: " + e.getMessage();
        }
    }

    // 네이버 가이드에 있는 Signature 생성 로직 그대로 구현
    private String makeSignature(String method, String url, String timestamp) throws Exception {
        String space = " ";
        String newLine = "\n";

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(accessKey)
                .toString();

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeBase64String(rawHmac);
    }
}
