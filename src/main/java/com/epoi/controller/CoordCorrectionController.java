package com.epoi.controller;

import com.epoi.api.EgovJsmApi;
import com.epoi.api.NaverMapApi;
import com.epoi.api.NaverSearchApi;
import com.epoi.service.CoordCorrectionService;
import com.epoi.service.UserSuggestionsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/coordCorrection")
public class CoordCorrectionController {
    private final CoordCorrectionService coordCorrectionService;
    private final EgovJsmApi egovJsmApi;
    private final NaverSearchApi naverSearchApi;
    private final NaverMapApi naverMapApi;

    public CoordCorrectionController(CoordCorrectionService coordCorrectionService, EgovJsmApi egovJsmApi, NaverSearchApi naverSearchApi, NaverMapApi naverMapApi) {
        this.coordCorrectionService = coordCorrectionService;
        this.egovJsmApi = egovJsmApi;
        this.naverSearchApi = naverSearchApi;
        this.naverMapApi = naverMapApi;
    }

    @GetMapping("/get-result")
    public Map<String, Object> getResult(@RequestParam Map<String, String> param) {
        return coordCorrectionService.getResult(param);
    }
}
