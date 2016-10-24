package org.apache.fineract.infrastructure.campaigns.sms.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public final class CampaignTriggerWithSubTypes {

    public enum ActualCampaignTriggerType {
        INVALID(0, "campaignTriggerType.invalid"), //
        LOAN(1, "campaignTriggerType.loan"), //
        SAVINGS(2, "campaignTriggerType.savings"), //
        CLIENTS(3, "campaignTriggerType.clients");

        private Integer value;
        private String code;

        private ActualCampaignTriggerType(Integer value, String code) {
            this.value = value;
            this.code = code;
        }

        public static ActualCampaignTriggerType fromInt(final Integer typeValue) {
            ActualCampaignTriggerType type = ActualCampaignTriggerType.INVALID;
            switch (typeValue) {
                case 0:
                case 1:
                    type = LOAN;
                break;
                case 2:
                    type = SAVINGS;
                break;
                case 3:
                    type = CLIENTS;
                break;
            }
            return type;
        }

        public Integer getValue() {
            return this.value;
        }

        public String getCode() {
            return this.code;
        }

        public static EnumOptionData toEnumOptionData(final ActualCampaignTriggerType triggerType) {
            final EnumOptionData optionData = new EnumOptionData(new Long(triggerType.getValue()), triggerType.getCode(), triggerType.name());
            return optionData;
        }

        public static EnumOptionData toEnumOptionData(final Integer triggerTypeValue) {
            ActualCampaignTriggerType actualCampaignTriggerType = ActualCampaignTriggerType.fromInt(triggerTypeValue);
            final EnumOptionData optionData = new EnumOptionData(new Long(actualCampaignTriggerType.getValue()),
                    actualCampaignTriggerType.getCode(), actualCampaignTriggerType.name());
            return optionData;
        }

        public boolean isInvalid() {
            return this.value.equals(ActualCampaignTriggerType.INVALID.getValue());
        }
    }

    public enum CampaignTriggerSubType {
        INVALID(0, ActualCampaignTriggerType.INVALID, "campaignTriggerSubType.invalid"), //
        DISBURSE(101, ActualCampaignTriggerType.LOAN, "campaignTriggerSubType.disburse"), //
        REPAYMENT(102, ActualCampaignTriggerType.LOAN, "campaignTriggerSubType.repayment"), //
        UNDO_DISBURSAL(103, ActualCampaignTriggerType.LOAN, "campaignTriggerSubType.undodisbursal"), //
        WRITE_OFF(104, ActualCampaignTriggerType.LOAN, "campaignTriggerSubType.writeoff"), //
        ADJUST(105, ActualCampaignTriggerType.LOAN, "campaignTriggerSubType.adjust"), //
        UNDO_WRITE_OFF(106, ActualCampaignTriggerType.LOAN, "campaignTriggerSubType.undowriteoff"), //

        DEPOSIT(201, ActualCampaignTriggerType.SAVINGS, "campaignTriggerSubType.deposit"), //
        WITHDRAWAL(202, ActualCampaignTriggerType.SAVINGS, "campaignTriggerSubType.withdrawal"), //

        ACTIVATE(301, ActualCampaignTriggerType.CLIENTS, "campaignTriggerSubType.activate"), //
        CLOSE(302, ActualCampaignTriggerType.CLIENTS, "campaignTriggerSubType.close");

        private Integer id;
        private ActualCampaignTriggerType type;
        private String code;

        private CampaignTriggerSubType(Integer id, ActualCampaignTriggerType type, String code) {
            this.id = id;
            this.type = type;
            this.code = code;
        }

        public static CampaignTriggerSubType fromInt(final Integer subTypeValue) {
            CampaignTriggerSubType subType = CampaignTriggerSubType.INVALID;
            switch (subTypeValue) {
                case 0:
                case 101:
                    subType = DISBURSE;
                break;
                case 102:
                    subType = REPAYMENT;
                break;
                case 103:
                    subType = ADJUST;
                break;
                case 104:
                    subType = UNDO_DISBURSAL;
                break;
                case 105:
                    subType = WRITE_OFF;
                break;
                case 106:
                    subType = UNDO_WRITE_OFF;
                break;
                case 401:
                    subType = DEPOSIT;
                break;
                case 402:
                    subType = WITHDRAWAL;
                break;
                case 501:
                    subType = ACTIVATE;
                break;
                case 502:
                    subType = CLOSE;
                break;
            }
            return subType;
        }

        public Integer getId() {
            return this.id;
        }

        public ActualCampaignTriggerType getType() {
            return this.type;
        }

        public String getCode() {
            return this.code;
        }

        public static EnumOptionData toEnumOptionData(final Integer triggerSubType) {
            CampaignTriggerSubType subTypeEnum = CampaignTriggerSubType.fromInt(triggerSubType);
            final EnumOptionData optionData = new EnumOptionData(new Long(subTypeEnum.getId()), subTypeEnum.getCode(), subTypeEnum.name());
            return optionData;
        }
    }

    public static List<EnumOptionData> addTypeSubTypeMapping(ActualCampaignTriggerType type) {
        List<EnumOptionData> subTypeList = new ArrayList<>();
        EnumOptionData optionData = null;
        for (CampaignTriggerSubType subType : CampaignTriggerSubType.values()) {
            if (subType.getType().equals(type)) {
                optionData = new EnumOptionData(subType.getId().longValue(), subType.getCode(), subType.name());
                subTypeList.add(optionData);
            }
        }
        return subTypeList;
    }

    public static Collection<TriggerTypeWithSubTypesData> getTriggerTypeAndSubTypes() {
        final Collection<TriggerTypeWithSubTypesData> typesList = new ArrayList<>();
        EnumOptionData actualTriggerType = null;
        for (ActualCampaignTriggerType triggerType : ActualCampaignTriggerType.values()) {
            if (triggerType.isInvalid()) {
                continue;
            }
            List<EnumOptionData> subTypeList = addTypeSubTypeMapping(triggerType);
            actualTriggerType = ActualCampaignTriggerType.toEnumOptionData(triggerType);
            TriggerTypeWithSubTypesData triggerTypeWithSubTypesData = new TriggerTypeWithSubTypesData(actualTriggerType, subTypeList);
            typesList.add(triggerTypeWithSubTypesData);
        }
        return typesList;
    }

}
