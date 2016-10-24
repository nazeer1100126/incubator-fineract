package org.apache.fineract.infrastructure.campaigns.sms.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformServiceUnavailableException;

public class ConnectionFailureException extends AbstractPlatformServiceUnavailableException {

    public ConnectionFailureException(final String connectionName) {
        super("error.msg.unable.to.connect.to.intermediate.server", "Unable to connect to the server with connection name - "
                + connectionName, connectionName);
    }

}
