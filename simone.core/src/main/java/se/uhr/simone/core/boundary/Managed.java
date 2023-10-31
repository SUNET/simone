package se.uhr.simone.core.boundary;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.ws.rs.NameBinding;

/**
 * Indicates that the resource (API) is managed by SimOne, i.e. possible to manipulate with the admin API.
 */

@Target({ ElementType.TYPE })
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface Managed {

	/**
	 * The name of the SimOne instance.
	 *
	 * @return The SimOne instance name.
	 */

	String value() default "";
}
