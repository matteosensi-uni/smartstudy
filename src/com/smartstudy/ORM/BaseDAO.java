package com.smartstudy.ORM;

import java.sql.Connection;

public abstract class BaseDAO {
    protected final Connection conn;
    protected BaseDAO(Connection conn){
        this.conn = conn;
    }
}
