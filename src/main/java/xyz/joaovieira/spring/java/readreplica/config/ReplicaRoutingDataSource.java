package xyz.joaovieira.spring.java.readreplica.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class ReplicaRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return DatabaseContextHolder.getEnvironment();
    }
}
