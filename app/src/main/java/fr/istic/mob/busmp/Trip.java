package fr.istic.mob.busmp;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Trip {

    @NonNull
    @PrimaryKey
    private String trip_id;
    private String route_id;
    private String service_id;
    private String trip_headsign;
    private String trip_short_name;
    private String direction_id;
    private String block_id;
    private String shape_id;
    private String wheelchair_accessible;
    private String bikes_allowed;

    public Trip(String route_id, String service_id, String trip_id, String trip_headsign, String trip_short_name, String direction_id, String block_id, String shape_id, String wheelchair_accessible, String bikes_allowed) {
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

    public Trip(String[] attributes){
        this.route_id = attributes[0].replace("\"","");
        this.service_id = attributes[1].replace("\"","");
        this.trip_id = attributes[2].replace("\"","");
        this.trip_headsign = attributes[3].replace("\"","");
        this.trip_short_name = attributes[4].replace("\"","");
        this.direction_id = attributes[5].replace("\"","");
        this.block_id = attributes[6].replace("\"","");
        this.shape_id = attributes[7].replace("\"","");
        this.wheelchair_accessible = attributes[8].replace("\"","");
        this.bikes_allowed = attributes[9].replace("\"","");
    }

    public String getRoute_id() {
        return route_id;
    }

    public void setRoute_id(String route_id) {
        this.route_id = route_id;
    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
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

    public String getDirection_id() {
        return direction_id;
    }

    public void setDirection_id(String direction_id) {
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

    public String getWheelchair_accessible() {
        return wheelchair_accessible;
    }

    public void setWheelchair_accessible(String wheelchair_accessible) {
        this.wheelchair_accessible = wheelchair_accessible;
    }

    public String getBikes_allowed() {
        return bikes_allowed;
    }

    public void setBikes_allowed(String bikes_allowed) {
        this.bikes_allowed = bikes_allowed;
    }








}
