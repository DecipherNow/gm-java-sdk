package com.deciphernow.greymatter.data.http.client.models;

import java.io.Serializable;

public class Metadata implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String action;
    private String parentoid;
    private Boolean isFile;
    private String mimetype;
    private Security security;
    private ObjectPolicy objectpolicy;

    public Metadata() {
    }

    public Metadata(String name, String action, String parentoid, Boolean isFile, String mimetype, Security security, ObjectPolicy objectPolicy) {
        this.name = name;
        this.action = action;
        this.parentoid = parentoid;
        this.isFile = isFile;
        this.mimetype = mimetype;
        this.security = security;
        this.objectpolicy = objectPolicy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getParentoid() {
        return parentoid;
    }

    public void setParentoid(String parentoid) {
        this.parentoid = parentoid;
    }

    public Boolean getIsFile() {
        return isFile;
    }

    public void setIsFile(Boolean file) {
        isFile = file;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public ObjectPolicy getObjectPolicy() {
        return objectpolicy;
    }

    public void setObjectPolicy(ObjectPolicy objectPolicy) {
        this.objectpolicy = objectPolicy;
    }
}
