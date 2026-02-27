package com.epoi.service;

import com.epoi.api.NaverSearchApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CoordCorrectionService {
    Logger logger = LoggerFactory.getLogger(getClass());

    private final NaverSearchApi naverSearchApi;

    public CoordCorrectionService(NaverSearchApi naverSearchApi) {
        this.naverSearchApi = naverSearchApi;
    }

    public Map<String, Object> getResult(Map<String, String> param) {
        Map<String, Object> result = new HashMap<>();
        return result;
    }
}
