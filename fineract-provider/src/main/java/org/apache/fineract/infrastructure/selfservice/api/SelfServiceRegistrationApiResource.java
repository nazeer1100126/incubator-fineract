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

package org.apache.fineract.infrastructure.selfservice.api;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.data.AuthenticatedUserData;
import org.apache.fineract.infrastructure.selfservice.domain.SelfServiceRegistration;
import org.apache.fineract.infrastructure.selfservice.service.SelfServiceRegistrationWritePlatformService;
import org.apache.fineract.infrastructure.selfservice.service.SelfServiceRegistrationReadPlatformService;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

@Path("/self/registration")
@Component
@Scope("singleton")
public class SelfServiceRegistrationApiResource {
    
    private final ToApiJsonSerializer apiJsonSerializerService;
    private final FromJsonHelper fromApiJsonHelper;
    private final SelfServiceRegistrationReadPlatformService selfServiceRegistrationReadPlatformService;
    private final JdbcTemplate jdbcTemplate;
    private final SelfServiceRegistrationWritePlatformService selfServiceRegistrationWritePlatformService;
    
    @Autowired
    public SelfServiceRegistrationApiResource(ToApiJsonSerializer<AuthenticatedUserData> apiJsonSerializerService,
            final FromJsonHelper fromApiJsonHelper,
            final SelfServiceRegistrationReadPlatformService selfServiceRegistrationReadPlatformService,
            final RoutingDataSource dataSource,
            final SelfServiceRegistrationWritePlatformService selfServiceRegistrationWritePlatformService) {
        this.apiJsonSerializerService = apiJsonSerializerService;
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.selfServiceRegistrationReadPlatformService = selfServiceRegistrationReadPlatformService;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.selfServiceRegistrationWritePlatformService = selfServiceRegistrationWritePlatformService;
    }
    
    @POST
    @Produces({ MediaType.APPLICATION_JSON })
    public String createSelfServiceRegistrationRequest(final String apiRequestBodyAsJson) {
        SelfServiceRegistration selfServiceRegistration = this.selfServiceRegistrationWritePlatformService.createRegistrationRequest(apiRequestBodyAsJson);
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("requestId", selfServiceRegistration.getId().toString());
        responseMap.put("authenticationToken", selfServiceRegistration.getAuthenticationToken());
        return new Gson().toJson(responseMap);        
    }
    
    @POST
    @Path("user")
    @Produces({ MediaType.APPLICATION_JSON })
    public String createSelfServiceUser(final String apiRequestBodyAsJson) {
        AppUser user = this.selfServiceRegistrationWritePlatformService.createUser(apiRequestBodyAsJson);
        return "";   
    }
    
}
