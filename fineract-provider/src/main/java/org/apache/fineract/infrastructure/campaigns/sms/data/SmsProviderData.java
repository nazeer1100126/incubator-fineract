package org.apache.fineract.infrastructure.campaigns.sms.data;

public class SmsProviderData {

    private Long id;

    private String tenantId;
    
    private String phoneNo;
    
    private String providerAppKey;

    private String providerName;

    private String providerDescription;

    public SmsProviderData(final Long id, final String providerAppKey, final String providerName, final String providerDescription, final String tenantId, 
            final String phoneNo) {
        this.id = id;
        this.providerAppKey = providerAppKey;
        this.providerName = providerName;
        this.providerDescription = providerDescription;
        this.tenantId = tenantId;
        this.phoneNo = phoneNo;
    }
    
    public SmsProviderData() {
        
    }

    public Long getId() {
        return this.id;
    }

    public String getProviderAppKey() {
        return this.providerAppKey;
    }

    public String getProviderName() {
        return this.providerName;
    }

    public String getProviderDescription() {
        return this.providerDescription;
    }

    public String getTenantId() {
        return this.tenantId;
    }

    public String getPhoneNo() {
        return this.phoneNo;
    }
}
