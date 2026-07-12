package com.smartstudy.DomainModel;
import com.smartstudy.DomainModel.enums.StudyAreaType;


public class StudyArea extends BaseModel {
    private String name;
    private final int floor;
    private StudyAreaType type;
    private final long libraryId;
    private long timePolicyId;

    private StudyArea(long id, String name, int floor, StudyAreaType type, long libraryId, long timePolicyId) {
        super(id);
        this.name = name;
        this.floor = floor;
        this.type = type;
        this.libraryId = libraryId;
        this.timePolicyId = timePolicyId;
    }

    public static StudyArea valueOf(long id, String name, int floor, StudyAreaType type, long libraryId, long timePolicyId){
        return new StudyArea(id, name, floor, type, libraryId, timePolicyId);
    }

    public String getName() { return name; }
    public StudyAreaType getType() { return type; }
    public int getFloor() { return floor; }
    public long getLibraryId() {return libraryId;}
    public long getTimePolicyId() {return timePolicyId;}

    public void setName(String name) {
        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("Il nuovo nome non può essere vuoto");
        }
        this.name = name;
    }
    public void changeStudyAreaType(StudyAreaType newType){ type = newType; }
    public void changePolicy(long timePolicyId){ this.timePolicyId = timePolicyId; }

}