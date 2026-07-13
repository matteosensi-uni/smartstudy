package com.smartstudy.domainModel;
import java.time.LocalTime;

public class Library extends BaseModel {
    private final String name;
    private final LocalTime openingTime;
    private final LocalTime closingTime;
    private final String street;
    private final String number;
    private final String city;


    private Library(long id, String name, LocalTime openingTime, LocalTime closingTime, String street, String number, String city) {
        super(id);
        this.name = name;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.street = street;
        this.number = number;
        this.city = city;
    }
    public static Library valueOf(long id, String name, LocalTime openingTime, LocalTime closingTime, String street, String number, String city) {
        return new Library(id, name, openingTime, closingTime, street, number, city);
    }

    public LocalTime getOpeningTime() {return openingTime;}
    public String getName() {return name;}
    public LocalTime getClosingTime() {return closingTime;}
    public String getStreet() {return street;}
    public String getNumber() {return number;}
    public String getCity() {return city;}

}