package org.bitmap.comnhalam.validator;

import org.bitmap.comnhalam.form.AddProductForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AddProductValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass == AddProductForm.class;
    }

    @Override
    public void validate(Object o, Errors errors) {
        AddProductForm addProductForm = (AddProductForm) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "NotEmpty.addProductForm.name");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "quantity", "NotEmpty.addProductForm.quantity");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "price", "NotEmpty.addProductForm.price");
//        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "multipartFile", "NotEmpty.addProductForm.multipartFile");

        if (!errors.hasFieldErrors("quantity")) {
            Pattern pattern = Pattern.compile("\\d*");
            Matcher matcher = pattern.matcher(addProductForm.getQuantity());
            if (!matcher.matches()) {
                errors.rejectValue("quantity", "Pattern.addProductForm.quantity");
            }
        }

        if (!errors.hasFieldErrors("price")) {
            Pattern pattern = Pattern.compile("(-?\\d+(\\.\\d+)?)");
            Matcher matcher = pattern.matcher(addProductForm.getPrice());
            if (!matcher.matches()) {
                errors.rejectValue("price", "Pattern.addProductForm.price");
            }
        }
    }
}
