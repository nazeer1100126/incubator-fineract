package org.apache.fineract.infrastructure.campaigns.sms.service;

import org.apache.fineract.infrastructure.campaigns.sms.data.GatewayConnectionConfigurationData;

public interface GatewayConnectionConfigurationReadPlatformService {

    GatewayConnectionConfigurationData retrieveOneByConnectionName(String connectionName);

}
