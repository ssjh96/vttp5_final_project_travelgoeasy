package vttp5.batcha.travelgoeasy.server.model;

import java.util.Date;

import org.springframework.jdbc.support.rowset.SqlRowSet;

public class Place 
{
    private Integer placeId;
    private Integer tripId;
    private String placeName;
    private String address;
    private Double latitude;
    private Double longitude;
    private Integer dayNumber;
    private Integer orderIndex;
    private Date createdAt;
    
    
   
    public Place() {
    }

    

    public Place(Integer tripId, String placeName, String address, Double latitude, Double longitude, Integer dayNumber,
            Integer orderIndex) {
        this.tripId = tripId;
        this.placeName = placeName;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.dayNumber = dayNumber;
        this.orderIndex = orderIndex;
    }



    public Place(Integer placeId, Integer tripId, String placeName, String address, Double latitude, Double longitude,
            Integer dayNumber, Integer orderIndex, Date createdAt) {
        this.placeId = placeId;
        this.tripId = tripId;
        this.placeName = placeName;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.dayNumber = dayNumber;
        this.orderIndex = orderIndex;
        this.createdAt = createdAt;
    }

    public Integer getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Integer placeId) {
        this.placeId = placeId;
    }

    public Integer getTripId() {
        return tripId;
    }

    public void setTripId(Integer tripId) {
        this.tripId = tripId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(Integer dayNumber) {
        this.dayNumber = dayNumber;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    // HELPER
    public static Place toPlace (SqlRowSet rs) 
    {
        Place place = new Place();

        place.setPlaceId(rs.getInt("place_id"));
        place.setTripId(rs.getInt("trip_id"));
        place.setPlaceName(rs.getString("place_name"));
        place.setAddress(rs.getString("address"));
        place.setLatitude(rs.getDouble("latitude"));
        place.setLongitude(rs.getDouble("longitude"));
        place.setDayNumber(rs.getInt("day_number"));
        place.setOrderIndex(rs.getInt("order_index"));
        place.setCreatedAt(rs.getDate("created_at"));

        return place;
    }
}
