package com.deciphernow.greymatter.data.http.client.models;

import java.io.Serializable;

public class ObjectPolicy implements Serializable {
    private static final long serialVersionUID = 1L;
    private String label;
    private Requirements requirements;

    public ObjectPolicy(String label, Requirements r) {
        this.label = label;
        this.requirements = r;
    }

    public Requirements getRequirements() {
        return requirements;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setRequirements(Requirements requirements) {
        this.requirements = requirements;
    }
}

