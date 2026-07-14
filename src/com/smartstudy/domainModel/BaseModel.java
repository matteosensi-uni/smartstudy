package com.smartstudy.domainModel;

import com.smartstudy.exceptions.DomainViolationException;

public abstract class BaseModel {
    private long id;

    BaseModel(){ }
    BaseModel(long id){
        checkId(id,"");
        this.id = id;
    }

    void checkId(long id, String entityName){
        if(id <= 0){
            throw new DomainViolationException("Id dell'entità "+ entityName +" errato");
        }
    }

    public final long getId() {return id;}
}
