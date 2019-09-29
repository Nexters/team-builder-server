package com.nexters.teambuilder.user.api.validator;

import java.util.Optional;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.nexters.teambuilder.user.api.annotation.UniqueId;
import com.nexters.teambuilder.user.domain.User;
import com.nexters.teambuilder.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

public class UniqueIdValidator implements ConstraintValidator<UniqueId, String> {
    @Autowired
    private UserService userService;

    @Override
    public void initialize(UniqueId parameters) {
        // no parameters.
    }

    @Override
    public boolean isValid(String id, ConstraintValidatorContext context) {
        boolean isValid;
        Optional<User> optionalUser = userService.findById(id);

        isValid = !optionalUser.isPresent();
        if (!isValid) {
            User user = optionalUser.get();
            context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate("error.id.unique")
                            .addConstraintViolation();
        }

        return isValid;
    }
}