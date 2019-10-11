package com.deciphernow.greymatter.data.http.client.settings;

/**
 *
 */

public class GreyMatterClientConfig {
    /**
     * Grey Matter Edge URI
     */
    private String hostName;
    /**
     * Grey Matter Edge Port
     */
    private int port;
    /**
     * Grey Matter Data Service Path
     */
    private String prefix;
    /**
     * The default timeout for creating new connections.
     */
    public static final int DEFAULT_CONNECTION_TIMEOUT = 10 * 1000;
    /**
     * The default timeout for reading from a connected socket.
     */
    public static final int DEFAULT_SOCKET_TIMEOUT = 50 * 1000;

    /**
     * The default timeout for a request. This is disabled by default.
     */
    public static final int DEFAULT_REQUEST_TIMEOUT = 0;

    /**
     * The default timeout for a request. This is disabled by default.
     */
    public static final int DEFAULT_CLIENT_EXECUTION_TIMEOUT = 0;

    /**
     * The default max connection pool size.
     */
    public static final int DEFAULT_MAX_CONNECTIONS = 50;
    /**
     * The default HTTP user agent header for AWS Java SDK clients.
     */
    public static final String DEFAULT_USER_AGENT = "grey-matter-data-sdk";

    /**
     * A prefix to the HTTP user agent header passed with all HTTP requests.
     */
    private String userAgentPrefix = DEFAULT_USER_AGENT;

    /**
     * A suffix to the HTTP user agent header.
     */
    private String userAgentSuffix;

    /**
     * Returns the HTTP user agent header prefix to send with all requests.
     *
     * @return The user agent string prefix to use when sending requests.
     */
    public String getUserAgentPrefix() {
        return userAgentPrefix;
    }

    /**
     * Sets the HTTP user agent prefix to send with all requests.
     *
     * @param prefix The string to prefix to user agent to use when sending requests.
     */
    public void setUserAgentPrefix(String prefix) {
        this.userAgentPrefix = prefix;
    }

    /**
     * Returns the HTTP user agent header suffix to add to the end of the user agent header on all requests.
     *
     * @return The user agent string suffix to use when sending requests.
     */
    public String getUserAgentSuffix() {
        return userAgentSuffix;
    }

    /**
     * Sets the HTTP user agent suffix to send with all requests.
     *
     * @param suffix The string to suffix to user agent to use when sending requests.
     */
    public void setUserAgentSuffix(String suffix) {
        this.userAgentSuffix = suffix;
    }

    /**
     * Returns the Host Name where Grey Matter Data is located
     *
     * @return The Host Name of the edge proxy
     */

    public String getHostName() {
        return this.hostName;
    }

    /**
     * Sets the Host Name where Grey Matter Data is located
     * e.g. `fabric.development.deciphernow.com`
     *
     * @param host The Host Name of the edge proxy
     */
    public void setHostName(final String host) {
        this.hostName = host;
    }

    /**
     * Returns the port number for edge proxy
     *
     * @return Port number for the edge proxy
     */
    public int getPort() {
        return this.port;
    }

    /**
     * Sets the Port for the edge proxy
     * e.g. `9443`
     *
     * @param port Port for the proxy edge
     */
    public void setPort(final int port) {
        this.port = port;
    }

    /**
     * Returns the service path for Grey Matter Data
     *
     * @return Service location for Grey Matter Data
     */
    public String getPrefix() {
        return this.prefix;
    }

    /**
     * Sets the service path for Grey Matter Data
     * e.g. `/services/gm-data/1.0`
     *
     * @param prefix Service location for Grey Matter Data
     */
    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }

    public GreyMatterClientConfig(final String host, final int port, final String prefix) {
        this.hostName = host;
        this.port = port;
        this.prefix = prefix;
    }

}
