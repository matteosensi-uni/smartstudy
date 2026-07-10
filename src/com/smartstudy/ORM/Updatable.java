package com.smartstudy.ORM;

import java.util.Map;

public interface Updatable {
    void update(Map<String, Object> values, long id);
}
