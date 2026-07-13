package com.smartstudy.domainModel;

public abstract class BaseModel {
    private long id;

    BaseModel(){ }
    BaseModel(long id){
        this.id = id;
    }

    public final long getId() {return id;}
    public final void setId(long id) {this.id = id;}
}
