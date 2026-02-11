package com.epoi.trashbin;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.util.*;

//@Service
public class ValidityServiceEarlierVersion {
    // (selenmium) 네이버 지도 검색 결과 반환
    public Map<String, Object> getResult(Map<String, String> param) {
        Map<String, Object> result = new HashMap<>();

        // 메시지
        String msgFind = "있어요 (O)";
        String msgNotFind = "없어요 (X)";
        String msgSimilar = "[보류] 장소가 달라요";
        String msgError1 = "error: 데이터 추출 중 예외가 발생했어요. (01: 반환값이 없음)";
        String msgError2 = "error: 데이터 추출 중 예외가 발생했어요. (02: 드라이버 동작 과정에서 예외 발생)";

        /* 키워드 정제
         * place       : 상호
         * placeAndAddr: 지역 + 상호 (지역은 시·구 혹은 도·군 단위로 한정)
         * addrLoad    : 도로명 주소 (지번 주소가 인입되는 경우가 있으므로, 도로명 주소인지 여부 확인 후 값 추가)
         * addrNum     : 지번 주소
         * local       : 지역
         * */
        Map<String, String> keywords = new HashMap<>();
        keywords.put("place", param.get("place").split(" ", 2)[1]);
        keywords.put("placeAndAddr", (
                !param.get("addrLoad").isEmpty() ?
                        param.get("addrLoad").split(" ")[0] + " " + param.get("addrLoad").split(" ")[1] + " " + param.get("place") :
                        param.get("addrNum").split(" ")[0] + " " + param.get("addrNum").split(" ")[1] + " " + param.get("place"))
        );
        keywords.put("addrLoad", (!param.get("addrLoad").isEmpty() && distAddr(param.get("addrLoad"))) ? removeSameWord(param.get("addrLoad")) : "");
        keywords.put("addrNum", !param.get("addrNum").isEmpty() ? removeSameWord(param.get("addrNum")) : "");
        keywords.put("local", param.get("addrNum").split(" ")[0] + " " + param.get("addrNum").split(" ")[1] + " " + param.get("addrNum").split(" ")[2]);

        // Selenium 드라이버 설정
        ChromeOptions options = new ChromeOptions();
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64;r x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        options.addArguments("--proxy-server='direct://'");
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        // options.addArguments("--disable-gpu"); // GPU 가속 해제 (리소스 절약)
        options.addArguments("--blink-settings=imagesEnabled=false"); // 이미지 로딩 방지 (속도 향상)
        options.addArguments("--proxy-bypass-list=*");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-infobars");
        options.addArguments("--page-load-strategy=eager"); // DOM이 생성되면 바로 실행

        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver(options);

        try {
            // 1. 지역 + 상호 검색
            result = searchPlace(driver, keywords, "place");

            if ("find".equals(result.get("find"))) result.put("msg", msgFind);
            if ("similar".equals(result.get("find"))) result.put("msg", msgSimilar);
            if ("notFind".equals(result.get("find"))) {
                // 2. 주소 + 상호 검색
                result = searchPlace(driver, keywords, "addrAndPlace");

                if ("find".equals(result.get("find"))) {
                    result.put("msg", msgFind);
                } else {
                    result.put("msg", msgNotFind);
                    result.put("list", searchPlace(driver, keywords, "addr"));
                }
            }

            // 예외 발생시
            if (result.isEmpty()) {
                result.put("msg", msgError1);
                result.put("result", "error1");
            }
        } catch (Exception e) {
            result.put("msg", msgError2);
            result.put("result", "error2");
            result.put("log", e.getMessage());
        } finally {
            driver.quit();
        }

        return result;
    }

    // 검색 결과를 목록으로 저장
    public Map<String, Object> searchPlace(WebDriver driver, Map<String, String> param, String flag) throws UnsupportedEncodingException {
        // 검색 결과가 있을 경우       : 키워드와 검색 결과 대조
        // => 동일한 장소를 찾았을 경우    : 작업 종료 및 결과 반환
        // => 동일한 장소를 찾지 못했을 경우: 다음 페이지로 전환
        //    => 마지막 페이지일 경우          : 작업 종료 및 결과 반환

        Map<String, Object> result = new HashMap<>();

        // 검색 관련 변수
        String searchUrl = "https://pcmap.place.naver.com/place/list?query=";
        String keywords = "";

        if ("place".equals(flag)) keywords = param.get("placeAndAddr");
        if ("addrAndPlace".equals(flag)) keywords = param.get("addrLoad").isEmpty() ? param.get("addrLoad") + " " + param.get("place") : param.get("addrNum") + " " + param.get("place");
        if ("addr".equals(flag)) keywords = param.get("addrLoad").isEmpty() ? param.get("addrLoad") : param.get("addrNum");

        // 검색 결과 관련 변수
        boolean samePlace = false;     // 같은 장소를 찾았을 경우 true
        boolean diffAddr = false;      // 상호가 같지만 주소는 다른 장소를 찾았을 경우 true
        boolean nextPageExist = false; // 다음 페이지가 있을 경우 true
        List<WebElement> elemList = new ArrayList<>();
        List<Map<String, String>> resultList = new ArrayList<>();

        driver.get(searchUrl + java.net.URLEncoder.encode(keywords, "UTF-8"));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#app-root")));

        // 검색 결과가 있는 경우
        if (!driver.findElements(By.cssSelector("#_pcmap_list_scroll_container")).isEmpty()) {
            do {
                WebElement elemNavNext = driver.findElement(By.cssSelector(".zRM9F .eUTV2:last-child"));
                elemList = driver.findElements(By.cssSelector("#_pcmap_list_scroll_container li"));
                nextPageExist = !Objects.requireNonNull(elemNavNext.getDomAttribute("aria-disabled")).equals("true");

                // 키워드와 검색 결과 대조
                for (WebElement elem : elemList) {
                    Map<String, String> search = new HashMap<>();

                    // 상호 저장
                    search.put("place", elem.findElement(By.cssSelector(".place_bluelink span")).getText());

                    // 주소 저장
                    elem.findElement(By.cssSelector(".uFxr1")).click();

                    String[] addrFormer = elem.findElement(By.cssSelector(".suKMR")).getText().split(" ");
                    StringBuilder addrFormerCut = new StringBuilder();

                    for (int i = 0; i < (addrFormer.length - 1); i++) addrFormerCut.append(addrFormer[i]).append(" ");
                    search.put("addrLoad", addrFormerCut + elem.findElement(By.cssSelector(".TvoI6")).getText());
                    search.put("addrNum", addrFormerCut + elem.findElement(By.cssSelector(".zZfO1:nth-child(2) .TvoI6")).getText());
                    search.put("local", elem.findElement(By.cssSelector(".suKMR")).getText());

                    // 상호와 도로명 주소 또는 지번 주소가 검색어와 같은 경우 반복문 종료
                    if (
                            (search.get("place").equals(param.get("place")) && search.get("addrLoad").contains(param.get("addrLoad"))) ||
                                    (search.get("place").equals(param.get("place")) && search.get("addrNum").contains(param.get("addrNum")))
                    ) {
                        samePlace = true;
                        break;
                    }

                    // 상호와 지역은 동일하나 상세 주소가 다른 경우 반복문 종료
                    if (search.get("place").equals(param.get("place")) && search.get("local").equals(param.get("local"))) {
                        diffAddr = true;
                        resultList.clear();
                        resultList.add(search);
                        break;
                    }

                    // 상호와 도로명 주소 또는 지번 주소가 같지 않을 경우 resultList에 map 저장
                    resultList.add(search);
                }

                // 다음 페이지 열기
                if (nextPageExist) {
                    elemNavNext.click();
                }
            } while (!(samePlace || diffAddr) && nextPageExist); // 동일한 장소를 찾지 못한 상태에서 다음 페이지가 존재하는 경우 do문 반복
        }

        if (samePlace) {
            result.put("find", "find");
        } else if (diffAddr) {
            result.put("find", "similar");
            result.put("list", resultList);
        } else {
            result.put("find", "notFind");
            result.put("list", resultList.isEmpty() ? "" : resultList);
        }

        return result;
    }

    // 중복 단어 제거
    public String removeSameWord (String strOrig) {
        String[] list = strOrig.split(" ");
        StringBuilder result = new StringBuilder();

        result.append(list[0]);
        for (int i = 1; i < list.length; i++) {
            if (!list[i - 1].equals(list[i])) result.append(" ").append(list[i]);
        }

        return result.toString();
    }

    // 도로명주소/지번주소 구별 (도로명주소일 경우 true 반환)
    public boolean distAddr (String addr) {
        boolean result = false;
        String[] list =  addr.split(" ");

        if (list[2].endsWith("로") || list[2].endsWith("길") || list[3].endsWith("로") || list[3].endsWith("길")) result = true;

        return result;
    }
}
