package vttp5.batcha.travelgoeasy.server.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import vttp5.batcha.travelgoeasy.server.model.Trip;
import vttp5.batcha.travelgoeasy.server.service.TripService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/trips")
public class TripController 
{
    @Autowired
    private TripService tripService;

    // Create Trip
    @PostMapping("/create")
    public ResponseEntity<Integer> createTrip(@RequestBody Trip trip) 
    {
        int tripId = tripService.createTrip(trip);
        if (tripId <= 0)
        {
            return ResponseEntity.badRequest().body(tripId);
        }

        return ResponseEntity.ok(tripId);
    }


    // Get all trips by userId
    @GetMapping("/all-trips/{userId}")
    public ResponseEntity<List<Trip>> getAllTrips(@PathVariable int userId) 
    {
        Optional<List<Trip>> tripListOpt = tripService.getTripsByUserId(userId);

        if (tripListOpt.isEmpty())
        {
            System.out.println("Trip List is empty for userId: " + userId);
            return ResponseEntity.status(404).body(null); // http status code 204
        }

        List<Trip> trips = tripListOpt.get();
        System.out.println("trips: " + trips);
        return ResponseEntity.ok(trips);
    }

     // Get trip by tripId
     @GetMapping("/trip-id/{tripId}")
     public ResponseEntity<Trip> getTripById(@PathVariable int tripId) 
     {
         Optional<Trip> tripOpt = tripService.getTripsById(tripId);
 
         if (tripOpt.isEmpty())
         {
             System.out.println("Trip does not exist for tripId: " + tripId);
             return ResponseEntity.status(404).body(null); // http status not found
         }
 
         Trip trip = tripOpt.get();
         System.out.println("trips: " + trip);
         return ResponseEntity.ok(trip);
     }
 


    // Update trip by tripId
    @PutMapping("update/{tripId}")
    public ResponseEntity<String> updateTripById(@PathVariable int tripId, @RequestBody Trip updatedTrip) 
    {
        Optional<Trip> existingTripOpt = tripService.getTripsById(tripId);

        if(existingTripOpt.isEmpty())
        {
            return ResponseEntity.badRequest().body("No existing trip for given id: " + tripId); 
        }

        updatedTrip.setTripId(tripId);
        boolean bUpdated = tripService.updateTrip(updatedTrip);

        if(!bUpdated)
        {
            return ResponseEntity.badRequest().body("Failed to update trip...");
        }

        return ResponseEntity.ok("Trip updated successfully!");
    }


    // Delete trip by tripId
    @DeleteMapping("/delete/{tripId}")
    public ResponseEntity<Map<String, Boolean>> deleteTrip(@PathVariable int tripId)
    {
        boolean bDeleted = tripService.deleteTrip(tripId);

        if(!bDeleted)
        {
            return ResponseEntity.badRequest().body(null);
        }

        Map<String, Boolean> response = new HashMap<>();
		response.put("deleted", Boolean.TRUE);
		return ResponseEntity.ok(response);
    }
    
}
