package com.epoi.service;

import com.epoi.api.GoogleSheetApi;
import com.epoi.api.NaverSearchApi;
import com.epoi.controller.dto.CoordCorrectionDTO;
import com.epoi.controller.dto.ValidityDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CoordCorrectionService {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${naver.map.api.client-id}") // 환경변수에서 Access Key ID
    private String accessKey;

    private final NaverSearchApi naverSearchApi;
    private final GoogleSheetApi googleSheetApi;
    private final ValidityService validityService;

    public CoordCorrectionService(NaverSearchApi naverSearchApi, GoogleSheetApi googleSheetApi, ValidityService validityService) {
        this.naverSearchApi = naverSearchApi;
        this.googleSheetApi = googleSheetApi;
        this.validityService = validityService;
    }

    public List<List<Object>> getData() {
        String sheetId = "1R2XHSDi8Dnpwv5_iyDbwaVweNnV-jydsZeoUo2dSHwM";
        String range = "Sheet1!A2:Q";

        try {
            return googleSheetApi.getSheetData(sheetId, range);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<CoordCorrectionDTO> setData() {
        List<CoordCorrectionDTO> result = new ArrayList<>();
        List<List<Object>> lawData = getData();

        for (List<Object> row : lawData) {
            CoordCorrectionDTO dto = new CoordCorrectionDTO();

            if (row.size() == 15 || row.size() == 16) {
                String coord = row.get(5).toString().trim();
                coord = coord.replaceAll("POINT\\(", "").replaceAll("\\)", "");
                String[] coordSet = coord.split(" ");

                dto.setLocalProfileID((String) row.get(0));
                dto.setPlace((String) row.get(2));
                dto.setAddrLoad((String) row.get(4));
                dto.setAddrNum((String) row.get(3));
                dto.setCoordinatesX(coordSet[0]);
                dto.setCoordinatesY(coordSet[1]);
                dto.setTruncatedAddr((String) row.get(6));
                dto.setGeocodeLat((String) row.get(7));
                dto.setGeocodeLon((String) row.get(8));
                dto.setCategory((String) row.get(10));
                dto.setIsCorrected((String) row.get(14));
                if (row.size() == 15) dto.setStatus("");
                if (row.size() == 16) dto.setStatus((String) row.get(15));

                result.add(dto);
            } else {
                dto.setPlace("행의 일부 값이 올바르지 않습니다.");
            }
        }

        return result;
    }

    public String getKey() {
        return accessKey;
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

        keywords.put("place", param.get("place"));
        keywords.put("local", validityService.getLocal(param.get("addrNum")));
        keywords.put("localAndPlace", (keywords.get("local") + " " + param.get("place")));
        keywords.put("addrLoad", param.get("addrLoad").isEmpty() ? "" : validityService.getAddrOnly(param.get("addrLoad")));
        keywords.put("addrNum",  param.get("addrNum").isEmpty() ? "" : validityService.getAddrOnly(param.get("addrNum")));
        logger.info("key: {}", keywords);

        // 지역 + 상호로 검색
        // 관련 장소를 찾지 못한 경우 주소 + 상호로 검색
        Map<String, Object> map = new HashMap<>();
        List<ValidityDTO> dtoList = new ArrayList<>();
        List<String> flagList = new ArrayList<>();

        flagList.add("localAndPlace");
        flagList.add("addrAndPlace");

        for (String flag : flagList) {
            map = validityService.getResultList(keywords, flag);

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

    public int getSearchResultLength(Map<String, String> map) {
        int result = 0;

        if (!map.get("place").isEmpty()) result += validityService.jsonParser(naverSearchApi.searchPlace(map.get("place"))).size();
        if (!map.get("addrLoad").isEmpty()) result += validityService.jsonParser(naverSearchApi.searchPlace(map.get("addrLoad"))).size();
        if (!map.get("addrNum").isEmpty()) result += validityService.jsonParser(naverSearchApi.searchPlace(map.get("addrNum"))).size();
        if (!map.get("addrTruncated").isEmpty()) result += validityService.jsonParser(naverSearchApi.searchPlace(map.get("addrTruncated"))).size();

        return result;
    }
}
