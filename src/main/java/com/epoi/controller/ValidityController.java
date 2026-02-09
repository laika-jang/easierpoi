package com.epoi.controller;

import com.epoi.service.ValidityService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/validity")
public class ValidityController {
    private final ValidityService validityService;

    public ValidityController(ValidityService validityService) {
        this.validityService = validityService;
    }

    @GetMapping("/get-result")
    public Map<String, String> getResult(@RequestParam Map<String, String> param) {
        return validityService.getResult(param);
    }
}
