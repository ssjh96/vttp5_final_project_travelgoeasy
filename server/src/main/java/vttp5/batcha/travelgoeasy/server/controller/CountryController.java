package vttp5.batcha.travelgoeasy.server.controller;

import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import vttp5.batcha.travelgoeasy.server.model.Country;
import vttp5.batcha.travelgoeasy.server.service.CountryService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;



@RestController
@RequestMapping(path = "/api/countries", produces = MediaType.APPLICATION_JSON_VALUE)
public class CountryController 
{

    @Autowired
    private CountryService countryService;

    @GetMapping("/all")
    public ResponseEntity<String> getAllCountries() 
    {
        JsonArray result = null;
        JsonArrayBuilder jab = Json.createArrayBuilder();

        List<Country> countries = countryService.getAllCountries();

        countries.forEach(c -> jab.add(c.toJson()));
        result = jab.build();

        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(result.toString());
    }
        
}
