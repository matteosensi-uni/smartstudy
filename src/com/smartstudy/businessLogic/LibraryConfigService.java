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

    public ArrayList<String> getAllPolicies(){
        ArrayList<TimePolicy> timePolicies = timePolicyDAO.getAllPolicies();
        ArrayList<String> policiesNames = new ArrayList<>();
        for(TimePolicy timePolicy : timePolicies){
            policiesNames.add(timePolicy.getName());
        }
        return policiesNames;
    }

    public void updateStudyAreaType(long studyAreaId, long adminId, String studyAreaType) {
        StudyArea studyArea = studyAreaDAO.getStudyAreaById(studyAreaId).orElseThrow(()->new BusinessViolationException("Area studio non valida"));
        Admin admin = adminDAO.getAdminById(adminId).orElseThrow(() ->  new BusinessViolationException("Admin non trovato"));
        if(studyAreaType == null || studyAreaType.isBlank())
            throw new  BusinessViolationException("Inserire un tipo valido");
        TransactionManager.executeInTransaction(() -> {
            if (!studyArea.getLibrary().hasAdmin(admin.getId())) {
                throw new BusinessViolationException("L'admin non può modificare questa biblioteca");
            }
            studyArea.changeStudyAreaType(studyAreaType);
            studyAreaDAO.update(studyArea);
        });
    }

    public void updateStudyAreaName(long studyAreaId, long adminId, String name) {
        TransactionManager.executeInTransaction(() -> {
            StudyArea studyArea = studyAreaDAO.getStudyAreaById(studyAreaId).orElseThrow(()->new BusinessViolationException("Area studio non valida"));
            Admin admin = adminDAO.getAdminById(adminId).orElseThrow(() ->  new BusinessViolationException("Admin non trovato"));
            if (!studyArea.getLibrary().hasAdmin(admin.getId())) {
                throw new BusinessViolationException("L'admin non può modificare questa biblioteca");
            }
            studyArea.changeName(name);
            studyAreaDAO.update(studyArea);
        });
    }

    public void updateStudyAreaPolicy(long studyAreaId, long adminId, String name) {
        TransactionManager.executeInTransaction(() -> {
            StudyArea studyArea = studyAreaDAO.getStudyAreaById(studyAreaId).orElseThrow(()->new BusinessViolationException("Area studio non valida"));
            Admin admin = adminDAO.getAdminById(adminId).orElseThrow(() ->  new BusinessViolationException("Admin non trovato"));
            TimePolicy timePolicy = timePolicyDAO.getTimePolicyByName(name).orElseThrow(()->new BusinessViolationException("La regola non è valida"));
            if (!studyArea.getLibrary().hasAdmin(admin.getId())) {
                throw new BusinessViolationException("L'admin non può modificare questa biblioteca");
            }
            studyArea.changePolicy(timePolicy);
            studyAreaDAO.update(studyArea);
        });
    }

    public void updateSeatStatus(long seatId, long adminId, String seatStatus)  {
        TransactionManager.executeInTransaction(() -> {
            Seat seat = seatDAO.getSeatById(seatId).orElseThrow(() -> new BusinessViolationException("Posto non trovato"));
            Admin admin = adminDAO.getAdminById(adminId).orElseThrow(() ->  new BusinessViolationException("Admin non trovato"));
            if(seatStatus == null || seatStatus.isBlank()){
                throw new  BusinessViolationException("Inserire uno stato del posto non vuoto");
            }
            try {
                SeatStatus status = SeatStatus.valueOf(seatStatus);
                if (!seat.getStudyArea().getLibrary().hasAdmin(admin.getId())) {
                    throw new BusinessViolationException("L'admin non può modificare questa biblioteca");
                }
                if(status == SeatStatus.AVAILABLE){
                    seat.repair();
                }else if(status == SeatStatus.BROKEN)
                    seat.markBroken();
                else
                    throw new BusinessViolationException("Inserire uno stato valido");
                seatDAO.update(seat);
            }catch (IllegalArgumentException e){
                throw new  BusinessViolationException("Inserire un stato valido per il posto");
            }
        });
    }

    public void updateSeatType(long seatId, long adminId, String type)  {
        Seat seat = seatDAO.getSeatById(seatId).orElseThrow(() -> new BusinessViolationException("Posto non trovato"));
        Admin admin = adminDAO.getAdminById(adminId).orElseThrow(() ->  new BusinessViolationException("Admin non trovato"));
        TransactionManager.executeInTransaction(() -> {
            if (!seat.getStudyArea().getLibrary().hasAdmin(admin.getId())) {
                throw new BusinessViolationException("L'admin non può modificare questa biblioteca");
            }
            seat.changeSeatType(type);
            seatDAO.update(seat);
        });

    }

    public ArrayList<StudyArea> getLibraryStudyAreasByAdmin(long adminId){
        Library library = libraryDAO.getLibraryByAdmin(adminId).orElseThrow(() -> new BusinessViolationException("Non è stata trovata una biblioteca associata all'admin"));
        return  studyAreaDAO.getLibraryStudyAreas(library.getId());
    }

    public ArrayList<Seat> getLibrarySeatsByAdmin(long adminId){
        Library library = libraryDAO.getLibraryByAdmin(adminId).orElseThrow(() -> new BusinessViolationException("Non è stata trovata una biblioteca associata all'admin"));
        return  seatDAO.getLibrarySeats(library.getId());
    }
}
