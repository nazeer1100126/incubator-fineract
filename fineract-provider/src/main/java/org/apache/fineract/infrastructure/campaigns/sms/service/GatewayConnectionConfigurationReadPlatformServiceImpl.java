package org.apache.fineract.infrastructure.campaigns.sms.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.fineract.infrastructure.campaigns.sms.data.MessageGatewayConfigurationData;
import org.apache.fineract.infrastructure.campaigns.sms.exception.GatewayConnectionConfigurationNotFound;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class GatewayConnectionConfigurationReadPlatformServiceImpl implements GatewayConnectionConfigurationReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final GatewayConfigRowMapper gatewayConfigRowMapper;

    @Autowired
    public GatewayConnectionConfigurationReadPlatformServiceImpl(RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.gatewayConfigRowMapper = new GatewayConfigRowMapper();
    }

    @Override
    public MessageGatewayConfigurationData retrieveOneByConnectionName(String connectionName) {
        try {
            final String sql = "select " + this.gatewayConfigRowMapper.schema() + " where isc.connection_name = ?";
            return this.jdbcTemplate.queryForObject(sql, this.gatewayConfigRowMapper, new Object[] { connectionName });
        } catch (final EmptyResultDataAccessException e) {
            throw new GatewayConnectionConfigurationNotFound(connectionName);
        }
    }

    private static final class GatewayConfigRowMapper implements RowMapper<MessageGatewayConfigurationData> {

        final String schema;

        private GatewayConfigRowMapper() {
            final StringBuilder sql = new StringBuilder(400);
            sql.append("isc.id as id, ");
            sql.append("isc.connection_name as connectionName, ");
            sql.append("isc.host_name as hostName, ");
            sql.append("isc.url_end_point as endPoint, ");
            sql.append("isc.port_number as portNumber, ");
            sql.append("isc.username as userName, ");
            sql.append("isc.password as password, ");
            sql.append("isc.ssl_enabled as sslEnabled, ");
            sql.append("isc.tenant_app_key as tenantAppKey ");
            sql.append("from m_intermediate_server_connections isc ");

            this.schema = sql.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public MessageGatewayConfigurationData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            Long id = JdbcSupport.getLong(rs, "id");
            String connectionName = rs.getString("connectionName");
            String hostName = rs.getString("hostName");
            String endPoint = rs.getString("endPoint");
            int portNumber = rs.getInt("portNumber");
            String userName = rs.getString("userName");
            String password = rs.getString("password");
            boolean sslEnabled = rs.getBoolean("sslEnabled");
            String tenantAppKey = rs.getString("tenantAppKey");
            return new MessageGatewayConfigurationData(id, connectionName, hostName, portNumber, endPoint, userName, password,
                    sslEnabled, tenantAppKey);
        }

    }
}
