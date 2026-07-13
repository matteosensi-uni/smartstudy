package com.smartstudy;

import com.smartstudy.db.ConnectionManager;
import com.smartstudy.db.DataBaseInitializer;

public class Main {
    public static void main(String[] args) {
        DataBaseInitializer dbi = new DataBaseInitializer(ConnectionManager.getInstance().getConnection());

        dbi.initializeDatabase();
    }
}
