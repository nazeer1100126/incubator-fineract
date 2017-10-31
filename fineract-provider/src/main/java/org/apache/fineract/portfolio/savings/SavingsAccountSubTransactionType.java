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
package org.apache.fineract.portfolio.savings;

public enum SavingsAccountSubTransactionType {
	
    INVALID(0, "savingsAccountSubTransactionType.invalid"), //
    INTERBRANCH_DEPOSIT(1, "savingsAccountSubTransactionType.interBranchDeposit"), //
    INTERBRANCH_WITHDRAWAL(2, "savingsAccountSubTransactionType.interBranchWithdrawal");

    private final Integer value;
    private final String code;

    private SavingsAccountSubTransactionType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static SavingsAccountSubTransactionType fromInt(final Integer transactionType) {

        if (transactionType == null) { return SavingsAccountSubTransactionType.INVALID; }

        SavingsAccountSubTransactionType savingsAccountTransactionType = SavingsAccountSubTransactionType.INVALID;
        switch (transactionType) {
            case 1:
                savingsAccountTransactionType = SavingsAccountSubTransactionType.INTERBRANCH_DEPOSIT;
            break;
            case 2:
                savingsAccountTransactionType = SavingsAccountSubTransactionType.INTERBRANCH_WITHDRAWAL;
            break;
        }
        return savingsAccountTransactionType;
    }
}
