package com.epoi.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class EgovJsmApi {

    @Value("${egov.jsm.api.client-key}") // 환경변수에서 Access Key ID
    private String accessKey;

    // 도로명주소 검색 공공 API
    public Map<String, String> getAddr(String keyword) {
        Logger logger = LoggerFactory.getLogger(getClass());

        Map<String, String> result = new HashMap<>();

        try {
            // API
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            URI uri = UriComponentsBuilder
                    .fromUriString("https://business.juso.go.kr/addrlink/addrLinkApi.do")
                    .queryParam("currentPage", 1)
                    .queryParam("countPerPage", 1)
                    .queryParam("resultType", "json")
                    .queryParam("keyword", keyword)
                    .queryParam("confmKey", accessKey)
                    .encode(StandardCharsets.UTF_8)
                    .build()
                    .toUri();

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            // 도로명주소 및 지번주소 반환
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.getBody());

            if (root.path("results").path("common").path("totalCount").asInt() > 0) {
                result.put("addrLoad", root.path("results").path("juso").get(0).path("roadAddrPart1").asText());
                result.put("addrNum", root.path("results").path("juso").get(0).path("jibunAddr").asText());
            }
        } catch (Exception e) {
            result.put("error", e.getMessage());
        }

        return result;
    }
}
