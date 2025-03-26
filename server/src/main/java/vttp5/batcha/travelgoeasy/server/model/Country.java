package vttp5.batcha.travelgoeasy.server.model;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class Country 
{
    private String name;
    private String code;
    private double lat;
    private double lng;
    private String googleMapsUrl;
    private String pngUrl;
    private String pngAlt;
    
    public Country() {
    }

    public Country(String name, String code, double lat, double lng, String googleMapsUrl, String pngUrl,
            String pngAlt) {
        this.name = name;
        this.code = code;
        this.lat = lat;
        this.lng = lng;
        this.googleMapsUrl = googleMapsUrl;
        this.pngUrl = pngUrl;
        this.pngAlt = pngAlt;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getCode() {
        return code;
    }


    public void setCode(String code) {
        this.code = code;
    }


    public double getLat() {
        return lat;
    }


    public void setLat(double lat) {
        this.lat = lat;
    }


    public double getLng() {
        return lng;
    }


    public void setLng(double lng) {
        this.lng = lng;
    }


    public String getGoogleMapsUrl() {
        return googleMapsUrl;
    }


    public void setGoogleMapsUrl(String googleMapsUrl) {
        this.googleMapsUrl = googleMapsUrl;
    }


    public String getPngUrl() {
        return pngUrl;
    }


    public void setPngUrl(String pngUrl) {
        this.pngUrl = pngUrl;
    }


    public String getPngAlt() {
        return pngAlt;
    }


    public void setPngAlt(String pngAlt) {
        this.pngAlt = pngAlt;
    }



    // Helper
    public JsonObject toJson()
    {
        return Json.createObjectBuilder()
                .add("name", getName())
                .add("code", getCode())
                .add("lat", getLat())
                .add("lng", getLng())
                .add("googleMapsUrl", getGoogleMapsUrl())
                .add("pngUrl", getPngUrl())
                .add("pngAlt", pngAlt)
                .build();
    }

    



}
