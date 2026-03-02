package com.epoi.controller;

import com.epoi.service.CoordCorrectionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class CommonController {
    private final CoordCorrectionService coordCorrectionService;

    public CommonController(CoordCorrectionService coordCorrectionService) {
        this.coordCorrectionService = coordCorrectionService;
    }

    @GetMapping ("/")
    public ModelAndView index(ModelAndView mav) {
        mav.setViewName("index");
        mav.addObject("ncpClientId", coordCorrectionService.getKey());
        return mav;
    }
}
