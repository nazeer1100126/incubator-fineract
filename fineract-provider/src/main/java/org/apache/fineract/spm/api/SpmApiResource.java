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
package org.apache.fineract.spm.api;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.spm.data.SurveyData;
import org.apache.fineract.spm.domain.Survey;
import org.apache.fineract.spm.exception.SurveyNotFoundException;
import org.apache.fineract.spm.service.SpmService;
import org.apache.fineract.spm.util.SurveyMapper;
import org.apache.openjpa.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Path("/surveys")
@Component
@Scope("singleton")
public class SpmApiResource {

    private final PlatformSecurityContext securityContext;
    private final SpmService spmService;

    @Autowired
    public SpmApiResource(final PlatformSecurityContext securityContext,
                          final SpmService spmService) {
        this.securityContext = securityContext;
        this.spmService = spmService;
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Transactional
    public List<SurveyData> fetchActiveSurveys() {
        this.securityContext.authenticatedUser();

        final List<SurveyData> result = new ArrayList<>();

        final List<Survey> surveys = this.spmService.fetchValidSurveys();

        if (surveys != null) {
            for (final Survey survey : surveys) {
                result.add(SurveyMapper.map(survey));
            }
        }

        return result;
    }

    @GET
    @Path("/{id}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Transactional
    public SurveyData findSurvey(@PathParam("id") final Long id) {
        this.securityContext.authenticatedUser();

        final Survey survey = this.spmService.findById(id);

        if (survey == null) {
            throw new SurveyNotFoundException(id);
        }

        return SurveyMapper.map(survey);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Transactional
    public void createSurvey(final SurveyData surveyData) {
        
        try {
            this.securityContext.authenticatedUser();
            final Survey survey = SurveyMapper.map(surveyData, new Survey());

            this.spmService.createSurvey(survey);
        }catch(final EntityExistsException dve) {
            handleDataIntegrityIssues(dve, dve);
        }catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(dve.getMostSpecificCause(), dve);

        }catch (final JpaSystemException dve) {
            handleDataIntegrityIssues(dve.getMostSpecificCause(), dve);

        }catch (final PersistenceException dve) {
            handleDataIntegrityIssues(dve, dve);
        }
    }
    
    @PUT
    @Path("/{id}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Transactional
    public void editSurvey(@PathParam("id") final Long id, final SurveyData surveyData) {
        try {
            this.securityContext.authenticatedUser();
            
            final Survey surveyToUpdate = this.spmService.findById(id);

            if (surveyToUpdate == null) {
                throw new SurveyNotFoundException(id);
            }
            
            final Survey survey = SurveyMapper.map(surveyData, surveyToUpdate);

            this.spmService.updateSurvey(survey);
        }catch(final EntityExistsException dve) {
            handleDataIntegrityIssues(dve, dve);
        }catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(dve.getMostSpecificCause(), dve);

        }catch (final JpaSystemException dve) {
            handleDataIntegrityIssues(dve.getMostSpecificCause(), dve);

        }catch (final PersistenceException dve) {
            handleDataIntegrityIssues(dve, dve);

        }
    }

    @DELETE
    @Path("/{id}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Transactional
    public void deactivateSurvey(@PathParam("id") final Long id) {
        this.securityContext.authenticatedUser();

        this.spmService.deactivateSurvey(id);
    }
    
    private void handleDataIntegrityIssues(final Throwable realCause, final Exception dve) {

        if (realCause.getMessage().contains("key")) {
            throw new PlatformDataIntegrityException("error.msg.survey.duplicate.key", "Survey with key already exists",
                    "name", "");
        }

        throw new PlatformDataIntegrityException("error.msg.survey.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource: " + realCause.getMessage());
    }
}
