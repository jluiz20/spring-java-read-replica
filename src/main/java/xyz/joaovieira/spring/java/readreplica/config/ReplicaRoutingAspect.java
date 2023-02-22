package xyz.joaovieira.spring.java.readreplica.config;


import jakarta.persistence.EntityManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.Session;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(0)
@ConditionalOnProperty("RDBMS_READ_FROM_REPLICA")
public class ReplicaRoutingAspect
{

    private final EntityManager entityManager;
    public ReplicaRoutingAspect(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }

    @Around("@annotation(useReadOnlyDatabase)")
    public Object proceed(ProceedingJoinPoint proceedingJoinPoint,
                          UseReadOnlyDatabase useReadOnlyDatabase) throws Throwable
    {
        try
        {
            if (DatabaseEnvironment.WRITABLE.equals(DatabaseContextHolder.getEnvironment()))
            {
                disconnectConnection();

                DatabaseContextHolder.set(DatabaseEnvironment.READ_ONLY);
            }

            return proceedingJoinPoint.proceed();
        }
        catch (Exception ex)
        {
            disconnectConnection();

            DatabaseContextHolder.reset();

            return proceedingJoinPoint.proceed();
        }
        finally
        {
            if (DatabaseEnvironment.READ_ONLY.equals(DatabaseContextHolder.getEnvironment()))
            {
                disconnectConnection();

                DatabaseContextHolder.reset();
            }
        }
    }

    private void disconnectConnection()
    {
        try (Session session = entityManager.unwrap(Session.class))
        {
//            session.d();
        }
    }
}