package com.smartstudy.DomainModel;
import java.time.LocalDateTime;

public class Library extends BaseModel {
    private String name;
    private LocalDateTime opening_time;
    private LocalDateTime closing_time;
    private String street;
    private String number;
    private String city;

    public Library(long id, String name, LocalDateTime opening_time, LocalDateTime closing_time, String street, String number, String city) {
        super(id);
        this.name = name;
        this.opening_time = opening_time;
        this.closing_time = closing_time;
        this.street = street;
        this.number = number;
        this.city = city;
    }

    public LocalDateTime getOpening_time() {
        return opening_time;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getClosing_time() {
        return closing_time;
    }

    public String getStreet() {
        return street;
    }

    public String getNumber() {
        return number;
    }

    public String getCity() {
        return city;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOpening_time(LocalDateTime opening_time) {
        this.opening_time = opening_time;
    }

    public void setClosing_time(LocalDateTime closing_time) {
        this.closing_time = closing_time;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
