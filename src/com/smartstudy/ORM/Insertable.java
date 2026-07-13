package com.smartstudy.ORM;

import com.smartstudy.domainModel.BaseModel;

public interface Insertable <T extends BaseModel>{
    T insert(T model);
}
