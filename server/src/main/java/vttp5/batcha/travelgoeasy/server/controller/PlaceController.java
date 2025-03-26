package vttp5.batcha.travelgoeasy.server.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import vttp5.batcha.travelgoeasy.server.model.Place;
import vttp5.batcha.travelgoeasy.server.service.PlaceService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/places")
public class PlaceController 
{
    @Autowired
    private PlaceService placeService;

    // Create Itinerary if place not in,
    // Update Itinerary if place inside
    @PutMapping("create/{tripId}")
    public ResponseEntity<String> updateItinerary(@PathVariable String tripId, @RequestBody List<Place> itinerary) 
    {
        try {
            if(itinerary.size() == 0)
            {
                return ResponseEntity.badRequest().body("error - Itinerary is empty..");
            }

            // Delete trip - initial got no trip
            placeService.deleteItinerary(Integer.parseInt(tripId));

            // Recreate trip
            placeService.createItinerary(itinerary);
            System.out.println("Itinerary created");
            
            return ResponseEntity.ok(Json.createObjectBuilder().add("message", "successfully created itinerary").build().toString());
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Json.createObjectBuilder().add("error", "error: " + e.getMessage()).build().toString()); // status 500
        }
    }


    // Get all places by tripId (Itinerary)
    @GetMapping("/get-all/{tripId}")
    public ResponseEntity<List<Place>> getItinerary(@PathVariable String tripId) 
    {
        Optional<List<Place>> placeListOpt = placeService.getPlacesByTripId(Integer.parseInt(tripId));

        if (placeListOpt.isEmpty())
        {
            System.out.println("Itinerary is empty for tripId: " + tripId);
            return ResponseEntity.status(404).body(null); // http status code 404
        }

        List<Place> places = placeListOpt.get();
        System.out.println("places: " + places);
        return ResponseEntity.ok(places);
    } 
}
