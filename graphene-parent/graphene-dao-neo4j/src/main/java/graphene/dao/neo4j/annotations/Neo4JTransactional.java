
package graphene.dao.neo4j.annotations;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.apache.tapestry5.ioc.annotations.AnnotationUseContext.BEAN;
import static org.apache.tapestry5.ioc.annotations.AnnotationUseContext.COMPONENT;
import static org.apache.tapestry5.ioc.annotations.AnnotationUseContext.MIXIN;
import static org.apache.tapestry5.ioc.annotations.AnnotationUseContext.PAGE;
import static org.apache.tapestry5.ioc.annotations.AnnotationUseContext.SERVICE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apache.tapestry5.ioc.annotations.UseWith;

/**
 * Marks a method of a service (or a component method) as transactional: the
 * call will create a tx, invoke the method, and then should call tx.success()
 * after invoking the method. Failure through exception will cause Neo4J to abort the commit.
 * 
 * @author djue
 * 
 */
@Target({METHOD,FIELD,java.lang.annotation.ElementType.TYPE})
@Retention(RUNTIME)
@Documented
@UseWith({ COMPONENT, MIXIN, PAGE, SERVICE, BEAN })
public @interface Neo4JTransactional {

}
