package com.smartstudy.ORM;

import com.smartstudy.domainModel.BaseModel;

public interface Updatable<T extends BaseModel> {
    void update(T model);
}
