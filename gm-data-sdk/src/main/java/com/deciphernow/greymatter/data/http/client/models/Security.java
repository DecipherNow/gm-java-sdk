package com.deciphernow.greymatter.data.http.client.models;

import java.io.Serializable;

public class Security implements Serializable {
    private static final long serialVersionUID = 1L;
    private String label;
    private String foreground;
    private String background;

    public Security(String label, String foreground, String background) {
        this.label = label;
        this.foreground = foreground;
        this.background = background;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getForeground() {
        return foreground;
    }

    public void setForeground(String foreground) {
        this.foreground = foreground;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }
}

