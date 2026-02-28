package com.epoi.service;

import com.epoi.api.GoogleSheetApi;
import com.epoi.api.NaverSearchApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoordCorrectionService {
    Logger logger = LoggerFactory.getLogger(getClass());

    private final NaverSearchApi naverSearchApi;
    private final GoogleSheetApi googleSheetApi;

    public CoordCorrectionService(NaverSearchApi naverSearchApi, GoogleSheetApi googleSheetApi) {
        this.naverSearchApi = naverSearchApi;
        this.googleSheetApi = googleSheetApi;
    }

    public List<List<Object>> getData() {
        String sheetId = "1R2XHSDi8Dnpwv5_iyDbwaVweNnV-jydsZeoUo2dSHwM";
        String range = "Sheet1!A2:N";

        try {
            return googleSheetApi.getSheetData(sheetId, range);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
