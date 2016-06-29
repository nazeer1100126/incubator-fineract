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

ALTER TABLE `m_savings_account`
ADD COLUMN `nominal_annual_interest_rate_overdraft` DECIMAL(19,6) NULL DEFAULT 0 AFTER `overdraft_limit`,
ADD COLUMN `total_overdraft_interest_derived` DECIMAL(19,6) NULL DEFAULT 0 AFTER `total_interest_posted_derived`,
ADD COLUMN `min_overdraft_for_interest_calculation` DECIMAL(19,6) NULL DEFAULT 0 AFTER `nominal_annual_interest_rate_overdraft`;

ALTER TABLE `m_savings_product`
ADD COLUMN `nominal_annual_interest_rate_overdraft` DECIMAL(19,6) NULL DEFAULT 0 AFTER `overdraft_limit`,
ADD COLUMN `min_overdraft_for_interest_calculation` DECIMAL(19,6) NULL DEFAULT 0 AFTER `nominal_annual_interest_rate_overdraft`;
