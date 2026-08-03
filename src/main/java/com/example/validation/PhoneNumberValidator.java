package com.example.validation;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String>{
    // [数字]　数字内の文字が1文字以上あれば問題ないし　{2,4}桁数を指定する　2〜４桁の数字
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{2,4}-[0-9]{2,4}-[0-9]{4}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true; // null/空は @NotBlank に任せる
        return PHONE_PATTERN.matcher(value).matches();
    }
}
