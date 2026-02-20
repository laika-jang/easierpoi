package com.epoi.service;

import com.epoi.api.NaverMapApi;
import com.epoi.api.NaverSearchApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserSuggestionsService {
    Logger logger = LoggerFactory.getLogger(getClass());

    private final NaverSearchApi naverSearchApi;

    public UserSuggestionsService(NaverSearchApi naverSearchApi, NaverMapApi naverMapApi) {
        this.naverSearchApi = naverSearchApi;
    }
}
