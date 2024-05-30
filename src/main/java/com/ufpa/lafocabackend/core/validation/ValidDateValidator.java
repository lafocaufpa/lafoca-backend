package com.ufpa.lafocabackend.core.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ValidDateValidator implements ConstraintValidator<ValidDate, LocalDate> {

    private DateTimeFormatter formatter;

    @Override
    public void initialize(ValidDate constraintAnnotation) {
        this.formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (date == null) {
            return true; // @NotNull will handle null case
        }
        try {
            String dateString = date.format(formatter);
            LocalDate parsedDate = LocalDate.parse(dateString, formatter);
            return parsedDate != null;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}