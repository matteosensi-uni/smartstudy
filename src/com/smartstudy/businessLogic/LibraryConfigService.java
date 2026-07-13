package com.smartstudy.businessLogic;

import com.smartstudy.domainModel.Admin;
import com.smartstudy.domainModel.Seat;
import com.smartstudy.domainModel.StudyArea;
import com.smartstudy.ORM.AdminDAO;
import com.smartstudy.ORM.LibraryDAO;
import com.smartstudy.ORM.SeatDAO;
import com.smartstudy.ORM.StudyAreaDAO;
import com.smartstudy.db.TransactionManager;
import com.smartstudy.exceptions.BusinessViolationException;
import com.smartstudy.exceptions.DataAccessException;

import java.sql.SQLException;
import java.util.ArrayList;

public class LibraryConfigService {
    private final SeatDAO seatDAO;
    private final StudyAreaDAO studyAreaDAO;
    private final AdminDAO adminDAO;
    private final LibraryDAO libraryDAO;

    public LibraryConfigService(SeatDAO seatDAO, StudyAreaDAO studyAreaDAO, AdminDAO adminDAO, LibraryDAO libraryDAO) {
        this.seatDAO = seatDAO;
        this.studyAreaDAO = studyAreaDAO;
        this.adminDAO = adminDAO;
        this.libraryDAO = libraryDAO;
    }

    public StudyArea updateStudyArea(long studyAreaId, long adminId) {
        StudyArea studyArea = studyAreaDAO.getStudyAreaById(studyAreaId);
        Admin admin = adminDAO.getAdminById(adminId);
        if(studyArea == null || admin == null){
            throw new  BusinessViolationException("Inserire dei dati validi");
        }
        TransactionManager.executeInTransaction(() -> {
            if (!adminDAO.existsById(admin.getId())) {
                throw new IllegalArgumentException("L'admin non è riconosciuto dal sistema");
            }
            if (admin.getLibraryId() != studyArea.getLibraryId()) {
                throw new BusinessViolationException("L'admin non può modificare questa biblioteca");
            }
            studyAreaDAO.update(studyArea);
        });
        return studyArea;
    }

    public Seat updateSeat(long seatId, long adminId)  {
        Seat seat = seatDAO.getSeatById(seatId);
        Admin admin = adminDAO.getAdminById(adminId);
        if(seat == null ||  admin == null){
            throw new BusinessViolationException("Inserire dei dati validi");
        }
        TransactionManager.executeInTransaction(() -> {
            if (!adminDAO.existsById(admin.getId())) {
                throw new IllegalArgumentException("L'admin non è riconosciuto dal sistema");
            }
            if (admin.getLibraryId() != libraryDAO.getLibraryBySeat(seat.getId()).getId()) {
                throw new BusinessViolationException("L'admin non può modificare questa biblioteca");
            }
            seatDAO.update(seat);
        });
        return seat;
    }

    public ArrayList<StudyArea> getLibraryStudyAreas(long libraryId){
        return  studyAreaDAO.getLibraryStudyAreas(libraryId);
    }

    public ArrayList<Seat> getLibrarySeats(long libraryId){
        return  seatDAO.getLibrarySeats(libraryId);
    }
}
