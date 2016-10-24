package org.apache.fineract.infrastructure.campaigns.sms.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class GatewayConnectionConfigurationNotFound extends AbstractPlatformResourceNotFoundException {

    public GatewayConnectionConfigurationNotFound(String connectionName) {
        super("error.msg.gateway.config.not.found", "Gateway Connection Configuration with name `" + connectionName + "` does not exist",
                connectionName);
    }

}
