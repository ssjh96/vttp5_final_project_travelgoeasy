package vttp5.batcha.travelgoeasy.server.model;

import java.util.Date;

import org.springframework.jdbc.support.rowset.SqlRowSet;

public class Trip 
{
    private Integer tripId; // auto incr
    private String userId;
    private String tripName;
    private String destination; 
    private Date startDate;
    private Date endDate;
    private Date createdAt;
    private String tripMates; // CSV emails

    // CONSTRUCTORS
    public Trip() {
    }
    
    public Trip(String userId, String tripName, String destination, Date startDate, Date endDate, Date createdAt,
            String tripMates) {
        this.userId = userId;
        this.tripName = tripName;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.tripMates = tripMates;
    } 

    public Trip(Integer tripId, String userId, String tripName, String destination, Date startDate, Date endDate,
            Date createdAt, String tripMates) {
        this.tripId = tripId;
        this.userId = userId;
        this.tripName = tripName;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.tripMates = tripMates;
    }

    
    // GETTERS SETTERS
    public Integer getTripId() {
        return tripId;
    }

    public void setTripId(Integer tripId) {
        this.tripId = tripId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getTripMates() {
        return tripMates;
    }

    public void setTripMates(String tripMates) {
        this.tripMates = tripMates;
    }


    // HELPER
    public static Trip toTrip(SqlRowSet rs)
    {
        Trip trip = new Trip();

        trip.setTripId(rs.getInt("trip_id"));
        trip.setUserId(rs.getString("user_id"));
        trip.setTripName(rs.getString("trip_name"));
        trip.setDestination(rs.getString("destination"));
        trip.setStartDate(rs.getDate("start_date"));
        trip.setEndDate(rs.getDate("end_date"));
        trip.setTripMates(rs.getString("trip_mates"));
        trip.setCreatedAt(rs.getDate("created_at"));

        return trip;
    }
    
}
