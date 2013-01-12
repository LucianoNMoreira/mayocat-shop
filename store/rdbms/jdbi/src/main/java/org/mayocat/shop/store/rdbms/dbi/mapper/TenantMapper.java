package org.mayocat.shop.store.rdbms.dbi.mapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.model.TenantConfiguration;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.collect.Multimap;

public class TenantMapper implements ResultSetMapper<Tenant>
{
    @Override
    public Tenant map(int index, ResultSet result, StatementContext statementContext) throws SQLException
    {
        String slug = result.getString("tenant.slug");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new GuavaModule());
        Integer configurationVersion = result.getInt("configuration.version");

        try {
            Multimap<String, Object> data = mapper.readValue(result.getString("configuration.data"), Multimap.class);

            TenantConfiguration configuration = new TenantConfiguration(configurationVersion, data);

            Tenant tenant = new Tenant(result.getLong("tenant.id"), slug, configuration);
            tenant.setSlug(slug);

            return tenant;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration for tenant with slug ["
                    + slug + "]", e);
        }
    }
}
