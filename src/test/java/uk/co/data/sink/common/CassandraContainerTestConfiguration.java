package uk.co.data.sink.common;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@ContextConfiguration(initializers = CassandraContainerTestConfiguration.Initializer.class)
@EnableConfigurationProperties
@Testcontainers
public class CassandraContainerTestConfiguration extends EmbeddedKafkaTestConfiguration {
    public static final String KEY_SPACE = "testkeyspace";

    @Container
    public static CassandraContainer cassandra =
            (CassandraContainer) new CassandraContainer("cassandra:3")
                    .withExposedPorts(9042);

    @BeforeAll
    public static void setupCassandra() {
        Cluster cluster = cassandra.getCluster();

        try (Session session = cluster.connect()) {
            session.execute("CREATE KEYSPACE IF NOT EXISTS " + KEY_SPACE + " WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 1};");
            session.execute("USE " + KEY_SPACE + ";");
            session.execute("DROP TABLE IF EXISTS user;");
            session.execute("CREATE TABLE user(user_id text, firstname text, lastname text, city text, PRIMARY KEY (user_id, firstname));");
            session.execute("SELECT * from user;");
        }
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues values = TestPropertyValues.of(
                    "spring.data.cassandra.contact-points=" + cassandra.getContainerIpAddress(),
                    "spring.data.cassandra.port=" + cassandra.getFirstMappedPort(),
                    "spring.data.cassandra.keyspace =" + KEY_SPACE,
                    "spring.data.cassandra.username =" + "cassandra",
                    "spring.data.cassandra.password =" + "cassandra"
            );
            values.applyTo(configurableApplicationContext);

            Cluster cluster = cassandra.getCluster();

            try (Session session = cluster.connect()) {
                session.execute("CREATE KEYSPACE IF NOT EXISTS " + KEY_SPACE + " WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 1};");
            }
        }
    }
}
