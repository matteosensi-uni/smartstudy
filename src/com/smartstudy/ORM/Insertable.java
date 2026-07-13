package com.smartstudy.ORM;

import com.smartstudy.DomainModel.BaseModel;

import java.util.Map;

public interface Insertable <T extends BaseModel>{
    Long insert(T model);
}
