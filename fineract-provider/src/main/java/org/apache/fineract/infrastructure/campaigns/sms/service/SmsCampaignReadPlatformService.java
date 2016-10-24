package org.apache.fineract.infrastructure.campaigns.sms.service;

import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.campaigns.sms.data.SmsCampaignData;

public interface SmsCampaignReadPlatformService {

    public SmsCampaignData retrieveOne(final Long campaignId);

    public List<SmsCampaignData> retrieveAll();

    public SmsCampaignData retrieveTemplate(final String reportType);

    Collection<SmsCampaignData> retrieveAllScheduleActiveCampaign();

}
