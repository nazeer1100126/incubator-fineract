package org.apache.fineract.infrastructure.campaigns.constants;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum CampaignType {
    INVALID(0, "campaignType.invalid"), SMS(1, "campaignType.sms");

    private Integer value;
    private String code;

    private CampaignType(Integer value, String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }

    public static CampaignType fromInt(final Integer typeValue) {
        CampaignType type = null;
        switch (typeValue) {
            case 0:
                type = INVALID;
            break;
            case 1:
                type = SMS;
            break;
        }
        return type;
    }
    
    public static EnumOptionData campaignType(final Integer campaignTypeId) {
        return campaignType(CampaignType.fromInt(campaignTypeId));
    }

    public static EnumOptionData campaignType(final CampaignType campaignType) {
        EnumOptionData optionData = new EnumOptionData(CampaignType.INVALID.getValue().longValue(), CampaignType.INVALID.getCode(),
                "Invalid");
        switch (campaignType) {
            case INVALID:
                optionData = new EnumOptionData(CampaignType.INVALID.getValue().longValue(), CampaignType.INVALID.getCode(), "Invalid");
            break;
            case SMS:
                optionData = new EnumOptionData(CampaignType.SMS.getValue().longValue(), CampaignType.SMS.getCode(), "SMS");
            break;
        }
        return optionData;
    }

    public boolean isSms() {
        return this.value.equals(CampaignType.SMS.getValue());
    }
}
