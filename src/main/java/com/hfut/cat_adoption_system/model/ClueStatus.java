package com.hfut.cat_adoption_system.model;

public enum ClueStatus {
    PENDING_VERIFY("待核实"),
    VERIFIED_VALID("已核实有效"),
    CREATED_CAT("已建档"),
    DUPLICATE("重复线索"),
    INVALID("无效线索");

    private final String label;

    ClueStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
