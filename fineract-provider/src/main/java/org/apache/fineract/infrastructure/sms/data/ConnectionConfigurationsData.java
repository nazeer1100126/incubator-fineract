package org.apache.fineract.infrastructure.sms.data;

public class ConnectionConfigurationsData {

    private final Long id;
    private final String connectionName;
    private final String hostName;
    private final String endPoint;
    private final String portNumber;
    private final String userName;
    private final String password;
    private final boolean sslEnabled;

    public ConnectionConfigurationsData(final Long id, final String connectionName, final String hostName, final String endPoint,
            final String portNumber, final String userName, final String password, final boolean sslEnabled) {
        this.id = id;
        this.connectionName = connectionName;
        this.hostName = hostName;
        this.endPoint = endPoint;
        this.portNumber = portNumber;
        this.userName = userName;
        this.password = password;
        this.sslEnabled = sslEnabled;
    }

    public Long getId() {
        return this.id;
    }

    public String getConnectionName() {
        return this.connectionName;
    }

    public String getHostName() {
        return this.hostName;
    }

    public String getEndPoint() {
        return this.endPoint;
    }

    public String getPortNumber() {
        return this.portNumber;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getPassword() {
        return this.password;
    }

    public boolean isSslEnabled() {
        return this.sslEnabled;
    }

}
