package org.apache.fineract.infrastructure.campaigns.sms.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.campaigns.sms.service.SmsCampaignWritePlatformService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "SMSCAMPAIGN", action = "CLOSE")
public class CloseSmsCampaignCommandHandler implements NewCommandSourceHandler {
    private final SmsCampaignWritePlatformService smsCampaignWritePlatformService;

   @Autowired
    public CloseSmsCampaignCommandHandler(final SmsCampaignWritePlatformService smsCampaignWritePlatformService) {
        this.smsCampaignWritePlatformService = smsCampaignWritePlatformService;
    }

    @Override
    public CommandProcessingResult processCommand(JsonCommand command) {
       return this.smsCampaignWritePlatformService.closeSmsCampaign(command.entityId(), command);
    }
}
