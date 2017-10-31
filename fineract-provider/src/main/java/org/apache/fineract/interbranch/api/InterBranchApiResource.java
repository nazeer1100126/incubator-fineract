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
package org.apache.fineract.interbranch.api;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.staff.data.StaffData;
import org.apache.fineract.portfolio.account.api.AccountTransfersApiConstants;
import org.apache.fineract.portfolio.account.data.AccountTransferData;
import org.apache.fineract.portfolio.account.data.PortfolioAccountData;
import org.apache.fineract.portfolio.account.service.AccountTransfersReadPlatformService;
import org.apache.fineract.portfolio.accountdetails.data.AccountSummaryCollectionData;
import org.apache.fineract.portfolio.accountdetails.data.LoanAccountSummaryData;
import org.apache.fineract.portfolio.accountdetails.service.AccountDetailsReadPlatformService;
import org.apache.fineract.portfolio.calendar.data.CalendarData;
import org.apache.fineract.portfolio.calendar.domain.CalendarEntityType;
import org.apache.fineract.portfolio.calendar.service.CalendarReadPlatformService;
import org.apache.fineract.portfolio.charge.data.ChargeData;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.portfolio.collateral.data.CollateralData;
import org.apache.fineract.portfolio.floatingrates.data.InterestRatePeriodData;
import org.apache.fineract.portfolio.fund.data.FundData;
import org.apache.fineract.portfolio.loanaccount.data.DisbursementData;
import org.apache.fineract.portfolio.loanaccount.data.LoanAccountData;
import org.apache.fineract.portfolio.loanaccount.data.LoanChargeData;
import org.apache.fineract.portfolio.loanaccount.data.LoanTermVariationsData;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionData;
import org.apache.fineract.portfolio.loanaccount.data.PaidInAdvanceData;
import org.apache.fineract.portfolio.loanaccount.data.RepaymentScheduleRelatedLoanData;
import org.apache.fineract.portfolio.loanaccount.guarantor.data.GuarantorData;
import org.apache.fineract.portfolio.loanaccount.loanschedule.data.LoanScheduleData;
import org.apache.fineract.portfolio.loanaccount.loanschedule.service.LoanScheduleHistoryReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;
import org.apache.fineract.portfolio.loanproduct.data.TransactionProcessingStrategyData;
import org.apache.fineract.portfolio.note.data.NoteData;
import org.apache.fineract.portfolio.savings.DepositAccountType;
import org.apache.fineract.portfolio.savings.data.SavingsAccountChargeData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountTransactionData;
import org.apache.fineract.portfolio.savings.service.SavingsAccountChargeReadPlatformService;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Path("/interbranch")
@Component
@Scope("singleton")
public class InterBranchApiResource {

    private final PlatformSecurityContext context;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final AccountTransfersReadPlatformService accountTransfersReadPlatformService;
    private final LoanReadPlatformService loanReadPlatformService;
    private final ClientReadPlatformService clientReadPlatformService;
    @SuppressWarnings("rawtypes")
    private final DefaultToApiJsonSerializer toApiJsonSerializer;
    private final AccountDetailsReadPlatformService accountDetailsReadPlatformService;
    private final SavingsAccountReadPlatformService savingsAccountReadPlatformService;
    private final CalendarReadPlatformService calendarReadPlatformService;
    private final LoanScheduleHistoryReadPlatformService loanScheduleHistoryReadPlatformService;
    private final SavingsAccountChargeReadPlatformService savingsAccountChargeReadPlatformService;

    @SuppressWarnings("rawtypes")
    @Autowired
    public InterBranchApiResource(final PlatformSecurityContext context,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final AccountTransfersReadPlatformService accountTransfersReadPlatformService,
            final LoanReadPlatformService loanReadPlatformService, final ClientReadPlatformService clientReadPlatformService,
            final DefaultToApiJsonSerializer toApiJsonSerializer, final AccountDetailsReadPlatformService accountDetailsReadPlatformService,
            final SavingsAccountReadPlatformService savingsAccountReadPlatformService,
            final CalendarReadPlatformService calendarReadPlatformService,
            final LoanScheduleHistoryReadPlatformService loanScheduleHistoryReadPlatformService,
            final SavingsAccountChargeReadPlatformService savingsAccountChargeReadPlatformService) {
        this.context = context;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.accountTransfersReadPlatformService = accountTransfersReadPlatformService;
        this.loanReadPlatformService = loanReadPlatformService;
        this.clientReadPlatformService = clientReadPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.accountDetailsReadPlatformService = accountDetailsReadPlatformService;
        this.savingsAccountReadPlatformService = savingsAccountReadPlatformService;
        this.calendarReadPlatformService = calendarReadPlatformService;
        this.loanScheduleHistoryReadPlatformService = loanScheduleHistoryReadPlatformService;
        this.savingsAccountChargeReadPlatformService = savingsAccountChargeReadPlatformService;
    }

    @Path("loans/{loanId}/repayment")
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String loanRepayment(@PathParam("loanId") final Long loanId, final String apiRequestBodyAsJson) {
        this.context.authenticatedUser();
        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);
        final CommandWrapper commandRequest = builder.interBranchLoanRepaymentTransaction(loanId).build();
        CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }
    
    @Path("loans/{loanId}/transactions/{transactionId}/undo")
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String adjustLoanTransaction(@PathParam("loanId") final Long loanId, @PathParam("transactionId") final Long transactionId,
            final String apiRequestBodyAsJson) {
        
        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);
        
        final CommandWrapper commandRequest = builder.interBranchAdjustTransaction(loanId, transactionId).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @Path("savingsaccounts/{savingsId}/deposit")
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String deposit(@PathParam("savingsId") final Long savingsId, final String apiRequestBodyAsJson) {
        this.context.authenticatedUser();
        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);
        final CommandWrapper commandRequest = builder.interBranchSavingsAccountDeposit(savingsId).build();
        CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }

    @Path("savingsaccounts/{savingsId}/withdrawal")
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String withdrawal(@PathParam("savingsId") final Long savingsId, final String apiRequestBodyAsJson) {
        this.context.authenticatedUser();
        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);
        final CommandWrapper commandRequest = builder.interBranchSavingsAccountWithdrawal(savingsId).build();
        CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }
    
    @POST
    @Path("savingsaccounts/{savingsId}/transactions/{transactionId}/undo")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String adjustTransaction(@PathParam("savingsId") final Long savingsId, @PathParam("transactionId") final Long transactionId,
            final String apiRequestBodyAsJson) {
        
        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);
        final CommandWrapper commandRequest = builder.interBranchUndoSavingsAccountTransaction(savingsId, transactionId).build();
        CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }
    
    @POST
    @Path("savingsaccounts/{savingsAccountId}/charges/{savingsAccountChargeId}/pay")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String paySavingsAccountCharge(@PathParam("savingsAccountId") final Long savingsAccountId,
            @PathParam("savingsAccountChargeId") final Long savingsAccountChargeId, final String apiRequestBodyAsJson) {
    	
    	final CommandWrapper commandRequest = new CommandWrapperBuilder()
        .payInterBranchSavingsAccountCharge(savingsAccountId, savingsAccountChargeId).withJson(apiRequestBodyAsJson).build();

		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		
		return this.toApiJsonSerializer.serialize(result);
    }

    @Path("accounttransfers")
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String transfer(final String apiRequestBodyAsJson) {
        this.context.authenticatedUser();
        
        final CommandWrapper commandRequest = new CommandWrapperBuilder().interBranchAccountTransfer().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @GET
    @Path("accounttransfers/template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String accountTransfersTemplate(@QueryParam("fromOfficeId") final Long fromOfficeId,
            @QueryParam("fromClientId") final Long fromClientId, @QueryParam("fromAccountId") final Long fromAccountId,
            @QueryParam("fromAccountType") final Integer fromAccountType, @QueryParam("toOfficeId") final Long toOfficeId,
            @QueryParam("toClientId") final Long toClientId, @QueryParam("toAccountType") final Integer toAccountType) {

        this.context.authenticatedUser().validateHasReadPermission(AccountTransfersApiConstants.ACCOUNT_TRANSFER_RESOURCE_NAME);
        final AccountTransferData transferData = this.accountTransfersReadPlatformService.retrieveInterBranchTemplate(fromOfficeId,
                fromClientId, fromAccountId, fromAccountType, toOfficeId, toClientId, toAccountType);
        return this.toApiJsonSerializer.serialize(transferData);
    }

    @GET
    @Path("loans/{loanId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveLoanAccountData(@PathParam("loanId") final Long loanId) {
    	   	
    	this.context.authenticatedUser();
    	LoanAccountData loanBasicDetails = this.loanReadPlatformService.retrieveInterBranchLoan(loanId);
    	if (loanBasicDetails.isInterestRecalculationEnabled()) {
            Collection<CalendarData> interestRecalculationCalendarDatas = this.calendarReadPlatformService
                    .retrieveCalendarsByEntity(loanBasicDetails.getInterestRecalculationDetailId(),
                            CalendarEntityType.LOAN_RECALCULATION_REST_DETAIL.getValue(), null);
            CalendarData calendarData = null;
            if (!CollectionUtils.isEmpty(interestRecalculationCalendarDatas)) {
                calendarData = interestRecalculationCalendarDatas.iterator().next();
            }

            Collection<CalendarData> interestRecalculationCompoundingCalendarDatas = this.calendarReadPlatformService
                    .retrieveCalendarsByEntity(loanBasicDetails.getInterestRecalculationDetailId(),
                            CalendarEntityType.LOAN_RECALCULATION_COMPOUNDING_DETAIL.getValue(), null);
            CalendarData compoundingCalendarData = null;
            if (!CollectionUtils.isEmpty(interestRecalculationCompoundingCalendarDatas)) {
                compoundingCalendarData = interestRecalculationCompoundingCalendarDatas.iterator().next();
            }
            loanBasicDetails = LoanAccountData.withInterestRecalculationCalendarData(loanBasicDetails, calendarData,
                    compoundingCalendarData);
        }
        if (loanBasicDetails.isMonthlyRepaymentFrequencyType()) {
        	Collection<CalendarData> loanCalendarDatas = this.calendarReadPlatformService
                    .retrieveCalendarsByEntity(loanId,
                            CalendarEntityType.LOANS.getValue(), null);
            CalendarData calendarData = null;
            if (!CollectionUtils.isEmpty(loanCalendarDatas)) {
                calendarData = loanCalendarDatas.iterator().next();
            }
            if(calendarData != null)
            	loanBasicDetails = LoanAccountData.withLoanCalendarData(loanBasicDetails, calendarData);
        }
        Collection<InterestRatePeriodData> interestRatesPeriods = this.loanReadPlatformService.retrieveLoanInterestRatePeriodData(loanBasicDetails);
        Collection<LoanTransactionData> loanRepayments = null;
        LoanScheduleData repaymentSchedule = null;
        Collection<LoanChargeData> charges = null;
        Collection<GuarantorData> guarantors = null;
        Collection<CollateralData> collateral = null;
        CalendarData meeting = null;
        Collection<NoteData> notes = null;
        PortfolioAccountData linkedAccount = null;
        Collection<DisbursementData> disbursementData = null;
        Collection<LoanTermVariationsData> emiAmountVariations = null;

        final Collection<LoanTransactionData> currentLoanRepayments = this.loanReadPlatformService.retrieveLoanTransactions(loanId);
        if (!CollectionUtils.isEmpty(currentLoanRepayments)) {
            loanRepayments = currentLoanRepayments;
        }

        disbursementData = this.loanReadPlatformService.retrieveLoanDisbursementDetails(loanId);

        final RepaymentScheduleRelatedLoanData repaymentScheduleRelatedData = loanBasicDetails.repaymentScheduleRelatedData();
        repaymentSchedule = this.loanReadPlatformService.retrieveRepaymentSchedule(loanId, repaymentScheduleRelatedData,
                disbursementData, loanBasicDetails.isInterestRecalculationEnabled(), loanBasicDetails.getTotalPaidFeeCharges());

        if (loanBasicDetails.isInterestRecalculationEnabled()
                && loanBasicDetails.isActive()) {
            LoanScheduleData loanScheduleData = this.loanScheduleHistoryReadPlatformService.retrieveRepaymentArchiveSchedule(
                    loanId, repaymentScheduleRelatedData, disbursementData);
            loanBasicDetails = LoanAccountData.withOriginalSchedule(loanBasicDetails, loanScheduleData);
        }

        Collection<LoanProductData> productOptions = null;
        Collection<EnumOptionData> loanTermFrequencyTypeOptions = null;
        Collection<EnumOptionData> repaymentFrequencyTypeOptions = null;
        Collection<EnumOptionData> repaymentFrequencyNthDayTypeOptions = null;
        Collection<EnumOptionData> repaymentFrequencyDayOfWeekTypeOptions = null;
        Collection<TransactionProcessingStrategyData> repaymentStrategyOptions = null;
        Collection<EnumOptionData> interestRateFrequencyTypeOptions = null;
        Collection<EnumOptionData> amortizationTypeOptions = null;
        Collection<EnumOptionData> interestTypeOptions = null;
        Collection<EnumOptionData> interestCalculationPeriodTypeOptions = null;
        Collection<FundData> fundOptions = null;
        Collection<StaffData> allowedLoanOfficers = null;
        Collection<ChargeData> chargeOptions = null;
        ChargeData chargeTemplate = null;
        Collection<CodeValueData> loanPurposeOptions = null;
        Collection<CodeValueData> loanCollateralOptions = null;
        Collection<CalendarData> calendarOptions = null;
        Collection<PortfolioAccountData> accountLinkingOptions = null;
        PaidInAdvanceData paidInAdvanceTemplate = null;
        Collection<LoanAccountSummaryData> clientActiveLoanOptions = null;


        Collection<ChargeData> overdueCharges = null;

        paidInAdvanceTemplate = this.loanReadPlatformService.retrieveTotalPaidInAdvance(loanId);

        final LoanAccountData loanAccount = LoanAccountData.associationsAndTemplate(loanBasicDetails, repaymentSchedule, loanRepayments,
                charges, collateral, guarantors, meeting, productOptions, loanTermFrequencyTypeOptions, repaymentFrequencyTypeOptions,
                repaymentFrequencyNthDayTypeOptions, repaymentFrequencyDayOfWeekTypeOptions, repaymentStrategyOptions, 
                interestRateFrequencyTypeOptions, amortizationTypeOptions, interestTypeOptions, interestCalculationPeriodTypeOptions, 
                fundOptions, chargeOptions, chargeTemplate, allowedLoanOfficers, loanPurposeOptions, loanCollateralOptions, 
                calendarOptions, notes, accountLinkingOptions, linkedAccount, disbursementData, emiAmountVariations,
                overdueCharges, paidInAdvanceTemplate, interestRatesPeriods, clientActiveLoanOptions);
        
        return this.toApiJsonSerializer.serialize(loanAccount);
    }

    @GET
    @Path("clients/{clientId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(@PathParam("clientId") final Long clientId) {

        this.context.authenticatedUser();
        final ClientData clientData = this.clientReadPlatformService.retrieveOneWithInterBranchDetails(clientId);
        return this.toApiJsonSerializer.serialize(clientData);
    }
    
    @GET
    @Path("clients/{clientId}/basicdetails")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOneClientWithBasicDetails(@PathParam("clientId") final Long clientId) {

        this.context.authenticatedUser();
        final ClientData clientData = this.clientReadPlatformService.retrieveOneWithInterBranchBasicDetails(clientId);
        return this.toApiJsonSerializer.serialize(clientData);
    }

    @GET
    @Path("clients/{clientId}/account")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAssociatedAccounts(@PathParam("clientId") final Long clientId) {

        this.context.authenticatedUser();
        AccountSummaryCollectionData clientAccount = this.accountDetailsReadPlatformService
                .retrieveClientLoanAndSavingAccountDetails(clientId);
        return this.toApiJsonSerializer.serialize(clientAccount);
    }
    
    @GET
    @Path("savingsaccounts/{accountId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveSavingAccount(@PathParam("accountId") final Long accountId) {
        this.context.authenticatedUser();
        Collection<SavingsAccountTransactionData> transactions = null;
        Collection<SavingsAccountChargeData> charges = null;
        final Collection<SavingsAccountChargeData> currentCharges = this.savingsAccountChargeReadPlatformService
                .retrieveSavingsAccountCharges(accountId, "active");
        if (!CollectionUtils.isEmpty(currentCharges)) {
            charges = currentCharges;
        }
        final SavingsAccountData savingsAccount = this.savingsAccountReadPlatformService.retrieveInterBranchSavingAccount(accountId);
        final Collection<SavingsAccountTransactionData> currentTransactions = this.savingsAccountReadPlatformService
                .retrieveAllTransactions(accountId, DepositAccountType.SAVINGS_DEPOSIT);
        if (!CollectionUtils.isEmpty(currentTransactions)) {
            transactions = currentTransactions;
        }
        final SavingsAccountData templateData = null;
        SavingsAccountData  savingsAccountTemplate = SavingsAccountData.withTemplateOptions(savingsAccount, templateData, transactions, charges);
        return this.toApiJsonSerializer.serialize(savingsAccountTemplate);
    }


}
