package com.epoi.service;

import com.epoi.api.NaverMapApi;
import com.epoi.api.NaverSearchApi;
import com.epoi.controller.dto.ValidityDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ValidityService {
    Logger logger = LoggerFactory.getLogger(getClass());

    private final NaverSearchApi naverSearchApi;

    public ValidityService(NaverSearchApi naverSearchApi, NaverMapApi naverMapApi) {
        this.naverSearchApi = naverSearchApi;
    }

    // 네이버 지역 검색 결과 반환
    public Map<String, Object> getResult(Map<String, String> param) {
        Map<String, Object> result = new HashMap<>();

        // 메시지
        String msgFind = "있어요 (O)";
        String msgNotFind = "없어요 (X)";
        String msgSimilar = "[보류] 장소가 달라요";

        /* 키워드 정제
         * place       : 상호
         * placeAndAddr: 지역 + 상호 (지역은 시·구 혹은 도·군 단위로 한정)
         * addrLoad    : 도로명 주소
         * addrNum     : 지번 주소
         * local       : 지역
         * */
        Map<String, String> keywords = new HashMap<>();
        keywords.put("place", param.get("place").split(";", 2)[0].split(" ", 2)[1]);
        keywords.put("placeAndAddr", (param.get("addrNum").split(" ")[0] + " " + param.get("addrNum").split(" ")[1] + " " + param.get("place")));
        keywords.put("addrLoad", param.get("addrLoad").isEmpty() ? "" : param.get("addrLoad"));
        keywords.put("addrNum", param.get("addrNum"));
        keywords.put("local", param.get("addrNum").split(" ")[0] + " " + param.get("addrNum").split(" ")[1] + " " + param.get("addrNum").split(" ")[2]);
        if (param.get("place").split(";").length > 1) keywords.put("addKeywords", param.get("place").split(";", 2)[1]);

        // 검색 결과를 java 객체로 변환
        List<ValidityDTO> list = jsonParser(naverSearchApi.searchPlace(keywords.get("placeAndAddr")));
        List<ValidityDTO> resultList = new ArrayList<>();

        for (ValidityDTO dto : list) {
            resultList.add(dto);

            // 상호와 도로명 주소 또는 지번 주소가 검색어와 같은 경우
            if (
                    (dto.getPlace().equals(keywords.get("place")) && dto.getAddrLoad().contains(keywords.get("addrLoad"))) ||
                    (dto.getPlace().equals(keywords.get("place")) && dto.getAddrNum().contains(keywords.get("addrNum")))
            ) {
                logger.info("status = 1, msg = " + msgFind);
                result.put("msg", msgFind);
                result.put("status", "1");

            // 상호와 지역은 동일하나 상세 주소가 다른 경우
            } else if (dto.getPlace().equals(keywords.get("place")) && dto.getAddrNum().contains(keywords.get("local"))) {
                logger.info("status = 3, msg = " + msgSimilar);
                result.put("msg", msgSimilar);
                result.put("status", "3");
            }

            if (result.containsKey("msg")) {
                resultList.clear();
                resultList.add(dto);
                logger.info("msg exists. resultList.size() = " + resultList.size());
                break;
            }

            logger.info("Go to next dto.");
        }

        // 관련 장소를 찾지 못한 경우
        if (!result.containsKey("msg")) {
            logger.info("msg does not exists. Find other places.");

            list = jsonParser(naverSearchApi.searchPlace(keywords.get("addrNum") + " " + keywords.get("place")));

            for (ValidityDTO dto : list) {
                resultList.add(dto);

                // 상호와 도로명 주소 또는 지번 주소가 검색어와 같은 경우
                if (
                        (dto.getPlace().equals(keywords.get("place")) && dto.getAddrLoad().contains(keywords.get("addrLoad"))) ||
                        (dto.getPlace().equals(keywords.get("place")) && dto.getAddrNum().contains(keywords.get("addrNum")))
                ) {
                    logger.info("status = 1, msg = " + msgFind);
                    resultList.clear();
                    resultList.add(dto);
                    logger.info("msg exists. resultList.size() = " + resultList.size());
                    result.put("msg", msgFind);
                    result.put("status", "1");
                    break;
                }
            }

            if (!result.containsKey("msg")) {
                logger.info("status = 2, msg = " + msgNotFind);
                result.put("msg", msgNotFind);
                result.put("status", "2");
            }
        }

        result.put("list", list);

        // 추가 검색 키워드가 있는 경우
        if (keywords.containsKey("addKeywords")) {
            for (String keywordsEach : keywords.get("addKeywords").split(";")) {
                result.put(keywordsEach, jsonParser(naverSearchApi.searchPlace(keywords.get("addrNum") + " " + keywordsEach.trim())));
            }
        }

        return result;
    }

    // 검색 결과를 java 객체로 변환
    public List<ValidityDTO> jsonParser(String jsonString) {
        List<ValidityDTO> result = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Map<String, Object> map = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {});

            if (!map.isEmpty()) {
                for (Map<String, Object> item : (List<Map<String, Object>>) map.get("items")) {
                    ValidityDTO dto = new ValidityDTO();

                    dto.setPlace(item.get("title").toString().replaceAll("<[^>]*>", ""));
                    dto.setAddrLoad(item.get("roadAddress").toString());
                    dto.setAddrNum(item.get("address").toString());
                    dto.setCategory(item.get("category").toString());
                    dto.setMapx(item.get("mapx").toString());
                    dto.setMapy(item.get("mapy").toString());

                    result.add(dto);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return result;
    }
}
