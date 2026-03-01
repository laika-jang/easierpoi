package com.epoi.controller.dto;

public class CoordCorrectionDTO {
    String localProfileID;
    String place;
    String addrLoad;
    String addrNum;
    String coordinatesX;
    String coordinatesY;
    String truncatedAddr;
    String geocodeLat;
    String geocodeLon;
    String isCorrected;
    String status;

    public String getLocalProfileID() {
        return localProfileID;
    }

    public String getPlace() {
        return place;
    }

    public String getAddrLoad() {
        return addrLoad;
    }

    public String getAddrNum() {
        return addrNum;
    }

    public String getCoordinatesX() {
        return coordinatesX;
    }

    public String getCoordinatesY() {
        return coordinatesY;
    }

    public String getTruncatedAddr() {
        return truncatedAddr;
    }

    public String getGeocodeLat() {
        return geocodeLat;
    }

    public String getGeocodeLon() {
        return geocodeLon;
    }

    public String getIsCorrected() {
        return isCorrected;
    }

    public String getStatus() {
        return status;
    }

    public void setLocalProfileID(String localProfileID) {
        this.localProfileID = localProfileID;
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

    public void setCoordinatesX(String coordinatesX) {
        this.coordinatesX = coordinatesX;
    }

    public void setCoordinatesY(String coordinatesY) {
        this.coordinatesY = coordinatesY;
    }

    public void setTruncatedAddr(String truncatedAddr) {
        this.truncatedAddr = truncatedAddr;
    }

    public void setGeocodeLat(String geocodeLat) {
        this.geocodeLat = geocodeLat;
    }

    public void setGeocodeLon(String geocodeLon) {
        this.geocodeLon = geocodeLon;
    }

    public void setIsCorrected(String isCorrected) {
        this.isCorrected = isCorrected;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
