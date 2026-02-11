package com.epoi.service;

import com.epoi.api.NaverSearchApiConfig;
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

    public final NaverSearchApiConfig naverSearchApiConfig;

    public ValidityService(NaverSearchApiConfig naverSearchApiConfig) {
        this.naverSearchApiConfig = naverSearchApiConfig;
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
        keywords.put("place", param.get("place").split(" ", 2)[1]);
        keywords.put("placeAndAddr", (param.get("addrNum").split(" ")[0] + " " + param.get("addrNum").split(" ")[1] + " " + param.get("place")));
        keywords.put("addrLoad", param.get("addrLoad").isEmpty() ? "" : param.get("addrLoad"));
        keywords.put("addrNum", param.get("addrNum"));
        keywords.put("local", param.get("addrNum").split(" ")[0] + " " + param.get("addrNum").split(" ")[1] + " " + param.get("addrNum").split(" ")[2]);
        logger.info("keywords: " + keywords);

        // 검색 결과를 java 객체로 변환
        List<ValidityDTO> list = jsonParser(naverSearchApiConfig.searchPlace(param.get("place")));
        List<ValidityDTO> resultList = new ArrayList<>();

        for (ValidityDTO dto : list) {
            logger.info("dto: place " + dto.getPlace() + ", addrLoad " + dto.getAddrLoad() + ", addrNum " + dto.getAddrNum() + ", category " + dto.getCategory());

            // 상호와 도로명 주소 또는 지번 주소가 검색어와 같은 경우
            if (
                    (dto.getPlace().equals(keywords.get("place")) && dto.getAddrLoad().contains(keywords.get("addrLoad"))) ||
                    (dto.getPlace().equals(keywords.get("place")) && dto.getAddrNum().contains(keywords.get("addrNum")))
            ) {
                resultList.clear();
                result.put("msg", msgFind);
                break;
            }

            // 상호와 지역은 동일하나 상세 주소가 다른 경우
            if (dto.getPlace().equals(keywords.get("place")) && dto.getAddrNum().contains(keywords.get("local"))) {
                resultList.clear();
                resultList.add(dto);
                result.put("msg", msgSimilar);
                break;
            }

            // 상호와 도로명 주소 또는 지번 주소가 같지 않을 경우
            resultList.add(dto);
        }

        if (!result.containsKey("msg")) result.put("msg", msgNotFind);
        if (!resultList.isEmpty()) result.put("list", list);

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
