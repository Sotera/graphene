package graphene.util.db;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Target({ PARAMETER, FIELD })
@Retention(RUNTIME)
@Documented
public @interface MainDB {

}
