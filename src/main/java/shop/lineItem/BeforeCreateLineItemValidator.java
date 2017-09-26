package shop.lineItem;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class BeforeCreateLineItemValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return LineItem.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        System.out.println("Sup!");
        LineItem lineItem = (LineItem)target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "order", "required", "line items must be part of an order");

    }
}
