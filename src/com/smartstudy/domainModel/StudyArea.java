package com.smartstudy.domainModel;
import com.smartstudy.domainModel.enums.StudyAreaType;
import com.smartstudy.exceptions.DomainViolationException;


public class StudyArea extends BaseModel {
    private String name;
    private final int floor;
    private StudyAreaType type;
    private TimePolicy timePolicy;
    private final Library library;

    private StudyArea(long id, String name, int floor, StudyAreaType type, TimePolicy timePolicy, Library library) {
        super(id);
        if(timePolicy == null){
            throw new DomainViolationException("TimePolicy non può essere nulla");
        }
        if(library == null){
            throw new DomainViolationException("La StudyArea deve essere associata ad una biblioteca");
        }
        if(name == null || name.isBlank()){
            throw new DomainViolationException("La StudyArea deve avere un nome valido");
        }
        if(type == null){
            throw new DomainViolationException("La StudyArea non pupò avere un tipo nullo");
        }
        this.name = name;
        this.floor = floor;
        this.type = type;
        this.timePolicy = timePolicy; // è una classe immutabile
        this.library = library;
    }

    public static StudyArea valueOf(long id, String name, int floor, StudyAreaType type, TimePolicy timePolicy,  Library library) {
        return new StudyArea(id, name, floor, type, timePolicy, library);
    }
    public static StudyArea copy(StudyArea sa){
        return new StudyArea(sa.getId(), sa.getName(), sa.getFloor(), sa.getType(), sa.getTimePolicy(), sa.getLibrary());
    }


    public String getName() { return name; }
    public StudyAreaType getType() { return type; }
    public int getFloor() { return floor; }
    public TimePolicy getTimePolicy() {return timePolicy;}
    public Library getLibrary() {
        return library;
    }

    public void setName(String name){
        if(name == null || name.isBlank()){
            throw new DomainViolationException("Il nuovo nome non può essere vuoto");
        }
        this.name = name;
    }
    public void changeStudyAreaType(StudyAreaType newType){
        if(newType == null){
            throw new DomainViolationException("Il tipo non può essere nullo");
        }
        type = newType;
    }
    public void changePolicy(TimePolicy timePolicy){
        if(timePolicy == null){
            throw new DomainViolationException("La policy indicata non è valida");
        }
        this.timePolicy = timePolicy;
    }
}