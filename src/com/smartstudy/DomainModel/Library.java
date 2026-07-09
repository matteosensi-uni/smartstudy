package com.smartstudy.DomainModel;
import java.time.LocalTime;

public class Library extends BaseModel {
    private String name;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private final String street;
    private final String number;
    private final String city;


    public Library(String name, LocalTime openingTime, LocalTime closingTime, String street, String number, String city) {
        super();
        this.name = name;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.street = street;
        this.number = number;
        this.city = city;
    }

    public Library(long id, String name, LocalTime openingTime, LocalTime closingTime, String street, String number, String city) {
        super(id);
        this.name = name;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.street = street;
        this.number = number;
        this.city = city;
    }

    public LocalTime getOpeningTime() {
        return openingTime;
    }
    public String getName() {
        return name;
    }
    public LocalTime getClosingTime() { return closingTime; }
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
    public void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }
    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }
}