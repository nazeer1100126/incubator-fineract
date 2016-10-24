package org.apache.fineract.infrastructure.campaigns.sms.data;

import java.util.List;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public class TriggerTypeWithSubTypesData {

    private final EnumOptionData actualTriggerType;
    private final List<EnumOptionData> triggerSubTypes;

    public TriggerTypeWithSubTypesData(final EnumOptionData actualTriggerType, List<EnumOptionData> triggerSubTypes) {
        this.actualTriggerType = actualTriggerType;
        this.triggerSubTypes = triggerSubTypes;
    }

    public EnumOptionData getActualTriggerType() {
        return this.actualTriggerType;
    }

    public List<EnumOptionData> getTriggerSubTypes() {
        return this.triggerSubTypes;
    }

}
