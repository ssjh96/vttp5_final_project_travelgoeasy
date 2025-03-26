package vttp5.batcha.travelgoeasy.server.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import vttp5.batcha.travelgoeasy.server.model.Place;

@Repository
public class PlaceRepository 
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // CREATE
    public static final String SQL_CREATE_ITINERARY = """
            INSERT INTO places 
                (trip_id, place_name, address, latitude, longitude, day_number, order_index)
            VALUES
                (?, ?, ?, ?, ?, ?, ?)
            """;

    public int createPlace(Place place)
    {
        int rowCreated = jdbcTemplate.update(SQL_CREATE_ITINERARY, 
                                place.getTripId(), place.getPlaceName(), 
                                place.getAddress(), place.getLatitude(),
                                place.getLongitude(), place.getDayNumber(),
                                place.getOrderIndex());
        return rowCreated;
    }
    
    
    
    // READ 
    public static final String SQL_GET_PLACES_BY_TRIP_ID = "SELECT * FROM places WHERE trip_id = ?";

    public Optional<List<Place>> getPlacesByTripId(int tripId) 
    {
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_GET_PLACES_BY_TRIP_ID, tripId);
        List<Place> places = new ArrayList<>();

        while(rs.next())
        {
            places.add(Place.toPlace(rs));
        }

        if(places.size() == 0)
        {
            return Optional.empty();
        }

        return Optional.of(places);
    }



    // DELETE by trip_id (Whole itinerary)
    public static final String SQL_DELETE_BY_TRIP_ID = "DELETE FROM places WHERE trip_id = ?";

    public int deleteItinerary(int tripId)
    {
        return jdbcTemplate.update(SQL_DELETE_BY_TRIP_ID, tripId);
    }


}
