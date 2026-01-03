package com.wlqq.phantom.library.utils;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
public @interface SuppressFBWarnings {
    /**
     * The set of FindBugs warnings that are to be suppressed in
     * annotated element. The value can be a bug category, kind or pattern.
     *
     * @return The set of FindBugs warnings
     */
    String[] value() default {};

    /**
     * Optional documentation of the reason why the warning is suppressed
     *
     * @return Optional documentation
     */
    String justification() default "";
}
