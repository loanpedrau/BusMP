package fr.istic.mob.busmp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Calendar {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String service_id ;
    private int monday;
    private int tuesday;
    private int wednesday;
    private int thursday;
    private int friday;
    private int saturday;
    private int sunday;
    private String start_date;
    private String end_date;

    public Calendar(String service_id, int monday, int tuesday, int wednesday, int thursday, int friday, int saturday, int sunday, String start_date, String end_date) {
        this.service_id = service_id;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
        this.start_date = start_date;
        this.end_date = end_date;
    }

    public Calendar(String[] attributes){
        this.service_id = attributes[0].replace("\"","");
        this.monday = Integer.parseInt(attributes[1].replace("\"",""));
        this.tuesday = Integer.parseInt(attributes[2].replace("\"",""));
        this.wednesday = Integer.parseInt(attributes[3].replace("\"",""));
        this.thursday = Integer.parseInt(attributes[4].replace("\"",""));
        this.friday = Integer.parseInt(attributes[5].replace("\"",""));
        this.saturday = Integer.parseInt(attributes[6].replace("\"",""));
        this.sunday = Integer.parseInt(attributes[7].replace("\"",""));
        this.start_date = attributes[8].replace("\"","");
        this.end_date = attributes[9].replace("\"","");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public int getMonday() {
        return monday;
    }

    public void setMonday(int monday) {
        this.monday = monday;
    }

    public int getTuesday() {
        return tuesday;
    }

    public void setTuesday(int tuesday) {
        this.tuesday = tuesday;
    }

    public int getWednesday() {
        return wednesday;
    }

    public void setWednesday(int wednesday) {
        this.wednesday = wednesday;
    }

    public int getThursday() {
        return thursday;
    }

    public void setThursday(int thursday) {
        this.thursday = thursday;
    }

    public int getFriday() {
        return friday;
    }

    public void setFriday(int friday) {
        this.friday = friday;
    }

    public int getSaturday() {
        return saturday;
    }

    public void setSaturday(int saturday) {
        this.saturday = saturday;
    }

    public int getSunday() {
        return sunday;
    }

    public void setSunday(int sunday) {
        this.sunday = sunday;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }



}
