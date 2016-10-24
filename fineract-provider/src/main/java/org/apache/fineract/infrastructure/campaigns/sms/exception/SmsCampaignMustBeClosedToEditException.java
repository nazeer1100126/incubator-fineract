package org.apache.fineract.infrastructure.campaigns.sms.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class SmsCampaignMustBeClosedToEditException extends AbstractPlatformDomainRuleException {

    public SmsCampaignMustBeClosedToEditException(final Long resourceId) {
        super("error.msg.sms.campaign.cannot.be.updated",
                "Campaign with identifier " + resourceId + " cannot be updated as it is not in `Closed` state.", resourceId);
    }
}
