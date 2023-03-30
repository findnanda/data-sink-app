package uk.co.data.sink.config;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.convert.CassandraConverter;
import org.springframework.data.cassandra.core.convert.MappingCassandraConverter;
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Slf4j
@Configuration
@EnableCassandraRepositories(basePackages = "uk.co.data.sink.repository")
public class CassandraConfig extends AbstractCassandraConfiguration {

    @Value("${spring.data.cassandra.keyspace}")
    private String keyspace;
    @Value("${spring.data.cassandra.contact-points}")
    private String host;
    @Value("${spring.data.cassandra.port}")
    private int port;
    @Value("${spring.data.cassandra.username}")
    private String username;
    @Value("${spring.data.cassandra.password}")
    private String password;

    @Override
    public SchemaAction getSchemaAction() {
        return SchemaAction.NONE;
    }

    @Override
    public String[] getEntityBasePackages() {
        return new String[]{"uk.co.data.sink.model"};
    }

    @Bean
    @Override
    public CassandraMappingContext cassandraMapping() throws ClassNotFoundException {
        CassandraMappingContext mappingContext = new CassandraMappingContext();
        mappingContext.setInitialEntitySet(getInitialEntitySet());
        return mappingContext;
    }

    @SneakyThrows
    @Bean
    public CassandraConverter cassandraConverter() {
        return new MappingCassandraConverter(cassandraMapping());
    }

    @Override
    protected String getKeyspaceName() {
        return this.keyspace;
    }

    @Override
    protected String getContactPoints() {
        return this.host;
    }

    @Override
    protected int getPort() {
        return port;
    }
}
