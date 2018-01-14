package com.paprika.teachme.controller;

import com.indoorway.android.common.sdk.model.RegisteredVisitor;
import com.indoorway.android.common.sdk.model.VisitorLocation;

public class User {
    private String uuid;
    private RegisteredVisitor visitorData;
    private VisitorLocation visitorLocation;

    public RegisteredVisitor getVisitorData() {
        return visitorData;
    }

    public void setVisitorData(RegisteredVisitor visitorData) {
        this.visitorData = visitorData;
    }

    public VisitorLocation getVisitorLocation() {
        return visitorLocation;
    }

    public void setVisitorLocation(VisitorLocation visitorLocation) {
        this.visitorLocation = visitorLocation;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
