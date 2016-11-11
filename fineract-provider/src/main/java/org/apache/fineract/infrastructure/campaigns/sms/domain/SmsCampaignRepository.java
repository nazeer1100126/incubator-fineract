package org.apache.fineract.infrastructure.campaigns.sms.domain;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SmsCampaignRepository extends JpaRepository<SmsCampaign, Long>, JpaSpecificationExecutor<SmsCampaign> {

    List<SmsCampaign> findByCampaignType(final Integer campaignType);

    Collection<SmsCampaign> findByCampaignTypeAndTriggerTypeAndStatus(final Integer campaignType, final Integer triggerType,
            final Integer status);

    Collection<SmsCampaign> findByTriggerType(final Integer triggerType) ;
}
