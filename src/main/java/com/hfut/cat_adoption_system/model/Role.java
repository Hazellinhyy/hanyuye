package com.hfut.cat_adoption_system.model;

public enum Role {
    STUDENT("普通用户"),
    VOLUNTEER("志愿者"),
    HOSPITAL("合作医院"),
    ADMIN("管理员");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
