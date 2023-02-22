package xyz.joaovieira.spring.java.readreplica.config;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface UseReadOnlyDatabase
{

}