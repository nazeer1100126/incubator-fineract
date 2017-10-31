--
-- Licensed to the Apache Software Foundation (ASF) under one
-- or more contributor license agreements. See the NOTICE file
-- distributed with this work for additional information
-- regarding copyright ownership. The ASF licenses this file
-- to you under the Apache License, Version 2.0 (the
-- "License"); you may not use this file except in compliance
-- with the License. You may obtain a copy of the License at
--
-- http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing,
-- software distributed under the License is distributed on an
-- "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
-- KIND, either express or implied. See the License for the
-- specific language governing permissions and limitations
-- under the License.
--


ALTER TABLE `m_savings_account_transaction` 
 ADD COLUMN `transaction_sub_type_enum` SMALLINT(5) NULL DEFAULT NULL,
 ADD COLUMN `created_at_office` BIGINT(20) NULL DEFAULT NULL ;
 
ALTER TABLE `m_loan_transaction` 
ADD COLUMN `transaction_sub_type_enum` SMALLINT(5) NULL DEFAULT NULL,
ADD COLUMN `created_at_office` BIGINT(20) NULL DEFAULT NULL;

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES
('inter_branch_transactions', 'INTERBRANCH_REPAYMENT_LOAN', 'LOAN', 'INTERBRANCH_REPAYMENT', 0),
('inter_branch_transactions', 'INTERBRANCH_DEPOSIT_SAVINGSACCOUNT', 'SAVINGSACCOUNT', 'INTERBRANCH_DEPOSIT', 0),
('inter_branch_transactions', 'INTERBRANCH_WITHDRAWAL_SAVINGSACCOUNT', 'SAVINGSACCOUNT', 'INTERBRANCH_WITHDRAWAL', 0),
('inter_branch_transactions', 'INTERBRANCH_TRANSFER_SAVINGSACCOUNT', 'SAVINGSACCOUNT', 'INTERBRANCH_TRANSFER', 0) ,
('inter_branch_transactions', 'READ_INTER_BRANCH_LOAN_DETAILS', 'INTER_BRANCH_LOAN_DETAILS', 'READ', 0),
('inter_branch_transactions', 'READ_INTER_BRANCH_SAVING_DETAILS', 'INTER_BRANCH_SAVING_DETAILS', 'READ', 0),
('inter_branch_transactions', 'INTERBRANCH_ADJUST_LOAN', 'LOAN', 'INTERBRANCH_ADJUST', 0),
('inter_branch_transactions', 'INTERBRANCH_UNDOTRANSACTION_SAVINGSACCOUNT', 'SAVINGSACCOUNT', 'INTERBRANCH_UNDOTRANSACTION', 0),
('inter_branch_transactions', 'INTERBRANCH_PAY_SAVINGSACCOUNTCHARGE', 'SAVINGSACCOUNTCHARGE', 'INTERBRANCH_PAY', 0);