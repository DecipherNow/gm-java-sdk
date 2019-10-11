package com.deciphernow.greymatter.data.http.client.models;

import java.io.Serializable;

public class Requirements implements Serializable {
    private static final long serialVersionUID = 1L;
    private String f;

    public Requirements(String f) {
        this.f = f;
    }

    public String getF() {
        return f;
    }

    public void setF(String f) {
        this.f = f;
    }
}
