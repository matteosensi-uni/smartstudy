package com.smartstudy.DomainModel;
import com.smartstudy.DomainModel.enums.StudyAreaType;


public class StudyArea extends BaseModel {
    private String name;
    private final int floor;
    private StudyAreaType type;
    private long library_id;
    private long timePolicy_id ;

    public StudyArea(String name, int floor, StudyAreaType type, long library_id, long timepolicy_id) {
        super();
        this.name = name;
        this.floor = floor;
        this.type = type;
        this.library_id = library_id;
        this.timePolicy_id = timepolicy_id;
    }

    public StudyArea(long id, String name, int floor, StudyAreaType type, long library_id, long timepolicy_id) {
        super(id);
        this.name = name;
        this.floor = floor;
        this.type = type;
        this.library_id = library_id;
        this.timePolicy_id = timepolicy_id;
    }

    public String getName() { return name; }
    public void setName(String name) {  this.name=name; }

    public StudyAreaType getType() { return type; }
    public int getFloor() { return floor; }
    public long getLibraryId() {return library_id;}
    public long getTimePolicyId() {return timePolicy_id;}


    public void changeStudyAreaType(StudyAreaType new_type){
        type = new_type;
    }
    public void changeLibrary(long library_id){ this.library_id = library_id; }
    public void changePolicy(long timePolicy_id ){ this.timePolicy_id = timePolicy_id; }

}
