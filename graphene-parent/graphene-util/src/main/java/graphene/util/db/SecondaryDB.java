package graphene.util.db;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ PARAMETER, FIELD })
@Retention(RUNTIME)
@Documented
public @interface SecondaryDB {

}
