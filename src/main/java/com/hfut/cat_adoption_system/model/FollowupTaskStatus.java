package com.hfut.cat_adoption_system.model;

public enum FollowupTaskStatus {
    PENDING("待回访"),
    COMPLETED("已完成"),
    OVERDUE("逾期"),
    ABNORMAL("异常");

    private final String label;

    FollowupTaskStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
