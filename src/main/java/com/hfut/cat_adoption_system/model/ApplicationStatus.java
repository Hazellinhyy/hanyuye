package com.hfut.cat_adoption_system.model;

public enum ApplicationStatus {
    PENDING_INITIAL("待初审"),
    INITIAL_APPROVED("初审通过"),
    INITIAL_REJECTED("初审拒绝"),
    PENDING_FINAL("待终审"),
    FINAL_APPROVED("终审通过"),
    FINAL_REJECTED("终审拒绝"),
    PENDING_HANDOVER("待交接"),
    CANCELLED("已取消"),
    PENDING("待审核"),
    APPROVED("待交接"),
    REJECTED("已拒绝"),
    WITHDRAWN("已撤回"),
    HANDED_OVER("已交接");

    private final String label;

    ApplicationStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
