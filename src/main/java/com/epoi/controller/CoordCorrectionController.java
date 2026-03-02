package com.epoi.controller;

import com.epoi.api.EgovJsmApi;
import com.epoi.api.NaverMapApi;
import com.epoi.api.NaverSearchApi;
import com.epoi.controller.dto.CoordCorrectionDTO;
import com.epoi.service.CoordCorrectionService;
import com.epoi.service.UserSuggestionsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/coord-corr")
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

    @GetMapping("/get-data")
    public List<CoordCorrectionDTO> getData() {
        return coordCorrectionService.setData();
    }

    @GetMapping("/get-length")
    public int getLength(@RequestParam Map<String, String> map) {
        return coordCorrectionService.getLength(map);
    }
}
