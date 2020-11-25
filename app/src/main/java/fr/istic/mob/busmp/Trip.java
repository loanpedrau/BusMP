package fr.istic.mob.busmp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Trip {

    private int route_id;
    private int service_id;
    @PrimaryKey
    private int trip_id;
    private String trip_headsign;
    private String trip_short_name;
    private int direction_id;
    private String block_id;
    private String shape_id;
    private int wheelchair_accessible;
    private String bikes_allowed;

    public Trip(int route_id, int service_id, int trip_id, String trip_headsign, String trip_short_name, int direction_id, String block_id, String shape_id, int wheelchair_accessible, String bikes_allowed) {
        this.route_id = route_id;
        this.service_id = service_id;
        this.trip_id = trip_id;
        this.trip_headsign = trip_headsign;
        this.trip_short_name = trip_short_name;
        this.direction_id = direction_id;
        this.block_id = block_id;
        this.shape_id = shape_id;
        this.wheelchair_accessible = wheelchair_accessible;
        this.bikes_allowed = bikes_allowed;
    }

    public int getRoute_id() {
        return route_id;
    }

    public void setRoute_id(int route_id) {
        this.route_id = route_id;
    }

    public int getService_id() {
        return service_id;
    }

    public void setService_id(int service_id) {
        this.service_id = service_id;
    }

    public int getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(int trip_id) {
        this.trip_id = trip_id;
    }

    public String getTrip_headsign() {
        return trip_headsign;
    }

    public void setTrip_headsign(String trip_headsign) {
        this.trip_headsign = trip_headsign;
    }

    public String getTrip_short_name() {
        return trip_short_name;
    }

    public void setTrip_short_name(String trip_short_name) {
        this.trip_short_name = trip_short_name;
    }

    public int getDirection_id() {
        return direction_id;
    }

    public void setDirection_id(int direction_id) {
        this.direction_id = direction_id;
    }

    public String getBlock_id() {
        return block_id;
    }

    public void setBlock_id(String block_id) {
        this.block_id = block_id;
    }

    public String getShape_id() {
        return shape_id;
    }

    public void setShape_id(String shape_id) {
        this.shape_id = shape_id;
    }

    public int getWheelchair_accessible() {
        return wheelchair_accessible;
    }

    public void setWheelchair_accessible(int wheelchair_accessible) {
        this.wheelchair_accessible = wheelchair_accessible;
    }

    public String getBikes_allowed() {
        return bikes_allowed;
    }

    public void setBikes_allowed(String bikes_allowed) {
        this.bikes_allowed = bikes_allowed;
    }








}
