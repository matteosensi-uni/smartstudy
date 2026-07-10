package com.smartstudy;

import com.smartstudy.DomainModel.enums.ReservationStatus;
import com.smartstudy.ORM.LibraryDAO;
import com.smartstudy.ORM.UserDAO;
import com.smartstudy.db.ConnectionManager;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        System.out.println(ReservationStatus.valueOf("ACTIVE"));
    }
}
