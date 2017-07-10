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
package org.apache.fineract.infrastructure.selfservice.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.client.domain.Client;

@Entity
@Table(name = "request_audit_table")
public class SelfServiceRegistration  extends AbstractPersistableCustom<Long> {
    
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    
    @Column(name = "firstname", length = 50, nullable = false)
    private String firstName;

    @Column(name = "lastname", length = 50, nullable = true)
    private String lastName;

    @Column(name = "mobile_number", length = 50, nullable = true)
    private String mobileNumber;
    
    @Column(name = "email", length = 50, nullable = true)
    private String email;
    
    @Column(name = "provider_id", length = 50, nullable = false)
    private Long providerId;
    
    @Column(name = "authentication_token", length = 50, nullable = true)
    private String authenticationToken;
    
    @Column(name = "username", length = 50, nullable = false)
    private String username;

    @Column(name = "password", length = 50, nullable = true)
    private String password;
    
    @Column(name = "created_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date createdDate;

    public SelfServiceRegistration(final Client client,final  String firstName,final String lastName,final String mobileNumber,final String email,
            final String authenticationToken, final String username, final String password, final Long providerId) {
        this.client = client;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.authenticationToken = authenticationToken;
        this.username = username;
        this.password = password;
        this.createdDate = new Date();
        this.providerId = providerId;
    }
    
    public static SelfServiceRegistration instance(final Client client,final  String firstname,final String lastName,final String mobileNumber,final String email,
            final String authenticationToken, final String username, final String password, final Long providerId){
        return new SelfServiceRegistration(client,firstname,lastName,mobileNumber,email,authenticationToken, username, password, providerId);
    }
    
    public Long getProviderId() {
        return this.providerId;
    }
    
    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    public Client getClient() {
        return this.client;
    }

    
    public String getFirstName() {
        return this.firstName;
    }

    
    public String getLastName() {
        return this.lastName;
    }

    
    public String getMobileNumber() {
        return this.mobileNumber;
    }

    
    public String getEmail() {
        return this.email;
    }

    
    public String getAuthenticationToken() {
        return this.authenticationToken;
    }

    
    public Date getCreatedDate() {
        return this.createdDate;
    }

    
    public String getUsername() {
        return this.username;
    }

    
    public String getPassword() {
        return this.password;
    }
    
}
