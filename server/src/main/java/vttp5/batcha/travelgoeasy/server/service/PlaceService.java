package vttp5.batcha.travelgoeasy.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vttp5.batcha.travelgoeasy.server.model.Place;
import vttp5.batcha.travelgoeasy.server.repository.PlaceRepository;

@Service
public class PlaceService 
{
    @Autowired
    private PlaceRepository placeRepository;

    // CREATE
    public void createItinerary (List<Place> itinerary) throws Exception
    {
        try {
            for(Place place : itinerary)
            { 
                placeRepository.createPlace(place);
            }   
        } catch (Exception e) {
            throw new Exception ("Error creating itinerary...");
        }
        
    }

     // READ
    public Optional<List<Place>> getPlacesByTripId(int tripId)
    {
        return placeRepository.getPlacesByTripId(tripId);
    }
    
    
    // DELETE TRIP
    public boolean deleteItinerary (int tripId) throws Exception
    {
        try {
            int rowsDeleted = placeRepository.deleteItinerary(tripId);
            return rowsDeleted > 1;
        } catch (Exception e) {
            throw new Exception ("Error deleting itinerary...");
        }
        
    }

}
