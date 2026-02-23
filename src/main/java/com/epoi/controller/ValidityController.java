package com.epoi.controller;

import com.epoi.api.EgovJsmApi;
import com.epoi.api.NaverMapApi;
import com.epoi.service.ValidityService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/validity")
public class ValidityController {
    private final ValidityService validityService;
    private final EgovJsmApi egovJsmApi;
    private final NaverMapApi naverMapApi;

    public ValidityController(ValidityService validityService, EgovJsmApi egovJsmApi, NaverMapApi naverMapApi) {
        this.validityService = validityService;
        this.egovJsmApi = egovJsmApi;
        this.naverMapApi = naverMapApi;
    }

    @GetMapping("/get-addr")
    public Map<String, String> getAddr(@RequestParam Map<String, String> param) {
        return egovJsmApi.getAddr(param.get("addr"));
    }

    @GetMapping("/get-result")
    public Map<String, Object> getResult(@RequestParam Map<String, String> param) {
        return validityService.getResult(param);
    }

    @GetMapping("/get-code")
    public String getCode(@RequestParam Map<String, String> param) {
        return naverMapApi.geocode(param.get("query"));
    }

    @GetMapping("/get-img")
    public byte[] getImg(@RequestParam Map<String, String> param) {
        return naverMapApi.raster(param);
    }
}
