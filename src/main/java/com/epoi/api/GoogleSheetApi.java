package com.epoi.api;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class GoogleSheetApi {

    public List<List<Object>> getSheetData(String sheetId, String range) throws Exception {
        // JSON 키 파일을 이용해 인증
        GoogleCredentials credentials = ServiceAccountCredentials.fromStream(
                        new ClassPathResource("static/key/epoi-488801-c2f80eed9058.json").getInputStream())
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS_READONLY));

        String APPLICATION_NAME = "epoi";
        Sheets service = new Sheets.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();

        // 시트 범위 지정
        ValueRange response = service.spreadsheets().values()
                .get(sheetId, range)
                .execute();

        return response.getValues();
    }
}
