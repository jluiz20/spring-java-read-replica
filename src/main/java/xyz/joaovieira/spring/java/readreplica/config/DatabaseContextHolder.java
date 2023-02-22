package xyz.joaovieira.spring.java.readreplica.config;


import java.util.Objects;

public class DatabaseContextHolder {

    private static final ThreadLocal<DatabaseEnvironment> CONTEXT = new ThreadLocal<>();

    private DatabaseContextHolder() {}

    public static void set(DatabaseEnvironment databaseEnvironment) {
        CONTEXT.set(databaseEnvironment);
    }

    public static DatabaseEnvironment getEnvironment() {
        DatabaseEnvironment databaseEnvironment = CONTEXT.get();

        return Objects.requireNonNullElse(databaseEnvironment, DatabaseEnvironment.WRITABLE);
    }

    public static void reset() {
        CONTEXT.remove();
    }
}
