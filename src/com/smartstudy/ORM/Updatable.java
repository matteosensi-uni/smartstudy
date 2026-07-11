package com.smartstudy.ORM;

import com.smartstudy.DomainModel.BaseModel;

import java.util.Map;

public interface Updatable<T extends BaseModel> {
    void update(T model);
}
