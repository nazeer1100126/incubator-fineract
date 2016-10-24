package org.apache.fineract.infrastructure.campaigns.sms.domain;

import org.apache.fineract.infrastructure.campaigns.sms.constants.SmsCampaignStatus;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public class SmsCampaignStatusEnumerations {
    public static EnumOptionData status(final Integer statusId) {
        return status(SmsCampaignStatus.fromInt(statusId));
    }

    public static EnumOptionData status(final SmsCampaignStatus status) {
        EnumOptionData optionData = new EnumOptionData(SmsCampaignStatus.INVALID.getValue().longValue(),
                SmsCampaignStatus.INVALID.getCode(), "Invalid");
        switch (status) {
            case INVALID:
                optionData = new EnumOptionData
                (SmsCampaignStatus.INVALID.getValue().longValue(),
                        SmsCampaignStatus.INVALID.getCode(), "Invalid");
                break;
            case PENDING:
                optionData = new EnumOptionData(SmsCampaignStatus.PENDING.getValue().longValue(),
                        SmsCampaignStatus.PENDING.getCode(), "Pending");
                break;
            case ACTIVE:
                optionData = new EnumOptionData(SmsCampaignStatus.ACTIVE.getValue().longValue(), SmsCampaignStatus.ACTIVE.getCode(),
                        "active");
                break;
            case CLOSED:
                optionData = new EnumOptionData(SmsCampaignStatus.CLOSED.getValue().longValue(),
                        SmsCampaignStatus.CLOSED.getCode(), "closed");
                break;

        }

        return optionData;
    }
}
