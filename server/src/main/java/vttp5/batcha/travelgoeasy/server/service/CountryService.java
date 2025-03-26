package vttp5.batcha.travelgoeasy.server.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp5.batcha.travelgoeasy.server.model.Country;

@Service
public class CountryService 
{
    public static final String countriesUrl = "https://restcountries.com/v3.1";

    public List<Country> getAllCountries()
    {
        String url = countriesUrl + "/all";
        System.out.println(url);

        List<Country> countries = new ArrayList<>();

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);

        JsonReader jReader = Json.createReader(new StringReader(response));
        JsonArray jCountriesArray = jReader.readArray();

        for(int i = 0; i < jCountriesArray.size(); i++)
        {
            Country country = new Country();
            JsonObject jCountry = jCountriesArray.getJsonObject(i);

            if (jCountry.containsKey("name") && jCountry.getJsonObject("name").containsKey("common")) {
                country.setName(jCountry.getJsonObject("name").getString("common", ""));
            } else {
                country.setName("");
            }

            if (jCountry.containsKey("cca2")) {
                country.setCode(jCountry.getString("cca2", ""));
            } else {
                country.setCode("");
            }

            if (jCountry.containsKey("capitalInfo") && jCountry.getJsonObject("capitalInfo").containsKey("latlng")) {
                JsonArray latlngArray = jCountry.getJsonObject("capitalInfo").getJsonArray("latlng");
                country.setLat(latlngArray.getInt(0));
                country.setLng(latlngArray.getInt(1));
            } else {
                country.setLat(0);
                country.setLng(0);
            }

            if (jCountry.containsKey("maps") && jCountry.getJsonObject("maps").containsKey("googleMaps")) {
                country.setGoogleMapsUrl(jCountry.getJsonObject("maps").getString("googleMaps"));
            } else {
                country.setGoogleMapsUrl("");
            }

            if (jCountry.containsKey("flags") && jCountry.getJsonObject("flags").containsKey("png")) {
                country.setPngUrl(jCountry.getJsonObject("flags").getString("png"));
            } else {
                country.setPngUrl("");
            }

            if (jCountry.containsKey("flags") && jCountry.getJsonObject("flags").containsKey("alt")) {
                country.setPngAlt(jCountry.getJsonObject("flags").getString("alt"));
            } else {
                country.setPngAlt("");
            }
            
            // country.setName(jCountry.getJsonObject("name").getString("common", ""));
            // country.setLat(jCountry.getJsonObject("capitalInfo").getJsonArray("latlng").getInt(0));
            // country.setLng(jCountry.getJsonObject("capitalInfo").getJsonArray("latlng").getInt(1));
            // country.setGoogleMapsUrl(jCountry.getJsonObject("maps").getString("googleMaps", ""));
            // country.setPngUrl(jCountry.getJsonObject("flags").getString("png", ""));
            // country.setPngAlt(jCountry.getJsonObject("flags").getString("alt", ""));

            countries.add(country);
        }
        
        return countries;
    }

}
