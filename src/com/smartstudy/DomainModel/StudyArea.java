package com.smartstudy.DomainModel;
import com.smartstudy.DomainModel.enums.StudyAreaType;


public class StudyArea extends BaseModel {
    private String name;
    private final int floor;
    private StudyAreaType type;
    private final long libraryId;
    private long timePolicyId;

    public StudyArea(String name, int floor, StudyAreaType type, long libraryId, long timePolicyId) {
        super();
        this.name = name;
        this.floor = floor;
        this.type = type;
        this.libraryId = libraryId;
        this.timePolicyId = timePolicyId;
    }

    public StudyArea(long id, String name, int floor, StudyAreaType type, long libraryId, long timePolicyId) {
        super(id);
        this.name = name;
        this.floor = floor;
        this.type = type;
        this.libraryId = libraryId;
        this.timePolicyId = timePolicyId;
    }

    public String getName() { return name; }
    public void setName(String name) {  this.name=name; }

    public StudyAreaType getType() { return type; }
    public int getFloor() { return floor; }
    public long getLibraryId() {return libraryId;}
    public long getTimePolicyId() {return timePolicyId;}


    public void changeStudyAreaType(StudyAreaType newType){
        type = newType;
    }
    public void changePolicy(long timePolicyId){ this.timePolicyId = timePolicyId; }

}