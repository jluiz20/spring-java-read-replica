package xyz.joaovieira.spring.java.readreplica.config;


import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.read-replica.url}")
    private String readReplicaUrl;

    @Value("${spring.datasource.read-replica.enabled}")
    private boolean readReplicaEnabled;

    /**
     * When read replica is enabled (RDBMS_READ_FROM_REPLICA), this datasource that will route queries
     * to the read replica in methods with {@link UseReadOnlyDatabase}
     *
     * @return a read/write datasource when RDBMS_READ_FROM_REPLICA is false or a read only or writable depending
     * on the existence of {@link UseReadOnlyDatabase} annotation
     */
    @Bean
    public DataSource dataSource() {
        if (readReplicaEnabled) {
            return createReplicaRoutingDataSource();
        } else {
            return createWritableDataSource();
        }
    }

    private HikariDataSource createWritableDataSource() {
        HikariDataSource hikariDataSource = createBaseDataSource(url);
        hikariDataSource.setReadOnly(false);
        hikariDataSource.setPoolName("Hikari-Writable-Pool");

        return hikariDataSource;
    }

    private HikariDataSource createReadOnlyDataSource() {
        HikariDataSource hikariDataSource = createBaseDataSource(readReplicaUrl);
        hikariDataSource.setReadOnly(true);
        hikariDataSource.setPoolName("Hikari-ReadOnly-Pool");

        return hikariDataSource;
    }

    private HikariDataSource createBaseDataSource(String url) {
        HikariDataSource hikariDataSource = new HikariDataSource();

        hikariDataSource.setDriverClassName(driverClassName);
        hikariDataSource.setJdbcUrl(url);
        hikariDataSource.setUsername(username);
        hikariDataSource.setPassword(password);

        return hikariDataSource;
    }

    private ReplicaRoutingDataSource createReplicaRoutingDataSource() {
        DataSource writableDataSource = createWritableDataSource();
        DataSource readOnlyDataSource = createReadOnlyDataSource();

        Map<Object, Object> targetDataSources = Map.of(
                DatabaseEnvironment.WRITABLE, writableDataSource,
                DatabaseEnvironment.READ_ONLY, readOnlyDataSource);

        ReplicaRoutingDataSource replicaRoutingDataSource = new ReplicaRoutingDataSource();
        replicaRoutingDataSource.setTargetDataSources(targetDataSources);
        replicaRoutingDataSource.setDefaultTargetDataSource(writableDataSource);

        return replicaRoutingDataSource;
    }
}
