package com.smartstudy.domainModel;
import com.smartstudy.exceptions.DomainViolationException;

import java.time.LocalTime;
import java.util.ArrayList;

public class Library extends BaseModel {
    private  String name;
    private  LocalTime openingTime;
    private  LocalTime closingTime;
    private  String street;
    private  String number;
    private  String city;
    private  ArrayList<Admin> libraryAdmins;

    private Library(long id, String name, LocalTime openingTime, LocalTime closingTime, String street, String number, String city, ArrayList<Admin> libraryAdmins) {
        super(id);
        setName(name);
        setOpeningTime(openingTime);
        setClosingTime(closingTime);
        setStreet(street);
        setNumber(number);
        setCity(city);
        setLibraryAdmins(libraryAdmins);
    }
    public static Library valueOf(long id, String name, LocalTime openingTime, LocalTime closingTime, String street, String number, String city, ArrayList<Admin> libraryAdmins) {
        return new Library(id, name, openingTime, closingTime, street, number, city, libraryAdmins);
    }

    public static Library copy(Library library) {
        return new Library(library.getId(), library.getName(), library.getOpeningTime(), library.getClosingTime(), library.getStreet(), library.getNumber(), library.getCity(), library.getLibraryAdmins());
    }

    private void setName(String name) {
        if(name == null || name.isBlank()){
            throw new DomainViolationException("Il nome della biblioteca è vuoto");
        }
        this.name = name;
    }

    private void setOpeningTime(LocalTime openingTime) {
        if(openingTime == null){
            throw new DomainViolationException("L'orario di apertura della biblioteca è nullo");
        }
        this.openingTime = openingTime;
    }

    private void setClosingTime(LocalTime closingTime) {
        if(closingTime == null){
            throw new DomainViolationException("L'orario di chiusura della biblioteca è nullo");
        }
        this.closingTime = closingTime;
    }

    private void setStreet(String street) {
        if(street == null || street.isBlank()){
            throw new DomainViolationException("La strada della biblioteca è vuota");
        }
        this.street = street;
    }

    private void setNumber(String number) {
        if(number == null || number.isBlank()){
            throw new DomainViolationException("Il della biblioteca numero è vuoto");
        }
        this.number = number;
    }

    private void setCity(String city) {
        if(city == null || city.isBlank()){
            throw new DomainViolationException("La della biblioteca città è vuota");
        }
        this.city = city;
    }

    private void setLibraryAdmins(ArrayList<Admin> libraryAdmins) {
        if(libraryAdmins == null || libraryAdmins.isEmpty()){
            throw new DomainViolationException("La biblioteca deve avere degli admin");
        }
        this.libraryAdmins = new ArrayList<>();
        for(Admin admin : libraryAdmins){
            this.libraryAdmins.add(Admin.copy(admin));
        }
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
        if(openingTime.equals(closingTime)) return true;
        if(closingTime.isBefore(openingTime)){
            return now.isAfter(openingTime) || now.isBefore(closingTime);
        }
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