package com.smartstudy.domainModel;
import com.smartstudy.exceptions.DomainViolationException;

import java.time.LocalTime;
import java.util.ArrayList;

public class Library extends BaseModel {
    private final String name;
    private final LocalTime openingTime;
    private final LocalTime closingTime;
    private final String street;
    private final String number;
    private final String city;
    private final ArrayList<Admin> libraryAdmins;

    private Library(long id, String name, LocalTime openingTime, LocalTime closingTime, String street, String number, String city, ArrayList<Admin> libraryAdmins) {
        super(id);
        if(name == null || name.isBlank()){
            throw new DomainViolationException("Il nome non può essere vuoto");
        }
        if(street == null || street.isBlank()){
            throw new DomainViolationException("La strada non può essere vuoto");
        }
        if(number == null || number.isBlank()){
            throw new DomainViolationException("Il numero non può essere vuoto");
        }
        if(city == null || city.isBlank()){
            throw new DomainViolationException("La città non può essere vuota");
        }
        if(openingTime == null || closingTime == null) {
            throw new DomainViolationException("I tempi di apertura e chiusura della biblioteca non possono essere nulli");
        }
        if(libraryAdmins == null || libraryAdmins.isEmpty()){
            throw new DomainViolationException("La biblioteca deve avere degli admin");
        }
        this.name = name;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.street = street;
        this.number = number;
        this.city = city;
        this.libraryAdmins = new ArrayList<>(libraryAdmins);
    }
    public static Library valueOf(long id, String name, LocalTime openingTime, LocalTime closingTime, String street, String number, String city, ArrayList<Admin> libraryAdmins) {
        return new Library(id, name, openingTime, closingTime, street, number, city, libraryAdmins);
    }

    public static Library copy(Library library) {
        return new Library(library.getId(), library.getName(), library.getOpeningTime(), library.getClosingTime(), library.getStreet(), library.getNumber(), library.getCity(), library.getLibraryAdmins());
    }

    public LocalTime getOpeningTime() {return openingTime;}
    public String getName() {return name;}
    public LocalTime getClosingTime() {return closingTime;}
    public String getStreet() {return street;}
    public String getNumber() {return number;}
    public String getCity() {return city;}

    public ArrayList<Admin> getLibraryAdmins() {
        ArrayList<Admin> res = new ArrayList<>();
        for(Admin admin : libraryAdmins){
            res.add(Admin.copy(admin));
        }
        return res;
    }

    public boolean isOpen(){
        LocalTime now = LocalTime.now();
        return now.isAfter(openingTime) &&  now.isBefore(closingTime);
    }

    public boolean hasAdmin(long id){
        for(Admin admin : libraryAdmins){
            if(admin.getId() == id){
                return true;
            }
        }
        return false;
    }
}