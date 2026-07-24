package com.smartstudy.businessLogic;

import com.smartstudy.ORM.*;
import com.smartstudy.domainModel.*;
import com.smartstudy.db.TransactionManager;
import com.smartstudy.domainModel.enums.SeatStatus;
import com.smartstudy.exceptions.BusinessViolationException;

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

    public ArrayList<TimePolicy> getAllPolicies(){
        return timePolicyDAO.getAllPolicies();
    }

    public void updateStudyAreaType(long studyAreaId, long adminId, String studyAreaType) {
        StudyArea studyArea = studyAreaDAO.getStudyAreaById(studyAreaId);
        Admin admin = adminDAO.getAdminById(adminId);
        if(studyAreaType == null || studyAreaType.isBlank())
            throw new  BusinessViolationException("Inserire un tipo valido");
        if(studyArea == null || admin == null){
            throw new  BusinessViolationException("Inserire dei dati validi");
        }
        TransactionManager.executeInTransaction(() -> {
            if (!studyArea.getLibrary().hasAdmin(admin.getId())) {
                throw new BusinessViolationException("L'admin non può modificare questa biblioteca");
            }
            studyArea.changeStudyAreaType(studyAreaType);
            studyAreaDAO.update(studyArea);
        });
    }

    public void updateStudyAreaName(long studyAreaId, long adminId, String name) {
        StudyArea studyArea = studyAreaDAO.getStudyAreaById(studyAreaId);
        Admin admin = adminDAO.getAdminById(adminId);
        if(studyArea == null || admin == null){
            throw new  BusinessViolationException("Inserire dei dati validi");
        }
        TransactionManager.executeInTransaction(() -> {
            if (!studyArea.getLibrary().hasAdmin(admin.getId())) {
                throw new BusinessViolationException("L'admin non può modificare questa biblioteca");
            }
            studyArea.changeName(name);
            studyAreaDAO.update(studyArea);
        });
    }

    public void updateStudyAreaPolicy(long studyAreaId, long adminId, String name) {
        StudyArea studyArea = studyAreaDAO.getStudyAreaById(studyAreaId);
        Admin admin = adminDAO.getAdminById(adminId);
        TimePolicy timePolicy = timePolicyDAO.getTimePolicyByName(name);
        if(studyArea == null || admin == null || timePolicy == null){
            throw new  BusinessViolationException("Inserire dei dati validi");
        }
        TransactionManager.executeInTransaction(() -> {
            if (!studyArea.getLibrary().hasAdmin(admin.getId())) {
                throw new BusinessViolationException("L'admin non può modificare questa biblioteca");
            }
            studyArea.changePolicy(timePolicy);
            studyAreaDAO.update(studyArea);
        });
    }

    public void updateSeatStatus(long seatId, long adminId, String seatStatus)  {
        Seat seat = seatDAO.getSeatById(seatId);
        Admin admin = adminDAO.getAdminById(adminId);
        if(seatStatus == null || seatStatus.isBlank()){
            throw new  BusinessViolationException("Inserire uno stato del posto non vuoto");
        }
        SeatStatus status;
        try {
            status = SeatStatus.valueOf(seatStatus);
        }catch (IllegalArgumentException e){
            throw new  BusinessViolationException("Inserire un stato valido per il posto");
        }
        if(seat == null ||  admin == null){
            throw new BusinessViolationException("Inserire dei dati validi");
        }
        if(status == SeatStatus.UNAVAILABLE){
            throw new BusinessViolationException("Inserire uno stato valido");
        }
        TransactionManager.executeInTransaction(() -> {
            if (!seat.getStudyArea().getLibrary().hasAdmin(admin.getId())) {
                throw new BusinessViolationException("L'admin non può modificare questa biblioteca");
            }
            if(status == SeatStatus.AVAILABLE){
                if(seat.isBroken()){
                    seat.repair();
                } else {
                    seat.free();
                }
            }else if(status == SeatStatus.BROKEN)
                seat.markBroken();
            else
                throw new BusinessViolationException("Inserire uno stato valido");
            seatDAO.update(seat);
        });
    }

    public void updateSeatType(long seatId, long adminId, String type)  {
        Seat seat = seatDAO.getSeatById(seatId);
        Admin admin = adminDAO.getAdminById(adminId);
        if(seat == null ||  admin == null){
            throw new BusinessViolationException("Inserire dei dati validi");
        }
        TransactionManager.executeInTransaction(() -> {
            if (!seat.getStudyArea().getLibrary().hasAdmin(admin.getId())) {
                throw new BusinessViolationException("L'admin non può modificare questa biblioteca");
            }
            seat.changeSeatType(type);
            seatDAO.update(seat);
        });

    }

    public ArrayList<StudyArea> getLibraryStudyAreasByAdmin(long adminId){
        Library library = libraryDAO.getLibraryByAdmin(adminId);
        if(library == null){
            throw new BusinessViolationException("Non è stata trovata una biblioteca associata all'admin");
        }
        return  studyAreaDAO.getLibraryStudyAreas(library.getId());
    }

    public ArrayList<Seat> getLibrarySeatsByAdmin(long adminId){
        Library library = libraryDAO.getLibraryByAdmin(adminId);
        if(library == null){
            throw new BusinessViolationException("Non è stata trovata una biblioteca associata all'admin");
        }
        return  seatDAO.getLibrarySeats(library.getId());
    }
}
