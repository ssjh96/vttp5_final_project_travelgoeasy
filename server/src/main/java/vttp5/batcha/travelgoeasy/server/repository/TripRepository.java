package vttp5.batcha.travelgoeasy.server.repository;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import vttp5.batcha.travelgoeasy.server.model.Trip;

@Repository
public class TripRepository 
{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    // CREATE
    public static final String SQL_CREATE_TRIP = """
            INSERT INTO trips 
                (user_id, trip_name, destination, start_date, end_date, trip_mates)
            VALUES
                (? ,?, ?, ?, ?, ?)
            """;

    public int createTrip(Trip trip)
    {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator psc = new PreparedStatementCreator() {

            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(SQL_CREATE_TRIP, new String[]{"trip_id"});

                // Need to convert java.util.date to java.sql.date
                java.util.Date startDate = trip.getStartDate();
                java.util.Date endDate = trip.getEndDate();
                
                java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
                java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());

                ps.setString(1, trip.getUserId());
                ps.setString(2, trip.getTripName());
                ps.setString(3, trip.getDestination());
                ps.setDate(4, sqlStartDate);
                ps.setDate(5, sqlEndDate);
                ps.setString(6, trip.getTripMates());

                return ps;
            }
        };

        // Execute insertion and capture generated key (trip_id)
        jdbcTemplate.update(psc, keyHolder);
        
        // Pri key trip_id is in key holder
        int tripId = keyHolder.getKey().intValue();
        return tripId;
    }
    
    
    // READ 
    public static final String SQL_GET_TRIPS_BY_USER_ID = "SELECT * FROM trips WHERE user_id = ?";

    public Optional<List<Trip>> getTripsByUserId(int userId) 
    {
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_GET_TRIPS_BY_USER_ID, userId);
        List<Trip> trips = new ArrayList<>();

        while(rs.next())
        {
            trips.add(Trip.toTrip(rs));
        }

        if(trips.size() == 0)
        {
            return Optional.empty();
        }

        return Optional.of(trips);
    }



    public static final String SQL_GET_TRIP_BY_TRIP_ID = "SELECT * FROM trips WHERE trip_id = ?";

    public Optional<Trip> getTripsByTripId(int tripId)
    {
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_GET_TRIP_BY_TRIP_ID, tripId);

        if(!rs.next())
        {
            return Optional.empty();
        }

        Trip trip = Trip.toTrip(rs);
        return Optional.of(trip);
    }



    // UPDATE
    public static final String SQL_UPDATE_TRIP = """
            UPDATE trips SET 
                trip_name = ?,
                destination = ?,
                start_date = ?,
                end_date = ?,
                trip_mates =? 
            WHERE
                trip_id = ?
            """;

    public int updateTrip (Trip trip)
    {
        return jdbcTemplate.update(SQL_UPDATE_TRIP, trip.getTripName(), 
                                                    trip.getDestination(), 
                                                    trip.getStartDate(), 
                                                    trip.getEndDate(), 
                                                    trip.getTripMates(), 
                                                    trip.getTripId());
    }



    // DELETE
    public static final String SQL_DELETE_TRIP = "DELETE FROM trips WHERE trip_id = ?";

    public int deleteTrip(int tripId)
    {
        return jdbcTemplate.update(SQL_DELETE_TRIP, tripId);
    }

    

    public static final String SQL_GET_TRIP_COUNT = "SELECT COUNT(*) FROM trips WHERE user_id = ?";

    public Integer countTrips(Integer userId)
    {
        return jdbcTemplate.queryForObject(SQL_GET_TRIP_COUNT, Integer.class, userId);
    }

}
