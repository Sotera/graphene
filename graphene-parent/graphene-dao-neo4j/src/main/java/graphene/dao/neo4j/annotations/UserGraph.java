package graphene.dao.neo4j.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ PARAMETER, FIELD, METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UserGraph {

}
