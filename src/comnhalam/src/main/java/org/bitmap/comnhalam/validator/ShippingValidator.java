package org.bitmap.comnhalam.validator;

import org.apache.commons.validator.routines.EmailValidator;
import org.bitmap.comnhalam.form.ShippingForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class ShippingValidator implements Validator {

    private EmailValidator emailValidator = EmailValidator.getInstance();

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass == ShippingForm.class;
    }

    @Override
    public void validate(Object o, Errors errors) {
        ShippingForm shippingForm = (ShippingForm) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "NotEmpty.shippingForm.email");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "NotEmpty.shippingForm.firstName");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "NotEmpty.shippingForm.lastName");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address", "NotEmpty.shippingForm.address");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "numberPhone", "NotEmpty.shippingForm.numberPhone");

        if (!errors.hasFieldErrors("email")) {
            if (!this.emailValidator.isValid(shippingForm.getEmail())) {
                errors.rejectValue("email", "Pattern.shippingForm.email");
            }
        }
    }
}
