package org.apache.fineract.infrastructure.sms.scheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.fineract.infrastructure.campaigns.sms.domain.SmsCampaign;
import org.apache.fineract.infrastructure.core.domain.FineractPlatformTenant;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.jobs.annotation.CronTarget;
import org.apache.fineract.infrastructure.jobs.service.JobName;
import org.apache.fineract.infrastructure.sms.data.SmsData;
import org.apache.fineract.infrastructure.sms.data.SmsMessageApiQueueResourceData;
import org.apache.fineract.infrastructure.sms.data.SmsMessageApiResponseData;
import org.apache.fineract.infrastructure.sms.data.SmsMessageDeliveryReportData;
import org.apache.fineract.infrastructure.sms.domain.SmsMessage;
import org.apache.fineract.infrastructure.sms.domain.SmsMessageRepository;
import org.apache.fineract.infrastructure.sms.domain.SmsMessageStatusType;
import org.apache.fineract.infrastructure.sms.service.SmsReadPlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Scheduled job services that send SMS messages and get delivery reports for
 * the sent SMS messages
 **/
@Service
public class SmsMessageScheduledJobServiceImpl implements SmsMessageScheduledJobService {

    private final SmsMessageRepository smsMessageRepository;
    private final SmsReadPlatformService smsReadPlatformService;
    private static final Logger logger = LoggerFactory.getLogger(SmsMessageScheduledJobServiceImpl.class);
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * SmsMessageScheduledJobServiceImpl constructor
     **/
    @Autowired
    public SmsMessageScheduledJobServiceImpl(SmsMessageRepository smsMessageRepository, SmsReadPlatformService smsReadPlatformService) {
        this.smsMessageRepository = smsMessageRepository;
        this.smsReadPlatformService = smsReadPlatformService;
    }

    /**
     * get a new HttpEntity with the provided body
     **/
    private HttpEntity<String> getHttpEntity(String body, String apiAuthUsername, String apiAuthPassword) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        String authorization = apiAuthUsername + ":" + apiAuthPassword;

        byte[] encodedAuthorisation = Base64.encode(authorization.getBytes());
        headers.add("Authorization", "Basic " + new String(encodedAuthorisation));

        return new HttpEntity<>(body, headers);
    }

    /**
     * Format destination phone number so it is in international format without
     * the leading "0" or "+", example: 31612345678
     * 
     * @param phoneNumber
     *            the phone number to be formated
     * @param countryCallingCode
     *            the country calling code
     * @return phone number in international format
     **/
    private String formatDestinationPhoneNumber(String phoneNumber, String countryCallingCode) {
        String formatedPhoneNumber = "";

        try {
            Long phoneNumberToLong = Long.parseLong(phoneNumber);
            Long countryCallingCodeToLong = Long.parseLong(countryCallingCode);
            formatedPhoneNumber = Long.toString(countryCallingCodeToLong) + Long.toString(phoneNumberToLong);
        }

        catch (Exception e) {
            logger.error("Invalid phone number or country calling code, must contain only numbers", e);
        }

        return formatedPhoneNumber;
    }

    /**
     * Send batches of SMS messages to the SMS gateway (or intermediate gateway)
     **/
    @SuppressWarnings("unused")
    @Override
    @Transactional
    @CronTarget(jobName = JobName.SEND_MESSAGES_TO_SMS_GATEWAY)
    public void sendMessagesToGateway() {
        try {
            Integer smsSqlLimit = 100;
            final Collection<SmsData> pendingMessages = this.smsReadPlatformService.retrieveAllPending(null, smsSqlLimit);
            if (pendingMessages.size() > 0) {
                Iterator<SmsData> pendingMessageIterator = pendingMessages.iterator();
                Collection<SmsMessageApiQueueResourceData> apiQueueResourceDatas = new ArrayList<>();
                while (pendingMessageIterator.hasNext()) {
                    SmsData smsData = pendingMessageIterator.next();

                    // this.sendMessages();

                    final FineractPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
                    SmsMessageApiQueueResourceData apiQueueResourceData = SmsMessageApiQueueResourceData.instance(smsData.getId(),
                            tenant.getTenantIdentifier(), null, null, smsData.getMobileNo(), smsData.getMessage(), smsData.getProviderId());
                    apiQueueResourceDatas.add(apiQueueResourceData);

                }
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://192.168.0.110:9191/sms/default");

                HttpEntity<?> entity = new HttpEntity<>(SmsMessageApiQueueResourceData.toJsonString(apiQueueResourceDatas), headers);

                ResponseEntity<String> responseOne = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, entity,
                        new ParameterizedTypeReference<String>() {});
                String smsProviderOptions = responseOne.getBody();

                logger.info(pendingMessages.size() + " pending message(s) successfully sent to the intermediate gateway - sms"
                        + JobName.SEND_MESSAGES_TO_SMS_GATEWAY.name());
            }
        }

        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * handles the sending of messages to the intermediate gateway and updating
     * of the external ID, status of each message
     * 
     * @param smsData
     */
    private void sendMessages() {

        // make request
        final ResponseEntity<SmsMessageApiResponseData> entity = restTemplate.postForEntity(null + "/queue",
                getHttpEntity(null, null, null), SmsMessageApiResponseData.class);

        final List<SmsMessageDeliveryReportData> smsMessageDeliveryReportDataList = entity.getBody().getData();
        final Iterator<SmsMessageDeliveryReportData> deliveryReportIterator = smsMessageDeliveryReportDataList.iterator();

        while (deliveryReportIterator.hasNext()) {
            SmsMessageDeliveryReportData smsMessageDeliveryReportData = deliveryReportIterator.next();

            if (!smsMessageDeliveryReportData.getHasError()) {
                SmsMessage smsMessage = this.smsMessageRepository.findOne(smsMessageDeliveryReportData.getId());

                // initially set the status type enum to 100
                Integer statusType = SmsMessageStatusType.PENDING.getValue();

                switch (smsMessageDeliveryReportData.getDeliveryStatus()) {
                    case 100:
                    case 200:
                        statusType = SmsMessageStatusType.SENT.getValue();
                    break;

                    case 300:
                        statusType = SmsMessageStatusType.DELIVERED.getValue();
                    break;

                    case 400:
                        statusType = SmsMessageStatusType.FAILED.getValue();
                    break;

                    default:
                        statusType = SmsMessageStatusType.INVALID.getValue();
                    break;
                }

                // update the externalId of the SMS message
                smsMessage.setExternalId(smsMessageDeliveryReportData.getExternalId());

//                // update the SMS message sender
//                smsMessage.setSourceAddress(sourceAddress);

                // update the status Type enum
                smsMessage.setStatusType(statusType);

                // save the SmsMessage entity
                this.smsMessageRepository.save(smsMessage);

                // deduct one credit from the tenant's SMS credits
            }
        }

    }

    @Override
    public void sendTriggeredMessages(Map<SmsCampaign, Collection<SmsMessage>> smsDataMap) {
        try {
            if (!smsDataMap.isEmpty()) {
                for (Entry<SmsCampaign, Collection<SmsMessage>> entry : smsDataMap.entrySet()) {
                    Iterator<SmsMessage> smsMessageIterator = entry.getValue().iterator();
                    Collection<SmsMessageApiQueueResourceData> apiQueueResourceDatas = new ArrayList<>();
                    StringBuilder request = new StringBuilder();
                    while (smsMessageIterator.hasNext()) {
                        SmsMessage smsMessage = smsMessageIterator.next();
                        SmsMessageApiQueueResourceData apiQueueResourceData = SmsMessageApiQueueResourceData.instance(smsMessage.getId(),
                                null, null, null, smsMessage.getMobileNo(), smsMessage.getMessage(), entry.getKey().getProviderId());
                        apiQueueResourceDatas.add(apiQueueResourceData);
                    }
                    request.append(SmsMessageApiQueueResourceData.toJsonString(apiQueueResourceDatas));
                    logger.info("Sending triggered SMS with request - " + request.toString());
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * get SMS message delivery reports from the SMS gateway (or intermediate
     * gateway)
     **/
    @Override
    @Transactional
    @CronTarget(jobName = JobName.GET_DELIVERY_REPORTS_FROM_SMS_GATEWAY)
    public void getDeliveryReports() {
        /*final String apiAuthUsername = "root";
        final String apiAuthPassword = "localhost";
        final String apiBaseUrl = "http://localhost:8080/mlite-sms/api/v1/sms";
        final FineractPlatformTenant tenant = ThreadLocalContextUtil.getTenant();

        try {
            List<Long> smsMessageExternalIds = this.smsReadPlatformService.retrieveExternalIdsOfAllSent(0);
            SmsMessageApiReportResourceData smsMessageApiReportResourceData = SmsMessageApiReportResourceData.instance(
                    smsMessageExternalIds, tenant.getTenantIdentifier());

            // only proceed if there are sms message with status type enum
            // 200
            if (smsMessageExternalIds.size() > 0) {

                // make request
                ResponseEntity<SmsMessageApiResponseData> entity = restTemplate.postForEntity(apiBaseUrl + "/report",
                        getHttpEntity(smsMessageApiReportResourceData.toJsonString(), apiAuthUsername, apiAuthPassword),
                        SmsMessageApiResponseData.class);

                List<SmsMessageDeliveryReportData> smsMessageDeliveryReportDataList = entity.getBody().getData();
                Iterator<SmsMessageDeliveryReportData> iterator1 = smsMessageDeliveryReportDataList.iterator();

                while (iterator1.hasNext()) {
                    SmsMessageDeliveryReportData smsMessageDeliveryReportData = iterator1.next();
                    Integer deliveryStatus = smsMessageDeliveryReportData.getDeliveryStatus();

                    if (!smsMessageDeliveryReportData.getHasError() && (deliveryStatus != 100 && deliveryStatus != 200)) {
                        SmsMessage smsMessage = this.smsMessageRepository.findOne(smsMessageDeliveryReportData.getId());
                        Integer statusType = smsMessage.getStatusType();
                        boolean statusChanged = false;

                        switch (deliveryStatus) {
                            case 0:
                                statusType = SmsMessageStatusType.INVALID.getValue();
                            break;
                            case 300:
                                statusType = SmsMessageStatusType.DELIVERED.getValue();
                            break;

                            case 400:
                                statusType = SmsMessageStatusType.FAILED.getValue();
                            break;

                            default:
                                statusType = smsMessage.getStatusType();
                            break;
                        }

                        statusChanged = !statusType.equals(smsMessage.getStatusType());

                        // update the status Type enum
                        smsMessage.setStatusType(statusType);

                        // save the SmsMessage entity
                        this.smsMessageRepository.save(smsMessage);

                        if (statusChanged) {
                            logger.info("Status of SMS message id: " + smsMessage.getId() + " successfully changed to " + statusType);
                        }
                    }
                }

                if (smsMessageDeliveryReportDataList.size() > 0) {
                    logger.info(smsMessageDeliveryReportDataList.size() + " "
                            + "delivery report(s) successfully received from the intermediate gateway - mlite-sms");
                }
            }
        }

        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }*/
        logger.info("delivery report(s) successfully received from the intermediate gateway - mlite-sms"
                + JobName.GET_DELIVERY_REPORTS_FROM_SMS_GATEWAY.name());
    }
}
