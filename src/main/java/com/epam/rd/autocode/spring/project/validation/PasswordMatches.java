package com.epam.rd.autocode.spring.project.validation;

import com.epam.rd.autocode.spring.project.validation.PasswordMatchesValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchesValidator.class)
public @interface PasswordMatches {
    String message() default "{auth.password.confirm.mismatch}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}