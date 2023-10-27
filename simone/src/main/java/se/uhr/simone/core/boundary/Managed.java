package se.uhr.simone.core.boundary;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.ws.rs.NameBinding;

@Target({ ElementType.TYPE })
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface Managed {
	String value() default "";
}
