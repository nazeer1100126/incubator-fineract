/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.infrastructure.campaigns.sms.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.fineract.infrastructure.campaigns.sms.constants.SmsCampaignStatus;
import org.apache.fineract.infrastructure.campaigns.sms.constants.SmsCampaignTriggerType;
import org.apache.fineract.infrastructure.campaigns.sms.data.CampaignTriggerWithSubTypes.CampaignTriggerSubType;
import org.apache.fineract.infrastructure.campaigns.sms.domain.SmsCampaign;
import org.apache.fineract.infrastructure.campaigns.sms.domain.SmsCampaignRepository;
import org.apache.fineract.infrastructure.sms.domain.SmsMessage;
import org.apache.fineract.infrastructure.sms.scheduler.SmsMessageScheduledJobService;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientTransaction;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.apache.fineract.portfolio.common.service.BusinessEventListner;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanCharge;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;
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
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_DISBURSAL, new SmsLoanBusinessActionEvent(
                CampaignTriggerSubType.DISBURSE));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_MAKE_REPAYMENT, new SmsLoanBusinessActionEvent(
                CampaignTriggerSubType.REPAYMENT));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_ADJUST_TRANSACTION, new SmsLoanBusinessActionEvent(
                CampaignTriggerSubType.ADJUST));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_UNDO_DISBURSAL, new SmsLoanBusinessActionEvent(
                CampaignTriggerSubType.UNDO_DISBURSAL));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_WRITTEN_OFF, new SmsLoanBusinessActionEvent(
                CampaignTriggerSubType.WRITE_OFF));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_UNDO_WRITTEN_OFF, new SmsLoanBusinessActionEvent(
                CampaignTriggerSubType.UNDO_WRITE_OFF));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_FORECLOSURE, new SmsLoanBusinessActionEvent(
                CampaignTriggerSubType.FORECLOSURE));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_APPROVED, new SmsLoanBusinessActionEvent(
                CampaignTriggerSubType.APPROVED));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_WAIVE_INTEREST, new SmsLoanBusinessActionEvent(
                CampaignTriggerSubType.WAIVE_INTEREST));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_CLOSE, new SmsLoanBusinessActionEvent(
                CampaignTriggerSubType.CLOSE));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_CLOSE_AS_RESCHEDULE, new SmsLoanBusinessActionEvent(
                CampaignTriggerSubType.CLOSE_AS_RESCHEDULE));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_ADD_CHARGE, new SmsLoanBusinessActionEvent(
                CampaignTriggerSubType.ADD_CHARGE));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_UPDATE_CHARGE, new SmsLoanBusinessActionEvent(
                CampaignTriggerSubType.UPDATE_CHARGE));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_WAIVE_CHARGE, new SmsLoanBusinessActionEvent(
                CampaignTriggerSubType.WAIVE_CHARGE));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_DELETE_CHARGE, new SmsLoanBusinessActionEvent(
                CampaignTriggerSubType.DELETE_CHARGE));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_CHARGE_PAYMENT, new SmsLoanBusinessActionEvent(
                CampaignTriggerSubType.CHARGE_PAYMENT));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_INITIATE_TRANSFER, new SmsLoanBusinessActionEvent(
                CampaignTriggerSubType.INITIATE_TRANSFER));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_ACCEPT_TRANSFER, new SmsLoanBusinessActionEvent(
                CampaignTriggerSubType.ACCEPT_TRANSFER));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_WITHDRAW_TRANSFER, new SmsLoanBusinessActionEvent(
                CampaignTriggerSubType.WITHDRAW_TRANSFER));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_REJECT_TRANSFER, new SmsLoanBusinessActionEvent(
                CampaignTriggerSubType.REJECT_TRANSFER));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_REASSIGN_OFFICER, new SmsLoanBusinessActionEvent(
                CampaignTriggerSubType.REASSIGN_OFFICER));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_REMOVE_OFFICER, new SmsLoanBusinessActionEvent(
                CampaignTriggerSubType.REMOVE_OFFICER));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_APPLY_OVERDUE_CHARGE, new SmsLoanBusinessActionEvent(
                CampaignTriggerSubType.APPLY_OVERDUE_CHARGE));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_INTEREST_RECALCULATION, new SmsLoanBusinessActionEvent(
                CampaignTriggerSubType.INTEREST_RECALCULATION));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_REFUND, new SmsLoanBusinessActionEvent(
                CampaignTriggerSubType.REFUND));
    }

    @PostConstruct
    public void registerSavingListener() {
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.SAVINGS_DEPOSIT, new SmsSavingsBusinessActionEvent(
                CampaignTriggerSubType.SAVINGS_DEPOSIT));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.SAVINGS_WITHDRAWAL, new SmsSavingsBusinessActionEvent(
                CampaignTriggerSubType.SAVINGS_WITHDRAWAL));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.SAVINGS_ACTIVATE, new SmsSavingsBusinessActionEvent(
                CampaignTriggerSubType.SAVINGS_ACTIVATE));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.SAVINGS_ADJUST_TRANSACTION,
                new SmsSavingsBusinessActionEvent(CampaignTriggerSubType.SAVINGS_ADJUST_TRANSACTION));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.SAVINGS_APPLY_ANNUAL_FEE, new SmsSavingsBusinessActionEvent(
                CampaignTriggerSubType.SAVINGS_APPLY_ANNUAL_FEE));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.SAVINGS_CALCULATE_INTEREST,
                new SmsSavingsBusinessActionEvent(CampaignTriggerSubType.SAVINGS_CALCULATE_INTEREST));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.SAVINGS_CLOSE, new SmsSavingsBusinessActionEvent(
                CampaignTriggerSubType.SAVINGS_CLOSE));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.SAVINGS_POST_INTEREST, new SmsSavingsBusinessActionEvent(
                CampaignTriggerSubType.SAVINGS_POST_INTEREST));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.SAVINGS_REJECT, new SmsSavingsBusinessActionEvent(
                CampaignTriggerSubType.SAVINGS_REJECT));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.SAVINGS_UNDO, new SmsSavingsBusinessActionEvent(
                CampaignTriggerSubType.SAVINGS_UNDO));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.SAVINGS_ADD_CHARGE, new SmsSavingsBusinessActionEvent(
                CampaignTriggerSubType.SAVINGS_ADD_CHARGE));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.SAVINGS_WAIVE_CHARGE, new SmsSavingsBusinessActionEvent(
                CampaignTriggerSubType.SAVINGS_WAIVE_CHARGE));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.SAVINGS_PAY_CHARGE, new SmsSavingsBusinessActionEvent(
                CampaignTriggerSubType.SAVINGS_PAY_CHARGE));
    }

    @PostConstruct
    public void registerClientListener() {
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.CLIENTS_ACTIVATE, new SmsClientBusinessActionEvent(
                CampaignTriggerSubType.CLIENTS_ACTIVATE));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.CLIENTS_CLOSE, new SmsClientBusinessActionEvent(
                CampaignTriggerSubType.CLIENTS_CLOSE));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.CLIENTS_ACCEPT_TRANSFER, new SmsClientBusinessActionEvent(
                CampaignTriggerSubType.CLIENTS_ACCEPT_TRANSFER));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.CLIENTS_ASSIGN_STAFF,
                new SmsClientBusinessActionEvent(CampaignTriggerSubType.CLIENTS_ASSIGN_STAFF));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.CLIENTS_CREATE, new SmsClientBusinessActionEvent(
                CampaignTriggerSubType.CLIENTS_CREATE));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.CLIENTS_DELETE,
                new SmsClientBusinessActionEvent(CampaignTriggerSubType.CLIENTS_DELETE));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.CLIENTS_PROPOSE_TRANSFER, new SmsClientBusinessActionEvent(
                CampaignTriggerSubType.CLIENTS_PROPOSE_TRANSFER));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.CLIENTS_REACTIVATE, new SmsClientBusinessActionEvent(
                CampaignTriggerSubType.CLIENTS_REACTIVATE));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.CLIENTS_REJECT, new SmsClientBusinessActionEvent(
                CampaignTriggerSubType.CLIENTS_REJECT));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.CLIENTS_REJECT_TRANSFER, new SmsClientBusinessActionEvent(
                CampaignTriggerSubType.CLIENTS_REJECT_TRANSFER));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.CLIENTS_WITHDRAW, new SmsClientBusinessActionEvent(
                CampaignTriggerSubType.CLIENTS_WITHDRAW));
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.CLIENTS_WITHDRAW_TRANSFER, new SmsClientBusinessActionEvent(
                CampaignTriggerSubType.CLIENTS_WITHDRAW_TRANSFER));
    }

    public void genericMessageSender(final CampaignTriggerSubType actionType, Client client) {
        Map<SmsCampaign, Collection<SmsMessage>> smsDataMap = new HashMap<>();
        Collection<SmsCampaign> smsCampaigns = this.smsCampaignRepository.findByTriggerTypeAndTriggerActionTypeAndStatus(
                SmsCampaignTriggerType.TRIGGERED.getValue(), actionType.getId(), SmsCampaignStatus.ACTIVE.getValue());
        Iterator<SmsCampaign> smsCampaignIterator = smsCampaigns.iterator();
        while (smsCampaignIterator.hasNext()) {
            SmsCampaign smsCampaign = smsCampaignIterator.next();
            this.smsCampaignWritePlatformService.insertTriggeredCampaignIntoSmsOutboundTable(smsDataMap, smsCampaign, client);
            this.smsMessageScheduledJobService.sendTriggeredMessages(smsDataMap);
        }
    }

    class SmsLoanBusinessActionEvent implements BusinessEventListner {

        final CampaignTriggerSubType actionType;

        public SmsLoanBusinessActionEvent(final CampaignTriggerSubType actionType) {
            this.actionType = actionType;
        }

        @SuppressWarnings("unused")
        @Override
        public void businessEventToBeExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
            // TODO Auto-generated method stub
        }

        @Override
        public void businessEventWasExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
            Set<BUSINESS_ENTITY> keys = businessEventEntity.keySet();
            BUSINESS_ENTITY key = keys.iterator().next();
            Object entity = businessEventEntity.get(key);
            Client client = null;
            if (entity instanceof Loan) {
                client = ((Loan) entity).getClient();
            } else if (entity instanceof LoanTransaction) {
                client = ((LoanTransaction) entity).getLoan().getClient();
            } else if (entity instanceof LoanCharge) {
                client = ((LoanCharge)entity).getLoan().getClient();
            }
            genericMessageSender(this.actionType, client);
        }
    }

    class SmsSavingsBusinessActionEvent implements BusinessEventListner {

        final CampaignTriggerSubType actionType;

        public SmsSavingsBusinessActionEvent(final CampaignTriggerSubType actionType) {
            this.actionType = actionType;
        }

        @SuppressWarnings("unused")
        @Override
        public void businessEventToBeExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
            // TODO Auto-generated method stub
        }

        @Override
        public void businessEventWasExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
            Set<BUSINESS_ENTITY> keys = businessEventEntity.keySet();
            BUSINESS_ENTITY key = keys.iterator().next();
            Object entity = businessEventEntity.get(key);
            Client client = null;
            if (entity instanceof SavingsAccount) {
                client = ((SavingsAccount) entity).getClient();
            } else if (entity instanceof SavingsAccountTransaction) {
                client = ((SavingsAccountTransaction) entity).getSavingsAccount().getClient();
            } 
            genericMessageSender(this.actionType, client);
        }
    }
    
    class SmsClientBusinessActionEvent implements BusinessEventListner {

        final CampaignTriggerSubType actionType;

        public SmsClientBusinessActionEvent(final CampaignTriggerSubType actionType) {
            this.actionType = actionType;
        }

        @SuppressWarnings("unused")
        @Override
        public void businessEventToBeExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
            // TODO Auto-generated method stub
        }

        @Override
        public void businessEventWasExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
            Set<BUSINESS_ENTITY> keys = businessEventEntity.keySet();
            BUSINESS_ENTITY key = keys.iterator().next();
            Object entity = businessEventEntity.get(key);
            Client client = null;
            if (entity instanceof Client) {
                client = (Client)entity;
            } else if (entity instanceof ClientTransaction) {
                client = ((ClientTransaction) entity).getClient();
            } 
            genericMessageSender(this.actionType, client);
        }
    }

}
