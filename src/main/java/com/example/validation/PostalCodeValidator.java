package com.example.validation;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PostalCodeValidator implements ConstraintValidator<PostalCode,String>{
    // \d：数字1文字　(\dは文字列でも書かれるため\\dにする)
    private static final Pattern POSTAL_PATTERN = Pattern.compile("^\\d{3}-\\d{4}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank())
            return true; // null/空は @NotBlank に任せる
        return POSTAL_PATTERN.matcher(value).matches();
    }
}
