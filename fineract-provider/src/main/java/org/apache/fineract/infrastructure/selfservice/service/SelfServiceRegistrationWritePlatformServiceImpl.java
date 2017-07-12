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
package org.apache.fineract.infrastructure.selfservice.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.domain.EmailDetail;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.GmailBackedPlatformEmailService;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.selfservice.SelfServiceApiConstants;
import org.apache.fineract.infrastructure.selfservice.domain.SelfServiceRegistration;
import org.apache.fineract.infrastructure.selfservice.domain.SelfServiceRegistrationRepository;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.client.exception.ClientNotFoundException;
import org.apache.fineract.useradministration.domain.AppUser;
import org.apache.fineract.useradministration.domain.PasswordValidationPolicy;
import org.apache.fineract.useradministration.domain.PasswordValidationPolicyRepository;
import org.apache.fineract.useradministration.domain.Role;
import org.apache.fineract.useradministration.domain.UserDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Service
public class SelfServiceRegistrationWritePlatformServiceImpl implements SelfServiceRegistrationWritePlatformService {

    private final SelfServiceRegistrationRepository selfServiceRegistrationRepository;
    private final FromJsonHelper fromApiJsonHelper;
    private final SelfServiceRegistrationReadPlatformService selfServiceRegistrationReadPlatformService;
    private final JdbcTemplate jdbcTemplate;
    private final ClientRepositoryWrapper clientRepository;
    private final PasswordValidationPolicyRepository passwordValidationPolicy;
    private final UserDomainService userDomainService;
    private final GmailBackedPlatformEmailService gmailBackedPlatformEmailService;

    @Autowired
    public SelfServiceRegistrationWritePlatformServiceImpl(final SelfServiceRegistrationRepository selfServiceRegistrationRepository,
            final FromJsonHelper fromApiJsonHelper,
            final SelfServiceRegistrationReadPlatformService selfServiceRegistrationReadPlatformService,
            final RoutingDataSource dataSource, final ClientRepositoryWrapper clientRepository,
            final PasswordValidationPolicyRepository passwordValidationPolicy, final UserDomainService userDomainService,
            final GmailBackedPlatformEmailService gmailBackedPlatformEmailService) {
        this.selfServiceRegistrationRepository = selfServiceRegistrationRepository;
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.selfServiceRegistrationReadPlatformService = selfServiceRegistrationReadPlatformService;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.clientRepository = clientRepository;
        this.passwordValidationPolicy = passwordValidationPolicy;
        this.userDomainService = userDomainService;
        this.gmailBackedPlatformEmailService = gmailBackedPlatformEmailService;
    }

    @Override
    public SelfServiceRegistration createRegistrationRequest(String apiRequestBodyAsJson) {
        Gson gson = new Gson();
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("user");
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, apiRequestBodyAsJson,
                SelfServiceApiConstants.REGISTRATION_RESPONSE_DATA_PARAMETERS);
        JsonElement element = gson.fromJson(apiRequestBodyAsJson.toString(), JsonElement.class);

        Long clientId = this.fromApiJsonHelper.extractLongNamed(SelfServiceApiConstants.clientIdParamName, element);
        baseDataValidator.reset().parameter(SelfServiceApiConstants.clientIdParamName).value(clientId).notNull().integerGreaterThanZero();

        String firstName = this.fromApiJsonHelper.extractStringNamed(SelfServiceApiConstants.firstNameParamName, element);
        baseDataValidator.reset().parameter(SelfServiceApiConstants.firstNameParamName).value(firstName).notBlank()
                .notExceedingLengthOf(100);

        String lastName = this.fromApiJsonHelper.extractStringNamed(SelfServiceApiConstants.lastNameParamName, element);
        baseDataValidator.reset().parameter(SelfServiceApiConstants.lastNameParamName).value(lastName).notBlank().notExceedingLengthOf(100);

        String username = this.fromApiJsonHelper.extractStringNamed(SelfServiceApiConstants.usernameParamName, element);
        baseDataValidator.reset().parameter(SelfServiceApiConstants.usernameParamName).value(username).notBlank().notExceedingLengthOf(100);

        // validate password policy
        String password = this.fromApiJsonHelper.extractStringNamed(SelfServiceApiConstants.passwordParamName, element);
        final PasswordValidationPolicy validationPolicy = this.passwordValidationPolicy.findActivePasswordValidationPolicy();
        final String regex = validationPolicy.getRegex();
        final String description = validationPolicy.getDescription();
        baseDataValidator.reset().parameter(SelfServiceApiConstants.passwordParamName).value(password)
                .matchesRegularExpression(regex, description);

        String mobileNumber = null;
        String email = null;
        Long providerId = null;
        if (this.fromApiJsonHelper.parameterExists(SelfServiceApiConstants.mobileNumberParamName, element)) {
            mobileNumber = this.fromApiJsonHelper.extractStringNamed(SelfServiceApiConstants.mobileNumberParamName, element);
            providerId = this.fromApiJsonHelper.extractLongNamed(SelfServiceApiConstants.providerIdParamName, element);
            baseDataValidator.reset().parameter(SelfServiceApiConstants.providerIdParamName).value(providerId).notNull()
                    .integerGreaterThanZero();
        }
        if (this.fromApiJsonHelper.parameterExists(SelfServiceApiConstants.emailParamName, element)) {
            email = this.fromApiJsonHelper.extractStringNamed(SelfServiceApiConstants.emailParamName, element);
        }

        throwExceptionIfValidationError(dataValidationErrors, clientId, firstName, lastName, mobileNumber);

        String authenticationToken = randomAuthorizationTokenGeneration();
        Client client = this.clientRepository.findOneWithNotFoundDetection(clientId);
        SelfServiceRegistration selfServiceRegistration = SelfServiceRegistration.instance(client, firstName, lastName, mobileNumber,
                email, authenticationToken, username, password, providerId);
        this.selfServiceRegistrationRepository.saveAndFlush(selfServiceRegistration);
        sendAuthorizationToken(selfServiceRegistration);
        return selfServiceRegistration;

    }

    public void sendAuthorizationToken(SelfServiceRegistration selfServiceRegistration) {
        final String subject = "Authorization token ";
        final String body = "Hi  " + selfServiceRegistration.getFirstName() + "," + "\n" + "To Create user your use \n" + "Request Id - "
                + selfServiceRegistration.getId() + "Authentication Token - " + selfServiceRegistration.getAuthenticationToken();

        final EmailDetail emailDetail = new EmailDetail(subject, body, selfServiceRegistration.getEmail(),
                selfServiceRegistration.getFirstName());
        this.gmailBackedPlatformEmailService.sendDefinedEmail(emailDetail);
    }

    private void throwExceptionIfValidationError(final List<ApiParameterError> dataValidationErrors, Long clientId, String firstName,
            String lastName, String mobileNumber) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
        boolean isClientExist = this.selfServiceRegistrationReadPlatformService.isClientExist(clientId, firstName, lastName, mobileNumber);
        if (!isClientExist) { throw new ClientNotFoundException(); }
    }

    public static String randomAuthorizationTokenGeneration() {
        Integer randomPIN = (int) (Math.random() * 9000) + 1000;
        return randomPIN.toString();
    }

    @Override
    public AppUser createUser(String apiRequestBodyAsJson) {
        Gson gson = new Gson();
        JsonElement element = gson.fromJson(apiRequestBodyAsJson.toString(), JsonElement.class);
        Long id = this.fromApiJsonHelper.extractLongNamed(SelfServiceApiConstants.requestIdParamName, element);
        String authenticationToken = this.fromApiJsonHelper.extractStringNamed(
                SelfServiceApiConstants.authenticationTokenParamNameParamName, element);

        SelfServiceRegistration selfServiceRegistration = this.selfServiceRegistrationRepository.getRequestByIdAndAuthenticationToken(id,
                authenticationToken);

        Client client = selfServiceRegistration.getClient();
        final boolean passwordNeverExpire = true;
        final boolean isSelfServiceUser = true;
        final Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("DUMMY_ROLE_NOT_USED_OR_PERSISTED_TO_AVOID_EXCEPTION"));
        final Set<Role> allRoles = new HashSet<>();
        List<Client> clients = new ArrayList<>(Arrays.asList(client));
        User user = new User(selfServiceRegistration.getUsername(), selfServiceRegistration.getPassword(), authorities);
        AppUser appUser = new AppUser(client.getOffice(), user, allRoles, selfServiceRegistration.getEmail(), client.getFirstname(),
                client.getLastname(), null, passwordNeverExpire, isSelfServiceUser, clients);
        this.userDomainService.create(appUser, true);
        return appUser;
    }

}
