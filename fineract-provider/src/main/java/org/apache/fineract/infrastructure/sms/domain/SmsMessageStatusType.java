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
package org.apache.fineract.infrastructure.sms.domain;

public enum SmsMessageStatusType {

    INVALID(0, "smsMessageStatusType.invalid"), //
    PENDING(100, "smsMessageStatusType.pending"), //
    WAITING_FOR_DELIVERY_REPORT(200, "smsMessageStatusType.waitingForDeliveryReport"), 
    SENT(300, "smsMessageStatusType.sent"), //
    DELIVERED(400, "smsMessageStatusType.delivered"), //
    FAILED(500, "smsMessageStatusType.failed");

    private final Integer value;
    private final String code;

    public static SmsMessageStatusType fromInt(final Integer statusValue) {

        SmsMessageStatusType enumeration = SmsMessageStatusType.INVALID;
        switch (statusValue) {
            case 100:
                enumeration = SmsMessageStatusType.PENDING;
            break;
            case 200:
                enumeration = SmsMessageStatusType.WAITING_FOR_DELIVERY_REPORT;
            break;
            case 300:
                enumeration = SmsMessageStatusType.SENT;
            break;
            case 400:
                enumeration = SmsMessageStatusType.DELIVERED;
            break;
            case 500:
                enumeration = SmsMessageStatusType.FAILED;
            break;
        }
        return enumeration;
    }

    private SmsMessageStatusType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }
}