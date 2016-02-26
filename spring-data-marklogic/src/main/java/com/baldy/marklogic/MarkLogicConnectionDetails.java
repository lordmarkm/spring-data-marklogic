package com.baldy.marklogic;

import org.springframework.core.style.ToStringCreator;

/**
 *
 * @author Mark Baldwin B. Martinez on Feb 23, 2016
 *
 */
public class MarkLogicConnectionDetails {

    private String username;
    private String password;
    private String host;
    private Integer port;
    private String authenticationType;

    @Override
    public String toString() {
        return new ToStringCreator(this)
            .append("un", username)
            .append("pw", password)
            .append("host", host)
            .append("port", port)
            .append("auth", authenticationType)
            .toString();
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getHost() {
        return host;
    }
    public void setHost(String host) {
        this.host = host;
    }
    public Integer getPort() {
        return port;
    }
    public void setPort(Integer port) {
        this.port = port;
    }
    public String getAuthenticationType() {
        return authenticationType != null ? authenticationType.toUpperCase() : "";
    }
    public void setAuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }

}
