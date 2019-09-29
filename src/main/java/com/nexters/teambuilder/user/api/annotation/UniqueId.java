package com.nexters.teambuilder.user.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

import com.nexters.teambuilder.user.api.validator.UniqueIdValidator;

@Constraint(validatedBy = UniqueIdValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueId {
    /**
     * default messages.
     * @return
     */
    String message() default "error.id.unique";

    /**
     * default groups.
     * @return
     */
    Class<?>[] groups() default {};

    /**
     * default payload.
     * @return
     */
    Class<? extends Payload>[] payload() default {};
}