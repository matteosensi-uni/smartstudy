package com.smartstudy.ORM;

import java.sql.Connection;

public abstract class BaseDAO {
    final Connection conn;
    BaseDAO(Connection conn){
        this.conn = conn;
    }
}
