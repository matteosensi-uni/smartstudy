package com.smartstudy.DomainModel;
import java.time.LocalTime;

public class Library extends BaseModel {
    private String name;
    private LocalTime opening_time;
    private LocalTime closing_time;
    private final String street;
    private final String number;
    private final String city;


    public Library(String name, LocalTime opening_time, LocalTime closing_time, String street, String number, String city) {
        super();
        this.name = name;
        this.opening_time = opening_time;
        this.closing_time = closing_time;
        this.street = street;
        this.number = number;
        this.city = city;
    }

    public Library(long id, String name, LocalTime opening_time, LocalTime closing_time, String street, String number, String city) {
        super(id);
        this.name = name;
        this.opening_time = opening_time;
        this.closing_time = closing_time;
        this.street = street;
        this.number = number;
        this.city = city;
    }
    
    public LocalTime getOpening_time() {
        return opening_time;
    }
    public String getName() {
        return name;
    }
    public LocalTime getClosing_time() { return closing_time; }
    public String getStreet() {
        return street;
    }
    public String getNumber() {
        return number;
    }
    public String getCity() {
        return city;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setOpening_time(LocalTime opening_time) {
        this.opening_time = opening_time;
    }
    public void setClosing_time(LocalTime closing_time) {
        this.closing_time = closing_time;
    }
}
