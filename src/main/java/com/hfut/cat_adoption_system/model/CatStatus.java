package com.hfut.cat_adoption_system.model;

public enum CatStatus {
    PENDING_VERIFY("待核实"),
    OBSERVING("观察中"),
    MEDICAL("医疗中"),
    ADOPTABLE("可认养"),
    APPLYING("申请中"),
    FOLLOWING("回访中"),
    SUSPENDED("暂停认养"),
    RETURN_PENDING("退养待处理"),
    RESERVED("待交接"),
    ADOPTED("已认养"),
    MISSING("失联");

    private final String label;

    CatStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
