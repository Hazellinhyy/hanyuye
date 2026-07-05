package com.hfut.cat_adoption_system.model;

public enum FollowupResult {
    NORMAL("正常"),
    ATTENTION("需关注"),
    RETURNED("退养");

    private final String label;

    FollowupResult(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
