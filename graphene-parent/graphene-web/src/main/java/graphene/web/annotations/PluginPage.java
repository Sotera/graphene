package graphene.web.annotations;

import graphene.model.idl.G_VisualType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for marking pages that can be discovered by customer modules.
 * 
 * @author djue
 * 
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PluginPage {
	G_VisualType[] visualType() default { G_VisualType.DEFAULT };

	String icon() default "";
	
	String menuName();
}
