package com.smartstudy.DomainModel;

public abstract class BaseModel {
    private long id;

    public BaseModel(){
    }

    public BaseModel(long id){
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
