package com.smartstudy.ORM;

import java.util.Map;

public interface Insertable {
    void insert(Map<String, Object> values);
}
