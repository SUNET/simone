package se.uhr.nya.integration.sim.server.boundary;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies that this resource is a part of the admin interface catagory. Used to be able to enable or disable services.  
 */

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface AdminCatagory {

}
