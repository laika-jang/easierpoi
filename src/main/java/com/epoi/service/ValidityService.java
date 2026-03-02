package com.epoi.service;

import com.epoi.api.NaverSearchApi;
import com.epoi.controller.dto.ValidityDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class ValidityService {
    Logger logger = LoggerFactory.getLogger(getClass());

    private final NaverSearchApi naverSearchApi;

    public ValidityService(NaverSearchApi naverSearchApi) {
        this.naverSearchApi = naverSearchApi;
    }

    // 네이버 지역 검색 결과 반환
    public Map<String, Object> getResult(Map<String, String> param) {
        logger.info("getResult works. param: {}", param);
        Map<String, Object> result = new HashMap<>();

        /* 키워드 정제
         * place     : 상호
         * local     : 지역
         * addrLoad  : 도로명 주소
         * addrNum   : 지번 주소
         * */
        Map<String, String> keywords = new HashMap<>();

        keywords.put("place", param.get("place").contains(" ") ? param.get("place").split(" ", 2)[1] : param.get("place"));
        keywords.put("local", getLocal(param.get("addrNum")));
        keywords.put("localAndPlace", (keywords.get("local") + " " + param.get("place")));
        keywords.put("addrLoad", param.get("addrLoad").isEmpty() ? "" : param.get("addrLoad"));
        keywords.put("addrNum", param.get("addrNum"));

        // 지역 + 상호로 검색
        // 관련 장소를 찾지 못한 경우 주소 + 상호로 검색
        Map<String, Object> map = new HashMap<>();
        List<ValidityDTO> dtoList = new ArrayList<>();
        List<String> flagList = new ArrayList<>();

        flagList.add("localAndPlace");
        flagList.add("addrAndPlace");

        for (String flag : flagList) {
            map = getResultList(keywords, flag);

            if (map != null) {
                if ((boolean) map.get("status")) {
                    result.put("status", true);
                    result.put("list", map.get("list"));
                    return result;
                } else {
                    List<ValidityDTO> newItems = (List<ValidityDTO>) map.get("list");

                    // 중복값 제거
                    Set<ValidityDTO> set = new LinkedHashSet<>(dtoList);
                    set.addAll(newItems);

                    dtoList = new ArrayList<>(set);
                }
            }
        }

        result.put("status", false);
        result.put("list", dtoList);

        return result;
    }

    // 검색 결과 목록 정제
    public Map<String, Object> getResultList(Map<String, String> keywords, String flag) {
        Map<String, Object> result = new HashMap<>();
        String keyword = flag.equals("localAndPlace") ?
                keywords.get("local") + " " + keywords.get("place") :
                keywords.get("addrNum") + " " + keywords.get("place");
        boolean hasStatus1 = false;

        // 검색 결과를 java 객체로 변환
        List<ValidityDTO> list = jsonParser(naverSearchApi.searchPlace(keyword));
        List<ValidityDTO> resultList = new ArrayList<>();

        for (ValidityDTO dto : list) {
            // 지역 비교
            boolean isSameLocal = getLocal(dto.getAddrNum()).equals(keywords.get("local"));

            // 상호 비교
            boolean isSamePlace =
                    dto.getPlace().equals(keywords.get("place")) ||
                            dto.getPlace().replaceAll(" ", "").equals(keywords.get("place").replaceAll(" ", ""));

            // 주소 비교
            boolean isSameAddr =
                    !keywords.get("addrLoad").isEmpty() ?
                            getAddrOnly(dto.getAddrLoad()).equals(getAddrOnly(keywords.get("addrLoad"))) :
                            getAddrOnly(dto.getAddrNum()).equals(getAddrOnly(keywords.get("addrNum")));

            // 상태값 저장
            if (isSamePlace && isSameAddr) { // 있어요
                dto.setStatus("1");
                hasStatus1 = true;
                resultList.clear();
                resultList.add(dto);
                break;
            }
            if (isSamePlace) dto.setStatus("3"); // 다른 장소
            if (!isSamePlace && isSameAddr) dto.setStatus("2"); // 상호 확인 필요
            if (!isSamePlace && !isSameAddr) dto.setStatus("4"); // 상호 및 주소 확인 필요

            // 같은 지역인 경우에만 목록에 저장
            if (isSameLocal) resultList.add(dto);
        }

        result.put("status", hasStatus1);
        result.put("list", resultList);

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
                    dto.setStatus("");

                    result.add(dto);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return result;
    }

    // local 추출
    public String getLocal(String addrNum) {
        if (addrNum == null || addrNum.isEmpty()) return "";

        StringBuilder local = new StringBuilder();
        String[] strlist = addrNum.split(" ");

        for (String s : strlist) {
            local.append(s).append(" ");
            if (s.endsWith("동") || s.endsWith("면") || s.endsWith("가")) break;
        }

        return local.toString().trim();
    }

    // addr 추출
    public String getAddrOnly(String addrFull) {
        if (addrFull == null || addrFull.isEmpty()) return "";

        StringBuilder addr = new StringBuilder();
        String[] strlist = addrFull.split(" ");

        for (String s : strlist) {
            addr.append(s).append(" ");
            if (Pattern.matches("^[\\d,-]+$", s)) break;
        }

        return addr.toString().replaceAll(",", "").trim();
    }
}
