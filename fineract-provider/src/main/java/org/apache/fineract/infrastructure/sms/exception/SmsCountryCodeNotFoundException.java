package org.apache.fineract.infrastructure.sms.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class SmsCountryCodeNotFoundException extends AbstractPlatformResourceNotFoundException {

    public SmsCountryCodeNotFoundException() {
        super("error.msg.sms.country.code.not.found", "SMS country code does not exist");
    }
}
