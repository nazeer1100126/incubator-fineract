package org.apache.fineract.infrastructure.sms.data;

/** 
 * Immutable data object representing an outbound SMS message delivery report data 
 **/
public class SmsMessageDeliveryReportData {
	private Long id;
	private String externalId;
	private String addedOnDate;
	private String deliveredOnDate;
	private Integer deliveryStatus;
	private Boolean hasError;
	private String errorMessage;
	
	/** 
	 * SmsMessageDeliveryReportData constructor
	 * 
	 * @return void 
	 **/
	private SmsMessageDeliveryReportData(Long id, String externalId, String addedOnDate, String deliveredOnDate, 
			Integer deliveryStatus, Boolean hasError, String errorMessage) {
		this.id = id;
		this.externalId = externalId;
		this.addedOnDate = addedOnDate;
		this.deliveredOnDate = deliveredOnDate;
		this.deliveryStatus = deliveryStatus;
		this.hasError = hasError;
		this.errorMessage = errorMessage;
	}
	
	/** 
	 * Default SmsMessageDeliveryReportData constructor 
	 * 
	 * @return void
	 **/
	protected SmsMessageDeliveryReportData() {}
	
	/** 
	 * @return an instance of the SmsMessageDeliveryReportData class
	 **/
	public static SmsMessageDeliveryReportData getInstance(Long id, String externalId, String addedOnDate, String deliveredOnDate, 
			Integer deliveryStatus, Boolean hasError, String errorMessage) {
		
		return new SmsMessageDeliveryReportData(id, externalId, addedOnDate, deliveredOnDate, deliveryStatus, hasError, errorMessage);
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the externalId
	 */
	public String getExternalId() {
		return externalId;
	}

	/**
	 * @return the addedOnDate
	 */
	public String getAddedOnDate() {
		return addedOnDate;
	}

	/**
	 * @return the deliveredOnDate
	 */
	public String getDeliveredOnDate() {
		return deliveredOnDate;
	}

	/**
	 * @return the deliveryStatus
	 */
	public Integer getDeliveryStatus() {
		return deliveryStatus;
	}

	/**
	 * @return the hasError
	 */
	public Boolean getHasError() {
		return hasError;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
}
