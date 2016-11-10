
package org.apache.fineract.infrastructure.campaigns.sms.service;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.fineract.infrastructure.campaigns.sms.constants.SmsCampaignTriggerType;
import org.apache.fineract.infrastructure.campaigns.sms.domain.SmsCampaign;
import org.apache.fineract.infrastructure.campaigns.sms.domain.SmsCampaignRepository;
import org.apache.fineract.infrastructure.sms.domain.SmsMessage;
import org.apache.fineract.infrastructure.sms.domain.SmsMessageRepository;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.organisation.office.domain.OfficeRepository;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.apache.fineract.portfolio.common.service.BusinessEventListner;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
import org.apache.fineract.portfolio.group.domain.Group;
import org.apache.fineract.portfolio.group.domain.GroupRepository;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.loanaccount.exception.InvalidLoanTypeException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsCampaignDomainServiceImpl implements SmsCampaignDomainService {

    private final SmsCampaignRepository smsCampaignRepository;
    private final SmsMessageRepository smsMessageRepository;
    private final OfficeRepository officeRepository;
    private final BusinessEventNotifierService businessEventNotifierService;
    private final SmsCampaignWritePlatformService smsCampaignWritePlatformCommandHandler;
    private final GroupRepository groupRepository;

    @Autowired
    public SmsCampaignDomainServiceImpl(final SmsCampaignRepository smsCampaignRepository, final SmsMessageRepository smsMessageRepository,
                                        final BusinessEventNotifierService businessEventNotifierService, final OfficeRepository officeRepository,
                                        final SmsCampaignWritePlatformService smsCampaignWritePlatformCommandHandler,
                                        final GroupRepository groupRepository){
        this.smsCampaignRepository = smsCampaignRepository;
        this.smsMessageRepository = smsMessageRepository;
        this.businessEventNotifierService = businessEventNotifierService;
        this.officeRepository = officeRepository;
        this.smsCampaignWritePlatformCommandHandler = smsCampaignWritePlatformCommandHandler;
        this.groupRepository = groupRepository;
    }

    @PostConstruct
    public void addListners() {
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_APPROVED, new SendSmsOnLoanApproved());
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_REJECTED, new SendSmsOnLoanRejected());
        this.businessEventNotifierService.addBusinessEventPostListners(BUSINESS_EVENTS.LOAN_MAKE_REPAYMENT, new SendSmsOnLoanRepayment());
    }

    private void notifyRejectedLoanOwner(Loan loan) {
        ArrayList<SmsCampaign> smsCampaigns = retrieveSmsCampaigns("loan rejected");
        if(smsCampaigns.size()>0){
            for (SmsCampaign campaign:smsCampaigns){
                if(campaign.isActive()) {
                    this.smsCampaignWritePlatformCommandHandler.insertDirectCampaignIntoSmsOutboundTable(loan, campaign);
                }
            }
        }
    }

    private void notifyAcceptedLoanOwner(Loan loan) {
        ArrayList<SmsCampaign> smsCampaigns = retrieveSmsCampaigns("loan approved");

        if(smsCampaigns.size()>0){
            for (SmsCampaign campaign:smsCampaigns){
                if(campaign.isActive()) {
                    this.smsCampaignWritePlatformCommandHandler.insertDirectCampaignIntoSmsOutboundTable(loan, campaign);
                }
            }
        }
    }

    private void sendSmsForLoanRepayment(LoanTransaction loanTransaction) {
        ArrayList<SmsCampaign> smsCampaigns = retrieveSmsCampaigns("loan repayment");

        if(smsCampaigns.size()>0){
            for (SmsCampaign smsCampaign:smsCampaigns){
                if(smsCampaign.isActive()) {
                    try {
                        Loan loan = loanTransaction.getLoan();
                        final Set<Client> groupClients = new HashSet<>();
                        if(loan.hasInvalidLoanType()){
                            throw new InvalidLoanTypeException("Loan Type cannot be 0 for the Triggered Sms Campaign");
                        }
                        if(loan.isGroupLoan()){
                            Group group = this.groupRepository.findOne(loan.getGroupId());
                            groupClients.addAll(group.getClientMembers());
                        }else{
                            groupClients.add(loan.client());
                        }
                        HashMap<String, String> campaignParams = new ObjectMapper().readValue(smsCampaign.getParamValue(), new TypeReference<HashMap<String, String>>() {});

                        if(groupClients.size()>0) {
                            for(Client client : groupClients) {
                                HashMap<String, Object> smsParams = processRepaymentDataForSms(loanTransaction, client);
                                for (String key : campaignParams.keySet()) {
                                    String value = campaignParams.get(key);
                                    String spvalue = null;
                                    boolean spkeycheck = smsParams.containsKey(key);
                                    if (spkeycheck) {
                                        spvalue = smsParams.get(key).toString();
                                    }
                                    if (spkeycheck && !(value.equals("-1") || spvalue.equals(value))) {
                                        if(key.equals("${officeId}")){
                                            Office campaignOffice = this.officeRepository.findOne(Long.valueOf(value));
                                            if(campaignOffice.doesNotHaveAnOfficeInHierarchyWithId(client.getOffice().getId())){
                                                throw new RuntimeException();
                                            }
                                        }else{throw new RuntimeException();}
                                    }
                                }
                                String message = this.smsCampaignWritePlatformCommandHandler.compileSmsTemplate(smsCampaign.getMessage(), smsCampaign.getCampaignName(), smsParams);
                                Object mobileNo = smsParams.get("mobileNo");

                                if (mobileNo != null) {
                                	SmsMessage smsMessage = SmsMessage.pendingSms(null, null, client, null, message, mobileNo.toString(), smsCampaign) ;
                                    this.smsMessageRepository.save(smsMessage);
                                }
                            }
                        }
                    } catch (final IOException e) {
                        System.out.println("smsParams does not contain the following key: " + e.getMessage());
                    } catch (final RuntimeException e) {
                        System.out.println("RuntimeException: " + e.getMessage());
                    }
                }
            }
        }
    }

    private ArrayList<SmsCampaign> retrieveSmsCampaigns(String paramValue){
        Collection<SmsCampaign> initialSmsCampaignList = smsCampaignRepository.findByTriggerType(SmsCampaignTriggerType.TRIGGERED.getValue());
        ArrayList<SmsCampaign> smsCampaigns = new ArrayList<>();

        for(SmsCampaign campaign : initialSmsCampaignList){
            if(campaign.getParamValue().toLowerCase().contains(paramValue)){
                smsCampaigns.add(campaign);
            }
        }
        return smsCampaigns;
    }

    private HashMap<String, Object> processRepaymentDataForSms(final LoanTransaction loanTransaction, Client groupClient){

        HashMap<String, Object> smsParams = new HashMap<String, Object>();
        Loan loan = loanTransaction.getLoan();
        final Client client;
        if(loan.isGroupLoan() && groupClient != null){
            client = groupClient;
        }else if(loan.isIndividualLoan()){
            client = loan.getClient();
        }else{
            throw new InvalidParameterException("");
        }

        DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("MMM:d:yyyy");

        smsParams.put("id",loanTransaction.getLoan().getClientId());
        smsParams.put("firstname",client.getFirstname());
        smsParams.put("middlename",client.getMiddlename());
        smsParams.put("lastname",client.getLastname());
        smsParams.put("FullName",client.getDisplayName());
        smsParams.put("mobileNo",client.mobileNo());
        smsParams.put("LoanAmount",loan.getPrincpal());
        smsParams.put("LoanOutstanding",loanTransaction.getOutstandingLoanBalance());
        smsParams.put("loanId",loan.getId());
        smsParams.put("LoanAccountId", loan.getAccountNumber());
        smsParams.put("${officeId}", client.getOffice().getId());
        smsParams.put("${staffId}", client.getStaff().getId());
        smsParams.put("repaymentAmount", loanTransaction.getAmount(loan.getCurrency()));
        smsParams.put("RepaymentDate", loanTransaction.getCreatedDateTime().toLocalDate().toString(dateFormatter));
        smsParams.put("RepaymentTime", loanTransaction.getCreatedDateTime().toLocalTime().toString(timeFormatter));
        smsParams.put("receiptNumber", loanTransaction.getPaymentDetail().getReceiptNumber());

        return smsParams;
    }

    private class SendSmsOnLoanApproved implements BusinessEventListner{

        @Override
        public void businessEventToBeExecuted(Map<BusinessEventNotificationConstants.BUSINESS_ENTITY, Object> businessEventEntity) {

        }

        @Override
        public void businessEventWasExecuted(Map<BusinessEventNotificationConstants.BUSINESS_ENTITY, Object> businessEventEntity) {
            Object entity = businessEventEntity.get(BusinessEventNotificationConstants.BUSINESS_ENTITY.LOAN);
            if (entity instanceof Loan) {
                Loan loan = (Loan) entity;
                notifyAcceptedLoanOwner(loan);
            }
        }
    }

    private class SendSmsOnLoanRejected implements BusinessEventListner{

        @Override
        public void businessEventToBeExecuted(Map<BusinessEventNotificationConstants.BUSINESS_ENTITY, Object> businessEventEntity) {

        }

        @Override
        public void businessEventWasExecuted(Map<BusinessEventNotificationConstants.BUSINESS_ENTITY, Object> businessEventEntity) {
            Object entity = businessEventEntity.get(BusinessEventNotificationConstants.BUSINESS_ENTITY.LOAN);
            if (entity instanceof Loan) {
                Loan loan = (Loan) entity;
                notifyRejectedLoanOwner(loan);
            }
        }
    }

    private class SendSmsOnLoanRepayment implements BusinessEventListner{

        @Override
        public void businessEventToBeExecuted(Map<BusinessEventNotificationConstants.BUSINESS_ENTITY, Object> businessEventEntity) {

        }

        @Override
        public void businessEventWasExecuted(Map<BusinessEventNotificationConstants.BUSINESS_ENTITY, Object> businessEventEntity) {
            Object entity = businessEventEntity.get(BusinessEventNotificationConstants.BUSINESS_ENTITY.LOAN_TRANSACTION);
            if (entity instanceof LoanTransaction) {
                LoanTransaction loanTransaction = (LoanTransaction) entity;
                sendSmsForLoanRepayment(loanTransaction);
            }
        }
    }
}
