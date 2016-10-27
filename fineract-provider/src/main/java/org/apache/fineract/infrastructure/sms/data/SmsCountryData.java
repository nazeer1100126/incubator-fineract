package org.apache.fineract.infrastructure.sms.data;

public class SmsCountryData {

    private final Long id;

    private final Long officeId;

    private final String countryName;

    private final String countryCode;

    public SmsCountryData(final Long id, final Long officeId, final String countryName, final String countryCode) {
        this.id = id;
        this.officeId = officeId;
        this.countryName = countryName;
        this.countryCode = countryCode;
    }

    public static SmsCountryData instance(final Long id, final Long officeId, final String countryName, final String countryCode) {
        return new SmsCountryData(id, officeId, countryName, countryCode);
    }

    public Long getId() {
        return this.id;
    }

    public Long getOfficeId() {
        return this.officeId;
    }

    public String getCountryName() {
        return this.countryName;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

}
