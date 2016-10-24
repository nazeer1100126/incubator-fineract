package org.apache.fineract.infrastructure.campaigns.sms.service;

import java.util.Collection;

import org.apache.fineract.infrastructure.campaigns.sms.data.SmsProviderData;
import org.apache.fineract.infrastructure.campaigns.sms.data.TriggerTypeWithSubTypesData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public interface SmsCampaignDropdownReadPlatformService {

    Collection<EnumOptionData> retrieveCampaignTriggerTypes();

    Collection<SmsProviderData> retrieveSmsProviders();

    Collection<EnumOptionData> retrieveCampaignTypes();

    Collection<EnumOptionData> retrieveWeeks();

    Collection<EnumOptionData> retrieveMonths();

    Collection<EnumOptionData> retrivePeriodFrequencyTypes();

    Collection<TriggerTypeWithSubTypesData> getTriggerTypeAndSubTypes();
}
