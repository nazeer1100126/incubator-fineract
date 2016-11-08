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
    public void registerLoanListner() {
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
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_FORECLOSURE, new SmsBusinessActionEvent(
                CampaignTriggerSubType.FORECLOSURE));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_APPROVED, new SmsBusinessActionEvent(
                CampaignTriggerSubType.APPROVED));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_WAIVE_INTEREST, new SmsBusinessActionEvent(
                CampaignTriggerSubType.WAIVE_INTEREST));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_CLOSE, new SmsBusinessActionEvent(
                CampaignTriggerSubType.CLOSE));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_CLOSE_AS_RESCHEDULE, new SmsBusinessActionEvent(
                CampaignTriggerSubType.CLOSE_AS_RESCHEDULE));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_ADD_CHARGE, new SmsBusinessActionEvent(
                CampaignTriggerSubType.ADD_CHARGE));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_UPDATE_CHARGE, new SmsBusinessActionEvent(
                CampaignTriggerSubType.UPDATE_CHARGE));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_WAIVE_CHARGE, new SmsBusinessActionEvent(
                CampaignTriggerSubType.WAIVE_CHARGE));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_DELETE_CHARGE, new SmsBusinessActionEvent(
                CampaignTriggerSubType.DELETE_CHARGE));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_CHARGE_PAYMENT, new SmsBusinessActionEvent(
                CampaignTriggerSubType.CHARGE_PAYMENT));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_INITIATE_TRANSFER, new SmsBusinessActionEvent(
                CampaignTriggerSubType.INITIATE_TRANSFER));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_ACCEPT_TRANSFER, new SmsBusinessActionEvent(
                CampaignTriggerSubType.ACCEPT_TRANSFER));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_WITHDRAW_TRANSFER, new SmsBusinessActionEvent(
                CampaignTriggerSubType.WITHDRAW_TRANSFER));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_REJECT_TRANSFER, new SmsBusinessActionEvent(
                CampaignTriggerSubType.REJECT_TRANSFER));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_REASSIGN_OFFICER, new SmsBusinessActionEvent(
                CampaignTriggerSubType.REASSIGN_OFFICER));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_REMOVE_OFFICER, new SmsBusinessActionEvent(
                CampaignTriggerSubType.REMOVE_OFFICER));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_APPLY_OVERDUE_CHARGE, new SmsBusinessActionEvent(
                CampaignTriggerSubType.APPLY_OVERDUE_CHARGE));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_INTEREST_RECALCULATION, new SmsBusinessActionEvent(
                CampaignTriggerSubType.INTEREST_RECALCULATION));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_REFUND, new SmsBusinessActionEvent(
                CampaignTriggerSubType.REFUND));
    }

    @PostConstruct
    public void registerSavingListener() {
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.SAVINGS_DEPOSIT, new SmsBusinessActionEvent(
                CampaignTriggerSubType.SAVINGS_DEPOSIT));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.SAVINGS_WITHDRAWAL, new SmsBusinessActionEvent(
                CampaignTriggerSubType.SAVINGS_WITHDRAWAL));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.SAVINGS_ACTIVATE, new SmsBusinessActionEvent(
                CampaignTriggerSubType.SAVINGS_ACTIVATE));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.SAVINGS_ADJUST_TRANSACTION,
                new SmsBusinessActionEvent(CampaignTriggerSubType.SAVINGS_ADJUST_TRANSACTION));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.SAVINGS_APPLY_FEE, new SmsBusinessActionEvent(
                CampaignTriggerSubType.SAVINGS_APPLY_FEE));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.SAVINGS_CALCULATE_INTEREST,
                new SmsBusinessActionEvent(CampaignTriggerSubType.SAVINGS_CALCULATE_INTEREST));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.SAVINGS_CLOSE, new SmsBusinessActionEvent(
                CampaignTriggerSubType.SAVINGS_CLOSE));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.SAVINGS_POST_INTEREST, new SmsBusinessActionEvent(
                CampaignTriggerSubType.SAVINGS_POST_INTEREST));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.SAVINGS_REJECT, new SmsBusinessActionEvent(
                CampaignTriggerSubType.SAVINGS_REJECT));
    }

    @PostConstruct
    public void registerClientListener() {
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.CLIENTS_ACTIVATE, new SmsBusinessActionEvent(
                CampaignTriggerSubType.CLIENTS_ACTIVATE));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.CLIENTS_CLOSE, new SmsBusinessActionEvent(
                CampaignTriggerSubType.CLIENTS_CLOSE));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.CLIENTS_ACCEPT_TRANSFER, new SmsBusinessActionEvent(
                CampaignTriggerSubType.CLIENTS_ACCEPT_TRANSFER));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.CLIENTS_ASSIGN_STAFF,
                new SmsBusinessActionEvent(CampaignTriggerSubType.CLIENTS_ASSIGN_STAFF));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.CLIENTS_CREATE, new SmsBusinessActionEvent(
                CampaignTriggerSubType.CLIENTS_CREATE));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.CLIENTS_DELETE,
                new SmsBusinessActionEvent(CampaignTriggerSubType.CLIENTS_DELETE));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.CLIENTS_PROPOSE_TRANSFER, new SmsBusinessActionEvent(
                CampaignTriggerSubType.CLIENTS_PROPOSE_TRANSFER));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.CLIENTS_REACTIVATE, new SmsBusinessActionEvent(
                CampaignTriggerSubType.CLIENTS_REACTIVATE));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.CLIENTS_REJECT, new SmsBusinessActionEvent(
                CampaignTriggerSubType.CLIENTS_REJECT));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.CLIENTS_REJECT_TRANSFER, new SmsBusinessActionEvent(
                CampaignTriggerSubType.CLIENTS_REJECT_TRANSFER));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.CLIENTS_WITHDRAW, new SmsBusinessActionEvent(
                CampaignTriggerSubType.CLIENTS_WITHDRAW));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.CLIENTS_WITHDRAW_TRANSFER, new SmsBusinessActionEvent(
                CampaignTriggerSubType.CLIENTS_WITHDRAW_TRANSFER));
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
