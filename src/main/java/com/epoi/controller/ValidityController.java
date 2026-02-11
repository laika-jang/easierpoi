package com.epoi.controller;

import com.epoi.api.EgovJsmApiConfig;
import com.epoi.service.ValidityService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/validity")
public class ValidityController {
    private final ValidityService validityService;
    private final EgovJsmApiConfig egovJsmApiConfig;

    public ValidityController(ValidityService validityService, EgovJsmApiConfig egovJsmApiConfig) {
        this.validityService = validityService;
        this.egovJsmApiConfig = egovJsmApiConfig;
    }

    @GetMapping("/get-addr-num")
    public String getAddrNum(@RequestParam Map<String, String> param) {
        return egovJsmApiConfig.getAddrNum(param.get("addr"));
    }

    @GetMapping("/get-result")
    public Map<String, Object> getResult(@RequestParam Map<String, String> param) {
        return validityService.getResult(param);
    }
}
