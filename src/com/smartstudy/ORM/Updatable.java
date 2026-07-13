package com.smartstudy.ORM;

import com.smartstudy.DomainModel.BaseModel;

public interface Updatable<T extends BaseModel> {
    void update(T model);
}
