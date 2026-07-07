package com.smartstudy.DomainModel;

import com.smartstudy.DomainModel.enums.StudyAreaType;


public class StudyArea extends BaseModel {
    private String name;
    private int floor;
    private StudyAreaType type;
    private long library_id;
    private TimePolicy timepolicy;

    public StudyArea(long id, String name, int floor, StudyAreaType type, long library_id, TimePolicy timepolicy) {
        super(id);
        this.name = name;
        this.floor = floor;
        this.type = type;
        this.library_id = library_id;
        this.timepolicy = timepolicy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StudyAreaType getType() {
        return type;
    }

    public void setType(StudyAreaType type) {
        this.type = type;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public long getLibraryId() {
        return library_id;
    }

    public void setLibraryId(long library_id) {
        this.library_id = library_id;
    }

    public TimePolicy getTimepolicy() {
        return timepolicy;
    }

    public void setTimepolicy(TimePolicy timepolicy) {
        this.timepolicy = timepolicy;
    }
}
