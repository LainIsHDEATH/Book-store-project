package com.epam.rd.autocode.spring.project.validation;

import com.epam.rd.autocode.spring.project.dto.RegisterRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, RegisterRequestDTO> {

    @Override
    public boolean isValid(RegisterRequestDTO dto, ConstraintValidatorContext ctx) {
        if (dto == null) return true;

        String p = dto.getPassword();
        String c = dto.getConfirmPassword();

        // Не дублюємо @NotBlank помилки
        if (p == null || c == null) return true;

        boolean ok = p.equals(c);
        if (!ok) {
            // Прив’язуємо помилку до поля confirmPassword (щоб показувалось під інпутом)
            ctx.disableDefaultConstraintViolation();
            ctx.buildConstraintViolationWithTemplate(ctx.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("confirmPassword")
                    .addConstraintViolation();
        }
        return ok;
    }
}
