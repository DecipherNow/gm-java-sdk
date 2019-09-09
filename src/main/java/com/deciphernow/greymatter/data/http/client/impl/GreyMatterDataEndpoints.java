package com.deciphernow.greymatter.data.http.client.impl;

/**
 * Endpoints that can be used
 * <li>{@link #DERIVED}</li>
 * <li>{@link #HISTORY}</li>
 * <li>{@link #LIST}</li>
 * <li>{@link #MACROS}</li>
 * <li>{@link #NOTIFICATIONS}</li>
 * <li>{@link #PROPS}</li>
 * <li>{@link #READ}</li>
 * <li>{@link #SHOW}</li>
 * <li>{@link #STREAM}</li>
 * <li>{@link #WRITE}</li>
 */

public enum GreyMatterDataEndpoints {

    /**
     * returns an array of Object IDs that are related to specified Object ID
     */
    DERIVED("/derived"),
    /**
     * returns every event associated with a given Object ID. Request structure /history/{oid}/{path}/?queryString1={queryValue1}
     */
    HISTORY("/history"),
    /**
     * returns most recent event for each child Object ID associated with provided Object ID. Request structure /list/{oid}/{path}/?queryString1={queryValue1}&?queryString2={queryValue2}
     */
    LIST("/list"),
    /**
     * an internal method for accessing validity of the event’s security policy.
     */
    MACROS("/macros"),
    /**
     * an array of Event Objects related to the Object ID. Request structure /notifications/{oid}/{path}
     */
    NOTIFICATIONS("/notifications"),
    /**
     * returns latest event for an Object ID. Request structure /props/{oid}/{path}/?queryString1={queryValue1}
     */
    PROPS("/props"),
    /**
     * performs a bulk query of other endpoints. Returns an array containing input request URL and matching response.
     */
    READ("/read"),
    /**
     * returns a stream of file bytes wrapped in an iFrame with associated security banner. Request structure /history/{oid}/{path}/?queryString1={queryValue1}&?queryString2={queryValue2}
     */
    SHOW("/show"),
    /**
     * stream of file's bytes. Request structure /stream/{oid}/{path}
     */
    STREAM("/stream"),
    /**
     * the only way to modify the system. Write a transaction of changes to Event Objects. Request structure /write (must attach form data to the request with additional necessary information)
     */
    WRITE("/write");

    private final String endpoint;

    GreyMatterDataEndpoints(String ep) {
        this.endpoint = ep;
    }

    public String getEndpoint() {
        return endpoint;
    }

}
