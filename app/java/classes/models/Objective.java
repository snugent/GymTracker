package com.example.admin1.gymtracker.models;

/**
 * Created by admin1 on 24/06/2017.
 */

public class Objective {
    private String  objectiveId;
    private String  name;
    private String  label;
    private boolean isDeleted;
    private String  viewType;

    public Objective(String objectiveId, String name, String label, boolean isDeleted, String viewType) {
        this.objectiveId = objectiveId;
        this.name = name;
        this.label = label;
        this.isDeleted = isDeleted;
        this.viewType = viewType;
    }


    public String getObjectiveId() {
        return objectiveId;
    }

    public void setObjectiveId(String objectiveId) {
        this.objectiveId = objectiveId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }
}
