package fr.istic.mob.busmp;

import androidx.room.Entity;

@Entity
public class Route {

    private int route_id;
    private int agency_id;
    private String route_short_name;
    private String route_long_name;
    private String route_desc;
    private int route_type;
    private String route_url;
    private String route_color;
    private String route_text_color;
    private int route_sort_order;


    public Route(int route_id, int agency_id, String route_short_name, String route_long_name,
                 String route_desc, int route_type, String route_url, String route_color,
                 String route_text_color, int route_sort_order) {
        this.route_id = route_id;
        this.agency_id = agency_id;
        this.route_short_name = route_short_name;
        this.route_long_name = route_long_name;
        this.route_desc = route_desc;
        this.route_type = route_type;
        this.route_url = route_url;
        this.route_color = route_color;
        this.route_text_color = route_text_color;
        this.route_sort_order = route_sort_order;
    }

    public int getRoute_id() {
        return route_id;
    }

    public void setRoute_id(int route_id) {
        this.route_id = route_id;
    }

    public int getAgency_id() {
        return agency_id;
    }

    public void setAgency_id(int agency_id) {
        this.agency_id = agency_id;
    }

    public String getRoute_short_name() {
        return route_short_name;
    }

    public void setRoute_short_name(String route_short_name) {
        this.route_short_name = route_short_name;
    }

    public String getRoute_long_name() {
        return route_long_name;
    }

    public void setRoute_long_name(String route_long_name) {
        this.route_long_name = route_long_name;
    }

    public String getRoute_desc() {
        return route_desc;
    }

    public void setRoute_desc(String route_desc) {
        this.route_desc = route_desc;
    }

    public int getRoute_type() {
        return route_type;
    }

    public void setRoute_type(int route_type) {
        this.route_type = route_type;
    }

    public String getRoute_url() {
        return route_url;
    }

    public void setRoute_url(String route_url) {
        this.route_url = route_url;
    }

    public String getRoute_color() {
        return route_color;
    }

    public void setRoute_color(String route_color) {
        this.route_color = route_color;
    }

    public String getRoute_text_color() {
        return route_text_color;
    }

    public void setRoute_text_color(String route_text_color) {
        this.route_text_color = route_text_color;
    }

    public int getRoute_sort_order() {
        return route_sort_order;
    }

    public void setRoute_sort_order(int route_sort_order) {
        this.route_sort_order = route_sort_order;
    }

}
