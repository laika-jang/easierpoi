package com.epoi.controller;

import com.epoi.api.EgovJsmApi;
import com.epoi.api.NaverMapApi;
import com.epoi.api.NaverSearchApi;
import com.epoi.service.UserSuggestionsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/userSuggestions")
public class UserSuggestionsController {
    private final UserSuggestionsService userSuggestionsService;
    private final EgovJsmApi egovJsmApi;
    private final NaverSearchApi naverSearchApi;
    private final NaverMapApi naverMapApi;

    public UserSuggestionsController(UserSuggestionsService userSuggestionsService, EgovJsmApi egovJsmApi, NaverSearchApi naverSearchApi, NaverMapApi naverMapApi) {
        this.userSuggestionsService = userSuggestionsService;
        this.egovJsmApi = egovJsmApi;
        this.naverSearchApi = naverSearchApi;
        this.naverMapApi = naverMapApi;
    }
}
