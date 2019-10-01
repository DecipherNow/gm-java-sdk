package com.deciphernow.greymatter.data.http.client.impl;

import org.apache.http.*;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.client.*;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.InputStreamFactory;
import org.apache.http.client.protocol.RequestExpectContinue;
import org.apache.http.client.protocol.*;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Lookup;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.conn.util.PublicSuffixMatcherLoader;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.auth.*;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.*;
import org.apache.http.impl.execchain.*;
import org.apache.http.protocol.*;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.TextUtils;
import org.apache.http.util.VersionInfo;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.io.Closeable;
import java.io.IOException;
import java.net.ProxySelector;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class GreyMatterHttpClientBuilder {
    private HttpRequestExecutor requestExec;
    private HostnameVerifier hostnameVerifier;
    private LayeredConnectionSocketFactory sslSocketFactory;
    private SSLContext sslContext;
    private HttpClientConnectionManager connManager;
    private boolean connManagerShared;
    private SchemePortResolver schemePortResolver;
    private ConnectionReuseStrategy reuseStrategy;
    private ConnectionKeepAliveStrategy keepAliveStrategy;
    private AuthenticationStrategy targetAuthStrategy;
    private AuthenticationStrategy proxyAuthStrategy;
    private UserTokenHandler userTokenHandler;
    private HttpProcessor httpprocessor;
    private DnsResolver dnsResolver;

    private LinkedList<HttpRequestInterceptor> requestFirst;
    private LinkedList<HttpRequestInterceptor> requestLast;
    private LinkedList<HttpResponseInterceptor> responseFirst;
    private LinkedList<HttpResponseInterceptor> responseLast;

    private HttpRequestRetryHandler retryHandler;
    private HttpRoutePlanner routePlanner;
    private RedirectStrategy redirectStrategy;
    private ConnectionBackoffStrategy connectionBackoffStrategy;
    private BackoffManager backoffManager;
    private ServiceUnavailableRetryStrategy serviceUnavailStrategy;
    private Lookup<AuthSchemeProvider> authSchemeRegistry;
    private Lookup<CookieSpecProvider> cookieSpecRegistry;
    private Map<String, InputStreamFactory> contentDecoderMap;
    private CookieStore cookieStore;
    private CredentialsProvider credentialsProvider;
    private String userAgent;
    private HttpHost proxy;
    private Collection<? extends Header> defaultHeaders;
    private SocketConfig defaultSocketConfig;
    private ConnectionConfig defaultConnectionConfig;
    private RequestConfig defaultRequestConfig;
    private boolean evictExpiredConnections;
    private boolean evictIdleConnections;
    private long maxIdleTime;
    private TimeUnit maxIdleTimeUnit;

    private boolean systemProperties;
    private boolean redirectHandlingDisabled;
    private boolean automaticRetriesDisabled;
    private boolean contentCompressionDisabled;
    private boolean cookieManagementDisabled;
    private boolean authCachingDisabled;
    private boolean connectionStateDisabled;
    private boolean defaultUserAgentDisabled;

    private int maxConnTotal = 0;
    private int maxConnPerRoute = 0;

    private long connTimeToLive = -1;
    private TimeUnit connTimeToLiveTimeUnit = TimeUnit.MILLISECONDS;

    private List<Closeable> closeables;

    private PublicSuffixMatcher publicSuffixMatcher;

    public static GreyMatterHttpClientBuilder create() {
        return new GreyMatterHttpClientBuilder();
    }

    protected GreyMatterHttpClientBuilder() {
        super();
    }

    /**
     * Assigns {@link HttpRequestExecutor} instance.
     *
     * @param requestExec Http request executor
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder setRequestExecutor(final HttpRequestExecutor requestExec) {
        this.requestExec = requestExec;
        return this;
    }

    /**
     * Assigns {@link X509HostnameVerifier} instance.
     * <p>
     * Please note this value can be overridden by the {@link #setConnectionManager(
     *org.apache.http.conn.HttpClientConnectionManager)} and the {@link #setSSLSocketFactory(
     *org.apache.http.conn.socket.LayeredConnectionSocketFactory)} methods.
     * </p>
     *
     * @param hostnameVerifier X509HostnameVerifier
     * @return GreyMatterHttpClientBuilder
     * @deprecated (4.4)
     */
    @Deprecated
    public final GreyMatterHttpClientBuilder setHostnameVerifier(final X509HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    /**
     * Assigns {@link javax.net.ssl.HostnameVerifier} instance.
     * <p>
     * Please note this value can be overridden by the {@link #setConnectionManager(
     *org.apache.http.conn.HttpClientConnectionManager)} and the {@link #setSSLSocketFactory(
     *org.apache.http.conn.socket.LayeredConnectionSocketFactory)} methods.
     * </p>
     *
     * @param hostnameVerifier X509HostnameVerifier
     * @return GreyMatterHttpClientBuilder
     * @since 4.4
     */
    public final GreyMatterHttpClientBuilder setSSLHostnameVerifier(final HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    /**
     * Assigns file containing public suffix matcher. Instances of this class can be created
     * with {@link org.apache.http.conn.util.PublicSuffixMatcherLoader}.
     *
     * @param publicSuffixMatcher PublicSuffixMatcher
     * @return GreyMatterHttpClientBuilder
     * @see org.apache.http.conn.util.PublicSuffixMatcher
     * @see org.apache.http.conn.util.PublicSuffixMatcherLoader
     * @since 4.4
     */
    public final GreyMatterHttpClientBuilder setPublicSuffixMatcher(final PublicSuffixMatcher publicSuffixMatcher) {
        this.publicSuffixMatcher = publicSuffixMatcher;
        return this;
    }

    /**
     * Assigns {@link SSLContext} instance.
     * <p>
     * Please note this value can be overridden by the {@link #setConnectionManager(
     *org.apache.http.conn.HttpClientConnectionManager)} and the {@link #setSSLSocketFactory(
     *org.apache.http.conn.socket.LayeredConnectionSocketFactory)} methods.
     * </p>
     *
     * @param sslcontext SSLContext
     * @return GreyMatterHttpClientBuilder
     * @deprecated (4.5) use {@link #setSSLContext(SSLContext)}
     */
    @Deprecated
    public final GreyMatterHttpClientBuilder setSslcontext(final SSLContext sslcontext) {
        return setSSLContext(sslcontext);
    }

    /**
     * Assigns {@link SSLContext} instance.
     * <p>
     * Please note this value can be overridden by the {@link #setConnectionManager(
     *org.apache.http.conn.HttpClientConnectionManager)} and the {@link #setSSLSocketFactory(
     *org.apache.http.conn.socket.LayeredConnectionSocketFactory)} methods.
     * </p>
     *
     * @param SSLContext SSLContext
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder setSSLContext(final SSLContext SSLContext) {
        this.sslContext = sslContext;
        return this;
    }

    /**
     * Assigns {@link LayeredConnectionSocketFactory} instance.
     * <p>
     * Please note this value can be overridden by the {@link #setConnectionManager(
     *org.apache.http.conn.HttpClientConnectionManager)} method.
     * </p>
     *
     * @param sslSocketFactory LayeredConnectionSocketFactory
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder setSSLSocketFactory(
            final LayeredConnectionSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
        return this;
    }

    /**
     * Assigns maximum total connection value.
     * <p>
     * Please note this value can be overridden by the {@link #setConnectionManager(
     *org.apache.http.conn.HttpClientConnectionManager)} method.
     * </p>
     *
     * @param maxConnTotal integer value for maximum total connections
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder setMaxConnTotal(final int maxConnTotal) {
        this.maxConnTotal = maxConnTotal;
        return this;
    }

    /**
     * Assigns maximum connection per route value.
     * <p>
     * Please note this value can be overridden by the {@link #setConnectionManager(
     *org.apache.http.conn.HttpClientConnectionManager)} method.
     * </p>
     *
     * @param maxConnPerRoute integer value for maximum connections per route
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder setMaxConnPerRoute(final int maxConnPerRoute) {
        this.maxConnPerRoute = maxConnPerRoute;
        return this;
    }

    /**
     * Assigns default {@link SocketConfig}.
     * <p>
     * Please note this value can be overridden by the {@link #setConnectionManager(
     *org.apache.http.conn.HttpClientConnectionManager)} method.
     * </p>
     *
     * @param config SocketConfig
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder setDefaultSocketConfig(final SocketConfig config) {
        this.defaultSocketConfig = config;
        return this;
    }

    /**
     * Assigns default {@link ConnectionConfig}.
     * <p>
     * Please note this value can be overridden by the {@link #setConnectionManager(
     *org.apache.http.conn.HttpClientConnectionManager)} method.
     * </p>
     *
     * @param config ConnectionConfig
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder setDefaultConnectionConfig(final ConnectionConfig config) {
        this.defaultConnectionConfig = config;
        return this;
    }

    /**
     * Sets maximum time to live for persistent connections
     * <p>
     * Please note this value can be overridden by the {@link #setConnectionManager(
     *org.apache.http.conn.HttpClientConnectionManager)} method.
     * </p>
     *
     * @param connTimeToLive         long
     * @param connTimeToLiveTimeUnit TimeUnit
     * @return GreyMatterHttpClientBuilder
     * @since 4.4
     */
    public final GreyMatterHttpClientBuilder setConnectionTimeToLive(final long connTimeToLive, final TimeUnit connTimeToLiveTimeUnit) {
        this.connTimeToLive = connTimeToLive;
        this.connTimeToLiveTimeUnit = connTimeToLiveTimeUnit;
        return this;
    }

    /**
     * Assigns {@link HttpClientConnectionManager} instance.
     *
     * @param connManager HttpClientConnectionManager
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder setConnectionManager(
            final HttpClientConnectionManager connManager) {
        this.connManager = connManager;
        return this;
    }

    /**
     * Defines the connection manager is to be shared by multiple
     * client instances.
     * <p>
     * If the connection manager is shared its life-cycle is expected
     * to be managed by the caller and it will not be shut down
     * if the client is closed.
     * </p>
     *
     * @param shared defines whether or not the connection manager can be shared
     *               by multiple clients.
     * @return GreyMatterHttpClientBuilder
     * @since 4.4
     */
    public final GreyMatterHttpClientBuilder setConnectionManagerShared(
            final boolean shared) {
        this.connManagerShared = shared;
        return this;
    }

    /**
     * Assigns {@link ConnectionReuseStrategy} instance.
     *
     * @param reuseStrategy ConnectionReuseStrategy
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder setConnectionReuseStrategy(
            final ConnectionReuseStrategy reuseStrategy) {
        this.reuseStrategy = reuseStrategy;
        return this;
    }

    /**
     * Assigns {@link ConnectionKeepAliveStrategy} instance.
     *
     * @param keepAliveStrategy ConnectionKeepAliveStrategy
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder setKeepAliveStrategy(
            final ConnectionKeepAliveStrategy keepAliveStrategy) {
        this.keepAliveStrategy = keepAliveStrategy;
        return this;
    }

    /**
     * Assigns {@link AuthenticationStrategy} instance for target
     * host authentication.
     *
     * @param targetAuthStrategy AuthenticationStrategy
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder setTargetAuthenticationStrategy(
            final AuthenticationStrategy targetAuthStrategy) {
        this.targetAuthStrategy = targetAuthStrategy;
        return this;
    }

    /**
     * Assigns {@link AuthenticationStrategy} instance for proxy
     * authentication.
     *
     * @param proxyAuthStrategy AuthenticationStrategy
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder setProxyAuthenticationStrategy(
            final AuthenticationStrategy proxyAuthStrategy) {
        this.proxyAuthStrategy = proxyAuthStrategy;
        return this;
    }

    /**
     * Assigns {@link UserTokenHandler} instance.
     * <p>
     * Please note this value can be overridden by the {@link #disableConnectionState()}
     * method.
     * </p>
     *
     * @param userTokenHandler UserTokenHandler
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder setUserTokenHandler(final UserTokenHandler userTokenHandler) {
        this.userTokenHandler = userTokenHandler;
        return this;
    }

    /**
     * Disables connection state tracking.
     *
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder disableConnectionState() {
        connectionStateDisabled = true;
        return this;
    }

    /**
     * Assigns {@link SchemePortResolver} instance.
     *
     * @param schemePortResolver SchemePortResolver
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder setSchemePortResolver(
            final SchemePortResolver schemePortResolver) {
        this.schemePortResolver = schemePortResolver;
        return this;
    }

    /**
     * Assigns {@code User-Agent} value.
     * <p>
     * Please note this value can be overridden by the {@link #setHttpProcessor(
     *org.apache.http.protocol.HttpProcessor)} method.
     * </p>
     *
     * @param userAgent User-Agent
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder setUserAgent(final String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    /**
     * Assigns default request header values.
     * <p>
     * Please note this value can be overridden by the {@link #setHttpProcessor(
     *org.apache.http.protocol.HttpProcessor)} method.
     * </p>
     *
     * @param defaultHeaders Collection of headers
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder setDefaultHeaders(final Collection<? extends Header> defaultHeaders) {
        this.defaultHeaders = defaultHeaders;
        return this;
    }

    /**
     * Adds this protocol interceptor to the head of the protocol processing list.
     * <p>
     * Please note this value can be overridden by the {@link #setHttpProcessor(
     *org.apache.http.protocol.HttpProcessor)} method.
     * </p>
     *
     * @param itcp HttpResponseInterceptor
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder addInterceptorFirst(final HttpResponseInterceptor itcp) {
        if (itcp == null) {
            return this;
        }
        if (responseFirst == null) {
            responseFirst = new LinkedList<HttpResponseInterceptor>();
        }
        responseFirst.addFirst(itcp);
        return this;
    }

    /**
     * Adds this protocol interceptor to the tail of the protocol processing list.
     * <p>
     * Please note this value can be overridden by the {@link #setHttpProcessor(
     *org.apache.http.protocol.HttpProcessor)} method.
     * </p>
     *
     * @param itcp HttpResponseInterceptor
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder addInterceptorLast(final HttpResponseInterceptor itcp) {
        if (itcp == null) {
            return this;
        }
        if (responseLast == null) {
            responseLast = new LinkedList<HttpResponseInterceptor>();
        }
        responseLast.addLast(itcp);
        return this;
    }

    /**
     * Adds this protocol interceptor to the head of the protocol processing list.
     * <p>
     * Please note this value can be overridden by the {@link #setHttpProcessor(
     *org.apache.http.protocol.HttpProcessor)} method.
     *
     * @param itcp HttpRequestInterceptor
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder addInterceptorFirst(final HttpRequestInterceptor itcp) {
        if (itcp == null) {
            return this;
        }
        if (requestFirst == null) {
            requestFirst = new LinkedList<HttpRequestInterceptor>();
        }
        requestFirst.addFirst(itcp);
        return this;
    }

    /**
     * Adds this protocol interceptor to the tail of the protocol processing list.
     * <p>
     * Please note this value can be overridden by the {@link #setHttpProcessor(
     *org.apache.http.protocol.HttpProcessor)} method.
     *
     * @param itcp HttpRequestInterceptor
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder addInterceptorLast(final HttpRequestInterceptor itcp) {
        if (itcp == null) {
            return this;
        }
        if (requestLast == null) {
            requestLast = new LinkedList<HttpRequestInterceptor>();
        }
        requestLast.addLast(itcp);
        return this;
    }

    /**
     * Disables state (cookie) management.
     * <p>
     * Please note this value can be overridden by the {@link #setHttpProcessor(
     *org.apache.http.protocol.HttpProcessor)} method.
     * </p>
     *
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder disableCookieManagement() {
        this.cookieManagementDisabled = true;
        return this;
    }

    /**
     * Disables automatic content decompression.
     * <p>
     * Please note this value can be overridden by the {@link #setHttpProcessor(
     *org.apache.http.protocol.HttpProcessor)} method.
     * </p>
     *
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder disableContentCompression() {
        contentCompressionDisabled = true;
        return this;
    }

    /**
     * Disables authentication scheme caching.
     * <p>
     * Please note this value can be overridden by the {@link #setHttpProcessor(
     *org.apache.http.protocol.HttpProcessor)} method.
     * </p>
     *
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder disableAuthCaching() {
        this.authCachingDisabled = true;
        return this;
    }

    /**
     * Assigns {@link HttpProcessor} instance.
     *
     * @param httpprocessor HttpProcessor
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder setHttpProcessor(final HttpProcessor httpprocessor) {
        this.httpprocessor = httpprocessor;
        return this;
    }

    /**
     * Assigns {@link DnsResolver} instance.
     * <p>
     * Please note this value can be overridden by the {@link #setConnectionManager(HttpClientConnectionManager)} method.
     *
     * @param dnsResolver DnsResolver
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder setDnsResolver(final DnsResolver dnsResolver) {
        this.dnsResolver = dnsResolver;
        return this;
    }

    /**
     * Assigns {@link HttpRequestRetryHandler} instance.
     * <p>
     * Please note this value can be overridden by the {@link #disableAutomaticRetries()}
     * method.
     *
     * @param retryHandler HttpRequestRetryHandler
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder setRetryHandler(final HttpRequestRetryHandler retryHandler) {
        this.retryHandler = retryHandler;
        return this;
    }

    /**
     * Disables automatic request recovery and re-execution.
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder disableAutomaticRetries() {
        automaticRetriesDisabled = true;
        return this;
    }

    /**
     * Assigns default proxy value.
     * <p>
     * Please note this value can be overridden by the {@link #setRoutePlanner(
     *org.apache.http.conn.routing.HttpRoutePlanner)} method.
     * </p>
     *
     * @param proxy HttpHost
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder setProxy(final HttpHost proxy) {
        this.proxy = proxy;
        return this;
    }

    /**
     * Assigns {@link HttpRoutePlanner} instance.
     *
     * @param routePlanner HttpRoutePlanner
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder setRoutePlanner(final HttpRoutePlanner routePlanner) {
        this.routePlanner = routePlanner;
        return this;
    }

    /**
     * Assigns {@link RedirectStrategy} instance.
     * <p>
     * Please note this value can be overridden by the {@link #disableRedirectHandling()}
     * method.
     * </p>
     *
     * @param redirectStrategy RedirectStrategys
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder setRedirectStrategy(final RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
        return this;
    }

    /**
     * Disables automatic redirect handling.
     *
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder disableRedirectHandling() {
        redirectHandlingDisabled = true;
        return this;
    }

    /**
     * Assigns {@link ConnectionBackoffStrategy} instance.
     *
     * @param connectionBackoffStrategy ConnectionBackoffStrategy
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder setConnectionBackoffStrategy(
            final ConnectionBackoffStrategy connectionBackoffStrategy) {
        this.connectionBackoffStrategy = connectionBackoffStrategy;
        return this;
    }

    /**
     * Assigns {@link BackoffManager} instance.
     *
     * @param backoffManager BackoffManager
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder setBackoffManager(final BackoffManager backoffManager) {
        this.backoffManager = backoffManager;
        return this;
    }

    /**
     * Assigns {@link ServiceUnavailableRetryStrategy} instance.
     *
     * @param serviceUnavailStrategy ServiceUnavailableRetryStrategy
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder setServiceUnavailableRetryStrategy(
            final ServiceUnavailableRetryStrategy serviceUnavailStrategy) {
        this.serviceUnavailStrategy = serviceUnavailStrategy;
        return this;
    }

    /**
     * Assigns default {@link CookieStore} instance which will be used for
     * request execution if not explicitly set in the client execution context.
     *
     * @param cookieStore CookieStore
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder setDefaultCookieStore(final CookieStore cookieStore) {
        this.cookieStore = cookieStore;
        return this;
    }

    /**
     * Assigns default {@link CredentialsProvider} instance which will be used
     * for request execution if not explicitly set in the client execution
     * context.
     *
     * @param credentialsProvider CredentialsProvider
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder setDefaultCredentialsProvider(
            final CredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
        return this;
    }

    /**
     * Assigns default {@link org.apache.http.auth.AuthScheme} registry which will
     * be used for request execution if not explicitly set in the client execution
     * context.
     *
     * @param authSchemeRegistry collection of {@link org.apache.http.auth.AuthSchemeProvider} in {@link org.apache.http.config.Lookup}
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder setDefaultAuthSchemeRegistry(
            final Lookup<AuthSchemeProvider> authSchemeRegistry) {
        this.authSchemeRegistry = authSchemeRegistry;
        return this;
    }

    /**
     * Assigns default {@link org.apache.http.cookie.CookieSpec} registry which will
     * be used for request execution if not explicitly set in the client execution
     * context.
     *
     * @param cookieSpecRegistry collection of {@link org.apache.http.cookie.CookieSpecProvider} in {@link org.apache.http.config.Lookup}
     * @return GreyMatterHttpClientBuilder
     * @see CookieSpecRegistries
     */
    public final GreyMatterHttpClientBuilder setDefaultCookieSpecRegistry(
            final Lookup<CookieSpecProvider> cookieSpecRegistry) {
        this.cookieSpecRegistry = cookieSpecRegistry;
        return this;
    }


    /**
     * Assigns a map of {@link org.apache.http.client.entity.InputStreamFactory}s
     * to be used for automatic content decompression.
     *
     * @param contentDecoderMap map of InputStreamFactory
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder setContentDecoderRegistry(
            final Map<String, InputStreamFactory> contentDecoderMap) {
        this.contentDecoderMap = contentDecoderMap;
        return this;
    }

    /**
     * Assigns default {@link RequestConfig} instance which will be used
     * for request execution if not explicitly set in the client execution
     * context.
     *
     * @param config RequestConfig
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder setDefaultRequestConfig(final RequestConfig config) {
        this.defaultRequestConfig = config;
        return this;
    }

    /**
     * Use system properties when creating and configuring default
     * implementations.
     *
     * @return GreyMatterHttpClientBuilder
     */
    public final GreyMatterHttpClientBuilder useSystemProperties() {
        this.systemProperties = true;
        return this;
    }

    /**
     * Makes this instance of HttpClient proactively evict expired connections from the
     * connection pool using a background thread.
     * <p>
     * One MUST explicitly close HttpClient with {@link CloseableHttpClient#close()} in order
     * to stop and release the background thread.
     * <p>
     * Please note this method has no effect if the instance of HttpClient is configuted to
     * use a shared connection manager.
     * <p>
     * Please note this method may not be used when the instance of HttpClient is created
     * inside an EJB container.
     * @return GreyMatterHttpClientBuilder
     * @see #setConnectionManagerShared(boolean)
     * @see org.apache.http.conn.HttpClientConnectionManager#closeExpiredConnections()
     * @since 4.4
     */
    public final GreyMatterHttpClientBuilder evictExpiredConnections() {
        evictExpiredConnections = true;
        return this;
    }

    /**
     * Makes this instance of HttpClient proactively evict idle connections from the
     * connection pool using a background thread.
     * <p>
     * One MUST explicitly close HttpClient with {@link CloseableHttpClient#close()} in order
     * to stop and release the background thread.
     * <p>
     * Please note this method has no effect if the instance of HttpClient is configuted to
     * use a shared connection manager.
     * <p>
     * Please note this method may not be used when the instance of HttpClient is created
     * inside an EJB container.
     *
     * @param maxIdleTime     maximum time persistent connections can stay idle while kept alive
     *                        in the connection pool. Connections whose inactivity period exceeds this value will
     *                        get closed and evicted from the pool.
     * @param maxIdleTimeUnit time unit for the above parameter.
     * @return GreyMatterHttpClientBuilder
     * @see #setConnectionManagerShared(boolean)
     * @see org.apache.http.conn.HttpClientConnectionManager#closeExpiredConnections()
     * @since 4.4
     * @deprecated (4.5) use {@link #evictIdleConnections(long, TimeUnit)}
     */
    @Deprecated
    public final GreyMatterHttpClientBuilder evictIdleConnections(final Long maxIdleTime, final TimeUnit maxIdleTimeUnit) {
        return evictIdleConnections(maxIdleTime.longValue(), maxIdleTimeUnit);
    }

    /**
     * Makes this instance of HttpClient proactively evict idle connections from the
     * connection pool using a background thread.
     * <p>
     * One MUST explicitly close HttpClient with {@link CloseableHttpClient#close()} in order
     * to stop and release the background thread.
     * <p>
     * Please note this method has no effect if the instance of HttpClient is configuted to
     * use a shared connection manager.
     * <p>
     * Please note this method may not be used when the instance of HttpClient is created
     * inside an EJB container.
     *
     * @param maxIdleTime     maximum time persistent connections can stay idle while kept alive
     *                        in the connection pool. Connections whose inactivity period exceeds this value will
     *                        get closed and evicted from the pool.
     * @param maxIdleTimeUnit time unit for the above parameter.
     * @return GreyMatterHttpClientBuilder
     * @see #setConnectionManagerShared(boolean)
     * @see org.apache.http.conn.HttpClientConnectionManager#closeExpiredConnections()
     * @since 4.4
     */
    public final GreyMatterHttpClientBuilder evictIdleConnections(final long maxIdleTime, final TimeUnit maxIdleTimeUnit) {
        this.evictIdleConnections = true;
        this.maxIdleTime = maxIdleTime;
        this.maxIdleTimeUnit = maxIdleTimeUnit;
        return this;
    }

    /**
     * Disables the default user agent set by this builder if none has been provided by the user.
     * @return GreyMatterHttpClientBuilder
     * @since 4.5.7
     */
    public final GreyMatterHttpClientBuilder disableDefaultUserAgent() {
        this.defaultUserAgentDisabled = true;
        return this;
    }

    /**
     * Produces an instance of {@link ClientExecChain} to be used as a main exec.
     * <p>
     * Default implementation produces an instance of {@link MainClientExec}
     * </p>
     * <p>
     * For internal use.
     * </p>
     * @param requestExec HttpRequestExecutor
     * @param connManager HttpClientConnectionManager
     * @param reuseStrategy ConnectionReuseStrategy
     * @param keepAliveStrategy ConnectionKeepAliveStrategy
     * @param proxyHttpProcessor HttpProcessor
     * @param targetAuthStrategy AuthenticationStrategy
     * @param proxyAuthStrategy AuthenticationStrategy
     * @param userTokenHandler UserTokenHandler
     * @return ClientExecChain
     * @since 4.4
     */
    protected ClientExecChain createMainExec(
            final HttpRequestExecutor requestExec,
            final HttpClientConnectionManager connManager,
            final ConnectionReuseStrategy reuseStrategy,
            final ConnectionKeepAliveStrategy keepAliveStrategy,
            final HttpProcessor proxyHttpProcessor,
            final AuthenticationStrategy targetAuthStrategy,
            final AuthenticationStrategy proxyAuthStrategy,
            final UserTokenHandler userTokenHandler) {
        return new MainClientExec(
                requestExec,
                connManager,
                reuseStrategy,
                keepAliveStrategy,
                proxyHttpProcessor,
                targetAuthStrategy,
                proxyAuthStrategy,
                userTokenHandler);
    }

    /**
     * For internal use.
     */
    protected ClientExecChain decorateMainExec(final ClientExecChain mainExec) {
        return mainExec;
    }

    /**
     * For internal use.
     */
    protected ClientExecChain decorateProtocolExec(final ClientExecChain protocolExec) {
        return protocolExec;
    }

    /**
     * For internal use.
     */
    protected void addCloseable(final Closeable closeable) {
        if (closeable == null) {
            return;
        }
        if (closeables == null) {
            closeables = new ArrayList<Closeable>();
        }
        closeables.add(closeable);
    }

    private static String[] split(final String s) {
        if (TextUtils.isBlank(s)) {
            return null;
        }
        return s.split(" *, *");
    }

    public GreyMatterCloseableHttpClient build() {
        // Create main request executor
        // We copy the instance fields to avoid changing them, and rename to avoid accidental use of the wrong version
        PublicSuffixMatcher publicSuffixMatcherCopy = this.publicSuffixMatcher;
        if (publicSuffixMatcherCopy == null) {
            publicSuffixMatcherCopy = PublicSuffixMatcherLoader.getDefault();
        }

        HttpRequestExecutor requestExecCopy = this.requestExec;
        if (requestExecCopy == null) {
            requestExecCopy = new HttpRequestExecutor();
        }
        HttpClientConnectionManager connManagerCopy = this.connManager;
        if (connManagerCopy == null) {
            LayeredConnectionSocketFactory sslSocketFactoryCopy = this.sslSocketFactory;
            if (sslSocketFactoryCopy == null) {
                final String[] supportedProtocols = systemProperties ? split(
                        System.getProperty("https.protocols")) : null;
                final String[] supportedCipherSuites = systemProperties ? split(
                        System.getProperty("https.cipherSuites")) : null;
                HostnameVerifier hostnameVerifierCopy = this.hostnameVerifier;
                if (hostnameVerifierCopy == null) {
                    hostnameVerifierCopy = new DefaultHostnameVerifier(publicSuffixMatcherCopy);
                }
                if (sslContext != null) {
                    sslSocketFactoryCopy = new SSLConnectionSocketFactory(
                            sslContext, supportedProtocols, supportedCipherSuites, hostnameVerifierCopy);
                } else {
                    if (systemProperties) {
                        sslSocketFactoryCopy = new SSLConnectionSocketFactory(
                                (SSLSocketFactory) SSLSocketFactory.getDefault(),
                                supportedProtocols, supportedCipherSuites, hostnameVerifierCopy);
                    } else {
                        sslSocketFactoryCopy = new SSLConnectionSocketFactory(
                                SSLContexts.createDefault(),
                                hostnameVerifierCopy);
                    }
                }
            }
            @SuppressWarnings("resource") final PoolingHttpClientConnectionManager poolingmgr = new PoolingHttpClientConnectionManager(
                    RegistryBuilder.<ConnectionSocketFactory>create()
                            .register("http", PlainConnectionSocketFactory.getSocketFactory())
                            .register("https", sslSocketFactoryCopy)
                            .build(),
                    null,
                    null,
                    dnsResolver,
                    connTimeToLive,
                    connTimeToLiveTimeUnit != null ? connTimeToLiveTimeUnit : TimeUnit.MILLISECONDS);
            if (defaultSocketConfig != null) {
                poolingmgr.setDefaultSocketConfig(defaultSocketConfig);
            }
            if (defaultConnectionConfig != null) {
                poolingmgr.setDefaultConnectionConfig(defaultConnectionConfig);
            }
            if (systemProperties) {
                String s = System.getProperty("http.keepAlive", "true");
                if ("true".equalsIgnoreCase(s)) {
                    s = System.getProperty("http.maxConnections", "5");
                    final int max = Integer.parseInt(s);
                    poolingmgr.setDefaultMaxPerRoute(max);
                    poolingmgr.setMaxTotal(2 * max);
                }
            }
            if (maxConnTotal > 0) {
                poolingmgr.setMaxTotal(maxConnTotal);
            }
            if (maxConnPerRoute > 0) {
                poolingmgr.setDefaultMaxPerRoute(maxConnPerRoute);
            }
            connManagerCopy = poolingmgr;
        }
        ConnectionReuseStrategy reuseStrategyCopy = this.reuseStrategy;
        if (reuseStrategyCopy == null) {
            if (systemProperties) {
                final String s = System.getProperty("http.keepAlive", "true");
                if ("true".equalsIgnoreCase(s)) {
                    reuseStrategyCopy = DefaultClientConnectionReuseStrategy.INSTANCE;
                } else {
                    reuseStrategyCopy = NoConnectionReuseStrategy.INSTANCE;
                }
            } else {
                reuseStrategyCopy = DefaultClientConnectionReuseStrategy.INSTANCE;
            }
        }
        ConnectionKeepAliveStrategy keepAliveStrategyCopy = this.keepAliveStrategy;
        if (keepAliveStrategyCopy == null) {
            keepAliveStrategyCopy = DefaultConnectionKeepAliveStrategy.INSTANCE;
        }
        AuthenticationStrategy targetAuthStrategyCopy = this.targetAuthStrategy;
        if (targetAuthStrategyCopy == null) {
            targetAuthStrategyCopy = TargetAuthenticationStrategy.INSTANCE;
        }
        AuthenticationStrategy proxyAuthStrategyCopy = this.proxyAuthStrategy;
        if (proxyAuthStrategyCopy == null) {
            proxyAuthStrategyCopy = ProxyAuthenticationStrategy.INSTANCE;
        }
        UserTokenHandler userTokenHandlerCopy = this.userTokenHandler;
        if (userTokenHandlerCopy == null) {
            if (!connectionStateDisabled) {
                userTokenHandlerCopy = DefaultUserTokenHandler.INSTANCE;
            } else {
                userTokenHandlerCopy = NoopUserTokenHandler.INSTANCE;
            }
        }

        String userAgentCopy = this.userAgent;
        if (userAgentCopy == null) {
            if (systemProperties) {
                userAgentCopy = System.getProperty("http.agent");
            }
            if (userAgentCopy == null && !defaultUserAgentDisabled) {
                userAgentCopy = VersionInfo.getUserAgent("Apache-HttpClient",
                        "org.apache.http.client", getClass());
            }
        }

        ClientExecChain execChain = createMainExec(
                requestExecCopy,
                connManagerCopy,
                reuseStrategyCopy,
                keepAliveStrategyCopy,
                new ImmutableHttpProcessor(new RequestTargetHost(), new RequestUserAgent(userAgentCopy)),
                targetAuthStrategyCopy,
                proxyAuthStrategyCopy,
                userTokenHandlerCopy);

        execChain = decorateMainExec(execChain);

        HttpProcessor httpprocessorCopy = this.httpprocessor;
        if (httpprocessorCopy == null) {

            final HttpProcessorBuilder b = HttpProcessorBuilder.create();
            if (requestFirst != null) {
                for (final HttpRequestInterceptor i : requestFirst) {
                    b.addFirst(i);
                }
            }
            if (responseFirst != null) {
                for (final HttpResponseInterceptor i : responseFirst) {
                    b.addFirst(i);
                }
            }
            b.addAll(
                    new RequestDefaultHeaders(defaultHeaders),
                    new RequestContent(),
                    new RequestTargetHost(),
                    new RequestClientConnControl(),
                    new RequestUserAgent(userAgentCopy),
                    new RequestExpectContinue());
            if (!cookieManagementDisabled) {
                b.add(new RequestAddCookies());
            }
            if (!contentCompressionDisabled) {
                if (contentDecoderMap != null) {
                    final List<String> encodings = new ArrayList<String>(contentDecoderMap.keySet());
                    Collections.sort(encodings);
                    b.add(new RequestAcceptEncoding(encodings));
                } else {
                    b.add(new RequestAcceptEncoding());
                }
            }
            if (!authCachingDisabled) {
                b.add(new RequestAuthCache());
            }
            if (!cookieManagementDisabled) {
                b.add(new ResponseProcessCookies());
            }
            if (!contentCompressionDisabled) {
                if (contentDecoderMap != null) {
                    final RegistryBuilder<InputStreamFactory> b2 = RegistryBuilder.create();
                    for (final Map.Entry<String, InputStreamFactory> entry : contentDecoderMap.entrySet()) {
                        b2.register(entry.getKey(), entry.getValue());
                    }
                    b.add(new ResponseContentEncoding(b2.build()));
                } else {
                    b.add(new ResponseContentEncoding());
                }
            }
            if (requestLast != null) {
                for (final HttpRequestInterceptor i : requestLast) {
                    b.addLast(i);
                }
            }
            if (responseLast != null) {
                for (final HttpResponseInterceptor i : responseLast) {
                    b.addLast(i);
                }
            }
            httpprocessorCopy = b.build();
        }
        execChain = new ProtocolExec(execChain, httpprocessorCopy);

        execChain = decorateProtocolExec(execChain);

        // Add request retry executor, if not disabled
        if (!automaticRetriesDisabled) {
            HttpRequestRetryHandler retryHandlerCopy = this.retryHandler;
            if (retryHandlerCopy == null) {
                retryHandlerCopy = DefaultHttpRequestRetryHandler.INSTANCE;
            }
            execChain = new RetryExec(execChain, retryHandlerCopy);
        }

        HttpRoutePlanner routePlannerCopy = this.routePlanner;
        if (routePlannerCopy == null) {
            SchemePortResolver schemePortResolverCopy = this.schemePortResolver;
            if (schemePortResolverCopy == null) {
                schemePortResolverCopy = DefaultSchemePortResolver.INSTANCE;
            }
            if (proxy != null) {
                routePlannerCopy = new DefaultProxyRoutePlanner(proxy, schemePortResolverCopy);
            } else if (systemProperties) {
                routePlannerCopy = new SystemDefaultRoutePlanner(
                        schemePortResolverCopy, ProxySelector.getDefault());
            } else {
                routePlannerCopy = new DefaultRoutePlanner(schemePortResolverCopy);
            }
        }

        // Optionally, add service unavailable retry executor
        final ServiceUnavailableRetryStrategy serviceUnavailStrategyCopy = this.serviceUnavailStrategy;
        if (serviceUnavailStrategyCopy != null) {
            execChain = new ServiceUnavailableRetryExec(execChain, serviceUnavailStrategyCopy);
        }

        // Add redirect executor, if not disabled
        if (!redirectHandlingDisabled) {
            RedirectStrategy redirectStrategyCopy = this.redirectStrategy;
            if (redirectStrategyCopy == null) {
                redirectStrategyCopy = DefaultRedirectStrategy.INSTANCE;
            }
            execChain = new RedirectExec(execChain, routePlannerCopy, redirectStrategyCopy);
        }

        // Optionally, add connection back-off executor
        if (this.backoffManager != null && this.connectionBackoffStrategy != null) {
            execChain = new BackoffStrategyExec(execChain, this.connectionBackoffStrategy, this.backoffManager);
        }

        Lookup<AuthSchemeProvider> authSchemeRegistryCopy = this.authSchemeRegistry;
        if (authSchemeRegistryCopy == null) {
            authSchemeRegistryCopy = RegistryBuilder.<AuthSchemeProvider>create()
                    .register(AuthSchemes.BASIC, new BasicSchemeFactory())
                    .register(AuthSchemes.DIGEST, new DigestSchemeFactory())
                    .register(AuthSchemes.NTLM, new NTLMSchemeFactory())
                    .register(AuthSchemes.SPNEGO, new SPNegoSchemeFactory())
                    .register(AuthSchemes.KERBEROS, new KerberosSchemeFactory())
                    .build();
        }
        Lookup<CookieSpecProvider> cookieSpecRegistryCopy = this.cookieSpecRegistry;
        if (cookieSpecRegistryCopy == null) {
            cookieSpecRegistryCopy = CookieSpecRegistries.createDefault(publicSuffixMatcherCopy);
        }

        CookieStore defaultCookieStore = this.cookieStore;
        if (defaultCookieStore == null) {
            defaultCookieStore = new BasicCookieStore();
        }

        CredentialsProvider defaultCredentialsProvider = this.credentialsProvider;
        if (defaultCredentialsProvider == null) {
            if (systemProperties) {
                defaultCredentialsProvider = new SystemDefaultCredentialsProvider();
            } else {
                defaultCredentialsProvider = new BasicCredentialsProvider();
            }
        }

        List<Closeable> closeablesCopy = closeables != null ? new ArrayList<Closeable>(closeables) : null;
        if (!this.connManagerShared) {
            if (closeablesCopy == null) {
                closeablesCopy = new ArrayList<Closeable>(1);
            }
            final HttpClientConnectionManager cm = connManagerCopy;

            if (evictExpiredConnections || evictIdleConnections) {
                final IdleConnectionEvictor connectionEvictor = new IdleConnectionEvictor(cm,
                        maxIdleTime > 0 ? maxIdleTime : 10, maxIdleTimeUnit != null ? maxIdleTimeUnit : TimeUnit.SECONDS,
                        maxIdleTime, maxIdleTimeUnit);
                closeablesCopy.add(new Closeable() {

                    @Override
                    public void close() throws IOException {
                        connectionEvictor.shutdown();
                        try {
                            connectionEvictor.awaitTermination(1L, TimeUnit.SECONDS);
                        } catch (final InterruptedException interrupted) {
                            Thread.currentThread().interrupt();
                        }
                    }

                });
                connectionEvictor.start();
            }
            closeablesCopy.add(new Closeable() {

                @Override
                public void close() throws IOException {
                    cm.shutdown();
                }

            });
        }

        return new GreyMatterInternalHttpClient(
                execChain,
                connManagerCopy,
                routePlannerCopy,
                cookieSpecRegistryCopy,
                authSchemeRegistryCopy,
                defaultCookieStore,
                defaultCredentialsProvider,
                defaultRequestConfig != null ? defaultRequestConfig : RequestConfig.DEFAULT,
                closeablesCopy);
    }
}
