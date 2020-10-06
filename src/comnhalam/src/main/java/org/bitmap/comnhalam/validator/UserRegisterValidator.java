package org.bitmap.comnhalam.validator;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.util.ValidatorUtils;
import org.bitmap.comnhalam.form.UserRegisterForm;
import org.bitmap.comnhalam.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UserRegisterValidator implements Validator {
    @Autowired
    private UserRepository userRepository;

    private EmailValidator emailValidator = EmailValidator.getInstance();

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass == UserRegisterForm.class;
    }

    @Override
    public void validate(Object o, Errors errors) {
        UserRegisterForm userRegisterForm = (UserRegisterForm) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "NotEmpty.userRegisterForm.email");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty.userRegisterForm.password");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "NotEmpty.userRegisterForm.confirmPassword");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "NotEmpty.userRegisterForm.firstName");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "NotEmpty.userRegisterForm.lastName");

        if (!this.emailValidator.isValid(userRegisterForm.getEmail())) {
            errors.rejectValue("email", "Pattern.userRegisterForm.email");
        }

        if (!errors.hasFieldErrors("email")) {
            if (userRepository.findByEmail(userRegisterForm.getEmail()) != null) {
                errors.rejectValue("email", "Duplicate.userRegisterForm.email");
            }
        }

        if (!errors.hasErrors()) {
            if (!userRegisterForm.getPassword().equals(userRegisterForm.getConfirmPassword())) {
                errors.rejectValue("confirmPassword", "Match.userRegisterForm.confirmPassword");
            }
        }
    }
}
