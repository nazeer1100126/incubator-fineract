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
package org.apache.fineract.commands;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class CommandProcessingConstants {
    
    public static final String INTERBRANCH_LOAN_REPAYMENT_PERMISSION = "INTERBRANCH_REPAYMENT_LOAN";
    public static final String INTERBRANCH_UNDO_LOAN_REPAYMENT_PERMISSION = "INTERBRANCH_ADJUST_LOAN";
    public static final String INTERBRANCH_DEPOSIT_PERMISSION = "INTERBRANCH_DEPOSIT_SAVINGSACCOUNT";
    public static final String INTERBRANCH_WITHDRAWAL_PERMISSION = "INTERBRANCH_WITHDRAWAL_SAVINGSACCOUNT";
    public static final String INTERBRANCH_FUND_TRANSFER_PERMISSION = "INTERBRANCH_TRANSFER_SAVINGSACCOUNT";
    public static final String INTERBRANCH_UNDO_SAVINGS_PERMISSION = "INTERBRANCH_UNDOTRANSACTION_SAVINGSACCOUNT";
    
    public static final Set<String> ALLOWED_INTERBRANCH_TASK_PERMISSIONS = new HashSet<>(Arrays.asList(INTERBRANCH_LOAN_REPAYMENT_PERMISSION, INTERBRANCH_UNDO_LOAN_REPAYMENT_PERMISSION,
            INTERBRANCH_DEPOSIT_PERMISSION, INTERBRANCH_WITHDRAWAL_PERMISSION, INTERBRANCH_FUND_TRANSFER_PERMISSION, INTERBRANCH_UNDO_SAVINGS_PERMISSION));

}
