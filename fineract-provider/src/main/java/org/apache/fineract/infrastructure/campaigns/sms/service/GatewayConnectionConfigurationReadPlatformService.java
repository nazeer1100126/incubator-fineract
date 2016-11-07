package org.apache.fineract.infrastructure.campaigns.sms.service;

import org.apache.fineract.infrastructure.campaigns.sms.data.MessageGatewayConfigurationData;

public interface GatewayConnectionConfigurationReadPlatformService {

    MessageGatewayConfigurationData retrieveOneByConnectionName(String connectionName);

}
