package com.epoi.service;

import com.epoi.api.GoogleSheetApi;
import com.epoi.api.NaverSearchApi;
import com.epoi.controller.dto.CoordCorrectionDTO;
import com.epoi.controller.dto.ValidityDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CoordCorrectionService {
    Logger logger = LoggerFactory.getLogger(getClass());

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

    public int getLength(Map<String, String> map) {
        int result = 0;

        if (!map.get("place").isEmpty()) result += validityService.jsonParser(naverSearchApi.searchPlace(map.get("place"))).size();
        if (!map.get("addrLoad").isEmpty()) result += validityService.jsonParser(naverSearchApi.searchPlace(map.get("addrLoad"))).size();
        if (!map.get("addrNum").isEmpty()) result += validityService.jsonParser(naverSearchApi.searchPlace(map.get("addrNum"))).size();

        return result;
    }
}
