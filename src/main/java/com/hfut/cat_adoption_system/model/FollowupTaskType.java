package com.hfut.cat_adoption_system.model;

public enum FollowupTaskType {
    DAY_7("First adaptation follow-up"),
    DAY_30("Stable condition follow-up"),
    DAY_90("Long-term condition follow-up");

    private final String label;

    FollowupTaskType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
