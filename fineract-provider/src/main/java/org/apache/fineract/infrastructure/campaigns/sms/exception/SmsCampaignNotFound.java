package org.apache.fineract.infrastructure.campaigns.sms.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class SmsCampaignNotFound extends AbstractPlatformResourceNotFoundException{

    public SmsCampaignNotFound(final Long resourceId) {
        super("error.msg.sms.campaign.identifier.not.found", "Sms Campaign with identifier `" + resourceId + "` does not exist", resourceId);
    }
}
