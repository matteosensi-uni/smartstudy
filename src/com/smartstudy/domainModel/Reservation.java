package com.smartstudy.domainModel;
import com.smartstudy.domainModel.enums.ReservationStatus;
import com.smartstudy.exceptions.DomainViolationException;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Reservation extends BaseModel{
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ReservationStatus status;
    private ArrayList<TemporaryLeave> temporaryLeaves;
    private ArrayList<AbandonmentReport> abandonmentReports;
    private Seat seat;
    private AccessSession session;

    private Reservation(AccessSession session, Seat seat) {
        super();
        setSeat(seat);
        setSession(session);
        setStartTime(LocalDateTime.now());
        setStatus(ReservationStatus.ACTIVE);
        setTemporaryLeaves(new ArrayList<>());
        setAbandonmentReports(new ArrayList<>());
    }

    private Reservation(long id, LocalDateTime startTime, LocalDateTime endTime, ReservationStatus status, ArrayList<TemporaryLeave> temporaryLeaves, ArrayList<AbandonmentReport> abandonmentReports, AccessSession session, Seat seat) {
        super(id);
        setStartTime(startTime);
        setEndTime(endTime);
        setStatus(status);
        setTemporaryLeaves(temporaryLeaves);
        setAbandonmentReports(abandonmentReports);
        setSession(session);
        setSeat(seat);
    }

    public static Reservation valueOf(long id, LocalDateTime startTime, LocalDateTime endTime, ReservationStatus status, ArrayList<TemporaryLeave> temporaryLeaves, AccessSession session, Seat seat, ArrayList<AbandonmentReport> reports){
        return new Reservation(id, startTime, endTime, status, temporaryLeaves,reports, session, seat);
    }

    public static Reservation start(AccessSession session, Seat seat){
        if(seat != null) seat.occupy();
        return new Reservation(session, seat);
    }

    private void setSeat(Seat seat) {
        if(seat == null){
            throw new DomainViolationException("Posto della prenotazione nullo");
        }
        this.seat = Seat.copy(seat);
    }

    private void setAbandonmentReports(ArrayList<AbandonmentReport> abandonmentReports) {
        if(abandonmentReports == null){
            throw new DomainViolationException("La lista dei report non può essere nulla");
        }
        this.abandonmentReports = new ArrayList<>();
        for(AbandonmentReport report : abandonmentReports){
            this.abandonmentReports.add(AbandonmentReport.copy(report));
        }
    }

    private void setTemporaryLeaves(ArrayList<TemporaryLeave> temporaryLeaves) {
        if(temporaryLeaves == null){
            throw new DomainViolationException("La lista delle temporary leaves non può essere nulla");
        }
        this.temporaryLeaves = new ArrayList<>(temporaryLeaves);
    }

    private void setStatus(ReservationStatus status) {
        if(status == null){
            throw new DomainViolationException("Lo stato della prenotazione non può essere nullo");
        }
        if((status == ReservationStatus.ACTIVE || status == ReservationStatus.TEMPORARILY_LEFT) && endTime != null){
            throw new DomainViolationException("Una prenotazione attiva non può avere un tempo di fine");
        }
        if(status == ReservationStatus.CLOSED && endTime == null){
            throw new DomainViolationException("Una prenotazione attiva non può avere un tempo di fine");
        }
        this.status = status;
    }

    private void setEndTime(LocalDateTime endTime) {
        if(endTime != null && startTime != null && endTime.isBefore(startTime)){
            throw new DomainViolationException("Inserire una data di entrata valida");
        }
        this.endTime = endTime;
    }

    private void setStartTime(LocalDateTime startTime) {
        if(startTime == null){
            throw new DomainViolationException("la data di inizio prenotazione non può essere nulla");
        }
        if(endTime != null && endTime.isBefore(startTime)){
            throw new DomainViolationException("Inserire una data di entrata valida");
        }
        this.startTime = startTime;
    }

    private void setSession(AccessSession session) {
        if(session == null){
            throw new DomainViolationException("Sessione dello studente inserita nulla");
        }
        this.session = AccessSession.copy(session);
    }

    public LocalDateTime getStartTime() {return startTime;}
    public LocalDateTime getEndTime() {return endTime;}
    public ReservationStatus getStatus() {return status;}
    public Seat getSeat() {return Seat.copy(seat);}
    public AccessSession getSession() {return AccessSession.copy(session);}

    public boolean isActive(){ return status == ReservationStatus.ACTIVE; }
    public boolean isTemporarilyLeft(){ return status == ReservationStatus.TEMPORARILY_LEFT; }


    public ArrayList<AbandonmentReport> close() {
        if(endTime != null || status == ReservationStatus.CLOSED)
            throw new DomainViolationException("La prenotazione è già stata chiusa");
        seat.free();
        ArrayList<AbandonmentReport> closedReports = new ArrayList<>();
        for(AbandonmentReport abandonmentReport : abandonmentReports){
            try{
                abandonmentReport.close();
                closedReports.add(AbandonmentReport.copy(abandonmentReport));
            }catch(Exception ignored){}
        }
        setEndTime(LocalDateTime.now());
        setStatus(ReservationStatus.CLOSED);
        return closedReports;
    }

    private void markTemporarilyLeft(){
        if(status == ReservationStatus.CLOSED ||  status == ReservationStatus.TEMPORARILY_LEFT){
            throw new DomainViolationException("La prenotazione non può essere modificata");
        }
        status = ReservationStatus.TEMPORARILY_LEFT;
    }

    public void markActive(){
        if(status == ReservationStatus.CLOSED){
            throw new DomainViolationException("La prenotazione non può essere modificata");
        }
        status = ReservationStatus.ACTIVE;
    }

    public TemporaryLeave addTemporaryLeave(){
        if(seat.getStudyArea().getTimePolicy().reachedLimit(temporaryLeaves.size())){
            throw new DomainViolationException("è stato raggiunto il limite massimo di pause");
        }
        if(hasValidTemporaryLeave()){
            throw new DomainViolationException("Impossibile inserire una pausa, ne esiste già una attiva");
        }
        if(hasActiveReport()){
            throw new DomainViolationException("Impossibile creare una pausa, c'è un report attivo sulla prenotazione");
        }
        markTemporarilyLeft();
        TemporaryLeave leave = TemporaryLeave.create(seat.getStudyArea().getTimePolicy().getMaxTemporaryLeaveMin());
        temporaryLeaves.add(leave);
        return leave;
    }

    public ArrayList<TemporaryLeave> getTemporaryLeaves() {
        return new ArrayList<>(temporaryLeaves);
    }

    public ArrayList<AbandonmentReport> getAbandonmentReports() {
        ArrayList<AbandonmentReport> res = new ArrayList<>();
        for (AbandonmentReport report : abandonmentReports) {
            res.add(AbandonmentReport.copy(report));
        }
        return res;
    }

    public void addAbandonmentReport(AbandonmentReport abandonmentReport){
        if(abandonmentReport == null){
            throw new DomainViolationException("Inserire una segnalazione valida");
        }
        if(hasActiveReport())
            throw new DomainViolationException("La prenotazione ha già una segnalazione attiva");
        abandonmentReports.add(AbandonmentReport.copy(abandonmentReport));
    }


    public boolean hasActiveReport(){
        for(AbandonmentReport report : abandonmentReports){
            if(report.isActive()) return true;        }
        return false;
    }

    public boolean hasValidTemporaryLeave(){
        for(TemporaryLeave leave : temporaryLeaves){
            if(leave.isValid()) return true;
        }
        return false;
    }
}