package org.apache.fineract.infrastructure.campaigns.sms.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.campaigns.sms.service.SmsCampaignWritePlatformService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "SMSCAMPAIGN", action = "UPDATE")
public class UpdateSmsCampaignCommandHandler implements NewCommandSourceHandler {

    private SmsCampaignWritePlatformService smsCampaignWritePlatformService;

    @Autowired
    public UpdateSmsCampaignCommandHandler(final SmsCampaignWritePlatformService smsCampaignWritePlatformService) {
        this.smsCampaignWritePlatformService = smsCampaignWritePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(JsonCommand command) {
        return this.smsCampaignWritePlatformService.update(command.entityId(), command);
    }
}
