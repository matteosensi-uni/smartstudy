package com.smartstudy.domainModel;
import com.smartstudy.domainModel.enums.ReservationStatus;
import com.smartstudy.exceptions.DomainViolationException;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Reservation extends BaseModel{
    private final LocalDateTime startTime;
    private LocalDateTime endTime;
    private ReservationStatus status;
    private final ArrayList<TemporaryLeave> temporaryLeaves;
    private final ArrayList<AbandonmentReport> abandonmentReports;
    private final Seat seat;
    private final AccessSession session;

    private Reservation(AccessSession session, Seat seat) {
        super();
        this.abandonmentReports = new ArrayList<>();
        if(session == null ||  seat == null){
            throw new DomainViolationException("La sessione e il posto non possono essere nulli");
        }
        this.startTime = LocalDateTime.now();
        this.status = ReservationStatus.ACTIVE;
        this.session = AccessSession.copy(session);
        this.seat = seat;
        temporaryLeaves = new ArrayList<>();
    }

    private Reservation(long id, LocalDateTime startTime, LocalDateTime endTime, ReservationStatus status, ArrayList<TemporaryLeave> temporaryLeaves, ArrayList<AbandonmentReport> abandonmentReports, AccessSession session, Seat seat) {
        super(id);
        if(session == null ||  seat == null){
            throw new DomainViolationException("La sessione e il posto non possono essere nulli");
        }
        if(temporaryLeaves == null){
            throw new DomainViolationException("La lista delle temporary leaves non può essere nulla");
        }
        if(abandonmentReports == null){
            throw new DomainViolationException("La lista dei report non può essere nulla");
        }
        if(startTime == null){
            throw new DomainViolationException("la data di inizio prenotazione non può essere nulla");
        }
        if(status == null){
            throw new DomainViolationException("Lo stato della prenotazione non può essere nullo");
        }
        if((status == ReservationStatus.ACTIVE || status == ReservationStatus.TEMPORARILY_LEFT) && endTime != null){
            throw new DomainViolationException("Una prenotazione attiva non può avere un tempo di fine");
        }
        if(status == ReservationStatus.CLOSED && endTime == null){
            throw new DomainViolationException("Una prenotazione attiva non può avere un tempo di fine");
        }
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.session = AccessSession.copy(session);
        this.seat = seat;
        this.temporaryLeaves = new ArrayList<>(temporaryLeaves);
        this.abandonmentReports = new ArrayList<>();
        for(AbandonmentReport report : abandonmentReports){
            this.abandonmentReports.add(AbandonmentReport.copy(report));
        }

    }

    public static Reservation valueOf(long id, LocalDateTime startTime, LocalDateTime endTime, ReservationStatus status, ArrayList<TemporaryLeave> temporaryLeaves, AccessSession session, Seat seat, ArrayList<AbandonmentReport> reports){
        return new Reservation(id, startTime, endTime, status, temporaryLeaves,reports, session, seat);
    }
    public static Reservation copy(Reservation reservation){
        if(reservation == null){
            throw new DomainViolationException("Prenotazione inserita nulla");
        }
        return new Reservation(reservation.getId(), reservation.getStartTime(), reservation.getEndTime(), reservation.getStatus(), reservation.getTemporaryLeaves(), reservation.getAbandonmentReports(), reservation.getSession(), reservation.getSeat());
    }

    public static Reservation start(AccessSession session, Seat seat){
        seat.occupy();
        return new Reservation(session, seat);
    }

    public LocalDateTime getStartTime() {return startTime;}
    public LocalDateTime getEndTime() {return endTime;}
    public ReservationStatus getStatus() {return status;}
    public Seat getSeat() {return Seat.copy(seat);}
    public AccessSession getSession() {return AccessSession.copy(session);}
    public boolean isActive(){ return status == ReservationStatus.ACTIVE; }

    public void close() {
        if(endTime != null || status == ReservationStatus.CLOSED)
            throw new DomainViolationException("La prenotazione è già stata chiusa");
        seat.free();
        for(AbandonmentReport abandonmentReport : abandonmentReports){
            try{
                abandonmentReport.close();
            }catch(Exception ignored){}
        }
        endTime =  LocalDateTime.now();
        status = ReservationStatus.CLOSED;
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


    private boolean hasActiveReport(){
        for(AbandonmentReport report : abandonmentReports){
            if(report.isActive()) return true;        }
        return false;
    }

    private boolean hasValidTemporaryLeave(){
        for(TemporaryLeave leave : temporaryLeaves){
            if(leave.isValid()) return true;
        }
        return false;
    }
}