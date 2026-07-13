package com.smartstudy.ORM;

import com.smartstudy.domainModel.BaseModel;

public interface Insertable <T extends BaseModel>{
    Long insert(T model);
}
