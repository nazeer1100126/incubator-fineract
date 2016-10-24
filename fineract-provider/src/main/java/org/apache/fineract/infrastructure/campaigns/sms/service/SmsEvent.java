package org.apache.fineract.infrastructure.campaigns.sms.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.fineract.infrastructure.campaigns.sms.constants.SmsCampaignStatus;
import org.apache.fineract.infrastructure.campaigns.sms.constants.SmsCampaignTriggerType;
import org.apache.fineract.infrastructure.campaigns.sms.data.CampaignTriggerWithSubTypes.CampaignTriggerSubType;
import org.apache.fineract.infrastructure.campaigns.sms.domain.SmsCampaign;
import org.apache.fineract.infrastructure.campaigns.sms.domain.SmsCampaignRepository;
import org.apache.fineract.infrastructure.sms.domain.SmsMessage;
import org.apache.fineract.infrastructure.sms.scheduler.SmsMessageScheduledJobService;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.apache.fineract.portfolio.common.service.BusinessEventListner;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SmsEvent {

    private final BusinessEventNotifierService businessEventNotifierService;
    private final SmsCampaignWritePlatformService smsCampaignWritePlatformService;
    private final SmsCampaignRepository smsCampaignRepository;
    private final SmsMessageScheduledJobService smsMessageScheduledJobService;

    @Autowired
    public SmsEvent(final BusinessEventNotifierService businessEventNotifierService,
            final SmsCampaignWritePlatformService smsCampaignWritePlatformService, final SmsCampaignRepository SmsCampaignRepository,
            final SmsMessageScheduledJobService smsMessageScheduledJobService) {
        this.businessEventNotifierService = businessEventNotifierService;
        this.smsCampaignWritePlatformService = smsCampaignWritePlatformService;
        this.smsCampaignRepository = SmsCampaignRepository;
        this.smsMessageScheduledJobService = smsMessageScheduledJobService;
    }

    @PostConstruct
    public void registerListner() {
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_DISBURSAL, new SmsBusinessActionEvent(
                CampaignTriggerSubType.DISBURSE));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_MAKE_REPAYMENT, new SmsBusinessActionEvent(
                CampaignTriggerSubType.REPAYMENT));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_ADJUST_TRANSACTION, new SmsBusinessActionEvent(
                CampaignTriggerSubType.ADJUST));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_UNDO_DISBURSAL, new SmsBusinessActionEvent(
                CampaignTriggerSubType.UNDO_DISBURSAL));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_WRITTEN_OFF, new SmsBusinessActionEvent(
                CampaignTriggerSubType.WRITE_OFF));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_UNDO_WRITTEN_OFF, new SmsBusinessActionEvent(
                CampaignTriggerSubType.UNDO_WRITE_OFF));
    }

    public void genericMessageSender(final CampaignTriggerSubType actionType) {
        Map<SmsCampaign, Collection<SmsMessage>> smsDataMap = new HashMap<>();
        Collection<SmsCampaign> smsCampaigns = this.smsCampaignRepository.findByTriggerTypeAndTriggerActionTypeAndStatus(
                SmsCampaignTriggerType.TRIGGERED.getValue(), actionType.getId(), SmsCampaignStatus.ACTIVE.getValue());
        Iterator<SmsCampaign> smsCampaignIterator = smsCampaigns.iterator();
        while (smsCampaignIterator.hasNext()) {
            SmsCampaign smsCampaign = smsCampaignIterator.next();
            this.smsCampaignWritePlatformService.insertTriggeredCampaignIntoSmsOutboundTable(smsDataMap, smsCampaign);
            this.smsMessageScheduledJobService.sendTriggeredMessages(smsDataMap);
        }
    }

    class SmsBusinessActionEvent implements BusinessEventListner {

        final CampaignTriggerSubType actionType;

        public SmsBusinessActionEvent(final CampaignTriggerSubType actionType) {
            this.actionType = actionType;
        }

        @SuppressWarnings("unused")
        @Override
        public void businessEventToBeExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
            // TODO Auto-generated method stub
        }

        @SuppressWarnings("unused")
        @Override
        public void businessEventWasExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
            genericMessageSender(this.actionType);
        }
    }

}
