package org.apache.fineract.infrastructure.campaigns.sms.data;

public class CampaignPreviewData {

    private final String campaignMessage;

    private final Integer totalNumberOfMessages;

    public CampaignPreviewData(String campaignMessage, Integer totalNumberOfMessages) {
        this.campaignMessage = campaignMessage;
        this.totalNumberOfMessages = totalNumberOfMessages;
    }

    public String getCampaignMessage() {
        return campaignMessage;
    }

    public Integer getTotalNumberOfMessages() {
        return totalNumberOfMessages;
    }
}
