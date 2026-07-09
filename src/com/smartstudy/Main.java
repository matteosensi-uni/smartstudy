package com.smartstudy;

import com.smartstudy.ORM.LibraryDAO;
import com.smartstudy.ORM.UserDAO;

public class Main {
    public static void main(String[] args) {
        System.out.println(LibraryDAO.selectById(2).getName());
    }
}
