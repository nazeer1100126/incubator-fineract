package org.apache.fineract.infrastructure.campaigns.sms.data;

import java.util.Map;

public class SmsBusinessRulesData {

    private final Long reportId;

    private final String reportName;

    private final String reportType;

    private final String reportSubType;

    private final String reportDescription;

    private final Map<String, Object> reportParamName;

    public SmsBusinessRulesData(final Long reportId, final String reportName, final String reportType, final String reportSubType,
            final Map<String, Object> reportParamName, final String reportDescription) {
        this.reportId = reportId;
        this.reportName = reportName;
        this.reportType = reportType;
        this.reportSubType = reportSubType;
        this.reportParamName = reportParamName;
        this.reportDescription = reportDescription;
    }

    public static SmsBusinessRulesData instance(final Long reportId, final String reportName, final String reportType,
            final String reportSubType, final Map<String, Object> reportParamName, final String reportDescription) {
        return new SmsBusinessRulesData(reportId, reportName, reportType, reportSubType, reportParamName, reportDescription);
    }

    public Map<String, Object> getReportParamName() {
        return reportParamName;
    }

    public String getReportType() {
        return reportType;
    }

    public String getReportSubType() {
        return this.reportSubType;
    }

    public String getReportName() {
        return reportName;
    }

    public Long getReportId() {
        return reportId;
    }

    public String getReportDescription() {
        return reportDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SmsBusinessRulesData that = (SmsBusinessRulesData) o;

        if (reportId != null ? !reportId.equals(that.reportId) : that.reportId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return reportId != null ? reportId.hashCode() : 0;
    }
}
