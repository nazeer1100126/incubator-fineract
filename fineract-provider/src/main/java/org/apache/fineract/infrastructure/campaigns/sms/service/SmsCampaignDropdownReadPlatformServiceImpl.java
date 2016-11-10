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

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.UriBuilder;

import org.apache.fineract.infrastructure.campaigns.constants.CampaignType;
import org.apache.fineract.infrastructure.campaigns.sms.constants.SmsCampaignConstants;
import org.apache.fineract.infrastructure.campaigns.sms.constants.SmsCampaignEnumerations;
import org.apache.fineract.infrastructure.campaigns.sms.constants.SmsCampaignTriggerType;
import org.apache.fineract.infrastructure.campaigns.sms.data.CampaignTriggerWithSubTypes;
import org.apache.fineract.infrastructure.campaigns.sms.data.MessageGatewayConfigurationData;
import org.apache.fineract.infrastructure.campaigns.sms.data.SmsProviderData;
import org.apache.fineract.infrastructure.campaigns.sms.data.TriggerTypeWithSubTypesData;
import org.apache.fineract.infrastructure.campaigns.sms.exception.ConnectionFailureException;
import org.apache.fineract.infrastructure.configuration.service.ExternalServicesPropertiesReadPlatformService;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.FineractPlatformTenant;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.portfolio.calendar.domain.CalendarWeekDaysType;
import org.apache.fineract.portfolio.calendar.service.CalendarEnumerations;
import org.apache.fineract.portfolio.common.domain.PeriodFrequencyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SmsCampaignDropdownReadPlatformServiceImpl implements SmsCampaignDropdownReadPlatformService {

    private final RestTemplate restTemplate;

    private final ExternalServicesPropertiesReadPlatformService propertiesReadPlatformService;

    @Autowired
    public SmsCampaignDropdownReadPlatformServiceImpl(final ExternalServicesPropertiesReadPlatformService propertiesReadPlatformService) {
        this.restTemplate = new RestTemplate();
        this.propertiesReadPlatformService = propertiesReadPlatformService;
    }

    @Override
    public Collection<EnumOptionData> retrieveCampaignTriggerTypes() {
        final List<EnumOptionData> triggerTypeCodeValues = Arrays.asList( //
                SmsCampaignEnumerations.smscampaignTriggerType(SmsCampaignTriggerType.DIRECT), //
                SmsCampaignEnumerations.smscampaignTriggerType(SmsCampaignTriggerType.SCHEDULE), //
                SmsCampaignEnumerations.smscampaignTriggerType(SmsCampaignTriggerType.TRIGGERED) //
                );

        return triggerTypeCodeValues;
    }

    @Override
    public Collection<SmsProviderData> retrieveSmsProviders() {
        Collection<SmsProviderData> smsProviderOptions = new ArrayList<>();
        try {
            MessageGatewayConfigurationData messageGatewayConfigurationData = this.propertiesReadPlatformService.getSMSGateway();
            final FineractPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add(SmsCampaignConstants.FINERACT_PLATFORM_TENANT_ID, tenant.getTenantIdentifier());
            headers.add(SmsCampaignConstants.FINERACT_TENANT_APP_KEY, messageGatewayConfigurationData.getTenantAppKey());

            StringBuilder pathBuilder = new StringBuilder();
            pathBuilder = (messageGatewayConfigurationData.getEndPoint() != null ? pathBuilder.append("{endPoint}/smsbridges")
                    : pathBuilder.append("smsbridges"));
            UriBuilder builder = UriBuilder.fromPath(pathBuilder.toString()).host(messageGatewayConfigurationData.getHostName())
                    .scheme("http").port(messageGatewayConfigurationData.getPortNumber());

            URI uri = (messageGatewayConfigurationData.getEndPoint() != null ? builder.build(messageGatewayConfigurationData.getEndPoint())
                    : builder.build());

            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<Collection<SmsProviderData>> responseOne = restTemplate.exchange(uri, HttpMethod.GET, entity,
                    new ParameterizedTypeReference<Collection<SmsProviderData>>() {});
            smsProviderOptions = responseOne.getBody();
            if (!responseOne.getStatusCode().equals(HttpStatus.OK)) {
                System.out.println(responseOne.getStatusCode().name());
                throw new ConnectionFailureException(SmsCampaignConstants.SMS_BRIDGE);
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
        return smsProviderOptions;
    }

    @Override
    public Collection<EnumOptionData> retrieveCampaignTypes() {
        final List<EnumOptionData> campaignTypeCodeValues = Arrays.asList( //
                SmsCampaignEnumerations.smscampaignType(CampaignType.SMS)//
                );
        return campaignTypeCodeValues;
    }

    @Override
    public Collection<EnumOptionData> retrieveMonths() {
        Collection<EnumOptionData> monthsList = SmsCampaignEnumerations.calendarMonthType();
        return monthsList;
    }

    @Override
    public Collection<EnumOptionData> retrieveWeeks() {
        Collection<EnumOptionData> weeksList = CalendarEnumerations.calendarWeekDaysType(CalendarWeekDaysType.values());
        return weeksList;
    }

    @Override
    public Collection<EnumOptionData> retrivePeriodFrequencyTypes() {
        Collection<EnumOptionData> periodFrequencyTypes = SmsCampaignEnumerations
                .calendarPeriodFrequencyTypes(PeriodFrequencyType.values());
        return periodFrequencyTypes;
    }

    @Override
    public Collection<TriggerTypeWithSubTypesData> getTriggerTypeAndSubTypes() {
        return CampaignTriggerWithSubTypes.getTriggerTypeAndSubTypes();
    }
}
