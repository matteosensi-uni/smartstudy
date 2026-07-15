package com.smartstudy.businessLogic;

import com.smartstudy.ORM.*;
import com.smartstudy.domainModel.*;
import com.smartstudy.db.TransactionManager;
import com.smartstudy.domainModel.enums.SeatStatus;
import com.smartstudy.domainModel.enums.SeatType;
import com.smartstudy.domainModel.enums.StudyAreaType;
import com.smartstudy.exceptions.BusinessViolationException;
import com.smartstudy.exceptions.DataAccessException;
import com.smartstudy.exceptions.DomainViolationException;

import java.sql.SQLException;
import java.util.ArrayList;

public class LibraryConfigService {
    private final SeatDAO seatDAO;
    private final StudyAreaDAO studyAreaDAO;
    private final AdminDAO adminDAO;
    private final LibraryDAO libraryDAO;
    private final TimePolicyDAO timePolicyDAO;

    public LibraryConfigService(SeatDAO seatDAO, StudyAreaDAO studyAreaDAO, AdminDAO adminDAO, LibraryDAO libraryDAO, TimePolicyDAO timePolicyDAO) {
        this.seatDAO = seatDAO;
        this.studyAreaDAO = studyAreaDAO;
        this.adminDAO = adminDAO;
        this.libraryDAO = libraryDAO;
        this.timePolicyDAO = timePolicyDAO;
    }

    public TimePolicy getStudyAreaTimePolicy(long studyAreaId) {
        StudyArea studyArea = studyAreaDAO.getStudyAreaById(studyAreaId);
        if(studyArea ==  null){
            throw new  BusinessViolationException("Inserire dei dati validi");
        }
        return timePolicyDAO.getTimePolicyById(studyArea.getTimePolicyId());
    }

    public ArrayList<TimePolicy> getAllPolicies(){
        return timePolicyDAO.getAllPolicies();
    }

    public StudyArea updateStudyAreaType(long studyAreaId, long adminId, StudyAreaType  studyAreaType) {
        StudyArea studyArea = studyAreaDAO.getStudyAreaById(studyAreaId);
        Admin admin = adminDAO.getAdminById(adminId);
        if(studyArea == null || admin == null){
            throw new  BusinessViolationException("Inserire dei dati validi");
        }
        return TransactionManager.executeInTransaction(() -> {
            if (!adminDAO.existsById(admin.getId())) {
                throw new IllegalArgumentException("L'admin non è riconosciuto dal sistema");
            }
            if (admin.getLibraryId() != studyArea.getLibraryId()) {
                throw new BusinessViolationException("L'admin non può modificare questa biblioteca");
            }
            studyArea.changeStudyAreaType(studyAreaType);
            studyAreaDAO.update(studyArea);
            return studyArea;
        });
    }

    public StudyArea updateStudyAreaName(long studyAreaId, long adminId, String name) {
        StudyArea studyArea = studyAreaDAO.getStudyAreaById(studyAreaId);
        Admin admin = adminDAO.getAdminById(adminId);
        if(studyArea == null || admin == null){
            throw new  BusinessViolationException("Inserire dei dati validi");
        }
        return TransactionManager.executeInTransaction(() -> {
            if (!adminDAO.existsById(admin.getId())) {
                throw new IllegalArgumentException("L'admin non è riconosciuto dal sistema");
            }
            if (admin.getLibraryId() != studyArea.getLibraryId()) {
                throw new BusinessViolationException("L'admin non può modificare questa biblioteca");
            }
            studyArea.setName(name);
            studyAreaDAO.update(studyArea);
            return studyArea;
        });
    }

    public StudyArea updateStudyAreaPolicy(long studyAreaId, long adminId, String name) {
        StudyArea studyArea = studyAreaDAO.getStudyAreaById(studyAreaId);
        Admin admin = adminDAO.getAdminById(adminId);
        TimePolicy timePolicy = timePolicyDAO.getTimePolicyByName(name);
        if(studyArea == null || admin == null || timePolicy == null){
            throw new  BusinessViolationException("Inserire dei dati validi");
        }
        return TransactionManager.executeInTransaction(() -> {
            if (!adminDAO.existsById(admin.getId())) {
                throw new IllegalArgumentException("L'admin non è riconosciuto dal sistema");
            }
            if (admin.getLibraryId() != studyArea.getLibraryId()) {
                throw new BusinessViolationException("L'admin non può modificare questa biblioteca");
            }
            studyArea.changePolicy(timePolicy.getId());
            studyAreaDAO.update(studyArea);
            return studyArea;
        });
    }

    public Seat updateSeatStatus(long seatId, long adminId, SeatStatus status)  {
        Seat seat = seatDAO.getSeatById(seatId);
        Admin admin = adminDAO.getAdminById(adminId);
        if(seat == null ||  admin == null){
            throw new BusinessViolationException("Inserire dei dati validi");
        }
        if(status == null || status == SeatStatus.UNAVAILABLE){
            throw new BusinessViolationException("Inserire uno stato valido");
        }
        return TransactionManager.executeInTransaction(() -> {
            if (!adminDAO.existsById(admin.getId())) {
                throw new IllegalArgumentException("L'admin non è riconosciuto dal sistema");
            }
            if (admin.getLibraryId() != libraryDAO.getLibraryBySeat(seat.getId()).getId()) {
                throw new BusinessViolationException("L'admin non può modificare questa biblioteca");
            }
            if(status == SeatStatus.AVAILABLE){
                seat.free();
            }else if(status == SeatStatus.BROKEN)
                seat.markBroken();
            else
                throw new  BusinessViolationException("Inserire uno stato valido");
            seatDAO.update(seat);
            return seat;
        });
    }

    public Seat updateSeatType(long seatId, long adminId, SeatType type)  {
        Seat seat = seatDAO.getSeatById(seatId);
        Admin admin = adminDAO.getAdminById(adminId);
        if(seat == null ||  admin == null){
            throw new BusinessViolationException("Inserire dei dati validi");
        }
        return TransactionManager.executeInTransaction(() -> {
            if (!adminDAO.existsById(admin.getId())) {
                throw new IllegalArgumentException("L'admin non è riconosciuto dal sistema");
            }
            if (admin.getLibraryId() != libraryDAO.getLibraryBySeat(seat.getId()).getId()) {
                throw new BusinessViolationException("L'admin non può modificare questa biblioteca");
            }
            seat.changeSeatType(type);
            seatDAO.update(seat);
            return seat;
        });

    }

    public ArrayList<StudyArea> getLibraryStudyAreas(long libraryId){
        Library library = libraryDAO.getLibraryById(libraryId);
        if(library == null){
            throw new BusinessViolationException("Inserire una libreria valida");
        }
        return  studyAreaDAO.getLibraryStudyAreas(libraryId);
    }

    public ArrayList<Seat> getLibrarySeats(long libraryId){
        Library library = libraryDAO.getLibraryById(libraryId);
        if(library == null){
            throw new BusinessViolationException("Inserire una libreria valida");
        }
        return  seatDAO.getLibrarySeats(libraryId);
    }

    public int getBrokenSeats(long libraryId){
        Library library = libraryDAO.getLibraryById(libraryId);
        if(library == null){
            throw new BusinessViolationException("Inserire una libreria valida");
        }
        return seatDAO.countBrokenSeatsByLibrary(libraryId);
    }
    public int getAvailableSeats(long libraryId){
        Library library = libraryDAO.getLibraryById(libraryId);
        if(library == null){
            throw new BusinessViolationException("Inserire una libreria valida");
        }
        return seatDAO.countAvailableSeatsByLibrary(libraryId);
    }
    public int getOccupiedSeats(long libraryId){
        Library library = libraryDAO.getLibraryById(libraryId);
        if(library == null){
            throw new BusinessViolationException("Inserire una libreria valida");
        }
        return seatDAO.countOccupiedSeatsByLibrary(libraryId);
    }
}
