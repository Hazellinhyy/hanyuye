package com.hfut.cat_adoption_system.model;

public enum HealthLevel {
    A("健康"),
    B("需观察"),
    C("需治疗");

    private final String label;

    HealthLevel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
