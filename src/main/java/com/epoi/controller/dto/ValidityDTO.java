package com.epoi.controller.dto;

public class ValidityDTO {
    private String place;
    private String addrLoad;
    private String addrNum;
    private String category;
    private String mapx;
    private String mapy;
    private String status;

    public String getPlace() {
        return place;
    }

    public String getAddrLoad() {
        return addrLoad;
    }

    public String getAddrNum() {
        return addrNum;
    }

    public String getCategory() {
        return category;
    }

    public String getMapx() {
        return mapx;
    }

    public String getMapy() {
        return mapy;
    }

    public String getStatus() {
        return status;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setAddrLoad(String addrLoad) {
        this.addrLoad = addrLoad;
    }

    public void setAddrNum(String addrNum) {
        this.addrNum = addrNum;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setMapx(String mapx) {
        this.mapx = mapx;
    }

    public void setMapy(String mapy) {
        this.mapy = mapy;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
