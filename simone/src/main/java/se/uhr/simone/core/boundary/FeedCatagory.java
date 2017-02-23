package se.uhr.simone.core.boundary;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies that this is a Feed resource. Used to be able to enable or disable the feed service 
 * from the admin interface.  
 */

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface FeedCatagory {

}
