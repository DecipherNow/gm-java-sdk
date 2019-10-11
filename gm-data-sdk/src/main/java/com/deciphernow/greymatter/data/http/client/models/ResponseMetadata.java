package com.deciphernow.greymatter.data.http.client.models;

import java.io.Serializable;

public class ResponseMetadata extends Metadata implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    private String checkedtstamp;
    private String cluster;
    private Object derived;
    private String expiration;
    private String jwthash;
    private String oid;
    private String schemaversion;
    private String tstamp;
    private long size;

    public ResponseMetadata() {
        super();
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getCheckedtstamp() {
        return checkedtstamp;
    }

    public void setCheckedtstamp(String checkedtstamp) {
        this.checkedtstamp = checkedtstamp;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public Object getDerived() {
        return derived;
    }

    public void setDerived(Object derived) {
        this.derived = derived;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public String getJwthash() {
        return jwthash;
    }

    public void setJwthash(String jwthash) {
        this.jwthash = jwthash;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getSchemaversion() {
        return schemaversion;
    }

    public void setSchemaversion(String schemaversion) {
        this.schemaversion = schemaversion;
    }

    public String getTstamp() {
        return tstamp;
    }

    public void setTstamp(String tstamp) {
        this.tstamp = tstamp;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
