package com.smartstudy.domainModel;
import com.smartstudy.domainModel.enums.StudyAreaType;
import com.smartstudy.exceptions.DomainViolationException;


public class StudyArea extends BaseModel {
    private String name;
    private int floor;
    private StudyAreaType type;
    private TimePolicy timePolicy;
    private Library library;

    private StudyArea(long id, String name, int floor, StudyAreaType type, TimePolicy timePolicy, Library library) {
        super(id);
        setName(name);
        setFloor(floor);
        setType(type);
        setTimePolicy(timePolicy);
        setLibrary(library);
    }

    public static StudyArea valueOf(long id, String name, int floor, StudyAreaType type, TimePolicy timePolicy,  Library library) {
        return new StudyArea(id, name, floor, type, timePolicy, library);
    }
    public static StudyArea copy(StudyArea sa){
        return new StudyArea(sa.getId(), sa.getName(), sa.getFloor(), sa.getType(), sa.getTimePolicy(), sa.getLibrary());
    }

    private void setFloor(int floor) {
        this.floor = floor;
    }

    private void setType(StudyAreaType type) {
        if(type == null){
            throw new DomainViolationException("La StudyArea non pupò avere un tipo nullo");
        }
        this.type = type;
    }

    private void setTimePolicy(TimePolicy timePolicy) {
        if(timePolicy == null){
            throw new DomainViolationException("TimePolicy non può essere nulla");
        }
        this.timePolicy = timePolicy;
    }

    private void setLibrary(Library library) {
        if(library == null){
            throw new DomainViolationException("La StudyArea deve essere associata ad una biblioteca");
        }
        this.library = Library.copy(library);
    }

    private void setName(String name){
        if(name == null || name.isBlank()){
            throw new DomainViolationException("Il nome non può essere vuoto");
        }
        this.name = name;
    }

    public String getName() { return name; }
    public StudyAreaType getType() { return type; }
    public int getFloor() { return floor; }
    public TimePolicy getTimePolicy() {return timePolicy;}
    public Library getLibrary() {
        return Library.copy(library);
    }

    public void changeName(String newName){
        setName(newName);
    }

    public void changeStudyAreaType(StudyAreaType newType){
        setType(newType);
    }
    public void changePolicy(TimePolicy timePolicy){
        setTimePolicy(timePolicy);
    }
}