package org.apache.fineract.infrastructure.campaigns.sms.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class SmsCampaignMustBeClosedToBeDeletedException  extends AbstractPlatformDomainRuleException {

    public SmsCampaignMustBeClosedToBeDeletedException(final Long resourceId) {
        super("error.msg.sms.campaign.cannot.be.deleted",
                "Campaign with identifier " + resourceId + " cannot be deleted as it is not in `Closed` state.", resourceId);    }
}
