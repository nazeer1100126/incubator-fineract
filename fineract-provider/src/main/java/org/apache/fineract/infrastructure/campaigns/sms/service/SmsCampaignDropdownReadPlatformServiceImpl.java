package org.apache.fineract.infrastructure.campaigns.sms.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.UriBuilder;

import org.apache.fineract.infrastructure.campaigns.constants.CampaignType;
import org.apache.fineract.infrastructure.campaigns.sms.constants.SmsCampaignEnumerations;
import org.apache.fineract.infrastructure.campaigns.sms.constants.SmsCampaignTriggerType;
import org.apache.fineract.infrastructure.campaigns.sms.data.CampaignTriggerWithSubTypes;
import org.apache.fineract.infrastructure.campaigns.sms.data.GatewayConnectionConfigurationData;
import org.apache.fineract.infrastructure.campaigns.sms.data.SmsProviderData;
import org.apache.fineract.infrastructure.campaigns.sms.data.TriggerTypeWithSubTypesData;
import org.apache.fineract.infrastructure.campaigns.sms.exception.ConnectionFailureException;
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

    private final GatewayConnectionConfigurationReadPlatformService configurationReadPlatformService;

    @Autowired
    public SmsCampaignDropdownReadPlatformServiceImpl(
            final GatewayConnectionConfigurationReadPlatformService configurationReadPlatformService) {
        this.restTemplate = new RestTemplate();
        this.configurationReadPlatformService = configurationReadPlatformService;
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
        // Todo-> Retrieve these details from intermediate server based on
        // logged in tenant. Now Temp implementation to proceed with feature
        // complete
        Collection<SmsProviderData> smsProviderOptions = new ArrayList<>();
        GatewayConnectionConfigurationData configurationData = this.configurationReadPlatformService.retrieveOneByConnectionName("sms_bridge");
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            final FineractPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
            /*
             * UriComponentsBuilder builder =
             * UriComponentsBuilder.fromPath(configurationData
             * .getHostName()).scheme("http")
             * .port(configurationData.getPortNumber
             * ()).path(configurationData.getEndPoint
             * ()).path(tenant.getTenantIdentifier());
             */
            UriBuilder builder = UriBuilder.fromPath("{endPoint}/{tenantId}").host(configurationData.getHostName()).scheme("http")
                    .port(configurationData.getPortNumber());

            URI uri = builder.build(configurationData.getEndPoint(), tenant.getTenantIdentifier());
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<Collection<SmsProviderData>> responseOne = restTemplate.exchange(uri, HttpMethod.GET, entity,
                    new ParameterizedTypeReference<Collection<SmsProviderData>>() {});
            smsProviderOptions = responseOne.getBody();
            if (!responseOne.getStatusCode().equals(HttpStatus.OK)) {
                System.out.println(responseOne.getStatusCode().name());
                throw new ConnectionFailureException("sms_bridge");
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
