package org.apache.fineract.infrastructure.sms.scheduler;

import java.util.Collection;
import java.util.Map;

import org.apache.fineract.infrastructure.campaigns.sms.domain.SmsCampaign;
import org.apache.fineract.infrastructure.sms.domain.SmsMessage;

/**
 * Scheduled Job service interface for SMS message
 **/
public interface SmsMessageScheduledJobService {

    /**
     * sends a batch of SMS messages to the SMS gateway
     **/
    public void sendMessagesToGateway();

    /**
     * sends triggered batch SMS messages to SMS gateway
     * @param smsDataMap
     */
    public void sendTriggeredMessages(Map<SmsCampaign, Collection<SmsMessage>> smsDataMap);

    /**
     * get delivery report from the SMS gateway
     **/
    public void getDeliveryReports();
}