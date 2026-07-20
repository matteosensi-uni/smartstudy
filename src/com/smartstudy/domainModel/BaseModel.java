package com.smartstudy.domainModel;

import com.smartstudy.exceptions.DomainViolationException;

public abstract class BaseModel {
    private long id;

    BaseModel(){ }
    BaseModel(long id){
        if(id <= 0){
            throw new DomainViolationException("Inserire un id valido");
        }
        this.id = id;
    }

    public final long getId() {return id;}
}
