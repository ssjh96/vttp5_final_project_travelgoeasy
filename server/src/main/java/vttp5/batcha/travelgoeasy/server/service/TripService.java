package vttp5.batcha.travelgoeasy.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vttp5.batcha.travelgoeasy.server.model.Trip;
import vttp5.batcha.travelgoeasy.server.repository.TripRepository;

@Service
public class TripService 
{
    @Autowired
    private TripRepository tripRepository;    

    // CREATE
    public Integer createTrip (Trip trip)
    {
        System.out.println("Creating Trip for " + trip.getTripName());

        int tripId = tripRepository.createTrip(trip);
        return tripId;
    }


    // READ
    public Optional<List<Trip>> getTripsByUserId(int userId)
    {
        return tripRepository.getTripsByUserId(userId);
    }

    public Optional<Trip> getTripsById(int tripId)
    {
        return tripRepository.getTripsByTripId(tripId);
    }


    // UPDATE
    public boolean updateTrip(Trip trip)
    {
        int rowsUpdated = tripRepository.updateTrip(trip);
        
        if(rowsUpdated < 1)
        {
            return false;
        }

        return true;
    }
    
    
    // DELETE TRIP
    public boolean deleteTrip (int tripId)
    {
        int rowsDeleted = tripRepository.deleteTrip(tripId);

        if(rowsDeleted < 1)
        {
            return false;
        }

        return true;

    }
}
