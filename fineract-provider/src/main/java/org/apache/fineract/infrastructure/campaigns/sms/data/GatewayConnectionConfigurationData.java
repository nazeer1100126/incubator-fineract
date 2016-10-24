package org.apache.fineract.infrastructure.campaigns.sms.data;

public class GatewayConnectionConfigurationData {

    private final Long id;
    private final String connectionName;
    private final String hostName;
    private final int portNumber;
    private final String endPoint;
    private final String userName;
    private final String password;
    private final boolean sslEnabled;

    public GatewayConnectionConfigurationData(final Long id, final String connectionName, final String hostName, final int portNumber,
            final String endPoint, final String userName, final String password, final boolean sslEnabled) {
        this.id = id;
        this.connectionName = connectionName;
        this.hostName = hostName;
        this.portNumber = portNumber;
        this.endPoint = endPoint;
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

    public int getPortNumber() {
        return this.portNumber;
    }

    public String getEndPoint() {
        return this.endPoint;
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
