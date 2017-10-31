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
package org.apache.fineract.portfolio.loanaccount.domain;

public enum LoanTransactionSubType {

    INVALID(0, "loanTransactionType.invalid"),
    INTERBRANCH_LOAN_REPAYMENT(50,"loanTransactionSubType.interBranchLoanRepayment");

    private final Integer value;
    private final String code;

    private LoanTransactionSubType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static LoanTransactionSubType fromInt(final Integer transactionSubTypeValue) {

        if (transactionSubTypeValue == null) { return LoanTransactionSubType.INVALID; }

        LoanTransactionSubType transactionSubType = null;
        switch (transactionSubTypeValue) {
            case 50:
                transactionSubType = LoanTransactionSubType.INTERBRANCH_LOAN_REPAYMENT;
            break;
            default:
                transactionSubType = LoanTransactionSubType.INVALID;
            break;
        }
        return transactionSubType;
    }
    
    public boolean isInterBranchLoanRepayment() {
        return this.value.equals(LoanTransactionSubType.INTERBRANCH_LOAN_REPAYMENT.getValue());
    }
}
